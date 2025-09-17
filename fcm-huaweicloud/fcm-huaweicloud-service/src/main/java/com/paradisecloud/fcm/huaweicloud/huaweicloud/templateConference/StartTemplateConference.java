package com.paradisecloud.fcm.huaweicloud.huaweicloud.templateConference;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huaweicloud.sdk.meeting.v1.model.*;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.uuid.UUID;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryCallMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuHwcloudTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryCall;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudTemplateConference;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.AttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.CropDirAttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.*;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.*;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiMcuHwcloudHistoryConferenceService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.task.HwcloudDelayTaskService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.task.InviteAttendeeHwcloudTask;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.utils.HwcloudConferenceContextUtils;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.client.HwcloudMeetingWebsocketReconnecter;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.*;

/**
 * @author nj
 * @date 2023/4/20 11:53
 */
public class StartTemplateConference extends BuildTemplateConferenceContext {

    Logger logger = LoggerFactory.getLogger(getClass());

    public synchronized HwcloudConferenceContext startTemplateConference(long templateConferenceId) {

        logger.info("模板会议启动入口：" + templateConferenceId);

        BusiMcuHwcloudTemplateConferenceMapper busiMcuHwcloudTemplateConferenceMapper = BeanFactory.getBean(BusiMcuHwcloudTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuHwcloudTemplateConference tc = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(templateConferenceId);
        IBusiMcuHwcloudHistoryConferenceService busiMcuHwcloudHistoryConferenceService = BeanFactory.getBean(IBusiMcuHwcloudHistoryConferenceService.class);
        if (tc == null) {
            return null;
        }
        // 获取会议上下文
        HwcloudConferenceContext conferenceContext = buildTemplateConferenceContext(templateConferenceId);
        if (conferenceContext == null) {
            return null;
        }
        // 启动前校验会议是否已开始
        if (conferenceContext.isStart()) {
            throw new SystemException(1009874, "会议已开始，请勿重复开始");
        }

        HwcloudBridge bridge = HwcloudBridgeCache.getInstance().getAvailableBridgesByDept(tc.getDeptId());
        if (bridge == null) {
            return null;
        }
        HwcloudMeetingBridge hwcloudMeetingBridge = new HwcloudMeetingBridge(bridge);

        String confId = tc.getConfId();

        if (confId == null) {

            try {
                RestConfConfigDTO restConfConfigDTO = new RestConfConfigDTO();

                restConfConfigDTO.setIsAutoMute(tc.getMuteType() == 1);
                restConfConfigDTO.setProlongLength(60);


                Map<String, Object> businessProperties = tc.getBusinessProperties();
                if (businessProperties != null) {
                    Object isSendNotify = businessProperties.get("isSendNotify");
                    if (isSendNotify != null) {
                        restConfConfigDTO.setIsSendNotify((Boolean) isSendNotify);
                    }
                    Object isSendCalendar = businessProperties.get("isSendCalendar");
                    if (isSendCalendar != null) {
                        restConfConfigDTO.setIsSendCalendar((Boolean) isSendCalendar);
                    }
                    Object callInRestriction = businessProperties.get("callInRestriction");
                    if (callInRestriction != null) {
                        restConfConfigDTO.setCallInRestriction((Integer) callInRestriction);
                    }
                    Object enableWaitingRoom = businessProperties.get("enableWaitingRoom");
                    if (enableWaitingRoom != null) {
                        restConfConfigDTO.setEnableWaitingRoom((Boolean) enableWaitingRoom);
                    }
                    Object allowGuestStartConf = businessProperties.get("allowGuestStartConf");
                    if (allowGuestStartConf != null) {
                        restConfConfigDTO.setAllowGuestStartConf((Boolean) allowGuestStartConf);
                    }
                }
                if (tc.getConferencePassword() != null && Strings.isNotBlank(tc.getConferencePassword())) {
                    restConfConfigDTO.setGuestPwd(tc.getConferencePassword());
                } else {
                    restConfConfigDTO.setGuestPwd("");
                    restConfConfigDTO.setIsGuestFreePwd(true);
                }

                Integer autoRecord = 0;

                if (tc.getRecordingEnabled() != null && tc.getRecordingEnabled() == 1) {
                    autoRecord = 1;
                }

                List<RestAttendeeDTO> attendees = new ArrayList<>();
                //会议开始时间（UTC时间） yyyy-MM-dd HH:mm 创建预约会议时，如果没有指定开始时间或填空串，则表示会议马上开始
                //时间是UTC时间，即0时区的时间
                ConferenceInfo meetingInfo = hwcloudMeetingBridge.getMeetingManager().createMeeting(tc.getName(), "", tc.getDurationTime(), "",
                        tc.getMaxParticipantNum() == null ? 500 : tc.getMaxParticipantNum(), autoRecord, restConfConfigDTO, attendees);
                String accessNumber = meetingInfo.getConferenceID();
                conferenceContext.setConferenceNumber(accessNumber);
                List<PasswordEntry> passwordEntry = meetingInfo.getPasswordEntry();
                String hostPassword = "";
                for (PasswordEntry entry : passwordEntry) {
                    String conferenceRole = entry.getConferenceRole();
                    if (Objects.equals(conferenceRole, "chair")) {
                        hostPassword = entry.getPassword();
                        conferenceContext.setChairmanPassword(hostPassword);
                        tc.setChairmanPassword(hostPassword);
                        break;
                    }
                }
                //激活会议
                hwcloudMeetingBridge.getMeetingManager().startMeeting(meetingInfo.getConferenceID(), hostPassword);
                conferenceContext.setMeetingId(meetingInfo.getConferenceID());
                conferenceContext.setMeetingUUID(meetingInfo.getConfUUID());
                Date startDate = new Date();
                Date endDate = DateUtils.addMinutes(startDate, tc.getDurationTime().intValue());
                conferenceContext.setEndTime(endDate);

                settingBridge(hwcloudMeetingBridge, meetingInfo.getConferenceID(), hostPassword);

                tc.setConfId(accessNumber);
                busiMcuHwcloudTemplateConferenceMapper.updateBusiMcuHwcloudTemplateConference(tc);


            } catch (Exception e) {
                logger.error("开始会议失败" + e.getMessage());
                throw new CustomException("开始会议失败" + e.getMessage());
            }

        } else {
            //查询在线会议列表
            SearchOnlineMeetingsResponse searchOnlineMeetingsResponse = hwcloudMeetingBridge.getMeetingManager().searchOnlineMeeting(confId);
            if (searchOnlineMeetingsResponse != null && CollectionUtils.isNotEmpty(searchOnlineMeetingsResponse.getData())) {
                List<ConferenceInfo> data = searchOnlineMeetingsResponse.getData();
                for (ConferenceInfo conferenceInfo : data) {
                    List<PasswordEntry> passwordEntry1 = conferenceInfo.getPasswordEntry();
                    String password = passwordEntry1.get(0).getPassword();
                    conferenceContext.setChairmanPassword(password);
                    conferenceContext.setMeetingUUID(conferenceInfo.getConfUUID());
                    conferenceContext.setConferenceNumber(confId);
                    conferenceContext.setMeetingId(confId);

                    String endTime = conferenceInfo.getEndTime();
                    try {
                        Date date = DateUtils.parseDate(endTime, "yyyy-MM-dd HH:mm");
                        conferenceContext.setEndTime(date);
                    } catch (ParseException e) {
                    }

                }
            }else {
                hwcloudMeetingBridge.getMeetingManager().startMeeting(confId, tc.getChairmanPassword());

                conferenceContext.setChairmanPassword(tc.getChairmanPassword());
                conferenceContext.setConferenceNumber(confId);
                conferenceContext.setMeetingId(confId);
                Date startDate = new Date();
                Date endDate = DateUtils.addMinutes(startDate, tc.getDurationTime().intValue());
                conferenceContext.setEndTime(endDate);
                settingBridge(hwcloudMeetingBridge, confId,  tc.getChairmanPassword());
            }
        }

        List<AttendeeHwcloud> attendeeHwcloudList = new ArrayList<>();
        HwcloudConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            if (a instanceof CropDirAttendeeHwcloud) {
                if (!a.isMeetingJoined()) {
                    attendeeHwcloudList.add(a);

                }

            }

        });
        doCall(conferenceContext, attendeeHwcloudList);
        Map<String, Object> businessProperties = tc.getBusinessProperties();

