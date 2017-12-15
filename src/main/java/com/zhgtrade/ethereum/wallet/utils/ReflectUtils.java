package com.zhgtrade.ethereum.wallet.utils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 招股金服
 * CopyRight : www.zhgtrade.com
 * Author : 林超（362228416@qq.com）
 * Date： 2017-08-15 17:18
 */
public class ReflectUtils {

    public static <T> T convertToObject(Map<String, Object> map, Class<T> clazz) {
        try {
            T obj = clazz.newInstance();
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().startsWith("set")) {
                    String name = method.getName().substring(3);
                    String first = name.substring(0, 1);
                    String other = name.substring(1);
                    String fieldName = first.toLowerCase() + other;
                    Object val = map.get(fieldName);
                    if (val != null) {
                        method.invoke(obj, val);
                    }
                }
            }
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
