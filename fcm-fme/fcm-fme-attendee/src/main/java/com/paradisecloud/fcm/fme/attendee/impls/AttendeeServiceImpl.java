/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.service.impls
 * @author lilinhai
 * @since 2021-02-05 17:39
 * @version  V1.0
 */
package com.paradisecloud.fcm.fme.attendee.impls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiLiveSettingMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTemplatePollingSchemeMapper;
import com.paradisecloud.fcm.dao.mapper.BusiUserTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiLiveSetting;
import com.paradisecloud.fcm.dao.model.BusiTemplatePollingScheme;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.BusiUserTerminal;
import com.paradisecloud.fcm.fme.attendee.constant.BatchConstant;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.attendee.interfaces.IBusiTemplatePollingSchemeService;
import com.paradisecloud.fcm.fme.attendee.interfaces.ICallLegUpdateCallBackProcessor;
import com.paradisecloud.fcm.fme.attendee.interfaces.ICallegService;
import com.paradisecloud.fcm.fme.attendee.model.attendeemessage.AttendeeMessageQueue;
import com.paradisecloud.fcm.fme.attendee.model.attendeemessage.CallFailedAttendeeMessage;
import com.paradisecloud.fcm.fme.attendee.model.attendeemessage.CallTimeoutAttendeeMessage;
import com.paradisecloud.fcm.fme.attendee.model.attendeemessage.SignalingNotDisconnectedAttendeeMessage;
import com.paradisecloud.fcm.fme.attendee.model.busiprocessor.*;
import com.paradisecloud.fcm.fme.attendee.model.core.AttendeeInfo;
import com.paradisecloud.fcm.fme.attendee.model.core.CallAttendeesInfo;
import com.paradisecloud.fcm.fme.attendee.model.core.ParticipantServiceNewSynchronizer;
import com.paradisecloud.fcm.fme.attendee.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.fme.attendee.model.operation.*;
import com.paradisecloud.fcm.fme.attendee.model.polling.PollingScheme;
import com.paradisecloud.fcm.fme.attendee.utils.AttendeeUtils;
import com.paradisecloud.fcm.fme.attendee.utils.ConferenceContextUtils;
import com.paradisecloud.fcm.fme.cache.AttendeeCallCache;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.SplitScreenCreaterMap;
import com.paradisecloud.fcm.fme.cache.model.enumer.ParticipantBulkOperationMode;
import com.paradisecloud.fcm.fme.cache.utils.FcmThreadPool;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.AttendeeCountingStatistics;
import com.paradisecloud.fcm.fme.model.busi.attendee.InvitedAttendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.TerminalAttendee;
import com.paradisecloud.fcm.fme.model.busi.layout.SplitScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.creater.CustomScreenCreater;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.OneSplitScreen;
import com.paradisecloud.fcm.fme.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.paradisecloud.fcm.fme.model.cms.callleg.*;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;
import com.paradisecloud.fcm.fme.model.parambuilder.CallParamBuilder;
import com.paradisecloud.fcm.fme.model.parambuilder.CustomParamBuilder;
import com.paradisecloud.fcm.fme.model.parambuilder.ParticipantParamBuilder;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.attendee.BaseFixedParamValue;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.paradisecloud.fcm.wvp.gb28181.reponse.WvpPlayStartResponse;
import com.paradisecloud.fcm.wvp.gb28181.service.WvpDeviceService;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ThreadUtils;
import org.apache.http.NameValuePair;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * <pre>界面上参会者业务处理类</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-05 17:39
 */
@Transactional
@Service
public class AttendeeServiceImpl implements IAttendeeService {

    public static final String LAYOUT_TEMPLATE = "layoutTemplate";
    public static final String DEFAULT_LAYOUT = "defaultLayout";
    public static final String CHOSEN_LAYOUT = "chosenLayout";
    public static final String AUTOMATIC = "automatic";
    private final Logger logger = LoggerFactory.getLogger(AttendeeServiceImpl.class);

    @Resource
    private BusiTemplatePollingSchemeMapper busiTemplatePollingSchemeMapper;

    @Resource
    private IBusiTemplatePollingSchemeService busiTemplatePollingSchemeService;

    @Resource
    private ICallegService callegService;

    @Resource
    private IMqttService mqttService;
    @Resource
    private BusiUserTerminalMapper busiUserTerminalMapper;
    @Resource
    private BusiLiveSettingMapper busiLiveSettingMapper;

    @Resource
    private WvpDeviceService wvpDeviceService;


