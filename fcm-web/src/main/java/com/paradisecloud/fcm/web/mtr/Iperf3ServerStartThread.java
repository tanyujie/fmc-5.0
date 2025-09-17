package com.paradisecloud.fcm.web.mtr;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.terminal.fs.constant.FcmConfigConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Iperf3ServerStartThread extends Thread {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void run() {
        logger.debug("iperf3服务端模式开启");

        try {
            startServer();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.debug("iperf3服务端模式结束");
    }

    private void startServer() throws Exception {
        String sourceServerIp = StringUtils.isNotEmpty(MtrMonitorThreadCache.debugSourceServer) ? MtrMonitorThreadCache.debugSourceServer : "127.0.0.1";
        if ("127.0.0.1".equals(sourceServerIp)) {
            if (StringUtils.isNotEmpty(MtrMonitorThreadCache.getInstance().getIperf3ServerPid())) {
                if (isWindows()) {
                    Runtime.getRuntime().exec("taskkill /f /pid " + MtrMonitorThreadCache.getInstance().getIperf3ServerPid());
                } else {
                    Runtime.getRuntime().exec("kill -9 " + MtrMonitorThreadCache.getInstance().getIperf3ServerPid());
                }
                MtrMonitorThreadCache.getInstance().setIperf3ServerPid(null);
            }
            MtrMonitorThreadCache.getInstance().setIperf3ServerStarted(true);
            Scanner scanner = new Scanner(Runtime.getRuntime().exec("iperf3 -s").getInputStream());
            while (scanner.hasNextLine()) {
                if (isInterrupted()) {
                    break;
                }
                String newText = scanner.nextLine();
                logger.debug(newText);
                if (MtrMonitorThreadCache.getInstance().isIperf3ServerStarted()) {
                    MtrMonitorThreadCache.getInstance().appendServerMsg(newText + "\n");
                }
            }
            scanner.close();
        } else {
            String region = ExternalConfigCache.getInstance().getRegion();
            if ("ops".equalsIgnoreCase(region)) {
                if (StringUtils.isNotEmpty(MtrMonitorThreadCache.getInstance().getIperf3ServerPid())) {
                    if (isWindows()) {
                        Runtime.getRuntime().exec("taskkill /f /pid " + MtrMonitorThreadCache.getInstance().getIperf3ServerPid());
                    } else {
                        Runtime.getRuntime().exec("kill -9 " + MtrMonitorThreadCache.getInstance().getIperf3ServerPid());
                    }
                    MtrMonitorThreadCache.getInstance().setIperf3ServerPid(null);
                }
                MtrMonitorThreadCache.getInstance().setIperf3ServerStarted(true);
                Scanner scanner = new Scanner(Runtime.getRuntime().exec("iperf3 -s").getInputStream());
                while (scanner.hasNextLine()) {
                    if (isInterrupted()) {
                        break;
                    }
                    String newText = scanner.nextLine();
                    logger.debug(newText);
                    if (MtrMonitorThreadCache.getInstance().isIperf3ServerStarted()) {
                        MtrMonitorThreadCache.getInstance().appendServerMsg(newText + "\n");
                    }
                }
                scanner.close();
            } else {
                try {
                    if (!SshRemoteServerOperateForServerStart.getInstance().isLogined()) {
                        SshRemoteServerOperateForServerStart.getInstance().sshRemoteCallLogin(sourceServerIp, FcmConfigConstant.SERVER_DEFAULT_USER_NAME, FcmConfigConstant.DEFAULT_PASSWORD, FcmConfigConstant.DEFAULT_SERVER_PORT);
                    }
                    if (SshRemoteServerOperateForServerStart.getInstance().isLogined()) {
                        if (StringUtils.isNotEmpty(MtrMonitorThreadCache.getInstance().getIperf3ServerPid())) {
                            SshRemoteServerOperateForServerStart.getInstance().execCommand("kill -9 " + MtrMonitorThreadCache.getInstance().getIperf3ServerPid());
                            MtrMonitorThreadCache.getInstance().setIperf3ServerPid(null);
                        }
                        String result = SshRemoteServerOperateForServerStart.getInstance().execCommand("iperf3 -s");
                        logger.debug(result);
                        MtrMonitorThreadCache.getInstance().appendServerMsg(result + "\n");
                    }
                } catch (Exception e) {
                    SshRemoteServerOperateForServerStart.getInstance().closeSession();
                    logger.error("iperf3服务启动信息错误。", e);
                    throw e;
                } finally {
//                    SshRemoteServerOperateForServerStart.getInstance().closeSession();
                }
            }
        }
    }

    private boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            return true;
        } else {
            return false;
        }
    }
}
