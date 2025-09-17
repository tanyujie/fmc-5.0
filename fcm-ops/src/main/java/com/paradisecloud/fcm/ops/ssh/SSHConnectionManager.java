package com.paradisecloud.fcm.ops.ssh;

import com.jcraft.jsch.JSchException;
import com.paradisecloud.fcm.ops.utils.NetPlanConfigGen;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author  nj
 */
public class SSHConnectionManager {
    private static SSHConnectionPool connectionPool;
    private static SSHExecutor executor;

    /**
     * 获取共享的 SSHConnectionPool 实例
     */
    public static SSHConnectionPool getConnectionPool() throws JSchException {
        if (connectionPool == null) {
            synchronized (SSHConnectionManager.class) {
                if (connectionPool == null) {
                    connectionPool =  new SSHConnectionPool(3, 5, getHost(), "opsadm", "P@rad1se");
                }
            }
        }
        return connectionPool;
    }

    /**
     * 获取共享的 SSHExecutor 实例
     */
    public static SSHExecutor getExecutor() throws JSchException {
        if (executor == null) {
            synchronized (SSHConnectionManager.class) {
                if (executor == null) {
                    executor = new SSHExecutor(getConnectionPool(), 10);
                }
            }
        }
        return executor;
    }

    /**
     * 关闭连接池和执行器
     */
    public static void shutdown() {
        if (executor != null) {
            executor.shutdown();
        }
        if (connectionPool != null) {
            connectionPool.close();
        }
    }

    private static String getHost() {
        String mountedDirPath = "/etc/netplan";
        String filePath2 = null;
        try {
            filePath2 = getnetFilePath(mountedDirPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String host = NetPlanConfigGen.getIpAddress(filePath2);
        return host;
    }


    private static String getnetFilePath(String mountedDirPath) throws IOException {
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(mountedDirPath));

        for (Path path : directoryStream) {
            if (Files.isRegularFile(path)) {
                Path fileName = path.getFileName();
                mountedDirPath = mountedDirPath + "/" + fileName.toString();
                break;
            }
        }

        return mountedDirPath;
    }
}

