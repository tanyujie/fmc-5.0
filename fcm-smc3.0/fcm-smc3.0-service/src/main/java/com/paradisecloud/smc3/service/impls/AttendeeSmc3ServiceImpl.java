/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.service.impls
 * @author lilinhai
 * @since 2021-02-05 17:39
 * @version  V1.0
 */
package com.paradisecloud.smc3.service.impls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.AesEnsUtils;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc3TemplatePollingSchemeMapper;
import com.paradisecloud.fcm.dao.mapper.BusiUserTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.BusiUserTerminal;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.attendee.BaseFixedParamValue;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.smc3.busi.AttendeeSmc3Info;
import com.paradisecloud.smc3.busi.ConferenceNode;
import com.paradisecloud.smc3.busi.DefaultAttendeeOperation;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.InvitedAttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.TerminalAttendeeSmc3;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.busi.enumer.AttendeeImportance;
import com.paradisecloud.smc3.busi.operation.*;
import com.paradisecloud.smc3.busi.utils.AttendeeUtils;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextUtils;
import com.paradisecloud.smc3.busi.utils.Smc3WebSocketMessagePusher;
import com.paradisecloud.smc3.invoker.ConferenceState;
import com.paradisecloud.smc3.model.*;
import com.paradisecloud.smc3.model.request.ConferenceStatusRequest;
import com.paradisecloud.smc3.model.request.MultiPicInfoReq;
import com.paradisecloud.smc3.model.request.MultiPicInfoTalkReq;
import com.paradisecloud.smc3.model.request.TextTipsSetting;
import com.paradisecloud.smc3.model.response.SmcParitipantsStateRep;
import com.paradisecloud.smc3.service.interfaces.IAttendeeSmc3Service;
import com.paradisecloud.smc3.service.interfaces.IBusiSmc3TemplatePollingSchemeService;
import com.paradisecloud.smc3.task.InviteAttendeeSmc3Task;
import com.paradisecloud.smc3.task.Smc3DelayTaskService;
import com.paradisecloud.smc3.task.Smc3MeetingRoomRegTask;
import com.paradisecloud.smc3.websocket.client.SMC3WebsocketClient;
import com.paradisecloud.smc3.websocket.client.Smc3WebSocketProcessor;
import com.paradisecloud.smc3.websocket.client.Smc3WebsocketContext;
import com.paradisecloud.smc3.websocket.client.TopicMessage;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.util.Asserts;
import org.apache.logging.log4j.util.Strings;
import org.java_websocket.enums.ReadyState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
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
public class AttendeeSmc3ServiceImpl implements IAttendeeSmc3Service {

    public static final String LAYOUT_TEMPLATE = "layoutTemplate";
    public static final String DEFAULT_LAYOUT = "defaultLayout";
    public static final String CHOSEN_LAYOUT = "chosenLayout";
    public static final String AUTOMATIC = "automatic";
    private Logger logger = LoggerFactory.getLogger(AttendeeSmc3ServiceImpl.class);

    @Resource
    private BusiMcuSmc3TemplatePollingSchemeMapper busiMcuSmc3TemplatePollingSchemeMapper;

    @Resource
    private IBusiSmc3TemplatePollingSchemeService busiSmc3TemplatePollingSchemeService;

    @Resource
    private IMqttService mqttService;

    @Resource
    private Smc3DelayTaskService smc3delayTaskService;

    @Resource
    private RedisCache redisCache;

    @Resource
    private BusiUserTerminalMapper busiUserTerminalMapper;

