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

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.AttendeeCallTheRollStatus;
import com.paradisecloud.fcm.common.enumer.AttendeeMixingStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.AttendeeImportance;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.AttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudBridge;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudMeetingBridge;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudWebSocketMessagePusher;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.SmcParitipantsStateRep;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * <pre>点名与会者操作</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-20 16:42
 */
public class CallTheRollAttendeeOperation extends AttendeeOperation {

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     *
     * @since 2021-02-22 12:48
     */
    private static final long serialVersionUID = 1L;
    private AttendeeHwcloud callTheRollAttendee;



    public CallTheRollAttendeeOperation(HwcloudConferenceContext conferenceContext, AttendeeHwcloud attendee) {
        super(conferenceContext);
        this.callTheRollAttendee = attendee;
    }

    @Override
    public void operate() {
        AttendeeHwcloud masterAttendee = conferenceContext.getMasterAttendee();
        if (masterAttendee == null) {
            throw new CustomException("没有主会场,无法选看");
        }

        if (this.callTheRollAttendee.getCallTheRollStatus() == AttendeeCallTheRollStatus.YES.getValue()) {
            return;
        }


        SmcParitipantsStateRep.ContentDTO hwcloudParticipant = callTheRollAttendee.getSmcParticipant();
        HwcloudMeetingBridge hwcloudMeetingBridge = conferenceContext.getHwcloudMeetingBridge();
        String token = hwcloudMeetingBridge.getTokenInfo().getToken();
        String confID = hwcloudMeetingBridge.getConfID();


        hwcloudMeetingBridge.getMeetingControl().rollCall(token,confID,hwcloudParticipant.getGeneralParam().getId());

        if (callTheRollAttendee != null) {
            AttendeeImportance.POINT.processAttendeeWebsocketMessage(callTheRollAttendee);
            callTheRollAttendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
            HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, callTheRollAttendee);
        }


    }




    @Override
    public void cancel() {

        HwcloudMeetingBridge hwcloudMeetingBridge = conferenceContext.getHwcloudMeetingBridge();
        String token = hwcloudMeetingBridge.getTokenInfo().getToken();
        String confID = hwcloudMeetingBridge.getConfID();

        if(callTheRollAttendee!=null){
            AttendeeImportance.COMMON.processAttendeeWebsocketMessage(callTheRollAttendee);
            callTheRollAttendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
            HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, callTheRollAttendee);
            //取消混音
            hwcloudMeetingBridge.getMeetingControl().muteParticipant(hwcloudMeetingBridge.getTokenInfo().getToken(), hwcloudMeetingBridge.getConfID(), callTheRollAttendee.getParticipantUuid(), 1);

        }
        //取消广播
        hwcloudMeetingBridge.getMeetingControl().cancelBroadcast(token,confID);


    }

}
