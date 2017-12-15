package com.zhgtrade.ethereum.wallet.utils;

import com.alibaba.druid.util.JdbcUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 招股金服
 * CopyRight : www.zhgtrade.com
 * Author : 林超（362228416@qq.com）
 * Date： 2017-08-10 18:06
 */
public class SyncinfoUtils {

    public static String get(String key) {
        String value = null;
        try {
            List<Map<String, Object>> list = JdbcUtils.executeQuery(DBUtils.getDataSource(), "select * from syncinfo where `key` = ?", key);
            if (list.size() > 0) {
                value = list.get(0).get("value").toString();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static void set(String key, Object value) {
        try {
            JdbcUtils.execute(DBUtils.getDataSource(), "replace into syncinfo values (?,?)", key, value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
