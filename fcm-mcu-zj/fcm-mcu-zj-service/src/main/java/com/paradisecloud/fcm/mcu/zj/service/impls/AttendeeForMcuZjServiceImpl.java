/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.service.impls
 * @author lilinhai 
 * @since 2021-02-05 17:39
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.zj.service.impls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZjTemplatePollingSchemeMapper;
import com.paradisecloud.fcm.dao.mapper.BusiUserTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuZjTemplatePollingScheme;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.BusiUserTerminal;
import com.paradisecloud.fcm.mcu.zj.attendee.model.operation.*;
import com.paradisecloud.fcm.mcu.zj.attendee.model.polling.PollingScheme;
import com.paradisecloud.fcm.mcu.zj.attendee.utils.AttendeeUtils;
import com.paradisecloud.fcm.mcu.zj.attendee.utils.McuZjConferenceContextUtils;
import com.paradisecloud.fcm.mcu.zj.cache.AttendeeCountingStatistics;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjConferenceContextCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.SourceTemplate;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.InvitedAttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.TerminalAttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.mcu.zj.model.core.McuZjAttendeeInfo;
import com.paradisecloud.fcm.mcu.zj.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.mcu.zj.model.request.cc.CcCameraControlRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cc.CcSetBannerRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cc.CcUpdateMrEpsStatusRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cc.CcUpdateMrStatusRequest;
import com.paradisecloud.fcm.mcu.zj.model.response.cc.CcQueryMrEpsMediaResponse;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IAttendeeForMcuZjService;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjTemplatePollingSchemeService;
import com.paradisecloud.fcm.mcu.zj.task.*;
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
public class AttendeeForMcuZjServiceImpl implements IAttendeeForMcuZjService {

    public static final String LAYOUT_TEMPLATE = "layoutTemplate";
    public static final String DEFAULT_LAYOUT = "defaultLayout";
    public static final String CHOSEN_LAYOUT = "chosenLayout";
    public static final String AUTOMATIC = "automatic";
    private Logger logger = LoggerFactory.getLogger(AttendeeForMcuZjServiceImpl.class);

    @Resource
    private BusiMcuZjTemplatePollingSchemeMapper busiMcuZjTemplatePollingSchemeMapper;

    @Resource
    private IBusiMcuZjTemplatePollingSchemeService busiMcuZjTemplatePollingSchemeService;
    
    @Resource
    private IMqttService mqttService;

    @Resource
    private DelayTaskService delayTaskService;

    @Resource
    private RedisCache redisCache;

    @Resource
    private BusiUserTerminalMapper busiUserTerminalMapper;

    private boolean isContains(AttendeeForMcuZj a, AttendeeForMcuZj... excludes)
    {
        if (!ObjectUtils.isEmpty(excludes))
        {
            for (AttendeeForMcuZj attendee : excludes)
            {
                if (a == attendee)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateAttendeeImportance(AttendeeForMcuZj attendee, AttendeeImportance attendeeImportance)
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeForMcuZj attendee = conferenceContext.getAttendeeById(attendeeId);
            if (attendee != null) {
                BusiTerminal terminal = TerminalCache.getInstance().getBySn(attendee.getSn());
                if (terminal != null) {
                    if (TerminalType.isFCMSIP(terminal.getType())) {
                        BeanFactory.getBean(IMqttService.class).inviteAttendeeJoinConference(attendee, conferenceContext, AttendType.AUTO_JOIN.getValue());
                        return;
                    }
                }
                InviteAttendeesTask inviteAttendeesTask = new InviteAttendeesTask(conferenceContext.getId(), 100, conferenceContext, attendee);
                delayTaskService.addTask(inviteAttendeesTask);
            }
            if (attendee.isMcuAttendee()) {
                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(attendee.getCascadeConferenceId()));
                if (baseConferenceContext != null) {
                    baseConferenceContext.setUpCascadeRemoteParty(conferenceContext.getConferenceRemoteParty());
                }
            }
        }
    }
    
    public void callAttendee(AttendeeForMcuZj attendee)
    {
//        new CallAttendeeProcessor(attendee).process();
    }
    