    private boolean isContains(AttendeeSmc3 a, AttendeeSmc3... excludes) {
        if (!ObjectUtils.isEmpty(excludes)) {
            for (AttendeeSmc3 attendee : excludes) {
                if (a == attendee) {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateAttendeeImportance(AttendeeSmc3 attendee, AttendeeImportance attendeeImportance) {
        if (attendee != null && attendee.isMeetingJoined()) {
            List<String> participantIds = new ArrayList<>();
            if (!attendeeImportance.is(attendee.getImportance())) {
                participantIds.add(attendee.getParticipantUuid());
            }

            if (!participantIds.isEmpty()) {
//                ParticipantParamBuilder participantParamBuilder = new ParticipantParamBuilder();
//                participantParamBuilder.importance(attendeeImportance.getStartValue());
//                updateAttendeeAttrs(attendee.getDeptId(), attendee.getConferenceNumber(), participantParamBuilder.build(), ParticipantBulkOperationMode.SELECTED, participantIds.toArray(new String[participantIds.size()]));
            }
        }
    }

    @Override
    public void presentationSetting(String conferenceId, String attendeeId, List<BaseFixedParamValue> params) {
        // 辅流分辨率qualityPresentation: unset（空字符串）|unrestricted|max1080p30|max720p5
        // 开启辅流presentationContributionAllowed：unset（空字符串）|true|false
        // 接收双流presentationViewingAllowed: unset（空字符串）|true|false
        // 分享双流sipPresentationChannelEnabled: unset（空字符串）|true|false
        // 辅流模式bfcpMode: unset（空字符串）|serverOnly(客户端模式)|serverAndClient(服务器模式)
        String[] paramNames = {"qualityPresentation", "presentationContributionAllowed", "presentationViewingAllowed", "sipPresentationChannelEnabled", "bfcpMode"};
//        processCallLegUpdate(conferenceId, attendeeId, params, paramNames, null);
    }

    @Override
    public void mainSetting(String conferenceId, String attendeeId, List<BaseFixedParamValue> params) {
        // 主流分辨率qualityMain: unset（空字符串）|unrestricted|max1080p30|max720p30|max480p30
        // 显示画面txVideoMute（空字符串）|true|false
        // 关闭镜头 rxVideoMute: unset（空字符串）|true|false
        // 远端镜头控制controlRemoteCameraAllowed: unset（空字符串）|true|false
        String[] paramNames = {"qualityMain", "txAudioMute", "txVideoMute", "rxVideoMute", "controlRemoteCameraAllowed"};
//        processCallLegUpdate(conferenceId, attendeeId, params, paramNames, (Map<String, BaseFixedParamValue> fpvMap, FmeBridge fmeBridge, Participant participant) ->
//        {
//            // 执行关键帧请求
//            if (fpvMap.get("generateKeyframe") != null)
//            {
//                RestResponse rr0 = fmeBridge.getCallLegInvoker().generateKeyframe(participant.getCallLeg().getId());
//                if (!rr0.isSuccess())
//                {
//                    throw new SystemException(1008544, "请求关键帧失败：" + rr0.getMessage());
//                }
//            }
//        });
    }

    @Override
    public void subtitle(String conferenceId, String attendeeId, List<BaseFixedParamValue> params) {
        // 与会者名称nameLabelOverride: 任意字符串
        // 会议抬头位置 meetingTitlePosition：unset（空字符串）|disabled|top|middle|bottom
        // 显示与会者名称participantLabels: unset（空字符串）|true|false
        String[] paramNames = {"nameLabelOverride", "meetingTitlePosition", "participantLabels"};
//        processCallLegUpdate(conferenceId, attendeeId, params, paramNames, null);
    }

    @Override
    public void layoutSetting(String conferenceId, String attendeeId, List<BaseFixedParamValue> params) {
        String[] paramNames = {"chosenLayout", "defaultLayout"};
//        processCallLegUpdate(conferenceId, attendeeId, params, paramNames, null);
    }


    @Override
    public void recordStreamSetting(String conferenceId, String attendeeId, List<BaseFixedParamValue> params) {
        // 1. 布局设置要同时传两个参数chosenLayout和defaultLayout，传的值相同
        // 2. 主流分辨率: qualityMain  unset（空字符串）|unrestricted|max1080p30|max720p30|max480p30
        // 3. 是否录制辅流: presentationViewingAllowed unset（空字符串）|true|false
        String[] paramNames = {"chosenLayout", "defaultLayout", "qualityMain", "presentationViewingAllowed"};
//        processCallLegUpdate(conferenceId, attendeeId, params, paramNames, null);
    }

    @Override
    public void advanceSetting(String conferenceId, String attendeeId, List<BaseFixedParamValue> params) {
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
//        processCallLegUpdate(conferenceId, attendeeId, params, paramNames, null);
    }

    @Override
    public JSONObject attendeeCallLegSetting(String conferenceId, String attendeeId) {
        String cn = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
//        AttendeeInfo ai = new AttendeeInfo(cn, attendeeId);
//        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(ai.getAttendee());
//        Participant participant = fmeBridge.getDataCache().getParticipantByUuid(ai.getAttendee().getParticipantUuid());
//        JSONObject json = new JSONObject();
//        json.put("callLegConfiguration", participant.getCallLeg().getConfiguration());
//        json.put("fixedSettings", ai.getAttendee().getFixedSettings());
//        return json;
        return null;
    }

    @Override
    public void recall(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeSmc3 attendee = conferenceContext.getAttendeeById(attendeeId);
            if (attendee != null) {


                if (attendee.getCallRequestSentTime() != null && (System.currentTimeMillis() - attendee.getCallRequestSentTime()) < 5 * 1000) {
                    StringBuilder messageTip = new StringBuilder();
                    messageTip.append("【").append(attendee.getName()).append("】重呼请求已发起，请耐心等待响应结果，期间不要频繁发起！");
                    Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                    return;
                }


                ParticipantStatus participantStatus = new ParticipantStatus();
                participantStatus.setIsOnline(true);
                participantStatus.setId(attendee.getParticipantUuid());
                Smc3Bridge bridge = Smc3BridgeCache.getInstance().getBridgesByDept(conferenceContext.getDeptId());
                if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                    bridge.getSmcParticipantsInvoker().conferencesParticipantStatusOnlyCascade(conferenceContext.getSmc3conferenceId(), attendee.getParticipantUuid(), participantStatus, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                } else {
                    bridge.getSmcParticipantsInvoker().conferencesParticipantStatusOnly(conferenceContext.getSmc3conferenceId(), attendee.getParticipantUuid(), participantStatus, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }
                if (attendee.getParticipantUuid() == null) {
                    InviteAttendeeSmc3Task inviteAttendeeSmc3Task = new InviteAttendeeSmc3Task(attendeeId, 100, conferenceContext, attendee);
                    Smc3DelayTaskService delayTaskService = BeanFactory.getBean(Smc3DelayTaskService.class);
                    delayTaskService.addTask(inviteAttendeeSmc3Task);
                }
                StringBuilder messageTip = new StringBuilder();
                messageTip.append("【").append(attendee.getName()).append("】呼叫已发起！");
                Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                attendee.setHangUp(false);
                attendee.setCallRequestSentTime(System.currentTimeMillis());
                if (attendee instanceof TerminalAttendeeSmc3) {
                    BusiTerminal terminal = TerminalCache.getInstance().get(attendee.getTerminalId());
                    if (terminal != null && TerminalType.isFCMSIP(terminal.getType()) && com.paradisecloud.common.utils.StringUtils.isNotEmpty(terminal.getSn())) {
                        BeanFactory.getBean(IMqttService.class).inviteAttendeeJoinConference(attendee, conferenceContext, AttendType.AUTO_JOIN.getValue());
                        return;
                    }
                }
                if (conferenceContext.getMasterAttendee() != null) {
                    if (Objects.equals(attendeeId, conferenceContext.getMasterAttendee().getId())) {
                        AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
                        if (attendeeOperation instanceof ChangeMasterAttendeeOperation) {
                            while (true) {
                                if (attendee.isMeetingJoined()) {
                                    ChangeMasterAttendeeOperation changeMasterAttendeeOperation = new ChangeMasterAttendeeOperation(conferenceContext, conferenceContext.getMasterAttendee());
                                    changeMasterAttendeeOperation.operate();
                                    break;
                                }
                            }
                        } else if (attendeeOperation instanceof CallTheRollAttendeeOperation) {

                            while (true) {
                                if (attendee.isMeetingJoined()) {
                                    attendeeOperation.operate();
                                    break;
                                }
                            }
                        } else {
                            while (true) {
                                if (attendee.isMeetingJoined()) {
                                    ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
                                    String participantUuid = attendee.getParticipantUuid();
                                    conferenceStatusRequest.setChairman(participantUuid);
                                    Smc3Bridge smc3Bridge = conferenceContext.getSmc3Bridge();

                                    if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                                        smc3Bridge.getSmcConferencesInvoker().conferencesStatusControlCascade(conferenceContext.getSmc3conferenceId(), conferenceStatusRequest, smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                                    } else {
                                        smc3Bridge.getSmcConferencesInvoker().conferencesStatusControl(conferenceContext.getSmc3conferenceId(), conferenceStatusRequest, smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                if (attendee.isMcuAttendee()) {
                    BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(attendee.getCascadeConferenceId()));
                    if (baseConferenceContext != null) {
                        baseConferenceContext.setUpCascadeRemoteParty(conferenceContext.getConferenceRemoteParty());
                    }
                }

            }

        }
    }

    @Override
    public void callAttendee(AttendeeSmc3 attendee) {
//        new CallAttendeeProcessor(attendee).process();
    }

    @Override
    public void hangUp(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeSmc3 attendeeSmc3 = conferenceContext.getAttendeeById(attendeeId);
            if (attendeeSmc3 != null) {
                attendeeSmc3.setHangUp(true);
                String participantId = attendeeSmc3.getSmcParticipant().getGeneralParam().getId();

                ParticipantStatus participantStatus = new ParticipantStatus();
                participantStatus.setIsOnline(false);
                participantStatus.setId(participantId);
                Smc3Bridge bridge = Smc3BridgeCache.getInstance().getBridgesByDept(conferenceContext.getDeptId());
                if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                    bridge.getSmcParticipantsInvoker().conferencesParticipantStatusOnlyCascade(conferenceContext.getSmc3conferenceId(), participantId, participantStatus, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                } else {
                    bridge.getSmcParticipantsInvoker().conferencesParticipantStatusOnly(conferenceContext.getSmc3conferenceId(), participantId, participantStatus, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }
                AttendeeSmc3 masterAttendee = conferenceContext.getMasterAttendee();
                if (masterAttendee != null && Objects.equals(attendeeId, masterAttendee.getId())) {
                    //主席轮询操作中断
                    AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();

                    if (attendeeOperation != null) {
                        if (attendeeOperation instanceof ChangeMasterAttendeeOperation) {
                            ChangeMasterAttendeeOperation changeMasterAttendeeOperation = (ChangeMasterAttendeeOperation) attendeeOperation;
                            changeMasterAttendeeOperation.cancel();
                        } else if (attendeeOperation instanceof ChairmanPollingAttendeeOperation) {
                            ChairmanPollingAttendeeOperation chairmanPollingAttendeeOperation = (ChairmanPollingAttendeeOperation) attendeeOperation;
                            chairmanPollingAttendeeOperation.cancel();
                        } else if (attendeeOperation instanceof CallTheRollAttendeeOperation) {
                            CallTheRollAttendeeOperation callTheRollAttendeeOperation = (CallTheRollAttendeeOperation) attendeeOperation;
                            callTheRollAttendeeOperation.cancel();
                        }
                    }
                }

                Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendeeSmc3.getName() + "】挂断请求已发起");

            }
        }
    }

    @Override
    public void remove(String conferenceId, String attendeeId) {
        try {
            String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
            Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
            if (conferenceContext != null) {
                AttendeeSmc3 attendeeSmc3 = conferenceContext.getAttendeeById(attendeeId);
                if (attendeeSmc3 != null) {
                    conferenceContext.removeAttendeeById(attendeeId);
                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("id", attendeeSmc3.getId());
                    updateMap.put("deptId", attendeeSmc3.getDeptId());
                    updateMap.put("mcuAttendee", attendeeSmc3.isMcuAttendee());

                    if (attendeeSmc3.getSmcParticipant() != null) {
                        String participantId = attendeeSmc3.getSmcParticipant().getGeneralParam().getId();

                        List<String> participantIds = new ArrayList<>();
                        participantIds.add(participantId);
                        Smc3Bridge bridge = Smc3BridgeCache.getInstance().getBridgesByDept(conferenceContext.getDeptId());
                        if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                            bridge.getSmcParticipantsInvoker().deleteParticipantsCascade(conferenceContext.getSmc3conferenceId(), participantIds, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

                        } else {
                            bridge.getSmcParticipantsInvoker().deleteParticipants(conferenceContext.getSmc3conferenceId(), participantIds, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

                        }
                    }
                    Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                } else {
                    throw new CustomException("该终端已被移除，请刷新页面后重试");
                }

            }
        } catch (Exception e) {
            logger.error("与会者移除错误" + e.getMessage());
            throw new CustomException("移除失败");
        }
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

        String cn = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
//        AttendeeInfo ai = new AttendeeInfo(cn, attendeeId);
//        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(ai.getAttendee());
//        Participant participant = fmeBridge.getDataCache().getParticipantByUuid(ai.getAttendee().getParticipantUuid());
//        CallLeg callLeg = participant.getCallLeg();
//        if (callLeg != null)
//        {
//            String snapshot = fmeBridge.getCallLegInvoker().takeSnapshot(callLeg.getId(), direction, Integer.parseInt(maxWidth));
//            WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(ai.getConferenceContext(), WebsocketMessageType.MESSAGE_TIP, "【" + ai.getAttendee().getName() + "】获取快照成功！");
//            return snapshot;
//        }
        return null;
    }

    /**
     * <pre>相机控制</pre>
     *
     * @param conferenceId
     * @param attendeeId
     * @param params
     * @author sinhy
     * @since 2021-08-18 13:55
     */
    @Override
    public void cameraControl(String conferenceId, String attendeeId, JSONObject params) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        AttendeeSmc3 attendeeSmc3 = conferenceContext.getAttendeeById(attendeeId);
        if (attendeeSmc3 != null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("number", params.get("number"));
            jsonObject.put("operate", params.get("operate"));
            jsonObject.put("controlType", params.get("controlType"));
            Smc3Bridge smc3Bridge = conferenceContext.getSmc3Bridge();
            if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                smc3Bridge.getSmcParticipantsInvoker().participantsCameraControlCascade(conferenceContext.getSmc3conferenceId(), attendeeSmc3.getSmcParticipant().getGeneralParam().getId(), jsonObject, smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            } else {
                smc3Bridge.getSmcParticipantsInvoker().participantsCameraControl(conferenceContext.getSmc3conferenceId(), attendeeSmc3.getSmcParticipant().getGeneralParam().getId(), jsonObject, smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }
            Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendeeSmc3.getName() + "】镜头调整成功！");

        }
    }

    /**
     * 变更主会场
     *
     * @author lilinhai
     * @since 2021-02-09 11:32  void
     */
    @Override
    public void changeMaster(String conferenceId, String attendeeId) {
        if (StringUtil.isEmpty(attendeeId)) {
            return;
        }
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext cc = Smc3ConferenceContextCache.getInstance().get(contextKey);
        AttendeeSmc3 attendeeSmc3 = cc.getAttendeeById(attendeeId);
        if (attendeeSmc3 != null) {

            if (cc.isDownCascadeConference()) {
                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(cc.getUpCascadeConferenceId()));
                if (baseConferenceContext != null) {
                    if (!attendeeSmc3.getRemoteParty().equals(cc.getUpCascadeRemoteParty())) {
                        throw new SystemException(1005454, "该会议正被级联，不允许修改主会场！");
                    }
                }
            }

            AttendeeOperation old = cc.getAttendeeOperation();
            cc.setLastAttendeeOperation(old);
            old.cancel();
            AttendeeOperation attendeeOperation = new ChangeMasterAttendeeOperation(cc, attendeeSmc3);
            cc.setAttendeeOperation(attendeeOperation);
            attendeeOperation.operate();
        }
    }

    /**
     * 选看
     *
     * @author lilinhai
     * @since 2021-02-09 11:32  void
     */
    @Override
    public void chooseSee(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext cc = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (cc.getMasterAttendee() == null || cc.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue()) {
            throw new SystemException(1005454, "主会场未设置，无法进行选看操作！");
        }


        AttendeeSmc3 attendeeSmc3 = cc.getAttendeeById(attendeeId);
        if (attendeeSmc3 == null) {
            throw new CustomException("与会者不存在，无法进行选看操作！");
        }
        if (cc != null) {
            AttendeeOperation old = cc.getAttendeeOperation();
            cc.setLastAttendeeOperation(old);
            old.cancel();
            AttendeeOperation attendeeOperation = new ChooseSeeAttendeeOperation(cc, attendeeSmc3);
            cc.setAttendeeOperation(attendeeOperation);
            attendeeOperation.operate();
        }
    }

    /**
     * 选看
     *
     * @author lilinhai
     * @since 2021-02-09 11:32  void
     */
    @Override
    public void chooseSee(String conferenceId, String attendeeId, boolean upCascadeOperate, boolean upCascadeBroadcast, boolean upCascadePolling, boolean upCascadeRollCall) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext.getMasterAttendee() == null || conferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue()) {
            throw new SystemException(1005454, "主会场未设置，无法进行选看操作！");
        }


        if (conferenceContext != null) {

            AttendeeSmc3 chooseSeeAttendee = conferenceContext.getAttendeeById(attendeeId);
            if (chooseSeeAttendee != null) {
                if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                    conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
                }
                AttendeeOperation old = conferenceContext.getAttendeeOperation();
                old.cancel();
                AttendeeOperation attendeeOperation = new ChooseSeeAttendeeOperation(conferenceContext, chooseSeeAttendee);
                attendeeOperation.setUpCascadeOperate(upCascadeOperate);
                attendeeOperation.setUpCascadeBroadcast(upCascadeBroadcast);
                attendeeOperation.setUpCascadePolling(upCascadePolling);
                attendeeOperation.setUpCascadeRollCall(upCascadeRollCall);
                conferenceContext.setAttendeeOperation(attendeeOperation);
                attendeeOperation.operate();
            }
        }
    }

    /**
     * 默认选看（主会场切换时执行）
     *
     * @author lilinhai
     * @since 2021-02-09 11:32  void
     */
    @Override
    public void defaultChooseSee(Smc3ConferenceContext mainConferenceContext) {
        if (mainConferenceContext.getMasterAttendee() == null || !mainConferenceContext.getMasterAttendee().isMeetingJoined()) {
            return;
        }

        // 只有当操作是默认的选看时，才会执行该逻辑
        AttendeeSmc3 a = Smc3ConferenceContextUtils.getDefaultChooseToSee(mainConferenceContext);
        if (a != null) {
            logger.info("已找到默认选看参会, ConferenceNumber：" + mainConferenceContext.getConferenceNumber() + ", Attendee: " + a.getName());

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
     * @since 2021-02-22 18:07
     */
    @Override
    public void callTheRoll(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext cc = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (cc.getMasterAttendee() == null || cc.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue()) {
            throw new SystemException(1005454, "主会场未设置，无法进行点名操作！");
        }


        AttendeeSmc3 attendee = cc.getAttendeeById(attendeeId);
        if (cc != null) {
            cc.setLastAttendeeOperation(cc.getAttendeeOperation());
            CallTheRollAttendeeOperation callTheRollAttendeeOperation = new CallTheRollAttendeeOperation(cc, attendee);
            AttendeeOperation attendeeOperation = cc.getAttendeeOperation();
            attendeeOperation.cancel();
            cc.setAttendeeOperation(callTheRollAttendeeOperation);
            callTheRollAttendeeOperation.operate();
        } else {
            throw new SystemException(1005454, "该会议无法进行点名操作！");
        }
    }

    /**
     * 对话
     *
     * @param conferenceId
     * @param attendeeId
     * @author sinhy
     * @since 2021-12-02 12:47
     */
    @Override
    public void talk(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);

        if (conferenceContext.getMasterAttendee() == null || conferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue()) {
            throw new SystemException(1005452, "主会场未设置，无法进行对话操作！");
        }

        conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
        conferenceContext.getAttendeeOperation().cancel();
        AttendeeOperation attendeeOperation = new TalkAttendeeOperation(conferenceContext, conferenceContext.getAttendeeById(attendeeId));
        conferenceContext.setAttendeeOperation(attendeeOperation);
        attendeeOperation.operate();


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
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);

        if (conferenceContext != null) {
            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            if (attendeeOperation instanceof CallTheRollAttendeeOperation) {
                attendeeOperation.cancel();
                conferenceContext.setAttendeeOperation(conferenceContext.getLastAttendeeOperation());
            }
        }
    }

    /**
     * 取消对话
     *
     * @param conferenceId
     */
    @Override
    public void cancelTalk(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            attendeeOperation.cancel();
            AttendeeOperation lastAttendeeOperation = conferenceContext.getLastAttendeeOperation();
            if (lastAttendeeOperation != null) {
                if (!(lastAttendeeOperation instanceof TalkAttendeeOperation)) {
                    conferenceContext.setAttendeeOperation(lastAttendeeOperation);
                    lastAttendeeOperation.operate();
                } else {
                    conferenceContext.defaultAttendeeOperation();
                }
            } else {
                conferenceContext.defaultAttendeeOperation();
            }

        }
    }

    /**
     * <pre>取消当前操作</pre>
     *
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-02-23 16:14
     */
    @Override
    public void cancelCurrentOperation(Smc3ConferenceContext conferenceContext) {
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
     * @since 2021-02-22 18:07
     */
    @Override
    public void openMixing(String conferenceId, String attendeeId) {
        logger.info("开启单个参会者混音入口：" + attendeeId);
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);

        AttendeeSmc3 attendeeById = conferenceContext.getAttendeeById(attendeeId);


        List<ParticipantStatusDto> participantStatusList = new ArrayList<>();
        ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
        participantStatusDto.setIsMute(false);
        participantStatusDto.setId(attendeeById.getSmcParticipant().getGeneralParam().getId());
        Smc3Bridge bridgesByDept = Smc3BridgeCache.getInstance().getBridgesByDept(conferenceContext.getDeptId());
        String conferenceIdSmc = conferenceContext.getSmc3conferenceId();
        participantStatusList.add(participantStatusDto);

        if (Objects.equals(conferenceContext.getCategory(), "CASCADE")) {
            bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnlyCascade(conferenceIdSmc, participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            attendeeById.setMixingStatus(AttendeeMixingStatus.YES.getValue());

            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeById.getUpdateMap());
        }else {
            bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnly(conferenceIdSmc, participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }

    }

    /**
     * 接受举手
     *
     * @param conferenceId
     * @param attendeeId
     * @author sinhy
     * @since 2021-12-07 10:27
     */
    @Override
    public void acceptRaiseHand(String conferenceId, String attendeeId) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext.getMasterAttendee() == null || conferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue()) {
            throw new SystemException(1005454, "主会场未设置，无法进行举手操作！");
        }
        if (conferenceContext.getMasterAttendee().getId() == attendeeId) {
            throw new SystemException(1005454, "该参会者是主会场，无法进行举手操作！");
        }


        if (conferenceContext != null) {

        } else {
            throw new SystemException(1005454, "该会议无法进行举手操作！");
        }


        mqttService.sendConferenceInfoToPushTargetTerminal(conferenceContext);
    }

    /**
     * 拒绝举手
     *
     * @param conferenceId
     * @param attendeeId
     * @author sinhy
     * @since 2021-12-07 10:27
     */
    @Override
    public void rejectRaiseHand(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        RaiseHandStatus raiseHandStatus = RaiseHandStatus.NO;
        // 关闭举手
        AttendeeSmc3 raiseHandsAttendee = conferenceContext.getAttendeeById(attendeeId);
        raiseHandsAttendee.resetUpdateMap();
        raiseHandsAttendee.setRaiseHandStatus(raiseHandStatus.getValue());
        //TODO
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
    @Override
    public void raiseHand(String conferenceId, String attendeeId, RaiseHandStatus raiseHandStatus) {

    }

    @Override
    public void setBanner(String conferenceId, String attendeeId, JSONObject params) {


    }

    @Override
    public void sendBanner(String conferenceId, JSONObject jsonObject) {

    }

    /**
     * <pre>关闭混音</pre>
     *
     * @param conferenceId
     * @param attendeeId
     * @author lilinhai
     * @since 2021-02-22 18:07
     */
    @Override
    public void closeMixing(String conferenceId, String attendeeId) {
        logger.info("关闭单个参会者混音入口：" + attendeeId);
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        AttendeeSmc3 attendeeById = conferenceContext.getAttendeeById(attendeeId);



        List<ParticipantStatusDto> participantStatusList = new ArrayList<>();
        ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
        participantStatusDto.setIsMute(true);
        participantStatusDto.setId(attendeeById.getSmcParticipant().getGeneralParam().getId());
        Smc3Bridge bridgesByDept = Smc3BridgeCache.getInstance().getBridgesByDept(conferenceContext.getDeptId());
        String conferenceIdSmc = conferenceContext.getSmc3conferenceId();
        participantStatusList.add(participantStatusDto);

        if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
            bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnlyCascade(conferenceIdSmc, participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            attendeeById.setMixingStatus(AttendeeMixingStatus.NO.getValue());

            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeById.getUpdateMap());
        } else {
            bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnly(conferenceIdSmc, participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }

    }


    public void invite(String conferenceId, Integer callType, List<Long> terminalIds) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);

        Integer maxParticipantNum = conferenceContext.getMaxParticipantNum();
        int total = conferenceContext.getAttendeeCountingStatistics().getTotal();
        if (total >= maxParticipantNum) {
            throw new CustomException("会议人数已达到上限，如需加入请联系会议管理员");
        }

        List<AttendeeSmc3> attendees = new ArrayList<>();
        List<BusiTerminal> terminals = new ArrayList<>();
        for (Long terminalId : terminalIds) {
            BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
            if (busiTerminal != null) {
                terminals.add(busiTerminal);
            }
            TerminalAttendeeSmc3 ta = conferenceContext.getTerminalAttendeeMap().get(terminalId);
            if (ta == null) {
                ta = AttendeeUtils.packTerminalAttendee(terminalId);
                if (ta != null) {
                    ta.setConferenceNumber(conferenceContext.getConferenceNumber());
                    ta.setDeptId(conferenceContext.getDeptId());
                    BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(terminalId);
                    if (busiUserTerminal != null) {
                        ta.setUserId(busiUserTerminal.getUserId());
                    }
                    ta.setCallType(callType);
                    conferenceContext.addAttendee(ta);
                    attendees.add(ta);
                    Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, ta);
                    Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + ta.getName() + "】被邀请加入");

                    mqttService.sendConferenceInfoToPushTargetTerminal(conferenceContext);

                    Smc3MeetingRoomRegTask smc3MeetingRoomRegTask = new Smc3MeetingRoomRegTask(ta.getId(), ta.getRemoteParty(), 10, busiTerminal.getDeptId(), busiTerminal);
                    smc3delayTaskService.addTask(smc3MeetingRoomRegTask);
                }

            }
        }


        recallAttendees(conferenceId, attendees);
    }

