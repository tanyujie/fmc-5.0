package com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client;

import com.huaweicloud.sdk.core.exception.SdkException;
import com.huaweicloud.sdk.meeting.v1.MeetingClient;
import com.huaweicloud.sdk.meeting.v1.model.*;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client.body.RestCustomMultiPictureBody2;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client.request.SetCustomMultiPictureRequest2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author nj
 * @date 2024/2/27 17:21
 */
public class MeetingControl {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final MeetingClient userClient;

    public MeetingControl(MeetingClient userClient) {
        this.userClient = userClient;
    }

    public String  createConfToken(String conferenceId, String token) {
        logger.info("Start createConfToken...");

        CreateWebSocketTokenRequest request = new CreateWebSocketTokenRequest()
                .withConferenceID(conferenceId)
                .withXConferenceAuthorization(token);
        try {
            CreateWebSocketTokenResponse response = userClient.createWebSocketToken(request);
            return  response.getWebSocketToken();
        } catch (SdkException e) {
            logger.info(e.getMessage());
        }

        return null;
    }

    public CreateConfTokenResponse createConfTokenResponse(String conferenceId, String hostPassword) {
        logger.info("Start createConfToken...");
        String confToken = "";

        CreateConfTokenRequest request = new CreateConfTokenRequest()
                .withConferenceID(conferenceId)
                .withXPassword(hostPassword)
                .withXLoginType(1);
        try {
            CreateConfTokenResponse response = userClient.createConfToken(request);
            confToken = response.getData().getToken();
            logger.info("The token of conference %s is %s \r\n", conferenceId, confToken);
            return response;
        } catch (SdkException e) {
            logger.info(e.getMessage());
        }

        return null;
    }

    public void inviteParticipants(String confToken, String conferenceId, String sipNumber, String name) {
        logger.info("Start inviteParticipants...");

        Attendee participant = new Attendee()
                .withName(name)
                .withPhone(sipNumber)
                .withType("normal");
        List<Attendee> participants = new ArrayList<>();
        participants.add(participant);
        RestInviteReqBody body = new RestInviteReqBody()
                .withAttendees(participants);

        InviteParticipantRequest request = new InviteParticipantRequest()
                .withXConferenceAuthorization(confToken)
                .withConferenceID(conferenceId)
                .withBody(body);

        try {
            userClient.inviteParticipant(request);
        } catch (SdkException e) {
            logger.info(e.getMessage());
        }
    }

    public void hangUpParticipant(String confToken, String conferenceId, String id) {
        logger.info("Start hangUpParticipant...");

        List<String> bulkHangUpParticipants = new ArrayList<>();
        bulkHangUpParticipants.add(id);
        RestBulkHangUpReqBody restBulkHangUpReqBody = new RestBulkHangUpReqBody().withBulkHangUpParticipants(bulkHangUpParticipants);

        HangUpRequest request = new HangUpRequest()
                .withXConferenceAuthorization(confToken)
                .withConferenceID(conferenceId)
                .withBody(restBulkHangUpReqBody);

        try {
            userClient.hangUp(request);
        } catch (SdkException e) {
            logger.info(e.getMessage());
        }
    }

    public void deleteAttendee(String confToken, String conferenceId, String number,String participantID) {
        logger.info("Start hangUpParticipant...");
        List<DelAttendInfo> bulkDelAttendInfo = new ArrayList<>();
        DelAttendInfo delAttendInfo = new DelAttendInfo();
        delAttendInfo.setNumber(number);
        delAttendInfo.setParticipantID(participantID);
        bulkDelAttendInfo.add(delAttendInfo);

        RestBulkDelAttendReqBody restBulkHangUpReqBody = new RestBulkDelAttendReqBody().withBulkDelAttendInfo(bulkDelAttendInfo);

        DeleteAttendeesRequest request = new DeleteAttendeesRequest()
                .withXConferenceAuthorization(confToken)
                .withConferenceID(conferenceId)
                .withBody(restBulkHangUpReqBody);

        try {
            userClient.deleteAttendees(request);
        } catch (SdkException e) {
            logger.info(e.getMessage());
        }
    }


