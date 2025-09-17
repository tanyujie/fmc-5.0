package com.paradisecloud.fcm.huaweicloud.huaweicloud.event;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.AttendeeMeetingStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.AttendeeCountingStatistics;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.AttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.CropDirAttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContextCache;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudWebSocketMessagePusher;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.event.model.NetConditionNotifyDTO;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.event.model.NetConditionNotifyParticipant;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.event.model.SpeakerChangeNotify;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.ParticipantState;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.SmcParitipantsStateRep;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.message.ConfDynamicInfoNotifyMessage;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.message.ErrorCode;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.message.InviteResultNotifyMessage;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.message.ParticipantsNotifyMessage;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiMcuHwcloudHistoryConferenceService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.templateConference.updateprocess.AttendeeMessagePusher;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.templateConference.updateprocess.SelfCallAttendeeHwcloudProcessor;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.utils.AttendeeUtils;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.client.HwcloudWebSocketProcessor;
import com.sinhy.core.processormessage.ProcessorMessage;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author nj
 * @date 2023/7/10 9:34
 */
public class HwcloudMeetingMessage extends ProcessorMessage<JSONObject> {

    public static final String COHOST = "1";

    public HwcloudMeetingMessage(JSONObject jsonObject, String itemId) {
        super(jsonObject, itemId);
    }

    @Override
    protected void process0() {


    }


    private SmcParitipantsStateRep.ContentDTO updateContent(SmcParitipantsStateRep.ContentDTO contentDTO,ParticipantsNotifyMessage.DataDTO dataDTO) {

        SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam = contentDTO.getGeneralParam();
        ParticipantState participantState = contentDTO.getState();

        if (dataDTO.getPinfoMap() != null) {
            generalParam.setName(dataDTO.getPinfoMap().getName());

            if(dataDTO.getPinfoMap().getState()!=null){
                participantState.setOnline(Objects.equals("0", dataDTO.getPinfoMap().getState()));
            }

            if(dataDTO.getPinfoMap().getMute()!=null){
                participantState.setMute(Objects.equals("1", dataDTO.getPinfoMap().getMute()));
            }

            if(dataDTO.getPinfoMap().getVideo()!=null){
                participantState.setVideoMute(Objects.equals("1", dataDTO.getPinfoMap().getVideo()));
            }

            if(dataDTO.getPinfoMap().getBroadcast()!=null){
                participantState.setBroadcast(Objects.equals("1", dataDTO.getPinfoMap().getBroadcast()));
            }

            if(dataDTO.getPinfoMap().getRollcall()!=null){
                participantState.setRollcall(Objects.equals("1", dataDTO.getPinfoMap().getRollcall()));
            }

            if (dataDTO.getPinfoMap().getRole() != null) {

                Integer role = Integer.valueOf(dataDTO.getPinfoMap().getRole());
                //主持人
                if(role==1){
                    contentDTO.setUserRole(2);
                }else {
                    contentDTO.setUserRole(0);
                }
            }
            String isCohost = dataDTO.getPinfoMap().getIsCohost();
            if(Objects.equals(COHOST,isCohost)){
                contentDTO.setUserRole(2);
            }else {
                contentDTO.setUserRole(0);
            }

            if(dataDTO.getPinfoMap().getHand()!=null){
                participantState.setRaise_hands_state(Objects.equals("1", dataDTO.getPinfoMap().getHand()));
            }
            if(dataDTO.getPinfoMap().getShare()!=null){
                participantState.setScreen_shared_state(Objects.equals("1", dataDTO.getPinfoMap().getShare()));
            }
            if(dataDTO.getPinfoMap().getUserAgent()!=null){
                contentDTO.setUserAgent(dataDTO.getPinfoMap().getUserAgent());
            }

            contentDTO.setAccountId(dataDTO.getPinfoMap().getAccount());
            if (dataDTO.getPinfoMap().getClientLoginType() != null) {
                contentDTO.setCallType(dataDTO.getPinfoMap().getClientLoginType());
            }

        }

        return contentDTO;
    }

