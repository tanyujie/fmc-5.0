/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : StartTemplateConference.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.templateconference
 * @author sinhy
 * @since 2021-09-22 21:12
 * @version  V1.0
 */
package com.paradisecloud.smc3.busi.templateconference;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.com.fcm.smc.modle.response.UserInfoRep;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.common.utils.uuid.UUID;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberService;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.smc3.busi.ConferenceNode;
import com.paradisecloud.smc3.busi.DefaultAttendeeOperation;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.TerminalAttendeeSmc3;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextUtils;
import com.paradisecloud.smc3.busi.utils.Smc3WebSocketMessagePusher;
import com.paradisecloud.smc3.model.*;
import com.paradisecloud.smc3.model.request.CasecadeTemplateRequest;
import com.paradisecloud.smc3.model.request.TemplateNode;
import com.paradisecloud.smc3.model.request.TemplateNodeTemp;
import com.paradisecloud.smc3.model.response.*;
import com.paradisecloud.smc3.monitor.ConferenceParticipantSmc3SyncThread;
import com.paradisecloud.smc3.service.interfaces.IBusiConferenceNumberSmc3Service;
import com.paradisecloud.smc3.service.interfaces.IBusiMcuSmc3HistoryConferenceService;
import com.paradisecloud.smc3.task.InviteAttendeeSmc3Task;
import com.paradisecloud.smc3.task.Smc3DelayTaskService;
import com.paradisecloud.smc3.utils.AuthenticationUtil;
import com.paradisecloud.smc3.utils.BusiTerminalUtils;
import com.paradisecloud.smc3.utils.UTCTimeFormatUtil;
import com.paradisecloud.smc3.websocket.client.SMC3WebsocketClient;
import com.paradisecloud.smc3.websocket.client.Smc3WebSocketProcessor;
import com.paradisecloud.smc3.websocket.client.Smc3WebsocketContext;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author Administrator
 */
public class StartTemplateConference extends BuildTemplateConferenceContext {

