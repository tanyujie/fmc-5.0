/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : BookingConferenceTask.java
 * Package     : com.paradisecloud.fcm.fme.conference.task
 * @author lilinhai
 * @since 2021-05-20 18:47
 * @version  V1.0
 */
package com.paradisecloud.fcm.fme.conference.scheduler;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.cache.LicenseCache;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiOperationLog;
import com.paradisecloud.fcm.dao.model.BusiTemplateConference;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.service.eunm.NotifyType;
import com.paradisecloud.fcm.service.interfaces.IBusiOperationLogService;
import com.paradisecloud.fcm.service.ops.OpsDataCache;
import com.paradisecloud.fcm.service.task.CloudSmsLocaltoRemoteTask;
import com.paradisecloud.fcm.service.task.NotifyTask;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.task.EndDownCascadeConferenceTask;
import com.paradisecloud.fcm.service.conference.task.StartDownCascadeConferenceTask;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceAppointmentService;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.ITemplateConferenceStartService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.system.dao.mapper.SysUserMapper;
import com.paradisecloud.system.dao.model.SysUser;
import com.sinhy.enumer.DateTimeFormatPattern;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.CauseUtils;
import com.sinhy.utils.DateUtils;
import com.sinhy.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Component
public class AppointmentConferenceScheduler extends Thread implements InitializingBean
{

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private BusiConferenceAppointmentMapper busiConferenceAppointmentMapper;

    @Resource
    private IBusiConferenceAppointmentService busiConferenceAppointmentService;

    @Resource
    private BusiTemplateConferenceMapper busiTemplateConferenceMapper;

    @Resource
    private ITemplateConferenceStartService templateConferenceStartService;

    @Resource
    private IBusiConferenceService busiConferenceService;

    private Pattern timePattern = Pattern.compile("^\\d{2}:\\d{2}:\\d{2}$");


    private volatile int conferenceEndTipMsgTimes;
    private volatile boolean checkAutoCall;

    @Override
    public void run()
    {
        logger.info("FME预约会议调度器启动成功！");
        ThreadUtils.sleep(80 * 1000);
        while (true)
        {
            try
            {
                BusiConferenceAppointment con = new BusiConferenceAppointment();
                con.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                List<BusiConferenceAppointment> bca = busiConferenceAppointmentMapper.selectBusiConferenceAppointmentList(con);
                for (BusiConferenceAppointment busiConferenceAppointment : bca)
                {
                    exec(busiConferenceAppointment);
                }
                checkAutoCall = !checkAutoCall;
            }
            catch (Throwable e)
            {
                logger.error("预约会议任务调度出错", e);
            }
            finally
            {
                ThreadUtils.sleep(5000);
            }
        }
    }