    public static Collection getDiffent(Collection collmax, Collection collmin) {
        Collection csReturn = new LinkedList();
        Collection max = collmax;
        Collection min = collmin;
        if (collmax.size() < collmin.size()) {
            max = collmin;
            min = collmax;
        }
        Map<Object, Integer> map = new HashMap<Object, Integer>(max.size());
        for (Object object : max) {
            map.put(object, 1);
        }
        for (Object object : min) {
            if (map.get(object) == null) {
                csReturn.add(object);
            } else {
                map.put(object, 2);
            }
        }
        for (Map.Entry<Object, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                csReturn.add(entry.getKey());
            }
        }
        return csReturn;
    }

    public static Collection getDiffentNoDuplicate(Collection collmax, Collection collmin) {
        return new HashSet(getDiffent(collmax, collmin));
    }

    public void updateAttendeeImportance(ConferenceContext cc, AttendeeImportance attendeeImportance, Attendee... excludes) {
        List<String> participantIds = new ArrayList<>();
        ConferenceContextUtils.eachNonFmeAttendeeInConference(cc, (a) -> {

            // a.isLayoutFixed()排除布局为固定状态的参会者
            if (a != cc.getMasterAttendee() && a.isMeetingJoined() && !attendeeImportance.is(a.getImportance()) && !isContains(a, excludes)) {
                participantIds.add(a.getParticipantUuid());
            }
        });

        if (cc.getMasterAttendee() != null
                && cc.getMasterAttendee().isMeetingJoined()
                && !attendeeImportance.is(cc.getMasterAttendee().getImportance())
                && !isContains(cc.getMasterAttendee(), excludes)) {
            participantIds.add(cc.getMasterAttendee().getParticipantUuid());
        }

        if (!participantIds.isEmpty()) {
            ParticipantParamBuilder participantParamBuilder = new ParticipantParamBuilder();
            participantParamBuilder.importance(attendeeImportance.getStartValue());
            updateAttendeeAttrs(cc, cc.getDeptId(), cc.getConferenceNumber(), participantParamBuilder.build(), ParticipantBulkOperationMode.SELECTED, participantIds.toArray(new String[participantIds.size()]));
        }
    }

    private boolean isContains(Attendee a, Attendee... excludes) {
        if (!ObjectUtils.isEmpty(excludes)) {
            for (Attendee attendee : excludes) {
                if (a == attendee) {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateAttendeeImportance(ConferenceContext cc, Attendee attendee, AttendeeImportance attendeeImportance) {
        if (attendee != null && attendee.isMeetingJoined()) {
            List<String> participantIds = new ArrayList<>();
            if (!attendeeImportance.is(attendee.getImportance())) {
                participantIds.add(attendee.getParticipantUuid());
            }

            if (!participantIds.isEmpty()) {
                ParticipantParamBuilder participantParamBuilder = new ParticipantParamBuilder();
                participantParamBuilder.importance(attendeeImportance.getStartValue());
                updateAttendeeAttrs(cc, attendee.getDeptId(), attendee.getConferenceNumber(), participantParamBuilder.build(), ParticipantBulkOperationMode.SELECTED, participantIds.toArray(new String[participantIds.size()]));
            }
        }
    }

    @Override
    public void presentationSetting(String conferenceId, String attendeeId, List<? extends BaseFixedParamValue> params) {
        // 辅流分辨率qualityPresentation: unset（空字符串）|unrestricted|max1080p30|max720p5
        // 开启辅流presentationContributionAllowed：unset（空字符串）|true|false
        // 接收双流presentationViewingAllowed: unset（空字符串）|true|false
        // 分享双流sipPresentationChannelEnabled: unset（空字符串）|true|false
        // 辅流模式bfcpMode: unset（空字符串）|serverOnly(客户端模式)|serverAndClient(服务器模式)
        String[] paramNames = {"qualityPresentation", "presentationContributionAllowed", "presentationViewingAllowed", "sipPresentationChannelEnabled", "bfcpMode"};
        processCallLegUpdate(conferenceId, attendeeId, params, paramNames, null);
    }

    @Override
    public void mainSetting(String conferenceId, String attendeeId, List<? extends BaseFixedParamValue> params) {
        // 主流分辨率qualityMain: unset（空字符串）|unrestricted|max1080p30|max720p30|max480p30
        // 显示画面txVideoMute（空字符串）|true|false
        // 关闭镜头 rxVideoMute: unset（空字符串）|true|false
        // 远端镜头控制controlRemoteCameraAllowed: unset（空字符串）|true|false
        String[] paramNames = {"qualityMain", "txAudioMute", "txVideoMute", "rxVideoMute", "controlRemoteCameraAllowed"};
        processCallLegUpdate(conferenceId, attendeeId, params, paramNames, (Map<String, BaseFixedParamValue> fpvMap, FmeBridge fmeBridge, Participant participant) ->
        {
            // 执行关键帧请求
            if (fpvMap.get("generateKeyframe") != null) {
                RestResponse rr0 = fmeBridge.getCallLegInvoker().generateKeyframe(participant.getCallLeg().getId());
                if (!rr0.isSuccess()) {
                    throw new SystemException(1008544, "请求关键帧失败：" + rr0.getMessage());
                }
            }
        });
    }

    public void subtitle(String conferenceId, String attendeeId, List<? extends BaseFixedParamValue> params) {
        // 与会者名称nameLabelOverride: 任意字符串
        // 会议抬头位置 meetingTitlePosition：unset（空字符串）|disabled|top|middle|bottom
        // 显示与会者名称participantLabels: unset（空字符串）|true|false
        String[] paramNames = {"nameLabelOverride", "meetingTitlePosition", "participantLabels"};
        processCallLegUpdate(conferenceId, attendeeId, params, paramNames, null);
    }

    @Override
    public void layoutSetting(String conferenceId, String attendeeId, List<? extends BaseFixedParamValue> params) {
        String[] paramNames = {"chosenLayout", "defaultLayout"};
        processCallLegUpdate(conferenceId, attendeeId, params, paramNames, null);
    }

    @Override
    public void recordStreamSetting(String conferenceId, String attendeeId, List<? extends BaseFixedParamValue> params) {
        // 1. 布局设置要同时传两个参数chosenLayout和defaultLayout，传的值相同
        // 2. 主流分辨率: qualityMain  unset（空字符串）|unrestricted|max1080p30|max720p30|max480p30
        // 3. 是否录制辅流: presentationViewingAllowed unset（空字符串）|true|false
        String[] paramNames = {"chosenLayout", "defaultLayout", "qualityMain", "presentationViewingAllowed"};
        processCallLegUpdate(conferenceId, attendeeId, params, paramNames, null);
    }

    @Override
    public void advanceSetting(String conferenceId, String attendeeId, List<? extends BaseFixedParamValue> params) {
        // 自动音频增益 audioGainMode: unset（空字符串）|disabled|agc
        // SIP媒体加密 sipMediaEncryption: unset（空字符串）|optional可选,required必须,prohibited禁止
        // 需要激活 needsActivation: unset（空字符串）|true|false
        // 激活者模式 deactivationMode: unset（空字符串）|deactivate|disconnect|remainActivated
        // 激活者模式时间 deactivationModeTime: 输入类型  单位:秒 值:number类型
        // 会议锁定 callLockAllowed: unset（空字符串）|true|false
        // 允许挂断会议 endCallAllowed: unset（空字符串）|true|false
        // 允许断开其他与会者的连接 disconnectOthersAllowed: unset（空字符串）|true|false
        // 允许添加其他与会者 addParticipantAllowed: unset（空字符串）|true|false
        // 静音或取消其他参与者的音频 muteOthersAllowed: unset（空字符串）|true|false
        // 禁用其他参与者的视频 videoMuteOthersAllowed: unset（空字符串）|true|false
        // 静音或不静音自己的音频 muteSelfAllowed: unset（空字符串）|true|false
        // 禁用自己的视频 videoMuteSelfAllowed: unset（空字符串）|true|false
        // 允许更改SIP端点上的屏幕布局 changeLayoutAllowed: unset（空字符串）|true|false
        String[] paramNames = {"audioGainMode", "sipMediaEncryption", "needsActivation"
                , "deactivationMode", "deactivationModeTime", "callLockAllowed", "endCallAllowed"
                , "disconnectOthersAllowed", "addParticipantAllowed", "muteOthersAllowed", "videoMuteOthersAllowed"
                , "muteSelfAllowed", "videoMuteSelfAllowed", "changeLayoutAllowed"};


        processCallLegUpdate(conferenceId, attendeeId, params, paramNames, null);
    }

    @Override
    public JSONObject attendeeCallLegSetting(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        AttendeeInfo ai = new AttendeeInfo(contextKey, attendeeId);
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext.getRecordingAttendee() != null && conferenceContext.getRecordingAttendee().getId().equals(attendeeId)) {
            conferenceContext.setRecordingCustomsLayout(true);
        } else if (conferenceContext.getStreamingAttendee() != null && conferenceContext.getStreamingAttendee().getId().equals(attendeeId)) {
            conferenceContext.setStreamingCustomsLayout(true);
        }
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(ai.getAttendee());
        Participant participant = fmeBridge.getDataCache().getParticipantByUuid(ai.getAttendee().getParticipantUuid());
        JSONObject json = new JSONObject();
        json.put("callLegConfiguration", participant.getCallLeg().getConfiguration());
        json.put("fixedSettings", ai.getAttendee().getFixedSettings());
        json.put("layoutTemplatName", "");
        CallLegConfiguration configuration = participant.getCallLeg().getConfiguration();
        if(configuration!=null){
            String layoutTemplate = configuration.getLayoutTemplate();
            if(Strings.isNotBlank(layoutTemplate)){
                CustomScreenCreater customScreenCreater = SplitScreenCreaterMap.LAYOUT_TEMPLATE_ID_MAP.get(layoutTemplate);
                if(customScreenCreater!=null){
                    json.put("layoutTemplatName", customScreenCreater.getLayoutName());
                }

            }
        }
        return json;
    }

    /**
     * 批量修改参会者业务参数，支持集群
     *
     * @param deptId
     * @param conferenceNumber
     * @param nameValuePairs
     * @param participantIds
     * @author lilinhai
     * @since 2021-04-01 17:03
     */
    @Override
    public void updateAttendeeAttrs(ConferenceContext cc, long deptId, String conferenceNumber, List<NameValuePair> nameValuePairs, ParticipantBulkOperationMode participantBulkOperationMode, String... participantIds) {
        if (!ObjectUtils.isEmpty(participantIds)) {
            FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(deptId, new FmeBridgeAddpterProcessor() {
                public void process(FmeBridge fmeBridge) {
                    Call call = fmeBridge.getDataCache().getCallByConferenceNumber(conferenceNumber);
                    if (call == null) {
                        logger.info("批量修改参会属性失败，找不到指定的call");
                        return;
                    }
                    StringBuilder participantIdsStrBuilder = new StringBuilder();
                    Participant p = null;

                    int i = 0;
                    for (String participantId : participantIds) {
                        if (participantId == null) {
                            continue;
                        }
                        if ((p = fmeBridge.getDataCache().getParticipantByUuid(participantId)) != null) {
                            i++;
                            if (participantIdsStrBuilder.length() > 0) {
                                participantIdsStrBuilder.append(",");
                            }
                            participantIdsStrBuilder.append(p.getId());

                            if (i >= BatchConstant.BATCH_PUT_SIZE) {
                                RestResponse rr = fmeBridge.getParticipantInvoker().bulkUpdateParticipant(call.getId(), nameValuePairs, participantBulkOperationMode, participantIdsStrBuilder.toString());
                                if (!rr.isSuccess()) {
                                    StringBuilder messageTip = new StringBuilder();
                                    messageTip.append("在【FME_").append(fmeBridge.getBusiFme().getIp()).append("】上批量修改参会属性失败：" + rr.getMessage());
                                    logger.error(messageTip.toString());
                                    WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(cc, WebsocketMessageType.MESSAGE_ERROR, messageTip);
                                }
                                i = 0;
                                participantIdsStrBuilder = new StringBuilder();
                            }
                        }
                    }

                    if (participantIdsStrBuilder.length() > 0) {
                        RestResponse rr = fmeBridge.getParticipantInvoker().bulkUpdateParticipant(call.getId(), nameValuePairs, participantBulkOperationMode, participantIdsStrBuilder.toString());
                        if (!rr.isSuccess()) {
                            StringBuilder messageTip = new StringBuilder();
                            messageTip.append("在【FME_").append(fmeBridge.getBusiFme().getIp()).append("】上批量修改参会属性失败：" + rr.getMessage());
                            logger.error(messageTip.toString());
                            WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(cc, WebsocketMessageType.MESSAGE_ERROR, messageTip);
                        }
                    }
                }
            });
        }
    }

    /**
     * 批量修改参会者业务参数，支持集群
     *
     * @param cc
     * @param attendees
     * @param nameValuePairs void
     * @author lilinhai
     * @since 2021-04-19 11:45
     */
    public void updateAttendeeAttrs(ConferenceContext cc, List<Attendee> attendees, ParticipantBulkOperationMode participantBulkOperationMode, List<NameValuePair> nameValuePairs) {
        if (!ObjectUtils.isEmpty(attendees)) {
            Map<String, CallAttendeesInfo> attendeesMap = new HashMap<>();
            for (Attendee attendee : attendees) {
                if (attendee.isMeetingJoined()) {
                    CallAttendeesInfo callAttendeesInfo = attendeesMap.get(attendee.getCallId());
                    if (callAttendeesInfo == null) {
                        callAttendeesInfo = new CallAttendeesInfo();
                        callAttendeesInfo.setFmeBridge(FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(attendee));
                        callAttendeesInfo.setCallId(attendee.getCallId());
                        attendeesMap.put(attendee.getCallId(), callAttendeesInfo);
                    }
                    callAttendeesInfo.addAttendee(attendee);
                }
            }

            attendeesMap.forEach((callId, callAttendeesInfo) -> {
                String nextBatchIds = null;
                while ((nextBatchIds = callAttendeesInfo.getNextBatchIds()) != null) {
                    String _nextBatchIds = nextBatchIds;
                    FcmThreadPool.exec(() -> {
                        RestResponse rr = callAttendeesInfo.getFmeBridge().getParticipantInvoker().bulkUpdateParticipant(callAttendeesInfo.getCallId(), nameValuePairs, participantBulkOperationMode, _nextBatchIds);
                        if (!rr.isSuccess()) {
                            StringBuilder messageTip = new StringBuilder();
                            messageTip.append("在【FME_").append(callAttendeesInfo.getFmeBridge().getBusiFme().getIp()).append("】上批量修改参会属性失败：" + rr.getMessage());
                            logger.error(messageTip.toString());
                            WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(cc, WebsocketMessageType.MESSAGE_ERROR, messageTip);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void recall(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
        Attendee attendee = conferenceContext.getAttendeeById(attendeeId);
        if (attendee.getCallRequestSentTime() != null && (System.currentTimeMillis() - attendee.getCallRequestSentTime()) < 5000) {
            throw new CustomException("【" + attendee.getName() + "】重呼请求已发起，请耐心等待响应结果，期间不要频繁发起！");
        }
        if (Objects.equals(attendee.getAttendeeType(), "TerminalAttendee")) {
            TerminalAttendee terminalAttendee = (TerminalAttendee) attendee;
            BusiTerminal terminal = TerminalCache.getInstance().get(terminalAttendee.getTerminalId());
            if (terminal != null && TerminalType.isRtsp(terminal.getType())) {
                new RtspCallAttendeeProcessor(contextKey, attendeeId, terminal.getProtocol()).process();
                return;
            }
            if (terminal != null && TerminalType.isGB28181(terminal.getType())) {
                if(Strings.isBlank(terminal.getProtocol())){
                    WvpPlayStartResponse play = wvpDeviceService.play(terminal.getNumber());
                    String rtsp = Optional.ofNullable(play).map(WvpPlayStartResponse::getData).map(WvpPlayStartResponse.DataDTO::getRtsp).get();
                    new RtspCallAttendeeProcessor(attendee.getContextKey(), attendee.getId(), rtsp).process();
                }else {
                    new RtspCallAttendeeProcessor(attendee.getContextKey(), attendee.getId(), terminal.getProtocol()).process();
                }
                return;
            }
            if (terminal != null && TerminalType.isFCMSIP(terminal.getType()) && com.paradisecloud.common.utils.StringUtils.isNotEmpty(terminal.getSn())) {
                BeanFactory.getBean(IMqttService.class).inviteAttendeeJoinConference(terminalAttendee, conferenceContext, AttendType.AUTO_JOIN.getValue());
                return;
            }
        }
        if (attendee.isMcuAttendee()) {
            BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(attendee.getCascadeConferenceId()));
            if (baseConferenceContext != null) {
                baseConferenceContext.setUpCascadeRemoteParty(conferenceContext.getConferenceRemoteParty());
            }
        }

        new RecallAttendeeProcessor(contextKey, attendeeId).process();
    }

    public void callAttendee(Attendee attendee) {

        try {
            ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(attendee.getContextKey());
            if (Objects.equals(attendee.getAttendeeType(), "TerminalAttendee")) {
                TerminalAttendee terminalAttendee = (TerminalAttendee) attendee;
                BusiTerminal terminal = TerminalCache.getInstance().get(terminalAttendee.getTerminalId());
                if (terminal != null && TerminalType.isRtsp(terminal.getType())) {

                    new RtspCallAttendeeProcessor(attendee.getContextKey(), attendee.getId(), terminal.getProtocol()).process();
                    return;
                }
                if (terminal != null && TerminalType.isGB28181(terminal.getType())) {
                    if(Strings.isBlank(terminal.getProtocol())){
                        WvpPlayStartResponse play = wvpDeviceService.play(terminal.getNumber());
                        String rtsp = Optional.ofNullable(play).map(WvpPlayStartResponse::getData).map(WvpPlayStartResponse.DataDTO::getRtsp).get();
                        new RtspCallAttendeeProcessor(attendee.getContextKey(), attendee.getId(), rtsp).process();
                    }else {
                        new RtspCallAttendeeProcessor(attendee.getContextKey(), attendee.getId(), terminal.getProtocol()).process();
                    }
                    return;
                }
                if (terminal != null && TerminalType.isFCMSIP(terminal.getType()) && com.paradisecloud.common.utils.StringUtils.isNotEmpty(terminal.getSn())) {
                    BeanFactory.getBean(IMqttService.class).inviteAttendeeJoinConference(terminalAttendee, conferenceContext, AttendType.AUTO_JOIN.getValue());
                    return;
                }
            }

        } catch (Exception e) {

        }

        new CallAttendeeProcessor(attendee).process();
    }

    @Override
    public void callRtsp(ConferenceContext conferenceContext, Attendee attendee, String rtspUri) {
        new RtspCallAttendeeProcessor(conferenceContext.getContextKey(), attendee.getId(), rtspUri).process();
    }

    @Override
    public void hangUp(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        new HangUpAttendeeProcessor(contextKey, attendeeId).process();
    }

    @Override
    public void remove(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        hangUp(conferenceId, attendeeId);
        FcmThreadPool.exec(() -> {
            ThreadUtils.sleep(500);
            ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
            if (conferenceContext != null) {
                AttendeeInfo ai = new AttendeeInfo(contextKey, attendeeId);
                if (ai != null && ai.getAttendee() != null) {
                    conferenceContext.removeAttendeeById(ai.getAttendee().getId());
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + ai.getAttendee().getName() + "】被移除");

                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("id", ai.getAttendee().getId());
                    updateMap.put("deptId", ai.getAttendee().getDeptId());
                    updateMap.put("mcuAttendee", ai.getAttendee().isMcuAttendee());
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                } else {
                    throw new CustomException("该终端已被移除，请刷新页面后重试");
                }
            }
        });
    }

    @Override
    public String takeSnapshot(String conferenceId, String attendeeId, JSONObject params) {
        String direction = "rx";
        if (params.containsKey("direction")) {
            direction = params.getString("direction");
            Assert.isTrue(direction.equals("rx") || direction.equals("tx"), "direction 只能为rx或tx");
        }

        String maxWidth = "640";
        if (params.containsKey("maxWidth")) {
            maxWidth = params.getString("maxWidth");
            Assert.isTrue(maxWidth.matches("^\\d+$"), "maxWidth只能为正整数！");
        }

        try {
            String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
            AttendeeInfo ai = new AttendeeInfo(contextKey, attendeeId);
            FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(ai.getAttendee());
            Participant participant = fmeBridge.getDataCache().getParticipantByUuid(ai.getAttendee().getParticipantUuid());
            CallLeg callLeg = participant.getCallLeg();
            if (callLeg != null) {
                String snapshot = fmeBridge.getCallLegInvoker().takeSnapshot(callLeg.getId(), direction, Integer.parseInt(maxWidth));
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(ai.getConferenceContext(), WebsocketMessageType.MESSAGE_TIP, "【" + ai.getAttendee().getName() + "】获取快照成功！");
                return snapshot;
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * <pre>相机控制</pre>
     *
     * @param conferenceId
     * @param attendeeId
     * @param params
     * @author sinhy
     * @see com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService#cameraControl(java.lang.String, java.lang.String, com.alibaba.fastjson.JSONObject)
     * @since 2021-08-18 13:55
     */
    @Override
    public void cameraControl(String conferenceId, String attendeeId, JSONObject params) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        AttendeeInfo ai = new AttendeeInfo(contextKey, attendeeId);
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(ai.getAttendee());
        Participant participant = fmeBridge.getDataCache().getParticipantByUuid(ai.getAttendee().getParticipantUuid());
        CallLeg callLeg = participant.getCallLeg();
        if (callLeg != null) {
            fmeBridge.getCallLegInvoker().cameraControl(callLeg.getId(), params.getString("pan"), params.getString("tilt"), params.getString("zoom"), params.getString("focus"));
            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(ai.getConferenceContext(), WebsocketMessageType.MESSAGE_TIP, "【" + ai.getAttendee().getName() + "】镜头调整成功！");
        }
    }

    /**
     * 变更主会场
     *
     * @author lilinhai
     * @since 2021-02-09 11:32  void
     */
    public void changeMaster(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
        Attendee terminalAttendee = conferenceContext.getAttendeeById(attendeeId);
        if (conferenceContext.isDownCascadeConference()) {
            BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceContext.getUpCascadeConferenceId()));
            if (baseConferenceContext != null) {
                if (!terminalAttendee.getRemoteParty().equals(conferenceContext.getUpCascadeRemoteParty())) {
                    throw new SystemException(1005454, "该会议正被级联，不允许修改主会场！");
                }
            }
        }
        if (terminalAttendee.isLiveBroadcast() || terminalAttendee.isRecorder()) {
            Assert.isTrue(false, "设置主会场失败，请重新选择其他终端！");
        }
        new MasterChangeAttendeeProcessor(contextKey, attendeeId).process();
    }

    /**
     * 选看
     *
     * @author lilinhai
     * @since 2021-02-09 11:32  void
     */
    public void chooseSee(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue()) {
            throw new SystemException(1005454, "主会场未设置，无法进行选看操作！");
        }
        AttendeeOperation attendeeOperationNow = mainConferenceContext.getAttendeeOperation();
        if (attendeeOperationNow instanceof ChooseToSeeAttendeeOperation) {
            ChooseToSeeAttendeeOperation chooseToSeeAttendeeOperation = (ChooseToSeeAttendeeOperation) attendeeOperationNow;
            List<Attendee> attendees = chooseToSeeAttendeeOperation.getAttendees();
            if (attendees != null && attendees.size() == 1) {
                Attendee attendee = attendees.get(0);
                if (attendeeId.equals(attendee.getId())) {
                    return;
                }
            }
        }
        new Thread(() -> {
            ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
            conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
            AttendeeInfo ai = new AttendeeInfo(contextKey, attendeeId);
            SplitScreen ss = new OneSplitScreen(AttendeeImportance.CHOOSE_SEE.getEndValue());

            // 执行当前操作
            AttendeeOperation attendeeOperation = new ChooseToSeeAttendeeOperation(conferenceContext, ss, Arrays.asList(ai.getAttendee()));
            conferenceContext.setAttendeeOperation(attendeeOperation);
            conferenceContext.getLastAttendeeOperation().cancel(attendeeOperation);
        }).start();
    }

    /**
     * 选看
     *
     * @author lilinhai
     * @since 2021-02-09 11:32  void
     */
    public void chooseSee(String conferenceId, String attendeeId, boolean upCascadeOperate, boolean upCascadeBroadcast, boolean upCascadePolling, boolean upCascadeRollCall) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue()) {
            throw new SystemException(1005454, "主会场未设置，无法进行选看操作！");
        }
        new Thread(() -> {
            ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
            conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
            AttendeeInfo ai = new AttendeeInfo(contextKey, attendeeId);
            SplitScreen ss = new OneSplitScreen(AttendeeImportance.CHOOSE_SEE.getEndValue());

            // 执行当前操作
            AttendeeOperation attendeeOperation = new ChooseToSeeAttendeeOperation(conferenceContext, ss, Arrays.asList(ai.getAttendee()));
            attendeeOperation.setUpCascadeOperate(upCascadeOperate);
            attendeeOperation.setUpCascadeBroadcast(upCascadeBroadcast);
            attendeeOperation.setUpCascadePolling(upCascadePolling);
            attendeeOperation.setUpCascadeRollCall(upCascadeRollCall);
            conferenceContext.setAttendeeOperation(attendeeOperation);
            conferenceContext.getLastAttendeeOperation().cancel(attendeeOperation);
        }).start();
    }

    /**
     * 默认选看（主会场切换时执行）
     *
     * @author lilinhai
     * @since 2021-02-09 11:32  void
     */
    public void defaultChooseSee(ConferenceContext mainConferenceContext) {
        if (mainConferenceContext.getMasterAttendee() == null || !mainConferenceContext.getMasterAttendee().isMeetingJoined()) {
            return;
        }

        // 只有当操作是默认的选看时，才会执行该逻辑
        Attendee a = ConferenceContextUtils.getDefaultChooseToSee(mainConferenceContext);
        if (a != null) {
            logger.info("已找到默认选看参会, ConferenceNumber：" + mainConferenceContext.getConferenceNumber() + ", Attendee: " + a.getName());
            mainConferenceContext.setLastAttendeeOperation(mainConferenceContext.getAttendeeOperation());

            AttendeeInfo ai = new AttendeeInfo(mainConferenceContext.getContextKey(), a.getId());
            SplitScreen ss = new OneSplitScreen(AttendeeImportance.CHOOSE_SEE.getStartValue());

            // 执行选看逻辑
            AttendeeOperation ao = new DefaultChooseToSeeAttendeeOperation(mainConferenceContext, ss, Arrays.asList(ai.getAttendee()));
            mainConferenceContext.setAttendeeOperation(ao);
            mainConferenceContext.getLastAttendeeOperation().cancel(ao);
        } else {
            logger.info("未找到默认选看参会, ConferenceNumber：" + mainConferenceContext.getConferenceNumber());
        }
    }

    /**
     * <pre>点名</pre>
     *
     * @param conferenceId
     * @param attendeeId
     * @author lilinhai
     * @see com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService#callTheRoll(java.lang.String, java.lang.String)
     * @since 2021-02-22 18:07
     */
    @Override
    public void callTheRoll(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue()) {
            throw new SystemException(1005454, "主会场未设置，无法进行点名操作！");
        }
        new Thread(() -> {
            ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
            conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
            AttendeeInfo ai = new AttendeeInfo(contextKey, attendeeId);
            SplitScreen ss = new OneSplitScreen(AttendeeImportance.POINT.getStartValue());
            AttendeeOperation attendeeOperation = new CallTheRollAttendeeOperation(conferenceContext, ss, Arrays.asList(ai.getAttendee()));
            conferenceContext.setAttendeeOperation(attendeeOperation);
            conferenceContext.getLastAttendeeOperation().cancel(attendeeOperation);
        }).start();
    }

    /**
     * <pre>取消点名</pre>
     *
     * @param conferenceId
     * @author lilinhai
     * @since 2021-02-23 16:14
     */
    @Override
    public void cancelCallTheRoll(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        cancelCurrentOperation(ConferenceContextCache.getInstance().get(contextKey));
    }

    /**
     * 对话
     *
     * @param conferenceId
     * @param attendeeId
     * @author sinhy
     * @see com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService#talk(java.lang.String, java.lang.String)
     * @since 2021-12-02 12:47
     */
    @Override
    public void talk(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue()) {
            throw new SystemException(1005452, "主会场未设置，无法进行对话操作！");
        }
        new Thread(() -> {
            ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
            conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
            AttendeeInfo ai = new AttendeeInfo(contextKey, attendeeId);
            SplitScreen ss = new OneSplitScreen(AttendeeImportance.TALK.getStartValue());
            AttendeeOperation attendeeOperation = new TalkAttendeeOperation(conferenceContext, ss, Arrays.asList(ai.getAttendee()));
            conferenceContext.setAttendeeOperation(attendeeOperation);
            conferenceContext.getLastAttendeeOperation().cancel(attendeeOperation);
        }).start();
    }

    /**
     * <pre>取消对话</pre>
     *
     * @param conferenceId
     * @author lilinhai
     * @since 2021-02-23 16:14
     */
    @Override
    public void cancelTalk(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        cancelCurrentOperation(ConferenceContextCache.getInstance().get(contextKey));
    }

    /**
     * <pre>取消当前操作</pre>
     *
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-02-23 16:14
     */
    public void cancelCurrentOperation(ConferenceContext conferenceContext) {
        try {
            if (conferenceContext.getAttendeeOperation() != null) {
                conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
                AttendeeOperation attendeeOperation = conferenceContext.getDefaultViewOperation();
                conferenceContext.setAttendeeOperation(conferenceContext.getDefaultViewOperation());
                conferenceContext.getLastAttendeeOperation().cancel(attendeeOperation);
            }
        } catch (Throwable e) {
            logger.error("cancelCallTheRoll error", e);
        }
    }

    /**
     * <pre>混音</pre>
     *
     * @param conferenceId
     * @param attendeeId
     * @author lilinhai
     * @see com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService#callTheRoll(java.lang.String, java.lang.String)
     * @since 2021-02-22 18:07
     */
    public void openMixing(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        logger.info("开启单个参会者混音入口：" + contextKey + ", " + attendeeId);
        new MixingAttendeeProcessor(contextKey, attendeeId, false).process();
    }

    /**
     * 接受举手
     *
     * @param conferenceId
     * @param attendeeId
     * @author sinhy
     * @since 2021-12-07 10:27
     */
    public void acceptRaiseHand(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);

        // 开启对话
        talk(conferenceId, attendeeId);

        // 关闭举手
        RaiseHandProcessor raiseHandProcessor = new RaiseHandProcessor(contextKey, attendeeId, RaiseHandStatus.NO);
        raiseHandProcessor.process();

        if (raiseHandProcessor.getTargetAttendee() instanceof TerminalAttendee) {
            TerminalAttendee ta = (TerminalAttendee) raiseHandProcessor.getTargetAttendee();
            if (TerminalCache.getInstance().get(ta.getTerminalId()).getSn() != null) {
                try {
                    mqttService.acceptRaiseHand(ConferenceContextCache.getInstance().get(contextKey).getConferenceNumber(), ta);
                } catch (Throwable e) {
                    logger.error("acceptRaiseHand - mqtt error", e);
                }
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("messageDuration", 10);
        jsonObject.put("messageText", "主持人同意了【" + raiseHandProcessor.getTargetAttendee().getName() + "】的发言请求！");
        jsonObject.put("messagePosition", "top");
        sendMessage(conferenceId, jsonObject);
    }

    /**
     * 拒绝举手
     *
     * @param conferenceId
     * @param attendeeId
     * @author sinhy
     * @since 2021-12-07 10:27
     */
    public void rejectRaiseHand(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);

        // 关闭举手
        RaiseHandProcessor raiseHandProcessor = new RaiseHandProcessor(contextKey, attendeeId, RaiseHandStatus.NO);
        raiseHandProcessor.process();

        if (raiseHandProcessor.getTargetAttendee() instanceof TerminalAttendee) {
            TerminalAttendee ta = (TerminalAttendee) raiseHandProcessor.getTargetAttendee();
            if (TerminalCache.getInstance().get(ta.getTerminalId()).getSn() != null) {
                try {
                    mqttService.rejectRaiseHand(ConferenceContextCache.getInstance().get(contextKey).getConferenceNumber(), ta);
                } catch (Throwable e) {
                    logger.error("rejectRaiseHand - mqtt error", e);
                }
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("messageDuration", 10);
        jsonObject.put("messageText", "主持人拒绝了【" + raiseHandProcessor.getTargetAttendee().getName() + "】的发言请求！");
        jsonObject.put("messagePosition", "top");
        sendMessage(conferenceId, jsonObject);
    }

    /**
     * 举手
     *
     * @param conferenceId
     * @param attendeeId
     * @param raiseHandStatus void
     * @author sinhy
     * @since 2021-12-07 10:27
     */
    public void raiseHand(String conferenceId, String attendeeId, RaiseHandStatus raiseHandStatus) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        RaiseHandProcessor raiseHandProcessor = new RaiseHandProcessor(contextKey, attendeeId, raiseHandStatus);
        raiseHandProcessor.process();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("messageDuration", 60);
        jsonObject.put("messageText", "【" + raiseHandProcessor.getTargetAttendee().getName() + "】正在发言，请求主持人同意！");
        jsonObject.put("messagePosition", "top");
        sendMessage(conferenceId, jsonObject);
    }

    @Override
    public void setBanner(String conferenceId, String attendeeId, JSONObject params) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        AttendeeInfo attendeeInfo = new AttendeeInfo(contextKey, attendeeId);
        if (attendeeInfo.getAttendee() instanceof TerminalAttendee) {
            TerminalAttendee ta = (TerminalAttendee) attendeeInfo.getAttendee();
            if (TerminalCache.getInstance().get(ta.getTerminalId()).getSn() != null) {
                try {
                    mqttService.setBanner(ConferenceContextCache.getInstance().get(contextKey).getConferenceNumber(), ta, params);
                } catch (Throwable e) {
                    logger.error("setBanner - mqtt error", e);
                }
            }
        }
    }

    @Override
    public void sendBanner(String conferenceId, JSONObject jsonObject) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
        ConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            if (a instanceof TerminalAttendee) {
                TerminalAttendee ta = (TerminalAttendee) a;
                if (TerminalCache.getInstance().get(ta.getTerminalId()).getSn() != null) {
                    FcmThreadPool.exec(() -> {
                        try {
                            mqttService.setBanner(conferenceContext.getConferenceNumber(), ta, jsonObject);
                        } catch (Throwable e) {
                            logger.error("setBanner - mqtt error", e);
                        }
                    });
                }
            }
        });
    }

    /**
     * <pre>关闭混音</pre>
     *
     * @param conferenceId
     * @param attendeeId
     * @author lilinhai
     * @see com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService#callTheRoll(java.lang.String, java.lang.String)
     * @since 2021-02-22 18:07
     */
    public void closeMixing(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        logger.info("关闭单个参会者混音入口：" + contextKey + ", " + attendeeId);
        new MixingAttendeeProcessor(contextKey, attendeeId, true).process();
    }

    @Override
    public void invite(String conferenceId, List<Long> terminalIds) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
        for (Long terminalId : terminalIds) {
            TerminalAttendee ta = conferenceContext.getTerminalAttendeeMap().get(terminalId);
            if (ta == null) {
                ta = AttendeeUtils.packTerminalAttendee(terminalId);
                String remoteParty = ta.getRemoteParty();
                //新直播方式地址不能被邀请
                BusiLiveSetting busiLiveSettingCon = new BusiLiveSetting();
                busiLiveSettingCon.setRemoteParty(remoteParty);
                List<BusiLiveSetting> busiLiveSettingList = busiLiveSettingMapper.selectBusiLiveSettingList(busiLiveSettingCon);
                if (busiLiveSettingList != null && busiLiveSettingList.size() > 0) {
                    if (terminalIds.size() == 1) {
                        throw new SystemException(1, "该地址（" + remoteParty + "）为保留地址，不能被邀请！");
                    } else {
                        continue;
                    }
                }
                ta.setConferenceNumber(conferenceContext.getConferenceNumber());
                ta.setDeptId(conferenceContext.getDeptId());
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(terminalId);
                if (busiUserTerminal != null) {
                    ta.setUserId(busiUserTerminal.getUserId());
                }
                conferenceContext.addAttendee(ta);
                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, ta);
                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + ta.getName() + "】被邀请加入");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
            }
            recall(conferenceId, ta.getId());
        }

        mqttService.sendConferenceInfoToPushTargetTerminal(conferenceContext);
    }

    @Override
    public void invite(String conferenceId, JSONObject jsonObj) {
        Assert.isTrue(jsonObj.containsKey("name"), "名字是必填参数！");
        Assert.isTrue(jsonObj.containsKey("uri"), "URI是必填参数！");
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
        String remoteParty = jsonObj.getString("uri");
        String dtmfInfo = jsonObj.getString("dtmfInfo");
        //新直播方式地址不能被邀请
        BusiLiveSetting busiLiveSettingCon = new BusiLiveSetting();
        busiLiveSettingCon.setRemoteParty(remoteParty);
        List<BusiLiveSetting> busiLiveSettingList = busiLiveSettingMapper.selectBusiLiveSettingList(busiLiveSettingCon);
        if (busiLiveSettingList != null && busiLiveSettingList.size() > 0) {
            throw new SystemException(1, "该地址（" + remoteParty + "）为保留地址，不能被邀请！");
        }
        Map<String, Attendee> uuidAttendeeMap = conferenceContext.getUuidAttendeeMapByUri(remoteParty);
        if (uuidAttendeeMap == null) {
            if (remoteParty.contains("@")) {
                try {
                    String[] remotePartyArr = remoteParty.split("@");
                    String credential = remotePartyArr[0];
                    String ip = remotePartyArr[1];
                    if (StringUtils.hasText(ip)) {
                        FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getByDomainName(ip);
                        if (fsbcBridge != null) {
                            String remotePartyNew = credential + "@" + fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                            uuidAttendeeMap = conferenceContext.getUuidAttendeeMapByUri(remotePartyNew);
                        }
                        if (uuidAttendeeMap == null) {
                            FcmBridge fcmBridge = FcmBridgeCache.getInstance().getByDomainName(ip);
                            if (fcmBridge != null) {
                                String remotePartyNew = credential + "@" + fcmBridge.getBusiFreeSwitch().getIp();
                                uuidAttendeeMap = conferenceContext.getUuidAttendeeMapByUri(remotePartyNew);
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
        if (uuidAttendeeMap != null) {
            return;
        }

        BusiTerminal busiTerminal = TerminalCache.getInstance().getByRemoteParty(conferenceContext.getDeptId(), remoteParty);
        String remotePartyNew = "";
        if (busiTerminal == null) {
            if (remoteParty.contains("@")) {
                try {
                    String[] remotePartyArr = remoteParty.split("@");
                    String credential = remotePartyArr[0];
                    String ip = remotePartyArr[1];
                    if (StringUtils.hasText(ip)) {
                        FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getByDomainName(ip);
                        if (fsbcBridge != null) {
                            String remotePartyIp = credential + "@" + fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                            busiTerminal = TerminalCache.getInstance().getByRemoteParty(conferenceContext.getDeptId(), remotePartyIp);
                        }
                        if (busiTerminal == null) {
                            FcmBridge fcmBridge = FcmBridgeCache.getInstance().getByDomainName(ip);
                            if (fcmBridge != null) {
                                String remotePartyIp = credential + "@" + fcmBridge.getBusiFreeSwitch().getIp();
                                busiTerminal = TerminalCache.getInstance().getByRemoteParty(conferenceContext.getDeptId(), remotePartyIp);
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        } else {
            if (remoteParty.contains("@")) {
                String[] remotePartyArr = remoteParty.split("@");
                String credential = remotePartyArr[0];
                String ip = remotePartyArr[1];
                if (StringUtils.hasText(ip)) {
                    FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getByIp(ip);
                    if (fsbcBridge != null) {
                        String domainName = fsbcBridge.getBusiFsbcRegistrationServer().getDomainName();
                        if (!ObjectUtils.isEmpty(domainName)) {
                            remotePartyNew = credential + "@" + domainName;
                        }
                    } else {
                        FcmBridge fcmBridge = FcmBridgeCache.getInstance().getByIp(ip);
                        if (fcmBridge != null) {
                            String domainName = fcmBridge.getBusiFreeSwitch().getDomainName();
                            if (!ObjectUtils.isEmpty(domainName)) {
                                remotePartyNew = credential + "@" + domainName;
                            }
                        }
                    }
                }
            }
        }
        if (busiTerminal != null) {
            invite(conferenceId, Arrays.asList(busiTerminal.getId()));
            return;
        }

        InvitedAttendee ia = new InvitedAttendee();
        ia.setConferenceNumber(conferenceContext.getConferenceNumber());
        ia.setId(UUID.randomUUID().toString());
        ia.setName(jsonObj.getString("name"));
        ia.setRemoteParty(remoteParty);
        ia.setContextKey(contextKey);
        ia.setWeight(1);
        ia.setDtmfStr(dtmfInfo);
        ia.setDeptId(conferenceContext.getDeptId());
        if (remoteParty.startsWith("rtsp://")) {
            ia.setIp("127.0.0.1");
        } else {
            if (ia.getRemoteParty().contains("@")) {
                ia.setIp(ia.getRemoteParty().split("@")[1]);
            } else {
                ia.setIp(ia.getRemoteParty());
            }
            if (!ObjectUtils.isEmpty(remotePartyNew)) {
                ia.setRemotePartyNew(remotePartyNew);
                if (remotePartyNew.contains("@")) {
                    ia.setIpNew(remotePartyNew.split("@")[1]);
                } else {
                    ia.setIpNew(remotePartyNew);
                }
            }
        }


        if (remoteParty.startsWith("rtsp://")) {
            new RtspCallAttendeeProcessor(ia, remoteParty).process();
            conferenceContext.getRemotePartyAttendeesMap().removeAttendeeByRemotePartyAndUuid(remoteParty, ia.getId());
        } else {
            conferenceContext.addAttendee(ia);
            WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, ia);
            WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + ia.getName() + "】被邀请加入");
            recall(conferenceId, ia.getId());
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);

        mqttService.sendConferenceInfoToPushTargetTerminal(conferenceContext);
    }

    @Override
    public void openCamera(String conferenceId, String attendeeId) {
        List<Attendee> as = new ArrayList<>();
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        AttendeeInfo ai = new AttendeeInfo(contextKey, attendeeId);
        as.add(ai.getAttendee());
        updateAttendeeAttrs(contextKey, as, new ParticipantParamBuilder()
                .rxVideoMute(false)
                .build());
    }

    @Override
    public void closeCamera(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        List<Attendee> as = new ArrayList<>();
        AttendeeInfo ai = new AttendeeInfo(contextKey, attendeeId);
        as.add(ai.getAttendee());
        updateAttendeeAttrs(contextKey, as, new ParticipantParamBuilder()
                .rxVideoMute(true)
                .build());
    }

    @Override
    public void openMixing(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        openMixing(ConferenceContextCache.getInstance().get(contextKey));
    }

    public void openMixing(ConferenceContext cc) {
        List<Attendee> as = new ArrayList<>();
        ConferenceContextUtils.eachNonFmeAttendeeInConference(cc, (a) -> {
            if (a.isMeetingJoined()) {
                as.add(a);
            }
        });

        updateAttendeeAttrs(cc, as, ParticipantBulkOperationMode.SELECTED, new ParticipantParamBuilder()
                .rxAudioMute(false)
                .build());
    }

    @Override
    public void openDisplayDevice(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        List<Attendee> as = new ArrayList<>();
        ConferenceContextUtils.eachNonFmeAttendeeInConference(ConferenceContextCache.getInstance().get(contextKey), (a) -> {
            if (a.isMeetingJoined()) {
                as.add(a);
            }
        });

        updateAttendeeAttrs(contextKey, as, new ParticipantParamBuilder()
                .txVideoMute(false)
                .build());
    }

    @Override
    public void closeDisplayDevice(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        List<Attendee> as = new ArrayList<>();
        ConferenceContextUtils.eachNonFmeAttendeeInConference(ConferenceContextCache.getInstance().get(contextKey), (a) -> {
            if (a.isMeetingJoined()) {
                as.add(a);
            }
        });

        updateAttendeeAttrs(contextKey, as, new ParticipantParamBuilder()
                .txVideoMute(true)
                .build());
    }

    @Override
    public void openDisplayDevice(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        List<Attendee> as = new ArrayList<>();
        AttendeeInfo ai = new AttendeeInfo(contextKey, attendeeId);
        as.add(ai.getAttendee());
        updateAttendeeAttrs(contextKey, as, new ParticipantParamBuilder()
                .txVideoMute(false)
                .build());
    }

    @Override
    public void closeDisplayDevice(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        List<Attendee> as = new ArrayList<>();
        AttendeeInfo ai = new AttendeeInfo(contextKey, attendeeId);
        as.add(ai.getAttendee());
        updateAttendeeAttrs(contextKey, as, new ParticipantParamBuilder()
                .txVideoMute(true)
                .build());
    }

    @Override
    public void closeCamera(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        List<Attendee> as = new ArrayList<>();
        ConferenceContextUtils.eachNonFmeAttendeeInConference(ConferenceContextCache.getInstance().get(contextKey), (a) -> {
            if (a.isMeetingJoined()) {
                as.add(a);
            }
        });

        updateAttendeeAttrs(contextKey, as, new ParticipantParamBuilder()
                .rxVideoMute(true)
                .build());
    }

    @Override
    public void openCamera(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        List<Attendee> as = new ArrayList<>();
        ConferenceContextUtils.eachNonFmeAttendeeInConference(ConferenceContextCache.getInstance().get(contextKey), (a) -> {
            if (a.isMeetingJoined()) {
                as.add(a);
            }
        });

        updateAttendeeAttrs(contextKey, as, new ParticipantParamBuilder()
                .rxVideoMute(false)
                .build());
    }

    @Override
    public void closeMixing(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext cc = ConferenceContextCache.getInstance().get(contextKey);
        closeMixing(cc, cc.getMasterAttendee());
    }

    public void closeMixing(ConferenceContext cc, Attendee... excludes) {
        List<Attendee> as = new ArrayList<>();
        ConferenceContextUtils.eachNonFmeAttendeeInConference(cc, (a) -> {
            for (Attendee attendee : excludes) {
                if (a == attendee) {
                    return;
                }
            }
            if (a.isMeetingJoined()) {
                as.add(a);
            }
        });

        updateAttendeeAttrs(cc, as, ParticipantBulkOperationMode.SELECTED, new ParticipantParamBuilder()
                .rxAudioMute(true)
                .build());
    }

    @Override
    public void sendSystemMessage(String conferenceId, String message, int duration) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("messageText", message);
        jsonObject.put("messageDuration", "permanent");
        jsonObject.put("messagePosition", "top");
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext cc = ConferenceContextCache.getInstance().get(contextKey);
        FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(cc.getDeptId(), new FmeBridgeAddpterProcessor() {
            public void process(FmeBridge fmeBridge) {
                Call call = fmeBridge.getDataCache().getCallByConferenceNumber(cc.getConferenceNumber());
                if (call == null) {
                    logger.info("批量修改参会属性失败，找不到指定的call");
                    return;
                }

                fmeBridge.getCallInvoker().updateCall(call.getId(), new CallParamBuilder().messageDuration(jsonObject.getString("messageDuration"))
                        .messagePosition(jsonObject.getString("messagePosition")).messageText(jsonObject.getString("messageText")).build());
            }
        });
        cc.setSystemMessageEndTime(System.currentTimeMillis() + (duration * 1000));
    }

    @Override
    public void sendMessage(String conferenceId, JSONObject jsonObject) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext cc = ConferenceContextCache.getInstance().get(contextKey);
        FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(cc.getDeptId(), new FmeBridgeAddpterProcessor() {
            public void process(FmeBridge fmeBridge) {
                Call call = fmeBridge.getDataCache().getCallByConferenceNumber(cc.getConferenceNumber());
                if (call == null) {
                    logger.info("批量修改参会属性失败，找不到指定的call");
                    return;
                }

                fmeBridge.getCallInvoker().updateCall(call.getId(), new CallParamBuilder().messageDuration(jsonObject.getString("messageDuration"))
                        .messagePosition(jsonObject.getString("messagePosition")).messageText(jsonObject.getString("messageText")).build());
            }
        });
        cc.setSystemMessageEndTime(0);
    }

    public void setMessageBannerText(String conferenceId, JSONObject jsonObject) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext cc = ConferenceContextCache.getInstance().get(contextKey);
        FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(cc.getDeptId(), new FmeBridgeAddpterProcessor() {
            public void process(FmeBridge fmeBridge) {
                Call call = fmeBridge.getDataCache().getCallByConferenceNumber(cc.getConferenceNumber());
                if (call == null) {
                    logger.info("批量修改参会属性失败，找不到指定的call");
                    return;
                }

                fmeBridge.getCallInvoker().updateCall(call.getId(), new CallParamBuilder().messageBannerText(jsonObject.getString("messageBannerText")).build());
            }
        });
    }

    @Override
    public void polling(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
        ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(conferenceContext);

        BusiTemplatePollingScheme con0 = new BusiTemplatePollingScheme();
        con0.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        con0.setEnableStatus(YesOrNo.YES.getValue());
        List<BusiTemplatePollingScheme> pss = busiTemplatePollingSchemeMapper.selectBusiTemplatePollingSchemeList(con0);
        BusiTemplatePollingScheme ps = null;
        for (BusiTemplatePollingScheme busiTemplatePollingScheme : pss) {
            if (ps == null || ps.getWeight() < busiTemplatePollingScheme.getWeight()) {
                ps = busiTemplatePollingScheme;
            }
        }
        if (ps == null) {
            throw new SystemException(1008243, "您当前还未配置已启用的轮询方案，请先配置轮询方案，再点开始轮询！");
        }

        conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
        PollingScheme pollingScheme = busiTemplatePollingSchemeService.convert(ps, conferenceContext);
        if (mainConferenceContext.getMasterAttendee() == null || !mainConferenceContext.getMasterAttendee().isMeetingJoined()) {
            if (pollingScheme.getIsBroadcast() != YesOrNo.YES || pollingScheme.getIsFixSelf() == YesOrNo.YES) {
                throw new SystemException(1005454, "主会场未设置或未呼入,无法进行轮询操作！");
            }
        }
        AttendeeOperation o = new PollingAttendeeOpreationImpl(conferenceContext, pollingScheme);
        conferenceContext.setAttendeeOperation(o);
        conferenceContext.getLastAttendeeOperation().cancel(o);
    }

    @Override
    public void pollingPause(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext.getRoundRobin()) {
            ((PollingAttendeeOpreationImpl) conferenceContext.getAttendeeOperation()).setPause(true);
        }
    }

    @Override
    public void pollingResume(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext.getRoundRobin()) {
            ((PollingAttendeeOpreationImpl) conferenceContext.getAttendeeOperation()).setPause(false);
        }
    }

    @Override
    public void cancelPolling(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        cancelCurrentOperation(ConferenceContextCache.getInstance().get(contextKey));
    }

    @Override
    public JSONObject detail(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        AttendeeInfo ai = new AttendeeInfo(contextKey, attendeeId);
        if (ai.getAttendee() != null && ai.getAttendee().isMeetingJoined()) {
            FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(ai.getAttendee());
            ParticipantServiceNewSynchronizer psns = new ParticipantServiceNewSynchronizer(fmeBridge, ai.getAttendee().getParticipantUuid());
            psns.sync();
            Participant p = psns.getFmeBridge().getDataCache().getParticipantByUuid(ai.getAttendee().getParticipantUuid());
            if (p != null) {
                CallLeg callLeg = p.getCallLeg();
                JSONObject d = toDetail(callLeg);
                d.put("attendeeId", attendeeId);
                return d;
            }
        }

        return null;
    }

    public JSONObject toDetail(CallLeg callLeg) {
        if (callLeg != null && callLeg.getStatus() != null) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("direction", callLeg.getDirection());
            jsonObj.put("type", callLeg.getType());
            jsonObj.put("isEncrypted", callLeg.getStatus().getEncryptedMedia());
            jsonObj.put("durationSeconds", callLeg.getStatus().getDurationSeconds());
            jsonObj.put("sipCallId", callLeg.getStatus().getSipCallId());
            jsonObj.put("remoteParty", callLeg.getRemoteParty());

            JSONObject downLink = new JSONObject();
            jsonObj.put("downLink", downLink);

            // 下行视频
            if (!ObjectUtils.isEmpty(callLeg.getStatus().getTxVideo())) {
                JSONArray videos = new JSONArray();
                for (CallLegStatusTxVideo callLegStatusTxVideo : callLeg.getStatus().getTxVideo()) {
                    JSONObject video = new JSONObject();
                    video.put("role", callLegStatusTxVideo.getRole());
                    video.put("resolutionRatio", callLegStatusTxVideo.getWidth() != null ? callLegStatusTxVideo.getWidth() + "x" + callLegStatusTxVideo.getHeight() : null);
                    video.put("frameRate", callLegStatusTxVideo.getFrameRate());
                    video.put("videoCodec", callLegStatusTxVideo.getCodec());
                    video.put("bandwidth", (callLegStatusTxVideo.getBitRate() != null ? callLegStatusTxVideo.getBitRate() : 0) / 1024.0);
                    video.put("packetLossPercentage", callLegStatusTxVideo.getPacketLossPercentage());
                    video.put("jitter", callLegStatusTxVideo.getJitter());
                    video.put("roundTripTime", callLegStatusTxVideo.getRoundTripTime());
                    video.put("packetLossPercentage", callLegStatusTxVideo.getPacketLossPercentage());
                    videos.add(video);
                }
                downLink.put("videos", videos);
            }

            if (callLeg.getStatus().getTxAudio() != null) {
                JSONObject audio = new JSONObject();
                CallLegStatusTxAudio callLegStatusTxVideo = callLeg.getStatus().getTxAudio();
                audio.put("codec", callLegStatusTxVideo.getCodec());
                audio.put("bandwidth", (callLegStatusTxVideo.getBitRate() != null ? callLegStatusTxVideo.getBitRate() : 0) / 1024.0);
                audio.put("packetLossPercentage", callLegStatusTxVideo.getPacketLossPercentage());
                audio.put("codecBitRate", callLegStatusTxVideo.getCodecBitRate() != null ? (callLegStatusTxVideo.getCodecBitRate() / 1000) + "k" : null);
                audio.put("jitter", callLegStatusTxVideo.getJitter());
                audio.put("roundTripTime", callLegStatusTxVideo.getRoundTripTime());
                audio.put("gainApplied", callLegStatusTxVideo.getGainApplied());
                downLink.put("audio", audio);
            }

            JSONObject upLink = new JSONObject();
            jsonObj.put("upLink", upLink);

            // 上行视频
            if (!ObjectUtils.isEmpty(callLeg.getStatus().getRxVideo())) {
                JSONArray videos = new JSONArray();
                for (CallLegStatusRxVideo callLegStatusTxVideo : callLeg.getStatus().getRxVideo()) {
                    JSONObject video = new JSONObject();
                    video.put("role", callLegStatusTxVideo.getRole());
                    video.put("resolutionRatio", callLegStatusTxVideo.getWidth() != null ? callLegStatusTxVideo.getWidth() + "x" + callLegStatusTxVideo.getHeight() : null);
                    video.put("frameRate", callLegStatusTxVideo.getFrameRate());
                    video.put("videoCodec", callLegStatusTxVideo.getCodec());
                    video.put("bandwidth", (callLegStatusTxVideo.getBitRate() != null ? callLegStatusTxVideo.getBitRate() : 0) / 1024.0);
                    video.put("packetLossPercentage", callLegStatusTxVideo.getPacketLossPercentage());
                    video.put("jitter", callLegStatusTxVideo.getJitter());
                    video.put("roundTripTime", callLegStatusTxVideo.getRoundTripTime());
                    videos.add(video);
                }
                upLink.put("videos", videos);
            }

            if (callLeg.getStatus().getRxAudio() != null) {
                JSONObject audio = new JSONObject();
                CallLegStatusRxAudio callLegStatusTxVideo = callLeg.getStatus().getRxAudio();
                audio.put("codec", callLegStatusTxVideo.getCodec());
                audio.put("bandwidth", (callLegStatusTxVideo.getBitRate() != null ? callLegStatusTxVideo.getBitRate() : 0) / 1024.0);
                audio.put("packetLossPercentage", callLegStatusTxVideo.getPacketLossPercentage());
                audio.put("codecBitRate", callLegStatusTxVideo.getCodecBitRate() != null ? (callLegStatusTxVideo.getCodecBitRate() / 1000) + "k" : null);
                audio.put("jitter", callLegStatusTxVideo.getJitter());
                audio.put("roundTripTime", callLegStatusTxVideo.getRoundTripTime());
                audio.put("gainApplied", callLegStatusTxVideo.getGainApplied());
                upLink.put("audio", audio);
            }

            return jsonObj;
        }
        return null;
    }

    /**
     * 与会者呼叫失败通知
     *
     * @param participantUuid void
     * @author lilinhai
     * @since 2021-02-08 13:49
     */
    public void callAttendeeFailedNotice(String participantUuid, String reason) {
        // 已呼叫的参会者
        Attendee attendee = AttendeeCallCache.getInstance().remove(participantUuid);
        if (attendee != null) {
            ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(attendee.getContextKey());
            if (conferenceContext != null) {
                if ("remoteBusy".equals(reason)) {
                    // 推送终端信令未断开的参会消息
                    AttendeeMessageQueue.getInstance().put(new SignalingNotDisconnectedAttendeeMessage(attendee));
                } else if ("timeout".equals(reason)) {
                    // 推送终端呼叫超时的参会消息
                    AttendeeMessageQueue.getInstance().put(new CallTimeoutAttendeeMessage(attendee));
                } else {
                    AttendeeMessageQueue.getInstance().put(new CallFailedAttendeeMessage(attendee));
                }

                StringBuilder msgBuilder = new StringBuilder();
                msgBuilder.append("【").append(attendee.getName()).append("】呼叫失败");

                if (attendee.getCallRequestSentTime() != null) {
                    long timeDiff = (System.currentTimeMillis() - attendee.getCallRequestSentTime()) / 1000;
                    if (timeDiff > 0) {
                        msgBuilder.append("【").append(timeDiff).append("秒】：");
                    } else {
                        msgBuilder.append("：");
                    }
                } else {
                    msgBuilder.append("：");
                }
                msgBuilder.append(reason);
                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, msgBuilder);
            } else {
                logger.info("找不到conferenceContext：" + attendee.getConferenceNumber());
            }

            // 清空uuid
            if (!attendee.isMeetingJoined()) {
                attendee.setParticipantUuid(null);
            }
            attendee.setCallRequestSentTime(null);
        }
    }

    /**
     * 批量修改参会者业务参数，支持集群
     *
     * @param contextKey
     * @param attendees
     * @param nameValuePairs void
     * @author lilinhai
     * @since 2021-04-19 11:45
     */
    private void updateAttendeeAttrs(String contextKey, List<Attendee> attendees, List<NameValuePair> nameValuePairs) {
        updateAttendeeAttrs(ConferenceContextCache.getInstance().get(contextKey), attendees, ParticipantBulkOperationMode.SELECTED, nameValuePairs);
    }

    private void processCallLegUpdate(String conferenceId, String attendeeId, List<? extends BaseFixedParamValue> params, String[] paramNames, ICallLegUpdateCallBackProcessor callLegUpdateCallBackProcessor) {

        Map<String, BaseFixedParamValue> fpvMap = new HashMap<>();
        for (BaseFixedParamValue fixedParamValue : params) {
            fpvMap.put(fixedParamValue.getName(), fixedParamValue);
        }

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        AttendeeInfo ai = new AttendeeInfo(contextKey, attendeeId);
        Assert.notNull(ai.getAttendee(), "找不到参会者：" + attendeeId);
        CustomParamBuilder customParamBuilder = new CustomParamBuilder();

        if (fpvMap.get(LAYOUT_TEMPLATE) == null) {
            for (String paramName : paramNames) {
                BaseFixedParamValue fixedParamValue = fpvMap.get(paramName);
                Assert.notNull(fixedParamValue, paramName + "不能为空！");
                Assert.notNull(fixedParamValue.getValue(), paramName + "值不能为空！");
                customParamBuilder.param(fixedParamValue.getName(), fixedParamValue.getValue());

                ai.getAttendee().getFixedSettings().getByName(paramName).setFixed(fixedParamValue.isFixed());
            }
            customParamBuilder.param(LAYOUT_TEMPLATE, "");
        } else {
            BaseFixedParamValue templateParamValue = fpvMap.get(LAYOUT_TEMPLATE);
            ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
            List<FmeBridge> fbs = FmeBridgeCache.getInstance().getFmeBridgesByDept(conferenceContext.getDeptId());
            if (!CollectionUtils.isEmpty(fbs)) {
                CustomScreenCreater customScreenCreater = fbs.get(0).getDataCache().getSplitScreenCreaterMap().get(templateParamValue.getValue());
                customParamBuilder.param(LAYOUT_TEMPLATE, customScreenCreater.getLayout());
                customParamBuilder.param(DEFAULT_LAYOUT, "");
                customParamBuilder.param(CHOSEN_LAYOUT, AUTOMATIC);
            }
            ai.getAttendee().getFixedSettings().getByName(CHOSEN_LAYOUT).setFixed(true);
        }


        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(ai.getAttendee());
        Participant participant = fmeBridge.getDataCache().getParticipantByUuid(ai.getAttendee().getParticipantUuid());
        CallLeg callLeg = participant.getCallLeg();
        RestResponse rr = fmeBridge.getCallLegInvoker().updateCallLeg(callLeg.getId(), customParamBuilder.build());
        if (!rr.isSuccess()) {
            throw new SystemException(1008544, rr.getMessage());
        }

        callLeg = callegService.getCallLegByParticipantUuid(fmeBridge, participant);
        participant.setCallLeg(callLeg);
        if (callLegUpdateCallBackProcessor != null) {
            callLegUpdateCallBackProcessor.process(fpvMap, fmeBridge, participant);
        }
    }

    @Override
    public List<String> takeSnapshotPolling(String conferenceId, JSONObject params) {

        List<Attendee> opList = new ArrayList<>();
        String direction = "rx";
        String maxWidth = "640";
        if (params.containsKey("maxWidth")) {
            maxWidth = params.getString("maxWidth");
            Assert.isTrue(maxWidth.matches("^\\d+$"), "maxWidth只能为正整数！");
        }
        String pollingId = params.getString("pollingId");
        int screenNumber = params.getIntValue("screenNumber");
        List<String> snapshotList = new ArrayList<>();
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
        List<Attendee> attendees = conferenceContext.getAttendees();
        if (attendees.isEmpty()) {
            return snapshotList;
        }
        for (Attendee attendee : attendees) {
            if (attendee.isMeetingJoined()) {
                Attendee attendeeNew = new InvitedAttendee();
                attendeeNew.setParticipantUuid(attendee.getParticipantUuid());
                attendeeNew.setMeetingStatus(attendee.getMeetingStatus());
                attendeeNew.setId(attendee.getId());
                attendeeNew.setDeptId(attendee.getDeptId());
                attendeeNew.setCallId(attendee.getCallId());
                opList.add(attendeeNew);
            }

        }
        Map<String, Integer> resourcesCount = conferenceContext.getResourcesSnapshotCount();
        Map<String, List<Attendee>> resourcesAttendee = conferenceContext.getResourcesSnapshotAttendee();

        List<Attendee> attendeeViewGroup = new ArrayList<>();
        Integer count = resourcesCount.get(pollingId);

        if (screenNumber >= opList.size()) {
            getSnapshot(direction, maxWidth, snapshotList, opList);
            return snapshotList;
        }


        if (count == null) {
            resourcesCount.put(pollingId, 1);
            List<Attendee> subAttendee = polling(direction, maxWidth, snapshotList, opList, attendeeViewGroup, screenNumber);
            subAttendee.addAll(attendeeViewGroup);
            attendeeViewGroup.clear();
            resourcesAttendee.put(pollingId, subAttendee);
        } else {
            resourcesCount.put(pollingId, count + 1);
            List<Attendee> attendees1 = resourcesAttendee.get(pollingId);
            Collection diffentNoDuplicate = getDiffentNoDuplicate(opList, attendees1);
            if (!CollectionUtils.isEmpty(diffentNoDuplicate)) {
                attendees1.addAll(diffentNoDuplicate);
            }
            List<Attendee> subAttendee = polling(direction, maxWidth, snapshotList, attendees1, attendeeViewGroup, screenNumber);
            subAttendee.addAll(attendeeViewGroup);
            attendeeViewGroup.clear();
            resourcesAttendee.put(pollingId, subAttendee);
        }


        return snapshotList;
    }

    private List<Attendee> polling(String direction, String maxWidth, List<String> snapshotList, List<Attendee> attendees, List<Attendee> attendeeViewGroup, int screenNumber) {
        for (int i = 0; i < attendees.size(); i++) {
            Attendee pollingAttendee = attendees.get(i);
            if (!pollingAttendee.isMeetingJoined()) {
                continue;
            }
            if (!attendeeViewGroup.contains(pollingAttendee)) {
                attendeeViewGroup.add(pollingAttendee);
                if (attendeeViewGroup.size() == screenNumber) {
                    getSnapshot(direction, maxWidth, snapshotList, attendeeViewGroup);
                    return attendees.subList(i + 1, attendees.size());
                }
            }

        }
        return null;
    }

    private void getSnapshot(String direction, String maxWidth, List<String> snapshotList, List<Attendee> attendeeViewGroup) {
        for (Attendee attendee : attendeeViewGroup) {
            FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(attendee);
            Participant participant = fmeBridge.getDataCache().getParticipantByUuid(attendee.getParticipantUuid());
            CallLeg callLeg = participant.getCallLeg();
            if (callLeg != null) {
                String snapshot = fmeBridge.getCallLegInvoker().takeSnapshot(callLeg.getId(), direction, Integer.parseInt(maxWidth));
                snapshotList.add(snapshot);
            }
        }
    }
}
