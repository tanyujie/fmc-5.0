package com.paradisecloud.fcm.web.mtr;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.dao.mapper.BusiMtrMapper;
import com.paradisecloud.fcm.dao.model.BusiMtr;
import com.paradisecloud.fcm.terminal.fs.constant.FcmConfigConstant;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MtrThread extends Thread {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void run() {

        while (true) {
            if (isInterrupted()) {
                return;
            }
            logger.debug("MTR探测任务开始");
            MtrMonitorThreadCache.getInstance().setMtrChangesTime(System.currentTimeMillis());
            boolean needCloseSsh = true;

            BusiMtrMapper busiMtrMapper = BeanFactory.getBean(BusiMtrMapper.class);
            try {
                List<BusiMtr> busiMtrListLong = busiMtrMapper.selectBusiMtrListForLongNotEnd();
                if (busiMtrListLong != null && busiMtrListLong.size() > 0) {
                    needCloseSsh = false;
                    BusiMtr busiMtr = busiMtrListLong.get(0);
                    if (StringUtils.isEmpty(busiMtr.getFileName())) {
                        busiMtr.setFileName("mtr_" + busiMtr.getId() + ".txt");
                    }
                    if (StringUtils.isNotEmpty(busiMtr.getPid())) {
                        boolean running = checkMtr(busiMtr);
                        if (!running) {
                            busiMtr.setStatus(1);
                            busiMtr.setUpdateTime(new Date());
                            busiMtrMapper.updateBusiMtr(busiMtr);
                        }
                    } else {
                        String pid = startMtr(busiMtr);
                        if (StringUtils.isNotEmpty(pid)) {
                            busiMtr.setPid(pid);
                            busiMtrMapper.updateBusiMtr(busiMtr);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            try {
                List<BusiMtr> busiMtrListShort = busiMtrMapper.selectBusiMtrListForShortNotEnd();
                if (busiMtrListShort != null && busiMtrListShort.size() > 0) {
                    needCloseSsh = false;
                    BusiMtr busiMtr = busiMtrListShort.get(0);
                    if (StringUtils.isEmpty(busiMtr.getFileName())) {
                        busiMtr.setFileName("mtr_" + busiMtr.getId() + ".txt");
                    }
                    if (StringUtils.isNotEmpty(busiMtr.getPid())) {
                        boolean running = checkMtr(busiMtr);
                        if (!running) {
                            busiMtr.setStatus(1);
                            busiMtr.setUpdateTime(new Date());
                            busiMtrMapper.updateBusiMtr(busiMtr);
                        }
                    } else {
                        String pid = startMtr(busiMtr);
                        if (StringUtils.isNotEmpty(pid)) {
                            busiMtr.setPid(pid);
                            busiMtrMapper.updateBusiMtr(busiMtr);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            try {
                List<BusiMtr> busiMtrListDeleted = busiMtrMapper.selectBusiMtrListForDeletedNotEnd();
                if (busiMtrListDeleted != null && busiMtrListDeleted.size() > 0) {
                    needCloseSsh = false;
                    for (int i = 0; i < busiMtrListDeleted.size(); i++) {
                        BusiMtr busiMtr = busiMtrListDeleted.get(i);
                        if (StringUtils.isNotEmpty(busiMtr.getPid())) {
                            boolean running = checkMtr(busiMtr);
                            if (running) {
                                stopMtr(busiMtr);
                            } else {
                                busiMtr.setStatus(1);
                                busiMtr.setUpdateTime(new Date());
                                busiMtrMapper.updateBusiMtr(busiMtr);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            try {
                if (needCloseSsh) {
                    SshRemoteServerOperateForMtr.getInstance().closeSession();
                }
            } catch (Exception e) {
            }
            logger.debug("MTR探测任务结束");

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private String startMtr(BusiMtr busiMtr) {
        String pid = null;
        String region = ExternalConfigCache.getInstance().getRegion();
        if ("ops".equalsIgnoreCase(region)) {
            String cmdStr = "mtr -r -c " + busiMtr.getTimes() + " " + busiMtr.getTargetIp() + " > /home/mtr/" + busiMtr.getFileName() + " & echo $!";
            String[] cmd = {"sh", "-c", cmdStr};
            String result = exec(cmd, 5);
            logger.error("startMtr result:[" + result + "]" + "," + result.length());
            if (StringUtils.isNotEmpty(result)) {
                for (int i = 0; i < 3; i++) {
                    result = result.replace("\r\n", "");
                    result = result.replace("\r", "");
                    result = result.replace("\n", "");
                }
                result = result.trim();
                logger.error("startMtr result 2:[" + result + "]" + "," + result.length());
                if (NumberUtils.isDigits(result)) {
                    pid = result;
                    logger.error("startMtr pid:[" + pid + "]" + "," + pid.length());
                }
            }
        } else {
            try {
                if (!SshRemoteServerOperateForMtr.getInstance().isLogined()) {
                    String sourceServerIp = StringUtils.isNotEmpty(MtrMonitorThreadCache.debugSourceServer) ? MtrMonitorThreadCache.debugSourceServer : busiMtr.getSourceIp();
                    SshRemoteServerOperateForMtr.getInstance().sshRemoteCallLogin(sourceServerIp, FcmConfigConstant.SERVER_DEFAULT_USER_NAME, FcmConfigConstant.DEFAULT_PASSWORD, FcmConfigConstant.DEFAULT_SERVER_PORT);
                }
                if (SshRemoteServerOperateForMtr.getInstance().isLogined()) {
                    String result = SshRemoteServerOperateForMtr.getInstance().execCommand("mtr -r -c " + busiMtr.getTimes() + " " + busiMtr.getTargetIp() + " > /home/mtr/" + busiMtr.getFileName() + " & echo $!");
                    logger.error("startMtr result:" + result);
                    if (StringUtils.isNotEmpty(result)) {
                        result = result.replace("\n", "");
                        if (NumberUtils.isDigits(result)) {
                            pid = result;
                            logger.error("startMtr pid:" + pid);
                        }
                    }
                }
            } catch (Exception e) {
                SshRemoteServerOperateForMtr.getInstance().closeSession();
                logger.error("执行MTR信息错误。", e);
            } finally {
//                SshRemoteServerOperateForMtr.getInstance().closeSession();
            }
        }
        return pid;
    }

    private boolean checkMtr(BusiMtr busiMtr) throws Exception {
        boolean running = true;
        String region = ExternalConfigCache.getInstance().getRegion();
        if ("ops".equalsIgnoreCase(region)) {
            String cmdStr = "ps -p " + busiMtr.getPid();
            String[] cmd = {"sh", "-c", cmdStr};
            String result = exec(cmd, 5);
            logger.error(result);
            if (StringUtils.isNotEmpty(result)) {
                if (result.contains("mtr")) {
                    if (result.contains("Done")) {
                        running = false;
                    }
                } else {
                    running = false;
                }
            }
        } else {
            try {
                if (!SshRemoteServerOperateForMtr.getInstance().isLogined()) {
                    String sourceServerIp = StringUtils.isNotEmpty(MtrMonitorThreadCache.debugSourceServer) ? MtrMonitorThreadCache.debugSourceServer : busiMtr.getSourceIp();
                    SshRemoteServerOperateForMtr.getInstance().sshRemoteCallLogin(sourceServerIp, FcmConfigConstant.SERVER_DEFAULT_USER_NAME, FcmConfigConstant.DEFAULT_PASSWORD, FcmConfigConstant.DEFAULT_SERVER_PORT);
                }
                if (SshRemoteServerOperateForMtr.getInstance().isLogined()) {
                    String result = SshRemoteServerOperateForMtr.getInstance().execCommand("ps -p " + busiMtr.getPid());
                    logger.error(result);
                    if (StringUtils.isNotEmpty(result)) {
                        if (result.contains("mtr")) {
                            if (result.contains("Done")) {
                                running = false;
                            }
                        } else {
                            running = false;
                        }
                    }
                }
            } catch (Exception e) {
                SshRemoteServerOperateForMtr.getInstance().closeSession();
                logger.error("执行MTR信息错误。", e);
                throw e;
            } finally {
//                SshRemoteServerOperateForMtr.getInstance().closeSession();
            }
        }
        return running;
    }

    private void stopMtr(BusiMtr busiMtr) {
        String region = ExternalConfigCache.getInstance().getRegion();
        if ("ops".equalsIgnoreCase(region)) {
            String cmdStr = "kill -9 " + busiMtr.getPid();
            String[] cmd = {"sh", "-c", cmdStr};
            String result = exec(cmd, 5);
            logger.error(result);
        } else {
            try {
                if (!SshRemoteServerOperateForMtr.getInstance().isLogined()) {
                    String sourceServerIp = StringUtils.isNotEmpty(MtrMonitorThreadCache.debugSourceServer) ? MtrMonitorThreadCache.debugSourceServer : busiMtr.getSourceIp();
                    SshRemoteServerOperateForMtr.getInstance().sshRemoteCallLogin(sourceServerIp, FcmConfigConstant.SERVER_DEFAULT_USER_NAME, FcmConfigConstant.DEFAULT_PASSWORD, FcmConfigConstant.DEFAULT_SERVER_PORT);
                }
                if (SshRemoteServerOperateForMtr.getInstance().isLogined()) {
                    String result = SshRemoteServerOperateForMtr.getInstance().execCommand("kill -9 " + busiMtr.getPid());
                    logger.error(result);
                }
            } catch (Exception e) {
                SshRemoteServerOperateForMtr.getInstance().closeSession();
                logger.error("结束MTR信息错误。", e);
            } finally {
//                SshRemoteServerOperateForMtr.getInstance().closeSession();
            }
        }
    }


    private static String exec(String[] cmd, int timeout) {
        String os = System.getProperty("os.name").toLowerCase();
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            boolean res = process.waitFor(timeout, TimeUnit.SECONDS);
            if (!res) {
                return "error:time out";
            }
            InputStream inputStream = process.getInputStream();
            byte[] data = new byte[1024];
            StringBuilder result = new StringBuilder();
            while (inputStream.read(data) != -1) {
                if (os.contains("indows")) {
                    result.append(new String(data, "GBK"));
                } else {
                    result.append(new String(data, "UTF-8"));
                }
            }
            if (result.toString().equals("")) {
                InputStream errorStream = process.getErrorStream();
                while (errorStream.read(data) != -1) {
                    if (os.contains("indows")) {
                        result.append(new String(data, "GBK"));
                    } else {
                        result.append(new String(data, "UTF-8"));
                    }
                }
            }
            return result.toString();
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }
}
