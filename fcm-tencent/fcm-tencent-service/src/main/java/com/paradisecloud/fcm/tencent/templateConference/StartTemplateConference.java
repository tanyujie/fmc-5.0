package com.paradisecloud.fcm.tencent.templateConference;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.uuid.UUID;
import com.paradisecloud.fcm.common.enumer.AppointmentConferenceStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryCallMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuTencentConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuTencentTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuTencentTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentTemplateConference;
import com.paradisecloud.fcm.tencent.busi.attende.AttendeeTencent;
import com.paradisecloud.fcm.tencent.busi.attende.RoomAttendeeTencent;
import com.paradisecloud.fcm.tencent.cache.*;
import com.paradisecloud.fcm.tencent.model.AutoRecordType;
import com.paradisecloud.fcm.tencent.model.TencentMeetingStateEnum;
import com.paradisecloud.fcm.tencent.model.client.TencentMeetingClient;
import com.paradisecloud.fcm.tencent.model.client.TencentUserClient;
import com.paradisecloud.fcm.tencent.model.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.tencent.model.reponse.QueryUserMsOpenIdResponse;
import com.paradisecloud.fcm.tencent.model.request.CreateMeetingRequestLocal;
import com.paradisecloud.fcm.tencent.model.request.QueryUserMsOpenIdRequest;
import com.paradisecloud.fcm.tencent.model.request.TencentDismissMeetingRequest;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiMcuTencentHistoryConferenceService;
import com.paradisecloud.fcm.tencent.task.InviteAttendeeTencentTask;
import com.paradisecloud.fcm.tencent.task.TencentDelayTaskService;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;
import com.tencentcloudapi.wemeet.common.constants.InstanceEnum;
import com.tencentcloudapi.wemeet.common.exception.WemeetSdkException;
import com.tencentcloudapi.wemeet.models.LiveConfig;
import com.tencentcloudapi.wemeet.models.MeetingSetting;
import com.tencentcloudapi.wemeet.models.meeting.*;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author nj
 * @date 2023/4/20 11:53
 */
public class StartTemplateConference extends BuildTemplateConferenceContext {

    Logger logger = LoggerFactory.getLogger(getClass());

