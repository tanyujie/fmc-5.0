/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ConferenceSynchronizer.java
 * Package     : com.paradisecloud.fcm.fme.conferencemonitor.messageconsumer
 * @author lilinhai
 * @since 2021-03-02 14:26
 * @version  V1.0
 */
package com.paradisecloud.fcm.fme.monitor.messageprocessor;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.paradisecloud.fcm.common.async.AsyncMessageProcessor;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.message.FmeBridgeMessage;
import com.paradisecloud.fcm.fme.cache.utils.FcmThreadPool;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;

import java.util.Collection;

/**
 * <pre>会议桥消息处理器</pre>
 * @author lilinhai
 * @since 2021-03-02 14:26
 * @version V1.0
 */
@Component
public class FmeBridgeMessageProcessor extends AsyncMessageProcessor<FmeBridgeMessage> implements InitializingBean
{

    @Autowired
    private IBusiConferenceService busiConferenceService;

    /**
     * <pre>构造方法</pre>
     * @author lilinhai
     * @since 2020-12-02 14:23
     */
    public FmeBridgeMessageProcessor()
    {
        super("FmeBridgeMessageProcessor-Thread");
        this.sleepMillisecondsPerProcess = 3000;
        this.waitMessage = " FmeBridgeMessageProcessor---Sleep---没有FmeBridgeMessage对象，会议桥消息处理器进入休眠状态！";
        this.workMessage = " FmeBridgeMessageProcessor---Work---收到FmeBridgeMessage对象通知，会议桥消息处理器进入工作状态！";
    }

    @Override
    protected void process(FmeBridgeMessage fmeBridgeMessage)
    {
        FcmThreadPool.exec(() -> {
            FmeBridge fmeBridge = fmeBridgeMessage.getFmeBridge();
            fmeBridge.getDataCache().eachCoSpace((coSpace) -> {

                ConferenceContext conferenceContextExist = null;
                Collection<ConferenceContext> conferenceContextList = ConferenceContextCache.getInstance().getConferenceContextListByConferenceNum(coSpace.getUri());
                if (conferenceContextList != null && conferenceContextList.size() > 0) {
                    if (fmeBridge != null) {
                        for (ConferenceContext conferenceContextTemp : conferenceContextList) {
                            if (coSpace.getId().equals(conferenceContextTemp.getCoSpaceId())) {
                                conferenceContextExist = conferenceContextTemp;
                                break;
                            }
                        }
                    }
                }

                ConferenceContext cc = conferenceContextExist;
                if (cc != null)
                {
                    FcmThreadPool.exec(() -> {

                        // 同步会议
                        try {
                            logger.info(fmeBridge.isAvailable() ? "FME【"+fmeBridge.getBusiFme().getIp()+"】恢复触发自动同步"+cc.getConferenceNumber() : "FME【"+fmeBridge.getBusiFme().getIp()+"】故障触发自动同步"+cc.getConferenceNumber());
                            busiConferenceService.sync(cc, fmeBridge.isAvailable() ? "FME【"+fmeBridge.getBusiFme().getIp()+"】恢复触发自动同步" : "FME【"+fmeBridge.getBusiFme().getIp()+"】故障触发自动同步");
                        } catch (Exception e) {
                            logger.error("同步会议错误",e);
                            e.printStackTrace();
                        }
                    });
                }
            });
        });
    }

    public void afterPropertiesSet() throws Exception
    {
        this.start();
    }
}
