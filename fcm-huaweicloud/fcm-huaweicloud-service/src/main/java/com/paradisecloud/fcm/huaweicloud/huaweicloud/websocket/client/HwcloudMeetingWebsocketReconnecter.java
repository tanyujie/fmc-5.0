/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : WebsocketAbnormalMonitor.java
 * Package     : com.paradisecloud.fcm.sync.thread
 * @author lilinhai
 * @since 2020-12-17 18:17
 * @version  V1.0
 */
package com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.client;

import com.paradisecloud.fcm.common.async.AsyncMessageProcessor;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContextCache;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudMeetingBridge;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.HwcloudBridgeStatus;
import com.sinhy.utils.CauseUtils;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.framing.CloseFrame;

import java.util.Iterator;
import java.util.Objects;

/**
 * <pre>websocket重连</pre>
 * @author lilinhai
 * @since 2020-12-17 18:17
 * @version V1.0
 */
public class HwcloudMeetingWebsocketReconnecter extends AsyncMessageProcessor<HwcloudMeetingBridge>
{

    /**
     * 单例线程对象
     */
    private static final HwcloudMeetingWebsocketReconnecter INSTANCE = new HwcloudMeetingWebsocketReconnecter();

    /**
     * <pre>构造方法</pre>
     * @author lilinhai
     * @since 2020-12-02 14:23
     */
    private HwcloudMeetingWebsocketReconnecter()
    {
        super("WebsocketReconnecter-Thread");
        this.sleepMillisecondsPerProcess = 3000;
        this.waitMessage = " FCM-FME-WEBSOCKET----WebsocketReconnecter---Sleep---没有断开的websocket连接对象，websocket重连器线程进入休眠状态！";
        this.workMessage = " FCM-FME-WEBSOCKET----WebsocketReconnecter---Work---收到有断开的websocket连接对象，websocket重连器线程进入工作状态！";
    }

    @Override
    protected synchronized void process()
    {
        for (Iterator<HwcloudMeetingBridge> iterator = queue.iterator(); iterator.hasNext();)
        {
            HwcloudMeetingBridge hwcloudMeetingBridge = iterator.next();
            try
            {

                    if (hwcloudMeetingBridge.getWebsocketAvailable())
                    {
                        iterator.remove();
                        continue;
                    }
                HwcloudConferenceContext hwcloudConferenceContext = HwcloudConferenceContextCache.getInstance().get(hwcloudMeetingBridge.getConfID());
                    if(hwcloudConferenceContext==null||hwcloudConferenceContext.isEnd()){
                        iterator.remove();
                        continue;
                    }

                // 执行重连尝试
                    HwcloudMeetingWebsocketClient mwsc = HwcloudWebsocketClientObj.createWebsocketClientObj(hwcloudMeetingBridge);

                    // 连接失败，会在onclose执行重试，因此不必单独处理try-catch
                    if (mwsc != null) {
                        try {

                            if (Objects.equals(mwsc.getReadyState(), ReadyState.NOT_YET_CONNECTED)) {

                                if (mwsc.connectBlocking()) {
                                    tokenAdd(hwcloudMeetingBridge,mwsc);
                                } else {
                                    Thread.sleep(2000);
                                    mwsc.closeConnection(CloseFrame.ABNORMAL_CLOSE, "连接失败，connectBlocking=false");
                                    mwsc.onClose(CloseFrame.ABNORMAL_CLOSE, "连接失败，connectBlocking=false", false);
                                }
                            } else if (Objects.equals(mwsc.getReadyState(), ReadyState.CLOSED) || Objects.equals(mwsc.getReadyState(), ReadyState.CLOSING)) {
                                if (mwsc.reconnectBlocking()) {
                                    tokenAdd(hwcloudMeetingBridge,mwsc);
                                } else {
                                    Thread.sleep(2000);
                                    mwsc.closeConnection(CloseFrame.ABNORMAL_CLOSE, "连接失败，reconnectBlocking=false");
                                    mwsc.onClose(CloseFrame.ABNORMAL_CLOSE, "连接失败，reconnectBlocking=false", false);
                                }
                            }


                        } catch (InterruptedException e) {
                           logger.info("链接异常",e.getMessage());
                            hwcloudMeetingBridge.setBridgeStatus(HwcloudBridgeStatus.AVAILABLE, mwsc.getToken());
                        }
                    }


            }
            catch (Throwable e)
            {
                hwcloudMeetingBridge.setConnectionFailedReason("连接失败：" + CauseUtils.getRootCause(e));
                hwcloudMeetingBridge.setBridgeStatus(HwcloudBridgeStatus.AVAILABLE, null);
                logger.error("链接异常");
            }
        }
    }

    private void tokenAdd(HwcloudMeetingBridge hwcloudMeetingBridge, HwcloudMeetingWebsocketClient mwsc) {
        hwcloudMeetingBridge.addWebsocketConnectionCount(mwsc.getToken());
        hwcloudMeetingBridge.setBridgeStatus(HwcloudBridgeStatus.AVAILABLE, mwsc.getToken());
        hwcloudMeetingBridge.setWebsocketAvailable(true);
    }



    @Override
    public void add(HwcloudMeetingBridge hwcloudMeetingBridge) {
        // websocket连接数减一
        if (!queue.isEmpty()) {
            HwcloudMeetingBridge peek = this.queue.peek();
            if (!Objects.equals(hwcloudMeetingBridge, peek)) {
                super.add(hwcloudMeetingBridge);
            }
        } else {
            super.add(hwcloudMeetingBridge);
        }
    }

    /**
     * <pre>获取单例</pre>
     * @author lilinhai
     * @since 2020-12-02 14:24
     * @return WebsocketAbnormalMonitor
     */
    public static HwcloudMeetingWebsocketReconnecter getInstance()
    {
        return INSTANCE;
    }
}
