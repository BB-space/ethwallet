package com.zhgtrade.ethereum.wallet;

import com.alibaba.druid.util.JdbcUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhgtrade.ethereum.wallet.model.Admin;
import com.zhgtrade.ethereum.wallet.utils.AdminUtils;
import com.zhgtrade.ethereum.wallet.utils.DBUtils;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Base64;

/**
 * 招股金服
 * CopyRight : www.zhgtrade.com
 * Author : 林超（362228416@qq.com）
 * Date： 2017-08-25 17:42
 */
public class DBAdminServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAuth(req, resp)) return;
        InputStream in = req.getClass().getClassLoader().getResourceAsStream("dbadmin.html");     // prod
//        FileInputStream in = new FileInputStream("src/main/resources/dbadmin.html");        // dev
        String content = IOUtils.toString(in, "utf-8");
        IOUtils.closeQuietly(in);
        resp.setHeader("Content-Type", "text/html; charset=utf-8");
        resp.getWriter().write(content);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAuth(req, resp)) return;

        Object ret = null;
        String type = req.getParameter("type");
        String sql = req.getParameter("sql");
        if ("query".equals(type)) {
            try {
                ret = JdbcUtils.executeQuery(DBUtils.getDataSource(), sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if ("update".equals(type)) {
            try {
                ret = JdbcUtils.executeUpdate(DBUtils.getDataSource(), sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        resp.getWriter().write(objectMapper.writeValueAsString(ret));
    }

    private boolean isAuth(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String auth = req.getHeader("Authorization");
            if (auth == null || !auth.startsWith("Basic ")) {
                requiredAuth(resp);
                return false;
            }
            auth = new String(Base64.getDecoder().decode(auth.substring(6)));
            String[] userinfo = auth.split(":");
            Admin user = AdminUtils.getUser(userinfo[0]);
            if (user == null || !user.getPassword().equals(userinfo[1])) {
                requiredAuth(resp);
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void requiredAuth(HttpServletResponse resp) throws IOException {
        resp.setHeader("WWW-Authenticate", "Basic realm=\"admin\"");
        resp.setStatus(401);
        resp.getWriter().write("");
    }
}
