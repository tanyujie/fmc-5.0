package com.paradisecloud.fcm.web.encrypt;

import org.apache.commons.lang3.StringUtils;

public class Converter {

    public static byte[] hexString2ByteArray(String hexString) {
        byte[] byteArray = new byte[hexString.length() / 2];
        for (int i = 0; i < byteArray.length; i++) {
            byteArray[i] = (byte) Integer.parseInt(hexString.substring(2 * i, 2 * i + 2), 16);
        }

        return byteArray;
    }

    public static String byteArray2HexString(byte[] array, int len) {
        StringBuffer hexString = new StringBuffer();

        for (int i = 0; i < len; i++) {
            int intVal = array[i] & 0xFF;

            if (intVal < 0x10) {
                hexString.append("0");
            }

            hexString.append(Integer.toHexString(intVal).toUpperCase());
        }
        return hexString.toString();
    }

    public static String byteArray2HexString(byte[] array) {
        return byteArray2HexString(array, array.length);
    }

    public static byte[] byteArrayFixLength(byte[] array) {
        int length = array.length / 8 * 8 + (array.length % 8 == 0 ? 0 : 8);
        byte[] fixedByte = new byte[length];
        for (int i = 0; i < length; i++) {
            if (i < array.length) {
                fixedByte[i] = array[i];
            } else {
                fixedByte[i] = 0x00;
            }
        }
        return fixedByte;
    }

    public static String hexStringFixLength(String hexString) {
        int length = hexString.length() / 16 * 16 + (hexString.length() % 16 == 0 ? 0 : 16);
        String fixedString = StringUtils.rightPad(hexString, length, "0");
        return fixedString;
    }
}