    public static final int RATE_NUMBER = 8;
    public static final String MAIN_MCU_ID = "mainMcuId";
    public static final String MAIN_MCU_NAME = "mainMcuName";
    public static final String MAIN_SERVICE_ZONE_ID = "mainServiceZoneId";
    public static final String MAIN_SERVICE_ZONE_NAME = "mainServiceZoneName";
    public static final String AUDIO_PROTOCOL = "audioProtocol";
    public static final int DIFF_TIME = 10000;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static String localToUTC(String localTimeStr) {
        try {
            Date localDate = getLocalSDF().parse(localTimeStr);
            return getUTCSDF().format(localDate) + " UTC";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static SimpleDateFormat getLocalSDF() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    private static SimpleDateFormat getUTCSDF() {
        SimpleDateFormat utcSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        utcSDF.setTimeZone(TimeZone.getTimeZone("UTC"));
        return utcSDF;
    }

    public synchronized String startTemplateConference(long templateConferenceId) {
        logger.info("模板会议启动入口：" + templateConferenceId);
        // 获取会议上下文
        Smc3ConferenceContext conferenceContext = buildTemplateConferenceContext(templateConferenceId);
        if (conferenceContext == null) {
            return null;
        }

        // 启动前校验会议是否已开始
        if (conferenceContext.isStart()) {
            if (conferenceContext.isEnd()) {
                throw new SystemException(1009874, "会议正在结束，请稍后重试");
            } else {
                throw new SystemException(1009874, "会议已开始，请勿重复开始");
            }
        }

        try {
            BusiMcuSmc3TemplateConference tc = BeanFactory.getBean(BusiMcuSmc3TemplateConferenceMapper.class).selectBusiMcuSmc3TemplateConferenceById(templateConferenceId);
            if (Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())) {
                List<TemplateNodeTemp> nodesTemplateTemp_local = JSON.parseArray(tc.getCascadeNodesTemp(), TemplateNodeTemp.class);
                for (TemplateNodeTemp templateNodeTemp : nodesTemplateTemp_local) {

                    if(templateNodeTemp.getTemplateId() != null){
                        BusiMcuSmc3TemplateConference sunTc = BeanFactory.getBean(BusiMcuSmc3TemplateConferenceMapper.class).selectBusiMcuSmc3TemplateConferenceById(templateNodeTemp.getTemplateId());
                        if (sunTc.getLastConferenceId() != null) {
                            BusiHistoryConference busiHistoryConference = BeanFactory.getBean(IBusiMcuSmc3HistoryConferenceService.class).selectBusiHistoryConferenceById(Long.valueOf(sunTc.getLastConferenceId()));
                            if (busiHistoryConference != null) {
                                Date endTime = busiHistoryConference.getConferenceEndTime();
                                if (endTime != null) {
                                    long diff = System.currentTimeMillis() - endTime.getTime();
                                    if (diff < 15000) {
                                        throw new SystemException(1009874, "该会议室刚结束会议，正在准备中，请等待" + (15 - diff / 1000) + "秒");
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
            } else {
                if (tc.getLastConferenceId() != null) {
                    BusiHistoryConference busiHistoryConference = BeanFactory.getBean(IBusiMcuSmc3HistoryConferenceService.class).selectBusiHistoryConferenceById(Long.valueOf(tc.getLastConferenceId()));
                    if (busiHistoryConference != null) {
                        Date endTime = busiHistoryConference.getConferenceEndTime();
                        if (endTime != null) {
                            long diff = System.currentTimeMillis() - endTime.getTime();
                            if (diff < 15000) {
                                throw new SystemException(1009874, "该会议室刚结束会议，正在准备中，请等待" + (15 - diff / 1000) + "秒");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }

        if(Objects.equals(ConstAPI.NORMAL,conferenceContext.getCategory())){
            if (conferenceContext.getConferenceNumber() == null) {
                BusiConferenceNumber busiConferenceNumber = BeanFactory.getBean(IBusiConferenceNumberSmc3Service.class).autoCreateConferenceNumber(conferenceContext.getDeptId());
                if (busiConferenceNumber == null) {
                    logger.error("会议号码创建错误,模板ID:" + templateConferenceId);
                    throw new CustomException("会议号码创建错误,模板ID:" + templateConferenceId);
                }
                // 获取模板会议实体对象
                BusiMcuSmc3TemplateConference tc = BeanFactory.getBean(BusiMcuSmc3TemplateConferenceMapper.class).selectBusiMcuSmc3TemplateConferenceById(templateConferenceId);
                tc.setConferenceNumber(busiConferenceNumber.getId());
                BeanFactory.getBean(BusiMcuSmc3TemplateConferenceMapper.class).updateBusiMcuSmc3TemplateConference(tc);
                logger.info("自动生成会议号成功：" + busiConferenceNumber.getId());
                conferenceContext.setConferenceNumber(busiConferenceNumber.getId() + "");
            }
        }
        conferenceContext.setTemplateConferenceId(templateConferenceId);
        deleteSmcTemplate(conferenceContext);
        createSmcTemplate(conferenceContext);
        try {
            // 开始模板会议
            return startTemplateConference(conferenceContext);
        } catch (Exception e) {

            throw e;
        }
    }

    public synchronized String startTemplateConference(long templateConferenceId, String conferenceId) {
        return startTemplateConference(templateConferenceId, conferenceId, false);
    }

    public synchronized String startTemplateConference(long templateConferenceId, String conferenceId, boolean downCascade) {
        logger.info("模板会议启动入口：" + templateConferenceId);
        // 获取会议上下文
        Smc3ConferenceContext conferenceContext = buildTemplateConferenceContext(templateConferenceId);
        if (conferenceContext == null) {
            return null;
        }

        // 启动前校验会议是否已开始
        if (conferenceContext.isStart()) {
            throw new SystemException(1009874, "会议"+conferenceContext.getId()+"已开始，请勿重复开始");
        }

        conferenceContext.setTemplateConferenceId(templateConferenceId);
        if (!downCascade) {
            deleteSmcTemplate(conferenceContext);
            createSmcTemplate(conferenceContext);
        }
        try {
            // 开始模板会议
            return startTemplateConference(conferenceContext, conferenceId);
        } catch (Exception e) {

            throw e;
        }
    }

    /**
     * <pre>开始模板会议</pre>
     *
     * @param conferenceContext void
     * @author lilinhai
     * @since 2021-02-03 12:57
     */
    private synchronized String startTemplateConference(Smc3ConferenceContext conferenceContext) {
        logger.info("模板会议开始启动：" + conferenceContext.getConferenceNumber());

        Long deptId = conferenceContext.getDeptId();

        BusiMcuSmc3TemplateConference tc = BeanFactory.getBean(BusiMcuSmc3TemplateConferenceMapper.class).selectBusiMcuSmc3TemplateConferenceById(conferenceContext.getTemplateConferenceId());

        BusiHistoryConferenceMapper busiHistoryConferenceMapper = BeanFactory.getBean(BusiHistoryConferenceMapper.class);


        if (tc.getLastConferenceId() != null) {
            BusiHistoryConference busiHistoryConference = busiHistoryConferenceMapper.selectBusiHistoryConferenceById(Long.valueOf(tc.getLastConferenceId()));
            if (busiHistoryConference != null) {
                Date endTime = busiHistoryConference.getConferenceEndTime();
                if (endTime != null) {
                    long diff = System.currentTimeMillis() - endTime.getTime();
                    if (diff < DIFF_TIME) {
                        Threads.sleep(1000);
                    }
                }
            }
        }

        Smc3Bridge bridgesByDept = Smc3BridgeCache.getInstance().getBridgesByDept(deptId);
        if (bridgesByDept == null) {
            throw new CustomException("未找到可用MCU");
        }
        conferenceContext.setSmc3Bridge(bridgesByDept);


        try {
            if(Objects.equals(ConstAPI.CASCADE,tc.getCategory())){

                List<TemplateNode> nodesTemplate_local = JSON.parseArray(tc.getCascadeNodes(), TemplateNode.class);
                for (TemplateNode templateNode : nodesTemplate_local) {

                    if(Strings.isBlank(templateNode.getTemplateId())){
                        BusiMcuSmc3TemplateConference sunTc = BeanFactory.getBean(BusiMcuSmc3TemplateConferenceMapper.class).selectBusiMcuSmc3TemplateConferenceBySmcTemplateId(templateNode.getTemplateId());
                        String key= EncryptIdUtil.generateContextKey(Long.valueOf(sunTc.getId()), McuType.SMC3);
                        Smc3ConferenceContext sun = Smc3ConferenceContextCache.getInstance().get(key);
                        if(sun!=null){
                            throw new CustomException("开启多级会议失败,多级会议中的模板正在被其它会议使用");
                        }
                    }
                }

//                List<TemplateNodeTemp> nodesTemplateTemp_local = JSON.parseArray(tc.getCascadeNodesTemp(), TemplateNodeTemp.class);
//                for (TemplateNodeTemp templateNodeTemp : nodesTemplateTemp_local) {
//
//                    if(templateNodeTemp.getTemplateId() != null){
//                        BusiMcuSmc3TemplateConference sunTc = BeanFactory.getBean(BusiMcuSmc3TemplateConferenceMapper.class).selectBusiMcuSmc3TemplateConferenceById(templateNodeTemp.getTemplateId());
//                        String key= EncryptIdUtil.generateContextKey(Long.valueOf(sunTc.getId()), McuType.SMC3);
//                        Smc3ConferenceContext sun = Smc3ConferenceContextCache.getInstance().get(key);
//                        if(sun!=null){
//                            throw new CustomException("开启多级会议失败,多级会议中的模板正在被其它会议使用");
//                        }
//                    }
//                }


                conferenceContext.setCategory(ConstAPI.CASCADE);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("instant",true);
                jsonObject.put("duration",Integer.MAX_VALUE);
                jsonObject.put("scheduleStartTime",localToUTC(DateUtil.convertDateToString(new Date(), null)));

                String res= bridgesByDept.getSmcConferencesTemplateInvoker().startCascadeConferencesTemplateById(tc.getCascadeId(),JSONObject.toJSONString(jsonObject),bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                if(res.contains(ConstAPI.TOKEN_NOT_EXIST)){
                    throw new CustomException("开始模板会议失败:token不存在");
                }
                if (res != null&& res.contains(ConstAPI.ERRO)) {
                   SmcErrorResponse smcErrorResponse = JSON.parseObject(res, SmcErrorResponse.class);
                    throw new CustomException("开始模板会议:" + smcErrorResponse.getErrorDesc());
                }
                SmcCreateTemplateRep.ConferenceDTO conferenceDTO = JSONObject.parseObject(res, SmcCreateTemplateRep.ConferenceDTO.class);

                if(conferenceDTO==null){
                    throw new CustomException("开始模板会议错误");
                }
                conferenceContext.setSmc3conferenceId(conferenceDTO.getId());
                String accessCode = conferenceDTO.getAccessCode();
                conferenceContext.setConferenceNumber( accessCode.substring(1));
                Threads.sleep(5000);
                //获取tree
                nodeTree(conferenceContext, tc.getName(),tc.getId(), bridgesByDept, nodesTemplate_local, conferenceDTO.getId());

                return conferenceContext.getContextKey();
            }else {
                BusiConferenceNumber busiConferenceNumber = BeanFactory.getBean(BusiConferenceNumberMapper.class).selectBusiConferenceNumberById(Long.parseLong(conferenceContext.getConferenceNumber()));

                createConferenceMcu(conferenceContext, tc, bridgesByDept,busiConferenceNumber);
                conferenceContext.setCategory(ConstAPI.NORMAL);
            }


        } catch (Exception e) {
            logger.error("会议启动失败：" + conferenceContext.getConferenceNumber(), e);
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, "会议启动失败：" + e.getMessage());

            throw new CustomException("开始会议错误" + e.getMessage());
        }


        // 添加发起方会议上下文
        List<BaseAttendee> mqttJoinTerminals = new ArrayList<>();
        List<AttendeeSmc3> outAttendees= new ArrayList<>();
        Smc3ConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            if (a instanceof TerminalAttendeeSmc3) {
                TerminalAttendeeSmc3 ta = (TerminalAttendeeSmc3) a;
                BusiTerminal bt = TerminalCache.getInstance().get(ta.getTerminalId());

                // 被叫由会控负责发起呼叫
                if (AttendType.convert(ta.getAttendType()) == AttendType.OUT_BOUND) {
                    if (!ta.isMeetingJoined()) {
                        outAttendees.add(a);
                    }
                }

                if (AttendType.convert(ta.getAttendType()) == AttendType.AUTO_JOIN) {
                    if (!ObjectUtils.isEmpty(bt.getSn())) {
                        if (!ta.isMeetingJoined()) {
                            // 自动主叫终端由mqtt下发入会邀请后主叫入会
                            mqttJoinTerminals.add(ta);
                        }
                    }
                }
            }

        });
        // 处理mqtt业务
        doMqttService(mqttJoinTerminals,conferenceContext);
        doCall(conferenceContext,outAttendees);

        // 保存历史记录
        BusiHistoryConference busiHistoryConference = saveHistory(conferenceContext);
        tc.setLastConferenceId(String.valueOf(busiHistoryConference.getId()));
        BeanFactory.getBean(BusiMcuSmc3TemplateConferenceMapper.class).updateBusiMcuSmc3TemplateConference(tc);

        String message = "会议【" + conferenceContext.getName() + "】启动成功！";
        Map<String, Object> obj = new HashMap<>();
        obj.put("startTime", conferenceContext.getStartTime());
        obj.put("message", message);
        obj.put("conferenceNumber", tc.getConferenceNumber());
        obj.put("streamingUrl", conferenceContext.getStreamingUrl());
        obj.put("streamingUrlList", conferenceContext.getStreamUrlList());
        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_STARTED, obj);
        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, message);
        Smc3ConferenceContextCache.getInstance().add(conferenceContext);
        conferenceContext.setAttendeeOperation(conferenceContext.getDefaultViewOperation());

        if(conferenceContext.getConferenceNumber()!=null){
            String conferenceRemoteParty = conferenceContext.getTenantId()==null?"":conferenceContext.getTenantId() + conferenceContext.getConferenceNumber() + "@" + conferenceContext.getMcuCallIp();
            if (conferenceContext.getMcuCallPort() != null && conferenceContext.getMcuCallPort() != 5060) {
                conferenceRemoteParty += ":" + conferenceContext.getMcuCallPort();
            }
            conferenceContext.setConferenceRemoteParty(conferenceRemoteParty);
        }

        SmcConferenceTemplate.ConfPresetParamDTO confPresetParamDTO = conferenceContext.getConfPresetParamDTO();
        if(confPresetParamDTO!=null){
            List<PresetMultiPicReqDto> presetMultiPics = confPresetParamDTO.getPresetMultiPics();
            if(CollectionUtils.isNotEmpty(presetMultiPics)){
                JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(confPresetParamDTO));
                DefaultAttendeeOperation defaultViewOperation = new DefaultAttendeeOperation(conferenceContext, jsonObject,1);
                conferenceContext.setDefaultViewOperation(defaultViewOperation);
            }
        }
        try {
            ConferenceParticipantSmc3SyncThread conferenceParticipantSmc3SyncThread = new ConferenceParticipantSmc3SyncThread(conferenceContext);
            conferenceParticipantSmc3SyncThread.start();
            conferenceParticipantSmc3SyncThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("已设置默认布局：" + conferenceContext.getConferenceNumber());
        conferenceContext.getDefaultViewOperation().operate();
        logger.info("默认布局已开始运行：" + conferenceContext.getConferenceNumber());
        conferenceContext.setStart(true);
        conferenceContext.setStartTime(new Date());
        return conferenceContext.getContextKey();

    }

    private void nodeTree(Smc3ConferenceContext conferenceContext,String tcName,Long tcId, Smc3Bridge bridgesByDept, List<TemplateNode> nodesTemplate_local, String conferenceID) {
        String casTemplate= bridgesByDept.getSmcConferencesTemplateInvoker().getConferencesCascadeTree(conferenceID, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        List<Smc3ConferenceContext> conferenceContextList = new ArrayList<>();
        List<TemplateNode> nodes = JSON.parseArray(casTemplate, TemplateNode.class);
        List<ConferenceNode> conferenceNodeTree = new ArrayList<>();
        for (TemplateNode templateNode : nodes) {
            ConferenceNode conferenceNode = new ConferenceNode();
            conferenceNode.setName(templateNode.getSubject());
            Optional<TemplateNode> first = nodesTemplate_local.stream().filter(s -> Objects.equals(tcName + "-" + s.getSubject(), templateNode.getSubject())).findFirst();
            if(first.isPresent()){
                String templateId_sun = first.get().getTemplateId();
                BusiMcuSmc3TemplateConference sunTc = BeanFactory.getBean(BusiMcuSmc3TemplateConferenceMapper.class).selectBusiMcuSmc3TemplateConferenceBySmcTemplateId(templateId_sun);
                try {
                    String s = new StartConference().startConference(sunTc.getId(), templateNode.getTemplateId(), true);
                    Smc3ConferenceContext conferenceContext_sub = Smc3ConferenceContextCache.getInstance().get(s);
                    conferenceNode.setConferenceId(conferenceContext_sub.getId());

                    conferenceContext_sub.setCascadeConferenceTree(conferenceNodeTree);

                    Map<String, SMC3WebsocketClient> smcWebsocketClientMap = Smc3WebsocketContext.getSmcWebsocketClientMap();
                    SMC3WebsocketClient smc3WebsocketClient = smcWebsocketClientMap.get(bridgesByDept.getBridgeIp());
                    if (smc3WebsocketClient != null) {
                        Smc3WebSocketProcessor webSocketProcessor = smc3WebsocketClient.getWebSocketProcessor();
                        webSocketProcessor.firstSubscription(conferenceContext_sub.getSmc3conferenceId());
                    }
                    conferenceContext_sub.setCategory(ConstAPI.CASCADE);
                    if (Strings.isNotBlank(templateNode.getParentTemplateId())) {
                        String parentTemplateId = first.get().getParentTemplateId();
                        BusiMcuSmc3TemplateConference parenT = BeanFactory.getBean(BusiMcuSmc3TemplateConferenceMapper.class).selectBusiMcuSmc3TemplateConferenceBySmcTemplateId(parentTemplateId);
                        conferenceNode.setParentConferenceId(EncryptIdUtil.generateConferenceId(parenT.getId(), McuType.SMC3.getCode()));
                        String casTemplateSub = bridgesByDept.getSmcConferencesTemplateInvoker().getConferencesCascadeTree(templateNode.getTemplateId(), bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                        List<TemplateNode> nodesSub = JSON.parseArray(casTemplateSub, TemplateNode.class);
                        conferenceContext_sub.setCascadeTree(nodesSub);
                        conferenceContext_sub.setParentConferenceId(conferenceID);
                        conferenceContext_sub.setParentConferenceContextKey(EncryptIdUtil.generateContextKey(parenT.getId(), McuType.SMC3));
                        conferenceContextList.add(conferenceContext_sub);
                    } else {
                        conferenceContext_sub.setCascadeTree(nodes);
                        conferenceContext_sub.setCascadeLocalTemplateId(tcId);
                        Smc3ConferenceContextCache.getInstance().put(conferenceContext.getContextKey(), conferenceContext_sub);
                    }
                    BusiHistoryConference busiHistoryConference = saveHistory(conferenceContext_sub);
                    sunTc.setLastConferenceId(busiHistoryConference.getId().toString());
                    BeanFactory.getBean(BusiMcuSmc3TemplateConferenceMapper.class).updateBusiMcuSmc3TemplateConference(sunTc);
                } catch (Exception e) {
                }
            }
            conferenceNodeTree.add(conferenceNode);
        }
        Smc3ConferenceContext smc3ConferenceContext = Smc3ConferenceContextCache.getInstance().get(conferenceContext.getContextKey());
        for (Smc3ConferenceContext context : conferenceContextList) {
            List<AttendeeSmc3> attendees = context.getAttendees();
            for (AttendeeSmc3 attendee : attendees) {
                smc3ConferenceContext.addCascadeAttendeeNew(attendee);
            }
        }
    }

    private BusiHistoryConference saveHistory(Smc3ConferenceContext conferenceContext) {
        String callId = UUID.randomUUID().toString();
        conferenceContext.setCreateUserId(AuthenticationUtil.getUserId());
        // 保存历史记录
        IBusiMcuSmc3HistoryConferenceService busiTeleHistoryConferenceService = BeanFactory.getBean(IBusiMcuSmc3HistoryConferenceService.class);
        BusiHistoryConference busiHistoryConference = busiTeleHistoryConferenceService.saveHistory(conferenceContext);
        conferenceContext.setHistoryConference(busiHistoryConference);

        //历史call保存
        BusiHistoryCall busiHistoryCall = new BusiHistoryCall();
        busiHistoryCall.setCallId(callId);
        busiHistoryCall.setCoSpace(conferenceContext.getCoSpaceId());
        busiHistoryCall.setDeptId(conferenceContext.getDeptId());
        busiHistoryCall.setCreateTime(new Date());
        busiHistoryCall.setHistoryConferenceId(busiHistoryConference.getId());
        BeanFactory.getBean(BusiHistoryCallMapper.class).insertBusiHistoryCall(busiHistoryCall);

        return busiHistoryConference;
    }

    private void createConferenceMcu(Smc3ConferenceContext conferenceContext, BusiMcuSmc3TemplateConference tc, Smc3Bridge bridgesByDept, BusiConferenceNumber busiConferenceNumber) {
        SmcConferenceCreate smcConferenceR = new SmcConferenceCreate();

        SmcConferenceIntAccessCode smcConference=new SmcConferenceIntAccessCode();
        Date now = new Date();
        smcConference.setScheduleStartTime(localToUTC(DateUtil.convertDateToString(now, null)));
        smcConference.setSubject(tc.getName());
        smcConference.setChairmanPassword("");
        smcConference.setGuestPassword("");
        if(Strings.isNotBlank(tc.getChairmanPassword())){
            smcConference.setChairmanPassword(tc.getChairmanPassword());
        }
        if(Strings.isNotBlank(tc.getGuestPassword())){
            smcConference.setGuestPassword(tc.getGuestPassword());
        }
        if(tc.getDurationTime()==null){
            smcConference.setDuration(Integer.MAX_VALUE);
        }else {
            smcConference.setDuration(tc.getDurationTime());
        }

        String vmrNumber = conferenceContext.getSmc3Bridge().getTenantId() + busiConferenceNumber.getId();
        if (busiConferenceNumber.getCreateType() == ConferenceNumberCreateType.MANUAL.getValue()) {
            smcConference.setVmrNumber(vmrNumber);
        } else {
            smcConference.setVmrNumber("");
            smcConference.setAccessCode(busiConferenceNumber.getId());
        }
        smcConference.setConferenceTimeType(ConferenceTimeType.INSTANT_CONFERENCE.name());
        smcConferenceR.setConference(smcConference);

        SmcMultiConferenceServiceCreate multiConferenceService=new SmcMultiConferenceServiceCreate();
        ConferencePolicySetting conferencePolicySetting=new ConferencePolicySetting();
        ConferenceCapabilitySettingCreate conferenceCapabilitySetting=new ConferenceCapabilitySettingCreate();
        conferenceCapabilitySetting.setType("AVC");
        Integer bandwidth = tc.getBandwidth();
        if(bandwidth==null){
            bandwidth=1920;
        }else {
            if(bandwidth<= RATE_NUMBER){
                bandwidth=bandwidth*1024;
            }
        }
        String videoProtocol = tc.getVideoProtocol();
        String videoResolution = tc.getVideoResolution();
        if (Objects.equals(ConstAPI.NORMAL,conferenceContext.getCategory())) {
            if (Strings.isBlank(videoProtocol)) {
                videoProtocol = "H264_BP";
            }
            if (Strings.isBlank(videoResolution)) {
                videoResolution = "MPI_720P";
            }
        }

        conferenceCapabilitySetting.setRate(bandwidth);
        conferenceCapabilitySetting.setMediaEncrypt("NOT_ENCRYPT_MODE");
        conferenceCapabilitySetting.setAudioProtocol("AAC_LD_S");
        conferenceCapabilitySetting.setVideoProtocol(videoProtocol);
        conferenceCapabilitySetting.setVideoResolution(videoResolution);
        conferenceCapabilitySetting.setDataConfProtocol("DATA_RESOLUTION_STANDARD");
        conferenceCapabilitySetting.setReserveResource(0);
        multiConferenceService.setConferencePolicySetting(conferencePolicySetting);
        multiConferenceService.setConferenceCapabilitySetting(conferenceCapabilitySetting);

        conferencePolicySetting.setAutoExtend(true);
        conferencePolicySetting.setAutoEnd(true);
        conferencePolicySetting.setAutoMute(tc.getMuteType()==1);
        conferencePolicySetting.setLanguage(1);
        conferencePolicySetting.setVoiceActive(false);
        conferencePolicySetting.setChairmanPassword("");
        conferencePolicySetting.setGuestPassword("");
        conferencePolicySetting.setMaxParticipantNum(tc.getMaxParticipantNum()==null?500:tc.getMaxParticipantNum());
        if(Strings.isNotBlank(tc.getChairmanPassword())){
            conferencePolicySetting.setChairmanPassword(tc.getChairmanPassword());
        }
        if(Strings.isNotBlank(tc.getGuestPassword())){
            conferencePolicySetting.setGuestPassword(tc.getGuestPassword());
        }

        smcConferenceR.setMultiConferenceService(multiConferenceService);

        SmcConferenceTemplate.SubtitleServiceDTO subtitleService=new SmcConferenceTemplate.SubtitleServiceDTO();
        subtitleService.setEnableSubtitle(false);
        subtitleService.setSrcLang("CHINESE");
        smcConferenceR.setSubtitleService(subtitleService);
        SmcCreateTemplateRep.CheckInServiceDTO checkInService=new SmcCreateTemplateRep.CheckInServiceDTO();
        checkInService.setCheckInDuration(10);
        checkInService.setEnableCheckIn(false);
        smcConferenceR.setCheckInService(checkInService);

        SmcConferenceTemplate.ConfPresetParamDTO confPresetParamDTO = new SmcConferenceTemplate.ConfPresetParamDTO();
        Map<String, Object> confPresetParam = tc.getConfPresetParam();
        if(confPresetParam!=null){
            Object presetMultiPics = confPresetParam.get("presetMultiPics");
            List<PresetMultiPicReqDto> presetMultiPicReqDto = JSONArray.parseArray(JSONObject.toJSONString(presetMultiPics), PresetMultiPicReqDto.class);
            confPresetParamDTO.setPresetMultiPics(presetMultiPicReqDto);
        }
        smcConferenceR.setConfPresetParam(confPresetParamDTO);
        conferenceContext.setConfPresetParamDTO(confPresetParamDTO);

        Map<String, Object> businessProperties = tc.getBusinessProperties();
        if(businessProperties!=null){
            if(businessProperties.get(MAIN_MCU_ID)!=null){
                smcConferenceR.getMultiConferenceService().setMainMcuId((String)businessProperties.get("mainMcuId"));
            }
            if(businessProperties.get(MAIN_MCU_NAME)!=null){
                smcConferenceR.getMultiConferenceService().setMainMcuName((String)businessProperties.get("mainMcuName"));
            }
            if(businessProperties.get(MAIN_SERVICE_ZONE_ID)!=null){
                smcConferenceR.getMultiConferenceService().setMainServiceZoneId((String)businessProperties.get("mainServiceZoneId"));
            }
            if(businessProperties.get(MAIN_SERVICE_ZONE_NAME)!=null){
                smcConferenceR.getMultiConferenceService().setMainServiceZoneName((String)businessProperties.get("mainServiceZoneName"));
            }
            if(businessProperties.get(AUDIO_PROTOCOL)!=null){
                smcConferenceR.getMultiConferenceService().getConferenceCapabilitySetting().setAudioProtocol((String)businessProperties.get("audioProtocol"));
            }

            Object streamService = businessProperties.get("streamService");
            if(streamService!=null){
                smcConferenceR.setStreamService(JSONObject.parseObject(JSONObject.toJSONString(streamService),SmcConferenceTemplate.StreamServiceDTO.class));
            }
        }

        String res= bridgesByDept.getSmcConferencesInvoker().createConference(smcConferenceR, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        if(res.contains(ConstAPI.TOKEN_NOT_EXIST)){
            SmcErrorResponse smcErrorResponse = JSONObject.parseObject(res, SmcErrorResponse.class);
            throw new CustomException(smcErrorResponse.getParam());
        }
        if(res.contains(ConstAPI.ERRORNO_10000001)){
            SmcErrorResponse smcErrorResponse = JSONObject.parseObject(res, SmcErrorResponse.class);
            throw new CustomException(smcErrorResponse.getParam());
        }
        if(res.contains(ConstAPI.ERRORNO_0x20050003)){
            throw new CustomException("录播资源不足");
        }
        if(res.contains(ConstAPI.ERRO)){
            SmcErrorResponse smcErrorResponse = JSONObject.parseObject(res, SmcErrorResponse.class);
            throw new CustomException(smcErrorResponse.getErrorDesc());
        }
        SmcConferenceR conferenceR = JSON.parseObject(res, SmcConferenceR.class);
        if(conferenceR==null||!conferenceR.getConference().getActive()){
            throw new CustomException("erro"+res);
        }
        conferenceContext.setSmc3conferenceId(conferenceR.getConference().getId());
        conferenceContext.setStartTime(new Date());
        Smc3ConferenceContextCache.getInstance().add(conferenceContext);

        if(conferenceContext.getConferenceNumber()!=null){
            String conferenceRemoteParty = conferenceContext.getTenantId()==null?"":conferenceContext.getTenantId() + conferenceContext.getConferenceNumber() + "@" + conferenceContext.getMcuCallIp();
            if (conferenceContext.getMcuCallPort() != null && conferenceContext.getMcuCallPort() != 5060) {
                conferenceRemoteParty += ":" + conferenceContext.getMcuCallPort();
            }
            conferenceContext.setConferenceRemoteParty(conferenceRemoteParty);
        }

    }



    private void doMqttService(List<BaseAttendee> mqttJoinTerminals, Smc3ConferenceContext conferenceContext) {
        Thread thread = new Thread(() -> {
            try {
                IMqttService mqttService = BeanFactory.getBean(IMqttService.class);
                mqttService.pushConferenceInfo(conferenceContext.getId(), conferenceContext.getConferencePassword(), mqttJoinTerminals, conferenceContext.getLiveTerminals());
                logger.info("pushConferenceInfo执行成功，发送mqtt终端数：" + mqttJoinTerminals.size() + ", 直播终端数：" + conferenceContext.getLiveTerminals().size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Threads.sleep(1000);

        thread.start();

    }

    private boolean doCall(Smc3ConferenceContext conferenceContext, AttendeeSmc3 a, BusiTerminal b) {

        try {
            Smc3DelayTaskService delayTaskService = BeanFactory.getBean(Smc3DelayTaskService.class);
            InviteAttendeeSmc3Task inviteAttendeesTask = new InviteAttendeeSmc3Task(conferenceContext.getConferenceNumber(), 200, conferenceContext, a);
            delayTaskService.addTask(inviteAttendeesTask);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("呼叫与会者发生异常-doCall：" + a, e);
            StringBuilder messageTip = new StringBuilder();
            messageTip.append("【").append(a.getName()).append("】呼叫失败：").append(e.getMessage());
            Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip);
        }
        return false;
    }

    private  void doCall(Smc3ConferenceContext conferenceContext, List<AttendeeSmc3> attendeeSmc3s) {
        if(Objects.equals(ConstAPI.NORMAL,conferenceContext.getCategory())){
            new Thread(()->{
                Threads.sleep(2000);
                try {
                    Smc3DelayTaskService delayTaskService = BeanFactory.getBean(Smc3DelayTaskService.class);
                    InviteAttendeeSmc3Task inviteAttendeesTask = new InviteAttendeeSmc3Task(conferenceContext.getConferenceNumber(), 200, conferenceContext, attendeeSmc3s);
                    delayTaskService.addTask(inviteAttendeesTask);
                } catch (Exception e) {
                    logger.error("呼叫与会者发生异常-doCall：", e);

                    Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, e.getMessage());
                }
            }).start();

        }else {
            Threads.sleep(2000);
        }

    }


    private synchronized String startTemplateConference(Smc3ConferenceContext conferenceContext,String  smc3ConferenceId) {
        logger.info("模板会议开始启动：" + conferenceContext.getConferenceNumber());

        Long deptId = conferenceContext.getDeptId();

        conferenceContext.setStart(true);
        conferenceContext.setStartTime(new Date());
        // 会议号和会议室信息
        BusiMcuSmc3TemplateConference tc = BeanFactory.getBean(BusiMcuSmc3TemplateConferenceMapper.class).selectBusiMcuSmc3TemplateConferenceById(conferenceContext.getTemplateConferenceId());

        Smc3Bridge bridgesByDept = Smc3BridgeCache.getInstance().getBridgesByDept(deptId);
        if (bridgesByDept == null) {
            throw new CustomException("未找到可用MCU");
        }
        conferenceContext.setSmc3Bridge(bridgesByDept);


        try {
            if(Strings.isNotBlank(smc3ConferenceId)){

                DetailConference detailConference = bridgesByDept.getSmcConferencesInvoker().getDetailConferencesById(smc3ConferenceId, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

                if(tc.getConferenceNumber()!=null){
                    conferenceContext.setConferenceNumber(tc.getConferenceNumber()+"");
                }else {
                    String number = detailConference.getConferenceUiParam().getAccessCode().substring(1);
                    conferenceContext.setConferenceNumber(number);
                }
                conferenceContext.setSmc3conferenceId(detailConference.getConferenceUiParam().getId());
                conferenceContext.setStartTime(UTCTimeFormatUtil.utcToLocal(detailConference.getConferenceUiParam().getScheduleStartTime()));
                conferenceContext.setLockPresenterId(detailConference.getConferenceState().getLockPresenterId());
                conferenceContext.setEnableSiteNameEditByGuest(detailConference.getConferenceState().getEnableSiteNameEditByGuest());
                conferenceContext.setEnableUnmuteByGuest(detailConference.getConferenceState().getEnableUnmuteByGuest());
                conferenceContext.setLocked(detailConference.getConferenceState().getLock());
                conferenceContext.setMuteStatus(detailConference.getConferenceState().getMute()==true?1:2);
                conferenceContext.setMultiPicBroadcastStatus(Strings.isNotBlank(detailConference.getConferenceState().getBroadcastId()));
                conferenceContext.setMultiPicPollStatus(detailConference.getConferenceState().getMultiPicPollStatus());
                conferenceContext.setDirecting(detailConference.getConferenceState().getDirecting());
                conferenceContext.setMaxParticipantNum(detailConference.getConferenceState().getMaxParticipantNum());
                conferenceContext.setChairmanId(detailConference.getConferenceState().getChairmanId());

                conferenceContext.setName(detailConference.getConferenceUiParam().getSubject());

                ConferenceParticipantSmc3SyncThread conferenceParticipantSmc3SyncThread = new ConferenceParticipantSmc3SyncThread(conferenceContext);
                conferenceParticipantSmc3SyncThread.start();
                conferenceParticipantSmc3SyncThread.join();
                String presenterId = detailConference.getConferenceState().getPresenterId();
                conferenceContext.setPresenterId(presenterId);
                if(Strings.isNotBlank(presenterId)){
                    AttendeeSmc3 attendeeBySmc3Id = conferenceContext.getAttendeeBySmc3Id(presenterId);
                    if (attendeeBySmc3Id!=null){
                        attendeeBySmc3Id.setPresentStatus(YesOrNo.YES.getValue());
                    }
                }

                Smc3ConferenceContextCache.getInstance().add(conferenceContext);

                ConferenceUiParam conferenceUiParam = detailConference.getConferenceUiParam();

                if(conferenceUiParam!=null){
                    ConfTextTip confTextTip = conferenceUiParam.getConfTextTip();
                    if(confTextTip!=null){
                        conferenceContext.setBanner(confTextTip.getBanner());
                        conferenceContext.setCaption(confTextTip.getCaption());
                    }
                }

            }


        } catch (Exception e) {
            logger.error("会议启动失败：" + conferenceContext.getConferenceNumber(), e);
            return null;

        }


        String message = "会议【" + conferenceContext.getName() + "】启动成功！";
        Map<String, Object> obj = new HashMap<>();
        obj.put("startTime", conferenceContext.getStartTime());
        obj.put("message", message);
        obj.put("conferenceNumber", conferenceContext.getConferenceNumber());
        obj.put("streamingUrl", conferenceContext.getStreamingUrl());
        obj.put("streamingUrlList", conferenceContext.getStreamUrlList());
        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_STARTED, obj);
        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, message);
        Smc3ConferenceContextCache.getInstance().add(conferenceContext);
        if(conferenceContext.getAttendeeOperation()==null){
            conferenceContext.setAttendeeOperation(conferenceContext.getDefaultViewOperation());
            logger.info("已设置默认布局：" + conferenceContext.getConferenceNumber());
            conferenceContext.getDefaultViewOperation().operate();
            logger.info("默认布局已开始运行：" + conferenceContext.getConferenceNumber());
        }

        return conferenceContext.getContextKey();

    }

    private void createSmcTemplate(Smc3ConferenceContext conferenceContext) {
        BusiMcuSmc3TemplateConferenceMapper busiMcuSmc3TemplateConferenceMapper = BeanFactory.getBean(BusiMcuSmc3TemplateConferenceMapper.class);
        BusiConferenceNumberMapper busiConferenceNumberMapper = BeanFactory.getBean(BusiConferenceNumberMapper.class);
        BusiMcuSmc3TemplateParticipantMapper busiMcuSmc3TemplateParticipantMapper = BeanFactory.getBean(BusiMcuSmc3TemplateParticipantMapper.class);

        BusiConferenceNumber busiConferenceNumber = null;
        if (StringUtils.isNotEmpty(conferenceContext.getConferenceNumber())) {
            busiConferenceNumber = busiConferenceNumberMapper.selectBusiConferenceNumberById(Long.parseLong(conferenceContext.getConferenceNumber()));
        }
        String vmrNumber = "";
        if (busiConferenceNumber != null && busiConferenceNumber.getCreateType() == ConferenceNumberCreateType.MANUAL.getValue()) {// 开始会议时候创建虚拟会议室
            if (McuType.SMC3.getCode().equals(busiConferenceNumber.getMcuType())) {
                String conferenceNumber = busiConferenceNumber.getId().toString();
                Smc3Bridge smc3Bridge = Smc3BridgeCache.getInstance().getBridgesByDept(busiConferenceNumber.getDeptId());
                try {
                    vmrNumber = smc3Bridge.getTenantId() + conferenceNumber;
                    String organizationId = null;
                    try {
                        organizationId = (String) busiConferenceNumber.getParams().get("organizationId");
                    } catch (Exception e) {
                    }
                    if (StringUtils.isEmpty(organizationId)) {
                        SysDept sysDept = SysDeptCache.getInstance().get(conferenceContext.getDeptId());
                        if (sysDept != null) {
                            String deptName = sysDept.getDeptName();
                            if (deptName.length() <= 2) {
                                SmcOrganization smcOrganization = smc3Bridge.getSmcOrganizationMap().get(deptName);
                                if (smcOrganization != null) {
                                    organizationId = smcOrganization.getId();
                                }
                            } else {
                                for (int i = 2; i < deptName.length(); i++) {
                                    String orgName = deptName.substring(0, i);
                                    SmcOrganization smcOrganization = smc3Bridge.getSmcOrganizationMap().get(orgName);
                                    if (smcOrganization != null) {
                                        organizationId = smcOrganization.getId();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (StringUtils.isEmpty(organizationId)) {
                        String userInfo = smc3Bridge.getSmcUserInvoker().getUserInfo(smc3Bridge.getSmcportalTokenInvoker().getUserName(), smc3Bridge.getSmcportalTokenInvoker().getSystemHeaders());
                        com.paradisecloud.smc3.model.response.UserInfoRep userInfoRep = JSON.parseObject(userInfo, com.paradisecloud.smc3.model.response.UserInfoRep.class);
                        organizationId = userInfoRep.getAccount().getOrganization().getId();
                    }
                    String vmrId= null;
                    String getVmrResponseStr = smc3Bridge.getSmcportalTokenInvoker().getVmr(conferenceContext.getConferenceNumber(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                    GetVmrResponse getVmrResponse = JSON.parseObject(getVmrResponseStr, GetVmrResponse.class);
                    if (StringUtils.isNotEmpty(getVmrResponse.getId())) {
                        vmrId = getVmrResponse.getId();
                    } else {
                        HashMap<String, Object> params = new HashMap<>();
                        params.put("name", conferenceNumber);
                        params.put("vmrNumber", conferenceNumber);
                        params.put("vmrType", "PROJECT");
                        params.put("organizationId", organizationId);
                        String paramsStr = JSON.toJSONString(params);
                        String responseStr = smc3Bridge.getSmcportalTokenInvoker().createVmr(paramsStr, smc3Bridge.getSmcportalTokenInvoker().getSystemHeaders());
                        AddVmrResponse addVmrResponse = JSON.parseObject(responseStr, AddVmrResponse.class);
                        if (!vmrNumber.equals(addVmrResponse.getVmrNumber())) {
                            throw new CustomException("添加SMC虚拟会议室号码失败，请使用其它号码或者联系管理员！");
                        }
                        vmrId = addVmrResponse.getId();
                    }
                    String response = smc3Bridge.getSmcportalTokenInvoker().changeVmrPwd(vmrId, "", "", smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                } catch (Exception e) {
                    logger.error("创建固定会议号码失败!", e);
                    throw new CustomException("创建虚拟会议室失败");
                }
            }
        }
        if (true) {// 创建和更新模板时候不在SMC上创建模板，在开始会议时创建SMC模板
            BusiMcuSmc3TemplateConference busiTemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(conferenceContext.getTemplateConferenceId());
            Smc3Bridge smc3Bridge = Smc3BridgeCache.getInstance().getBridgesByDept(busiTemplateConference.getDeptId());
            SmcConferenceTemplate smcConferenceTemplate = getSmcConferenceTemplate(busiTemplateConference);
            BusiMcuSmc3TemplateParticipant busiMcuSmc3TemplateParticipantCon = new BusiMcuSmc3TemplateParticipant();
            busiMcuSmc3TemplateParticipantCon.setTemplateConferenceId(busiTemplateConference.getId());
            List<BusiMcuSmc3TemplateParticipant> busiTemplateParticipants = busiMcuSmc3TemplateParticipantMapper.selectBusiMcuSmc3TemplateParticipantList(busiMcuSmc3TemplateParticipantCon);
            List<ParticipantRspDto> templateParticipants = new ArrayList<>();
            if (!ObjectUtils.isEmpty(busiTemplateParticipants)) {
                // 添加模板与会者顺序信息
                for (BusiMcuSmc3TemplateParticipant busiTemplateParticipant : busiTemplateParticipants) {
                    busiTemplateParticipant.setTemplateConferenceId(busiTemplateConference.getId());
                    busiTemplateParticipant.setCreateTime(new Date());
                    busiTemplateParticipant.setUuid(java.util.UUID.randomUUID().toString());

                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(busiTemplateParticipant.getTerminalId());
                    if (busiTerminal != null) {
                        ParticipantRspDto participantRspDto = new ParticipantRspDto();
                        String number = busiTerminal.getNumber();
                        participantRspDto.setName(busiTerminal.getName());
                        participantRspDto.setUri(number);
                        participantRspDto.setIpProtocolType(2);
                        participantRspDto.setDialMode("OUT");
                        participantRspDto.setVoice(false);
                        participantRspDto.setRate(0);
                        participantRspDto.setMainParticipant(Objects.equals(busiTemplateParticipant.getId(), busiTemplateConference.getMasterParticipantId()));
                        participantRspDto.setUri(BusiTerminalUtils.getUri(busiTerminal));
                        if (TerminalType.isFmeTemplate(busiTerminal.getType())) {
                            BusiTemplateConferenceMapper templateConferenceMapper = BeanFactory.getBean(BusiTemplateConferenceMapper.class);
                            BusiTemplateConference templateConference = new BusiTemplateConference();
                            templateConference.setConferenceNumber(Long.valueOf(busiTerminal.getNumber()));
                            List<BusiTemplateConference> templateConferences = templateConferenceMapper.selectBusiTemplateConferenceList(templateConference);
                            if (!org.springframework.util.CollectionUtils.isEmpty(templateConferences)) {
                                String conferencePassword = templateConferences.get(0).getConferencePassword();
                                if (io.jsonwebtoken.lang.Strings.hasText(conferencePassword)) {
                                    participantRspDto.setDtmfInfo(conferencePassword);
                                }
                            }
                        }
                        if (TerminalType.isSMCSIP(busiTerminal.getType())) {
                            if (busiTemplateParticipant.getBusinessProperties() != null) {
                                Map<String, Object> businessProperties = busiTemplateParticipant.getBusinessProperties();
                                if (businessProperties.get("audioProtocol") != null) {
                                    participantRspDto.setAudioProtocol((Integer) businessProperties.get("audioProtocol"));
                                }
                                if (businessProperties.get("videoProtocol") != null) {
                                    participantRspDto.setVideoProtocol((Integer) businessProperties.get("videoProtocol"));
                                }
                                if (businessProperties.get("videoResolution") != null) {
                                    participantRspDto.setVideoResolution((Integer) businessProperties.get("videoResolution"));
                                }
                                if (businessProperties.get("rate") != null) {
                                    participantRspDto.setRate((Integer) businessProperties.get("rate"));
                                }
                                if (businessProperties.get("dtmfInfo") != null) {
                                    participantRspDto.setDtmfInfo((String) businessProperties.get("dtmfInfo"));
                                }
                                if (businessProperties.get("serviceZoneId") != null) {
                                    participantRspDto.setServiceZoneId((String) businessProperties.get("serviceZoneId"));
                                }

                            }

                        }

                        if (TerminalType.isMcuTemplateCisco(busiTerminal.getType())) {
                            participantRspDto.setUri(busiTerminal.getNumber() + "@" + busiTerminal.getIp());
                        }

                        templateParticipants.add(participantRspDto);
                    }
                }

            }
            smcConferenceTemplate.setTemplateParticipants(templateParticipants);
            //多级会议模板：
            if (StringUtils.isNotEmpty(busiTemplateConference.getCascadeNodesTemp()) && !Objects.equals("null", busiTemplateConference.getCascadeNodesTemp())) {
                smcConferenceTemplate.getConferenceCapabilitySetting().setVideoProtocol("");
                smcConferenceTemplate.getConferenceCapabilitySetting().setVideoResolution("");
                List<TemplateNode> templateNodes = new ArrayList<>();
                List<TemplateNodeTemp> templateNodesTemp = JSONArray.parseArray(busiTemplateConference.getCascadeNodesTemp(), TemplateNodeTemp.class);
                for (int i = 0; i < templateNodesTemp.size(); i++) {
                    TemplateNode templateNode = new TemplateNode();
                    TemplateNodeTemp templateNodeTemp = templateNodesTemp.get(i);
                    templateNode.setConferenceType(templateNodeTemp.getConferenceType());
                    templateNode.setSubject(templateNodeTemp.getSubject());
                    if (templateNodeTemp.getTemplateId() != null) {
                        BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(templateNodeTemp.getTemplateId());
                        if (busiMcuSmc3TemplateConference != null) {
                            Smc3ConferenceContext conferenceContextTemp = buildTemplateConferenceContext(busiMcuSmc3TemplateConference.getId());
                            if (StringUtils.isNotEmpty(busiMcuSmc3TemplateConference.getSmcTemplateId())) {
                                deleteSmcTemplate(conferenceContextTemp);
                            }
                            if (conferenceContextTemp != null) {
                                if (conferenceContextTemp.getConferenceNumber() == null) {
                                    BusiConferenceNumber busiConferenceNumberTemp = BeanFactory.getBean(IBusiConferenceNumberSmc3Service.class).autoCreateConferenceNumber(conferenceContextTemp.getDeptId());
                                    if (busiConferenceNumberTemp == null) {
                                        logger.error("会议号码创建错误,模板ID:" + busiMcuSmc3TemplateConference.getId());
                                        throw new CustomException("会议号码创建错误,模板ID:" + busiMcuSmc3TemplateConference.getId());
                                    }
                                    // 获取模板会议实体对象
                                    busiMcuSmc3TemplateConference.setConferenceNumber(busiConferenceNumberTemp.getId());
                                    busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiMcuSmc3TemplateConference);
                                    logger.info("自动生成会议号成功：" + busiConferenceNumberTemp.getId());
                                    conferenceContextTemp.setConferenceNumber(busiConferenceNumberTemp.getId() + "");
                                }
                                createSmcTemplate(conferenceContextTemp);
                            }
                                busiMcuSmc3TemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(templateNodeTemp.getTemplateId());
                            templateNode.setTemplateId(busiMcuSmc3TemplateConference.getSmcTemplateId());
                        }
                    }
                    if (templateNodeTemp.getParentTemplateId() != null) {
                        BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(templateNodeTemp.getParentTemplateId());
                        if (busiMcuSmc3TemplateConference != null) {
                            if (StringUtils.isNotEmpty(busiMcuSmc3TemplateConference.getSmcTemplateId())) {
                            } else {
                                Smc3ConferenceContext conferenceContextTemp = buildTemplateConferenceContext(busiMcuSmc3TemplateConference.getId());
                                if (conferenceContextTemp != null) {
                                    if (conferenceContextTemp.getConferenceNumber() == null) {
                                        BusiConferenceNumber busiConferenceNumberTemp = BeanFactory.getBean(IBusiConferenceNumberSmc3Service.class).autoCreateConferenceNumber(conferenceContextTemp.getDeptId());
                                        if (busiConferenceNumberTemp == null) {
                                            logger.error("会议号码创建错误,模板ID:" + busiMcuSmc3TemplateConference.getId());
                                            throw new CustomException("会议号码创建错误,模板ID:" + busiMcuSmc3TemplateConference.getId());
                                        }
                                        // 获取模板会议实体对象
                                        busiMcuSmc3TemplateConference.setConferenceNumber(busiConferenceNumberTemp.getId());
                                        busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiMcuSmc3TemplateConference);
                                        logger.info("自动生成会议号成功：" + busiConferenceNumberTemp.getId());
                                        conferenceContextTemp.setConferenceNumber(busiConferenceNumberTemp.getId() + "");
                                    }
                                    createSmcTemplate(conferenceContextTemp);
                                }
                                busiMcuSmc3TemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(templateNodeTemp.getTemplateId());
                            }
                            templateNode.setParentTemplateId(busiMcuSmc3TemplateConference.getSmcTemplateId());
                        }
                    }
                    templateNodes.add(templateNode);
                }
                busiTemplateConference.setCascadeNodes(JSONArray.toJSONString(templateNodes));
                logger.error("====================" + busiTemplateConference.getCascadeNodes());

                CasecadeTemplateRequest casecadeTemplateRequest = new CasecadeTemplateRequest();

                com.paradisecloud.com.fcm.smc.modle.SmcConferenceTemplate.ConferencePolicySettingDTO policySettingDTO = new com.paradisecloud.com.fcm.smc.modle.SmcConferenceTemplate.ConferencePolicySettingDTO();
                policySettingDTO.setAutoEnd(true);
                policySettingDTO.setAutoExtend(true);
                policySettingDTO.setAutoMute(busiTemplateConference.getMuteType() == 1 ? true : false);
                policySettingDTO.setChairmanPassword(busiTemplateConference.getChairmanPassword());
                policySettingDTO.setGuestPassword(busiTemplateConference.getGuestPassword());
                policySettingDTO.setLanguage(1);
                policySettingDTO.setVoiceActive(false);
                policySettingDTO.setMaxParticipantNum(busiTemplateConference.getMaxParticipantNum());

                casecadeTemplateRequest.setSubject(busiTemplateConference.getName());
                casecadeTemplateRequest.setTemplateNodes(templateNodes);
                casecadeTemplateRequest.setShowSecurityLevel(false);
                casecadeTemplateRequest.setConferencePolicySetting(policySettingDTO);
                casecadeTemplateRequest.setChairmanPassword(busiTemplateConference.getChairmanPassword());
                casecadeTemplateRequest.setDuration(Integer.MAX_VALUE);
                casecadeTemplateRequest.setGuestPassword(busiTemplateConference.getGuestPassword());

                String userInfo = smc3Bridge.getSmcUserInvoker().getUserInfo(smc3Bridge.getSmcportalTokenInvoker().getUserName(), smc3Bridge.getSmcportalTokenInvoker().getSystemHeaders());
                UserInfoRep userInfoRep = JSON.parseObject(userInfo, UserInfoRep.class);
                String id = userInfoRep.getAccount().getOrganization().getId();
                casecadeTemplateRequest.setOrganizationId(id);

                String s = smc3Bridge.getSmcConferencesTemplateInvoker().creatConferencesCascadeTemplate(JSON.toJSONString(casecadeTemplateRequest), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                try {
                    CasecadeTemplateRequest cascadeTemplateResponse = JSONObject.parseObject(s, CasecadeTemplateRequest.class);
                    if (cascadeTemplateResponse != null && io.jsonwebtoken.lang.Strings.hasText(cascadeTemplateResponse.getId())) {
                        busiTemplateConference.setCascadeId(cascadeTemplateResponse.getId());
//                        busiTemplateConference.setCategory(ConstAPI.CASCADE);
                        busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiTemplateConference);

//                        for (TemplateNode templateNode : templateNodes) {
//                            BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceBySmcTemplateId(templateNode.getTemplateId());
//                            busiMcuSmc3TemplateConference.setCascadeId(cascadeTemplateResponse.getId());
//                            busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiMcuSmc3TemplateConference);
//                        }
                    } else {
                        logger.error(s);
                        throw new CustomException("多级会议模板创建错误");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new CustomException("多级会议模板创建错误" + e.getMessage());
                }
            } else {
                smcConferenceTemplate.setVmrNumber(vmrNumber);
                Map<String, Object> businessProperties = busiTemplateConference.getBusinessProperties();
                if(businessProperties!=null){
                    if(businessProperties.get(MAIN_MCU_ID)!=null){
                        smcConferenceTemplate.setMainMcuId((String)businessProperties.get("mainMcuId"));
                    }
                    if(businessProperties.get(MAIN_MCU_NAME)!=null){
                        smcConferenceTemplate.setMainMcuName((String)businessProperties.get("mainMcuName"));
                    }
                    if(businessProperties.get(MAIN_SERVICE_ZONE_ID)!=null){
                        smcConferenceTemplate.setMainServiceZoneId((String)businessProperties.get("mainServiceZoneId"));
                    }
                }
                //同步在SMC上创建模板
                String result = smc3Bridge.getSmcConferencesTemplateInvoker().creatConferencesTemplate(JSONObject.toJSONString(smcConferenceTemplate), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                if (result != null && result.contains(ConstAPI.ERRORNO)) {
                    com.paradisecloud.com.fcm.smc.modle.response.SmcErrorResponse smcErrorResponse = JSON.parseObject(result, com.paradisecloud.com.fcm.smc.modle.response.SmcErrorResponse.class);

                    throw new CustomException("新增模板失败:" + smcErrorResponse.getErrorDesc());
                }
                com.paradisecloud.com.fcm.smc.modle.SmcConferenceTemplate smcConferenceTemplate1 = JSON.parseObject(result, com.paradisecloud.com.fcm.smc.modle.SmcConferenceTemplate.class);
                if (Objects.isNull(smcConferenceTemplate1)) {
                    throw new CustomException("创建模板会议失败");
                }
                String template1Id = smcConferenceTemplate1.getId();
                busiTemplateConference.setSmcTemplateId(template1Id);
                busiTemplateConference.setCategory(ConstAPI.NORMAL);
                busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiTemplateConference);
            }
        }
    }

    private void deleteSmcTemplate(Smc3ConferenceContext conferenceContext) {
        BusiMcuSmc3TemplateConferenceMapper busiMcuSmc3TemplateConferenceMapper = BeanFactory.getBean(BusiMcuSmc3TemplateConferenceMapper.class);
        BusiConferenceNumberMapper busiConferenceNumberMapper = BeanFactory.getBean(BusiConferenceNumberMapper.class);
        IBusiConferenceNumberService busiConferenceNumberService = BeanFactory.getBean(IBusiConferenceNumberService.class);
        if (true) {
            BusiMcuSmc3TemplateConference busiTemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(conferenceContext.getTemplateConferenceId());
            if (false) {
                if (busiTemplateConference.getConferenceNumber() != null) {
                    BusiMcuSmc3TemplateConference con = new BusiMcuSmc3TemplateConference();
                    con.setConferenceNumber(busiTemplateConference.getConferenceNumber());
                    List<BusiMcuSmc3TemplateConference> cs = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceList(con);
                    if (ObjectUtils.isEmpty(cs)) {
                        // 修改号码状态为闲置
                        BusiConferenceNumber cn = new BusiConferenceNumber();
                        cn.setId(busiTemplateConference.getConferenceNumber());
                        cn.setStatus(ConferenceNumberStatus.IDLE.getValue());
                        busiConferenceNumberMapper.updateBusiConferenceNumber(cn);
                    }

                    // 若是自动创建的会议号，则删除模板的时候同步进行删除
                    BusiConferenceNumber bcn = busiConferenceNumberService.selectBusiConferenceNumberById(busiTemplateConference.getConferenceNumber());
                    if (bcn != null) {
                        if (ConferenceNumberCreateType.convert(bcn.getCreateType()) == ConferenceNumberCreateType.AUTO) {
                            busiConferenceNumberService.deleteBusiConferenceNumberById(busiTemplateConference.getConferenceNumber());
                        }
                    }

                }
            }

            Smc3Bridge smc3Bridge = Smc3BridgeCache.getInstance().getBridgesByDept(busiTemplateConference.getDeptId());
            if (Objects.equals(ConstAPI.CASCADE, busiTemplateConference.getCategory())) {
                if (StringUtils.isNotEmpty(busiTemplateConference.getCascadeId())) {
                    smc3Bridge.getSmcConferencesTemplateInvoker().deleteCascadeTemplate(busiTemplateConference.getCascadeId(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }
            }
            if (StringUtils.isNotEmpty(busiTemplateConference.getSmcTemplateId())) {
                smc3Bridge.getSmcConferencesTemplateInvoker().deleteConferencesTemplate(busiTemplateConference.getSmcTemplateId(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }
            if (busiTemplateConference.getCreateType() == ConferenceNumberCreateType.MANUAL.getValue()) {
                try {
                    String responseStr = smc3Bridge.getSmcportalTokenInvoker().getVmr(conferenceContext.getConferenceNumber(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                    GetVmrResponse getVmrResponse = JSON.parseObject(responseStr, GetVmrResponse.class);
                    if (StringUtils.isNotEmpty(getVmrResponse.getId())) {
                        responseStr = smc3Bridge.getSmcportalTokenInvoker().deleteVmr(getVmrResponse.getId(), smc3Bridge.getSmcportalTokenInvoker().getSystemHeaders());
                        if (responseStr != null) {
                            throw new CustomException("删除SMC虚拟会议室号码失败，请重试或者联系管理员！");
                        }
                    }
                } catch (Exception e) {
                    logger.error("删除虚拟会议室失败!");
                }
            }
        }
    }

}
