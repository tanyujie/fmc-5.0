package com.paradisecloud.fcm.service.conference.task;

import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.service.conference.cascade.ConferenceCascadeHandler;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.sinhy.spring.BeanFactory;

import java.util.List;

public class StartDownCascadeConferenceTask extends Task {

    private BaseConferenceContext upCascadeConferenceContext;

    public StartDownCascadeConferenceTask(String id, long delayInMilliseconds, BaseConferenceContext upCascadeConferenceContext) {
        super("start_down_c_" + id, delayInMilliseconds);
        this.upCascadeConferenceContext = upCascadeConferenceContext;
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
            String conferenceRemoteParty = upCascadeConferenceContext.getTenantId() + upCascadeConferenceContext.getConferenceNumber() + "@" + upCascadeConferenceContext.getMcuCallIp();
            if (upCascadeConferenceContext.getMcuCallPort() != null && upCascadeConferenceContext.getMcuCallPort() != 5060) {
                conferenceRemoteParty += ":" + upCascadeConferenceContext.getMcuCallPort();
            }
            upCascadeConferenceContext.setConferenceRemoteParty(conferenceRemoteParty);
            BusiHistoryConference upCascadeHistoryConference = upCascadeConferenceContext.getHistoryConference();
            upCascadeHistoryConference.setMcuType(upCascadeConferenceContext.getMcuType());
            BusiHistoryConferenceMapper bean = BeanFactory.getBean(BusiHistoryConferenceMapper.class);
            bean.updateBusiHistoryConference(upCascadeHistoryConference);
            int extendDelayInMilliseconds = 0;
            List<BaseAttendee> mcuAttendees = upCascadeConferenceContext.getMcuAttendees();
            for (BaseAttendee baseAttendee : mcuAttendees) {
                if (baseAttendee.isMcuAttendee()) {
                    BaseConferenceContext baseConferenceContext = ConferenceCascadeHandler.startConference(baseAttendee.getId());
                    if (baseConferenceContext != null) {
                        baseConferenceContext.setUpCascadeRemoteParty(upCascadeConferenceContext.getConferenceRemoteParty());
                        BusiHistoryConference historyConference = baseConferenceContext.getHistoryConference();
                        // 更新历史会议
                        if (historyConference != null&&upCascadeHistoryConference!=null) {
                            historyConference.setMcuType(baseConferenceContext.getMcuType());
                            historyConference.setUpCascadeId(upCascadeHistoryConference.getId());
                            bean.updateBusiHistoryConference(historyConference);
                        }
                        if (!baseAttendee.isMeetingJoined()) {
                            long delayInMilliseconds = 5000;
                            if (McuType.MCU_ZJ.getCode().equals(upCascadeConferenceContext.getMcuType()) && McuType.MCU_ZJ.getCode().equals(baseConferenceContext.getMcuType())) {
                                delayInMilliseconds += extendDelayInMilliseconds;
                                extendDelayInMilliseconds += 2000;
                            }
                            if (McuType.FME.getCode().equals(upCascadeConferenceContext.getMcuType()) && McuType.FME.getCode().equals(baseConferenceContext.getMcuType())) {
                                delayInMilliseconds = 1000;
                            }
                            if (McuType.SMC3.getCode().equals(upCascadeConferenceContext.getMcuType())||McuType.SMC2.getCode().equals(upCascadeConferenceContext.getMcuType())) {
                                delayInMilliseconds = 3000;
                            }
                            if (McuType.MCU_TENCENT.getCode().equals(upCascadeConferenceContext.getMcuType())) {
                                delayInMilliseconds = 3000;
                            }
                            if (McuType.MCU_ZTE.getCode().equals(upCascadeConferenceContext.getMcuType())) {
                                delayInMilliseconds = 2000;
                            }
                            InviteAttendeeTask inviteAttendeeTask = new InviteAttendeeTask(upCascadeHistoryConference.getId() + "_" + baseAttendee.getId(), delayInMilliseconds, upCascadeConferenceContext, baseAttendee);
                            BeanFactory.getBean(TaskService.class).addTask(inviteAttendeeTask);

                        }
                    }
                }
            }
        }
    }
}
