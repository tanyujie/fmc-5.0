package com.paradisecloud.fcm.mcu.kdc.monitor.apc;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiMcuKdcConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuKdcTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuKdcConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiMcuKdcTemplateConference;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcBridgeCache;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcConferenceContextCache;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcBridge;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.monitor.cache.MonitorThreadCache;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcConferenceAppointmentService;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcConferenceService;
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
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class AppointmentConferenceThread extends Thread {

    private BusiMcuKdcConferenceAppointmentMapper busiMcuKdcConferenceAppointmentMapper = null;
    private BusiMcuKdcTemplateConferenceMapper busiMcuKdcTemplateConferenceMapper = null;
    private IBusiMcuKdcConferenceAppointmentService busiMcuKdcConferenceAppointmentService = null;
    private IBusiMcuKdcConferenceService busiMcuKdcConferenceService = null;

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

                if (busiMcuKdcConferenceAppointmentMapper == null) {
                    busiMcuKdcConferenceAppointmentMapper = BeanFactory.getBean(BusiMcuKdcConferenceAppointmentMapper.class);
                }
                if (busiMcuKdcConferenceAppointmentService == null) {
                    busiMcuKdcConferenceAppointmentService = BeanFactory.getBean(IBusiMcuKdcConferenceAppointmentService.class);
                }
                if (busiMcuKdcTemplateConferenceMapper == null) {
                    busiMcuKdcTemplateConferenceMapper = BeanFactory.getBean(BusiMcuKdcTemplateConferenceMapper.class);
                }
                if (busiMcuKdcConferenceService == null) {
                    busiMcuKdcConferenceService = BeanFactory.getBean(IBusiMcuKdcConferenceService.class);
                }

                BusiMcuKdcConferenceAppointment busiMcuKdcConferenceAppointmentCon = new BusiMcuKdcConferenceAppointment();
                busiMcuKdcConferenceAppointmentCon.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                List<BusiMcuKdcConferenceAppointment> busiMcuKdcConferenceAppointmentList = busiMcuKdcConferenceAppointmentMapper.selectBusiMcuKdcConferenceAppointmentList(busiMcuKdcConferenceAppointmentCon);
                for (BusiMcuKdcConferenceAppointment busiMcuKdcConferenceAppointment : busiMcuKdcConferenceAppointmentList) {
                    McuKdcBridge mcuKdcBridge = null;
                    try {
                        mcuKdcBridge = McuKdcBridgeCache.getInstance().getAvailableMcuKdcBridgesByDept(busiMcuKdcConferenceAppointment.getDeptId()).getMasterMcuKdcBridge();
                    } catch (Exception e) {
                    }
                    if (mcuKdcBridge != null && mcuKdcBridge.isDataInitialized()) {
                        process(busiMcuKdcConferenceAppointment);
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

    private void process(BusiMcuKdcConferenceAppointment busiMcuKdcConferenceAppointment) {
        try {
            Date curDate = new Date();
            AppointmentConferenceRepeatRate appointmentConferenceRepeatRate = AppointmentConferenceRepeatRate.convert(busiMcuKdcConferenceAppointment.getRepeatRate());
            if (appointmentConferenceRepeatRate.isOK(busiMcuKdcConferenceAppointment.getRepeatDate())) {
                String today = DateUtils.formatTo(DateTimeFormatPattern.PATTERN_13.getPattern());
                Date start = null;
                Date end = null;
                if (appointmentConferenceRepeatRate == AppointmentConferenceRepeatRate.CUSTOM) {
                    start = DateUtils.convertToDate(busiMcuKdcConferenceAppointment.getStartTime());
                    end = DateUtils.convertToDate(busiMcuKdcConferenceAppointment.getEndTime());
                } else {
                    if (timePattern.matcher(busiMcuKdcConferenceAppointment.getStartTime()).matches()) {
                        start = DateUtils.convertToDate(today + " " + busiMcuKdcConferenceAppointment.getStartTime());
                        end = DateUtils.convertToDate(today + " " + busiMcuKdcConferenceAppointment.getEndTime());
                    } else {
                        start = DateUtils.convertToDate(busiMcuKdcConferenceAppointment.getStartTime());
                        end = DateUtils.convertToDate(busiMcuKdcConferenceAppointment.getEndTime());
                        if (curDate.after(end)) {
                            start = DateUtils.convertToDate(today + " " + DateUtils.formatTo("HH:mm:ss", start));
                            end = DateUtils.convertToDate(today + " " + DateUtils.formatTo("HH:mm:ss", end));
                        }
                    }
                }

                if (busiMcuKdcConferenceAppointment.getExtendMinutes() != null) {
                    end = DateUtils.getDiffDate(end, busiMcuKdcConferenceAppointment.getExtendMinutes(), TimeUnit.MINUTES);
                }

                Long timeDiff = start.getTime() - curDate.getTime();
                if (timeDiff != null && timeDiff > 0) {
                    long min = timeDiff / (60 * 1000);
                    if (min <= 10 * 60) {
                        if (timeDiff > (9 * 60 * 1000 + 55000)) {
                            NotifyTask notifyTask = new NotifyTask(busiMcuKdcConferenceAppointment.getId().toString(), 10000, null, busiMcuKdcConferenceAppointment.getId(), McuType.FME.getCode(), "即将开始");
                            BeanFactory.getBean(TaskService.class).addTask(notifyTask);
                        }
                    }
                }
                if (curDate.after(start) && curDate.before(end)) {
                    try {
                        startConference(busiMcuKdcConferenceAppointment, end, curDate);
                    } catch (Throwable e) {
                        // 启动失败原因记录
                        setStartFailedInfo(busiMcuKdcConferenceAppointment, e);
                    }
                } else {
                    resetConferenceAppointmentStatus(busiMcuKdcConferenceAppointment);
                    if (appointmentConferenceRepeatRate == AppointmentConferenceRepeatRate.CUSTOM && curDate.after(end)) {
                        busiMcuKdcConferenceAppointmentService.deleteBusiMcuKdcConferenceAppointmentById(busiMcuKdcConferenceAppointment.getId());
                    }
                }
            } else {
                resetConferenceAppointmentStatus(busiMcuKdcConferenceAppointment);
            }
        } catch (Throwable e) {
            // 启动失败原因记录
            setStartFailedInfo(busiMcuKdcConferenceAppointment, e);
        }
    }

    private void setStartFailedInfo(BusiMcuKdcConferenceAppointment busiMcuKdcConferenceAppointment, Throwable e) {
        // 启动失败原因记录
        busiMcuKdcConferenceAppointment.setStartFailedReason(CauseUtils.getRootCause(e));
//        busiMcuKdcConferenceAppointment.setExtendMinutes(null);
//        busiMcuKdcConferenceAppointment.setIsHangUp(null);
//        busiMcuKdcConferenceAppointment.setIsStart(null);
        busiMcuKdcConferenceAppointment.setUpdateTime(new Date());
        busiMcuKdcConferenceAppointmentMapper.updateBusiMcuKdcConferenceAppointment(busiMcuKdcConferenceAppointment);
    }

    private void startConference(BusiMcuKdcConferenceAppointment busiMcuKdcConferenceAppointment, Date end, Date curDate) {
        // 任务没有处于挂断状态，则进行调度处理
        if (busiMcuKdcConferenceAppointment.getIsHangUp() == null || YesOrNo.convert(busiMcuKdcConferenceAppointment.getIsHangUp()) == YesOrNo.NO) {
            BusiMcuKdcTemplateConference busiMcuKdcTemplateConference = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(busiMcuKdcConferenceAppointment.getTemplateId());
            Long conferenceNumber = busiMcuKdcTemplateConference.getConferenceNumber();
            if (YesOrNo.convert(busiMcuKdcTemplateConference.getIsAutoCreateConferenceNumber()) == YesOrNo.YES) {
                if (conferenceNumber == null) {
                    String contextKey = busiMcuKdcConferenceService.startConference(busiMcuKdcConferenceAppointment.getTemplateId());
                    if (StringUtils.isNotEmpty(contextKey)) {
                        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                        if (baseConferenceContext != null) {
                            StartDownCascadeConferenceTask startDownCascadeConferenceTask = new StartDownCascadeConferenceTask(baseConferenceContext.getId(), 0, baseConferenceContext);
                            BeanFactory.getBean(TaskService.class).addTask(startDownCascadeConferenceTask);
                        }
                    }
                    busiMcuKdcTemplateConference = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(busiMcuKdcConferenceAppointment.getTemplateId());
                    conferenceNumber = busiMcuKdcTemplateConference.getConferenceNumber();
                    McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiMcuKdcTemplateConference.getId(), McuType.MCU_KDC.getCode()));
                    McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "预约会议已开始");
                    if (conferenceContext != null) {
                        NotifyTask notifyTask = new NotifyTask(busiMcuKdcConferenceAppointment.getId().toString(), 10000, null, busiMcuKdcConferenceAppointment.getId(), McuType.FME.getCode(), "开始");
                        BeanFactory.getBean(TaskService.class).addTask(notifyTask);
                    }
                } else {
                    startConference(busiMcuKdcConferenceAppointment, busiMcuKdcTemplateConference.getId());
                }
            } else {
                startConference(busiMcuKdcConferenceAppointment, busiMcuKdcTemplateConference.getId());
            }
            if (conferenceNumber != null) {
                McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiMcuKdcTemplateConference.getId(), McuType.MCU_KDC.getCode()));
                conferenceContext.setConferenceAppointment(busiMcuKdcConferenceAppointment);
                McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.APPOINTMENT_INFO, busiMcuKdcConferenceAppointment);

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
                            McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_TIME_COUNTDOWN, msg);
                        }
                    }
                    if (timeDiff < 6) {
                        if (s >= 0 && s < 5) {
                            BeanFactory.getBean(IMqttService.class).sendConferenceComingToEndMessage(conferenceContext, min, s);
                        }
                    }
                }
            }

            if (busiMcuKdcConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiMcuKdcConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                busiMcuKdcConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                busiMcuKdcConferenceAppointmentMapper.updateBusiMcuKdcConferenceAppointment(busiMcuKdcConferenceAppointment);
            }
        }
    }

    private void startConference(BusiMcuKdcConferenceAppointment busiMcuKdcConferenceAppointment, Long id) {
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(id, McuType.MCU_KDC.getCode()));
        if (conferenceContext == null) {
            String contextKey = busiMcuKdcConferenceService.startConference(busiMcuKdcConferenceAppointment.getTemplateId());
            if (StringUtils.isNotEmpty(contextKey)) {
                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                if (baseConferenceContext != null) {
                    StartDownCascadeConferenceTask startDownCascadeConferenceTask = new StartDownCascadeConferenceTask(baseConferenceContext.getId(), 0, baseConferenceContext);
                    BeanFactory.getBean(TaskService.class).addTask(startDownCascadeConferenceTask);
                }
                NotifyTask notifyTask = new NotifyTask(busiMcuKdcConferenceAppointment.getId().toString(), 10000, null, busiMcuKdcConferenceAppointment.getId(), McuType.FME.getCode(), "开始");
                BeanFactory.getBean(TaskService.class).addTask(notifyTask);
            }
            conferenceContext = McuKdcConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(id, McuType.MCU_KDC.getCode()));
            McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "预约会议已开始");
        }
    }

    private void resetConferenceAppointmentStatus(BusiMcuKdcConferenceAppointment busiMcuKdcConferenceAppointment) {
        if (busiMcuKdcConferenceAppointment.getExtendMinutes() == null && busiMcuKdcConferenceAppointment.getIsHangUp() == null && busiMcuKdcConferenceAppointment.getIsStart() == null) {
            return;
        }

        BusiMcuKdcTemplateConference busiMcuKdcTemplateConference = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(busiMcuKdcConferenceAppointment.getTemplateId());
        Long conferenceNumber = busiMcuKdcTemplateConference.getConferenceNumber();
        if (conferenceNumber != null) {
            McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiMcuKdcTemplateConference.getId(), McuType.MCU_KDC.getCode()));
            if (conferenceContext != null) {
                reset(busiMcuKdcConferenceAppointment);
                conferenceContext.setEndReasonsType(EndReasonsType.AUTO_END);
                busiMcuKdcConferenceService.endConference(conferenceNumber.toString(), ConferenceEndType.COMMON.getValue(), true, false);
                EndDownCascadeConferenceTask endDownCascadeConferenceTask = new EndDownCascadeConferenceTask(conferenceContext.getId(), 0, conferenceContext);
                BeanFactory.getBean(TaskService.class).addTask(endDownCascadeConferenceTask);
            } else {
                reset(busiMcuKdcConferenceAppointment);
            }
        } else {
            reset(busiMcuKdcConferenceAppointment);
        }
    }

    private void reset(BusiMcuKdcConferenceAppointment busiMcuKdcConferenceAppointment) {
        busiMcuKdcConferenceAppointment.setExtendMinutes(null);
        busiMcuKdcConferenceAppointment.setIsHangUp(null);
        busiMcuKdcConferenceAppointment.setIsStart(null);
        busiMcuKdcConferenceAppointment.setStartFailedReason(null);
        busiMcuKdcConferenceAppointment.setUpdateTime(new Date());
        busiMcuKdcConferenceAppointmentMapper.updateBusiMcuKdcConferenceAppointment(busiMcuKdcConferenceAppointment);
    }
}
