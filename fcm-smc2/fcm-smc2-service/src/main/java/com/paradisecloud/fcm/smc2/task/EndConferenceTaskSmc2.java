package com.paradisecloud.fcm.smc2.task;

import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryParticipantMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.BusiHistoryParticipant;
import com.paradisecloud.fcm.smc2.service.IBusiSmc2HistoryConferenceService;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EndConferenceTaskSmc2 extends Smc2DelayTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndConferenceTaskSmc2.class);

    private Long conferenceId;

    public EndConferenceTaskSmc2(String id, long delayInMilliseconds, Long conferenceId) {
        super(id, delayInMilliseconds);
        this.conferenceId = conferenceId;
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
        LOGGER.info("SMC2会议结束,处理开始ID:" + getId());

        if (conferenceId != null) {
            BusiHistoryConferenceMapper busiHistoryConferenceMapper = BeanFactory.getBean(BusiHistoryConferenceMapper.class);
            BusiHistoryParticipantMapper busiHistoryParticipantMapper = BeanFactory.getBean(BusiHistoryParticipantMapper.class);
            IBusiSmc2HistoryConferenceService busiSmc2HistoryConferenceService = BeanFactory.getBean(IBusiSmc2HistoryConferenceService.class);
            BusiHistoryConference busiHistoryConference = busiHistoryConferenceMapper.selectBusiHistoryConferenceById(conferenceId);
            if (busiHistoryConference != null && busiHistoryConference.getConferenceEndTime() != null) {
                BusiHistoryParticipant busiHistoryParticipantCon = new BusiHistoryParticipant();
                busiHistoryParticipantCon.setHistoryConferenceId(conferenceId);
                List<BusiHistoryParticipant> busiHistoryParticipantList = busiHistoryParticipantMapper.selectBusiHistoryParticipantList(busiHistoryParticipantCon);
                for (BusiHistoryParticipant busiHistoryParticipant : busiHistoryParticipantList) {
                    if (busiHistoryParticipant.getOutgoingTime() == null || busiHistoryParticipant.getDurationSeconds() == null || busiHistoryParticipant.getDurationSeconds().intValue() == 0) {
                        busiSmc2HistoryConferenceService.updateBusiHistoryParticipant(busiHistoryParticipant, busiHistoryConference);
                    }
                }
            }
        }
    }
}
