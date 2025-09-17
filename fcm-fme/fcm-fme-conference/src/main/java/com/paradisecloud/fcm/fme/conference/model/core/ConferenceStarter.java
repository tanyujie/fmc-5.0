/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : ConfefrenceStarter.java
 * Package : com.paradisecloud.fcm.fme.service.model
 *
 * @author lilinhai
 *
 * @since 2021-02-01 14:13
 *
 * @version V1.0
 */
package com.paradisecloud.fcm.fme.conference.model.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.constant.MqttConfigConstant;
import com.paradisecloud.fcm.common.constant.ResponseInfo;
import com.paradisecloud.fcm.common.constant.TerminalTopic;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiCallLegProfileService;
import com.paradisecloud.fcm.fme.apiservice.interfaces.ICoSpaceService;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeFieldService;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.attendee.interfaces.ICallService;
import com.paradisecloud.fcm.fme.attendee.model.busiprocessor.CallAttendeeProcessor;
import com.paradisecloud.fcm.fme.cache.FmeDataCache;
import com.paradisecloud.fcm.fme.conference.task.ConferenceTakeSnapshotTask;
import com.paradisecloud.fcm.fme.conference.task.StartCloudConferenceTask;
import com.paradisecloud.fcm.fme.model.busi.attendee.*;
import com.paradisecloud.fcm.service.eunm.NotifyType;
import com.paradisecloud.fcm.service.im.IMResult;
import com.paradisecloud.fcm.service.im.IMService;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.fme.attendee.model.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.fme.attendee.utils.ConferenceContextUtils;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.SplitScreenCreaterMap;
import com.paradisecloud.fcm.fme.cache.model.UriParticipantsMap;
import com.paradisecloud.fcm.fme.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.fme.cache.utils.FcmThreadPool;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiHistoryConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiRecordsService;
import com.paradisecloud.fcm.fme.conference.listener.MqttForFmePushMessageCache;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.OneSplitScreen;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.paradisecloud.fcm.fme.model.cms.CallLegProfile;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;
import com.paradisecloud.fcm.fme.model.parambuilder.CallParamBuilder;
import com.paradisecloud.fcm.fme.model.parambuilder.CoSpaceParamBuilder;
import com.paradisecloud.fcm.fme.model.response.calllegprofile.CallLegProfileInfoResponse;
import com.paradisecloud.fcm.service.minutes.StartMeetingMinutesTask;
import com.paradisecloud.fcm.service.model.CloudConference;
import com.paradisecloud.fcm.service.ops.OpsDataCache;
import com.paradisecloud.fcm.service.task.CloudSmsLocaltoRemoteTask;
import com.paradisecloud.fcm.service.util.HuaweiCloudUtil;
import com.paradisecloud.fcm.service.util.TencentCloudUtil;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.exception.SystemException;
import com.sinhy.model.GenericValue;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.CauseUtils;
import com.sinhy.utils.DateUtils;
import com.sinhy.utils.ThreadUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <pre>会议启动器</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-01 14:13
 */
public class ConferenceStarter {

    private Logger logger = LoggerFactory.getLogger(ConferenceStarter.class);

    /**
     * 当前会议所属部门
     */
    private long deptId;

    /**
     * 会议号
     */
    private BusiConferenceNumber busiConferenceNumber;

    /**
     * 会议室名
     */
    private String conferenceName;

    /**
     * 会议室标准名
     */
    private String conferenceStandardName;

    /**
     * 会议主导方的参会者
     */
    private List<Attendee> attendees;

    /**
     * 会议桥FME对象
     */
    private FmeBridge fmeBridge;

    /**
     * 入会带宽
     */
    private Integer bandwidth;

    /**
     * 模板会议的ID，标记出是哪个模板发起的，好从模板点击进入会议进行关联
     */
    private Long templateConferenceId;

    /**
     * 会议上下文
     */
    private ConferenceContext conferenceContext;

    /**
     * <pre>构造方法</pre>
     *
     * @param fmeBridge
     * @author lilinhai
     * @since 2021-02-01 14:26
     */
    public ConferenceStarter(FmeBridge fmeBridge, List<Attendee> attendees) {
        this.attendees = attendees;
        this.fmeBridge = fmeBridge;
    }

    /**
     * <pre>构造方法</pre>
     *
     * @param fmeBridge
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-02-01 14:26
     */
    public ConferenceStarter(FmeBridge fmeBridge, List<Attendee> attendees, ConferenceContext conferenceContext) {
        this(fmeBridge, attendees);
        this.conferenceContext = conferenceContext;
    }

