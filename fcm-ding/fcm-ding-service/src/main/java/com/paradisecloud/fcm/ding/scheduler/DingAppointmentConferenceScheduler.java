/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : BookingConferenceTask.java
 * Package     : com.paradisecloud.fcm.fme.conference.task
 * @author lilinhai
 * @since 2021-05-20 18:47
 * @version  V1.0
 */
package com.paradisecloud.fcm.ding.scheduler;

import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.ding.cache.DingConferenceContext;
import com.paradisecloud.fcm.ding.cache.DingConferenceContextCache;
import com.paradisecloud.fcm.ding.cache.DingWebSocketMessagePusher;
import com.paradisecloud.fcm.ding.service2.interfaces.IBusiDingConferenceService;
import com.paradisecloud.fcm.ding.service2.interfaces.IBusiMcuDingConferenceAppointmentService;
import com.paradisecloud.fcm.ding.templateConference.StartConference;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiMcuDingConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuDingTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuDingConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiMcuDingTemplateConference;
import com.paradisecloud.fcm.service.task.NotifyTask;
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
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Component
public class DingAppointmentConferenceScheduler extends Thread implements InitializingBean {

    public static final int INT = 6;
    private Logger logger = LoggerFactory.getLogger(getClass());


    @Resource
    private BusiMcuDingConferenceAppointmentMapper busiMcuDingConferenceAppointmentMapper;

    @Resource
    private IBusiMcuDingConferenceAppointmentService busiMcuDingConferenceAppointmentService;

    @Resource
    private BusiMcuDingTemplateConferenceMapper busiMcuDingTemplateConferenceMapper;

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
                BusiMcuDingConferenceAppointment con = new BusiMcuDingConferenceAppointment();
                con.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                List<BusiMcuDingConferenceAppointment> bca = busiMcuDingConferenceAppointmentMapper.selectBusiMcuDingConferenceAppointmentList(con);
                for (BusiMcuDingConferenceAppointment busiConferenceAppointment : bca)
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

    private synchronized void exec(BusiMcuDingConferenceAppointment busiConferenceAppointment)
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

