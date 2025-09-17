package com.paradisecloud.fcm.smc2.model.layout;

import com.paradisecloud.com.fcm.smc.modle.request.MultiPicPollRequest;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.cascade.ConferenceCascadeHandler;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.McuAttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.enumer.AttendeeImportance;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author nj
 * @date 2023/6/6 17:13
 */
public class ChairManSmc2PollingThread extends Thread {


    private static final Logger logger = LoggerFactory.getLogger(ChairManSmc2PollingThread.class);
    public static final int _WAITINGTIME = 1000;
    private Integer interval = 1;
    private List<List<String>> subPics;
    private boolean supended = true;
    private String confId;
    private String target;
    private Integer presenceMode;
    private String chairmanUri = null;
    private boolean stopState = false;
    private Smc2ConferenceContext smc2ConferenceContext;
    private Set<AttendeeSmc2> tempIds = new HashSet<>();
    private ConferenceServiceEx conferenceServiceEx;
    private  MultiPicPollRequest multiPicPollRequest;
    private  List<AttendeeSmc2> pollingAttendeeSmc2List = new ArrayList<>();
    public ChairManSmc2PollingThread(Integer interval, List<List<String>> subPics, String confId, String target, Integer presenceMode, String chairmanUri, ConferenceServiceEx conferenceServiceEx,Smc2ConferenceContext smc2ConferenceContext,MultiPicPollRequest multiPicPollRequest, List<AttendeeSmc2> pollingAttendeeSmc2List) {
        this.interval = interval;
        this.subPics = subPics;
        this.confId = confId;
        this.target = target;
        this.presenceMode = presenceMode;
        this.chairmanUri = chairmanUri;
        this.conferenceServiceEx = conferenceServiceEx;
        this.supended=false;
        this.smc2ConferenceContext=smc2ConferenceContext;
        this.multiPicPollRequest= multiPicPollRequest;
        this.pollingAttendeeSmc2List=pollingAttendeeSmc2List;
    }

