package com.paradisecloud.fcm.mcu.kdc.task;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryParticipantMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryParticipant;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.AttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IAttendeeForMcuKdcService;
import com.paradisecloud.fcm.service.interfaces.IBusiHistoryParticipantTerminalService;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class UpdateAttendeeMediaInfoTask extends DelayTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateAttendeeMediaInfoTask.class);

    private McuKdcConferenceContext conferenceContext;
    private AttendeeForMcuKdc attendee;
    private BusiHistoryParticipant busiHistoryParticipant;

    public UpdateAttendeeMediaInfoTask(String id, long delayInMilliseconds, McuKdcConferenceContext conferenceContext, AttendeeForMcuKdc attendee, BusiHistoryParticipant busiHistoryParticipant) {
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
        LOGGER.info("MCU_PLC更新参会者媒体开始。ID:" + getId());

        if (conferenceContext != null && attendee != null) {
            IAttendeeForMcuKdcService attendeeForMcuKdcService = BeanFactory.getBean(IAttendeeForMcuKdcService.class);
            IBusiHistoryParticipantTerminalService busiHistoryParticipantTerminalService = BeanFactory.getBean(IBusiHistoryParticipantTerminalService.class);
            BusiHistoryParticipantMapper busiHistoryParticipantMapper = BeanFactory.getBean(BusiHistoryParticipantMapper.class);

            JSONObject jsonObject = null;
            for (int i = 0; i < 3; i++) {
                try {
                    jsonObject = attendeeForMcuKdcService.detail(conferenceContext, attendee);
                    if (jsonObject != null) {
                        String codec = jsonObject.getJSONObject("upLink").getJSONObject("audio").getString("codec");
                        if (!"UnknownAlgorithm".equals(codec)) {
                            break;
                        } else {
                            jsonObject = null;
                        }
                    }
                } catch (Exception e) {
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
            if (jsonObject != null) {
                if (busiHistoryParticipant == null) {
                    busiHistoryParticipant = busiHistoryParticipantMapper.selectBusiHistoryParticipantByCallLegId(conferenceContext.getCoSpaceId() + attendee.getParticipantUuid());
                }
                if (busiHistoryParticipant != null) {
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
}
