/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : TerminalModuleInitializer.java
 * Package : com.paradisecloud.fcm.terminal.core
 * 
 * @author lilinhai
 * 
 * @since 2021-01-22 18:13
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.terminal.core;

import java.util.List;

import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.terminal.cache.ZjAccountCache;
import com.paradisecloud.fcm.terminal.fs.cache.*;
import com.paradisecloud.fcm.terminal.fs.model.LiveBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.DeptFsbcMappingCache;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.paradisecloud.fcm.terminal.monitor.OnlineStatusMonitor;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;

/**
 * <pre>终端模块初始化器</pre>
 * 
 * @author lilinhai
 * @since 2021-01-22 18:13
 * @version V1.0
 */
@Order(100)
@Component
public class TerminalModuleInitializer implements ApplicationRunner
{
    
    private final Logger LOGGER = LoggerFactory.getLogger(TerminalModuleInitializer.class);
    
    @Autowired
    private BusiTerminalMapper busiTerminalMapper;
    
    @Autowired
    private BusiFsbcRegistrationServerMapper busiFsbcRegistrationServerMapper;
    
    @Autowired
    private BusiFsbcServerDeptMapper busiFsbcServerDeptMapper;
    
    @Autowired
    private BusiTerminalMeetingJoinSettingsMapper busiTerminalMeetingJoinSettingsMapper;
    
    @Autowired
    private BusiFreeSwitchMapper busiFreeSwitchMapper;
    
    @Autowired
    private BusiFreeSwitchDeptMapper busiFreeSwitchDeptMapper;

    @Resource
    private BusiFreeSwitchClusterMapper busiFreeSwitchClusterMapper;

    @Resource
    private BusiFreeSwitchClusterMapMapper busiFreeSwitchClusterMapMapper;

    @Autowired
    private BusiFcmNumberSectionMapper busiFcmNumberSectionMapper;

    @Resource
    private BusiLiveDeptMapper busiLiveDeptMapper;

    @Resource
    private BusiLiveClusterMapper busiLiveClusterMapper;
    @Resource
    private BusiLiveMapper busiLiveMapper;
    @Resource
    private BusiLiveClusterMapMapper busiLiveClusterMapMapper;
    @Resource
    private BusiZjNumberSectionMapper busiZjNumberSectionMapper;
    @Resource
    private BusiLiveSettingMapper busiLiveSettingMapper;
    
    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        List<BusiFsbcRegistrationServer> bfrss = busiFsbcRegistrationServerMapper.selectBusiFsbcRegistrationServerList(new BusiFsbcRegistrationServer());
        for (BusiFsbcRegistrationServer busiFsbcRegistrationServer : bfrss)
        {
            FsbcBridgeCache.getInstance().update(new FsbcBridge(busiFsbcRegistrationServer));
        }
        
        List<BusiFsbcServerDept> fsds = busiFsbcServerDeptMapper.selectBusiFsbcServerDeptList(new BusiFsbcServerDept());
        for (BusiFsbcServerDept busiFsbcServerDept : fsds)
        {
            DeptFsbcMappingCache.getInstance().put(busiFsbcServerDept.getDeptId(), busiFsbcServerDept);
        }
        
        // 获取BusiFreeSwitch信息放入缓存
        this.getBusiFreeSwitchPutCache();

        this.fcmAccountCache();
        // Zj账号分配放入缓存
        this.zjAccountCache();

        // 获取BusiFreeSwitchDept信息放入缓存
        this.getBusiFreeSwitchDeptPutCache();

        // 获取BusiLiveDeptCache信息放入缓存
        this.getAllBusiLivePutCache();

        // 获取LiveSetting信息放入缓存
        this.getLiveSettingPutCache();
        
        List<BusiTerminal> ts = busiTerminalMapper.selectBusiTerminalList(new BusiTerminal());
        for (BusiTerminal busiTerminal : ts)
        {
            TerminalCache.getInstance().put(busiTerminal.getId(), busiTerminal);
        }
        // 终端加载完成
        TerminalCache.getInstance().setLoadFinished();
        
        List<BusiTerminalMeetingJoinSettings> tmjss = busiTerminalMeetingJoinSettingsMapper.selectBusiTerminalMeetingJoinSettingsList(new BusiTerminalMeetingJoinSettings());
        for (BusiTerminalMeetingJoinSettings busiTerminalMeetingJoinSettings : tmjss)
        {
            TerminalCache.getInstance().update(busiTerminalMeetingJoinSettings);
        }
        
