/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : WebsocketAbnormalMonitor.java
 * Package     : com.paradisecloud.fcm.sync.thread
 * @author lilinhai
 * @since 2020-12-17 18:17
 * @version  V1.0
 */
package com.paradisecloud.smc3.websocket.client;

import com.paradisecloud.fcm.common.async.AsyncMessageProcessor;
import com.paradisecloud.fcm.common.enumer.FmeBridgeStatus;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.model.SmcBridgeStatus;
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
public class Smc3WebsocketReconnecter extends AsyncMessageProcessor<Smc3Bridge>
{

    /**
     * 单例线程对象
     */
    private static final Smc3WebsocketReconnecter INSTANCE = new Smc3WebsocketReconnecter();

    /**
     * <pre>构造方法</pre>
     * @author lilinhai
     * @since 2020-12-02 14:23
     */
    private Smc3WebsocketReconnecter()
    {
        super("WebsocketReconnecter-Thread");
        this.sleepMillisecondsPerProcess = 3000;
        this.waitMessage = " FCM-FME-WEBSOCKET----WebsocketReconnecter---Sleep---没有断开的websocket连接对象，websocket重连器线程进入休眠状态！";
        this.workMessage = " FCM-FME-WEBSOCKET----WebsocketReconnecter---Work---收到有断开的websocket连接对象，websocket重连器线程进入工作状态！";
    }

    @Override
    protected synchronized void process()
    {
        for (Iterator<Smc3Bridge> iterator = queue.iterator(); iterator.hasNext();)
        {
            Smc3Bridge smcBridge = iterator.next();
            try
            {

                    if (smcBridge.getWebsocketAvailable())
                    {
                        iterator.remove();
                        continue;
                    }

                    // 执行重连尝试
                    SMC3WebsocketClient mwsc = Smc3WebsocketClientObj.createWebsocketClientObj(smcBridge);

                    // 连接失败，会在onclose执行重试，因此不必单独处理try-catch
                    if (mwsc != null) {
                        try {

                            if (Objects.equals(mwsc.getReadyState(), ReadyState.NOT_YET_CONNECTED)) {

                                if (mwsc.connectBlocking()) {
                                    tokenAdd(smcBridge,mwsc);
                                } else {
                                    Thread.sleep(2000);
                                    mwsc.closeConnection(CloseFrame.ABNORMAL_CLOSE, "连接失败，connectBlocking=false");
                                    mwsc.onClose(CloseFrame.ABNORMAL_CLOSE, "连接失败，connectBlocking=false", false);
                                }
                            } else if (Objects.equals(mwsc.getReadyState(), ReadyState.CLOSED) || Objects.equals(mwsc.getReadyState(), ReadyState.CLOSING)) {
                                if (mwsc.reconnectBlocking()) {
                                    tokenAdd(smcBridge,mwsc);
                                } else {
                                    Thread.sleep(2000);
                                    mwsc.closeConnection(CloseFrame.ABNORMAL_CLOSE, "连接失败，reconnectBlocking=false");
                                    mwsc.onClose(CloseFrame.ABNORMAL_CLOSE, "连接失败，reconnectBlocking=false", false);
                                }
                            }


                        } catch (InterruptedException e) {
                            System.out.println("链接异常");
                            smcBridge.setBridgeStatus(SmcBridgeStatus.AVAILABLE, mwsc.getToken());
                            e.printStackTrace();
                        }
                    }


            }
            catch (Throwable e)
            {
                smcBridge.setConnectionFailedReason("连接失败：" + CauseUtils.getRootCause(e));
                smcBridge.setBridgeStatus(SmcBridgeStatus.AVAILABLE, null);
                logger.error("链接异常");
            }finally {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void tokenAdd(Smc3Bridge fmeBridge, SMC3WebsocketClient mwsc) {
        fmeBridge.addWebsocketConnectionCount(mwsc.getToken());
        fmeBridge.setBridgeStatus(SmcBridgeStatus.AVAILABLE, mwsc.getToken());
    }



    @Override
    public void add(Smc3Bridge smcBridge) {
        // websocket连接数减一
        if (!queue.isEmpty()) {
            Smc3Bridge peek = this.queue.peek();
            if (!Objects.equals(smcBridge, peek)) {
                super.add(smcBridge);
            }
        } else {
            super.add(smcBridge);
        }
    }

    /**
     * <pre>获取单例</pre>
     * @author lilinhai
     * @since 2020-12-02 14:24
     * @return WebsocketAbnormalMonitor
     */
    public static Smc3WebsocketReconnecter getInstance()
    {
        return INSTANCE;
    }
}
