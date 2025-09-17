/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.service.impls
 * @author lilinhai
 * @since 2021-02-05 17:29
 * @version  V1.0
 */
package com.paradisecloud.fcm.tencent.service2.impls;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.AttendeeMeetingStatus;
import com.paradisecloud.fcm.common.enumer.AttendeeMixingStatus;
import com.paradisecloud.fcm.common.enumer.RaiseHandStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.AesEnsUtils;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiUserTerminalMapper;
import com.paradisecloud.fcm.fme.attendee.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.attendee.BaseFixedParamValue;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.tencent.busi.attende.AttendeeTencent;
import com.paradisecloud.fcm.tencent.busi.attende.McuAttendeeTencent;
import com.paradisecloud.fcm.tencent.busi.attende.RoomAttendeeTencent;
import com.paradisecloud.fcm.tencent.cache.*;
import com.paradisecloud.fcm.tencent.model.AttendeeNoticeInfo;
import com.paradisecloud.fcm.tencent.model.SmcParitipantsStateRep;
import com.paradisecloud.fcm.tencent.model.client.TencentConferenceCtrlClient;
import com.paradisecloud.fcm.tencent.model.client.TencentMeetingClient;
import com.paradisecloud.fcm.tencent.model.operation.AttendeeOperation;
import com.paradisecloud.fcm.tencent.model.operation.CallTheRollAttendeeOperation;
import com.paradisecloud.fcm.tencent.model.operation.ChangeMasterAttendeeOperation;
import com.paradisecloud.fcm.tencent.model.operation.ChooseToSeeAttendeeOperation;
import com.paradisecloud.fcm.tencent.model.reponse.MeetingStatusResponse;
import com.paradisecloud.fcm.tencent.model.reponse.RoomCallReponse;
import com.paradisecloud.fcm.tencent.model.reponse.RoomResponse;
import com.paradisecloud.fcm.tencent.model.request.*;
import com.paradisecloud.fcm.tencent.service2.interfaces.IAttendeeTencentService;
import com.paradisecloud.fcm.tencent.task.TencentDelayTaskService;
import com.paradisecloud.fcm.tencent.utils.StringUtils;
import com.paradisecloud.fcm.tencent.utils.TencentConferenceContextUtils;
import com.sinhy.exception.SystemException;
import com.sinhy.utils.Base64Utils;
import com.tencentcloudapi.wemeet.common.exception.WemeetSdkException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.NameValuePair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
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
public class AttendeeTencentServiceImpl implements IAttendeeTencentService {

    public static final String LAYOUT_TEMPLATE = "layoutTemplate";
    public static final String DEFAULT_LAYOUT = "defaultLayout";
    public static final String NOT_MRA_VIDEO = "非MRA用户不支持开启视频";
    public static final String NO_MEMBER = "该会议室不支持改名";

    @Resource
    private IMqttService mqttService;

    @Resource
    private TencentDelayTaskService TencentdelayTaskService;

    @Resource
    private RedisCache redisCache;

    @Resource
    private BusiUserTerminalMapper busiUserTerminalMapper;

    private boolean isContains(AttendeeTencent a, AttendeeTencent... excludes)
    {
        if (!ObjectUtils.isEmpty(excludes))
        {
            for (AttendeeTencent attendee : excludes)
            {
                if (a == attendee)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateAttendeeImportance(AttendeeTencent attendee, AttendeeImportance attendeeImportance)
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
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);

        AttendeeTencent attendee = conferenceContext.getAttendeeById(attendeeId);
        if(attendee!=null&&attendee instanceof RoomAttendeeTencent){
            RoomCallRequest request = new RoomCallRequest();
            request.setMeetingRoomId(attendee.getId());
            request.setOperatorIdType(1);
            request.setOperatorId(conferenceContext.getTencentUser());
            request.setMeetingId(conferenceContext.getMeetingId());
            TencentConferenceCtrlClient conferenceCtrlClient = conferenceContext.getTencentBridge().getConferenceCtrlClient();
            try {
                RoomCallReponse roomCallReponse = conferenceCtrlClient.roomsInvite(request);
                TencentWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【"+attendee.getName()+"】"+"开始呼叫");

            } catch (WemeetSdkException e) {
            }
        }


    }

    @Override
    public void callAttendee(AttendeeTencent attendee)
    {
//        new CallAttendeeProcessor(attendee).process();
    }

    @Override
    public void hangUp(String conferenceId, String attendeeId)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);

