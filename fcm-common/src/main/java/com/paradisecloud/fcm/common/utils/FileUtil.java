package com.paradisecloud.fcm.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FileUtil {

    public static final int SIZE_UNIT_B = 0;
    public static final int SIZE_UNIT_KB = 1;
    public static final int SIZE_UNIT_MB = 2;
    public static final int SIZE_UNIT_GB = 3;
    public static final int SIZE_UNIT_TB = 4;

    /**
     * 获取文件大小
     * @param fileSize
     * @param sizeUnit
     * @return
     */
    public static double getFileSize(long fileSize, int sizeUnit) {
        long kbCriticalSize = 1024;
        long mbCriticalSize = kbCriticalSize * 1024;
        long gbCriticalSize = mbCriticalSize * 1024;
        long tbCriticalSize = gbCriticalSize * 1024;
        BigDecimal fileSizeDecimal = new BigDecimal(fileSize);
        BigDecimal criticalSizeDecimal = new BigDecimal(mbCriticalSize);
        double size = 0.0;

        if (sizeUnit == SIZE_UNIT_TB) {
            size = fileSizeDecimal.divide(new BigDecimal(tbCriticalSize)).setScale(2, RoundingMode.HALF_UP).doubleValue();
        } else if (sizeUnit == SIZE_UNIT_GB) {
            size = fileSizeDecimal.divide(new BigDecimal(gbCriticalSize)).setScale(2, RoundingMode.HALF_UP).doubleValue();
        } else if (size == SIZE_UNIT_MB) {
            size = fileSizeDecimal.divide(criticalSizeDecimal).setScale(2, RoundingMode.HALF_UP).doubleValue();
        } else if (size == SIZE_UNIT_KB) {
            size = fileSizeDecimal.divide(new BigDecimal(kbCriticalSize)).setScale(2, RoundingMode.HALF_UP).doubleValue();
        } else {
            size = fileSize;
        }
        return size;
    }

    /**
     * 获取文件大小
     * @param fileSize
     * @param sizeUnit
     * @return
     */
    public static String getFileSizeWithUnit(long fileSize, int sizeUnit) {
        long kbCriticalSize = 1024;
        long mbCriticalSize = kbCriticalSize * 1024;
        long gbCriticalSize = mbCriticalSize * 1024;
        long tbCriticalSize = gbCriticalSize * 1024;
        BigDecimal fileSizeDecimal = new BigDecimal(fileSize);
        BigDecimal criticalSizeDecimal = new BigDecimal(mbCriticalSize);
        double size = 0.0;
        String result = "";

        if (sizeUnit == SIZE_UNIT_TB) {
            size = fileSizeDecimal.divide(new BigDecimal(tbCriticalSize)).setScale(2, RoundingMode.HALF_UP).doubleValue();
            result = size + "T";
        } else if (sizeUnit == SIZE_UNIT_GB) {
            size = fileSizeDecimal.divide(new BigDecimal(gbCriticalSize)).setScale(2, RoundingMode.HALF_UP).doubleValue();
            result = size + "G";
        } else if (size == SIZE_UNIT_MB) {
            size = fileSizeDecimal.divide(criticalSizeDecimal).setScale(2, RoundingMode.HALF_UP).doubleValue();
            result = size + "M";
        } else if (size == SIZE_UNIT_KB) {
            size = fileSizeDecimal.divide(new BigDecimal(kbCriticalSize)).setScale(2, RoundingMode.HALF_UP).doubleValue();
            result = size + "KB";
        } else {
            size = fileSize;
            result = size + "B";
        }
        return result;
    }

    /**
     * 获取文件大小
     * @param fileSize
     * @return
     */
    public static String getFileSizeWithUnit(long fileSize) {
        long kbCriticalSize = 1024;
        long mbCriticalSize = kbCriticalSize * 1024;
        long gbCriticalSize = mbCriticalSize * 1024;
        long tbCriticalSize = gbCriticalSize * 1024;
        BigDecimal fileSizeDecimal = new BigDecimal(fileSize);
        BigDecimal criticalSizeDecimal = new BigDecimal(mbCriticalSize);
        double size = 0.0;
        StringBuffer stringBuffer = new StringBuffer();

        if (fileSize >= tbCriticalSize) {
            size = fileSizeDecimal.divide(new BigDecimal(tbCriticalSize)).setScale(2, RoundingMode.HALF_UP).doubleValue();
            stringBuffer.append(size).append("T");
        } else if (fileSize >= gbCriticalSize) {
            size = fileSizeDecimal.divide(new BigDecimal(gbCriticalSize)).setScale(2, RoundingMode.HALF_UP).doubleValue();
            stringBuffer.append(size).append("G");
        } else if (fileSize >= mbCriticalSize) {
            size = fileSizeDecimal.divide(criticalSizeDecimal).setScale(2, RoundingMode.HALF_UP).doubleValue();
            stringBuffer.append(size).append("M");
        } else if (fileSize < mbCriticalSize) {
            size = fileSizeDecimal.divide(new BigDecimal(kbCriticalSize)).setScale(2, RoundingMode.HALF_UP).doubleValue();
            stringBuffer.append(size).append("KB");
        }
        return stringBuffer.toString();
    }
}
