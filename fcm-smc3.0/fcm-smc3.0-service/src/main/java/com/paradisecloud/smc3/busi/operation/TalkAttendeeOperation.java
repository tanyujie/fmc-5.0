/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : TalkAttendeeOperation.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.operation
 * @author sinhy
 * @since 2021-12-01 10:16
 * @version  V1.0
 */
package com.paradisecloud.smc3.busi.operation;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.AttendeeTalkStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.smc3.busi.DefaultAttendeeOperation;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.utils.Smc3WebSocketMessagePusher;
import com.paradisecloud.smc3.invoker.ConferenceState;
import com.paradisecloud.smc3.model.ConstAPI;
import com.paradisecloud.smc3.model.ParticipantStatusDto;
import com.paradisecloud.smc3.model.request.MultiPicInfoReq;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 参会者对话操作
 *
 */
public class TalkAttendeeOperation extends AttendeeOperation
{


    private AttendeeSmc3 target;
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-12-01 10:16
     */
    private static final long serialVersionUID = 1L;

    /**
     * <pre>构造方法</pre>
     * @param conferenceContext
     */
    public TalkAttendeeOperation(Smc3ConferenceContext conferenceContext, AttendeeSmc3 attendee)
    {
        super(conferenceContext);
        this.target=attendee;

    }

