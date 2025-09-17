package com.paradisecloud.smc3.websocket.interfaces;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.websocket.client.Smc3WebSocketProcessor;

/**
 * @author nj
 * @date 2023/3/1 14:41
 */
public interface ISmc3WebSocketService {

    void process(JSONObject json, Smc3Bridge smcBridge);
    void process(JSONObject json, Smc3Bridge smcBridge, Smc3WebSocketProcessor smc3WebSocketProcessor);
}