                Long timeDiff = start.getTime() - curDate.getTime();
                long min = timeDiff / (60 * 1000);
                if (min <= 10 * 60) {
                    if (timeDiff > (9 * 60 * 1000 + 55000)) {
                        NotifyTask notifyTask = new NotifyTask(busiConferenceAppointment.getId().toString(), 10000, null, busiConferenceAppointment.getId(), McuType.FME.getCode(), "即将开始");
                        BeanFactory.getBean(TaskService.class).addTask(notifyTask);
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
                        busiMcuDingConferenceAppointmentService.deleteBusiMcuDingConferenceAppointmentById(busiConferenceAppointment.getId());
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

    private void setStartFailedInfo(BusiMcuDingConferenceAppointment busiConferenceAppointment, Throwable e)
    {
        // 启动失败原因记录
        busiConferenceAppointment.setStartFailedReason(CauseUtils.getRootCause(e));
        busiConferenceAppointment.setExtendMinutes(null);
        busiConferenceAppointment.setIsHangUp(null);
        busiConferenceAppointment.setIsStart(null);
        busiConferenceAppointment.setUpdateTime(new Date());
        busiMcuDingConferenceAppointmentMapper.updateBusiMcuDingConferenceAppointment(busiConferenceAppointment);
    }

    private void startConference(BusiMcuDingConferenceAppointment busiConferenceAppointment, Date end, Date curDate)
    {
        // 任务没有处于挂断状态，则进行调度处理
        if (busiConferenceAppointment.getIsHangUp() == null || YesOrNo.convert(busiConferenceAppointment.getIsHangUp()) == YesOrNo.NO)
        {
            BusiMcuDingTemplateConference busiTemplateConference = busiMcuDingTemplateConferenceMapper.selectBusiMcuDingTemplateConferenceById(busiConferenceAppointment.getTemplateId());
            Long conferenceNumber = busiTemplateConference.getConferenceNumber();
            if (YesOrNo.convert(busiTemplateConference.getIsAutoCreateConferenceNumber()) == YesOrNo.YES)
            {
                if (conferenceNumber == null)
                {
                    new StartConference().startConference(busiConferenceAppointment.getTemplateId());
                    busiTemplateConference = busiMcuDingTemplateConferenceMapper.selectBusiMcuDingTemplateConferenceById(busiConferenceAppointment.getTemplateId());
                    conferenceNumber = busiTemplateConference.getConferenceNumber();
                    DingConferenceContext cc = DingConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiConferenceAppointment.getTemplateId(),McuType.MCU_DING));
                    if (cc != null) {
                        NotifyTask notifyTask = new NotifyTask(busiConferenceAppointment.getId().toString(), 10000, null, busiConferenceAppointment.getId(), McuType.FME.getCode(), "即将开始");
                        BeanFactory.getBean(TaskService.class).addTask(notifyTask);
                    }
                    DingWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.MESSAGE_TIP, "预约会议已开始");
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
                DingConferenceContext cc = DingConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiConferenceAppointment.getTemplateId(),McuType.MCU_DING));
                cc.setConferenceAppointment(busiConferenceAppointment);
                DingWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.APPOINTMENT_INFO, busiConferenceAppointment);

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
                            DingWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.MESSAGE_TIP, msg);
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
                busiMcuDingConferenceAppointmentMapper.updateBusiMcuDingConferenceAppointment(busiConferenceAppointment);
            }
        }
    }

    private void startConference(BusiMcuDingConferenceAppointment busiConferenceAppointment, Long conferenceNumber)
    {
        String contextKey = EncryptIdUtil.generateContextKey(busiConferenceAppointment.getTemplateId(), McuType.MCU_DING);
        DingConferenceContext cc = DingConferenceContextCache.getInstance().get(contextKey);
        if (cc == null)
        {
            new StartConference().startConference(busiConferenceAppointment.getTemplateId());
            cc = DingConferenceContextCache.getInstance().get(contextKey);
            DingWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.MESSAGE_TIP, "预约会议已开始");
            NotifyTask notifyTask = new NotifyTask(busiConferenceAppointment.getId().toString(), 10000, null, busiConferenceAppointment.getId(), McuType.FME.getCode(), "开始");
            BeanFactory.getBean(TaskService.class).addTask(notifyTask);
        }
    }

    private void resetConferenceAppointmentStatus(BusiMcuDingConferenceAppointment busiConferenceAppointment)
    {
        if (busiConferenceAppointment.getExtendMinutes() == null && busiConferenceAppointment.getIsHangUp() == null && busiConferenceAppointment.getIsStart() == null)
        {
            return;
        }

        BusiMcuDingTemplateConference busiTemplateConference = busiMcuDingTemplateConferenceMapper.selectBusiMcuDingTemplateConferenceById(busiConferenceAppointment.getTemplateId());
        Long conferenceNumber = busiTemplateConference.getConferenceNumber();
        if (conferenceNumber != null)
        {
            String contextKey = EncryptIdUtil.generateContextKey(busiTemplateConference.getId(), McuType.MCU_DING);
            DingConferenceContext cc = DingConferenceContextCache.getInstance().get(contextKey);
            if (cc != null)
            {
                reset(busiConferenceAppointment);
                BeanFactory.getBean(IBusiDingConferenceService.class).endConference(cc.getId(), EndReasonsType.AUTO_END,true,true);
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

    private void reset(BusiMcuDingConferenceAppointment busiConferenceAppointment)
    {
        busiConferenceAppointment.setExtendMinutes(null);
        busiConferenceAppointment.setIsHangUp(null);
        busiConferenceAppointment.setIsStart(null);
        busiConferenceAppointment.setStartFailedReason(null);
        busiConferenceAppointment.setUpdateTime(new Date());
        busiMcuDingConferenceAppointmentMapper.updateBusiMcuDingConferenceAppointment(busiConferenceAppointment);
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        this.start();
    }
}
