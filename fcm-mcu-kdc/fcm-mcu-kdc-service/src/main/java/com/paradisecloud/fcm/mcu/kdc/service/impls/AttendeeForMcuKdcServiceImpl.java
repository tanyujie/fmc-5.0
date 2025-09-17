/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.service.impls
 * @author lilinhai 
 * @since 2021-02-05 17:39
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.kdc.service.impls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiMcuKdcTemplatePollingSchemeMapper;
import com.paradisecloud.fcm.dao.mapper.BusiUserTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuKdcTemplatePollingScheme;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.BusiUserTerminal;
import com.paradisecloud.fcm.mcu.kdc.attendee.model.operation.*;
import com.paradisecloud.fcm.mcu.kdc.attendee.model.polling.PollingScheme;
import com.paradisecloud.fcm.mcu.kdc.attendee.utils.AttendeeUtils;
import com.paradisecloud.fcm.mcu.kdc.attendee.utils.McuKdcConferenceContextUtils;
import com.paradisecloud.fcm.mcu.kdc.cache.AttendeeCountingStatistics;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcConferenceContextCache;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.AttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.InvitedAttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.TerminalAttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.mcu.kdc.model.core.McuKdcAttendeeInfo;
import com.paradisecloud.fcm.mcu.kdc.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.mcu.kdc.model.request.cc.*;
import com.paradisecloud.fcm.mcu.kdc.model.response.cc.*;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IAttendeeForMcuKdcService;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcTemplatePollingSchemeService;
import com.paradisecloud.fcm.mcu.kdc.task.*;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.attendee.BaseFixedParamValue;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <pre>界面上参会者业务处理类</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-05 17:39
 */
@Transactional
@Service
public class AttendeeForMcuKdcServiceImpl implements IAttendeeForMcuKdcService {

    public static final String LAYOUT_TEMPLATE = "layoutTemplate";
    public static final String DEFAULT_LAYOUT = "defaultLayout";
    public static final String CHOSEN_LAYOUT = "chosenLayout";
    public static final String AUTOMATIC = "automatic";
    private Logger logger = LoggerFactory.getLogger(AttendeeForMcuKdcServiceImpl.class);

    @Resource
    private BusiMcuKdcTemplatePollingSchemeMapper busiMcuKdcTemplatePollingSchemeMapper;

    @Resource
    private IBusiMcuKdcTemplatePollingSchemeService busiMcuKdcTemplatePollingSchemeService;
    
    @Resource
    private IMqttService mqttService;

    @Resource
    private McuKdcDelayTaskService mcuKdcDelayTaskService;

    @Resource
    private RedisCache redisCache;

    @Resource
    private BusiUserTerminalMapper busiUserTerminalMapper;

