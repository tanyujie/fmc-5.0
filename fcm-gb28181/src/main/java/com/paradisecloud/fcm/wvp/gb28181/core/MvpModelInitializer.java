/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : SyncInit.java
 * Package     : com.paradisecloud.sync.core
 * @author lilinhai
 * @since 2020-12-08 09:25
 * @version  V1.0
 */
package com.paradisecloud.fcm.wvp.gb28181.core;

import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.dao.mapper.BusiOpsInfoMapper;
import com.paradisecloud.fcm.dao.model.BusiOpsInfo;
import com.paradisecloud.fcm.wvp.gb28181.WvpBridge;
import com.paradisecloud.fcm.wvp.gb28181.WvpBridgeCache;
import com.paradisecloud.fcm.wvp.gb28181.task.DeviceQueryScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * <pre>同步模块初始化器</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2020-12-08 09:25
 */
@Order(14)
@Component
public class MvpModelInitializer implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(MvpModelInitializer.class);

    @Resource
    BusiOpsInfoMapper busiOpsInfoMapper;

    @Resource
    DeviceQueryScheduler deviceQueryScheduler;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!ExternalConfigCache.getInstance().isDisableService28281()) {
                    initBridge();
                    LOGGER.info("MvpModelInitializer------------------------------------WVP启动成功！");
                } else {
                    LOGGER.info("MvpModelInitializer------------------------------------WVP服务设置不启动！");
                }
            }
        }).start();
    }

    private void initBridge() {
        List<BusiOpsInfo> busiOpsInfos = busiOpsInfoMapper.selectBusiOpsInfoList(new BusiOpsInfo());
        if(!CollectionUtils.isEmpty(busiOpsInfos)){
            BusiOpsInfo busiOpsInfo = busiOpsInfos.get(0);
            String ipAddress = busiOpsInfo.getIpAddress();
            WvpBridge wvpBridge = new WvpBridge(ipAddress, 9090);
            WvpBridgeCache.getInstance().update(wvpBridge);
            initMonitorThread(wvpBridge);
            Threads.sleep(2000);
            deviceQueryScheduler.startQueryingDevices();
        }else {
            LOGGER.info("MvpModelInitializer 未启动！！！！！");
        }


    }

    private void initMonitorThread(WvpBridge wvpBridge) {
        WvpKeepAliveThread wvpKeepAliveThread = new WvpKeepAliveThread("wvp登录线程",wvpBridge);
        wvpKeepAliveThread.start();
    }


}
