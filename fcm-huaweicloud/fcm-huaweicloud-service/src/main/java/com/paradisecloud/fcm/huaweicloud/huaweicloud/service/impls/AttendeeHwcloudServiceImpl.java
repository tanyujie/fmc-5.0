/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.service.impls
 * @author lilinhai
 * @since 2021-02-05 17:29
 * @version  V1.0
 */
package com.paradisecloud.fcm.huaweicloud.huaweicloud.service.impls;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.huaweicloud.sdk.meeting.v1.model.Attendee;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.AesEnsUtils;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiUserTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.AttendeeImportance;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.AttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.CropDirAttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.McuAttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.TerminalAttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContextCache;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudMeetingBridge;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudWebSocketMessagePusher;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.SmcParitipantsStateRep;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client.MeetingControl;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation.AttendeeOperation;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation.CallTheRollAttendeeOperation;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation.ChangeMasterAttendeeOperation;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation.ChooseToSeeAttendeeOperation;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IAttendeeHwcloudService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.task.HwcloudDelayTaskService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.task.InviteAttendeeHwcloudTask;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.utils.HwcloudConferenceContextUtils;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.attendee.BaseFixedParamValue;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.NameValuePair;
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
@Slf4j
public class AttendeeHwcloudServiceImpl implements IAttendeeHwcloudService {



    @Resource
    private IMqttService mqttService;

    @Resource
    private HwcloudDelayTaskService hwclouddelayTaskService;

    @Resource
    private RedisCache redisCache;

    @Resource
    private BusiUserTerminalMapper busiUserTerminalMapper;

