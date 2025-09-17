package com.paradisecloud.fcm.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Random;

public class SipAccountUtil {

    public static String createAccount(int num) {
        String numStr = String.valueOf(num);
        String account = "3" + StringUtils.leftPad(numStr, 8, "0");
        return account;
    }

    public static String createPassword() {
        String password = "";
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int num = random.nextInt(10);
            password += num;
        }
        return password;
    }

    public static boolean isAutoAccount(String account) {
        if (account != null) {
            if (account.length() == 9 && account.startsWith("3")) {
                return true;
            }
        }
        return false;
    }

    public static String createZjAccount(int num) {
        String numStr = String.valueOf(num);
        String account = "4" + StringUtils.leftPad(numStr, 8, "0");
        return account;
    }

    public static String createZjPassword() {
        String password = "";
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int num = random.nextInt(10);
            password += num;
        }
        return password;
    }

    public static boolean isZjAutoAccount(String account) {
        if (account != null) {
            if (account.length() == 9 && account.startsWith("4")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isZjReservedAccount(String account) {
        if (account != null) {
            if ((account.length() == 4 && account.startsWith("1")) || "2000".equals(account)) {
                return true;
            }
        }
        return false;
    }
}