        LOGGER.info("终端缓存初始化成功！");
        OnlineStatusMonitor.getInstance().init(busiTerminalMapper);
        if (ts.size() > 0) {
            TerminalCache.getInstance().setNeedUpdateMqStatus(true);
        }
    }

    private void getLiveSettingPutCache() {
        List<BusiLiveSetting> busiLiveSettings = busiLiveSettingMapper.selectBusiLiveSettingList(new BusiLiveSetting());
        if (busiLiveSettings != null && busiLiveSettings.size() > 0) {
            for (BusiLiveSetting busiLiveSetting : busiLiveSettings) {
                LiveSettingCache.getInstance().put(busiLiveSetting.getId(), busiLiveSetting);
            }
        }
    }


    private void fcmAccountCache() {
        List<BusiFcmNumberSection> list = busiFcmNumberSectionMapper.selectBusiFcmNumberSectionVo();
        for (int i = 0; i < list.size(); i++) {
            FcmAccountCacheAndUtils.getInstance().add(list.get(i));
        }
    }

    private void zjAccountCache() {
        List<BusiZjNumberSection> busiZjNumberSectionList = busiZjNumberSectionMapper.selectBusiZjNumberSectionList(new BusiZjNumberSection());
        if (busiZjNumberSectionList != null && busiZjNumberSectionList.size() > 0) {
            for (BusiZjNumberSection busiZjNumberSection : busiZjNumberSectionList) {
                ZjAccountCache.getInstance().add(busiZjNumberSection);
            }
        }
    }

    private void getBusiFreeSwitchDeptPutCache()
    {
        BusiFreeSwitchDept freeSwitchDept = new BusiFreeSwitchDept();
        List<BusiFreeSwitchDept> switchDepts = busiFreeSwitchDeptMapper.selectBusiFreeSwitchDeptList(freeSwitchDept);
        if (null != switchDepts)
        {
            for (BusiFreeSwitchDept busiFreeSwitchDept : switchDepts)
            {
                DeptFcmMappingCache.getInstance().put(busiFreeSwitchDept.getDeptId(), busiFreeSwitchDept);
            }
        }
        
    }

    private void getAllBusiLivePutCache() {
        BusiLiveDept busiLiveDept = new BusiLiveDept();
        List<BusiLiveDept> liveDeptList = busiLiveDeptMapper.selectBusiLiveDeptList(busiLiveDept);
        if (null != liveDeptList)
        {
            for (BusiLiveDept liveDept : liveDeptList)
            {
                LiveDeptCache.getInstance().put(liveDept.getDeptId(), liveDept);
            }
        }
        List<BusiLiveCluster> busiLiveClusters = busiLiveClusterMapper.selectBusiLiveClusterList(new BusiLiveCluster());
        if (busiLiveClusters != null && busiLiveClusters.size() > 0) {
            for (BusiLiveCluster busiLiveCluster : busiLiveClusters) {
                LiveClusterCache.getInstance().put(busiLiveCluster.getId(), busiLiveCluster);
            }
        }

        List<BusiLive> busiLives = busiLiveMapper.selectBusiLiveList(new BusiLive());
        if (busiLives != null && busiLives.size() > 0) {
            for (BusiLive busiLive : busiLives) {
                LiveCache.getInstance().put(busiLive.getId(), busiLive);
            }
        }


        BusiLive busiFreeSwitch = new BusiLive();
        List<BusiLive> busiLiveList = busiLiveMapper.selectBusiLiveList(busiFreeSwitch);
        if (null != busiLiveList)
        {
            for (BusiLive freeSwitch : busiLiveList)
            {
                LiveBridgeCache.getInstance().update(new LiveBridge(freeSwitch));
            }
        }

        BusiLiveClusterMap busiLiveClusterMap = new BusiLiveClusterMap();
        List<BusiLiveClusterMap> busiLiveClusterMaps = busiLiveClusterMapMapper.selectBusiLiveClusterMapList(busiLiveClusterMap);
        if (!ObjectUtils.isEmpty(busiLiveClusterMaps))
        {
            for (BusiLiveClusterMap busiFreeSwitchClusterMap : busiLiveClusterMaps)
            {
                LiveBridgeCache.getInstance().update(busiFreeSwitchClusterMap);
            }
        }

    }
    
    private void getBusiFreeSwitchPutCache()
    {
        List<BusiFreeSwitchCluster> busiFreeSwitchClusters = busiFreeSwitchClusterMapper.selectBusiFreeSwitchClusterList(new BusiFreeSwitchCluster());
        if (!ObjectUtils.isEmpty(busiFreeSwitchClusters))
        {
            for (BusiFreeSwitchCluster busiFreeSwitchCluster : busiFreeSwitchClusters)
            {
                FreeSwitchClusterCache.getInstance().put(busiFreeSwitchCluster.getId(), busiFreeSwitchCluster);
            }
        }

        BusiFreeSwitch busiFreeSwitch = new BusiFreeSwitch();
        List<BusiFreeSwitch> freeSwitchs = busiFreeSwitchMapper.selectBusiFreeSwitchList(busiFreeSwitch);
        if (null != freeSwitchs)
        {
            for (BusiFreeSwitch freeSwitch : freeSwitchs)
            {
                FcmBridgeCache.getInstance().update(new FcmBridge(freeSwitch));
            }
        }

        BusiFreeSwitchClusterMap busiFreeSwitchClusterMapCon = new BusiFreeSwitchClusterMap();
        List<BusiFreeSwitchClusterMap> busiFreeSwitchClusterMaps = busiFreeSwitchClusterMapMapper.selectBusiFreeSwitchClusterMapList(busiFreeSwitchClusterMapCon);
        if (!ObjectUtils.isEmpty(busiFreeSwitchClusterMaps))
        {
            for (BusiFreeSwitchClusterMap busiFreeSwitchClusterMap : busiFreeSwitchClusterMaps)
            {
                FcmBridgeCache.getInstance().update(busiFreeSwitchClusterMap);
            }
        }
    }
}
