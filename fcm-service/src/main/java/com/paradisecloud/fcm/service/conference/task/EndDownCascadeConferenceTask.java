package com.paradisecloud.fcm.service.conference.task;

import com.paradisecloud.fcm.common.enumer.ConferenceEndType;
import com.paradisecloud.fcm.service.conference.cascade.ConferenceCascadeHandler;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.sinhy.spring.BeanFactory;

import java.util.List;

public class EndDownCascadeConferenceTask extends Task {

    private BaseConferenceContext upCascadeConferenceContext;
    private int endType = ConferenceEndType.CASCADE.getValue();

    public EndDownCascadeConferenceTask(String id, long delayInMilliseconds, BaseConferenceContext upCascadeConferenceContext) {
        super("start_down_c_" + id, delayInMilliseconds);
        this.upCascadeConferenceContext = upCascadeConferenceContext;
    }

    public EndDownCascadeConferenceTask(String id, long delayInMilliseconds, BaseConferenceContext upCascadeConferenceContext, int endType) {
        super("start_down_c_" + id, delayInMilliseconds);
        this.upCascadeConferenceContext = upCascadeConferenceContext;
        this.endType = endType;
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
        if (upCascadeConferenceContext != null) {
            BusiHistoryConference historyConference = upCascadeConferenceContext.getHistoryConference();
            historyConference.setMcuType(upCascadeConferenceContext.getMcuType());
            BusiHistoryConferenceMapper bean = BeanFactory.getBean(BusiHistoryConferenceMapper.class);
            bean.updateBusiHistoryConference(historyConference);
            List<BaseAttendee> mcuAttendees = upCascadeConferenceContext.getMcuAttendees();
            for (BaseAttendee baseAttendee : mcuAttendees) {
                if (baseAttendee.isMcuAttendee()) {
                    BaseConferenceContext baseConferenceContext = ConferenceCascadeHandler.endConference(baseAttendee.getId(), endType);
                    if (baseConferenceContext != null) {
                        baseConferenceContext.setUpCascadeRemoteParty(null);
                        // 更新历史会议
                        BusiHistoryConference busiHistoryConference = baseConferenceContext.getHistoryConference();
                        // 更新历史会议
                        if (busiHistoryConference != null) {
                            busiHistoryConference.setMcuType(baseConferenceContext.getMcuType());
                            busiHistoryConference.setUpCascadeId(historyConference.getId());
                            bean.updateBusiHistoryConference(busiHistoryConference);
                        }
                    }
                }
            }
        }
    }
}