    public void hangUpParticipants(String confToken, String conferenceId, List<String> bulkHangUpParticipants) {
        logger.info("Start hangUpParticipants...");


        RestBulkHangUpReqBody restBulkHangUpReqBody = new RestBulkHangUpReqBody().withBulkHangUpParticipants(bulkHangUpParticipants);

        HangUpRequest request = new HangUpRequest()
                .withXConferenceAuthorization(confToken)
                .withConferenceID(conferenceId)
                .withBody(restBulkHangUpReqBody);

        try {
            userClient.hangUp(request);
        } catch (SdkException e) {
            logger.info(e.getMessage());
        }
    }


    public void inviteParticipants(String confToken, String conferenceId, List<Attendee> participants) {
        logger.info("Start inviteParticipants...");

        if (CollectionUtils.isEmpty(participants)) {
            return;
        }
        RestInviteReqBody body = new RestInviteReqBody()
                .withAttendees(participants);

        InviteParticipantRequest request = new InviteParticipantRequest()
                .withXConferenceAuthorization(confToken)
                .withConferenceID(conferenceId)
                .withBody(body);

        InviteParticipantResponse inviteParticipantResponse = userClient.inviteParticipant(request);
        int httpStatusCode = inviteParticipantResponse.getHttpStatusCode();

    }


    public void setMultiImageLayout(String confToken, String conferenceId, String sipNumber) {
        logger.info("Start setMultiImageLayout...");

        List<String> sipNumbers = new ArrayList<>();
        sipNumbers.add(sipNumber);

        RestSubscriberInPic subscriberInPic = new RestSubscriberInPic()
                .withIndex(1)
                .withSubscriber(sipNumbers);

        List<RestSubscriberInPic> subscriberInPics = new ArrayList<>();
        subscriberInPics.add(subscriberInPic);

        RestCustomMultiPictureBody body = new RestCustomMultiPictureBody()
                .withManualSet(1)
                .withSubscriberInPics(subscriberInPics)
                .withImageType("Six")
                .withMultiPicSaveOnly(false);

        SetCustomMultiPictureRequest request = new SetCustomMultiPictureRequest()
                .withXConferenceAuthorization(confToken)
                .withConferenceID(conferenceId)
                .withBody(body);

        try {
            userClient.setCustomMultiPicture(request);
        } catch (SdkException e) {
            logger.info(e.getMessage());
        }
    }




    public void stopMeeting(String confToken, String conferenceId) {
        logger.info("Start stopMeeting...");
        StopMeetingRequest stopMeetingRequest = new StopMeetingRequest()
                .withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken);

