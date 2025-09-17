/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : SyncInit.java
 * Package     : com.paradisecloud.sync.core
 * @author lilinhai
 * @since 2020-12-08 09:25
 * @version  V1.0
 */
package com.paradisecloud.fcm.mcu.zj.core;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjConferenceContextCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.cache.DeptMcuZjMappingCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjClusterCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.conference.model.templateconference.BuildTemplateConferenceContext;
import com.paradisecloud.fcm.mcu.zj.monitor.task.ThreadMonitorTask;
import org.apache.poi.ss.formula.functions.T;
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
public class McuZjModuleInitializer implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(McuZjModuleInitializer.class);

    @Resource
    private BusiMcuZjMapper busiMcuZjMapper;
    @Resource
    private BusiMcuZjClusterMapper busiMcuZjClusterMapper;
    @Resource
    private BusiMcuZjDeptMapper busiMcuZjDeptMapper;
    @Resource
    private BusiMcuZjTemplateConferenceMapper busiMcuZjTemplateConferenceMapper;
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
        List<BusiMcuZjDept> busiMcuZjDeptList = busiMcuZjDeptMapper.selectBusiMcuZjDeptList(new BusiMcuZjDept());
        for (BusiMcuZjDept busiMcuZjDept : busiMcuZjDeptList)
        {
            DeptMcuZjMappingCache.getInstance().put(busiMcuZjDept.getDeptId(), busiMcuZjDept);
        }
    }

    private void initBridge() {
        List<BusiMcuZjCluster> busiMcuZjClusters = busiMcuZjClusterMapper.selectBusiMcuZjClusterList(new BusiMcuZjCluster());
        if (!ObjectUtils.isEmpty(busiMcuZjClusters))
        {
            for (BusiMcuZjCluster busiMcuZjCluster : busiMcuZjClusters)
            {
                McuZjClusterCache.getInstance().put(busiMcuZjCluster.getId(), busiMcuZjCluster);
            }
        }
        List<BusiMcuZj> busiMcuZjList = busiMcuZjMapper.selectBusiMcuZjList(new BusiMcuZj());
        for (BusiMcuZj busiMcuZj : busiMcuZjList) {
            McuZjBridge mcuZjBridge = new McuZjBridge(busiMcuZj);
            McuZjBridgeCache.getInstance().update(mcuZjBridge);
        }

        initMonitorThread();
    }

    private void initMonitorThread() {
        ThreadMonitorTask threadMonitorTask = new ThreadMonitorTask();
        threadMonitorTask.checkCmGetChangesThread();
    }

//    private void initConference() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                List<BusiMcuZjTemplateConference> busiMcuZjTemplateConferenceList = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceList(new BusiMcuZjTemplateConference());
//                for (BusiMcuZjTemplateConference busiMcuZjTemplateConference : busiMcuZjTemplateConferenceList) {
//                    McuZjConferenceContext mcuZjConferenceContext = new BuildTemplateConferenceContext().buildTemplateConferenceContext(busiMcuZjTemplateConference.getId());
//                    if (busiMcuZjTemplateConference.getLastConferenceId() != null) {
//                        try {
//                            Long historyConferenceId = busiMcuZjTemplateConference.getLastConferenceId();
//                            BusiHistoryConference busiHistoryConference = busiHistoryConferenceMapper.selectBusiHistoryConferenceById(historyConferenceId);
//                            if (busiHistoryConference != null && busiHistoryConference.getConferenceEndTime() == null) {
//                                mcuZjConferenceContext.setStart(true);
//                                mcuZjConferenceContext.setStartTime(busiHistoryConference.getConferenceStartTime());
//                                mcuZjConferenceContext.setHistoryConference(busiHistoryConference);
//                                McuZjConferenceContextCache.getInstance().add(mcuZjConferenceContext);
//                            }
//                        } catch (Exception e) {
//                        }
//                    }
//                }
//            }
//        }).start();
//    }
}
