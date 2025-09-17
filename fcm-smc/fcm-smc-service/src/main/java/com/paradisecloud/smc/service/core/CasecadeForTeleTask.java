package com.paradisecloud.smc.service.core;

import com.paradisecloud.com.fcm.smc.modle.DetailConference;
import com.paradisecloud.fcm.common.event.BusinessSmcEvent;
import com.paradisecloud.fcm.common.event.SmcEventModel;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridgeCache;
import com.paradisecloud.fcm.smc.cache.modle.SmcConferenceContextCache;
import com.paradisecloud.smc.dao.model.BusiSmcConferenceState;
import com.paradisecloud.smc.service.IBusiSmcConferenceStateService;
import com.paradisecloud.smc.service.SmcConferenceService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author nj
 * @date 2022/8/1 15:01
 */
//@Component
//@Order
public class CasecadeForTeleTask implements ApplicationRunner, ApplicationContextAware {


    public static final String PROD = "prod";


    private static ApplicationContext applicationContext;

    @Resource
    private IBusiSmcConferenceStateService conferenceStateService;

    @Resource
    private SmcConferenceService smcConferenceService;

    @Override
    public void run(ApplicationArguments args)   {
        Map<String, String> chooseParticipantMap = SmcConferenceContextCache.getInstance().getChooseParticipantMap();
        List<BusiSmcConferenceState> busiSmcConferenceStates = conferenceStateService.selectBusiSmcConferenceStateList(new BusiSmcConferenceState());
        if(!CollectionUtils.isEmpty(busiSmcConferenceStates)){
            for (BusiSmcConferenceState busiSmcConferenceState : busiSmcConferenceStates) {
                chooseParticipantMap.put(busiSmcConferenceState.getConferenceId(), busiSmcConferenceState.getChooseid());
            }
        }

        final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
            scheduledExecutorService.scheduleAtFixedRate(()-> {
                try {
                    Map<String, SmcBridge> conferenceBridge = SmcBridgeCache.getInstance().getConferenceBridge();
                    if(conferenceBridge!=null){
                        conferenceBridge.forEach((k,v)->{
                            String s = chooseParticipantMap.get(k);

                           Map<String, String>  uriMap = SmcConferenceContextCache.getInstance().getCascadeConference().get(k);
                           if(!CollectionUtils.isEmpty(uriMap)){
                               for (String uri : uriMap.values()) {
                                   if(Strings.isNotBlank(uri)&&Strings.isBlank(s)){
                                       DetailConference detailConference = null;
                                       try {
                                           detailConference = smcConferenceService.getDetailConferenceInfoById(k);
                                       } catch (Exception e) {
                                           e.printStackTrace();
                                       }
                                       if(detailConference!=null){
                                           String broadcastId = detailConference.getConferenceState().getBroadcastId();
                                           if(Strings.isBlank(broadcastId)){

                                               String[] split = uri.split("@");
                                               String number = split[0];
                                               String ip = split[1];

                                               SmcEventModel smcEventModel = new SmcEventModel();
                                               smcEventModel.setIp(ip);
                                               smcEventModel.setNumber(number);
                                               BusinessSmcEvent businessSmcEvent = new BusinessSmcEvent(smcEventModel);
                                               applicationContext.publishEvent(businessSmcEvent);

                                           }
                                       }
                                   }

                               }
                           }
                        });
                    }


                } catch (Throwable e) {
                    e.printStackTrace();
                }
            },6,10, TimeUnit.SECONDS);

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CasecadeForTeleTask.applicationContext=applicationContext;
    }
}
