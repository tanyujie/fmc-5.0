package com.paradisecloud.fcm.service.conference.task;

import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.cascade.ConferenceCascadeHandler;
import org.apache.logging.log4j.util.Strings;

import java.util.Objects;

public class InviteAttendeeTask extends Task {

    private final BaseConferenceContext conferenceContext;
    private BaseAttendee attendee;
    private String tencentRemoteParty;
    private String conferenceName;

    public InviteAttendeeTask(String id, long delayInMilliseconds, BaseConferenceContext conferenceContext, BaseAttendee attendee) {
        super("invite_a_" + id, delayInMilliseconds);
        this.attendee = attendee;
        this.conferenceContext = conferenceContext;
    }

    public InviteAttendeeTask(String id, long delayInMilliseconds, BaseConferenceContext conferenceContext, String conferenceName, String tencentRemoteParty) {
        super("invite_a_" + id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.tencentRemoteParty = tencentRemoteParty;
        this.conferenceName = conferenceName;
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        if (conferenceContext != null && attendee != null) {

            String contextKey = EncryptIdUtil.parasToContextKey(attendee.getId());
            BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);

            if (baseConferenceContext != null) {
                String remoteParty = baseConferenceContext.getTenantId() + baseConferenceContext.getConferenceNumber() + "@" + baseConferenceContext.getMcuCallIp();
                if (baseConferenceContext.getMcuCallPort() != null && baseConferenceContext.getMcuCallPort() != 5060) {
                    remoteParty += ":" + baseConferenceContext.getMcuCallPort();
                }
                String oldRemoteParty = attendee.getRemoteParty();
                attendee.setRemoteParty(remoteParty);
                attendee.setIp(baseConferenceContext.getMcuCallIp());
                conferenceContext.updateAttendeeToRemotePartyMap(oldRemoteParty, attendee);
                attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
            } else {
                attendee.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
            }
            conferenceContext.updateAttendeeToRemotePartyMap(null, attendee);
            if (!attendee.isMeetingJoined()) {
                if (Objects.equals(McuType.MCU_TENCENT.getCode(), conferenceContext.getMcuType())) {
                    if (Strings.isNotBlank(conferenceContext.getConferencePassword())) {
                        ConferenceCascadeHandler.invite(baseConferenceContext.getId(), conferenceContext.getName(), conferenceContext.getConferenceNumber() + "@" + conferenceContext.getMcuCallIp(), conferenceContext.getConferencePassword() + "#");
                    } else {
                        ConferenceCascadeHandler.invite(baseConferenceContext.getId(), conferenceContext.getName(), conferenceContext.getConferenceNumber() + "@" + conferenceContext.getMcuCallIp(), conferenceContext.getConferencePassword());
                    }
                    return;
                }
                ConferenceCascadeHandler.recall(conferenceContext.getId(), attendee.getId());
            }
        }
    }
}
