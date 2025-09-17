/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : StartTemplateConference.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.templateconference
 * @author sinhy
 * @since 2021-09-22 21:12
 * @version  V1.0
 */
package com.paradisecloud.fcm.zte.model.templateconference;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.common.utils.uuid.UUID;
import com.paradisecloud.fcm.common.constant.MqttConfigConstant;
import com.paradisecloud.fcm.common.constant.ResponseInfo;
import com.paradisecloud.fcm.common.constant.TerminalTopic;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberService;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveDeptCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.zte.attendee.utils.McuZteConferenceContextUtils;
import com.paradisecloud.fcm.zte.cache.McuZteBridgeCache;
import com.paradisecloud.fcm.zte.cache.McuZteConferenceContextCache;
import com.paradisecloud.fcm.zte.cache.McuZteWebSocketMessagePusher;
import com.paradisecloud.fcm.zte.cache.model.McuZteBridge;
import com.paradisecloud.fcm.zte.cache.model.McuZteBridgeCollection;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.listener.MqttForMcuZtePushMessageCache;
import com.paradisecloud.fcm.zte.model.busi.attendee.AttendeeForMcuZte;
import com.paradisecloud.fcm.zte.model.busi.attendee.TerminalAttendeeForMcuZte;
import com.paradisecloud.fcm.zte.model.request.cc.CcAddMrTerminalRequest;
import com.paradisecloud.fcm.zte.model.request.cm.CmStartMrRequest;
import com.paradisecloud.fcm.zte.model.response.CommonResponse;
import com.paradisecloud.fcm.zte.model.response.cm.CmStartMrResponse;
import com.paradisecloud.fcm.zte.monitor.cache.MonitorThreadCache;
import com.paradisecloud.fcm.zte.service.interfaces.IBusiHistoryConferenceForMcuZteService;
import com.paradisecloud.fcm.zte.service.interfaces.IBusiMcuZteConferenceAppointmentService;
import com.paradisecloud.fcm.zte.task.InviteAttendeesTask;
import com.paradisecloud.fcm.zte.task.McuZteDelayTaskService;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import com.zte.m900.bean.Participant;
import com.zte.m900.request.CancelConferenceReservedRequest;
import com.zte.m900.request.CreateConferenceRequest;
import com.zte.m900.request.EndConferenceRequest;
import com.zte.m900.request.ModifyConferenceReservedRequest;
import com.zte.m900.response.CancelConferenceReservedResponse;
import com.zte.m900.response.CreateConferenceResponse;
import com.zte.m900.response.EndConferenceResponse;
import com.zte.m900.response.ModifyConferenceReservedResponse;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.*;

public class StartTemplateConference extends BuildTemplateConferenceContext
{
    Logger logger = LoggerFactory.getLogger(getClass());

