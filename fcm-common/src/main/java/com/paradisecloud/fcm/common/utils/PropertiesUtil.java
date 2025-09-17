package com.paradisecloud.fcm.common.utils;

import java.io.*;
import java.util.Properties;

public class PropertiesUtil {

    /**
     * 读取Properties文件
     *
     * @param filePath
     * @return
     */
    public static Properties readProperties(String filePath) {
        Properties properties = null;
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
                properties = new Properties();
                properties.load(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return properties;
    }
}
