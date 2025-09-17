package com.paradisecloud.fcm.tencent.monitor;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.tencent.busi.attende.AttendeeTencent;
import com.paradisecloud.fcm.tencent.busi.attende.McuAttendeeTencent;
import com.paradisecloud.fcm.tencent.busi.attende.RemotePartyAttendeesMap;
import com.paradisecloud.fcm.tencent.cache.*;
import com.paradisecloud.fcm.tencent.model.MeetingRoom;
import com.paradisecloud.fcm.tencent.model.ParticipantState;
import com.paradisecloud.fcm.tencent.model.SmcParitipantsStateRep;
import com.paradisecloud.fcm.tencent.model.client.TencentConferenceCtrlClient;
import com.paradisecloud.fcm.tencent.model.client.TencentMeetingClient;
import com.paradisecloud.fcm.tencent.model.client.TencentUserClient;
import com.paradisecloud.fcm.tencent.model.reponse.MRAstatusResponse;
import com.paradisecloud.fcm.tencent.model.reponse.RealTimeParticipantsResponse;
import com.paradisecloud.fcm.tencent.model.reponse.RoomResponse;
import com.paradisecloud.fcm.tencent.model.request.NameParticipantRequest;
import com.paradisecloud.fcm.tencent.model.request.QueryMRAStatusRequest;
import com.paradisecloud.fcm.tencent.model.request.QueryMeetingParticipantsResponse;
import com.paradisecloud.fcm.tencent.model.request.QueryRealTimeParticipantsRequest;
import com.paradisecloud.fcm.tencent.service2.interfaces.IAttendeeTencentService;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiMcuTencentHistoryConferenceService;
import com.paradisecloud.fcm.tencent.templateConference.updateprocess.SelfCallAttendeeTencentProcessor;
import com.paradisecloud.fcm.tencent.utils.AttendeeUtils;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ThreadUtils;
import com.tencentcloudapi.wemeet.common.exception.WemeetSdkException;
import com.tencentcloudapi.wemeet.models.meeting.QueryParticipantsRequest;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author nj
 * @date 2023/7/11 10:03
 */
@Component
public class RealTimeParticipantsThread extends Thread implements InitializingBean {

    public static final int PAGE_SIZE = 50;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private long roomsListLastUpdateTime = 0;


