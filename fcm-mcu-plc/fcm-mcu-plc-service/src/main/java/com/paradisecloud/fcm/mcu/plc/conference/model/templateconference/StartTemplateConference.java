/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : StartTemplateConference.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.templateconference
 * @author sinhy 
 * @since 2021-09-22 21:12
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.plc.conference.model.templateconference;

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
import com.paradisecloud.fcm.mcu.plc.attendee.utils.McuPlcConferenceContextUtils;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcConferenceContextCache;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcBridgeCache;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcBridge;
import com.paradisecloud.fcm.mcu.plc.listener.MqttForMcuPlcPushMessageCache;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.AttendeeForMcuPlc;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.TerminalAttendeeForMcuPlc;
import com.paradisecloud.fcm.mcu.plc.model.request.cm.CmStartMrRequest;
import com.paradisecloud.fcm.mcu.plc.model.response.CommonResponse;
import com.paradisecloud.fcm.mcu.plc.model.response.cm.CmStartMrResponse;
import com.paradisecloud.fcm.mcu.plc.monitor.cache.MonitorThreadCache;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiHistoryConferenceForMcuPlcService;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcConferenceAppointmentService;
import com.paradisecloud.fcm.mcu.plc.task.McuPlcDelayTaskService;
import com.paradisecloud.fcm.mcu.plc.task.InviteAttendeesTask;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberService;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveDeptCache;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
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

        BusiMcuPlcTemplateConferenceMapper busiMcuPlcTemplateConferenceMapper = BeanFactory.getBean(BusiMcuPlcTemplateConferenceMapper.class);
        IBusiMcuPlcConferenceAppointmentService busiMcuPlcConferenceAppointmentService = BeanFactory.getBean(IBusiMcuPlcConferenceAppointmentService.class);
        IBusiHistoryConferenceForMcuPlcService busiHistoryConferenceForMcuPlcService = BeanFactory.getBean(IBusiHistoryConferenceForMcuPlcService.class);
        McuPlcDelayTaskService delayTaskService = BeanFactory.getBean(McuPlcDelayTaskService.class);
        // 获取模板会议实体对象
        BusiMcuPlcTemplateConference busiMcuPlcTemplateConference = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(templateConferenceId);
        // 获取会议上下文
        McuPlcConferenceContext conferenceContext = buildTemplateConferenceContext(templateConferenceId);
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

        if (busiMcuPlcTemplateConference.getLastConferenceId() != null) {
            BusiHistoryConference busiHistoryConference = busiHistoryConferenceForMcuPlcService.selectBusiHistoryConferenceById(busiMcuPlcTemplateConference.getLastConferenceId());
            if (busiHistoryConference != null) {
                Date endTime = busiHistoryConference.getConferenceEndTime();
                if (endTime != null) {
                    long diff = System.currentTimeMillis() - endTime.getTime();
                    if (diff < 5000) {
                        throw new SystemException(1009874, "该会议室刚结束会议，正在准备中，请等待" + (5 - diff / 1000) + "秒");
                    }
                }
            }
        }
        
        if (conferenceContext.getConferenceNumber() == null)
        {
            BusiConferenceNumber busiConferenceNumber = BeanFactory.getBean(IBusiConferenceNumberService.class).autoCreateConferenceNumber(conferenceContext.getDeptId(), McuType.MCU_PLC.getCode());
            busiMcuPlcTemplateConference.setConferenceNumber(busiConferenceNumber.getId());
            busiMcuPlcTemplateConferenceMapper.updateBusiMcuPlcTemplateConference(busiMcuPlcTemplateConference);
            conferenceContext.setConferenceNumber(busiConferenceNumber.getId().toString());
            if (conferenceContext.getIsAutoCreateStreamUrl() == 1) {
                String streamingUrl = createStreamingUrl(busiMcuPlcTemplateConference.getDeptId(), Long.valueOf(busiMcuPlcTemplateConference.getConferenceNumber()));
                logger.info("直播地址：" + streamingUrl);
                busiMcuPlcTemplateConference.setStreamUrl(streamingUrl);
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
            String streamingUrl = createStreamingUrl(busiMcuPlcTemplateConference.getDeptId(), Long.valueOf(conferenceContext.getTenantId() + conferenceContext.getConferenceNumber()));
            logger.info("直播地址：" + streamingUrl);
            busiMcuPlcTemplateConference.setStreamUrl(streamingUrl);
            conferenceContext.setStreamingUrl(streamingUrl);
        }

        int duration = -1;
        List<BusiMcuPlcConferenceAppointment> busiMcuPlcConferenceAppointmentList = busiMcuPlcConferenceAppointmentService.selectBusiMcuPlcConferenceAppointmentByTemplateId(templateConferenceId);
        if (busiMcuPlcConferenceAppointmentList != null && busiMcuPlcConferenceAppointmentList.size() > 0) {
            BusiMcuPlcConferenceAppointment busiMcuPlcConferenceAppointment = busiMcuPlcConferenceAppointmentList.get(0);
            if (busiMcuPlcConferenceAppointment.getEndTime() != null && busiMcuPlcConferenceAppointment.getEndTime().startsWith("9999")) {
                duration = 360000000;
            }
        }

        Integer muteType = busiMcuPlcTemplateConference.getMuteType();
        McuPlcBridge mcuPlcBridge = McuPlcBridgeCache.getInstance().getAvailableMcuPlcBridgesByDept(busiMcuPlcTemplateConference.getDeptId()).getMasterMcuPlcBridge();
        Integer bandwidthK = null;
        if (StringUtils.isEmpty(conferenceContext.getConferenceCtrlPassword())) {
            String conferenceCtrlPassword = generatePassword();
            Integer bandwidth = 1;
            bandwidthK = bandwidth * 1024;

            busiMcuPlcTemplateConference.setBandwidth(bandwidth);
            busiMcuPlcTemplateConference.setConferenceCtrlPassword(conferenceCtrlPassword);
            busiMcuPlcTemplateConferenceMapper.updateBusiMcuPlcTemplateConference(busiMcuPlcTemplateConference);
            conferenceContext.setConferenceCtrlPassword(conferenceCtrlPassword);
        }

        if (bandwidthK == null) {
            if (busiMcuPlcTemplateConference.getBandwidth() != null) {
                bandwidthK = busiMcuPlcTemplateConference.getBandwidth() * 1024;
            } else {
                bandwidthK = 1024;
            }
        }

        if (McuPlcConferenceContextCache.getInstance().getByConferenceName(busiMcuPlcTemplateConference.getName()) != null) {
            busiMcuPlcTemplateConference.setName(busiMcuPlcTemplateConference.getName() + "(" + busiMcuPlcTemplateConference.getConferenceNumber() + ")");
        }
        CmStartMrRequest cmStartMrRequest = new CmStartMrRequest();
        if (muteType != null && muteType == 1) {
            cmStartMrRequest.setMute(true);
        }
        cmStartMrRequest.setConferenceNum(busiMcuPlcTemplateConference.getConferenceNumber().toString());
        cmStartMrRequest.setName(busiMcuPlcTemplateConference.getName());
        cmStartMrRequest.setCtrlPassword(conferenceContext.getConferenceCtrlPassword());
        cmStartMrRequest.setPassword(conferenceContext.getConferencePassword());
        cmStartMrRequest.setTransferRate(bandwidthK);
        if ("1080".equals(busiMcuPlcTemplateConference.getHdResolution())) {
            cmStartMrRequest.setHdResolution("hd_1080");
        } else {
            cmStartMrRequest.setHdResolution("hd_720");
        }
        CmStartMrResponse cmStartMrResponse = mcuPlcBridge.getConferenceManageApi().startMr(cmStartMrRequest);
        if (cmStartMrResponse != null && CommonResponse.STATUS_OK.equals(cmStartMrResponse.getStatus())) {
            busiMcuPlcTemplateConference.setConfId(cmStartMrResponse.getId());
            conferenceContext.setConfId(cmStartMrResponse.getId());
        } else {
            throw new SystemException(1, "开始会议失败，请稍后再试！");
        }
        conferenceContext.setAttendeeOperation(conferenceContext.getDefaultViewOperation());
        logger.info("已设置默认布局：" + conferenceContext.getConferenceNumber());
        conferenceContext.setStart(true);
        conferenceContext.setStartTime(new Date());

        String callId = UUID.randomUUID().toString();

        // 保存历史记录
        BusiHistoryConference busiHistoryConference = busiHistoryConferenceForMcuPlcService.saveHistory(conferenceContext);
        conferenceContext.setHistoryConference(busiHistoryConference);

        // 历史call保存
        BusiHistoryCall busiHistoryCall = new BusiHistoryCall();
        busiHistoryCall.setCallId(callId);
        busiHistoryCall.setCoSpace(conferenceContext.getCoSpaceId());
        busiHistoryCall.setDeptId(conferenceContext.getDeptId());
        busiHistoryCall.setCreateTime(new Date());
        busiHistoryCall.setHistoryConferenceId(busiHistoryConference.getId());
        BeanFactory.getBean(BusiHistoryCallMapper.class).insertBusiHistoryCall(busiHistoryCall);

        busiMcuPlcTemplateConference.setLastConferenceId(busiHistoryConference.getId());// 存会议id
        busiMcuPlcTemplateConferenceMapper.updateBusiMcuPlcTemplateConference(busiMcuPlcTemplateConference);

        Long deptId = conferenceContext.getDeptId();
        List<String> streamUrlList = createStreamUrlList(deptId, conferenceContext.getConferenceNumber());
        if (streamUrlList != null && streamUrlList.size() > 0) {
            conferenceContext.setStreamUrlList(streamUrlList);
        }

        McuPlcConferenceContextCache.getInstance().add(conferenceContext);

        List<BusiTerminal> busiTerminalList = new ArrayList<>();
        List<TerminalAttendeeForMcuPlc> autoJoinTerminals = new ArrayList<>();
        List<AttendeeForMcuPlc> outBoundTerminals = new ArrayList<>();
        McuPlcConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            if (a instanceof TerminalAttendeeForMcuPlc) {
                TerminalAttendeeForMcuPlc ta = (TerminalAttendeeForMcuPlc) a;
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
        obj.put("tenantId", conferenceContext.getTenantId());
        obj.put("conferenceNumber", conferenceContext.getConferenceNumber());
        obj.put("streamingUrl", conferenceContext.getStreamingUrl());
        obj.put("streamingUrlList", conferenceContext.getStreamUrlList());
        McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_STARTED, obj);
        McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, message);

        if (MqttForMcuPlcPushMessageCache.getInstance().getMqttForMcuPlcPushMessageListener() != null) {
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
                            MqttForMcuPlcPushMessageCache.getInstance().getMqttForMcuPlcPushMessageListener().onPushMessage(ResponseInfo.CODE_200, "会议启动成功！", topic, action, jsonObject, clientId, "");
                        }
                    }
                }
            }
        }

        // 处理终端业务
        doTerminal(conferenceContext, autoJoinTerminals, outBoundTerminals);

        return conferenceContext.getContextKey();
    }

    private void doTerminal(McuPlcConferenceContext conferenceContext, List<TerminalAttendeeForMcuPlc> autoJoinTerminals, List<AttendeeForMcuPlc> outBoundTerminals) {

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
    private void doOutBoundTerminal(List<AttendeeForMcuPlc> outBoundTerminals, McuPlcConferenceContext conferenceContext) {
        McuPlcDelayTaskService delayTaskService = BeanFactory.getBean(McuPlcDelayTaskService.class);
        InviteAttendeesTask inviteAttendeesTask = new InviteAttendeesTask(conferenceContext.getId(), 1000, conferenceContext, outBoundTerminals);
        delayTaskService.addTask(inviteAttendeesTask);
    }

    /**
     * 处理自动主叫终端业务
     *
     * @author sinhy
     * @since 2021-11-18 17:20
     */
    private void doAutoJoinTerminal(List<TerminalAttendeeForMcuPlc> autoJoinTerminals, McuPlcConferenceContext conferenceContext) {
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
                mqttService.pushConferenceInfo(conferenceContext.getConferenceNumber(), conferenceContext.getConferencePassword(), autoJoinTerminals, conferenceContext.getLiveTerminals());
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

}
