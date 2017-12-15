package com.zhgtrade.ethereum.wallet.utils;

import com.alibaba.druid.util.JdbcUtils;
import com.zhgtrade.ethereum.wallet.model.Admin;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 招股金服
 * CopyRight : www.zhgtrade.com
 * Author : 林超（362228416@qq.com）
 * Date： 2017-08-08 15:59
 */
public class AdminUtils {

    public static Admin getUser(String username) throws SQLException {
        List<Map<String, Object>> list = JdbcUtils.executeQuery(DBUtils.getDataSource(), "select * from admin where username = ?", username);
        Admin user = null;
        if (list.size() > 0) {
            user = new Admin();
            user.setUsername(list.get(0).get("username") + "");
            user.setPassword(list.get(0).get("password") + "");
        }
        return user;
    }

}
