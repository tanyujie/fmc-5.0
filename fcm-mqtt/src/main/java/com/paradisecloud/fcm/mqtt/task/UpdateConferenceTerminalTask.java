package com.paradisecloud.fcm.mqtt.task;

import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;

import java.util.Collection;

public class UpdateConferenceTerminalTask extends Task {

    private long terminalId;

    public UpdateConferenceTerminalTask(String id, long delayInMilliseconds, long terminalId) {
        super("update_c_t_" + id, delayInMilliseconds);
        this.terminalId = terminalId;
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
        BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
        if (busiTerminal != null) {
            Collection<BaseConferenceContext> conferenceContexts = AllConferenceContextCache.getInstance().values();
            for (BaseConferenceContext conferenceContext : conferenceContexts) {
                BaseAttendee attendee = conferenceContext.getAttendeeByTerminalId(terminalId);
                attendee.setRemoteParty(TerminalCache.getInstance().getRemoteParty(busiTerminal));
                attendee.setIp(busiTerminal.getIp());
            }
        }
    }
}
