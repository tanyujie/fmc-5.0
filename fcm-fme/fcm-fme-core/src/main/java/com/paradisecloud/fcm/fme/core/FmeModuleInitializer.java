/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : SyncInit.java
 * Package     : com.paradisecloud.sync.core
 * @author lilinhai
 * @since 2020-12-08 09:25
 * @version  V1.0
 */
package com.paradisecloud.fcm.fme.core;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.fme.attendee.model.core.ConferenceAttendeeImportanceMonitor;
import com.paradisecloud.fcm.fme.cache.*;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.AttendeeCountingStatistics;
import com.paradisecloud.fcm.fme.model.busi.attendee.TerminalAttendee;
import com.paradisecloud.fcm.fme.websocket.async.FmeWebSocketClientMonitor;
import com.paradisecloud.fcm.fme.websocket.async.WebsocketOnOpenProcessor;
import com.paradisecloud.fcm.fme.websocket.async.WebsocketReconnecter;
import com.paradisecloud.fcm.fme.websocket.interfaces.IFmeCacheService;
import com.paradisecloud.fcm.fme.websocket.model.BusiFmeDBSynchronizer;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fs.server.FreeSwitchServerManager;
import com.paradisecloud.fcm.terminal.fs.server.FreeSwitchTerminalStatusChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>同步模块初始化器</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2020-12-08 09:25
 */
