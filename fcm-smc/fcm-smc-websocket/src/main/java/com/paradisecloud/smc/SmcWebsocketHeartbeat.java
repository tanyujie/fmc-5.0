package com.paradisecloud.smc;


import com.paradisecloud.common.utils.Threads;
import com.sinhy.utils.ThreadUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * @author nj
 * @date 2023/3/21 14:52
 */
@Component
public class SmcWebsocketHeartbeat  extends Thread implements InitializingBean {


    @Override
    public void run() {
        new Thread(()->{
            while (true){
                ThreadUtils.sleep(10000);
                try {
                    Map<String, SMCWebsocketClient> smcWebsocketClientMap = SmcWebsocketContext.getSmcWebsocketClientMap();
                    smcWebsocketClientMap.forEach((k,v)->v.sendMessage(TopicMessage.getHeartbeatMessage()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}
