/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.service.impls
 * @author lilinhai
 * @since 2021-02-05 17:39
 * @version  V1.0
 */
package com.paradisecloud.fcm.zte.service.impls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZteTemplatePollingSchemeMapper;
import com.paradisecloud.fcm.dao.mapper.BusiUserTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuZteTemplatePollingScheme;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.BusiUserTerminal;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.attendee.BaseFixedParamValue;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.zte.attendee.model.operation.*;
import com.paradisecloud.fcm.zte.attendee.model.polling.PollingScheme;
import com.paradisecloud.fcm.zte.attendee.utils.AttendeeUtils;
import com.paradisecloud.fcm.zte.attendee.utils.McuZteConferenceContextUtils;
import com.paradisecloud.fcm.zte.cache.AttendeeCountingStatistics;
import com.paradisecloud.fcm.zte.cache.McuZteConferenceContextCache;
import com.paradisecloud.fcm.zte.cache.McuZteWebSocketMessagePusher;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.zte.model.busi.attendee.AttendeeForMcuZte;
import com.paradisecloud.fcm.zte.model.busi.attendee.InvitedAttendeeForMcuZte;
import com.paradisecloud.fcm.zte.model.busi.attendee.TerminalAttendeeForMcuZte;
import com.paradisecloud.fcm.zte.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.zte.model.core.McuZteAttendeeInfo;
import com.paradisecloud.fcm.zte.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.zte.model.request.cc.*;
import com.paradisecloud.fcm.zte.model.response.CommonResponse;
import com.paradisecloud.fcm.zte.model.response.cc.*;
import com.paradisecloud.fcm.zte.service.interfaces.IAttendeeForMcuZteService;
import com.paradisecloud.fcm.zte.service.interfaces.IBusiMcuZteTemplatePollingSchemeService;
import com.paradisecloud.fcm.zte.task.InviteAttendeesTask;
import com.paradisecloud.fcm.zte.task.McuZteDelayTaskService;
import com.paradisecloud.fcm.zte.task.McuZteMuteStatusCheckTask;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import com.zte.m900.request.*;
import com.zte.m900.response.*;
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
public class AttendeeForMcuZteServiceImpl implements IAttendeeForMcuZteService {

    public static final String LAYOUT_TEMPLATE = "layoutTemplate";
    public static final String DEFAULT_LAYOUT = "defaultLayout";
    public static final String CHOSEN_LAYOUT = "chosenLayout";
    public static final String AUTOMATIC = "automatic";
    private Logger logger = LoggerFactory.getLogger(AttendeeForMcuZteServiceImpl.class);

    @Resource
    private BusiMcuZteTemplatePollingSchemeMapper busiMcuZteTemplatePollingSchemeMapper;

    @Resource
    private IBusiMcuZteTemplatePollingSchemeService busiMcuZteTemplatePollingSchemeService;

    @Resource
    private IMqttService mqttService;

    @Resource
    private McuZteDelayTaskService mcuZteDelayTaskService;

    @Resource
    private RedisCache redisCache;

    @Resource
    private BusiUserTerminalMapper busiUserTerminalMapper;

    private boolean isContains(AttendeeForMcuZte a, AttendeeForMcuZte... excludes)
    {
        if (!ObjectUtils.isEmpty(excludes))
        {
            for (AttendeeForMcuZte attendee : excludes)
            {
                if (a == attendee)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateAttendeeImportance(AttendeeForMcuZte attendee, AttendeeImportance attendeeImportance)
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

    @Override
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
    public Object getCameraInfo(String conferenceId, String attendeeId) {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeForMcuZte attendee = conferenceContext.getAttendeeById(attendeeId);
            if (attendee != null) {
                GetParticipantCameraInfoRequest getParticipantCameraInfoRequest = new GetParticipantCameraInfoRequest();
                getParticipantCameraInfoRequest.setConferenceIdentifier(conferenceContext.getConfId());
                getParticipantCameraInfoRequest.setTerminalIdentifier(attendee.getParticipantUuid());
                GetParticipantCameraInfoResponse participantCameraInfo = conferenceContext.getConferenceControlApi().getParticipantCameraInfo(getParticipantCameraInfoRequest);
                return  participantCameraInfo;
            }
        }

        return null;
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
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeForMcuZte attendee = conferenceContext.getAttendeeById(attendeeId);
            if (attendee != null) {
                BusiTerminal terminal = TerminalCache.getInstance().getBySn(attendee.getSn());
                if (terminal != null) {
                    if (TerminalType.isFCMSIP(terminal.getType())) {
                        BeanFactory.getBean(IMqttService.class).inviteAttendeeJoinConference(attendee, conferenceContext, AttendType.AUTO_JOIN.getValue());
                        return;
                    }
                }
                InviteAttendeesTask inviteAttendeesTask = new InviteAttendeesTask(conferenceContext.getId(), 100, conferenceContext, attendee);
                mcuZteDelayTaskService.addTask(inviteAttendeesTask);
            }
            if (attendee.isMcuAttendee()) {
                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(attendee.getCascadeConferenceId()));
                if (baseConferenceContext != null) {
                    baseConferenceContext.setUpCascadeRemoteParty(conferenceContext.getConferenceRemoteParty());
                }
            }
        }
    }

    @Override
    public void callAttendee(AttendeeForMcuZte attendee)
    {
//        new CallAttendeeProcessor(attendee).process();
    }

