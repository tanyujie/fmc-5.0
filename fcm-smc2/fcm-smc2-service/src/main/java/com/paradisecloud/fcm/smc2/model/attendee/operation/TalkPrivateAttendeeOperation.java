package com.paradisecloud.fcm.smc2.model.attendee.operation;


import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
import com.paradisecloud.fcm.smc2.model.AttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.smc2.model.layout.ContinuousPresenceModeEnum;
import com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq;
import com.paradisecloud.fcm.smc2.model.request.MultiPicInfoTalkReq;
import com.suntek.smc.esdk.ServiceFactoryEx;
import com.suntek.smc.esdk.pojo.local.WSCtrlSiteCommParamEx;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 点聊
 *
 * @author Administrator
 */
public class TalkPrivateAttendeeOperation extends AttendeeOperation {

    private final List<AttendeeSmc2> targets = new ArrayList<>();
    private final List<String> urls = new ArrayList<>();
    private MultiPicInfoTalkReq multiPicInfoTalkReq;
    private volatile Boolean isTalking = false;
    private Thread currentThread;

    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-02-22 13:47
     */
    public TalkPrivateAttendeeOperation(Smc2ConferenceContext conferenceContext) {
        super(conferenceContext);
    }

    public TalkPrivateAttendeeOperation(Smc2ConferenceContext conferenceContext, MultiPicInfoTalkReq multiPicInfoTalkReq) {
        super(conferenceContext);

        this.multiPicInfoTalkReq = multiPicInfoTalkReq;
    }

    public Boolean getTalking() {
        return isTalking;
    }

    /**
     * 操作方法
     *
     * @author lilinhai
     * @since 2021-02-20 16:39  void
     */
    @Override
    public synchronized void operate() {
        initTargetAttendees();
        operateScreen();
    }


    private void operateScreen() {
        isTalking = true;
        AttendeeSmc2 masterAttendee = conferenceContext.getMasterAttendee();
        if (masterAttendee == null) {
            return;
        }
        if (multiPicInfoTalkReq == null) {
            return;
        }

        List<String> participantIds = multiPicInfoTalkReq.getParticipantIds();
        MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO = multiPicInfoTalkReq.getMultiPicInfo();
        String confId = conferenceContext.getSmc2conferenceId();
        List<String> list = new ArrayList<>();
        //取消广播
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        conferenceServiceEx.setBroadcastContinuousPresenceEx(confId, 1);
        currentThread = new Thread(() -> {
            while (isTalking) {
                try {
                    initTargetAttendees();
                    for (String participantId : participantIds) {
                        if (!isTalking) {
                            break;
                        }
                        AttendeeSmc2 attendeeSmc2 = conferenceContext.getAttendeeById(participantId);
                        String uri = attendeeSmc2.getRemoteParty();
                        list.add(uri);
                        //锁定视频源
                        List<WSCtrlSiteCommParamEx> wsCtrlSiteCommParams = new ArrayList<>();
                        WSCtrlSiteCommParamEx item1 = new WSCtrlSiteCommParamEx();
                        //锁定
                        item1.setOperaTypeParam(1);
                        item1.setSiteUri(uri);
                        wsCtrlSiteCommParams.add(item1);
                        conferenceServiceEx.setVSAttrCtrlEx(conferenceContext.getSmc2conferenceId(), wsCtrlSiteCommParams);


                        String target = "(%CP)";
                        List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicList = multiPicInfoDTO.getSubPicList();
                        Integer picNum = multiPicInfoDTO.getPicNum();
                        Integer mode = multiPicInfoDTO.getMode();
                        int presenceMode = ContinuousPresenceModeEnum.getModelValue(picNum, mode);
                        if (presenceMode == -1) {
                            throw new CustomException("多画面设置失败:不支持该" + picNum + "画面");
                        }
                        List<String> subPics = new ArrayList<>();
                        if (!CollectionUtils.isEmpty(subPicList)) {
                            for (MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicList) {
                                String m_participantId = subPicListDTO.getParticipantId();
                                if (Strings.isNotBlank(m_participantId)) {
                                    AttendeeSmc2 attendeeById = conferenceContext.getAttendeeById(m_participantId);
                                    if (attendeeById != null && attendeeById.isMeetingJoined()) {
                                        subPics.add(attendeeById.getRemoteParty());
                                    }
                                }
                            }

                        }


                        Integer integer = conferenceServiceEx.setContinuousPresenceEx(confId, target, presenceMode, subPics);
                        Integer integer1 = conferenceServiceEx.setVideoSourceEx(confId, uri, "", 0);
                        //解锁
                        if (!("会议监控smc2").equals(attendeeSmc2.getName())) {
                            item1.setOperaTypeParam(0);
                            item1.setSiteUri(uri);
                            wsCtrlSiteCommParams.add(item1);
                            conferenceServiceEx.setVSAttrCtrlEx(conferenceContext.getSmc2conferenceId(), wsCtrlSiteCommParams);
                        }
                    }

                    //开音
                    conferenceServiceEx.setSitesMuteEx(conferenceContext.getSmc2conferenceId(), list, 0);


                    List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicListChoose = multiPicInfoDTO.getSubPicList();
                    if (!CollectionUtils.isEmpty(subPicListChoose)) {
                        for (MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicListChoose) {
                            String chooseId = subPicListDTO.getParticipantId();
                            AttendeeImportance.CHOOSE_SEE.processAttendeeWebsocketMessage(conferenceContext.getAttendeeById(chooseId));
                        }
                    }
                    urls.clear();
                    if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(targets)) {
                        for (AttendeeSmc2 attendeeSmc2 : targets) {
                            //关闭声音 麦克风 观看自己
                            if (!participantIds.contains(attendeeSmc2.getId())) {
                                urls.add(attendeeSmc2.getRemoteParty());
                                Integer integer = conferenceServiceEx.setVideoSourceEx(confId, attendeeSmc2.getRemoteParty(), attendeeSmc2.getRemoteParty(), 0);
                            }


                        }

                        muteAndQuiet(conferenceContext.getSmc2conferenceId(), urls, 1);
                    }


                } catch (CustomException e) {
                    e.printStackTrace();
                    break;
                }finally {
                    Threads.sleep(200);
                }
            }
        });
        currentThread.start();