    private boolean isContains(AttendeeForMcuKdc a, AttendeeForMcuKdc... excludes)
    {
        if (!ObjectUtils.isEmpty(excludes))
        {
            for (AttendeeForMcuKdc attendee : excludes)
            {
                if (a == attendee)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateAttendeeImportance(AttendeeForMcuKdc attendee, AttendeeImportance attendeeImportance)
    {
        if (attendee != null && attendee.isMeetingJoined())
        {
            List<String> participantIds = new ArrayList<>();
            if (!attendeeImportance.is(attendee.getImportance()))
            {
                participantIds.add(attendee.getParticipantUuid());
            }

            if (!participantIds.isEmpty())
            {
//                ParticipantParamBuilder participantParamBuilder = new ParticipantParamBuilder();
//                participantParamBuilder.importance(attendeeImportance.getStartValue());
//                updateAttendeeAttrs(attendee.getDeptId(), attendee.getConferenceNumber(), participantParamBuilder.build(), ParticipantBulkOperationMode.SELECTED, participantIds.toArray(new String[participantIds.size()]));
            }
        }
    }
    
    @Override
    public void presentationSetting(String conferenceId, String attendeeId, List<BaseFixedParamValue> params)
    {
        // 辅流分辨率qualityPresentation: unset（空字符串）|unrestricted|max1080p30|max720p5
        // 开启辅流presentationContributionAllowed：unset（空字符串）|true|false
        // 接收双流presentationViewingAllowed: unset（空字符串）|true|false
        // 分享双流sipPresentationChannelEnabled: unset（空字符串）|true|false
        // 辅流模式bfcpMode: unset（空字符串）|serverOnly(客户端模式)|serverAndClient(服务器模式)
        String[] paramNames = {"qualityPresentation", "presentationContributionAllowed", "presentationViewingAllowed", "sipPresentationChannelEnabled", "bfcpMode"};
//        processCallLegUpdate(conferenceId, attendeeId, params, paramNames, null);
    }

    @Override
    public void mainSetting(String conferenceId, String attendeeId, List<BaseFixedParamValue> params)
    {
        // 主流分辨率qualityMain: unset（空字符串）|unrestricted|max1080p30|max720p30|max480p30
        // 显示画面txVideoMute（空字符串）|true|false
        // 关闭镜头 rxVideoMute: unset（空字符串）|true|false
        // 远端镜头控制controlRemoteCameraAllowed: unset（空字符串）|true|false
        String[] paramNames = {"qualityMain", "txAudioMute", "txVideoMute", "rxVideoMute", "controlRemoteCameraAllowed"};
//        processCallLegUpdate(conferenceId, attendeeId, params, paramNames, (Map<String, FixedParamValue> fpvMap, FmeBridge fmeBridge, Participant participant) ->
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
    
    public void subtitle(String conferenceId, String attendeeId, List<BaseFixedParamValue> params)
    {
        // 与会者名称nameLabelOverride: 任意字符串
        // 会议抬头位置 meetingTitlePosition：unset（空字符串）|disabled|top|middle|bottom
        // 显示与会者名称participantLabels: unset（空字符串）|true|false
        String[] paramNames = {"nameLabelOverride", "meetingTitlePosition", "participantLabels"};
//        processCallLegUpdate(conferenceId, attendeeId, params, paramNames, null);
    }

    @Override
    public void layoutSetting(String conferenceId, String attendeeId, List<BaseFixedParamValue> params)
    {
        String[] paramNames = {"chosenLayout", "defaultLayout"};
//        processCallLegUpdate(conferenceId, attendeeId, params, paramNames, null);
    }
    
    
    @Override
    public void recordStreamSetting(String conferenceId, String attendeeId, List<BaseFixedParamValue> params)
    {
        // 1. 布局设置要同时传两个参数chosenLayout和defaultLayout，传的值相同
        // 2. 主流分辨率: qualityMain  unset（空字符串）|unrestricted|max1080p30|max720p30|max480p30
        // 3. 是否录制辅流: presentationViewingAllowed unset（空字符串）|true|false
        String[] paramNames = {"chosenLayout", "defaultLayout", "qualityMain", "presentationViewingAllowed"};
//        processCallLegUpdate(conferenceId, attendeeId, params, paramNames, null);
    }

    @Override
    public void advanceSetting(String conferenceId, String attendeeId, List<BaseFixedParamValue> params)
    {
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
    public JSONObject attendeeCallLegSetting(String conferenceId, String attendeeId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
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
    public void recall(String conferenceId, String attendeeId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeForMcuKdc attendee = conferenceContext.getAttendeeById(attendeeId);
            if (attendee != null) {
                BusiTerminal terminal = TerminalCache.getInstance().getBySn(attendee.getSn());
                if (terminal != null) {
                    if (TerminalType.isFCMSIP(terminal.getType())) {
                        BeanFactory.getBean(IMqttService.class).inviteAttendeeJoinConference(attendee, conferenceContext, AttendType.AUTO_JOIN.getValue());
                        return;
                    }
                }
                InviteAttendeesTask inviteAttendeesTask = new InviteAttendeesTask(conferenceContext.getId(), 100, conferenceContext, attendee);
                mcuKdcDelayTaskService.addTask(inviteAttendeesTask);
            }
            if (attendee.isMcuAttendee()) {
                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(attendee.getCascadeConferenceId()));
                if (baseConferenceContext != null) {
                    baseConferenceContext.setUpCascadeRemoteParty(conferenceContext.getConferenceRemoteParty());
                }
            }
        }
    }
    
    public void callAttendee(AttendeeForMcuKdc attendee)
    {
//        new CallAttendeeProcessor(attendee).process();
    }
    
    @Override
    public void hangUp(String conferenceId, String attendeeId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeForMcuKdc attendeeForMcuKdc = conferenceContext.getAttendeeById(attendeeId);
            if (attendeeForMcuKdc != null) {
                if (StringUtils.hasText(attendeeForMcuKdc.getParticipantUuid())) {

                    if (conferenceContext.getAttendeeOperation() instanceof RollCallAttendeeOperation) {
                        if (attendeeForMcuKdc.getCallTheRollStatus() == AttendeeCallTheRollStatus.YES.getValue()) {
                            cancelCallTheRoll(conferenceId);
                            if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                                DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) conferenceContext.getAttendeeOperation();
                                defaultAttendeeOperation.operate();
                            }
                        }
                    } else if (conferenceContext.getAttendeeOperation() instanceof TalkAttendeeOperation) {
                        if (attendeeForMcuKdc.getTalkStatus() == AttendeeTalkStatus.YES.getValue()) {
                            cancelTalk(conferenceId);
                            if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                                DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) conferenceContext.getAttendeeOperation();
                                defaultAttendeeOperation.operate();
                            }
                        }
                    } else if (conferenceContext.getAttendeeOperation() instanceof ChooseSeeAttendeeOperation) {
                        if (attendeeForMcuKdc.getChooseSeeStatus() == AttendeeChooseSeeStatus.YES.getValue()) {
                            cancelChooseSee(conferenceId);
                            if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                                DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) conferenceContext.getAttendeeOperation();
                                defaultAttendeeOperation.operate();
                            }
                        }
                    }

                    CcHangUpMrTerminalRequest ccHangUpMrTerminalRequest = new CcHangUpMrTerminalRequest();
                    ccHangUpMrTerminalRequest.setConf_id(conferenceContext.getConfId());
                    List<CcHangUpMrTerminalRequest.Mt> mts = new ArrayList<>();
                    {
                        CcHangUpMrTerminalRequest.Mt mt = new CcHangUpMrTerminalRequest.Mt();
                        mt.setMt_id(attendeeForMcuKdc.getParticipantUuid());
                        mts.add(mt);
                    }
                    ccHangUpMrTerminalRequest.setMts(mts);
                    CcHangUpMrTerminalResponse ccHangUpMrTerminalResponse = conferenceContext.getConferenceControlApi().hangUpMrTerminal(ccHangUpMrTerminalRequest);
                    if (ccHangUpMrTerminalResponse != null && ccHangUpMrTerminalResponse.isSuccess()) {
                    }
                    attendeeForMcuKdc.setHangUp(true);
                }
            }
        }
    }
    
    @Override
    public void remove(String conferenceId, String attendeeId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeForMcuKdc attendeeForMcuKdc = conferenceContext.getAttendeeById(attendeeId);
            if (attendeeForMcuKdc != null) {
                if (StringUtils.hasText(attendeeForMcuKdc.getParticipantUuid())) {

                    if (conferenceContext.getAttendeeOperation() instanceof RollCallAttendeeOperation) {
                        if (attendeeForMcuKdc.getCallTheRollStatus() == AttendeeCallTheRollStatus.YES.getValue()) {
                            cancelCallTheRoll(conferenceId);
                            if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                                DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) conferenceContext.getAttendeeOperation();
                                defaultAttendeeOperation.operate();
                            }
                        }
                    } else if (conferenceContext.getAttendeeOperation() instanceof TalkAttendeeOperation) {
                        if (attendeeForMcuKdc.getTalkStatus() == AttendeeTalkStatus.YES.getValue()) {
                            cancelTalk(conferenceId);
                            if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                                DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) conferenceContext.getAttendeeOperation();
                                defaultAttendeeOperation.operate();
                            }
                        }
                    } else if (conferenceContext.getAttendeeOperation() instanceof ChooseSeeAttendeeOperation) {
                        if (attendeeForMcuKdc.getChooseSeeStatus() == AttendeeChooseSeeStatus.YES.getValue()) {
                            cancelChooseSee(conferenceId);
                            if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                                DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) conferenceContext.getAttendeeOperation();
                                defaultAttendeeOperation.operate();
                            }
                        }
                    }

                    CcHangUpMrTerminalRequest ccHangUpMrTerminalRequest = new CcHangUpMrTerminalRequest();
                    ccHangUpMrTerminalRequest.setConf_id(conferenceContext.getConfId());
                    List<CcHangUpMrTerminalRequest.Mt> mts = new ArrayList<>();
                    {
                        CcHangUpMrTerminalRequest.Mt mt = new CcHangUpMrTerminalRequest.Mt();
                        mt.setMt_id(attendeeForMcuKdc.getParticipantUuid());
                        mts.add(mt);
                    }
                    ccHangUpMrTerminalRequest.setMts(mts);
                    CcHangUpMrTerminalResponse ccHangUpMrTerminalResponse = conferenceContext.getConferenceControlApi().hangUpMrTerminal(ccHangUpMrTerminalRequest);
                    if (ccHangUpMrTerminalResponse != null && ccHangUpMrTerminalResponse.isSuccess()) {
                        String uuid = conferenceContext.getDisconnectedParticipantUuidByRemoteParty(attendeeForMcuKdc.getRemoteParty());
                        if (StringUtils.hasText(uuid) && !uuid.equals(attendeeForMcuKdc.getParticipantUuid())) {
                            CcDeleteMrTerminalRequest ccDeleteMrTerminalRequest = new CcDeleteMrTerminalRequest();
                            ccDeleteMrTerminalRequest.setConf_id(conferenceContext.getConfId());
                            List<CcDeleteMrTerminalRequest.Mt> mtsDelete = new ArrayList<>();
                            {
                                CcDeleteMrTerminalRequest.Mt mt = new CcDeleteMrTerminalRequest.Mt();
                                mt.setMt_id(attendeeForMcuKdc.getParticipantUuid());
                                mtsDelete.add(mt);
                            }
                            ccDeleteMrTerminalRequest.setMts(mtsDelete);
                            CcDeleteMrTerminalResponse ccDeleteMrTerminalResponse = conferenceContext.getConferenceControlApi().deleteMrTerminal(ccDeleteMrTerminalRequest);
                            if (ccDeleteMrTerminalResponse != null && ccDeleteMrTerminalResponse.isSuccess()) {
                            }
                        }
                        attendeeForMcuKdc.setMeetingStatus(AttendeeMeetingStatus.OUT.getValue());
                    }
                }
                if (attendeeForMcuKdc.getMeetingStatus() == AttendeeMeetingStatus.OUT.getValue()) {
                    conferenceContext.removeAttendeeById(attendeeId);

                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("id", attendeeForMcuKdc.getId());
                    updateMap.put("deptId", attendeeForMcuKdc.getDeptId());
                    updateMap.put("mcuAttendee", attendeeForMcuKdc.isMcuAttendee());
                    McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                    McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                }
            }
        }
    }
    
    @Override
    public String takeSnapshot(String conferenceId, String attendeeId, JSONObject params)
    {
        String direction = "rx";
        if (params.containsKey("direction"))
        {
            direction = params.getString("direction");
            Assert.isTrue(direction.equals("rx") || direction.equals("tx"), "direction 只能为rx或tx");
        }
        
        String maxWidth = "640";
        if (params.containsKey("maxWidth"))
        {
            maxWidth = params.getString("maxWidth");
            Assert.isTrue(maxWidth.matches("^\\d+$"), "maxWidth只能为正整数！");
        }
        
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
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
     * @author sinhy
     * @since 2021-08-18 13:55 
     * @param conferenceId
     * @param attendeeId
     * @param params
     */
    @Override
    public void cameraControl(String conferenceId, String attendeeId, JSONObject params)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuKdcAttendeeInfo ai = new McuKdcAttendeeInfo(contextKey, attendeeId);
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        AttendeeForMcuKdc attendeeForMcuKdc = conferenceContext.getAttendeeById(attendeeId);
        if (attendeeForMcuKdc != null) {
            if (StringUtils.hasText(attendeeForMcuKdc.getParticipantUuid())) {
                String movement = "";
                Collection<Object> values = params.values();
                for (Object value : values) {
                    String action = value.toString();
                    movement = action;
                }
                if (System.currentTimeMillis() - attendeeForMcuKdc.getLastControlCameraTime() > 500  || !movement.equals(attendeeForMcuKdc.getLastControlCameraMove())) {
                    if (StringUtils.hasText(attendeeForMcuKdc.getLastControlCameraMove()) && !movement.equals(attendeeForMcuKdc.getLastControlCameraMove())) {
                        CcCameraControlRequest ccCameraControlRequest = new CcCameraControlRequest();
                        ccCameraControlRequest.setConf_id(conferenceContext.getConfId());
                        ccCameraControlRequest.setState(CcCameraControlRequest.state_stop);
                        ccCameraControlRequest.setMt_id(attendeeForMcuKdc.getParticipantUuid());
                        ccCameraControlRequest.setType(CcCameraControlRequest.type_down);
                        conferenceContext.getConferenceControlApi().controlCamera(ccCameraControlRequest);
                        attendeeForMcuKdc.setLastControlCameraTime(0);
                        attendeeForMcuKdc.setLastControlCameraMove(null);
                        return;
                    }
                    attendeeForMcuKdc.setLastControlCameraMove(movement);
                    attendeeForMcuKdc.setLastControlCameraTime(System.currentTimeMillis());
                    CcCameraControlRequest ccCameraControlRequest = new CcCameraControlRequest();
                    ccCameraControlRequest.setConf_id(conferenceContext.getConfId());
                    if (attendeeForMcuKdc.getLastControlCameraTime() == 0) {
                        ccCameraControlRequest.setState(CcCameraControlRequest.state_start);
                    } else {
                        ccCameraControlRequest.setState(CcCameraControlRequest.state_start);
                    }
                    ccCameraControlRequest.setType(CcCameraControlRequest.convertToType(movement));
                    ccCameraControlRequest.setMt_id(attendeeForMcuKdc.getParticipantUuid());
                    CcCameraControlResponse ccCameraControlResponse = conferenceContext.getConferenceControlApi().controlCamera(ccCameraControlRequest);
                    if (ccCameraControlResponse != null && ccCameraControlResponse.isSuccess()) {
                        StopControlCameraTask stopControlCameraTask = new StopControlCameraTask(conferenceContext.getId() + "_" + attendeeForMcuKdc.getParticipantUuid(), 1000, conferenceContext, attendeeForMcuKdc, movement);
                        mcuKdcDelayTaskService.addTask(stopControlCameraTask);
                        McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(ai.getConferenceContext(), WebsocketMessageType.MESSAGE_TIP, "【" + ai.getAttendee().getName() + "】镜头调整成功！");
                    }
                }
            }
        }
    }

    /**
     * 变更主会场
     * @author lilinhai
     * @since 2021-02-09 11:32  void
     */
    public void changeMaster(String conferenceId, String attendeeId)
    {
        if (StringUtil.isEmpty(attendeeId)) {
            return;
        }
        McuKdcConferenceContext mcuKdcConferenceContext = McuKdcConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId));
        AttendeeForMcuKdc attendeeForMcuKdc = mcuKdcConferenceContext.getAttendeeById(attendeeId);
        if (attendeeForMcuKdc != null) {
            if (mcuKdcConferenceContext.isDownCascadeConference()) {
                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(mcuKdcConferenceContext.getUpCascadeConferenceId()));
                if (baseConferenceContext != null) {
                    if (!attendeeForMcuKdc.getRemoteParty().equals(mcuKdcConferenceContext.getUpCascadeRemoteParty())) {
                        throw new SystemException(1005454, "该会议正被级联，不允许修改主会场！");
                    }
                }
            }
            AttendeeForMcuKdc oldMasterAttendee = mcuKdcConferenceContext.getMasterAttendee();
            if (oldMasterAttendee == null || !attendeeForMcuKdc.getId().equals(oldMasterAttendee.getId())) {
                mcuKdcConferenceContext.setMasterAttendee(attendeeForMcuKdc);
                redisCache.setCacheObject(mcuKdcConferenceContext.getId() + "_" + "_master_attendee", attendeeForMcuKdc.getId(), 48, TimeUnit.HOURS);
                if (mcuKdcConferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                    mcuKdcConferenceContext.setLastAttendeeOperation(mcuKdcConferenceContext.getAttendeeOperation());
                }
                AttendeeOperation old = mcuKdcConferenceContext.getAttendeeOperation();
                old.cancel();
                AttendeeOperation attendeeOperation = new ChangeMasterAttendeeOperation(mcuKdcConferenceContext);
                mcuKdcConferenceContext.setAttendeeOperation(attendeeOperation);

                StringBuilder messageTip = new StringBuilder();
                messageTip.append("主会场已切换至【").append(attendeeForMcuKdc.getName()).append("】");
                Map<String, Object> data = new HashMap<>();
                data.put("oldMasterAttendee", oldMasterAttendee);
                data.put("newMasterAttendee", attendeeForMcuKdc);
                McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(mcuKdcConferenceContext, WebsocketMessageType.MASTER_CHANGED, data);
                McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(mcuKdcConferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
            }
        }
    }
    
    /**
     * 选看
     * @author lilinhai
     * @since 2021-02-09 11:32  void
     */
    public void chooseSee(String conferenceId, String attendeeId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuKdcConferenceContext mainConferenceContext = McuKdcConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue())
        {
            throw new SystemException(1005454, "主会场未设置，无法进行选看操作！");
        }

        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeForMcuKdc chooseSeeAttendee = conferenceContext.getAttendeeById(attendeeId);
            if (chooseSeeAttendee != null) {
                if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                    conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
                }
                AttendeeOperation old = conferenceContext.getAttendeeOperation();
                old.cancel();
                AttendeeOperation attendeeOperation = new ChooseSeeAttendeeOperation(conferenceContext, chooseSeeAttendee);
                conferenceContext.setAttendeeOperation(attendeeOperation);
            }
        }
    }

    /**
     * 选看
     * @author lilinhai
     * @since 2021-02-09 11:32  void
     */
    public void chooseSee(String conferenceId, String attendeeId, boolean upCascadeOperate, boolean upCascadeBroadcast, boolean upCascadePolling, boolean upCascadeRollCall)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuKdcConferenceContext mainConferenceContext = McuKdcConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue())
        {
            throw new SystemException(1005454, "主会场未设置，无法进行选看操作！");
        }

        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeForMcuKdc chooseSeeAttendee = conferenceContext.getAttendeeById(attendeeId);
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
            }
        }
    }
    
    /**
     * 默认选看（主会场切换时执行）
     * @author lilinhai
     * @since 2021-02-09 11:32  void
     */
    public void defaultChooseSee(McuKdcConferenceContext mainConferenceContext)
    {
        if (mainConferenceContext.getMasterAttendee() == null || !mainConferenceContext.getMasterAttendee().isMeetingJoined())
        {
            return;
        }
        
        // 只有当操作是默认的选看时，才会执行该逻辑
        AttendeeForMcuKdc a = McuKdcConferenceContextUtils.getDefaultChooseToSee(mainConferenceContext);
        if (a != null)
        {
            logger.info("已找到默认选看参会, ConferenceNumber：" + mainConferenceContext.getConferenceNumber() + ", Attendee: " + a.getName());
//            mainConferenceContext.setLastAttendeeOperation(mainConferenceContext.getAttendeeOperation());
//
//            AttendeeInfo ai = new AttendeeInfo(mainConferenceContext.getConferenceNumber(), a.getId());
//            SplitScreen ss = new OneSplitScreen(AttendeeImportance.CHOOSE_SEE.getStartValue());
//
//            // 执行选看逻辑
//            AttendeeOperation ao = new DefaultChooseToSeeAttendeeOperation(mainConferenceContext, ss, Arrays.asList(ai.getAttendee()));
//            mainConferenceContext.setAttendeeOperation(ao);
//            mainConferenceContext.getLastAttendeeOperation().cancel(ao);
        }
        else
        {
            logger.info("未找到默认选看参会, ConferenceNumber：" + mainConferenceContext.getConferenceNumber());
        }
    }
    
    /**
     * <pre>点名</pre>
     * @author lilinhai
     * @since 2021-02-22 18:07 
     * @param conferenceId
     * @param attendeeId
     */
    @Override
    public void callTheRoll(String conferenceId, String attendeeId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuKdcConferenceContext mainConferenceContext = McuKdcConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue())
        {
            throw new SystemException(1005454, "主会场未设置，无法进行点名操作！");
        }

        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeForMcuKdc rollCallAttendee = conferenceContext.getAttendeeById(attendeeId);
            if (rollCallAttendee != null) {
                if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                    conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
                }
                AttendeeOperation old = conferenceContext.getAttendeeOperation();
                old.cancel();
                AttendeeOperation attendeeOperation = new RollCallAttendeeOperation(conferenceContext, rollCallAttendee);
                conferenceContext.setAttendeeOperation(attendeeOperation);
            }
        } else {
            throw new SystemException(1005454, "该会议无法进行点名操作！");
        }
    }
    
    /**
     * 对话
     * @author sinhy
     * @since 2021-12-02 12:47 
     * @param conferenceId
     * @param attendeeId
     */
    @Override
    public void talk(String conferenceId, String attendeeId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuKdcConferenceContext mainConferenceContext = McuKdcConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue())
        {
            throw new SystemException(1005452, "主会场未设置，无法进行对话操作！");
        }
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeForMcuKdc talkAttendee = conferenceContext.getAttendeeById(attendeeId);
            if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
            }
            AttendeeOperation old = conferenceContext.getAttendeeOperation();
            if (old instanceof TalkAttendeeOperation) {
                TalkAttendeeOperation talkAttendeeOperation = (TalkAttendeeOperation) old;
                if (talkAttendeeOperation.getTalkUuid().equals(talkAttendee.getParticipantUuid())) {
                    return;
                }
            }
            old.cancel();
            AttendeeOperation attendeeOperation = new TalkAttendeeOperation(conferenceContext, talkAttendee);
            conferenceContext.setAttendeeOperation(attendeeOperation);
        }
    }
    
    /**
     * <pre>取消点名</pre>
     * @author lilinhai
     * @since 2021-02-23 16:14 
     * @param conferenceId
     */
    @Override
    public void cancelCallTheRoll(String conferenceId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            if (attendeeOperation instanceof RollCallAttendeeOperation) {
                attendeeOperation.cancel();
                conferenceContext.setAttendeeOperation(conferenceContext.getLastAttendeeOperation());
            }
        }
    }

    /**
     * 取消对话
     * @param conferenceId
     */
    @Override
    public void cancelTalk(String conferenceId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            if (attendeeOperation instanceof TalkAttendeeOperation) {
                attendeeOperation.cancel();
                conferenceContext.setAttendeeOperation(conferenceContext.getLastAttendeeOperation());
            }
        }
    }

    /**
     * 取消选看
     * @param conferenceId
     */
    @Override
    public void cancelChooseSee(String conferenceId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            if (attendeeOperation instanceof ChooseSeeAttendeeOperation) {
                attendeeOperation.cancel();
                conferenceContext.setAttendeeOperation(conferenceContext.getLastAttendeeOperation());
            }
        }
    }
    
    /**
     * <pre>取消当前操作</pre>
     * @author lilinhai
     * @since 2021-02-23 16:14 
     * @param conferenceContext
     */
    public void cancelCurrentOperation(McuKdcConferenceContext conferenceContext)
    {
        try
        {
            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            if (attendeeOperation != null)
            {
                if (!(attendeeOperation instanceof DefaultAttendeeOperation)) {
                    AttendeeOperation defaultViewOperation = conferenceContext.getDefaultViewOperation();
                    conferenceContext.setAttendeeOperation(defaultViewOperation);
                    attendeeOperation.cancel();
                }
            }
        }
        catch (Throwable e)
        {
            logger.error("cancelCallTheRoll error", e);
        }
    }

    /**
     * <pre>混音</pre>
     * @author lilinhai
     * @since 2021-02-22 18:07 
     * @param conferenceId
     * @param attendeeId
     */
    public void openMixing(String conferenceId, String attendeeId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        logger.info("开启单个参会者混音入口：" + contextKey + ", " + attendeeId);
        McuKdcConferenceContext mcuKdcConferenceContext = McuKdcConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId));
        AttendeeForMcuKdc attendeeForMcuKdc = mcuKdcConferenceContext.getAttendeeById(attendeeId);
        if (attendeeForMcuKdc != null && StringUtils.hasText(attendeeForMcuKdc.getParticipantUuid())) {
            if (mcuKdcConferenceContext.isDiscuss()) {
                CcTerminalForceMuteRequest ccTerminalForceMuteRequest = new CcTerminalForceMuteRequest();
                ccTerminalForceMuteRequest.setConf_id(mcuKdcConferenceContext.getConfId());
                ccTerminalForceMuteRequest.setMt_id(attendeeForMcuKdc.getParticipantUuid());
                ccTerminalForceMuteRequest.setValue(0);
                CcTerminalForceMuteResponse ccTerminalForceMuteResponse = mcuKdcConferenceContext.getConferenceControlApi().terminalForceMute(ccTerminalForceMuteRequest);
                if (ccTerminalForceMuteResponse != null && ccTerminalForceMuteResponse.isSuccess()) {
                    attendeeForMcuKdc.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                    McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(mcuKdcConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuKdc.getUpdateMap());
                }
            } else {
                if (mcuKdcConferenceContext.isMuteParties()) {
                    CcStartMrMixingRequest ccStartMrMixingRequest = new CcStartMrMixingRequest();
                    ccStartMrMixingRequest.setConf_id(mcuKdcConferenceContext.getConfId());
                    ccStartMrMixingRequest.setMode(2);
                    List<CcStartMrMixingRequest.Member> members = new ArrayList<>();
                    {
                        CcStartMrMixingRequest.Member member = new CcStartMrMixingRequest.Member();
                        member.setMt_id(attendeeForMcuKdc.getParticipantUuid());
                        members.add(member);
                    }
                    ccStartMrMixingRequest.setMembers(members);
                    CcStartMrMixingResponse ccStartMrMixingResponse = mcuKdcConferenceContext.getConferenceControlApi().startMrMixing(ccStartMrMixingRequest);
                    if (ccStartMrMixingResponse != null && ccStartMrMixingResponse.isSuccess()) {
                        mcuKdcConferenceContext.setMuteParties(false);
                        CcTerminalForceMuteRequest ccTerminalForceMuteRequest = new CcTerminalForceMuteRequest();
                        ccTerminalForceMuteRequest.setConf_id(mcuKdcConferenceContext.getConfId());
                        ccTerminalForceMuteRequest.setMt_id(attendeeForMcuKdc.getParticipantUuid());
                        ccTerminalForceMuteRequest.setValue(0);
                        CcTerminalForceMuteResponse ccTerminalForceMuteResponse = mcuKdcConferenceContext.getConferenceControlApi().terminalForceMute(ccTerminalForceMuteRequest);
                        if (ccTerminalForceMuteResponse != null && ccTerminalForceMuteResponse.isSuccess()) {
                            attendeeForMcuKdc.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                            McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(mcuKdcConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuKdc.getUpdateMap());
                        }
                    }
                } else {
                    CcAddMixingTerminalRequest ccAddMixingTerminalRequest = new CcAddMixingTerminalRequest();
                    ccAddMixingTerminalRequest.setConf_id(mcuKdcConferenceContext.getConfId());
                    ccAddMixingTerminalRequest.setMix_id("1");
                    List<CcAddMixingTerminalRequest.Mt> members = new ArrayList<>();
                    {
                        CcAddMixingTerminalRequest.Mt mt = new CcAddMixingTerminalRequest.Mt();
                        mt.setMt_id(attendeeForMcuKdc.getParticipantUuid());
                        members.add(mt);
                    }
                    ccAddMixingTerminalRequest.setMembers(members);
                    CcAddMixingTerminalResponse ccAddMixingTerminalResponse = mcuKdcConferenceContext.getConferenceControlApi().addMixingTerminal(ccAddMixingTerminalRequest);
                    if (ccAddMixingTerminalResponse != null && ccAddMixingTerminalResponse.isSuccess()) {
                        CcTerminalForceMuteRequest ccTerminalForceMuteRequest = new CcTerminalForceMuteRequest();
                        ccTerminalForceMuteRequest.setConf_id(mcuKdcConferenceContext.getConfId());
                        ccTerminalForceMuteRequest.setMt_id(attendeeForMcuKdc.getParticipantUuid());
                        ccTerminalForceMuteRequest.setValue(0);
                        CcTerminalForceMuteResponse ccTerminalForceMuteResponse = mcuKdcConferenceContext.getConferenceControlApi().terminalForceMute(ccTerminalForceMuteRequest);
                        if (ccTerminalForceMuteResponse != null && ccTerminalForceMuteResponse.isSuccess()) {
                            attendeeForMcuKdc.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                            McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(mcuKdcConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuKdc.getUpdateMap());
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 接受举手
     * @author sinhy
     * @since 2021-12-07 10:27 
     * @param conferenceId
     * @param attendeeId
     */
    public void acceptRaiseHand(String conferenceId, String attendeeId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuKdcConferenceContext mainConferenceContext = McuKdcConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue())
        {
            throw new SystemException(1005454, "主会场未设置，无法进行举手操作！");
        }
        if (mainConferenceContext.getMasterAttendee().getId() == attendeeId) {
            throw new SystemException(1005454, "该参会者是主会场，无法进行举手操作！");
        }

        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeForMcuKdc talkAttendee = conferenceContext.getAttendeeById(attendeeId);
            if (talkAttendee != null) {
                if (!talkAttendee.isMeetingJoined()) {
                    throw new SystemException(1005454, "该参会者已离会，无法进行举手操作！");
                }
            } else {
                throw new SystemException(1005454, "该参会者已离会，无法进行举手操作！");
            }
        } else {
            throw new SystemException(1005454, "该会议无法进行举手操作！");
        }

        // 对话
        talk(conferenceId, attendeeId);

        // 关闭举手
        AttendeeForMcuKdc raiseHandsAttendee = conferenceContext.getAttendeeById(attendeeId);
        RaiseHandStatus raiseHandStatus = RaiseHandStatus.NO;
        raiseHandsAttendee.resetUpdateMap();
        raiseHandsAttendee.setRaiseHandStatus(raiseHandStatus.getValue());
        if (RaiseHandStatus.YES == raiseHandStatus) {
            conferenceContext.setLastRaiseHandAttendeeId(attendeeId);
        } else {
            if (attendeeId.equals(conferenceContext.getLastRaiseHandAttendeeId())) {
                conferenceContext.setLastRaiseHandAttendeeId(null);
            }
        }
        McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(raiseHandsAttendee.getUpdateMap()));

        StringBuilder messageTip = new StringBuilder();
        messageTip.append("【").append(raiseHandsAttendee.getName()).append("】").append(raiseHandStatus.getName());

        // 消息和参会者信息同步到主级会议
        McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
        if (raiseHandsAttendee instanceof TerminalAttendeeForMcuKdc)
        {
            TerminalAttendeeForMcuKdc ta = (TerminalAttendeeForMcuKdc) raiseHandsAttendee;
            if (TerminalCache.getInstance().get(ta.getTerminalId()).getSn() != null)
            {
                try
                {
                    mqttService.acceptRaiseHand(conferenceContext.getConferenceNumber(), ta);
                }
                catch (Throwable e)
                {
                    logger.error("acceptRaiseHand - mqtt error", e);
                }
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("messageDuration", 10);
        jsonObject.put("messageText", "主持人同意了【" + raiseHandsAttendee.getName() + "】的发言请求！");
        jsonObject.put("messagePosition", "top");
        sendMessage(conferenceId, jsonObject);

        mqttService.sendConferenceInfoToPushTargetTerminal(conferenceContext);
    }
    
    /**
     * 拒绝举手
     * @author sinhy
     * @since 2021-12-07 10:27 
     * @param conferenceId
     * @param attendeeId
     */
    public void rejectRaiseHand(String conferenceId, String attendeeId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        RaiseHandStatus raiseHandStatus = RaiseHandStatus.NO;
        // 关闭举手
        AttendeeForMcuKdc raiseHandsAttendee = conferenceContext.getAttendeeById(attendeeId);
        raiseHandsAttendee.resetUpdateMap();
        raiseHandsAttendee.setRaiseHandStatus(raiseHandStatus.getValue());
        if (RaiseHandStatus.YES == raiseHandStatus) {
            conferenceContext.setLastRaiseHandAttendeeId(attendeeId);
        } else {
            if (attendeeId.equals(conferenceContext.getLastRaiseHandAttendeeId())) {
                conferenceContext.setLastRaiseHandAttendeeId(null);
            }
        }
        McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(raiseHandsAttendee.getUpdateMap()));

        StringBuilder messageTip = new StringBuilder();
        messageTip.append("【").append(raiseHandsAttendee.getName()).append("】").append(raiseHandStatus.getName());

        // 消息和参会者信息同步到主级会议
        McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
        if (raiseHandsAttendee instanceof TerminalAttendeeForMcuKdc)
        {
            TerminalAttendeeForMcuKdc ta = (TerminalAttendeeForMcuKdc) raiseHandsAttendee;
            if (TerminalCache.getInstance().get(ta.getTerminalId()).getSn() != null)
            {
                try
                {
                    mqttService.acceptRaiseHand(conferenceContext.getConferenceNumber(), ta);
                }
                catch (Throwable e)
                {
                    logger.error("acceptRaiseHand - mqtt error", e);
                }
            }
        }
        
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("messageDuration", 10);
        jsonObject.put("messageText", "主持人拒绝了【" + raiseHandsAttendee.getName() + "】的发言请求！");
        jsonObject.put("messagePosition", "top");
        sendMessage(conferenceId, jsonObject);

        mqttService.sendConferenceInfoToPushTargetTerminal(conferenceContext);
    }
    
    /**
     * 举手
     * @author sinhy
     * @since 2021-12-07 10:27 
     * @param conferenceId
     * @param attendeeId
     * @param raiseHandStatus void
     */
    public void raiseHand(String conferenceId, String attendeeId, RaiseHandStatus raiseHandStatus)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeForMcuKdc raiseHandsAttendee = conferenceContext.getAttendeeById(attendeeId);
            if (raiseHandsAttendee != null && raiseHandsAttendee.isMeetingJoined()) {
                raiseHandsAttendee.resetUpdateMap();
                raiseHandsAttendee.setRaiseHandStatus(raiseHandStatus.getValue());
                if (RaiseHandStatus.YES == raiseHandStatus) {
                    conferenceContext.setLastRaiseHandAttendeeId(attendeeId);
                } else {
                    if (attendeeId.equals(conferenceContext.getLastRaiseHandAttendeeId())) {
                        conferenceContext.setLastRaiseHandAttendeeId(null);
                    }
                }
                McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(raiseHandsAttendee.getUpdateMap()));

                StringBuilder messageTip = new StringBuilder();
                messageTip.append("【").append(raiseHandsAttendee.getName()).append("】").append(raiseHandStatus.getName());

                // 消息和参会者信息同步到主级会议
                McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("messageDuration", 60);
                jsonObject.put("messageText", "【" + raiseHandsAttendee.getName() + "】正在发言，请求主持人同意！");
                jsonObject.put("messagePosition", "top");
                sendMessage(conferenceId, jsonObject);
            }
        }
    }
    
    @Override
    public void setBanner(String conferenceId, String attendeeId, JSONObject params)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
