package com.paradisecloud.smc3.websocket.client;


import com.paradisecloud.smc3.model.SmcBridgeStatus;
import com.sinhy.utils.ThreadUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * @author nj
 * @date 2023/3/21 14:52
 */
@Component
public class Smc3WebsocketHeartbeat extends Thread implements InitializingBean {


    @Override
    public void run() {
        new Thread(()->{
            while (true){
                ThreadUtils.sleep(10000);
                try {
                    Map<String, SMC3WebsocketClient> smcWebsocketClientMap = Smc3WebsocketContext.getSmcWebsocketClientMap();
                    smcWebsocketClientMap.forEach((k,v)->{
                        if(v!=null){
                            if(v.isOpen()){
                                v.sendMessage(TopicMessage.getHeartbeatMessage());
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
//                    Map<String, SMC3WebsocketClient> smcWebsocketClientMap = Smc3WebsocketContext.getSmcWebsocketClientMap();
//                    smcWebsocketClientMap.forEach((k,v)->{
//                        if(v!=null){
//                           if(!v.isOpen()){
//                               Smc3WebsocketReconnecter.getInstance().add(v.getSmcBridge());
//                               v.getSmcBridge().setBridgeStatus(SmcBridgeStatus.NOT_AVAILABLE,v.getToken());
//                               v.getSmcBridge().setWebsocketAvailable(false);
//                           }
//                        }
//                    });
                }
            }
        }).start();

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}
