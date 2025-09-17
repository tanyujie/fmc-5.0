package com.paradisecloud.fcm.mcu.plc.monitor.apc;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiMcuPlcConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuPlcTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuPlcConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiMcuPlcTemplateConference;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcBridgeCache;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcConferenceContextCache;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcBridge;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.monitor.cache.MonitorThreadCache;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcConferenceAppointmentService;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcConferenceService;
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

    private BusiMcuPlcConferenceAppointmentMapper busiMcuPlcConferenceAppointmentMapper = null;
    private BusiMcuPlcTemplateConferenceMapper busiMcuPlcTemplateConferenceMapper = null;
    private IBusiMcuPlcConferenceAppointmentService busiMcuPlcConferenceAppointmentService = null;
    private IBusiMcuPlcConferenceService busiMcuPlcConferenceService = null;

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

                if (busiMcuPlcConferenceAppointmentMapper == null) {
                    busiMcuPlcConferenceAppointmentMapper = BeanFactory.getBean(BusiMcuPlcConferenceAppointmentMapper.class);
                }
                if (busiMcuPlcConferenceAppointmentService == null) {
                    busiMcuPlcConferenceAppointmentService = BeanFactory.getBean(IBusiMcuPlcConferenceAppointmentService.class);
                }
                if (busiMcuPlcTemplateConferenceMapper == null) {
                    busiMcuPlcTemplateConferenceMapper = BeanFactory.getBean(BusiMcuPlcTemplateConferenceMapper.class);
                }
                if (busiMcuPlcConferenceService == null) {
                    busiMcuPlcConferenceService = BeanFactory.getBean(IBusiMcuPlcConferenceService.class);
                }

                BusiMcuPlcConferenceAppointment busiMcuPlcConferenceAppointmentCon = new BusiMcuPlcConferenceAppointment();
                busiMcuPlcConferenceAppointmentCon.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                List<BusiMcuPlcConferenceAppointment> busiMcuPlcConferenceAppointmentList = busiMcuPlcConferenceAppointmentMapper.selectBusiMcuPlcConferenceAppointmentList(busiMcuPlcConferenceAppointmentCon);
                for (BusiMcuPlcConferenceAppointment busiMcuPlcConferenceAppointment : busiMcuPlcConferenceAppointmentList) {
                    McuPlcBridge mcuPlcBridge = null;
                    try {
                        mcuPlcBridge = McuPlcBridgeCache.getInstance().getAvailableMcuPlcBridgesByDept(busiMcuPlcConferenceAppointment.getDeptId()).getMasterMcuPlcBridge();
                    } catch (Exception e) {
                    }
                    if (mcuPlcBridge != null && mcuPlcBridge.isDataInitialized()) {
                        process(busiMcuPlcConferenceAppointment);
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

    private void process(BusiMcuPlcConferenceAppointment busiMcuPlcConferenceAppointment) {
        try {
            Date curDate = new Date();
            AppointmentConferenceRepeatRate appointmentConferenceRepeatRate = AppointmentConferenceRepeatRate.convert(busiMcuPlcConferenceAppointment.getRepeatRate());
            if (appointmentConferenceRepeatRate.isOK(busiMcuPlcConferenceAppointment.getRepeatDate())) {
                String today = DateUtils.formatTo(DateTimeFormatPattern.PATTERN_13.getPattern());
                Date start = null;
                Date end = null;
                if (appointmentConferenceRepeatRate == AppointmentConferenceRepeatRate.CUSTOM) {
                    start = DateUtils.convertToDate(busiMcuPlcConferenceAppointment.getStartTime());
                    end = DateUtils.convertToDate(busiMcuPlcConferenceAppointment.getEndTime());
                } else {
                    if (timePattern.matcher(busiMcuPlcConferenceAppointment.getStartTime()).matches()) {
                        start = DateUtils.convertToDate(today + " " + busiMcuPlcConferenceAppointment.getStartTime());
                        end = DateUtils.convertToDate(today + " " + busiMcuPlcConferenceAppointment.getEndTime());
                    } else {
                        start = DateUtils.convertToDate(busiMcuPlcConferenceAppointment.getStartTime());
                        end = DateUtils.convertToDate(busiMcuPlcConferenceAppointment.getEndTime());
                        if (curDate.after(end)) {
                            start = DateUtils.convertToDate(today + " " + DateUtils.formatTo("HH:mm:ss", start));
                            end = DateUtils.convertToDate(today + " " + DateUtils.formatTo("HH:mm:ss", end));
                        }
                    }
                }

                if (busiMcuPlcConferenceAppointment.getExtendMinutes() != null) {
                    end = DateUtils.getDiffDate(end, busiMcuPlcConferenceAppointment.getExtendMinutes(), TimeUnit.MINUTES);
                }

                Long timeDiff = start.getTime() - curDate.getTime();
                if (timeDiff != null && timeDiff > 0) {
                    long min = timeDiff / (60 * 1000);
                    if (min <= 10 * 60) {
                        if (timeDiff > (9 * 60 * 1000 + 55000)) {
                            NotifyTask notifyTask = new NotifyTask(busiMcuPlcConferenceAppointment.getId().toString(), 10000, null, busiMcuPlcConferenceAppointment.getId(), McuType.FME.getCode(), "即将开始");
                            BeanFactory.getBean(TaskService.class).addTask(notifyTask);
                        }
                    }
                }
                if (curDate.after(start) && curDate.before(end)) {
                    try {
                        startConference(busiMcuPlcConferenceAppointment, end, curDate);
                    } catch (Throwable e) {
                        // 启动失败原因记录
                        setStartFailedInfo(busiMcuPlcConferenceAppointment, e);
                    }
                } else {
                    resetConferenceAppointmentStatus(busiMcuPlcConferenceAppointment);
                    if (appointmentConferenceRepeatRate == AppointmentConferenceRepeatRate.CUSTOM && curDate.after(end)) {
                        busiMcuPlcConferenceAppointmentService.deleteBusiMcuPlcConferenceAppointmentById(busiMcuPlcConferenceAppointment.getId());
                    }
                }
            } else {
                resetConferenceAppointmentStatus(busiMcuPlcConferenceAppointment);
            }
        } catch (Throwable e) {
            // 启动失败原因记录
            setStartFailedInfo(busiMcuPlcConferenceAppointment, e);
        }
    }

    private void setStartFailedInfo(BusiMcuPlcConferenceAppointment busiMcuPlcConferenceAppointment, Throwable e) {
        // 启动失败原因记录
        busiMcuPlcConferenceAppointment.setStartFailedReason(CauseUtils.getRootCause(e));
//        busiMcuPlcConferenceAppointment.setExtendMinutes(null);
//        busiMcuPlcConferenceAppointment.setIsHangUp(null);
//        busiMcuPlcConferenceAppointment.setIsStart(null);
        busiMcuPlcConferenceAppointment.setUpdateTime(new Date());
        busiMcuPlcConferenceAppointmentMapper.updateBusiMcuPlcConferenceAppointment(busiMcuPlcConferenceAppointment);
    }

    private void startConference(BusiMcuPlcConferenceAppointment busiMcuPlcConferenceAppointment, Date end, Date curDate) {
        // 任务没有处于挂断状态，则进行调度处理
        if (busiMcuPlcConferenceAppointment.getIsHangUp() == null || YesOrNo.convert(busiMcuPlcConferenceAppointment.getIsHangUp()) == YesOrNo.NO) {
            BusiMcuPlcTemplateConference busiMcuPlcTemplateConference = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(busiMcuPlcConferenceAppointment.getTemplateId());
            Long conferenceNumber = busiMcuPlcTemplateConference.getConferenceNumber();
            if (YesOrNo.convert(busiMcuPlcTemplateConference.getIsAutoCreateConferenceNumber()) == YesOrNo.YES) {
                if (conferenceNumber == null) {
                    String contextKey = busiMcuPlcConferenceService.startConference(busiMcuPlcConferenceAppointment.getTemplateId());
                    if (StringUtils.isNotEmpty(contextKey)) {
                        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                        if (baseConferenceContext != null) {
                            StartDownCascadeConferenceTask startDownCascadeConferenceTask = new StartDownCascadeConferenceTask(baseConferenceContext.getId(), 0, baseConferenceContext);
                            BeanFactory.getBean(TaskService.class).addTask(startDownCascadeConferenceTask);
                        }
                        if (baseConferenceContext != null) {
                            NotifyTask notifyTask = new NotifyTask(busiMcuPlcConferenceAppointment.getId().toString(), 10000, null, busiMcuPlcConferenceAppointment.getId(), McuType.FME.getCode(), "开始");
                            BeanFactory.getBean(TaskService.class).addTask(notifyTask);
                        }
                    }
                    busiMcuPlcTemplateConference = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(busiMcuPlcConferenceAppointment.getTemplateId());
                    conferenceNumber = busiMcuPlcTemplateConference.getConferenceNumber();
                    McuPlcConferenceContext conferenceContext = McuPlcConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiMcuPlcTemplateConference.getId(), McuType.MCU_PLC.getCode()));
                    McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "预约会议已开始");
                } else {
                    startConference(busiMcuPlcConferenceAppointment, busiMcuPlcTemplateConference.getId());
                }
            } else {
                startConference(busiMcuPlcConferenceAppointment, busiMcuPlcTemplateConference.getId());
            }
            if (conferenceNumber != null) {
                McuPlcConferenceContext conferenceContext = McuPlcConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiMcuPlcTemplateConference.getId(), McuType.MCU_PLC.getCode()));
                conferenceContext.setConferenceAppointment(busiMcuPlcConferenceAppointment);
                McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.APPOINTMENT_INFO, busiMcuPlcConferenceAppointment);

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
                            McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_TIME_COUNTDOWN, msg);
                        }
                    }
                    if (timeDiff < 6) {
                        if (s >= 0 && s < 5) {
                            BeanFactory.getBean(IMqttService.class).sendConferenceComingToEndMessage(conferenceContext, min, s);
                        }
                    }
                }
            }

            if (busiMcuPlcConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiMcuPlcConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                busiMcuPlcConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                busiMcuPlcConferenceAppointmentMapper.updateBusiMcuPlcConferenceAppointment(busiMcuPlcConferenceAppointment);
            }
        }
    }

    private void startConference(BusiMcuPlcConferenceAppointment busiMcuPlcConferenceAppointment, Long id) {
        McuPlcConferenceContext conferenceContext = McuPlcConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(id, McuType.MCU_PLC.getCode()));
        if (conferenceContext == null) {
            String contextKey = busiMcuPlcConferenceService.startConference(busiMcuPlcConferenceAppointment.getTemplateId());
            if (StringUtils.isNotEmpty(contextKey)) {
                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                if (baseConferenceContext != null) {
                    StartDownCascadeConferenceTask startDownCascadeConferenceTask = new StartDownCascadeConferenceTask(baseConferenceContext.getId(), 0, baseConferenceContext);
                    BeanFactory.getBean(TaskService.class).addTask(startDownCascadeConferenceTask);
                }
                NotifyTask notifyTask = new NotifyTask(busiMcuPlcConferenceAppointment.getId().toString(), 10000, null, busiMcuPlcConferenceAppointment.getId(), McuType.FME.getCode(), "开始");
                BeanFactory.getBean(TaskService.class).addTask(notifyTask);
            }
            conferenceContext = McuPlcConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(id, McuType.MCU_PLC.getCode()));
            McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "预约会议已开始");
        }
    }

    private void resetConferenceAppointmentStatus(BusiMcuPlcConferenceAppointment busiMcuPlcConferenceAppointment) {
        if (busiMcuPlcConferenceAppointment.getExtendMinutes() == null && busiMcuPlcConferenceAppointment.getIsHangUp() == null && busiMcuPlcConferenceAppointment.getIsStart() == null) {
            return;
        }

        BusiMcuPlcTemplateConference busiMcuPlcTemplateConference = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(busiMcuPlcConferenceAppointment.getTemplateId());
        Long conferenceNumber = busiMcuPlcTemplateConference.getConferenceNumber();
        if (conferenceNumber != null) {
            McuPlcConferenceContext conferenceContext = McuPlcConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiMcuPlcTemplateConference.getId(), McuType.MCU_PLC.getCode()));
            if (conferenceContext != null) {
                reset(busiMcuPlcConferenceAppointment);
                conferenceContext.setEndReasonsType(EndReasonsType.AUTO_END);
                busiMcuPlcConferenceService.endConference(conferenceContext.getId(), ConferenceEndType.COMMON.getValue(), true, false);
                EndDownCascadeConferenceTask endDownCascadeConferenceTask = new EndDownCascadeConferenceTask(conferenceContext.getId(), 0, conferenceContext);
                BeanFactory.getBean(TaskService.class).addTask(endDownCascadeConferenceTask);
            } else {
                reset(busiMcuPlcConferenceAppointment);
            }
        } else {
            reset(busiMcuPlcConferenceAppointment);
        }
    }

    private void reset(BusiMcuPlcConferenceAppointment busiMcuPlcConferenceAppointment) {
        busiMcuPlcConferenceAppointment.setExtendMinutes(null);
        busiMcuPlcConferenceAppointment.setIsHangUp(null);
        busiMcuPlcConferenceAppointment.setIsStart(null);
        busiMcuPlcConferenceAppointment.setStartFailedReason(null);
        busiMcuPlcConferenceAppointment.setUpdateTime(new Date());
        busiMcuPlcConferenceAppointmentMapper.updateBusiMcuPlcConferenceAppointment(busiMcuPlcConferenceAppointment);
    }
}
