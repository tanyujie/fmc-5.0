/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : WebsocketAbnormalMonitor.java
 * Package     : com.paradisecloud.fcm.sync.thread
 * @author lilinhai
 * @since 2020-12-17 18:17
 * @version  V1.0
 */
package com.paradisecloud.fcm.fme.websocket.async;

import com.paradisecloud.fcm.common.async.AsyncMessageProcessor;
import com.paradisecloud.fcm.common.enumer.FmeBridgeStatus;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.websocket.core.WebsocketClient;
import com.paradisecloud.fcm.fme.websocket.model.BusiFmeDBSynchronizer;
import com.sinhy.utils.CauseUtils;
import com.sinhy.utils.HostUtils;
import com.sinhy.utils.ThreadUtils;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.framing.CloseFrame;
import org.springframework.util.ObjectUtils;

import java.net.URI;
import java.util.Iterator;
import java.util.Objects;

/**
 * <pre>websocket重连</pre>
 * @author lilinhai
 * @since 2020-12-17 18:17
 * @version V1.0
 */
public class WebsocketReconnecter extends AsyncMessageProcessor<FmeBridge>
{

    /**
     * 单例线程对象
     */
    private static final WebsocketReconnecter INSTANCE = new WebsocketReconnecter();

    /**
     * <pre>构造方法</pre>
     * @author lilinhai
     * @since 2020-12-02 14:23
     */
    private WebsocketReconnecter()
    {
        super("WebsocketReconnecter-Thread");
        this.sleepMillisecondsPerProcess = 3000;
        this.waitMessage = " FCM-FME-WEBSOCKET----WebsocketReconnecter---Sleep---没有断开的websocket连接对象，websocket重连器线程进入休眠状态！";
        this.workMessage = " FCM-FME-WEBSOCKET----WebsocketReconnecter---Work---收到有断开的websocket连接对象，websocket重连器线程进入工作状态！";
    }

    protected synchronized void process()
    {
        for (Iterator<FmeBridge> iterator = queue.iterator(); iterator.hasNext();)
        {
            FmeBridge fmeBridge = iterator.next();
            try
            {
                if (!fmeBridge.isDeleted())
                {
                    if (fmeBridge.isAvailable() || fmeBridge.getWsAuthTokens().size() > 0)
                    {
                        iterator.remove();
                        continue;
                    }

                    // 执行重连尝试
                    WebsocketClient mwsc = WebsocketClientObj.createWebsocketClientObj(fmeBridge);

                    // 连接失败，会在onclose执行重试，因此不必单独处理try-catch
                    if (mwsc != null) {
                        try {

                            if (Objects.equals(mwsc.getReadyState(), ReadyState.NOT_YET_CONNECTED)) {

                                if (mwsc.connectBlocking()) {
                                    tokenAdd(iterator, fmeBridge, mwsc);
                                } else {
                                    Thread.sleep(2000);
                                    mwsc.closeConnection(CloseFrame.ABNORMAL_CLOSE, "连接失败，connectBlocking=false");
                                    mwsc.onClose(CloseFrame.ABNORMAL_CLOSE, "连接失败，connectBlocking=false", false);
                                }
                            } else if (Objects.equals(mwsc.getReadyState(), ReadyState.CLOSED) || Objects.equals(mwsc.getReadyState(), ReadyState.CLOSING)) {
                                if (mwsc.reconnectBlocking()) {
                                    tokenAdd(iterator, fmeBridge, mwsc);
                                } else {
                                    Thread.sleep(2000);
                                    mwsc.closeConnection(CloseFrame.ABNORMAL_CLOSE, "连接失败，reconnectBlocking=false");
                                    mwsc.onClose(CloseFrame.ABNORMAL_CLOSE, "连接失败，reconnectBlocking=false", false);
                                }
                            }


                        } catch (InterruptedException e) {
                            fmeBridge.getFmeLogger().logWebsocketInfo("连接失败，异常终止！", true, e);
                            e.printStackTrace();
                        }
                    }
                }
                else
                {
                    fmeBridge.getFmeLogger().logWebsocketInfo("会议桥已删除，停止重连！", true);
                    iterator.remove();
                    BusiFmeDBSynchronizer.getInstance().add(fmeBridge.getBusiFme());
                }
            }
            catch (Throwable e)
            {
                fmeBridge.setConnectionFailedReason("连接失败：" + CauseUtils.getRootCause(e));
                fmeBridge.getFmeLogger().logWebsocketInfo("fme websocket connection build error", true, e);
            }
        }
    }

    private void tokenAdd(Iterator<FmeBridge> iterator, FmeBridge fmeBridge, WebsocketClient mwsc) {
        fmeBridge.addWebsocketConnectionCount(mwsc.getAuthToken());
        iterator.remove();
        fmeBridge.getFmeLogger().logWebsocketInfo("连接创建成功，准备同步FME数据！", true);
    }

    /**
     * <pre>创建websocket连接对象</pre>
     *
     * @author lilinhai
     * @param fmeBridge
     * @since 2020-12-01 18:29
     * @param url
     * @return McuNodeWebSocketClient
     */
    public WebsocketClient createWebsocketClientObj(FmeBridge fmeBridge)
    {
        WebsocketClient mwsc = null;
        try
        {
            if (HostUtils.isHostReachable(fmeBridge.getBusiFme().getIp(), 1000))
            {
                String authToken = fmeBridge.getWebSocketAuthToken();
                String url = fmeBridge.getWebSocketEventUrl(authToken);
                if (!ObjectUtils.isEmpty(url))
                {
                    mwsc = new WebsocketClient(new URI(url), fmeBridge, authToken);
                }
                else
                {
                    fmeBridge.setBridgeStatus(FmeBridgeStatus.NOT_AVAILABLE, null);
                    BusiFmeDBSynchronizer.getInstance().add(fmeBridge.getBusiFme());
                    fmeBridge.getFmeLogger().logWebsocketInfo("获取authToken失败", true, true);
                    fmeBridge.setConnectionFailedReason("获取authToken失败!");
                }
            }
            else
            {
                fmeBridge.setBridgeStatus(FmeBridgeStatus.NOT_AVAILABLE, null);
                fmeBridge.setConnectionFailedReason("会控无法拼通HostReachable=false!");
                BusiFmeDBSynchronizer.getInstance().add(fmeBridge.getBusiFme());
            }
        }
        catch (Throwable e)
        {
            fmeBridge.getFmeLogger().logWebsocketInfo("获取authToken失败", true, e);
            fmeBridge.setConnectionFailedReason("获取authToken失败: " + CauseUtils.getRootCause(e));
        }
        return mwsc;
    }

    public void add(FmeBridge fmeBridge) {
        // websocket连接数减一
        if (!queue.isEmpty()) {
            FmeBridge peek = this.queue.peek();
            if (!Objects.equals(fmeBridge, peek)) {
                super.add(fmeBridge);
            }
        } else {
            super.add(fmeBridge);
        }
    }

    /**
     * <pre>获取单例</pre>
     * @author lilinhai
     * @since 2020-12-02 14:24
     * @return WebsocketAbnormalMonitor
     */
    public static WebsocketReconnecter getInstance()
    {
        return INSTANCE;
    }
}
