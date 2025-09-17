/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FSTerminalOnlineStatusChecker.java
 * Package     : com.paradisecloud.fcm.terminal.monitor
 * @author sinhy 
 * @since 2021-12-15 21:49
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.terminal.monitor;

import java.util.*;
import java.util.concurrent.CountDownLatch;

import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridgeCluster;
import com.paradisecloud.fcm.terminal.fs.server.FreeSwitchServerManager;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.sinhy.utils.ThreadUtils;

/**  
 * FCM-SIP终端在线状态检测器
 * @author sinhy
 * @since 2021-12-15 21:49
 * @version V1.0  
 */
public class FSTerminalOnlineStatusChecker
{

    private Map<Long, FcmBridge> fcmBridgeMap;
    private CountDownLatch latch;
    
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-12-15 21:52 
     * @param fcmBridgeMap
     * @param latch 
     */
    public FSTerminalOnlineStatusChecker(Map<Long, FcmBridge> fcmBridgeMap, CountDownLatch latch)
    {
        this.fcmBridgeMap = fcmBridgeMap;
        this.latch = latch;
    }
 
    public void check()
    {
        Map<Long, Map<String, BusiTerminal>> fcmTerminalsMap = TerminalCache.getInstance().getFcmTerminalsMap();
        for (Long deptId : fcmTerminalsMap.keySet()) {
            Map<String, BusiTerminal> busiTerminalMap = fcmTerminalsMap.get(deptId);
            for (BusiTerminal busiTerminal : busiTerminalMap.values()) {
                if (busiTerminal.getOnlineStatus() != null && busiTerminal.getOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
                    Long fcmTerminalOnlineTime = TerminalCache.getInstance().getFcmTerminalOnlineTime(busiTerminal.getCredential());
                    if (fcmTerminalOnlineTime == null || System.currentTimeMillis() - fcmTerminalOnlineTime > 60000) {
                        OnlineStatusMonitor.getInstance().processTerminalInfo(busiTerminal, TerminalOnlineStatus.ONLINE, TerminalOnlineStatus.OFFLINE);
                    }
                }
            }
        }
        fcmBridgeMap.forEach((fsServerId, fcmBridge) -> {
            OnlineStatusMonitor.getInstance().getFixedThreadPool().execute(() -> {
                ThreadUtils.sleep(10);
                try
                {
                    if (FreeSwitchServerManager.getInstance().canGetOnlineUserFromDb(fcmBridge.getBusiFreeSwitch().getIp())) {
                        FreeSwitchServerManager.getInstance().updateOnlineUserFromDbTimes(fcmBridge.getBusiFreeSwitch().getIp());
                        Set<String> onlineSet = fcmBridge.getFsOnlineUser();
                        Set<Long> deptIds = new HashSet<>();
                        // 集群
                        Map<Long, FcmBridgeCluster> fcmInCluster = FcmBridgeCache.getInstance().getFcmBridgeClusterMapByFcmId(fsServerId);
                        if (fcmInCluster != null) {
                            for (Long clusterId : fcmInCluster.keySet()) {
                                List<BusiFreeSwitchDept> deptList = DeptFcmMappingCache.getInstance().getDeptListByClusterId(clusterId);
                                for (BusiFreeSwitchDept busiFreeSwitchDept : deptList) {
                                    deptIds.add(busiFreeSwitchDept.getDeptId());
                                }
                            }
                        }
                        // 单节点
                        List<BusiFreeSwitchDept> deptList = DeptFcmMappingCache.getInstance().getDeptListByFcmId(fsServerId);
                        for (BusiFreeSwitchDept busiFreeSwitchDept : deptList) {
                            deptIds.add(busiFreeSwitchDept.getDeptId());
                        }
                        for (long deptId : deptIds) {
                            Map<String, BusiTerminal> m = TerminalCache.getInstance().getFcmTerminalsMap().get(deptId);
                            if (!ObjectUtils.isEmpty(m)) {
                                m.forEach((credential, terminal) -> {
                                    TerminalOnlineStatus oldStatus = TerminalOnlineStatus.convert(terminal.getOnlineStatus());
                                    TerminalOnlineStatus newStatus = onlineSet.contains(credential) ? TerminalOnlineStatus.ONLINE : TerminalOnlineStatus.OFFLINE;
                                    if (newStatus == null) {
                                        fcmBridge.getFcmLogger().logInfo(credential + "找不到对应在线状态信息, map.size: " + onlineSet.size(), true, true);
                                        newStatus = TerminalOnlineStatus.OFFLINE;
                                    }
                                    if (newStatus == TerminalOnlineStatus.ONLINE) {
                                        terminal.setFsServerId(fsServerId);
                                        terminal.setIp(fcmBridge.getBusiFreeSwitch().getIp());
                                        OnlineStatusMonitor.getInstance().processTerminalInfo(terminal, oldStatus, newStatus);
                                    } else {
                                        if (terminal.getFsServerId() == fsServerId) {
                                            OnlineStatusMonitor.getInstance().processTerminalInfo(terminal, oldStatus, newStatus);
                                        }
                                    }
                                });
                            } else {
                                fcmBridge.getFcmLogger().logInfo("找不到fcm注册终端在线状态信息:\n" + onlineSet, true, true);
                            }
                        }
                    }
                } 
                catch (Throwable e)
                {
                    fcmBridge.getFcmLogger().logInfo("获取并解析FCM-SIP注册信息失败", true, e);
                }
                finally
                {
                    latch.countDown();
                    fcmBridge.getFcmLogger().logInfo("FS本轮监听已结束", true, false);
                }
            });
        });
    }
}
