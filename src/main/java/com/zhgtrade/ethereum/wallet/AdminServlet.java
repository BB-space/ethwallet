package com.zhgtrade.ethereum.wallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhgtrade.ethereum.wallet.model.Token;
import com.zhgtrade.ethereum.wallet.model.User;
import com.zhgtrade.ethereum.wallet.utils.TokenUtils;
import com.zhgtrade.ethereum.wallet.utils.UserUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 招股金服
 * CopyRight : www.zhgtrade.com
 * Author : 林超（362228416@qq.com）
 * Date： 2017-08-15 10:49
 */
public class AdminServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, RpcCall> methods = new HashMap<>();

    private Logger log = Logger.getLogger(getClass().getName());

    public static Object getToken(HttpServletRequest req) {
        Token token = null;
        try {
            String id = req.getParameter("id");
            token = TokenUtils.getToken(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return token;
    }

    public static Object tokens() {
        List tokens = null;
        try {
            tokens = TokenUtils.getTokensAndBalance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tokens;
    }

    public static Object saveToken(HttpServletRequest req) {
        try {
            Token token = new Token();
            token.setName(req.getParameter("name"));
            token.setContractAddress(req.getParameter("contractAddress"));
            token.setUnlockPassword(req.getParameter("unlockPassword"));
            token.setAccessPassword(req.getParameter("accessPassword"));
            token.setType(req.getParameter("type"));
            if (token.getType() == null || token.getType().trim().length() == 0) {
                token.setType("token");
            }
            TokenUtils.addToken(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Object delToken(HttpServletRequest req) {
        try {
            String name = req.getParameter("name");
            TokenUtils.deleteToken(name);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Object getUser(HttpServletRequest req) {
        User user = null;
        try {
            String username = req.getParameter("username");
            user = UserUtils.getUser(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static Object users() {
        List users = null;
        try {
            users = UserUtils.getUsers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static Object saveUser(HttpServletRequest req) {
        try {
            User user = new User();
            user.setUsername(req.getParameter("username"));
            user.setPassword(req.getParameter("password"));
            user.setIdentify(req.getParameter("identify"));
            UserUtils.addUser(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Object delUser(HttpServletRequest req) {
        try {
            String username = req.getParameter("username");
            UserUtils.deleteUser(username);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
//        System.out.println(uri);
        Map<String, Object> ret = new HashMap<>();
        ret.put("code", 200);
        switch (uri) {
            case "/mytokens/admin/token":
                ret.put("data", getToken(req));
                break;
            case "/mytokens/admin/tokens":
                ret.put("data", tokens());
                break;
            case "/mytokens/admin/save-token":
                ret.put("data", saveToken(req));
                break;
            case "/mytokens/admin/del-token":
                ret.put("data", delToken(req));
                break;
            case "/mytokens/admin/user":
                ret.put("data", getUser(req));
                break;
            case "/mytokens/admin/users":
                ret.put("data", users());
                break;
            case "/mytokens/admin/save-user":
                ret.put("data", saveUser(req));
                break;
            case "/mytokens/admin/del-user":
                ret.put("data", delUser(req));
                break;
        }
        resp.getWriter().write(objectMapper.writeValueAsString(ret));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
