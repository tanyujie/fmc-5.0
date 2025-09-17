package com.paradisecloud.fcm.telep.service.impls;

import com.paradisecloud.fcm.telep.cache.TeleBridgeCache;
import com.paradisecloud.fcm.telep.cache.TelepBridge;
import com.paradisecloud.fcm.telep.cache.invoker.TeleConferenceApiInvoker;
import com.paradisecloud.fcm.telep.model.busi.ConferencesResponse;
import com.paradisecloud.fcm.telep.model.busi.TeleConference;
import com.paradisecloud.fcm.telep.model.busi.participants.TeleParticipant;
import com.paradisecloud.fcm.telep.model.busi.participants.VideoToUse;
import com.paradisecloud.fcm.telep.model.request.EnumerateFilter;
import com.paradisecloud.fcm.telep.service.interfaces.IBusiTeleConferenceService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author nj
 * @date 2022/10/14 15:15
 */
@Service
public class BusiTeleConferenceServiceImpl implements IBusiTeleConferenceService {

    public static final String SMC_MCU_ID = "HUAWEI MCU";
    @Override
    public void startConference(String number, String ip,String accessCode) {

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
                    telepBridge.getTeleConferenceApiInvoker().conferenceModify(first.get().getConferenceName());
                    try {
                        new Thread(()-> {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            List<TeleParticipant> result = new ArrayList<>();
                            List<TeleParticipant> enumerate = telepBridge.getTeleParticipantApiInvoker().enumerate(EnumerateFilter.CONNECTED, null, result);
                            if (!CollectionUtils.isEmpty(enumerate)) {
                                Optional<TeleParticipant> find = enumerate.stream().filter(sc -> Objects.equals(sc.getDisplayName(), SMC_MCU_ID)).findFirst();

                                if (find.isPresent()) {
                                    TeleParticipant todues = find.get();
                                    for (TeleParticipant p : enumerate) {
                                        if (!Objects.equals(todues.getParticipantName(), p.getParticipantName())) {
                                            p.setFocusType("participant");
                                            VideoToUse videoToUse = new VideoToUse();
                                            videoToUse.setParticipantProtocol(todues.getParticipantProtocol());
                                            videoToUse.setParticipantName(todues.getParticipantName());
                                            videoToUse.setParticipantType(todues.getParticipantType());
                                            p.setFocusParticipant(videoToUse);
                                            p.setAudioRxMuted(true);
                                        } else {
                                            p.setAudioRxMuted(false);
                                            p.setImportant(true);
                                        }
                                        try {
                                            telepBridge.getTeleParticipantApiInvoker().participantModify(p);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
//                                    todues.setAudioRxMuted(false);
//                                    telepBridge.getTeleParticipantApiInvoker().participantModify(todues);
                                }
                            }

                        }).start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    @Override
    public void startConference(String number, String ip) {

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
                    telepBridge.getTeleConferenceApiInvoker().conferenceModify(first.get().getConferenceName());
                    try {
//                        new Thread(() -> {
//                            try {
//                                Thread.sleep(800);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            List<TeleParticipant> result = new ArrayList<>();
//                            List<TeleParticipant> enumerate = telepBridge.getTeleParticipantApiInvoker().enumerate(EnumerateFilter.CONNECTED, null, result);
//                            if (!CollectionUtils.isEmpty(enumerate)) {
//                                Optional<TeleParticipant> find = enumerate.stream().filter(sc -> Objects.equals(sc.getDisplayName(), SMC_MCU_ID)).findFirst();
//
//                                if (find.isPresent()) {
//                                    TeleParticipant todues = find.get();
//                                    for (TeleParticipant p : enumerate) {
//                                        try {
//                                            if (!Objects.equals(todues.getParticipantName(), p.getParticipantName())) {
//                                                p.setFocusType("participant");
//                                                VideoToUse videoToUse = new VideoToUse();
//                                                videoToUse.setParticipantProtocol(todues.getParticipantProtocol());
//                                                videoToUse.setParticipantName(todues.getParticipantName());
//                                                videoToUse.setParticipantType(todues.getParticipantType());
//                                                p.setFocusParticipant(videoToUse);
//                                                p.setAudioRxMuted(true);
//                                            } else {
//                                                p.setAudioRxMuted(false);
//                                                p.setImportant(true);
//                                            }
//
//                                            telepBridge.getTeleParticipantApiInvoker().participantModify(p);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//
//                                    }
//                                    todues.setAudioRxMuted(false);
//                                    telepBridge.getTeleParticipantApiInvoker().participantModify(todues);
//                                }
//                            }
//
//                        }).start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    @Override
    public void settingConference(String number, String ip,String accessCode) {

        if (Strings.isBlank(number) || Strings.isBlank(ip)) {
            return;
        }

        Map<String, TelepBridge> ipToTeleBridgeMap = TeleBridgeCache.getInstance().getIpToTeleBridgeMap();

        TelepBridge telepBridge = ipToTeleBridgeMap.get(ip);
        if (telepBridge == null) {
            return;
        }
        /**

         ConferencesResponse response = telepBridge.getTeleConferenceApiInvoker().enumerateBean(null);
         if (response != null) {
         List<TeleConference> conferences = response.getConferences();
         if (!CollectionUtils.isEmpty(conferences)) {
         Optional<TeleConference> first = conferences.stream().filter(p -> Objects.equals(number, p.getNumericId())).findFirst();
         if (first.isPresent()) {
         try {
         new Thread(()-> {
         List<TeleParticipant> result = new ArrayList<>();
         List<TeleParticipant> enumerate = telepBridge.getTeleParticipantApiInvoker().enumerate(EnumerateFilter.CONNECTED, null, result);
         if (!CollectionUtils.isEmpty(enumerate)) {
         Optional<TeleParticipant> find = enumerate.stream().filter(sc -> Objects.equals(sc.getDisplayName(), SMC_MCU_ID)).findFirst();

         if (find.isPresent()) {
         TeleParticipant todues = find.get();
         for (TeleParticipant p : enumerate) {
         if (!Objects.equals(p.getParticipantName(), todues.getParticipantName())) {
         p.setFocusType("participant");
         VideoToUse videoToUse = new VideoToUse();
         videoToUse.setParticipantProtocol(todues.getParticipantProtocol());
         videoToUse.setParticipantName(todues.getParticipantName());
         videoToUse.setParticipantType(todues.getParticipantType());
         p.setFocusParticipant(videoToUse);
         } else {
         p.setImportant(true);
         p.setAudioRxMuted(false);
         }

         telepBridge.getTeleParticipantApiInvoker().participantModify(p);
         }
         }
         }

         }).start();

         } catch (Throwable e) {
         e.printStackTrace();
         }
         }
         }
         }
         **/
    }

    @Override
    public void conferenceLock(String uri, Boolean locked) {

        if (Strings.isBlank(uri)) {
            return;
        }
        String[] split = uri.split("@");
        String number = split[0];
        String ip = split[1];


        Map<String, TelepBridge> ipToTeleBridgeMap = TeleBridgeCache.getInstance().getIpToTeleBridgeMap();
        TelepBridge telepBridge = ipToTeleBridgeMap.get(ip);
        TeleConferenceApiInvoker teleConferenceApiInvoker = telepBridge.getTeleConferenceApiInvoker();
        ConferencesResponse response = teleConferenceApiInvoker.enumerateBean(null);
        if (response != null) {
            List<TeleConference> conferences = response.getConferences();
            if (!CollectionUtils.isEmpty(conferences)) {
                Optional<TeleConference> first = conferences.stream().filter(p -> Objects.equals(number, p.getNumericId())).findFirst();
                if (first.isPresent()) {
                    String conferenceName = first.get().getConferenceName();
                    teleConferenceApiInvoker.conferenceLock(conferenceName, locked);
                }
            }
        }
    }

    @Override
    public void conferenceEnd(String ip,String conferenceName) {
        Map<String, TelepBridge> ipToTeleBridgeMap = TeleBridgeCache.getInstance().getIpToTeleBridgeMap();
        TelepBridge telepBridge = ipToTeleBridgeMap.get(ip);
        telepBridge.getTeleConferenceApiInvoker().end(conferenceName);
    }

    @Override
    public ConferencesResponse conferenceList(String ip, EnumerateFilter enumerateFilter) {
        Map<String, TelepBridge> ipToTeleBridgeMap = TeleBridgeCache.getInstance().getIpToTeleBridgeMap();
        TelepBridge telepBridge = ipToTeleBridgeMap.get(ip);
        return telepBridge.getTeleConferenceApiInvoker().enumerateBean(null);
    }
}
