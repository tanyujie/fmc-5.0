/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : StartTemplateConference.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.templateconference
 * @author sinhy 
 * @since 2021-09-22 21:12
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.zj.conference.model.templateconference;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.common.utils.uuid.UUID;
import com.paradisecloud.fcm.common.constant.MqttConfigConstant;
import com.paradisecloud.fcm.common.constant.ResponseInfo;
import com.paradisecloud.fcm.common.constant.TerminalTopic;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mcu.zj.attendee.utils.McuZjConferenceContextUtils;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjConferenceContextCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.listener.MqttForMcuZjPushMessageCache;
import com.paradisecloud.fcm.mcu.zj.model.SourceTemplate;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.TerminalAttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.CmAddRoomRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.CmAddScheduleRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.CmStartMrRequest;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.CmAddRoomResponse;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.CmAddScheduleResponse;
import com.paradisecloud.fcm.mcu.zj.monitor.cache.MonitorThreadCache;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiHistoryConferenceForMcuZjService;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjConferenceAppointmentService;
import com.paradisecloud.fcm.mcu.zj.task.DelayTaskService;
import com.paradisecloud.fcm.mcu.zj.task.InviteAttendeesTask;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberService;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveDeptCache;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class StartTemplateConference extends BuildTemplateConferenceContext
{
    Logger logger = LoggerFactory.getLogger(getClass());
    
    public synchronized String startTemplateConference(long templateConferenceId)
    {
        logger.info("模板会议启动入口：" + templateConferenceId);

        BusiMcuZjTemplateConferenceMapper busiMcuZjTemplateConferenceMapper = BeanFactory.getBean(BusiMcuZjTemplateConferenceMapper.class);
        IBusiMcuZjConferenceAppointmentService busiMcuZjConferenceAppointmentService = BeanFactory.getBean(IBusiMcuZjConferenceAppointmentService.class);
        IBusiHistoryConferenceForMcuZjService busiHistoryConferenceForMcuZjService = BeanFactory.getBean(IBusiHistoryConferenceForMcuZjService.class);
        DelayTaskService delayTaskService = BeanFactory.getBean(DelayTaskService.class);
        // 获取模板会议实体对象
        BusiMcuZjTemplateConference busiMcuZjTemplateConference = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(templateConferenceId);
        // 获取会议上下文
        McuZjConferenceContext conferenceContext = buildTemplateConferenceContext(templateConferenceId);
        if (conferenceContext == null)
        {
            return null;
        }

        // 启动前校验会议是否已开始
        if (conferenceContext.isStart())
        {
            if (conferenceContext.isEnd()) {
                throw new SystemException(1009874, "会议正在结束，请稍后重试");
            } else {
                throw new SystemException(1009874, "会议已开始，请勿重复开始");
            }
        }

        if (busiMcuZjTemplateConference.getLastConferenceId() != null) {
            BusiHistoryConference busiHistoryConference = busiHistoryConferenceForMcuZjService.selectBusiHistoryConferenceById(busiMcuZjTemplateConference.getLastConferenceId());
            if (busiHistoryConference != null) {
                Date endTime = busiHistoryConference.getConferenceEndTime();
                if (endTime != null) {
                    long diff = System.currentTimeMillis() - endTime.getTime();
                    if (diff < 30000) {
                        throw new SystemException(1009874, "该会议室刚结束会议，正在准备中，请等待" + (30 - diff / 1000) + "秒");
                    }
                }
            }
        }
        
        if (conferenceContext.getConferenceNumber() == null)
        {
            BusiConferenceNumber busiConferenceNumber = BeanFactory.getBean(IBusiConferenceNumberService.class).autoCreateConferenceNumber(conferenceContext.getDeptId(), McuType.MCU_ZJ.getCode());
            busiMcuZjTemplateConference.setConferenceNumber(busiConferenceNumber.getId());
            busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(busiMcuZjTemplateConference);
            conferenceContext.setConferenceNumber(busiConferenceNumber.getId().toString());
            if (conferenceContext.getIsAutoCreateStreamUrl() == 1) {
                String streamingUrl = createStreamingUrl(busiMcuZjTemplateConference.getDeptId(), Long.valueOf(busiMcuZjTemplateConference.getTenantId() + busiMcuZjTemplateConference.getConferenceNumber()));
                logger.info("直播地址：" + streamingUrl);
                busiMcuZjTemplateConference.setStreamUrl(streamingUrl);
                conferenceContext.setStreamingUrl(streamingUrl);
            }
            logger.info("自动生成会议号成功：" + busiConferenceNumber.getId());
        }
        if (conferenceContext.getConferenceNumber() != null) {
            String conferenceRemoteParty = conferenceContext.getTenantId() + conferenceContext.getConferenceNumber() + "@" + conferenceContext.getMcuCallIp();
            if (conferenceContext.getMcuCallPort() != null && conferenceContext.getMcuCallPort() != 5060) {
                conferenceRemoteParty += ":" + conferenceContext.getMcuCallPort();
            }
            conferenceContext.setConferenceRemoteParty(conferenceRemoteParty);
        }
        if (conferenceContext.getIsAutoCreateStreamUrl() == 1 && StringUtils.isEmpty(conferenceContext.getStreamingUrl())) {
            String streamingUrl = createStreamingUrl(busiMcuZjTemplateConference.getDeptId(), Long.valueOf(conferenceContext.getTenantId() + conferenceContext.getConferenceNumber()));
            logger.info("直播地址：" + streamingUrl);
            busiMcuZjTemplateConference.setStreamUrl(streamingUrl);
            conferenceContext.setStreamingUrl(streamingUrl);
        }

        int duration = -1;
        List<BusiMcuZjConferenceAppointment> busiMcuZjConferenceAppointmentList = busiMcuZjConferenceAppointmentService.selectBusiMcuZjConferenceAppointmentByTemplateId(templateConferenceId);
        if (busiMcuZjConferenceAppointmentList != null && busiMcuZjConferenceAppointmentList.size() > 0) {
            BusiMcuZjConferenceAppointment busiMcuZjConferenceAppointment = busiMcuZjConferenceAppointmentList.get(0);
            if (busiMcuZjConferenceAppointment.getEndTime() != null && busiMcuZjConferenceAppointment.getEndTime().startsWith("9999")) {
                duration = 360000000;
            }
        } else {
            duration = 172800;
        }

        Integer muteType = busiMcuZjTemplateConference.getMuteType();
        McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().getAvailableMcuZjBridgesByDept(busiMcuZjTemplateConference.getDeptId()).getMasterMcuZjBridge();
        Integer bandwidthK = null;
        int roomId = 0;
        if (StringUtils.isEmpty(conferenceContext.getConferenceCtrlPassword())) {
            String conferenceCtrlPassword = generatePassword();
            String supervisorPassword = generateSupervisorPassword(conferenceCtrlPassword);
            Integer resourceTemplateId = busiMcuZjTemplateConference.getResourceTemplateId();
            Integer bandwidth = 1;
            if (resourceTemplateId != null) {
                SourceTemplate sourceTemplate = mcuZjBridge.getSourceTemplateById(resourceTemplateId);
                if (sourceTemplate == null) {
                    sourceTemplate = mcuZjBridge.getDefaultSourceTemplate();
                    if (sourceTemplate == null) {
                        throw new SystemException(1, "添加会议模板失败，请稍后再试！");
                    }
                }

                resourceTemplateId = sourceTemplate.getId();
                try {
                    String bw = sourceTemplate.getRes_bw();
                    String bwS = bw.split("@")[1].replace("M", "");
                    bandwidth = Integer.valueOf(bwS);
                } catch (Exception e) {
                }
            }
            bandwidthK = bandwidth * 1024;

            // 添加会议室
            CmAddRoomRequest cmAddRoomRequest = CmAddRoomRequest.buildDefaultRequest();
            if (muteType != null && muteType == 0) {
                cmAddRoomRequest.setAll_guests_mute(0);
            }
            cmAddRoomRequest.setRoom_name("会议室" + conferenceContext.getConferenceNumber());
            cmAddRoomRequest.setRoom_mark(conferenceContext.getConferenceNumber());
            cmAddRoomRequest.setCtrl_pwd(conferenceCtrlPassword);
            cmAddRoomRequest.setJoin_pwd(conferenceContext.getConferencePassword());
            cmAddRoomRequest.setSupervisor_pwd(supervisorPassword);
            cmAddRoomRequest.setResource_template_id(resourceTemplateId);
            cmAddRoomRequest.setBandwidth(bandwidthK);
            if (duration > -1) {
                cmAddRoomRequest.setDuration(duration);
            }
            List<Integer> belong_to_departments = new ArrayList<>();
            belong_to_departments.add(mcuZjBridge.getTopDepartmentId());
            cmAddRoomRequest.setBelong_to_departments(belong_to_departments);
            CmAddRoomResponse cmAddRoomResponse = mcuZjBridge.getConferenceManageApi().addRoom(cmAddRoomRequest);
            if (cmAddRoomResponse == null) {
                throw new SystemException(1, "创建会议室失败，请稍后再试。");
            }
            roomId = cmAddRoomResponse.getRoom_id();

            busiMcuZjTemplateConference.setResourceTemplateId(resourceTemplateId);
            busiMcuZjTemplateConference.setBandwidth(bandwidth);
            busiMcuZjTemplateConference.setTenantId(mcuZjBridge.getTenantId());
            busiMcuZjTemplateConference.setConferenceCtrlPassword(conferenceCtrlPassword);
            busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(busiMcuZjTemplateConference);
            conferenceContext.setTenantId(mcuZjBridge.getTenantId());
            conferenceContext.setConferenceCtrlPassword(conferenceCtrlPassword);
            conferenceContext.setSupervisorPassword(supervisorPassword);
        } else {
            String supervisorPassword = generateSupervisorPassword(conferenceContext.getConferenceCtrlPassword());
            conferenceContext.setSupervisorPassword(supervisorPassword);
        }

        if (mcuZjBridge.getUsedResourceCount() >= mcuZjBridge.getSystemResourceCount()) {
            throw new SystemException(1, "MCU资源已耗尽，请关闭一些会议后重试。");
        }
        SourceTemplate sourceTemplate = mcuZjBridge.getSourceTemplateById(busiMcuZjTemplateConference.getResourceTemplateId());
        if (mcuZjBridge.getUsedResourceCount() > 0 && sourceTemplate.getEvaluationResourceCount() + mcuZjBridge.getUsedResourceCount() > mcuZjBridge.getSystemResourceCount()) {
            throw new SystemException(1, "MCU资源不足，请关闭一些会议后重试。");
        }

        if (bandwidthK == null) {
            if (busiMcuZjTemplateConference.getBandwidth() != null) {
                bandwidthK = busiMcuZjTemplateConference.getBandwidth() * 1024;
            } else {
                bandwidthK = 1024;
            }
        }

        if (duration > -1) {
            CmAddScheduleRequest cmAddScheduleRequest = CmAddScheduleRequest.buildDefaultRequest();
            if (muteType != null && muteType == 0) {
                cmAddScheduleRequest.setAll_guests_mute(0);
            }
            cmAddScheduleRequest.setRoom_id(roomId);
            cmAddScheduleRequest.setMr_name(busiMcuZjTemplateConference.getName());
            cmAddScheduleRequest.setCtrl_pwd(conferenceContext.getConferenceCtrlPassword());
            cmAddScheduleRequest.setJoin_pwd(conferenceContext.getConferencePassword());
            cmAddScheduleRequest.setResource_template_id(busiMcuZjTemplateConference.getResourceTemplateId());
            cmAddScheduleRequest.setBandwidth(bandwidthK);
            cmAddScheduleRequest.setSupervisor_pwd(conferenceContext.getSupervisorPassword());
            cmAddScheduleRequest.setDuration(duration);
            if (conferenceContext.isRecorded()) {
                cmAddScheduleRequest.setAuto_record(1);
            }
            CmAddScheduleResponse cmAddScheduleResponse = mcuZjBridge.getConferenceManageApi().addSchedules(cmAddScheduleRequest);
            if (cmAddScheduleResponse != null) {
                int scheduleId = cmAddScheduleResponse.getSchedule_id();
                busiMcuZjTemplateConference.setCallLegProfileId(String.valueOf(scheduleId));
            } else {
                throw new SystemException(1, "开始会议失败，请稍后再试！");
            }
        } else {
            CmStartMrRequest cmStartMrRequest = CmStartMrRequest.buildDefaultRequest();
            if (muteType != null && muteType == 0) {
                cmStartMrRequest.setAll_guests_mute(0);
            }
            cmStartMrRequest.setMr_id(busiMcuZjTemplateConference.getTenantId() + busiMcuZjTemplateConference.getConferenceNumber());
            cmStartMrRequest.setMr_name(busiMcuZjTemplateConference.getName());
            cmStartMrRequest.setCtrl_pwd(conferenceContext.getConferenceCtrlPassword());
            cmStartMrRequest.setJoin_pwd(conferenceContext.getConferencePassword());
            cmStartMrRequest.setSupervisor_pwd(conferenceContext.getSupervisorPassword());
            cmStartMrRequest.setResource_template_id(busiMcuZjTemplateConference.getResourceTemplateId());
            cmStartMrRequest.setBandwidth(bandwidthK);
            if (conferenceContext.isRecorded()) {
                cmStartMrRequest.setAuto_record(1);
            }
            boolean result = mcuZjBridge.getConferenceManageApi().startMr(cmStartMrRequest);
            if (!result) {
                throw new SystemException(1, "开始会议失败，请稍后再试！");
            }
        }
        conferenceContext.setAttendeeOperation(conferenceContext.getDefaultViewOperation());
        if (conferenceContext.getDefaultViewOperation().getDefaultViewIsBroadcast() == YesOrNo.YES.getValue()) {
            conferenceContext.setAttendeeOperationForGuest(conferenceContext.getDefaultViewOperation());
        } else {
            conferenceContext.setAttendeeOperationForGuest(conferenceContext.getDefaultViewOperationForGuest());
        }
        conferenceContext.setLastAttendeeOperationForGuest(conferenceContext.getDefaultViewOperationForGuest());
        logger.info("已设置默认布局：" + conferenceContext.getConferenceNumber());
        conferenceContext.setStart(true);
        conferenceContext.setStartTime(new Date());

        String callId = UUID.randomUUID().toString();

        // 保存历史记录
        BusiHistoryConference busiHistoryConference = busiHistoryConferenceForMcuZjService.saveHistory(conferenceContext);
        conferenceContext.setHistoryConference(busiHistoryConference);

        // 历史call保存
        BusiHistoryCall busiHistoryCall = new BusiHistoryCall();
        busiHistoryCall.setCallId(callId);
        busiHistoryCall.setCoSpace(conferenceContext.getCoSpaceId());
        busiHistoryCall.setDeptId(conferenceContext.getDeptId());
        busiHistoryCall.setCreateTime(new Date());
        busiHistoryCall.setHistoryConferenceId(busiHistoryConference.getId());
        BeanFactory.getBean(BusiHistoryCallMapper.class).insertBusiHistoryCall(busiHistoryCall);

        busiMcuZjTemplateConference.setLastConferenceId(busiHistoryConference.getId());// 存会议id
        busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(busiMcuZjTemplateConference);

        Long deptId = conferenceContext.getDeptId();
        List<String> streamUrlList = createStreamUrlList(deptId, conferenceContext.getConferenceNumber());
        if (streamUrlList != null && streamUrlList.size() > 0) {
            conferenceContext.setStreamUrlList(streamUrlList);
        }

        McuZjConferenceContextCache.getInstance().add(conferenceContext);

        List<BusiTerminal> busiTerminalList = new ArrayList<>();
        List<TerminalAttendeeForMcuZj> autoJoinTerminals = new ArrayList<>();
        List<AttendeeForMcuZj> outBoundTerminals = new ArrayList<>();
        List<AttendeeForMcuZj> outBoundTerminalsZj = new ArrayList<>();
        McuZjConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            if (a instanceof TerminalAttendeeForMcuZj) {
                TerminalAttendeeForMcuZj ta = (TerminalAttendeeForMcuZj) a;
                BusiTerminal bt = TerminalCache.getInstance().get(ta.getTerminalId());

                busiTerminalList.add(bt);

                // 被叫由会控负责发起呼叫
                if (AttendType.convert(ta.getAttendType()) == AttendType.OUT_BOUND) {
                    if (!ta.isMeetingJoined()) {
                        if (TerminalType.isZJ(bt.getType())) {
                            outBoundTerminalsZj.add(ta);
                        } else {
                            outBoundTerminals.add(ta);
                        }
                    }
                }

                if (AttendType.convert(ta.getAttendType()) == AttendType.AUTO_JOIN) {
                    if (!ObjectUtils.isEmpty(bt.getSn())) {
                        if (!ta.isMeetingJoined()) {
                            autoJoinTerminals.add(ta);
                        }
                    }
                }
            } else {
//                FcmThreadPool.exec(() -> {
//                    BeanFactory.getBean(IAttendeeService.class).callAttendee(a);
//                });
            }
        });

        if (MonitorThreadCache.getInstance().getCcGetChangesThread() != null) {
            MonitorThreadCache.getInstance().getCcGetChangesThread().startConferenceThread(conferenceContext);
        }
        String message = "会议【" + conferenceContext.getName() + "】启动成功！";
        Map<String, Object> obj = new HashMap<>();
        obj.put("startTime", conferenceContext.getStartTime());
        obj.put("message", message);
        obj.put("mcuIp", conferenceContext.getMcuIp());
        obj.put("mcuPort", conferenceContext.getMcuPort());
        obj.put("supervisorPassword", conferenceContext.getSupervisorPassword());
        obj.put("tenantId", conferenceContext.getTenantId());
        obj.put("conferenceNumber", conferenceContext.getConferenceNumber());
        obj.put("streamingUrl", conferenceContext.getStreamingUrl());
        obj.put("streamingUrlList", conferenceContext.getStreamUrlList());
        McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_STARTED, obj);
        McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, message);

        if (MqttForMcuZjPushMessageCache.getInstance().getMqttForMcuZjPushMessageListener() != null) {
            Long createUserId = conferenceContext.getCreateUserId();
            if (createUserId != null) {
                BusiUserTerminalMapper busiUserTerminalMapper = BeanFactory.getBean(BusiUserTerminalMapper.class);
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByUserId(createUserId);
                if (busiUserTerminal != null) {
                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(busiUserTerminal.getTerminalId());
                    if (busiTerminal != null && StringUtils.isNotEmpty(busiTerminal.getSn())) {
                        if (busiTerminal.getMqttOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
                            String clientId = busiTerminal.getSn();
                            String topic = MqttConfigConstant.TOPIC_PREFIX + clientId;
                            String action = TerminalTopic.CONFERENCE_INFO;
                            ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                            String jsonStr = null;
                            try {
                                jsonStr = objectMapper.writeValueAsString(conferenceContext);
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }

                            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                            jsonObject.remove("supervisorPassword");
                            if (jsonObject.get("conferenceAppointment") == null) {
                                if (!conferenceContext.isAppointment() && conferenceContext.getConferenceAppointment() == null) {
                                    BusiMcuZjConferenceAppointment busiMcuZjConferenceAppointment = new BusiMcuZjConferenceAppointment();
                                    String startTimeStr = DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", conferenceContext.getStartTime());
                                    Date endTime = DateUtils.getDiffDate(conferenceContext.getStartTime(), conferenceContext.getDurationTime(), TimeUnit.MINUTES);
                                    String endTimeStr = DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", endTime);
                                    busiMcuZjConferenceAppointment.setStartTime(startTimeStr);
                                    busiMcuZjConferenceAppointment.setEndTime(endTimeStr);
                                    busiMcuZjConferenceAppointment.setDeptId(conferenceContext.getDeptId());
                                    busiMcuZjConferenceAppointment.setTemplateId(conferenceContext.getTemplateConferenceId());
                                    jsonObject.put("conferenceAppointment", busiMcuZjConferenceAppointment);
                                }
                            }
                            MqttForMcuZjPushMessageCache.getInstance().getMqttForMcuZjPushMessageListener().onPushMessage(ResponseInfo.CODE_200, "会议启动成功！", topic, action, jsonObject, clientId, "");
                        }
                    }
                }
            }
        }

