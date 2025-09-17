package com.paradisecloud.fcm.web.mtr;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.service.conference.cache.BaseWebSocketMessagePusher;
import com.paradisecloud.fcm.terminal.fs.constant.FcmConfigConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Iperf3ServerStatusThread extends Thread {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void run() {

        while (true) {
            if (isInterrupted()) {
                return;
            }
            logger.debug("iperf3服务器检测任务开始");
            MtrMonitorThreadCache.getInstance().setIperf3ServerChangesTime(System.currentTimeMillis());
            boolean needCloseSsh = false;

            try {
                boolean running = checkIperf3();
                if (MtrMonitorThreadCache.getInstance().isIperf3ServerStarted()) {
                    if (!running) {
                        MtrMonitorThreadCache.getInstance().setIperf3ServerStarted(running);
                        MtrMonitorThreadCache.getInstance().clearServerMsg();
                        BaseWebSocketMessagePusher.getInstance().pushNetCheckServerMessage(WebsocketMessageType.NET_CHECK_SERVER_STOPPED, "服务端模式停止");
                    }
                } else {
                    if (running) {
                        BaseWebSocketMessagePusher.getInstance().pushNetCheckServerMessage(WebsocketMessageType.NET_CHECK_SERVER_STARTED, "服务端模式开启");
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            if (!MtrMonitorThreadCache.getInstance().isIperf3ServerStarted()) {
                if (System.currentTimeMillis() - MtrMonitorThreadCache.getInstance().getIperf3ServerRequestTime() > 300000) {
                    needCloseSsh = true;
                }
            }
            try {
                if (needCloseSsh) {
                    SshRemoteServerOperateForNetCheck.getInstance().closeSession();
                }
            } catch (Exception e) {
            }
            logger.debug("iperf3服务器检测任务开始");

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private boolean checkIperf3() throws Exception {
        boolean running = false;
        String sourceServerIp = StringUtils.isNotEmpty(MtrMonitorThreadCache.debugSourceServer) ? MtrMonitorThreadCache.debugSourceServer : "127.0.0.1";
        if ("127.0.0.1".equals(sourceServerIp)) {
            if (isWindows()) {
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command("cmd", "/c", "netstat -ano | findstr \"5201\"");
                Process process = processBuilder.start();

                String result = "";
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result = line;
                }

                process.waitFor();

                logger.debug(result);
                if (StringUtils.isNotEmpty(result)) {
                    if (result.contains("LISTENING")) {
                        String pid = result.substring(result.indexOf("LISTENING")).replace("LISTENING", "").trim();
                        if (StringUtils.isNotEmpty(pid)) {
                            MtrMonitorThreadCache.getInstance().setIperf3ServerPid(pid);
                            if (MtrMonitorThreadCache.getInstance().isIperf3ServerStarted()) {
                                running = true;
                            }
                        }
                    }
                }
            } else {
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command("sh", "-c", "lsof -i:5201");
                Process process = processBuilder.start();

                String result = "";
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result = line;
                }

                process.waitFor();

                logger.debug(result);
                if (StringUtils.isNotEmpty(result)) {
                    if (result.contains("iperf3")) {
                        String resultTemp = result.substring(result.indexOf("iperf3"));
                        while (true) {
                            long oldLen = resultTemp.length();
                            resultTemp = resultTemp.replace("  ", " ");
                            if (oldLen == resultTemp.length()) {
                                break;
                            }
                        }
                        String[] resultArr = resultTemp.split(" ");
                        if (resultArr.length > 1) {
                            MtrMonitorThreadCache.getInstance().setIperf3ServerPid(resultArr[1]);
                            if (MtrMonitorThreadCache.getInstance().isIperf3ServerStarted()) {
                                running = true;
                            }
                        }
                    }
                }
            }
        } else {
            String region = ExternalConfigCache.getInstance().getRegion();
            if ("ops".equalsIgnoreCase(region)) {
                if (isWindows()) {
                    ProcessBuilder processBuilder = new ProcessBuilder();
                    processBuilder.command("cmd", "/c", "netstat -ano | findstr \"5201\"");
                    Process process = processBuilder.start();

                    String result = "";
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result = line;
                    }

                    process.waitFor();

                    logger.debug(result);
                    if (StringUtils.isNotEmpty(result)) {
                        if (result.contains("LISTENING")) {
                            String pid = result.substring(result.indexOf("LISTENING")).replace("LISTENING", "").trim();
                            if (StringUtils.isNotEmpty(pid)) {
                                MtrMonitorThreadCache.getInstance().setIperf3ServerPid(pid);
                                if (MtrMonitorThreadCache.getInstance().isIperf3ServerStarted()) {
                                    running = true;
                                }
                            }
                        }
                    }
                } else {
                    ProcessBuilder processBuilder = new ProcessBuilder();
                    processBuilder.command("sh", "-c", "lsof -i:5201");
                    Process process = processBuilder.start();

                    String result = "";
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result = line;
                    }

                    process.waitFor();

                    logger.debug(result);
                    if (StringUtils.isNotEmpty(result)) {
                        if (result.contains("iperf3")) {
                            String resultTemp = result.substring(result.indexOf("iperf3"));
                            while (true) {
                                long oldLen = resultTemp.length();
                                resultTemp = resultTemp.replace("  ", " ");
                                if (oldLen == resultTemp.length()) {
                                    break;
                                }
                            }
                            String[] resultArr = resultTemp.split(" ");
                            if (resultArr.length > 1) {
                                MtrMonitorThreadCache.getInstance().setIperf3ServerPid(resultArr[1]);
                                if (MtrMonitorThreadCache.getInstance().isIperf3ServerStarted()) {
                                    running = true;
                                }
                            }
                        }
                    }
                }
            } else {
                try {
                    if (!SshRemoteServerOperateForNetCheck.getInstance().isLogined()) {
                        SshRemoteServerOperateForNetCheck.getInstance().sshRemoteCallLogin(sourceServerIp, FcmConfigConstant.SERVER_DEFAULT_USER_NAME, FcmConfigConstant.DEFAULT_PASSWORD, FcmConfigConstant.DEFAULT_SERVER_PORT);
                    }
                    if (SshRemoteServerOperateForNetCheck.getInstance().isLogined()) {
                        String result = SshRemoteServerOperateForNetCheck.getInstance().execCommand("lsof -i:5201");
                        logger.error(result);
                        if (StringUtils.isNotEmpty(result)) {
                            if (result.contains("iperf3")) {
                                String resultTemp = result.substring(result.indexOf("iperf3"));
                                while (true) {
                                    long oldLen = resultTemp.length();
                                    resultTemp = resultTemp.replace("  ", " ");
                                    if (oldLen == resultTemp.length()) {
                                        break;
                                    }
                                }
                                String[] resultArr = resultTemp.split(" ");
                                if (resultArr.length > 1) {
                                    MtrMonitorThreadCache.getInstance().setIperf3ServerPid(resultArr[1]);
                                    if (MtrMonitorThreadCache.getInstance().isIperf3ServerStarted()) {
                                        running = true;
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    SshRemoteServerOperateForNetCheck.getInstance().closeSession();
                    logger.error("执行iperf3信息错误。", e);
                    throw e;
                } finally {
//                    SshRemoteServerOperateForNetCheck.getInstance().closeSession();
                }
            }
        }
        return running;
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