        AttendeeTencent attendee = conferenceContext.getAttendeeById(attendeeId);

        SmcParitipantsStateRep.ContentDTO participant = attendee.getSmcParticipant();
        TencentMeetingClient meeting_client = conferenceContext.getTencentBridge().getMEETING_CLIENT();

        Integer instanceid = participant.getInstanceid();
        if(instanceid!=9){
            throw new CustomException("设备类型不支持");
        }
        HangupRequest hangupRequest = new HangupRequest();
        hangupRequest.setInstanceid(1);
        hangupRequest.setOperatorIdType(1);
        hangupRequest.setMeetingId(conferenceContext.getMeetingId());
        HangupRequest.UserDTO userDTO = new HangupRequest.UserDTO();
        userDTO.setInstanceid(9);
        userDTO.setMsOpenId(participant.getMs_open_id());
        hangupRequest.setUser(userDTO);
        hangupRequest.setOperatorId(conferenceContext.getTencentUser());

        try {
            meeting_client.hangUp(hangupRequest);
            attendee.setHangUp(true);
        } catch (WemeetSdkException e) {
            log.info(e.getMessage());
            throw new CustomException(e.getMessage());
        }
    }

    @Override
    public void remove(String conferenceId, String attendeeId)
    {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        AttendeeTencent attendee = conferenceContext.getAttendeeById(attendeeId);
        SmcParitipantsStateRep.ContentDTO participant = attendee.getSmcParticipant();

        TencentConferenceCtrlClient conferenceCtrlClient = conferenceContext.getTencentBridge().getConferenceCtrlClient();

        RemoveParticipantRequest request=new RemoveParticipantRequest();

        request.setMeetingId(conferenceContext.getMeetingId());
        request.setInstanceid(1);
        request.setAllowRejoin(true);
        request.setReason("MANAGER_REMOVE");
        request.setOperatorIdType(4);
        request.setOperatorId(conferenceContext.getMsopenid());
        RemoveParticipantRequest.UsersDTO usersDTO = new RemoveParticipantRequest.UsersDTO();
        usersDTO.setUuid(participant.getUuid());
        usersDTO.setToOperatorId(participant.getMs_open_id());
        usersDTO.setToOperatorIdType(4);
        usersDTO.setInstanceid(participant.getInstanceid());
        request.setUsers(Arrays.asList(usersDTO));
        try {
            conferenceCtrlClient.kickout(request);
           // conferenceContext.removeAttendeeById(attendee.getId());
        } catch (WemeetSdkException e) {
            log.info(e.getMessage());
            String join = StringUtils.extractChinese(e.getMessage());
            throw new CustomException(join);
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
        TencentConferenceContext cc = TencentConferenceContextCache.getInstance().get(contextKey);
        AttendeeTencent attendeeTencent = cc.getAttendeeById(attendeeId);
        if (attendeeTencent != null) {
            if (cc.isDownCascadeConference()) {
                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(cc.getUpCascadeConferenceId()));
                if (baseConferenceContext != null) {
                    if (!attendeeTencent.getRemoteParty().equals(cc.getUpCascadeRemoteParty())) {
                        throw new SystemException(1005454, "该会议正被级联，不允许修改主会场！");
                    }
                }
            }

            AttendeeOperation old = cc.getAttendeeOperation();
            if(old!=null){
                cc.setLastAttendeeOperation(old);
                old.cancel();
            }
            AttendeeOperation attendeeOperation = new ChangeMasterAttendeeOperation(cc, attendeeTencent);
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
        TencentConferenceContext cc = TencentConferenceContextCache.getInstance().get(contextKey);
        if (cc.getMasterAttendee() == null || cc.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue()) {
            throw new SystemException(1005454, "主会场未设置，无法进行选看操作！");
        }

        AttendeeTencent attendeeTencent = cc.getAttendeeById(attendeeId);
        if (attendeeTencent == null) {
            throw new CustomException("与会者不存在，无法进行选看操作！");
        }
        if (cc != null) {
            AttendeeOperation old = cc.getAttendeeOperation();
            if(old!=null){
                cc.setLastAttendeeOperation(old);
                old.cancel();
            }
            AttendeeOperation attendeeOperation = new ChooseToSeeAttendeeOperation(cc, attendeeTencent);
            cc.setAttendeeOperation(attendeeOperation);
            attendeeOperation.operate();
        }
    }

    @Override
    public void chooseSee(String conferenceId, String attendeeId, boolean upCascadeOperate, boolean upCascadeBroadcast, boolean upCascadePolling, boolean upCascadeRollCall) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext.getMasterAttendee() == null || conferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue()) {
            throw new SystemException(1005454, "主会场未设置，无法进行选看操作！");
        }

        if (conferenceContext != null) {
            AttendeeTencent chooseSeeAttendee = conferenceContext.getAttendeeById(attendeeId);
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
    public void defaultChooseSee(TencentConferenceContext mainConferenceContext)
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
        TencentConferenceContext cc = TencentConferenceContextCache.getInstance().get(contextKey);
        if (cc.getMasterAttendee() == null || cc.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue()) {
            throw new SystemException(1005454, "主会场未设置，无法进行点名操作！");
        }

        AttendeeTencent attendee = cc.getAttendeeById(attendeeId);
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
        TencentConferenceContext mainConferenceContext = TencentConferenceContextCache.getInstance().getMainConferenceContext(cn);
        if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue())
        {
            throw new SystemException(1005452, "主会场未设置，无法进行对话操作！");
        }
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(cn);
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
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);

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
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(cn);
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
    public void cancelCurrentOperation(TencentConferenceContext conferenceContext)
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
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        AttendeeTencent attendee = conferenceContext.getAttendeeById(attendeeId);
        com.paradisecloud.fcm.tencent.model.SmcParitipantsStateRep.ContentDTO participant =attendee.getSmcParticipant() ;
        TencentConferenceCtrlClient conferenceCtrlClient = conferenceContext.getTencentBridge().getConferenceCtrlClient();
        MuteParticipantRequest request=new MuteParticipantRequest();

        request.setMeetingId(conferenceContext.getMeetingId());
        request.setInstanceid(1);
        request.setMute(false);
        request.setOperatorIdType(4);
        request.setOperatorId(conferenceContext.getMsopenid());
        MuteParticipantRequest.UserDTO usersDTO = new MuteParticipantRequest.UserDTO();