    private synchronized void exec(BusiConferenceAppointment busiConferenceAppointment)
    {
        try
        {
            AppointmentConferenceRepeatRate rr = AppointmentConferenceRepeatRate.convert(busiConferenceAppointment.getRepeatRate());
            if (rr.isOK(busiConferenceAppointment.getRepeatDate()))
            {
                Date curDate = new Date();
                String today = DateUtils.formatTo(DateTimeFormatPattern.PATTERN_13.getPattern());
                Date start = null;
                Date end = null;
                if (rr == AppointmentConferenceRepeatRate.CUSTOM)
                {
                    start = DateUtils.convertToDate(busiConferenceAppointment.getStartTime());
                    end = DateUtils.convertToDate(busiConferenceAppointment.getEndTime());
                }
                else
                {
                    if (timePattern.matcher(busiConferenceAppointment.getStartTime()).matches()) {
                        start = DateUtils.convertToDate(today + " " + busiConferenceAppointment.getStartTime());
                        end = DateUtils.convertToDate(today + " " + busiConferenceAppointment.getEndTime());
                    } else {
                        start = DateUtils.convertToDate(busiConferenceAppointment.getStartTime());
                        end = DateUtils.convertToDate(busiConferenceAppointment.getEndTime());
                        if (curDate.after(end)) {
                            start = DateUtils.convertToDate(today + " " + DateUtils.formatTo("HH:mm:ss", start));
                            end = DateUtils.convertToDate(today + " " + DateUtils.formatTo("HH:mm:ss", end));
                        }
                    }
                }

                if (busiConferenceAppointment.getExtendMinutes() != null)
                {
                    end = DateUtils.getDiffDate(end, busiConferenceAppointment.getExtendMinutes(), TimeUnit.MINUTES);
                }

                Long timeDiff = start.getTime() - curDate.getTime();
                if (timeDiff != null && timeDiff > 0) {
                    long min = timeDiff / (60 * 1000);
                    if (min <= 10 * 60) {
                        if (timeDiff > (9 * 60 * 1000 + 55000)) {
                            NotifyTask notifyTask = new NotifyTask(busiConferenceAppointment.getId().toString(), 10000, null, busiConferenceAppointment.getId(), McuType.FME.getCode(), "即将开始");
                            BeanFactory.getBean(TaskService.class).addTask(notifyTask);
                        }
                    }
                }
                if (curDate.after(start) && curDate.before(end))
                {
                    try
                    {
                        startConference(busiConferenceAppointment, end, curDate);
                    }
                    catch (Throwable e)
                    {
                        // 启动失败原因记录
                        setStartFailedInfo(busiConferenceAppointment, e);
                    }
                }
                else
                {
                    resetConferenceAppointmentStatus(busiConferenceAppointment);
                    if (rr == AppointmentConferenceRepeatRate.CUSTOM && curDate.after(end))
                    {
                        busiConferenceAppointmentService.deleteBusiConferenceAppointmentById(busiConferenceAppointment.getId());
                    }
                }
            }
            else
            {
                resetConferenceAppointmentStatus(busiConferenceAppointment);
            }
        }
        catch (Throwable e)
        {
            logger.error("预约会议任务运行出错：" + busiConferenceAppointment, e);

            // 启动失败原因记录
            setStartFailedInfo(busiConferenceAppointment, e);
        }
    }

    private void setStartFailedInfo(BusiConferenceAppointment busiConferenceAppointment, Throwable e)
    {
        // 启动失败原因记录
        String rootCause = CauseUtils.getRootCause(e);
        if (rootCause.length() > 1500) {
            rootCause = rootCause.substring(0, 1500);
        }
        busiConferenceAppointment.setStartFailedReason(rootCause);
        busiConferenceAppointment.setExtendMinutes(null);
        busiConferenceAppointment.setIsHangUp(null);
        busiConferenceAppointment.setIsStart(null);
        busiConferenceAppointment.setUpdateTime(new Date());
        busiConferenceAppointmentMapper.updateBusiConferenceAppointment(busiConferenceAppointment);
    }

