package com.paradisecloud.fcm.mcu.plc.monitor.ct;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.AttendType;
import com.paradisecloud.fcm.common.enumer.ConferenceEndType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryParticipantMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuPlcConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuPlcTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mcu.plc.attendee.utils.McuPlcConferenceContextUtils;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcConferenceContextCache;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.AttendeeForMcuPlc;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.InvitedAttendeeForMcuPlc;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.TerminalAttendeeForMcuPlc;
import com.paradisecloud.fcm.mcu.plc.monitor.cache.MonitorThreadCache;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcConferenceService;
import com.paradisecloud.fcm.mcu.plc.task.InviteAttendeesTask;
import com.paradisecloud.fcm.mcu.plc.task.McuPlcDelayTaskService;
import com.paradisecloud.fcm.service.interfaces.IBusiHistoryParticipantTerminalService;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ConferenceMonitorThread extends Thread {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void run() {

        while (true) {
            if (isInterrupted()) {
                return;
            }

            try {
                long currentTimeMillis = System.currentTimeMillis();
                MonitorThreadCache.getInstance().setConferenceMonitorTime(currentTimeMillis);

                BusiMcuPlcConferenceAppointmentMapper busiMcuPlcConferenceAppointmentMapper = BeanFactory.getBean(BusiMcuPlcConferenceAppointmentMapper.class);
                BusiMcuPlcTemplateConferenceMapper busiMcuPlcTemplateConferenceMapper = BeanFactory.getBean(BusiMcuPlcTemplateConferenceMapper.class);
                BusiHistoryConferenceMapper busiHistoryConferenceMapper = BeanFactory.getBean(BusiHistoryConferenceMapper.class);
                BusiHistoryParticipantMapper busiHistoryParticipantMapper = BeanFactory.getBean(BusiHistoryParticipantMapper.class);
                IBusiMcuPlcConferenceService busiMcuPlcConferenceService = BeanFactory.getBean(IBusiMcuPlcConferenceService.class);
                List<BusiHistoryConference> notEndConferences = busiHistoryConferenceMapper.selectNotEndHistoryConferenceList(McuType.MCU_PLC.getCode());
                if (!ObjectUtils.isEmpty(notEndConferences)) {
                    for (BusiHistoryConference busiHistoryConference : notEndConferences) {
                        if (System.currentTimeMillis() - busiHistoryConference.getConferenceStartTime().getTime() < 10000) {
                            continue;
                        }
                        BusiMcuPlcTemplateConference busiMcuPlcTemplateConferenceCon = new BusiMcuPlcTemplateConference();
                        busiMcuPlcTemplateConferenceCon.setLastConferenceId(busiHistoryConference.getId());
                        List<BusiMcuPlcTemplateConference> busiMcuPlcTemplateConferenceList = busiMcuPlcTemplateConferenceMapper.selectAllBusiMcuPlcTemplateConferenceList(busiMcuPlcTemplateConferenceCon);
                        if (busiMcuPlcTemplateConferenceList == null || busiMcuPlcTemplateConferenceList.size() == 0) {
                            Date currentDate = new Date();
                            busiHistoryConference.setConferenceEndTime(currentDate);
                            busiHistoryConference.setUpdateTime(currentDate);
                            if (busiHistoryConference.getConferenceStartTime() == null) {
                                busiHistoryConference.setConferenceStartTime(new Date(System.currentTimeMillis() - new Random().nextInt(18) * 1000 * 60));
                            }
                            busiHistoryConference.setDuration((int) ((currentDate.getTime() - busiHistoryConference.getConferenceStartTime().getTime()) / 1000));
                            busiHistoryConferenceMapper.updateBusiHistoryConference(busiHistoryConference);
                            logger.info("检测到会议已结束，更新历史记录：" + busiHistoryConference);

                            BusiHistoryParticipant busiHistoryParticipantCon = new BusiHistoryParticipant();
                            busiHistoryParticipantCon.setHistoryConferenceId(busiHistoryConference.getId());
                            List<BusiHistoryParticipant> notEndParticipants = busiHistoryParticipantMapper.selectBusiHistoryParticipantList(busiHistoryParticipantCon);
                            for (BusiHistoryParticipant busiHistoryParticipant : notEndParticipants) {
                                if (busiHistoryParticipant.getOutgoingTime() == null) {
                                    busiHistoryParticipant.setOutgoingTime(currentDate);
                                    busiHistoryParticipant.setUpdateTime(currentDate);
                                    busiHistoryParticipant.setDurationSeconds((int) ((currentDate.getTime() - busiHistoryParticipant.getJoinTime().getTime()) / 1000));
                                    updateBusiHistoryParticipantTerminal(busiHistoryParticipant);
                                    logger.info("检测到会议已结束，更新历史参会者记录：" + busiHistoryParticipant);
                                }
                            }
                        } else {
                            BusiMcuPlcTemplateConference busiMcuPlcTemplateConference = busiMcuPlcTemplateConferenceList.get(0);
                            BusiMcuPlcConferenceAppointment busiMcuPlcConferenceAppointmentCon = new BusiMcuPlcConferenceAppointment();
                            busiMcuPlcConferenceAppointmentCon.setTemplateId(busiMcuPlcTemplateConference.getId());
                            List<BusiMcuPlcConferenceAppointment> busiMcuPlcConferenceAppointmentList = busiMcuPlcConferenceAppointmentMapper.selectBusiMcuPlcConferenceAppointmentList(busiMcuPlcConferenceAppointmentCon);
                            if (busiMcuPlcConferenceAppointmentList == null || busiMcuPlcConferenceAppointmentList.size() == 0) {
                                McuPlcConferenceContext conferenceContext = McuPlcConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiMcuPlcTemplateConference.getId(), McuType.MCU_PLC.getCode()));
                                if (conferenceContext != null && conferenceContext.getDurationEnabled() != null && conferenceContext.getDurationEnabled() == 1) {
                                    Date end = DateUtils.getDiffDate(conferenceContext.getStartTime(), conferenceContext.getDurationTime(), TimeUnit.MINUTES);
                                    long timeDiff = end.getTime() - new Date().getTime();
                                    if (timeDiff < 10 * 60 * 1000) {
                                        long min = timeDiff / (60 * 1000);
                                        long s = (timeDiff / 1000) % 60;
                                        if ((s >= 0 && s < 10) || (s >= 30 && s < 40)) {
                                            StringBuilder msg = new StringBuilder();
                                            msg.append("距离会议结束还剩【");
                                            if (min > 0) {
                                                msg.append(min).append("分");
                                            }
                                            if (s > 0) {
                                                msg.append(s).append("秒");
                                            }

                                            if (s > 0 || min > 0) {
                                                msg.append("】请做好散会准备，如需继续进行本会议，请延长本会议结束时间！");
                                                McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_TIME_COUNTDOWN, msg);
                                            }
                                        }
                                        if (timeDiff < 6) {
                                            if (s >= 0 && s < 5) {
                                                BeanFactory.getBean(IMqttService.class).sendConferenceComingToEndMessage(conferenceContext, min, s);
                                            }
                                        }
                                    }
                                    if (timeDiff <= 0) {
                                        conferenceContext.setEndReasonsType(EndReasonsType.AUTO_END);
                                        busiMcuPlcConferenceService.endConference(conferenceContext.getId(), ConferenceEndType.COMMON.getValue(), true, false);
                                    }
                                }
                            }
                            // 离线重邀
                            McuPlcConferenceContext conferenceContext = McuPlcConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiMcuPlcTemplateConference.getId(), McuType.MCU_PLC.getCode()));
                            if (conferenceContext != null) {
                                if (conferenceContext.isAutoCallTerminal()) {
                                    List<AttendeeForMcuPlc> outBoundTerminals = new ArrayList<>();
                                    McuPlcConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
                                        if (a instanceof TerminalAttendeeForMcuPlc) {
                                            TerminalAttendeeForMcuPlc ta = (TerminalAttendeeForMcuPlc) a;

                                            // 被叫由会控负责发起呼叫
                                            if (AttendType.convert(ta.getAttendType()) == AttendType.OUT_BOUND) {
                                                if (!ta.isMeetingJoined() && !ta.isHangUp()) {
                                                    outBoundTerminals.add(ta);
                                                }
                                            }
                                        } else if (a instanceof InvitedAttendeeForMcuPlc) {
                                            InvitedAttendeeForMcuPlc ia = (InvitedAttendeeForMcuPlc) a;

                                            if (!ia.isMeetingJoined() && !ia.isHangUp()) {
                                                outBoundTerminals.add(ia);
                                            }
                                        }
                                    });
                                    if (outBoundTerminals.size() > 0) {
                                        McuPlcDelayTaskService delayTaskService = BeanFactory.getBean(McuPlcDelayTaskService.class);
                                        InviteAttendeesTask inviteAttendeesTask = new InviteAttendeesTask(conferenceContext.getId(), 100, conferenceContext, outBoundTerminals);
                                        delayTaskService.addTask(inviteAttendeesTask);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }

            try {
                sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 更新参会者终端信息
     *
     * @param busiHistoryParticipant
     */
    private void updateBusiHistoryParticipantTerminal(BusiHistoryParticipant busiHistoryParticipant) {
        if (busiHistoryParticipant != null) {
            if (busiHistoryParticipant.getTerminalId() != null) {
                BusiTerminal busiTerminal = TerminalCache.getInstance().get(busiHistoryParticipant.getTerminalId());
                if (busiTerminal != null) {
                    busiHistoryParticipant.setName(busiTerminal.getName());
                }
            }
            try {
                IBusiHistoryParticipantTerminalService busiHistoryParticipantTerminalService = BeanFactory.getBean(IBusiHistoryParticipantTerminalService.class);
                busiHistoryParticipantTerminalService.updateBusiHistoryParticipantTerminalByBusiHistoryParticipant(busiHistoryParticipant);
            } catch (Exception e) {
            }

        }
    }
}