    private boolean isContains(AttendeeHwcloud a, AttendeeHwcloud... excludes)
    {
        if (!ObjectUtils.isEmpty(excludes))
        {
            for (AttendeeHwcloud attendee : excludes)
            {
                if (a == attendee)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateAttendeeImportance(AttendeeHwcloud attendee, AttendeeImportance attendeeImportance)
    {

    }

    @Override
    public void presentationSetting(String conferenceId, String attendeeId, List<BaseFixedParamValue> params)
    {

    }

    @Override
    public void mainSetting(String conferenceId, String attendeeId, List<BaseFixedParamValue> params)
    {

    }

    @Override
    public void subtitle(String conferenceId, String attendeeId, List<BaseFixedParamValue> params)
    {

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
        // 2. 主流分辨率: qualityMain  unset（空字符串）|unrestricted|max1080p20|max720p20|max480p20
        // 2. 是否录制辅流: presentationViewingAllowed unset（空字符串）|true|false
        String[] paramNames = {"chosenLayout", "defaultLayout", "qualityMain", "presentationViewingAllowed"};
//        processCallLegUpdate(conferenceId, attendeeId, params, paramNames, null);
    }

    @Override
    public void advanceSetting(String conferenceId, String attendeeId, List<BaseFixedParamValue> params)
    {

    }

    @Override
    public JSONObject attendeeCallLegSetting(String conferenceId, String attendeeId)
    {

        return null;
    }

    @Override
    public void recall(String conferenceId, String attendeeId)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeHwcloud attendee = conferenceContext.getAttendeeById(attendeeId);
            if (attendee != null) {


                if (attendee.getCallRequestSentTime() != null && (System.currentTimeMillis() - attendee.getCallRequestSentTime()) < 40 * 1000)
                {
                    StringBuilder messageTip = new StringBuilder();
                    messageTip.append("【").append(attendee.getName()).append("】重呼请求已发起，请耐心等待响应结果，期间不要频繁发起！");
                    HwcloudWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                    return;
                }

                InviteAttendeeHwcloudTask inviteAttendeesTask = new InviteAttendeeHwcloudTask(contextKey, 100, conferenceContext, attendee);
                hwclouddelayTaskService.addTask(inviteAttendeesTask);

                StringBuilder messageTip = new StringBuilder();
                messageTip.append("【").append(attendee.getName()).append("】呼叫已发起！");
                HwcloudWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                attendee.setHangUp(false);
                attendee.setCallRequestSentTime(System.currentTimeMillis());
                if(attendee instanceof TerminalAttendeeHwcloud){
                    BusiTerminal terminal = TerminalCache.getInstance().get(attendee.getTerminalId());
                    if (terminal != null && TerminalType.isFCMSIP(terminal.getType()) && com.paradisecloud.common.utils.StringUtils.isNotEmpty(terminal.getSn())) {
                        BeanFactory.getBean(IMqttService.class).inviteAttendeeJoinConference(attendee, conferenceContext, AttendType.AUTO_JOIN.getValue());
                        return;
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
    public void callAttendee(AttendeeHwcloud attendee)
    {
//        new CallAttendeeProcessor(attendee).process();
    }

    @Override
    public void hangUp(String conferenceId, String attendeeId)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);

        if (conferenceContext != null) {
            AttendeeHwcloud attendeeHwcloud = conferenceContext.getAttendeeById(attendeeId);
            if (attendeeHwcloud != null) {
                attendeeHwcloud.setHangUp(true);
                String participantId = attendeeHwcloud.getParticipantUuid();
                HwcloudMeetingBridge hwcloudMeetingBridge = conferenceContext.getHwcloudMeetingBridge();
                hwcloudMeetingBridge.getMeetingControl().hangUpParticipant(hwcloudMeetingBridge.getTokenInfo().getToken(),hwcloudMeetingBridge.getConfID(),participantId);

                HwcloudWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendeeHwcloud.getName() + "】挂断请求已发起");

            }
        }


    }

    @Override
    public void remove(String conferenceId, String attendeeId)
    {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        AttendeeHwcloud attendee = conferenceContext.getAttendeeById(attendeeId);

        if (attendee != null) {
            conferenceContext.removeAttendeeById(attendeeId);
            conferenceContext.getHwcloudMeetingBridge().getMeetingControl().deleteAttendee(conferenceContext.getHwcloudMeetingBridge().getTokenInfo().getToken(),conferenceContext.getMeetingId(),attendee.getNumber(),attendee.getParticipantUuid());
            HwcloudWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, attendee);
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
     * @author sinhy
     * @since 2021-08-18 12:55
     * @param conferenceId
     * @param attendeeId
     * @param params
     */
    @Override
    public void cameraControl(String conferenceId, String attendeeId, JSONObject params)
    {

    }

    /**
     * 变更主会场
     * @author lilinhai
     * @since 2021-02-09 11:22  void
     */
    @Override
    public void changeMaster(String conferenceId, String attendeeId)
    {
        if (StringUtil.isEmpty(attendeeId)) {
            return;
        }
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext cc = HwcloudConferenceContextCache.getInstance().get(contextKey);
        AttendeeHwcloud attendeeHwcloud = cc.getAttendeeById(attendeeId);
        if (attendeeHwcloud != null) {
            if (cc.isDownCascadeConference()) {
                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(cc.getUpCascadeConferenceId()));
                if (baseConferenceContext != null) {
                    if (!attendeeHwcloud.getRemoteParty().equals(cc.getUpCascadeRemoteParty())) {
                        throw new SystemException(1005454, "该会议正被级联，不允许修改主会场！");
                    }
                }
            }

            AttendeeOperation old = cc.getAttendeeOperation();
            if(old!=null){
                cc.setLastAttendeeOperation(old);
                old.cancel();
            }
            AttendeeOperation attendeeOperation = new ChangeMasterAttendeeOperation(cc, attendeeHwcloud);
            cc.setAttendeeOperation(attendeeOperation);
            attendeeOperation.operate();
        }
    }

    /**
     * 选看
     * @author lilinhai
     * @since 2021-02-09 11:22  void
     */
    @Override
    public void chooseSee(String conferenceId, String attendeeId)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext cc = HwcloudConferenceContextCache.getInstance().get(contextKey);
        if (cc.getMasterAttendee() == null || cc.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue()) {
            throw new SystemException(1005454, "主会场未设置，无法进行选看操作！");
        }

        AttendeeHwcloud attendeeHwcloud = cc.getAttendeeById(attendeeId);
        if (attendeeHwcloud == null) {
            throw new CustomException("与会者不存在，无法进行选看操作！");
        }
        if (cc != null) {
            AttendeeOperation old = cc.getAttendeeOperation();
            if(old!=null){
                cc.setLastAttendeeOperation(old);
                old.cancel();
            }
            AttendeeOperation attendeeOperation = new ChooseToSeeAttendeeOperation(cc, attendeeHwcloud);
            cc.setAttendeeOperation(attendeeOperation);
            attendeeOperation.operate();
        }
    }

    @Override
    public void chooseSee(String conferenceId, String attendeeId, boolean upCascadeOperate, boolean upCascadeBroadcast, boolean upCascadePolling, boolean upCascadeRollCall) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext.getMasterAttendee() == null || conferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue()) {
            throw new SystemException(1005454, "主会场未设置，无法进行选看操作！");
        }

        if (conferenceContext != null) {
            AttendeeHwcloud chooseSeeAttendee = conferenceContext.getAttendeeById(attendeeId);
            if (chooseSeeAttendee != null) {
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
     * @author lilinhai
     * @since 2021-02-09 11:22  void
     */
    @Override
    public void defaultChooseSee(HwcloudConferenceContext mainConferenceContext)
    {

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
        HwcloudConferenceContext cc = HwcloudConferenceContextCache.getInstance().get(contextKey);
        if (cc.getMasterAttendee() == null || cc.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue()) {
            throw new SystemException(1005454, "主会场未设置，无法进行点名操作！");
        }

        AttendeeHwcloud attendee = cc.getAttendeeById(attendeeId);
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
     * @author sinhy
     * @since 2021-12-02 12:47
     * @param conferenceId
     * @param attendeeId
     */
    @Override
    public void talk(String conferenceId, String attendeeId)
    {
        String cn = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        HwcloudConferenceContext mainConferenceContext = HwcloudConferenceContextCache.getInstance().getMainConferenceContext(cn);
        if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue())
        {
            throw new SystemException(1005452, "主会场未设置，无法进行对话操作！");
        }
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(cn);
        if (conferenceContext != null) {
            //TODO
        }
    }

    /**
     * <pre>取消点名</pre>
     * @author lilinhai
     * @since 2021-02-22 16:14
     * @param conferenceId
     */
    @Override
    public void cancelCallTheRoll(String conferenceId)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);

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
     * @param conferenceId
     */
    @Override
    public void cancelTalk(String conferenceId)
    {
        String cn = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(cn);
        if (conferenceContext != null) {
            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            //TODO
        }
    }

    /**
     * <pre>取消当前操作</pre>
     * @author lilinhai
     * @since 2021-02-22 16:14
     * @param conferenceContext
     */
    @Override
    public void cancelCurrentOperation(HwcloudConferenceContext conferenceContext)
    {

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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        AttendeeHwcloud attendee = conferenceContext.getAttendeeById(attendeeId);

        if(attendee!=null){
            String participantUuid = attendee.getParticipantUuid();
            conferenceContext.getHwcloudMeetingBridge().getMeetingControl().muteParticipant( conferenceContext.getHwcloudMeetingBridge().getTokenInfo().getToken(),conferenceContext.getMeetingId(),participantUuid,0);
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext mainConferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue())
        {
            throw new SystemException(1005454, "主会场未设置，无法进行举手操作！");
        }
        if (mainConferenceContext.getMasterAttendee().getId() == attendeeId) {
            throw new SystemException(1005454, "该参会者是主会场，无法进行举手操作！");
        }

        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeHwcloud attendee = conferenceContext.getAttendeeById(attendeeId);
            if(attendee!=null){
                String participantUuid = attendee.getParticipantUuid();
                conferenceContext.getHwcloudMeetingBridge().getMeetingControl().hangUpParticipant( conferenceContext.getHwcloudMeetingBridge().getTokenInfo().getToken(),conferenceContext.getMeetingId(),participantUuid);
            }
        } else {
            throw new SystemException(1005454, "该会议无法进行举手操作！");
        }


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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        RaiseHandStatus raiseHandStatus = RaiseHandStatus.NO;
        // 关闭举手
        AttendeeHwcloud raiseHandsAttendee = conferenceContext.getAttendeeById(attendeeId);
        raiseHandsAttendee.resetUpdateMap();
        raiseHandsAttendee.setRaiseHandStatus(raiseHandStatus.getValue());
        //TODO
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

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);


    }

