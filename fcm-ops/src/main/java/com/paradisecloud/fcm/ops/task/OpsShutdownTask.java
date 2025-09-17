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
public class OpsShutdownTask extends Task {


    private static final Logger log = LoggerFactory.getLogger(OpsShutdownTask.class);
    private String ip;
    private String fmeIp;

    public OpsShutdownTask(String id, long delayInMilliseconds, String ip,String fmeIp) {
        super(id, delayInMilliseconds);
        this.ip=ip;
        this.fmeIp=fmeIp;
    }


    @Override
    public void run() {
        log.info("OPS服务器关机ID:" + getId() + " ip地址:" + ip);


        SshRemoteServerOperateForOpsRestart remoteServerOperateForOpsRestart = SshRemoteServerOperateForOpsRestart.getInstance();
        try {

            remoteServerOperateForOpsRestart.sshRemoteCallLogin(ip, SshConfigConstant.SERVER_DEFAULT_USER_NAME_FOR_OPS, SshConfigConstant.SERVER_DEFAULT_PASSWORD, SshConfigConstant.DEFAULT_SERVER_PORT);
            boolean logined = remoteServerOperateForOpsRestart.isLogined();
            if (logined) {
                log.info("OPS{}服务器正在准备关机》》》》》》》》》》》》》》》》》》", ip);
                remoteServerOperateForOpsRestart.execCommand("sudo shutdown now");
                log.info("OPS{}服务器信正在关机》》》》》》》》》》》》》》》》》》", ip);
                Threads.sleep(1000);
                remoteServerOperateForOpsRestart.execCommand("sudo shutdown now");
                Threads.sleep(1000);
                remoteServerOperateForOpsRestart.execCommand("sudo shutdown now");
                Threads.sleep(1000);
                remoteServerOperateForOpsRestart.execCommand("sudo shutdown now");
                Threads.sleep(1000);
                remoteServerOperateForOpsRestart.execCommand("sudo shutdown now");
                Threads.sleep(1000);
                remoteServerOperateForOpsRestart.execCommand("sudo shutdown now");
                Threads.sleep(1000);
                remoteServerOperateForOpsRestart.execCommand("sudo shutdown now");
                Threads.sleep(1000);
                remoteServerOperateForOpsRestart.execCommand("sudo shutdown now");
                Threads.sleep(1000);
                remoteServerOperateForOpsRestart.execCommand("sudo shutdown now");
                Threads.sleep(1000);
                remoteServerOperateForOpsRestart.execCommand("sudo shutdown now");
                Threads.sleep(1000);
                remoteServerOperateForOpsRestart.execCommand("sudo shutdown now");
            }
        } catch (Exception e) {
            log.info("OPS服务器关机失败：" +e.getMessage());
        } finally {
            remoteServerOperateForOpsRestart.closeSession();
        }
    }

}
