package com.paradisecloud.fcm.tencent.event;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.tencent.busi.AttendeeCountingStatistics;
import com.paradisecloud.fcm.tencent.busi.attende.AttendeeTencent;
import com.paradisecloud.fcm.tencent.busi.attende.McuAttendeeTencent;
import com.paradisecloud.fcm.tencent.busi.attende.RemotePartyAttendeesMap;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContext;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContextCache;
import com.paradisecloud.fcm.tencent.cache.TencentWebSocketMessagePusher;
import com.paradisecloud.fcm.tencent.model.ParticipantState;
import com.paradisecloud.fcm.tencent.model.SmcParitipantsStateRep;
import com.paradisecloud.fcm.tencent.model.TencentMeetingStateEnum;
import com.paradisecloud.fcm.tencent.model.event.ParticipantJoined;
import com.paradisecloud.fcm.tencent.model.reponse.RealTimeParticipantsResponse;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiMcuTencentHistoryConferenceService;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiTencentConferenceService;
import com.paradisecloud.fcm.tencent.templateConference.updateprocess.SelfCallAttendeeTencentProcessor;
import com.paradisecloud.fcm.tencent.utils.AttendeeUtils;
import com.sinhy.core.processormessage.ProcessorMessage;
import com.sinhy.spring.BeanFactory;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2023/7/10 9:34
 */
public class TencentMeetingMessage extends ProcessorMessage<JSONObject> {

    public TencentMeetingMessage(JSONObject jsonObject, String itemId) {
        super(jsonObject, itemId);
    }

    public static String decode2(String base64Str) {
        // 解码
        byte[] base64Data = Base64.getDecoder().decode(base64Str);
        // byte[]-->String（解码后的字符串）
        String str = new String(base64Data, StandardCharsets.UTF_8);
        return str;
    }