//        AttendeeInfo attendeeInfo = new AttendeeInfo(cn, attendeeId);
//        if (attendeeInfo.getAttendee() instanceof TerminalAttendee)
//        {
//            TerminalAttendee ta = (TerminalAttendee) attendeeInfo.getAttendee();
//            if (TerminalCache.getInstance().get(ta.getTerminalId()).getSn() != null)
//            {
//                try
//                {
//                    mqttService.setBanner(cn, ta, params);
//                }
//                catch (Throwable e)
//                {
//                    logger.error("setBanner - mqtt error", e);
//                }
//            }
//        }
    }
    
    @Override
    public void sendBanner(String conferenceId, JSONObject jsonObject)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        McuKdcConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            if (a instanceof TerminalAttendeeForMcuKdc)
            {
                TerminalAttendeeForMcuKdc ta = (TerminalAttendeeForMcuKdc) a;
                if (TerminalCache.getInstance().get(ta.getTerminalId()).getSn() != null)
                {
//                    FcmThreadPool.exec(() -> {
//                        try
//                        {
//                            mqttService.setBanner(cn, ta, jsonObject);
//                        }
//                        catch (Throwable e)
//                        {
//                            logger.error("setBanner - mqtt error", e);
//                        }
//                    });
                }
            }
        });
    }

    /**
     * <pre>关闭混音</pre>
     * @author lilinhai
     * @since 2021-02-22 18:07 
     * @param conferenceId
     * @param attendeeId
     */
    public void closeMixing(String conferenceId, String attendeeId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        logger.info("关闭单个参会者混音入口：" + contextKey + ", " + attendeeId);
        McuKdcConferenceContext mcuKdcConferenceContext = McuKdcConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId));
        AttendeeForMcuKdc attendeeForMcuKdc = mcuKdcConferenceContext.getAttendeeById(attendeeId);
        if (attendeeForMcuKdc != null && StringUtils.hasText(attendeeForMcuKdc.getParticipantUuid())) {
            if (mcuKdcConferenceContext.isDiscuss()) {
                CcTerminalForceMuteRequest ccTerminalForceMuteRequest = new CcTerminalForceMuteRequest();
                ccTerminalForceMuteRequest.setConf_id(mcuKdcConferenceContext.getConfId());
                ccTerminalForceMuteRequest.setMt_id(attendeeForMcuKdc.getParticipantUuid());
                ccTerminalForceMuteRequest.setValue(1);
                CcTerminalForceMuteResponse ccTerminalForceMuteResponse = mcuKdcConferenceContext.getConferenceControlApi().terminalForceMute(ccTerminalForceMuteRequest);
                if (ccTerminalForceMuteResponse != null && ccTerminalForceMuteResponse.isSuccess()) {
                    attendeeForMcuKdc.setMixingStatus(AttendeeMixingStatus.NO.getValue());
                    McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(mcuKdcConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuKdc.getUpdateMap());
                }
            } else {
                if (!mcuKdcConferenceContext.isMuteParties()) {
                    CcRemoveMixingTerminalRequest ccRemoveMixingTerminalRequest = new CcRemoveMixingTerminalRequest();
                    ccRemoveMixingTerminalRequest.setConf_id(mcuKdcConferenceContext.getConfId());
                    ccRemoveMixingTerminalRequest.setMix_id("1");
                    List<CcAddMixingTerminalRequest.Mt> members = new ArrayList<>();
                    {
                        CcAddMixingTerminalRequest.Mt mt = new CcAddMixingTerminalRequest.Mt();
                        mt.setMt_id(attendeeForMcuKdc.getParticipantUuid());
                        members.add(mt);
                    }
                    ccRemoveMixingTerminalRequest.setMembers(members);
                    CcRemoveMixingTerminalResponse ccRemoveMixingTerminalResponse = mcuKdcConferenceContext.getConferenceControlApi().removeMixingTerminal(ccRemoveMixingTerminalRequest);
                    if (ccRemoveMixingTerminalResponse != null && ccRemoveMixingTerminalResponse.isSuccess()) {
                        attendeeForMcuKdc.setMixingStatus(AttendeeMixingStatus.NO.getValue());
                        McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(mcuKdcConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuKdc.getUpdateMap());

                        CcTerminalForceMuteRequest ccTerminalForceMuteRequest = new CcTerminalForceMuteRequest();
                        ccTerminalForceMuteRequest.setConf_id(mcuKdcConferenceContext.getConfId());
                        ccTerminalForceMuteRequest.setMt_id(attendeeForMcuKdc.getParticipantUuid());
                        ccTerminalForceMuteRequest.setValue(1);
                        CcTerminalForceMuteResponse ccTerminalForceMuteResponse = mcuKdcConferenceContext.getConferenceControlApi().terminalForceMute(ccTerminalForceMuteRequest);
                        if (ccTerminalForceMuteResponse != null && ccTerminalForceMuteResponse.isSuccess()) {
                        }
                    }
                } else {
                    if (attendeeForMcuKdc == mcuKdcConferenceContext.getMasterAttendee()) {
                        CcTerminalForceMuteRequest ccTerminalForceMuteRequest = new CcTerminalForceMuteRequest();
                        ccTerminalForceMuteRequest.setConf_id(mcuKdcConferenceContext.getConfId());
                        ccTerminalForceMuteRequest.setMt_id(attendeeForMcuKdc.getParticipantUuid());
                        ccTerminalForceMuteRequest.setValue(1);
                        CcTerminalForceMuteResponse ccTerminalForceMuteResponse = mcuKdcConferenceContext.getConferenceControlApi().terminalForceMute(ccTerminalForceMuteRequest);
                        if (ccTerminalForceMuteResponse != null && ccTerminalForceMuteResponse.isSuccess()) {
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void invite(String conferenceId, List<Long> terminalIds)
    {
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId));
        List<AttendeeForMcuKdc> attendees = new ArrayList<>();
        List<BusiTerminal> terminals = new ArrayList<>();
        for (Long terminalId : terminalIds)
        {
            BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
            if (busiTerminal != null) {
                terminals.add(busiTerminal);
            }
            TerminalAttendeeForMcuKdc ta = conferenceContext.getTerminalAttendeeMap().get(terminalId);
            if (ta == null)
            {
                ta = AttendeeUtils.packTerminalAttendee(terminalId);
                ta.setConferenceNumber(conferenceContext.getConferenceNumber());
                ta.setDeptId(conferenceContext.getDeptId());
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(terminalId);
                if (busiUserTerminal != null) {
                    ta.setUserId(busiUserTerminal.getUserId());
                }
                conferenceContext.addAttendee(ta);
                attendees.add(ta);
                McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, ta);
                McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + ta.getName() + "】被邀请加入");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);

                mqttService.sendConferenceInfoToPushTargetTerminal(conferenceContext);
            }
        }

        recallAttendees(conferenceId, attendees);
    }

    @Override
    public void invite(String conferenceId, JSONObject jsonObj)
    {
        Assert.isTrue(jsonObj.containsKey("name"), "名字是必填参数！");
        Assert.isTrue(jsonObj.containsKey("uri"), "URI是必填参数！");
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId));
        if (conferenceContext.getUuidAttendeeMapByUri(jsonObj.getString("uri")) != null)
        {
            return;
        }

        String name = jsonObj.getString("name");
        String uri = jsonObj.getString("uri");
        Integer callType = jsonObj.getInteger("callType");
        if (callType != null && callType != 1) {
            callType = null;
        }
        
        BusiTerminal busiTerminal = TerminalCache.getInstance().getByRemoteParty(conferenceContext.getDeptId(), uri);
        if (busiTerminal != null)
        {
            invite(conferenceId, Arrays.asList(busiTerminal.getId()));
            return;
        }

        Map<String, AttendeeForMcuKdc> attendeeForMcuKdcMap = conferenceContext.getAttendeeMapByUri(uri);
        if (attendeeForMcuKdcMap != null && attendeeForMcuKdcMap.size() > 0) {
            McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + name + "】已在会");
            return;
        }
        
        InvitedAttendeeForMcuKdc ia = new InvitedAttendeeForMcuKdc();
        ia.setConferenceNumber(conferenceContext.getConferenceNumber());
        ia.setId(UUID.randomUUID().toString());
        ia.setName(name);
        ia.setRemoteParty(jsonObj.getString("uri"));
        ia.setWeight(1);
        ia.setDeptId(conferenceContext.getDeptId());
        if (ia.getRemoteParty().contains("@"))
        {
            ia.setIp(ia.getRemoteParty().split("@")[1]);
        }
        else
        {
            ia.setIp(ia.getRemoteParty());
        }
        ia.setCallType(callType);
        
        conferenceContext.addAttendee(ia);
        McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, ia);
        McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + ia.getName() + "】被邀请加入");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
        McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
        recallAttendee(conferenceId, ia);

        mqttService.sendConferenceInfoToPushTargetTerminal(conferenceContext);
    }

    private void recallAttendee(String conferenceId, AttendeeForMcuKdc attendee) {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            InviteAttendeesTask inviteAttendeesTask = new InviteAttendeesTask(conferenceContext.getId(), 100, conferenceContext, attendee);
            mcuKdcDelayTaskService.addTask(inviteAttendeesTask);
        }
    }

    private void recallAttendees(String conferenceId, List<AttendeeForMcuKdc> attendees) {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            InviteAttendeesTask inviteAttendeesTask = new InviteAttendeesTask(conferenceContext.getId(), 100, conferenceContext, attendees);
            mcuKdcDelayTaskService.addTask(inviteAttendeesTask);
        }
    }

    @Override
    public void openCamera(String conferenceId, String attendeeId)
    {
    }

    @Override
    public void closeCamera(String conferenceId, String attendeeId)
    {
    }

    @Override
    public void openMixing(String conferenceId)
    {
        openMixing(McuKdcConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId)));
    }
    
    public void openMixing(McuKdcConferenceContext cc)
    {
        if (cc.isDiscuss()) {
            CcMrForceMuteRequest ccMrForceMuteRequest = new CcMrForceMuteRequest();
            ccMrForceMuteRequest.setConf_id(cc.getConfId());
            ccMrForceMuteRequest.setForce_mute(1);
            ccMrForceMuteRequest.setValue(0);
            CcMrForceMuteResponse ccMrForceMuteResponse = cc.getConferenceControlApi().mrForceMute(ccMrForceMuteRequest);
            if (ccMrForceMuteResponse != null && ccMrForceMuteResponse.isSuccess()) {
            }
        } else {
            if (cc.isMuteParties()) {
                List<CcStartMrMixingRequest.Member> members = new ArrayList<>();
                McuKdcConferenceContextUtils.eachAttendeeInConference(cc, (attendee) -> {
                    if (attendee.isMeetingJoined()) {
                        if (AttendeeMixingStatus.NO.getValue() == attendee.getMixingStatus()) {
                            CcStartMrMixingRequest.Member member = new CcStartMrMixingRequest.Member();
                            member.setMt_id(attendee.getParticipantUuid());
                            members.add(member);
                        }
                    }
                });
                if (members.size() > 0) {
                    CcStartMrMixingRequest ccStartMrMixingRequest = new CcStartMrMixingRequest();
                    ccStartMrMixingRequest.setConf_id(cc.getConfId());
                    ccStartMrMixingRequest.setMode(2);
                    ccStartMrMixingRequest.setMembers(members);
                    CcStartMrMixingResponse ccStartMrMixingResponse = cc.getConferenceControlApi().startMrMixing(ccStartMrMixingRequest);
                    if (ccStartMrMixingResponse != null && ccStartMrMixingResponse.isSuccess()) {
                        cc.setMuteParties(false);
                    }
                }
            } else {
                List<CcAddMixingTerminalRequest.Mt> members = new ArrayList<>();
                McuKdcConferenceContextUtils.eachAttendeeInConference(cc, (attendee) -> {
                    if (attendee.isMeetingJoined()) {
                        if (AttendeeMixingStatus.NO.getValue() == attendee.getMixingStatus()) {
                            CcAddMixingTerminalRequest.Mt mt = new CcAddMixingTerminalRequest.Mt();
                            mt.setMt_id(attendee.getParticipantUuid());
                            members.add(mt);
                        }
                    }
                });
                if (members.size() > 0) {
                    CcAddMixingTerminalRequest ccAddMixingTerminalRequest = new CcAddMixingTerminalRequest();
                    ccAddMixingTerminalRequest.setConf_id(cc.getConfId());
                    ccAddMixingTerminalRequest.setMix_id("1");
                    ccAddMixingTerminalRequest.setMembers(members);
                    CcAddMixingTerminalResponse ccAddMixingTerminalResponse = cc.getConferenceControlApi().addMixingTerminal(ccAddMixingTerminalRequest);
                    if (ccAddMixingTerminalResponse != null && ccAddMixingTerminalResponse.isSuccess()) {
//                        McuKdcConferenceContextUtils.eachAttendeeInConference(cc, (attendee) -> {
//                            if (attendee.isMeetingJoined()) {
//                                if (AttendeeMixingStatus.NO.getValue() == attendee.getMixingStatus()) {
//                                    attendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
//                                    McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(cc, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
//                                }
//                            }
//                        });
//                        McuKdcMuteStatusCheckTask muteStatusCheckTask = new McuKdcMuteStatusCheckTask(cc.getConferenceNumber(), 1000, cc);
//                        BeanFactory.getBean(McuKdcDelayTaskService.class).addTask(muteStatusCheckTask);
                    }
                }
            }
        }
    }
    
    @Override
    public void openDisplayDevice(String conferenceId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        // 允许所有人观看（打开所有下行视频）
    }
    
    @Override
    public void closeDisplayDevice(String conferenceId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        // 禁止所有观众观看（关闭所有下行视频）
    }
    

    @Override
    public void openDisplayDevice(String conferenceId, String attendeeId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        // 打开终端摄像头（打开上行视频）
    }
    
    @Override
    public void closeDisplayDevice(String conferenceId, String attendeeId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        // 打开终端摄像头（打开上行视频）
    }

    @Override
    public void closeCamera(String conferenceId)
    {
        McuKdcConferenceContextUtils.eachNonMcuAttendeeInConference(McuKdcConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId)), (a) -> {
            if (a.isMeetingJoined())
            {
                closeCamera(conferenceId, a.getId());
            }
        });
    }
    
    @Override
    public void openCamera(String conferenceId)
    {
        McuKdcConferenceContextUtils.eachNonMcuAttendeeInConference(McuKdcConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId)), (a) -> {
            if (a.isMeetingJoined())
            {
                openCamera(conferenceId, a.getId());
            }
        });
    }

    @Override
    public void closeMixing(String conferenceId)
    {
        McuKdcConferenceContext cc = McuKdcConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId));
        closeMixing(cc, cc.getMasterAttendee());
    }
    
    public void closeMixing(McuKdcConferenceContext cc, AttendeeForMcuKdc... excludes)
    {
        if (cc.isDiscuss()) {
            CcMrForceMuteRequest ccMrForceMuteRequest = new CcMrForceMuteRequest();
            ccMrForceMuteRequest.setConf_id(cc.getConfId());
            ccMrForceMuteRequest.setForce_mute(1);
            ccMrForceMuteRequest.setValue(1);
            CcMrForceMuteResponse ccMrForceMuteResponse = cc.getConferenceControlApi().mrForceMute(ccMrForceMuteRequest);
            if (ccMrForceMuteResponse != null && ccMrForceMuteResponse.isSuccess()) {
            }
            for (AttendeeForMcuKdc attendeeForMcuKdc : excludes) {
                if (attendeeForMcuKdc != null && StringUtils.hasText(attendeeForMcuKdc.getParticipantUuid())) {
                    CcTerminalForceMuteRequest ccTerminalForceMuteRequest = new CcTerminalForceMuteRequest();
                    ccTerminalForceMuteRequest.setConf_id(cc.getConfId());
                    ccTerminalForceMuteRequest.setMt_id(attendeeForMcuKdc.getParticipantUuid());
                    ccTerminalForceMuteRequest.setValue(0);
                    CcTerminalForceMuteResponse ccTerminalForceMuteResponse = cc.getConferenceControlApi().terminalForceMute(ccTerminalForceMuteRequest);
                    if (ccTerminalForceMuteResponse != null && ccTerminalForceMuteResponse.isSuccess()) {
                    }
                }
            }
        } else {
            if (!cc.isMuteParties()) {
                {
                    CcStopMrMixingRequest ccStopMrMixingRequest = new CcStopMrMixingRequest();
                    ccStopMrMixingRequest.setConf_id(cc.getConfId());
                    ccStopMrMixingRequest.setMix_id("1");
                    CcStopMrMixingResponse ccStopMrMixingResponse = cc.getConferenceControlApi().stopMrMixing(ccStopMrMixingRequest);
                    if (ccStopMrMixingResponse != null && ccStopMrMixingResponse.isSuccess()) {
                        cc.setMuteParties(true);
                    }
                }

                if (cc.isMuteParties()) {
                    List<CcStartMrMixingRequest.Member> members = new ArrayList<>();
                    for (AttendeeForMcuKdc attendeeForMcuKdc : excludes) {
                        if (attendeeForMcuKdc != null && StringUtils.hasText(attendeeForMcuKdc.getParticipantUuid())) {
                            CcStartMrMixingRequest.Member member = new CcStartMrMixingRequest.Member();
                            member.setMt_id(attendeeForMcuKdc.getParticipantUuid());
                            members.add(member);
                        }
                    }
                    if (members.size() > 0) {
                        CcStartMrMixingRequest ccStartMrMixingRequest = new CcStartMrMixingRequest();
                        ccStartMrMixingRequest.setConf_id(cc.getConfId());
                        ccStartMrMixingRequest.setMode(2);
                        ccStartMrMixingRequest.setMembers(members);
                        CcStartMrMixingResponse ccStartMrMixingResponse = cc.getConferenceControlApi().startMrMixing(ccStartMrMixingRequest);
                        if (ccStartMrMixingResponse != null && ccStartMrMixingResponse.isSuccess()) {
                            cc.setMuteParties(false);
                        }
                    }
                } else {
                    List<CcAddMixingTerminalRequest.Mt> members = new ArrayList<>();
                    for (AttendeeForMcuKdc attendeeForMcuKdc : excludes) {
                        if (attendeeForMcuKdc != null && StringUtils.hasText(attendeeForMcuKdc.getParticipantUuid())) {
                            CcAddMixingTerminalRequest.Mt mt = new CcAddMixingTerminalRequest.Mt();
                            mt.setMt_id(attendeeForMcuKdc.getParticipantUuid());
                            members.add(mt);
                        }
                    }
                    if (members.size() > 0) {
                        CcAddMixingTerminalRequest ccAddMixingTerminalRequest = new CcAddMixingTerminalRequest();
                        ccAddMixingTerminalRequest.setConf_id(cc.getConfId());
                        ccAddMixingTerminalRequest.setMix_id("1");
                        ccAddMixingTerminalRequest.setMembers(members);
                        CcAddMixingTerminalResponse ccAddMixingTerminalResponse = cc.getConferenceControlApi().addMixingTerminal(ccAddMixingTerminalRequest);
                        if (ccAddMixingTerminalResponse != null && ccAddMixingTerminalResponse.isSuccess()) {
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void sendMessage(String conferenceId, JSONObject jsonObject)
    {

    }

    /**
     * 设置横幅
     *
     * @param conferenceId
     * @param jsonObject void
     */
    @Override
    public void setMessageBannerText(String conferenceId, JSONObject jsonObject) {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        String messageBannerText = jsonObject.getString("messageBannerText");
        if (!ObjectUtils.isEmpty(messageBannerText)) {
            if (messageBannerText.getBytes().length <= 60) {
                String replacedText = messageBannerText.replaceAll("\t|\r|\n", "");
                if (replacedText.length() < messageBannerText.length()) {
                    throw new SystemException("横幅不支持【制表】【回车】等特殊字符！");
                }
            } else {
                if (messageBannerText.getBytes().length > 60) {
                    throw new SystemException("横幅最多支持30个字符！");
                }
            }
        }
        setMessageBannerText(contextKey, messageBannerText);
    }

    /**
     * 设置横幅
     *
     * @param contextKey
     * @param text
     */
    @Override
    public void setMessageBannerText(String contextKey, String text) {

    }

    @Override
    public void polling(String conferenceId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        McuKdcConferenceContext mainConferenceContext = McuKdcConferenceContextCache.getInstance().getMainConferenceContext(conferenceContext);

        if (conferenceContext != null) {
            BusiMcuKdcTemplatePollingScheme con0 = new BusiMcuKdcTemplatePollingScheme();
            con0.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
            con0.setEnableStatus(YesOrNo.YES.getValue());
            List<BusiMcuKdcTemplatePollingScheme> pss = busiMcuKdcTemplatePollingSchemeMapper.selectBusiMcuKdcTemplatePollingSchemeList(con0);
            BusiMcuKdcTemplatePollingScheme ps = null;
            for (BusiMcuKdcTemplatePollingScheme busiTemplatePollingScheme : pss) {
                if (ps == null
                        || (ps.getWeight() == null && busiTemplatePollingScheme.getWeight() != null)
                        || (ps.getWeight() != null && busiTemplatePollingScheme.getWeight() != null && ps.getWeight() < busiTemplatePollingScheme.getWeight())) {
                    ps = busiTemplatePollingScheme;
                }
            }
            if (ps == null) {
                throw new SystemException(1008243, "您当前还未配置已启用的轮询方案，请先配置轮询方案，再点开始轮询！");
            }

            AttendeeOperation oldAttendeeOperation = conferenceContext.getAttendeeOperation();
            if (oldAttendeeOperation instanceof DefaultAttendeeOperation) {
                conferenceContext.setLastAttendeeOperation(oldAttendeeOperation);
            }
            PollingScheme pollingScheme = busiMcuKdcTemplatePollingSchemeService.convert(ps, conferenceContext);
            if (!conferenceContext.isSupportBroadcast()) {
                pollingScheme.setIsBroadcast(YesOrNo.NO);
            }
            if (mainConferenceContext.getMasterAttendee() == null || !mainConferenceContext.getMasterAttendee().isMeetingJoined()) {
                if (pollingScheme.getIsBroadcast() != YesOrNo.YES || pollingScheme.getIsFixSelf() == YesOrNo.YES) {
                    throw new SystemException(1005454, "主会场未设置或未呼入,无法进行轮询操作！");
                }
            }
            AttendeeOperation newAttendeeOperation = new PollingAttendeeOperation(conferenceContext, pollingScheme);
            conferenceContext.setAttendeeOperation(newAttendeeOperation);
            oldAttendeeOperation.cancel();
        }
    }
    
    @Override
    public void pollingPause(String conferenceId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            if (attendeeOperation instanceof PollingAttendeeOperation) {
                PollingAttendeeOperation pollingAttendeeOperation = (PollingAttendeeOperation) attendeeOperation;
                pollingAttendeeOperation.setPause(true);
            }
        }
    }

    @Override
    public void pollingResume(String conferenceId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            if (attendeeOperation instanceof PollingAttendeeOperation) {
                PollingAttendeeOperation pollingAttendeeOperation = (PollingAttendeeOperation) attendeeOperation;
                pollingAttendeeOperation.setPause(false);
            }
        }
    }

    /**
     * <pre>取消轮询</pre>
     * @author lilinhai
     * @since 2021-02-23 16:14
     * @param conferenceId
     */
    @Override
    public void cancelPolling(String conferenceId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            if (attendeeOperation instanceof PollingAttendeeOperation) {
                attendeeOperation.cancel();
                conferenceContext.setAttendeeOperation(conferenceContext.getLastAttendeeOperation());
            }
        }
    }

    @Override
    public JSONObject detail(String conferenceId, String attendeeId)
    {
        McuKdcAttendeeInfo ai = new McuKdcAttendeeInfo(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId), attendeeId);
        if (ai.getAttendee() != null && ai.getAttendee().isMeetingJoined())
        {
            McuKdcConferenceContext mcuKdcConferenceContext = McuKdcConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId));
            CcGetTerminalInfoRequest ccGetTerminalInfoRequest = new CcGetTerminalInfoRequest();
            ccGetTerminalInfoRequest.setConf_id(mcuKdcConferenceContext.getConfId());
            ccGetTerminalInfoRequest.setMt_id(ai.getAttendee().getParticipantUuid());
            CcGetTerminalInfoResponse ccGetTerminalInfoResponse = mcuKdcConferenceContext.getConferenceControlApi().getCcTerminalInfo(ccGetTerminalInfoRequest);
            if (ccGetTerminalInfoResponse != null && ccGetTerminalInfoResponse.isSuccess())
            {
                JSONObject d = toDetail(ccGetTerminalInfoResponse, ai.getAttendee(), mcuKdcConferenceContext);
                d.put("attendeeId", ai.getAttendee().getId());
                return d;
            }
        }
        
        return null;
    }

    @Override
    public JSONObject detail(McuKdcConferenceContext conferenceContext, AttendeeForMcuKdc attendee)
    {
        if (attendee != null && attendee.isMeetingJoined())
        {
            CcGetTerminalInfoRequest ccGetTerminalInfoRequest = new CcGetTerminalInfoRequest();
            ccGetTerminalInfoRequest.setConf_id(conferenceContext.getConfId());
            ccGetTerminalInfoRequest.setMt_id(attendee.getParticipantUuid());
            CcGetTerminalInfoResponse ccGetTerminalInfoResponse = conferenceContext.getConferenceControlApi().getCcTerminalInfo(ccGetTerminalInfoRequest);
            if (ccGetTerminalInfoResponse != null && ccGetTerminalInfoResponse.isSuccess())
            {
                JSONObject d = toDetail(ccGetTerminalInfoResponse, attendee, conferenceContext);
                d.put("attendeeId", attendee.getId());
                return d;
            }
        }

        return null;
    }

    public JSONObject toDetail(CcGetTerminalInfoResponse ccGetTerminalInfoResponse, AttendeeForMcuKdc attendee, McuKdcConferenceContext conferenceContext)
    {
        if (ccGetTerminalInfoResponse != null && ccGetTerminalInfoResponse.isSuccess())
        {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("direction", attendee.getDirection());
            Integer protocol = ccGetTerminalInfoResponse.getProtocol();
            String protocolType = "自动";
            if (protocol != null) {
                protocolType = protocol == 1 ? "SIP" : "H323";
            }
            jsonObj.put("type", protocolType);
            jsonObj.put("isEncrypted", false);
            int durationSeconds = 0;
            Date joinedTime = attendee.getJoinedTime();
            if (joinedTime != null) {
                long diffTime = 0;
                if (conferenceContext.getMcuKdcBridge() != null) {
                    diffTime = conferenceContext.getMcuKdcBridge().getDiffTime();
                }
                durationSeconds = (int) ((System.currentTimeMillis() - diffTime - joinedTime.getTime()) / 1000);
            }
            jsonObj.put("durationSeconds", durationSeconds);
//            jsonObj.put("sipCallId", null);
            jsonObj.put("remoteParty", attendee.getRemoteParty());

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

            // 下行视频
            {
                List<CcGetTerminalInfoResponse.Chn> v_rcv_chn = ccGetTerminalInfoResponse.getV_rcv_chn();
                CcGetTerminalInfoResponse.Chn chn = null;
                if (v_rcv_chn != null && v_rcv_chn.size() > 0) {
                    chn = v_rcv_chn.get(0);
                }
                JSONObject video = new JSONObject();
                video.put("role", "main");
                if (chn != null) {
                    String resolutionRatio = new CcGetTerminalInfoResponse().resolutionRatio(chn.getResolution());
                    video.put("resolutionRatio", resolutionRatio);
                    String formatStr = new CcGetTerminalInfoResponse().getFormatStr(chn.getFormat());
                    video.put("videoCodec", formatStr);
                    video.put("bandwidth", chn.getBitrate());
                } else {
                    video.put("resolutionRatio", "");
                    video.put("videoCodec", "");
                    video.put("bandwidth", 0);
                }

                video.put("frameRate", 30);
                video.put("packetLossPercentage", 0);
                video.put("jitter", 0);
                video.put("roundTripTime", 1);
                downLinkVideos.add(video);
            }
            // 上行视频
            {
                List<CcGetTerminalInfoResponse.Chn> v_snd_chn = ccGetTerminalInfoResponse.getV_snd_chn();
                CcGetTerminalInfoResponse.Chn chn = null;
                if (v_snd_chn != null && v_snd_chn.size() > 0) {
                    chn = v_snd_chn.get(0);
                }
                JSONObject video = new JSONObject();
                video.put("role", "main");

                if (chn != null) {
                    String resolutionRatio = new CcGetTerminalInfoResponse().resolutionRatio(chn.getResolution());
                    video.put("resolutionRatio", resolutionRatio);
                    String formatStr = new CcGetTerminalInfoResponse().getFormatStr(chn.getFormat());
                    video.put("videoCodec", formatStr);
                    video.put("bandwidth", chn.getBitrate());
                } else {
                    video.put("resolutionRatio", "");
                    video.put("videoCodec", "");
                    video.put("bandwidth", 0);
                }
                video.put("frameRate", 30);
                video.put("packetLossPercentage", 0);
                video.put("jitter", 0);
                video.put("roundTripTime", 1);
                upLinkVideos.add(video);
            }
            // 下行音频
            {
                downLinkAudio.put("codec", "");
                downLinkAudio.put("bandwidth", 0);
                downLinkAudio.put("packetLossPercentage", 0);
                downLinkAudio.put("codecBitRate", 0);
                downLinkAudio.put("jitter", 0);
                downLinkAudio.put("roundTripTime", 1);
                downLinkAudio.put("gainApplied", 0);
            }
            // 上行音频
            {
                upLinkAudio.put("codec", "");
                upLinkAudio.put("bandwidth", 0);
                upLinkAudio.put("packetLossPercentage", 0);
                upLinkAudio.put("codecBitRate", 0);
                upLinkAudio.put("jitter", 0);
                upLinkAudio.put("roundTripTime", 1);
                upLinkAudio.put("gainApplied", 0);
            }
            // 下行辅流
            if (conferenceContext.hasPresent()) {
                if (!ObjectUtils.isEmpty(ccGetTerminalInfoResponse.getDv_rcv_chn())) {
                    List<CcGetTerminalInfoResponse.Chn> dv_rcv_chn = ccGetTerminalInfoResponse.getDv_rcv_chn();
                    if (dv_rcv_chn != null && dv_rcv_chn.size() > 0) {
                        CcGetTerminalInfoResponse.Chn chn = dv_rcv_chn.get(0);
                        if (chn.getFormat() != 255) {
                            JSONObject content = new JSONObject();
                            content.put("role", "presentation");
                            if (chn != null) {
                                String resolutionRatio = new CcGetTerminalInfoResponse().resolutionRatio(chn.getResolution());
                                content.put("resolutionRatio", resolutionRatio);
                                String formatStr = new CcGetTerminalInfoResponse().getFormatStr(chn.getFormat());
                                content.put("videoCodec", formatStr);
                                content.put("bandwidth", chn.getBitrate());
                            } else {
                                content.put("resolutionRatio", "");
                                content.put("videoCodec", "");
                                content.put("bandwidth", 0);
                            }
                            content.put("frameRate", 30);
                            content.put("packetLossPercentage", 0);
                            content.put("jitter", 0);
                            content.put("roundTripTime", 1);
                            downLinkVideos.add(content);
                        }
                    }
                }
                // 上行辅流
                if (!ObjectUtils.isEmpty(ccGetTerminalInfoResponse.getDv_snd_chn())) {
                    List<CcGetTerminalInfoResponse.Chn> dv_snd_chn = ccGetTerminalInfoResponse.getDv_snd_chn();
                    if (dv_snd_chn != null && dv_snd_chn.size() > 0) {
                        CcGetTerminalInfoResponse.Chn chn = dv_snd_chn.get(0);
                        if (chn.getFormat() != 255) {
                            JSONObject video = new JSONObject();
                            video.put("role", "presentation");
                            if (chn != null) {
                                String resolutionRatio = new CcGetTerminalInfoResponse().resolutionRatio(chn.getResolution());
                                video.put("resolutionRatio", resolutionRatio);
                                String formatStr = new CcGetTerminalInfoResponse().getFormatStr(chn.getFormat());
                                video.put("videoCodec", formatStr);
                                video.put("bandwidth", chn.getBitrate());
                            } else {
                                video.put("resolutionRatio", "");
                                video.put("videoCodec", "");
                                video.put("bandwidth", 0);
                            }
                            video.put("frameRate", 30);
                            video.put("packetLossPercentage", 0);
                            video.put("jitter", 0);
                            video.put("roundTripTime", 1);
                            upLinkVideos.add(video);
                        }
                    }
                }
            }
            return jsonObj;
        }
        return null;
    }

    /**
     * 与会者呼叫失败通知
     * @author lilinhai
     * @since 2021-02-08 13:49 
     * @param participantUuid void
     */
    public void callAttendeeFailedNotice(String participantUuid, String reason)
    {
        // 已呼叫的参会者
//        Attendee attendee = AttendeeCallCache.getInstance().remove(participantUuid);
//        if (attendee != null)
//        {
//            ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(attendee.getConferenceNumber());
//            if (conferenceContext != null)
//            {
//                if ("remoteBusy".equals(reason))
//                {
//                    // 推送终端信令未断开的参会消息
//                    AttendeeMessageQueue.getInstance().put(new SignalingNotDisconnectedAttendeeMessage(attendee));
//                }
//                else if ("timeout".equals(reason))
//                {
//                    // 推送终端呼叫超时的参会消息
//                    AttendeeMessageQueue.getInstance().put(new CallTimeoutAttendeeMessage(attendee));
//                }
//                else
//                {
//                    AttendeeMessageQueue.getInstance().put(new CallFailedAttendeeMessage(attendee));
//                }
//
//                StringBuilder msgBuilder = new StringBuilder();
//                msgBuilder.append("【").append(attendee.getName()).append("】呼叫失败");
//
//                if (attendee.getCallRequestSentTime() != null)
//                {
//                    long timeDiff = (System.currentTimeMillis() - attendee.getCallRequestSentTime()) / 1000;
//                    if (timeDiff > 0)
//                    {
//                        msgBuilder.append("【").append(timeDiff).append("秒】：");
//                    }
//                    else
//                    {
//                        msgBuilder.append("：");
//                    }
//                }
//                else
//                {
//                    msgBuilder.append("：");
//                }
//                msgBuilder.append(reason);
//                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, msgBuilder);
//            }
//            else
//            {
//                logger.info("找不到conferenceContext：" + attendee.getConferenceNumber());
//            }
//
//            // 清空uuid
//            if (!attendee.isMeetingJoined())
//            {
//                attendee.setParticipantUuid(null);
//            }
//            attendee.setCallRequestSentTime(null);
//        }
    }
    
    /**
     * 批量修改参会者业务参数，支持集群
     * @author lilinhai
     * @since 2021-04-19 11:45 
     * @param conferenceNumber
     * @param attendees
     * @param nameValuePairs void
     */
    private void updateAttendeeAttrs(String conferenceNumber, List<AttendeeForMcuKdc> attendees, List<NameValuePair> nameValuePairs) {
//        updateAttendeeAttrs(ConferenceContextCache.getInstance().get(conferenceNumber), attendees, ParticipantBulkOperationMode.SELECTED, nameValuePairs);
    }