    @Override
    public void run() {
        logger.info("腾讯会议【RealTimeParticipantsThread】 start!!!!");
        while (true) {
            try {
                Collection<TencentConferenceContext> values = TencentConferenceContextCache.getInstance().values();
                for (TencentConferenceContext conferenceContext : values) {
                    if (conferenceContext == null || conferenceContext.isEnd()) {
                        break;
                    }
                    List<RealTimeParticipantsResponse.ParticipantsDTO> participants = new ArrayList<>();
                    getRealParticipants(conferenceContext,1, participants);
                    for (RealTimeParticipantsResponse.ParticipantsDTO participant : participants) {
                        String userid = participant.getUserid();
                        String msOpenId = participant.getMsOpenId();

                        Boolean raiseHandsState = null;
                        //查询MRA
                        try {
                            if (participant.getInstanceid() == 9) {
                                MRAstatusResponse mrAstatusResponse = getMrAstatusResponse(conferenceContext, participant, msOpenId);
                                raiseHandsState = mrAstatusResponse.getRaiseHandsState();
                            }
                        } catch (WemeetSdkException e) {
                        }
                        synchronized (this){
                            if (Strings.isNotBlank(msOpenId)) {
                                List<McuAttendeeTencent> mcuAttendees = conferenceContext.getMcuAttendees();
                                for (McuAttendeeTencent mcuAttendee : mcuAttendees) {


                                    if (Objects.equals(mcuAttendee.getName(), decode2(participant.getUserName()))) {
                                        mcuAttendee.setMs_open_id(msOpenId);
                                        mcuAttendee.setInstanceid(participant.getInstanceid());
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
                                            contentDTO.setUserRole(participant.getUserRole());
                                            ParticipantState state = new ParticipantState();
                                            state.setRaise_hands_state(raiseHandsState);
                                            contentDTO.setState(state);
                                            contentDTO.setWeight(0);
                                            String userName = decode2(participant.getUserName());
                                            generalParam.setName(userName);
                                            String id = UUID.randomUUID().toString();
                                            generalParam.setId(id);
                                            state.setParticipantId(id);
                                            state.setOnline(true);
                                            state.setVideoMute(!participant.getVideoState());
                                            state.setMute(!participant.getAudioState());
                                            state.setMs_open_id(participant.getMsOpenId());
                                            state.setScreen_shared_state(participant.getScreenSharedState());
                                            contentDTO.setTerminalOnline(true);
                                            contentDTO.setMs_open_id(participant.getMsOpenId());

                                            contentDTO.setNick_name(participant.getUserName());
                                            contentDTO.setInstanceid(participant.getInstanceid());

                                            mcuAttendee.setSmcParticipant(contentDTO);

                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put("id", mcuAttendee.getId());
                                            jsonObject.put("ms_open_id", msOpenId);
                                            jsonObject.put("instanceid", participant.getInstanceid());
                                            TencentWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, jsonObject);
                                        }

                                    } else {
                                        String name_check = decode2(participant.getUserName());
                                        if (name_check.length() == 5 && Integer.valueOf(name_check) != null) {

                                            if (Objects.equals(McuType.SMC2.getCode(), mcuAttendee.getCascadeMcuType())) {
                                                mcuAttendee.setMs_open_id(msOpenId);
                                                mcuAttendee.setInstanceid(participant.getInstanceid());
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
                                                    contentDTO.setUserRole(participant.getUserRole());
                                                    ParticipantState state = new ParticipantState();
                                                    state.setRaise_hands_state(raiseHandsState);
                                                    contentDTO.setState(state);
                                                    contentDTO.setWeight(0);
                                                    String userName = mcuAttendee.getName();
                                                    generalParam.setName(userName);
                                                    String id = UUID.randomUUID().toString();
                                                    generalParam.setId(id);
                                                    state.setParticipantId(id);
                                                    state.setOnline(true);
                                                    state.setVideoMute(!participant.getVideoState());
                                                    state.setMute(!participant.getAudioState());
                                                    state.setMs_open_id(participant.getMsOpenId());
                                                    state.setScreen_shared_state(participant.getScreenSharedState());
                                                    contentDTO.setTerminalOnline(true);
                                                    contentDTO.setMs_open_id(participant.getMsOpenId());


                                                    contentDTO.setInstanceid(participant.getInstanceid());

                                                    mcuAttendee.setSmcParticipant(contentDTO);


                                                    TencentConferenceCtrlClient conferenceCtrlClient = conferenceContext.getTencentBridge().getConferenceCtrlClient();
                                                    NameParticipantRequest request = new NameParticipantRequest();

                                                    request.setMeetingId(conferenceContext.getMeetingId());
                                                    request.setInstanceid(1);
                                                    request.setOperatorIdType(4);
                                                    request.setOperatorId(conferenceContext.getMsopenid());
                                                    NameParticipantRequest.UsersDTO usersDTO = new NameParticipantRequest.UsersDTO();
                                                    usersDTO.setMsOpenid(msOpenId);
                                                    usersDTO.setInstanceid(participant.getInstanceid());
                                                    usersDTO.setNick_name(mcuAttendee.getName());
                                                    request.setUsers(Arrays.asList(usersDTO));
                                                    try {
                                                        conferenceCtrlClient.nameChange(request);
                                                    } catch (WemeetSdkException e) {
                                                    }

                                                    JSONObject jsonObject = new JSONObject();
                                                    jsonObject.put("id", mcuAttendee.getId());
                                                    jsonObject.put("ms_open_id", msOpenId);
                                                    jsonObject.put("instanceid", participant.getInstanceid());
                                                    TencentWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, jsonObject);
                                                }
                                            }

                                        }
                                    }
                                }


                                AttendeeTencent attendee = conferenceContext.getUuidAttendeeByMsOpenId(msOpenId);
                                if (attendee != null) {
                                    conferenceContext.getParticipantAttendeeAllMap().put(attendee.getSmcParticipant().getGeneralParam().getId(), attendee);
                                    changeParticipant(attendee.getSmcParticipant(), participant, conferenceContext, raiseHandsState, attendee);
                                } else {
                                    addParticpant(participant, conferenceContext, raiseHandsState);
                                }
                            } else {
                                Boolean flag = true;
                                Map<String, AttendeeTencent> attendeeMap = conferenceContext.getParticipantAttendeeAllMap();
                                if (!CollectionUtils.isEmpty(attendeeMap)) {
                                    for (Map.Entry<String, AttendeeTencent> stringAttendeeTencentEntry : attendeeMap.entrySet()) {
                                        AttendeeTencent value = stringAttendeeTencentEntry.getValue();
                                        SmcParitipantsStateRep.ContentDTO smcParticipant = value.getSmcParticipant();
                                        String ms_open_id = smcParticipant.getMs_open_id();
                                        if (Objects.equals(ms_open_id, msOpenId)) {
                                            flag = false;
                                            changeParticipant(smcParticipant, participant, conferenceContext, raiseHandsState, value);
                                            break;
                                        }
                                    }

                                }
                                if (flag) {
                                    participant.setUserid(UUID.randomUUID().toString());
                                    addParticpant(participant, conferenceContext, raiseHandsState);
                                }
                            }
                        }

                    }


