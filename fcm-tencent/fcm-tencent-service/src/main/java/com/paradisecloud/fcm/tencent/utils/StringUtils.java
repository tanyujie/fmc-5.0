package com.paradisecloud.fcm.tencent.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nj
 * @date 2023/8/11 14:04
 */
public class StringUtils {

    public static   Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]+");

    public static String removeMatch(String str) {
        if (com.paradisecloud.common.utils.StringUtils.isEmpty(str)) {
            return str;
        }
        return str.replaceAll("[^\u4e00-\u9fa5]", "");
    }

    public static String extractChinese(String input) {
        if (com.paradisecloud.common.utils.StringUtils.isEmpty(input)) {
            return input;
        }
        Matcher matcher = pattern.matcher(input);

        StringBuilder chineseBuilder = new StringBuilder();
        while (matcher.find()) {
            chineseBuilder.append(matcher.group());
        }

        return chineseBuilder.toString();
    }

}
