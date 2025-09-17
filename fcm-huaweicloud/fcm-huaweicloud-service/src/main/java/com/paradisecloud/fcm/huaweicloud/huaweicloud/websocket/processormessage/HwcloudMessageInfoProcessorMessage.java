package com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.processormessage;


import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudBridge;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.client.HwcloudWebSocketProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author nj
 * @date 2023/5/15 15:34
 */
public class HwcloudMessageInfoProcessorMessage extends HwcloudBusiProcessorMessage {

    private Logger logger= LoggerFactory.getLogger(getClass());

    private HwcloudWebSocketProcessor hwcloudWebSocketProcessor;

    public static final int PAGE = 10;
    public static final int _TYPE = 3;
    public static final String SUB_1 = "sub-1";

    @Override
    protected void process0() {

    }

    public HwcloudMessageInfoProcessorMessage(HwcloudBridge hwcloudBridge, JSONObject updateItem, HwcloudWebSocketProcessor hwcloudWebSocketProcessor) {
        super(hwcloudBridge, updateItem, updateItem.getString("messageId"));
        this.hwcloudWebSocketProcessor = hwcloudWebSocketProcessor;
    }

}
