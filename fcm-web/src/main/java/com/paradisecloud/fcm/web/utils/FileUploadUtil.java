package com.paradisecloud.fcm.web.utils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author nj
 * @date 2022/11/3 9:13
 */
public class FileUploadUtil {

    public static boolean uploadFile(String filename, InputStream input, String userName, String password, String ip, Integer port, String path) {
        boolean result = false;
        ChannelSftp sftp = null;
        FTPClient ftp = new FTPClient();
        File file = null;
        try {
            JSch jsch = new JSch();
            //获取sshSession  账号-ip-端口
            Session sshSession = jsch.getSession(userName, ip, port);
            //添加密码
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            //严格主机密钥检查
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            //开启sshSession链接
            sshSession.connect();
            //获取sftp通道
            Channel channel = sshSession.openChannel("sftp");
            //开启
            channel.connect();
            sftp = (ChannelSftp) channel;
            //服务器路径
            file = new File(path);
            //设置为被动模式
            ftp.enterLocalPassiveMode();
            //设置上传文件的类型为二进制类型
            //进入到要上传的目录  然后上传文件
            sftp.cd(path);
            sftp.put(input, filename);
            input.close();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 修改文件名
     * @param oldFilename
     * @param newFilename
     * @param userName
     * @param password
     * @param ip
     * @param port
     * @param path
     * @return
     */
    public static boolean renameFile(String oldFilename, String newFilename, String userName, String password, String ip, Integer port, String path) {
        boolean result = false;
        ChannelSftp sftp = null;
        FTPClient ftp = new FTPClient();
        File file = null;
        try {
            JSch jsch = new JSch();
            //获取sshSession  账号-ip-端口
            Session sshSession = jsch.getSession(userName, ip, port);
            //添加密码
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            //严格主机密钥检查
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            //开启sshSession链接
            sshSession.connect();
            //获取sftp通道
            Channel channel = sshSession.openChannel("sftp");
            //开启
            channel.connect();
            sftp = (ChannelSftp) channel;
            //服务器路径
            file = new File(path);
            //设置为被动模式
            ftp.enterLocalPassiveMode();
            //设置上传文件的类型为二进制类型
            //进入到要上传的目录  然后上传文件
            sftp.cd(path);
            sftp.rename(oldFilename, newFilename);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


}
