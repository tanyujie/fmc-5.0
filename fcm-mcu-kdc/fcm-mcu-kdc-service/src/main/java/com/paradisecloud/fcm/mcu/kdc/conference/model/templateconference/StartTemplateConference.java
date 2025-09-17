/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : StartTemplateConference.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.templateconference
 * @author sinhy 
 * @since 2021-09-22 21:12
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.kdc.conference.model.templateconference;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.common.utils.uuid.UUID;
import com.paradisecloud.fcm.common.constant.MqttConfigConstant;
import com.paradisecloud.fcm.common.constant.ResponseInfo;
import com.paradisecloud.fcm.common.constant.TerminalTopic;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mcu.kdc.attendee.utils.McuKdcConferenceContextUtils;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcConferenceContextCache;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcBridgeCache;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcBridge;
import com.paradisecloud.fcm.mcu.kdc.listener.MqttForMcuKdcPushMessageCache;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.AttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.TerminalAttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.model.request.cm.CmStartMrRequest;
import com.paradisecloud.fcm.mcu.kdc.model.response.cm.CmStartMrResponse;
import com.paradisecloud.fcm.mcu.kdc.monitor.cache.MonitorThreadCache;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiHistoryConferenceForMcuKdcService;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcConferenceAppointmentService;
import com.paradisecloud.fcm.mcu.kdc.task.McuKdcDelayTaskService;
import com.paradisecloud.fcm.mcu.kdc.task.InviteAttendeesTask;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
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

        BusiMcuKdcTemplateConferenceMapper busiMcuKdcTemplateConferenceMapper = BeanFactory.getBean(BusiMcuKdcTemplateConferenceMapper.class);
        IBusiMcuKdcConferenceAppointmentService busiMcuKdcConferenceAppointmentService = BeanFactory.getBean(IBusiMcuKdcConferenceAppointmentService.class);
        IBusiHistoryConferenceForMcuKdcService busiHistoryConferenceForMcuKdcService = BeanFactory.getBean(IBusiHistoryConferenceForMcuKdcService.class);
        McuKdcDelayTaskService delayTaskService = BeanFactory.getBean(McuKdcDelayTaskService.class);
        // 获取模板会议实体对象
        BusiMcuKdcTemplateConference busiMcuKdcTemplateConference = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(templateConferenceId);
        // 获取会议上下文
        McuKdcConferenceContext conferenceContext = buildTemplateConferenceContext(templateConferenceId);
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

        if (busiMcuKdcTemplateConference.getLastConferenceId() != null) {
            BusiHistoryConference busiHistoryConference = busiHistoryConferenceForMcuKdcService.selectBusiHistoryConferenceById(busiMcuKdcTemplateConference.getLastConferenceId());
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

        int duration = -1;
        List<BusiMcuKdcConferenceAppointment> busiMcuKdcConferenceAppointmentList = busiMcuKdcConferenceAppointmentService.selectBusiMcuKdcConferenceAppointmentByTemplateId(templateConferenceId);
        if (busiMcuKdcConferenceAppointmentList != null && busiMcuKdcConferenceAppointmentList.size() > 0) {
            BusiMcuKdcConferenceAppointment busiMcuKdcConferenceAppointment = busiMcuKdcConferenceAppointmentList.get(0);
            if (busiMcuKdcConferenceAppointment.getEndTime() != null && busiMcuKdcConferenceAppointment.getEndTime().startsWith("9999")) {
                duration = 360000000;
            }
        }

        Integer muteType = busiMcuKdcTemplateConference.getMuteType();
        McuKdcBridge mcuKdcBridge = McuKdcBridgeCache.getInstance().getAvailableMcuKdcBridgesByDept(busiMcuKdcTemplateConference.getDeptId()).getMasterMcuKdcBridge();

        if (mcuKdcBridge.getUsedResourceCount() >= mcuKdcBridge.getSystemResourceCount()) {
            throw new SystemException(1, "MCU资源已耗尽，请关闭一些会议后重试。");
        }

        Integer bandwidthK = null;
        if (StringUtils.isEmpty(conferenceContext.getConferenceCtrlPassword())) {
            String conferenceCtrlPassword = generatePassword();
            Integer bandwidth = 1;
            bandwidthK = bandwidth * 1024;

            busiMcuKdcTemplateConference.setBandwidth(bandwidth);
            busiMcuKdcTemplateConference.setConferenceCtrlPassword(conferenceCtrlPassword);
            busiMcuKdcTemplateConferenceMapper.updateBusiMcuKdcTemplateConference(busiMcuKdcTemplateConference);
            conferenceContext.setConferenceCtrlPassword(conferenceCtrlPassword);
        }

        if (bandwidthK == null) {
            if (busiMcuKdcTemplateConference.getBandwidth() != null) {
                bandwidthK = busiMcuKdcTemplateConference.getBandwidth() * 1024;
            } else {
                bandwidthK = 1024;
            }
        }
        Integer resolution = 12;
        String hdResolution = busiMcuKdcTemplateConference.getHdResolution();
        if ("1080".equals(hdResolution)) {
            resolution = 13;
        } else if ("4k".equals(hdResolution)) {
            resolution = 16;
        }

        if (McuKdcConferenceContextCache.getInstance().getByConferenceName(busiMcuKdcTemplateConference.getName()) != null) {
            busiMcuKdcTemplateConference.setName(busiMcuKdcTemplateConference.getName() + "(" + busiMcuKdcTemplateConference.getConferenceNumber() + ")");
        }
        CmStartMrRequest cmStartMrRequest = CmStartMrRequest.buildDefaultRequest();
        cmStartMrRequest.setMute(0);
        cmStartMrRequest.setName(busiMcuKdcTemplateConference.getName());
        cmStartMrRequest.setPassword(conferenceContext.getConferencePassword());
        cmStartMrRequest.setBitrate(bandwidthK);
        List<CmStartMrRequest.VideoFormat> videoFormatList = new ArrayList<>();
        {
            CmStartMrRequest.VideoFormat videoFormat = new CmStartMrRequest.VideoFormat();
            videoFormat.setFormat(5);
            videoFormat.setResolution(resolution);
            videoFormat.setFrame(30);
            videoFormat.setBitrate(bandwidthK);
            videoFormatList.add(videoFormat);
        }
        {
            CmStartMrRequest.VideoFormat videoFormat = new CmStartMrRequest.VideoFormat();
            videoFormat.setFormat(4);
            videoFormat.setResolution(resolution);
            videoFormat.setFrame(30);
            videoFormat.setBitrate(bandwidthK);
            videoFormatList.add(videoFormat);
        }
        {
            CmStartMrRequest.VideoFormat videoFormat = new CmStartMrRequest.VideoFormat();
            videoFormat.setFormat(6);
            videoFormat.setResolution(resolution);
            videoFormat.setFrame(30);
            videoFormat.setBitrate(bandwidthK);
            videoFormatList.add(videoFormat);
        }
        cmStartMrRequest.setVideo_formats(videoFormatList);
        CmStartMrResponse cmStartMrResponse = mcuKdcBridge.getConferenceManageApi().startMr(cmStartMrRequest);
        if (cmStartMrResponse != null && cmStartMrResponse.isSuccess()) {
            busiMcuKdcTemplateConference.setConfId(cmStartMrResponse.getConf_id());
            busiMcuKdcTemplateConference.setConferenceNumber(Long.valueOf(cmStartMrResponse.getConf_id()));
            conferenceContext.setConfId(cmStartMrResponse.getConf_id());
            conferenceContext.setConferenceNumber(cmStartMrResponse.getConf_id());
            conferenceContext.setId(EncryptIdUtil.generateConferenceId(conferenceContext.getContextKey()));
        } else {
            if (cmStartMrResponse != null && cmStartMrResponse.getError_code() == 21758) {
                throw new SystemException(1, "MCU资源已耗尽，请关闭一些会议后重试。");
            }
            throw new SystemException(1, "开始会议失败，请稍后再试！");
        }

        if (conferenceContext.getConferenceNumber() != null) {
            String conferenceRemoteParty = conferenceContext.getTenantId() + conferenceContext.getConferenceNumber() + "@" + conferenceContext.getMcuCallIp();
            if (conferenceContext.getMcuCallPort() != null && conferenceContext.getMcuCallPort() != 5060) {
                conferenceRemoteParty += ":" + conferenceContext.getMcuCallPort();
            }
            conferenceContext.setConferenceRemoteParty(conferenceRemoteParty);
        }
        if (conferenceContext.getIsAutoCreateStreamUrl() == 1 && StringUtils.isEmpty(conferenceContext.getStreamingUrl())) {
            String streamingUrl = createStreamingUrl(busiMcuKdcTemplateConference.getDeptId(), Long.valueOf(conferenceContext.getTenantId() + conferenceContext.getConferenceNumber()));
            logger.info("直播地址：" + streamingUrl);
            busiMcuKdcTemplateConference.setStreamUrl(streamingUrl);
            conferenceContext.setStreamingUrl(streamingUrl);
        }
        conferenceContext.setAttendeeOperation(conferenceContext.getDefaultViewOperation());
        logger.info("已设置默认布局：" + conferenceContext.getConferenceNumber());
        conferenceContext.setStart(true);
        conferenceContext.setStartTime(new Date());

        String callId = UUID.randomUUID().toString();

        // 保存历史记录
        BusiHistoryConference busiHistoryConference = busiHistoryConferenceForMcuKdcService.saveHistory(conferenceContext);
        conferenceContext.setHistoryConference(busiHistoryConference);

        // 历史call保存
        BusiHistoryCall busiHistoryCall = new BusiHistoryCall();
        busiHistoryCall.setCallId(callId);
        busiHistoryCall.setCoSpace(conferenceContext.getCoSpaceId());
        busiHistoryCall.setDeptId(conferenceContext.getDeptId());
        busiHistoryCall.setCreateTime(new Date());
        busiHistoryCall.setHistoryConferenceId(busiHistoryConference.getId());
        BeanFactory.getBean(BusiHistoryCallMapper.class).insertBusiHistoryCall(busiHistoryCall);

        busiMcuKdcTemplateConference.setLastConferenceId(busiHistoryConference.getId());// 存会议id
        busiMcuKdcTemplateConferenceMapper.updateBusiMcuKdcTemplateConference(busiMcuKdcTemplateConference);

        Long deptId = conferenceContext.getDeptId();
        List<String> streamUrlList = createStreamUrlList(deptId, conferenceContext.getConferenceNumber());
        if (streamUrlList != null && streamUrlList.size() > 0) {
            conferenceContext.setStreamUrlList(streamUrlList);
        }

        McuKdcConferenceContextCache.getInstance().add(conferenceContext);

        List<BusiTerminal> busiTerminalList = new ArrayList<>();
        List<TerminalAttendeeForMcuKdc> autoJoinTerminals = new ArrayList<>();
        List<AttendeeForMcuKdc> outBoundTerminals = new ArrayList<>();
        McuKdcConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            if (a instanceof TerminalAttendeeForMcuKdc) {
                TerminalAttendeeForMcuKdc ta = (TerminalAttendeeForMcuKdc) a;
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
        obj.put("tenantId", conferenceContext.getTenantId());
        obj.put("conferenceNumber", conferenceContext.getConferenceNumber());
        obj.put("streamingUrl", conferenceContext.getStreamingUrl());
        obj.put("streamingUrlList", conferenceContext.getStreamUrlList());
        McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_STARTED, obj);
        McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, message);

        if (MqttForMcuKdcPushMessageCache.getInstance().getMqttForMcuKdcPushMessageListener() != null) {
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
                            MqttForMcuKdcPushMessageCache.getInstance().getMqttForMcuKdcPushMessageListener().onPushMessage(ResponseInfo.CODE_200, "会议启动成功！", topic, action, jsonObject, clientId, "");
                        }
                    }
                }
            }
        }

        // 处理终端业务
        doTerminal(conferenceContext, autoJoinTerminals, outBoundTerminals);

        return conferenceContext.getContextKey();
    }

    private void doTerminal(McuKdcConferenceContext conferenceContext, List<TerminalAttendeeForMcuKdc> autoJoinTerminals, List<AttendeeForMcuKdc> outBoundTerminals) {

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
    private void doOutBoundTerminal(List<AttendeeForMcuKdc> outBoundTerminals, McuKdcConferenceContext conferenceContext) {
        McuKdcDelayTaskService delayTaskService = BeanFactory.getBean(McuKdcDelayTaskService.class);
        InviteAttendeesTask inviteAttendeesTask = new InviteAttendeesTask(conferenceContext.getId(), 1000, conferenceContext, outBoundTerminals);
        delayTaskService.addTask(inviteAttendeesTask);
    }

    /**
     * 处理自动主叫终端业务
     *
     * @author sinhy
     * @since 2021-11-18 17:20
     */
    private void doAutoJoinTerminal(List<TerminalAttendeeForMcuKdc> autoJoinTerminals, McuKdcConferenceContext conferenceContext) {
        // 自动主叫终端由mqtt下发入会邀请后主叫入会
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                IMqttService mqttForMcuKdcService = BeanFactory.getBean(IMqttService.class);
                mqttForMcuKdcService.pushConferenceInfo(conferenceContext.getId(), conferenceContext.getConferencePassword(), autoJoinTerminals, conferenceContext.getLiveTerminals());
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