//        McuZjTerminalRegisterTask mcuZjTerminalRegisterTask = new McuZjTerminalRegisterTask(conferenceContext.getId(), 10, conferenceContext, busiTerminalList);
//        delayTaskService.addTask(mcuZjTerminalRegisterTask);

        // 处理终端业务
        doTerminal(conferenceContext, autoJoinTerminals, outBoundTerminals, outBoundTerminalsZj);

        return conferenceContext.getContextKey();
    }

    private void doTerminal(McuZjConferenceContext conferenceContext, List<TerminalAttendeeForMcuZj> autoJoinTerminals, List<AttendeeForMcuZj> outBoundTerminals, List<AttendeeForMcuZj> outBoundTerminalsZj) {

        // 处理被叫终端业务
        doOutBoundTerminal(outBoundTerminals, conferenceContext, outBoundTerminalsZj);
        // 处理自动主叫终端业务
        doAutoJoinTerminal(autoJoinTerminals, conferenceContext);
    }

    private String createStreamingUrl(Long deptId, Long conferenceNumber) {
        String streamUrl = null;
        try {
            BusiLiveDept busiLiveDept = LiveDeptCache.getInstance().get(deptId);
            if (busiLiveDept != null) {
                if (busiLiveDept.getLiveType() == 1) {
                    BusiLive busiLive = BeanFactory.getBean(BusiLiveMapper.class).selectBusiLiveById(busiLiveDept.getLiveId());
                    if (busiLive.getDomainName() != null && busiLive.getDomainName().length() > 0) {
                        streamUrl = busiLive.getProtocolType() + "://" + busiLive.getDomainName() + "/" + busiLive.getUriPath() + "/" + conferenceNumber;
                    } else {
                        streamUrl = busiLive.getProtocolType() + "://" + busiLive.getIp() + "/" + busiLive.getUriPath() + "/" + conferenceNumber;
                    }
                } else {
                    BusiLiveClusterMap busiLiveClusterMap = new BusiLiveClusterMap();
                    busiLiveClusterMap.setClusterId(busiLiveDept.getLiveId());
                    List<BusiLiveClusterMap> busiLiveClusterMaps = BeanFactory.getBean(BusiLiveClusterMapMapper.class).selectBusiLiveClusterMapList(busiLiveClusterMap);
                    if (busiLiveClusterMaps != null && busiLiveClusterMaps.size() > 0) {
                        for (BusiLiveClusterMap liveClusterMap : busiLiveClusterMaps) {
                            if (liveClusterMap.getLiveType() == 1) {
                                BusiLive busiLive = BeanFactory.getBean(BusiLiveMapper.class).selectBusiLiveById(liveClusterMap.getLiveId());
                                if (busiLive.getDomainName() != null && busiLive.getDomainName().length() > 0) {
                                    streamUrl = busiLive.getProtocolType() + "://" + busiLive.getDomainName() + "/" + busiLive.getUriPath() + "/" + conferenceNumber;
                                } else {
                                    streamUrl = busiLive.getProtocolType() + "://" + busiLive.getIp() + "/" + busiLive.getUriPath() + "/" + conferenceNumber;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return streamUrl;
    }

    public List<String> createStreamUrlList(long deptId, String conferenceNumber) {
        List<String> stringList = new ArrayList<>();
        try {
            BusiLiveDept busiLiveDept = LiveDeptCache.getInstance().get(deptId);
            if (busiLiveDept != null) {
                if (busiLiveDept.getLiveType() == 100) {
                    BusiLiveClusterMap busiLiveClusterMap = new BusiLiveClusterMap();
                    busiLiveClusterMap.setClusterId(busiLiveDept.getLiveId());
                    List<BusiLiveClusterMap> busiLiveClusterMaps = BeanFactory.getBean(BusiLiveClusterMapMapper.class).selectBusiLiveClusterMapList(busiLiveClusterMap);
                    if (busiLiveClusterMaps != null && busiLiveClusterMaps.size() > 0) {
                        for (BusiLiveClusterMap liveClusterMap : busiLiveClusterMaps) {
                            BusiLive busiLive = BeanFactory.getBean(BusiLiveMapper.class).selectBusiLiveById(liveClusterMap.getLiveId());
                            if (busiLive.getDomainName() != null && busiLive.getDomainName().length() > 0) {
                                busiLive.setIp(busiLive.getDomainName());
                            }
                            String url = busiLive.getProtocolType() + "://" + busiLive.getIp() + "/" + busiLive.getUriPath() + "/" + conferenceNumber;
                            stringList.add(url);
                        }
                    }
                } else {
                    BusiLive busiLive = BeanFactory.getBean(BusiLiveMapper.class).selectBusiLiveById(busiLiveDept.getLiveId());
                    if (busiLive != null) {

                        if (busiLive.getDomainName() != null && busiLive.getDomainName().length() > 0) {
                            busiLive.setIp(busiLive.getDomainName());
                        }
                        String url = busiLive.getProtocolType() + "://" + busiLive.getIp() + "/" + busiLive.getUriPath() + "/" + conferenceNumber;
                        stringList.add(url);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(String.valueOf(e));
        }
        return stringList;
    }


    /**
     * 处理被叫终端业务
     *
     * @param outBoundTerminals
     * @param conferenceContext
     */
    private void doOutBoundTerminal(List<AttendeeForMcuZj> outBoundTerminals, McuZjConferenceContext conferenceContext, List<AttendeeForMcuZj> outBoundTerminalsZj) {
        DelayTaskService delayTaskService = BeanFactory.getBean(DelayTaskService.class);
        InviteAttendeesTask inviteAttendeesTaskZj = new InviteAttendeesTask("zj_" + conferenceContext.getId(), 1000, conferenceContext, outBoundTerminalsZj);
        delayTaskService.addTask(inviteAttendeesTaskZj);
        if (conferenceContext.getStreamingAttendee() != null) {
            InviteAttendeesTask inviteAttendeesTaskStream = new InviteAttendeesTask("stream_" + conferenceContext.getConferenceNumber(), 2000, conferenceContext, conferenceContext.getStreamingAttendee());
            delayTaskService.addTask(inviteAttendeesTaskStream);
        }
        InviteAttendeesTask inviteAttendeesTask = new InviteAttendeesTask(conferenceContext.getId(), 5000, conferenceContext, outBoundTerminals);
        delayTaskService.addTask(inviteAttendeesTask);
    }

    /**
     * 处理自动主叫终端业务
     *
     * @author sinhy
     * @since 2021-11-18 17:20
     */
    private void doAutoJoinTerminal(List<TerminalAttendeeForMcuZj> autoJoinTerminals, McuZjConferenceContext conferenceContext) {
        // 自动主叫终端由mqtt下发入会邀请后主叫入会
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                IMqttService mqttService = BeanFactory.getBean(IMqttService.class);
                mqttService.pushConferenceInfo(conferenceContext.getId(), conferenceContext.getConferencePassword(), autoJoinTerminals, conferenceContext.getLiveTerminals());
                logger.info("pushConferenceInfo执行成功，发送mqtt终端数：" + autoJoinTerminals.size() + ", 直播终端数：" + conferenceContext.getLiveTerminals().size());
            }
        }).start();
    }

    private String generatePassword() {
        String password = "";
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            password += "" + random.nextInt(9);
        }
        return password;
    }

    private String generateSupervisorPassword(String controlPassword) {
        String password = "";
        if (StringUtils.isNotEmpty(controlPassword)) {
            password += "6";
            String passwordA = "";
            for (int i = 0; i < controlPassword.length(); i++) {
                String pwdS = controlPassword.substring(i, i + 1);
                try {
                    passwordA += (9 - Integer.valueOf(pwdS));
                } catch (Exception e) {
                    break;
                }
            }
            if (passwordA.length() < controlPassword.length()) {
                passwordA = "6666";
            }
            password += passwordA;
        }
        return password;
    }

}
