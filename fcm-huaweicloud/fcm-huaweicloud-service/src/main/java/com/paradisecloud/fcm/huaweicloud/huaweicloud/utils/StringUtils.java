package com.paradisecloud.fcm.huaweicloud.huaweicloud.utils;

import java.util.Random;

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
    public static String generateNumericSequence(int minLength, int maxLength) {
        Random random = new Random();
        int length = random.nextInt(maxLength - minLength + 1) + minLength;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
