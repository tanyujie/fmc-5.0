/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : BookingConferenceTask.java
 * Package     : com.paradisecloud.fcm.fme.conference.task
 * @author lilinhai 
 * @since 2021-05-20 18:47
 * @version  V1.0
 */ 
package com.paradisecloud.smc3.monitor;

import com.paradisecloud.fcm.common.cache.LicenseCache;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.model.BusiOperationLog;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.interfaces.IBusiOperationLogService;
import com.paradisecloud.fcm.service.ops.OpsDataCache;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.templateconference.StartConference;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.smc3.busi.utils.Smc3WebSocketMessagePusher;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc3ConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc3TemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3ConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplateConference;
import com.paradisecloud.smc3.service.impls.BusiSmc3ConferenceServiceImpl;
import com.paradisecloud.smc3.service.interfaces.IAttendeeSmc3Service;
import com.paradisecloud.smc3.service.interfaces.IBusiMcuSmc3ConferenceAppointmentService;
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
public class ConferenceAppointmentScheduler extends Thread implements InitializingBean
{

    public static final String RES_RESOURCE_NOT_EXIST = "开始会议错误录播资源不足";
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Resource
    private BusiMcuSmc3ConferenceAppointmentMapper busiMcuSmc3ConferenceAppointmentMapper;
    
    @Resource
    private IBusiMcuSmc3ConferenceAppointmentService busiMcuSmc3ConferenceAppointmentService;
    
    @Resource
    private BusiMcuSmc3TemplateConferenceMapper busiMcuSmc3TemplateConferenceMapper;

    private Pattern timePattern = Pattern.compile("^\\d{2}:\\d{2}:\\d{2}$");


    private volatile int conferenceEndTipMsgTimes;
    
    @Override
    public void run()
    {
        logger.info("预约会议调度器启动成功！");
        ThreadUtils.sleep(3 * 1000);
        while (true)
        {
            try
            {
                BusiMcuSmc3ConferenceAppointment con = new BusiMcuSmc3ConferenceAppointment();
                con.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                List<BusiMcuSmc3ConferenceAppointment> bca = busiMcuSmc3ConferenceAppointmentMapper.selectBusiMcuSmc3ConferenceAppointmentList(con);
                for (BusiMcuSmc3ConferenceAppointment busiConferenceAppointment : bca)
                {
                    exec(busiConferenceAppointment);
                }
            }
            catch (Throwable e)
            {
                logger.error("预约会议任务调度出错", e);
            }
            finally 
            {
                ThreadUtils.sleep(1000);
            }
        }
    }

    private synchronized void exec(BusiMcuSmc3ConferenceAppointment busiConferenceAppointment)
    {
        try
        {
            Date curDate = new Date();
            AppointmentConferenceRepeatRate rr = AppointmentConferenceRepeatRate.convert(busiConferenceAppointment.getRepeatRate());
            if (rr.isOK(busiConferenceAppointment.getRepeatDate()))
            {
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
                        busiMcuSmc3ConferenceAppointmentService.deleteBusiMcuSmc3ConferenceAppointmentById(busiConferenceAppointment.getId());
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

    private void setStartFailedInfo(BusiMcuSmc3ConferenceAppointment busiConferenceAppointment, Throwable e)
    {
        // 启动失败原因记录
        busiConferenceAppointment.setStartFailedReason(CauseUtils.getRootCause(e));
        busiConferenceAppointment.setExtendMinutes(null);
        busiConferenceAppointment.setIsHangUp(null);
        busiConferenceAppointment.setIsStart(null);
        busiConferenceAppointment.setUpdateTime(new Date());
        if(Objects.equals(CauseUtils.getRootCause(e), RES_RESOURCE_NOT_EXIST)){
            busiConferenceAppointment.setStatus(2);
        }
        busiMcuSmc3ConferenceAppointmentMapper.updateBusiMcuSmc3ConferenceAppointment(busiConferenceAppointment);
    }

    private void startConference(BusiMcuSmc3ConferenceAppointment busiConferenceAppointment, Date end, Date curDate)
    {
        // 任务没有处于挂断状态，则进行调度处理
        if (busiConferenceAppointment.getIsHangUp() == null || YesOrNo.convert(busiConferenceAppointment.getIsHangUp()) == YesOrNo.NO)
        {
            BusiMcuSmc3TemplateConference busiTemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(busiConferenceAppointment.getTemplateId());
            Long conferenceNumber = busiTemplateConference.getConferenceNumber();
            if (YesOrNo.convert(busiTemplateConference.getIsAutoCreateConferenceNumber()) == YesOrNo.YES)
            {
                if (conferenceNumber == null)
                {
                    new StartConference().startConference(busiConferenceAppointment.getTemplateId());
                    busiTemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(busiConferenceAppointment.getTemplateId());
                    conferenceNumber = busiTemplateConference.getConferenceNumber();
                    Smc3ConferenceContext cc = Smc3ConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiConferenceAppointment.getTemplateId(),McuType.SMC3));
                    Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.MESSAGE_TIP, "预约会议已开始");
                }
                else
                {
                    startConference(busiConferenceAppointment, conferenceNumber);
                }
            }
            else
            {
                startConference(busiConferenceAppointment, conferenceNumber);
            }
            if (conferenceNumber != null)
            {
                Smc3ConferenceContext cc = Smc3ConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiConferenceAppointment.getTemplateId(),McuType.SMC3));
                cc.setConferenceAppointment(busiConferenceAppointment);
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.APPOINTMENT_INFO, busiConferenceAppointment);

