/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : SyncInit.java
 * Package     : com.paradisecloud.sync.core
 * @author lilinhai
 * @since 2020-12-08 09:25
 * @version  V1.0
 */
package com.paradisecloud.fcm.mcu.kdc.core;

import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcBridge;
import com.paradisecloud.fcm.mcu.kdc.cache.DeptMcuKdcMappingCache;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcBridgeCache;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcClusterCache;
import com.paradisecloud.fcm.mcu.kdc.monitor.task.McuKdcThreadMonitorTask;
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
public class McuKdcModuleInitializer implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(McuKdcModuleInitializer.class);

    @Resource
    private BusiMcuKdcMapper busiMcuKdcMapper;
    @Resource
    private BusiMcuKdcClusterMapper busiMcuKdcClusterMapper;
    @Resource
    private BusiMcuKdcDeptMapper busiMcuKdcDeptMapper;
    @Resource
    private BusiMcuKdcTemplateConferenceMapper busiMcuKdcTemplateConferenceMapper;
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
        List<BusiMcuKdcDept> busiMcuKdcDeptList = busiMcuKdcDeptMapper.selectBusiMcuKdcDeptList(new BusiMcuKdcDept());
        for (BusiMcuKdcDept busiMcuKdcDept : busiMcuKdcDeptList)
        {
            DeptMcuKdcMappingCache.getInstance().put(busiMcuKdcDept.getDeptId(), busiMcuKdcDept);
        }
    }

    private void initBridge() {
        List<BusiMcuKdcCluster> busiMcuKdcClusters = busiMcuKdcClusterMapper.selectBusiMcuKdcClusterList(new BusiMcuKdcCluster());
        if (!ObjectUtils.isEmpty(busiMcuKdcClusters))
        {
            for (BusiMcuKdcCluster busiMcuKdcCluster : busiMcuKdcClusters)
            {
                McuKdcClusterCache.getInstance().put(busiMcuKdcCluster.getId(), busiMcuKdcCluster);
            }
        }
        List<BusiMcuKdc> busiMcuKdcList = busiMcuKdcMapper.selectBusiMcuKdcList(new BusiMcuKdc());
        for (BusiMcuKdc busiMcuKdc : busiMcuKdcList) {
            McuKdcBridge mcuKdcBridge = new McuKdcBridge(busiMcuKdc);
            McuKdcBridgeCache.getInstance().update(mcuKdcBridge);
        }

        initMonitorThread();
    }

    private void initMonitorThread() {
        McuKdcThreadMonitorTask mcuKdcThreadMonitorTask = new McuKdcThreadMonitorTask();
        mcuKdcThreadMonitorTask.checkCmGetChangesThread();
    }

//    private void initConference() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                List<BusiMcuKdcTemplateConference> busiMcuKdcTemplateConferenceList = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceList(new BusiMcuKdcTemplateConference());
//                for (BusiMcuKdcTemplateConference busiMcuKdcTemplateConference : busiMcuKdcTemplateConferenceList) {
//                    McuKdcConferenceContext mcuKdcConferenceContext = new BuildTemplateConferenceContext().buildTemplateConferenceContext(busiMcuKdcTemplateConference.getId());
//                    if (busiMcuKdcTemplateConference.getLastConferenceId() != null) {
//                        try {
//                            Long historyConferenceId = busiMcuKdcTemplateConference.getLastConferenceId();
//                            BusiHistoryConference busiHistoryConference = busiHistoryConferenceMapper.selectBusiHistoryConferenceById(historyConferenceId);
//                            if (busiHistoryConference != null && busiHistoryConference.getConferenceEndTime() == null) {
//                                mcuKdcConferenceContext.setStart(true);
//                                mcuKdcConferenceContext.setStartTime(busiHistoryConference.getConferenceStartTime());
//                                mcuKdcConferenceContext.setHistoryConference(busiHistoryConference);
//                                McuKdcConferenceContextCache.getInstance().add(mcuKdcConferenceContext);
//                            }
//                        } catch (Exception e) {
//                        }
//                    }
//                }
//            }
//        }).start();
//    }
}
