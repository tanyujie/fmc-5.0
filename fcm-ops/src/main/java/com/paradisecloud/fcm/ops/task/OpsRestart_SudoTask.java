package com.paradisecloud.fcm.ops.task;


import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.ops.utils.SshRemoteServerOperateForOpsRestart;
import com.paradisecloud.fcm.web.utils.SshConfigConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author admin
 */
public class OpsRestart_SudoTask extends Task {


    private static final Logger log = LoggerFactory.getLogger(OpsRestart_SudoTask.class);
    private String ip;

    public OpsRestart_SudoTask(String id, long delayInMilliseconds, String ip) {
        super(id, delayInMilliseconds);
        this.ip=ip;
    }


    @Override
    public void run() {
        Threads.sleep(1000*10);
        log.info("OPS服务器信重启开始。ID:" + getId()+" ip地址:"+ip);
        SshRemoteServerOperateForOpsRestart remoteServerOperateForOpsRestart = SshRemoteServerOperateForOpsRestart.getInstance();
        try {
            remoteServerOperateForOpsRestart.sshRemoteCallLogin(ip, SshConfigConstant.SERVER_DEFAULT_USER_NAME_FOR_OPS, SshConfigConstant.SERVER_DEFAULT_PASSWORD, SshConfigConstant.DEFAULT_SERVER_PORT);
            boolean logined = remoteServerOperateForOpsRestart.isLogined();
            if (logined) {

                log.info("OPS{}服务器信正在准备重启》》》》》》》》》》》》》》》》》》", ip);
                remoteServerOperateForOpsRestart.execCommand("sudo reboot");
                Threads.sleep(1000);
                remoteServerOperateForOpsRestart.execCommand("sudo reboot");
                Threads.sleep(1000);
                remoteServerOperateForOpsRestart.execCommand("sudo reboot");
                Threads.sleep(1000);
                remoteServerOperateForOpsRestart.execCommand("sudo reboot");
                Threads.sleep(1000);
                remoteServerOperateForOpsRestart.execCommand("sudo reboot");
                Threads.sleep(1000);
                remoteServerOperateForOpsRestart.execCommand("sudo reboot");
                Threads.sleep(1000);
                remoteServerOperateForOpsRestart.execCommand("sudo reboot");
                Threads.sleep(1000);
                remoteServerOperateForOpsRestart.execCommand("sudo reboot");
                Threads.sleep(1000);
                remoteServerOperateForOpsRestart.execCommand("sudo reboot");
                log.info("OPS{}服务器信正在重启》》》》》》》》》》》》》》》》》》", ip);
            }
        } catch (Exception e) {
            log.info("OPS服务器信重启失败：" +e.getMessage());
        } finally {
            remoteServerOperateForOpsRestart.closeSession();
        }
    }

}
