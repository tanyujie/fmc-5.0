package com.paradisecloud.smc3.monitor;


import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.model.SmcBridgeStatus;
import com.paradisecloud.smc3.websocket.client.SMC3WebsocketClient;
import com.paradisecloud.smc3.websocket.client.Smc3WebsocketContext;
import com.paradisecloud.smc3.websocket.client.Smc3WebsocketReconnecter;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.paradisecloud.smc3.invoker.util.ScEndpointUtil.UNAUTHORIZED;

/**
 * @author nj
 * @date 2024/5/13 8:42
 */
@Component
public class Smc3WebsocketContextMonitor extends Thread implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(Smc3WebsocketContextMonitor.class);




    @Override
    public void run() {

        logger.info("SMC3.0---WEBSOCKET在线状态监视器启动并初始化成功");
        while (true) {
            try {

                Map<String, SMC3WebsocketClient> smcWebsocketClientMap = Smc3WebsocketContext.getSmcWebsocketClientMap();
                if(smcWebsocketClientMap!=null){
                    for (SMC3WebsocketClient smc3WebsocketClient : smcWebsocketClientMap.values()) {
                        Smc3Bridge smcBridge = smc3WebsocketClient.getSmcBridge();
                        if(smcBridge.isAvailable()){
                            String s = smc3WebsocketClient.getSmcBridge().getSmcMeetingroomsInvoker().getMeetingRoomsByName("test-name", smcBridge.getSmcportalTokenInvoker().getSystemHeaders());
                            if (Strings.isNotBlank(s) && UNAUTHORIZED.equals(s)) {
                                logger.info("SMC3.0---WEBSOCKET在线状态监视器发现token已失效");
                                smcBridge.setBridgeStatus(SmcBridgeStatus.NOT_AVAILABLE,smc3WebsocketClient.getToken());
                                smcBridge.setConnectionFailedReason("用户认证失败,账户密码错误或过期");
                                smcBridge.setWebsocketAvailable(false);
                                Smc3WebsocketReconnecter.getInstance().add(smcBridge);
                            }
                        }
                    }
                }

            } catch (Throwable e) {
                logger.error("主线程wait异常：", e);
            } finally {
                try {
                    Thread.sleep(1000*30);
                } catch (InterruptedException e) {
                    logger.error("主线程wait异常：", e);
                }
            }
        }

    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}
