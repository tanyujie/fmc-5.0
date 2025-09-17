/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.service.impls
 * @author lilinhai
 * @since 2021-02-05 17:29
 * @version  V1.0
 */
package com.paradisecloud.fcm.smc2.setvice2.impls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.google.common.net.InetAddresses;
import com.paradisecloud.com.fcm.smc.modle.*;
import com.paradisecloud.com.fcm.smc.modle.request.TextTipsSetting;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.AttendeeMeetingStatus;
import com.paradisecloud.fcm.common.enumer.RaiseHandStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.AesEnsUtils;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc2TemplatePollingSchemeMapper;
import com.paradisecloud.fcm.dao.mapper.BusiUserTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.BusiUserTerminal;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.attendee.BaseFixedParamValue;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContextCache;
import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
import com.paradisecloud.fcm.smc2.model.AttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.InvitedAttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.TerminalAttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.operation.*;
import com.paradisecloud.fcm.smc2.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq;
import com.paradisecloud.fcm.smc2.model.request.MultiPicInfoTalkReq;
import com.paradisecloud.fcm.smc2.monitor.ConferenceSmc2AttendeeOperationThread;
import com.paradisecloud.fcm.smc2.service.Smc2ConferenceService;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IAttendeeSmc2Service;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiSmc2TemplatePollingSchemeService;
import com.paradisecloud.fcm.smc2.task.InviteAttendeeSmc2Task;
import com.paradisecloud.fcm.smc2.task.Smc2DelayTaskService;
import com.paradisecloud.fcm.smc2.utils.AttendeeSmc2Utils;
import com.paradisecloud.fcm.smc2.utils.Smc2ConferenceContextUtils;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.sinhy.exception.SystemException;
import com.suntek.smc.esdk.ServiceFactoryEx;
import com.suntek.smc.esdk.pojo.local.SiteVolumeEx;
import com.suntek.smc.esdk.pojo.local.WSConfTextParamEx;
import com.suntek.smc.esdk.pojo.local.WSCtrlSiteCommParamEx;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * <pre>界面上参会者业务处理类</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-05 17:29
 */
@Transactional
@Service
public class AttendeeSmc2ServiceImpl implements IAttendeeSmc2Service {

    public static final String LAYOUT_TEMPLATE = "layoutTemplate";
    public static final String DEFAULT_LAYOUT = "defaultLayout";
    public static final String CHOSEN_LAYOUT = "chosenLayout";
    public static final String AUTOMATIC = "automatic";
    private Logger logger = LoggerFactory.getLogger(AttendeeSmc2ServiceImpl.class);
    @Resource
    private Smc2ConferenceService smc2ConferenceService;
    @Resource
    private BusiMcuSmc2TemplatePollingSchemeMapper busiMcuSmc2TemplatePollingSchemeMapper;

    @Resource
    private IBusiSmc2TemplatePollingSchemeService busiSmc2TemplatePollingSchemeService;

    @Resource
    private IMqttService mqttService;

    @Resource
    private Smc2DelayTaskService smc2delayTaskService;

    @Resource
    private RedisCache redisCache;

    @Resource
    private BusiUserTerminalMapper busiUserTerminalMapper;