        conferenceContext.setStart(true);
        conferenceContext.setStartTime(new Date());

        saveHistory(busiMcuHwcloudHistoryConferenceService, tc, conferenceContext);
        //缓存
        String message = "会议【" + conferenceContext.getName() + "】启动成功！";
        Map<String, Object> obj = new HashMap<>(3);
        obj.put("startTime", conferenceContext.getStartTime());
        obj.put("endTime", conferenceContext.getEndTime());
        obj.put("message", message);
        obj.put("conferenceNumber", conferenceContext.getConferenceNumber());
        obj.put("streamingUrl", conferenceContext.getStreamingUrl());
        obj.put("streamingUrlList", conferenceContext.getStreamUrlList());
        HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, message);
        HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_STARTED, obj);
        conferenceContext.setId(EncryptIdUtil.generateConferenceId(conferenceContext.getContextKey()));
        HwcloudConferenceContextCache.getInstance().add(conferenceContext);


        HwcloudMeetingWebsocketReconnecter.getInstance().add(hwcloudMeetingBridge);
        conferenceContext.setHwcloudMeetingBridge(hwcloudMeetingBridge);


        //多画面设置

        Object confPresetParam = businessProperties.get("confPresetParam");
        if (confPresetParam != null) {
            ConfPresetParamDTO confPresetParamDTO = new ConfPresetParamDTO();
            Map<String, Object> objectMap = (Map<String, Object>) confPresetParam;
            Object presetMultiPics = objectMap.get("presetMultiPics");
            List<PresetMultiPicReqDto> presetMultiPicReqDto = JSONArray.parseArray(JSONObject.toJSONString(presetMultiPics), PresetMultiPicReqDto.class);
            confPresetParamDTO.setPresetMultiPics(presetMultiPicReqDto);
            conferenceContext.setConfPresetParam(confPresetParamDTO);
            //转换结构
            PicB picB = picAtoB(presetMultiPicReqDto);
            List<MultiPicDisplayVo> picLayouts = picB.getPicLayouts();
            if (CollectionUtils.isNotEmpty(picLayouts)) {
                //保存多画面
                for (MultiPicDisplayVo picLayout : picLayouts) {
                    RestPicLayout restPicLayout = new RestPicLayout();
                    restPicLayout.setImageType(picLayout.getImageType());
                    restPicLayout.setSwitchTime(picLayout.getSwitchTime());
                    restPicLayout.setLayOutName(picLayout.getName());
                    restPicLayout.setUuid(UUID.randomUUID().toString());
                    restPicLayout.setSubscriberInPics(picLayout.getSubscriberInPics());
                    hwcloudMeetingBridge.getMeetingControl().saveLayout(hwcloudMeetingBridge.getTokenInfo().getToken(), hwcloudMeetingBridge.getConfID(), restPicLayout);
                }
            }
            List<MultiPicDisplayVo> picDisplay = picB.getPicDisplay();
            for (MultiPicDisplayVo multiPicDisplayVo : picDisplay) {
                //自定义多画面
                DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext, JSON.parseObject(JSON.toJSONString(multiPicDisplayVo)), !multiPicDisplayVo.getAutoApplyMultiPic());
                conferenceContext.setAttendeeOperation(defaultAttendeeOperation);
                defaultAttendeeOperation.operate();
                PresetMultiPicReqDto multiPicReqDto = presetMultiPicReqDto.stream().filter(p -> p.getAutoEffect()).findFirst().get();
                conferenceContext.setMultiPicInfo(multiPicReqDto);

            }
        }

        return conferenceContext;
    }

    private void settingBridge(HwcloudMeetingBridge hwcloudMeetingBridge, String confId, String password) {
        CreateConfTokenResponse confToken = hwcloudMeetingBridge.getMeetingControl().createConfTokenResponse(confId, password);
        hwcloudMeetingBridge.setHostPassword(password);
        hwcloudMeetingBridge.setConfID(confId);
        hwcloudMeetingBridge.setTokenInfo(confToken.getData());
    }

    private PicB picAtoB(List<PresetMultiPicReqDto> presetMultiPicReqDto) {

        PicB picB = new PicB();
        List<MultiPicDisplayVo> picLayouts = new ArrayList();
        List<MultiPicDisplayVo> picDisplay = new ArrayList();

        picB.setPicLayouts(picLayouts);
        picB.setPicDisplay(picDisplay);

        for (PresetMultiPicReqDto multiPicReqDto : presetMultiPicReqDto) {

            Boolean autoEffect = multiPicReqDto.getAutoEffect();
            if (autoEffect) {
                MultiPicDisplayVo multiPicDisplayVo = new MultiPicDisplayVo();
                multiPicDisplayVo.setManualSet(1);
                multiPicDisplayVo.setSwitchTime(multiPicReqDto.getSwitchTime());
                multiPicDisplayVo.setImageType(ImageTypeEnum.getByNumberAndMode(multiPicReqDto.getPicNum(), multiPicReqDto.getMode()).getName());
                List<PicInfoNotify> subscriberInPics = new ArrayList<>();
                List<PresetMultiPicReqDto.PresetMultiPicRollsDTO> presetMultiPicRolls = multiPicReqDto.getSubPicPollInfoList();
                for (int i = 0; i < presetMultiPicRolls.size(); i++) {
                    PicInfoNotify subscriberInPicvo = new PicInfoNotify();
                    subscriberInPicvo.setIndex(i + 1);
                    PresetMultiPicReqDto.PresetMultiPicRollsDTO presetMultiPicRollsDTO = presetMultiPicRolls.get(i);

                    if (CollectionUtils.isNotEmpty(presetMultiPicRollsDTO.getParticipantIds())) {
                        subscriberInPicvo.setShare(presetMultiPicRolls.get(i).getParticipantIds().get(0).getStreamNumber());
                    } else {
                        subscriberInPicvo.setShare(0);
                    }

                    List<String> ids = new ArrayList<>();

                    List<PresetMultiPicReqDto.PresetMultiPicRollsDTO.SubPicListDTO> subPicList1 = presetMultiPicRolls.get(i).getParticipantIds();
                    for (PresetMultiPicReqDto.PresetMultiPicRollsDTO.SubPicListDTO subPicListDTO : subPicList1) {
                        String participantId = subPicListDTO.getParticipantId();
                        ids.add(participantId);

                    }
                    subscriberInPicvo.setId(ids);
                    subscriberInPics.add(subscriberInPicvo);
                }

                multiPicDisplayVo.setSubscriberInPics(subscriberInPics);
                multiPicDisplayVo.setSkipEmptyPic(0);
                multiPicDisplayVo.setAutoApplyMultiPic(true);
                multiPicDisplayVo.setName(multiPicReqDto.getName());
                picDisplay.add(multiPicDisplayVo);
            } else {
                MultiPicDisplayVo multiPicDisplayVo = new MultiPicDisplayVo();
                multiPicDisplayVo.setManualSet(1);
                multiPicDisplayVo.setSwitchTime(multiPicReqDto.getSwitchTime());
                multiPicDisplayVo.setImageType(ImageTypeEnum.getByNumberAndMode(multiPicReqDto.getPicNum(), multiPicReqDto.getMode()).getName());
                List<PicInfoNotify> subscriberInPics = new ArrayList<>();
                List<PresetMultiPicReqDto.PresetMultiPicRollsDTO> presetMultiPicRolls = multiPicReqDto.getSubPicPollInfoList();
                for (int i = 0; i < presetMultiPicRolls.size(); i++) {
                    PicInfoNotify subscriberInPicvo = new PicInfoNotify();
                    subscriberInPicvo.setIndex(i + 1);

                    PresetMultiPicReqDto.PresetMultiPicRollsDTO presetMultiPicRollsDTO = presetMultiPicRolls.get(i);
                    if (CollectionUtils.isNotEmpty(presetMultiPicRollsDTO.getParticipantIds())) {
                        subscriberInPicvo.setShare(presetMultiPicRolls.get(i).getParticipantIds().get(0).getStreamNumber());
                    } else {
                        subscriberInPicvo.setShare(0);
                    }
                    List<String> ids = new ArrayList<>();

                    List<PresetMultiPicReqDto.PresetMultiPicRollsDTO.SubPicListDTO> subPicList1 = presetMultiPicRolls.get(i).getParticipantIds();
                    for (PresetMultiPicReqDto.PresetMultiPicRollsDTO.SubPicListDTO subPicListDTO : subPicList1) {
                        String participantId = subPicListDTO.getParticipantId();
                        ids.add(participantId);

                    }
                    subscriberInPicvo.setId(ids);
                    subscriberInPics.add(subscriberInPicvo);
                }

                multiPicDisplayVo.setSubscriberInPics(subscriberInPics);
                multiPicDisplayVo.setSkipEmptyPic(0);
                multiPicDisplayVo.setAutoApplyMultiPic(false);
                multiPicDisplayVo.setName(multiPicReqDto.getName());
                picLayouts.add(multiPicDisplayVo);
            }

        }
        return picB;
    }

    private void doCall(HwcloudConferenceContext conferenceContext, List<AttendeeHwcloud> attendeeHwcloudList) {
        try {
            HwcloudDelayTaskService delayTaskService = BeanFactory.getBean(HwcloudDelayTaskService.class);
            InviteAttendeeHwcloudTask inviteAttendeesTask = new InviteAttendeeHwcloudTask(conferenceContext.getConferenceNumber(), 200, conferenceContext, attendeeHwcloudList);
            delayTaskService.addTask(inviteAttendeesTask);
        } catch (Exception e) {
            logger.error("呼叫与会者发生异常-doCall：", e);
            HwcloudWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, e.getMessage());
        }
    }


    private void saveHistory(IBusiMcuHwcloudHistoryConferenceService busiMcuHwcloudHistoryConferenceService, BusiMcuHwcloudTemplateConference tc, HwcloudConferenceContext conferenceContext) {
        // 保存历史记录
        String callId = UUID.randomUUID().toString();
        // 保存历史记录
        BusiHistoryConference busiHistoryConference = busiMcuHwcloudHistoryConferenceService.saveHistory(conferenceContext);
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

    public synchronized String createConferenceNumber(long templateConferenceId, String startTime) {
        logger.info("模板会议启动入口：" + templateConferenceId);

        BusiMcuHwcloudTemplateConferenceMapper busiMcuHwcloudTemplateConferenceMapper = BeanFactory.getBean(BusiMcuHwcloudTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuHwcloudTemplateConference tc = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(templateConferenceId);
        if (tc == null) {
            return null;
        }
        // 获取会议上下文
        HwcloudConferenceContext conferenceContext = buildTemplateConferenceContext(templateConferenceId);
        if (conferenceContext == null) {
            return null;
        }
        // 启动前校验会议是否已开始
        if (conferenceContext.isStart()) {
            throw new SystemException(1009874, "会议已开始，请勿重复开始");
        }

        HwcloudBridge bridge = HwcloudBridgeCache.getInstance().getAvailableBridgesByDept(tc.getDeptId());
        if (bridge == null) {
            return null;
        }
        HwcloudMeetingBridge hwcloudMeetingBridge = new HwcloudMeetingBridge(bridge);

        String confId = tc.getConfId();

        if (confId == null) {

            try {
                RestConfConfigDTO restConfConfigDTO = new RestConfConfigDTO();

                restConfConfigDTO.setIsAutoMute(tc.getMuteType() == 1);
                restConfConfigDTO.setProlongLength(60);


                Map<String, Object> businessProperties = tc.getBusinessProperties();
                if (businessProperties != null) {
                    Object isSendNotify = businessProperties.get("isSendNotify");
                    if (isSendNotify != null) {
                        restConfConfigDTO.setIsSendNotify((Boolean) isSendNotify);
                    }
                    Object isSendCalendar = businessProperties.get("isSendCalendar");
                    if (isSendCalendar != null) {
                        restConfConfigDTO.setIsSendCalendar((Boolean) isSendCalendar);
                    }
                    Object callInRestriction = businessProperties.get("callInRestriction");
                    if (callInRestriction != null) {
                        restConfConfigDTO.setCallInRestriction((Integer) callInRestriction);
                    }
                    Object enableWaitingRoom = businessProperties.get("enableWaitingRoom");
                    if (enableWaitingRoom != null) {
                        restConfConfigDTO.setEnableWaitingRoom((Boolean) enableWaitingRoom);
                    }
                    Object allowGuestStartConf = businessProperties.get("allowGuestStartConf");
                    if (allowGuestStartConf != null) {
                        restConfConfigDTO.setAllowGuestStartConf((Boolean) allowGuestStartConf);
                    }
                }
                if (tc.getConferencePassword() != null && Strings.isNotBlank(tc.getConferencePassword())) {
                    restConfConfigDTO.setGuestPwd(tc.getConferencePassword());
                    conferenceContext.setConferencePassword(tc.getConferencePassword());
                } else {
                    restConfConfigDTO.setGuestPwd("");
                    restConfConfigDTO.setIsGuestFreePwd(true);
                }

                Integer autoRecord = 0;

                if (tc.getRecordingEnabled() != null && tc.getRecordingEnabled() == 1) {
                    autoRecord = 1;
                }

                List<RestAttendeeDTO> attendees = new ArrayList<>();
                //会议开始时间（UTC时间） yyyy-MM-dd HH:mm 创建预约会议时，如果没有指定开始时间或填空串，则表示会议马上开始
                //时间是UTC时间，即0时区的时间
                ConferenceInfo meetingInfo = hwcloudMeetingBridge.getMeetingManager().createMeeting(tc.getName(), startTime, tc.getDurationTime(), "",
                        tc.getMaxParticipantNum() == null ? 500 : tc.getMaxParticipantNum(), autoRecord, restConfConfigDTO, attendees);
                String accessNumber = meetingInfo.getConferenceID();
                conferenceContext.setConferenceNumber(accessNumber);
                List<PasswordEntry> passwordEntry = meetingInfo.getPasswordEntry();
                String hostPassword = "";
                for (PasswordEntry entry : passwordEntry) {
                    String conferenceRole = entry.getConferenceRole();
                    if (Objects.equals(conferenceRole, "chair")) {
                        hostPassword = entry.getPassword();
                        conferenceContext.setChairmanPassword(hostPassword);
                        tc.setChairmanPassword(hostPassword);
                        break;
                    }
                }
//                conferenceContext.setMeetingId(meetingInfo.getConferenceID());
//                conferenceContext.setMeetingUUID(meetingInfo.getConfUUID());
//
//                Date startDate = DateUtil.convertDateByString(startTime, null);
//                Date endDate = DateUtils.addMinutes(startDate,tc.getDurationTime().intValue());
//                conferenceContext.setEndTime(endDate);

                tc.setConfId(accessNumber);
                tc.setConferenceNumber(Long.valueOf(accessNumber));
                busiMcuHwcloudTemplateConferenceMapper.updateBusiMcuHwcloudTemplateConference(tc);
                confId = accessNumber;
            } catch (Exception e) {
                logger.error("预约会议失败" + e.getMessage());
            }
        }
        return confId;
    }

    public synchronized void editConference(long templateConferenceId, String startTime) {
        logger.info("模板会议启动入口：" + templateConferenceId);

        BusiMcuHwcloudTemplateConferenceMapper busiMcuHwcloudTemplateConferenceMapper = BeanFactory.getBean(BusiMcuHwcloudTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuHwcloudTemplateConference tc = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(templateConferenceId);
        if (tc == null) {
            return;
        }
        // 获取会议上下文
        HwcloudConferenceContext conferenceContext = buildTemplateConferenceContext(templateConferenceId);
        if (conferenceContext == null) {
            return;
        }
        // 启动前校验会议是否已开始
        if (conferenceContext.isStart()) {
            throw new SystemException(1009874, "会议已开始，请勿重复开始");
        }

        HwcloudBridge bridge = HwcloudBridgeCache.getInstance().getAvailableBridgesByDept(tc.getDeptId());
        if (bridge == null) {
            return;
        }
        HwcloudMeetingBridge hwcloudMeetingBridge = new HwcloudMeetingBridge(bridge);

        String confId = tc.getConfId();

        if (confId != null) {

            try {
                RestConfConfigDTO restConfConfigDTO = new RestConfConfigDTO();

                restConfConfigDTO.setIsAutoMute(tc.getMuteType() == 1);
                restConfConfigDTO.setProlongLength(60);


                Map<String, Object> businessProperties = tc.getBusinessProperties();
                if (businessProperties != null) {
                    Object isSendNotify = businessProperties.get("isSendNotify");
                    if (isSendNotify != null) {
                        restConfConfigDTO.setIsSendNotify((Boolean) isSendNotify);
                    }
                    Object isSendCalendar = businessProperties.get("isSendCalendar");
                    if (isSendCalendar != null) {
                        restConfConfigDTO.setIsSendCalendar((Boolean) isSendCalendar);
                    }
                    Object callInRestriction = businessProperties.get("callInRestriction");
                    if (callInRestriction != null) {
                        restConfConfigDTO.setCallInRestriction((Integer) callInRestriction);
                    }
                    Object enableWaitingRoom = businessProperties.get("enableWaitingRoom");
                    if (enableWaitingRoom != null) {
                        restConfConfigDTO.setEnableWaitingRoom((Boolean) enableWaitingRoom);
                    }
                    Object allowGuestStartConf = businessProperties.get("allowGuestStartConf");
                    if (allowGuestStartConf != null) {
                        restConfConfigDTO.setAllowGuestStartConf((Boolean) allowGuestStartConf);
                    }
                }
                if (tc.getConferencePassword() != null && Strings.isNotBlank(tc.getConferencePassword())) {
                    restConfConfigDTO.setGuestPwd(tc.getConferencePassword());
                } else {
                    restConfConfigDTO.setGuestPwd("");
                    restConfConfigDTO.setIsGuestFreePwd(true);
                }

                Integer autoRecord = 0;

                if (tc.getRecordingEnabled() != null && tc.getRecordingEnabled() == 1) {
                    autoRecord = 1;
                }

                List<RestAttendeeDTO> attendees = new ArrayList<>();
                //会议开始时间（UTC时间） yyyy-MM-dd HH:mm 创建预约会议时，如果没有指定开始时间或填空串，则表示会议马上开始
                //时间是UTC时间，即0时区的时间
                ConferenceInfo meetingInfo = hwcloudMeetingBridge.getMeetingManager().updateMeeting(confId, tc.getName(), startTime, tc.getDurationTime(), "",
                        tc.getMaxParticipantNum() == null ? 500 : tc.getMaxParticipantNum(), autoRecord, restConfConfigDTO, attendees);

            } catch (Exception e) {
                logger.error("修改预约会议失败" + e.getMessage());
            }
        }

    }

    public synchronized void deleteConference(long templateConferenceId) {
        logger.info("模板会议启动入口：" + templateConferenceId);

        BusiMcuHwcloudTemplateConferenceMapper busiMcuHwcloudTemplateConferenceMapper = BeanFactory.getBean(BusiMcuHwcloudTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuHwcloudTemplateConference tc = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(templateConferenceId);
        if (tc == null) {
            return;
        }
        // 获取会议上下文
        HwcloudConferenceContext conferenceContext = buildTemplateConferenceContext(templateConferenceId);
        if (conferenceContext == null) {
            return;
        }
        // 启动前校验会议是否已开始
        if (conferenceContext.isStart()) {
            return;
        }

        HwcloudBridge bridge = HwcloudBridgeCache.getInstance().getAvailableBridgesByDept(tc.getDeptId());
        if (bridge == null) {
            return;
        }
        HwcloudMeetingBridge hwcloudMeetingBridge = new HwcloudMeetingBridge(bridge);

        String confId = tc.getConfId();

        if (confId != null) {
            hwcloudMeetingBridge.getMeetingManager().deleteMeeting(confId);
        }
    }

    public HwcloudConferenceContext startTemplateConference(Long templateConferenceId, HwcloudBridge bridge) {
        logger.info("模板会议启动入口：" + templateConferenceId);

        BusiMcuHwcloudTemplateConferenceMapper busiMcuHwcloudTemplateConferenceMapper = BeanFactory.getBean(BusiMcuHwcloudTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuHwcloudTemplateConference tc = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(templateConferenceId);
        IBusiMcuHwcloudHistoryConferenceService busiMcuHwcloudHistoryConferenceService = BeanFactory.getBean(IBusiMcuHwcloudHistoryConferenceService.class);
        if (tc == null) {
            return null;
        }
        // 获取会议上下文
        HwcloudConferenceContext conferenceContext = buildTemplateConferenceContext(templateConferenceId);
        if (conferenceContext == null) {
            return null;
        }
        // 启动前校验会议是否已开始
        if (conferenceContext.isStart()) {
            throw new SystemException(1009874, "会议已开始，请勿重复开始");
        }

        if (bridge == null) {
            return null;
        }
        HwcloudMeetingBridge hwcloudMeetingBridge = new HwcloudMeetingBridge(bridge);

        String confId = tc.getConfId();

        if (confId == null) {

            try {
                RestConfConfigDTO restConfConfigDTO = new RestConfConfigDTO();

                restConfConfigDTO.setIsAutoMute(tc.getMuteType() == 1);
                restConfConfigDTO.setProlongLength(60);


                Map<String, Object> businessProperties = tc.getBusinessProperties();
                if (businessProperties != null) {
                    Object isSendNotify = businessProperties.get("isSendNotify");
                    if (isSendNotify != null) {
                        restConfConfigDTO.setIsSendNotify((Boolean) isSendNotify);
                    }
                    Object isSendCalendar = businessProperties.get("isSendCalendar");
                    if (isSendCalendar != null) {
                        restConfConfigDTO.setIsSendCalendar((Boolean) isSendCalendar);
                    }
                    Object callInRestriction = businessProperties.get("callInRestriction");
                    if (callInRestriction != null) {
                        restConfConfigDTO.setCallInRestriction((Integer) callInRestriction);
                    }
                    Object enableWaitingRoom = businessProperties.get("enableWaitingRoom");
                    if (enableWaitingRoom != null) {
                        restConfConfigDTO.setEnableWaitingRoom((Boolean) enableWaitingRoom);
                    }
                    Object allowGuestStartConf = businessProperties.get("allowGuestStartConf");
                    if (allowGuestStartConf != null) {
                        restConfConfigDTO.setAllowGuestStartConf((Boolean) allowGuestStartConf);
                    }
                }
                if (tc.getConferencePassword() != null && Strings.isNotBlank(tc.getConferencePassword())) {
                    restConfConfigDTO.setGuestPwd(tc.getConferencePassword());
                } else {
                    restConfConfigDTO.setGuestPwd("");
                    restConfConfigDTO.setIsGuestFreePwd(true);
                }

                Integer autoRecord = 0;

                if (tc.getRecordingEnabled() != null && tc.getRecordingEnabled() == 1) {
                    autoRecord = 1;
                }

                List<RestAttendeeDTO> attendees = new ArrayList<>();
                //会议开始时间（UTC时间） yyyy-MM-dd HH:mm 创建预约会议时，如果没有指定开始时间或填空串，则表示会议马上开始
                //时间是UTC时间，即0时区的时间
                ConferenceInfo meetingInfo = hwcloudMeetingBridge.getMeetingManager().createMeeting(tc.getName(), "", tc.getDurationTime(), "",
                        tc.getMaxParticipantNum() == null ? 500 : tc.getMaxParticipantNum(), autoRecord, restConfConfigDTO, attendees);
                String accessNumber = meetingInfo.getConferenceID();
                conferenceContext.setConferenceNumber(accessNumber);
                List<PasswordEntry> passwordEntry = meetingInfo.getPasswordEntry();
                String hostPassword = "";
                for (PasswordEntry entry : passwordEntry) {
                    String conferenceRole = entry.getConferenceRole();
                    if (Objects.equals(conferenceRole, "chair")) {
                        hostPassword = entry.getPassword();
                        conferenceContext.setChairmanPassword(hostPassword);
                        tc.setChairmanPassword(hostPassword);
                        break;
                    }
                }
                //激活会议
                hwcloudMeetingBridge.getMeetingManager().startMeeting(meetingInfo.getConferenceID(), hostPassword);
                conferenceContext.setMeetingId(meetingInfo.getConferenceID());
                conferenceContext.setMeetingUUID(meetingInfo.getConfUUID());
                Date startDate = new Date();
                Date endDate = DateUtils.addMinutes(startDate, tc.getDurationTime().intValue());
                conferenceContext.setEndTime(endDate);

                settingBridge(hwcloudMeetingBridge, meetingInfo.getConferenceID(), hostPassword);

                tc.setConfId(accessNumber);
                busiMcuHwcloudTemplateConferenceMapper.updateBusiMcuHwcloudTemplateConference(tc);


            } catch (Exception e) {
                logger.error("开始会议失败" + e.getMessage());
                throw new CustomException("开始会议失败" + e.getMessage());
            }

        } else {
            //查询在线会议列表
            SearchOnlineMeetingsResponse searchOnlineMeetingsResponse = hwcloudMeetingBridge.getMeetingManager().searchOnlineMeeting(confId);
            if (searchOnlineMeetingsResponse != null && CollectionUtils.isNotEmpty(searchOnlineMeetingsResponse.getData())) {
                List<ConferenceInfo> data = searchOnlineMeetingsResponse.getData();
                for (ConferenceInfo conferenceInfo : data) {
                    List<PasswordEntry> passwordEntry1 = conferenceInfo.getPasswordEntry();
                    String password = passwordEntry1.get(0).getPassword();
                    conferenceContext.setChairmanPassword(password);
                    conferenceContext.setMeetingUUID(conferenceInfo.getConfUUID());
                    conferenceContext.setConferenceNumber(confId);
                    conferenceContext.setMeetingId(confId);

                    String endTime = conferenceInfo.getEndTime();
                    try {
                        Date date = DateUtils.parseDate(endTime, "yyyy-MM-dd HH:mm");
                        conferenceContext.setEndTime(date);
                    } catch (ParseException e) {
                    }

                }
            }else {
                hwcloudMeetingBridge.getMeetingManager().startMeeting(confId, tc.getChairmanPassword());

                conferenceContext.setChairmanPassword(tc.getChairmanPassword());
                conferenceContext.setConferenceNumber(confId);
                conferenceContext.setMeetingId(confId);
                Date startDate = new Date();
                Date endDate = DateUtils.addMinutes(startDate, tc.getDurationTime().intValue());
                conferenceContext.setEndTime(endDate);
                settingBridge(hwcloudMeetingBridge, confId,  tc.getChairmanPassword());
            }
        }

        List<AttendeeHwcloud> attendeeHwcloudList = new ArrayList<>();
        HwcloudConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            if (a instanceof CropDirAttendeeHwcloud) {
                if (!a.isMeetingJoined()) {
                    attendeeHwcloudList.add(a);

                }

            }

        });
        doCall(conferenceContext, attendeeHwcloudList);
        Map<String, Object> businessProperties = tc.getBusinessProperties();

        conferenceContext.setStart(true);
        conferenceContext.setStartTime(new Date());

        saveHistory(busiMcuHwcloudHistoryConferenceService, tc, conferenceContext);
        //缓存
        String message = "会议【" + conferenceContext.getName() + "】启动成功！";
        Map<String, Object> obj = new HashMap<>(3);
        obj.put("startTime", conferenceContext.getStartTime());
        obj.put("endTime", conferenceContext.getEndTime());
        obj.put("message", message);
        obj.put("conferenceNumber", conferenceContext.getConferenceNumber());
        obj.put("streamingUrl", conferenceContext.getStreamingUrl());
        obj.put("streamingUrlList", conferenceContext.getStreamUrlList());
        HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, message);
        HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_STARTED, obj);
        conferenceContext.setId(EncryptIdUtil.generateConferenceId(conferenceContext.getContextKey()));
        HwcloudConferenceContextCache.getInstance().add(conferenceContext);


        HwcloudMeetingWebsocketReconnecter.getInstance().add(hwcloudMeetingBridge);
        conferenceContext.setHwcloudMeetingBridge(hwcloudMeetingBridge);


        //多画面设置

        Object confPresetParam = businessProperties.get("confPresetParam");
        if (confPresetParam != null) {
            ConfPresetParamDTO confPresetParamDTO = new ConfPresetParamDTO();
            Map<String, Object> objectMap = (Map<String, Object>) confPresetParam;
            Object presetMultiPics = objectMap.get("presetMultiPics");
            List<PresetMultiPicReqDto> presetMultiPicReqDto = JSONArray.parseArray(JSONObject.toJSONString(presetMultiPics), PresetMultiPicReqDto.class);
            confPresetParamDTO.setPresetMultiPics(presetMultiPicReqDto);
            conferenceContext.setConfPresetParam(confPresetParamDTO);
            //转换结构
            PicB picB = picAtoB(presetMultiPicReqDto);
            List<MultiPicDisplayVo> picLayouts = picB.getPicLayouts();
            if (CollectionUtils.isNotEmpty(picLayouts)) {
                //保存多画面
                for (MultiPicDisplayVo picLayout : picLayouts) {
                    RestPicLayout restPicLayout = new RestPicLayout();
                    restPicLayout.setImageType(picLayout.getImageType());
                    restPicLayout.setSwitchTime(picLayout.getSwitchTime());
                    restPicLayout.setLayOutName(picLayout.getName());
                    restPicLayout.setUuid(UUID.randomUUID().toString());
                    restPicLayout.setSubscriberInPics(picLayout.getSubscriberInPics());
                    hwcloudMeetingBridge.getMeetingControl().saveLayout(hwcloudMeetingBridge.getTokenInfo().getToken(), hwcloudMeetingBridge.getConfID(), restPicLayout);
                }
            }
            List<MultiPicDisplayVo> picDisplay = picB.getPicDisplay();
            for (MultiPicDisplayVo multiPicDisplayVo : picDisplay) {
                //自定义多画面
                DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext, JSON.parseObject(JSON.toJSONString(multiPicDisplayVo)), !multiPicDisplayVo.getAutoApplyMultiPic());
                conferenceContext.setAttendeeOperation(defaultAttendeeOperation);
                defaultAttendeeOperation.operate();
                PresetMultiPicReqDto multiPicReqDto = presetMultiPicReqDto.stream().filter(p -> p.getAutoEffect()).findFirst().get();
                conferenceContext.setMultiPicInfo(multiPicReqDto);

            }
        }

        return conferenceContext;
    }
}
