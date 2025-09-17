package com.paradisecloud.fcm.ding.utils;

/**
 * @author nj
 * @date 2023/8/11 14:04
 */
public class StringUtils {

    public static String removeMatch(String str) {
        if (com.paradisecloud.common.utils.StringUtils.isEmpty(str)) {
            return str;
        }
        return str.replaceAll("[^\u4e00-\u9fa5]", "");
    }
}
