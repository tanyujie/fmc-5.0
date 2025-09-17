/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : SyncInit.java
 * Package     : com.paradisecloud.sync.core
 * @author lilinhai
 * @since 2020-12-08 09:25
 * @version  V1.0
 */
package com.paradisecloud.fcm.zte.core;

import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.BusiMcuZte;
import com.paradisecloud.fcm.dao.model.BusiMcuZteCluster;
import com.paradisecloud.fcm.dao.model.BusiMcuZteDept;

import com.paradisecloud.fcm.zte.cache.DeptMcuZteMappingCache;
import com.paradisecloud.fcm.zte.cache.McuZteBridgeCache;
import com.paradisecloud.fcm.zte.cache.McuZteClusterCache;
import com.paradisecloud.fcm.zte.cache.model.McuZteBridge;
import com.paradisecloud.fcm.zte.monitor.task.McuZteThreadMonitorTask;
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
public class McuZteModuleInitializer implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(McuZteModuleInitializer.class);

    @Resource
    private BusiMcuZteMapper busiMcuZteMapper;
    @Resource
    private BusiMcuZteClusterMapper busiMcuZteClusterMapper;
    @Resource
    private BusiMcuZteDeptMapper busiMcuZteDeptMapper;
    @Resource
    private BusiMcuZteTemplateConferenceMapper busiMcuZteTemplateConferenceMapper;
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
        List<BusiMcuZteDept> busiMcuZteDeptList = busiMcuZteDeptMapper.selectBusiMcuZteDeptList(new BusiMcuZteDept());
        for (BusiMcuZteDept busiMcuZteDept : busiMcuZteDeptList)
        {
            DeptMcuZteMappingCache.getInstance().put(busiMcuZteDept.getDeptId(), busiMcuZteDept);
        }
    }

    private void initBridge() {
        List<BusiMcuZteCluster> busiMcuZteClusters = busiMcuZteClusterMapper.selectBusiMcuZteClusterList(new BusiMcuZteCluster());
        if (!ObjectUtils.isEmpty(busiMcuZteClusters))
        {
            for (BusiMcuZteCluster busiMcuZteCluster : busiMcuZteClusters)
            {
                McuZteClusterCache.getInstance().put(busiMcuZteCluster.getId(), busiMcuZteCluster);
            }
        }
        List<BusiMcuZte> busiMcuZteList = busiMcuZteMapper.selectBusiMcuZteList(new BusiMcuZte());
        for (BusiMcuZte busiMcuZte : busiMcuZteList) {
            McuZteBridge mcuZteBridge = new McuZteBridge(busiMcuZte);
            McuZteBridgeCache.getInstance().update(mcuZteBridge);
        }

        initMonitorThread();
    }

    private void initMonitorThread() {
        McuZteThreadMonitorTask mcuZteThreadMonitorTask = new McuZteThreadMonitorTask();
        mcuZteThreadMonitorTask.checkCmGetChangesThread();
    }


}
