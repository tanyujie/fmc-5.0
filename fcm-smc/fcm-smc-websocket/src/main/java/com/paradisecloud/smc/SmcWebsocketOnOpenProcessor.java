/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : WebsocketAbnormalMonitor.java
 * Package : com.paradisecloud.fcm.sync.thread
 *
 * @author lilinhai
 *
 * @since 2020-12-17 18:17
 *
 * @version V1.0
 */
package com.paradisecloud.smc;

import com.paradisecloud.fcm.common.async.AsyncMessageProcessor;
import com.paradisecloud.smc.interfaces.ISmcWebSocketService;
import com.sinhy.spring.BeanFactory;

/**
 * <pre>websocket首次打开执行器</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2020-12-17 18:17
 */
public class SmcWebsocketOnOpenProcessor extends AsyncMessageProcessor<SMCWebsocketClient> {

    /**
     * 单例线程对象
     */
    private static final SmcWebsocketOnOpenProcessor INSTANCE = new SmcWebsocketOnOpenProcessor();

    private ISmcWebSocketService mcuNodeWebSocketService;

    /**
     * <pre>构造方法</pre>
     *
     * @author lilinhai
     * @since 2020-12-02 14:23
     */
    private SmcWebsocketOnOpenProcessor() {
        super("WebsocketOnOpenProcessor-Thread");
        this.sleepMillisecondsPerProcess = 3000;
        this.waitMessage = " FCM-FME-WEBSOCKET----SmcWebsocketOnOpenProcessor---Sleep---没有新创建的websocket客户端对象，websocket首次打开执行器线程进入休眠状态！";
        this.workMessage = " FCM-FME-WEBSOCKET----SmcWebsocketOnOpenProcessor---Work---收到新创建的websocket连接对象通知，websocket首次打开执行器线程进入工作状态！";
        this.mcuNodeWebSocketService = BeanFactory.getBean(ISmcWebSocketService.class);
    }

    @Override
    protected void process(SMCWebsocketClient websocketClient) {
        new Thread(() -> {
            websocketClient.getWebSocketProcessor().sendFirstSubscriptionRequest();
        }).start();
    }



    /**
     * <pre>获取单例</pre>
     *
     * @return SmcWebsocketOnOpenProcessor
     * @author lilinhai
     * @since 2020-12-02 14:24
     */
    public static SmcWebsocketOnOpenProcessor getInstance() {
        return INSTANCE;
    }
}