@Order(3)
@Component
public class FmeModuleInitializer implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(FmeModuleInitializer.class);

    @Autowired
    private IFmeCacheService fmeCacheService;

    @Autowired
    private BusiFmeClusterMapper busiFmeClusterMapper;

    @Autowired
    private BusiFmeMapper busiFmeMapper;

    @Autowired
    private BusiFmeDeptMapper busiFmeDeptMapper;

    @Autowired
    private BusiFmeClusterMapMapper busiFmeClusterMapMapper;

    @Autowired
    private BusiTenantSettingsMapper busiTenantSettingsMapper;

    @Resource
    private BusiConferenceAppointmentMapper busiConferenceAppointmentMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Thread() {
            public void run() {
                List<BusiFmeDept> fgds = busiFmeDeptMapper.selectBusiFmeDeptList(new BusiFmeDept());
                for (BusiFmeDept busiFmeDept : fgds) {
                    DeptFmeMappingCache.getInstance().put(busiFmeDept.getDeptId(), busiFmeDept);
                }

                List<BusiTenantSettings> tss = busiTenantSettingsMapper.selectBusiTenantSettingsList(new BusiTenantSettings());
                for (BusiTenantSettings busiTenantSettings : tss) {
                    DeptTenantCache.getInstance().put(busiTenantSettings.getDeptId(), busiTenantSettings);
                }

                ConferenceAttendeeImportanceMonitor.getInstance().start();
                WebsocketReconnecter.getInstance().start();
                BusiFmeDBSynchronizer.getInstance().start();
                FmeWebSocketClientMonitor.getInstance().start();
                WebsocketOnOpenProcessor.getInstance().start();
                initBridgeAndWebSocket();
                LOGGER.info(" FCM-FME-INIT------------------------------------MCU集群数据同步模块初始化成功！");
                addFreeSwitchTerminalStatusChangeListener();
                appointmentConferenceStatusChange();
            }
        }.start();
    }


    /**
     * <pre>初始化会议桥和初始化MCU集群节点WEBSOCKET连接</pre>
     *
     * @author lilinhai
     * @since 2020-12-16 10:59  void
     */
    private void initBridgeAndWebSocket() {
        List<BusiFmeCluster> busiFmeClusters = busiFmeClusterMapper.selectBusiFmeClusterList(new BusiFmeCluster());
        if (!ObjectUtils.isEmpty(busiFmeClusters)) {
            for (BusiFmeCluster busiFmeCluster : busiFmeClusters) {
                FmeClusterCache.getInstance().put(busiFmeCluster.getId(), busiFmeCluster);
            }
        }

        List<BusiFme> bridgeHosts = busiFmeMapper.selectBusiFmeList(new BusiFme());
        for (BusiFme busiFme : bridgeHosts) {
            FmeBridge fmeBridge = new FmeBridge(busiFme);
            FmeBridgeCache.getInstance().update(fmeBridge);
        }
        BusiFmeClusterMap condition = new BusiFmeClusterMap();
        List<BusiFmeClusterMap> busiFmeClusterMaps = busiFmeClusterMapMapper.selectBusiFmeClusterMapList(condition);
        if (!ObjectUtils.isEmpty(busiFmeClusterMaps)) {
            for (BusiFmeClusterMap busiFmeClusterMap : busiFmeClusterMaps) {
                FmeBridgeCache.getInstance().update(busiFmeClusterMap);
            }
        }

        for (FmeBridge fmeBridge : FmeBridgeCache.getInstance().getFmeBridges()) {
            fmeCacheService.initFmeBridge(fmeBridge);
        }
    }

    /**
     * 添加FCM终端状态改变监听
     */
    private void addFreeSwitchTerminalStatusChangeListener() {
        FreeSwitchServerManager.getInstance().setFreeSwitchTerminalStatusChangeListener(new FreeSwitchTerminalStatusChangeListener() {
            @Override
            public void onServerChange(BusiTerminal busiTerminal) {

                // 更新模板会议参与者信息
                for (ConferenceContext conferenceContext : ConferenceContextCache.getInstance().values()) {
                    if (!ObjectUtils.isEmpty(conferenceContext.getTemplateConferenceId())) {
                        TerminalAttendee terminalAttendee = conferenceContext.getTerminalAttendeeMap().get(busiTerminal.getId());
                        if (terminalAttendee != null) {
                            String oldRemoteParty = terminalAttendee.getRemoteParty();
                            String newRemoteParty = TerminalCache.getInstance().getRemoteParty(busiTerminal);
                            terminalAttendee.setIp(busiTerminal.getIp());
                            terminalAttendee.setRemoteParty(newRemoteParty);
                            FcmBridge fcmBridge = FcmBridgeCache.getInstance().getByIp(busiTerminal.getIp());
                            if (fcmBridge != null) {
                                String domainName = fcmBridge.getBusiFreeSwitch().getDomainName();
                                if (!ObjectUtils.isEmpty(domainName)) {
                                    terminalAttendee.setIpNew(domainName);
                                    terminalAttendee.setRemotePartyNew(busiTerminal.getCredential() + "@" + domainName);
                                }
                            }
                            conferenceContext.updateAttendeeToRemotePartyMap(oldRemoteParty, terminalAttendee);
                            Map<String, Object> updateMap = new HashMap<>();
                            updateMap.put("id", terminalAttendee.getId());
                            updateMap.put("ip", terminalAttendee.getIp());
                            WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(updateMap));
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                        }
                    }
                }
            }
        });
    }


    private void appointmentConferenceStatusChange() {
        try {
            List<BusiConferenceAppointment> busiConferenceAppointments = busiConferenceAppointmentMapper.selectBusiConferenceAppointmentList(new BusiConferenceAppointment());
            for (BusiConferenceAppointment busiConferenceAppointment : busiConferenceAppointments) {
                if (busiConferenceAppointment.getIsStart()!=null && busiConferenceAppointment.getIsStart() == 1) {
                    busiConferenceAppointment.setIsStart(2);
                    busiConferenceAppointmentMapper.updateBusiConferenceAppointment(busiConferenceAppointment);
                    FmeDataCache.initiTemplateId.add(busiConferenceAppointment.getTemplateId());
                }
            }
            FmeDataCache.initiFlag = true;
        } catch (Exception e) {
            FmeDataCache.initiFlag = true;
        }
    }
}
