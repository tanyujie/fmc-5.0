package com.paradisecloud.fcm.huaweicloud.huaweicloud.task;

import com.huaweicloud.sdk.meeting.v1.model.Attendee;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.AttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.CropDirAttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudMeetingBridge;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudWebSocketMessagePusher;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class InviteAttendeeHwcloudTask extends HwcloudDelayTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(InviteAttendeeHwcloudTask.class);

    private final HwcloudConferenceContext conferenceContext;
    private final List<AttendeeHwcloud> attendees;

    public InviteAttendeeHwcloudTask(String id, long delayInMilliseconds, HwcloudConferenceContext conferenceContext, AttendeeHwcloud attendee) {
        super(id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.attendees = new ArrayList<>();
        this.attendees.add(attendee);
    }

    public InviteAttendeeHwcloudTask(String id, long delayInMilliseconds, HwcloudConferenceContext conferenceContext, List<AttendeeHwcloud> attendees) {
        super(id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.attendees = attendees;
    }

    @Override
    public void run() {
        LOGGER.info("Hwcloud=====================================================================================================================终端邀请开始ID:" + getId());
        try {
            if (conferenceContext == null || conferenceContext.isEnd()) {
                return;
            }
            String HwcloudconferenceId = conferenceContext.getMeetingId();
            if (Strings.isBlank(HwcloudconferenceId)) {
                return;
            }
            if (CollectionUtils.isEmpty(attendees)) {
                return;
            }
            List<Attendee> participants = new ArrayList<>();
            for (AttendeeHwcloud attendee : attendees) {

                if (attendee instanceof CropDirAttendeeHwcloud) {
                    CropDirAttendeeHwcloud cropDirAttendeeHwcloud = (CropDirAttendeeHwcloud) attendee;
                    Attendee attendeeHw = new Attendee();
                    attendeeHw.setName(cropDirAttendeeHwcloud.getName());
                    attendeeHw.setPhone(cropDirAttendeeHwcloud.getPhone());
                    attendeeHw.setType("normal");
                    attendeeHw.setRole(cropDirAttendeeHwcloud.getRole());
                    participants.add(attendeeHw);
                }


            }
            if (CollectionUtils.isEmpty(participants)) {
                return;
            }
            try {
                HwcloudMeetingBridge hwcloudMeetingBridge = conferenceContext.getHwcloudMeetingBridge();
                try {
                    hwcloudMeetingBridge.getMeetingControl().inviteParticipants(hwcloudMeetingBridge.getTokenInfo().getToken(), conferenceContext.getMeetingId(), participants);
                } catch (Exception e) {
                    LOGGER.error("呼叫失败：呼叫失败");
                    HwcloudWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, "呼叫失败：MCU资源不足呼叫失败");
                }
            } catch (Exception e) {
                LOGGER.error("呼叫失败：" + e.getMessage());
                StringBuilder messageTip = new StringBuilder();
                if (attendees.size() == 1) {
                    messageTip.append("【").append(attendees.get(0).getName()).append("】呼叫失败：").append(e.getMessage());
                } else {
                    messageTip.append("【").append("】呼叫失败：").append(e.getMessage());
                }
                HwcloudWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip);
            }
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());
        }

    }
}
