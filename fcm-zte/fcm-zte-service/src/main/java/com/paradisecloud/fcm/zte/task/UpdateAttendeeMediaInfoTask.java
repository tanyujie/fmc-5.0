package com.paradisecloud.fcm.zte.task;

import com.paradisecloud.fcm.dao.model.BusiHistoryParticipant;


import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.model.busi.attendee.AttendeeForMcuZte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UpdateAttendeeMediaInfoTask extends DelayTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateAttendeeMediaInfoTask.class);

    private McuZteConferenceContext conferenceContext;
    private AttendeeForMcuZte attendee;
    private BusiHistoryParticipant busiHistoryParticipant;

    public UpdateAttendeeMediaInfoTask(String id, long delayInMilliseconds, McuZteConferenceContext conferenceContext, AttendeeForMcuZte attendee, BusiHistoryParticipant busiHistoryParticipant) {
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
        LOGGER.info("MCU_Zte更新参会者媒体开始。ID:" + getId());
//
//        if (conferenceContext != null && attendee != null) {
//            IAttendeeForMcuZteService attendeeForMcuZteService = BeanFactory.getBean(IAttendeeForMcuZteService.class);
//            IBusiHistoryParticipantTerminalService busiHistoryParticipantTerminalService = BeanFactory.getBean(IBusiHistoryParticipantTerminalService.class);
//            BusiHistoryParticipantMapper busiHistoryParticipantMapper = BeanFactory.getBean(BusiHistoryParticipantMapper.class);
//
//            JSONObject jsonObject = null;
//            for (int i = 0; i < 3; i++) {
//                try {
//                    jsonObject = attendeeForMcuZteService.detail(conferenceContext, attendee);
//                    if (jsonObject != null) {
//                        String codec = jsonObject.getJSONObject("upLink").getJSONObject("audio").getString("codec");
//                        if (!"UnknownAlgorithm".equals(codec)) {
//                            break;
//                        } else {
//                            jsonObject = null;
//                        }
//                    }
//                } catch (Exception e) {
//                }
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                }
//            }
//            if (jsonObject != null) {
//                if (busiHistoryParticipant == null) {
//                    busiHistoryParticipant = busiHistoryParticipantMapper.selectBusiHistoryParticipantByCallLegId(conferenceContext.getCoSpaceId() + attendee.getParticipantUuid());
//                }
//                if (busiHistoryParticipant != null) {
//                    BusiHistoryParticipant busiHistoryParticipantUpdate = new BusiHistoryParticipant();
//                    busiHistoryParticipantUpdate.setId(busiHistoryParticipant.getId());
//                    busiHistoryParticipantUpdate.setMediaInfo(jsonObject);
//                    busiHistoryParticipantUpdate.setUpdateTime(new Date());
//                    busiHistoryParticipantMapper.updateBusiHistoryParticipantForJoin(busiHistoryParticipantUpdate);
//                    busiHistoryParticipant.setMediaInfo(jsonObject);
//                    busiHistoryParticipant.setUpdateTime(busiHistoryParticipantUpdate.getUpdateTime());
//                    try {
//                        busiHistoryParticipantTerminalService.updateBusiHistoryParticipantTerminalByBusiHistoryParticipant(busiHistoryParticipant);
//                    } catch (Exception e) {
//                    }
//                }
//            }
//        }
    }
}
