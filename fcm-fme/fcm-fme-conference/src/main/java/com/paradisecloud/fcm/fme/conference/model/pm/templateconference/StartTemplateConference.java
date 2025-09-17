/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : StartTemplateConference.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.templateconference
 * @author sinhy 
 * @since 2021-09-22 21:12
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.model.pm.templateconference;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.constant.MqttConfigConstant;
import com.paradisecloud.fcm.common.constant.ResponseInfo;
import com.paradisecloud.fcm.common.constant.TerminalTopic;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.DeptFmeMappingCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.utils.BridgeUtils;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiTemplateConferenceService;
import com.paradisecloud.fcm.fme.conference.listener.MqttForFmePushMessageCache;
import com.paradisecloud.fcm.fme.conference.model.core.ConferenceStarter;
import com.paradisecloud.fcm.fme.conference.task.RemoveDuplicatesModeConferenceHistoryTask;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.FmeAttendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.UpFmeAttendee;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberService;
import com.paradisecloud.fcm.service.live.LiveService;
import com.paradisecloud.fcm.service.live.LiveValue;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveBridgeCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveDeptCache;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StartTemplateConference extends BuildTemplateConferenceContext
{

    public static final int MAX_CALL_COUNT = 49;

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-22 21:12 
     * @param method 
     */
    public StartTemplateConference(Method method)
    {
        super(method);
    }
    
    public synchronized String startTemplateConference(long templateConferenceId)
    {
        logger.info("模板会议启动入口：" + templateConferenceId);
        // 获取会议上下文
        ConferenceContext conferenceContext = buildTemplateConferenceContext(templateConferenceId);
        if (conferenceContext == null)
        {
            return null;
        }
        
        // 启动前校验会议是否已开始
        if (conferenceContext.isStart())
        {
            throw new SystemException(1009874, "会议已开始，请勿重复开始");
        }
        
        if (conferenceContext.getConferenceNumber() == null)
        {
             int callCount=0;
            Long deptId = conferenceContext.getDeptId();
            BusiFmeDept busiFmeDept = DeptFmeMappingCache.getInstance().get(deptId);
            if(busiFmeDept!=null){
                if(busiFmeDept.getFmeType().intValue()== FmeType.SINGLE_NODE.getValue()){
                    FmeBridge fmeBridge = FmeBridgeCache.getInstance().get(busiFmeDept.getFmeId());
                    fmeBridge.checkCallCount();
                } else {
                    checkCount(conferenceContext, callCount);
                }
            } else {
                checkCount(conferenceContext, callCount);
            }

            BusiConferenceNumber busiConferenceNumber = BeanFactory.getBean(IBusiConferenceNumberService.class).autoCreateConferenceNumber(conferenceContext.getDeptId(), McuType.FME.getCode());
            if (busiConferenceNumber == null) {
                logger.error("会议号码创建错误,模板ID:" + templateConferenceId);
                throw new CustomException("会议号码创建错误,模板ID:" + templateConferenceId);
            }
            BusiTemplateConference tc = BeanFactory.getBean(BusiTemplateConferenceMapper.class).selectBusiTemplateConferenceById(templateConferenceId);
            // 获取模板会议实体对象
            if (conferenceContext.getIsAutoCreateStreamUrl() == 1) {
                if (tc.getStreamingEnabled() == StreamingEnabledType.CLOUDS.getValue()) {
                    LiveService liveService = BeanFactory.getBean(LiveService.class);
                    if (StringUtils.hasText(tc.getStreamUrl())) {
                        String streamingUrl = tc.getStreamUrl();
                        LiveValue liveValue = liveService.generateUrlFromPushUrl(streamingUrl);
                        logger.info("直播地址：" + streamingUrl);
                        conferenceContext.setStreamingUrl(liveValue.getPullUrl());
                        conferenceContext.setCloudsStreamingUrl(streamingUrl);
                        conferenceContext.getStreamUrlList().clear();
                        conferenceContext.addStreamUrlList(liveValue.getPullUrlList());
                    } else {
                        LocalDateTime expirationTime = LocalDateTime.now().plusDays(2);
                        LiveValue liveValue = liveService.generateUrl(busiConferenceNumber.getId().toString(), expirationTime);
                        String streamingUrl = liveValue.getPushUrl();
                        logger.info("直播地址：" + streamingUrl);
                        tc.setStreamUrl(streamingUrl);
                        conferenceContext.setStreamingUrl(liveValue.getPullUrl());
                        conferenceContext.setCloudsStreamingUrl(streamingUrl);
                        conferenceContext.getStreamUrlList().clear();
                        conferenceContext.addStreamUrlList(liveValue.getPullUrlList());
                    }
                } else if (tc.getStreamingEnabled() == StreamingEnabledType.THIRD_PARTY.getValue()) {
                    // 第三方
                } else {
                    String streamingUrl = createStreamingUrl(tc.getDeptId(), busiConferenceNumber.getId());
                    logger.info("直播地址：" + streamingUrl);
                    tc.setStreamUrl(streamingUrl);
                    conferenceContext.setStreamingUrl(streamingUrl);
                }
            }
            tc.setConferenceNumber(busiConferenceNumber.getId());
            BeanFactory.getBean(BusiTemplateConferenceMapper.class).updateBusiTemplateConference(tc);
            logger.info("自动生成会议号成功：" + busiConferenceNumber.getId());
            conferenceContext.setConferenceNumber(tc.getConferenceNumber().toString());
        }
        if (conferenceContext.getIsAutoCreateStreamUrl() == 1 && conferenceContext.getStreamingUrl() == null) {
            BusiTemplateConference tc = BeanFactory.getBean(BusiTemplateConferenceMapper.class).selectBusiTemplateConferenceById(templateConferenceId);
            if (tc.getStreamingEnabled() == StreamingEnabledType.CLOUDS.getValue()) {
                LiveService liveService = BeanFactory.getBean(LiveService.class);
                if (StringUtils.hasText(tc.getStreamUrl())) {
                    String streamingUrl = tc.getStreamUrl();
                    LiveValue liveValue = liveService.generateUrlFromPushUrl(streamingUrl);
                    logger.info("直播地址：" + streamingUrl);
                    conferenceContext.setStreamingUrl(liveValue.getPullUrl());
                    conferenceContext.setCloudsStreamingUrl(streamingUrl);
                    conferenceContext.getStreamUrlList().clear();
                    conferenceContext.addStreamUrlList(liveValue.getPullUrlList());
                } else {
                    LocalDateTime expirationTime = LocalDateTime.now().plusDays(2);
                    LiveValue liveValue = liveService.generateUrl(conferenceContext.getConferenceNumber(), expirationTime);
                    String streamingUrl = liveValue.getPushUrl();
                    logger.info("直播地址：" + streamingUrl);
                    tc.setStreamUrl(streamingUrl);
                    conferenceContext.setStreamingUrl(liveValue.getPullUrl());
                    conferenceContext.setCloudsStreamingUrl(streamingUrl);
                    conferenceContext.getStreamUrlList().clear();
                    conferenceContext.addStreamUrlList(liveValue.getPullUrlList());
                }
            } else if (tc.getStreamingEnabled() == StreamingEnabledType.THIRD_PARTY.getValue()) {
                // 第三方直播
            } else {
                String streamingUrl = createStreamingUrl(conferenceContext.getDeptId(), Long.valueOf(conferenceContext.getConferenceNumber()));
                logger.info("直播地址：" + streamingUrl);
                if (streamingUrl != null && streamingUrl.length() > 0) {
                    tc.setStreamUrl(streamingUrl);
                    conferenceContext.setStreamingUrl(streamingUrl);
                    BeanFactory.getBean(BusiTemplateConferenceMapper.class).updateBusiTemplateConference(tc);
                }
            }
        }
        // 开始模板会议
         startTemplateConference(conferenceContext);
         return conferenceContext.getContextKey();
    }

    private String createStreamingUrl(Long deptId, Long conferenceNumber) {
        String streamUrl = null;
        try {
            BusiLiveDept busiLiveDept = LiveDeptCache.getInstance().get(deptId);
            if (busiLiveDept != null) {
                if (busiLiveDept.getLiveType() == 1) {
                    BusiLive busiLive = BeanFactory.getBean(BusiLiveMapper.class).selectBusiLiveById(busiLiveDept.getLiveId());
//                    if (busiLive.getDomainName() != null && busiLive.getDomainName().length() > 0) {
//                        streamUrl = busiLive.getProtocolType() + "://" + busiLive.getDomainName() + "/" + busiLive.getUriPath() + "/" + conferenceNumber;
//                    } else {
                        streamUrl = busiLive.getProtocolType() + "://" + busiLive.getIp() + "/" + busiLive.getUriPath() + "/" + conferenceNumber;
//                    }
                } else {
                    BusiLiveClusterMap busiLiveClusterMap = new BusiLiveClusterMap();
                    busiLiveClusterMap.setClusterId(busiLiveDept.getLiveId());
                    List<BusiLiveClusterMap> busiLiveClusterMaps = BeanFactory.getBean(BusiLiveClusterMapMapper.class).selectBusiLiveClusterMapList(busiLiveClusterMap);
                    if (busiLiveClusterMaps != null && busiLiveClusterMaps.size() > 0) {
                        for (BusiLiveClusterMap liveClusterMap : busiLiveClusterMaps) {
                            if (liveClusterMap.getLiveType() == 1) {
                                BusiLive busiLive = BeanFactory.getBean(BusiLiveMapper.class).selectBusiLiveById(liveClusterMap.getLiveId());
//                                if (busiLive.getDomainName() != null && busiLive.getDomainName().length() > 0) {
//                                    streamUrl = busiLive.getProtocolType() + "://" + busiLive.getDomainName() + "/" + busiLive.getUriPath() + "/" + conferenceNumber;
//                                } else {
                                    streamUrl = busiLive.getProtocolType() + "://" + busiLive.getIp() + "/" + busiLive.getUriPath() + "/" + conferenceNumber;
                                    try {
                                        Integer status = LiveBridgeCache.getInstance().get(busiLive.getId()).getBusiLive().getStatus();
                                        if (status != null && status == 1) {
                                            break;
                                        }
                                    } catch (Exception e) {
                                    }
//                                }
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


    private void checkCount(ConferenceContext conferenceContext, int callCount) {
        List<FmeBridge> fmeBridgesByDept = FmeBridgeCache.getInstance().getFmeBridgesByDept(conferenceContext.getDeptId());
        if (!CollectionUtils.isEmpty(fmeBridgesByDept)) {
            for (FmeBridge fmeBridge : fmeBridgesByDept) {
                fmeBridge.checkCallCount();
            }
        }
    }

    /**
     * <pre>开始模板会议</pre>
     * @author lilinhai
     * @since 2021-02-03 12:57 
     * @param conferenceContext void
     */
    private synchronized void startTemplateConference(ConferenceContext conferenceContext)
    {
        logger.info("模板会议开始启动：" + conferenceContext.getConferenceNumber());

        Long deptId = conferenceContext.getDeptId();
        if (conferenceContext.getStreamingEnabled() != StreamingEnabledType.CLOUDS.getValue() && conferenceContext.getStreamingEnabled() != StreamingEnabledType.THIRD_PARTY.getValue()) {
            if (StringUtils.hasText(conferenceContext.getStreamingUrl())) {
                List<String> streamUrlList = new ArrayList<>();
                streamUrlList.add(conferenceContext.getStreamingUrl());
                conferenceContext.getStreamUrlList().clear();
                conferenceContext.addStreamUrlList(streamUrlList);
            } else {
                List<String> streamUrlList = createStreamUrlList(deptId, conferenceContext.getConferenceNumber());
                if (streamUrlList != null && streamUrlList.size() > 0) {
                    conferenceContext.getStreamUrlList().clear();
                    conferenceContext.addStreamUrlList(streamUrlList);
                }
            }
        }

        // 会议号和会议室信息
        BusiConferenceNumber busiConferenceNumber = BeanFactory.getBean(BusiConferenceNumberMapper.class).selectBusiConferenceNumberById(Long.parseLong(conferenceContext.getConferenceNumber()));
        BeanFactory.getBean(IBusiTemplateConferenceService.class).createCoSpaceId(busiConferenceNumber, deptId);
        
        // 构建所有会议室启动器
        List<ConferenceStarter> confefrenceStarters = buildConfefrenceStarters(conferenceContext, busiConferenceNumber);
        
        // 循环初始化每个级联会议室启动器
        for (ConferenceStarter confefrenceStarter : confefrenceStarters)
        {
            confefrenceStarter.init();
        }
        
        logger.info("ConfefrenceStarter初始化完成：" + conferenceContext.getConferenceNumber());
        
        // 添加发起方会议上下文
        ConferenceContextCache.getInstance().add(conferenceContext);
        
        // 绑定父子级联会议关系
        FmeBridge upFmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceContext(conferenceContext);
        for (ConferenceStarter confefrenceStarter : confefrenceStarters)
        {
            if (confefrenceStarter.getConferenceContext() != conferenceContext)
            {
                confefrenceStarter.getConferenceContext().setAutoCallTerminal(conferenceContext.isAutoCallTerminal());
                ConferenceContextCache.getInstance().add(confefrenceStarter.getConferenceContext());

                // 将子会议同父级会议绑定级联关系
                ConferenceContextCache.getInstance().addCascadeConferenceContext(String.valueOf(conferenceContext.getConferenceNumber()), confefrenceStarter.getConferenceContext());
                
                UpFmeAttendee upFmeAttendee = new UpFmeAttendee();
                upFmeAttendee.setName(SysDeptCache.getInstance().get(conferenceContext.getDeptId()).getDeptName());
                upFmeAttendee.setWeight(1000000);
                upFmeAttendee.setCascadeDeptId(conferenceContext.getDeptId());
                upFmeAttendee.setDeptId(confefrenceStarter.getConferenceContext().getDeptId());
                upFmeAttendee.setIp(upFmeBridge.getAttendeeIp());
                upFmeAttendee.setFmeId(upFmeBridge.getBusiFme().getId());
                upFmeAttendee.setCascadeConferenceNumber(conferenceContext.getConferenceNumber());
                
                upFmeAttendee.setConferenceNumber(String.valueOf(confefrenceStarter.getConferenceContext().getConferenceNumber()));
                upFmeAttendee.setId(UUID.randomUUID().toString());
                
                // 必须覆盖fmeAttendee
                upFmeAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                upFmeAttendee.setMeetingStatus(AttendeeMeetingStatus.OUT.getValue());
                
                // 设置fme与会者的级联会议号
                confefrenceStarter.getConferenceContext().addAttendeeToRemotePartyMap(upFmeAttendee);
            }
        }
        
        conferenceContext.setAttendeeOperation(conferenceContext.getDefaultViewOperation());
        logger.info("已设置默认布局：" + conferenceContext.getConferenceNumber());
        
        // 循环启动每个会议室启动器
        for (ConferenceStarter confefrenceStarter : confefrenceStarters)
        {
            try
            {
                confefrenceStarter.start();
                logger.info("会议已启动：" + conferenceContext.getConferenceNumber());
                

                //会议模式
                String conferenceModel = confefrenceStarter.getConferenceContext().getConferenceMode();
                if(Strings.isNotBlank(conferenceModel)){
                    IBusiConferenceService busiConferenceService = BeanFactory.getBean(IBusiConferenceService.class);
                    busiConferenceService.mode(confefrenceStarter.getConferenceContext().getId(), conferenceModel);
                    logger.info("默认会议模式已开始运行：" + conferenceContext.getConferenceNumber());
                }else {
                    // 设置默认视图
                    if (confefrenceStarter.getConferenceContext().getDefaultViewOperation() != null)
                    {
                        conferenceContext.getDefaultViewOperation().operate();
                        logger.info("默认布局已开始运行：" + conferenceContext.getConferenceNumber());
                    }
                }
                //相同会议场景过滤任务
                TaskService taskService = BeanFactory.getBean(TaskService.class);
                RemoveDuplicatesModeConferenceHistoryTask removeDuplicatesModeConferenceHistoryTask = new RemoveDuplicatesModeConferenceHistoryTask(McuType.FME.getCode(), 3000);
                taskService.addTask(removeDuplicatesModeConferenceHistoryTask);
            }
            catch (Throwable e)
            {
                logger.error("会议启动失败：" + conferenceContext.getConferenceNumber(), e);
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, "会议启动失败：" + e.getMessage());
                ConferenceContextCache.getInstance().remove(confefrenceStarter.getConferenceContext().getContextKey());

                if (MqttForFmePushMessageCache.getInstance().getMqttForFmePushMessageListener() != null) {
                    Long createUserId = conferenceContext.getCreateUserId();
                    if (createUserId != null) {
                        BusiUserTerminalMapper busiUserTerminalMapper = BeanFactory.getBean(BusiUserTerminalMapper.class);
                        BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByUserId(createUserId);
                        if (busiUserTerminal != null) {
                            BusiTerminal busiTerminal = TerminalCache.getInstance().get(busiUserTerminal.getTerminalId());
                            if (busiTerminal != null && StringUtils.hasText(busiTerminal.getSn())) {
                                String clientId = busiTerminal.getSn();
                                String topic = MqttConfigConstant.TOPIC_PREFIX + clientId;
                                String action = TerminalTopic.CREATE_CONFERENCE;
                                JSONObject jObj = (JSONObject) JSON.toJSON(conferenceContext);
                                MqttForFmePushMessageCache.getInstance().getMqttForFmePushMessageListener().onPushMessage(ResponseInfo.CODE_500, "会议启动失败！", topic, action, jObj, clientId, "");
                            }
                        }
                    }
                }
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    /**
     * <pre>构建所有会议启动器</pre>
     * @author lilinhai
     * @since 2021-02-02 17:31 
     * @param conferenceContext
     * @param busiConferenceNumber
     * @return List<ConfefrenceStarter>
     */
    private List<ConferenceStarter> buildConfefrenceStarters(ConferenceContext conferenceContext, BusiConferenceNumber busiConferenceNumber)
    {
        //ConferenceType conferenceType = ConferenceType.convert(conferenceContext.getType());
        String conferenceNameFormat = "会议【" + conferenceContext.getConferenceNumber() + "】{0}";

        // 启动主体会议
        // 获取会议桥工具对象
        FmeBridge fmeBridge = BridgeUtils.getFmeBridgeByDeptIdAndConferenceNumber(conferenceContext.getDeptId(), busiConferenceNumber.getId().toString(), true);
        
        // 检测活跃会议数是否超过49
        fmeBridge.checkCallCount();
        
        List<ConferenceStarter> confefrenceStarters = new ArrayList<>();
        ConferenceStarter mainConfefrenceStarter = new ConferenceStarter(fmeBridge, conferenceContext.getAttendees(), conferenceContext);
        mainConfefrenceStarter.setBusiConferenceNumber(busiConferenceNumber);
        mainConfefrenceStarter.setDeptId(conferenceContext.getDeptId());
        mainConfefrenceStarter.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        mainConfefrenceStarter.setBandwidth(conferenceContext.getBandwidth());
        mainConfefrenceStarter.setConferenceName(MessageFormat.format(conferenceNameFormat, "发起方：" + SysDeptCache.getInstance().get(conferenceContext.getDeptId()).getDeptName()));
        
        // 添加到会议启动器集合中，等待地州会议启动器都初始化好后，一起启动
        confefrenceStarters.add(mainConfefrenceStarter);
        
        List<FmeAttendee> fmeAddtendees = conferenceContext.getFmeAttendees();
        if (!ObjectUtils.isEmpty(fmeAddtendees))
        {
            for (FmeAttendee fmeAttendee : fmeAddtendees)
            {
                // 构建级联子会议启动器
                buildSubConferenceStart(conferenceContext, conferenceNameFormat, confefrenceStarters, fmeAttendee);
            }
        }
        
        return confefrenceStarters;
    }

    /**
     * <pre>构建级联子会议启动器</pre>
     * @author lilinhai
     * @since 2021-02-02 17:30 
     * @param conferenceContext
     * @param conferenceNameFormat
     * @param confefrenceStarters
     * @param fmeAttendee void
     */
    private void buildSubConferenceStart(ConferenceContext conferenceContext, String conferenceNameFormat, List<ConferenceStarter> confefrenceStarters, FmeAttendee fmeAttendee)
    {
        // 地州与会者集合，需要加上地州的主会场（不是真正的地州主会场，地州真实主会场实际上是省的mcu）
        List<Attendee> as = new ArrayList<>(conferenceContext.getCascadeAttendeesMap().get(fmeAttendee.getCascadeDeptId()));
        for (Attendee masterAttendee0 : conferenceContext.getMasterAttendees())
        {
            if (masterAttendee0.getDeptId() == fmeAttendee.getCascadeDeptId())
            {
                as.add(0, masterAttendee0);
                break;
            }
        }
        
        // 子会议启动器
        FmeBridge fmeBridge = BridgeUtils.getFmeBridgeByDeptIdAndConferenceNumber(fmeAttendee.getCascadeDeptId(), fmeAttendee.getCascadeConferenceNumber(), false);
        ConferenceStarter subConferenceStarter = new ConferenceStarter(fmeBridge, as);
        subConferenceStarter.setConferenceStandardName(conferenceContext.getName());
        subConferenceStarter.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        
        BusiConferenceNumber busiConferenceNumber = BeanFactory.getBean(BusiConferenceNumberMapper.class).selectBusiConferenceNumberById(Long.parseLong(fmeAttendee.getCascadeConferenceNumber()));
        subConferenceStarter.setBusiConferenceNumber(busiConferenceNumber);
        subConferenceStarter.setDeptId(fmeAttendee.getCascadeDeptId());
        subConferenceStarter.setConferenceName(MessageFormat.format(conferenceNameFormat, "下级集群：" + SysDeptCache.getInstance().get(fmeAttendee.getCascadeDeptId()).getDeptName()));
        subConferenceStarter.setBandwidth(conferenceContext.getBandwidth());
        subConferenceStarter.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        // 添加到会议启动器集合中
        confefrenceStarters.add(subConferenceStarter);
    }
}
