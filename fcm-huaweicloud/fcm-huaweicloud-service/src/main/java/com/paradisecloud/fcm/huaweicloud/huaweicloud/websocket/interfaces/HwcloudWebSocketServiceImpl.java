package com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.interfaces;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudBridge;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.client.HwcloudWebSocketProcessor;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.processormessage.HwcloudMessageInfoProcessorMessage;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.processormessage.HwcloudMessageInfoProcessorMessageQueue;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.processormessage.HwcloudRealTimeInfoProcessorMessage;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.processormessage.HwcloudRealTimeInfoProcessorMessageQueue;
import org.springframework.stereotype.Service;

/**
 * @author nj
 * @date 2023/3/1 16:06
 */
@Service
public class HwcloudWebSocketServiceImpl implements IHwcloudWebSocketService {
    @Override
    public void process(JSONObject json, HwcloudBridge smcBridge) {
        HwcloudRealTimeInfoProcessorMessageQueue.getInstance().put(new HwcloudRealTimeInfoProcessorMessage(smcBridge,json));
    }

    @Override
    public void process(JSONObject  json, HwcloudBridge smcBridge, HwcloudWebSocketProcessor hwcloudWebSocketProcessor) {
        HwcloudMessageInfoProcessorMessageQueue.getInstance().put(new HwcloudMessageInfoProcessorMessage(smcBridge,json, hwcloudWebSocketProcessor));
    }
}