    @Override
    protected void process0() {//trace_id -> 575c5f25-6c05-44bb-a158-2063b772e8b1
        try {
            String event = (String) this.updateItem.get("event");
            String trace_id = (String) this.updateItem.get("trace_id");
            ParticipantJoined participant = JSONObject.toJavaObject(updateItem, ParticipantJoined.class);
            List<ParticipantJoined.PayloadDTO> payload = participant.getPayload();
            if (!CollectionUtils.isEmpty(payload)) {
                for (ParticipantJoined.PayloadDTO payloadDTO : payload) {
                    ParticipantJoined.PayloadDTO.OperatorDTO operatorDTO = payloadDTO.getOperator();
                    ParticipantJoined.PayloadDTO.OperatorDTO toOperator = payloadDTO.getToOperator();

                    String uuid = operatorDTO.getUuid() + trace_id;
                    if (!skippingMessage(uuid)) {
                        EventMessage.processedMessageIds.add(uuid);
                        TencentConferenceContext tencentConferenceContext = TencentConferenceContextCache.getInstance().get(payloadDTO.getMeetingInfo().getMeetingId());
                        if (tencentConferenceContext != null) {
                            JSONObject jsonObject = new JSONObject();
                            Map<String, AttendeeTencent> uuidAttendeeMapByUri = tencentConferenceContext.getUuidAttendeeMapByUri(operatorDTO.getMsOpenId());
                            switch (event) {
                                case "meeting.participant-joined":
                                    addParticipant(payloadDTO);
                                    tencentConferenceContext.setMeetingStatus(TencentMeetingStateEnum.MEETING_STATE_STARTED.name());
                                    jsonObject.put("meetingStatus", TencentMeetingStateEnum.MEETING_STATE_STARTED.name());
                                    jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(tencentConferenceContext));
                                    TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(tencentConferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                                    break;
                                case "meeting.participant-left":
                                    removeParticipant(payloadDTO);
                                    jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(tencentConferenceContext));
                                    TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(tencentConferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                                    break;
                                case "meeting.started":
                                    tencentConferenceContext.setMeetingStatus(TencentMeetingStateEnum.MEETING_STATE_STARTED.name());
                                    jsonObject.put("meetingStatus", TencentMeetingStateEnum.MEETING_STATE_STARTED.name());
                                    //查询会议设置状态
                                    jsonObject.put("allowUnmuteBySelf", tencentConferenceContext.getAllowUnmuteBySelf());
                                    jsonObject.put("allowChat", tencentConferenceContext.getAllowChat());
                                    jsonObject.put("shareScreen", tencentConferenceContext.getShareScreen());
                                    jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(tencentConferenceContext));
                                    TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(tencentConferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                                    break;
                                case "meeting.end":
                                    if (tencentConferenceContext != null) {
                                        tencentConferenceContext.setMeetingStatus(TencentMeetingStateEnum.MEETING_STATE_ENDED.name());
                                        jsonObject.put("meetingStatus", TencentMeetingStateEnum.MEETING_STATE_ENDED.name());
                                        TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(tencentConferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                                        IBusiTencentConferenceService bean = BeanFactory.getBean(IBusiTencentConferenceService.class);
                                        bean.endConference(tencentConferenceContext.getId(), EndReasonsType.AUTO_END);


                                    }

                                    break;
                                case "meeting.created":
                                    ParticipantJoined create = JSONObject.toJavaObject(updateItem, ParticipantJoined.class);
                                    if (tencentConferenceContext != null) {
                                        tencentConferenceContext.setCreator(create.getPayload().get(0).getMeetingInfo().getCreator());
                                        tencentConferenceContext.setMeetingStatus(TencentMeetingStateEnum.MEETING_STATE_INIT.name());
                                        jsonObject.put("meetingStatus", TencentMeetingStateEnum.MEETING_STATE_INIT.name());
                                        TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(tencentConferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                                    }
                                    break;
                                case "meeting.canceled":
                                    if (tencentConferenceContext != null) {
                                        tencentConferenceContext.setMeetingStatus(TencentMeetingStateEnum.MEETING_STATE_CANCELLED.name());
                                        jsonObject.put("meetingStatus", TencentMeetingStateEnum.MEETING_STATE_CANCELLED.name());
                                        TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(tencentConferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                                        IBusiTencentConferenceService bean = BeanFactory.getBean(IBusiTencentConferenceService.class);
                                        bean.endConference(tencentConferenceContext.getId(), EndReasonsType.AUTO_END);
                                    }
                                    break;

                                case "room.alert":
                                    break;
                                case "room.response":
                                    break;
                                case "meeting.mute-all":
                                    if (tencentConferenceContext != null) {
                                        tencentConferenceContext.setMuteAll(true);
                                        jsonObject.put("muteAll", true);
                                        TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(tencentConferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                                    }
                                    break;
                                case "meeting.unmute-all":
                                    if (tencentConferenceContext != null) {
                                        tencentConferenceContext.setMuteAll(false);
                                        jsonObject.put("muteAll", false);
                                        TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(tencentConferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                                    }
                                    break;


                                //用户等待主持人入会
                                case "meeting.participant-jbh-waiting":
                                    break;
                                //用户进入等候室
                                case "meeting.participant-joined-waiting-room":
                                    break;
                                //用户离开等候室
                                case "meeting.participant-left-waiting-room":
                                    break;
                                //用户离开等候室
                                case "meeting.participant-put-in-waiting-room":
                                    break;
                                //共享屏幕开启
                                case "meeting.share-screen-open":

                                    if (uuidAttendeeMapByUri != null) {
                                        Collection<AttendeeTencent> values = uuidAttendeeMapByUri.values();
                                        for (AttendeeTencent value : values) {
                                            value.setPresentStatus(YesOrNo.YES.getValue());
                                            jsonObject.put("presentStatus", YesOrNo.YES.getValue());
                                            jsonObject.put("id", value.getId());
                                            tencentConferenceContext.setPresentAttendeeId(value.getId());
                                            TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(tencentConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, jsonObject);
                                        }
                                    }
                                    break;
                                //共享屏幕结束
                                case "meeting.share-screen-close":
                                    if (uuidAttendeeMapByUri != null) {
                                        Collection<AttendeeTencent> values = uuidAttendeeMapByUri.values();
                                        for (AttendeeTencent value : values) {
                                            value.setPresentStatus(YesOrNo.NO.getValue());
                                            jsonObject.put("presentStatus", YesOrNo.NO.getValue());
                                            jsonObject.put("id", value.getId());
                                            tencentConferenceContext.setPresentAttendeeId(null);
                                            TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(tencentConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, jsonObject);
                                        }
                                    }
                                    break;
                                case "meeting.participant-role-changed":
                                    if (uuidAttendeeMapByUri != null) {
                                        Collection<AttendeeTencent> values = uuidAttendeeMapByUri.values();
                                        for (AttendeeTencent value : values) {
                                            value.setUserRole(toOperator.getUserRole());
                                            jsonObject.put("userRole", toOperator.getUserRole());
                                            jsonObject.put("id", value.getId());
                                            tencentConferenceContext.setPresentAttendeeId(null);
                                            TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(tencentConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, jsonObject);
                                        }
                                    }
                                    break;
                                case "meeting.updated":
                                    break;
                                default:
                                    break;
                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            logger.info("tencent事件处理错误：" + e.getMessage());
        }

    }

    private synchronized void addParticipant(ParticipantJoined.PayloadDTO payloadDTO) {
        try {
            ParticipantJoined.PayloadDTO.OperatorDTO operatorDTO = payloadDTO.getOperator();
            String userid = operatorDTO.getUserid();
            ParticipantJoined.PayloadDTO.MeetingInfoDTO meetingInfo = payloadDTO.getMeetingInfo();
            String uuid = operatorDTO.getUuid();
            RealTimeParticipantsResponse.ParticipantsDTO participantsDTO = new RealTimeParticipantsResponse.ParticipantsDTO();
            participantsDTO.setUserid(userid);
            participantsDTO.setMsOpenId(operatorDTO.getMsOpenId());
            participantsDTO.setUserName(operatorDTO.getUserName());
            participantsDTO.setInstanceid(Integer.valueOf(operatorDTO.getInstanceId()));


            TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(meetingInfo.getMeetingId());
            if (conferenceContext == null) {
                return;
            }
            AttendeeTencent attendee = conferenceContext.getUuidAttendeeByMsOpenId(operatorDTO.getMsOpenId());
            if (attendee != null) {
                conferenceContext.getParticipantAttendeeAllMap().put(attendee.getSmcParticipant().getGeneralParam().getId(), attendee);
                changeParticipant(attendee.getSmcParticipant(), participantsDTO, conferenceContext, null, attendee);
            } else {

                AttendeeTencent attendee_name = conferenceContext.getParticipantByName(operatorDTO.getUserName());
                if (attendee_name == null) {
                    addParticpant(participantsDTO, conferenceContext, null, uuid);
                    List<McuAttendeeTencent> mcuAttendees = conferenceContext.getMcuAttendees();
                    String msOpenId = operatorDTO.getMsOpenId();
                    for (McuAttendeeTencent mcuAttendee : mcuAttendees) {
                        if (Objects.equals(mcuAttendee.getName(), decode2(operatorDTO.getUserName()))) {
                            mcuAttendee.setMs_open_id(msOpenId);
                            mcuAttendee.setInstanceid(Integer.valueOf(operatorDTO.getInstanceId()));
                            mcuAttendee.setUserRole(operatorDTO.getUserRole());
                            RemotePartyAttendeesMap remotePartyAttendeesMap = conferenceContext.getRemotePartyAttendeesMap();
                            remotePartyAttendeesMap.addAttendee(mcuAttendee);
                            Map<String, AttendeeTencent> uuidAttendeeMapByUri = conferenceContext.getUuidAttendeeMapByUri(msOpenId);
                            if (uuidAttendeeMapByUri == null) {
                                uuidAttendeeMapByUri = new ConcurrentHashMap<>();
                                uuidAttendeeMapByUri.put(mcuAttendee.getId(), mcuAttendee);
                                remotePartyAttendeesMap.put(msOpenId, uuidAttendeeMapByUri);

                                SmcParitipantsStateRep.ContentDTO contentDTO = new SmcParitipantsStateRep.ContentDTO();
                                SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam = new SmcParitipantsStateRep.ContentDTO.GeneralParamDTO();
                                contentDTO.setGeneralParam(generalParam);
                                contentDTO.setUserRole(operatorDTO.getUserRole());
                                ParticipantState state = new ParticipantState();
                                contentDTO.setState(state);
                                contentDTO.setWeight(0);
                                String userName = decode2(operatorDTO.getUserName());
                                generalParam.setName(userName);
                                String id = UUID.randomUUID().toString();
                                generalParam.setId(id);
                                state.setParticipantId(id);
                                state.setOnline(true);
                                state.setMs_open_id(msOpenId);
                                contentDTO.setTerminalOnline(true);
                                contentDTO.setMs_open_id(msOpenId);

                                contentDTO.setNick_name(operatorDTO.getUserName());
                                contentDTO.setInstanceid(Integer.valueOf(operatorDTO.getInstanceId()));

                                mcuAttendee.setSmcParticipant(contentDTO);

                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("id", mcuAttendee.getId());
                                jsonObject.put("ms_open_id", msOpenId);
                                jsonObject.put("instanceid", Integer.valueOf(operatorDTO.getInstanceId()));
                                TencentWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, jsonObject);
                            }
                        }
                        attendee = conferenceContext.getUuidAttendeeByMsOpenId(operatorDTO.getMsOpenId());
                        if (attendee == null) {
                            addParticpant(participantsDTO, conferenceContext, null, uuid);
                        }
                    }
                } else {

                    SmcParitipantsStateRep.ContentDTO contentDTO = new SmcParitipantsStateRep.ContentDTO();
                    SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam = new SmcParitipantsStateRep.ContentDTO.GeneralParamDTO();
                    contentDTO.setGeneralParam(generalParam);
                    contentDTO.setUserRole(participantsDTO.getUserRole());
                    ParticipantState state = new ParticipantState();
                    contentDTO.setState(state);
                    contentDTO.setWeight(0);
                    String userName = participantsDTO.getUserName();
                    generalParam.setName(userName);
                    String user_id = UUID.randomUUID().toString();
                    generalParam.setId(user_id);
                    state.setParticipantId(user_id);
                    state.setOnline(true);
                    state.setMs_open_id(participantsDTO.getMsOpenId());
                    state.setScreen_shared_state(participantsDTO.getScreenSharedState());
                    contentDTO.setTerminalOnline(true);
                    contentDTO.setMs_open_id(participantsDTO.getMsOpenId());
                    contentDTO.setNick_name(participantsDTO.getUserName());
                    contentDTO.setInstanceid(participantsDTO.getInstanceid());
                    contentDTO.setUuid(operatorDTO.getUuid());

                    attendee_name.setSmcParticipant(contentDTO);
                    attendee_name.setMs_open_id(participantsDTO.getMsOpenId());
                    attendee_name.setInstanceid(participantsDTO.getInstanceid());
                    attendee_name.setRemoteParty(participantsDTO.getMsOpenId());
                    attendee_name.setSmcParticipant(contentDTO);
                    conferenceContext.getParticipantAttendeeAllMap().put(user_id, attendee_name);
                    TencentWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee_name);

                    processUpdateParticipant(conferenceContext, attendee_name, false);

                }


            }
        } catch (Exception e) {
            logger.error("addParticipant .." + e.getMessage());
        }
    }

    private boolean skippingMessage(String uuid) {
        if (EventMessage.processedMessageIds.contains(uuid)) {
            logger.info("Duplicate message ID: " + uuid + ". Skipping processing.");
            return true;
        }
        return false;
    }

    private void removeParticipant(ParticipantJoined.PayloadDTO payloadDTO) {
        try {
            ParticipantJoined.PayloadDTO.OperatorDTO operatorDTO = payloadDTO.getOperator();

            ParticipantJoined.PayloadDTO.MeetingInfoDTO meetingInfo = payloadDTO.getMeetingInfo();
            TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(meetingInfo.getMeetingId());
            AttendeeTencent attendee = conferenceContext.getUuidAttendeeByMsOpenId(operatorDTO.getMsOpenId());
            if (attendee != null) {
                removeParticipan(conferenceContext, attendee);
            }
        } catch (Exception e) {
            logger.info("removeParticipant.." + e.getMessage());
        }
    }

    private void removeParticipan(TencentConferenceContext conferenceContext, AttendeeTencent attendee) {
        attendee.leaveMeeting();
        // 从缓存中移除
        conferenceContext.removeAttendeeById(attendee.getId());
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("id", attendee.getId());
        updateMap.put("deptId", attendee.getDeptId());
        updateMap.put("mcuAttendee", attendee.isMcuAttendee());
        TencentWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
        String reason = "【" + attendee.getName() + "】离会";
        TencentWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, reason);

        if (attendee == conferenceContext.getMasterAttendee()) {
            Map<String, Object> data = new HashMap<>();
            data.put("oldMasterAttendee", attendee);
            data.put("newMasterAttendee", null);
            TencentWebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MASTER_CHANGED, data);

            StringBuilder messageTip = new StringBuilder();
            messageTip.append("主会场已离会【").append(attendee.getName()).append("】");
            TencentWebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
            conferenceContext.clearMasterAttendee();
        }
        processUpdateParticipant(conferenceContext, attendee, false);
    }

    private synchronized void addParticpant(RealTimeParticipantsResponse.ParticipantsDTO participant, TencentConferenceContext conferenceContext, Boolean raiseHandsState, String uuId) {
        SmcParitipantsStateRep.ContentDTO contentDTO = new SmcParitipantsStateRep.ContentDTO();
        SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam = new SmcParitipantsStateRep.ContentDTO.GeneralParamDTO();
        contentDTO.setGeneralParam(generalParam);
        contentDTO.setUserRole(participant.getUserRole());
        ParticipantState state = new ParticipantState();
        state.setRaise_hands_state(raiseHandsState);
        contentDTO.setState(state);
        contentDTO.setWeight(0);
        String userName = participant.getUserName();
        generalParam.setName(userName);
        String userid = UUID.randomUUID().toString();
        generalParam.setId(userid);
        state.setParticipantId(userid);
        state.setOnline(true);
        state.setMs_open_id(participant.getMsOpenId());
        state.setScreen_shared_state(participant.getScreenSharedState());
        contentDTO.setTerminalOnline(true);
        contentDTO.setMs_open_id(participant.getMsOpenId());
        contentDTO.setNick_name(participant.getUserName());
        contentDTO.setInstanceid(participant.getInstanceid());
        contentDTO.setUuid(uuId);
        new SelfCallAttendeeTencentProcessor(contentDTO, conferenceContext).process();
    }

    private void processUpdateParticipant(TencentConferenceContext conferenceContext, AttendeeTencent attendeeTencent, boolean updateMediaInfo) {
        IBusiMcuTencentHistoryConferenceService teleHistoryConferenceService = BeanFactory.getBean(IBusiMcuTencentHistoryConferenceService.class);
        teleHistoryConferenceService.updateBusiHistoryParticipant(conferenceContext, attendeeTencent, updateMediaInfo);

    }

    private void changeParticipant(SmcParitipantsStateRep.ContentDTO content, RealTimeParticipantsResponse.ParticipantsDTO participant, TencentConferenceContext conferenceContext, Boolean raiseHandsState, AttendeeTencent attendee) {

        Boolean audioState = participant.getAudioState();
        Boolean videoState = participant.getVideoState();
        Boolean screenSharedState = participant.getScreenSharedState();
        String userName = participant.getUserName();
        Integer userRole = participant.getUserRole();
        Integer oldUserRole = content.getUserRole();
        ParticipantState state = content.getState();
        Boolean videoMute = state.getVideoMute();
        Boolean mute = state.getMute();

        if (Objects.equals(videoState, videoMute) ||
                Objects.equals(audioState, mute) ||
                !Objects.equals(userRole, oldUserRole) ||
//                !Objects.equals(decode2(userName), content.getGeneralParam().getName()) ||
                !Objects.equals(raiseHandsState, content.getState().getRaise_hands_state()) ||
                !Objects.equals(screenSharedState, content.getState().getScreen_shared_state())) {
            state.setVideoMute(!videoState);
            state.setMute(!audioState);
            state.setRaise_hands_state(raiseHandsState);
            state.setScreen_shared_state(screenSharedState);
            content.setUserRole(participant.getUserRole());
            content.getGeneralParam().setName(userName);
            content.setNick_name(userName);
        }
        AttendeeUtils.updateByParticipant(conferenceContext, content, attendee);
        processUpdateParticipant(conferenceContext, attendee, false);
    }

}
