package com.paradisecloud.fcm.web.task;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.dao.model.BusiOperationLog;
import com.paradisecloud.fcm.service.interfaces.IBusiOperationLogService;
import com.paradisecloud.fcm.web.cache.SystemUpdateCache;
import com.paradisecloud.fcm.web.encrypt.ResultValue;
import com.paradisecloud.fcm.web.encrypt.SecurityUtility;
import com.paradisecloud.fcm.web.encrypt.ZipUtility;
import com.paradisecloud.fcm.web.utils.IpUtil;
import com.paradisecloud.fcm.web.utils.SshConfigConstant;
import com.paradisecloud.fcm.web.utils.SshRemoteServerOperateForFmc;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class UpdateFmcTask extends Task {

    private static final Logger logger = LoggerFactory.getLogger(UpdateFmcTask.class);

    private String filePath;

    public UpdateFmcTask(String id, long delayInMilliseconds, String filePath) {
        super("update_fmc_" + id, delayInMilliseconds);
        this.filePath = filePath;
    }

    @Override
    public void run() {
        logger.info("更新FMC任务启动");
        SystemUpdateCache.updateFmcUpdateStatus(SystemUpdateCache.UPDATE_STATUS_ANALYZING);
        IBusiOperationLogService busiOperationLogService = BeanFactory.getBean(IBusiOperationLogService.class);

        File updateFile = new File(filePath);
        if (!updateFile.exists()) {
            SystemUpdateCache.updateFmcUpdateStatus(SystemUpdateCache.UPDATE_STATUS_ANALYSIS_ERROR);
            try {
                BusiOperationLog busiOperationLog = new BusiOperationLog();
                busiOperationLog.setActionDetails("升级FMC系统：" + "解析文件发生错误");
                busiOperationLog.setTime(new Date());
                busiOperationLog.setActionResult(2);
                busiOperationLogService.insertBusiOperationLog(busiOperationLog);
            } catch (Exception e) {
            }
            return;
        }

        File parentFile = updateFile.getParentFile();

        String fileType = "A";
        String sn = fileType;
        String outputFilePath = parentFile.getAbsolutePath() + "/temp";
        File outputFile = new File(outputFilePath);
        if (outputFile.exists() && outputFile.isDirectory()) {
            try {
                FileUtils.deleteDirectory(outputFile);
            } catch (IOException ioe) {
                logger.info("删除文件发生错误", ioe);
                SystemUpdateCache.updateFmcUpdateStatus(SystemUpdateCache.UPDATE_STATUS_ANALYSIS_ERROR);
                try {
                    BusiOperationLog busiOperationLog = new BusiOperationLog();
                    busiOperationLog.setActionDetails("升级FMC系统：" + "删除文件发生错误");
                    busiOperationLog.setTime(new Date());
                    busiOperationLog.setActionResult(2);
                    busiOperationLogService.insertBusiOperationLog(busiOperationLog);
                } catch (Exception e) {
                }
                return;
            }
        }
        SecurityUtility securityUtility = new SecurityUtility();
        ResultValue resultValue = securityUtility.decryptFile(filePath, outputFilePath, sn);
        List<String> messages = resultValue.getMessages();
        if (messages != null && messages.size() > 0) {
            for (String message : messages) {
                logger.info(message);
            }
        }
        if (resultValue.isSuccess()) {
            logger.info("===解析文件成功===");
        } else {
            logger.info("!!!解析文件失败!!!");
            SystemUpdateCache.updateFmcUpdateStatus(SystemUpdateCache.UPDATE_STATUS_ANALYSIS_ERROR);
            try {
                BusiOperationLog busiOperationLog = new BusiOperationLog();
                busiOperationLog.setActionDetails("升级FMC系统：" + "验证文件发生错误");
                busiOperationLog.setTime(new Date());
                busiOperationLog.setActionResult(2);
                busiOperationLogService.insertBusiOperationLog(busiOperationLog);
            } catch (Exception e) {
            }
            return;
        }

        SystemUpdateCache.updateFmcUpdateStatus(SystemUpdateCache.UPDATE_STATUS_UPDATING);

        String version = "";
        BufferedReader fileReader = null;
        try {
            File versionFile = new File(outputFilePath + "/version");
            if (versionFile.exists() && versionFile.isFile()) {
                fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(versionFile)));
                version = fileReader.readLine();
                fileReader.close();
            }
        } catch (Exception e) {
            logger.error("读取版本信息错误", e);
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                }
            }
        }

        // 压缩文件
        String zipFilePath = parentFile.getAbsolutePath() + "/fmc.zip";
        File zipFile = new File(zipFilePath);
        zipFile.delete();
        boolean result = ZipUtility.zipFile(outputFilePath, zipFilePath, "");
        zipFile = new File(zipFilePath);
        if (!result || !zipFile.exists() || !zipFile.isFile()) {
            logger.info("压缩文件发生错误");
            SystemUpdateCache.updateFmcUpdateStatus(SystemUpdateCache.UPDATE_STATUS_ANALYSIS_ERROR);
            try {
                BusiOperationLog busiOperationLog = new BusiOperationLog();
                busiOperationLog.setActionDetails("升级FMC系统：" + "压缩文件发生错误");
                busiOperationLog.setTime(new Date());
                busiOperationLog.setActionResult(2);
                busiOperationLogService.insertBusiOperationLog(busiOperationLog);
            } catch (Exception e) {
            }
            return;
        }

        try {
            updateFmc(zipFilePath, version, busiOperationLogService);
        } catch (Exception e) {
            String errorMsg = "";
            if (e instanceof CustomException) {
                errorMsg = e.getMessage();
            }
            logger.info(errorMsg);
            SystemUpdateCache.updateFmcUpdateStatus(SystemUpdateCache.UPDATE_STATUS_UPDATE_ERROR);
            try {
                BusiOperationLog busiOperationLog = new BusiOperationLog();
                busiOperationLog.setActionDetails("升级FMC系统：" + errorMsg);
                busiOperationLog.setTime(new Date());
                busiOperationLog.setActionResult(2);
                busiOperationLogService.insertBusiOperationLog(busiOperationLog);
            } catch (Exception e1) {
            }
            return;
        }
    }

    /**
     * 更新FMC
     *
     * @throws Exception
     */
    private void updateFmc(String zipFilePath, String version, IBusiOperationLogService busiOperationLogService) throws Exception {
        String localIp = IpUtil.getLocalIp();
        String region = ExternalConfigCache.getInstance().getRegion();
        if (true) {
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
                    // 删除既存文件
                    String remoteZipFilePath = "/home/upload/fmc/fmc.zip";
                    String remoteTempDirPath = "/home/upload/fmc/temp";
                    sshRemoteServerOperate.execCommand("mkdir " + "/home/upload/fmc");
                    String result = sshRemoteServerOperate.execCommand("ls " + remoteZipFilePath);
                    if (result.endsWith("\n")) {
                        result = result.substring(0, result.length() - 1);
                    }
                    if (remoteZipFilePath.equals(result)) {
                        sshRemoteServerOperate.execCommand("rm -r " + remoteZipFilePath);
                        sshRemoteServerOperate.execCommand("rm -rf " + remoteTempDirPath);
                    }
                    // 上传文件
                    sshRemoteServerOperate.uploadFile(remoteZipFilePath, zipFilePath);
                    // 检查文件
                    result = sshRemoteServerOperate.execCommand("ls " + remoteZipFilePath);
                    if (result.endsWith("\n")) {
                        result = result.substring(0, result.length() - 1);
                    }
                    if (!remoteZipFilePath.equals(result)) {
                        throw new CustomException("传输文件发生错误");
                    }
                    String remoteTempDirJarPath = remoteTempDirPath + "/fcm-application.jar";
                    String remoteTempDirHtmlFcmPath = remoteTempDirPath + "/fcm";
                    String remoteTempDirHtmlFcmIndexPath = remoteTempDirPath + "/fcm/index.html";
                    String remoteTempDirHtmlMobilePath = remoteTempDirPath + "/mobile";
                    String remoteTempDirHtmlMobileIndexPath = remoteTempDirPath + "/mobile/index.html";
                    String remoteTempDirScriptsPath = remoteTempDirPath + "/scripts";
                    String remoteHtmlDirPath = "/home/all_confs/nginx/html";
                    String remoteHtmlDirFcmDirPath = remoteHtmlDirPath + "/fcm";
                    String remoteHtmlDirMobileDirPath = remoteHtmlDirPath + "/mobile";
                    String remoteAppDirPath = "/home/all_confs/fmm/lib";
                    String remoteAppDirJarPath = remoteAppDirPath + "/fcm-application.jar";
                    String remoteAppDirHtmlFcmIndexPath = remoteAppDirPath + "/fcm/index.html";
                    String remoteAppDirHtmlMobileIndexPath = remoteAppDirPath + "/mobile/index.html";
                    String remoteAppBakDirPath = "/home/all_confs/fmm/lib_bak";
                    String remoteAppBakDirJarPath = remoteAppBakDirPath + "/fcm-application.jar";
                    String remoteScriptsPath = "/home/all_confs/scripts";
                    result = sshRemoteServerOperate.execCommand("unzip " + remoteZipFilePath + " -d " + remoteTempDirPath);
                    result = sshRemoteServerOperate.execCommand("ls " + remoteTempDirJarPath);
                    if (result.endsWith("\n")) {
                        result = result.substring(0, result.length() - 1);
                    }
                    // 备份旧
                    // 不更新jar文件时不做备份
                    // 从没有备份时需要备份
                    boolean needBackup = false;
                    if (remoteTempDirJarPath.equals(result)) {
                        needBackup = true;
                    }

                    if (needBackup) {
                        //备份html
                        result = sshRemoteServerOperate.execCommand("ls " + remoteAppDirHtmlFcmIndexPath);
                        if (result.endsWith("\n")) {
                            result = result.substring(0, result.length() - 1);
                        }
                        if (!remoteAppDirHtmlFcmIndexPath.equals(result)) {
                            logger.info("拷贝文件 " + remoteHtmlDirFcmDirPath + " => " + remoteAppDirPath);
                            result = sshRemoteServerOperate.execCommand("cp -rf " + remoteHtmlDirFcmDirPath + " " + remoteAppDirPath);
                        }
                        //备份mobile
                        result = sshRemoteServerOperate.execCommand("ls " + remoteAppDirHtmlMobileIndexPath);
                        if (result.endsWith("\n")) {
                            result = result.substring(0, result.length() - 1);
                        }
                        if (!remoteAppDirHtmlMobileIndexPath.equals(result)) {
                            logger.info("拷贝文件 " + remoteHtmlDirMobileDirPath + " => " + remoteAppDirPath);
                            result = sshRemoteServerOperate.execCommand("cp -rf " + remoteHtmlDirMobileDirPath + " " + remoteAppDirPath);
                        }
                        //删除lib_bak
                        result = sshRemoteServerOperate.execCommand("ls " + remoteAppBakDirJarPath);
                        if (result.endsWith("\n")) {
                            result = result.substring(0, result.length() - 1);
                        }
                        if (remoteAppBakDirJarPath.equals(result)) {
                            result = sshRemoteServerOperate.execCommand("rm -rf " + remoteAppBakDirPath);
                        }
                        //备份新的lib_bak
                        result = sshRemoteServerOperate.execCommand("ls " + remoteAppDirJarPath);
                        if (result.endsWith("\n")) {
                            result = result.substring(0, result.length() - 1);
                        }
                        if (remoteAppDirJarPath.equals(result)) {
                            logger.info("备份文件 " + remoteAppDirPath + " => " + remoteAppBakDirPath);
                            result = sshRemoteServerOperate.execCommand("cp -rf " + remoteAppDirPath + "/. " + remoteAppBakDirPath);
                        }
                    }

                    // 更新html
                    boolean needUpdateHtmlFcm = false;
                    result = sshRemoteServerOperate.execCommand("ls " + remoteTempDirHtmlFcmIndexPath);
                    if (result.endsWith("\n")) {
                        result = result.substring(0, result.length() - 1);
                    }
                    if (remoteTempDirHtmlFcmIndexPath.equals(result)) {
                        needUpdateHtmlFcm = true;
                    }
                    // 拷贝html
                    if (needUpdateHtmlFcm) {
                        logger.info("删除html： " + remoteHtmlDirFcmDirPath);
                        result = sshRemoteServerOperate.execCommand("rm -rf " + remoteHtmlDirFcmDirPath);
                        logger.info("拷贝文件 " + remoteTempDirHtmlFcmPath + " => " + remoteHtmlDirPath);
                        result = sshRemoteServerOperate.execCommand("cp -rf " + remoteTempDirHtmlFcmPath + " " + remoteHtmlDirPath);
                        // 删除元有的html
                        result = sshRemoteServerOperate.execCommand("rm -rf " + remoteAppDirPath + "/fcm");
                    }

                    // 更新mobile
                    boolean needUpdateHtmlMobile = false;
                    result = sshRemoteServerOperate.execCommand("ls " + remoteTempDirHtmlMobileIndexPath);
                    if (result.endsWith("\n")) {
                        result = result.substring(0, result.length() - 1);
                    }
                    if (remoteTempDirHtmlMobileIndexPath.equals(result)) {
                        needUpdateHtmlMobile = true;
                    }
                    // 拷贝mobile
                    if (needUpdateHtmlMobile) {
                        logger.info("删除mobile： " + remoteHtmlDirMobileDirPath);
                        result = sshRemoteServerOperate.execCommand("rm -rf " + remoteHtmlDirMobileDirPath);
                        logger.info("拷贝文件 " + remoteTempDirHtmlMobilePath + " => " + remoteHtmlDirPath);
                        result = sshRemoteServerOperate.execCommand("cp -rf " + remoteTempDirHtmlMobilePath + " " + remoteHtmlDirPath);
                        // 删除元有的mobile
                        result = sshRemoteServerOperate.execCommand("rm -rf " + remoteAppDirPath + "/mobile");
                    }

                    // 更新scripts
                    boolean needUpdateScripts = false;
                    result = sshRemoteServerOperate.execCommand("ls " + remoteTempDirScriptsPath);
                    if (result.endsWith("\n")) {
                        result = result.substring(0, result.length() - 1);
                    }
                    if (result.contains("No such")) {
                        needUpdateScripts = false;
                    } else {
                        needUpdateScripts = true;
                    }
                    // 拷贝html
                    if (needUpdateScripts) {
                        logger.info("拷贝文件 " + remoteTempDirScriptsPath + " => " + remoteScriptsPath);
                        result = sshRemoteServerOperate.execCommand("cp -rf " + remoteTempDirScriptsPath + "/. " + remoteScriptsPath);
                    }

                    // 拷贝新
                    result = sshRemoteServerOperate.execCommand("cp -rf " + remoteTempDirPath + "/. " + remoteAppDirPath);

                    try {
                        BusiOperationLog busiOperationLog = new BusiOperationLog();
                        busiOperationLog.setActionDetails("升级FMC系统：" + "更新成功");
                        busiOperationLog.setTime(new Date());
                        busiOperationLog.setActionResult(1);
                        busiOperationLogService.insertBusiOperationLog(busiOperationLog);
                    } catch (Exception e) {
                    }

                    SystemUpdateCache.updateFmcUpdateStatus(SystemUpdateCache.UPDATE_STATUS_UPDATED);
                    SystemUpdateCache.updateFmcUpdateStatus(SystemUpdateCache.UPDATE_STATUS_REBOOTING);

                    // 重启
//                    sshRemoteServerOperate.execCommand("sudo reboot");
//                    logger.info("重启系统成功");
//                    try {
//                        BusiOperationLog busiOperationLog = new BusiOperationLog();
//                        busiOperationLog.setActionDetails("升级FMC系统：" + "重启系统成功");
//                        busiOperationLog.setTime(new Date());
//                        busiOperationLog.setActionResult(2);
//                        busiOperationLogService.insertBusiOperationLog(busiOperationLog);
//                    } catch (Exception e) {
//                    }
                    logger.info("更新主机状态成功");
                    SystemRebootTask systemRebootTask = new SystemRebootTask("reboot", 5000);
                    BeanFactory.getBean(TaskService.class).addTask(systemRebootTask);
                } else {
                    logger.info("主机登录失败");
                    throw new CustomException("登录主机发生错误");
                }
            } catch (Exception e) {
                logger.info("更新FMC发生错误", e);
                if (e instanceof CustomException) {
                    throw e;
                } else {
                    throw new CustomException("更新FMC发生错误");
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
                    // 删除既存文件
                    String remoteZipFilePath = "/home/upload/fmc/fmc.zip";
                    String remoteTempDirPath = "/home/upload/fmc/temp";
                    String result = sshRemoteServerOperate.execCommand("ls " + remoteZipFilePath);
                    if (result.endsWith("\n")) {
                        result = result.substring(0, result.length() - 1);
                    }
                    if (remoteZipFilePath.equals(result)) {
                        sshRemoteServerOperate.execCommand("rm -r " + remoteZipFilePath);
                        sshRemoteServerOperate.execCommand("rm -rf " + remoteTempDirPath);
                    }
                    // 上传文件
                    sshRemoteServerOperate.uploadFile(remoteZipFilePath, zipFilePath);
                    // 检查文件
                    result = sshRemoteServerOperate.execCommand("ls " + remoteZipFilePath);
                    if (result.endsWith("\n")) {
                        result = result.substring(0, result.length() - 1);
                    }
                    if (!remoteZipFilePath.equals(result)) {
                        throw new CustomException("传输文件发生错误");
                    }
                    String remoteTempDirJarPath = remoteTempDirPath + "/fcm-application.jar";
                    String remoteTempDirHtmlFcmPath = remoteTempDirPath + "/fcm";
                    String remoteTempDirHtmlFcmIndexPath = remoteTempDirPath + "/fcm/index.html";
                    String remoteTempDirHtmlMobilePath = remoteTempDirPath + "/mobile";
                    String remoteTempDirHtmlMobileIndexPath = remoteTempDirPath + "/mobile/index.html";
                    String remoteHtmlDirPath = "/home/html";
                    String remoteHtmlDirFcmDirPath = remoteHtmlDirPath + "/fcm";
                    String remoteHtmlDirMobileDirPath = remoteHtmlDirPath + "/mobile";
                    String remoteAppDirPath = "/home/fcm/fcm-application/lib";
                    String remoteAppDirJarPath = remoteAppDirPath + "/fcm-application.jar";
                    String remoteAppDirHtmlFcmIndexPath = remoteAppDirPath + "/fcm/index.html";
                    String remoteAppDirHtmlMobileIndexPath = remoteAppDirPath + "/mobile/index.html";
                    String remoteAppBakDirPath = "/home/fcm/fcm-application/lib_bak";
                    String remoteAppBakDirJarPath = remoteAppBakDirPath + "/fcm-application.jar";
                    result = sshRemoteServerOperate.execCommand("unzip " + remoteZipFilePath + " -d " + remoteTempDirPath);
                    result = sshRemoteServerOperate.execCommand("ls " + remoteTempDirJarPath);
                    if (result.endsWith("\n")) {
                        result = result.substring(0, result.length() - 1);
                    }
                    // 备份旧
                    // 不更新jar文件时不做备份
                    // 从没有备份时需要备份
                    boolean needBackup = false;
                    if (remoteTempDirJarPath.equals(result)) {
                        needBackup = true;
                    }

                    if (needBackup) {
                        //备份html
                        result = sshRemoteServerOperate.execCommand("ls " + remoteAppDirHtmlFcmIndexPath);
                        if (result.endsWith("\n")) {
                            result = result.substring(0, result.length() - 1);
                        }
                        if (!remoteAppDirHtmlFcmIndexPath.equals(result)) {
                            logger.info("拷贝文件 " + remoteHtmlDirFcmDirPath + " => " + remoteAppDirPath);
                            result = sshRemoteServerOperate.execCommand("cp -rf " + remoteHtmlDirFcmDirPath + " " + remoteAppDirPath);
                        }
                        //备份mobile
                        result = sshRemoteServerOperate.execCommand("ls " + remoteAppDirHtmlMobileIndexPath);
                        if (result.endsWith("\n")) {
                            result = result.substring(0, result.length() - 1);
                        }
                        if (!remoteAppDirHtmlMobileIndexPath.equals(result)) {
                            logger.info("拷贝文件 " + remoteHtmlDirMobileDirPath + " => " + remoteAppDirPath);
                            result = sshRemoteServerOperate.execCommand("cp -rf " + remoteHtmlDirMobileDirPath + " " + remoteAppDirPath);
                        }
                        //删除lib_bak
                        result = sshRemoteServerOperate.execCommand("ls " + remoteAppBakDirJarPath);
                        if (result.endsWith("\n")) {
                            result = result.substring(0, result.length() - 1);
                        }
                        if (remoteAppBakDirJarPath.equals(result)) {
                            result = sshRemoteServerOperate.execCommand("rm -rf " + remoteAppBakDirPath);
                        }
                        //备份新的lib_bak
                        result = sshRemoteServerOperate.execCommand("ls " + remoteAppDirJarPath);
                        if (result.endsWith("\n")) {
                            result = result.substring(0, result.length() - 1);
                        }
                        if (remoteAppDirJarPath.equals(result)) {
                            logger.info("备份文件 " + remoteAppDirPath + " => " + remoteAppBakDirPath);
                            result = sshRemoteServerOperate.execCommand("cp -rf " + remoteAppDirPath + "/. " + remoteAppBakDirPath);
                        }
                    }

                    // 更新html
                    boolean needUpdateHtmlFcm = false;
                    result = sshRemoteServerOperate.execCommand("ls " + remoteTempDirHtmlFcmIndexPath);
                    if (result.endsWith("\n")) {
                        result = result.substring(0, result.length() - 1);
                    }
                    if (remoteTempDirHtmlFcmIndexPath.equals(result)) {
                        needUpdateHtmlFcm = true;
                    }
                    // 拷贝html
                    if (needUpdateHtmlFcm) {
                        logger.info("删除html： " + remoteHtmlDirFcmDirPath);
                        result = sshRemoteServerOperate.execCommand("rm -rf " + remoteHtmlDirFcmDirPath);
                        logger.info("拷贝文件 " + remoteTempDirHtmlFcmPath + " => " + remoteHtmlDirPath);
                        result = sshRemoteServerOperate.execCommand("cp -rf " + remoteTempDirHtmlFcmPath + " " + remoteHtmlDirPath);
                        // 删除元有的html
                        result = sshRemoteServerOperate.execCommand("rm -rf " + remoteAppDirPath + "/fcm");
                    }

                    // 更新mobile
                    boolean needUpdateHtmlMobile = false;
                    result = sshRemoteServerOperate.execCommand("ls " + remoteTempDirHtmlMobileIndexPath);
                    if (result.endsWith("\n")) {
                        result = result.substring(0, result.length() - 1);
                    }
                    if (remoteTempDirHtmlMobileIndexPath.equals(result)) {
                        needUpdateHtmlMobile = true;
                    }
                    // 拷贝mobile
                    if (needUpdateHtmlMobile) {
                        logger.info("删除mobile： " + remoteHtmlDirMobileDirPath);
                        result = sshRemoteServerOperate.execCommand("rm -rf " + remoteHtmlDirMobileDirPath);
                        logger.info("拷贝文件 " + remoteTempDirHtmlMobilePath + " => " + remoteHtmlDirPath);
                        result = sshRemoteServerOperate.execCommand("cp -rf " + remoteTempDirHtmlMobilePath + " " + remoteHtmlDirPath);
                        // 删除元有的mobile
                        result = sshRemoteServerOperate.execCommand("rm -rf " + remoteAppDirPath + "/mobile");
                    }

                    // 拷贝新
                    result = sshRemoteServerOperate.execCommand("cp -rf " + remoteTempDirPath + "/. " + remoteAppDirPath);

                    try {
                        BusiOperationLog busiOperationLog = new BusiOperationLog();
                        busiOperationLog.setActionDetails("升级FMC系统：" + "更新成功");
                        busiOperationLog.setTime(new Date());
                        busiOperationLog.setActionResult(1);
                        busiOperationLogService.insertBusiOperationLog(busiOperationLog);
                    } catch (Exception e) {
                    }

                    SystemUpdateCache.updateFmcUpdateStatus(SystemUpdateCache.UPDATE_STATUS_UPDATED);
                    SystemUpdateCache.updateFmcUpdateStatus(SystemUpdateCache.UPDATE_STATUS_REBOOTING);

                    // 重启
//                    sshRemoteServerOperate.execCommand("sudo reboot");
//                    logger.info("重启系统成功");
//                    try {
//                        BusiOperationLog busiOperationLog = new BusiOperationLog();
//                        busiOperationLog.setActionDetails("升级FMC系统：" + "重启系统成功");
//                        busiOperationLog.setTime(new Date());
//                        busiOperationLog.setActionResult(2);
//                        busiOperationLogService.insertBusiOperationLog(busiOperationLog);
//                    } catch (Exception e) {
//                    }
                    logger.info("更新主机状态成功");
                    SystemRebootTask systemRebootTask = new SystemRebootTask("reboot", 5000);
                    BeanFactory.getBean(TaskService.class).addTask(systemRebootTask);
                } else {
                    logger.info("主机登录失败");
                    throw new CustomException("登录主机发生错误");
                }
            } catch (Exception e) {
                logger.info("更新FMC发生错误");
                if (e instanceof CustomException) {
                    throw e;
                } else {
                    throw new CustomException("更新FMC发生错误");
                }
            } finally {
                sshRemoteServerOperate.closeSession();
            }
        }

    }
}
