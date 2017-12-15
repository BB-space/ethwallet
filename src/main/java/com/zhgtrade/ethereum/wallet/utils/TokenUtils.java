package com.zhgtrade.ethereum.wallet.utils;

import com.alibaba.druid.util.JdbcUtils;
import com.zhgtrade.ethereum.wallet.model.Token;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 招股金服
 * CopyRight : www.zhgtrade.com
 * Author : 林超（362228416@qq.com）
 * Date： 2017-08-08 14:46
 */
public class TokenUtils {

    private static Token parseTokenRecord(Map<String, Object> map) {
        return ReflectUtils.convertToObject(map, Token.class);
    }

    public final static String MAIN_TOKEN_NAME = "ether";

    public static List<Token> getTokens() throws SQLException {
        List<Map<String, Object>> list = JdbcUtils.executeQuery(DBUtils.getDataSource(), "select * from token");
        final List<Token> tokens = new ArrayList<>(list.size());
        list.forEach(row -> tokens.add(parseTokenRecord(row)));
        return tokens;
    }

    public static List<Token> getTokens(String param) throws SQLException {
        List<Map<String, Object>> list = JdbcUtils.executeQuery(DBUtils.getDataSource(), "select * from token where name=?",param);
        final List<Token> tokens = new ArrayList<>(list.size());
        list.forEach(row -> tokens.add(parseTokenRecord(row)));
        return tokens;
    }

    public static Token getToken(String contractAddress) throws SQLException {
        List<Map<String, Object>> list = JdbcUtils.executeQuery(DBUtils.getDataSource(), "select * from token where contractAddress = ?", contractAddress);
        Token token = null;
        if (list.size() > 0) {
            token = parseTokenRecord(list.get(0));
        }
        return token;
    }

    public static Token getTokenById(String id) throws SQLException {
        List<Map<String, Object>> list = JdbcUtils.executeQuery(DBUtils.getDataSource(), "select * from token where id =?", id);
        Token token = null;
        if (list.size() > 0) {
            token = parseTokenRecord(list.get(0));
        }
        return token;
    }

    public static void addToken(Token token) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("name", token.getName());
        map.put("contractAddress", token.getContractAddress());
        map.put("accessPassword", token.getAccessPassword());
        map.put("unlockPassword", token.getUnlockPassword());
        map.put("type", token.getType());
        map.put("limitGas", token.getLimitGas());
        map.put("unit", token.getUnit());
        JdbcUtils.insertToTable(DBUtils.getDataSource(), "token", map);
    }
    public static void updateToken(String identify, Integer limitGas) throws SQLException {
        JdbcUtils.executeUpdate(DBUtils.getDataSource(), "update token set limitGas = ? where id = ?",  limitGas, identify);
    }

    public static void updateToken(Integer identify, String name) throws SQLException {
        JdbcUtils.executeUpdate(DBUtils.getDataSource(), "update token set name = ? where id = ?",  name, identify);
    }

    public static void deleteToken(String name) throws SQLException {
        JdbcUtils.executeUpdate(DBUtils.getDataSource(), "delete from token where id = ?", name);
    }

    public static List<Map<String, Object>> getTokensAndBalance() throws SQLException {
        return JdbcUtils.executeQuery(DBUtils.getDataSource(), "SELECT a.id, a.name, a.ico, IFNULL(b.amount, 0) amount FROM token a LEFT JOIN (SELECT identify, IFNULL(SUM(amount), 0) amount FROM account GROUP BY identify) b on a.id = b.identify");
    }

}
