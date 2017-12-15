package com.zhgtrade.ethereum.wallet.utils;

import com.alibaba.druid.util.JdbcUtils;
import com.zhgtrade.ethereum.wallet.model.User;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 招股金服
 * CopyRight : www.zhgtrade.com
 * Author : 林超（362228416@qq.com）
 * Date： 2017-08-08 14:49
 */
public class UserUtils {

    public static User getUser(String username) throws SQLException {
        List<Map<String, Object>> list = JdbcUtils.executeQuery(DBUtils.getDataSource(), "select * from user where username = ?", username);
        User user = null;
        if (list.size() > 0) {
            user = new User();
            user.setUsername(list.get(0).get("username") + "");
            user.setPassword(list.get(0).get("password") + "");
            user.setIdentify(list.get(0).get("identify") + "");
        }
        return user;
    }

    public static List<Map<String, Object>> getUsers() throws SQLException {
        return JdbcUtils.executeQuery(DBUtils.getDataSource(), "SELECT a.id, a.ico, a.name, b.username, b.password FROM token a, user b WHERE b.identify = a.id");
    }

    public static void addUser(User user) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("username", user.getUsername());
        map.put("password", user.getPassword());
        map.put("identify", user.getIdentify());
        JdbcUtils.insertToTable(DBUtils.getDataSource(), "user", map);
    }

    public static void deleteUser(String username) throws SQLException {
        JdbcUtils.executeUpdate(DBUtils.getDataSource(), "delete from user where username = ?", username);
    }

}
