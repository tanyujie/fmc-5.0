package com.paradisecloud.fcm.mcu.zj.monitor.ct;

import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.AttendType;
import com.paradisecloud.fcm.common.enumer.ConferenceEndType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryParticipantMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZjConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZjTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mcu.zj.attendee.utils.McuZjConferenceContextUtils;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjConferenceContextCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.InvitedAttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.TerminalAttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.monitor.cache.MonitorThreadCache;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjConferenceService;
import com.paradisecloud.fcm.mcu.zj.task.DelayTaskService;
import com.paradisecloud.fcm.mcu.zj.task.InviteAttendeesTask;
import com.paradisecloud.fcm.service.interfaces.IBusiHistoryParticipantTerminalService;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.*;
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

                BusiMcuZjConferenceAppointmentMapper busiMcuZjConferenceAppointmentMapper = BeanFactory.getBean(BusiMcuZjConferenceAppointmentMapper.class);
                BusiMcuZjTemplateConferenceMapper busiMcuZjTemplateConferenceMapper = BeanFactory.getBean(BusiMcuZjTemplateConferenceMapper.class);
                BusiHistoryConferenceMapper busiHistoryConferenceMapper = BeanFactory.getBean(BusiHistoryConferenceMapper.class);
                BusiHistoryParticipantMapper busiHistoryParticipantMapper = BeanFactory.getBean(BusiHistoryParticipantMapper.class);
                IBusiMcuZjConferenceService busiMcuZjConferenceService = BeanFactory.getBean(IBusiMcuZjConferenceService.class);
                List<BusiHistoryConference> notEndConferences = busiHistoryConferenceMapper.selectNotEndHistoryConferenceList(McuType.MCU_ZJ.getCode());
                if (!ObjectUtils.isEmpty(notEndConferences)) {
                    for (BusiHistoryConference busiHistoryConference : notEndConferences) {
                        if (System.currentTimeMillis() - busiHistoryConference.getConferenceStartTime().getTime() < 10000) {
                            continue;
                        }
                        BusiMcuZjTemplateConference busiMcuZjTemplateConferenceCon = new BusiMcuZjTemplateConference();
                        busiMcuZjTemplateConferenceCon.setLastConferenceId(busiHistoryConference.getId());
                        List<BusiMcuZjTemplateConference> busiMcuZjTemplateConferenceList = busiMcuZjTemplateConferenceMapper.selectAllBusiMcuZjTemplateConferenceList(busiMcuZjTemplateConferenceCon);
                        if (busiMcuZjTemplateConferenceList == null || busiMcuZjTemplateConferenceList.size() == 0) {
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
                            BusiMcuZjTemplateConference busiMcuZjTemplateConference = busiMcuZjTemplateConferenceList.get(0);
                            BusiMcuZjConferenceAppointment busiMcuZjConferenceAppointmentCon = new BusiMcuZjConferenceAppointment();
                            busiMcuZjConferenceAppointmentCon.setTemplateId(busiMcuZjTemplateConference.getId());
                            List<BusiMcuZjConferenceAppointment> busiMcuZjConferenceAppointmentList = busiMcuZjConferenceAppointmentMapper.selectBusiMcuZjConferenceAppointmentList(busiMcuZjConferenceAppointmentCon);
                            if (busiMcuZjConferenceAppointmentList == null || busiMcuZjConferenceAppointmentList.size() == 0) {
                                McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(EncryptIdUtil.generateConferenceId(busiMcuZjTemplateConference.getId(), McuType.MCU_ZJ.getCode()));
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
                                                McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, msg);
                                            }
                                        }
                                        if (timeDiff < 6 * 60 * 1000) {
                                            if (s >= 0 && s < 5) {
                                                BeanFactory.getBean(IMqttService.class).sendConferenceComingToEndMessage(conferenceContext, min, s);
                                            }
                                        }
                                    }
                                    if (timeDiff <= 0) {
                                        conferenceContext.setEndReasonsType(EndReasonsType.AUTO_END);
                                        busiMcuZjConferenceService.endConference(conferenceContext.getContextKey(), ConferenceEndType.COMMON.getValue(), true, false);
                                    }
                                }
                                // 自动结束
                                if (conferenceContext != null) {
                                    RedisCache redisCache = BeanFactory.getBean(RedisCache.class);
                                    final Set<Boolean> set = new HashSet<>();
                                    McuZjConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
                                        if (a.isMeetingJoined()) {
                                            if (set.size() == 0) {
                                                redisCache.setCacheObject(conferenceContext.getConferenceNumber() + "_" + conferenceContext.getStartTime().getTime() + "_" + "idle_time", System.currentTimeMillis(), 48, TimeUnit.HOURS);
                                                set.add(true);
                                            }
                                        }
                                    });
                                    Long idleTime = redisCache.getCacheObject(conferenceContext.getConferenceNumber() + "_" + conferenceContext.getStartTime().getTime() + "_" + "idle_time");
                                    if (idleTime != null) {
                                        if (System.currentTimeMillis() - idleTime > 600000) {
                                            conferenceContext.setEndReasonsType(EndReasonsType.IDLE_TOO_LONG);
                                            busiMcuZjConferenceService.endConference(conferenceContext.getContextKey(), ConferenceEndType.COMMON.getValue(), true, false);
                                        }
                                    }
                                }
                            }
                            // 离线重邀
                            McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(EncryptIdUtil.generateConferenceId(busiMcuZjTemplateConference.getId(), McuType.MCU_ZJ.getCode()));
                            if (conferenceContext != null) {
                                if (conferenceContext.isAutoCallTerminal()) {
                                    List<AttendeeForMcuZj> outBoundTerminals = new ArrayList<>();
                                    McuZjConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
                                        if (a instanceof TerminalAttendeeForMcuZj) {
                                            TerminalAttendeeForMcuZj ta = (TerminalAttendeeForMcuZj) a;

                                            // 被叫由会控负责发起呼叫
                                            if (AttendType.convert(ta.getAttendType()) == AttendType.OUT_BOUND) {
                                                if (!ta.isMeetingJoined() && !ta.isHangUp()) {
                                                    outBoundTerminals.add(ta);
                                                }
                                            }
                                        } else if (a instanceof InvitedAttendeeForMcuZj) {
                                            InvitedAttendeeForMcuZj ia = (InvitedAttendeeForMcuZj) a;

                                            if (!ia.isMeetingJoined() && !ia.isHangUp()) {
                                                outBoundTerminals.add(ia);
                                            }
                                        }
                                    });
                                    if (outBoundTerminals.size() > 0) {
                                        DelayTaskService delayTaskService = BeanFactory.getBean(DelayTaskService.class);
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
