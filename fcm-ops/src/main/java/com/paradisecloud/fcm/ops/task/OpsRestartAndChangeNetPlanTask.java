package com.paradisecloud.fcm.ops.task;


import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.PathUtil;
import com.paradisecloud.fcm.ops.utils.NetPlanConfigGen;
import com.paradisecloud.fcm.ops.utils.PropertiesUtil;
import com.paradisecloud.fcm.ops.utils.SshRemoteServerOperateForOPS;
import com.paradisecloud.fcm.web.utils.SshConfigConstant;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * @author admin
 */
public class OpsRestartAndChangeNetPlanTask extends Task {

    private static final String filePath = PathUtil.getRootPath() + "/external_config.properties";
    private static final Logger log = LoggerFactory.getLogger(OpsRestartAndChangeNetPlanTask.class);


    public OpsRestartAndChangeNetPlanTask(String id, long delayInMilliseconds){
        super(id, delayInMilliseconds);

    }


    @Override
    public void run() {
        synchronized (this){
            log.info("OPS服务器恢复出厂设置开始。ID:" + getId());

            PropertiesUtil.updateProperty(filePath, "autoLoginUser", "admin");
            PropertiesUtil.updateProperty(filePath, "autoLoginPassword", "123456");
            PropertiesUtil.updateProperty(filePath, "fmcRootUrl", "https://"+"172.16.99.200"+":8899");
            PropertiesUtil.updateProperty(filePath, "cloudUrl", "https://yun.ttclouds.cn/fcm");
            String mountedDirPath = "/etc/netplan";
            String filePath2 = null;
            try {
                filePath2 = getnetFilePath(mountedDirPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String scriptPath = "/home/all_confs/scripts/restore.sh";
            // 初始文件保存路径

            String host = NetPlanConfigGen.getIpAddress(filePath2);

            SshRemoteServerOperateForOPS sshRemoteServerOperate = SshRemoteServerOperateForOPS.getInstance();

            try {
                sshRemoteServerOperate.sshRemoteCallLogin(host, SshConfigConstant.SERVER_DEFAULT_USER_NAME_FOR_OPS,  SshConfigConstant.SERVER_DEFAULT_PASSWORD, SshConfigConstant.DEFAULT_SERVER_PORT);
                boolean logined = sshRemoteServerOperate.isLogined();
                if(logined){
                    try {
                        log.error("OPS服务器恢复出厂设置 创建文件NetPlan---------------------------------------------------------------------------");

                        List<String> networkInterfaces = NetPlanConfigGen.getNetworkInterfacesByLshw();
                        //读取macaddress的值
                        String macAddress = NetPlanConfigGen.getMacAddress(filePath2, "br0");
                        NetPlanConfigGen.generateNetplanConfig(networkInterfaces,"172.16.99.200/24", "172.16.99.2",filePath2,macAddress);
                    } catch (IOException e) {
                        log.error("创建文件NetPlan失败--------------------------------------------------------------------------------------------"+e.getMessage());
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {

                String md = executeRemoteScript(host, SshConfigConstant.SERVER_DEFAULT_USER_NAME_FOR_OPS, SshConfigConstant.SERVER_DEFAULT_PASSWORD, "chroot /host");
                log.info("md Script output:\n" + md);
                String output = executeRemoteScript(host, SshConfigConstant.SERVER_DEFAULT_USER_NAME_FOR_OPS, SshConfigConstant.SERVER_DEFAULT_PASSWORD, scriptPath);
                log.info("restore Script output:\n" + output);

            } catch (Exception e) {
                log.info("OPS服务器恢复出厂设置修改失败..:" + e.getMessage());
            }

            try {
                TaskService taskService = BeanFactory.getBean(TaskService.class);
                OpsRestartTask opsRestartTask = new OpsRestartTask("修改ops配置信息重启",20000, host);
                taskService.addTask(opsRestartTask);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }





    private String getnetFilePath(String mountedDirPath) throws IOException {
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(mountedDirPath));

        for (Path path : directoryStream) {
            // 检查是否为常规文件
            if (Files.isRegularFile(path)) {
                Path fileName = path.getFileName();
                mountedDirPath = mountedDirPath + "/" + fileName.toString();
                break;
            }
        }
        log.info("mountedDirPath：" + mountedDirPath);

        return mountedDirPath;
    }










    public static String executeRemoteScript(String host, String user, String password, String scriptPath)
            throws  Exception {
        JSch jsch = new JSch();
        Session session = null;
        Channel channel = null;
        StringBuilder output = new StringBuilder();

        try {
            // 创建会话
            session = jsch.getSession(user, host, 2233);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // 打开执行命令的通道
            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand("bash " + scriptPath);

            // 获取命令执行的输出流
            InputStream in = channel.getInputStream();

            // 启动通道，执行远程脚本
            channel.connect();

            // 等待脚本执行完成
            while (!channel.isClosed()) {
                Thread.sleep(1000);
            }

            // 读取脚本执行的输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

        } finally {
            // 关闭通道和会话
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }

        return output.toString();
    }


}