    private SmcParitipantsStateRep.ContentDTO initContent(ParticipantsNotifyMessage.DataDTO dataDTO) {
        SmcParitipantsStateRep.ContentDTO contentDTO = new SmcParitipantsStateRep.ContentDTO();
        SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam = new SmcParitipantsStateRep.ContentDTO.GeneralParamDTO();
        ParticipantState participantState = new ParticipantState();

        generalParam.setId(dataDTO.getPid());
        participantState.setParticipantId(dataDTO.getPid());
        generalParam.setId(dataDTO.getPid());

        if (dataDTO.getPinfoMap() != null) {
            generalParam.setName(dataDTO.getPinfoMap().getName());

            if(dataDTO.getPinfoMap().getState()!=null){
                participantState.setOnline(Objects.equals("0", dataDTO.getPinfoMap().getState()));
            }

            if(dataDTO.getPinfoMap().getMute()!=null){
                participantState.setMute(Objects.equals("1", dataDTO.getPinfoMap().getMute()));
            }

            if(dataDTO.getPinfoMap().getVideo()!=null){
                participantState.setVideoMute(Objects.equals("1", dataDTO.getPinfoMap().getVideo()));
            }

            if(dataDTO.getPinfoMap().getBroadcast()!=null){
                participantState.setBroadcast(Objects.equals("1", dataDTO.getPinfoMap().getBroadcast()));
            }

            if(dataDTO.getPinfoMap().getRollcall()!=null){
                participantState.setRollcall(Objects.equals("1", dataDTO.getPinfoMap().getRollcall()));
            }

            if (dataDTO.getPinfoMap().getRole() != null) {

                Integer role = Integer.valueOf(dataDTO.getPinfoMap().getRole());
                //主持人
                if(role==1){
                    contentDTO.setUserRole(2);
                }else {
                    contentDTO.setUserRole(0);
                }
            }
            String isCohost = dataDTO.getPinfoMap().getIsCohost();
            if(Objects.equals(COHOST,isCohost)){
                contentDTO.setUserRole(2);
            }else {
                contentDTO.setUserRole(0);
            }

            if(dataDTO.getPinfoMap().getHand()!=null){
                participantState.setRaise_hands_state(Objects.equals("1", dataDTO.getPinfoMap().getHand()));
            }
            if(dataDTO.getPinfoMap().getShare()!=null){
                participantState.setScreen_shared_state(Objects.equals("1", dataDTO.getPinfoMap().getShare()));
            }
            if(dataDTO.getPinfoMap().getUserAgent()!=null){
              contentDTO.setUserAgent(dataDTO.getPinfoMap().getUserAgent());
            }

            contentDTO.setAccountId(dataDTO.getPinfoMap().getAccount());

            if (dataDTO.getPinfoMap().getClientLoginType() != null) {
                contentDTO.setCallType(dataDTO.getPinfoMap().getClientLoginType());
            }

        }


        contentDTO.setState(participantState);
        contentDTO.setGeneralParam(generalParam);
        contentDTO.setTerminalOnline(true);


        return contentDTO;
    }

    private void processParticipants(HwcloudConferenceContext conferenceContext, SmcParitipantsStateRep.ContentDTO contentDTO, SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam, AttendeeHwcloud a) {
        conferenceContext.getParticipantAttendeeAllMap().put(generalParam.getId(), a);
        a.setConferenceNumber(conferenceContext.getConferenceNumber());
        a.setSmcParticipant(contentDTO);
        a.setParticipantUuid(generalParam.getId());
        AttendeeUtils.updateByParticipant(conferenceContext, contentDTO, a);
        processUpdateParticipant(conferenceContext, a, false);
    }

    private void processUpdateParticipant(HwcloudConferenceContext conferenceContext, AttendeeHwcloud attendeeHwcloud, boolean updateMediaInfo) {
        IBusiMcuHwcloudHistoryConferenceService historyConferenceService = BeanFactory.getBean(IBusiMcuHwcloudHistoryConferenceService.class);
        historyConferenceService.updateBusiHistoryParticipant(conferenceContext, attendeeHwcloud, updateMediaInfo);
    }
}
