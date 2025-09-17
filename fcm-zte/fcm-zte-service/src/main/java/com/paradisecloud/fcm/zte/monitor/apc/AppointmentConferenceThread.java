package com.paradisecloud.fcm.zte.monitor.apc;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZteConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZteTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuZteConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiMcuZteTemplateConference;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.task.EndDownCascadeConferenceTask;
import com.paradisecloud.fcm.service.conference.task.StartDownCascadeConferenceTask;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.service.task.NotifyTask;
import com.paradisecloud.fcm.zte.cache.McuZteBridgeCache;
import com.paradisecloud.fcm.zte.cache.McuZteConferenceContextCache;
import com.paradisecloud.fcm.zte.cache.McuZteWebSocketMessagePusher;
import com.paradisecloud.fcm.zte.cache.model.McuZteBridge;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.monitor.cache.MonitorThreadCache;
import com.paradisecloud.fcm.zte.service.interfaces.IBusiMcuZteConferenceAppointmentService;
import com.paradisecloud.fcm.zte.service.interfaces.IBusiMcuZteConferenceService;
import com.sinhy.enumer.DateTimeFormatPattern;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.CauseUtils;
import com.sinhy.utils.DateUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AppointmentConferenceThread extends Thread {

    private BusiMcuZteConferenceAppointmentMapper busiMcuZteConferenceAppointmentMapper = null;
    private BusiMcuZteTemplateConferenceMapper busiMcuZteTemplateConferenceMapper = null;
    private IBusiMcuZteConferenceAppointmentService busiMcuZteConferenceAppointmentService = null;
    private IBusiMcuZteConferenceService busiMcuZteConferenceService = null;

    @Override
    public void run() {
        while (true) {
            if (isInterrupted()) {
                return;
            }


            try {
                long currentTimeMillis = System.currentTimeMillis();
                MonitorThreadCache.getInstance().setAppointmentConferenceTime(currentTimeMillis);

                if (busiMcuZteConferenceAppointmentMapper == null) {
                    busiMcuZteConferenceAppointmentMapper = BeanFactory.getBean(BusiMcuZteConferenceAppointmentMapper.class);
                }
                if (busiMcuZteConferenceAppointmentService == null) {
                    busiMcuZteConferenceAppointmentService = BeanFactory.getBean(IBusiMcuZteConferenceAppointmentService.class);
                }
                if (busiMcuZteTemplateConferenceMapper == null) {
                    busiMcuZteTemplateConferenceMapper = BeanFactory.getBean(BusiMcuZteTemplateConferenceMapper.class);
                }
                if (busiMcuZteConferenceService == null) {
                    busiMcuZteConferenceService = BeanFactory.getBean(IBusiMcuZteConferenceService.class);
                }

                BusiMcuZteConferenceAppointment busiMcuZteConferenceAppointmentCon = new BusiMcuZteConferenceAppointment();
                busiMcuZteConferenceAppointmentCon.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                List<BusiMcuZteConferenceAppointment> busiMcuZteConferenceAppointmentList = busiMcuZteConferenceAppointmentMapper.selectBusiMcuZteConferenceAppointmentList(busiMcuZteConferenceAppointmentCon);
                for (BusiMcuZteConferenceAppointment busiMcuZteConferenceAppointment : busiMcuZteConferenceAppointmentList) {
                    McuZteBridge mcuZteBridge = null;
                    try {
                        mcuZteBridge = McuZteBridgeCache.getInstance().getAvailableMcuZteBridgesByDept(busiMcuZteConferenceAppointment.getDeptId()).getMasterMcuZteBridge();
                    } catch (Exception e) {
                    }
                    if (mcuZteBridge != null && mcuZteBridge.isDataInitialized()) {
                        process(busiMcuZteConferenceAppointment);
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

    private void process(BusiMcuZteConferenceAppointment busiMcuZteConferenceAppointment) {
        try {
            AppointmentConferenceRepeatRate appointmentConferenceRepeatRate = AppointmentConferenceRepeatRate.convert(busiMcuZteConferenceAppointment.getRepeatRate());
            if (appointmentConferenceRepeatRate.isOK(busiMcuZteConferenceAppointment.getRepeatDate())) {
                String today = DateUtils.formatTo(DateTimeFormatPattern.PATTERN_13.getPattern());
                Date start = null;
                Date end = null;
                if (appointmentConferenceRepeatRate == AppointmentConferenceRepeatRate.CUSTOM) {
                    start = DateUtils.convertToDate(busiMcuZteConferenceAppointment.getStartTime());
                    end = DateUtils.convertToDate(busiMcuZteConferenceAppointment.getEndTime());
                } else {
                    start = DateUtils.convertToDate(today + " " + busiMcuZteConferenceAppointment.getStartTime());
                    end = DateUtils.convertToDate(today + " " + busiMcuZteConferenceAppointment.getEndTime());
                }

                if (busiMcuZteConferenceAppointment.getExtendMinutes() != null) {
                    end = DateUtils.getDiffDate(end, busiMcuZteConferenceAppointment.getExtendMinutes(), TimeUnit.MINUTES);
                }

                Date curDate = new Date();
                Long timeDiff = start.getTime() - curDate.getTime();
                if (timeDiff != null && timeDiff > 0) {
                    long min = timeDiff / (60 * 1000);
                    if (min <= 10 * 60) {
                        if (timeDiff > (9 * 60 * 1000 + 55000)) {
                            NotifyTask notifyTask = new NotifyTask(busiMcuZteConferenceAppointment.getId().toString(), 10000, null, busiMcuZteConferenceAppointment.getId(), McuType.FME.getCode(), "即将开始");
                            BeanFactory.getBean(TaskService.class).addTask(notifyTask);
                        }
                    }
                }
                if (curDate.after(start) && curDate.before(end)) {
                    try {
                        startConference(busiMcuZteConferenceAppointment, end, curDate);
                    } catch (Throwable e) {
                        // 启动失败原因记录
                        setStartFailedInfo(busiMcuZteConferenceAppointment, e);
                    }
                } else {
                    resetConferenceAppointmentStatus(busiMcuZteConferenceAppointment);
                    if (appointmentConferenceRepeatRate == AppointmentConferenceRepeatRate.CUSTOM && curDate.after(end)) {
                        busiMcuZteConferenceAppointmentService.deleteBusiMcuZteConferenceAppointmentById(busiMcuZteConferenceAppointment.getId());
                    }
                }
            } else {
                resetConferenceAppointmentStatus(busiMcuZteConferenceAppointment);
            }
        } catch (Throwable e) {
            // 启动失败原因记录
            setStartFailedInfo(busiMcuZteConferenceAppointment, e);
        }
    }

    private void setStartFailedInfo(BusiMcuZteConferenceAppointment busiMcuZteConferenceAppointment, Throwable e) {
        // 启动失败原因记录
        busiMcuZteConferenceAppointment.setStartFailedReason(CauseUtils.getRootCause(e));
//        busiMcuZteConferenceAppointment.setExtendMinutes(null);
//        busiMcuZteConferenceAppointment.setIsHangUp(null);
//        busiMcuZteConferenceAppointment.setIsStart(null);
        busiMcuZteConferenceAppointment.setUpdateTime(new Date());
        busiMcuZteConferenceAppointmentMapper.updateBusiMcuZteConferenceAppointment(busiMcuZteConferenceAppointment);
    }

    private void startConference(BusiMcuZteConferenceAppointment busiMcuZteConferenceAppointment, Date end, Date curDate) {
        // 任务没有处于挂断状态，则进行调度处理
        if (busiMcuZteConferenceAppointment.getIsHangUp() == null || YesOrNo.convert(busiMcuZteConferenceAppointment.getIsHangUp()) == YesOrNo.NO) {
            BusiMcuZteTemplateConference busiMcuZteTemplateConference = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(busiMcuZteConferenceAppointment.getTemplateId());
            Long conferenceNumber = busiMcuZteTemplateConference.getConferenceNumber();
            if (YesOrNo.convert(busiMcuZteTemplateConference.getIsAutoCreateConferenceNumber()) == YesOrNo.YES) {
                if (conferenceNumber == null) {
                    String contextKey = busiMcuZteConferenceService.startConference(busiMcuZteConferenceAppointment.getTemplateId());
                    if (StringUtils.isNotEmpty(contextKey)) {
                        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                        if (baseConferenceContext != null) {
                            StartDownCascadeConferenceTask startDownCascadeConferenceTask = new StartDownCascadeConferenceTask(baseConferenceContext.getId(), 0, baseConferenceContext);
                            BeanFactory.getBean(TaskService.class).addTask(startDownCascadeConferenceTask);
                        }
                        if (baseConferenceContext != null) {
                            NotifyTask notifyTask = new NotifyTask(busiMcuZteConferenceAppointment.getId().toString(), 10000, null, busiMcuZteConferenceAppointment.getId(), McuType.FME.getCode(), "开始");
                            BeanFactory.getBean(TaskService.class).addTask(notifyTask);
                        }
                    }
                    busiMcuZteTemplateConference = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(busiMcuZteConferenceAppointment.getTemplateId());
                    conferenceNumber = busiMcuZteTemplateConference.getConferenceNumber();
                    McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiMcuZteTemplateConference.getId(), McuType.MCU_ZTE.getCode()));
                    McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "预约会议已开始");
                } else {
                    startConference(busiMcuZteConferenceAppointment, busiMcuZteTemplateConference.getId());
                }
            } else {
                startConference(busiMcuZteConferenceAppointment, busiMcuZteTemplateConference.getId());
            }
            if (conferenceNumber != null) {
                McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiMcuZteTemplateConference.getId(), McuType.MCU_ZTE.getCode()));
                conferenceContext.setConferenceAppointment(busiMcuZteConferenceAppointment);
                McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.APPOINTMENT_INFO, busiMcuZteConferenceAppointment);

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
                            McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_TIME_COUNTDOWN, msg);
                        }
                    }
                    if (timeDiff < 6) {
                        if (s >= 0 && s < 5) {
                            BeanFactory.getBean(IMqttService.class).sendConferenceComingToEndMessage(conferenceContext, min, s);
                        }
                    }
                }
            }

            if (busiMcuZteConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiMcuZteConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                busiMcuZteConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                busiMcuZteConferenceAppointmentMapper.updateBusiMcuZteConferenceAppointment(busiMcuZteConferenceAppointment);
            }
        }
    }

    private void startConference(BusiMcuZteConferenceAppointment busiMcuZteConferenceAppointment, Long id) {
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(id, McuType.MCU_ZTE.getCode()));
        if (conferenceContext == null) {
            String contextKey = busiMcuZteConferenceService.startConference(busiMcuZteConferenceAppointment.getTemplateId());
            if (StringUtils.isNotEmpty(contextKey)) {
                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                if (baseConferenceContext != null) {
                    StartDownCascadeConferenceTask startDownCascadeConferenceTask = new StartDownCascadeConferenceTask(baseConferenceContext.getId(), 0, baseConferenceContext);
                    BeanFactory.getBean(TaskService.class).addTask(startDownCascadeConferenceTask);
                }
                NotifyTask notifyTask = new NotifyTask(busiMcuZteConferenceAppointment.getId().toString(), 10000, null, busiMcuZteConferenceAppointment.getId(), McuType.FME.getCode(), "开始");
                BeanFactory.getBean(TaskService.class).addTask(notifyTask);
            }
            conferenceContext = McuZteConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(id, McuType.MCU_ZTE.getCode()));
            McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "预约会议已开始");
        }
    }

    private void resetConferenceAppointmentStatus(BusiMcuZteConferenceAppointment busiMcuZteConferenceAppointment) {
        if (busiMcuZteConferenceAppointment.getExtendMinutes() == null && busiMcuZteConferenceAppointment.getIsHangUp() == null && busiMcuZteConferenceAppointment.getIsStart() == null) {
            return;
        }

        BusiMcuZteTemplateConference busiMcuZteTemplateConference = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(busiMcuZteConferenceAppointment.getTemplateId());
        Long conferenceNumber = busiMcuZteTemplateConference.getConferenceNumber();
        if (conferenceNumber != null) {
            McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiMcuZteTemplateConference.getId(), McuType.MCU_ZTE.getCode()));
            if (conferenceContext != null) {
                reset(busiMcuZteConferenceAppointment);
                conferenceContext.setEndReasonsType(EndReasonsType.AUTO_END);
                busiMcuZteConferenceService.endConference(conferenceContext.getId(), ConferenceEndType.COMMON.getValue(), true, false);
                EndDownCascadeConferenceTask endDownCascadeConferenceTask = new EndDownCascadeConferenceTask(conferenceContext.getId(), 0, conferenceContext);
                BeanFactory.getBean(TaskService.class).addTask(endDownCascadeConferenceTask);
            } else {
                reset(busiMcuZteConferenceAppointment);
            }
        } else {
            reset(busiMcuZteConferenceAppointment);
        }
    }

    private void reset(BusiMcuZteConferenceAppointment busiMcuZteConferenceAppointment) {
        busiMcuZteConferenceAppointment.setExtendMinutes(null);
        busiMcuZteConferenceAppointment.setIsHangUp(null);
        busiMcuZteConferenceAppointment.setIsStart(null);
        busiMcuZteConferenceAppointment.setStartFailedReason(null);
        busiMcuZteConferenceAppointment.setUpdateTime(new Date());
        busiMcuZteConferenceAppointmentMapper.updateBusiMcuZteConferenceAppointment(busiMcuZteConferenceAppointment);
    }
}
