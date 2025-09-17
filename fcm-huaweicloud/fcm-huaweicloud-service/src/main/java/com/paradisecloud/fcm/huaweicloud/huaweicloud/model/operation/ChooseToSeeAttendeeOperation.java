/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CallTheRollOperation.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.operation
 * @author lilinhai
 * @since 2021-02-20 16:42
 * @version  V1.0
 */
package com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation;

import com.huaweicloud.sdk.meeting.v1.model.PicInfoNotify;
import com.huaweicloud.sdk.meeting.v1.model.RestSubscriberInPic;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.AttendeeChooseSeeStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;

import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.AttendeeImportance;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.AttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudBridge;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudMeetingBridge;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudWebSocketMessagePusher;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.MultiPicDisplayVo;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.SmcParitipantsStateRep;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client.MyMeetingClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.*;



/**
 * <pre>选看与会者操作（主会场看选看者，分会场看主会场）</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-20 16:42
 */
public class ChooseToSeeAttendeeOperation extends AttendeeOperation {

    private final AttendeeHwcloud chooseSeeAttendee;
    private AttendeeHwcloud oldChooseSeeAttendee;

    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @param chooseSeeAttendee
     * @author lilinhai
     * @since 2021-02-22 13:47
     */
    public ChooseToSeeAttendeeOperation(HwcloudConferenceContext conferenceContext, AttendeeHwcloud chooseSeeAttendee) {
        super(conferenceContext);
        this.chooseSeeAttendee = chooseSeeAttendee;
    }

    /**
     * 操作方法
     *
     * @author lilinhai
     * @since 2021-02-20 16:39  void
     */
    @Override
    public void operate() {
        operateScreen();
    }

    private void operateScreen() {

        if (chooseSeeAttendee.getChooseSeeStatus() == AttendeeChooseSeeStatus.YES.getValue()) {
            return;
        }

        AttendeeOperation lastAttendeeOperation = conferenceContext.getLastAttendeeOperation();

        if (lastAttendeeOperation instanceof ChangeMasterAttendeeOperation) {
            ChangeMasterAttendeeOperation old = (ChangeMasterAttendeeOperation) lastAttendeeOperation;
            oldChooseSeeAttendee = old.getDefaultChooseSeeAttendee();
        }

        if (lastAttendeeOperation instanceof ChooseToSeeAttendeeOperation) {
            ChooseToSeeAttendeeOperation old = (ChooseToSeeAttendeeOperation) lastAttendeeOperation;
            oldChooseSeeAttendee = old.chooseSeeAttendee;

        }


        AttendeeHwcloud masterAttendee = conferenceContext.getMasterAttendee();

        if (masterAttendee == null) {
            throw new CustomException("没有主会场,无法选看");
        }


        if (chooseSeeAttendee.isMeetingJoined()) {

            SmcParitipantsStateRep.ContentDTO masterParticipant = masterAttendee.getSmcParticipant();
            if (masterParticipant == null) {
                return;
            }
            SmcParitipantsStateRep.ContentDTO smcParticipantChoose = chooseSeeAttendee.getSmcParticipant();
            if (smcParticipantChoose == null) {
                return;
            }

            HwcloudMeetingBridge hwcloudMeetingBridge = conferenceContext.getHwcloudMeetingBridge();
            String token = hwcloudMeetingBridge.getTokenInfo().getToken();
            String confID = hwcloudMeetingBridge.getConfID();

           // hwcloudMeetingBridge.getMeetingControl().partView(token,confID, masterAttendee.getParticipantUuid() ,chooseSeeAttendee.getParticipantUuid());
           // hwcloudMeetingBridge.getMeetingControl().chairView(hwcloudMeetingBridge.getTokenInfo().getToken(),hwcloudMeetingBridge.getConfID(),2,null,null,chooseSeeAttendee.getParticipantUuid());
            //新建多画面为一分屏
            List<RestSubscriberInPic> subscriberInPics = new ArrayList<>();
            RestSubscriberInPic restSubscriberInPic = new RestSubscriberInPic();
            restSubscriberInPic.setIndex(1);
            restSubscriberInPic.setIsAssistStream(0);
            List<String> ids=new ArrayList<>();
            ids.add(chooseSeeAttendee.getNumber());
            restSubscriberInPic.setSubscriber(ids);
            subscriberInPics.add(restSubscriberInPic);

            hwcloudMeetingBridge.getMeetingControl().setCustomMultiPicture(token,confID,10,null,1,"Single",subscriberInPics,true);
            hwcloudMeetingBridge.getMeetingControl().chairView(hwcloudMeetingBridge.getTokenInfo().getToken(),hwcloudMeetingBridge.getConfID(),1,null,null,null);

            chooseSeeAttendee.setChooseSeeStatus(AttendeeChooseSeeStatus.YES.getValue());

            if (isUpCascadeRollCall()) {
                AttendeeImportance.CHOOSE_SEE.processAttendeeWebsocketMessage(chooseSeeAttendee);
            } else if (isUpCascadePolling()) {
                if (isUpCascadeBroadcast()) {
                    AttendeeImportance.ROUND_BROADCAST.processAttendeeWebsocketMessage(chooseSeeAttendee);
                } else {
                    AttendeeImportance.ROUND.processAttendeeWebsocketMessage(chooseSeeAttendee);
                }
            } else {
                AttendeeImportance.CHOOSE_SEE.processAttendeeWebsocketMessage(chooseSeeAttendee);
            }
            if (oldChooseSeeAttendee != null&&!Objects.equals(oldChooseSeeAttendee.getId(),chooseSeeAttendee.getId())) {

                AttendeeImportance.COMMON.processAttendeeWebsocketMessage(oldChooseSeeAttendee);
                HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, oldChooseSeeAttendee);

            }
            HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, chooseSeeAttendee);
        }


    }



    @Override
    public void cancel() {
        if(chooseSeeAttendee!=null){
            AttendeeImportance.COMMON.processAttendeeWebsocketMessage(chooseSeeAttendee);
            HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, chooseSeeAttendee);
        }
    }
}