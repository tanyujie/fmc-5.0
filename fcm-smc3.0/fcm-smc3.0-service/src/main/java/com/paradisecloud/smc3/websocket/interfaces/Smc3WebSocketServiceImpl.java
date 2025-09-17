package com.paradisecloud.smc3.websocket.interfaces;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.websocket.client.Smc3WebSocketProcessor;
import com.paradisecloud.smc3.websocket.processormessage.Smc3MessageInfoProcessorMessage;
import com.paradisecloud.smc3.websocket.processormessage.Smc3MessageInfoProcessorMessageQueue;
import com.paradisecloud.smc3.websocket.processormessage.Smc3RealTimeInfoProcessorMessage;
import com.paradisecloud.smc3.websocket.processormessage.Smc3RealTimeInfoProcessorMessageQueue;
import org.springframework.stereotype.Service;

/**
 * @author nj
 * @date 2023/3/1 16:06
 */
@Service
public class Smc3WebSocketServiceImpl implements ISmc3WebSocketService {
    @Override
    public void process(JSONObject json, Smc3Bridge smcBridge) {
        Smc3RealTimeInfoProcessorMessageQueue.getInstance().put(new Smc3RealTimeInfoProcessorMessage(smcBridge,json));
    }

    @Override
    public void process(JSONObject  json, Smc3Bridge smcBridge, Smc3WebSocketProcessor smc3WebSocketProcessor) {
        Smc3MessageInfoProcessorMessageQueue.getInstance().put(new Smc3MessageInfoProcessorMessage(smcBridge,json, smc3WebSocketProcessor));
    }
}