    private void startConference(BusiConferenceAppointment busiConferenceAppointment, Date end, Date curDate)
    {
        // 任务没有处于挂断状态，则进行调度处理
        if (busiConferenceAppointment.getIsHangUp() == null || YesOrNo.convert(busiConferenceAppointment.getIsHangUp()) == YesOrNo.NO)
        {
            BusiTemplateConference busiTemplateConference = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
            Long conferenceNumber = busiTemplateConference.getConferenceNumber();
            if (YesOrNo.convert(busiTemplateConference.getIsAutoCreateConferenceNumber()) == YesOrNo.YES)
            {
                if (conferenceNumber == null)
                {
                    String contextKey = templateConferenceStartService.startTemplateConference(busiConferenceAppointment.getTemplateId());
                    if (StringUtils.isNotEmpty(contextKey)) {
                        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                        if (baseConferenceContext != null) {
                            StartDownCascadeConferenceTask startDownCascadeConferenceTask = new StartDownCascadeConferenceTask(baseConferenceContext.getId(), 0, baseConferenceContext);
                            BeanFactory.getBean(TaskService.class).addTask(startDownCascadeConferenceTask);
                            StartDownCascadeConferenceTask startDownCascadeConferenceTask_1 = new StartDownCascadeConferenceTask(baseConferenceContext.getId()+1, 2000, baseConferenceContext);
                            BeanFactory.getBean(TaskService.class).addTask(startDownCascadeConferenceTask_1);
                        }
                    }
                    busiTemplateConference = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
                    conferenceNumber = busiTemplateConference.getConferenceNumber();
                    ConferenceContext cc = ConferenceContextCache.getInstance().get(EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), McuType.FME.getCode()));
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.MESSAGE_TIP, "预约会议已开始");
                    if (cc != null) {
                        NotifyTask notifyTask = new NotifyTask(busiConferenceAppointment.getId().toString(), 10000, null, busiConferenceAppointment.getId(), McuType.FME.getCode(), "开始");
                        BeanFactory.getBean(TaskService.class).addTask(notifyTask);
                    }
                }
                else
                {
                    startConference(busiConferenceAppointment, EncryptIdUtil.generateKey(busiTemplateConference.getId(), McuType.FME.getCode()));
                }
            }
            else
            {
                startConference(busiConferenceAppointment, EncryptIdUtil.generateKey(busiTemplateConference.getId(), McuType.FME.getCode()));
            }
            if (conferenceNumber != null)
            {
                ConferenceContext cc = ConferenceContextCache.getInstance().get(EncryptIdUtil.generateKey(busiTemplateConference.getId(), McuType.FME.getCode()));
                if (cc != null) {
                    Attendee minutesAttendee = cc.getMinutesAttendee();
                    if (minutesAttendee != null && minutesAttendee.isMeetingJoined()) {
                        Integer asrTime = LicenseCache.getInstance().getAsrTime();
                        if (OpsDataCache.getInstance().getAsrTime() != 0) {
                            asrTime = OpsDataCache.getInstance().getAsrTime();
                            if (asrTime == -1) {
                                asrTime = null;
                            }
                        }
                        if (asrTime != null) {
                            long meetingJoinedTime = minutesAttendee.getMeetingJoinedTime();
                            if (System.currentTimeMillis() - meetingJoinedTime > asrTime * 60000) {
                                IAttendeeService attendeeService = BeanFactory.getBean(IAttendeeService.class);
                                attendeeService.hangUp(cc.getId(), minutesAttendee.getId());
                                if (cc.getAttendeeById(minutesAttendee.getId()) != null) {
                                    attendeeService.remove(cc.getId(), minutesAttendee.getId());
                                }
                                String text = "字幕使用时长" + asrTime + "分钟用完自动结束";
                                cc.minutesLog("------" + text + "------");
                                BusiOperationLog busiOperationLog = new BusiOperationLog();
                                busiOperationLog.setActionDetails(text);
                                busiOperationLog.setHistoryConferenceId(cc.getHistoryConference().getId());
                                busiOperationLog.setUserId(null);
                                busiOperationLog.setOperatorName("系统");
                                busiOperationLog.setTime(new Date());
                                busiOperationLog.setActionResult(2);
                                busiOperationLog.setIp("127.0.0.1");
                                busiOperationLog.setDeviceType("system");
                                IBusiOperationLogService busiOperationLogService = BeanFactory.getBean(IBusiOperationLogService.class);
                                busiOperationLogService.insertBusiOperationLog(busiOperationLog);
                            }
                        }
                    }
                    cc.setConferenceAppointment(busiConferenceAppointment);
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.APPOINTMENT_INFO, busiConferenceAppointment);

                    if (conferenceEndTipMsgTimes == 0) {
                        long timeDiff = end.getTime() - curDate.getTime();
                        if (timeDiff < 10 * 60 * 1000) {
                            long min = timeDiff / (60 * 1000);
                            long s = (timeDiff / 1000) % 60;
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
                                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.MESSAGE_TIP, msg);
                                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.MESSAGE_SHOW_TIP, msg);
                            }
                            if (timeDiff < 6) {
                                if (s >= 0 && s < 5) {
                                    BeanFactory.getBean(IMqttService.class).sendConferenceComingToEndMessage(cc, min, s);
                                }
                            }
                        }
                    }

                    conferenceEndTipMsgTimes++;
                    if (conferenceEndTipMsgTimes >= 6) {
                        conferenceEndTipMsgTimes = 0;
                    }

                    // 消除系统字幕
                    if (cc.getSystemMessageEndTime() > 0 && System.currentTimeMillis() - cc.getSystemMessageEndTime() >= 0) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("messageText", "");
                            jsonObject.put("messageDuration", "permanent");
                            jsonObject.put("messagePosition", "top");
                            BeanFactory.getBean(IAttendeeService.class).sendMessage(cc.getId(), jsonObject);
                            cc.setSystemMessageEndTime(0);
                        } catch (Exception e) {
                        }
                    }
                }
            }

            if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES)
            {
                busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                busiConferenceAppointmentMapper.updateBusiConferenceAppointment(busiConferenceAppointment);
            }
        }
    }

    private void startConference(BusiConferenceAppointment busiConferenceAppointment, String contextKey)
    {
        ConferenceContext cc = ConferenceContextCache.getInstance().get(contextKey);
        if (cc == null)
        {
            String contextKeyTemp = templateConferenceStartService.startTemplateConference(busiConferenceAppointment.getTemplateId());
            if (StringUtils.isNotEmpty(contextKeyTemp)) {
                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKeyTemp);
                if (baseConferenceContext != null) {
                    StartDownCascadeConferenceTask startDownCascadeConferenceTask = new StartDownCascadeConferenceTask(baseConferenceContext.getId(), 0, baseConferenceContext);
                    BeanFactory.getBean(TaskService.class).addTask(startDownCascadeConferenceTask);
                    StartDownCascadeConferenceTask startDownCascadeConferenceTask_1 = new StartDownCascadeConferenceTask(baseConferenceContext.getId()+1, 1000, baseConferenceContext);
                    BeanFactory.getBean(TaskService.class).addTask(startDownCascadeConferenceTask_1);
                }
                NotifyTask notifyTask = new NotifyTask(busiConferenceAppointment.getId().toString(), 10000, null, busiConferenceAppointment.getId(), McuType.FME.getCode(), "开始");
                BeanFactory.getBean(TaskService.class).addTask(notifyTask);
            }
            cc = ConferenceContextCache.getInstance().get(contextKey);
            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.MESSAGE_TIP, "预约会议已开始");

        }

    }

    private void sendSms(BusiConferenceAppointment busiConferenceAppointment, ConferenceContext cc) {
        try {
            String region = ExternalConfigCache.getInstance().getRegion();
            if(Objects.equals("ops",region)){
                String cfname= cc.getName();
                String cfNumber= cc.getConferenceNumber();
                String startTime = busiConferenceAppointment.getStartTime();
                String endTime = busiConferenceAppointment.getEndTime();
                SysUserMapper sysUserMapper = BeanFactory.getBean(SysUserMapper.class);
                SysUser admin = sysUserMapper.selectUserByUserName("admin");
                if(admin!=null&&admin.getPhonenumber()!=null){
                    TaskService taskService = BeanFactory.getBean(TaskService.class);
                    CloudSmsLocaltoRemoteTask cloudSmsLocaltoRemoteTask = new CloudSmsLocaltoRemoteTask(busiConferenceAppointment.getId().toString(),1000,cfname,cfNumber,admin.getPhonenumber(),startTime,endTime, NotifyType.ADMIN_MEETING_START);
                    taskService.addTask(cloudSmsLocaltoRemoteTask);
                }
            }
        } catch (Exception e) {
            logger.info("smscloudtencent error 2 "+e.getMessage());
        }
    }

    private void resetConferenceAppointmentStatus(BusiConferenceAppointment busiConferenceAppointment)
    {
        if (busiConferenceAppointment.getExtendMinutes() == null && busiConferenceAppointment.getIsHangUp() == null && busiConferenceAppointment.getIsStart() == null)
        {
            return;
        }

        BusiTemplateConference busiTemplateConference = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
        Long conferenceNumber = busiTemplateConference.getConferenceNumber();
        if (conferenceNumber != null)
        {
            ConferenceContext cc = ConferenceContextCache.getInstance().get(EncryptIdUtil.generateKey(busiTemplateConference.getId(), McuType.FME.getCode()));
            if (cc != null)
            {
                reset(busiConferenceAppointment);
                busiConferenceService.endConference(cc.getId(), ConferenceEndType.COMMON.getValue(), EndReasonsType.AUTO_END);
                EndDownCascadeConferenceTask endDownCascadeConferenceTask = new EndDownCascadeConferenceTask(cc.getId(), 0, cc);
                BeanFactory.getBean(TaskService.class).addTask(endDownCascadeConferenceTask);
            }
            else
            {
                reset(busiConferenceAppointment);
            }
        }
        else
        {
            reset(busiConferenceAppointment);
        }
    }

    private void reset(BusiConferenceAppointment busiConferenceAppointment)
    {
        busiConferenceAppointment.setExtendMinutes(null);
        busiConferenceAppointment.setIsHangUp(null);
        busiConferenceAppointment.setIsStart(null);
        busiConferenceAppointment.setStartFailedReason(null);
        busiConferenceAppointment.setUpdateTime(new Date());
        busiConferenceAppointmentMapper.updateBusiConferenceAppointment(busiConferenceAppointment);
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        this.start();
    }
}
