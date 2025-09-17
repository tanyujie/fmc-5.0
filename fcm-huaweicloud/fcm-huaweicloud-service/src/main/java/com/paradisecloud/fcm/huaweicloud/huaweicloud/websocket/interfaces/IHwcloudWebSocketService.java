package com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.interfaces;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudBridge;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.client.HwcloudWebSocketProcessor;

/**
 * @author nj
 * @date 2023/3/1 14:41
 */
public interface IHwcloudWebSocketService {

    void process(JSONObject json, HwcloudBridge smcBridge);
    void process(JSONObject json, HwcloudBridge smcBridge, HwcloudWebSocketProcessor HwcloudWebSocketProcessor);
}