    @Override
    public void operate()
    {
        AttendeeSmc3 masterAttendee = conferenceContext.getMasterAttendee();
        if(masterAttendee==null){
            logger.error("主会场不存在");
            return;
        }

        if (target==null||target == conferenceContext.getMasterAttendee())
        {
            logger.error("主会场不能和自己对话");
            return;
        }

        Smc3Bridge smc3Bridge = conferenceContext.getSmc3Bridge();

        if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
            smc3Bridge.getSmcConferencesInvoker().conferencesControlChooseCascade(conferenceContext.getSmc3conferenceId(), masterAttendee.getParticipantUuid(), target.getParticipantUuid(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            smc3Bridge.getSmcConferencesInvoker().conferencesControlChooseCascade(conferenceContext.getSmc3conferenceId(), target.getParticipantUuid(),masterAttendee.getParticipantUuid(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }else {
            smc3Bridge.getSmcConferencesInvoker().conferencesControlChoose(conferenceContext.getSmc3conferenceId(), masterAttendee.getParticipantUuid(), target.getParticipantUuid(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            smc3Bridge.getSmcConferencesInvoker().conferencesControlChoose(conferenceContext.getSmc3conferenceId(), target.getParticipantUuid(),masterAttendee.getParticipantUuid(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }


        target.setTalkStatus(AttendeeTalkStatus.YES.getValue());
        masterAttendee.setTalkStatus(AttendeeTalkStatus.YES.getValue());
        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE,masterAttendee.getUpdateMap());
        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE,target.getUpdateMap());

        List<String> ids = new ArrayList<>();
        ids.add(masterAttendee.getParticipantUuid());
        ids.add(target.getParticipantUuid());
        openMixing(ids);


        List<ParticipantStatusDto> participantStatusList=new ArrayList<>();
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(conferenceContext.getAttendees())) {
            for (AttendeeSmc3 attendee : conferenceContext.getAttendees()) {
                otherSiteMute(attendee,participantStatusList);
            }
        }
        for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
            List<AttendeeSmc3> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
            if (attendees != null) {
                for (AttendeeSmc3 attendee : attendees) {
                    otherSiteMute(attendee,participantStatusList);
                }
            }
        }

        for (AttendeeSmc3 attendee : conferenceContext.getMasterAttendees()) {
            otherSiteMute(attendee,participantStatusList);
        }

        if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
            smc3Bridge.getSmcParticipantsInvoker().PATCHParticipantsOnlyCascade(conferenceContext.getSmc3conferenceId(),participantStatusList, smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }else {
            smc3Bridge.getSmcParticipantsInvoker().PATCHParticipantsOnly(conferenceContext.getSmc3conferenceId(),participantStatusList, smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }




        MultiPicInfoReq.MultiPicInfoDTO multiPicInfoD=new  MultiPicInfoReq.MultiPicInfoDTO();
        multiPicInfoD.setPicNum(2);
        multiPicInfoD.setMode(1);
        List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicList=new ArrayList<>();
        MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO_master = new MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO();
        subPicListDTO_master.setParticipantId(masterAttendee.getParticipantUuid());
        subPicListDTO_master.setStreamNumber(0);
        subPicList.add(subPicListDTO_master);
        MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO_target = new MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO();
        subPicListDTO_target.setParticipantId(target.getParticipantUuid());
        subPicListDTO_target.setStreamNumber(0);
        subPicList.add(subPicListDTO_target);
        multiPicInfoD.setSubPicList(subPicList);


        // 取消除当前对话者外的其他已开麦参会者的混音状态
        for (ParticipantStatusDto participantStatusDto : participantStatusList) {


            if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
                smc3Bridge.getSmcConferencesInvoker().conferencesControlChooseCascade(conferenceContext.getSmc3conferenceId(),participantStatusDto.getId(),multiPicInfoD,smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }else {
                smc3Bridge.getSmcConferencesInvoker().conferencesControlChoose(conferenceContext.getSmc3conferenceId(),participantStatusDto.getId(),multiPicInfoD,smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }
        }
    }

    private void otherSiteMute(AttendeeSmc3 attendee,List<ParticipantStatusDto> participantStatusList) {

        if(Objects.equals(attendee.getParticipantUuid(), this.target.getParticipantUuid())){
            return;
        }
        ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
        participantStatusDto.setIsMute(true);
        participantStatusDto.setId(attendee.getParticipantUuid());
        participantStatusList.add(participantStatusDto);
    }


    public void openMixing(List<String> ids){
        if(CollectionUtils.isEmpty(ids)){
            return;
        }
        List<ParticipantStatusDto> participantStatusList=new ArrayList<>();
        for (String id : ids) {
            ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
            participantStatusDto.setIsMute(false);
            participantStatusDto.setId(id);
            participantStatusList.add(participantStatusDto);
        }
        String smc3conferenceId = conferenceContext.getSmc3conferenceId();
        Smc3Bridge smc3Bridge = conferenceContext.getSmc3Bridge();

        if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
            smc3Bridge.getSmcParticipantsInvoker().PATCHParticipantsOnlyCascade(smc3conferenceId,participantStatusList, smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }else {
            smc3Bridge.getSmcParticipantsInvoker().PATCHParticipantsOnly(smc3conferenceId,participantStatusList, smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }
    }

    public void cancelMixing(AttendeeSmc3 attendeeSmc3){
        List<ParticipantStatusDto> participantStatusList=new ArrayList<>();
        ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
        participantStatusDto.setIsMute(true);
        participantStatusDto.setId(attendeeSmc3.getParticipantUuid());
        participantStatusList.add(participantStatusDto);
        String smc3conferenceId = conferenceContext.getSmc3conferenceId();
        Smc3Bridge smc3Bridge = conferenceContext.getSmc3Bridge();
        if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
            smc3Bridge.getSmcParticipantsInvoker().PATCHParticipantsOnlyCascade(smc3conferenceId,participantStatusList, smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }else {
            smc3Bridge.getSmcParticipantsInvoker().PATCHParticipantsOnly(smc3conferenceId,participantStatusList, smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }

    }

    @Override
    public void cancel()
    {
        cancelMixing(target);
        target.setTalkStatus(AttendeeTalkStatus.NO.getValue());
        conferenceContext.getMasterAttendee().setTalkStatus(AttendeeTalkStatus.NO.getValue());
        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, conferenceContext.getMasterAttendee().getUpdateMap());
        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE,target.getUpdateMap());
    }



    public void defaultAttendeeOperation(){

        if(conferenceContext.getMasterAttendee()!=null){
            conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
            ChangeMasterAttendeeOperation changeMasterAttendeeOperation = new ChangeMasterAttendeeOperation(conferenceContext, conferenceContext.getMasterAttendee());
            conferenceContext.setAttendeeOperation(changeMasterAttendeeOperation);
            changeMasterAttendeeOperation.operate();
        }else {
            ConferenceState.StateDTO.MultiPicInfoDTO multiPicInfo =conferenceContext.getMultiPicInfo();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("multiPicInfo",multiPicInfo);
            jsonObject.put("conferenceId",conferenceContext.getSmc3conferenceId());
            jsonObject.put("broadcast",false);
            conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
            DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext,jsonObject);
            conferenceContext.setAttendeeOperation(defaultAttendeeOperation);
            defaultAttendeeOperation.operate();
        }
    }


}