        try {
            userClient.stopMeeting(stopMeetingRequest);
        } catch (SdkException e) {

            logger.info(e.getMessage());
        }

    }

    /**
     * @param confToken
     * @param conferenceId
     * @param participantID
     * @param applyChair    1：申请主持人, 0：释放主持人
     * @param chairmanPwd
     */
    public void applyChair(String confToken, String conferenceId, String participantID, Integer applyChair, String chairmanPwd) {

        RestChairTokenReqBody body = new RestChairTokenReqBody().withApplyChair(applyChair).withChairmanPwd(chairmanPwd);


        SetRoleRequest request = new SetRoleRequest()
                .withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken)
                .withBody(body)
                .withParticipantID(participantID);

        try {
            userClient.setRole(request);
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new CustomException(e.getMessage());
        }

    }

    /**
     * 申请联席主持人
     * 0：撤销联席主持人
     * 1：设置联席主持人
     * 最小值：0
     * 最大值：1
     *
     * @param confToken
     * @param conferenceId
     * @param participantID
     * @param isCohost
     */
    public void setCohost(String confToken, String conferenceId, String participantID, Integer isCohost) {
        RestSetCohostBody body = new RestSetCohostBody().withIsCohost(isCohost);

        SetCohostRequest request = new SetCohostRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken)
                .withParticipantID(participantID)
                .withBody(body);
        int httpStatusCode = 0;
        try {
            SetCohostResponse setCohostResponse = userClient.setCohost(request);
            httpStatusCode = setCohostResponse.getHttpStatusCode();

        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new CustomException(e.getMessage());
        }
        httpcode(httpStatusCode);
    }

    /**
     * 0：取消静音
     * 1：静音
     *
     * @param confToken
     * @param conferenceId
     * @param participantID
     * @param isMute
     */
    public void muteParticipant(String confToken, String conferenceId, String participantID, Integer isMute) {

        RestMuteParticipantReqBody body = new RestMuteParticipantReqBody().withIsMute(isMute);

        MuteParticipantRequest request = new MuteParticipantRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken)
                .withParticipantID(participantID)
                .withBody(body);
        try {
            userClient.muteParticipant(request);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 该接口用于设置整个会议所有与会者（主持人除外）的静音/取消静音状态。
     *
     * @param confToken
     * @param conferenceId
     * @param allowUnmuteByOneself 是否允许自己解除静音（仅静音时有效），默认为允许。
     *                             0： 不允许
     *                             1： 允许
     * @param isMute               0：取消静音 1：静音
     */
    public void muteMeeting(String confToken, String conferenceId, Integer allowUnmuteByOneself, Integer isMute) {

        RestMuteReqBody body = new RestMuteReqBody().withIsMute(isMute).withAllowUnmuteByOneself(allowUnmuteByOneself);

        MuteMeetingRequest request = new MuteMeetingRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken)
                .withBody(body);
        try {
            userClient.muteMeeting(request);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 该接口用于设置指定与会者的举手/放下举手状态。
     *
     * @param confToken
     * @param conferenceId
     * @param handsState   0：放下手
     *                     1：举手
     */
    public void handsState(String confToken, String conferenceId, Integer handsState) {

        RestHandsUpReqBody body = new RestHandsUpReqBody().withHandsState(handsState);

        HandRequest request = new HandRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken)
                .withBody(body);
        try {
            userClient.hand(request);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 该接口用于批量设置来宾的举手/放下举手状态。
     *
     * @param confToken
     * @param conferenceId
     * @param handsState   0: 放下手
     *                     <p>
     *                     1: 举手
     * @param pids         与会者标识列表。
     */
    public void handBatch(String confToken, String conferenceId, Integer handsState, List<String> pids) {

        RestBatchHandsUpReqBody body = new RestBatchHandsUpReqBody().withHandsState(handsState).withPids(pids);

        BatchHandRequest request = new BatchHandRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken)
                .withBody(body);
        try {
            userClient.batchHand(request);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 该接口用于锁定或解锁会议。锁定会议后，不允许新的来宾主动加入会议。会议锁定后使用主持人密码/主持人链接加入会议或者主持人邀请来宾不受影响。
     *
     * @param confToken
     * @param conferenceId
     * @param islock       0：解锁
     *                     1：锁定
     */
    public void lock(String confToken, String conferenceId, Integer islock) {

        RestLockReqBody body = new RestLockReqBody().withIsLock(islock);

        LockMeetingRequest request = new LockMeetingRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken)
                .withBody(body);
        int httpStatusCode = 0;
        try {
            LockMeetingResponse lockMeetingResponse = userClient.lockMeeting(request);
            httpStatusCode = lockMeetingResponse.getHttpStatusCode();

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        httpcode(httpStatusCode);
    }

    /**
     * 该接口用于延长会议时间。默认会议自动延长。
     *
     * @param confToken
     * @param conferenceId
     * @param auto         0：手动延长 1：自动延长（未携带延长时间时，默认每次延长15分钟）
     * @param duration     延长时间，单位为分钟。默认值：15
     */
    public void prolongMeeting(String confToken, String conferenceId, Integer auto, Integer duration) {

        RestProlongDurReqBody body = new RestProlongDurReqBody().withAuto(auto).withDuration(duration);

        ProlongMeetingRequest request = new ProlongMeetingRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken)
                .withBody(body);
        try {
            userClient.prolongMeeting(request);
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new CustomException(e.getMessage());
        }
    }

    /**
     * 该接口用于邀请/取消邀请指定与会人共享桌面。
     *
     * @param confToken
     * @param conferenceId
     * @param share         邀请标志。0：取消邀请  1：邀请
     * @param participantID
     */
    public void share(String confToken, String conferenceId, Integer share, String participantID) {

        InviteShareDTO body = new InviteShareDTO().withShare(share);

        InviteShareRequest request = new InviteShareRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken)
                .withParticipantID(participantID)
                .withBody(body);
        try {
            userClient.inviteShare(request);
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new CustomException(e.getMessage());
        }
    }

    /**
     * 主持人邀请与会者开启/关闭摄像头
     *
     * @param confToken
     * @param conferenceId
     * @param status        开关摄像头标志。0：开启视频 1：关闭视频
     * @param participantID
     */
    public void video(String confToken, String conferenceId, Integer status, String participantID) {

        RestVideoBody body = new RestVideoBody().withStatus(status);

        InviteOperateVideoRequest request = new InviteOperateVideoRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken)
                .withParticipantID(participantID)
                .withBody(body);
        try {
            userClient.inviteOperateVideo(request);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 广播会场
     * 该接口用于广播指定的与会者。同一时间，只允许一个与会者被广播。
     *
     * @param confToken
     * @param conferenceId
     * @param participantID
     */
    public void broadcast(String confToken, String conferenceId, String participantID) {

        BroadcastParticipantRequest request = new BroadcastParticipantRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken)
                .withParticipantID(participantID);
        try {
            userClient.broadcastParticipant(request);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 该接口用于取消广播，包括：取消广播多画面、取消广播会场、取消点名会场。
     *
     * @param confToken
     * @param conferenceId
     */
    public void cancelBroadcast(String confToken, String conferenceId) {

        CancelBroadcastRequest request = new CancelBroadcastRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken);
        try {
            userClient.cancelBroadcast(request);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 主持人选看
     * <p>
     * 该接口用于主持人轮询、主持人选看多画面、主持人选看会场操作。只适用于专业会议终端（如TE系列等）为主持人的场景。
     *
     * @param confToken
     * @param conferenceId
     * @param viewType      主持人观看的画面类型。
     *                      0： 主持人轮询
     *                      1： 主持人观看多画面
     *                      2： 主持人选看会场
     * @param switchTime    轮询间隔，单位：秒。主持人轮询时，必填字段。
     *                      范围:[10-120]，默认值：10。
     * @param status        启动/停止轮询。主持人轮询时，必填字段。
     *                      0： 停止轮询
     *                      1： 启动轮询
     * @param participantID
     */
    public void chairView(String confToken, String conferenceId, Integer viewType, Integer switchTime, Integer status, String participantID) {

        RestChairViewReqBody body = new RestChairViewReqBody().withParticipantID(participantID).withStatus(status).withSwitchTime(switchTime).withViewType(viewType);

        SetHostViewRequest request = new SetHostViewRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken).withBody(body);
        try {
            userClient.setHostView(request);
        } catch (Exception e) {
            logger.info(e.getMessage());
            if(e.getMessage().contains("SVC_SITE_NOT_SUPPORT_VIEW")){
                throw new CustomException("目前的主席不支持选看(专业终端才支持)");
            }
            throw new CustomException(e.getMessage());

        }
    }

    /**
     * 设置自定义多画面
     *
     * @param confToken
     * @param conferenceId
     * @param switchTime
     * @param picLayoutInfo
     * @param manualSet
     * @param imageType
     * @param subscriberInPics
     * @param multiPicSaveOnly
     */
    public void setCustomMultiPicture(String confToken, String conferenceId, Integer switchTime, PicLayoutInfo picLayoutInfo, Integer manualSet, String imageType, List<RestSubscriberInPic> subscriberInPics, Boolean multiPicSaveOnly) {

        RestCustomMultiPictureBody body = new RestCustomMultiPictureBody()
                .withManualSet(manualSet)
                .withImageType(imageType)
                .withSwitchTime(switchTime)
                .withPicLayoutInfo(picLayoutInfo)
                .withSubscriberInPics(subscriberInPics)
                .withMultiPicSaveOnly(multiPicSaveOnly);

        SetCustomMultiPictureRequest request = new SetCustomMultiPictureRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken).withBody(body);
        try {
            userClient.setCustomMultiPicture(request);
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new CustomException(e.getMessage());
        }
    }

    public void setCustomMultiPicture2(MyMeetingClient myMeetingClient, String confToken, String conferenceId, Integer switchTime, PicLayoutInfo picLayoutInfo, Integer manualSet, String imageType, List<RestSubscriberInPic> subscriberInPics, Boolean multiPicSaveOnly, Integer skipEmptyPic, Boolean isChairViewMultiPic) {

        RestCustomMultiPictureBody2 body = new RestCustomMultiPictureBody2()
                .withManualSet(manualSet)
                .withImageType(imageType)
                .withSwitchTime(switchTime)
                .withPicLayoutInfo(picLayoutInfo)
                .withSubscriberInPics(subscriberInPics)
                .withskipEmptyPic(skipEmptyPic)
                .withIsChairViewMultiPic(isChairViewMultiPic)
                .withMultiPicSaveOnly(multiPicSaveOnly);


        SetCustomMultiPictureRequest2 request = new SetCustomMultiPictureRequest2().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken).withBody(body);
        try {
            myMeetingClient.setCustomMultiPicture2(request);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 切换会议显示策略
     * 该接口用于切换会中视频画面显示策略，包括广播多画面，广播单画面，声控多画面。
     *
     * @param confToken
     * @param conferenceId
     * @param imageType
     * @param switchMode
     */
    public void switchMode(String confToken, String conferenceId, Integer imageType, String switchMode) {

        RestSwitchModeReqBody body = new RestSwitchModeReqBody().withSwitchMode(switchMode).withImageType(imageType);
        SwitchModeRequest request = new SwitchModeRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken).withBody(body);
        try {
            userClient.switchMode(request);
        } catch (Exception e) {
            logger.info(e.getMessage());

            throw new CustomException(e.getMessage());
        }
    }

    /**
     * 启停会议录制
     *
     * @param confToken
     * @param conferenceId
     * @param isRecord     录制启停开关。默认值为0。
     *                     0：停止会议录制
     *                     1：启动会议录制
     */
    public void record(String confToken, String conferenceId, Integer isRecord) {

        RestSetRecordReqBody body = new RestSetRecordReqBody().withIsRecord(isRecord);
        RecordRequest request = new RecordRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken).withBody(body);
        try {
            userClient.record(request);
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new CustomException(e.getMessage());
        }
    }

    /**
     * 启停会议直播
     * 该接口用于启动或停止会议直播。
     *
     * @param confToken
     * @param conferenceId
     * @param isLive       会议直播开关。默认值为0。
     *                     0：停止会议直播
     *                     1：启动会议直播
     */
    public void live(String confToken, String conferenceId, Integer isLive) {

        RestSetLiveReqBody body = new RestSetLiveReqBody().withIsLive(isLive);
        LiveRequest request = new LiveRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken).withBody(body);
        try {
            userClient.live(request);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 重命名与会者
     *
     * @param confToken
     * @param conferenceId
     * @param participantID 与会者标识。已入会的必须填写该字段。
     * @param number        与会者号码。
     * @param newName       新名称
     */
    public void renameParticipant(String confToken, String conferenceId, String participantID, String number, String newName) {

        RestRenamePartReqBody body = new RestRenamePartReqBody()
                .withParticipantID(participantID)
                .withNumber(number)
                .withNewName(newName);

        RenameParticipantRequest request = new RenameParticipantRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken).withBody(body);
        try {
            userClient.renameParticipant(request);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 点名会场
     * 该接口用于点名指定与会者。点名会场的效果是除了主持人外，点名与会者为非静音状态，未点名的与会者统一为静音状态。同一时间，只允许一个与会者被点名。
     *
     * @param confToken
     * @param conferenceId
     * @param participantID
     */
    public void rollCall(String confToken, String conferenceId, String participantID) {

        RollcallParticipantRequest request = new RollcallParticipantRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken).withParticipantID(participantID);
        int httpStatusCode = 0;
        try {
            RollcallParticipantResponse rollcallParticipantResponse = userClient.rollcallParticipant(request);
            httpStatusCode = rollcallParticipantResponse.getHttpStatusCode();
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        httpcode(httpStatusCode);
    }

    /**
     * 与会者自己解除静音
     *
     * @param confToken
     * @param conferenceId
     * @param allowUnmuteByOneself 是否允许自己解除静音（仅静音时有效），默认为允许。
     *                             <p>
     *                             0： 不允许
     *                             1： 允许
     */
    public void guestUnMute(String confToken, String conferenceId, Integer allowUnmuteByOneself) {
        RestAllowUnMuteReqBody body = new RestAllowUnMuteReqBody().withAllowUnmuteByOneself(allowUnmuteByOneself);


        AllowGuestUnmuteRequest request = new AllowGuestUnmuteRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken).withBody(body);
        try {
            userClient.allowGuestUnmute(request);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 会场选看
     * 该接口用于专业会议终端（如TE系列等）选看其他与会者
     *
     * @param confToken
     * @param conferenceId
     * @param participantID  专业会议终端的与会者标识。
     * @param participantID2 被选看的与会者标识。
     */
    public void partView(String confToken, String conferenceId, String participantID, String participantID2) {

        RestParticipantViewReqBody body = new RestParticipantViewReqBody().withParticipantID(participantID2).withViewType(2);

        SetParticipantViewResponse setParticipantViewResponse = null;
        SetParticipantViewRequest request = new SetParticipantViewRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken).withBody(body).withParticipantID(participantID);
        try {
            setParticipantViewResponse = userClient.setParticipantView(request);
        } catch (Exception e) {
            logger.info(e.getMessage());
            if(Objects.equals("NORMAL_SITE_NOT_SUPPORT_VIEW",e.getMessage())){
                throw new CustomException("会场选看错误:专业会议终端（如TE系列等）才可选看其他与会者" );
            }
            throw new CustomException("会场选看错误" + e.getMessage());
        }
        int httpStatusCode = setParticipantViewResponse.getHttpStatusCode();
        httpcode(httpStatusCode);
    }

    public void partView2(MyMeetingClient myMeetingClient,String confToken, String conferenceId, String participantID, String participantID2) {

        RestParticipantViewReqBody body = new RestParticipantViewReqBody().withParticipantID(participantID2).withViewType(2);

        SetParticipantViewResponse setParticipantViewResponse = null;
        SetParticipantViewRequest request = new SetParticipantViewRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken).withBody(body).withParticipantID(participantID);
        try {
            setParticipantViewResponse = myMeetingClient.setParticipantView2(request);
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new CustomException("会场选看错误" + e.getMessage());
        }
        int httpStatusCode = setParticipantViewResponse.getHttpStatusCode();
        httpcode(httpStatusCode);
    }

    private void httpcode(int httpStatusCode) {
        if (httpStatusCode == 0) {
            throw new CustomException("请求错误");
        }
        if (httpStatusCode == HttpStatus.SC_BAD_REQUEST) {
            throw new CustomException("参数异常");
        }
        if (httpStatusCode == HttpStatus.SC_UNAUTHORIZED) {
            throw new CustomException("未鉴权或鉴权失败。");
        }
        if (httpStatusCode == HttpStatus.SC_FORBIDDEN) {
            throw new CustomException("权限受限");
        }
        if (httpStatusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            throw new CustomException("服务端异常");
        }
    }

    /**
     * 锁定会场视频源
     * 该接口用于锁定或者解锁某在线会场的视频源。只适用于专业会议终端（如TE系列等）。
     *
     * @param conferenceId
     * @param participantID  专业会议终端的与会者标识
     * @param participantID2 被锁定视频源的与会者标识。
     * @param status         锁定标志。
     *                       0：取消锁定。
     *                       1： 锁定。
     */
    public void lockView(String confToken, String conferenceId, String participantID, String participantID2, Integer status) {
        RestLockSiteViewReqBody body = new RestLockSiteViewReqBody().withParticipantID(participantID2).withStatus(status);


        LockViewRequest request = new LockViewRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken).withBody(body).withParticipantID(participantID);
        int httpStatusCode = 0;
        try {
            LockViewResponse lockViewResponse = userClient.lockView(request);
            httpStatusCode = lockViewResponse.getHttpStatusCode();
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        httpcode(httpStatusCode);

    }

    /**
     * 会议ID和密码邀请与会者
     * 该接口用于通过会议ID和密码邀请与会者。一般用于App已知会议ID和来宾密码，通过扫码等方式获取其他终端的SIP号码后，使用该接口将其他终端邀请加入会议中。
     *
     * @param conferenceId
     * @param callNum
     * @param orgID
     * @param confID
     * @param pwd
     * @param numBelongsType
     * @param isNotOverlayPidName
     */
    public void inviteWithPwd(String conferenceId, String callNum, String orgID, String confID, String pwd, Integer numBelongsType, Boolean isNotOverlayPidName) {
        RestInviteWithPwdReqBody body = new RestInviteWithPwdReqBody().withPwd(pwd)
                .withCallNum(callNum)
                .withConfID(confID)
                .withOrgID(orgID)
                .withNumBelongsType(numBelongsType)
                .withIsNotOverlayPidName(isNotOverlayPidName);


        InviteWithPwdRequest request = new InviteWithPwdRequest().withConferenceID(conferenceId)
                .withBody(body);
        try {
            userClient.inviteWithPwd(request);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }


    /**
     * 会中修改配置
     * 该接口用于修改会议配置，包括会议共享是否锁定，允许呼入范围
     *
     * @param confToken
     * @param conferenceId
     * @param lockSharing       锁定共享：
     *                          0：不锁定
     *                          1：锁定
     * @param callInRestriction 允许呼入的范围。
     *                          <p>
     *                          0：所有用户
     *                          2：企业内用户
     *                          3：被邀请用户
     */
    public void updateStartedConfConfig(String confToken, String conferenceId, Integer lockSharing, Integer callInRestriction) {
        UpdateStartedConfigReqBody body = new UpdateStartedConfigReqBody().withLockSharing(lockSharing)
                .withCallInRestriction(callInRestriction);


        UpdateStartedConfConfigRequest request = new UpdateStartedConfConfigRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken)
                .withBody(body);
        try {
            userClient.updateStartedConfConfig(request);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 该接口用于查询当前会议已保存的多画面布局。
     *
     * @param confToken
     * @param conferenceId
     * @return
     */
    public List<RestPicLayout> showLayout(String confToken, String conferenceId) {


        ShowLayoutResponse showLayoutResponse = null;
        ShowLayoutRequest request = new ShowLayoutRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken);
        try {
            showLayoutResponse = userClient.showLayout(request);
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new CustomException(e.getMessage());
        }
        if (showLayoutResponse != null && showLayoutResponse.getReturnCode() == 0) {
            return showLayoutResponse.getPicLayouts();
        }

        if (showLayoutResponse != null && showLayoutResponse.getReturnCode() != 0) {
            throw new CustomException(showLayoutResponse.getReturnDesc());
        }
        return null;
    }

    /**
     * 保存多画面布局
     * 该接口用于保存多画面布局。保存的多画面布局，只能在当前会议使用，会议结束后，保存的多画面布局就会释放。
     *
     * @param confToken
     * @param conferenceId
     * @param restPicLayout
     */
    public void saveLayout(String confToken, String conferenceId, RestPicLayout restPicLayout) {
        RestPicLayoutBody body = new RestPicLayoutBody().withRestPicLayout(restPicLayout);
        SaveLayoutRequest request = new SaveLayoutRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken).withBody(body);
        try {
            SaveLayoutResponse saveLayoutResponse = userClient.saveLayout(request);
            int httpStatusCode = saveLayoutResponse.getHttpStatusCode();
            if (httpStatusCode == HttpStatus.SC_BAD_REQUEST) {
                throw new CustomException("保存错误");
            }

        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new CustomException(e.getMessage());
        }

    }

    /**
     * 删除多画面布局
     * 该接口用于删除当前会议已保存的多画面布局。
     *
     * @param confToken
     * @param conferenceId
     * @param uuid
     */
    public void deleteLayout(String confToken, String conferenceId, String uuid) {
        DeleteLayoutRequest request = new DeleteLayoutRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken).withUuID(uuid);
        try {
            DeleteLayoutResponse deleteLayoutResponse = userClient.deleteLayout(request);
            int httpStatusCode = deleteLayoutResponse.getHttpStatusCode();
            if (httpStatusCode == HttpStatus.SC_BAD_REQUEST) {
                throw new CustomException("删除错误");
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new CustomException(e.getMessage());
        }

    }

    public void moveToWaitingRoom(String confToken, String conferenceId, String participantID) {

        RestMoveToWaitingRoomReqBody body=new RestMoveToWaitingRoomReqBody().withParticipantID(participantID);

        MoveToWaitingRoomRequest request = new MoveToWaitingRoomRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken)
                .withBody(body);
        int httpStatusCode=0;
        try {
            MoveToWaitingRoomResponse moveToWaitingRoomResponse = userClient.moveToWaitingRoom(request);
            httpStatusCode = moveToWaitingRoomResponse.getHttpStatusCode();
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new CustomException(e.getMessage());
        }
        httpcode(httpStatusCode);

    }

    public void allowWaitingParticipant(String confToken, String conferenceId, String participantID,Boolean allowAll) {

        RestAllowWaitingParticipantReqBody body=new RestAllowWaitingParticipantReqBody().withParticipantID(participantID).withAllowAll(allowAll);
        AllowWaitingParticipantRequest request = new AllowWaitingParticipantRequest().withConferenceID(conferenceId)
                .withXConferenceAuthorization(confToken)
                .withBody(body);
        int httpStatusCode=0;
        try {
            AllowWaitingParticipantResponse allowWaitingParticipantResponse = userClient.allowWaitingParticipant(request);
            httpStatusCode = allowWaitingParticipantResponse.getHttpStatusCode();
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new CustomException(e.getMessage());
        }
        httpcode(httpStatusCode);

    }





}