    public ChairManSmc2PollingThread(Integer interval, List<List<String>> subPics, String confId, String target, Integer presenceMode, String chairmanUri, ConferenceServiceEx conferenceServiceEx,Smc2ConferenceContext smc2ConferenceContext) {
        this.interval = interval;
        this.subPics = subPics;
        this.confId = confId;
        this.target = target;
        this.presenceMode = presenceMode;
        this.chairmanUri = chairmanUri;
        this.conferenceServiceEx = conferenceServiceEx;
        this.supended=false;
        this.smc2ConferenceContext=smc2ConferenceContext;
    }
    @Override
    public void run() {

        logger.info("ChairManSmc2PollingThread " + this.confId + " start ... " + this.chairmanUri);

        try {
            while (!stopState) {
                while (this.supended) {
                    try {
                        synchronized (this) {
                            if (this.supended) {
                                this.wait();
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (this.stopState) {
                    break;
                }


                if (smc2ConferenceContext.isUpCascadeConference() && multiPicPollRequest.getPicNum() == 1 && isCascadePolling(smc2ConferenceContext, multiPicPollRequest)) {
                    for (AttendeeSmc2 attendeeSmc2 : pollingAttendeeSmc2List) {
                        for (AttendeeSmc2 tempId : tempIds) {
                            AttendeeImportance.COMMON.processAttendeeWebsocketMessage(tempId);
                            Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc2ConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, tempId.getUpdateMap());
                        }
                        tempIds.clear();
                        conferenceServiceEx.setVideoSourceEx(confId, chairmanUri, attendeeSmc2.getRemoteParty(), 0);
                        synchronized (this) {
                            if (this.supended) {
                                this.wait();
                            }
                            if (this.stopState) {
                                break;
                            }
                        }
                        if (attendeeSmc2 instanceof McuAttendeeSmc2) {
                            BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(attendeeSmc2.getCascadeConferenceId()));
                            if (downCascadeConferenceContext != null) {
                                ConferenceCascadeHandler.chooseSee(downCascadeConferenceContext.getId(), attendeeSmc2.getId(), true, false, true);
                            }
                        }

                        Map<String, AttendeeSmc2> uuidAttendeeMapByUri = smc2ConferenceContext.getUuidAttendeeMapByUri(attendeeSmc2.getRemoteParty());
                        if (uuidAttendeeMapByUri != null) {
                            for (AttendeeSmc2 value : uuidAttendeeMapByUri.values()) {
                                AttendeeImportance.ROUND.processAttendeeWebsocketMessage(value);
                            }
                        }
                        tempIds.add(attendeeSmc2);
                        reInterrupt(this.interval * 1000);
                    }

                }else {
                    for (List<String> subPic : subPics) {
                        synchronized (this) {
                            if (this.supended) {
                                this.wait();
                            }
                            if (this.stopState) {
                                break;
                            }
                        }
                        for (AttendeeSmc2 tempId : tempIds) {
                            synchronized (this) {
                                if (this.supended) {
                                    this.wait();
                                }
                                if (this.stopState) {
                                    break;
                                }
                            }
                            AttendeeImportance.COMMON.processAttendeeWebsocketMessage(tempId);
                            Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc2ConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, tempId.getUpdateMap());
                        }
                        tempIds.clear();
                        Integer resultCode = conferenceServiceEx.setContinuousPresenceEx(this.confId, this.target, this.presenceMode, subPic);
                        String videoSourceUri = "";
                        conferenceServiceEx.setVideoSourceEx(confId, chairmanUri, videoSourceUri, 0);
                        for (String uri : subPic) {
                            synchronized (this) {
                                if (this.supended) {
                                    this.wait();
                                }
                                if (this.stopState) {
                                    break;
                                }
                            }
                            Map<String, AttendeeSmc2> uuidAttendeeMapByUri = smc2ConferenceContext.getUuidAttendeeMapByUri(uri);
                            if(uuidAttendeeMapByUri!=null){
                                Collection<AttendeeSmc2> values = uuidAttendeeMapByUri.values();
                                for (AttendeeSmc2 attendeeSmc2 : values) {
                                    if (attendeeSmc2 instanceof McuAttendeeSmc2) {
                                        BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(attendeeSmc2.getCascadeConferenceId()));
                                        if (downCascadeConferenceContext != null) {
                                            ConferenceCascadeHandler.chooseSee(downCascadeConferenceContext.getId(), attendeeSmc2.getId(), true, false, true);
                                        }
                                    }
                                    AttendeeImportance.ROUND.processAttendeeWebsocketMessage(attendeeSmc2);
                                    tempIds.add(attendeeSmc2);
                                }
                            }
                        }
                        reInterrupt(this.interval * 1000);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("ChairManSmc2PollingThread " + this.confId + " error " + this.chairmanUri);
        }finally {

            for (AttendeeSmc2 tempId : tempIds) {
                AttendeeImportance.COMMON.processAttendeeWebsocketMessage(tempId);
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc2ConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, tempId.getUpdateMap());
            }
            tempIds.clear();
        }


    }


    public synchronized void supend() {
        this.supended = true;
    }

    public synchronized void starton() {
        this.supended = false;
        this.notify();
    }

    public void stops() {
        logger.info("ChairManSmc2PollingThread " + this.confId + " stop... " + this.chairmanUri);
        this.stopState = true;
        this.supended=false;
        this.interrupt();

    }

    private  synchronized void reInterrupt(long timemillis) {
        try {
            long sleepSeconds = timemillis / 1000;
            long remainMillis = timemillis % 1000;
            if(timemillis> _WAITINGTIME){
                for (int i = 0; i < sleepSeconds; i++) {
                    if (isMasterLeft()) {
                        stops();
                    }
                    if(supended){
                        this.wait();
                    }
                    Thread.sleep(1000);
                }
                if (isMasterLeft()) {
                    stops();
                }
                if(supended){
                    this.wait();
                }

                Thread.sleep(remainMillis);
            }else {
                if (isMasterLeft()) {
                    stops();
                }
                if(supended){
                    this.wait();
                }
                Thread.sleep(timemillis);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    private boolean isMasterLeft() {
        if(smc2ConferenceContext.getMasterAttendee()==null){
            return true;
        }
        if(!smc2ConferenceContext.getMasterAttendee().isMeetingJoined()||!smc2ConferenceContext.getMasterAttendee().isOnline()){
            return true;
        }
        return false;
    }

    public boolean isCascadePolling(Smc2ConferenceContext conferenceContext, MultiPicPollRequest multiPicPollRequest) {
        List<MultiPicPollRequest.SubPicPollInfoListDTO> subPicPollInfoList = multiPicPollRequest.getSubPicPollInfoList();
        for (MultiPicPollRequest.SubPicPollInfoListDTO subPicPollInfoListDTO : subPicPollInfoList) {
            List<MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO> participantIds = subPicPollInfoListDTO.getParticipantIds();
            for (MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO participantId : participantIds) {
                AttendeeSmc2 attendeeBySmc3Id = conferenceContext.getAttendeeBySmc2Id(participantId.getParticipantId());
                if (attendeeBySmc3Id == null) {
                    return true;
                }
            }
        }
        return false;
    }

}
