/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : BookingConferenceTask.java
 * Package     : com.paradisecloud.fcm.fme.conference.task
 * @author lilinhai
 * @since 2021-05-20 18:47
 * @version  V1.0
 */
package com.paradisecloud.fcm.huaweicloud.huaweicloud.scheduler;

import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudTemplateConference;

import com.paradisecloud.fcm.dao.mapper.BusiMcuHwcloudConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuHwcloudTemplateConferenceMapper;

import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContextCache;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudWebSocketMessagePusher;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.impls.BusiHwcloudConferenceServiceImpl;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiMcuHwcloudConferenceAppointmentService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.templateConference.StartConference;
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
public class HwcloudAppointmentConferenceScheduler extends Thread implements InitializingBean {

    public static final int INT = 6;
    private Logger logger = LoggerFactory.getLogger(getClass());


    @Resource
    private BusiMcuHwcloudConferenceAppointmentMapper busiMcuHwcloudConferenceAppointmentMapper;

    @Resource
    private IBusiMcuHwcloudConferenceAppointmentService busiMcuHwcloudConferenceAppointmentService;

    @Resource
    private BusiMcuHwcloudTemplateConferenceMapper busiMcuHwcloudTemplateConferenceMapper;

    private Pattern timePattern = Pattern.compile("^\\d{2}:\\d{2}:\\d{2}$");

    private volatile int conferenceEndTipMsgTimes;
    @Override
    public void run()
    {
        logger.info("腾讯[Hwcloud-APPOINTMENT]预约会议调度器启动成功！");
        ThreadUtils.sleep(3 * 1000);
        while (true)
        {
            try
            {
                BusiMcuHwcloudConferenceAppointment con = new BusiMcuHwcloudConferenceAppointment();
                con.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                List<BusiMcuHwcloudConferenceAppointment> bca = busiMcuHwcloudConferenceAppointmentMapper.selectBusiMcuHwcloudConferenceAppointmentList(con);
                for (BusiMcuHwcloudConferenceAppointment busiConferenceAppointment : bca)
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

    private synchronized void exec(BusiMcuHwcloudConferenceAppointment busiConferenceAppointment)
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
                        busiMcuHwcloudConferenceAppointmentService.deleteBusiMcuHwcloudConferenceAppointmentById(busiConferenceAppointment.getId());
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

    private void setStartFailedInfo(BusiMcuHwcloudConferenceAppointment busiConferenceAppointment, Throwable e)
    {
        // 启动失败原因记录
        busiConferenceAppointment.setStartFailedReason(CauseUtils.getRootCause(e));
        busiConferenceAppointment.setExtendMinutes(null);
        busiConferenceAppointment.setIsHangUp(null);
        busiConferenceAppointment.setIsStart(null);
        busiConferenceAppointment.setUpdateTime(new Date());
        busiMcuHwcloudConferenceAppointmentMapper.updateBusiMcuHwcloudConferenceAppointment(busiConferenceAppointment);
    }

    private void startConference(BusiMcuHwcloudConferenceAppointment busiConferenceAppointment, Date end, Date curDate)
    {
        // 任务没有处于挂断状态，则进行调度处理
        if (busiConferenceAppointment.getIsHangUp() == null || YesOrNo.convert(busiConferenceAppointment.getIsHangUp()) == YesOrNo.NO)
        {
            BusiMcuHwcloudTemplateConference busiTemplateConference = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(busiConferenceAppointment.getTemplateId());
            String confId = busiTemplateConference.getConfId();
            if (YesOrNo.convert(busiTemplateConference.getIsAutoCreateConferenceNumber()) == YesOrNo.YES)
            {
                if (confId == null)
                {
                    new StartConference().startConference(busiConferenceAppointment.getTemplateId());
                    HwcloudConferenceContext cc = HwcloudConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiConferenceAppointment.getTemplateId(),McuType.MCU_HWCLOUD));
                    HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.MESSAGE_TIP, "预约会议已开始");
                    if (cc != null) {
                        NotifyTask notifyTask = new NotifyTask(busiConferenceAppointment.getId().toString(), 10000, null, busiConferenceAppointment.getId(), McuType.FME.getCode(), "开始");
                        BeanFactory.getBean(TaskService.class).addTask(notifyTask);
                    }
                }
                else
                {
                    startConference(busiConferenceAppointment, null);
                }
            }
            else
            {
                startConference(busiConferenceAppointment, null);
            }
            if (confId != null)
            {
                HwcloudConferenceContext cc = HwcloudConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiConferenceAppointment.getTemplateId(),McuType.MCU_HWCLOUD));
                cc.setConferenceAppointment(busiConferenceAppointment);
                HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.APPOINTMENT_INFO, busiConferenceAppointment);

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
                            HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.MESSAGE_TIP, msg);
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
                busiMcuHwcloudConferenceAppointmentMapper.updateBusiMcuHwcloudConferenceAppointment(busiConferenceAppointment);
            }
        }
    }

    private void startConference(BusiMcuHwcloudConferenceAppointment busiConferenceAppointment, Long conferenceNumber)
    {
        String contextKey = EncryptIdUtil.generateContextKey(busiConferenceAppointment.getTemplateId(), McuType.MCU_HWCLOUD);
        HwcloudConferenceContext cc = HwcloudConferenceContextCache.getInstance().get(contextKey);
        if (cc == null)
        {
            new StartConference().startConference(busiConferenceAppointment.getTemplateId());
            cc = HwcloudConferenceContextCache.getInstance().get(contextKey);
            HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.MESSAGE_TIP, "预约会议已开始");
            NotifyTask notifyTask = new NotifyTask(busiConferenceAppointment.getId().toString(), 10000, null, busiConferenceAppointment.getId(), McuType.FME.getCode(), "开始");
            BeanFactory.getBean(TaskService.class).addTask(notifyTask);
        }
    }

    private void resetConferenceAppointmentStatus(BusiMcuHwcloudConferenceAppointment busiConferenceAppointment)
    {
        if (busiConferenceAppointment.getExtendMinutes() == null && busiConferenceAppointment.getIsHangUp() == null && busiConferenceAppointment.getIsStart() == null)
        {
            return;
        }

        BusiMcuHwcloudTemplateConference busiTemplateConference = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(busiConferenceAppointment.getTemplateId());
        Long conferenceNumber = busiTemplateConference.getConferenceNumber();
        if (conferenceNumber != null)
        {
            String contextKey = EncryptIdUtil.generateContextKey(busiTemplateConference.getId(), McuType.MCU_HWCLOUD);
            HwcloudConferenceContext cc = HwcloudConferenceContextCache.getInstance().get(contextKey);
            if (cc != null)
            {
                reset(busiConferenceAppointment);
                BeanFactory.getBean(BusiHwcloudConferenceServiceImpl.class).endConference(cc.getId(), EndReasonsType.AUTO_END,true,true);
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

    private void reset(BusiMcuHwcloudConferenceAppointment busiConferenceAppointment)
    {
        busiConferenceAppointment.setExtendMinutes(null);
        busiConferenceAppointment.setIsHangUp(null);
        busiConferenceAppointment.setIsStart(null);
        busiConferenceAppointment.setStartFailedReason(null);
        busiConferenceAppointment.setUpdateTime(new Date());
        busiMcuHwcloudConferenceAppointmentMapper.updateBusiMcuHwcloudConferenceAppointment(busiConferenceAppointment);
    }


    @Override
    public void afterPropertiesSet() throws Exception
    {
        this.start();
    }
}
