package com.paradisecloud.fcm.web.task;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.web.utils.IpUtil;
import com.paradisecloud.fcm.web.utils.SshConfigConstant;
import com.paradisecloud.fcm.web.utils.SshRemoteServerOperateForFmc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemRebootTask extends Task {

    private static final Logger logger = LoggerFactory.getLogger(SystemRebootTask.class);

    public SystemRebootTask(String id, long delayInMilliseconds) {
        super("system_reboot_" + id, delayInMilliseconds);
    }

    @Override
    public void run() {
        logger.info("重启系统任务启动");
        String localIp = IpUtil.getLocalIp();
        String region = ExternalConfigCache.getInstance().getRegion();
        if ("ops".equalsIgnoreCase(region)) {
            String userName = SshConfigConstant.SERVER_DEFAULT_USER_NAME_FOR_OPS;
            SshRemoteServerOperateForFmc sshRemoteServerOperate = new SshRemoteServerOperateForFmc();
            try {
                sshRemoteServerOperate.sshRemoteCallLogin(
                        localIp,
                        userName,
                        SshConfigConstant.SERVER_DEFAULT_PASSWORD,
                        SshConfigConstant.DEFAULT_SERVER_PORT);
                boolean logined = sshRemoteServerOperate.isLogined();
                if (logined) {
                    // 重启
                    sshRemoteServerOperate.execCommand("sudo reboot");
                    logger.info("重启系统成功");
                } else {
                    logger.info("主机登录失败");
                    throw new CustomException("登录主机发生错误");
                }
            } catch (Exception e) {
                logger.info("更新FMC发生错误", e);
            } finally {
                sshRemoteServerOperate.closeSession();
            }
        } else {
            boolean success = false;
            String userName = SshConfigConstant.SERVER_DEFAULT_USER_NAME;
            SshRemoteServerOperateForFmc sshRemoteServerOperate = new SshRemoteServerOperateForFmc();
            try {
                sshRemoteServerOperate.sshRemoteCallLogin(
                        "localhost",
                        userName,
                        SshConfigConstant.SERVER_DEFAULT_PASSWORD,
                        SshConfigConstant.DEFAULT_SERVER_PORT);
                boolean logined = sshRemoteServerOperate.isLogined();
                if (logined) {
                    // 重启
                    sshRemoteServerOperate.execCommand("sudo reboot");
                    logger.info("重启系统成功");
                } else {
                    logger.info("主机登录失败");
                    throw new CustomException("登录主机发生错误");
                }
            } catch (Exception e) {
                logger.info("更新FMC发生错误", e);
            } finally {
                sshRemoteServerOperate.closeSession();
            }
        }

    }
}
