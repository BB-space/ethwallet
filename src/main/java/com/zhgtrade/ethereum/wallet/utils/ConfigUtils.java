package com.zhgtrade.ethereum.wallet.utils;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 招股金服
 * CopyRight : www.zhgtrade.com
 * Author : 林超（362228416@qq.com）
 * Date： 2017-08-29 13:57
 */
public class ConfigUtils {

    private static Map<String, String> configMap = new HashMap<>();

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
            Set<String> keySet = config.stringPropertyNames();
            keySet.forEach(key -> {
                configMap.put(key, config.getProperty(key));
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public static String getConfig(String key) {
        return getConfig(key, null);
    }

    public static String getConfig(String key, String defaultValue) {
        String val = configMap.get(key);
        return val == null ? defaultValue : val;
    }

}