    @Override
    public void hangUp(String conferenceId, String attendeeId)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeForMcuZj attendeeForMcuZj = conferenceContext.getAttendeeById(attendeeId);
            if (attendeeForMcuZj != null) {
                if (StringUtils.hasText(attendeeForMcuZj.getEpUserId())) {
                    CcUpdateMrEpsStatusRequest ccUpdateMrEpsStatusRequest = new CcUpdateMrEpsStatusRequest();
                    ccUpdateMrEpsStatusRequest.setAction(CcUpdateMrEpsStatusRequest.ACTION_drop);
                    ccUpdateMrEpsStatusRequest.setUsr_ids(new String[]{attendeeForMcuZj.getEpUserId()});
                    conferenceContext.getConferenceControlApi().updateMrEpsStatus(ccUpdateMrEpsStatusRequest);
                    attendeeForMcuZj.setHangUp(true);
                }
            }
        }
    }
    
    @Override
    public void remove(String conferenceId, String attendeeId)
    {
        hangUp(conferenceId, attendeeId);
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeForMcuZj attendeeForMcuZj = conferenceContext.getAttendeeById(attendeeId);
            if (attendeeForMcuZj != null) {
                conferenceContext.removeAttendeeById(attendeeId);

                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("id", attendeeForMcuZj.getId());
                updateMap.put("deptId", attendeeForMcuZj.getDeptId());
                updateMap.put("mcuAttendee", attendeeForMcuZj.isMcuAttendee());
                McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                McuZjDeleteEpsTask mcuZjDeleteEpsTask = new McuZjDeleteEpsTask(attendeeForMcuZj.getEpUserId(), 5000, conferenceContext, attendeeForMcuZj.getEpUserId());
                delayTaskService.addTask(mcuZjDeleteEpsTask);
            } else {
                throw new CustomException("该终端已被移除，请刷新页面后重试");
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
        
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjAttendeeInfo ai = new McuZjAttendeeInfo(contextKey, attendeeId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        AttendeeForMcuZj attendeeForMcuZj = conferenceContext.getAttendeeById(attendeeId);
        if (attendeeForMcuZj != null) {
            if (StringUtils.hasText(attendeeForMcuZj.getEpUserId())) {
                String movement = "";
                Collection<Object> values = params.values();
                for (Object value : values) {
                    String action = value.toString();
                    movement = action;
                }
                if (System.currentTimeMillis() - attendeeForMcuZj.getLastControlCameraTime() > 500  || !movement.equals(attendeeForMcuZj.getLastControlCameraMove())) {
                    if (StringUtils.hasText(attendeeForMcuZj.getLastControlCameraMove()) && !movement.equals(attendeeForMcuZj.getLastControlCameraMove())) {
                        CcCameraControlRequest ccCameraControlRequest = new CcCameraControlRequest();
                        ccCameraControlRequest.setAction(CcCameraControlRequest.action_stop);
                        ccCameraControlRequest.setUsr_id(attendeeForMcuZj.getEpUserId());
                        ccCameraControlRequest.setSpeed(4);
                        ccCameraControlRequest.setTimeout(100);
                        conferenceContext.getConferenceControlApi().cameraControl(ccCameraControlRequest);
                        attendeeForMcuZj.setLastControlCameraTime(0);
                        attendeeForMcuZj.setLastControlCameraMove(null);
                        return;
                    }
                    attendeeForMcuZj.setLastControlCameraMove(movement);
                    attendeeForMcuZj.setLastControlCameraTime(System.currentTimeMillis());
                    CcCameraControlRequest ccCameraControlRequest = new CcCameraControlRequest();
                    if (attendeeForMcuZj.getLastControlCameraTime() == 0) {
                        ccCameraControlRequest.setAction(CcCameraControlRequest.action_start);
                    } else {
                        ccCameraControlRequest.setAction(CcCameraControlRequest.action_continue);
                    }
                    ccCameraControlRequest.setMovement(movement);
                    ccCameraControlRequest.setUsr_id(attendeeForMcuZj.getEpUserId());
                    ccCameraControlRequest.setSpeed(4);
                    ccCameraControlRequest.setTimeout(1000);
                    boolean cameraControl = conferenceContext.getConferenceControlApi().cameraControl(ccCameraControlRequest);
                    if (cameraControl) {
                        StopControlCameraTask stopControlCameraTask = new StopControlCameraTask(attendeeForMcuZj.getEpUserId(), 1000, conferenceContext, attendeeForMcuZj, movement);
                        delayTaskService.addTask(stopControlCameraTask);
                    }
                    if (cameraControl) {
                        McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(ai.getConferenceContext(), WebsocketMessageType.MESSAGE_TIP, "【" + ai.getAttendee().getName() + "】镜头调整成功！");
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
        McuZjConferenceContext mcuZjConferenceContext = McuZjConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
        AttendeeForMcuZj attendeeForMcuZj = mcuZjConferenceContext.getAttendeeById(attendeeId);
        if (attendeeForMcuZj != null) {
            if (mcuZjConferenceContext.isDownCascadeConference()) {
                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(mcuZjConferenceContext.getUpCascadeConferenceId()));
                if (baseConferenceContext != null) {
                    if (!attendeeForMcuZj.getRemoteParty().equals(mcuZjConferenceContext.getUpCascadeRemoteParty())) {
                        throw new SystemException(1005454, "该会议正被级联，不允许修改主会场！");
                    }
                }
            }
            CcUpdateMrEpsStatusRequest ccUpdateMrEpsStatusRequest = new CcUpdateMrEpsStatusRequest();
            ccUpdateMrEpsStatusRequest.setAction(CcUpdateMrEpsStatusRequest.ACTION_speaker);
            String[] strings = new String[1];
            strings[0] = attendeeForMcuZj.getEpUserId();
            ccUpdateMrEpsStatusRequest.setUsr_ids(strings);
            ccUpdateMrEpsStatusRequest.setValue(1);
            boolean success = mcuZjConferenceContext.getConferenceControlApi().updateMrEpsStatus(ccUpdateMrEpsStatusRequest);
            if (success) {
                if (mcuZjConferenceContext.isSupportChooseSee()) {
                    if (mcuZjConferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                        mcuZjConferenceContext.setLastAttendeeOperation(mcuZjConferenceContext.getAttendeeOperation());
                    }
                    AttendeeOperation old = mcuZjConferenceContext.getAttendeeOperation();
                    old.cancel();
                    AttendeeOperation attendeeOperation = new ChangeMasterAttendeeOperation(mcuZjConferenceContext);
                    mcuZjConferenceContext.setAttendeeOperation(attendeeOperation);
                    // 观众
                    if (mcuZjConferenceContext.getAttendeeOperationForGuest() instanceof DefaultAttendeeOperationForGuest) {
                        mcuZjConferenceContext.setLastAttendeeOperationForGuest(mcuZjConferenceContext.getAttendeeOperationForGuest());
                    }
                    AttendeeOperation oldGuest = mcuZjConferenceContext.getAttendeeOperationForGuest();
                    oldGuest.cancel();
                    mcuZjConferenceContext.setAttendeeOperationForGuest(attendeeOperation);
                }
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext mainConferenceContext = McuZjConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue())
        {
            throw new SystemException(1005454, "主会场未设置，无法进行选看操作！");
        }

        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            SourceTemplate sourceTemplate = conferenceContext.getMcuZjBridge().getSourceTemplateById(conferenceContext.getResourceTemplateId());
            if (sourceTemplate == null || !sourceTemplate.isSupportChooseSee()) {
                throw new SystemException(1005454, "该会议不支持进行选看操作！");
            }
            AttendeeForMcuZj chooseSeeAttendee = conferenceContext.getAttendeeById(attendeeId);
            if (chooseSeeAttendee != null) {
                if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                    conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
                }
                AttendeeOperation old = conferenceContext.getAttendeeOperation();
                old.cancel();
                AttendeeOperation attendeeOperation = new ChooseSeeAttendeeOperation(conferenceContext, chooseSeeAttendee);
                conferenceContext.setAttendeeOperation(attendeeOperation);
                // 观众
                if (conferenceContext.getAttendeeOperationForGuest() instanceof DefaultAttendeeOperationForGuest) {
                    conferenceContext.setLastAttendeeOperationForGuest(conferenceContext.getAttendeeOperationForGuest());
                }
                AttendeeOperation oldGuest = conferenceContext.getAttendeeOperationForGuest();
                oldGuest.cancel();
                conferenceContext.setAttendeeOperationForGuest(attendeeOperation);
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext mainConferenceContext = McuZjConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue())
        {
            throw new SystemException(1005454, "主会场未设置，无法进行选看操作！");
        }

        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            SourceTemplate sourceTemplate = conferenceContext.getMcuZjBridge().getSourceTemplateById(conferenceContext.getResourceTemplateId());
            if (sourceTemplate == null || !sourceTemplate.isSupportChooseSee()) {
                throw new SystemException(1005454, "该会议不支持进行选看操作！");
            }
            AttendeeForMcuZj chooseSeeAttendee = conferenceContext.getAttendeeById(attendeeId);
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
                // 观众
                if (conferenceContext.getAttendeeOperationForGuest() instanceof DefaultAttendeeOperationForGuest) {
                    conferenceContext.setLastAttendeeOperationForGuest(conferenceContext.getAttendeeOperationForGuest());
                }
                AttendeeOperation oldGuest = conferenceContext.getAttendeeOperationForGuest();
                oldGuest.cancel();
                conferenceContext.setAttendeeOperationForGuest(attendeeOperation);
            }
        }
    }
    
    /**
     * 默认选看（主会场切换时执行）
     * @author lilinhai
     * @since 2021-02-09 11:32  void
     */
    public void defaultChooseSee(McuZjConferenceContext mainConferenceContext)
    {
        if (mainConferenceContext.getMasterAttendee() == null || !mainConferenceContext.getMasterAttendee().isMeetingJoined())
        {
            return;
        }
        
        // 只有当操作是默认的选看时，才会执行该逻辑
        AttendeeForMcuZj a = McuZjConferenceContextUtils.getDefaultChooseToSee(mainConferenceContext);
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext mainConferenceContext = McuZjConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue())
        {
            throw new SystemException(1005454, "主会场未设置，无法进行点名操作！");
        }

        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            SourceTemplate sourceTemplate = conferenceContext.getMcuZjBridge().getSourceTemplateById(conferenceContext.getResourceTemplateId());
            if (sourceTemplate == null || !sourceTemplate.isSupportRollCall()) {
                throw new SystemException(1005454, "该会议不支持进行点名操作！");
            }
            AttendeeForMcuZj rollCallAttendee = conferenceContext.getAttendeeById(attendeeId);
            if (rollCallAttendee != null) {
                if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                    conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
                }
                AttendeeOperation old = conferenceContext.getAttendeeOperation();
                old.cancel();
                AttendeeOperation attendeeOperation = new RollCallAttendeeOperation(conferenceContext, rollCallAttendee);
                conferenceContext.setAttendeeOperation(attendeeOperation);
                // 观众
                if (conferenceContext.getAttendeeOperationForGuest() instanceof DefaultAttendeeOperationForGuest) {
                    conferenceContext.setLastAttendeeOperationForGuest(conferenceContext.getAttendeeOperationForGuest());
                }
                AttendeeOperation oldForGuest = conferenceContext.getAttendeeOperationForGuest();
                oldForGuest.cancel();
                conferenceContext.setAttendeeOperationForGuest(attendeeOperation);
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext mainConferenceContext = McuZjConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue())
        {
            throw new SystemException(1005452, "主会场未设置，无法进行对话操作！");
        }
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            SourceTemplate sourceTemplate = conferenceContext.getMcuZjBridge().getSourceTemplateById(conferenceContext.getResourceTemplateId());
            if (sourceTemplate == null || !sourceTemplate.isSupportTalk()) {
                throw new SystemException(1005454, "该会议不支持进行对话操作！");
            }
            AttendeeForMcuZj talkAttendee = conferenceContext.getAttendeeById(attendeeId);
            if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
            }
            AttendeeOperation old = conferenceContext.getAttendeeOperation();
            if (old instanceof TalkAttendeeOperation) {
                TalkAttendeeOperation talkAttendeeOperation = (TalkAttendeeOperation) old;
                if (talkAttendeeOperation.getTalkUserId().equals(talkAttendee.getEpUserId())) {
                    return;
                }
            }
            old.cancel();
            AttendeeOperation attendeeOperation = new TalkAttendeeOperation(conferenceContext, talkAttendee);
            conferenceContext.setAttendeeOperation(attendeeOperation);
            // 观众
            if (conferenceContext.getAttendeeOperationForGuest() instanceof DefaultAttendeeOperationForGuest) {
                conferenceContext.setLastAttendeeOperationForGuest(conferenceContext.getAttendeeOperationForGuest());
            }
            AttendeeOperation oldForGuest = conferenceContext.getAttendeeOperationForGuest();
            oldForGuest.cancel();
            conferenceContext.setAttendeeOperationForGuest(attendeeOperation);
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            if (attendeeOperation instanceof RollCallAttendeeOperation) {
                attendeeOperation.cancel();
                conferenceContext.setAttendeeOperation(conferenceContext.getLastAttendeeOperation());
                // 观众
                if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                    DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) conferenceContext.getAttendeeOperation();
                    if (defaultAttendeeOperation.getDefaultViewIsBroadcast() == YesOrNo.YES.getValue()) {
                        conferenceContext.setAttendeeOperationForGuest(defaultAttendeeOperation);
                    } else {
                        conferenceContext.setAttendeeOperationForGuest(conferenceContext.getLastAttendeeOperationForGuest());
                    }
                } else {
                    conferenceContext.setAttendeeOperationForGuest(conferenceContext.getLastAttendeeOperationForGuest());
                }
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            if (attendeeOperation instanceof TalkAttendeeOperation) {
                attendeeOperation.cancel();
                conferenceContext.setAttendeeOperation(conferenceContext.getLastAttendeeOperation());
                // 观众
                if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                    DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) conferenceContext.getAttendeeOperation();
                    if (defaultAttendeeOperation.getDefaultViewIsBroadcast() == YesOrNo.YES.getValue()) {
                        conferenceContext.setAttendeeOperationForGuest(defaultAttendeeOperation);
                    } else {
                        conferenceContext.setAttendeeOperationForGuest(conferenceContext.getLastAttendeeOperationForGuest());
                    }
                } else {
                    conferenceContext.setAttendeeOperationForGuest(conferenceContext.getLastAttendeeOperationForGuest());
                }
            }
        }
    }
    
    /**
     * <pre>取消当前操作</pre>
     * @author lilinhai
     * @since 2021-02-23 16:14 
     * @param conferenceContext
     */
    public void cancelCurrentOperation(McuZjConferenceContext conferenceContext)
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
                // 观众
                if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                    DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) conferenceContext.getAttendeeOperation();
                    if (defaultAttendeeOperation.getDefaultViewIsBroadcast() == YesOrNo.YES.getValue()) {
                        conferenceContext.setAttendeeOperationForGuest(defaultAttendeeOperation);
                    } else {
                        conferenceContext.setAttendeeOperationForGuest(conferenceContext.getLastAttendeeOperationForGuest());
                    }
                } else {
                    if (!(conferenceContext.getAttendeeOperationForGuest() instanceof DefaultAttendeeOperationForGuest)) {
                        conferenceContext.setAttendeeOperationForGuest(conferenceContext.getLastAttendeeOperationForGuest());
                    }
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        logger.info("开启单个参会者混音入口：" + contextKey + ", " + attendeeId);
        McuZjConferenceContext mcuZjConferenceContext = McuZjConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
        AttendeeForMcuZj attendeeById = mcuZjConferenceContext.getAttendeeById(attendeeId);
        CcUpdateMrEpsStatusRequest ccUpdateMrEpsStatusRequest = new CcUpdateMrEpsStatusRequest();
        ccUpdateMrEpsStatusRequest.setAction(ccUpdateMrEpsStatusRequest.ACTION_a_rx);
        String[] strings = new String[1];
        strings[0] = attendeeById.getEpUserId();
        ccUpdateMrEpsStatusRequest.setUsr_ids(strings);
        ccUpdateMrEpsStatusRequest.setValue(1);
        mcuZjConferenceContext.getConferenceControlApi().updateMrEpsStatus(ccUpdateMrEpsStatusRequest);
//        new MixingAttendeeProcessor(contextKey, attendeeId, false).process();
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext mainConferenceContext = McuZjConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue())
        {
            throw new SystemException(1005454, "主会场未设置，无法进行举手操作！");
        }
        if (mainConferenceContext.getMasterAttendee().getId() == attendeeId) {
            throw new SystemException(1005454, "该参会者是主会场，无法进行举手操作！");
        }

        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            SourceTemplate sourceTemplate = conferenceContext.getMcuZjBridge().getSourceTemplateById(conferenceContext.getResourceTemplateId());
            if (sourceTemplate == null || !sourceTemplate.isSupportRollCall()) {
                throw new SystemException(1005454, "该会议不支持进行举手操作！");
            }
            AttendeeForMcuZj talkAttendee = conferenceContext.getAttendeeById(attendeeId);
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
        AttendeeForMcuZj raiseHandsAttendee = conferenceContext.getAttendeeById(attendeeId);
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
        McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(raiseHandsAttendee.getUpdateMap()));

        StringBuilder messageTip = new StringBuilder();
        messageTip.append("【").append(raiseHandsAttendee.getName()).append("】").append(raiseHandStatus.getName());

        // 消息和参会者信息同步到主级会议
        McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
        if (raiseHandsAttendee instanceof TerminalAttendeeForMcuZj)
        {
            TerminalAttendeeForMcuZj ta = (TerminalAttendeeForMcuZj) raiseHandsAttendee;
            if (TerminalCache.getInstance().get(ta.getTerminalId()).getSn() != null)
            {
                try
                {
                    mqttService.acceptRaiseHand(mainConferenceContext.getConferenceNumber(), ta);
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        RaiseHandStatus raiseHandStatus = RaiseHandStatus.NO;
        // 关闭举手
        AttendeeForMcuZj raiseHandsAttendee = conferenceContext.getAttendeeById(attendeeId);
        raiseHandsAttendee.resetUpdateMap();
        raiseHandsAttendee.setRaiseHandStatus(raiseHandStatus.getValue());
        if (RaiseHandStatus.YES == raiseHandStatus) {
            conferenceContext.setLastRaiseHandAttendeeId(attendeeId);
        } else {
            if (attendeeId.equals(conferenceContext.getLastRaiseHandAttendeeId())) {
                conferenceContext.setLastRaiseHandAttendeeId(null);
            }
        }
        McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(raiseHandsAttendee.getUpdateMap()));

        StringBuilder messageTip = new StringBuilder();
        messageTip.append("【").append(raiseHandsAttendee.getName()).append("】").append(raiseHandStatus.getName());

        // 消息和参会者信息同步到主级会议
        McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
        if (raiseHandsAttendee instanceof TerminalAttendeeForMcuZj)
        {
            TerminalAttendeeForMcuZj ta = (TerminalAttendeeForMcuZj) raiseHandsAttendee;
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeForMcuZj raiseHandsAttendee = conferenceContext.getAttendeeById(attendeeId);
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
                McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(raiseHandsAttendee.getUpdateMap()));

                StringBuilder messageTip = new StringBuilder();
                messageTip.append("【").append(raiseHandsAttendee.getName()).append("】").append(raiseHandStatus.getName());

                // 消息和参会者信息同步到主级会议
                McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);

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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
//        AttendeeInfo attendeeInfo = new AttendeeInfo(contextKey, attendeeId);
//        if (attendeeInfo.getAttendee() instanceof TerminalAttendee)
//        {
//            TerminalAttendee ta = (TerminalAttendee) attendeeInfo.getAttendee();
//            if (TerminalCache.getInstance().get(ta.getTerminalId()).getSn() != null)
//            {
//                try
//                {
//                    mqttService.setBanner(contextKey, ta, params);
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        McuZjConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            if (a instanceof TerminalAttendeeForMcuZj)
            {
                TerminalAttendeeForMcuZj ta = (TerminalAttendeeForMcuZj) a;
                if (TerminalCache.getInstance().get(ta.getTerminalId()).getSn() != null)
                {
//                    FcmThreadPool.exec(() -> {
//                        try
//                        {
//                            mqttService.setBanner(contextKey, ta, jsonObject);
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        logger.info("关闭单个参会者混音入口：" + contextKey + ", " + attendeeId);
        McuZjConferenceContext mcuZjConferenceContext = McuZjConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
        AttendeeForMcuZj attendeeById = mcuZjConferenceContext.getAttendeeById(attendeeId);
        CcUpdateMrEpsStatusRequest ccUpdateMrEpsStatusRequest = new CcUpdateMrEpsStatusRequest();
        ccUpdateMrEpsStatusRequest.setAction(ccUpdateMrEpsStatusRequest.ACTION_a_rx);
        String[] strings = new String[1];
        strings[0] = attendeeById.getEpUserId();
        ccUpdateMrEpsStatusRequest.setUsr_ids(strings);
        ccUpdateMrEpsStatusRequest.setValue(0);
        mcuZjConferenceContext.getConferenceControlApi().updateMrEpsStatus(ccUpdateMrEpsStatusRequest);
//        new MixingAttendeeProcessor(contextKey, attendeeId, true).process();
    }
    
    @Override
    public void invite(String conferenceId, List<Long> terminalIds)
    {
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
        List<AttendeeForMcuZj> attendees = new ArrayList<>();
        List<BusiTerminal> terminals = new ArrayList<>();
        for (Long terminalId : terminalIds)
        {
            BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
            if (busiTerminal != null) {
                terminals.add(busiTerminal);
            }
            TerminalAttendeeForMcuZj ta = conferenceContext.getTerminalAttendeeMap().get(terminalId);
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
                McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, ta);
                McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + ta.getName() + "】被邀请加入");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);

                mqttService.sendConferenceInfoToPushTargetTerminal(conferenceContext);
            }
        }

