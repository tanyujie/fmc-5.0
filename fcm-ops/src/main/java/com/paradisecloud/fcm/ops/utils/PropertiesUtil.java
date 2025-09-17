package com.paradisecloud.fcm.ops.utils;

import java.io.*;
import java.util.Properties;
/**
 * @author nj
 * @date 2024/5/27 15:42
 */
public class PropertiesUtil {



    public static void updateProperty(String path,String key, String value) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(path)) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (OutputStream output = new FileOutputStream(path)) {
            properties.setProperty(key, value);
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