    @Override
    public void setBanner(String conferenceId, String attendeeId, JSONObject params)
    {
        String cn = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);

    }

    @Override
    public void sendBanner(String conferenceId, JSONObject jsonObject)
    {

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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        AttendeeHwcloud attendee = conferenceContext.getAttendeeById(attendeeId);

        if(attendee!=null){
            String participantUuid = attendee.getParticipantUuid();
            conferenceContext.getHwcloudMeetingBridge().getMeetingControl().muteParticipant( conferenceContext.getHwcloudMeetingBridge().getTokenInfo().getToken(),conferenceContext.getHwcloudMeetingBridge().getConfID(),participantUuid,1);
        }


    }

    @Override
    public void invite(String conferenceId, List<Long> terminalIds)
    {

    }

    @Override
    public void invite(String conferenceId, JSONObject jsonObj)
    {

    }

    private void recallAttendee(String conferenceId, AttendeeHwcloud attendee) {

    }

    private void recallAttendees(String conferenceId, List<AttendeeHwcloud> attendees) {

    }

    @Override
    public void openCamera(String conferenceId, String attendeeId)
    {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);

        // 打开终端摄像头（打开上行视频）
        AttendeeHwcloud attendee = conferenceContext.getAttendeeById(attendeeId);
        if (attendee != null) {
            conferenceContext.getHwcloudMeetingBridge().getMeetingControl().video(conferenceContext.getHwcloudMeetingBridge().getTokenInfo().getToken(),conferenceContext.getMeetingId(),0,attendee.getParticipantUuid());
        }
    }

    @Override
    public void closeCamera(String conferenceId, String attendeeId)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        // 打开终端摄像头（打开上行视频）
        AttendeeHwcloud attendee = conferenceContext.getAttendeeById(attendeeId);
        if (attendee != null) {
            conferenceContext.getHwcloudMeetingBridge().getMeetingControl().video(conferenceContext.getHwcloudMeetingBridge().getTokenInfo().getToken(),conferenceContext.getMeetingId(),1,attendee.getParticipantUuid());
        }
    }

    @Override
    public void openMixing(String conferenceId)
    {


        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            return ;
        }
        conferenceContext.getHwcloudMeetingBridge().getMeetingControl().muteMeeting( conferenceContext.getHwcloudMeetingBridge().getTokenInfo().getToken(),conferenceContext.getMeetingId(),1,0);


    }

    @Override
    public void openMixing(HwcloudConferenceContext cc)
    {
        List<AttendeeHwcloud> as = new ArrayList<>();
        HwcloudConferenceContextUtils.eachNonFmeAttendeeInConference(cc, (a) -> {
            if (a.isMeetingJoined())
            {
                as.add(a);
            }
        });

        if(CollectionUtils.isNotEmpty(as)){
            for (AttendeeHwcloud a : as) {
                openMixing(cc.getId(),a.getId());
            }
        }
    }

    @Override
    public void openDisplayDevice(String conferenceId)
    {
        String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(conferenceNumber);
        // 允许所有人观看（打开所有下行视频）


    }

    @Override
    public void closeDisplayDevice(String conferenceId)
    {
        String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(conferenceNumber);
        // 禁止所有观众观看（关闭所有下行视频）

    }


    @Override
    public void openDisplayDevice(String conferenceId, String attendeeId)
    {
        String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(conferenceNumber);
        // 打开终端摄像头（打开上行视频）
        AttendeeHwcloud attendee = conferenceContext.getAttendeeById(attendeeId);
        if (attendee != null) {
            conferenceContext.getHwcloudMeetingBridge().getMeetingControl().video(conferenceContext.getHwcloudMeetingBridge().getTokenInfo().getToken(),conferenceContext.getMeetingId(),1,attendee.getParticipantUuid());
        }
    }

    @Override
    public void closeDisplayDevice(String conferenceId, String attendeeId)
    {
        String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(conferenceNumber);
        // 打开终端摄像头（打开上行视频）
        AttendeeHwcloud attendee = conferenceContext.getAttendeeById(attendeeId);
        if (attendee != null) {
            conferenceContext.getHwcloudMeetingBridge().getMeetingControl().video(conferenceContext.getHwcloudMeetingBridge().getTokenInfo().getToken(),conferenceContext.getMeetingId(),0,attendee.getParticipantUuid());

        }
    }

    @Override
    public void closeCamera(String conferenceId)
    {
        HwcloudConferenceContextUtils.eachNonFmeAttendeeInConference(HwcloudConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId)), (a) -> {
            if (a.isMeetingJoined())
            {
                closeCamera(conferenceId, a.getId());
            }
        });
    }

    @Override
    public void openCamera(String conferenceId)
    {
        HwcloudConferenceContextUtils.eachNonFmeAttendeeInConference(HwcloudConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId)), (a) -> {
            if (a.isMeetingJoined())
            {
                openCamera(conferenceId, a.getId());
            }
        });
    }

    @Override
    public void closeMixing(String conferenceId)
    {


        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            return ;
        }
        conferenceContext.getHwcloudMeetingBridge().getMeetingControl().muteMeeting( conferenceContext.getHwcloudMeetingBridge().getTokenInfo().getToken(),conferenceContext.getMeetingId(),1,1);

    }

    @Override
    public void closeMixing(HwcloudConferenceContext cc, AttendeeHwcloud... excludes)
    {
        List<AttendeeHwcloud> as = new ArrayList<>();
        HwcloudConferenceContextUtils.eachNonFmeAttendeeInConference(cc, (a) -> {
            for (AttendeeHwcloud attendee : excludes)
            {
                if (a == attendee)
                {
                    return;
                }
            }
            if (a.isMeetingJoined())
            {
                as.add(a);
            }
        });
        if(CollectionUtils.isNotEmpty(as)){
            for (AttendeeHwcloud a : as) {
                closeMixing(cc.getId(),a.getId());
            }
        }
    }

    @Override
    public void sendMessage(String conferenceId, JSONObject jsonObject)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        Integer messageDuration = jsonObject.getInteger("messageDuration");
        if (messageDuration == null) {
            messageDuration = 5;
        }
        Long messageCloseTime = System.currentTimeMillis() + messageDuration * 1000;
        String messagePosition = jsonObject.getString("messagePosition");
        String messageText = jsonObject.getString("messageText");

    }

    /**
     * 设置横幅
     *
     * @param conferenceId
     * @param jsonObject void
     */
    @Override
    public void setMessageBannerText(String conferenceId, JSONObject jsonObject) {
        String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        String messageBannerText = jsonObject.getString("messageBannerText");
        if (!ObjectUtils.isEmpty(messageBannerText)) {
            if (messageBannerText.getBytes().length <= 60) {
                String replacedText = messageBannerText.replaceAll("\t|\r|\n", "");
                if (replacedText.length() < messageBannerText.length()) {
                    throw new SystemException("横幅不支持【制表】【回车】等特殊字符！");
                }
            } else {
                if (messageBannerText.getBytes().length > 60) {
                    throw new SystemException("横幅最多支持20个字符！");
                }
            }
        }
        setMessageBannerText(conferenceNumber, messageBannerText);
    }

    /**
     * 设置横幅
     *
     * @param conferenceNumber
     * @param text
     */
    @Override
    public void setMessageBannerText(String conferenceNumber, String text)
    {
        HwcloudConferenceContext cc = HwcloudConferenceContextCache.getInstance().get(conferenceNumber);
        int hasTitle = 0;
        String textW = "";
        if (StringUtil.isNotEmpty(text)) {
            hasTitle = 1;
            textW = "　" + text + "　";
        }

    }

    @Override
    public void polling(String conferenceId)
    {
        String cn = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(cn);
        HwcloudConferenceContext mainConferenceContext = HwcloudConferenceContextCache.getInstance().getMainConferenceContext(conferenceContext);
        if (mainConferenceContext.getMasterAttendee() == null || !mainConferenceContext.getMasterAttendee().isMeetingJoined())
        {
            throw new SystemException(1005454, "主会场未设置或未呼入,无法进行轮询操作！");
        }

        if (conferenceContext != null) {

        }


    }

    @Override
    public void pollingPause(String conferenceId)
    {
        String cn = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(cn);
        if (conferenceContext != null) {

        }
    }

    @Override
    public void pollingResume(String conferenceId)
    {
        String cn = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(cn);
        if (conferenceContext != null) {

        }
    }

    /**
     * <pre>取消轮询</pre>
     * @author lilinhai
     * @since 2021-02-22 16:14
     * @param conferenceId
     */
    @Override
    public void cancelPolling(String conferenceId)
    {
        String cn = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(cn);
        if (conferenceContext != null) {

        }
    }

    @Override
    public JSONObject detail(String conferenceId, String attendeeId)
    {

        return null;
    }

    private JSONObject subcriberReafInfo(String conferenceId, HwcloudConferenceContext HwcloudConferenceContext, String participantId){
        JSONObject jsonObject = null;
        try {

        } catch (Exception e) {
            log.info(e.getMessage());
        }

        return jsonObject;
    }

    @Override
    public JSONObject detail(HwcloudConferenceContext conferenceContext, AttendeeHwcloud attendee)
    {
        if (attendee != null && attendee.isMeetingJoined())
        {

        }

        return null;
    }

    public JSONObject toDetail(JSONObject jsonObject, AttendeeHwcloud attendee)
    {


        return null;
    }

    /**
     * 与会者呼叫失败通知
     * @author lilinhai
     * @since 2021-02-08 12:49
     * @param participantUuid void
     */
    @Override
    public void callAttendeeFailedNotice(String participantUuid, String reason)
    {

    }

    /**
     * 批量修改参会者业务参数，支持集群
     * @author lilinhai
     * @since 2021-04-19 11:45
     * @param conferenceNumber
     * @param attendees
     * @param nameValuePairs void
     */
    private void updateAttendeeAttrs(String conferenceNumber, List<AttendeeHwcloud> attendees, List<NameValuePair> nameValuePairs) {
    }

    @Override
    public void changeAttendeeName(String conferenceId, JSONObject jsonObj) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);

        String attendeeId = (String) jsonObj.get("attendeeId");
        String name = (String) jsonObj.get("name");
        AttendeeHwcloud attendeeById = conferenceContext.getAttendeeById(attendeeId);
        if (attendeeById == null) {
            return;
        }
        if(attendeeById instanceof McuAttendeeHwcloud){
            throw new CustomException("该会议室不支持改名");
        }
        conferenceContext.getHwcloudMeetingBridge().getMeetingControl().renameParticipant(conferenceContext.getHwcloudMeetingBridge().getTokenInfo().getToken(),conferenceContext.getMeetingId(),attendeeById.getParticipantUuid(),attendeeById.getNumber(),name);


    }

    @Override
    public void cohost(String conferenceId, String attendeeId, Boolean action) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        AttendeeHwcloud attendeeById = conferenceContext.getAttendeeById(attendeeId);
        if(attendeeById==null){
            throw new CustomException("与会者不存在");
        }
        if(attendeeById.getMaster()){
            throw new CustomException("主席不能被设置联席住持人");
        }
        conferenceContext.getHwcloudMeetingBridge().getMeetingControl().setCohost(conferenceContext.getHwcloudMeetingBridge().getTokenInfo().getToken(),conferenceContext.getMeetingId(),attendeeById.getParticipantUuid(),action?1:0);

    }

    @Override
    public Object videoSwitchAttribute(String conferenceId, String participantId, boolean b) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        AttendeeHwcloud a = conferenceContext.getAttendeeById(participantId);
        if(a==null){
            throw new CustomException("与会者不存在");
        }

        String confId = conferenceContext.getMeetingId();
        conferenceContext.getHwcloudMeetingBridge().getMeetingControl().lockView(conferenceContext.getHwcloudMeetingBridge().getTokenInfo().getToken(),confId,a.getParticipantUuid(),a.getParticipantUuid(),b?1:0);

        a.getSmcParticipant().getState().setVideoSwitchAttribute(b ? 1 : 0);
        a.setVideoSwitchAttribute(b ? 1 : 0);
        HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, a.getUpdateMap());
        return b;
    }

    @Override
    public void applyChair(String conferenceId, String attendeeId, Boolean action) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        AttendeeHwcloud a = conferenceContext.getAttendeeById(attendeeId);
        if(a==null){
            throw new CustomException("与会者不存在");
        }
        String hostPassword = conferenceContext.getHwcloudMeetingBridge().getHostPassword();
        String confId = conferenceContext.getMeetingId();
        conferenceContext.getHwcloudMeetingBridge().getMeetingControl().applyChair(conferenceContext.getHwcloudMeetingBridge().getTokenInfo().getToken(),confId,a.getParticipantUuid(),action?1:0,hostPassword);
        if(action){
            a.setUserRole(2);
        }else {
            a.setUserRole(0);
        }
        HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, a.getUpdateMap());

    }

    @Override
    public void batchInvite(String conferenceId, List<CropDirAttendeeHwcloud> attendeeHwclouds) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);

        HwcloudMeetingBridge hwcloudMeetingBridge = conferenceContext.getHwcloudMeetingBridge();
        List<Attendee> participants=new ArrayList<>();
        for (CropDirAttendeeHwcloud attendeeHwcloud : attendeeHwclouds) {
            Attendee attendee = new Attendee();
            attendee.setName(attendeeHwcloud.getName());
            attendee.setRole(attendeeHwcloud.getRole());
            attendee.setPhone(attendeeHwcloud.getSip());
            attendee.setType(attendeeHwcloud.getType());
            participants.add(attendee);
        }
        hwcloudMeetingBridge.getMeetingControl().inviteParticipants(hwcloudMeetingBridge.getTokenInfo().getToken(), conferenceContext.getMeetingId(), participants);
    }
}