//        McuZjTerminalRegisterTask mcuZjTerminalRegisterTask = new McuZjTerminalRegisterTask(conferenceContext.getId(), 10, conferenceContext, terminals);
//        delayTaskService.addTask(mcuZjTerminalRegisterTask);

        recallAttendees(conferenceId, attendees);
    }

    @Override
    public void invite(String conferenceId, JSONObject jsonObj)
    {
        Assert.isTrue(jsonObj.containsKey("name"), "名字是必填参数！");
        Assert.isTrue(jsonObj.containsKey("uri"), "URI是必填参数！");
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
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

        Map<String, AttendeeForMcuZj> attendeeForMcuZjMap = conferenceContext.getAttendeeMapByUri(uri);
        if (attendeeForMcuZjMap != null && attendeeForMcuZjMap.size() > 0) {
            McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + name + "】已在会");
            return;
        }
        
        InvitedAttendeeForMcuZj ia = new InvitedAttendeeForMcuZj();
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
        McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, ia);
        McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + ia.getName() + "】被邀请加入");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
        McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
        recallAttendee(conferenceId, ia);

        mqttService.sendConferenceInfoToPushTargetTerminal(conferenceContext);
    }

    private void recallAttendee(String conferenceId, AttendeeForMcuZj attendee) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            InviteAttendeesTask inviteAttendeesTask = new InviteAttendeesTask(conferenceContext.getId(), 100, conferenceContext, attendee);
            delayTaskService.addTask(inviteAttendeesTask);
        }
    }

    private void recallAttendees(String conferenceId, List<AttendeeForMcuZj> attendees) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            InviteAttendeesTask inviteAttendeesTask = new InviteAttendeesTask(conferenceContext.getId(), 100, conferenceContext, attendees);
            delayTaskService.addTask(inviteAttendeesTask);
        }
    }

    @Override
    public void openCamera(String conferenceId, String attendeeId)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        // 打开终端摄像头（打开上行视频）
        AttendeeForMcuZj attendee = conferenceContext.getAttendeeById(attendeeId);
        if (attendee != null) {
            String userId = attendee.getEpUserId();
            if (StringUtils.hasText(userId)) {
                CcUpdateMrEpsStatusRequest ccUpdateMrEpsStatusRequest = new CcUpdateMrEpsStatusRequest();
                ccUpdateMrEpsStatusRequest.setAction(CcUpdateMrEpsStatusRequest.ACTION_v_rx);
                ccUpdateMrEpsStatusRequest.setUsr_ids(new String[]{userId});
                ccUpdateMrEpsStatusRequest.setValue(1);
                conferenceContext.getConferenceControlApi().updateMrEpsStatus(ccUpdateMrEpsStatusRequest);
            }
        }
    }

    @Override
    public void closeCamera(String conferenceId, String attendeeId)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        // 打开终端摄像头（打开上行视频）
        AttendeeForMcuZj attendee = conferenceContext.getAttendeeById(attendeeId);
        if (attendee != null) {
            String userId = attendee.getEpUserId();
            if (StringUtils.hasText(userId)) {
                CcUpdateMrEpsStatusRequest ccUpdateMrEpsStatusRequest = new CcUpdateMrEpsStatusRequest();
                ccUpdateMrEpsStatusRequest.setAction(CcUpdateMrEpsStatusRequest.ACTION_v_rx);
                ccUpdateMrEpsStatusRequest.setUsr_ids(new String[]{userId});
                ccUpdateMrEpsStatusRequest.setValue(0);
                conferenceContext.getConferenceControlApi().updateMrEpsStatus(ccUpdateMrEpsStatusRequest);
            }
        }
    }

    @Override
    public void openMixing(String conferenceId)
    {
        openMixing(McuZjConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId)));
    }
    
    public void openMixing(McuZjConferenceContext cc)
    {
        CcUpdateMrStatusRequest ccUpdateMrStatusRequest = new CcUpdateMrStatusRequest();
        ccUpdateMrStatusRequest.setMrStatusAction(CcUpdateMrStatusRequest.PARAM_all_guests_mute);
        ccUpdateMrStatusRequest.setMrStatusValue(0);
        cc.getConferenceControlApi().updateMrStatus(ccUpdateMrStatusRequest);
    }
    
    @Override
    public void openDisplayDevice(String conferenceId)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        // 允许所有人观看（打开所有下行视频）
        CcUpdateMrStatusRequest ccUpdateMrStatusRequest = new CcUpdateMrStatusRequest();
        ccUpdateMrStatusRequest.setMrStatusAction(CcUpdateMrStatusRequest.PARAM_mr_vtx_mode);
        ccUpdateMrStatusRequest.setMrStatusValue(0);
        conferenceContext.getConferenceControlApi().updateMrStatus(ccUpdateMrStatusRequest);
    }
    
    @Override
    public void closeDisplayDevice(String conferenceId)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        // 禁止所有观众观看（关闭所有下行视频）
        CcUpdateMrStatusRequest ccUpdateMrStatusRequest = new CcUpdateMrStatusRequest();
        ccUpdateMrStatusRequest.setMrStatusAction(CcUpdateMrStatusRequest.PARAM_mr_vtx_mode);
        ccUpdateMrStatusRequest.setMrStatusValue(1);
        conferenceContext.getConferenceControlApi().updateMrStatus(ccUpdateMrStatusRequest);
    }
    

    @Override
    public void openDisplayDevice(String conferenceId, String attendeeId)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        // 打开终端摄像头（打开上行视频）
        AttendeeForMcuZj attendee = conferenceContext.getAttendeeById(attendeeId);
        if (attendee != null) {
            String userId = attendee.getEpUserId();
            if (StringUtils.hasText(userId)) {
                CcUpdateMrEpsStatusRequest ccUpdateMrEpsStatusRequest = new CcUpdateMrEpsStatusRequest();
                ccUpdateMrEpsStatusRequest.setAction(CcUpdateMrEpsStatusRequest.ACTION_v_tx);
                ccUpdateMrEpsStatusRequest.setUsr_ids(new String[]{userId});
                ccUpdateMrEpsStatusRequest.setValue(1);
                conferenceContext.getConferenceControlApi().updateMrEpsStatus(ccUpdateMrEpsStatusRequest);
            }
        }
    }
    
    @Override
    public void closeDisplayDevice(String conferenceId, String attendeeId)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        // 打开终端摄像头（打开上行视频）
        AttendeeForMcuZj attendee = conferenceContext.getAttendeeById(attendeeId);
        if (attendee != null) {
            String userId = attendee.getEpUserId();
            if (StringUtils.hasText(userId)) {
                CcUpdateMrEpsStatusRequest ccUpdateMrEpsStatusRequest = new CcUpdateMrEpsStatusRequest();
                ccUpdateMrEpsStatusRequest.setAction(CcUpdateMrEpsStatusRequest.ACTION_v_tx);
                ccUpdateMrEpsStatusRequest.setUsr_ids(new String[]{userId});
                ccUpdateMrEpsStatusRequest.setValue(0);
                conferenceContext.getConferenceControlApi().updateMrEpsStatus(ccUpdateMrEpsStatusRequest);
            }
        }
    }

    @Override
    public void closeCamera(String conferenceId)
    {
        McuZjConferenceContextUtils.eachNonMcuAttendeeInConference(McuZjConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId)), (a) -> {
            if (a.isMeetingJoined())
            {
                closeCamera(conferenceId, a.getId());
            }
        });
    }
    
    @Override
    public void openCamera(String conferenceId)
    {
        McuZjConferenceContextUtils.eachNonMcuAttendeeInConference(McuZjConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId)), (a) -> {
            if (a.isMeetingJoined())
            {
                openCamera(conferenceId, a.getId());
            }
        });
    }

    @Override
    public void closeMixing(String conferenceId)
    {
        McuZjConferenceContext cc = McuZjConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
        closeMixing(cc, cc.getMasterAttendee());
    }
    
    public void closeMixing(McuZjConferenceContext cc, AttendeeForMcuZj... excludes)
    {
        CcUpdateMrStatusRequest ccUpdateMrStatusRequest = new CcUpdateMrStatusRequest();
        ccUpdateMrStatusRequest.setMrStatusAction(CcUpdateMrStatusRequest.PARAM_all_guests_mute);
        ccUpdateMrStatusRequest.setMrStatusValue(1);
        cc.getConferenceControlApi().updateMrStatus(ccUpdateMrStatusRequest);
    }
    
    @Override
    public void sendMessage(String conferenceId, JSONObject jsonObject)
    {
        McuZjConferenceContext cc = McuZjConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
        Integer messageDuration = jsonObject.getInteger("messageDuration");
        if (messageDuration == null) {
            messageDuration = 5;
        }
        Long messageCloseTime = System.currentTimeMillis() + messageDuration * 1000;
        String messagePosition = jsonObject.getString("messagePosition");
        String messageText = jsonObject.getString("messageText");
        CcSetBannerRequest ccsetBannerRequest = new CcSetBannerRequest();
        if (StringUtil.isNotEmpty(messagePosition)) {
            switch (messagePosition) {
                case "top":
                    ccsetBannerRequest.setV_align(0);
                    break;
                case "middle":
                    ccsetBannerRequest.setV_align(1);
                    break;
                case "bottom":
                    ccsetBannerRequest.setV_align(2);
                    break;
                default:
                    break;
            }
        }
        ccsetBannerRequest.setTo_role(CcSetBannerRequest.to_role_guest);
        ccsetBannerRequest.setHas_title(1);
        ccsetBannerRequest.setMessage(messageText);
        ccsetBannerRequest.setSpeed(messageDuration);
        boolean successGuest = cc.getConferenceControlApi().setBanner(ccsetBannerRequest);
        ccsetBannerRequest.setTo_role(CcSetBannerRequest.to_role_speaker);
        boolean successSpeaker = cc.getConferenceControlApi().setBanner(ccsetBannerRequest);
        ccsetBannerRequest.setTo_role(CcSetBannerRequest.to_role_chair);
        boolean successChair = cc.getConferenceControlApi().setBanner(ccsetBannerRequest);
        if (successGuest || successSpeaker || successChair) {
            cc.setMessageCloseTime(messageCloseTime);
        }
    }

    /**
     * 设置横幅
     *
     * @param conferenceId
     * @param jsonObject void
     */
    @Override
    public void setMessageBannerText(String conferenceId, JSONObject jsonObject) {
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
        setMessageBannerText(conferenceId, messageBannerText);
    }

    /**
     * 设置横幅
     *
     * @param conferenceId
     * @param text
     */
    @Override
    public void setMessageBannerText(String conferenceId, String text)
    {
        McuZjConferenceContext cc = McuZjConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
        int hasTitle = 0;
        String textW = "";
        if (StringUtil.isNotEmpty(text)) {
            hasTitle = 1;
            textW = "　" + text + "　";
        }

        CcSetBannerRequest ccsetBannerRequest = new CcSetBannerRequest();
        ccsetBannerRequest.setHas_title(hasTitle);
        if (hasTitle == 1) {
            ccsetBannerRequest.setBg_alpha(80);
            ccsetBannerRequest.setBg_color(16711680);
            ccsetBannerRequest.setFont_color(16711422);
            ccsetBannerRequest.setH_align(1);
            ccsetBannerRequest.setH_margin(10);
            ccsetBannerRequest.setV_align(0);
            ccsetBannerRequest.setV_margin(0);
            ccsetBannerRequest.setY_pos(80);
            ccsetBannerRequest.setFont_size(100);
            ccsetBannerRequest.setMessage(textW);
        } else {
            ccsetBannerRequest.setH_align(1);
            ccsetBannerRequest.setV_align(2);
            ccsetBannerRequest.setFont_size(36);
            ccsetBannerRequest.setMessage("");
        }
        ccsetBannerRequest.setTo_role(CcSetBannerRequest.to_role_guest);
        boolean successGuest = cc.getConferenceControlApi().setBanner(ccsetBannerRequest);
        ccsetBannerRequest.setTo_role(CcSetBannerRequest.to_role_speaker);
        boolean successSpeaker = cc.getConferenceControlApi().setBanner(ccsetBannerRequest);
        ccsetBannerRequest.setTo_role(CcSetBannerRequest.to_role_chair);
        boolean successChair = cc.getConferenceControlApi().setBanner(ccsetBannerRequest);
        if (hasTitle == 1) {
            redisCache.setCacheObject(cc.getConferenceNumber() + "_" + cc.getStartTime().getTime() + "_banner_text", text, 48, TimeUnit.HOURS);
        } else {
            redisCache.deleteObject(cc.getConferenceNumber() + "_" + cc.getStartTime().getTime() + "_banner_text");
        }
        if (successGuest && successSpeaker && successChair) {
            cc.setMessageBannerText(text);
            cc.setMessageCloseTime(null);
        }
    }

    @Override
    public void polling(String conferenceId)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        McuZjConferenceContext mainConferenceContext = McuZjConferenceContextCache.getInstance().getMainConferenceContext(conferenceContext);

        if (conferenceContext != null) {
            SourceTemplate sourceTemplate = conferenceContext.getMcuZjBridge().getSourceTemplateById(conferenceContext.getResourceTemplateId());
            if (sourceTemplate == null || !sourceTemplate.isSupportPolling()) {
                throw new SystemException(1005454, "该会议不支持进行轮询操作！");
            }
            BusiMcuZjTemplatePollingScheme con0 = new BusiMcuZjTemplatePollingScheme();
            con0.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
            con0.setEnableStatus(YesOrNo.YES.getValue());
            List<BusiMcuZjTemplatePollingScheme> pss = busiMcuZjTemplatePollingSchemeMapper.selectBusiMcuZjTemplatePollingSchemeList(con0);
            BusiMcuZjTemplatePollingScheme ps = null;
            for (BusiMcuZjTemplatePollingScheme busiTemplatePollingScheme : pss) {
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
            PollingScheme pollingScheme = busiMcuZjTemplatePollingSchemeService.convert(ps, conferenceContext);
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
            // 观众
            if (conferenceContext.getAttendeeOperationForGuest() instanceof DefaultAttendeeOperationForGuest) {
                conferenceContext.setLastAttendeeOperationForGuest(conferenceContext.getAttendeeOperationForGuest());
            }
            if (pollingScheme.getIsBroadcast() == YesOrNo.YES || conferenceContext.isSingleView()) {
                AttendeeOperation oldForGuest = conferenceContext.getAttendeeOperationForGuest();
                oldForGuest.cancel();
                conferenceContext.setAttendeeOperationForGuest(newAttendeeOperation);
            } else {
                if (!(conferenceContext.getAttendeeOperationForGuest() instanceof DefaultAttendeeOperationForGuest)) {
                    conferenceContext.setAttendeeOperationForGuest(conferenceContext.getLastAttendeeOperationForGuest());
                }
            }
        }
    }
    
    @Override
    public void pollingPause(String conferenceId)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            SourceTemplate sourceTemplate = conferenceContext.getMcuZjBridge().getSourceTemplateById(conferenceContext.getResourceTemplateId());
            if (sourceTemplate == null || !sourceTemplate.isSupportPolling()) {
                throw new SystemException(1005454, "该会议不支持进行轮询操作！");
            }
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
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
        McuZjAttendeeInfo ai = new McuZjAttendeeInfo(EncryptIdUtil.parasToContextKey(conferenceId), attendeeId);
        if (ai.getAttendee() != null && ai.getAttendee().isMeetingJoined())
        {
            McuZjConferenceContext mcuZjConferenceContext = McuZjConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
            Integer epId = ai.getAttendee().getEpId();
            AttendeeForMcuZj attendee = ai.getAttendee();
            CcQueryMrEpsMediaResponse mrEpsMediaInfo = mcuZjConferenceContext.getConferenceControlApi().getMrEpsMediaInfo(String.valueOf(epId));
            if (mrEpsMediaInfo != null)
            {
                JSONObject d = toDetail(mrEpsMediaInfo, attendee);
                d.put("attendeeId", attendeeId);
                return d;
            }
        }
        
        return null;
    }

    @Override
    public JSONObject detail(McuZjConferenceContext conferenceContext, AttendeeForMcuZj attendee)
    {
        if (attendee != null && attendee.isMeetingJoined())
        {
            Integer epId = attendee.getEpId();
            CcQueryMrEpsMediaResponse mrEpsMediaInfo = conferenceContext.getConferenceControlApi().getMrEpsMediaInfo(String.valueOf(epId));
            if (mrEpsMediaInfo != null)
            {
                JSONObject d = toDetail(mrEpsMediaInfo, attendee);
                d.put("attendeeId", attendee.getId());
                return d;
            }
        }

        return null;
    }

    public JSONObject toDetail(CcQueryMrEpsMediaResponse ccQueryMrEpsMediaResponse, AttendeeForMcuZj attendee)
    {
        if (ccQueryMrEpsMediaResponse != null && ccQueryMrEpsMediaResponse.getData() != null)
        {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("direction", "outgoing");
            String protoType = null;
            if (attendee.getProtoType() != null) {
                switch (attendee.getProtoType()) {
                    case 0:
                        protoType = "自动";
                        break;
                    case 1:
                        protoType = "H323";
                        break;
                    case 2:
                        protoType = "SIP";
                        break;
                    case 3:
                        protoType = "ZJCS";
                        break;
                    case 4:
                        protoType = "WebRTC";
                        break;
                    default:
                        break;
                }
            }
            jsonObj.put("type", protoType);
            jsonObj.put("isEncrypted", false);
            if (ccQueryMrEpsMediaResponse.getData().size() > 0) {
                long start_time = ccQueryMrEpsMediaResponse.getData().get(0).getStart_time();
                long l = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                long durationSeconds = l - start_time;
                jsonObj.put("durationSeconds", durationSeconds);
            } else {
                jsonObj.put("durationSeconds", 0);
            }
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
            for (CcQueryMrEpsMediaResponse.Data data : ccQueryMrEpsMediaResponse.getData()) {
                String streamType = data.getType();
                Integer rxJitter = data.getRx_jitter();
                Integer txJitter = data.getTx_jitter();
                if ("video".equals(streamType)) {
                    if (!ObjectUtils.isEmpty(txJitter)) {
                        JSONObject video = new JSONObject();
                        video.put("role", "main");
                        video.put("resolutionRatio", data.getTx_width() != null ? data.getTx_width() + "x" + data.getTx_height() : null);
                        video.put("frameRate", data.getFps());
                        video.put("videoCodec", data.getTx_codec());
                        video.put("bandwidth", data.getTx_bitrate());
                        video.put("packetLossPercentage", data.getTx_packets_lost());
                        video.put("jitter", data.getTx_jitter());
                        video.put("roundTripTime", 1);
                        downLinkVideos.add(video);
                    } else {
                        JSONObject video = new JSONObject();
                        video.put("role", "main");
                        video.put("resolutionRatio", data.getRx_width() != null ? data.getRx_width() + "x" + data.getRx_height() : null);
                        video.put("frameRate", data.getFps());
                        video.put("videoCodec", data.getRx_codec());
                        video.put("bandwidth", data.getRx_bitrate());
                        video.put("packetLossPercentage", data.getRx_packets_lost());
                        video.put("jitter", data.getRx_jitter());
                        video.put("roundTripTime", 1);
                        upLinkVideos.add(video);
                    }
                } else if ("audio".equals(streamType)) {
                    if (!ObjectUtils.isEmpty(txJitter)) {
                        downLinkAudio.put("codec", data.getTx_codec());
                        downLinkAudio.put("bandwidth", data.getTx_bitrate());
                        downLinkAudio.put("packetLossPercentage", data.getTx_packets_lost());
                        downLinkAudio.put("codecBitRate", data.getFps());
                        downLinkAudio.put("jitter", data.getTx_jitter());
                        downLinkAudio.put("roundTripTime", 1);
                        downLinkAudio.put("gainApplied", 0);
                    } else {
                        upLinkAudio.put("codec", data.getRx_codec());
                        upLinkAudio.put("bandwidth", data.getRx_bitrate());
                        upLinkAudio.put("packetLossPercentage", data.getRx_packets_lost());
                        upLinkAudio.put("codecBitRate", data.getFps());
                        upLinkAudio.put("jitter", data.getRx_jitter());
                        upLinkAudio.put("roundTripTime", 1);
                        upLinkAudio.put("gainApplied", 0);
                    }
                } else if ("content".equals(streamType)) {
                    if (!ObjectUtils.isEmpty(txJitter)) {
                        JSONObject content = new JSONObject();
                        content.put("role", "presentation");
                        content.put("resolutionRatio", data.getTx_width() != null ? data.getTx_width() + "x" + data.getTx_height() : null);
                        content.put("frameRate", data.getFps());
                        content.put("videoCodec", data.getTx_codec());
                        content.put("bandwidth", data.getTx_bitrate());
                        content.put("packetLossPercentage", data.getTx_packets_lost());
                        content.put("jitter", data.getTx_jitter());
                        content.put("roundTripTime", 1);
                        downLinkVideos.add(content);
                    } else {
                        JSONObject video = new JSONObject();
                        video.put("role", "presentation");
                        video.put("resolutionRatio", data.getRx_width() != null ? data.getRx_width() + "x" + data.getRx_height() : null);
                        video.put("frameRate", data.getFps());
                        video.put("videoCodec", data.getRx_codec());
                        video.put("bandwidth", data.getRx_bitrate());
                        video.put("packetLossPercentage", data.getRx_packets_lost());
                        video.put("jitter", data.getRx_jitter());
                        video.put("roundTripTime", 1);
                        upLinkVideos.add(video);
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
    private void updateAttendeeAttrs(String conferenceNumber, List<AttendeeForMcuZj> attendees, List<NameValuePair> nameValuePairs) {
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
//        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
//        AttendeeInfo ai = new AttendeeInfo(contextKey, attendeeId);
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
