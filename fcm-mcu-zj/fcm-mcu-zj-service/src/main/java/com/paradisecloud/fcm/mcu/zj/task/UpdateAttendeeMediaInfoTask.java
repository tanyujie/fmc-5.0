package com.paradisecloud.fcm.mcu.zj.task;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryParticipantMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryParticipant;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IAttendeeForMcuZjService;
import com.paradisecloud.fcm.service.interfaces.IBusiHistoryParticipantTerminalService;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class UpdateAttendeeMediaInfoTask extends DelayTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateAttendeeMediaInfoTask.class);

    private McuZjConferenceContext conferenceContext;
    private AttendeeForMcuZj attendee;
    private BusiHistoryParticipant busiHistoryParticipant;

    public UpdateAttendeeMediaInfoTask(String id, long delayInMilliseconds, McuZjConferenceContext conferenceContext, AttendeeForMcuZj attendee, BusiHistoryParticipant busiHistoryParticipant) {
        super("update_m_" + id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.attendee = attendee;
        this.busiHistoryParticipant = busiHistoryParticipant;
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
        LOGGER.info("MCU_ZJ更新参会者媒体开始。ID:" + getId());

        if (conferenceContext != null && attendee != null) {
            IAttendeeForMcuZjService attendeeForMcuZjService = BeanFactory.getBean(IAttendeeForMcuZjService.class);
            IBusiHistoryParticipantTerminalService busiHistoryParticipantTerminalService = BeanFactory.getBean(IBusiHistoryParticipantTerminalService.class);
            BusiHistoryParticipantMapper busiHistoryParticipantMapper = BeanFactory.getBean(BusiHistoryParticipantMapper.class);

            JSONObject jsonObject = attendeeForMcuZjService.detail(conferenceContext, attendee);
            if (jsonObject != null) {
                BusiHistoryParticipant busiHistoryParticipantUpdate = new BusiHistoryParticipant();
                busiHistoryParticipantUpdate.setId(busiHistoryParticipant.getId());
                busiHistoryParticipantUpdate.setMediaInfo(jsonObject);
                busiHistoryParticipantUpdate.setUpdateTime(new Date());
                busiHistoryParticipantMapper.updateBusiHistoryParticipantForJoin(busiHistoryParticipantUpdate);
                busiHistoryParticipant.setMediaInfo(jsonObject);
                busiHistoryParticipant.setUpdateTime(busiHistoryParticipantUpdate.getUpdateTime());
                try {
                    busiHistoryParticipantTerminalService.updateBusiHistoryParticipantTerminalByBusiHistoryParticipant(busiHistoryParticipant);
                } catch (Exception e) {
                }
            }
        }
    }
}
