package com.paradisecloud.fcm.zte.task;


import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DeleteAttendeesTask extends DelayTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteAttendeesTask.class);

    private McuZteConferenceContext conferenceContext;
    private String uuid;


    public DeleteAttendeesTask(String id, long delayInMilliseconds, McuZteConferenceContext conferenceContext, String uuid) {
        super("invite_1_" + id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.uuid = uuid;
    }


    @Override
    public void run() {
        LOGGER.info("MCU_ZTE终端删除开始。ID:" + getId());
        this.conferenceContext.removeDeletedParticipant(uuid);
    }
}
