package com.zhgtrade.ethereum.wallet.utils;

import com.alibaba.druid.pool.DruidDataSource;
import org.web3j.utils.Files;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 招股金服
 * CopyRight : www.zhgtrade.com
 * Author : 林超（362228416@qq.com）
 * Date： 2017-08-08 15:40
 */
public class DBUtils {

    private static DruidDataSource dataSource;

    static {
//        File walletFile = new File("data/wallet.db");
//        if (!walletFile.exists()) {
//            throw new RuntimeException("no found wallet.dat");
//        }
        dataSource = new DruidDataSource();
//        String url = "jdbc:sqlite:" + walletFile.getAbsolutePath();
//        String url = "jdbc:h2:" + walletFile.getAbsolutePath();
//        String username = "sa";
//        String password = "";
        String driverClassName = ConfigUtils.getConfig("jdbc.driverClassName");
        String url = ConfigUtils.getConfig("jdbc.url");
        String username = ConfigUtils.getConfig("jdbc.username");
        String password = ConfigUtils.getConfig("jdbc.password");

        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setInitialSize(1);
        dataSource.setMinIdle(0);
        dataSource.setMaxActive(50);
        dataSource.setTestWhileIdle(false);
        dataSource.setPoolPreparedStatements(false);
        try {
            dataSource.init();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
//        initTables();
    }

    public static void initTables() {
        try {
            File file = new File("data/init.lock");
            if (!file.exists()) {
                String script = ConfigUtils.getConfig("jdbc.init.script");
                if (script != null && script.trim().length() > 0) {
                    File scriptFile = new File(script);
                    String sql = Files.readString(scriptFile);
                    try (Connection conn = getConnection()) {
                        Statement stmt = conn.createStatement();
                        String[] sqls = sql.split(";");
                        for (String s : sqls) {
                            if (s != null && s.trim().length() > 0) {
                                stmt.addBatch(s);
                            }
                        }
                        stmt.executeBatch();
                        stmt.close();
                    }
                }
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void main(String[] args) throws Exception {
//        System.out.println(getDataSource());
        System.out.println(getConnection());
    }

}
