package com.zhgtrade.ethereum.wallet.utils;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 招股金服
 * CopyRight : www.zhgtrade.com
 * Author : 林超（362228416@qq.com）
 * Date： 2017-08-10 10:27
 */
public class Web3jUtils {

    static Web3j web3j;

    static {
        File file = new File("conf/config.properties");
        if (!file.exists()) {
            throw new RuntimeException("no found config.properties");
        }
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            Properties config = new Properties();
            config.load(in);
            String web3Url = config.getProperty("web3.url");
            if (web3Url == null) {
                throw new RuntimeException("no found web3.url config");
            }
            System.out.println("Use web3 " + web3Url);
            web3j = Web3j.build(new HttpService(web3Url));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (web3j == null) {
            System.exit(-1);
        }
    }

    public static Web3j getWeb3j() {
        return web3j;
    }

}
