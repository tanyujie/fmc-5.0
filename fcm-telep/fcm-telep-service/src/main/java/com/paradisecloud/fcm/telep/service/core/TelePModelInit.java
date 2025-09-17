package com.paradisecloud.fcm.telep.service.core;


import com.paradisecloud.fcm.telep.cache.TeleBridgeCache;
import com.paradisecloud.fcm.telep.cache.TelepBridge;
import com.paradisecloud.fcm.telep.dao.model.BusiTele;
import com.paradisecloud.fcm.telep.model.busi.participants.TeleParticipant;
import com.paradisecloud.fcm.telep.service.interfaces.IBusiTeleDeptService;
import com.paradisecloud.fcm.telep.service.interfaces.IBusiTeleService;
import com.paradisecloud.system.service.ISysDeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nj
 * @date 2022/8/15 16:56
 */

@Component
@Slf4j
public class TelePModelInit implements ApplicationRunner {

    public static List<TeleParticipant> teleParticpantlist= new ArrayList<>();

    @Resource
    private IBusiTeleDeptService iBusiTeleDeptService;
    @Resource
    private IBusiTeleService iBusiTeleService;
    @Resource
    private ISysDeptService iSysDeptService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        List<BusiTele> busiTeles = iBusiTeleService.selectBusiTeleList(new BusiTele());

        for (BusiTele busiTele : busiTeles) {
            TelepBridge telepBridge = new TelepBridge(busiTele);
            TeleBridgeCache.getInstance().update(telepBridge);
        }
//        new Thread(()->{
//            while (true){
//                if(!CollectionUtils.isEmpty(busiTeles)){
//                    Map<String, TelepBridge> ipToTeleBridgeMap = TeleBridgeCache.getInstance().getIpToTeleBridgeMap();
//                    Set<Map.Entry<String, TelepBridge>> entries = ipToTeleBridgeMap.entrySet();
//                    for (Map.Entry<String, TelepBridge> entry : entries) {
//                        TelepBridge telepBridge = entry.getValue();
//                        TeleParticipantApiInvoker teleParticipantApiInvoker = telepBridge.getTeleParticipantApiInvoker();
//                        teleParticpantlist= teleParticipantApiInvoker.enumerate(null, null);
//
//                    }
//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        log.error("serveral calls error");
//                        break;
//
//                    }
//                }
//            }
//        }).start();

    }
}
