package com.paradisecloud.fcm.common.utils;

public class PathUtil {

    public static boolean isStartupFromJar() {
        String protocol = PathUtil.class.getResource("").getProtocol();
        return "jar".equals(protocol);
    }

    public static String getRootPath() {
        if (isStartupFromJar()) {
            String path = PathUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String os = System.getProperty("os.name");
            if (path.startsWith("file:")) {
                path = path.substring(5);
            }
            if (os.contains("indows")) {
                path = path.substring(1);
            }
            if (path.contains("jar")) {
                path = path.substring(0, path.indexOf(".jar"));
                path = path.substring(0, path.lastIndexOf("/"));
            }
            return path;
        } else {
            return System.getProperty("user.dir");
        }
    }
}
