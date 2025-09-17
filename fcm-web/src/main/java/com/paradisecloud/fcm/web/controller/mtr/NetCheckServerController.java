package com.paradisecloud.fcm.web.controller.mtr;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.terminal.fs.constant.FcmConfigConstant;
import com.paradisecloud.fcm.web.mtr.Iperf3ServerStartThread;
import com.paradisecloud.fcm.web.mtr.MtrMonitorThreadCache;
import com.paradisecloud.fcm.web.mtr.SshRemoteServerOperateForNetCheck;
import com.paradisecloud.fcm.web.mtr.SshRemoteServerOperateForServerStart;
import com.sinhy.exception.SystemException;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping({"/net/check"})
public class NetCheckServerController extends BaseController {

    @GetMapping({"/server/status"})
    @Operation(summary = "", description = "网络检测服务状态")
    public RestResponse getServerStatus() {
        boolean started = false;
        Map<String, Object> data = new HashMap<>();
        if (MtrMonitorThreadCache.getInstance().isIperf3ServerStarted()) {
            started = true;
        }
        data.put("started", started);
        data.put("msgText", MtrMonitorThreadCache.getInstance().getServerMsg());

        return RestResponse.success(data);
    }

    @PreAuthorize("@ss.hasPermi('net:check:server:start')")
    @PostMapping({"/server/start"})
    @Operation(summary = "", description = "启动网络检测服务")
    public RestResponse startServer() {
        try {
            if (MtrMonitorThreadCache.getInstance().isIperf3ServerStarted()) {
                return RestResponse.success("网络检测服务端模式已开启。");
            } else {
                MtrMonitorThreadCache.getInstance().setIperf3ServerRequestTime(System.currentTimeMillis());
                Iperf3ServerStartThread iperf3ServerStartThread = new Iperf3ServerStartThread();
                iperf3ServerStartThread.start();
                MtrMonitorThreadCache.getInstance().setIperf3ServerStartThread(iperf3ServerStartThread);
            }
        } catch (Exception e) {
            throw new SystemException("网络检测服务端模式开启失败！");
        }

        return RestResponse.success("网络检测服务端模式已开启。");
    }

    @PreAuthorize("@ss.hasPermi('net:check:server:stop')")
    @PostMapping({"/server/stop"})
    @Operation(summary = "", description = "停止网络检测服务端模式")
    public RestResponse stopServer() {
        String sourceServerIp = StringUtils.isNotEmpty(MtrMonitorThreadCache.debugSourceServer) ? MtrMonitorThreadCache.debugSourceServer : "127.0.0.1";
        if ("127.0.0.1".equals(sourceServerIp)) {
            try {
                if (MtrMonitorThreadCache.getInstance().isIperf3ServerStarted()) {
                    if (StringUtils.isNotEmpty(MtrMonitorThreadCache.getInstance().getIperf3ServerPid())) {
                        if (isWindows()) {
                            Runtime.getRuntime().exec("taskkill /f /pid " + MtrMonitorThreadCache.getInstance().getIperf3ServerPid());
                        } else {
                            Runtime.getRuntime().exec("kill -9 " + MtrMonitorThreadCache.getInstance().getIperf3ServerPid());
                        }
                        MtrMonitorThreadCache.getInstance().setIperf3ServerPid(null);
                    }
                    try {
                        SshRemoteServerOperateForServerStart.getInstance().closeSession();
                    } catch (Exception e) {
                    }
                    if (MtrMonitorThreadCache.getInstance().getIperf3ServerStartThread() != null) {
                        try {
                            MtrMonitorThreadCache.getInstance().getIperf3ServerStartThread().interrupt();
                        } catch (Exception e) {
                        }
                        MtrMonitorThreadCache.getInstance().setIperf3ServerStartThread(null);
                    }
                }
            } catch (Exception e) {
                throw new SystemException("网络检测服务端模式停止失败！");
            }
        } else {
            try {
                if (MtrMonitorThreadCache.getInstance().isIperf3ServerStarted()) {
                    if (!SshRemoteServerOperateForNetCheck.getInstance().isLogined()) {
                        SshRemoteServerOperateForNetCheck.getInstance().sshRemoteCallLogin(sourceServerIp, FcmConfigConstant.SERVER_DEFAULT_USER_NAME, FcmConfigConstant.DEFAULT_PASSWORD, FcmConfigConstant.DEFAULT_SERVER_PORT);
                    }
                    if (SshRemoteServerOperateForNetCheck.getInstance().isLogined()) {
                        if (StringUtils.isNotEmpty(MtrMonitorThreadCache.getInstance().getIperf3ServerPid())) {
                            String result = SshRemoteServerOperateForNetCheck.getInstance().execCommand("kill -9 " + MtrMonitorThreadCache.getInstance().getIperf3ServerPid());
                        }
                    }
                    try {
                        SshRemoteServerOperateForServerStart.getInstance().closeSession();
                    } catch (Exception e) {
                    }
                    if (MtrMonitorThreadCache.getInstance().getIperf3ServerStartThread() != null) {
                        try {
                            MtrMonitorThreadCache.getInstance().getIperf3ServerStartThread().interrupt();
                        } catch (Exception e) {
                        }
                        MtrMonitorThreadCache.getInstance().setIperf3ServerStartThread(null);
                    }
                }
            } catch (Exception e) {
                throw new SystemException("网络检测服务端模式停止失败！");
            }
        }

        return RestResponse.success("网络检测服务端模式已停止。");
    }