    private boolean isContains(AttendeeSmc2 a, AttendeeSmc2... excludes) {
        if (!ObjectUtils.isEmpty(excludes)) {
            for (AttendeeSmc2 attendee : excludes) {
                if (a == attendee) {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateAttendeeImportance(AttendeeSmc2 attendee, AttendeeImportance attendeeImportance) {
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
        // 辅流分辨率qualityPresentation: unset（空字符串）|unrestricted|max1080p20|max720p5
        // 开启辅流presentationContributionAllowed：unset（空字符串）|true|false
        // 接收双流presentationViewingAllowed: unset（空字符串）|true|false
        // 分享双流sipPresentationChannelEnabled: unset（空字符串）|true|false
        // 辅流模式bfcpMode: unset（空字符串）|serverOnly(客户端模式)|serverAndClient(服务器模式)
        String[] paramNames = {"qualityPresentation" , "presentationContributionAllowed" , "presentationViewingAllowed" , "sipPresentationChannelEnabled" , "bfcpMode"};
//        processCallLegUpdate(conferenceId, attendeeId, params, paramNames, null);
    }

    @Override
    public void mainSetting(String conferenceId, String attendeeId, List<BaseFixedParamValue> params) {
        // 主流分辨率qualityMain: unset（空字符串）|unrestricted|max1080p20|max720p20|max480p20
        // 显示画面txVideoMute（空字符串）|true|false
        // 关闭镜头 rxVideoMute: unset（空字符串）|true|false
        // 远端镜头控制controlRemoteCameraAllowed: unset（空字符串）|true|false
        String[] paramNames = {"qualityMain" , "txAudioMute" , "txVideoMute" , "rxVideoMute" , "controlRemoteCameraAllowed"};
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
        String[] paramNames = {"nameLabelOverride" , "meetingTitlePosition" , "participantLabels"};
//        processCallLegUpdate(conferenceId, attendeeId, params, paramNames, null);
    }

    @Override
    public void layoutSetting(String conferenceId, String attendeeId, List<BaseFixedParamValue> params) {
        String[] paramNames = {"chosenLayout" , "defaultLayout"};
//        processCallLegUpdate(conferenceId, attendeeId, params, paramNames, null);
    }


    @Override
    public void recordStreamSetting(String conferenceId, String attendeeId, List<BaseFixedParamValue> params) {
        // 1. 布局设置要同时传两个参数chosenLayout和defaultLayout，传的值相同
        // 2. 主流分辨率: qualityMain  unset（空字符串）|unrestricted|max1080p20|max720p20|max480p20
        // 2. 是否录制辅流: presentationViewingAllowed unset（空字符串）|true|false
        String[] paramNames = {"chosenLayout" , "defaultLayout" , "qualityMain" , "presentationViewingAllowed"};
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
        String[] paramNames = {"audioGainMode" , "sipMediaEncryption" , "needsActivation"
                , "deactivationMode" , "deactivationModeTime" , "callLockAllowed" , "endCallAllowed"
                , "disconnectOthersAllowed" , "addParticipantAllowed" , "muteOthersAllowed" , "videoMuteOthersAllowed"
                , "muteSelfAllowed" , "videoMuteSelfAllowed" , "changeLayoutAllowed"};
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
        String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceNumber);
        if (conferenceContext != null) {
            AttendeeSmc2 attendee = conferenceContext.getAttendeeById(attendeeId);
            if (attendee != null) {
                InviteAttendeeSmc2Task inviteAttendeesTask = new InviteAttendeeSmc2Task(conferenceNumber, 100, conferenceContext, attendee);
                smc2delayTaskService.addTask(inviteAttendeesTask);

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
    public void callAttendee(AttendeeSmc2 attendee) {
//        new CallAttendeeProcessor(attendee).process();
    }

    @Override
    public void hangUp(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeSmc2 attendeeSmc2 = conferenceContext.getAttendeeById(attendeeId);
            if (attendeeSmc2 != null) {
                String participantId = attendeeSmc2.getSmcParticipant().getGeneralParam().getId();

                //新建一个List对象，用于存放需要挂断的会场URI
                List<String> list = new ArrayList<>();
                list.add(attendeeSmc2.getSmcParticipant().getGeneralParam().getUri());
                ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
                Integer resultCode = conferenceServiceEx.disconnectSitesEx(conferenceContext.getSmc2conferenceId(), list);

            }
        }
    }

    @Override
    public void remove(String conferenceId, String attendeeId) {
        try {
            hangUp(conferenceId, attendeeId);
            String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
            Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
            if (conferenceContext != null) {
                AttendeeSmc2 attendeeSmc2 = conferenceContext.getAttendeeById(attendeeId);
                if (attendeeSmc2 != null) {
                    conferenceContext.removeAttendeeById(attendeeId);

                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("id" , attendeeSmc2.getId());
                    updateMap.put("deptId" , attendeeSmc2.getDeptId());
                    updateMap.put("mcuAttendee" , attendeeSmc2.isMcuAttendee());
                    ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
                    Integer resultCode = conferenceServiceEx.delSiteFromConfEx(conferenceContext.getSmc2conferenceId(), attendeeSmc2.getSmcParticipant().getGeneralParam().getUri(), null);

                    if (resultCode == 0) {
                        Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                    }
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
     * @since 2021-08-18 12:55
     */
    @Override
    public void cameraControl(String conferenceId, String attendeeId, JSONObject params) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        AttendeeSmc2 attendeeSmc2 = conferenceContext.getAttendeeById(attendeeId);
        if (attendeeSmc2 != null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("number" , params.get("number"));
            jsonObject.put("operate" , params.get("operate"));
            jsonObject.put("controlType" , params.get("controlType"));


        }
    }

    /**
     * 变更主会场
     *
     * @author lilinhai
     * @since 2021-02-09 11:22  void
     */
    @Override
    public void changeMaster(String conferenceId, String attendeeId) {
        if (StringUtil.isEmpty(attendeeId)) {
            return;
        }
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext cc = Smc2ConferenceContextCache.getInstance().get(contextKey);
        AttendeeSmc2 attendeeSmc2 = cc.getAttendeeById(attendeeId);
        if (attendeeSmc2 != null) {
            if (cc.isDownCascadeConference()) {
                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(cc.getUpCascadeConferenceId()));
                if (baseConferenceContext != null) {
                    if (!attendeeSmc2.getRemoteParty().equals(cc.getUpCascadeRemoteParty())) {
                        throw new SystemException(1005454, "该会议正被级联，不允许修改主会场！");
                    }
                }
            }

            AttendeeOperation old = cc.getAttendeeOperation();
            cc.setLastAttendeeOperation(old);
            old.cancel();
            AttendeeOperation attendeeOperation = new ChangeMasterAttendeeOperation(cc, attendeeSmc2);
            cc.setAttendeeOperation(attendeeOperation);
            attendeeOperation.operate();
        }
    }

    /**
     * 选看
     *
     * @author lilinhai
     * @since 2021-02-09 11:22  void
     */
    @Override
    public void chooseSee(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext cc = Smc2ConferenceContextCache.getInstance().get(contextKey);
        if (cc.getMasterAttendee() == null || cc.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue()) {
            throw new SystemException(1005454, "主会场未设置，无法进行选看操作！");
        }

        AttendeeSmc2 attendeeSmc2 = cc.getAttendeeById(attendeeId);
        if (attendeeSmc2 == null) {
            throw new CustomException("与会者不存在，无法进行选看操作！");
        }
        if (cc != null) {
            AttendeeOperation old = cc.getAttendeeOperation();
            if (!(old instanceof ChangeMasterAttendeeOperation)) {
                cc.setLastAttendeeOperation(old);
                old.cancel();
            }
            AttendeeOperation attendeeOperation = new ChooseToSeeAttendeeOperation(cc, attendeeSmc2);
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
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext.getMasterAttendee() == null || conferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue()) {
            throw new SystemException(1005454, "主会场未设置，无法进行选看操作！");
        }


        if (conferenceContext != null) {

            AttendeeSmc2 chooseSeeAttendee = conferenceContext.getAttendeeById(attendeeId);
            if (chooseSeeAttendee != null) {
                if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                    conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
                }
                AttendeeOperation old = conferenceContext.getAttendeeOperation();
                old.cancel();
                AttendeeOperation attendeeOperation = new ChooseToSeeAttendeeOperation(conferenceContext, chooseSeeAttendee);
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
     * @since 2021-02-09 11:22  void
     */
    @Override
    public void defaultChooseSee(Smc2ConferenceContext mainConferenceContext) {
        if (mainConferenceContext.getMasterAttendee() == null || !mainConferenceContext.getMasterAttendee().isMeetingJoined()) {
            return;
        }

        // 只有当操作是默认的选看时，才会执行该逻辑
        AttendeeSmc2 a = Smc2ConferenceContextUtils.getDefaultChooseToSee(mainConferenceContext);
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
        Smc2ConferenceContext cc = Smc2ConferenceContextCache.getInstance().get(contextKey);
        if (cc.getMasterAttendee() == null || cc.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue()) {
            throw new SystemException(1005454, "主会场未设置，无法进行点名操作！");
        }

        AttendeeSmc2 attendee = cc.getAttendeeById(attendeeId);
        if (cc != null) {

            AttendeeOperation attendeeOperation = cc.getAttendeeOperation();
            cc.setLastAttendeeOperation(attendeeOperation);
            attendeeOperation.cancel();
            CallTheRollAttendeeOperation callTheRollAttendeeOperation = new CallTheRollAttendeeOperation(cc, attendee);
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
        String cn = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        Smc2ConferenceContext mainConferenceContext = Smc2ConferenceContextCache.getInstance().getMainConferenceContext(cn);
        if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue()) {
            throw new SystemException(1005452, "主会场未设置，无法进行对话操作！");
        }
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(cn);
        if (conferenceContext != null) {

            conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
            conferenceContext.getAttendeeOperation().cancel();
            AttendeeOperation attendeeOperation = new TalkAttendeeOperation(conferenceContext, conferenceContext.getAttendeeById(attendeeId));
            conferenceContext.setAttendeeOperation(attendeeOperation);
            attendeeOperation.operate();
        }
    }

    /**
     * <pre>取消点名</pre>
     *
     * @param conferenceId
     * @author lilinhai
     * @since 2021-02-22 16:14
     */
    @Override
    public void cancelCallTheRoll(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            cancelCurrentOperation(conferenceContext);
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
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            //TODO
        }
    }

    /**
     * <pre>取消当前操作</pre>
     *
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-02-22 16:14
     */
    @Override
    public void cancelCurrentOperation(Smc2ConferenceContext conferenceContext) {
        try {
            if (conferenceContext.getAttendeeOperation() != null) {
                conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
                AttendeeOperation attendeeOperation = conferenceContext.getDefaultViewOperation();
                conferenceContext.setAttendeeOperation(conferenceContext.getDefaultViewOperation());
                conferenceContext.getLastAttendeeOperation().cancel(attendeeOperation);
            }
        } catch (Throwable e) {
            logger.error("cancelCallTheRoll error" , e);
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
        String cn = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        logger.info("开启单个参会者混音入口：" + cn + ", " + attendeeId);
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        AttendeeSmc2 attendeeById = smc2ConferenceContext.getAttendeeById(attendeeId);

        List<String> list = new ArrayList<String>();
        list.add(attendeeById.getSmcParticipant().getGeneralParam().getUri());
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        Integer resultCode = conferenceServiceEx.setSitesMuteEx(smc2ConferenceContext.getSmc2conferenceId(), list, 0);

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
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
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
    public void rejectRaiseHand(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        RaiseHandStatus raiseHandStatus = RaiseHandStatus.NO;
        // 关闭举手
        AttendeeSmc2 raiseHandsAttendee = conferenceContext.getAttendeeById(attendeeId);
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {


        }
    }

    @Override
    public void setBanner(String conferenceId, String attendeeId, JSONObject params) {
        String cn = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);

    }

    @Override
    public void sendBanner(String conferenceId, JSONObject jsonObject) {
        String cn = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(cn);
        Smc2ConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {

        });
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        AttendeeSmc2 attendeeById = conferenceContext.getAttendeeById(attendeeId);

        List<String> list = new ArrayList<String>();
        list.add(attendeeById.getSmcParticipant().getGeneralParam().getUri());
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        Integer resultCode = conferenceServiceEx.setSitesMuteEx(conferenceContext.getSmc2conferenceId(), list, 1);

    }


    public void invite(String conferenceId, Integer callType, List<Long> terminalIds) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        List<AttendeeSmc2> attendees = new ArrayList<>();
        List<BusiTerminal> terminals = new ArrayList<>();
        for (Long terminalId : terminalIds) {
            BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
            if (busiTerminal != null) {
                terminals.add(busiTerminal);
            }
            TerminalAttendeeSmc2 ta = conferenceContext.getTerminalAttendeeMap().get(terminalId);
            if (ta == null) {
                ta = AttendeeSmc2Utils.packTerminalAttendee(terminalId);
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
                    AttendeeOperation attendeeOperation_ = conferenceContext.getAttendeeOperation();
                    if(attendeeOperation_!=null){
                        if(attendeeOperation_ instanceof TalkPrivateAttendeeOperation){
                            ConferenceSmc2AttendeeOperationThread.add(ta);
                        }
                    }
                    Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, ta);
                    Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + ta.getName() + "】被邀请加入");

                    mqttService.sendConferenceInfoToPushTargetTerminal(conferenceContext);
                }
            }
        }


        recallAttendees(conferenceId, attendees);

    }

    @Override
    public void invite(String conferenceId, List<Long> terminalIds) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        List<AttendeeSmc2> attendees = new ArrayList<>();
        List<BusiTerminal> terminals = new ArrayList<>();
        for (Long terminalId : terminalIds) {
            BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
            if (busiTerminal != null) {
                terminals.add(busiTerminal);
            }
            TerminalAttendeeSmc2 ta = conferenceContext.getTerminalAttendeeMap().get(terminalId);
            if (ta == null) {
                ta = AttendeeSmc2Utils.packTerminalAttendee(terminalId);
                ta.setConferenceNumber(conferenceContext.getConferenceNumber());
                ta.setDeptId(conferenceContext.getDeptId());
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(terminalId);
                if (busiUserTerminal != null) {
                    ta.setUserId(busiUserTerminal.getUserId());
                }
                conferenceContext.addAttendee(ta);
                attendees.add(ta);
                AttendeeOperation attendeeOperation_ = conferenceContext.getAttendeeOperation();
                if(attendeeOperation_!=null){
                    if(attendeeOperation_ instanceof TalkPrivateAttendeeOperation){
                        ConferenceSmc2AttendeeOperationThread.add(ta);
                    }
                }
                Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, ta);
                Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + ta.getName() + "】被邀请加入");

                mqttService.sendConferenceInfoToPushTargetTerminal(conferenceContext);


            }
        }


        recallAttendees(conferenceId, attendees);
    }

    @Override
    public void invite(String conferenceId, JSONObject jsonObj) {
        Assert.isTrue(jsonObj.containsKey("name"), "名字是必填参数！");
        Assert.isTrue(jsonObj.containsKey("uri"), "URI是必填参数！");
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId));
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
            invite(conferenceId,callType,Arrays.asList(busiTerminal.getId()));
            return;
        }

        Map<String, AttendeeSmc2> AttendeeSmc2Map = conferenceContext.getUuidAttendeeMapByUri(uri);
        if (AttendeeSmc2Map != null && AttendeeSmc2Map.size() > 0) {
            Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + name + "】已在会");
            return;
        }

        InvitedAttendeeSmc2 ia = new InvitedAttendeeSmc2();
        ia.setConferenceNumber(conferenceContext.getConferenceNumber());
        ia.setId(UUID.randomUUID().toString());
        ia.setName(name);
        boolean inetAddress = InetAddresses.isInetAddress(uri);
        if (inetAddress) {
            uri = UUID.randomUUID() + "@" + uri;
        }
        ia.setRemoteParty(uri);
        ia.setWeight(1);
        ia.setDeptId(conferenceContext.getDeptId());
        if (ia.getRemoteParty().contains("@")) {
            ia.setIp(ia.getRemoteParty().split("@")[1]);
        } else {
            ia.setIp(ia.getRemoteParty());
        }
        ia.setCallType(callType);

        conferenceContext.addAttendee(ia);
        AttendeeOperation attendeeOperation_ = conferenceContext.getAttendeeOperation();
        if(attendeeOperation_!=null){
            if(attendeeOperation_ instanceof TalkPrivateAttendeeOperation){
                ConferenceSmc2AttendeeOperationThread.add(ia);
            }
        }
        Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, ia);
        Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + ia.getName() + "】被邀请加入");
        recallAttendee(conferenceId, ia);

        mqttService.sendConferenceInfoToPushTargetTerminal(conferenceContext);
    }

    private void recallAttendee(String conferenceId, AttendeeSmc2 attendee) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            InviteAttendeeSmc2Task inviteAttendeesTask = new InviteAttendeeSmc2Task(conferenceContext.getTenantId(), 100, conferenceContext, attendee);
            smc2delayTaskService.addTask(inviteAttendeesTask);
        }
    }

    private void recallAttendees(String conferenceId, List<AttendeeSmc2> attendees) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            InviteAttendeeSmc2Task inviteAttendeesTask = new InviteAttendeeSmc2Task(conferenceContext.getId(), 100, conferenceContext, attendees);
            smc2delayTaskService.addTask(inviteAttendeesTask);
        }
    }

    @Override
    public void openCamera(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        // 打开终端摄像头（打开上行视频）
        AttendeeSmc2 attendee = conferenceContext.getAttendeeById(attendeeId);
        if (attendee != null) {
            String uri = attendee.getSmcParticipant().getGeneralParam().getUri();
            String confId = conferenceContext.getSmc2conferenceId();
            List<String> siteUris = new ArrayList<>();
            siteUris.add(uri);
            ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
            Integer resultCode = conferenceServiceEx.displayConfSiteLocalVideoEx(confId, siteUris);
        }
    }

    @Override
    public void closeCamera(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        // 打开终端摄像头（打开上行视频）
        AttendeeSmc2 attendee = conferenceContext.getAttendeeById(attendeeId);
        if (attendee != null) {
            String uri = attendee.getSmcParticipant().getGeneralParam().getUri();
            String confId = conferenceContext.getSmc2conferenceId();
            List<String> siteUris = new ArrayList<>();
            siteUris.add(uri);
            ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
            Integer resultCode = conferenceServiceEx.hideConfSiteLocalVideoEx(confId, siteUris);
        }
    }

    @Override
    public void openMixing(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        openMixing(conferenceContext);
    }

    @Override
    public void openMixing(Smc2ConferenceContext cc) {
        List<AttendeeSmc2> as = new ArrayList<>();
        Smc2ConferenceContextUtils.eachNonFmeAttendeeInConference(cc, (a) -> {
            if (a.isMeetingJoined()) {
                as.add(a);
            }
        });

        if (CollectionUtils.isNotEmpty(as)) {
            for (AttendeeSmc2 a : as) {
                openMixing(cc.getId(), a.getId());
            }
        }
    }

    @Override
    public void openDisplayDevice(String conferenceId) {

        // 允许所有人观看（打开所有下行视频）


    }

    @Override
    public void closeDisplayDevice(String conferenceId) {

        // 禁止所有观众观看（关闭所有下行视频）

    }


    @Override
    public void openDisplayDevice(String conferenceId, String attendeeId) {
        String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceNumber);
        // 打开终端摄像头（打开上行视频）
        AttendeeSmc2 attendee = conferenceContext.getAttendeeById(attendeeId);
        if (attendee != null) {

        }
    }

    @Override
    public void closeDisplayDevice(String conferenceId, String attendeeId) {
        String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceNumber);
        // 打开终端摄像头（打开上行视频）
        AttendeeSmc2 attendee = conferenceContext.getAttendeeById(attendeeId);
        if (attendee != null) {

        }
    }

    @Override
    public void closeCamera(String conferenceId) {
        Smc2ConferenceContextUtils.eachNonFmeAttendeeInConference(Smc2ConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId)), (a) -> {
            if (a.isMeetingJoined()) {
                closeCamera(conferenceId, a.getId());
            }
        });
    }

    @Override
    public void openCamera(String conferenceId) {
        Smc2ConferenceContextUtils.eachNonFmeAttendeeInConference(Smc2ConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId)), (a) -> {
            if (a.isMeetingJoined()) {
                openCamera(conferenceId, a.getId());
            }
        });
    }

    @Override
    public void closeMixing(String conferenceId) {
        Smc2ConferenceContext cc = Smc2ConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
        closeMixing(cc, cc.getMasterAttendee());
    }

    @Override
    public void closeMixing(Smc2ConferenceContext cc, AttendeeSmc2... excludes) {
        List<AttendeeSmc2> as = new ArrayList<>();
        Smc2ConferenceContextUtils.eachNonFmeAttendeeInConference(cc, (a) -> {
            for (AttendeeSmc2 attendee : excludes) {
                if (a == attendee) {
                    return;
                }
            }
            if (a.isMeetingJoined()) {
                as.add(a);
            }
        });
        if (CollectionUtils.isNotEmpty(as)) {
            for (AttendeeSmc2 a : as) {
                closeMixing(cc.getId(), a.getId());
            }
        }
    }

    @Override
    public void sendMessage(String conferenceId, JSONObject jsonObject) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        Integer messageDuration = jsonObject.getInteger("messageDuration");
        if (messageDuration == null) {
            messageDuration = 5;
        }
        if (conferenceContext == null || conferenceContext.isEnd()) {
            return;
        }
        TextTipsSetting textTipsSetting = JSONObject.parseObject(jsonObject.toJSONString(), TextTipsSetting.class);
        textTipsSetting.setOpType(jsonObject.get("opType").toString());

        textTipsSetting.setType(TxtTypeEnum.CAPTION.name());
        WSConfTextParamEx value = new WSConfTextParamEx();
        String confId = conferenceContext.getSmc2conferenceId();
        List<String> siteUriList = new ArrayList<>();
        if (!Objects.equals(TxtOperationTypeEnumDto.SAVE.name(), textTipsSetting.getOpType())) {
            List<AttendeeSmc2> all = AttendeeSmc2Utils.getAll(conferenceContext);
            for (AttendeeSmc2 attendeeSmc2 : all) {
                if (attendeeSmc2.isMeetingJoined()) {
                    siteUriList.add(attendeeSmc2.getRemoteParty());
                }
            }
        }

        value.setContent(textTipsSetting.getContent());
        int disPosition = textTipsSetting.getDisPosition();
        int displayType = textTipsSetting.getDisplayType();
        String opType = textTipsSetting.getOpType();
        if (Objects.equals(TxtOperationTypeEnumDto.SET.name(), opType)) {
            //设置生效
            value.setOpType(0);
        }
        if (Objects.equals(TxtOperationTypeEnumDto.SAVE.name(), opType)) {
            value.setOpType(0);
            value.setSiteUriList(null);
        }
        if (Objects.equals(TxtOperationTypeEnumDto.CANCEL.name(), opType)) {
            value.setOpType(1);
        }
        value.setDisPos(disPosition);
        if (displayType == 3) {
            value.setDisType(1);

        } else if (displayType == 4) {
            value.setDisType(2);
        } else {
            value.setDisType(3);
        }
        String type = textTipsSetting.getType();
        if (Objects.equals(TxtTypeEnum.BANNER.name(), type)) {
            value.setTextType(0);
            value.setDisPos(null);
        }

        if (Objects.equals(TxtTypeEnum.CAPTION.name(), type) || Objects.equals(TxtTypeEnum.BOTTOMCAP.name(), type)) {
            value.setTextType(2);
        }
        if (Objects.equals(TxtTypeEnum.SHORTMSG.name(), type)) {
            value.setTextType(2);
        }

        value.setDisPos(1);
        value.setTextType(2);
        value.setDisType(3);
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        int resultCode = conferenceServiceEx.setConfTextCtrlEx(confId, value);
        if (resultCode == 1345323080) {
            throw new CustomException("当前显示位置不支持该显示效果");
        }
        if (resultCode == 1347440727) {
            throw new CustomException("指定的会场为SIP会场");
        }
        if (resultCode != 0) {
            throw new CustomException("当前显示错误：" + resultCode);
        }
    }

    /**
     * 设置横幅
     *
     * @param conferenceId
     * @param jsonObject   void
     */
    @Override
    public void setMessageBannerText(String conferenceId, JSONObject jsonObject) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        String messageBannerText = jsonObject.getString("messageBannerText");
        if (!ObjectUtils.isEmpty(messageBannerText)) {
            if (messageBannerText.getBytes().length <= 60) {
                String replacedText = messageBannerText.replaceAll("\t|\r|\n" , "");
                if (replacedText.length() < messageBannerText.length()) {
                    throw new SystemException("横幅不支持【制表】【回车】等特殊字符！");
                }
            } else {
                if (messageBannerText.getBytes().length > 60) {
                    throw new SystemException("横幅最多支持20个字符！");
                }
            }
        }
        if (conferenceContext == null || conferenceContext.isEnd()) {
            return;
        }
        TextTipsSetting textTipsSetting = JSONObject.parseObject(jsonObject.toJSONString(), TextTipsSetting.class);
        textTipsSetting.setOpType(jsonObject.get("opType").toString());

        textTipsSetting.setType(TxtTypeEnum.BANNER.name());
        WSConfTextParamEx value = new WSConfTextParamEx();
        String confId = conferenceContext.getSmc2conferenceId();
        List<String> siteUriList = new ArrayList<>();
        if (!Objects.equals(TxtOperationTypeEnumDto.SAVE.name(), textTipsSetting.getOpType())) {
            List<AttendeeSmc2> all = AttendeeSmc2Utils.getAll(conferenceContext);
            for (AttendeeSmc2 attendeeSmc2 : all) {
                if (attendeeSmc2.isMeetingJoined()) {
                    siteUriList.add(attendeeSmc2.getRemoteParty());
                }
            }
        }

        value.setSiteUriList(siteUriList);
        value.setContent(textTipsSetting.getContent());
        int disPosition = textTipsSetting.getDisPosition();
        int displayType = textTipsSetting.getDisplayType();
        String opType = textTipsSetting.getOpType();
        if (Objects.equals(TxtOperationTypeEnumDto.SET.name(), opType)) {
            //设置生效
            value.setOpType(0);
        }
        if (Objects.equals(TxtOperationTypeEnumDto.SAVE.name(), opType)) {
            value.setOpType(0);
            value.setSiteUriList(null);
        }
        if (Objects.equals(TxtOperationTypeEnumDto.CANCEL.name(), opType)) {
            value.setOpType(1);
        }
        value.setDisPos(disPosition);
        if (displayType == 3) {
            value.setDisType(1);

        } else if (displayType == 4) {
            value.setDisType(2);
        } else {
            value.setDisType(3);
        }
        String type = textTipsSetting.getType();
        if (Objects.equals(TxtTypeEnum.BANNER.name(), type)) {
            value.setTextType(0);
            value.setDisPos(null);
        }

        if (Objects.equals(TxtTypeEnum.CAPTION.name(), type) || Objects.equals(TxtTypeEnum.BOTTOMCAP.name(), type)) {
            value.setTextType(1);
        }
        if (Objects.equals(TxtTypeEnum.SHORTMSG.name(), type)) {
            value.setTextType(2);
        }

        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        int resultCode = conferenceServiceEx.setConfTextCtrlEx(confId, value);
        if (resultCode == 1345323080) {
            throw new CustomException("当前显示位置不支持该显示效果");
        }
        if (resultCode == 1347440727) {
            throw new CustomException("指定的会场为SIP会场");
        }
        if (resultCode != 0) {
            throw new CustomException("当前显示错误：" + resultCode);
        }
    }

    /**
     * 设置横幅
     *
     * @param conferenceId
     * @param text
     */
    @Override
    public void setMessageBannerText(String conferenceId, String text) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);


    }

    @Override
    public void polling(String conferenceId) {
        String cn = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(cn);
        Smc2ConferenceContext mainConferenceContext = Smc2ConferenceContextCache.getInstance().getMainConferenceContext(conferenceContext);
        if (mainConferenceContext.getMasterAttendee() == null || !mainConferenceContext.getMasterAttendee().isMeetingJoined()) {
            throw new SystemException(1005454, "主会场未设置或未呼入,无法进行轮询操作！");
        }

        if (conferenceContext != null) {

        }


    }

    @Override
    public void pollingPause(String conferenceId) {
        String cn = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(cn);
        if (conferenceContext != null) {

        }
    }

    @Override
    public void pollingResume(String conferenceId) {
        String cn = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(cn);
        if (conferenceContext != null) {

        }
    }

    /**
     * <pre>取消轮询</pre>
     *
     * @param conferenceId
     * @author lilinhai
     * @since 2021-02-22 16:14
     */
    @Override
    public void cancelPolling(String conferenceId) {
        String cn = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(cn);
        if (conferenceContext != null) {

        }
    }

    @Override
    public JSONObject detail(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        AttendeeSmc2 attendee = conferenceContext.getAttendeeById(attendeeId);
        if (attendee != null && attendee.isMeetingJoined()) {

            String participantId = attendee.getSmcParticipant().getGeneralParam().getId();

            JSONObject jsonObject = subcriberReafInfo(conferenceId, conferenceContext, participantId);

            if (jsonObject != null) {
                JSONObject d = toDetail(jsonObject, attendee);
                d.put("attendeeId" , attendeeId);
                return d;
            }
        }

        return null;
    }

    private JSONObject subcriberReafInfo(String conferenceId, Smc2ConferenceContext smc2ConferenceContext, String participantId) {
        JSONObject jsonObject = null;
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    @Override
    public JSONObject detail(Smc2ConferenceContext conferenceContext, AttendeeSmc2 attendee) {
        if (attendee != null && attendee.isMeetingJoined()) {

        }

        return null;
    }

    public JSONObject toDetail(JSONObject jsonObject, AttendeeSmc2 attendee) {

        SmcParitipantsStateRep.ContentDTO smcParticipant = attendee.getSmcParticipant();

        if (Objects.isNull(jsonObject) || Objects.isNull(smcParticipant)) {
            return null;
        }
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("direction" , "outgoing");
        Integer protoType = smcParticipant.getGeneralParam().getType();
        String protoTypeStr = null;
        if (protoType != null) {
            if (protoType == 1) {
                protoTypeStr = "sip";
            } else if (protoType == 0) {
                protoTypeStr = "h222";
            } else {
                protoTypeStr = "sipAndH222";
            }
        }
        jsonObj.put("type" , protoTypeStr);
        jsonObj.put("isEncrypted" , false);
        jsonObj.put("remoteParty" , smcParticipant.getGeneralParam().getUri());
        JSONObject upLink = new JSONObject();
        jsonObj.put("upLink" , upLink);
        JSONObject upLinkAudio = new JSONObject();
        JSONArray upLinkVideos = new JSONArray();

        JSONObject downLink = new JSONObject();
        jsonObj.put("downLink" , downLink);

        JSONObject downLinkAudio = new JSONObject();
        JSONArray downLinkVideos = new JSONArray();

        upLink.put("videos" , upLinkVideos);
        upLink.put("audio" , upLinkAudio);
        downLink.put("videos" , downLinkVideos);
        downLink.put("audio" , downLinkAudio);

        JSONObject realTimeInfo = (JSONObject) jsonObject.get("realTimeInfo");
        //下行信息
        JSONObject receiveRealTimeInfo = (JSONObject) realTimeInfo.get("receiveRealTimeInfo");

        JSONObject rttRealTimeInfo = (JSONObject) realTimeInfo.get("rttRealTimeInfo");
        JSONObject video = new JSONObject();
        video.put("role" , Objects.equals(false, receiveRealTimeInfo.get("openAux")) ? "main" : "");
        int videoResolutionCode = (int) receiveRealTimeInfo.get("videoResolution");
        int videoProtocolCode = (int) receiveRealTimeInfo.get("videoProtocol");
        video.put("resolutionRatio" , VideoResolutionEnum.getValueByCode(videoResolutionCode).name());
        video.put("frameRate" , receiveRealTimeInfo.get("videoFrameRate"));
        video.put("videoCodec" , VideoProtocolEnum.getValueByCode(videoProtocolCode).name());
        video.put("bandwidth" , receiveRealTimeInfo.get("videoBandWidth"));
        video.put("packetLossPercentage" , receiveRealTimeInfo.get("videoLoss"));
        video.put("jitter" , receiveRealTimeInfo.get("videoJitter"));
        video.put("roundTripTime" , rttRealTimeInfo.get("videoRtt"));
        downLinkVideos.add(video);


        int audioProtocolCode = (int) receiveRealTimeInfo.get("audioProtocol");

        // downLinkAudio.put("codec", AudioProtocolEnum.getValueByCode(audioProtocolCode).name());
        downLinkAudio.put("bandwidth" , receiveRealTimeInfo.get("audioBandWidth"));
        downLinkAudio.put("packetLossPercentage" , receiveRealTimeInfo.get("audioLoss"));
        downLinkAudio.put("codecBitRate" , null);
        downLinkAudio.put("jitter" , receiveRealTimeInfo.get("audioJitter"));
        downLinkAudio.put("roundTripTime" , rttRealTimeInfo.get("audioRtt"));
        downLinkAudio.put("gainApplied" , null);

        //上行
        JSONObject sendRealTimeInfo = (JSONObject) realTimeInfo.get("sendRealTimeInfo");
        if (sendRealTimeInfo == null) {
            JSONObject videoSend = new JSONObject();
            video.put("role" , Objects.equals(false, sendRealTimeInfo.get("openAux")) ? "main" : "");
            int videoResolutionCode2 = (int) sendRealTimeInfo.get("videoResolution");
            int videoProtocolCode2 = (int) sendRealTimeInfo.get("videoProtocol");
            video.put("resolutionRatio" , VideoResolutionEnum.getValueByCode(videoResolutionCode2).name());
            video.put("frameRate" , sendRealTimeInfo.get("videoFrameRate"));
            video.put("videoCodec" , VideoProtocolEnum.getValueByCode(videoProtocolCode2).name());
            video.put("bandwidth" , sendRealTimeInfo.get("videoBandWidth"));
            video.put("packetLossPercentage" , sendRealTimeInfo.get("videoLoss"));
            video.put("jitter" , sendRealTimeInfo.get("videoJitter"));
            video.put("roundTripTime" , rttRealTimeInfo.get("videoRtt"));
            upLinkVideos.add(videoSend);

            int audioProtocolCode2 = (int) sendRealTimeInfo.get("audioProtocol");

            upLinkAudio.put("codec" , AudioProtocolEnum.getValueByCode(audioProtocolCode2).name());
            upLinkAudio.put("bandwidth" , sendRealTimeInfo.get("audioBandWidth"));
            upLinkAudio.put("packetLossPercentage" , sendRealTimeInfo.get("audioLoss"));
            upLinkAudio.put("codecBitRate" , null);
            upLinkAudio.put("jitter" , sendRealTimeInfo.get("audioJitter"));
            upLinkAudio.put("roundTripTime" , rttRealTimeInfo.get("audioRtt"));
            upLinkAudio.put("gainApplied" , null);
        }

        return jsonObj;
    }

    /**
     * 与会者呼叫失败通知
     *
     * @param participantUuid void
     * @author lilinhai
     * @since 2021-02-08 12:49
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
    private void updateAttendeeAttrs(String conferenceNumber, List<AttendeeSmc2> attendees, List<NameValuePair> nameValuePairs) {
    }


    @Override
    public void closeSpeaker(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        AttendeeSmc2 attendeeById = conferenceContext.getAttendeeById(attendeeId);
        List<String> list = new ArrayList<String>();
        list.add(attendeeById.getRemoteParty());
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        Integer resultCode = conferenceServiceEx.setSitesQuietEx(conferenceContext.getSmc2conferenceId(), list, 1);
    }

    @Override
    public void openSpeaker(String conferenceId, String attendeeId) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        AttendeeSmc2 attendeeById = conferenceContext.getAttendeeById(attendeeId);
        List<String> list = new ArrayList<String>();
        list.add(attendeeById.getRemoteParty());
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        Integer resultCode = conferenceServiceEx.setSitesQuietEx(conferenceContext.getSmc2conferenceId(), list, 0);

    }

    @Override
    public Object lockPresenter(String conferenceId, String participantId, Boolean lock) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        AttendeeSmc2 a = conferenceContext.getAttendeeById(participantId);

        String confId = conferenceContext.getSmc2conferenceId();
        String siteUri = a.getRemoteParty();

        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        Integer resultCode = 0;
        if (lock) {
            resultCode = conferenceServiceEx.lockPresentationEx(confId, siteUri);
        } else {
            resultCode = conferenceServiceEx.unlockPresentationEx(confId);
        }
        if (resultCode != 0) {
            logger.error(siteUri + "取消/锁定会议材料：" + resultCode);
            return RestResponse.fail();

        }
        return resultCode;
    }

    @Override
    public Object videoSwitchAttribute(String conferenceId, String participantId, boolean lock) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        AttendeeSmc2 a = conferenceContext.getAttendeeById(participantId);

        String confId = conferenceContext.getSmc2conferenceId();
        String siteUri = a.getRemoteParty();


        List<WSCtrlSiteCommParamEx> wsCtrlSiteCommParams = new ArrayList<>();
        WSCtrlSiteCommParamEx item1 = new WSCtrlSiteCommParamEx();
        //锁定
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        if (lock) {
            item1.setOperaTypeParam(1);
        } else {
            item1.setOperaTypeParam(0);
        }

        item1.setSiteUri(siteUri);
        wsCtrlSiteCommParams.add(item1);
        int resultCode = conferenceServiceEx.setVSAttrCtrlEx(confId, wsCtrlSiteCommParams);
        if (resultCode != 0) {
            logger.error(siteUri + "锁定错误：" + resultCode);
            return RestResponse.fail(resultCode + "");
        }
        a.getSmcParticipant().getState().setVideoSwitchAttribute(lock ? 1 : 0);
        a.setVideoSwitchAttribute(lock ? 1 : 0);
        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, a.getUpdateMap());
        return lock;
    }


    @Override
    public Object setVolume(String conferenceId, String participantId, int volume) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        ParticipantStatus participantStatus = new ParticipantStatus();
        participantStatus.setVolume(volume);

        AttendeeSmc2 attendee = conferenceContext.getAttendeeById(participantId);

        String confId = conferenceContext.getSmc2conferenceId();
        String siteUri = attendee.getRemoteParty();


        List<SiteVolumeEx> siteVolumes = new ArrayList<SiteVolumeEx>();
        SiteVolumeEx siteVolumeEx1 = new SiteVolumeEx();
        siteVolumeEx1.setSiteUri(siteUri);
        siteVolumeEx1.setVolume(volume);
        siteVolumes.add(siteVolumeEx1);
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        //获取会议相关服务实例
        Integer resultCode = conferenceServiceEx.setConfSiteVolumeEx(confId, siteVolumes);
        if (resultCode != 0) {
            return RestResponse.fail(resultCode, "音量设置失败");
        }
        attendee.setVolume(volume);
        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
        return resultCode;
    }

    @Override
    public void privateTalk(String conferenceId, JSONObject jsonObject) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext cc = Smc2ConferenceContextCache.getInstance().get(contextKey);
        if (cc.getMasterAttendee() == null || !cc.getMasterAttendee().isMeetingJoined()) {
            throw new SystemException(1005454, "主会场未设置，无法进行操作！");
        }
        MultiPicInfoTalkReq multiPicInfoTalkReq = JSONObject.parseObject(jsonObject.toJSONString(), MultiPicInfoTalkReq.class);

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
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
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
                MultiPicInfoReq.MultiPicInfoDTO multiPicInfo = conferenceContext.getMultiPicInfo();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("multiPicInfo" , multiPicInfo);
                jsonObject.put("conferenceId" , conferenceContext.getSmc2conferenceId());
                jsonObject.put("broadcast" , false);
                conferenceContext.setLastAttendeeOperation(attendeeOperation);
                DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext, jsonObject);
                conferenceContext.setAttendeeOperation(defaultAttendeeOperation);
                defaultAttendeeOperation.operate();
            }
        }
    }
}
