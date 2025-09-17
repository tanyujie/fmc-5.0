package com.paradisecloud.fcm.smc.cache.modle.util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;


import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * @author nj
 * @date 2022/8/15 11:39
 */
public class NameValuePairsUtil {

    public static ArrayList<NameValuePair> getObjectNameValuePairs(Object obj) throws IllegalArgumentException, IllegalAccessException {
        ArrayList<NameValuePair> list = new ArrayList<>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            list.add( new BasicNameValuePair(field.getName(), String.valueOf(field.get(obj))));
        }
        return list;
    }
}
