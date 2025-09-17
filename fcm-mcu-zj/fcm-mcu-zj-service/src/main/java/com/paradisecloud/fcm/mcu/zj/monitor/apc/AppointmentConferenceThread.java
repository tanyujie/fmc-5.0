package com.paradisecloud.fcm.mcu.zj.monitor.apc;

import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZjConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZjTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuZjConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiMcuZjTemplateConference;
import com.paradisecloud.fcm.mcu.zj.attendee.utils.McuZjConferenceContextUtils;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjConferenceContextCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.monitor.cache.MonitorThreadCache;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjConferenceAppointmentService;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjConferenceService;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.task.EndDownCascadeConferenceTask;
import com.paradisecloud.fcm.service.conference.task.StartDownCascadeConferenceTask;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.service.task.NotifyTask;
import com.sinhy.enumer.DateTimeFormatPattern;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.CauseUtils;
import com.sinhy.utils.DateUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class AppointmentConferenceThread extends Thread {

    private BusiMcuZjConferenceAppointmentMapper busiMcuZjConferenceAppointmentMapper = null;
    private BusiMcuZjTemplateConferenceMapper busiMcuZjTemplateConferenceMapper = null;
    private IBusiMcuZjConferenceAppointmentService busiMcuZjConferenceAppointmentService = null;
    private IBusiMcuZjConferenceService busiMcuZjConferenceService = null;

    private Pattern timePattern = Pattern.compile("^\\d{2}:\\d{2}:\\d{2}$");

    @Override
    public void run() {
        while (true) {
            if (isInterrupted()) {
                return;
            }


            try {
                long currentTimeMillis = System.currentTimeMillis();
                MonitorThreadCache.getInstance().setAppointmentConferenceTime(currentTimeMillis);

                if (busiMcuZjConferenceAppointmentMapper == null) {
                    busiMcuZjConferenceAppointmentMapper = BeanFactory.getBean(BusiMcuZjConferenceAppointmentMapper.class);
                }
                if (busiMcuZjConferenceAppointmentService == null) {
                    busiMcuZjConferenceAppointmentService = BeanFactory.getBean(IBusiMcuZjConferenceAppointmentService.class);
                }
                if (busiMcuZjTemplateConferenceMapper == null) {
                    busiMcuZjTemplateConferenceMapper = BeanFactory.getBean(BusiMcuZjTemplateConferenceMapper.class);
                }
                if (busiMcuZjConferenceService == null) {
                    busiMcuZjConferenceService = BeanFactory.getBean(IBusiMcuZjConferenceService.class);
                }

                BusiMcuZjConferenceAppointment busiMcuZjConferenceAppointmentCon = new BusiMcuZjConferenceAppointment();
                busiMcuZjConferenceAppointmentCon.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                List<BusiMcuZjConferenceAppointment> busiMcuZjConferenceAppointmentList = busiMcuZjConferenceAppointmentMapper.selectBusiMcuZjConferenceAppointmentList(busiMcuZjConferenceAppointmentCon);
                for (BusiMcuZjConferenceAppointment busiMcuZjConferenceAppointment : busiMcuZjConferenceAppointmentList) {
                    McuZjBridge mcuZjBridge = null;
                    try {
                        mcuZjBridge = McuZjBridgeCache.getInstance().getAvailableMcuZjBridgesByDept(busiMcuZjConferenceAppointment.getDeptId()).getMasterMcuZjBridge();
                    } catch (Exception e) {
                    }
                    if (mcuZjBridge != null && mcuZjBridge.isDataInitialized()) {
                        process(busiMcuZjConferenceAppointment);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void process(BusiMcuZjConferenceAppointment busiMcuZjConferenceAppointment) {
        try {
            Date curDate = new Date();
            AppointmentConferenceRepeatRate appointmentConferenceRepeatRate = AppointmentConferenceRepeatRate.convert(busiMcuZjConferenceAppointment.getRepeatRate());
            if (appointmentConferenceRepeatRate.isOK(busiMcuZjConferenceAppointment.getRepeatDate())) {
                String today = DateUtils.formatTo(DateTimeFormatPattern.PATTERN_13.getPattern());
                Date start = null;
                Date end = null;
                if (appointmentConferenceRepeatRate == AppointmentConferenceRepeatRate.CUSTOM) {
                    start = DateUtils.convertToDate(busiMcuZjConferenceAppointment.getStartTime());
                    end = DateUtils.convertToDate(busiMcuZjConferenceAppointment.getEndTime());
                } else {
                    if (timePattern.matcher(busiMcuZjConferenceAppointment.getStartTime()).matches()) {
                        start = DateUtils.convertToDate(today + " " + busiMcuZjConferenceAppointment.getStartTime());
                        end = DateUtils.convertToDate(today + " " + busiMcuZjConferenceAppointment.getEndTime());
                    } else {
                        start = DateUtils.convertToDate(busiMcuZjConferenceAppointment.getStartTime());
                        end = DateUtils.convertToDate(busiMcuZjConferenceAppointment.getEndTime());
                        if (curDate.after(end)) {
                            start = DateUtils.convertToDate(today + " " + DateUtils.formatTo("HH:mm:ss", start));
                            end = DateUtils.convertToDate(today + " " + DateUtils.formatTo("HH:mm:ss", end));
                        }
                    }
                }

                if (busiMcuZjConferenceAppointment.getExtendMinutes() != null) {
                    end = DateUtils.getDiffDate(end, busiMcuZjConferenceAppointment.getExtendMinutes(), TimeUnit.MINUTES);
                }

                Long timeDiff = start.getTime() - curDate.getTime();
                if (timeDiff != null && timeDiff > 0) {
                    long min = timeDiff / (60 * 1000);
                    if (min <= 10 * 60) {
                        if (timeDiff > (9 * 60 * 1000 + 55000)) {
                            NotifyTask notifyTask = new NotifyTask(busiMcuZjConferenceAppointment.getId().toString(), 10000, null, busiMcuZjConferenceAppointment.getId(), McuType.FME.getCode(), "即将开始");
                            BeanFactory.getBean(TaskService.class).addTask(notifyTask);
                        }
                    }
                }
                if (curDate.after(start) && curDate.before(end)) {
                    try {
                        startConference(busiMcuZjConferenceAppointment, end, curDate);
                    } catch (Throwable e) {
                        // 启动失败原因记录
                        setStartFailedInfo(busiMcuZjConferenceAppointment, e);
                    }
                } else {
                    resetConferenceAppointmentStatus(busiMcuZjConferenceAppointment);
                    if (appointmentConferenceRepeatRate == AppointmentConferenceRepeatRate.CUSTOM && curDate.after(end)) {
                        busiMcuZjConferenceAppointmentService.deleteBusiMcuZjConferenceAppointmentById(busiMcuZjConferenceAppointment.getId());
                    }
                }
            } else {
                resetConferenceAppointmentStatus(busiMcuZjConferenceAppointment);
            }
        } catch (Throwable e) {
            // 启动失败原因记录
            setStartFailedInfo(busiMcuZjConferenceAppointment, e);
        }
    }

    private void setStartFailedInfo(BusiMcuZjConferenceAppointment busiMcuZjConferenceAppointment, Throwable e) {
        // 启动失败原因记录
        busiMcuZjConferenceAppointment.setStartFailedReason(CauseUtils.getRootCause(e));
//        busiMcuZjConferenceAppointment.setExtendMinutes(null);
//        busiMcuZjConferenceAppointment.setIsHangUp(null);
//        busiMcuZjConferenceAppointment.setIsStart(null);
        busiMcuZjConferenceAppointment.setUpdateTime(new Date());
        busiMcuZjConferenceAppointmentMapper.updateBusiMcuZjConferenceAppointment(busiMcuZjConferenceAppointment);
    }

    private void startConference(BusiMcuZjConferenceAppointment busiMcuZjConferenceAppointment, Date end, Date curDate) {
        // 任务没有处于挂断状态，则进行调度处理
        if (busiMcuZjConferenceAppointment.getIsHangUp() == null || YesOrNo.convert(busiMcuZjConferenceAppointment.getIsHangUp()) == YesOrNo.NO) {
            BusiMcuZjTemplateConference busiMcuZjTemplateConference = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(busiMcuZjConferenceAppointment.getTemplateId());
            Long conferenceNumber = busiMcuZjTemplateConference.getConferenceNumber();
            if (YesOrNo.convert(busiMcuZjTemplateConference.getIsAutoCreateConferenceNumber()) == YesOrNo.YES) {
                if (conferenceNumber == null) {
                    String contextKey = busiMcuZjConferenceService.startConference(busiMcuZjConferenceAppointment.getTemplateId());
                    if (StringUtils.isNotEmpty(contextKey)) {
                        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                        if (baseConferenceContext != null) {
                            StartDownCascadeConferenceTask startDownCascadeConferenceTask = new StartDownCascadeConferenceTask(baseConferenceContext.getId(), 0, baseConferenceContext);
                            BeanFactory.getBean(TaskService.class).addTask(startDownCascadeConferenceTask);
                        }
                        if (baseConferenceContext != null) {
                            NotifyTask notifyTask = new NotifyTask(busiMcuZjConferenceAppointment.getId().toString(), 10000, null, busiMcuZjConferenceAppointment.getId(), McuType.FME.getCode(), "即将开始");
                            BeanFactory.getBean(TaskService.class).addTask(notifyTask);
                        }
                    }
                    busiMcuZjTemplateConference = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(busiMcuZjConferenceAppointment.getTemplateId());
                    conferenceNumber = busiMcuZjTemplateConference.getConferenceNumber();
                    McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(EncryptIdUtil.generateKey(busiMcuZjTemplateConference.getId(), McuType.MCU_ZJ.getCode()));
                    McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "预约会议已开始");
                } else {
                    startConference(busiMcuZjConferenceAppointment, conferenceNumber);
                }
            } else {
                startConference(busiMcuZjConferenceAppointment, conferenceNumber);
            }
            if (conferenceNumber != null) {
                McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(EncryptIdUtil.generateKey(busiMcuZjTemplateConference.getId(), McuType.MCU_ZJ.getCode()));
                if (conferenceContext != null) {
                    conferenceContext.setConferenceAppointment(busiMcuZjConferenceAppointment);
                    McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.APPOINTMENT_INFO, busiMcuZjConferenceAppointment);

                    long timeDiff = end.getTime() - curDate.getTime();
                    if (timeDiff < 10 * 60 * 1000) {
                        long min = timeDiff / (60 * 1000);
                        long s = (timeDiff / 1000) % 60;
                        if ((s >= 0 && s < 5) || (s >= 30 && s < 35)) {
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
                    if (!"9999".equals(DateUtil.convertDateToString(end, "yyyy"))) {
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
                                    EndDownCascadeConferenceTask endDownCascadeConferenceTask = new EndDownCascadeConferenceTask(conferenceContext.getId(), 0, conferenceContext);
                                    BeanFactory.getBean(TaskService.class).addTask(endDownCascadeConferenceTask);
                                }
                            }
                        }
                    }
                }
            }

            if (busiMcuZjConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiMcuZjConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                busiMcuZjConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                busiMcuZjConferenceAppointmentMapper.updateBusiMcuZjConferenceAppointment(busiMcuZjConferenceAppointment);
            }
        }
    }

    private void startConference(BusiMcuZjConferenceAppointment busiMcuZjConferenceAppointment, Long conferenceNumber) {
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(EncryptIdUtil.generateKey(busiMcuZjConferenceAppointment.getTemplateId(), McuType.MCU_ZJ.getCode()));
        if (conferenceContext == null) {
            String contextKey = busiMcuZjConferenceService.startConference(busiMcuZjConferenceAppointment.getTemplateId());
            if (StringUtils.isNotEmpty(contextKey)) {
                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                if (baseConferenceContext != null) {
                    StartDownCascadeConferenceTask startDownCascadeConferenceTask = new StartDownCascadeConferenceTask(baseConferenceContext.getId(), 0, baseConferenceContext);
                    BeanFactory.getBean(TaskService.class).addTask(startDownCascadeConferenceTask);
                }
                if (baseConferenceContext != null) {
                    NotifyTask notifyTask = new NotifyTask(busiMcuZjConferenceAppointment.getId().toString(), 10000, null, busiMcuZjConferenceAppointment.getId(), McuType.FME.getCode(), "即将开始");
                    BeanFactory.getBean(TaskService.class).addTask(notifyTask);
                }
            }
            conferenceContext = McuZjConferenceContextCache.getInstance().get(EncryptIdUtil.generateConferenceId(busiMcuZjConferenceAppointment.getTemplateId(), McuType.MCU_ZJ.getCode()));
            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "预约会议已开始");
        }
    }

    private void resetConferenceAppointmentStatus(BusiMcuZjConferenceAppointment busiMcuZjConferenceAppointment) {
        if (busiMcuZjConferenceAppointment.getExtendMinutes() == null && busiMcuZjConferenceAppointment.getIsHangUp() == null && busiMcuZjConferenceAppointment.getIsStart() == null) {
            return;
        }

        BusiMcuZjTemplateConference busiMcuZjTemplateConference = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(busiMcuZjConferenceAppointment.getTemplateId());
        Long conferenceNumber = busiMcuZjTemplateConference.getConferenceNumber();
        if (conferenceNumber != null) {
            McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiMcuZjTemplateConference.getId(), McuType.MCU_ZJ.getCode()));
            if (conferenceContext != null) {
                reset(busiMcuZjConferenceAppointment);
                conferenceContext.setEndReasonsType(EndReasonsType.AUTO_END);
                busiMcuZjConferenceService.endConference(conferenceContext.getContextKey(), ConferenceEndType.COMMON.getValue(), true, false);
                EndDownCascadeConferenceTask endDownCascadeConferenceTask = new EndDownCascadeConferenceTask(conferenceContext.getId(), 0, conferenceContext);
                BeanFactory.getBean(TaskService.class).addTask(endDownCascadeConferenceTask);
            } else {
                reset(busiMcuZjConferenceAppointment);
            }
        } else {
            reset(busiMcuZjConferenceAppointment);
        }
    }

    private void reset(BusiMcuZjConferenceAppointment busiMcuZjConferenceAppointment) {
        busiMcuZjConferenceAppointment.setExtendMinutes(null);
        busiMcuZjConferenceAppointment.setIsHangUp(null);
        busiMcuZjConferenceAppointment.setIsStart(null);
        busiMcuZjConferenceAppointment.setStartFailedReason(null);
        busiMcuZjConferenceAppointment.setUpdateTime(new Date());
        busiMcuZjConferenceAppointmentMapper.updateBusiMcuZjConferenceAppointment(busiMcuZjConferenceAppointment);
    }
}