    @Override
    public void invite(String conferenceId, List<Long> terminalIds) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);

        Integer maxParticipantNum = conferenceContext.getMaxParticipantNum();
        int total = conferenceContext.getAttendeeCountingStatistics().getTotal();
        if (total >= maxParticipantNum) {
            throw new CustomException("会议人数已达到上限，如需加入请联系会议管理员");
        }

        List<AttendeeSmc3> attendees = new ArrayList<>();
        List<BusiTerminal> terminals = new ArrayList<>();
        for (Long terminalId : terminalIds) {
            BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
            if (busiTerminal != null) {
                terminals.add(busiTerminal);
            }
            TerminalAttendeeSmc3 ta = conferenceContext.getTerminalAttendeeMap().get(terminalId);
            if (ta == null) {
                ta = AttendeeUtils.packTerminalAttendee(terminalId);
                ta.setConferenceNumber(conferenceContext.getConferenceNumber());
                ta.setDeptId(conferenceContext.getDeptId());
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(terminalId);
                if (busiUserTerminal != null) {
                    ta.setUserId(busiUserTerminal.getUserId());
                }
                conferenceContext.addAttendee(ta);
                attendees.add(ta);
                Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, ta);
                Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + ta.getName() + "】被邀请加入");

                mqttService.sendConferenceInfoToPushTargetTerminal(conferenceContext);

                Smc3MeetingRoomRegTask smc3MeetingRoomRegTask = new Smc3MeetingRoomRegTask(ta.getId(), ta.getRemoteParty(), 10, busiTerminal.getDeptId(), busiTerminal);
                smc3delayTaskService.addTask(smc3MeetingRoomRegTask);
            }
        }


        recallAttendees(conferenceId, attendees);
    }

    @Override
    public void invite(String conferenceId, JSONObject jsonObj) {
        Assert.isTrue(jsonObj.containsKey("name"), "名字是必填参数！");
        Assert.isTrue(jsonObj.containsKey("uri"), "URI是必填参数！");
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext.getUuidAttendeeMapByUri(jsonObj.getString("uri")) != null) {
            return;
        }

        String name = jsonObj.getString("name");
        String uri = jsonObj.getString("uri");
        Integer callType = jsonObj.getInteger("callType");
        if (callType != null && callType != 1) {
            callType = null;
        }

        BusiTerminal busiTerminal = TerminalCache.getInstance().getByRemoteParty(conferenceContext.getDeptId(), uri);
        if (busiTerminal != null) {
            invite(conferenceId, callType, Arrays.asList(busiTerminal.getId()));
            return;
        }

        Map<String, AttendeeSmc3> AttendeeSmc3Map = conferenceContext.getUuidAttendeeMapByUri(uri);
        if (AttendeeSmc3Map != null && AttendeeSmc3Map.size() > 0) {
            Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + name + "】已在会");
            return;
        }

        InvitedAttendeeSmc3 ia = new InvitedAttendeeSmc3();
        ia.setConferenceNumber(conferenceContext.getConferenceNumber());
        ia.setId(UUID.randomUUID().toString());
        ia.setName(name);
        ia.setRemoteParty(jsonObj.getString("uri"));
        ia.setWeight(1);
        ia.setDeptId(conferenceContext.getDeptId());
        if (ia.getRemoteParty().contains("@")) {
            ia.setIp(ia.getRemoteParty().split("@")[1]);
        } else {
            ia.setIp(ia.getRemoteParty());
        }
        ia.setCallType(callType);

        conferenceContext.addAttendee(ia);
        Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, ia);
        Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + ia.getName() + "】被邀请加入");
        recallAttendee(conferenceId, ia);

        mqttService.sendConferenceInfoToPushTargetTerminal(conferenceContext);
    }

    private void recallAttendee(String conferenceId, AttendeeSmc3 attendee) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            InviteAttendeeSmc3Task inviteAttendeesTask = new InviteAttendeeSmc3Task(conferenceContext.getConferenceNumber(), 100, conferenceContext, attendee);
            smc3delayTaskService.addTask(inviteAttendeesTask);
        }
    }

    private void recallAttendees(String conferenceId, List<AttendeeSmc3> attendees) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            InviteAttendeeSmc3Task inviteAttendeesTask = new InviteAttendeeSmc3Task(contextKey, 100, conferenceContext, attendees);
            smc3delayTaskService.addTask(inviteAttendeesTask);
        }
    }

    @Override
    public void openCamera(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        // 打开终端摄像头（打开上行视频）
        AttendeeSmc3 attendee = conferenceContext.getAttendeeById(attendeeId);
        if (attendee != null) {
            List<ParticipantStatusDto> participantStatusList = new ArrayList<>();
            ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
            participantStatusDto.setIsVideoMute(false);
            participantStatusDto.setId(attendee.getSmcParticipant().getGeneralParam().getId());
            Smc3Bridge bridgesByDept = Smc3BridgeCache.getInstance().getBridgesByDept(conferenceContext.getDeptId());
            String conferenceIdSmc = conferenceContext.getSmc3conferenceId();
            participantStatusList.add(participantStatusDto);
            if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnlyCascade(conferenceIdSmc, participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            } else {
                bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnly(conferenceIdSmc, participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }
        }
    }

    @Override
    public void closeCamera(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        // 打开终端摄像头（打开上行视频）
        AttendeeSmc3 attendee = conferenceContext.getAttendeeById(attendeeId);
        if (attendee != null) {
            List<ParticipantStatusDto> participantStatusList = new ArrayList<>();
            ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
            participantStatusDto.setIsVideoMute(true);
            participantStatusDto.setId(attendee.getSmcParticipant().getGeneralParam().getId());
            Smc3Bridge bridgesByDept = Smc3BridgeCache.getInstance().getBridgesByDept(conferenceContext.getDeptId());
            String conferenceIdSmc = conferenceContext.getSmc3conferenceId();
            participantStatusList.add(participantStatusDto);
            if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnlyCascade(conferenceIdSmc, participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            } else {
                bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnly(conferenceIdSmc, participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }
        }
    }

    @Override
    public void openMixing(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        openMixing(conferenceContext);
    }

    @Override
    public void openMixing(Smc3ConferenceContext cc) {
        List<AttendeeSmc3> as = new ArrayList<>();
        Smc3ConferenceContextUtils.eachNonFmeAttendeeInConference(cc, (a) -> {
            if (a.isMeetingJoined()) {
                as.add(a);
            }
        });

        if (CollectionUtils.isNotEmpty(as)) {
            for (AttendeeSmc3 a : as) {
                openMixing(cc.getId(), a.getId());
            }
        }
    }

    @Override
    public void openDisplayDevice(String conferenceId) {
        String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(conferenceNumber);
        // 允许所有人观看（打开所有下行视频）


    }

    @Override
    public void closeDisplayDevice(String conferenceId) {
        String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(conferenceNumber);
        // 禁止所有观众观看（关闭所有下行视频）

    }


    @Override
    public void openDisplayDevice(String conferenceId, String attendeeId) {
        String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(conferenceNumber);
        // 打开终端摄像头（打开上行视频）
        AttendeeSmc3 attendee = conferenceContext.getAttendeeById(attendeeId);
        if (attendee != null) {

        }
    }

    @Override
    public void closeDisplayDevice(String conferenceId, String attendeeId) {
        String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(conferenceNumber);
        // 打开终端摄像头（打开上行视频）
        AttendeeSmc3 attendee = conferenceContext.getAttendeeById(attendeeId);
        if (attendee != null) {

        }
    }

    @Override
    public void closeCamera(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        Smc3ConferenceContextUtils.eachNonFmeAttendeeInConference(conferenceContext, (a) -> {
            if (a.isMeetingJoined()) {
                closeCamera(conferenceId, a.getId());
            }
        });
    }

    @Override
    public void openCamera(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        Smc3ConferenceContextUtils.eachNonFmeAttendeeInConference(conferenceContext, (a) -> {
            if (a.isMeetingJoined()) {
                openCamera(conferenceId, a.getId());
            }
        });
    }

    @Override
    public void closeMixing(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext cc = Smc3ConferenceContextCache.getInstance().get(contextKey);
        closeMixing(cc, cc.getMasterAttendee());
    }

    @Override
    public void closeMixing(Smc3ConferenceContext cc, AttendeeSmc3... excludes) {
        List<AttendeeSmc3> as = new ArrayList<>();
        Smc3ConferenceContextUtils.eachNonFmeAttendeeInConference(cc, (a) -> {
            for (AttendeeSmc3 attendee : excludes) {
                if (a == attendee) {
                    return;
                }
            }
            if (a.isMeetingJoined()) {
                as.add(a);
            }
        });
        if (CollectionUtils.isNotEmpty(as)) {
            for (AttendeeSmc3 a : as) {
                closeMixing(cc.getId(), a.getId());
            }
        }
    }

    @Override
    public JSONObject sendMessage(String conferenceId, JSONObject jsonObject) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        conferenceId = conferenceContext.getSmc3conferenceId();
        TextTipsSetting textTipsSetting = JSONObject.parseObject(JSONObject.toJSONString(jsonObject), TextTipsSetting.class);
        textTipsSetting.setType(TxtTypeEnum.CAPTION.name());
        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
        bridge.getSmcConferencesInvoker().textTipsSetting(conferenceId, textTipsSetting, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        conferenceContext.setCaption(textTipsSetting);
        HashMap<Object, Object> pushMap = new HashMap<>();
        pushMap.put("caption", jsonObject);
        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, pushMap);
        return jsonObject;
    }

    @Override
    public JSONObject sendMessageForMinutes(String conferenceId, JSONObject jsonObject) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        conferenceId = conferenceContext.getSmc3conferenceId();
        TextTipsSetting textTipsSetting = JSONObject.parseObject(JSONObject.toJSONString(jsonObject), TextTipsSetting.class);
        textTipsSetting.setType(TxtTypeEnum.BOTTOMCAP.name());
        String content = jsonObject.getString("content");
        if (StringUtils.isNotEmpty(content)) {
            textTipsSetting.setOpType(TxtOperationTypeEnumDto.SET.name());
        } else {
            textTipsSetting.setOpType(TxtOperationTypeEnumDto.CANCEL.name());
        }
        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
        bridge.getSmcConferencesInvoker().textTipsSetting(conferenceId, textTipsSetting, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        return jsonObject;
    }

    /**
     * 设置横幅
     *
     * @param conferenceId
     * @param jsonObject   void
     */
    @Override
    public JSONObject setMessageBannerText(String conferenceId, JSONObject jsonObject) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        String jsonStr = JSONObject.toJSONString(jsonObject);

        TextTipsSetting textTipsSetting = JSONObject.parseObject(jsonStr, TextTipsSetting.class);
        String content = textTipsSetting.getContent();
        if (!ObjectUtils.isEmpty(content)) {
            if (content.getBytes().length <= 60) {
                String replacedText = content.replaceAll("\t|\r|\n", "");
                if (replacedText.length() < content.length()) {
                    throw new SystemException("横幅不支持【制表】【回车】等特殊字符！");
                }
            } else {
                if (content.getBytes().length > 60) {
                    throw new SystemException("横幅最多支持30个字符！");
                }
            }
        }
        setMessageBannerText(contextKey, jsonStr);
        return jsonObject;
    }

    /**
     * 设置横幅
     *
     * @param contextKey
     * @param text
     */
    @Override
    public void setMessageBannerText(String contextKey, String text) {
        Smc3ConferenceContext cc = Smc3ConferenceContextCache.getInstance().get(contextKey);
        TextTipsSetting textTipsSetting = JSONObject.parseObject(text, TextTipsSetting.class);
        Smc3Bridge bridge = cc.getSmc3Bridge();
        bridge.getSmcConferencesInvoker().textTipsSetting(cc.getSmc3conferenceId(), textTipsSetting, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        if(Objects.equals(ConstAPI.CASCADE,cc.getCategory())){
            bridge.getSmcConferencesInvoker().textTipsSettingCascade(cc.getSmc3conferenceId(), textTipsSetting, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }
        cc.setBanner(textTipsSetting);
        HashMap<Object, Object> pushMap = new HashMap<>();
        pushMap.put("banner", textTipsSetting);
        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.CONFERENCE_CHANGE, pushMap);
    }

    @Override
    public void polling(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        Smc3ConferenceContext mainConferenceContext = Smc3ConferenceContextCache.getInstance().getMainConferenceContext(conferenceContext);
        if (mainConferenceContext.getMasterAttendee() == null || !mainConferenceContext.getMasterAttendee().isMeetingJoined()) {
            throw new SystemException(1005454, "主会场未设置或未呼入,无法进行轮询操作！");
        }

        if (conferenceContext != null) {

        }


    }

    @Override
    public void pollingPause(String conferenceId) {
        String cn = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(cn);
        if (conferenceContext != null) {

        }
    }

    @Override
    public void pollingResume(String conferenceId) {
        String cn = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(cn);
        if (conferenceContext != null) {

        }
    }

    /**
     * <pre>取消轮询</pre>
     *
     * @param conferenceId
     * @author lilinhai
     * @since 2021-02-23 16:14
     */
    @Override
    public void cancelPolling(String conferenceId) {
        String cn = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(cn);
        if (conferenceContext != null) {

        }
    }

    @Override
    public JSONObject detail(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        AttendeeSmc3Info ai = new AttendeeSmc3Info(contextKey, attendeeId);
        if (ai.getAttendee() != null && ai.getAttendee().isMeetingJoined()) {
            AttendeeSmc3 attendee = ai.getAttendee();
            String participantId = attendee.getSmcParticipant().getGeneralParam().getId();

            JSONObject jsonObject = subcriberReafInfo(conferenceContext.getSmc3conferenceId(), conferenceContext, participantId);

            if (jsonObject != null) {
                JSONObject d = toDetail(jsonObject, attendee);
                d.put("attendeeId", attendeeId);
                return d;
            }
        }

        return null;
    }

    private JSONObject subcriberReafInfo(String conferenceId, Smc3ConferenceContext smc3ConferenceContext, String participantId) {
        JSONObject jsonObject = null;
        try {
            Smc3Bridge smcBridge = Smc3BridgeCache.getInstance().getBridgesByDept(smc3ConferenceContext.getDeptId());
            SMC3WebsocketClient mwsc = getSmc3WebsocketClient(smcBridge);
            mwsc.sendMessage(TopicMessage.getUNSubscribeMessage("sub-70"));

            String tokenByConferencesId = smcBridge.getSmcConferencesInvoker().getTokenByConferencesId(conferenceId, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            List<String> participantIdList = new ArrayList<>();
            long groupId = System.currentTimeMillis();
            participantIdList.add(participantId);
            smcBridge.getSmcConferencesInvoker().realTimeInfoGroup(conferenceId, groupId + "", participantIdList, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            String destination = "/topic/conferences/" + conferenceId + "/participants/groups/" + groupId + "/realTimeInfo";
            TopicMessage subscribe = new TopicMessage("SUBSCRIBE", "sub-70", tokenByConferencesId, destination);
            String subscribeMessage = subscribe.getSubscribeMessage();
            mwsc.sendMessage(subscribeMessage);

            jsonObject = Smc3WebSocketProcessor.getRealTimeMap().get(participantId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    @Override
    public JSONObject detail(Smc3ConferenceContext conferenceContext, AttendeeSmc3 attendee) {
        if (attendee != null && attendee.isMeetingJoined()) {

        }

        return null;
    }

    public JSONObject toDetail(JSONObject jsonObject, AttendeeSmc3 attendee) {

        SmcParitipantsStateRep.ContentDTO smcParticipant = attendee.getSmcParticipant();

        if (Objects.isNull(jsonObject) || Objects.isNull(smcParticipant)) {
            return null;
        }
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("direction", "outgoing");
        Integer protoType = smcParticipant.getGeneralParam().getType();
        String protoTypeStr = null;
        if (protoType != null) {
            if (protoType == 1) {
                protoTypeStr = "sip";
            } else if (protoType == 0) {
                protoTypeStr = "h323";
            } else {
                protoTypeStr = "sipAndH323";
            }
        }
        jsonObj.put("type", protoTypeStr);
        jsonObj.put("isEncrypted", false);
        jsonObj.put("remoteParty", smcParticipant.getGeneralParam().getUri());
        JSONObject upLink = new JSONObject();
        jsonObj.put("upLink", upLink);
        JSONObject upLinkAudio = new JSONObject();
        JSONArray upLinkVideos = new JSONArray();

        JSONObject downLink = new JSONObject();
        jsonObj.put("downLink", downLink);

        JSONObject downLinkAudio = new JSONObject();
        JSONArray downLinkVideos = new JSONArray();

        upLink.put("videos", upLinkVideos);
        upLink.put("audio", upLinkAudio);
        downLink.put("videos", downLinkVideos);
        downLink.put("audio", downLinkAudio);

        JSONObject realTimeInfo = (JSONObject) jsonObject.get("realTimeInfo");
        //下行信息
        JSONObject receiveRealTimeInfo = (JSONObject) realTimeInfo.get("receiveRealTimeInfo");

        JSONObject rttRealTimeInfo = (JSONObject) realTimeInfo.get("rttRealTimeInfo");
        JSONObject video = new JSONObject();
        video.put("role", Objects.equals(false, receiveRealTimeInfo.get("openAux")) ? "main" : "");
        int videoResolutionCode = (int) receiveRealTimeInfo.get("videoResolution");
        int videoProtocolCode = (int) receiveRealTimeInfo.get("videoProtocol");
        video.put("resolutionRatio", VideoResolutionEnum.getValueByCode(videoResolutionCode).name());
        video.put("frameRate", receiveRealTimeInfo.get("videoFrameRate"));
        video.put("videoCodec", VideoProtocolEnum.getValueByCode(videoProtocolCode).name());
        video.put("bandwidth", receiveRealTimeInfo.get("videoBandWidth"));
        video.put("packetLossPercentage", receiveRealTimeInfo.get("videoLoss"));
        video.put("jitter", receiveRealTimeInfo.get("videoJitter"));
        video.put("roundTripTime", rttRealTimeInfo.get("videoRtt"));
        downLinkVideos.add(video);


        int audioProtocolCode = (int) receiveRealTimeInfo.get("audioProtocol");

        downLinkAudio.put("codec", AudioProtocolEnum.getValueByCode(audioProtocolCode).name());
        downLinkAudio.put("bandwidth", receiveRealTimeInfo.get("audioBandWidth"));
        downLinkAudio.put("packetLossPercentage", receiveRealTimeInfo.get("audioLoss"));
        downLinkAudio.put("codecBitRate", null);
        downLinkAudio.put("jitter", receiveRealTimeInfo.get("audioJitter"));
        downLinkAudio.put("roundTripTime", rttRealTimeInfo.get("audioRtt"));
        downLinkAudio.put("gainApplied", null);

        //上行
        JSONObject sendRealTimeInfo = (JSONObject) realTimeInfo.get("sendRealTimeInfo");
        if (sendRealTimeInfo == null) {
            JSONObject videoSend = new JSONObject();
            video.put("role", Objects.equals(false, sendRealTimeInfo.get("openAux")) ? "main" : "");
            int videoResolutionCode2 = (int) sendRealTimeInfo.get("videoResolution");
            int videoProtocolCode2 = (int) sendRealTimeInfo.get("videoProtocol");
            video.put("resolutionRatio", VideoResolutionEnum.getValueByCode(videoResolutionCode2).name());
            video.put("frameRate", sendRealTimeInfo.get("videoFrameRate"));
            video.put("videoCodec", VideoProtocolEnum.getValueByCode(videoProtocolCode2).name());
            video.put("bandwidth", sendRealTimeInfo.get("videoBandWidth"));
            video.put("packetLossPercentage", sendRealTimeInfo.get("videoLoss"));
            video.put("jitter", sendRealTimeInfo.get("videoJitter"));
            video.put("roundTripTime", rttRealTimeInfo.get("videoRtt"));
            upLinkVideos.add(videoSend);

            int audioProtocolCode2 = (int) sendRealTimeInfo.get("audioProtocol");

            upLinkAudio.put("codec", AudioProtocolEnum.getValueByCode(audioProtocolCode2).name());
            upLinkAudio.put("bandwidth", sendRealTimeInfo.get("audioBandWidth"));
            upLinkAudio.put("packetLossPercentage", sendRealTimeInfo.get("audioLoss"));
            upLinkAudio.put("codecBitRate", null);
            upLinkAudio.put("jitter", sendRealTimeInfo.get("audioJitter"));
            upLinkAudio.put("roundTripTime", rttRealTimeInfo.get("audioRtt"));
            upLinkAudio.put("gainApplied", null);
        }

        return jsonObj;
    }

    /**
     * 与会者呼叫失败通知
     *
     * @param participantUuid void
     * @author lilinhai
     * @since 2021-02-08 13:49
     */
    @Override
    public void callAttendeeFailedNotice(String participantUuid, String reason) {

    }

    /**
     * 批量修改参会者业务参数，支持集群
     *
     * @param conferenceNumber
     * @param attendees
     * @param nameValuePairs   void
     * @author lilinhai
     * @since 2021-04-19 11:45
     */
    private void updateAttendeeAttrs(String conferenceNumber, List<AttendeeSmc3> attendees, List<NameValuePair> nameValuePairs) {
    }


    private SMC3WebsocketClient getSmc3WebsocketClient(Smc3Bridge smcBridge) throws URISyntaxException, InterruptedException {
        SMC3WebsocketClient mwsc = Smc3WebsocketContext.getSmcWebsocketClientMap().get(smcBridge.getIp());
        if (mwsc == null || (!mwsc.getReadyState().equals(ReadyState.OPEN))) {
            String username = smcBridge.getBusiSMC().getMeetingUsername();
            String password = smcBridge.getBusiSMC().getMeetingPassword();
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            String s = "wss://" + smcBridge.getIp() + "/conf-portal/websocket";
            mwsc = new SMC3WebsocketClient(new URI(s), smcBridge.getIp(), "token", "ticket", username, password, smcBridge);
            mwsc.addHeader("Authorization", authHeader);
            mwsc.addHeader("Origin", "https://" + smcBridge.getIp());
            mwsc.connectBlocking();
            Smc3WebsocketContext.getSmcWebsocketClientMap().put(smcBridge.getIp(), mwsc);
        }
        return mwsc;
    }


    @Override
    public void privateTalk(String conferenceId, JSONObject jsonObject) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext cc = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (cc.getMasterAttendee() == null || !cc.getMasterAttendee().isMeetingJoined()) {
            throw new SystemException(1005454, "主会场未设置，无法进行操作！");
        }
        MultiPicInfoTalkReq multiPicInfoTalkReq = JSONObject.parseObject(jsonObject.toJSONString(), MultiPicInfoTalkReq.class);
        MultiPicInfoReq.MultiPicInfoDTO multiPicInfo = multiPicInfoTalkReq.getMultiPicInfo();
        if (multiPicInfo == null) {
            throw new CustomException("画面设置错误");
        }
        List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicList = multiPicInfo.getSubPicList();
        if (CollectionUtils.isEmpty(subPicList)) {
            throw new CustomException("画面设置错误");
        }
        for (MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicList) {

            if (Strings.isBlank(subPicListDTO.getParticipantId())) {
                throw new CustomException("子画面不能为空");
            }
        }


        if (cc != null) {
            cc.setLastAttendeeOperation(cc.getAttendeeOperation());
            TalkPrivateAttendeeOperation privateAttendeeOperation = new TalkPrivateAttendeeOperation(cc, multiPicInfoTalkReq);
            AttendeeOperation attendeeOperation = cc.getAttendeeOperation();
            attendeeOperation.cancel();
            cc.setAttendeeOperation(privateAttendeeOperation);
            privateAttendeeOperation.operate();
        } else {
            throw new SystemException(1005454, "该会议无法进行操作！");
        }
    }

    @Override
    public void cancelPrivateTalk(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();

        if (attendeeOperation instanceof TalkPrivateAttendeeOperation) {
            attendeeOperation.cancel();

            AttendeeOperation lastAttendeeOperation = conferenceContext.getLastAttendeeOperation();
            if (lastAttendeeOperation != null) {
                if (!(lastAttendeeOperation instanceof ChairmanPollingAttendeeOperation || lastAttendeeOperation instanceof PollingAttendeeOperation)) {
                    conferenceContext.setLastAttendeeOperation(attendeeOperation);
                    conferenceContext.setAttendeeOperation(lastAttendeeOperation);
                    lastAttendeeOperation.operate();
                }
            } else {
                ConferenceState.StateDTO.MultiPicInfoDTO multiPicInfo = conferenceContext.getMultiPicInfo();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("multiPicInfo", multiPicInfo);
                jsonObject.put("conferenceId", conferenceContext.getSmc3conferenceId());
                jsonObject.put("broadcast", false);
                conferenceContext.setLastAttendeeOperation(attendeeOperation);
                DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext, jsonObject);
                conferenceContext.setAttendeeOperation(defaultAttendeeOperation);
                defaultAttendeeOperation.operate();
            }
        }
    }

    @Override
    public void openSpeaker(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        String smc3conferenceId = conferenceContext.getSmc3conferenceId();
        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
        AttendeeSmc3 attendeeById = conferenceContext.getAttendeeById(attendeeId);
        if (attendeeById == null) {
            return;
        }
        List<ParticipantStatusDto> participantStatusList = new ArrayList<>();
        ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
        participantStatusDto.setId(attendeeById.getParticipantUuid());
        participantStatusDto.setIsQuiet(false);
        participantStatusList.add(participantStatusDto);
        if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
            bridge.getSmcParticipantsInvoker().PATCHParticipantsOnlyCascade(smc3conferenceId, participantStatusList, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        } else {
            bridge.getSmcParticipantsInvoker().PATCHParticipantsOnly(smc3conferenceId, participantStatusList, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }
    }

    @Override
    public void closeSpeaker(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        String smc3conferenceId = conferenceContext.getSmc3conferenceId();
        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
        AttendeeSmc3 attendeeById = conferenceContext.getAttendeeById(attendeeId);
        if (attendeeById == null) {
            return;
        }
        List<ParticipantStatusDto> participantStatusList = new ArrayList<>();
        ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
        participantStatusDto.setId(attendeeById.getParticipantUuid());
        participantStatusDto.setIsQuiet(true);
        participantStatusList.add(participantStatusDto);
        if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
            bridge.getSmcParticipantsInvoker().PATCHParticipantsOnlyCascade(smc3conferenceId, participantStatusList, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        } else {
            bridge.getSmcParticipantsInvoker().PATCHParticipantsOnly(smc3conferenceId, participantStatusList, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }

    }

    @Override
    public void changeAttendeeName(String conferenceId, JSONObject jsonObject) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        String smc3conferenceId = conferenceContext.getSmc3conferenceId();
        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
        String attendeeId = (String) jsonObject.get("attendeeId");
        String name = (String) jsonObject.get("name");
        AttendeeSmc3 attendeeById = conferenceContext.getAttendeeById(attendeeId);
        if (attendeeById == null) {
            return;
        }
        JSONObject up = new JSONObject();
        JSONObject js = new JSONObject();
        js.put("id", attendeeById.getParticipantUuid());
        js.put("name", name);
        up.put("participantNameInfo", js);

        bridge.getSmcParticipantsInvoker().participantsParam(smc3conferenceId, up, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }


    @Override
    public Object lockPresenter(String conferenceId, String participantId, Boolean lock) {
        Asserts.notNull(conferenceId, "请求参数,会议ID");
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        Asserts.notNull(conferenceContext, "会议不存在");
        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();

        if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
            bridge.getSmcConferencesInvoker().lockPresenterParticipantCascade(conferenceContext.getSmc3conferenceId(), participantId, lock, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        } else {
            bridge.getSmcConferencesInvoker().lockPresenterParticipant(conferenceContext.getSmc3conferenceId(), participantId, lock, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }
        return lock;
    }


    @Override
    public Object videoSwitchAttribute(String conferenceId, String participantId, boolean lock) {
        ParticipantStatus participantStatus = new ParticipantStatus();
        if (lock) {
            participantStatus.setVideoSwitchAttribute("CUSTOMIZED");
        } else {
            participantStatus.setVideoSwitchAttribute("AUTO");
        }

        Asserts.notNull(conferenceId, "请求参数,会议ID");
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        Asserts.notNull(conferenceContext, "会议不存在");
        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
        if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
            bridge.getSmcParticipantsInvoker().conferencesParticipantStatusOnlyCascade(conferenceContext.getSmc3conferenceId(), participantId, participantStatus, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        } else {
            bridge.getSmcParticipantsInvoker().conferencesParticipantStatusOnly(conferenceContext.getSmc3conferenceId(), participantId, participantStatus, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }
        return null;
    }

    @Override
    public Object setVolume(String conferenceId, String participantId, int volume) {
        ParticipantStatus participantStatus = new ParticipantStatus();
        participantStatus.setVolume(volume);

        Asserts.notNull(conferenceId, "请求参数,会议ID");
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        Asserts.notNull(conferenceContext, "会议不存在");
        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
        if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
            bridge.getSmcParticipantsInvoker().conferencesParticipantStatusOnlyCascade(conferenceContext.getSmc3conferenceId(), participantId, participantStatus, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        } else {
            bridge.getSmcParticipantsInvoker().conferencesParticipantStatusOnly(conferenceContext.getSmc3conferenceId(), participantId, participantStatus, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }
        return null;
    }

    @Override
    public void batchInvite(String conferenceId, List<BusiTerminal> attendeeSmc3s) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            List<AttendeeSmc3> attendees = new ArrayList<>();
            for (BusiTerminal busiTerminal_sorce : attendeeSmc3s) {

                TerminalAttendeeSmc3 ta = conferenceContext.getTerminalAttendeeMap().get(busiTerminal_sorce.getId());
                if (ta == null) {

                    ta = AttendeeUtils.packTerminalAttendee(busiTerminal_sorce.getId());

                    Map<String, Object> businessProperties = busiTerminal_sorce.getBusinessProperties();
                    if (businessProperties != null) {
                        if (businessProperties.get("terminalAbility") != null) {
                            Object terminalAbilityoBJ = businessProperties.get("terminalAbility");
                            if (terminalAbilityoBJ instanceof JSONObject) {

                                JSONObject terminalAbilityoBJ1 = (JSONObject) terminalAbilityoBJ;
                                try {
                                    if (terminalAbilityoBJ1.get("audioProtocol") != null && !Objects.equals("自动", terminalAbilityoBJ1.get("audioProtocol"))) {
                                        ta.setAudioProtocol((Integer) terminalAbilityoBJ1.get("audioProtocol"));
                                    }
                                    if (terminalAbilityoBJ1.get("videoProtocol") != null && !Objects.equals("自动", terminalAbilityoBJ1.get("videoProtocol"))) {
                                        ta.setVideoProtocol((Integer) terminalAbilityoBJ1.get("videoProtocol"));
                                    }
                                    if (terminalAbilityoBJ1.get("videoResolution") != null && !Objects.equals("自动", terminalAbilityoBJ1.get("videoResolution"))) {
                                        ta.setVideoResolution((Integer) terminalAbilityoBJ1.get("videoResolution"));
                                    }
                                    if (terminalAbilityoBJ1.get("dialMode") != null && !Objects.equals("自动", terminalAbilityoBJ1.get("dialMode"))) {
                                        ta.setDialMode(DialMode.valueOf(terminalAbilityoBJ1.get("dialMode") + ""));
                                    }
                                    if (terminalAbilityoBJ1.get("rate") != null && !Objects.equals("自动", terminalAbilityoBJ1.get("rate"))) {
                                        ta.setRate((Integer) terminalAbilityoBJ1.get("rate"));
                                    }

                                    if (terminalAbilityoBJ1.get("dtmfInfo") != null && !Objects.equals("自动", terminalAbilityoBJ1.get("dtmfInfo"))) {
                                        ta.setDtmfInfo((String) terminalAbilityoBJ1.get("dtmfInfo"));
                                    }
                                    if (terminalAbilityoBJ1.get("serviceZoneId") != null && !Objects.equals("自动", terminalAbilityoBJ1.get("serviceZoneId"))) {
                                        ta.setServiceZoneId((String) terminalAbilityoBJ1.get("serviceZoneId"));
                                    }
                                } catch (IllegalArgumentException e) {
                                    logger.info("编辑能力错误" + e.getMessage());
                                }
                            }
                        }
                    }

                    ta.setConferenceNumber(conferenceContext.getConferenceNumber());
                    ta.setDeptId(conferenceContext.getDeptId());
                    BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(busiTerminal_sorce.getId());
                    if (busiUserTerminal != null) {
                        ta.setUserId(busiUserTerminal.getUserId());
                    }
                    conferenceContext.addAttendee(ta);
                    attendees.add(ta);
                    Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, ta);
                    Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + ta.getName() + "】被邀请加入");

                    mqttService.sendConferenceInfoToPushTargetTerminal(conferenceContext);

                    Smc3MeetingRoomRegTask smc3MeetingRoomRegTask = new Smc3MeetingRoomRegTask(ta.getId(), ta.getRemoteParty(), 10, busiTerminal_sorce.getDeptId(), busiTerminal_sorce);
                    smc3delayTaskService.addTask(smc3MeetingRoomRegTask);
                }

            }

            InviteAttendeeSmc3Task inviteAttendeesTask = new InviteAttendeeSmc3Task(contextKey, 100, conferenceContext, attendees);
            smc3delayTaskService.addTask(inviteAttendeesTask);
        }

    }
}
