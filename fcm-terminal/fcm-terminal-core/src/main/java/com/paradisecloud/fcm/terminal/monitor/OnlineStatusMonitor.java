/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : OnlineStatusMonitor.java
 * Package : com.paradisecloud.fcm.terminal.core
 * 
 * @author lilinhai
 * 
 * @since 2021-01-22 17:55
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.terminal.monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.enumer.FcmBridgeStatus;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.message.terminal.Smc2TerminalOnlineStatusMessageQueue;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridgeCluster;
import com.paradisecloud.fcm.terminal.fs.server.FreeSwitchServerManager;
import com.paradisecloud.fcm.terminal.fs.server.FreeSwitchTerminalOnlineEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.message.terminal.TerminalOnlineStatusMessage;
import com.paradisecloud.fcm.common.message.terminal.TerminalOnlineStatusMessageQueue;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.sinhy.utils.HostUtils;
import com.sinhy.utils.ThreadUtils;

/**
 * <pre>终端在线状态监视器</pre>
 * 
 * @author lilinhai
 * @since 2021-01-22 17:55
 * @version V1.0
 */
public class OnlineStatusMonitor extends Thread
{
    
    private static final OnlineStatusMonitor INSTANCE = new OnlineStatusMonitor();
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OnlineStatusMonitor.class);

    private volatile long lastPushIpMessageTime = 0;
    private volatile boolean ipMessagePushed = false;
    
    private final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(500, new ThreadFactory()
    {
        private int index;
        public Thread newThread(Runnable r)
        {
            index++;
            return new Thread(r, "OnlineStatusMonitor-Fixed-Pool[" + 200 + "]-Thread-" + index);
        }
    });
    
    private final static long WAIT_TIME = 5 * 1000;
    
    private final static int TIMEOUT = 1000;
    
    private BusiTerminalMapper busiTerminalMapper;
    
    private OnlineStatusMonitor()
    {
        super("OnlineStatusMonitor");
    }
    
    /**
     * <p>
     * Set Method : busiTerminalMapper BusiTerminalMapper
     * </p>
     * 
     * @param busiTerminalMapper
     */
    public void init(BusiTerminalMapper busiTerminalMapper)
    {
        this.busiTerminalMapper = busiTerminalMapper;
        this.addFreeSwitchTerminalOnlineListener();
        this.start();
    }
    
    @Override
    public void run()
    {
        LOGGER.info("终端在线状态监视器启动并初始化成功");
        long startTime = 0;
        long endTime = 0;
        while (true)
        {
            try
            {
                startTime = System.currentTimeMillis();
                List<BusiTerminal> all = TerminalCache.getInstance().getCopiedOriginalTerminals();
                Map<Long, FsbcBridge> fsbcBridgeMap = new HashMap<>(FsbcBridgeCache.getInstance().getFsbcBridgeMap());
                Map<Long, FcmBridge> fcmBridgeMap = new HashMap<>(FcmBridgeCache.getInstance().getFcmBridgeMap());
                int allCount = all.size() + fsbcBridgeMap.size() + fcmBridgeMap.size();
                CountDownLatch latch = new CountDownLatch(allCount);
                new FsbcTerminalOnlineStatusChecker(fsbcBridgeMap, latch).check();
                new FSTerminalOnlineStatusChecker(fcmBridgeMap, latch).check();
                new LiveOnlineStatusCheck().check();
                long currentTime = System.currentTimeMillis();
                ipMessagePushed = false;
                if (!ObjectUtils.isEmpty(all))
                {
                    distributeDetectionTask(all, latch);
                }
                
                if (latch.getCount() > 0)
                {
                    latch.await();
                }
                if (ipMessagePushed) {
                    lastPushIpMessageTime = currentTime;
                }
                if (lastPushIpMessageTime > currentTime) {
                    lastPushIpMessageTime = currentTime;
                }
            }
            catch (Throwable e)
            {
                LOGGER.error("主线程wait异常：", e);
            }
            finally
            {
                endTime = System.currentTimeMillis();
                long spendTime = endTime - startTime;
                if (spendTime < WAIT_TIME)
                {
                    ThreadUtils.sleep(WAIT_TIME - spendTime);
                }
                LOGGER.info("本轮检测所有终端在线状态，共耗时：" + spendTime);
            }
        }
    }
    
    /**
     * <p>Get Method   :   fixedThreadPool ExecutorService</p>
     * @return fixedThreadPool
     */
    ExecutorService getFixedThreadPool()
    {
        return fixedThreadPool;
    }
    
    void processTerminalInfo(BusiTerminal busiTerminal, TerminalOnlineStatus oldStatus, TerminalOnlineStatus realStatus)
    {
        if (oldStatus != realStatus)
        {
            busiTerminal.setOnlineStatus(realStatus.getValue());
            busiTerminalMapper.updateBusiTerminal(busiTerminal);
            Smc2TerminalOnlineStatusMessageQueue.getInstance().put(new TerminalOnlineStatusMessage(busiTerminal.getId(), realStatus));
            TerminalOnlineStatusMessageQueue.getInstance().put(new TerminalOnlineStatusMessage(busiTerminal.getId(), realStatus));
            LOGGER.info("[" + busiTerminal.getName() + "]-[" + busiTerminal.getIp() + "]检测到终端在线状态有变化: " + oldStatus.getName() + "--->"
                    + TerminalOnlineStatus.convert(busiTerminal.getOnlineStatus()).getName());
        }
    }
    
    /**
     * <pre>分发检测任务</pre>
     * 
     * @author lilinhai
     * @since 2021-01-27 14:21
     * @param all
     * @param latch void
     */
    private void distributeDetectionTask(List<BusiTerminal> all, CountDownLatch latch)
    {
        for (BusiTerminal busiTerminal : all)
        {
            fixedThreadPool.execute(() -> {
                try
                {
                    Integer onlineStatus = busiTerminal.getOnlineStatus();
                    if (onlineStatus == null)
                    {
                        onlineStatus = TerminalOnlineStatus.OFFLINE.getValue();
                    }
                    TerminalOnlineStatus oldStatus = TerminalOnlineStatus.convert(onlineStatus);
                    TerminalOnlineStatus realStatus = testTerminalOnlineStatus(busiTerminal, oldStatus);
                    
                    processTerminalInfo(busiTerminal, oldStatus, realStatus);
                    if ("ops".equals(ExternalConfigCache.getInstance().getRegion())) {
                        if (TerminalType.isOnlyIP(busiTerminal.getType())) {
                            if (realStatus == TerminalOnlineStatus.ONLINE) {
                                if (System.currentTimeMillis() - lastPushIpMessageTime > 10000) {
                                    if (TerminalCache.getInstance().getIpTerminalEventListener() != null) {
                                        TerminalCache.getInstance().getIpTerminalEventListener().pushAll(busiTerminal.getId());
                                        ipMessagePushed = true;
                                    }
                                }
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    LOGGER.error("检测终端在线状态出现异常：" + busiTerminal.toString(), e);
                }
                finally
                {
                    latch.countDown();
                }
            });
        }
    }

    /**
     * <pre>测试终端在线状态</pre>
     * @author lilinhai
     * @since 2021-03-10 13:43 
     * @param busiTerminal
     * @param oldStatus 
     * @return TerminalOnlineStatus
     */
    private TerminalOnlineStatus testTerminalOnlineStatus(BusiTerminal busiTerminal, TerminalOnlineStatus oldStatus)
    {
        boolean isOnline = HostUtils.isHostReachable(busiTerminal.getIp(), TIMEOUT);
        TerminalOnlineStatus realStatus = isOnline ? TerminalOnlineStatus.ONLINE : TerminalOnlineStatus.OFFLINE;
        if (TerminalType.isIp(busiTerminal.getType())) {
            if (realStatus == TerminalOnlineStatus.OFFLINE) {
                if (checkTcp(busiTerminal.getIp(), 5060)) {
                    realStatus = TerminalOnlineStatus.ONLINE;
                    return realStatus;
                }
                try {
                    Process process = Runtime.getRuntime().exec("ping -c 3 " + busiTerminal.getIp());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line != null && line.contains("ttl=") && line.contains("time=")) {
                            isOnline = true;
                            break;
                        }
                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                realStatus = isOnline ? TerminalOnlineStatus.ONLINE : TerminalOnlineStatus.OFFLINE;
                if (realStatus == TerminalOnlineStatus.ONLINE) {
                    return realStatus;
                }
            }
        }
        if (oldStatus == TerminalOnlineStatus.ONLINE)
        {
            if (realStatus == TerminalOnlineStatus.OFFLINE)
            {
                for (int i = 0; i < 3; i++) {
                    ThreadUtils.sleep(500);
                    isOnline = HostUtils.isHostReachable(busiTerminal.getIp(), TIMEOUT);
                    realStatus = isOnline ? TerminalOnlineStatus.ONLINE : TerminalOnlineStatus.OFFLINE;
                    if (realStatus == TerminalOnlineStatus.ONLINE) {
                        return realStatus;
                    }
                }
            }
        }
        return realStatus;
    }
    
    public static OnlineStatusMonitor getInstance()
    {
        return INSTANCE;
    }

    /**
     * 添加fs 终端上下线监听
     */
    private void addFreeSwitchTerminalOnlineListener() {
        FreeSwitchServerManager.getInstance().setFreeSwitchTerminalOnlineEventListener(new FreeSwitchTerminalOnlineEventListener() {
            @Override
            public void online(long serverId, String username) {
                TerminalCache.getInstance().setFcmTerminalOnline(username);
                // 集群
                Map<Long, FcmBridgeCluster> fcmInCluster = FcmBridgeCache.getInstance().getFcmBridgeClusterMapByFcmId(serverId);
                if (fcmInCluster != null) {
                    for (Long clusterId : fcmInCluster.keySet()) {
                        List<BusiFreeSwitchDept> deptList = DeptFcmMappingCache.getInstance().getDeptListByClusterId(clusterId);
                        for (BusiFreeSwitchDept busiFreeSwitchDept : deptList) {
                            Map<String, BusiTerminal> terminalMap = TerminalCache.getInstance().getFcmTerminalsMap().get(busiFreeSwitchDept.getDeptId());
                            if (!ObjectUtils.isEmpty(terminalMap)) {
                                BusiTerminal terminal = terminalMap.get(username);
                                if (terminal != null) {
                                    TerminalOnlineStatus oldStatus = TerminalOnlineStatus.convert(terminal.getOnlineStatus());
                                    if (terminal.getFsServerId() == null || terminal.getFsServerId() != serverId) {
                                        FcmBridge fcmBridge = FcmBridgeCache.getInstance().get(serverId);
                                        String oldRemoteParty = TerminalCache.getInstance().getRemoteParty(terminal);
                                        terminal.setFsServerId(serverId);
                                        terminal.setIp(fcmBridge.getBusiFreeSwitch().getIp());
                                        oldStatus = TerminalOnlineStatus.OFFLINE;// 为更新fsServerId和ip
                                        TerminalCache.getInstance().updateRemotePartyTerminalMapForFcmChange(oldRemoteParty, terminal);
                                    }
                                    TerminalOnlineStatus newStatus = TerminalOnlineStatus.ONLINE;
                                    processTerminalInfo(terminal, oldStatus, newStatus);
                                    if (FreeSwitchServerManager.getInstance().getFreeSwitchTerminalStatusChangeListener() != null) {
                                        FreeSwitchServerManager.getInstance().getFreeSwitchTerminalStatusChangeListener().onServerChange(terminal);
                                    }
                                }
                            }
                        }
                    }
                }
                // 单节点
                List<BusiFreeSwitchDept> deptList = DeptFcmMappingCache.getInstance().getDeptListByFcmId(serverId);
                for (BusiFreeSwitchDept busiFreeSwitchDept : deptList) {
                    Map<String, BusiTerminal> terminalMap = TerminalCache.getInstance().getFcmTerminalsMap().get(busiFreeSwitchDept.getDeptId());
                    if (!ObjectUtils.isEmpty(terminalMap)) {
                        BusiTerminal terminal = terminalMap.get(username);
                        if (terminal != null) {
                            if (terminal.getFsServerId() == null || terminal.getFsServerId() != serverId) {
                                FcmBridge fcmBridge = FcmBridgeCache.getInstance().get(serverId);
                                terminal.setFsServerId(serverId);
                                terminal.setIp(fcmBridge.getBusiFreeSwitch().getIp());
                            }
                            TerminalOnlineStatus oldStatus = TerminalOnlineStatus.convert(terminal.getOnlineStatus());
                            TerminalOnlineStatus newStatus = TerminalOnlineStatus.ONLINE;
                            processTerminalInfo(terminal, oldStatus, newStatus);
                        }
                    }
                }
            }

            @Override
            public void offline(long serverId, String username) {
                // 集群
                Map<Long, FcmBridgeCluster> fcmInCluster = FcmBridgeCache.getInstance().getFcmBridgeClusterMapByFcmId(serverId);
                if (fcmInCluster != null) {
                    for (Long clusterId : fcmInCluster.keySet()) {
                        List<BusiFreeSwitchDept> deptList = DeptFcmMappingCache.getInstance().getDeptListByClusterId(clusterId);
                        for (BusiFreeSwitchDept busiFreeSwitchDept : deptList) {
                            Map<String, BusiTerminal> terminalMap = TerminalCache.getInstance().getFcmTerminalsMap().get(busiFreeSwitchDept.getDeptId());
                            if (!ObjectUtils.isEmpty(terminalMap)) {
                                BusiTerminal terminal = terminalMap.get(username);
                                if (terminal != null) {
                                    if (terminal.getFsServerId() != null && terminal.getFsServerId() == serverId) {
                                        TerminalOnlineStatus oldStatus = TerminalOnlineStatus.convert(terminal.getOnlineStatus());
                                        TerminalOnlineStatus newStatus = TerminalOnlineStatus.OFFLINE;
                                        processTerminalInfo(terminal, oldStatus, newStatus);
                                    }
                                }
                            }
                        }
                    }
                }
                // 单节点
                List<BusiFreeSwitchDept> deptList = DeptFcmMappingCache.getInstance().getDeptListByFcmId(serverId);
                for (BusiFreeSwitchDept busiFreeSwitchDept : deptList) {
                    Map<String, BusiTerminal> terminalMap = TerminalCache.getInstance().getFcmTerminalsMap().get(busiFreeSwitchDept.getDeptId());
                    if (!ObjectUtils.isEmpty(terminalMap)) {
                        BusiTerminal terminal = terminalMap.get(username);
                        if (terminal != null) {
                            if (terminal.getFsServerId() != null && terminal.getFsServerId() == serverId) {
                                TerminalOnlineStatus oldStatus = TerminalOnlineStatus.convert(terminal.getOnlineStatus());
                                TerminalOnlineStatus newStatus = TerminalOnlineStatus.OFFLINE;
                                processTerminalInfo(terminal, oldStatus, newStatus);
                            }
                        }
                    }
                }
            }

            @Override
            public void serverOffline(long serverId) {
                FcmBridge fcmBridge = FcmBridgeCache.getInstance().get(serverId);
                fcmBridge.setBridgeStatus(FcmBridgeStatus.NOT_AVAILABLE);
                // 集群
                Map<Long, FcmBridgeCluster> fcmInCluster = FcmBridgeCache.getInstance().getFcmBridgeClusterMapByFcmId(serverId);
                if (fcmInCluster != null) {
                    for (Long clusterId : fcmInCluster.keySet()) {
                        List<BusiFreeSwitchDept> deptList = DeptFcmMappingCache.getInstance().getDeptListByClusterId(clusterId);
                        for (BusiFreeSwitchDept busiFreeSwitchDept : deptList) {
                            Map<String, BusiTerminal> terminalMap = TerminalCache.getInstance().getFcmTerminalsMap().get(busiFreeSwitchDept.getDeptId());
                            if (!ObjectUtils.isEmpty(terminalMap)) {
                                for (String key : terminalMap.keySet()) {
                                    BusiTerminal terminal = terminalMap.get(key);
                                    if (terminal != null) {
                                        if (terminal.getFsServerId() != null && terminal.getFsServerId() == serverId) {
                                            TerminalOnlineStatus oldStatus = TerminalOnlineStatus.convert(terminal.getOnlineStatus());
                                            TerminalOnlineStatus newStatus = TerminalOnlineStatus.OFFLINE;
                                            processTerminalInfo(terminal, oldStatus, newStatus);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // 单节点
                List<BusiFreeSwitchDept> deptList = DeptFcmMappingCache.getInstance().getDeptListByFcmId(serverId);
                for (BusiFreeSwitchDept busiFreeSwitchDept : deptList) {
                    Map<String, BusiTerminal> terminalMap = TerminalCache.getInstance().getFcmTerminalsMap().get(busiFreeSwitchDept.getDeptId());
                    if (!ObjectUtils.isEmpty(terminalMap)) {
                        for (String key : terminalMap.keySet()) {
                            BusiTerminal terminal = terminalMap.get(key);
                            if (terminal != null) {
                                if (terminal.getFsServerId() != null && terminal.getFsServerId() == serverId) {
                                    TerminalOnlineStatus oldStatus = TerminalOnlineStatus.convert(terminal.getOnlineStatus());
                                    TerminalOnlineStatus newStatus = TerminalOnlineStatus.OFFLINE;
                                    processTerminalInfo(terminal, oldStatus, newStatus);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void serverOnline(long serverId) {
                FcmBridge fcmBridge = FcmBridgeCache.getInstance().get(serverId);
                fcmBridge.setBridgeStatus(FcmBridgeStatus.AVAILABLE);
            }
        });
    }

    private boolean checkTcp(String ip, int port) {
        if (null == ip || 0 == ip.length() || port < 1024 || port > 65535) {
            return false;
        }

        Socket s = new Socket();
        try {
            SocketAddress add = new InetSocketAddress(ip, port);
            s.connect(add, 500);// 超时3秒
            return true;
        } catch (IOException e) {
        } finally {
            try {
                s.close();
            } catch (Exception e) {

            }
        }
        return false;
    }

    private boolean checkUdp(String ip, int port) {
        if (null == ip || 0 == ip.length() || port < 1024 || port > 65535) {
            return false;
        }

        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.connect(InetAddress.getByName(ip), port);
            return !(socket.getLocalPort() == socket.getPort());
        } catch (Exception e) {
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception e) {
                }
            }
        }
        return false;
    }
    
}
