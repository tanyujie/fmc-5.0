package com.paradisecloud.smc.interfaces;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.smc.SmcWebSocketProcessor;
import com.paradisecloud.smc.processormessage.MessageInfoProcessorMessage;
import com.paradisecloud.smc.processormessage.MessageInfoProcessorMessageQueue;
import com.paradisecloud.smc.processormessage.RealTimeInfoProcessorMessage;
import com.paradisecloud.smc.processormessage.RealTimeInfoProcessorMessageQueue;
import org.springframework.stereotype.Service;

/**
 * @author nj
 * @date 2023/3/1 16:06
 */
@Service
public class SmcWebSocketServiceImpl implements ISmcWebSocketService{
    @Override
    public void process(JSONObject json, SmcBridge smcBridge) {
        RealTimeInfoProcessorMessageQueue.getInstance().put(new RealTimeInfoProcessorMessage(smcBridge,json));
    }

    @Override
    public void process(JSONObject  json, SmcBridge smcBridge, SmcWebSocketProcessor smcWebSocketProcessor) {
        MessageInfoProcessorMessageQueue.getInstance().put(new MessageInfoProcessorMessage(smcBridge,json,smcWebSocketProcessor));
    }
}