                BaseAttendee minutesAttendee = cc.getMinutesAttendee();
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
                            IAttendeeSmc3Service attendeeService = BeanFactory.getBean(IAttendeeSmc3Service.class);
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
                
                if (conferenceEndTipMsgTimes == 0)
                {
                    long timeDiff = end.getTime() - curDate.getTime();
                    if (timeDiff < 10 * 60 * 1000)
                    {
                        long min = timeDiff / (60 * 1000);
                        long s = (timeDiff / 1000) % 60;
                        StringBuilder msg = new StringBuilder();
                        msg.append("距离会议结束还剩【");
                        if (min > 0)
                        {
                            msg.append(min).append("分");
                        }
                        if (s > 0)
                        {
                            msg.append(s).append("秒");
                        }
                        
                        if (s > 0 || min > 0)
                        {
                            msg.append("】请做好散会准备，如需继续进行本会议，请延长本会议结束时间！");
                            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.MESSAGE_TIP, msg);
                        }
                    }
                }
                
                conferenceEndTipMsgTimes++;
                if (conferenceEndTipMsgTimes >= 6)
                {
                    conferenceEndTipMsgTimes = 0;
                }
            }
            
            if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES)
            {
                busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                busiMcuSmc3ConferenceAppointmentMapper.updateBusiMcuSmc3ConferenceAppointment(busiConferenceAppointment);
            }
        }
    }

    private void startConference(BusiMcuSmc3ConferenceAppointment busiConferenceAppointment, Long conferenceNumber)
    {
        String contextKey = EncryptIdUtil.generateContextKey(busiConferenceAppointment.getTemplateId(), McuType.SMC3);
        Smc3ConferenceContext cc = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (cc == null)
        {
            new StartConference().startConference(busiConferenceAppointment.getTemplateId());
            cc = Smc3ConferenceContextCache.getInstance().get(contextKey);
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.MESSAGE_TIP, "预约会议已开始");
        }
    }
    
    private void resetConferenceAppointmentStatus(BusiMcuSmc3ConferenceAppointment busiConferenceAppointment)
    {
        if (busiConferenceAppointment.getExtendMinutes() == null && busiConferenceAppointment.getIsHangUp() == null && busiConferenceAppointment.getIsStart() == null)
        {
            return;
        }
        
        BusiMcuSmc3TemplateConference busiTemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(busiConferenceAppointment.getTemplateId());
        Long conferenceNumber = busiTemplateConference.getConferenceNumber();
        if (conferenceNumber != null)
        {
            String contextKey = EncryptIdUtil.generateContextKey(busiTemplateConference.getId(), McuType.SMC3);
            Smc3ConferenceContext cc = Smc3ConferenceContextCache.getInstance().get(contextKey);
            if (cc != null)
            {
                reset(busiConferenceAppointment);
                BeanFactory.getBean(BusiSmc3ConferenceServiceImpl.class).endConference(cc.getId(), EndReasonsType.AUTO_END,true,true);
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

    private void reset(BusiMcuSmc3ConferenceAppointment busiConferenceAppointment)
    {
        busiConferenceAppointment.setExtendMinutes(null);
        busiConferenceAppointment.setIsHangUp(null);
        busiConferenceAppointment.setIsStart(null);
        busiConferenceAppointment.setStartFailedReason(null);
        busiConferenceAppointment.setUpdateTime(new Date());
        busiMcuSmc3ConferenceAppointmentMapper.updateBusiMcuSmc3ConferenceAppointment(busiConferenceAppointment);
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        this.start();
    }
}
