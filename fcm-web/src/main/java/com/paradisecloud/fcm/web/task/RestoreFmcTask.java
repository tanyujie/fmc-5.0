package com.paradisecloud.fcm.web.task;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.dao.model.BusiOperationLog;
import com.paradisecloud.fcm.service.interfaces.IBusiOperationLogService;
import com.paradisecloud.fcm.web.cache.SystemUpdateCache;
import com.paradisecloud.fcm.web.utils.IpUtil;
import com.paradisecloud.fcm.web.utils.SshConfigConstant;
import com.paradisecloud.fcm.web.utils.SshRemoteServerOperateForFmc;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class RestoreFmcTask extends Task {

    private static final Logger logger = LoggerFactory.getLogger(RestoreFmcTask.class);

    public RestoreFmcTask(String id, long delayInMilliseconds) {
        super("restore_fmc_" + id, delayInMilliseconds);
    }

    @Override
    public void run() {
        IBusiOperationLogService busiOperationLogService = BeanFactory.getBean(IBusiOperationLogService.class);
        try {
            restoreFmc(busiOperationLogService);
        } catch (Exception e) {
            String errorMsg = "";
            if (e instanceof CustomException) {
                errorMsg = e.getMessage();
            }
            logger.info(errorMsg);
            SystemUpdateCache.updateFmcUpdateStatus(SystemUpdateCache.UPDATE_STATUS_RESTORE_ERROR);
            try {
                BusiOperationLog busiOperationLog = new BusiOperationLog();
                busiOperationLog.setActionDetails("还原FMC系统：" + errorMsg);
                busiOperationLog.setTime(new Date());
                busiOperationLog.setActionResult(2);
                busiOperationLogService.insertBusiOperationLog(busiOperationLog);
            } catch (Exception e1) {
            }
            return;
        }
    }

    /**
     * 还原FMC
     *
     * @throws Exception
     */
    private void restoreFmc(IBusiOperationLogService busiOperationLogService) throws Exception {
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
                    // 还原备份
                    String remoteHtmlDirPath = "/home/all_confs/nginx/html";
                    String remoteHtmlFcmDirPath = remoteHtmlDirPath + "/fcm";
                    String remoteHtmlMobileDirPath = remoteHtmlDirPath + "/mobile";
                    String remoteAppDirPath = "/home/all_confs/fmm/lib";
                    String remoteAppDirHtmlFcmDirPath = remoteAppDirPath + "/fcm";
                    String remoteAppDirHtmlFcmIndexPath = remoteAppDirPath + "/fcm/index.html";
                    String remoteAppDirHtmlMobileDirPath = remoteAppDirPath + "/mobile";
                    String remoteAppDirHtmlMobileIndexPath = remoteAppDirPath + "/mobile/index.html";
                    String remoteAppBakDirPath = "/home/all_confs/fmm/lib_bak";
                    String remoteAppBakDirJarPath = remoteAppBakDirPath + "/fcm-application.jar";
                    // 查找备份文件
                    String result = sshRemoteServerOperate.execCommand("ls " + remoteAppBakDirJarPath);
                    if (result.endsWith("\n")) {
                        result = result.substring(0, result.length() - 1);
                    }
                    if (remoteAppBakDirJarPath.equals(result)) {
                        logger.info("删除文件 " + remoteAppDirPath);
                        result = sshRemoteServerOperate.execCommand("rm -rf " + remoteAppDirPath);
                        logger.info("拷贝文件 " + remoteAppBakDirPath + " => " + remoteAppDirPath);
                        result = sshRemoteServerOperate.execCommand("cp -rf " + remoteAppBakDirPath + "/. " + remoteAppDirPath);
                    } else {
                        throw new CustomException("找不到可还原的文件");
                    }

                    // html
                    boolean needRestoreHtmlFcm = false;
                    result = sshRemoteServerOperate.execCommand("ls " + remoteAppDirHtmlFcmIndexPath);
                    if (result.endsWith("\n")) {
                        result = result.substring(0, result.length() - 1);
                    }
                    if (remoteAppDirHtmlFcmIndexPath.equals(result)) {
                        needRestoreHtmlFcm = true;
                    }

                    // 还原html
                    if (needRestoreHtmlFcm) {
                        logger.info("删除html： " + remoteHtmlFcmDirPath);
                        result = sshRemoteServerOperate.execCommand("rm -rf " + remoteHtmlFcmDirPath);
                        logger.info("拷贝文件 " + remoteAppDirHtmlFcmDirPath + " => " + remoteHtmlDirPath);
                        result = sshRemoteServerOperate.execCommand("cp -rf " + remoteAppDirHtmlFcmDirPath + " " + remoteHtmlDirPath);
                    }

                    // mobile
                    boolean needRestoreHtmlMobile = false;
                    result = sshRemoteServerOperate.execCommand("ls " + remoteAppDirHtmlMobileIndexPath);
                    if (result.endsWith("\n")) {
                        result = result.substring(0, result.length() - 1);
                    }
                    if (remoteAppDirHtmlMobileIndexPath.equals(result)) {
                        needRestoreHtmlMobile = true;
                    }

                    // 还原html
                    if (needRestoreHtmlMobile) {
                        logger.info("删除html： " + remoteHtmlMobileDirPath);
                        result = sshRemoteServerOperate.execCommand("rm -rf " + remoteHtmlMobileDirPath);
                        logger.info("拷贝文件 " + remoteAppDirHtmlMobileDirPath + " => " + remoteHtmlDirPath);
                        result = sshRemoteServerOperate.execCommand("cp -rf " + remoteAppDirHtmlMobileDirPath + " " + remoteHtmlDirPath);
                    }

                    try {
                        BusiOperationLog busiOperationLog = new BusiOperationLog();
                        busiOperationLog.setActionDetails("还原FMC系统：" + "还原成功");
                        busiOperationLog.setTime(new Date());
                        busiOperationLog.setActionResult(1);
                        busiOperationLogService.insertBusiOperationLog(busiOperationLog);
                    } catch (Exception e) {
                    }

                    SystemUpdateCache.updateFmcUpdateStatus(SystemUpdateCache.UPDATE_STATUS_RESTORED);
                    SystemUpdateCache.updateFmcUpdateStatus(SystemUpdateCache.UPDATE_STATUS_REBOOTING);

                    // 重启
//                    sshRemoteServerOperate.execCommand("sudo reboot");
//                    logger.info("重启系统成功");
                    SystemRebootTask systemRebootTask = new SystemRebootTask("reboot", 5000);
                    BeanFactory.getBean(TaskService.class).addTask(systemRebootTask);
                } else {
                    logger.info("系登录失败");
                    throw new CustomException("登录主机发生错误");
                }
            } catch (Exception e) {
                logger.info("还原FMC发生错误");
                if (e instanceof CustomException) {
                    throw e;
                } else {
                    throw new CustomException("还原FMC发生错误");
                }
            } finally {
                sshRemoteServerOperate.closeSession();
            }
        } else {
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
                    // 还原备份
                    String remoteHtmlDirPath = "/home/html";
                    String remoteHtmlFcmDirPath = remoteHtmlDirPath + "/fcm";
                    String remoteHtmlMobileDirPath = remoteHtmlDirPath + "/mobile";
                    String remoteAppDirPath = "/home/fcm/fcm-application/lib";
                    String remoteAppDirHtmlFcmDirPath = remoteAppDirPath + "/fcm";
                    String remoteAppDirHtmlFcmIndexPath = remoteAppDirPath + "/fcm/index.html";
                    String remoteAppDirHtmlMobileDirPath = remoteAppDirPath + "/mobile";
                    String remoteAppDirHtmlMobileIndexPath = remoteAppDirPath + "/mobile/index.html";
                    String remoteAppBakDirPath = "/home/fcm/fcm-application/lib_bak";
                    String remoteAppBakDirJarPath = remoteAppBakDirPath + "/fcm-application.jar";
                    // 查找备份文件
                    String result = sshRemoteServerOperate.execCommand("ls " + remoteAppBakDirJarPath);
                    if (result.endsWith("\n")) {
                        result = result.substring(0, result.length() - 1);
                    }
                    if (remoteAppBakDirJarPath.equals(result)) {
                        logger.info("删除文件 " + remoteAppDirPath);
                        result = sshRemoteServerOperate.execCommand("rm -rf " + remoteAppDirPath);
                        logger.info("拷贝文件 " + remoteAppBakDirPath + " => " + remoteAppDirPath);
                        result = sshRemoteServerOperate.execCommand("cp -rf " + remoteAppBakDirPath + "/. " + remoteAppDirPath);
                    } else {
                        throw new CustomException("找不到可还原的文件");
                    }

                    // html
                    boolean needRestoreHtmlFcm = false;
                    result = sshRemoteServerOperate.execCommand("ls " + remoteAppDirHtmlFcmIndexPath);
                    if (result.endsWith("\n")) {
                        result = result.substring(0, result.length() - 1);
                    }
                    if (remoteAppDirHtmlFcmIndexPath.equals(result)) {
                        needRestoreHtmlFcm = true;
                    }

                    // 还原html
                    if (needRestoreHtmlFcm) {
                        logger.info("删除html： " + remoteHtmlFcmDirPath);
                        result = sshRemoteServerOperate.execCommand("rm -rf " + remoteHtmlFcmDirPath);
                        logger.info("拷贝文件 " + remoteAppDirHtmlFcmDirPath + " => " + remoteHtmlDirPath);
                        result = sshRemoteServerOperate.execCommand("cp -rf " + remoteAppDirHtmlFcmDirPath + " " + remoteHtmlDirPath);
                    }

                    // mobile
                    boolean needRestoreHtmlMobile = false;
                    result = sshRemoteServerOperate.execCommand("ls " + remoteAppDirHtmlMobileIndexPath);
                    if (result.endsWith("\n")) {
                        result = result.substring(0, result.length() - 1);
                    }
                    if (remoteAppDirHtmlMobileIndexPath.equals(result)) {
                        needRestoreHtmlMobile = true;
                    }

                    // 还原html
                    if (needRestoreHtmlMobile) {
                        logger.info("删除html： " + remoteHtmlMobileDirPath);
                        result = sshRemoteServerOperate.execCommand("rm -rf " + remoteHtmlMobileDirPath);
                        logger.info("拷贝文件 " + remoteAppDirHtmlMobileDirPath + " => " + remoteHtmlDirPath);
                        result = sshRemoteServerOperate.execCommand("cp -rf " + remoteAppDirHtmlMobileDirPath + " " + remoteHtmlDirPath);
                    }

                    try {
                        BusiOperationLog busiOperationLog = new BusiOperationLog();
                        busiOperationLog.setActionDetails("还原FMC系统：" + "还原成功");
                        busiOperationLog.setTime(new Date());
                        busiOperationLog.setActionResult(1);
                        busiOperationLogService.insertBusiOperationLog(busiOperationLog);
                    } catch (Exception e) {
                    }

                    SystemUpdateCache.updateFmcUpdateStatus(SystemUpdateCache.UPDATE_STATUS_RESTORED);
                    SystemUpdateCache.updateFmcUpdateStatus(SystemUpdateCache.UPDATE_STATUS_REBOOTING);

                    // 重启
//                    sshRemoteServerOperate.execCommand("sudo reboot");
//                    logger.info("重启系统成功");
                    SystemRebootTask systemRebootTask = new SystemRebootTask("reboot", 2000);
                    BeanFactory.getBean(TaskService.class).addTask(systemRebootTask);
                } else {
                    logger.info("系登录失败");
                    throw new CustomException("登录主机发生错误");
                }
            } catch (Exception e) {
                logger.info("还原FMC发生错误");
                if (e instanceof CustomException) {
                    throw e;
                } else {
                    throw new CustomException("还原FMC发生错误");
                }
            } finally {
                sshRemoteServerOperate.closeSession();
            }
        }

    }
}