//        usersDTO.setUuid(participant.getUuid());
        usersDTO.setToOperatorId(participant.getMs_open_id());
        usersDTO.setToOperatorIdType(4);
        usersDTO.setInstanceid(participant.getInstanceid());
        request.setUser(usersDTO);

        try {
            conferenceCtrlClient.muteParticpant(request);
        } catch (WemeetSdkException e) {
            log.info(e.getMessage());
            throw new CustomException(e.getMessage());
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
        String cn = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        TencentConferenceContext mainConferenceContext = TencentConferenceContextCache.getInstance().getMainConferenceContext(cn);
        if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue())
        {
            throw new SystemException(1005454, "主会场未设置，无法进行举手操作！");
        }
        if (mainConferenceContext.getMasterAttendee().getId() == attendeeId) {
            throw new SystemException(1005454, "该参会者是主会场，无法进行举手操作！");
        }

        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(cn);
        if (conferenceContext != null) {

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
        String cn = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(cn);
        RaiseHandStatus raiseHandStatus = RaiseHandStatus.NO;
        // 关闭举手
        AttendeeTencent raiseHandsAttendee = conferenceContext.getAttendeeById(attendeeId);
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
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        TencentMeetingClient meeting_client = conferenceContext.getTencentBridge().getMEETING_CLIENT();
        SmcParitipantsStateRep.ContentDTO participant = conferenceContext.getAttendeeById(attendeeId).getSmcParticipant();
        RaiseHandRequest request = new RaiseHandRequest();
        request.setMeetingId(conferenceId);
        request.setInstanceid(1);
        request.setOperatorIdType(4);
        request.setOperatorId(conferenceContext.getMsopenid());
        request.setRaise_hand(raiseHandStatus.getValue()==1?Boolean.TRUE:Boolean.FALSE);
        RaiseHandRequest.UserDTO userDTO = new RaiseHandRequest.UserDTO();
        userDTO.setInstanceid(participant.getInstanceid());
        userDTO.setMsOpenId(participant.getMs_open_id());
        request.setUser(userDTO);
        try {
            meeting_client.raiseHand(request);

        } catch (WemeetSdkException e) {
            log.info(e.getMessage());
            throw new CustomException(e.getMessage());
        }

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
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        com.paradisecloud.fcm.tencent.model.SmcParitipantsStateRep.ContentDTO participant = conferenceContext.getAttendeeById(attendeeId).getSmcParticipant();
        TencentConferenceCtrlClient conferenceCtrlClient = conferenceContext.getTencentBridge().getConferenceCtrlClient();
        MuteParticipantRequest request=new MuteParticipantRequest();

        request.setMeetingId(conferenceContext.getMeetingId());
        request.setInstanceid(1);
        request.setMute(true);
        request.setOperatorIdType(4);
        request.setOperatorId(conferenceContext.getMsopenid());
        MuteParticipantRequest.UserDTO usersDTO = new MuteParticipantRequest.UserDTO();
//        usersDTO.setUuid(participant.getUuid());
        usersDTO.setToOperatorId(participant.getMs_open_id());
        usersDTO.setToOperatorIdType(4);
        usersDTO.setInstanceid(participant.getInstanceid());
        request.setUser(usersDTO);

        try {
            conferenceCtrlClient.muteParticpant(request);

        } catch (WemeetSdkException e) {
            log.info(e.getMessage());
            throw new CustomException(e.getMessage());
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

    private void recallAttendee(String conferenceId, AttendeeTencent attendee) {

    }

    private void recallAttendees(String conferenceId, List<AttendeeTencent> attendees) {

    }

    @Override
    public void openCamera(String conferenceId, String attendeeId)
    {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        com.paradisecloud.fcm.tencent.model.SmcParitipantsStateRep.ContentDTO participant = conferenceContext.getAttendeeById(attendeeId).getSmcParticipant();
        TencentConferenceCtrlClient conferenceCtrlClient = conferenceContext.getTencentBridge().getConferenceCtrlClient();
        VideoParticipantRequest request=new VideoParticipantRequest();

        request.setMeetingId(conferenceContext.getMeetingId());
        request.setInstanceid(1);
        request.setVideo(true);
        request.setOperatorIdType(4);
        request.setOperatorId(conferenceContext.getMsopenid());
        VideoParticipantRequest.UserDTO usersDTO = new VideoParticipantRequest.UserDTO();
        usersDTO.setUuid(participant.getUuid());
        usersDTO.setToOperatorId(participant.getMs_open_id());
        usersDTO.setToOperatorIdType(4);
        usersDTO.setInstanceid(participant.getInstanceid());
        request.setUser(usersDTO);

        try {
            conferenceCtrlClient.videoParticpant(request);

        } catch (WemeetSdkException e) {
            log.info(e.getMessage());
            if(e.getMessage().contains(NOT_MRA_VIDEO)){
                throw new CustomException("非MRA用户不支持开启视频");
            }
        }


    }

    @Override
    public void closeCamera(String conferenceId, String attendeeId)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        com.paradisecloud.fcm.tencent.model.SmcParitipantsStateRep.ContentDTO participant = conferenceContext.getAttendeeById(attendeeId).getSmcParticipant();
        TencentConferenceCtrlClient conferenceCtrlClient = conferenceContext.getTencentBridge().getConferenceCtrlClient();
        VideoParticipantRequest request=new VideoParticipantRequest();

        request.setMeetingId(conferenceContext.getMeetingId());
        request.setInstanceid(1);
        request.setVideo(false);
        request.setOperatorIdType(4);
        request.setOperatorId(conferenceContext.getMsopenid());
        VideoParticipantRequest.UserDTO usersDTO = new VideoParticipantRequest.UserDTO();
        usersDTO.setUuid(participant.getUuid());
        usersDTO.setToOperatorId(participant.getMs_open_id());
        usersDTO.setToOperatorIdType(4);
        usersDTO.setInstanceid(participant.getInstanceid());
        request.setUser(usersDTO);

        try {
            conferenceCtrlClient.videoParticpant(request);
        } catch (WemeetSdkException e) {
            log.info(e.getMessage());
            throw new CustomException(e.getMessage());
        }
    }

    @Override
    public void openMixing(String conferenceId)
    {
//        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
//        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
//        openMixing(conferenceContext);

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            return ;
        }
        TencentConferenceCtrlClient conferenceCtrlClient = conferenceContext.getTencentBridge().getConferenceCtrlClient();

        ModifyConferenceRequest modifyConferenceRequest = new ModifyConferenceRequest();
        modifyConferenceRequest.setInstanceid(1);
        modifyConferenceRequest.setOperatorIdType(4);
        modifyConferenceRequest.setOperatorId(conferenceContext.getMsopenid());
        modifyConferenceRequest.setMeetingId(conferenceContext.getMeetingId());
        modifyConferenceRequest.setMuteAll(false);

        try {
            MeetingStatusResponse meetingStatusResponse = conferenceCtrlClient.modifyConferenceStatus(modifyConferenceRequest);
            conferenceContext.setMuteAll(true);
            TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE,meetingStatusResponse);
        } catch (WemeetSdkException e) {
            throw new CustomException(e.getMessage());
        }

    }

    @Override
    public void openMixing(TencentConferenceContext cc)
    {
        List<AttendeeTencent> as = new ArrayList<>();
        TencentConferenceContextUtils.eachNonFmeAttendeeInConference(cc, (a) -> {
            if (a.isMeetingJoined())
            {
                as.add(a);
            }
        });

        if(CollectionUtils.isNotEmpty(as)){
            for (AttendeeTencent a : as) {
                openMixing(cc.getId(),a.getId());
            }
        }
    }

    @Override
    public void openDisplayDevice(String conferenceId)
    {
        String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(conferenceNumber);
        // 允许所有人观看（打开所有下行视频）


    }

    @Override
    public void closeDisplayDevice(String conferenceId)
    {
        String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(conferenceNumber);
        // 禁止所有观众观看（关闭所有下行视频）

    }


    @Override
    public void openDisplayDevice(String conferenceId, String attendeeId)
    {
        String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(conferenceNumber);
        // 打开终端摄像头（打开上行视频）
        AttendeeTencent attendee = conferenceContext.getAttendeeById(attendeeId);
        if (attendee != null) {

        }
    }

    @Override
    public void closeDisplayDevice(String conferenceId, String attendeeId)
    {
        String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(conferenceNumber);
        // 打开终端摄像头（打开上行视频）
        AttendeeTencent attendee = conferenceContext.getAttendeeById(attendeeId);
        if (attendee != null) {

        }
    }

    @Override
    public void closeCamera(String conferenceId)
    {
        TencentConferenceContextUtils.eachNonFmeAttendeeInConference(TencentConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId)), (a) -> {
            if (a.isMeetingJoined())
            {
                closeCamera(conferenceId, a.getId());
            }
        });
    }

    @Override
    public void openCamera(String conferenceId)
    {
        TencentConferenceContextUtils.eachNonFmeAttendeeInConference(TencentConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId)), (a) -> {
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
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            return ;
        }
        AttendeeTencent masterAttendee = conferenceContext.getMasterAttendee();

        TencentConferenceCtrlClient conferenceCtrlClient = conferenceContext.getTencentBridge().getConferenceCtrlClient();


        if(masterAttendee!=null){
            masterAttendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
        }
        ModifyConferenceRequest modifyConferenceRequest = new ModifyConferenceRequest();
        modifyConferenceRequest.setInstanceid(1);
        modifyConferenceRequest.setOperatorIdType(4);
        modifyConferenceRequest.setOperatorId(conferenceContext.getMsopenid());
        modifyConferenceRequest.setMeetingId(conferenceContext.getMeetingId());
        modifyConferenceRequest.setMuteAll(true);
        try {
            MeetingStatusResponse meetingStatusResponse = conferenceCtrlClient.modifyConferenceStatus(modifyConferenceRequest);
            conferenceContext.setMuteAll(true);
            TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE,meetingStatusResponse);
        } catch (WemeetSdkException e) {
            throw new CustomException(e.getMessage());
        }



        if(masterAttendee!=null){
            openMixing(conferenceId, conferenceContext.getMasterAttendee().getId());
        }


    }

    @Override
    public void closeMixing(TencentConferenceContext cc, AttendeeTencent... excludes)
    {
        List<AttendeeTencent> as = new ArrayList<>();
        TencentConferenceContextUtils.eachNonFmeAttendeeInConference(cc, (a) -> {
            for (AttendeeTencent attendee : excludes)
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
            for (AttendeeTencent a : as) {
                closeMixing(cc.getId(),a.getId());
            }
        }
    }

    @Override
    public void sendMessage(String conferenceId, JSONObject jsonObject)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
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
        TencentConferenceContext cc = TencentConferenceContextCache.getInstance().get(conferenceNumber);
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
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(cn);
        TencentConferenceContext mainConferenceContext = TencentConferenceContextCache.getInstance().getMainConferenceContext(conferenceContext);
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
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(cn);
        if (conferenceContext != null) {

        }
    }

    @Override
    public void pollingResume(String conferenceId)
    {
        String cn = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(cn);
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
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(cn);
        if (conferenceContext != null) {

        }
    }

    @Override
    public JSONObject detail(String conferenceId, String attendeeId)
    {

        return null;
    }

    private JSONObject subcriberReafInfo(String conferenceId, TencentConferenceContext TencentConferenceContext, String participantId){
        JSONObject jsonObject = null;
        try {

        } catch (Exception e) {
            log.info(e.getMessage());
        }

        return jsonObject;
    }

    @Override
    public JSONObject detail(TencentConferenceContext conferenceContext, AttendeeTencent attendee)
    {
        if (attendee != null && attendee.isMeetingJoined())
        {

        }

        return null;
    }

    public JSONObject toDetail(JSONObject jsonObject, AttendeeTencent attendee)
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
    private void updateAttendeeAttrs(String conferenceNumber, List<AttendeeTencent> attendees, List<NameValuePair> nameValuePairs) {
    }

    @Override
    public void changeAttendeeName(String conferenceId, JSONObject jsonObj) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);

        String attendeeId = (String) jsonObj.get("attendeeId");
        String name = (String) jsonObj.get("name");
        AttendeeTencent attendeeById = conferenceContext.getAttendeeById(attendeeId);
        if (attendeeById == null) {
            return;
        }
        if(attendeeById instanceof McuAttendeeTencent){
            throw new CustomException("该会议室不支持改名");
        }


        TencentConferenceCtrlClient conferenceCtrlClient = conferenceContext.getTencentBridge().getConferenceCtrlClient();
        NameParticipantRequest request = new NameParticipantRequest();

        request.setMeetingId(conferenceContext.getMeetingId());
        request.setInstanceid(1);
        request.setOperatorIdType(4);
        request.setOperatorId(conferenceContext.getMsopenid());
        NameParticipantRequest.UsersDTO usersDTO = new NameParticipantRequest.UsersDTO();
        usersDTO.setMsOpenid(attendeeById.getMs_open_id());
        usersDTO.setInstanceid(attendeeById.getInstanceid());
        usersDTO.setNick_name(name);
        request.setUsers(Arrays.asList(usersDTO));
        try {
            conferenceCtrlClient.nameChange(request);
            attendeeById.setName(name);
            attendeeById.setNickName( Base64.getEncoder().encodeToString(name.getBytes(StandardCharsets.UTF_8)));
            TencentWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeById.getUpdateMap());
        } catch (WemeetSdkException e) {
            log.info(e.getMessage());
            if(e.getMessage().contains(NO_MEMBER)){
                throw new CustomException("该会议室不支持改名");
            }
        }
    }

    @Override
    public void cohost(String conferenceId, String attendeeId, Boolean action) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        TencentConferenceCtrlClient conferenceCtrlClient = conferenceContext.getTencentBridge().getConferenceCtrlClient();
        AttendeeTencent attendeeTencent = conferenceContext.getAttendeeById(attendeeId);

        HostsParticipantRequest request = new HostsParticipantRequest();
        request.setMeetingId(conferenceContext.getMeetingId());
        request.setInstanceid(1);
        request.setOperatorIdType(4);
        request.setOperatorId(conferenceContext.getMsopenid());
        request.setAction(action);
        HostsParticipantRequest.UserDTO usersDTO = new HostsParticipantRequest.UserDTO();
        usersDTO.setInstanceid(attendeeTencent.getInstanceid());
        usersDTO.setToOperatorIdType(4);
        usersDTO.setToOperatorId(attendeeTencent.getMs_open_id());
        request.setUser(usersDTO);
        try {
            conferenceCtrlClient.hostsAction(request);
            attendeeTencent.setHost(action);
            TencentWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeTencent.getUpdateMap());
        } catch (WemeetSdkException e) {
            throw new CustomException(e.getMessage());
        }
    }

    @Override
    public RoomResponse rooms(Long deptId,Integer pageIndex,Integer pageSize,String meetingRoomName) {

        TencentBridge tencentBridge = TencentBridgeCache.getInstance().getAvailableBridgesByDept(deptId);

        if(tencentBridge==null){
            return null;
        }
        return TencentRoomsCache.getInstance().getMeetingRoomList(tencentBridge.getBusiTencent().getId(), pageIndex, pageSize);
//        TencentConferenceCtrlClient conferenceCtrlClient = tencentBridge.getConferenceCtrlClient();
//        QueryRoomsRequest queryRoomsRequest = new QueryRoomsRequest();
//        queryRoomsRequest.setPage(pageIndex);
//        queryRoomsRequest.setPageSize(pageSize);
//        queryRoomsRequest.setMeeting_room_name(meetingRoomName);
//
//        try {
//            RoomResponse roomResponse = conferenceCtrlClient.queryRooms(queryRoomsRequest);
//            return roomResponse;
//        } catch (WemeetSdkException e) {
//            throw new CustomException(e.getMessage());
//        }
    }

    @Override
    public RoomResponse rooms(TencentBridge tencentBridge,Integer pageIndex,Integer pageSize,String meetingRoomName) {
        TencentConferenceCtrlClient conferenceCtrlClient = tencentBridge.getConferenceCtrlClient();
        QueryRoomsRequest queryRoomsRequest = new QueryRoomsRequest();
        queryRoomsRequest.setPage(pageIndex);
        queryRoomsRequest.setPageSize(pageSize);
        queryRoomsRequest.setMeeting_room_name(meetingRoomName);

        try {
            RoomResponse roomResponse = conferenceCtrlClient.queryRooms(queryRoomsRequest);
            return roomResponse;
        } catch (WemeetSdkException e) {
            throw new CustomException(e.getMessage());
        }
    }

    @Override
    public Object roomsInvite(String conferenceId, List<String> roomIds) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        List<String> result = new ArrayList<>();

        TencentConferenceCtrlClient conferenceCtrlClient = conferenceContext.getTencentBridge().getConferenceCtrlClient();
        for (String roomId : roomIds) {
            RoomCallRequest request = new RoomCallRequest();
            request.setMeetingRoomId(roomId);
            request.setOperatorIdType(1);
            request.setOperatorId(conferenceContext.getTencentUser());
            request.setMeetingId(conferenceContext.getMeetingId());
            try {
                RoomCallReponse roomCallReponse = conferenceCtrlClient.roomsInvite(request);
                result.add(roomCallReponse.getInviteId());
            } catch (WemeetSdkException e) {
                log.info(e.getMessage());
            }
        }
        return result;
    }
}