        conferenceContext.setMultiPicInfoTalkReq(multiPicInfoTalkReq);
        conferenceContext.setSecretTalk(true);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("secretTalk", true);
        jsonObject.put("multiPicInfoTalkReq", multiPicInfoTalkReq);
        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
    }

    private void initTargetAttendees() {

        for (AttendeeSmc2 attendee : new ArrayList<>(conferenceContext.getAttendees())) {
            if (attendee != null) {
                if (attendee.isMeetingJoined()&&!Objects.equals(attendee.getName(), "会议监控") && !Objects.equals("SMC-MONITOR", attendee.getName())) {
                    this.targets.add(attendee);
                }
            }
        }

        for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
            List<AttendeeSmc2> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
            if (attendees != null) {
                for (AttendeeSmc2 attendee : attendees) {
                    if (attendee.isMeetingJoined()&&!Objects.equals(attendee.getName(), "会议监控") && !Objects.equals("SMC-MONITOR", attendee.getName())) {
                        targets.add(attendee);
                    }
                }
            }
        }

        for (AttendeeSmc2 attendee : conferenceContext.getMasterAttendees()) {
            if (attendee != null) {
                if (attendee.isMeetingJoined()&&!Objects.equals(attendee.getName(), "会议监控") && !Objects.equals("SMC-MONITOR", attendee.getName())) {
                    targets.add(attendee);
                }
            }
        }
    }

    @Override
    public void cancel() {
        this.isTalking = false;
        if(currentThread!=null){
            currentThread.interrupt();
        }
        currentThread=null;
        Threads.sleep(1000);
        JSONObject quietJS = new JSONObject();
        quietJS.put("isQuiet", false);

        List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicListChoose = multiPicInfoTalkReq.getMultiPicInfo().getSubPicList();
        if (!CollectionUtils.isEmpty(subPicListChoose)) {
            for (MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicListChoose) {
                String chooseId = subPicListDTO.getParticipantId();
                if(Strings.isNotBlank(chooseId)){
                    AttendeeSmc2 attendeeBySmc3Id = conferenceContext.getAttendeeById(chooseId);
                    AttendeeImportance.COMMON.processAttendeeWebsocketMessage(attendeeBySmc3Id);
                    Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeBySmc3Id.getUpdateMap());
                }
            }
        }

        conferenceContext.setSecretTalk(false);
        conferenceContext.setMultiPicInfoTalkReq(null);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("secretTalk", false);
        jsonObject.put("multiPicInfoTalkReq", null);
        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);

    }


    private void muteAndQuiet(String confId, List<String> siteUrls, Integer isMute) {
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        Integer resultCode = conferenceServiceEx.setSitesMuteEx(confId, siteUrls, isMute);
        conferenceServiceEx.setSitesQuietEx(confId, siteUrls, isMute);
        if (resultCode != 0) {
            logger.error("开/闭麦操作：" + resultCode + "操作：" + isMute.intValue());
        }
    }

}