//    private void processCallLegUpdate(String conferenceId, String attendeeId, List<BaseFixedParamValue> params, String[] paramNames, ICallLegUpdateCallBackProcessor callLegUpdateCallBackProcessor) {
//
//        Map<String, FixedParamValue> fpvMap = new HashMap<>();
//        for (FixedParamValue fixedParamValue : params) {
//            fpvMap.put(fixedParamValue.getName(), fixedParamValue);
//        }
//
//
//        String cn = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
//        AttendeeInfo ai = new AttendeeInfo(cn, attendeeId);
//        Assert.notNull(ai.getAttendee(), "找不到参会者：" + attendeeId);
//        CustomParamBuilder customParamBuilder = new CustomParamBuilder();
//
//        if (fpvMap.get(LAYOUT_TEMPLATE) == null) {
//            for (String paramName : paramNames) {
//                FixedParamValue fixedParamValue = fpvMap.get(paramName);
//                Assert.notNull(fixedParamValue, paramName + "不能为空！");
//                Assert.notNull(fixedParamValue.getValue(), paramName + "值不能为空！");
//                customParamBuilder.param(fixedParamValue.getName(), fixedParamValue.getValue());
//                ai.getAttendee().getFixedSettings().getByName(paramName).setFixed(fixedParamValue.isFixed());
//            }
//        } else {
//            FixedParamValue templateParamValue = fpvMap.get(LAYOUT_TEMPLATE);
//            ConferenceContext conferenceContext = ConferenceContextCache.getInstance().getByEncryptedConferenceNumber(conferenceId);
//            List<FmeBridge> fbs = FmeBridgeCache.getInstance().getFmeBridgesByDept(conferenceContext.getDeptId());
//            if(!CollectionUtils.isEmpty(fbs)){
//                CustomScreenCreater customScreenCreater = fbs.get(0).getDataCache().getSplitScreenCreaterMap().get(templateParamValue.getValue());
//                customParamBuilder.param(LAYOUT_TEMPLATE, customScreenCreater.getLayout());
//                customParamBuilder.param(DEFAULT_LAYOUT, "");
//                customParamBuilder.param(CHOSEN_LAYOUT, AUTOMATIC);
//            }
//            ai.getAttendee().getFixedSettings().getByName(CHOSEN_LAYOUT).setFixed(true);
//        }
//
//
//        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(ai.getAttendee());
//        Participant participant = fmeBridge.getDataCache().getParticipantByUuid(ai.getAttendee().getParticipantUuid());
//        CallLeg callLeg = participant.getCallLeg();
//        RestResponse rr = fmeBridge.getCallLegInvoker().updateCallLeg(callLeg.getId(), customParamBuilder.build());
//        if (!rr.isSuccess()) {
//            throw new SystemException(1008544, rr.getMessage());
//        }
//
//        callLeg = callegService.getCallLegByParticipantUuid(fmeBridge, participant);
//        participant.setCallLeg(callLeg);
//        if (callLegUpdateCallBackProcessor != null)
//        {
//            callLegUpdateCallBackProcessor.process(fpvMap, fmeBridge, participant);
//        }
//    }
}
