/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DefaultChooseToSeeAttendeeOperation.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.operation
 * @author lilinhai
 * @since 2021-02-22 18:16
 * @version  V1.0
 */
package com.paradisecloud.fcm.smc2.model.attendee.operation;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
import com.paradisecloud.fcm.smc2.model.DefaultViewOperation;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.smc2.model.layout.CellScreen;
import com.paradisecloud.fcm.smc2.model.layout.ContinuousPresenceModeEnum;
import com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;
import io.swagger.v3.core.util.Json;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * <pre>会议室默认视图</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-22 18:16
 */
public class DefaultAttendeeOperation extends DefaultViewOperation {

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     *
     * @since 2021-02-22 18:16
     */
    private static final long serialVersionUID = 1L;
    private volatile List<JSONObject> defaultViewDepts = new ArrayList<>();

    private volatile Boolean enable=Boolean.FALSE;

    private volatile List<AttendeeSmc2> targetAttendees = new ArrayList<>();
    private volatile Set<String> checkedAttendeeIdSet = new HashSet<>();
    private volatile JSONObject jsonObject;
    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-04-12 17:48
     */
    public DefaultAttendeeOperation(Smc2ConferenceContext conferenceContext) {
        super(conferenceContext);
    }
    public DefaultAttendeeOperation(Smc2ConferenceContext conferenceContext,Boolean enable) {
        super(conferenceContext);
        this.enable=enable;
    }

    public DefaultAttendeeOperation(Smc2ConferenceContext conferenceContext,JSONObject jsonObject) {
        super(conferenceContext);
        this.jsonObject=jsonObject;
    }
    public void initSplitScreen() {

    }