    public synchronized TencentConferenceContext startTemplateConference(long templateConferenceId) {

        logger.info("模板会议启动入口：" + templateConferenceId);

        BusiMcuTencentTemplateConferenceMapper busiMcuTencentTemplateConferenceMapper = BeanFactory.getBean(BusiMcuTencentTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuTencentTemplateConference tc = busiMcuTencentTemplateConferenceMapper.selectBusiMcuTencentTemplateConferenceById(templateConferenceId);
        IBusiMcuTencentHistoryConferenceService busiMcutencentHistoryConferenceService = BeanFactory.getBean(IBusiMcuTencentHistoryConferenceService.class);
        if (tc == null) {
            return null;
        }
        // 获取会议上下文
        TencentConferenceContext conferenceContext = buildTemplateConferenceContext(templateConferenceId);
        if (conferenceContext == null) {
            return null;
        }
        // 启动前校验会议是否已开始
        if (conferenceContext.isStart()) {
            throw new SystemException(1009874, "会议已开始，请勿重复开始");
        }

        if (tc.getLastConferenceId() != null) {
            BusiHistoryConference busiHistoryConference = busiMcutencentHistoryConferenceService.selectBusiHistoryConferenceById(Long.valueOf(tc.getLastConferenceId()));
            if (busiHistoryConference != null) {
                Date endTime = busiHistoryConference.getConferenceEndTime();
                if (endTime != null) {
                    long diff = System.currentTimeMillis() - endTime.getTime();
                    if (diff < 10000) {
                        throw new SystemException(1009874, "该会议室刚结束会议，正在准备中，请等待" + (10 - diff / 1000) + "秒");
                    }
                }
            }
        }


      //  TencentBridge bridge = TencentBridgeCache.getInstance().getAvailableBridgesByDept(tc.getDeptId());
        TencentBridge bridge = conferenceContext.getTencentBridge();
        if (bridge == null) {
            return null;
        }
        TencentMeetingClient meeting_client = bridge.getMEETING_CLIENT();
        TencentUserClient user_client = bridge.getUSER_CLIENT();

        String confId = tc.getConfId();
        if (Strings.isNotBlank(confId)) {
            //查询
            try {

                QueryMeetingByIdRequest queryMeetingByIdRequest1 = new QueryMeetingByIdRequest();
                queryMeetingByIdRequest1.setMeetingId(tc.getConfId());
                queryMeetingByIdRequest1.setUserId(bridge.getTencentUserId());
                queryMeetingByIdRequest1.setInstanceId(1);
                QueryMeetingDetailResponse queryMeetingDetailResponse1 = meeting_client.queryMeetingById(queryMeetingByIdRequest1);


                List<MeetingInfo> meetingInfoList =  queryMeetingDetailResponse1.getMeetingInfoList();

                if (!CollectionUtils.isEmpty(meetingInfoList)) {
                    for (MeetingInfo meetingInfo : meetingInfoList) {
                        if (Objects.equals(meetingInfo.getStatus(), TencentMeetingStateEnum.MEETING_STATE_STARTED.name()) ||
                                Objects.equals(meetingInfo.getStatus(), TencentMeetingStateEnum.MEETING_STATE_INIT.name())) {

                            conferenceContext.setStart(true);
                            conferenceContext.setStartTime(new Date());

                            conferenceContext.setMeetingId(meetingInfo.getMeetingId());
                            conferenceContext.setConferenceNumber(meetingInfo.getMeetingCode());
                            conferenceContext.setConferenceRemoteParty(meetingInfo.getMeetingCode() + "@" + conferenceContext.getMcuCallIp());
                            tc.setConferenceNumber(Long.valueOf(meetingInfo.getMeetingCode()));

                            conferenceContext.setAllowUnmuteBySelf(meetingInfo.getSettings().getAllowUnmuteSelf());
                            QueryUserMsOpenIdRequest queryUserMsOpenIdRequest = new QueryUserMsOpenIdRequest();
                            queryUserMsOpenIdRequest.setMeetingId(conferenceContext.getMeetingId());
                            queryUserMsOpenIdRequest.setOperatorId(conferenceContext.getTencentUser());
                            queryUserMsOpenIdRequest.setOperatorIdType(1);
                            try {
                                QueryUserMsOpenIdResponse msOpenIdResponse = user_client.queryUserMsOpenId(queryUserMsOpenIdRequest);
                                String msOpenId = msOpenIdResponse.getMsOpenId();
                                conferenceContext.setMsopenid(msOpenId);
                            } catch (WemeetSdkException e) {
                                logger.info("conferenceContext setMsopenid error" + e.getMessage());
                            }
                            conferenceContext.setShareScreen(true);
                            conferenceContext.setAllowChat(0);
                            conferenceContext.setAutoWaitingRoom(meetingInfo.getSettings().getAutoInWaitingRoom());
                        }else {
                            createConference(tc, conferenceContext, bridge, meeting_client, user_client);
                        }
                    }
                }
            } catch (WemeetSdkException e) {
                throw new CustomException("开始会议失败" + e.getMessage());
            }

        }else {
            //创建会议
            createConference(tc, conferenceContext, bridge, meeting_client, user_client);
        }

        saveHistory(busiMcutencentHistoryConferenceService, tc, conferenceContext);
        // ops云会议
        BusiMcuTencentConferenceAppointmentMapper busiMcuTencentConferenceAppointmentMapper = BeanFactory.getBean(BusiMcuTencentConferenceAppointmentMapper.class);
        List<BusiMcuTencentConferenceAppointment> busiMcuTencentConferenceAppointmentList = busiMcuTencentConferenceAppointmentMapper.selectBusiMcuTencentConferenceAppointmentByTemplateId(templateConferenceId);
        if (busiMcuTencentConferenceAppointmentList.size() > 0) {
            BusiMcuTencentConferenceAppointment busiMcuTencentConferenceAppointment = busiMcuTencentConferenceAppointmentList.get(0);
            if (busiMcuTencentConferenceAppointment.getOpsId() != null) {
                Date currentDate = new Date();
                String startTimeStr = DateUtil.convertDateToString(currentDate, "yyyy-MM-dd HH:mm:ss");
                Date endTime = DateUtils.getDiffDate(currentDate, 2, TimeUnit.DAYS);
                String endTimeStr = DateUtil.convertDateToString(endTime, "yyyy-MM-dd HH:mm:ss");
                busiMcuTencentConferenceAppointment.setStartTime(startTimeStr);
                busiMcuTencentConferenceAppointment.setEndTime(endTimeStr);
                busiMcuTencentConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                busiMcuTencentConferenceAppointment.setIsStart(1);
                busiMcuTencentConferenceAppointmentMapper.updateBusiMcuTencentConferenceAppointment(busiMcuTencentConferenceAppointment);
                conferenceContext.setAppointmentType(2);
                conferenceContext.setConferenceAppointment(busiMcuTencentConferenceAppointment);
            }
        }
        //缓存
        tc.setConfId(conferenceContext.getMeetingId());
        busiMcuTencentTemplateConferenceMapper.updateBusiMcuTencentTemplateConference(tc);
        String message = "会议【" + conferenceContext.getName() + "】启动成功！";
        Map<String, Object> obj = new HashMap<>(3);
        obj.put("startTime", conferenceContext.getStartTime());
        obj.put("message", message);
        obj.put("conferenceNumber", conferenceContext.getConferenceNumber());
        obj.put("streamingUrl", conferenceContext.getStreamingUrl());
        obj.put("streamingUrlList", conferenceContext.getStreamUrlList());
        TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, message);
        TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_STARTED, obj);
        conferenceContext.setId(EncryptIdUtil.generateConferenceId(conferenceContext.getContextKey()));
        TencentConferenceContextCache.getInstance().add(conferenceContext);
        DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext);
        conferenceContext.setAttendeeOperation(defaultAttendeeOperation);
        defaultAttendeeOperation.operate();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("allowUnmuteBySelf", conferenceContext.getAllowUnmuteBySelf());
        jsonObject.put("allowChat", conferenceContext.getAllowChat());
        jsonObject.put("autoWaitingRoom", conferenceContext.getAutoWaitingRoom());
        jsonObject.put("shareScreen", conferenceContext.getShareScreen());
        TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);


        List<AttendeeTencent> attendeeList = new ArrayList<>();
        //邀请的企业联系人
        Map<String, Object> businessProperties = tc.getBusinessProperties();
        if (businessProperties != null) {
            Object attendees = businessProperties.get("attendees");
            if (attendees != null) {
                List<RoomAttendeeTencent> userDTOS = JSONArray.parseArray(JSONObject.toJSONString(attendees), RoomAttendeeTencent.class);
                for (RoomAttendeeTencent userDTO : userDTOS) {
                    RoomAttendeeTencent roomAttendeeTencent = new RoomAttendeeTencent();
                    roomAttendeeTencent.setName(userDTO.getName());
                    roomAttendeeTencent.setOnlineStatus(Objects.equals(userDTO.getMeetingRoomStatus(), 2) ? 1 : 2);
                    roomAttendeeTencent.setId(userDTO.getMeetingRoomId());
                    roomAttendeeTencent.setAccountType(userDTO.getAccountType());
                    roomAttendeeTencent.setIsallowCall(userDTO.getIsallowCall());
                    roomAttendeeTencent.setMeetingRoomName(userDTO.getMeetingRoomName());
                    roomAttendeeTencent.setName(userDTO.getMeetingRoomName());
                    roomAttendeeTencent.setDeptId(conferenceContext.getDeptId());
                    attendeeList.add(roomAttendeeTencent);
                    logger.info("TENCENT-邀请的room:{}", userDTO);
                }
            }
        }

        doCall(conferenceContext, attendeeList);


        return conferenceContext;
    }

    public synchronized TencentConferenceContext startTemplateConference(long templateConferenceId, TencentBridge bridge) {

        logger.info("模板会议启动入口：" + templateConferenceId);

        BusiMcuTencentTemplateConferenceMapper busiMcuTencentTemplateConferenceMapper = BeanFactory.getBean(BusiMcuTencentTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuTencentTemplateConference tc = busiMcuTencentTemplateConferenceMapper.selectBusiMcuTencentTemplateConferenceById(templateConferenceId);
        IBusiMcuTencentHistoryConferenceService busiMcutencentHistoryConferenceService = BeanFactory.getBean(IBusiMcuTencentHistoryConferenceService.class);
        if (tc == null) {
            return null;
        }
        // 获取会议上下文
        TencentConferenceContext conferenceContext = buildTemplateConferenceContext(templateConferenceId,bridge);
        if (conferenceContext == null) {
            return null;
        }
        // 启动前校验会议是否已开始
        if (conferenceContext.isStart()) {
            throw new SystemException(1009874, "会议已开始，请勿重复开始");
        }

        if (tc.getLastConferenceId() != null) {
            BusiHistoryConference busiHistoryConference = busiMcutencentHistoryConferenceService.selectBusiHistoryConferenceById(Long.valueOf(tc.getLastConferenceId()));
            if (busiHistoryConference != null) {
                Date endTime = busiHistoryConference.getConferenceEndTime();
                if (endTime != null) {
                    long diff = System.currentTimeMillis() - endTime.getTime();
                    if (diff < 10000) {
                        throw new SystemException(1009874, "该会议室刚结束会议，正在准备中，请等待" + (10 - diff / 1000) + "秒");
                    }
                }
            }
        }

        TencentMeetingClient meeting_client = bridge.getMEETING_CLIENT();
        TencentUserClient user_client = bridge.getUSER_CLIENT();

        String confId = tc.getConfId();
        if (Strings.isNotBlank(confId)) {
            //查询
            try {

                QueryMeetingByIdRequest queryMeetingByIdRequest1 = new QueryMeetingByIdRequest();
                queryMeetingByIdRequest1.setMeetingId(tc.getConfId());
                queryMeetingByIdRequest1.setUserId(bridge.getTencentUserId());
                queryMeetingByIdRequest1.setInstanceId(1);
                QueryMeetingDetailResponse queryMeetingDetailResponse1 = meeting_client.queryMeetingById(queryMeetingByIdRequest1);


                List<MeetingInfo> meetingInfoList =  queryMeetingDetailResponse1.getMeetingInfoList();

                if (!CollectionUtils.isEmpty(meetingInfoList)) {
                    for (MeetingInfo meetingInfo : meetingInfoList) {
                        if (Objects.equals(meetingInfo.getStatus(), TencentMeetingStateEnum.MEETING_STATE_STARTED.name()) ||
                                Objects.equals(meetingInfo.getStatus(), TencentMeetingStateEnum.MEETING_STATE_INIT.name())) {

                            conferenceContext.setStart(true);
                            conferenceContext.setStartTime(new Date());

                            conferenceContext.setMeetingId(meetingInfo.getMeetingId());
                            conferenceContext.setConferenceNumber(meetingInfo.getMeetingCode());
                            conferenceContext.setConferenceRemoteParty(meetingInfo.getMeetingCode() + "@" + conferenceContext.getMcuCallIp());
                            tc.setConferenceNumber(Long.valueOf(meetingInfo.getMeetingCode()));

                            conferenceContext.setAllowUnmuteBySelf(meetingInfo.getSettings().getAllowUnmuteSelf());
                            QueryUserMsOpenIdRequest queryUserMsOpenIdRequest = new QueryUserMsOpenIdRequest();
                            queryUserMsOpenIdRequest.setMeetingId(conferenceContext.getMeetingId());
                            queryUserMsOpenIdRequest.setOperatorId(conferenceContext.getTencentUser());
                            queryUserMsOpenIdRequest.setOperatorIdType(1);
                            try {
                                QueryUserMsOpenIdResponse msOpenIdResponse = user_client.queryUserMsOpenId(queryUserMsOpenIdRequest);
                                String msOpenId = msOpenIdResponse.getMsOpenId();
                                conferenceContext.setMsopenid(msOpenId);
                            } catch (WemeetSdkException e) {
                                logger.info("conferenceContext setMsopenid error" + e.getMessage());
                            }
                            conferenceContext.setShareScreen(true);
                            conferenceContext.setAllowChat(0);
                            conferenceContext.setAutoWaitingRoom(meetingInfo.getSettings().getAutoInWaitingRoom());
                        }else {
                            createConference(tc, conferenceContext, bridge, meeting_client, user_client);
                        }
                    }
                }
            } catch (WemeetSdkException e) {
                throw new CustomException("开始会议失败" + e.getMessage());
            }

        }else {
            //创建会议
            createConference(tc, conferenceContext, bridge, meeting_client, user_client);
        }





        saveHistory(busiMcutencentHistoryConferenceService, tc, conferenceContext);
        //缓存
        tc.setConfId(conferenceContext.getMeetingId());
        busiMcuTencentTemplateConferenceMapper.updateBusiMcuTencentTemplateConference(tc);
        String message = "会议【" + conferenceContext.getName() + "】启动成功！";
        Map<String, Object> obj = new HashMap<>(3);
        obj.put("startTime", conferenceContext.getStartTime());
        obj.put("message", message);
        obj.put("conferenceNumber", conferenceContext.getConferenceNumber());
        obj.put("streamingUrl", conferenceContext.getStreamingUrl());
        obj.put("streamingUrlList", conferenceContext.getStreamUrlList());
        TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, message);
        TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_STARTED, obj);
        conferenceContext.setId(EncryptIdUtil.generateConferenceId(conferenceContext.getContextKey()));
        TencentConferenceContextCache.getInstance().add(conferenceContext);
        DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext);
        conferenceContext.setAttendeeOperation(defaultAttendeeOperation);
        defaultAttendeeOperation.operate();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("allowUnmuteBySelf", conferenceContext.getAllowUnmuteBySelf());
        jsonObject.put("allowChat", conferenceContext.getAllowChat());
        jsonObject.put("autoWaitingRoom", conferenceContext.getAutoWaitingRoom());
        jsonObject.put("shareScreen", conferenceContext.getShareScreen());
        TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);


        List<AttendeeTencent> attendeeList = new ArrayList<>();
        //邀请的企业联系人
        Map<String, Object> businessProperties = tc.getBusinessProperties();
        if (businessProperties != null) {
            Object attendees = businessProperties.get("attendees");
            if (attendees != null) {
                List<RoomAttendeeTencent> userDTOS = JSONArray.parseArray(JSONObject.toJSONString(attendees), RoomAttendeeTencent.class);
                for (RoomAttendeeTencent userDTO : userDTOS) {
                    RoomAttendeeTencent roomAttendeeTencent = new RoomAttendeeTencent();
                    roomAttendeeTencent.setName(userDTO.getName());
                    roomAttendeeTencent.setOnlineStatus(Objects.equals(userDTO.getMeetingRoomStatus(), 2) ? 1 : 2);
                    roomAttendeeTencent.setId(userDTO.getMeetingRoomId());
                    roomAttendeeTencent.setAccountType(userDTO.getAccountType());
                    roomAttendeeTencent.setIsallowCall(userDTO.getIsallowCall());
                    roomAttendeeTencent.setMeetingRoomName(userDTO.getMeetingRoomName());
                    roomAttendeeTencent.setName(userDTO.getMeetingRoomName());
                    roomAttendeeTencent.setDeptId(conferenceContext.getDeptId());
                    attendeeList.add(roomAttendeeTencent);
                    logger.info("TENCENT-邀请的room:{}", userDTO);
                }
            }
        }

        doCall(conferenceContext, attendeeList);


        return conferenceContext;
    }

    private void createConference(BusiMcuTencentTemplateConference tc, TencentConferenceContext conferenceContext, TencentBridge bridge, TencentMeetingClient meeting_client, TencentUserClient user_client) {
        CreateMeetingRequestLocal request = new CreateMeetingRequestLocal();
        request.setUserId(bridge.getTencentUserId());
        request.setInstanceId(InstanceEnum.INSTANCE_MAC.getInstanceID());
        request.setSubject(tc.getName());
        request.setType(0);
        request.setInstanceId(1);
        request.setEnableLive(tc.getStreamingEnabled() != null && tc.getStreamingEnabled() == 1);

        if (tc.getGuestPassword() != null && Strings.isNotBlank(tc.getGuestPassword())) {
            request.setPassword(tc.getGuestPassword());
        }
        if (tc.getChairmanPassword() != null && Strings.isNotBlank(tc.getChairmanPassword())) {
            request.setHostKey(tc.getChairmanPassword());
            request.setEnableHostkey(true);
        } else {
            request.setEnableHostkey(false);
        }

        String liveConfig = null;
        String settings = null;
        Map<String, Object> businessProperties = tc.getBusinessProperties();
        if (businessProperties != null) {
            liveConfig = businessProperties.get("liveConfig") == null ? null : (String) businessProperties.get("liveConfig");
            settings = businessProperties.get("settings") == null ? null : (String) businessProperties.get("settings");
        }

        if (Strings.isNotBlank(liveConfig)) {
            JSONObject jsonObject = JSONObject.parseObject(liveConfig);
            LiveConfig liveConfig1 = JSONObject.toJavaObject(jsonObject, LiveConfig.class);
            request.setLiveConfig(liveConfig1);
        }
        request.setEnableLive(tc.getStreamingEnabled() != null && tc.getStreamingEnabled() == 1);
        if (Strings.isNotBlank(settings)) {
            JSONObject jsonObject = JSONObject.parseObject(settings);
            MeetingSetting meetingSetting = JSONObject.toJavaObject(jsonObject, MeetingSetting.class);
            request.setSettings(meetingSetting);
        } else {
            MeetingSetting meetingSetting = new MeetingSetting();
            if (tc.getRecordingEnabled() != null && tc.getRecordingEnabled() == 1) {
                meetingSetting.setAutoRecordType(AutoRecordType.CLOUD.name().toLowerCase());
                meetingSetting.setParticipantJoinAutoRecord(true);
                meetingSetting.setEnableHostPauseAutoRecord(true);

            } else {
                meetingSetting.setAutoRecordType(AutoRecordType.NONE.name().toLowerCase());
            }

            meetingSetting.setMuteEnableJoin(tc.getMuteType() != null && tc.getMuteType() == 1);

            request.setSettings(meetingSetting);
        }
        long startTime = System.currentTimeMillis() / 1000;
        request.setStartTime(startTime + "");
        Integer durationTime = tc.getDurationTime() == null ? 120 * 60 : tc.getDurationTime() * 60;
        long endTime = startTime + durationTime;
        request.setEndTime(endTime + "");
        QueryMeetingDetailResponse response;
        try {
            response = meeting_client.createMeeting(request);
            if (response == null) {
                throw new CustomException("开始会议失败");
            }
        } catch (WemeetSdkException e) {
            throw new CustomException("开始会议失败" + e.getMessage());
        }

        conferenceContext.setStart(true);
        conferenceContext.setStartTime(new Date());

        conferenceContext.setEndTime(new Date(endTime * 1000));


        List<MeetingInfo> meetingInfoList = response.getMeetingInfoList();
        MeetingInfo meetingInfo = meetingInfoList.get(0);
        conferenceContext.setMeetingId(meetingInfo.getMeetingId());
        conferenceContext.setConferenceNumber(meetingInfo.getMeetingCode());
        conferenceContext.setConferenceRemoteParty(meetingInfo.getMeetingCode() + "@" + conferenceContext.getMcuCallIp());
        tc.setConferenceNumber(Long.valueOf(meetingInfo.getMeetingCode()));
        conferenceContext.setJoinUrl(meetingInfo.getJoinUrl());

        conferenceContext.setAllowUnmuteBySelf(meetingInfo.getSettings().getAllowUnmuteSelf());
        QueryUserMsOpenIdRequest queryUserMsOpenIdRequest = new QueryUserMsOpenIdRequest();
        queryUserMsOpenIdRequest.setMeetingId(conferenceContext.getMeetingId());
        queryUserMsOpenIdRequest.setOperatorId(conferenceContext.getTencentUser());
        queryUserMsOpenIdRequest.setOperatorIdType(1);
        try {
            QueryUserMsOpenIdResponse msOpenIdResponse = user_client.queryUserMsOpenId(queryUserMsOpenIdRequest);
            String msOpenId = msOpenIdResponse.getMsOpenId();
            conferenceContext.setMsopenid(msOpenId);
        } catch (WemeetSdkException e) {
            logger.info("conferenceContext setMsopenid error" + e.getMessage());
        }
        conferenceContext.setShareScreen(true);
        conferenceContext.setAllowChat(0);
        conferenceContext.setAutoWaitingRoom(meetingInfo.getSettings().getAutoInWaitingRoom());
    }


    private void saveHistory(IBusiMcuTencentHistoryConferenceService busiMcuTencentHistoryConferenceService, BusiMcuTencentTemplateConference tc, TencentConferenceContext conferenceContext) {
        // 保存历史记录
        String callId = UUID.randomUUID().toString();
        // 保存历史记录
        BusiHistoryConference busiHistoryConference = busiMcuTencentHistoryConferenceService.saveHistory(conferenceContext);
        conferenceContext.setHistoryConference(busiHistoryConference);
        tc.setLastConferenceId(String.valueOf(busiHistoryConference.getId()));
        //历史call保存
        BusiHistoryCall busiHistoryCall = new BusiHistoryCall();
        busiHistoryCall.setCallId(callId);
        busiHistoryCall.setCoSpace(conferenceContext.getCoSpaceId());
        busiHistoryCall.setDeptId(conferenceContext.getDeptId());
        busiHistoryCall.setCreateTime(new Date());
        busiHistoryCall.setHistoryConferenceId(busiHistoryConference.getId());
        BeanFactory.getBean(BusiHistoryCallMapper.class).insertBusiHistoryCall(busiHistoryCall);
    }

    private void doCall(TencentConferenceContext conferenceContext, List<AttendeeTencent> attendeeList) {
        try {
            TencentDelayTaskService delayTaskService = BeanFactory.getBean(TencentDelayTaskService.class);
            InviteAttendeeTencentTask inviteAttendeesTask = new InviteAttendeeTencentTask(conferenceContext.getConferenceNumber(), 200, conferenceContext, attendeeList);
            delayTaskService.addTask(inviteAttendeesTask);
        } catch (Exception e) {
            logger.error("呼叫与会者发生异常-doCall：", e);
            TencentWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, e.getMessage());
        }
    }


    public synchronized String createConferenceNumber(long templateConferenceId, String startTime) {
        logger.info("模板会议启动入口：" + templateConferenceId);

        BusiMcuTencentTemplateConferenceMapper busiMcuTencentTemplateConferenceMapper = BeanFactory.getBean(BusiMcuTencentTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuTencentTemplateConference tc = busiMcuTencentTemplateConferenceMapper.selectBusiMcuTencentTemplateConferenceById(templateConferenceId);
        if (tc == null) {
            return null;
        }
        // 获取会议上下文
        TencentConferenceContext conferenceContext = buildTemplateConferenceContext(templateConferenceId);
        if (conferenceContext == null) {
            return null;
        }
        // 启动前校验会议是否已开始
        if (conferenceContext.isStart()) {
            throw new SystemException(1009874, "会议已开始，请勿重复开始");
        }

        //TencentBridge bridge = TencentBridgeCache.getInstance().getAvailableBridgesByDept(tc.getDeptId());
        TencentBridge bridge = conferenceContext.getTencentBridge();
        if (bridge == null) {
            return null;
        }

        Long conferenceNumber = tc.getConferenceNumber();
        if (conferenceNumber == null) {

            TencentMeetingClient meeting_client = bridge.getMEETING_CLIENT();
            TencentUserClient user_client = bridge.getUSER_CLIENT();
            //创建会议
            CreateMeetingRequestLocal request = new CreateMeetingRequestLocal();
            request.setUserId(bridge.getTencentUserId());
            request.setInstanceId(InstanceEnum.INSTANCE_MAC.getInstanceID());
            request.setSubject(tc.getName());
            request.setType(0);
            request.setInstanceId(1);
            request.setEnableLive(tc.getStreamingEnabled() != null && tc.getStreamingEnabled() == 1);

            if (tc.getGuestPassword() != null && Strings.isNotBlank(tc.getGuestPassword())) {
                request.setPassword(tc.getGuestPassword());
            }
            if (tc.getChairmanPassword() != null && Strings.isNotBlank(tc.getChairmanPassword())) {
                request.setHostKey(tc.getChairmanPassword());
                request.setEnableHostkey(true);
            } else {
                request.setEnableHostkey(false);
            }

            String liveConfig = null;
            String settings = null;
            Map<String, Object> businessProperties = tc.getBusinessProperties();
            if (businessProperties != null) {
                liveConfig = businessProperties.get("liveConfig") == null ? null : (String) businessProperties.get("liveConfig");
                settings = businessProperties.get("settings") == null ? null : (String) businessProperties.get("settings");
            }

            if (Strings.isNotBlank(liveConfig)) {
                JSONObject jsonObject = JSONObject.parseObject(liveConfig);
                LiveConfig liveConfig1 = JSONObject.toJavaObject(jsonObject, LiveConfig.class);
                request.setLiveConfig(liveConfig1);
            }
            request.setEnableLive(tc.getStreamingEnabled() != null && tc.getStreamingEnabled() == 1);
            if (Strings.isNotBlank(settings)) {
                JSONObject jsonObject = JSONObject.parseObject(settings);
                MeetingSetting meetingSetting = JSONObject.toJavaObject(jsonObject, MeetingSetting.class);
                request.setSettings(meetingSetting);
            } else {
                MeetingSetting meetingSetting = new MeetingSetting();
                if (tc.getRecordingEnabled() != null && tc.getRecordingEnabled() == 1) {
                    meetingSetting.setAutoRecordType(AutoRecordType.CLOUD.name().toLowerCase());
                    meetingSetting.setParticipantJoinAutoRecord(true);
                    meetingSetting.setEnableHostPauseAutoRecord(true);

                } else {
                    meetingSetting.setAutoRecordType(AutoRecordType.NONE.name().toLowerCase());
                }

                meetingSetting.setMuteEnableJoin(tc.getMuteType() != null && tc.getMuteType() == 1);

                request.setSettings(meetingSetting);
            }
            long startTimeLong = DateUtil.convertDateByString(startTime, null).getTime()/1000;
            request.setStartTime(startTimeLong + "");
            Integer durationTime = tc.getDurationTime() == null ? 120 * 60 : tc.getDurationTime() * 60;
            long endTime = startTimeLong + durationTime;
            request.setEndTime(endTime + "");
            QueryMeetingDetailResponse response;
            try {
                response = meeting_client.createMeeting(request);
                if (response == null) {
                    throw new CustomException("预约会议失败");
                }
            } catch (WemeetSdkException e) {
                if (e.getMessage().contains("无权限")) {
                    throw new CustomException("预约会议失败：无权限预定会议。");
                }
                throw new CustomException("预约会议失败：" + e.getMessage());
            }

            List<MeetingInfo> meetingInfoList = response.getMeetingInfoList();
            MeetingInfo meetingInfo = meetingInfoList.get(0);
            conferenceContext.setMeetingId(meetingInfo.getMeetingId());
            conferenceContext.setConferenceNumber(meetingInfo.getMeetingCode());
            conferenceContext.setConferenceRemoteParty(meetingInfo.getMeetingCode() + "@" + conferenceContext.getMcuCallIp());
            tc.setConferenceNumber(Long.valueOf(meetingInfo.getMeetingCode()));

            conferenceContext.setAllowUnmuteBySelf(meetingInfo.getSettings().getAllowUnmuteSelf());
            QueryUserMsOpenIdRequest queryUserMsOpenIdRequest = new QueryUserMsOpenIdRequest();
            queryUserMsOpenIdRequest.setMeetingId(conferenceContext.getMeetingId());
            queryUserMsOpenIdRequest.setOperatorId(conferenceContext.getTencentUser());
            queryUserMsOpenIdRequest.setOperatorIdType(1);
            try {
                QueryUserMsOpenIdResponse msOpenIdResponse = user_client.queryUserMsOpenId(queryUserMsOpenIdRequest);
                String msOpenId = msOpenIdResponse.getMsOpenId();
                conferenceContext.setMsopenid(msOpenId);
            } catch (WemeetSdkException e) {
                logger.info("conferenceContext setMsopenid error" + e.getMessage());
            }
            conferenceContext.setShareScreen(true);
            conferenceContext.setAllowChat(0);
            conferenceContext.setAutoWaitingRoom(meetingInfo.getSettings().getAutoInWaitingRoom());
            tc.setConfId(conferenceContext.getMeetingId());
            busiMcuTencentTemplateConferenceMapper.updateBusiMcuTencentTemplateConference(tc);
            return meetingInfo.getMeetingCode();
        }

        return conferenceNumber + "";

    }


    public synchronized void editeTencentConference(long templateConferenceId, String startTime) {
        logger.info("模板会议启动入口：" + templateConferenceId);

        BusiMcuTencentTemplateConferenceMapper busiMcuTencentTemplateConferenceMapper = BeanFactory.getBean(BusiMcuTencentTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuTencentTemplateConference tc = busiMcuTencentTemplateConferenceMapper.selectBusiMcuTencentTemplateConferenceById(templateConferenceId);
        if (tc == null) {
            return;
        }
        // 获取会议上下文
        TencentConferenceContext conferenceContext = buildTemplateConferenceContext(templateConferenceId);
        if (conferenceContext == null) {
            return;
        }
        // 启动前校验会议是否已开始
        if (conferenceContext.isStart()) {
            throw new SystemException(1009874, "会议已开始，请勿重复开始");
        }

        //TencentBridge bridge = TencentBridgeCache.getInstance().getAvailableBridgesByDept(tc.getDeptId());
        TencentBridge bridge = conferenceContext.getTencentBridge();
        if (bridge == null) {
            return;
        }

        String confId = tc.getConfId();
        if (Strings.isNotBlank(confId)) {

            TencentMeetingClient meeting_client = bridge.getMEETING_CLIENT();

            //创建会议
            ModifyMeetingRequest request = new ModifyMeetingRequest();
            request.setMeetingId(tc.getConfId());
            request.setUserId(bridge.getTencentUserId());
            request.setInstanceId(InstanceEnum.INSTANCE_MAC.getInstanceID());
            request.setSubject(tc.getName());
            request.setInstanceId(1);
            request.setEnableLive(tc.getStreamingEnabled() != null && tc.getStreamingEnabled() == 1);

            if (tc.getGuestPassword() != null && Strings.isNotBlank(tc.getGuestPassword())) {
                request.setPassword(tc.getGuestPassword());
            }


            String liveConfig = null;
            String settings = null;
            Map<String, Object> businessProperties = tc.getBusinessProperties();
            if (businessProperties != null) {
                liveConfig = businessProperties.get("liveConfig") == null ? null : (String) businessProperties.get("liveConfig");
                settings = businessProperties.get("settings") == null ? null : (String) businessProperties.get("settings");
            }

            if (Strings.isNotBlank(liveConfig)) {
                JSONObject jsonObject = JSONObject.parseObject(liveConfig);
                LiveConfig liveConfig1 = JSONObject.toJavaObject(jsonObject, LiveConfig.class);
                request.setLiveConfig(liveConfig1);
            }
            request.setEnableLive(tc.getStreamingEnabled() != null && tc.getStreamingEnabled() == 1);
            if (Strings.isNotBlank(settings)) {
                JSONObject jsonObject = JSONObject.parseObject(settings);
                MeetingSetting meetingSetting = JSONObject.toJavaObject(jsonObject, MeetingSetting.class);
                request.setSettings(meetingSetting);
            } else {
                MeetingSetting meetingSetting = new MeetingSetting();
                if (tc.getRecordingEnabled() != null && tc.getRecordingEnabled() == 1) {
                    meetingSetting.setAutoRecordType(AutoRecordType.CLOUD.name().toLowerCase());
                    meetingSetting.setParticipantJoinAutoRecord(true);
                    meetingSetting.setEnableHostPauseAutoRecord(true);

                } else {
                    meetingSetting.setAutoRecordType(AutoRecordType.NONE.name().toLowerCase());
                }

                meetingSetting.setMuteEnableJoin(tc.getMuteType() != null && tc.getMuteType() == 1);

                request.setSettings(meetingSetting);
            }
            long startTimeLong = DateUtil.convertDateByString(startTime, null).getTime()/1000;
            request.setStartTime(startTimeLong + "");
            Integer durationTime = tc.getDurationTime() == null ? 120 * 60 : tc.getDurationTime() * 60;
            long endTime = startTimeLong + durationTime;
            request.setEndTime(endTime + "");
            QueryMeetingDetailResponse response;
            try {
                response = meeting_client.modifyMeeting(request);
                if (response == null) {
                    throw new CustomException("修改预约会议失败");
                }
            } catch (WemeetSdkException e) {
                throw new CustomException("修改预约会议失败" + e.getMessage());
            }
          
        }

    }

    public synchronized void deleteConference(long templateConferenceId) {
        logger.info("模板会议启动入口：" + templateConferenceId);

        BusiMcuTencentTemplateConferenceMapper busiMcuTencentTemplateConferenceMapper = BeanFactory.getBean(BusiMcuTencentTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuTencentTemplateConference tc = busiMcuTencentTemplateConferenceMapper.selectBusiMcuTencentTemplateConferenceById(templateConferenceId);
        if (tc == null) {
            return;
        }
        // 获取会议上下文
        TencentConferenceContext conferenceContext = buildTemplateConferenceContext(templateConferenceId);
        if (conferenceContext == null) {
            return;
        }
        // 启动前校验会议是否已开始
        if (conferenceContext.isStart()) {
            return;
        }

        //TencentBridge bridge = TencentBridgeCache.getInstance().getAvailableBridgesByDept(tc.getDeptId());
        TencentBridge bridge = conferenceContext.getTencentBridge();
        if (bridge == null) {
            return;
        }


        String confId = tc.getConfId();

        if (confId != null) {
            TencentMeetingClient meeting_client = bridge.getMEETING_CLIENT();
            try {
                TencentDismissMeetingRequest dismissMeetingRequest = new TencentDismissMeetingRequest();
                dismissMeetingRequest.setMeetingId(confId);
                dismissMeetingRequest.setUserId(bridge.getTencentUserId());
                dismissMeetingRequest.setInstanceId(1);
                dismissMeetingRequest.setReasonCode(1);
                meeting_client.dismissMeeting(dismissMeetingRequest);
            } catch (WemeetSdkException e) {
                throw new CustomException("删除预约会议失败");
            }

            try {
                CancelMeetingRequest cancelMeetingRequest = new CancelMeetingRequest();
                cancelMeetingRequest.setMeetingId(confId);
                cancelMeetingRequest.setUserId(bridge.getTencentUserId());
                cancelMeetingRequest.setInstanceId(1);
                cancelMeetingRequest.setReasonCode(1);
                meeting_client.cancelMeeting(cancelMeetingRequest);
            } catch (Exception e) {
                throw new CustomException("删除预约会议失败");
            }
        }
    }

}