    public synchronized void init() {
        // 如果为空，则说明是子会议，需要构建一个子会议的上下文信息，供前端展示
        if (conferenceContext == null) {
            // 先根据ID查看当前会议上下文信息是否存在，若是存在，则证明该会议已开始
            Collection<ConferenceContext> conferenceContextList = ConferenceContextCache.getInstance().getConferenceContextListByConferenceNum(busiConferenceNumber.getId().toString());
            if (conferenceContextList != null && conferenceContextList.size() > 0) {
                if (fmeBridge != null) {
                    CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(busiConferenceNumber.getId().toString());
                    if (coSpace != null) {
                        for (ConferenceContext conferenceContextTemp : conferenceContextList) {
                            if (coSpace.getId().equals(conferenceContextTemp.getCoSpaceId())) {
                                conferenceContext = conferenceContextTemp;
                                break;
                            }
                        }
                    }
                }
            }
            if (conferenceContext == null) {
                conferenceContext = new ConferenceContext();
                conferenceContext.setId(AesEnsUtils.getAesEncryptor().encryptToHex(conferenceContext.getContextKey()));
                conferenceContext.setDeptId(deptId);
                conferenceContext.setConferenceNumber(busiConferenceNumber.getId().toString());
                if (fmeBridge != null) {
                    conferenceContext.setFmeAttendeeRemoteParty(fmeBridge.getAttendeeIp());
                }
            }

            if (attendees != conferenceContext.getAttendees()) {
                attendees.forEach((Attendee attendee) -> {
                    conferenceContext.addAttendee(attendee);
                });
            }

            conferenceContext.setBandwidth(bandwidth);

            // 设置会议名
            conferenceContext.setName(conferenceStandardName);

            // 排序级联子会议的参会者终端，根据weight排序
            Collections.sort(conferenceContext.getAttendees());
        }

        if (fmeBridge != null) {
            conferenceContext.setMcuBridge(fmeBridge);
            try {
                // 设置该会议作为级联子会议对应的fme参会者
                conferenceContext.setFmeAttendeeRemoteParty(fmeBridge.getAttendeeIp());
                String conferenceRemoteParty = conferenceContext.getTenantId() + conferenceContext.getConferenceNumber() + "@" + conferenceContext.getMcuCallIp();
                if (conferenceContext.getMcuCallPort() != null && conferenceContext.getMcuCallPort() != 5060) {
                    conferenceRemoteParty += ":" + conferenceContext.getMcuCallPort();
                }
                conferenceContext.setConferenceRemoteParty(conferenceRemoteParty);

                // 初始化入会方案
                CoSpace coSpace = BeanFactory.getBean(ICoSpaceService.class).getCoSpaceByConferenceNumber(fmeBridge, busiConferenceNumber.getId().toString());
                CallLegProfile callLegProfile = null;
                String calllegProfileId = null;
                if (!ObjectUtils.isEmpty(coSpace.getCallLegProfile())) {
                    callLegProfile = fmeBridge.getDataCache().getCallLegProfile(coSpace.getCallLegProfile());
                }
                conferenceContext.setCoSpaceId(coSpace.getId());

                CoSpaceParamBuilder coSpaceParamBuilder = new CoSpaceParamBuilder();
                if (conferenceContext.getTemplateConferenceId() != null) {
                    BusiTemplateConference busiTemplateConference = BeanFactory.getBean(BusiTemplateConferenceMapper.class)
                            .selectBusiTemplateConferenceById(conferenceContext.getTemplateConferenceId());
                    CallLegProfile callLegProfile0 = null;
                    if (busiTemplateConference.getCallLegProfileId() != null) {
                        callLegProfile0 = fmeBridge.getDataCache().getCallLegProfile(busiTemplateConference.getCallLegProfileId());
                    }
                    if (callLegProfile0 == null) {
                        if (callLegProfile == null) {

                            BusiCallLegProfile con = new BusiCallLegProfile();
                            con.setDeptId(deptId);
                            List<BusiCallLegProfile> clps = BeanFactory.getBean(IBusiCallLegProfileService.class).selectBusiCallLegProfileList(con);
                            if (!ObjectUtils.isEmpty(clps)) {
                                for (BusiCallLegProfile busiCallLegProfile : clps) {
                                    CallLegProfile clp = fmeBridge.getDataCache().getCallLegProfile(busiCallLegProfile.getCallLegProfileUuid());
                                    if (clp != null) {
                                        calllegProfileId = busiCallLegProfile.getCallLegProfileUuid();
                                        break;
                                    }
                                }

                            }
                            if (calllegProfileId == null) {
                                calllegProfileId = BeanFactory.getBean(IBusiCallLegProfileService.class).createDefaultCalllegProfile(fmeBridge, conferenceContext.getDeptId());
                            }

                            // coSpaceParamBuilder.callLegProfile(calllegProfileId);
                            callLegProfile = fmeBridge.getDataCache().getCallLegProfile(calllegProfileId);
                        }
                    } else {
                        callLegProfile = callLegProfile0;
                        // coSpaceParamBuilder.callLegProfile(callLegProfile.getId());
                    }


                    // 默认布局
                    if (!ObjectUtils.isEmpty(((DefaultAttendeeOperation) conferenceContext.getDefaultViewOperation()).getDefaultViewLayout())) {

                        if (SplitScreenCreaterMap.isCustomLayoutTemplate((conferenceContext.getDefaultViewOperation()).getDefaultViewLayout())) {
                            String coSpaceLayout = YesOrNo.convert(conferenceContext.getDefaultViewOperation().getDefaultViewIsBroadcast()) == YesOrNo.YES
                                    ? "automatic"
                                    : OneSplitScreen.LAYOUT;
                            coSpaceParamBuilder.defaultLayout(coSpaceLayout);
                        } else {
                            String coSpaceLayout = YesOrNo.convert(conferenceContext.getDefaultViewOperation().getDefaultViewIsBroadcast()) == YesOrNo.YES
                                    ? conferenceContext.getDefaultViewOperation().getDefaultViewLayout()
                                    : OneSplitScreen.LAYOUT;
                            coSpaceParamBuilder.defaultLayout(coSpaceLayout);
                        }

                    }

                    // 会议密码
                    if (!ObjectUtils.isEmpty(busiTemplateConference.getConferencePassword())) {
                        coSpaceParamBuilder.passcode(busiTemplateConference.getConferencePassword());
                    } else {
                        coSpaceParamBuilder.passcode("");
                    }

                    // 虚拟会议参数
                    if (!ObjectUtils.isEmpty(busiTemplateConference.getCallProfileId())) {
                        coSpaceParamBuilder.callProfile(busiTemplateConference.getCallProfileId());
                        coSpaceParamBuilder.streamUrl(busiTemplateConference.getStreamUrl());
                    }

                    // 呼入标识
                    if (!ObjectUtils.isEmpty(busiTemplateConference.getCallBrandingProfileId())) {
                        coSpaceParamBuilder.callBrandingProfile(busiTemplateConference.getCallBrandingProfileId());
                    }
                } else if (callLegProfile == null) {
                    calllegProfileId = BeanFactory.getBean(IBusiCallLegProfileService.class).createDefaultCalllegProfile(fmeBridge, conferenceContext.getDeptId());
                    //coSpaceParamBuilder.callLegProfile(calllegProfileId);
                    callLegProfile = fmeBridge.getDataCache().getCallLegProfile(calllegProfileId);
                }


                BusiTemplateConference tc = BeanFactory.getBean(BusiTemplateConferenceMapper.class).selectBusiTemplateConferenceById(templateConferenceId);
                Map<String, Object> businessProperties = tc.getBusinessProperties();
                if (businessProperties != null) {
                    boolean isMute = true;
                    Object isMuteObj = businessProperties.get("isMute");
                    try {
                        if (isMuteObj != null) {
                            isMute = (boolean) isMuteObj;
                        }
                    } catch (Exception e) {
                    }
                    String quality = "1080p";
                    Object quality_obj = businessProperties.get("quality");
                    if (quality_obj != null) {
                        quality = (String) quality_obj;
                    }
                    if (isMuteObj != null || quality_obj != null) {
                        if (quality.contains("1080")) {
                            calllegProfileId = BeanFactory.getBean(IBusiCallLegProfileService.class).createDefaultCalllegProfileNotInDb(fmeBridge, conferenceContext.getDeptId(), isMute, "max1080p30");
                        } else {
                            calllegProfileId = BeanFactory.getBean(IBusiCallLegProfileService.class).createDefaultCalllegProfileNotInDb(fmeBridge, conferenceContext.getDeptId(), isMute, "max720p30");
                        }
                        callLegProfile = fmeBridge.getDataCache().getCallLegProfile(calllegProfileId);
                    }
                }


                // 更新入会参数
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("defaultLayout", ""));
                BeanFactory.getBean(IBusiCallLegProfileService.class).updateCallLegProfile(fmeBridge, callLegProfile.getId(), nameValuePairs);


                String profileId = copyDefaultCalllegProfile(callLegProfile);
                coSpaceParamBuilder.callLegProfile(profileId);


                // coSpace对象中（比较更新）
                BeanFactory.getBean(ICoSpaceService.class).updateCoSpace(fmeBridge, coSpace, coSpaceParamBuilder);
                CallLegProfileInfoResponse callLegProfileInfoResponse = fmeBridge.getCallLegProfileInvoker().getCallLegProfile(profileId);
                callLegProfile = callLegProfileInfoResponse.getCallLegProfile();
                conferenceContext.setCallLegProfile(callLegProfile);
            } catch (Throwable e) {
                logger.error(conferenceContext.getConferenceNumber() + " - " + conferenceContext.getName() + " 初始化失败： ", e);
                throw new SystemException(1003454, "会议【" + conferenceContext.getConferenceNumber() + "】初始化失败：" + e.getMessage());
            }
        } else {
            throw new SystemException(1003454, "会议【" + conferenceContext.getConferenceNumber() + "】初始化失败：FME不存在！");
        }
    }

    public synchronized void start() {

        String deptName = SysDeptCache.getInstance().get(deptId).getDeptName();
        logger.info("[启动会议: " + deptName + "-" + busiConferenceNumber.getId() + "-1]，使用会议桥工具对象FmeBridge：{}", fmeBridge);

        updateCoSpaceName(deptName);

        // 获取call，不存在则创建
        CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(busiConferenceNumber.getId());
        Call call = fmeBridge.getDataCache().getCallByCoSpaceUuid(coSpace.getId());
        if (call != null) {
            if (!conferenceContext.isStart()) {
                setStartTime(call);
            }
        }

        GenericValue<Call> callValue = new GenericValue<>();
        List<BusiHistoryCall> busiHistoryCalls = new ArrayList<>();
        FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(conferenceContext.getDeptId(), new FmeBridgeAddpterProcessor() {
            public void process(FmeBridge fmeBridge) {
                Call call = BeanFactory.getBean(ICallService.class).createCall(fmeBridge, conferenceContext.getConferenceNumber(), conferenceContext.getName());
                callValue.setValue(call);
                if (BeanFactory.getBean(BusiHistoryCallMapper.class).selectBusiHistoryCallByCallId(call.getId()) == null) {
                    // 历史call保存
                    BusiHistoryCall historyCall = new BusiHistoryCall();
                    historyCall.setCallId(call.getId());
                    historyCall.setCoSpace(coSpace.getId());
                    historyCall.setDeptId(deptId);
                    historyCall.setCreateTime(new Date());
                    busiHistoryCalls.add(historyCall);
                }
            }
        });

        if (!conferenceContext.isStart()) {
            call = callValue.getValue();
            setStartTime(call);
        }
        if (call.getLocked()) {
            fmeBridge.getCallInvoker().updateCall(call.getId(), new CallParamBuilder().locked(false).build());
        }
        ThreadUtils.sleep(500);

        ConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            a.setParticipantUuid(null);
        });

        FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(conferenceContext.getDeptId(), new FmeBridgeAddpterProcessor() {
            public void process(FmeBridge fmeBridge) {
                UriParticipantsMap ps = fmeBridge.getDataCache().getUriParticipantMapByConferenceNumber(conferenceContext.getConferenceNumber());
                if (ps != null) {
                    for (Iterator<Map<String, Participant>> iterator = ps.values().iterator(); iterator.hasNext(); ) {
                        for (Iterator<Participant> iterator0 = iterator.next().values().iterator(); iterator0.hasNext(); ) {
                            Participant p = iterator0.next();
                            if (p.is(ParticipantState.CONNECTED)) {
                                BeanFactory.getBean(IAttendeeFieldService.class).updateByParticipant(fmeBridge, p);
                            }
                        }
                    }
                }
            }
        });

        List<TerminalAttendee> mqttJoinTerminals = new ArrayList<>();
        ConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            a.setConferenceNumber(conferenceContext.getConferenceNumber());
            if (a instanceof TerminalAttendee) {
                TerminalAttendee ta = (TerminalAttendee) a;
                BusiTerminal bt = TerminalCache.getInstance().get(ta.getTerminalId());
                if (TerminalType.isRtsp(bt.getType())) {
                    //主动呼叫
                    FcmThreadPool.exec(() -> {
                        try {
                            boolean contains = FmeDataCache.initiTemplateId.contains(templateConferenceId);
                            if (!contains) {
                                BeanFactory.getBean(IAttendeeService.class).callRtsp(conferenceContext, a, bt.getProtocol());
                            }
                        } catch (Exception e) {
                        }
                    });
                }else if (TerminalType.isGB28181(bt.getType())) {
                    //主动呼叫
                    FcmThreadPool.exec(() -> {
                        try {
                            boolean contains = FmeDataCache.initiTemplateId.contains(templateConferenceId);
                            if (!contains) {
                                BeanFactory.getBean(IAttendeeService.class).callRtsp(conferenceContext, a, bt.getProtocol());
                            }
                        } catch (Exception e) {
                        }
                    });
                }
                else {
                    // 被叫由会控负责发起呼叫
                    if (AttendType.convert(ta.getAttendType()) == AttendType.OUT_BOUND) {
                        FcmThreadPool.exec(() -> {
                            BeanFactory.getBean(IAttendeeService.class).callAttendee(a);
                        });
                    }

                    if (!ObjectUtils.isEmpty(bt.getSn())) {
                        if (!ta.isMeetingJoined()) {
                            mqttJoinTerminals.add(ta);
                        }
                    }
                }
            } else {
                if (!a.isMcuAttendee()) {
                    FcmThreadPool.exec(() -> {
                        BeanFactory.getBean(IAttendeeService.class).callAttendee(a);
                    });
                }
            }
        });

        // 处理mqtt业务
        doMqttService(mqttJoinTerminals);

        // 保存历史记录
        BusiHistoryConference s = BeanFactory.getBean(IBusiHistoryConferenceService.class).saveHistory(coSpace, call, conferenceContext);
        conferenceContext.setHistoryConference(s);
        for (BusiHistoryCall busiHistoryCall : busiHistoryCalls) {
            busiHistoryCall.setHistoryConferenceId(s.getId());
            BeanFactory.getBean(BusiHistoryCallMapper.class).insertBusiHistoryCall(busiHistoryCall);
        }

        // 开启直播功能
        BusiTemplateConference tc = BeanFactory.getBean(BusiTemplateConferenceMapper.class).selectBusiTemplateConferenceById(conferenceContext.getTemplateConferenceId());
        if (tc.getStreamingEnabled() != null) {
            if (tc.getStreamingEnabled() == StreamingEnabledType.CLOUDS.getValue()) {
                BeanFactory.getBean(IBusiConferenceService.class).stream(conferenceContext, true, conferenceContext.getCloudsStreamingUrl());
            } else if (tc.getStreamingEnabled() == StreamingEnabledType.THIRD_PARTY.getValue()) {
                BeanFactory.getBean(IBusiConferenceService.class).stream(conferenceContext.getId(), true, conferenceContext.getStreamingUrl());
            } else if (tc.getStreamingEnabled() == StreamingEnabledType.YES.getValue()) {
                if (!ObjectUtils.isEmpty(conferenceContext.getStreamingRemoteParty())) {
                    BeanFactory.getBean(IBusiConferenceService.class).stream(conferenceContext.getId(), true, conferenceContext.getStreamingUrl());
                } else {
                    BeanFactory.getBean(IBusiConferenceService.class).stream(conferenceContext, true, conferenceContext.getStreamingUrl());
                }
            } else if (!ObjectUtils.isEmpty(conferenceContext.getStreamingRemoteParty())) {
                BeanFactory.getBean(IBusiConferenceService.class).stream(conferenceContext.getId(), true, conferenceContext.getStreamingUrl());
            } else {
                if (!ObjectUtils.isEmpty(conferenceContext.getStreamingUrl())) {
                    CoSpaceParamBuilder coSpaceParamBuilder = new CoSpaceParamBuilder();
                    coSpaceParamBuilder.streamUrl(conferenceContext.getStreamingUrl());
                    FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceContext(conferenceContext);
                    try {
                        BeanFactory.getBean(ICoSpaceService.class).updateCoSpace(fmeBridge, fmeBridge.getDataCache().getCoSpaceByConferenceNumber(conferenceContext.getConferenceNumber()), coSpaceParamBuilder);
                    } catch (Exception e) {
                        throw new CustomException("设置直播地址失败!");
                    }
                }
            }
        } else {
            if (!ObjectUtils.isEmpty(conferenceContext.getStreamingUrl())) {
                CoSpaceParamBuilder coSpaceParamBuilder = new CoSpaceParamBuilder();
                coSpaceParamBuilder.streamUrl(conferenceContext.getStreamingUrl());
                FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceContext(conferenceContext);
                try {
                    BeanFactory.getBean(ICoSpaceService.class).updateCoSpace(fmeBridge, fmeBridge.getDataCache().getCoSpaceByConferenceNumber(conferenceContext.getConferenceNumber()), coSpaceParamBuilder);
                } catch (Exception e) {
                    throw new CustomException("设置直播地址失败!");
                }
            }
        }

        // 开启录制功能
        if (tc.getRecordingEnabled() != null && tc.getRecordingEnabled() == YesOrNo.YES.getValue()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    BeanFactory.getBean(IBusiRecordsService.class).updateBusiRecords(true, conferenceContext.getContextKey());
                }
            }).start();
        }

        // 开启会议纪要功能
        if (tc.getMinutesEnabled() != null && tc.getMinutesEnabled() == YesOrNo.YES.getValue()) {
            String host = "";
            BusiTransServerMapper busiTransServerMapper = BeanFactory.getBean(BusiTransServerMapper.class);
            List<BusiTransServer> busiTransServerList = busiTransServerMapper.selectBusiTransServerList(new BusiTransServer());
            if (busiTransServerList.size() > 0) {
                BusiTransServer busiTransServer = busiTransServerList.get(0);
                try {
                    host = busiTransServer.getIp();
                } catch (Exception e) {
                }
            }
            if (StringUtils.isNotEmpty(host)) {
                StartMeetingMinutesTask startMeetingMinutesTask = new StartMeetingMinutesTask(conferenceContext.getId(), 0, conferenceContext.getId(), host, 9900);
                TaskService taskService = BeanFactory.getBean(TaskService.class);
                taskService.addTask(startMeetingMinutesTask);
            }
        }

        //会议纪要文档
        try {
            ConferenceTakeSnapshotTask conferenceTakeSnapshotTask = new ConferenceTakeSnapshotTask(conferenceContext.getId(), 0, conferenceContext.getId(),conferenceContext.getConferenceNumber());
           // TaskServiceMeetingMinutes taskServiceMeetingMinutes = BeanFactory.getBean(TaskServiceMeetingMinutes.class);
            conferenceTakeSnapshotTask.start();
        } catch (Exception e) {
        }

        try {
            startCloudConference(tc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        TaskService taskService = BeanFactory.getBean(TaskService.class);
        // 如未开启云会议，尝试几遍
        StartCloudConferenceTask startCloudConferenceTask = new StartCloudConferenceTask(conferenceContext.getId(), 1000, tc);
        taskService.addTask(startCloudConferenceTask);

        String region = ExternalConfigCache.getInstance().getRegion();
        Map<String, Object> businessProperties = tc.getBusinessProperties();
        if (businessProperties != null) {
            //开启短信发送
            Object sendSms = businessProperties.get("sendSms");
            if (sendSms != null) {
                Boolean b = (Boolean) sendSms;
                if (b) {
                    //给终端发送短信
                    if (Objects.equals("ops", region)) {
                        //查询所有电话号码
                        List<Attendee> attendees = conferenceContext.getAttendees();
                        for (Attendee attendee : attendees) {
                            if (attendee instanceof TerminalAttendee) {
                                TerminalAttendee terminalAttendee = (TerminalAttendee) attendee;
                                String phone = terminalAttendee.getPhone();
                                if (Strings.isNotBlank(phone)) {
                                    CloudSmsLocaltoRemoteTask cloudSmsLocaltoRemoteTask = new CloudSmsLocaltoRemoteTask(attendee.getId() + "_BK", 1000, conferenceName, conferenceContext.getConferenceNumber(), phone, DateUtil.convertDateToString(conferenceContext.getStartTime(), null), null, NotifyType.ADMIN_MEETING_START);
                                    taskService.addTask(cloudSmsLocaltoRemoteTask);
                                }
                            }
                        }
                    }
                }
            }
        }

        //创建IM群组
        if (OpsDataCache.getInstance().getImTime() != 0 || ExternalConfigCache.getInstance().isEnableIm()) {
            IMService imService = BeanFactory.getBean(IMService.class);
            Object imGroupIdObj = businessProperties.get("imGroupId");
            if (imGroupIdObj != null) {
                String imGroupId = (String) imGroupIdObj;
                imService.createAVChatRoom(imGroupId);
            } else {
                String imGroupId = conferenceContext.getConferenceNumber() + "@" + tc.getId() + "@" + conferenceContext.getStartTime().getTime();
                imService.createAVChatRoom(imGroupId);
                businessProperties.put("imGroupId", imGroupId);
                tc.setBusinessProperties(businessProperties);
                BeanFactory.getBean(BusiTemplateConferenceMapper.class).updateBusiTemplateConference(tc);
            }
        }
    }

    private void startCloudConference(BusiTemplateConference tc) {
        BusiMcuTencentConferenceAppointmentMapper busiMcuTencentConferenceAppointmentMapper = BeanFactory.getBean(BusiMcuTencentConferenceAppointmentMapper.class);
        BusiMcuHwcloudConferenceAppointmentMapper busiMcuHwcloudConferenceAppointmentMapper = BeanFactory.getBean(BusiMcuHwcloudConferenceAppointmentMapper.class);
        ViewTemplateConferenceMapper viewTemplateConferenceMapper = BeanFactory.getBean(ViewTemplateConferenceMapper.class);
        ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
        viewTemplateConferenceCon.setUpCascadeId(tc.getId());
        viewTemplateConferenceCon.setUpCascadeMcuType(McuType.FME.getCode());
        List<ViewTemplateConference> downCascadeList = viewTemplateConferenceMapper.selectViewTemplateConferenceList(viewTemplateConferenceCon);
        for (ViewTemplateConference viewTemplateConference : downCascadeList) {
            if (McuType.MCU_TENCENT.getCode().equals(viewTemplateConference.getMcuType())) {
                List<BusiMcuTencentConferenceAppointment> busiMcuTencentConferenceAppointmentList = busiMcuTencentConferenceAppointmentMapper.selectBusiMcuTencentConferenceAppointmentByTemplateId(viewTemplateConference.getId());
                if (busiMcuTencentConferenceAppointmentList.size() > 0) {
                    BusiMcuTencentConferenceAppointment busiMcuTencentConferenceAppointment = busiMcuTencentConferenceAppointmentList.get(0);
                    if (busiMcuTencentConferenceAppointment.getIsCloudConference() != null && busiMcuTencentConferenceAppointment.getIsCloudConference() == 1) {
                        cloudConference(tc, viewTemplateConference.getMcuType(), busiMcuTencentConferenceAppointment);
                    }
                }
            } else if (McuType.MCU_HWCLOUD.getCode().equals(viewTemplateConference.getMcuType())) {
                List<BusiMcuHwcloudConferenceAppointment> busiMcuHwcloudConferenceAppointmentList = busiMcuHwcloudConferenceAppointmentMapper.selectBusiMcuHwcloudConferenceAppointmentByTemplateId(viewTemplateConference.getId());
                if (busiMcuHwcloudConferenceAppointmentList.size() > 0) {
                    BusiMcuHwcloudConferenceAppointment busiMcuHwcloudConferenceAppointment = busiMcuHwcloudConferenceAppointmentList.get(0);
                    if (busiMcuHwcloudConferenceAppointment.getIsCloudConference() != null && busiMcuHwcloudConferenceAppointment.getIsCloudConference() == 1) {
                        cloudConference(tc, viewTemplateConference.getMcuType(), busiMcuHwcloudConferenceAppointment);
                    }
                }
            }
        }

    }

    private void cloudConference(BusiTemplateConference tc, String cloudMcuType_s, BusiConferenceAppointment busiConferenceAppointment) {
        if (Objects.equals(cloudMcuType_s, McuType.MCU_TENCENT.getCode())) {
            BusiMcuTencentConferenceAppointment busiMcuTencentConferenceAppointment = (BusiMcuTencentConferenceAppointment) busiConferenceAppointment;
            Map<String, String> mapTencent = TencentCloudUtil.getConferenceNumber(tc.getName(), busiMcuTencentConferenceAppointment.getCloudConferenceId());
            if(mapTencent==null){
                return;
            }
            String cloudConferenceId = mapTencent.get("conferenceId");
            if (!cloudConferenceId.equals(busiMcuTencentConferenceAppointment.getCloudConferenceId())) {
                BusiMcuTencentConferenceAppointmentMapper busiMcuTencentConferenceAppointmentMapper = BeanFactory.getBean(BusiMcuTencentConferenceAppointmentMapper.class);
                BusiMcuTencentConferenceAppointment busiMcuTencentConferenceAppointmentUpdate = new BusiMcuTencentConferenceAppointment();
                busiMcuTencentConferenceAppointmentUpdate.setId(busiConferenceAppointment.getId());
                busiMcuTencentConferenceAppointmentUpdate.setCloudConferenceId(cloudConferenceId);
                busiMcuTencentConferenceAppointmentMapper.updateBusiMcuTencentConferenceAppointment(busiMcuTencentConferenceAppointmentUpdate);
            }
            mapTencent.put("conferenceNumber", mapTencent.get("conferenceNumber"));

            CloudConference cloudConference = new CloudConference();
            cloudConference.setConferenceNumber(mapTencent.get("conferenceNumber"));
            cloudConference.setCascadeMcuType(McuType.MCU_TENCENT.getCode());
            cloudConference.setCascadeConferenceId(mapTencent.get("conferenceId"));
            cloudConference.setName("腾讯会议" + mapTencent.get("conferenceNumber"));
            conferenceContext.getCloudConferenceList().add(cloudConference);
            conferenceContext.setCloudtencentId(cloudConference.getCascadeConferenceId());
            if (mapTencent != null) {

                logger.info("腾讯云会议号:{}", mapTencent.get("conferenceNumber"));
                logger.info("腾讯云会议号:{}", mapTencent.get("conferenceNumber"));
                logger.info("腾讯云会议号:{}", mapTencent.get("conferenceNumber"));
                logger.info("腾讯云会议号:{}", mapTencent.get("conferenceNumber"));

                InvitedAttendee ia = new InvitedAttendee();
                ia.setConferenceNumber(conferenceContext.getConferenceNumber());
                ia.setId(UUID.randomUUID().toString());
                ia.setName("腾讯会议" + mapTencent.get("conferenceNumber"));
                ia.setRemoteParty(mapTencent.get("conferenceNumber") + "@" + getMraIp());
                ia.setWeight(1);
                ia.setDeptId(conferenceContext.getDeptId());
                ia.setUpCascadeConferenceId(conferenceContext.getId());
                conferenceContext.addAttendee(ia);
                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, ia);
                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + ia.getName() + "】被邀请加入");
                new CallAttendeeProcessor(ia).process();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
            }

        } else if (Objects.equals(cloudMcuType_s, McuType.MCU_HWCLOUD.getCode())) {
            BusiMcuHwcloudConferenceAppointment busiMcuHwcloudConferenceAppointment = (BusiMcuHwcloudConferenceAppointment) busiConferenceAppointment;
            Map<String, String> mapHwcloud = HuaweiCloudUtil.getConferenceNumber(tc.getName(), busiMcuHwcloudConferenceAppointment.getCloudConferenceId());
            if(mapHwcloud==null){
                return;
            }
            String cloudConferenceId = mapHwcloud.get("conferenceId");
            if (!cloudConferenceId.equals(busiMcuHwcloudConferenceAppointment.getCloudConferenceId())) {
                BusiMcuHwcloudConferenceAppointmentMapper busiMcuHwcloudConferenceAppointmentMapper = BeanFactory.getBean(BusiMcuHwcloudConferenceAppointmentMapper.class);
                BusiMcuHwcloudConferenceAppointment busiMcuHwcloudConferenceAppointmentUpdate = new BusiMcuHwcloudConferenceAppointment();
                busiMcuHwcloudConferenceAppointmentUpdate.setId(busiConferenceAppointment.getId());
                busiMcuHwcloudConferenceAppointmentUpdate.setCloudConferenceId(cloudConferenceId);
                busiMcuHwcloudConferenceAppointmentMapper.updateBusiMcuHwcloudConferenceAppointment(busiMcuHwcloudConferenceAppointmentUpdate);
            }
            mapHwcloud.put("conferenceNumber", mapHwcloud.get("conferenceNumber"));

            CloudConference cloudConference = new CloudConference();
            cloudConference.setConferenceNumber(mapHwcloud.get("conferenceNumber"));
            cloudConference.setCascadeMcuType(McuType.MCU_HWCLOUD.getCode());
            cloudConference.setCascadeConferenceId(mapHwcloud.get("conferenceId"));
            cloudConference.setName("华为云会议" + mapHwcloud.get("conferenceNumber"));
            conferenceContext.getCloudConferenceList().add(cloudConference);
            conferenceContext.setCloudHwcloudId(cloudConference.getCascadeConferenceId());
            if (mapHwcloud != null) {

                logger.info("华为云会议号:{}", mapHwcloud.get("conferenceNumber"));
                logger.info("华为云会议号:{}", mapHwcloud.get("conferenceNumber"));
                logger.info("华为云会议号:{}", mapHwcloud.get("conferenceNumber"));
                logger.info("华为云会议号:{}", mapHwcloud.get("conferenceNumber"));

                InvitedAttendee ia = new InvitedAttendee();
                ia.setConferenceNumber(conferenceContext.getConferenceNumber());
                ia.setId(UUID.randomUUID().toString());
                ia.setName("华为云" + mapHwcloud.get("conferenceNumber"));
                ia.setRemoteParty(mapHwcloud.get("conferenceNumber") + "@" + getMraIp());
                ia.setWeight(1);
                ia.setDeptId(conferenceContext.getDeptId());
                ia.setUpCascadeConferenceId(conferenceContext.getId());
                conferenceContext.addAttendee(ia);
                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, ia);
                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + ia.getName() + "】被邀请加入");
                new CallAttendeeProcessor(ia).process();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
            }

        }
    }

    private String copyDefaultCalllegProfile(CallLegProfile source) {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("name", source.getName()));
        if (source.getRxAudioMute() != null) {
            nameValuePairs.add(new BasicNameValuePair("rxAudioMute", source.getRxAudioMute() == true ? "true" : "false"));
        }
        if (source.getAllowAllPresentationContributionAllowed() != null) {
            nameValuePairs.add(new BasicNameValuePair("allowAllPresentationContributionAllowed", source.getAllowAllPresentationContributionAllowed() == true ? "true" : "false"));
        }
        if (StringUtils.isNotBlank(source.getQualityMain())) {
            nameValuePairs.add(new BasicNameValuePair("qualityMain", source.getQualityMain()));
        }
        if (StringUtils.isNotBlank(source.getQualityPresentation())) {
            nameValuePairs.add(new BasicNameValuePair("qualityPresentation", source.getQualityPresentation()));
        }

        if (StringUtils.isNotBlank(source.getParticipantCounter())) {
            nameValuePairs.add(new BasicNameValuePair("participantCounter", source.getParticipantCounter()));
        }

        if (source.getParticipantLabels() != null) {
            nameValuePairs.add(new BasicNameValuePair("participantLabels", source.getParticipantLabels() == true ? "true" : "false"));
        }
        if (source.getMuteSelfAllowed() != null) {
            nameValuePairs.add(new BasicNameValuePair("muteSelfAllowed", source.getMuteSelfAllowed() == true ? "true" : "false"));
        }
        if (StringUtils.isNotBlank(source.getDefaultLayout())) {
            nameValuePairs.add(new BasicNameValuePair("defaultLayout", source.getDefaultLayout()));
        }
        if (source.getTxAudioMute() != null) {
            nameValuePairs.add(new BasicNameValuePair("txAudioMute", source.getTxAudioMute() == true ? "true" : "false"));
        }

        if (StringUtils.isNotBlank(source.getPresentationDisplayMode())) {
            nameValuePairs.add(new BasicNameValuePair("presentationDisplayMode", source.getPresentationDisplayMode()));
        }

        if (source.getPresentationViewingAllowed() != null) {
            nameValuePairs.add(new BasicNameValuePair("presentationViewingAllowed", source.getPresentationViewingAllowed() == true ? "true" : "false"));
        }

        if (source.getSipPresentationChannelEnabled() != null) {
            nameValuePairs.add(new BasicNameValuePair("sipPresentationChannelEnabled", source.getSipPresentationChannelEnabled() == true ? "true" : "false"));
        }
        if (StringUtils.isNotBlank(source.getBfcpMode())) {
            nameValuePairs.add(new BasicNameValuePair("bfcpMode", source.getBfcpMode()));
        }

        if (source.getControlRemoteCameraAllowed() != null) {
            nameValuePairs.add(new BasicNameValuePair("controlRemoteCameraAllowed", source.getControlRemoteCameraAllowed() == true ? "true" : "false"));
        }
        if (StringUtils.isNotBlank(source.getAudioGainMode())) {
            nameValuePairs.add(new BasicNameValuePair("audioGainMode", source.getAudioGainMode()));
        }


        if (source.getSetImportanceAllowed() != null) {
            nameValuePairs.add(new BasicNameValuePair("setImportanceAllowed", source.getSetImportanceAllowed() == true ? "true" : "false"));
        }
        if (StringUtils.isNotBlank(source.getSipMediaEncryption())) {
            nameValuePairs.add(new BasicNameValuePair("sipMediaEncryption", source.getSipMediaEncryption()));
        }


        if (source.getRxAudioMute() != null) {
            nameValuePairs.add(new BasicNameValuePair("rxAudioMute", source.getRxAudioMute() == true ? "true" : "false"));
        }

        if (source.getRxVideoMute() != null) {
            nameValuePairs.add(new BasicNameValuePair("rxVideoMute", source.getRxVideoMute() == true ? "true" : "false"));
        }

        if (source.getTxVideoMute() != null) {
            nameValuePairs.add(new BasicNameValuePair("txVideoMute", source.getTxVideoMute() == true ? "true" : "false"));
        }

        if (source.getNeedsActivation() != null) {
            nameValuePairs.add(new BasicNameValuePair("needsActivation", source.getNeedsActivation() == true ? "true" : "false"));
        }
        if (StringUtils.isNotBlank(source.getDeactivationMode())) {
            nameValuePairs.add(new BasicNameValuePair("deactivationMode", source.getDeactivationMode()));
        }


        if (source.getDeactivationModeTime() != null) {
            nameValuePairs.add(new BasicNameValuePair("deactivationModeTime", source.getDeactivationModeTime().toString()));
        }

        if (source.getTelepresenceCallsAllowed() != null) {
            nameValuePairs.add(new BasicNameValuePair("telepresenceCallsAllowed", source.getTelepresenceCallsAllowed() == true ? "true" : "false"));
        }
        if (source.getCallLockAllowed() != null) {
            nameValuePairs.add(new BasicNameValuePair("callLockAllowed", source.getCallLockAllowed() == true ? "true" : "false"));
        }

        if (source.getChangeJoinAudioMuteOverrideAllowed() != null) {
            nameValuePairs.add(new BasicNameValuePair("changeJoinAudioMuteOverrideAllowed", source.getChangeJoinAudioMuteOverrideAllowed() == true ? "true" : "false"));
        }
        if (source.getEndCallAllowed() != null) {
            nameValuePairs.add(new BasicNameValuePair("endCallAllowed", source.getEndCallAllowed() == true ? "true" : "false"));
        }
        if (source.getDisconnectOthersAllowed() != null) {
            nameValuePairs.add(new BasicNameValuePair("disconnectOthersAllowed", source.getDisconnectOthersAllowed() == true ? "true" : "false"));
        }
        if (source.getAddParticipantAllowed() != null) {
            nameValuePairs.add(new BasicNameValuePair("addParticipantAllowed", source.getAddParticipantAllowed() == true ? "true" : "false"));
        }
        if (source.getMuteOthersAllowed() != null) {
            nameValuePairs.add(new BasicNameValuePair("muteOthersAllowed", source.getMuteOthersAllowed() == true ? "true" : "false"));
        }
        if (source.getVideoMuteOthersAllowed() != null) {
            nameValuePairs.add(new BasicNameValuePair("videoMuteOthersAllowed", source.getVideoMuteOthersAllowed() == true ? "true" : "false"));
        }
        if (source.getVideoMuteSelfAllowed() != null) {
            nameValuePairs.add(new BasicNameValuePair("videoMuteSelfAllowed", source.getVideoMuteSelfAllowed() == true ? "true" : "false"));
        }

        if (source.getChangeLayoutAllowed() != null) {
            nameValuePairs.add(new BasicNameValuePair("changeLayoutAllowed", source.getChangeLayoutAllowed() == true ? "true" : "false"));
        }

        if (source.getAllowAllMuteSelfAllowed() != null) {
            nameValuePairs.add(new BasicNameValuePair("allowAllMuteSelfAllowed", source.getAllowAllMuteSelfAllowed() == true ? "true" : "false"));
        }

        if (source.getMaxCallDurationTime() != null) {
            nameValuePairs.add(new BasicNameValuePair("maxCallDurationTime", source.getMaxCallDurationTime().toString()));
        }


        String profileId = fmeBridge.getCallLegProfileInvoker().createCallLegProfile(nameValuePairs);
        return profileId;
    }

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     *
     * @param call
     * @author sinhy
     * @since 2021-12-15 09:58
     */
    private void setStartTime(Call call) {
        Date startTime = null;
        if (call.getDurationSeconds() != null) {
            startTime = DateUtils.getDiffDate(-call.getDurationSeconds(), TimeUnit.SECONDS);
        } else {
            startTime = new Date();
        }
        conferenceContext.setStartTime(startTime);
        String message = "会议【" + conferenceContext.getName() + "】启动成功！";
        Map<String, Object> obj = new HashMap<>();
        obj.put("startTime", conferenceContext.getStartTime());
        obj.put("message", message);
        obj.put("tenantId", conferenceContext.getTenantId());
        obj.put("conferenceNumber", conferenceContext.getConferenceNumber());
        obj.put("streamingUrl", conferenceContext.getStreamingUrl());
        obj.put("streamingUrlList", conferenceContext.getStreamUrlList());
        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_STARTED, obj);
        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, message);

        if (MqttForFmePushMessageCache.getInstance().getMqttForFmePushMessageListener() != null) {
            Long createUserId = conferenceContext.getCreateUserId();
            if (createUserId != null) {
                BusiUserTerminalMapper busiUserTerminalMapper = BeanFactory.getBean(BusiUserTerminalMapper.class);
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByUserId(createUserId);
                if (busiUserTerminal != null) {
                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(busiUserTerminal.getTerminalId());
                    if (busiTerminal != null && org.springframework.util.StringUtils.hasText(busiTerminal.getSn())) {
                        String clientId = busiTerminal.getSn();
                        String topic = MqttConfigConstant.TOPIC_PREFIX + clientId;
                        String action = TerminalTopic.CREATE_CONFERENCE;
                        JSONObject jObj = (JSONObject) JSON.toJSON(conferenceContext);
                        MqttForFmePushMessageCache.getInstance().getMqttForFmePushMessageListener().onPushMessage(ResponseInfo.CODE_200, "会议启动成功！", topic, action, jObj, clientId, "");
                    }
                }
            }
        }
    }

    /**
     * 处理mqtt业务
     *
     * @param mqttJoinTerminals void
     * @author sinhy
     * @since 2021-11-18 17:20
     */
    private void doMqttService(List<TerminalAttendee> mqttJoinTerminals) {
        try {
            if (ObjectUtils.isEmpty(conferenceContext.getLiveTerminals()) && ObjectUtils.isEmpty(mqttJoinTerminals)) {
                logger.info("直播终端和mqtt参会类型终端为空，mqtt请求发送终止！");
                return;
            }
            IMqttService mqttService = BeanFactory.getBean(IMqttService.class);
            CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(busiConferenceNumber.getId());
            mqttService.pushConferenceInfo(conferenceContext.getId(), coSpace.getPasscode(), mqttJoinTerminals, conferenceContext.getLiveTerminals());
            logger.info("pushConferenceInfo执行成功，发送mqtt终端数：" + mqttJoinTerminals.size() + ", 直播终端数：" + conferenceContext.getLiveTerminals().size());

            // mqtt主叫
            StringBuilder msgBuilder = new StringBuilder();
            int i = 0;
            if (!ObjectUtils.isEmpty(mqttJoinTerminals)) {
                msgBuilder.append("已向以下终端发送MQTT主叫入会请求：【");
                for (TerminalAttendee busiTerminal : mqttJoinTerminals) {
                    if (i > 0) {
                        msgBuilder.append(", ");
                    }
                    msgBuilder.append(busiTerminal.getName());
                    i++;
                }
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, msgBuilder.toString());
            }

            if (!ObjectUtils.isEmpty(conferenceContext.getLiveTerminals())) {
                // mqtt终端观看直播
                msgBuilder = new StringBuilder();
                msgBuilder.append("已向以下终端发送直播观看请求：【");
                i = 0;
                for (TerminalAttendee busiTerminal : conferenceContext.getLiveTerminals()) {
                    if (i > 0) {
                        msgBuilder.append(", ");
                    }
                    msgBuilder.append(busiTerminal.getName());
                    i++;
                }
                msgBuilder.append("】");
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, msgBuilder.toString());
            }
        } catch (Throwable e) {
            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, "MQTT直播和主叫请求发送失败：" + CauseUtils.getRootCause(e));
            logger.error("调用mqtt服务失败", e);
        }
    }

    /**
     * <pre>更新会议室名字</pre>
     *
     * @param deptName void
     * @author lilinhai
     * @since 2021-02-01 15:28
     */
    private void updateCoSpaceName(String deptName) {
        SystemException se = null;
        CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(busiConferenceNumber.getId());
        int i = 0;
        while ((i++) < 3) {
            try {
                RestResponse restResponse = fmeBridge.getCoSpaceInvoker().updateCoSpace(coSpace.getId(), new CoSpaceParamBuilder().name(conferenceContext.getName()).build());
                if (!restResponse.isSuccess()) {
                    throw new SystemException(1007348, "【" + deptName + "】更新会议室[" + busiConferenceNumber.getId() + "]名字失败，可能是会议号所属的会议室不存在，请排查：" + i);
                }

                logger.info("[启动会议: " + deptName + "-" + busiConferenceNumber.getId() + "-2]，更新会议室名字: {}", conferenceName);
                return;
            } catch (SystemException e) {
                ThreadUtils.sleep(100);
                se = e;
                fmeBridge.getFmeLogger().logInfo(deptName, true, e);
                FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(deptId, new FmeBridgeAddpterProcessor() {
                    public void process(FmeBridge fmeBridge) {
                        fmeBridge.getDataCache().deleteCoSpaceByUri(busiConferenceNumber.getId().toString());
                    }
                });
                coSpace = BeanFactory.getBean(ICoSpaceService.class).getCoSpaceByConferenceNumber(fmeBridge, busiConferenceNumber.getId().toString());
            }
        }

        if (se != null) {
            throw se;
        }
    }

    /**
     * <p>
     * Set Method : deptId long
     * </p>
     *
     * @param deptId
     */
    public void setDeptId(long deptId) {
        this.deptId = deptId;
    }

    /**
     * <p>
     * Set Method : busiConferenceNumber BusiConferenceNumber
     * </p>
     *
     * @param busiConferenceNumber
     */
    public void setBusiConferenceNumber(BusiConferenceNumber busiConferenceNumber) {
        this.busiConferenceNumber = busiConferenceNumber;
    }

    /**
     * <p>
     * Set Method : conferenceName String
     * </p>
     *
     * @param conferenceName
     */
    public void setConferenceName(String conferenceName) {
        this.conferenceName = conferenceName;
    }

    /**
     * <p>
     * Set Method : bandwidth Integer
     * </p>
     *
     * @param bandwidth
     */
    public void setBandwidth(Integer bandwidth) {
        this.bandwidth = bandwidth;
    }

    /**
     * <p>
     * Set Method : conferenceStandardName String
     * </p>
     *
     * @param conferenceStandardName
     */
    public void setConferenceStandardName(String conferenceStandardName) {
        this.conferenceStandardName = conferenceStandardName;
    }

    /**
     * <p>
     * Get Method : templateConferenceId Long
     * </p>
     *
     * @return templateConferenceId
     */
    public Long getTemplateConferenceId() {
        return templateConferenceId;
    }

    /**
     * <p>
     * Set Method : templateConferenceId Long
     * </p>
     *
     * @param templateConferenceId
     */
    public void setTemplateConferenceId(Long templateConferenceId) {
        this.templateConferenceId = templateConferenceId;
    }

    /**
     * <p>
     * Get Method : conferenceContext ConferenceContext
     * </p>
     *
     * @return conferenceContext
     */
    public ConferenceContext getConferenceContext() {
        return conferenceContext;
    }


    public void createTencentConference(String userId, String userName, String userSig, String userNum) {


    }

    public String getMraIp() {
        Set<String> mraIpList = ExternalConfigCache.getInstance().getMRAIpList();
        Iterator<String> iterator = mraIpList.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return "1.13.136.2";
    }
}