    @PreAuthorize("@ss.hasPermi('net:check:client:run')")
    @PostMapping({"/client/run"})
    @Operation(summary = "", description = "执行目标网络检测")
    public RestResponse runClient(@RequestBody JSONObject jsonObject) {
        if (jsonObject == null || !jsonObject.containsKey("ip")) {
            throw new SystemException("目标IP地址不能为空！");
        }
        String targetServerIp = jsonObject.getString("ip");

        String sourceServerIp = StringUtils.isNotEmpty(MtrMonitorThreadCache.debugSourceServer) ? MtrMonitorThreadCache.debugSourceServer : "127.0.0.1";
        if ("127.0.0.1".equals(sourceServerIp)) {
            if (pingIp(targetServerIp)) {
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder();
                    if (isWindows()) {
                        processBuilder.command("cmd", "/c", "iperf3 -c " + targetServerIp);
                    } else {
                        processBuilder.command("/bin/sh", "-c", "iperf3 -c " + targetServerIp);
                    }
                    Process process = processBuilder.start();

                    String result = "";
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result += line + "\n";
                    }

                    process.waitFor();

                    logger.debug(result);
                    if (StringUtils.isEmpty(result)) {
                        return RestResponse.fail("执行目标网络检测失败！无法检测目标地址！请检查目标检测服务是否启动。");
                    } else {
                        return RestResponse.success(result);
                    }
                } catch (Exception e) {
                }
            } else {
                return RestResponse.fail("执行目标网络检测失败！目标地址不存在，无法检测目标地址！");
            }
        } else {
            try {
                if (MtrMonitorThreadCache.getInstance().isIperf3ServerStarted()) {
                    return RestResponse.fail("网络检测服务端模式已开启。请关闭后重试！");
                } else {
                    if (!SshRemoteServerOperateForNetCheck.getInstance().isLogined()) {
                        SshRemoteServerOperateForNetCheck.getInstance().sshRemoteCallLogin(sourceServerIp, FcmConfigConstant.SERVER_DEFAULT_USER_NAME, FcmConfigConstant.DEFAULT_PASSWORD, FcmConfigConstant.DEFAULT_SERVER_PORT);
                    }
                    if (SshRemoteServerOperateForNetCheck.getInstance().isLogined()) {
                        if (pingIp(targetServerIp)) {
                            String result = SshRemoteServerOperateForNetCheck.getInstance().execCommand("iperf3 -c " + targetServerIp);
                            if (StringUtils.isEmpty(result)) {
                                return RestResponse.fail("执行目标网络检测失败！无法检测目标地址！请检查目标检测服务是否启动。");
                            } else {
                                return RestResponse.success(result);
                            }
                        } else {
                            return RestResponse.fail("执行目标网络检测失败！目标地址不存在，无法检测目标地址！");
                        }
                    }
                }
            } catch (Exception e) {
            }
        }

        return RestResponse.fail("执行目标网络检测失败！");
    }

    public boolean pingIp(String ip) {
        if (null == ip || 0 == ip.length()) {
            return false;
        }

        try {
            InetAddress.getByName(ip);
            return true;
        } catch (IOException e) {
            return false;
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
