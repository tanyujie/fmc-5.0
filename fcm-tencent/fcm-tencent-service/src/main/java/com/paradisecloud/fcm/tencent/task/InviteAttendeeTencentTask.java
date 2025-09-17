package com.paradisecloud.fcm.tencent.task;


import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;

import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.InvitedAttendee;
import com.paradisecloud.fcm.tencent.busi.attende.AttendeeTencent;
import com.paradisecloud.fcm.tencent.busi.attende.RoomAttendeeTencent;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContext;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContextCache;
import com.paradisecloud.fcm.tencent.cache.TencentWebSocketMessagePusher;
import com.paradisecloud.fcm.tencent.model.client.TencentConferenceCtrlClient;
import com.paradisecloud.fcm.tencent.model.reponse.RoomCallReponse;
import com.paradisecloud.fcm.tencent.model.request.RoomCallRequest;
import com.tencentcloudapi.wemeet.common.exception.WemeetSdkException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class InviteAttendeeTencentTask extends TencentDelayTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(InviteAttendeeTencentTask.class);

    private final TencentConferenceContext conferenceContext;
    private final List<AttendeeTencent> attendees;

    public InviteAttendeeTencentTask(String id, long delayInMilliseconds, TencentConferenceContext conferenceContext, AttendeeTencent attendee) {
        super(id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.attendees = new ArrayList<>();
        this.attendees.add(attendee);
    }

    public InviteAttendeeTencentTask(String id, long delayInMilliseconds, TencentConferenceContext conferenceContext, List<AttendeeTencent> attendees) {
        super(id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.attendees = attendees;
    }

    @Override
    public void run() {
        LOGGER.info("Tencent===========================================================终端邀请开始ID:" + getId());
        try {
            if (conferenceContext == null || conferenceContext.isEnd()) {
                return;
            }
            if(CollectionUtils.isEmpty(attendees)){
                return;
            }
            String tencentconferenceId = conferenceContext.getMeetingId();
            if (Strings.isBlank(tencentconferenceId)) {
                return;
            }
            if (CollectionUtils.isEmpty(attendees)) {
                return;
            }
            List<String> participants = new ArrayList<>();
            for (AttendeeTencent attendee : attendees) {

                participants.add(attendee.getId());


            }
            if (CollectionUtils.isEmpty(participants)) {
                return;
            }
            try {

                TencentConferenceCtrlClient conferenceCtrlClient = conferenceContext.getTencentBridge().getConferenceCtrlClient();
                for (String id : participants) {
                    RoomCallRequest request = new RoomCallRequest();
                    request.setMeetingRoomId(id);
                    request.setOperatorIdType(1);
                    request.setOperatorId(conferenceContext.getTencentUser());
                    request.setMeetingId(conferenceContext.getMeetingId());
                    RoomCallReponse roomCallReponse = conferenceCtrlClient.roomsInvite(request);
                }

            } catch (Exception e) {
                LOGGER.error("呼叫失败：" + e.getMessage());
                StringBuilder messageTip = new StringBuilder();
                if (attendees.size() == 1) {
                    messageTip.append("【").append(attendees.get(0).getName()).append("】呼叫失败：").append(e.getMessage());
                } else {
                    messageTip.append("【").append("】呼叫失败：").append(e.getMessage());
                }
                TencentWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip);
            }
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());
        }

    }
}