    @Override
    public void operate() {
        Boolean broadFlag=false;
        initTargetAttendees();
        MultiPicInfoReq parseObject= JSONObject.parseObject(JSONObject.toJSONString(jsonObject), MultiPicInfoReq.class);

        if(parseObject!=null&&parseObject.getMultiPicInfo()!=null){

            broadFlag=parseObject.getBroadcast();

            if(parseObject.getMultiPicInfo()!=null){
                createMulitiPic(parseObject);
            }
        }else  if(parseObject!=null&&parseObject.getMultiPicInfo()==null&&parseObject.getBroadcast()!=null){
            broadFlag=parseObject.getBroadcast();
        }

        Integer isBroadcast = broadFlag == true ? 0 : 1;
        String confId = conferenceContext.getSmc2conferenceId();
        ConferenceServiceEx conferenceServiceEx = conferenceContext.getSmc2Bridge().getConferenceServiceEx();
        Integer resultCode = conferenceServiceEx.setBroadcastContinuousPresenceEx(confId, isBroadcast);
        if (resultCode != 0) {
            throw new CustomException("多画面广播失败");
        }
        if(broadFlag){
            conferenceContext.setMultiPicBroadcastStatus(true);
            MultiPicInfoReq.MultiPicInfoDTO multiPicInfo = conferenceContext.getMultiPicInfo();
            List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicList = multiPicInfo.getSubPicList();
            if (!CollectionUtils.isEmpty(subPicList)) {
                for (MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicList) {
                    String participantId = subPicListDTO.getParticipantId();
                    if(Strings.isNotBlank(participantId)){
                        AttendeeSmc2 attendeeBySmc2Id = conferenceContext.getAttendeeBySmc2Id(participantId);
                        if(attendeeBySmc2Id!=null){
                            AttendeeImportance.BROADCAST.processAttendeeWebsocketMessage(attendeeBySmc2Id);
                        }
                    }

                }
            }
        }else {
            conferenceContext.setMultiPicBroadcastStatus(false);
            //广播主席
            AttendeeSmc2 masterAttendee = conferenceContext.getMasterAttendee();
            if(masterAttendee!=null&&masterAttendee.isMeetingJoined()){
                SmcParitipantsStateRep.ContentDTO participant = masterAttendee.getSmcParticipant();
                if (participant != null) {
                    conferenceServiceEx.setBroadcastSiteEx(confId, participant.getGeneralParam().getUri(), 0);
                }
            }
            MultiPicInfoReq.MultiPicInfoDTO multiPicInfo = conferenceContext.getMultiPicInfo();
            if(multiPicInfo!=null){
                List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicList = multiPicInfo.getSubPicList();
                if (!CollectionUtils.isEmpty(subPicList)) {
                    for (MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicList) {
                        String participantId = subPicListDTO.getParticipantId();
                        if(Strings.isNotBlank(participantId)){
                            AttendeeSmc2 attendeeBySmc2Id = conferenceContext.getAttendeeBySmc2Id(participantId);
                            if(attendeeBySmc2Id!=null){
                                attendeeBySmc2Id.setBroadcastStatus(2);
                                Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext,WebsocketMessageType.ATTENDEE_UPDATE,attendeeBySmc2Id.getUpdateMap());
                            }
                        }

                    }
                }
            }
            List<AttendeeSmc2> targetAttendees = this.targetAttendees;
            for (AttendeeSmc2 targetAttendee : targetAttendees) {
                AttendeeImportance.COMMON.processAttendeeWebsocketMessage(targetAttendee);
                if(targetAttendee.getUpdateMap().size()>1){
                    Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext,WebsocketMessageType.ATTENDEE_UPDATE,targetAttendee);
                }
            }

        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("multiPicBroadcastStatus",conferenceContext.getMultiPicBroadcastStatus());
        jsonObject.put("multiPicInfo",conferenceContext.getMultiPicInfo());
        Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);


    }



    @Override
    public void cancel() {
        if (conferenceContext != null) {
            Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.DEFAULT_VIEW_RUNNING, false);
            String confId = conferenceContext.getSmc2conferenceId();
            ConferenceServiceEx conferenceServiceEx = conferenceContext.getSmc2Bridge().getConferenceServiceEx();
            conferenceServiceEx.setBroadcastContinuousPresenceEx(confId, 1);

            for (AttendeeSmc2 targetAttendee : targetAttendees) {
                AttendeeImportance.COMMON.processAttendeeWebsocketMessage(targetAttendee);
                Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext,WebsocketMessageType.ATTENDEE_UPDATE,targetAttendee.getUpdateMap());
            }

            StringBuilder messageTip1 = new StringBuilder();
            messageTip1.append("默认视图已结束");
            Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip1);
            logger.info("默认视图已结束");
        }

    }



    public List<JSONObject> getDefaultViewDepts() {
        return defaultViewDepts;
    }


    public void addDefaultViewDept(JSONObject dept) {
        this.defaultViewDepts.add(dept);
    }


    @Override
    public boolean contains(AttendeeSmc2 attendee) {
        for (CellScreen cellScreen : splitScreen.getCellScreens()) {
            if (cellScreen.getLastOperationAttendee() == attendee) {
                return true;
            }
        }
        return false;
    }

    private void initTargetAttendees() {
        targetAttendees.clear();
        if (!ObjectUtils.isEmpty(attendees)) {
            targetAttendees.addAll(attendees);
        }

        if (conferenceContext.getMasterAttendee() != null) {
            if (!checkedAttendeeIdSet.contains(conferenceContext.getMasterAttendee().getId())) {
                targetAttendees.add(conferenceContext.getMasterAttendee());
            }

            if (conferenceContext.getMasterAttendee().getDeptId() != conferenceContext.getDeptId().longValue() && conferenceContext.getMasterAttendeeIdSet().contains(conferenceContext.getMasterAttendee().getId())) {
                List<AttendeeSmc2> as = conferenceContext.getCascadeAttendeesMap().get(conferenceContext.getMasterAttendee().getDeptId());
                if (as != null) {
                    for (AttendeeSmc2 attendee : new ArrayList<>(as)) {
                        if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                            targetAttendees.add(attendee);
                        }
                    }
                }
            }
        }

        for (AttendeeSmc2 attendee : new ArrayList<>(conferenceContext.getAttendees())) {
            if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                targetAttendees.add(attendee);
            }
        }

        for (AttendeeSmc2 attendee : conferenceContext.getMasterAttendees()) {
            if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                targetAttendees.add(attendee);
            }
            List<AttendeeSmc2> as = conferenceContext.getCascadeAttendeesMap().get(attendee.getDeptId());
            if (as != null) {
                for (AttendeeSmc2 a : new ArrayList<>(as)) {
                    if (!checkedAttendeeIdSet.contains(a.getId())) {
                        targetAttendees.add(a);
                    }
                }
            }
        }



        Set<String> idSet = new HashSet<>();

        if(!CollectionUtils.isEmpty(targetAttendees)){
            List<AttendeeSmc2> arrayList = new ArrayList<>();
            for (AttendeeSmc2 attendee : targetAttendees) {
                if (idSet.add(attendee.getId())) {
                    arrayList.add(attendee);
                }
            }
            targetAttendees=arrayList;
        }

    }


    public  void createMulitiPic(MultiPicInfoReq multiPicInfoReq) {

        String confId = conferenceContext.getSmc2conferenceId();
        MultiPicInfoReq.MultiPicInfoDTO multiPicInfo = multiPicInfoReq.getMultiPicInfo();
        String target = "(%CP)";
        Integer picNum = multiPicInfo.getPicNum();
        Integer mode = multiPicInfo.getMode();
        int presenceMode = ContinuousPresenceModeEnum.getModelValue(picNum, mode);
        if (presenceMode == -1) {
            throw new CustomException("多画面设置失败:不支持该" + picNum + "画面");
        }
        List<String> subPics = new ArrayList<>();
        List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicList = multiPicInfoReq.getMultiPicInfo().getSubPicList();
        if (!CollectionUtils.isEmpty(subPicList)) {
            for (MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicList) {
                String participantId = subPicListDTO.getParticipantId();
                if(Strings.isNotBlank(participantId)){
                    AttendeeSmc2 contextAttendeeBySmc3Id = conferenceContext.getAttendeeById(participantId);
                    SmcParitipantsStateRep.ContentDTO participant=contextAttendeeBySmc3Id.getSmcParticipant();
                    if (participant != null) {
                        subPics.add(participant.getGeneralParam().getUri());
                    }else {
                        subPics.add("");
                    }
                }else {
                    subPics.add("");
                }

            }
        }
        ConferenceServiceEx conferenceServiceEx = conferenceContext.getSmc2Bridge().getConferenceServiceEx();
        Integer resultCode = conferenceServiceEx.setContinuousPresenceEx(confId, target, presenceMode, subPics);
        if (resultCode != 0) {
            throw new CustomException("多画面设置失败:" + resultCode);
        } else {
            conferenceContext.setMultiPicInfoReq(multiPicInfoReq);
            conferenceContext.setMultiPicInfo(multiPicInfo);
        }
    }

}
