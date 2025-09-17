package com.paradisecloud.fcm.common.thread;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class PushMessageToIpTerminalThread extends Thread {

    private String host;
    private String message;

    public PushMessageToIpTerminalThread(String host, String message) {
        this.host = host;
        this.message = message;
    }

    @Override
    public void run() {
        Socket socket = null;
        try {
            // 创建Socket连接
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, 8890), 5000);

            // 获取输出流，发送数据到服务器
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(message.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {
            }
        }
    }
}
