package com.zhgtrade.ethereum.wallet;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhgtrade.ethereum.wallet.model.Token;
import com.zhgtrade.ethereum.wallet.model.User;
import com.zhgtrade.ethereum.wallet.utils.ConfigUtils;
import com.zhgtrade.ethereum.wallet.utils.TokenUtils;
import com.zhgtrade.ethereum.wallet.utils.UserUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 招股金服
 * CopyRight : www.zhgtrade.com
 * Author : 林超（362228416@qq.com）
 * Date： 2017-08-09 14:21
 */
public class RpcServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, RpcCall> methods = new HashMap<>();

    private Logger log = Logger.getLogger(getClass().getName());

    public RpcServlet() {
        methods.put("getbalance", Api::getBalance);
        methods.put("getaccount", Api::getAccount);
        methods.put("gettransaction", Api::getTransaction);
        methods.put("sendtoaddress", Api::sendTransaction);
        methods.put("sendfrom", Api::sendfrom);
        methods.put("settxfee", Api::settxfee);
        methods.put("listtransactions", Api::listTransactions);
        methods.put("getnewaddress", Api::getNewAccount);
        methods.put("walletpassphrase", Api::unLockAccount);
        methods.put("walletlock", Api::lockAccount);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().write("");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ip = req.getRemoteAddr();
        if(!ConfigUtils.getConfig("ip.white.list").contains(ip)){
            System.out.println("IP white list intercept："+ip);
            return;
        }
        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Basic ")) {
            requiredAuth(resp);
            return;
        }
        try {
            auth = new String(Base64.getDecoder().decode(auth.substring(6)));
            String[] userinfo = auth.split(":");
            Token token = TokenUtils.getToken(userinfo[0]);
            if (token == null || !token.getAccessPassword().equals(userinfo[1])) {
                requiredAuth(resp);
                return;
            }
            String requestBody = IOUtils.toString(req.getInputStream());
            log.debug(token.getContractAddress() + " " + requestBody);
            Map<String, Object> map = objectMapper.readValue(requestBody, Map.class);
            Object ret = exeRpcMethod(token, map);
            resp.getWriter().write(objectMapper.writeValueAsString(ret));
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("");
            return;
        }
    }

    private Object exeRpcMethod(Token token, Map<String, Object> params) throws Exception {
        String method = params.get("method") + "";
        RpcCall rpcCall = methods.get(method);
        Object ret;
        if (rpcCall != null) {
            ret = rpcCall.execute(token, params);
        } else {
            ret = Collections.singletonMap("error", "not implments!");
        }
        return ret;
    }

    private void requiredAuth(HttpServletResponse resp) throws IOException {
        resp.setHeader("WWW-Authenticate", "Basic realm=\"jsonrpc\"");
        resp.setStatus(401);
        resp.getWriter().write("");
    }

}