    @Override
    public void hangUp(String conferenceId, String attendeeId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeForMcuZte attendeeForMcuZte = conferenceContext.getAttendeeById(attendeeId);
            if (attendeeForMcuZte != null) {
                if (StringUtils.hasText(attendeeForMcuZte.getParticipantUuid())) {

                    if (conferenceContext.getAttendeeOperation() instanceof RollCallAttendeeOperation) {
                        if (attendeeForMcuZte.getCallTheRollStatus() == AttendeeCallTheRollStatus.YES.getValue()) {
                            cancelCallTheRoll(conferenceId);
                            if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                                DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) conferenceContext.getAttendeeOperation();
                                defaultAttendeeOperation.operate();
                            }
                        }
                    } else if (conferenceContext.getAttendeeOperation() instanceof TalkAttendeeOperation) {
                        if (attendeeForMcuZte.getTalkStatus() == AttendeeTalkStatus.YES.getValue()) {
                            cancelTalk(conferenceId);
                            if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                                DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) conferenceContext.getAttendeeOperation();
                                defaultAttendeeOperation.operate();
                            }
                        }
                    } else if (conferenceContext.getAttendeeOperation() instanceof ChooseSeeAttendeeOperation) {
                        if (attendeeForMcuZte.getChooseSeeStatus() == AttendeeChooseSeeStatus.YES.getValue()) {
                            cancelChooseSee(conferenceId);
                            if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                                DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) conferenceContext.getAttendeeOperation();
                                defaultAttendeeOperation.operate();
                            }
                        }
                    }

                    DisconnectParticipantRequest ccDisconnectParticipantRequest = new DisconnectParticipantRequest();
                    ccDisconnectParticipantRequest.setConferenceIdentifier(conferenceContext.getConfId());
                    ccDisconnectParticipantRequest.setTerminalIdentifier(new String[]{attendeeForMcuZte.getParticipantUuid()});
                    DisconnectParticipantResponse ccDisconnectParticipantResponse = conferenceContext.getConferenceControlApi().disconnectParticipant(ccDisconnectParticipantRequest);
                    if (ccDisconnectParticipantResponse != null && CommonResponse.STATUS_OK.equals(ccDisconnectParticipantResponse.getStatus())) {
                    }else {
                        throw new CustomException("断开连接失败");
                    }
                    attendeeForMcuZte.setHangUp(true);
                }
            }
        }
    }

    @Override
    public void remove(String conferenceId, String attendeeId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeForMcuZte attendeeForMcuZte = conferenceContext.getAttendeeById(attendeeId);
            if (attendeeForMcuZte != null) {
                if (StringUtils.hasText(attendeeForMcuZte.getParticipantUuid())) {

                    if (conferenceContext.getAttendeeOperation() instanceof RollCallAttendeeOperation) {
                        if (attendeeForMcuZte.getCallTheRollStatus() == AttendeeCallTheRollStatus.YES.getValue()) {
                            cancelCallTheRoll(conferenceId);
                            if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                                DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) conferenceContext.getAttendeeOperation();
                                defaultAttendeeOperation.operate();
                            }
                        }
                    } else if (conferenceContext.getAttendeeOperation() instanceof TalkAttendeeOperation) {
                        if (attendeeForMcuZte.getTalkStatus() == AttendeeTalkStatus.YES.getValue()) {
                            cancelTalk(conferenceId);
                            if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                                DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) conferenceContext.getAttendeeOperation();
                                defaultAttendeeOperation.operate();
                            }
                        }
                    } else if (conferenceContext.getAttendeeOperation() instanceof ChooseSeeAttendeeOperation) {
                        if (attendeeForMcuZte.getChooseSeeStatus() == AttendeeChooseSeeStatus.YES.getValue()) {
                            cancelChooseSee(conferenceId);
                            if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                                DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) conferenceContext.getAttendeeOperation();
                                defaultAttendeeOperation.operate();
                            }
                        }
                    }

                    String uuid = conferenceContext.getDisconnectedParticipantUuidByRemoteParty(attendeeForMcuZte.getRemoteParty());