                    List<SmcParitipantsStateRep.ContentDTO> contentList = new ArrayList<>();
                    Map<String, AttendeeTencent> attendeeMap = conferenceContext.getParticipantAttendeeAllMap();
                    for (Map.Entry<String, AttendeeTencent> stringAttendeeTencentEntry : attendeeMap.entrySet()) {
                        AttendeeTencent value = stringAttendeeTencentEntry.getValue();
                        if (value.getSmcParticipant() != null) {
                            contentList.add(value.getSmcParticipant());
                            String ms_open_id = value.getSmcParticipant().getMs_open_id();
                            if (CollectionUtils.isEmpty(participants)) {
                                removeParticipan(conferenceContext, value.getSmcParticipant(), value);
                            } else {
                                List<String> userIdCollector = participants.stream().map(x -> x.getMsOpenId()).collect(Collectors.toList());
                                if (!userIdCollector.contains(ms_open_id)) {
                                    removeParticipan(conferenceContext, value.getSmcParticipant(), value);
                                }
                            }

                        }
                    }
                }

                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - roomsListLastUpdateTime >= 10000) {
                    roomsListLastUpdateTime = currentTimeMillis;
                    try {
                        updateRoomsList();
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
            } catch (Exception e) {
                logger.info(e.getMessage());
            } finally {
                ThreadUtils.sleep(1000 * 3);
            }

        }

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
                !Objects.equals(userName, content.getGeneralParam().getName()) ||
                !Objects.equals(raiseHandsState, content.getState().getRaise_hands_state()) ||
                !Objects.equals(screenSharedState, content.getState().getScreen_shared_state())) {
            state.setVideoMute(!videoState);
            state.setMute(!audioState);
            state.setRaise_hands_state(raiseHandsState);
            state.setScreen_shared_state(screenSharedState);
            content.setUserRole(participant.getUserRole());
            content.getGeneralParam().setName(decode2(userName));
            content.setNick_name(userName);
        }
        AttendeeUtils.updateByParticipant(conferenceContext, content, attendee);
        processUpdateParticipant(conferenceContext, attendee, false);
    }

    private MRAstatusResponse getMrAstatusResponse(TencentConferenceContext conferenceContext, RealTimeParticipantsResponse.ParticipantsDTO participant, String msOpenId) throws WemeetSdkException {
        QueryMRAStatusRequest queryMRAStatusRequest = new QueryMRAStatusRequest();
        queryMRAStatusRequest.setMeetingId(conferenceContext.getMeetingId());
        queryMRAStatusRequest.setInstanceid(1);
        queryMRAStatusRequest.setOperatorIdType(1);
        queryMRAStatusRequest.setOperatorId(conferenceContext.getTencentUser());
        queryMRAStatusRequest.setUser_instance_id(participant.getInstanceid());
        queryMRAStatusRequest.setUser_ms_open_id(URLEncoder.encode(msOpenId));
        MRAstatusResponse mrAstatusResponse = conferenceContext.getTencentBridge().getMEETING_CLIENT().queryMRA_participantStatus(queryMRAStatusRequest);
        return mrAstatusResponse;
    }

    private void removeParticipan(TencentConferenceContext conferenceContext, SmcParitipantsStateRep.ContentDTO item, AttendeeTencent attendee) {
//        attendee.leaveMeeting();
//        // 从缓存中移除
//        conferenceContext.removeAttendeeById(attendee.getId());
//        Map<String, Object> updateMap = new HashMap<>();
//        updateMap.put("id", attendee.getId());
//        updateMap.put("deptId", attendee.getDeptId());
//        updateMap.put("mcuAttendee", attendee.isMcuAttendee());
//        TencentWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
//        String reason = "【" + attendee.getName() + "】离会";
//        TencentWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, reason);
//
//        if (attendee == conferenceContext.getMasterAttendee()) {
//            Map<String, Object> data = new HashMap<>();
//            data.put("oldMasterAttendee", attendee);
//            data.put("newMasterAttendee", null);
//            TencentWebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MASTER_CHANGED, data);
//
//            StringBuilder messageTip = new StringBuilder();
//            messageTip.append("主会场已离会【").append(attendee.getName()).append("】");
//            TencentWebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
//            conferenceContext.clearMasterAttendee();
//        }
//        processUpdateParticipant(conferenceContext, attendee, false);
    }

    private void addParticpant(RealTimeParticipantsResponse.ParticipantsDTO participant, TencentConferenceContext conferenceContext, Boolean raiseHandsState) {
//        SmcParitipantsStateRep.ContentDTO contentDTO = new SmcParitipantsStateRep.ContentDTO();
//        SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam = new SmcParitipantsStateRep.ContentDTO.GeneralParamDTO();
//        contentDTO.setGeneralParam(generalParam);
//        contentDTO.setUserRole(participant.getUserRole());
//        ParticipantState state = new ParticipantState();
//        state.setRaise_hands_state(raiseHandsState);
//        contentDTO.setState(state);
//        contentDTO.setWeight(0);
//        String userName = decode2(participant.getUserName());
//        generalParam.setName(userName);
//        String userid = UUID.randomUUID().toString();
//        generalParam.setId(userid);
//        state.setParticipantId(userid);
//        state.setOnline(true);
//        state.setVideoMute(!participant.getVideoState());
//        state.setMute(!participant.getAudioState());
//        state.setMs_open_id(participant.getMsOpenId());
//        state.setScreen_shared_state(participant.getScreenSharedState());
//        contentDTO.setTerminalOnline(true);
//        contentDTO.setMs_open_id(participant.getMsOpenId());
//        contentDTO.setNick_name(participant.getUserName());
//        contentDTO.setInstanceid(participant.getInstanceid());
//        new SelfCallAttendeeTencentProcessor(contentDTO, conferenceContext).process();

    }


    private void processUpdateParticipant(TencentConferenceContext conferenceContext, AttendeeTencent attendeeTencent, boolean updateMediaInfo) {
        IBusiMcuTencentHistoryConferenceService teleHistoryConferenceService = BeanFactory.getBean(IBusiMcuTencentHistoryConferenceService.class);
        teleHistoryConferenceService.updateBusiHistoryParticipant(conferenceContext, attendeeTencent, updateMediaInfo);

    }

    public RealTimeParticipantsResponse getRealParticipants(TencentConferenceContext conferenceContext,int page, List<RealTimeParticipantsResponse.ParticipantsDTO> participants) {
        TencentMeetingClient meeting_client = conferenceContext.getTencentBridge().getMEETING_CLIENT();
        QueryRealTimeParticipantsRequest request = new QueryRealTimeParticipantsRequest();
        request.setMeetingId(conferenceContext.getMeetingId());
        request.setOperatorIdType(1);
        request.setOperatorId(conferenceContext.getTencentBridge().getTencentUserId());
        request.setPage(page);
        request.setPageSize(PAGE_SIZE);
        RealTimeParticipantsResponse realTimeParticipantsResponse = null;
        try {
            realTimeParticipantsResponse = meeting_client.queryRealTimeParticipantsById(request);
        } catch (WemeetSdkException e) {
            e.printStackTrace();
        }
        if (realTimeParticipantsResponse != null) {
            List<RealTimeParticipantsResponse.ParticipantsDTO> participantsDTOList = realTimeParticipantsResponse.getParticipants();
            if (!CollectionUtils.isEmpty(participantsDTOList)) {
                participants.addAll(participantsDTOList);
            }
            Integer totalCount = realTimeParticipantsResponse.getTotalCount();
            Integer currentPage = realTimeParticipantsResponse.getCurrentPage();
            Integer currentSize = realTimeParticipantsResponse.getCurrentSize();
            if (currentPage * PAGE_SIZE + currentSize < totalCount) {
                getRealParticipants(conferenceContext,currentPage + 1, participants);
            } else {
                return realTimeParticipantsResponse;
            }
        }
        return realTimeParticipantsResponse;
    }

    public QueryMeetingParticipantsResponse getMeetingParticipants(TencentConferenceContext conferenceContext,List<QueryMeetingParticipantsResponse.Participant> participants, Integer pos) {
        TencentMeetingClient meeting_client = conferenceContext.getTencentBridge().getMEETING_CLIENT();
        QueryParticipantsRequest request = new QueryParticipantsRequest();
        request.setMeetingId(conferenceContext.getMeetingId());
        request.setUserId(conferenceContext.getTencentBridge().getTencentUserId());
        request.setSize(PAGE_SIZE);
        if (pos != null) {
            request.setPos(pos);
        }
        QueryMeetingParticipantsResponse queryParticipantsResponse = null;
        try {
            queryParticipantsResponse = meeting_client.queryParticipants(request);
        } catch (WemeetSdkException e) {
            e.printStackTrace();
        }
        if (queryParticipantsResponse != null) {
            List<QueryMeetingParticipantsResponse.Participant> participantsDTOList = queryParticipantsResponse.getParticipants();
            if (!CollectionUtils.isEmpty(participantsDTOList)) {
                participants.addAll(participantsDTOList);
            }
            Boolean hasRemaining = queryParticipantsResponse.getHasRemaining();
            if (hasRemaining) {
                getMeetingParticipants(conferenceContext,participants, queryParticipantsResponse.getNextPos());
            } else {
                return queryParticipantsResponse;
            }

        }
        return queryParticipantsResponse;
    }

    public static String decode2(String base64Str) {
        // 解码
        byte[] base64Data = Base64.getDecoder().decode(base64Str);
        // byte[]-->String（解码后的字符串）
        String str = new String(base64Data, StandardCharsets.UTF_8);
        return str;
    }

    /**
     * 更新rooms列表
     */
    private void updateRoomsList() {
        IAttendeeTencentService attendeeTencentService = BeanFactory.getBean(IAttendeeTencentService.class);
        Map<Long, TencentBridge> tencentBridgeMap = TencentBridgeCache.getInstance().getTencentBridgeMap();
        for (TencentBridge tencentBridge : tencentBridgeMap.values()) {
            List<MeetingRoom> allMeetingRoomList = new ArrayList<>();
            if (tencentBridge.isAvailable()) {
                int pageIndex = 1;
                int pageSize = 50;
                while (true) {
                    RoomResponse roomResponse = attendeeTencentService.rooms(tencentBridge, pageIndex, pageSize, null);
                    if (roomResponse == null) {
                        break;
                    }
                    if (roomResponse.getMeetingRoomList() != null) {
                        allMeetingRoomList.addAll(roomResponse.getMeetingRoomList());
                    }
                    if (roomResponse.getCurrentPage() >= roomResponse.getTotalPage()) {
                        break;
                    }
                    pageIndex++;
                }

            }
            TencentRoomsCache.getInstance().setMeetingRoomList(tencentBridge.getBusiTencent().getId(), allMeetingRoomList);
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}