    public synchronized String startTemplateConference(long templateConferenceId)
    {
        logger.info("模板会议启动入口：" + templateConferenceId);

        BusiMcuZteTemplateConferenceMapper busiMcuZteTemplateConferenceMapper = BeanFactory.getBean(BusiMcuZteTemplateConferenceMapper.class);
        IBusiMcuZteConferenceAppointmentService busiMcuZteConferenceAppointmentService = BeanFactory.getBean(IBusiMcuZteConferenceAppointmentService.class);
        IBusiHistoryConferenceForMcuZteService busiHistoryConferenceForMcuZteService = BeanFactory.getBean(IBusiHistoryConferenceForMcuZteService.class);
        McuZteDelayTaskService delayTaskService = BeanFactory.getBean(McuZteDelayTaskService.class);
        // 获取模板会议实体对象
        BusiMcuZteTemplateConference busiMcuZteTemplateConference = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(templateConferenceId);
        // 获取会议上下文
        McuZteConferenceContext conferenceContext = buildTemplateConferenceContext(templateConferenceId);
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

        if (busiMcuZteTemplateConference.getLastConferenceId() != null) {
            BusiHistoryConference busiHistoryConference = busiHistoryConferenceForMcuZteService.selectBusiHistoryConferenceById(busiMcuZteTemplateConference.getLastConferenceId());
            if (busiHistoryConference != null) {
                Date endTime = busiHistoryConference.getConferenceEndTime();
                if (endTime != null) {
                    long diff = System.currentTimeMillis() - endTime.getTime();
                    if (diff < 7000) {
                        throw new SystemException(1009874, "该会议室刚结束会议，正在准备中，请等待" + (7 - diff / 1000) + "秒");
                    }
                }
            }
        }

        if (conferenceContext.getConferenceNumber() == null)
        {
            BusiConferenceNumber busiConferenceNumber = BeanFactory.getBean(IBusiConferenceNumberService.class).autoCreateConferenceNumber(conferenceContext.getDeptId(), McuType.MCU_ZTE.getCode());
            busiMcuZteTemplateConference.setConferenceNumber(busiConferenceNumber.getId());
            busiMcuZteTemplateConferenceMapper.updateBusiMcuZteTemplateConference(busiMcuZteTemplateConference);
            conferenceContext.setConferenceNumber(busiConferenceNumber.getId().toString());
            if (conferenceContext.getIsAutoCreateStreamUrl() == 1) {
                String streamingUrl = createStreamingUrl(busiMcuZteTemplateConference.getDeptId(), Long.valueOf(busiMcuZteTemplateConference.getConferenceNumber()));
                logger.info("直播地址：" + streamingUrl);
                busiMcuZteTemplateConference.setStreamUrl(streamingUrl);
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
            String streamingUrl = createStreamingUrl(busiMcuZteTemplateConference.getDeptId(), Long.valueOf(conferenceContext.getTenantId() + conferenceContext.getConferenceNumber()));
            logger.info("直播地址：" + streamingUrl);
            busiMcuZteTemplateConference.setStreamUrl(streamingUrl);
            conferenceContext.setStreamingUrl(streamingUrl);
        }

        Integer duration = busiMcuZteTemplateConference.getDurationTime();
        if(duration==null){
            duration=0;
        }
        List<BusiMcuZteConferenceAppointment> busiMcuZteConferenceAppointmentList = busiMcuZteConferenceAppointmentService.selectBusiMcuZteConferenceAppointmentByTemplateId(templateConferenceId);
        if (busiMcuZteConferenceAppointmentList != null && busiMcuZteConferenceAppointmentList.size() > 0) {
            BusiMcuZteConferenceAppointment busiMcuZteConferenceAppointment = busiMcuZteConferenceAppointmentList.get(0);
            if (busiMcuZteConferenceAppointment.getEndTime() != null && busiMcuZteConferenceAppointment.getEndTime().startsWith("9999")) {
                duration = 0;
            }
        }

        Integer muteType = busiMcuZteTemplateConference.getMuteType();
        McuZteBridgeCollection availableMcuZteBridgesByDept = McuZteBridgeCache.getInstance().getAvailableMcuZteBridgesByDept(busiMcuZteTemplateConference.getDeptId());
        if(availableMcuZteBridgesByDept==null){
            throw new CustomException("没可用MCU");
        }
        McuZteBridge mcuZteBridge = availableMcuZteBridgesByDept.getMasterMcuZteBridge();
        List<BusiTerminal> busiTerminalList = new ArrayList<>();
        List<TerminalAttendeeForMcuZte> autoJoinTerminals = new ArrayList<>();
        List<AttendeeForMcuZte> outBoundTerminals = new ArrayList<>();

       if(conferenceContext.getConferenceAppointment()!=null){
           if(Strings.isNotBlank(busiMcuZteTemplateConference.getConfId())){
               ModifyConferenceReservedRequest modifyConferenceReservedRequest = new ModifyConferenceReservedRequest();
               if (muteType != null && muteType == 1) {
                   busiMcuZteTemplateConference.setMuteType(muteType);
               }
               modifyConferenceReservedRequest.setConferenceNumber(busiMcuZteTemplateConference.getConferenceNumber().toString());
               modifyConferenceReservedRequest.setConferenceName(busiMcuZteTemplateConference.getName());
               modifyConferenceReservedRequest.setConferencePassword(conferenceContext.getConferencePassword());
               modifyConferenceReservedRequest.setDuration(duration);
               modifyConferenceReservedRequest.setAccount(mcuZteBridge.getBusiMcuZte().getUsername());


               Map<String, Object> businessProperties = busiMcuZteTemplateConference.getBusinessProperties();
               if(businessProperties!=null){
                   if(businessProperties.get("conferenceTemplet")!=null){
                       modifyConferenceReservedRequest.setConferenceTemplet((String)businessProperties.get("mainMcuId"));
                   }
                   if(businessProperties.get("confCascadeMode")!=null){
                       modifyConferenceReservedRequest.setConfCascadeMode((int)businessProperties.get("confCascadeMode"));
                   }
                   if(businessProperties.get("enableMcuTitle")!=null){
                       modifyConferenceReservedRequest.setEnableMcuTitle((int)businessProperties.get("enableMcuTitle"));
                   }
                   if(businessProperties.get("enableMcuBanner")!=null){
                       modifyConferenceReservedRequest.setEnableMcuBanner((int)businessProperties.get("enableMcuBanner"));
                   }
                   if(businessProperties.get("enableVoiceRecord")!=null){
                       modifyConferenceReservedRequest.setEnableVoiceRecord((int)businessProperties.get("enableVoiceRecord"));
                   }

                   if(businessProperties.get("enableAutoVoiceRecord")!=null){
                       modifyConferenceReservedRequest.setEnableAutoVoiceRecord((int)businessProperties.get("enableAutoVoiceRecord"));
                   }
                   if(businessProperties.get("enableUpConf")!=null){
                       modifyConferenceReservedRequest.setUPConf((int)businessProperties.get("enableUpConf"));
                   }
                   if(businessProperties.get("multiViewNumber")!=null){
                       modifyConferenceReservedRequest.setMultiViewNumber((int)businessProperties.get("multiViewNumber"));
                   }
                   if(businessProperties.get("dynamicRes")!=null){
                       modifyConferenceReservedRequest.setDynamicRes((String) businessProperties.get("dynamicRes"));
                   }
                   if(businessProperties.get("multiPicControl")!=null){
                       modifyConferenceReservedRequest.setMultiPicControl((String) businessProperties.get("multiPicControl"));
                   }
                   if(businessProperties.get("maxParticipants")!=null){
                       modifyConferenceReservedRequest.setMultiPicControl((String) businessProperties.get("maxParticipants"));
                   }

               }

               McuZteConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
                   if (a instanceof TerminalAttendeeForMcuZte) {
                       TerminalAttendeeForMcuZte ta = (TerminalAttendeeForMcuZte) a;
                       BusiTerminal bt = TerminalCache.getInstance().get(ta.getTerminalId());
                       busiTerminalList.add(bt);
                       // 被叫由会控负责发起呼叫
                       if (AttendType.convert(ta.getAttendType()) == AttendType.OUT_BOUND) {
                           if (!ta.isMeetingJoined()) {
                               outBoundTerminals.add(ta);
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

                   }
               });

               Participant[] participants = new Participant[1];
               for (int i = 0; i < outBoundTerminals.size(); i++) {
                   AttendeeForMcuZte attendeeForMcuZte = outBoundTerminals.get(i);
                   if(attendeeForMcuZte instanceof TerminalAttendeeForMcuZte){
                       TerminalAttendeeForMcuZte terminalAttendeeForMcuZte=(TerminalAttendeeForMcuZte)attendeeForMcuZte;
                       int terType = 0;
                       Integer callModel = 1;
                       String remoteParty=attendeeForMcuZte.getRemoteParty();
                       if (terminalAttendeeForMcuZte.getTerminalId() != null) {
                           BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuZte.getTerminalId());
                           if (busiTerminal != null) {

                               if (TerminalType.isFCMSIP(busiTerminal.getType())) {
                                   FcmBridge fcmBridge = FcmBridgeCache.getInstance().get(busiTerminal.getFsServerId());
                                   if (fcmBridge != null) {
                                       Integer callPort = fcmBridge.getBusiFreeSwitch().getCallPort();
                                       if (callPort != null) {
                                           remoteParty += ":" + callPort;
                                       }
                                       terType = 3;
                                       callModel = 2;
                                   }
                               }
                               if (TerminalType.isFSBC(busiTerminal.getType())) {
                                   terType = 3;
                                   callModel = 2;
                               }
                               if (TerminalType.isZTE(busiTerminal.getType())) {
                                   terType = busiTerminal.getZteTerminalType();
                                   callModel = busiTerminal.getCallmodel();
                               }
                               if (TerminalType.isIp(busiTerminal.getType())) {

                                   callModel = 1;
                                   terType = 0;
                               }
                           }
                       }
                       participants[i]= new Participant();
                       participants[i].setTerminalNumber(remoteParty);
                       participants[i].setTerminalName(attendeeForMcuZte.getName());
                       participants[i].setIpAddress(attendeeForMcuZte.getIp());
                       Integer protoType = attendeeForMcuZte.getProtoType();
                       if(protoType==null){
                           protoType=0;
                       }
                       participants[i].setTerType(terType);
                       participants[i].setCallMode(callModel);
                       break;
                   }
               }
               modifyConferenceReservedRequest.setParticipants(participants);
               modifyConferenceReservedRequest.setConferenceNumber(busiMcuZteTemplateConference.getConferenceNumber()+"");
               modifyConferenceReservedRequest.setConferenceIdentifier(busiMcuZteTemplateConference.getConfId());
               ModifyConferenceReservedResponse modifyConferenceReservedResponse = mcuZteBridge.getConferenceManageApi().modifyConferenceReserved(modifyConferenceReservedRequest);
               if (modifyConferenceReservedResponse != null && CommonResponse.STATUS_OK.equals(modifyConferenceReservedResponse.getResult())) {
                  conferenceContext.setConfId(busiMcuZteTemplateConference.getConfId());
               } else {
                   if(!Objects.equals("18001709",modifyConferenceReservedResponse.getResult())){
                       throw new SystemException(1, "开始会议失败，请稍后再试！"+modifyConferenceReservedResponse.getResult());
                   }
               }
           }else {
               cmStart(busiMcuZteTemplateConference, conferenceContext, duration, muteType, mcuZteBridge, busiTerminalList, autoJoinTerminals, outBoundTerminals);
           }
       }else {
           cmStart(busiMcuZteTemplateConference, conferenceContext, duration, muteType, mcuZteBridge, busiTerminalList, autoJoinTerminals, outBoundTerminals);
       }



        conferenceContext.setAttendeeOperation(conferenceContext.getDefaultViewOperation());
        logger.info("已设置默认布局：" + conferenceContext.getConferenceNumber());
        conferenceContext.setStart(true);
        conferenceContext.setStartTime(new Date());

        String callId = UUID.randomUUID().toString();

        // 保存历史记录
        BusiHistoryConference busiHistoryConference = busiHistoryConferenceForMcuZteService.saveHistory(conferenceContext);
        conferenceContext.setHistoryConference(busiHistoryConference);

        // 历史call保存
        BusiHistoryCall busiHistoryCall = new BusiHistoryCall();
        busiHistoryCall.setCallId(callId);
        busiHistoryCall.setCoSpace(conferenceContext.getCoSpaceId());
        busiHistoryCall.setDeptId(conferenceContext.getDeptId());
        busiHistoryCall.setCreateTime(new Date());
        busiHistoryCall.setHistoryConferenceId(busiHistoryConference.getId());
        BeanFactory.getBean(BusiHistoryCallMapper.class).insertBusiHistoryCall(busiHistoryCall);

        busiMcuZteTemplateConference.setLastConferenceId(busiHistoryConference.getId());// 存会议id
        busiMcuZteTemplateConferenceMapper.updateBusiMcuZteTemplateConference(busiMcuZteTemplateConference);

        Long deptId = conferenceContext.getDeptId();
        List<String> streamUrlList = createStreamUrlList(deptId, conferenceContext.getConferenceNumber());
        if (streamUrlList != null && streamUrlList.size() > 0) {
            conferenceContext.setStreamUrlList(streamUrlList);
        }

        McuZteConferenceContextCache.getInstance().add(conferenceContext);



        if (MonitorThreadCache.getInstance().getCcGetChangesThread() != null) {
            MonitorThreadCache.getInstance().getCcGetChangesThread().startConferenceThread(conferenceContext);
        }
        String message = "会议【" + conferenceContext.getName() + "】启动成功！";
        Map<String, Object> obj = new HashMap<>();
        obj.put("startTime", conferenceContext.getStartTime());
        obj.put("message", message);
        obj.put("mcuIp", conferenceContext.getMcuIp());
        obj.put("mcuPort", conferenceContext.getMcuPort());
        obj.put("tenantId", conferenceContext.getTenantId());
        obj.put("conferenceNumber", conferenceContext.getConferenceNumber());
        obj.put("streamingUrl", conferenceContext.getStreamingUrl());
        obj.put("streamingUrlList", conferenceContext.getStreamUrlList());
        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_STARTED, obj);
        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, message);

        if (MqttForMcuZtePushMessageCache.getInstance().getMqttForMcuZtePushMessageListener() != null) {
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
                            MqttForMcuZtePushMessageCache.getInstance().getMqttForMcuZtePushMessageListener().onPushMessage(ResponseInfo.CODE_200, "会议启动成功！", topic, action, jsonObject, clientId, "");
                        }
                    }
                }
            }
        }

        // 处理终端业务
        doTerminal(conferenceContext, autoJoinTerminals, outBoundTerminals);

        return conferenceContext.getContextKey();
    }

    private void cmStart(BusiMcuZteTemplateConference busiMcuZteTemplateConference, McuZteConferenceContext conferenceContext, Integer duration, Integer muteType, McuZteBridge mcuZteBridge, List<BusiTerminal> busiTerminalList, List<TerminalAttendeeForMcuZte> autoJoinTerminals, List<AttendeeForMcuZte> outBoundTerminals) {
        CreateConferenceRequest cmStartMrRequest = new CreateConferenceRequest();
        if (muteType != null && muteType == 1) {
            busiMcuZteTemplateConference.setMuteType(muteType);
        }
        cmStartMrRequest.setConferenceNumber(busiMcuZteTemplateConference.getConferenceNumber().toString());
        cmStartMrRequest.setConferenceName(busiMcuZteTemplateConference.getName());
        cmStartMrRequest.setConferencePassword(conferenceContext.getConferencePassword());
        cmStartMrRequest.setDuration(duration);
        cmStartMrRequest.setAccount(mcuZteBridge.getBusiMcuZte().getUsername());


        Map<String, Object> businessProperties = busiMcuZteTemplateConference.getBusinessProperties();
        if (businessProperties != null) {
            if (businessProperties.get("conferenceTemplet") != null) {
                cmStartMrRequest.setConferenceTemplet((String) businessProperties.get("mainMcuId"));
            }
            if (businessProperties.get("confCascadeMode") != null) {
                cmStartMrRequest.setConfCascadeMode((Integer) businessProperties.get("confCascadeMode"));
            }
            if (businessProperties.get("enableMcuTitle") != null) {
                cmStartMrRequest.setEnableMcuTitle((Integer) businessProperties.get("enableMcuTitle"));
            }
            if (businessProperties.get("enableMcuBanner") != null) {
                cmStartMrRequest.setEnableMcuBanner((Integer) businessProperties.get("enableMcuBanner"));
            }
            if (businessProperties.get("enableVoiceRecord") != null) {
                cmStartMrRequest.setEnableVoiceRecord((Integer) businessProperties.get("enableVoiceRecord"));
            }

            if (businessProperties.get("enableAutoVoiceRecord") != null) {
                cmStartMrRequest.setEnableAutoVoiceRecord((int) businessProperties.get("enableAutoVoiceRecord"));
            }
            if (businessProperties.get("enableUpConf") != null) {
                cmStartMrRequest.setUPConf((Integer) businessProperties.get("enableUpConf"));
            }
            if (businessProperties.get("multiViewNumber") != null) {
                 cmStartMrRequest.setMultiViewNumber((Integer) businessProperties.get("multiViewNumber"));
            }
            if (businessProperties.get("dynamicRes") != null) {
                cmStartMrRequest.setDynamicRes((String) businessProperties.get("dynamicRes"));
            }
            if (businessProperties.get("multiPicControl") != null) {
                cmStartMrRequest.setMultiPicControl((String) businessProperties.get("multiPicControl"));
            }
            if (businessProperties.get("maxParticipants") != null) {
                cmStartMrRequest.setMultiPicControl((String) businessProperties.get("maxParticipants"));
            }

        }

        McuZteConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            if (a instanceof TerminalAttendeeForMcuZte) {
                TerminalAttendeeForMcuZte ta = (TerminalAttendeeForMcuZte) a;
                BusiTerminal bt = TerminalCache.getInstance().get(ta.getTerminalId());
                busiTerminalList.add(bt);
                // 被叫由会控负责发起呼叫
                if (AttendType.convert(ta.getAttendType()) == AttendType.OUT_BOUND) {
                    if (!ta.isMeetingJoined()) {
                        outBoundTerminals.add(ta);
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

            }
        });

        Participant[] participants = new Participant[1];
        for (int i = 0; i < outBoundTerminals.size(); i++) {
            AttendeeForMcuZte attendeeForMcuZte = outBoundTerminals.get(i);
            if (attendeeForMcuZte instanceof TerminalAttendeeForMcuZte) {
                TerminalAttendeeForMcuZte terminalAttendeeForMcuZte = (TerminalAttendeeForMcuZte) attendeeForMcuZte;
                int terType = 0;
                Integer callModel = 1;
                String remoteParty = attendeeForMcuZte.getRemoteParty();
                if (terminalAttendeeForMcuZte.getTerminalId() != null) {
                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuZte.getTerminalId());
                    if (busiTerminal != null) {

                        if (TerminalType.isFCMSIP(busiTerminal.getType())) {
                            FcmBridge fcmBridge = FcmBridgeCache.getInstance().get(busiTerminal.getFsServerId());
                            if (fcmBridge != null) {
                                Integer callPort = fcmBridge.getBusiFreeSwitch().getCallPort();
                                if (callPort != null) {
                                    remoteParty += ":" + callPort;
                                }
                                terType = 3;
                                callModel = 2;
                            }
                        }
                        if (TerminalType.isFSBC(busiTerminal.getType())) {
                            terType = 3;
                            callModel = 2;
                        }
                        if (TerminalType.isZTE(busiTerminal.getType())) {
                            terType = busiTerminal.getZteTerminalType();
                            callModel = busiTerminal.getCallmodel();
                        }
                        if (TerminalType.isIp(busiTerminal.getType())) {

                            callModel = 1;
                            terType = 0;
                        }
                    }
                }
                participants[i] = new Participant();
                participants[i].setTerminalNumber(remoteParty);
                participants[i].setTerminalName(attendeeForMcuZte.getName());
                participants[i].setIpAddress(attendeeForMcuZte.getIp());
                Integer protoType = attendeeForMcuZte.getProtoType();
                if (protoType == null) {
                    protoType = 0;
                }
                participants[i].setTerType(terType);
                participants[i].setCallMode(callModel);
                break;
            }

        }
        cmStartMrRequest.setParticipants(participants);


        CreateConferenceResponse cmStartMrResponse = mcuZteBridge.getConferenceManageApi().startMr(cmStartMrRequest);
        if (cmStartMrResponse != null && CommonResponse.STATUS_OK.equals(cmStartMrResponse.getResult())) {
            busiMcuZteTemplateConference.setConfId(cmStartMrResponse.getConferenceIdentifier());
            conferenceContext.setConfId(cmStartMrResponse.getConferenceIdentifier());
        } else {
            throw new SystemException(1, "开始会议失败，请稍后再试！"+cmStartMrResponse.getResult());
        }
    }

    private void doTerminal(McuZteConferenceContext conferenceContext, List<TerminalAttendeeForMcuZte> autoJoinTerminals, List<AttendeeForMcuZte> outBoundTerminals) {

        // 处理被叫终端业务
        doOutBoundTerminal(outBoundTerminals, conferenceContext);
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
            e.printStackTrace();
        }
        return stringList;
    }


    /**
     * 处理被叫终端业务
     *
     * @param outBoundTerminals
     * @param conferenceContext
     */
    private void doOutBoundTerminal(List<AttendeeForMcuZte> outBoundTerminals, McuZteConferenceContext conferenceContext) {
        McuZteDelayTaskService delayTaskService = BeanFactory.getBean(McuZteDelayTaskService.class);
        InviteAttendeesTask inviteAttendeesTask = new InviteAttendeesTask(conferenceContext.getId(), 1000, conferenceContext, outBoundTerminals);
        delayTaskService.addTask(inviteAttendeesTask);
    }

    /**
     * 处理自动主叫终端业务
     *
     * @author sinhy
     * @since 2021-11-18 17:20
     */
    private void doAutoJoinTerminal(List<TerminalAttendeeForMcuZte> autoJoinTerminals, McuZteConferenceContext conferenceContext) {
        // 自动主叫终端由mqtt下发入会邀请后主叫入会
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    IMqttService mqttService = BeanFactory.getBean(IMqttService.class);
                    mqttService.pushConferenceInfo(conferenceContext.getConferenceNumber(), conferenceContext.getConferencePassword(), autoJoinTerminals, conferenceContext.getLiveTerminals());
                    logger.info("pushConferenceInfo执行成功，发送mqtt终端数：" + autoJoinTerminals.size() + ", 直播终端数：" + conferenceContext.getLiveTerminals().size());
                } catch (Exception e) {
                    e.printStackTrace();
                }

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

    public synchronized void editConference(long templateConferenceId,String startTime)
    {
        BusiMcuZteTemplateConferenceMapper busiMcuZteTemplateConferenceMapper = BeanFactory.getBean(BusiMcuZteTemplateConferenceMapper.class);
        IBusiMcuZteConferenceAppointmentService busiMcuZteConferenceAppointmentService = BeanFactory.getBean(IBusiMcuZteConferenceAppointmentService.class);
        IBusiHistoryConferenceForMcuZteService busiHistoryConferenceForMcuZteService = BeanFactory.getBean(IBusiHistoryConferenceForMcuZteService.class);
        // 获取模板会议实体对象
        BusiMcuZteTemplateConference busiMcuZteTemplateConference = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(templateConferenceId);
        // 获取会议上下文
        McuZteConferenceContext conferenceContext = buildTemplateConferenceContext(templateConferenceId);
        if (conferenceContext == null)
        {
            return ;
        }

        // 启动前校验会议是否已开始
        if (conferenceContext.isStart())
        {
           return;
        }

        if (conferenceContext.getIsAutoCreateStreamUrl() == 1 && StringUtils.isEmpty(conferenceContext.getStreamingUrl())) {
            String streamingUrl = createStreamingUrl(busiMcuZteTemplateConference.getDeptId(), Long.valueOf(conferenceContext.getTenantId() + conferenceContext.getConferenceNumber()));
            logger.info("直播地址：" + streamingUrl);
            busiMcuZteTemplateConference.setStreamUrl(streamingUrl);
            conferenceContext.setStreamingUrl(streamingUrl);
        }

        Integer duration = busiMcuZteTemplateConference.getDurationTime();
        if(duration==null){
            duration=0;
        }
        List<BusiMcuZteConferenceAppointment> busiMcuZteConferenceAppointmentList = busiMcuZteConferenceAppointmentService.selectBusiMcuZteConferenceAppointmentByTemplateId(templateConferenceId);
        if (busiMcuZteConferenceAppointmentList != null && busiMcuZteConferenceAppointmentList.size() > 0) {
            BusiMcuZteConferenceAppointment busiMcuZteConferenceAppointment = busiMcuZteConferenceAppointmentList.get(0);
            if (busiMcuZteConferenceAppointment.getEndTime() != null && busiMcuZteConferenceAppointment.getEndTime().startsWith("9999")) {
                duration = 0;
            }
        }

        Integer muteType = busiMcuZteTemplateConference.getMuteType();
        McuZteBridge mcuZteBridge = McuZteBridgeCache.getInstance().getAvailableMcuZteBridgesByDept(busiMcuZteTemplateConference.getDeptId()).getMasterMcuZteBridge();



        ModifyConferenceReservedRequest modifyConferenceReservedRequest = new ModifyConferenceReservedRequest();
        if (muteType != null && muteType == 1) {
            busiMcuZteTemplateConference.setMuteType(muteType);
        }
        modifyConferenceReservedRequest.setConferenceNumber(busiMcuZteTemplateConference.getConferenceNumber().toString());
        modifyConferenceReservedRequest.setConferenceName(busiMcuZteTemplateConference.getName());
        modifyConferenceReservedRequest.setConferencePassword(conferenceContext.getConferencePassword());
        modifyConferenceReservedRequest.setDuration(duration);
        modifyConferenceReservedRequest.setAccount(mcuZteBridge.getBusiMcuZte().getUsername());


        Map<String, Object> businessProperties = busiMcuZteTemplateConference.getBusinessProperties();
        if(businessProperties!=null){
            if(businessProperties.get("conferenceTemplet")!=null){
                modifyConferenceReservedRequest.setConferenceTemplet((String)businessProperties.get("mainMcuId"));
            }
            if(businessProperties.get("confCascadeMode")!=null){
                modifyConferenceReservedRequest.setConfCascadeMode((int)businessProperties.get("confCascadeMode"));
            }
            if(businessProperties.get("enableMcuTitle")!=null){
                modifyConferenceReservedRequest.setEnableMcuTitle((int)businessProperties.get("enableMcuTitle"));
            }
            if(businessProperties.get("enableMcuBanner")!=null){
                modifyConferenceReservedRequest.setEnableMcuBanner((int)businessProperties.get("enableMcuBanner"));
            }
            if(businessProperties.get("enableVoiceRecord")!=null){
                modifyConferenceReservedRequest.setEnableVoiceRecord((int)businessProperties.get("enableVoiceRecord"));
            }

            if(businessProperties.get("enableAutoVoiceRecord")!=null){
                modifyConferenceReservedRequest.setEnableAutoVoiceRecord((int)businessProperties.get("enableAutoVoiceRecord"));
            }
            if(businessProperties.get("enableUpConf")!=null){
                modifyConferenceReservedRequest.setUPConf((int)businessProperties.get("enableUpConf"));
            }
            if(businessProperties.get("multiViewNumber")!=null){
                modifyConferenceReservedRequest.setMultiViewNumber((int)businessProperties.get("multiViewNumber"));
            }
            if(businessProperties.get("dynamicRes")!=null){
                modifyConferenceReservedRequest.setDynamicRes((String) businessProperties.get("dynamicRes"));
            }
            if(businessProperties.get("multiPicControl")!=null){
                modifyConferenceReservedRequest.setMultiPicControl((String) businessProperties.get("multiPicControl"));
            }
            if(businessProperties.get("maxParticipants")!=null){
                modifyConferenceReservedRequest.setMultiPicControl((String) businessProperties.get("maxParticipants"));
            }

        }



        List<BusiTerminal> busiTerminalList = new ArrayList<>();
        List<TerminalAttendeeForMcuZte> autoJoinTerminals = new ArrayList<>();
        List<AttendeeForMcuZte> outBoundTerminals = new ArrayList<>();
        McuZteConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            if (a instanceof TerminalAttendeeForMcuZte) {
                TerminalAttendeeForMcuZte ta = (TerminalAttendeeForMcuZte) a;
                BusiTerminal bt = TerminalCache.getInstance().get(ta.getTerminalId());
                busiTerminalList.add(bt);
                // 被叫由会控负责发起呼叫
                if (AttendType.convert(ta.getAttendType()) == AttendType.OUT_BOUND) {
                    if (!ta.isMeetingJoined()) {
                        outBoundTerminals.add(ta);
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

            }
        });

        Participant[] participants = new Participant[1];
        for (int i = 0; i < outBoundTerminals.size(); i++) {
            AttendeeForMcuZte attendeeForMcuZte = outBoundTerminals.get(i);
            if(attendeeForMcuZte instanceof TerminalAttendeeForMcuZte){
                TerminalAttendeeForMcuZte terminalAttendeeForMcuZte=(TerminalAttendeeForMcuZte)attendeeForMcuZte;
                int terType = 0;
                Integer callModel = 1;
                String remoteParty=attendeeForMcuZte.getRemoteParty();
                if (terminalAttendeeForMcuZte.getTerminalId() != null) {
                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuZte.getTerminalId());
                    if (busiTerminal != null) {

                        if (TerminalType.isFCMSIP(busiTerminal.getType())) {
                            FcmBridge fcmBridge = FcmBridgeCache.getInstance().get(busiTerminal.getFsServerId());
                            if (fcmBridge != null) {
                                Integer callPort = fcmBridge.getBusiFreeSwitch().getCallPort();
                                if (callPort != null) {
                                    remoteParty += ":" + callPort;
                                }
                                terType = 3;
                                callModel = 2;
                            }
                        }
                        if (TerminalType.isFSBC(busiTerminal.getType())) {
                            terType = 3;
                            callModel = 2;
                        }
                        if (TerminalType.isZTE(busiTerminal.getType())) {
                            terType = busiTerminal.getZteTerminalType();
                            callModel = busiTerminal.getCallmodel();
                        }
                        if (TerminalType.isIp(busiTerminal.getType())) {

                            callModel = 1;
                            terType = 0;
                        }
                    }
                }
                participants[i]= new Participant();
                participants[i].setTerminalNumber(remoteParty);
                participants[i].setTerminalName(attendeeForMcuZte.getName());
                participants[i].setIpAddress(attendeeForMcuZte.getIp());
                Integer protoType = attendeeForMcuZte.getProtoType();
                if(protoType==null){
                    protoType=0;
                }
                participants[i].setTerType(terType);
                participants[i].setCallMode(callModel);
                break;
            }
        }
        modifyConferenceReservedRequest.setParticipants(participants);

        modifyConferenceReservedRequest.setConferenceNumber(busiMcuZteTemplateConference.getConferenceNumber()+"");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.convertDateByString(startTime,null));
        modifyConferenceReservedRequest.setStartTime(calendar);
        modifyConferenceReservedRequest.setConferenceIdentifier(busiMcuZteTemplateConference.getConfId());
        ModifyConferenceReservedResponse modifyConferenceReservedResponse = mcuZteBridge.getConferenceManageApi().modifyConferenceReserved(modifyConferenceReservedRequest);
        if (modifyConferenceReservedResponse != null && CommonResponse.STATUS_OK.equals(modifyConferenceReservedResponse.getResult())) {
          logger.info("zte 修改预约会议成功(模版id:)"+busiMcuZteTemplateConference.getId());
        } else {
            throw new SystemException(1, "修改预约会议失败，请稍后再试！");
        }
    }


    public synchronized void deleteConference(long templateConferenceId) {
        logger.info("模板会议启动入口：" + templateConferenceId);

        BusiMcuZteTemplateConferenceMapper busiMcuZteTemplateConferenceMapper = BeanFactory.getBean(BusiMcuZteTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuZteTemplateConference tc = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(templateConferenceId);
        if (tc == null) {
            return;
        }
        // 获取会议上下文
        McuZteConferenceContext conferenceContext = buildTemplateConferenceContext(templateConferenceId);
        if (conferenceContext == null) {
            return;
        }
        // 启动前校验会议是否已开始
        if (conferenceContext.isStart()) {
            return;
        }

        McuZteBridge mcuZteBridge = McuZteBridgeCache.getInstance().getAvailableMcuZteBridgesByDept(conferenceContext.getDeptId()).getMasterMcuZteBridge();
        if (mcuZteBridge == null) {
            return;
        }


        String confId = tc.getConfId();

        if (confId != null) {
            CancelConferenceReservedRequest cancelConferenceReservedRequest = new CancelConferenceReservedRequest();
            cancelConferenceReservedRequest.setConferenceIdentifier(confId);
            CancelConferenceReservedResponse cancelConferenceReservedResponse = mcuZteBridge.getConferenceManageApi().cancelConferenceReserved(cancelConferenceReservedRequest);
            if(cancelConferenceReservedResponse!=null&&CommonResponse.STATUS_OK.equals(cancelConferenceReservedResponse.getResult())){
            logger.info("zte 取消预约会议成功"+tc.getName());
            }else {
                throw new CustomException("删除会议失败");
            }
        }
    }


    public synchronized String createConferenceNumber(long templateConferenceId, String startTime) {

        logger.info("模板会议启动入口：" + templateConferenceId);
        IBusiMcuZteConferenceAppointmentService busiMcuZteConferenceAppointmentService = BeanFactory.getBean(IBusiMcuZteConferenceAppointmentService.class);

        BusiMcuZteTemplateConferenceMapper busiMcuZteTemplateConferenceMapper = BeanFactory.getBean(BusiMcuZteTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuZteTemplateConference tc = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(templateConferenceId);
        if (tc == null) {
            return null;
        }
        Date date = DateUtil.convertDateByString(startTime, null);
        if(date.getTime()<System.currentTimeMillis()){
            return tc.getConferenceNumber()+"";
        }
        // 获取会议上下文
        McuZteConferenceContext conferenceContext = buildTemplateConferenceContext(templateConferenceId);
        if (conferenceContext == null) {
            return null;
        }
        // 启动前校验会议是否已开始
        if (conferenceContext.isStart()) {
            throw new SystemException(1009874, "会议已开始，请勿重复开始");
        }

        McuZteBridgeCollection availableMcuZteBridgesByDept = McuZteBridgeCache.getInstance().getAvailableMcuZteBridgesByDept(tc.getDeptId());
        if(availableMcuZteBridgesByDept==null){
            throw new CustomException("没可用MCU");
        }
        McuZteBridge mcuZteBridge = availableMcuZteBridgesByDept.getMasterMcuZteBridge();


        Long conferenceNumber = tc.getConferenceNumber();
        if (conferenceContext.getIsAutoCreateStreamUrl() == 1) {
            String streamingUrl = createStreamingUrl(tc.getDeptId(), Long.valueOf(tc.getConferenceNumber()));
            logger.info("直播地址：" + streamingUrl);
            tc.setStreamUrl(streamingUrl);
            conferenceContext.setStreamingUrl(streamingUrl);
        }

        Integer duration = tc.getDurationTime();
        if(duration==null){
            duration=0;
        }
        List<BusiMcuZteConferenceAppointment> busiMcuZteConferenceAppointmentList = busiMcuZteConferenceAppointmentService.selectBusiMcuZteConferenceAppointmentByTemplateId(templateConferenceId);
        if (busiMcuZteConferenceAppointmentList != null && busiMcuZteConferenceAppointmentList.size() > 0) {
            BusiMcuZteConferenceAppointment busiMcuZteConferenceAppointment = busiMcuZteConferenceAppointmentList.get(0);
            if (busiMcuZteConferenceAppointment.getEndTime() != null && busiMcuZteConferenceAppointment.getEndTime().startsWith("9999")) {
                duration = 0;
            }
        }

        Integer muteType = tc.getMuteType();


        CreateConferenceRequest cmStartMrRequest = new CreateConferenceRequest();
        if (muteType != null && muteType == 1) {
            tc.setMuteType(muteType);
        }
        cmStartMrRequest.setConferenceNumber(conferenceNumber+"");
        cmStartMrRequest.setConferenceName(tc.getName());
        cmStartMrRequest.setConferencePassword(conferenceContext.getConferencePassword());
        cmStartMrRequest.setDuration(duration);
        cmStartMrRequest.setAccount(mcuZteBridge.getBusiMcuZte().getUsername());


        Map<String, Object> businessProperties = tc.getBusinessProperties();
        if(businessProperties!=null){
            if(businessProperties.get("conferenceTemplet")!=null){
                cmStartMrRequest.setConferenceTemplet((String)businessProperties.get("mainMcuId"));
            }
            if(businessProperties.get("confCascadeMode")!=null){
                cmStartMrRequest.setConfCascadeMode((int)businessProperties.get("confCascadeMode"));
            }
            if(businessProperties.get("enableMcuTitle")!=null){
                cmStartMrRequest.setEnableMcuTitle((int)businessProperties.get("enableMcuTitle"));
            }
            if(businessProperties.get("enableMcuBanner")!=null){
                cmStartMrRequest.setEnableMcuBanner((int)businessProperties.get("enableMcuBanner"));
            }
            if(businessProperties.get("enableVoiceRecord")!=null){
                cmStartMrRequest.setEnableVoiceRecord((int)businessProperties.get("enableVoiceRecord"));
            }

            if(businessProperties.get("enableAutoVoiceRecord")!=null){
                cmStartMrRequest.setEnableAutoVoiceRecord((int)businessProperties.get("enableAutoVoiceRecord"));
            }
            if(businessProperties.get("enableUpConf")!=null){
                cmStartMrRequest.setUPConf((int)businessProperties.get("enableUpConf"));
            }
            if(businessProperties.get("multiViewNumber")!=null){
             //   cmStartMrRequest.setMultiViewNumber((int)businessProperties.get("multiViewNumber"));
            }
            if(businessProperties.get("dynamicRes")!=null){
                cmStartMrRequest.setDynamicRes((String) businessProperties.get("dynamicRes"));
            }
            if(businessProperties.get("multiPicControl")!=null){
                cmStartMrRequest.setMultiPicControl((String) businessProperties.get("multiPicControl"));
                conferenceContext.setMultiPicControl((String) businessProperties.get("multiPicControl"));
            }
            if(businessProperties.get("maxParticipants")!=null){
                cmStartMrRequest.setMultiPicControl((String) businessProperties.get("maxParticipants"));
            }

        }

        List<BusiTerminal> busiTerminalList = new ArrayList<>();
        List<TerminalAttendeeForMcuZte> autoJoinTerminals = new ArrayList<>();
        List<AttendeeForMcuZte> outBoundTerminals = new ArrayList<>();
        McuZteConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            if (a instanceof TerminalAttendeeForMcuZte) {
                TerminalAttendeeForMcuZte ta = (TerminalAttendeeForMcuZte) a;
                BusiTerminal bt = TerminalCache.getInstance().get(ta.getTerminalId());
                busiTerminalList.add(bt);
                // 被叫由会控负责发起呼叫
                if (AttendType.convert(ta.getAttendType()) == AttendType.OUT_BOUND) {
                    if (!ta.isMeetingJoined()) {
                        outBoundTerminals.add(ta);
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

            }
        });

        Participant[] participants = new Participant[1];
        for (int i = 0; i < outBoundTerminals.size(); i++) {
            AttendeeForMcuZte attendeeForMcuZte = outBoundTerminals.get(i);
            if(attendeeForMcuZte instanceof TerminalAttendeeForMcuZte){
                TerminalAttendeeForMcuZte terminalAttendeeForMcuZte=(TerminalAttendeeForMcuZte)attendeeForMcuZte;
                int terType = 0;
                Integer callModel = 1;
                String remoteParty=attendeeForMcuZte.getRemoteParty();
                if (terminalAttendeeForMcuZte.getTerminalId() != null) {
                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuZte.getTerminalId());
                    if (busiTerminal != null) {

                        if (TerminalType.isFCMSIP(busiTerminal.getType())) {
                            FcmBridge fcmBridge = FcmBridgeCache.getInstance().get(busiTerminal.getFsServerId());
                            if (fcmBridge != null) {
                                Integer callPort = fcmBridge.getBusiFreeSwitch().getCallPort();
                                if (callPort != null) {
                                    remoteParty += ":" + callPort;
                                }
                                terType = 3;
                                callModel = 2;
                            }
                        }
                        if (TerminalType.isFSBC(busiTerminal.getType())) {
                            terType = 3;
                            callModel = 2;
                        }
                        if (TerminalType.isZTE(busiTerminal.getType())) {
                            terType = busiTerminal.getZteTerminalType();
                            callModel = busiTerminal.getCallmodel();
                        }
                        if (TerminalType.isIp(busiTerminal.getType())) {

                            callModel = 1;
                            terType = 0;
                        }
                    }
                }
                participants[i]= new Participant();
                participants[i].setTerminalNumber(remoteParty);
                participants[i].setTerminalName(attendeeForMcuZte.getName());
                participants[i].setIpAddress(attendeeForMcuZte.getIp());
                Integer protoType = attendeeForMcuZte.getProtoType();
                if(protoType==null){
                    protoType=0;
                }
                participants[i].setTerType(terType);
                participants[i].setCallMode(callModel);
                break;
            }
        }
        cmStartMrRequest.setParticipants(participants);
        cmStartMrRequest.setConferenceNumber(tc.getConferenceNumber()+"");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        cmStartMrRequest.setStartTime(calendar);
        CreateConferenceResponse cmStartMrResponse = mcuZteBridge.getConferenceManageApi().startMr(cmStartMrRequest);
        if (cmStartMrResponse != null && CommonResponse.STATUS_OK.equals(cmStartMrResponse.getResult())) {
          logger.info("zte 预约会议成功"+conferenceNumber);
          tc.setConfId(cmStartMrResponse.getConferenceIdentifier());
          busiMcuZteTemplateConferenceMapper.updateBusiMcuZteTemplateConference(tc);
        } else {
            if(Objects.equals("10001707",cmStartMrResponse.getResult())){
                throw new CustomException("会议预约时间小于规定时间");
            }
            logger.info("开始预约会议失败:"+cmStartMrResponse.getResult());
            throw new SystemException(1, "开始预约会议失败，请稍后再试！");
        }



        return conferenceNumber + "";

    }






}