//                    if (StringUtils.hasText(uuid) && !uuid.equals(attendeeForMcuZte.getParticipantUuid())) {
//                        DeleteParticipantRequest ccDeleteMrTerminalRequest = new DeleteParticipantRequest();
//                        ccDeleteMrTerminalRequest.setConferenceIdentifier(conferenceContext.getConfId());
//                        ccDeleteMrTerminalRequest.setTerminalIdentifier(new String[]{uuid});
//                        conferenceContext.getConferenceControlApi().deleteParticipant(ccDeleteMrTerminalRequest);
//                    }
                    DeleteParticipantRequest ccDeleteMrTerminalRequest = new DeleteParticipantRequest();
                    ccDeleteMrTerminalRequest.setConferenceIdentifier(conferenceContext.getConfId());
                    ccDeleteMrTerminalRequest.setTerminalIdentifier(new String[]{attendeeForMcuZte.getParticipantUuid()});
                    DeleteParticipantResponse deleteParticipantResponse = conferenceContext.getConferenceControlApi().deleteParticipant(ccDeleteMrTerminalRequest);
                    if(deleteParticipantResponse!=null&&CommonResponse.STATUS_OK.equals(deleteParticipantResponse.getStatus())){
                        conferenceContext.setDeletedParticipant(attendeeForMcuZte.getParticipantUuid(),attendeeForMcuZte);
                    }
                    attendeeForMcuZte.setMeetingStatus(AttendeeMeetingStatus.OUT.getValue());
                }
                if (attendeeForMcuZte.getMeetingStatus() == AttendeeMeetingStatus.OUT.getValue()) {
                    conferenceContext.removeAttendeeById(attendeeId);

                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("id", attendeeForMcuZte.getId());
                    updateMap.put("deptId", attendeeForMcuZte.getDeptId());
                    updateMap.put("mcuAttendee", attendeeForMcuZte.isMcuAttendee());
                    McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                    McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                }
            }
        }
    }

    @Override
    public String takeSnapshot(String conferenceId, String attendeeId, JSONObject params)
    {

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
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        AttendeeForMcuZte attendeeForMcuZte = conferenceContext.getAttendeeById(attendeeId);
        if (attendeeForMcuZte != null) {

            ControlParticipantCameraRequest controlParticipantCameraRequest = new ControlParticipantCameraRequest();
            controlParticipantCameraRequest.setConferenceIdentifier(conferenceContext.getConfId());
            controlParticipantCameraRequest.setTerminalIdentifier(attendeeForMcuZte.getParticipantUuid());
            controlParticipantCameraRequest.setControlType(params.getIntValue("controlType"));
            controlParticipantCameraRequest.setParam1(params.getIntValue("param1"));

            ControlParticipantCameraResponse controlParticipantCameraResponse = conferenceContext.getConferenceControlApi().controlParticipantCamera(controlParticipantCameraRequest);
            if(controlParticipantCameraResponse!=null&&CommonResponse.STATUS_OK.equals(controlParticipantCameraResponse.getResult())){
                McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendeeForMcuZte.getName() + "】镜头调整成功！");
            }
        }
    }

    /**
     * 变更主会场
     * @author lilinhai
     * @since 2021-02-09 11:32  void
     */
    @Override
    public void changeMaster(String conferenceId, String attendeeId)
    {
        if (StringUtil.isEmpty(attendeeId)) {
            return;
        }
        McuZteConferenceContext mcuZteConferenceContext = McuZteConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId));
        AttendeeForMcuZte attendeeForMcuZte = mcuZteConferenceContext.getAttendeeById(attendeeId);
        if (attendeeForMcuZte != null) {
            if (mcuZteConferenceContext.isDownCascadeConference()) {
                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(mcuZteConferenceContext.getUpCascadeConferenceId()));
                if (baseConferenceContext != null) {
                    if (!attendeeForMcuZte.getRemoteParty().equals(mcuZteConferenceContext.getUpCascadeRemoteParty())) {
                        throw new SystemException(1005454, "该会议正被级联，不允许修改主会场！");
                    }
                }
            }
            AttendeeForMcuZte oldMasterAttendee = mcuZteConferenceContext.getMasterAttendee();
            if (oldMasterAttendee == null || !attendeeForMcuZte.getId().equals(oldMasterAttendee.getId())) {
                mcuZteConferenceContext.setMasterAttendee(attendeeForMcuZte);
                redisCache.setCacheObject(mcuZteConferenceContext.getId() + "_" + "_master_attendee", attendeeForMcuZte.getId(), 48, TimeUnit.HOURS);
                if (mcuZteConferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                    mcuZteConferenceContext.setLastAttendeeOperation(mcuZteConferenceContext.getAttendeeOperation());
                }
                AttendeeOperation old = mcuZteConferenceContext.getAttendeeOperation();
                old.cancel();
                AttendeeOperation attendeeOperation = new ChangeMasterAttendeeOperation(mcuZteConferenceContext);
                mcuZteConferenceContext.setAttendeeOperation(attendeeOperation);

                StringBuilder messageTip = new StringBuilder();
                messageTip.append("主会场已切换至【").append(attendeeForMcuZte.getName()).append("】");
                Map<String, Object> data = new HashMap<>();
                data.put("oldMasterAttendee", oldMasterAttendee);
                data.put("newMasterAttendee", attendeeForMcuZte);
                McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(mcuZteConferenceContext, WebsocketMessageType.MASTER_CHANGED, data);
                McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(mcuZteConferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
            }
        }
    }

    /**
     * 选看
     * @author lilinhai
     * @since 2021-02-09 11:32  void
     */
    @Override
    public void chooseSee(String conferenceId, String attendeeId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuZteConferenceContext mainConferenceContext = McuZteConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue())
        {
            throw new SystemException(1005454, "主会场未设置，无法进行选看操作！");
        }

        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeForMcuZte chooseSeeAttendee = conferenceContext.getAttendeeById(attendeeId);
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
    @Override
    public void chooseSee(String conferenceId, String attendeeId, boolean upCascadeOperate, boolean upCascadeBroadcast, boolean upCascadePolling, boolean upCascadeRollCall)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuZteConferenceContext mainConferenceContext = McuZteConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue())
        {
            throw new SystemException(1005454, "主会场未设置，无法进行选看操作！");
        }

        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeForMcuZte chooseSeeAttendee = conferenceContext.getAttendeeById(attendeeId);
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
    @Override
    public void defaultChooseSee(McuZteConferenceContext mainConferenceContext)
    {
        if (mainConferenceContext.getMasterAttendee() == null || !mainConferenceContext.getMasterAttendee().isMeetingJoined())
        {
            return;
        }

        // 只有当操作是默认的选看时，才会执行该逻辑
        AttendeeForMcuZte a = McuZteConferenceContextUtils.getDefaultChooseToSee(mainConferenceContext);
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
        McuZteConferenceContext mainConferenceContext = McuZteConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue())
        {
            throw new SystemException(1005454, "主会场未设置，无法进行点名操作！");
        }

        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeForMcuZte rollCallAttendee = conferenceContext.getAttendeeById(attendeeId);
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
        McuZteConferenceContext mainConferenceContext = McuZteConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue())
        {
            throw new SystemException(1005452, "主会场未设置，无法进行对话操作！");
        }
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeForMcuZte talkAttendee = conferenceContext.getAttendeeById(attendeeId);
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
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
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
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
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
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
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
    @Override
    public void cancelCurrentOperation(McuZteConferenceContext conferenceContext)
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
    @Override
    public void openMixing(String conferenceId, String attendeeId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        logger.info("开启单个参会者混音入口：" + contextKey + ", " + attendeeId);
        McuZteConferenceContext mcuZteConferenceContext = McuZteConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId));
        AttendeeForMcuZte attendeeForMcuZte = mcuZteConferenceContext.getAttendeeById(attendeeId);
        if (attendeeForMcuZte != null && StringUtils.hasText(attendeeForMcuZte.getParticipantUuid())) {

            CancelMuteParticipantRequest cancelMuteParticipantRquest = new CancelMuteParticipantRequest();
            cancelMuteParticipantRquest.setConferenceIdentifier(mcuZteConferenceContext.getConfId());
            cancelMuteParticipantRquest.setTerminalIdentifier(attendeeForMcuZte.getParticipantUuid());
            CancelMuteParticipantResponse ccCancelMuteParticipantReponse = mcuZteConferenceContext.getConferenceControlApi().cancelMuteParticipant(cancelMuteParticipantRquest);
            if (ccCancelMuteParticipantReponse != null && CommonResponse.STATUS_OK.equals(ccCancelMuteParticipantReponse.getStatus())) {
                attendeeForMcuZte.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(mcuZteConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZte.getUpdateMap());
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
    @Override
    public void acceptRaiseHand(String conferenceId, String attendeeId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuZteConferenceContext mainConferenceContext = McuZteConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue())
        {
            throw new SystemException(1005454, "主会场未设置，无法进行举手操作！");
        }
        if (mainConferenceContext.getMasterAttendee().getId() == attendeeId) {
            throw new SystemException(1005454, "该参会者是主会场，无法进行举手操作！");
        }

        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeForMcuZte talkAttendee = conferenceContext.getAttendeeById(attendeeId);
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
        AttendeeForMcuZte raiseHandsAttendee = conferenceContext.getAttendeeById(attendeeId);
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
        McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(raiseHandsAttendee.getUpdateMap()));

        StringBuilder messageTip = new StringBuilder();
        messageTip.append("【").append(raiseHandsAttendee.getName()).append("】").append(raiseHandStatus.getName());

        // 消息和参会者信息同步到主级会议
        McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
        if (raiseHandsAttendee instanceof TerminalAttendeeForMcuZte)
        {
            TerminalAttendeeForMcuZte ta = (TerminalAttendeeForMcuZte) raiseHandsAttendee;
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
    @Override
    public void rejectRaiseHand(String conferenceId, String attendeeId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        RaiseHandStatus raiseHandStatus = RaiseHandStatus.NO;
        // 关闭举手
        AttendeeForMcuZte raiseHandsAttendee = conferenceContext.getAttendeeById(attendeeId);
        raiseHandsAttendee.resetUpdateMap();
        raiseHandsAttendee.setRaiseHandStatus(raiseHandStatus.getValue());
        if (RaiseHandStatus.YES == raiseHandStatus) {
            conferenceContext.setLastRaiseHandAttendeeId(attendeeId);
        } else {
            if (attendeeId.equals(conferenceContext.getLastRaiseHandAttendeeId())) {
                conferenceContext.setLastRaiseHandAttendeeId(null);
            }
        }
        McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(raiseHandsAttendee.getUpdateMap()));

        StringBuilder messageTip = new StringBuilder();
        messageTip.append("【").append(raiseHandsAttendee.getName()).append("】").append(raiseHandStatus.getName());

        // 消息和参会者信息同步到主级会议
        McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
        if (raiseHandsAttendee instanceof TerminalAttendeeForMcuZte)
        {
            TerminalAttendeeForMcuZte ta = (TerminalAttendeeForMcuZte) raiseHandsAttendee;
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
    @Override
    public void raiseHand(String conferenceId, String attendeeId, RaiseHandStatus raiseHandStatus)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeForMcuZte raiseHandsAttendee = conferenceContext.getAttendeeById(attendeeId);
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
                McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(raiseHandsAttendee.getUpdateMap()));

                StringBuilder messageTip = new StringBuilder();
                messageTip.append("【").append(raiseHandsAttendee.getName()).append("】").append(raiseHandStatus.getName());

                // 消息和参会者信息同步到主级会议
                McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);

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
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        McuZteConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            if (a instanceof TerminalAttendeeForMcuZte)
            {
                TerminalAttendeeForMcuZte ta = (TerminalAttendeeForMcuZte) a;
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
    @Override
    public void closeMixing(String conferenceId, String attendeeId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        logger.info("关闭单个参会者混音入口：" + contextKey + ", " + attendeeId);
        McuZteConferenceContext mcuZteConferenceContext = McuZteConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId));
        AttendeeForMcuZte attendeeForMcuZte = mcuZteConferenceContext.getAttendeeById(attendeeId);
        if (attendeeForMcuZte != null && StringUtils.hasText(attendeeForMcuZte.getParticipantUuid())) {
            MuteParticipantRequest ccMuteParticipantRequest = new MuteParticipantRequest();
            ccMuteParticipantRequest.setConferenceIdentifier(mcuZteConferenceContext.getConfId());
            ccMuteParticipantRequest.setTerminalIdentifier(attendeeForMcuZte.getParticipantUuid());
            MuteParticipantResponse ccMuteParticipantRequestResponse = mcuZteConferenceContext.getConferenceControlApi().muteParticipant(ccMuteParticipantRequest);
            if (ccMuteParticipantRequestResponse != null && CommonResponse.STATUS_OK.equals(ccMuteParticipantRequestResponse.getStatus())) {
                attendeeForMcuZte.setMixingStatus(AttendeeMixingStatus.NO.getValue());
                McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(mcuZteConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZte.getUpdateMap());
            }

        }
    }

    @Override
    public void invite(String conferenceId, List<Long> terminalIds)
    {
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId));
        List<AttendeeForMcuZte> attendees = new ArrayList<>();
        List<BusiTerminal> terminals = new ArrayList<>();
        for (Long terminalId : terminalIds)
        {
            BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
            if (busiTerminal != null) {
                terminals.add(busiTerminal);
            }
            TerminalAttendeeForMcuZte ta = conferenceContext.getTerminalAttendeeMap().get(terminalId);
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
                McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, ta);
                McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + ta.getName() + "】被邀请加入");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);

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
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId));
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

        Map<String, AttendeeForMcuZte> attendeeForMcuZteMap = conferenceContext.getAttendeeMapByUri(uri);
        if (attendeeForMcuZteMap != null && attendeeForMcuZteMap.size() > 0) {
            McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + name + "】已在会");
            return;
        }

        InvitedAttendeeForMcuZte ia = new InvitedAttendeeForMcuZte();
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
        McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, ia);
        McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + ia.getName() + "】被邀请加入");
        recallAttendee(conferenceId, ia);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);

        mqttService.sendConferenceInfoToPushTargetTerminal(conferenceContext);
    }

    private void recallAttendee(String conferenceId, AttendeeForMcuZte attendee) {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            InviteAttendeesTask inviteAttendeesTask = new InviteAttendeesTask(conferenceContext.getId(), 100, conferenceContext, attendee);
            mcuZteDelayTaskService.addTask(inviteAttendeesTask);
        }
    }

    private void recallAttendees(String conferenceId, List<AttendeeForMcuZte> attendees) {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            InviteAttendeesTask inviteAttendeesTask = new InviteAttendeesTask(conferenceContext.getId(), 100, conferenceContext, attendees);
            mcuZteDelayTaskService.addTask(inviteAttendeesTask);
        }
    }

    @Override
    public void openCamera(String conferenceId, String attendeeId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        // 打开终端摄像头（打开上行视频）
        AttendeeForMcuZte attendeeForMcuZte = conferenceContext.getAttendeeById(attendeeId);
        if (attendeeForMcuZte != null && StringUtils.hasText(attendeeForMcuZte.getParticipantUuid())) {
            SetParticipantVideoSendRequest setParticipantVideoSendRequest = new SetParticipantVideoSendRequest();
            setParticipantVideoSendRequest.setConferenceIdentifier(conferenceContext.getConfId());
            setParticipantVideoSendRequest.setTerminalIdentifier(attendeeForMcuZte.getParticipantUuid());
            setParticipantVideoSendRequest.setValue(1);
            SetParticipantVideoSendResponse ccSetParticipantVideoSendResponse = conferenceContext.getConferenceControlApi().setParticipantVideoSendRequest(setParticipantVideoSendRequest);
            if (ccSetParticipantVideoSendResponse != null && CommonResponse.STATUS_OK.equals(ccSetParticipantVideoSendResponse.getStatus())) {
                attendeeForMcuZte.setVideoStatus(AttendeeVideoStatus.YES.getValue());
                McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZte.getUpdateMap());
            }
        }
    }

    @Override
    public void closeCamera(String conferenceId, String attendeeId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        // 打开终端摄像头（打开上行视频）
        AttendeeForMcuZte attendeeForMcuZte = conferenceContext.getAttendeeById(attendeeId);
        if (attendeeForMcuZte != null && StringUtils.hasText(attendeeForMcuZte.getParticipantUuid())) {
            SetParticipantVideoSendRequest setParticipantVideoSendRequest = new SetParticipantVideoSendRequest();
            setParticipantVideoSendRequest.setConferenceIdentifier(conferenceContext.getConfId());
            setParticipantVideoSendRequest.setTerminalIdentifier(attendeeForMcuZte.getParticipantUuid());
            setParticipantVideoSendRequest.setValue(0);
            SetParticipantVideoSendResponse ccSetParticipantVideoSendResponse = conferenceContext.getConferenceControlApi().setParticipantVideoSendRequest(setParticipantVideoSendRequest);
            if (ccSetParticipantVideoSendResponse != null && CommonResponse.STATUS_OK.equals(ccSetParticipantVideoSendResponse.getStatus())) {
                attendeeForMcuZte.setVideoStatus(AttendeeVideoStatus.NO.getValue());
                McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZte.getUpdateMap());
            }
        }
    }

    @Override
    public void openMixing(String conferenceId)
    {
        openMixing(McuZteConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId)));
    }

    @Override
    public void openMixing(McuZteConferenceContext cc)
    {

        CancelMuteParticipantRequest cancelMuteParticipantRequest = new CancelMuteParticipantRequest();
        cancelMuteParticipantRequest.setConferenceIdentifier(cc.getConfId());
        CancelMuteParticipantResponse cancelMuteParticipantResponse = cc.getConferenceControlApi().cancelMuteParticipant(cancelMuteParticipantRequest);
        if (cancelMuteParticipantResponse != null && CommonResponse.STATUS_OK.equals(cancelMuteParticipantResponse.getStatus())) {
            McuZteConferenceContextUtils.eachAttendeeInConference(cc, (attendee) -> {
                if (attendee.isMeetingJoined()) {
                    if (AttendeeMixingStatus.NO.getValue() == attendee.getMixingStatus()) {
                        attendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                        McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(cc, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                    }
                }
            });
        }
    }

    @Override
    public void openDisplayDevice(String conferenceId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        // 允许所有人观看（打开所有下行视频）
    }

    @Override
    public void closeDisplayDevice(String conferenceId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        // 禁止所有观众观看（关闭所有下行视频）
    }


    @Override
    public void openDisplayDevice(String conferenceId, String attendeeId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        // 打开终端摄像头（打开上行视频）
    }

    @Override
    public void closeDisplayDevice(String conferenceId, String attendeeId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        // 打开终端摄像头（打开上行视频）
    }

    @Override
    public void closeCamera(String conferenceId)
    {
        McuZteConferenceContextUtils.eachNonMcuAttendeeInConference(McuZteConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId)), (a) -> {
            if (a.isMeetingJoined())
            {
                closeCamera(conferenceId, a.getId());
            }
        });
    }

    @Override
    public void openCamera(String conferenceId)
    {
        McuZteConferenceContextUtils.eachNonMcuAttendeeInConference(McuZteConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId)), (a) -> {
            if (a.isMeetingJoined())
            {
                openCamera(conferenceId, a.getId());
            }
        });
    }

    @Override
    public void closeMixing(String conferenceId)
    {
        McuZteConferenceContext cc = McuZteConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId));
        closeMixing(cc, cc.getMasterAttendee());
    }

    @Override
    public void closeMixing(McuZteConferenceContext cc, AttendeeForMcuZte... excludes)
    {

            Set<String> excludeIdSet = new HashSet<>();
            for (AttendeeForMcuZte attendeeForMcuZte : excludes) {
                if (attendeeForMcuZte != null) {
                    excludeIdSet.add(attendeeForMcuZte.getId());
                }
            }
            McuZteConferenceContextUtils.eachAttendeeInConference(cc, (attendee) -> {
                if (AttendeeMixingStatus.YES.getValue() == attendee.getMixingStatus()) {
                    if (!excludeIdSet.contains(attendee.getId())) {

                        MuteParticipantRequest ccMuteParticipantRequest = new MuteParticipantRequest();
                        ccMuteParticipantRequest.setConferenceIdentifier(cc.getConfId());
                        ccMuteParticipantRequest.setTerminalIdentifier(attendee.getParticipantUuid());
                        MuteParticipantResponse ccMuteParticipantRequestResponse = cc.getConferenceControlApi().muteParticipant(ccMuteParticipantRequest);
                        if (ccMuteParticipantRequestResponse != null && CommonResponse.STATUS_OK.equals(ccMuteParticipantRequestResponse.getStatus())) {
                            attendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
                            McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(cc, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                        }

                    }
                }
            });


    }

    @Override
    public void sendMessage(String conferenceId, JSONObject jsonObject)
    {
        McuZteConferenceContext cc = McuZteConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId));
        Integer messageDuration = jsonObject.getInteger("messageDuration");
        if (messageDuration == null) {
            messageDuration = 5;
        }
        Long messageCloseTime = System.currentTimeMillis() + messageDuration * 1000;
        String messagePosition = jsonObject.getString("messagePosition");
        String messageText = jsonObject.getString("messageText");
        SendMcuTitleRequest ccsetBannerRequest = new SendMcuTitleRequest();
        ccsetBannerRequest.setConferenceIdentifier(cc.getConfId());
        if (StringUtil.isNotEmpty(messagePosition)) {
            switch (messagePosition) {
                case "top":
                    ccsetBannerRequest.setPosition(2);
                    break;
                case "middle":
                    ccsetBannerRequest.setPosition(0);
                    break;
                case "bottom":
                    ccsetBannerRequest.setPosition(5);
                    break;
                default:
                    break;
            }
        }

        ccsetBannerRequest.setType(1);
        ccsetBannerRequest.setSendMode(1);
        ccsetBannerRequest.setFontSize(12);
        ccsetBannerRequest.setFontColor(37);
        ccsetBannerRequest.setBGColor(48);
        ccsetBannerRequest.setContent(messageText);

        SendMcuTitleResponse response = cc.getConferenceControlApi().sendMcuTitle(ccsetBannerRequest);
        if (CommonResponse.STATUS_OK.equals(response.getResult())) {
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
        McuZteConferenceContext cc = McuZteConferenceContextCache.getInstance().get(contextKey);

        SendMcuTitleRequest ccsetBannerRequest = new SendMcuTitleRequest();
        ccsetBannerRequest.setConferenceIdentifier(cc.getConfId());
        ccsetBannerRequest.setType(2);
        String textW = "";
        if (StringUtil.isNotEmpty(text)) {
            textW = "　" + text + "　";
            ccsetBannerRequest.setSendMode(1);
        }else {
            ccsetBannerRequest.setSendMode(0);
        }

        ccsetBannerRequest.setFontSize(12);
        ccsetBannerRequest.setFontColor(37);
        ccsetBannerRequest.setBGColor(41);
        ccsetBannerRequest.setContent(textW);

        SendMcuTitleResponse response = cc.getConferenceControlApi().sendMcuTitle(ccsetBannerRequest);
        if (CommonResponse.STATUS_OK.equals(response.getResult())) {
            cc.setMessageBannerText(text);
            cc.setMessageCloseTime(null);
        }
    }

    @Override
    public void polling(String conferenceId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        McuZteConferenceContext mainConferenceContext = McuZteConferenceContextCache.getInstance().getMainConferenceContext(conferenceContext);

        if (conferenceContext != null) {
            BusiMcuZteTemplatePollingScheme con0 = new BusiMcuZteTemplatePollingScheme();
            con0.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
            con0.setEnableStatus(YesOrNo.YES.getValue());
            List<BusiMcuZteTemplatePollingScheme> pss = busiMcuZteTemplatePollingSchemeMapper.selectBusiMcuZteTemplatePollingSchemeList(con0);
            BusiMcuZteTemplatePollingScheme ps = null;
            for (BusiMcuZteTemplatePollingScheme busiTemplatePollingScheme : pss) {
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
            PollingScheme pollingScheme = busiMcuZteTemplatePollingSchemeService.convert(ps, conferenceContext);
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
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
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
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
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
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
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
//        McuZteAttendeeInfo ai = new McuZteAttendeeInfo(EncryptIdUtil.parasToContextKey(conferenceId), attendeeId);
//        if (ai.getAttendee() != null && ai.getAttendee().isMeetingJoined())
//        {
//            McuZteConferenceContext mcuZteConferenceContext = McuZteConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
//            CcGetTerminalInfoRequest ccGetTerminalInfoRequest = new CcGetTerminalInfoRequest();
//            ccGetTerminalInfoRequest.setId(mcuZteConferenceContext.getConfId());
//            ccGetTerminalInfoRequest.setParty_id(ai.getAttendee().getParticipantUuid());
//            CcGetTerminalInfoResponse ccGetTerminalInfoResponse = mcuZteConferenceContext.getConferenceControlApi().getCcTerminalInfo(ccGetTerminalInfoRequest);
//            if (ccGetTerminalInfoResponse != null && CommonResponse.STATUS_OK.equals(ccGetTerminalInfoResponse.getStatus()))
//            {
//                JSONObject d = toDetail(ccGetTerminalInfoResponse, ai.getAttendee(), mcuZteConferenceContext);
//                d.put("attendeeId", ai.getAttendee().getId());
//                return d;
//            }
//        }

        return null;
    }

    @Override
    public JSONObject detail(McuZteConferenceContext conferenceContext, AttendeeForMcuZte attendee)
    {
//        if (attendee != null && attendee.isMeetingJoined())
//        {
//            CcGetTerminalInfoRequest ccGetTerminalInfoRequest = new CcGetTerminalInfoRequest();
//            ccGetTerminalInfoRequest.setId(conferenceContext.getConfId());
//            ccGetTerminalInfoRequest.setParty_id(attendee.getParticipantUuid());
//            CcGetTerminalInfoResponse ccGetTerminalInfoResponse = conferenceContext.getConferenceControlApi().getCcTerminalInfo(ccGetTerminalInfoRequest);
//            if (ccGetTerminalInfoResponse != null && CommonResponse.STATUS_OK.equals(ccGetTerminalInfoResponse.getStatus()))
//            {
//                JSONObject d = toDetail(ccGetTerminalInfoResponse, attendee, conferenceContext);
//                d.put("attendeeId", attendee.getId());
//                return d;
//            }
//        }

        return null;
    }

    public JSONObject toDetail(CcGetTerminalInfoResponse ccGetTerminalInfoResponse, AttendeeForMcuZte attendee, McuZteConferenceContext conferenceContext)
    {
        if (ccGetTerminalInfoResponse != null && CommonResponse.STATUS_OK.equals(ccGetTerminalInfoResponse.getStatus()))
        {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("direction", attendee.getDirection());
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
                    default:
                        break;
                }
            }
            jsonObj.put("type", protoType);
            jsonObj.put("isEncrypted", false);
            int durationSeconds = 0;
            Date joinedTime = attendee.getJoinedTime();
            if (joinedTime != null) {
                long diffTime = 0;
                if (conferenceContext.getMcuZteBridge() != null) {
                    diffTime = conferenceContext.getMcuZteBridge().getDiffTime();
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
                JSONObject video = new JSONObject();
                video.put("role", "main");
                video.put("resolutionRatio", ccGetTerminalInfoResponse.getVideo_out_resolution());
                video.put("frameRate", ccGetTerminalInfoResponse.getVideo_out_video_frame_rate());
                video.put("videoCodec", ccGetTerminalInfoResponse.getVideo_out_protocol());
                video.put("bandwidth", ccGetTerminalInfoResponse.getVideo_out_bit_rate());
                video.put("packetLossPercentage", ccGetTerminalInfoResponse.getVideo_out_packet_loss());
                video.put("jitter", ccGetTerminalInfoResponse.getVideo_out_jitter());
                video.put("roundTripTime", 1);
                downLinkVideos.add(video);
            }
            // 上行视频
            {
                JSONObject video = new JSONObject();
                video.put("role", "main");
                video.put("resolutionRatio", ccGetTerminalInfoResponse.getVideo_in_resolution());
                video.put("frameRate", ccGetTerminalInfoResponse.getVideo_in_video_frame_rate());
                video.put("videoCodec", ccGetTerminalInfoResponse.getVideo_in_protocol());
                video.put("bandwidth", ccGetTerminalInfoResponse.getVideo_in_bit_rate());
                video.put("packetLossPercentage", ccGetTerminalInfoResponse.getVideo_in_packet_loss());
                video.put("jitter", ccGetTerminalInfoResponse.getVideo_in_jitter());
                video.put("roundTripTime", 1);
                upLinkVideos.add(video);
            }
            // 下行音频
            {
                downLinkAudio.put("codec", ccGetTerminalInfoResponse.getAudio_out_protocol());
                downLinkAudio.put("bandwidth", ccGetTerminalInfoResponse.getAudio_out_bit_rate());
                downLinkAudio.put("packetLossPercentage", ccGetTerminalInfoResponse.getAudio_out_packet_loss());
                downLinkAudio.put("codecBitRate", ccGetTerminalInfoResponse.getAudio_out_bit_rate());
                downLinkAudio.put("jitter", ccGetTerminalInfoResponse.getAudio_out_jitter());
                downLinkAudio.put("roundTripTime", 1);
                downLinkAudio.put("gainApplied", 0);
            }
            // 上行音频
            {
                upLinkAudio.put("codec", ccGetTerminalInfoResponse.getAudio_in_protocol());
                upLinkAudio.put("bandwidth", ccGetTerminalInfoResponse.getAudio_in_bit_rate());
                upLinkAudio.put("packetLossPercentage", ccGetTerminalInfoResponse.getAudio_in_packet_loss());
                upLinkAudio.put("codecBitRate", ccGetTerminalInfoResponse.getAudio_in_bit_rate());
                upLinkAudio.put("jitter", ccGetTerminalInfoResponse.getAudio_in_jitter());
                upLinkAudio.put("roundTripTime", 1);
                upLinkAudio.put("gainApplied", 0);
            }
            // 下行辅流
            if (!ObjectUtils.isEmpty(ccGetTerminalInfoResponse.getVideo_content_out_jitter())) {
                JSONObject content = new JSONObject();
                content.put("role", "presentation");
                content.put("resolutionRatio", ccGetTerminalInfoResponse.getVideo_content_out_resolution());
                content.put("frameRate", ccGetTerminalInfoResponse.getVideo_content_out_video_frame_rate());
                content.put("videoCodec", ccGetTerminalInfoResponse.getVideo_content_out_protocol());
                content.put("bandwidth", ccGetTerminalInfoResponse.getVideo_content_out_bit_rate());
                content.put("packetLossPercentage", ccGetTerminalInfoResponse.getVideo_content_out_packet_loss());
                content.put("jitter", ccGetTerminalInfoResponse.getVideo_content_out_jitter());
                content.put("roundTripTime", 1);
                downLinkVideos.add(content);
            }
            // 上行辅流
            if (!ObjectUtils.isEmpty(ccGetTerminalInfoResponse.getVideo_content_in_jitter())) {
                JSONObject video = new JSONObject();
                video.put("role", "presentation");
                video.put("resolutionRatio", ccGetTerminalInfoResponse.getVideo_content_in_resolution());
                video.put("frameRate", ccGetTerminalInfoResponse.getVideo_content_in_video_frame_rate());
                video.put("videoCodec", ccGetTerminalInfoResponse.getVideo_content_in_protocol());
                video.put("bandwidth", ccGetTerminalInfoResponse.getVideo_content_in_bit_rate());
                video.put("packetLossPercentage", ccGetTerminalInfoResponse.getVideo_content_in_packet_loss());
                video.put("jitter", ccGetTerminalInfoResponse.getVideo_content_in_jitter());
                video.put("roundTripTime", 1);
                upLinkVideos.add(video);
            }
            return jsonObj;
        }
        return null;
    }


    @Override
    public void openSpeaker(String conferenceId, String attendeeId) {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeForMcuZte attendee = conferenceContext.getAttendeeById(attendeeId);
            if (attendee != null) {
                CancelQuietParticipantRequest request = new CancelQuietParticipantRequest();
                request.setConferenceIdentifier(conferenceContext.getConfId());
                request.setTerminalIdentifier(attendee.getParticipantUuid());
                CancelQuietParticipantResponse cancelQuietParticipantResponse = conferenceContext.getConferenceControlApi().cancelQuietParticipant(request);
            }
        }
    }

    @Override
    public void closeSpeaker(String conferenceId, String attendeeId) {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeForMcuZte attendee = conferenceContext.getAttendeeById(attendeeId);
            if (attendee != null) {
                QuietParticipantRequest request = new QuietParticipantRequest();
                request.setConferenceIdentifier(conferenceContext.getConfId());
                request.setTerminalIdentifier(attendee.getParticipantUuid());
                QuietParticipantResponse quietParticipantResponse = conferenceContext.getConferenceControlApi().quietParticipant(request);
            }
        }
    }

    /**
     * 与会者呼叫失败通知
     * @author lilinhai
     * @since 2021-02-08 13:49
     * @param participantUuid void
     */
    @Override
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
    private void updateAttendeeAttrs(String conferenceNumber, List<AttendeeForMcuZte> attendees, List<NameValuePair> nameValuePairs) {
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
