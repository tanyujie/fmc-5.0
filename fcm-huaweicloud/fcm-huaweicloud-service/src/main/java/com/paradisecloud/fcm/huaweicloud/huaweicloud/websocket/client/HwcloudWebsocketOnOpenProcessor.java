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
package com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.client;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.async.AsyncMessageProcessor;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.event.SubscriptionTypeEnum;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.utils.StringUtils;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.interfaces.IHwcloudWebSocketService;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.Base64Utils;

/**
 * <pre>websocket首次打开执行器</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2020-12-17 18:17
 */
public class HwcloudWebsocketOnOpenProcessor extends AsyncMessageProcessor<HwcloudMeetingWebsocketClient> {

    /**
     * 单例线程对象
     */
    private static final HwcloudWebsocketOnOpenProcessor INSTANCE = new HwcloudWebsocketOnOpenProcessor();

    private IHwcloudWebSocketService mcuNodeWebSocketService;

    /**
     * <pre>构造方法</pre>
     *
     * @author lilinhai
     * @since 2020-12-02 14:23
     */
    private HwcloudWebsocketOnOpenProcessor() {
        super("WebsocketOnOpenProcessor-Thread");
        this.sleepMillisecondsPerProcess = 3000;
        this.waitMessage = " FCM-HWCLOUD-WEBSOCKET----HWCLOUDWebsocketOnOpenProcessor---Sleep---没有新创建的websocket客户端对象，websocket首次打开执行器线程进入休眠状态！";
        this.workMessage = " FCM-HWCLOUD-WEBSOCKET----HWCLOUDWebsocketOnOpenProcessor---Work---收到新创建的websocket连接对象通知，websocket首次打开执行器线程进入工作状态！";
        this.mcuNodeWebSocketService = BeanFactory.getBean(IHwcloudWebSocketService.class);
    }

    @Override
    protected void process(HwcloudMeetingWebsocketClient websocketClient) {
        new Thread(() -> {
            SubscribeData subscribeData = new SubscribeData();
            subscribeData.setSubscribeType(SubscriptionTypeEnum.getCodeList());
            subscribeData.setConferenceID(websocketClient.getHwcloudBridge().getConfID());
            subscribeData.setConfToken("Basic "+ Base64Utils.decode(websocketClient.getHwcloudBridge().getTokenInfo().getToken()));

            JSONObject sub = new JSONObject();
            sub.put("action","Subscribe");
            sub.put("sequence", StringUtils.generateNumericSequence(20,30));
            sub.put("data",subscribeData);
            websocketClient.send(sub.toJSONString());
        }).start();
    }



    /**
     * <pre>获取单例</pre>
     *
     * @return SmcWebsocketOnOpenProcessor
     * @author lilinhai
     * @since 2020-12-02 14:24
     */
    public static HwcloudWebsocketOnOpenProcessor getInstance() {
        return INSTANCE;
    }
}
