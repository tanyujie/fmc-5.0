package com.paradisecloud.smc3.busi.operation;


import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.enumer.AttendeeImportance;
import com.paradisecloud.smc3.busi.utils.Smc3WebSocketMessagePusher;
import com.paradisecloud.smc3.model.ConstAPI;
import com.paradisecloud.smc3.model.ParticipantStatusDto;
import com.paradisecloud.smc3.model.TxtOperationTypeEnumDto;
import com.paradisecloud.smc3.model.TxtTypeEnum;
import com.paradisecloud.smc3.model.request.MultiPicInfoReq;
import com.paradisecloud.smc3.model.request.MultiPicInfoTalkReq;
import com.paradisecloud.smc3.model.request.TextTipsSetting;
import com.paradisecloud.smc3.model.response.SmcParitipantsStateRep;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 点聊
 * @author Administrator
 */
public class TalkPrivateAttendeeOperation extends AttendeeOperation {

    private MultiPicInfoTalkReq multiPicInfoTalkReq;
    private List<AttendeeSmc3> targets=new ArrayList<>();
    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-02-22 13:47
     */
    public TalkPrivateAttendeeOperation(Smc3ConferenceContext conferenceContext) {
        super(conferenceContext);
    }

    public TalkPrivateAttendeeOperation(Smc3ConferenceContext conferenceContext, MultiPicInfoTalkReq multiPicInfoTalkReq) {
        super(conferenceContext);

        this.multiPicInfoTalkReq = multiPicInfoTalkReq;
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
        AttendeeSmc3 masterAttendee = conferenceContext.getMasterAttendee();
        if(masterAttendee==null){
            return;
        }
        String conferenceId = conferenceContext.getSmc3conferenceId();
        Smc3Bridge smc3Bridge = conferenceContext.getSmc3Bridge();
        if(multiPicInfoTalkReq==null){
            return;
        }

        List<String> participantIds = multiPicInfoTalkReq.getParticipantIds();
        MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO = multiPicInfoTalkReq.getMultiPicInfo();
        for (String participantId : participantIds) {

            if(Strings.isNotBlank(participantId)){
                if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
                    smc3Bridge.getSmcConferencesInvoker().conferencesControlChooseCascade(conferenceId, participantId, multiPicInfoDTO, smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }else {
                    smc3Bridge.getSmcConferencesInvoker().conferencesControlChoose(conferenceId, participantId, multiPicInfoDTO, smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }

                List<ParticipantStatusDto> participantStatusList = new ArrayList<>();
                ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
                participantStatusDto.setId(participantId);
                participantStatusDto.setIsMute(false);
                participantStatusList.add(participantStatusDto);
                //开音
                if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
                    smc3Bridge.getSmcParticipantsInvoker().PATCHParticipantsOnlyCascade(conferenceId,participantStatusList,smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }else {
                    smc3Bridge.getSmcParticipantsInvoker().PATCHParticipantsOnly(conferenceId,participantStatusList,smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }
            }


        }

        List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicListChoose = multiPicInfoDTO.getSubPicList();
        if (!CollectionUtils.isEmpty(subPicListChoose)) {
            for (MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicListChoose) {
                String chooseId = subPicListDTO.getParticipantId();
                if(Strings.isNotBlank(chooseId)){
                    AttendeeImportance.CHOOSE_SEE.processAttendeeWebsocketMessage(conferenceContext.getAttendeeBySmc3Id(chooseId));
                }
            }
        }

        List<ParticipantStatusDto> participantStatusList=new ArrayList<>();
        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(targets)){
            for (AttendeeSmc3 attendeeSmc3 : targets) {
                if(!participantIds.contains(attendeeSmc3.getParticipantUuid())){
                    SmcParitipantsStateRep.ContentDTO contentDTO = attendeeSmc3.getSmcParticipant();

                    ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
                    participantStatusList.add(participantStatusDto);
                    String id = contentDTO.getGeneralParam().getId();
                    participantStatusDto.setId(id);
                    participantStatusDto.setIsQuiet(true);
                    participantStatusDto.setIsMute(true);
                    MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTOOne=new  MultiPicInfoReq.MultiPicInfoDTO();
                    multiPicInfoDTOOne.setPicNum(1);
                    multiPicInfoDTOOne.setMode(1);
                    List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicList=new ArrayList<>();
                    MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO = new MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO();
                    subPicListDTO.setParticipantId(id);
                    subPicListDTO.setStreamNumber(0);
                    subPicList.add(subPicListDTO);
                    multiPicInfoDTOOne.setSubPicList(subPicList);

                    conferencesControlChoose(conferenceId, id, multiPicInfoDTOOne,smc3Bridge);
                    //关闭声音 麦克风
                    //发送字幕
                    if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
                        smc3Bridge.getSmcParticipantsInvoker().textTipsSettingCascade(conferenceId, id, smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                    }else {
                        smc3Bridge.getSmcParticipantsInvoker().textTipsSetting(conferenceId, id, smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                    }
                }
            }
        }
        if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
            smc3Bridge.getSmcParticipantsInvoker().PATCHParticipantsOnlyCascade(conferenceId,participantStatusList, smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }else {
            smc3Bridge.getSmcParticipantsInvoker().PATCHParticipantsOnly(conferenceId,participantStatusList, smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }
        conferenceContext.setMultiPicInfoTalkReq(multiPicInfoTalkReq);
        conferenceContext.setSecretTalk(true);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("secretTalk",true);
        jsonObject.put("multiPicInfoTalkReq",multiPicInfoTalkReq);
        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);

    }

    private void initTargetAttendees() {
        for (AttendeeSmc3 attendee : new ArrayList<>(conferenceContext.getAttendees())) {
            if (attendee != null) {
                if (attendee.isMeetingJoined()){
                    this.targets.add(attendee);
                }
            }
        }

        for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
            List<AttendeeSmc3> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
            if (attendees != null) {
                for (AttendeeSmc3 attendee : attendees) {
                    if (attendee.isMeetingJoined()){
                        targets.add(attendee);
                    }
                }
            }
        }

        for (AttendeeSmc3 attendee : conferenceContext.getMasterAttendees()) {
            if (attendee != null) {
                if (attendee.isMeetingJoined()){
                    targets.add(attendee);
                }
            }
        }
    }

    @Override
    public void cancel() {
        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
        String conferenceId = conferenceContext.getSmc3conferenceId();


        TextTipsSetting textTipsSetting = new TextTipsSetting();
        textTipsSetting.setType(TxtTypeEnum.CAPTION.name());
        textTipsSetting.setOpType(TxtOperationTypeEnumDto.CANCEL.name());
        textTipsSetting.setDisplayType(4);
        textTipsSetting.setDisPosition(2);
        textTipsSetting.setContent("会议主席正在点调对话中,请耐心等候......");
        textTipsSetting.setConferenceId(conferenceId);
        if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
            bridge.getSmcConferencesInvoker().textTipsSettingCascade(conferenceId, textTipsSetting, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }else {
            bridge.getSmcConferencesInvoker().textTipsSetting(conferenceId, textTipsSetting, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

        }
        JSONObject quietJS=new JSONObject();
        quietJS.put("isQuiet",false);
        if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
            bridge.getSmcConferencesInvoker().conferencesShareControlCascade(conferenceId, quietJS.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }else {
            bridge.getSmcConferencesInvoker().conferencesShareControl(conferenceId, quietJS.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }
        MultiPicInfoTalkReq multiPicInfoTalkReq = conferenceContext.getMultiPicInfoTalkReq();
        if(multiPicInfoTalkReq!=null){
            List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicListChoose = conferenceContext.getMultiPicInfoTalkReq().getMultiPicInfo().getSubPicList();
            if (!CollectionUtils.isEmpty(subPicListChoose)) {
                for (MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicListChoose) {
                    String chooseId = subPicListDTO.getParticipantId();
                    AttendeeSmc3 attendeeBySmc3Id = conferenceContext.getAttendeeBySmc3Id(chooseId);
                    AttendeeImportance.COMMON.processAttendeeWebsocketMessage(attendeeBySmc3Id);
                    Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeBySmc3Id.getUpdateMap());

                }
            }
        }


        conferenceContext.setSecretTalk(false);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("secretTalk",false);
        jsonObject.put("multiPicInfoTalkReq",null);
        conferenceContext.setMultiPicInfoTalkReq(null);
        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);

    }



    public void conferencesControlChoose(String conferenceId, String participantId, MultiPicInfoReq.MultiPicInfoDTO multiPicInfoD, Smc3Bridge bridge) {

        bridge.getSmcConferencesInvoker().conferencesControlChoose(conferenceId, participantId, multiPicInfoD, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

}
