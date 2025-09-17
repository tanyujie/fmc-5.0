/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : WebsocketAbnormalMonitor.java
 * Package     : com.paradisecloud.fcm.sync.thread
 * @author lilinhai 
 * @since 2020-12-17 18:17
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.websocket.model;

import java.util.Date;
import java.util.List;

import com.paradisecloud.fcm.common.async.AsyncMessageProcessor;
import com.paradisecloud.fcm.dao.mapper.BusiCallLegProfileMapper;
import com.paradisecloud.fcm.dao.mapper.BusiFmeMapper;
import com.paradisecloud.fcm.dao.model.BusiCallLegProfile;
import com.paradisecloud.fcm.dao.model.BusiFme;
import com.sinhy.spring.BeanFactory;

/**  
 * <pre>会议桥数据库同步线程</pre>
 * @author lilinhai
 * @since 2020-12-17 18:17
 * @version V1.0  
 */
public class BusiFmeDBSynchronizer extends AsyncMessageProcessor<BusiFme>
{
    
    /**
     * 单例线程对象
     */
    private static final BusiFmeDBSynchronizer INSTANCE = new BusiFmeDBSynchronizer();
    
    private BusiFmeMapper bridgeHostMapper;
    
    /**
     * 入会方案mapper，删除fme的时候需要先删除入会方案
     */
    private BusiCallLegProfileMapper callLegProfileMapper;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2020-12-02 14:23  
     */
    private BusiFmeDBSynchronizer()
    {
        super("BusiFmeDBSynchronizer-Thread");
        this.sleepMillisecondsPerProcess = 0;
        this.waitMessage = " FCM-FME-BridgeHost----BridgeHostDBSynchronizer---Sleep---没有需要更新的BridgeHost对象，会议桥数据库同步线程进入休眠状态！";
        this.workMessage = " FCM-FME-BridgeHost----BridgeHostDBSynchronizer---Work---收到需要更新的BridgeHost对象，会议桥数据库同步线程进入工作状态！";
        this.bridgeHostMapper = BeanFactory.getBean(BusiFmeMapper.class);
        this.callLegProfileMapper = BeanFactory.getBean(BusiCallLegProfileMapper.class);
    }
    
    protected void process(BusiFme busiFme)
    {
        synchronized (busiFme)
        {
            if (busiFme.getId() != null)
            {
                // 删除，则删除数据库
                if (busiFme.getParams().containsKey("isDeleted"))
                {
                    // 先删除FME对应的入会方案数据库表信息
                    BusiCallLegProfile busiCallLegProfile = new BusiCallLegProfile();
                    busiCallLegProfile.setFmeId(busiFme.getId());
                    List<BusiCallLegProfile> cl = callLegProfileMapper.selectBusiCallLegProfileList(busiCallLegProfile);
                    if (cl != null)
                    {
                        for (BusiCallLegProfile busiCallLegProfile2 : cl)
                        {
                            callLegProfileMapper.deleteBusiCallLegProfileById(busiCallLegProfile2.getId());
                        }
                    }
                    
                    // 删除FME
                    bridgeHostMapper.deleteBusiFmeById(busiFme.getId());
                    logger.info("删除会议桥信息成功：{}", busiFme);
                }
                else
                {
                    busiFme.setUpdateTime(new Date());
                    bridgeHostMapper.updateBusiFme(busiFme);
                    logger.info("更新会议桥信息成功：{}", busiFme);
                }
            }
            else
            {
                bridgeHostMapper.insertBusiFme(busiFme);
                logger.info("添加会议桥信息成功：{}", busiFme);
            }
        }
    }
    
    /**
     * <pre>获取单例</pre>
     * @author lilinhai
     * @since 2020-12-02 14:24 
     * @return WebsocketAbnormalMonitor
     */
    public static BusiFmeDBSynchronizer getInstance()
    {
        return INSTANCE;
    }
}
