/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : SyncInit.java
 * Package     : com.paradisecloud.sync.core
 * @author lilinhai
 * @since 2020-12-08 09:25
 * @version  V1.0
 */
package com.paradisecloud.fcm.mcu.plc.core;

import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcBridge;
import com.paradisecloud.fcm.mcu.plc.cache.DeptMcuPlcMappingCache;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcBridgeCache;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcClusterCache;
import com.paradisecloud.fcm.mcu.plc.monitor.task.McuPlcThreadMonitorTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * <pre>同步模块初始化器</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2020-12-08 09:25
 */
@Order(13)
@Component
public class McuPlcModuleInitializer implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(McuPlcModuleInitializer.class);

    @Resource
    private BusiMcuPlcMapper busiMcuPlcMapper;
    @Resource
    private BusiMcuPlcClusterMapper busiMcuPlcClusterMapper;
    @Resource
    private BusiMcuPlcDeptMapper busiMcuPlcDeptMapper;
    @Resource
    private BusiMcuPlcTemplateConferenceMapper busiMcuPlcTemplateConferenceMapper;
    @Resource
    private BusiHistoryConferenceMapper busiHistoryConferenceMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initDept();
                initBridge();
//                initConference();
                LOGGER.info("FCM-MCU-ZJ-INIT------------------------------------MCU集群数据同步模块初始化成功！");
            }
        }).start();
    }


    private void initDept() {
        List<BusiMcuPlcDept> busiMcuPlcDeptList = busiMcuPlcDeptMapper.selectBusiMcuPlcDeptList(new BusiMcuPlcDept());
        for (BusiMcuPlcDept busiMcuPlcDept : busiMcuPlcDeptList)
        {
            DeptMcuPlcMappingCache.getInstance().put(busiMcuPlcDept.getDeptId(), busiMcuPlcDept);
        }
    }

    private void initBridge() {
        List<BusiMcuPlcCluster> busiMcuPlcClusters = busiMcuPlcClusterMapper.selectBusiMcuPlcClusterList(new BusiMcuPlcCluster());
        if (!ObjectUtils.isEmpty(busiMcuPlcClusters))
        {
            for (BusiMcuPlcCluster busiMcuPlcCluster : busiMcuPlcClusters)
            {
                McuPlcClusterCache.getInstance().put(busiMcuPlcCluster.getId(), busiMcuPlcCluster);
            }
        }
        List<BusiMcuPlc> busiMcuPlcList = busiMcuPlcMapper.selectBusiMcuPlcList(new BusiMcuPlc());
        for (BusiMcuPlc busiMcuPlc : busiMcuPlcList) {
            McuPlcBridge mcuPlcBridge = new McuPlcBridge(busiMcuPlc);
            McuPlcBridgeCache.getInstance().update(mcuPlcBridge);
        }

        initMonitorThread();
    }

    private void initMonitorThread() {
        McuPlcThreadMonitorTask mcuPlcThreadMonitorTask = new McuPlcThreadMonitorTask();
        mcuPlcThreadMonitorTask.checkCmGetChangesThread();
    }

//    private void initConference() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                List<BusiMcuPlcTemplateConference> busiMcuPlcTemplateConferenceList = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceList(new BusiMcuPlcTemplateConference());
//                for (BusiMcuPlcTemplateConference busiMcuPlcTemplateConference : busiMcuPlcTemplateConferenceList) {
//                    McuPlcConferenceContext mcuPlcConferenceContext = new BuildTemplateConferenceContext().buildTemplateConferenceContext(busiMcuPlcTemplateConference.getId());
//                    if (busiMcuPlcTemplateConference.getLastConferenceId() != null) {
//                        try {
//                            Long historyConferenceId = busiMcuPlcTemplateConference.getLastConferenceId();
//                            BusiHistoryConference busiHistoryConference = busiHistoryConferenceMapper.selectBusiHistoryConferenceById(historyConferenceId);
//                            if (busiHistoryConference != null && busiHistoryConference.getConferenceEndTime() == null) {
//                                mcuPlcConferenceContext.setStart(true);
//                                mcuPlcConferenceContext.setStartTime(busiHistoryConference.getConferenceStartTime());
//                                mcuPlcConferenceContext.setHistoryConference(busiHistoryConference);
//                                McuPlcConferenceContextCache.getInstance().add(mcuPlcConferenceContext);
//                            }
//                        } catch (Exception e) {
//                        }
//                    }
//                }
//            }
//        }).start();
//    }
}
