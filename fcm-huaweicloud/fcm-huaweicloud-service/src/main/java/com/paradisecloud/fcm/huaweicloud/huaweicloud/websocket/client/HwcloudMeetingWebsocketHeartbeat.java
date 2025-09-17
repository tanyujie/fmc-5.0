package com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.client;


import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudMeetingBridge;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.utils.StringUtils;
import com.sinhy.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;


/**
 * @author nj
 * @date 2023/3/21 14:52
 */
@Component
public class HwcloudMeetingWebsocketHeartbeat extends Thread implements InitializingBean {

    private Logger logger= LoggerFactory.getLogger(getClass());


    @Override
    public void run() {
        new Thread(()->{
            while (true){
                ThreadUtils.sleep(60*1000);
                try {
                    Map<String, HwcloudMeetingWebsocketClient> smcWebsocketClientMap = HwcloudMeetingWebsocketContext.getSmcWebsocketClientMap();

                    Iterator<Map.Entry<String, HwcloudMeetingWebsocketClient>> iterator = smcWebsocketClientMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, HwcloudMeetingWebsocketClient> entry = iterator.next();
                        HwcloudMeetingWebsocketClient client = entry.getValue();
                        HwcloudMeetingBridge hwcloudBridge = client.getHwcloudBridge();
                        if (hwcloudBridge.isDeleted()) {
                            iterator.remove();
                        }else {

                        } JSONObject jsonObject = new JSONObject();
                        jsonObject.put("sequence", StringUtils.generateNumericSequence(20,30));
                        jsonObject.put("action", "HeartBeat");
                        client.sendMessage(jsonObject.toJSONString());

                    }

                } catch (Exception e) {
                    logger.info(e.getMessage());
                }
            }
        }).start();

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}
