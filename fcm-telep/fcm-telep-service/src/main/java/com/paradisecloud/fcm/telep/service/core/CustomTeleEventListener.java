package com.paradisecloud.fcm.telep.service.core;

import com.paradisecloud.fcm.common.event.BusinessSmcEvent;
import com.paradisecloud.fcm.common.event.SmcEventModel;
import com.paradisecloud.fcm.telep.cache.TeleBridgeCache;
import com.paradisecloud.fcm.telep.cache.TelepBridge;
import com.paradisecloud.fcm.telep.model.busi.ConferencesResponse;
import com.paradisecloud.fcm.telep.model.busi.TeleConference;
import com.paradisecloud.fcm.telep.model.busi.participants.TeleParticipant;
import com.paradisecloud.fcm.telep.model.busi.participants.VideoToUse;
import com.paradisecloud.fcm.telep.model.request.EnumerateFilter;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author nj
 * @date 2022/10/25 10:59
 */
@Component
public class CustomTeleEventListener implements ApplicationListener<ApplicationEvent> {


    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if(event instanceof BusinessSmcEvent){

            SmcEventModel source = (SmcEventModel)event.getSource();
            String number = source.getNumber();
            String ip = source.getIp();
            if (Strings.isBlank(number) || Strings.isBlank(ip)) {
                return;
            }

            Map<String, TelepBridge> ipToTeleBridgeMap = TeleBridgeCache.getInstance().getIpToTeleBridgeMap();

            TelepBridge telepBridge = ipToTeleBridgeMap.get(ip);
            if (telepBridge == null) {
                return;
            }
            ConferencesResponse response = telepBridge.getTeleConferenceApiInvoker().enumerateBean(null);
            if (response != null) {
                List<TeleConference> conferences = response.getConferences();
                if (!CollectionUtils.isEmpty(conferences)) {
                    Optional<TeleConference> first = conferences.stream().filter(p -> Objects.equals(number, p.getNumericId())).findFirst();
                    if (first.isPresent()) {
                        try {
                            Thread.sleep(1000);
                            new Thread(()-> {
                                List<TeleParticipant> result = new ArrayList<>();
                                List<TeleParticipant> enumerate = telepBridge.getTeleParticipantApiInvoker().enumerate(EnumerateFilter.CONNECTED, null, result);
                                if (!CollectionUtils.isEmpty(enumerate)) {
                                    Optional<TeleParticipant> find = enumerate.stream().filter(sc -> Objects.equals(sc.getDisplayName(), number)).findFirst();

                                    if (find.isPresent()) {
                                        TeleParticipant todues = find.get();
                                        for (TeleParticipant p : enumerate) {
                                            if (!Objects.equals(p.getDisplayName(), todues.getDisplayName())) {

                                                p.setFocusType("participant");
                                                VideoToUse videoToUse = new VideoToUse();
                                                videoToUse.setParticipantProtocol(todues.getParticipantProtocol());
                                                videoToUse.setParticipantName(todues.getParticipantName());
                                                videoToUse.setParticipantType(todues.getParticipantType());
                                                p.setFocusParticipant(videoToUse);
                                                telepBridge.getTeleParticipantApiInvoker().participantModify(p);
                                            }
                                        }
                                    }
                                }

                            }).start();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }
}
