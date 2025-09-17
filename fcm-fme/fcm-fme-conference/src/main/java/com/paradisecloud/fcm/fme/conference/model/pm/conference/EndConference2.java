/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : EndConference2.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.conference
 * @author sinhy 
 * @since 2021-09-18 11:34
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.model.pm.conference;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeWebsocketMessageService;
import com.paradisecloud.fcm.fme.conference.task.RemoveDuplicatesModeConferenceHistoryTask;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.im.IMService;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberService;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.fcm.dao.mapper.BusiConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiTemplateConference;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.fme.apiservice.interfaces.ICoSpaceService;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.fme.attendee.model.busiprocessor.HangUpAttendeeProcessor;
import com.paradisecloud.fcm.fme.attendee.utils.ConferenceContextUtils;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceAppointmentService;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiHistoryConferenceService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.FmeAttendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.TerminalAttendee;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.sinhy.proxy.ProxyMethod;
import com.sinhy.spring.BeanFactory;

public class EndConference2 extends ProxyMethod
{
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-18 11:34  
     */
    public EndConference2(Method method)
    {
        super(method);
    }
    
    public void endConference(String contextKey, int endReasonsType)
    {
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null)
        {
            try
            {
                //录制会议处理
                if (conferenceContext.isRecorded()){
                    BeanFactory.getBean(IAttendeeWebsocketMessageService.class).updateBusiRecords(false, conferenceContext.getConferenceNumber(), conferenceContext);
                }
                // 设置结束状态
                conferenceContext.setEnd(true);
                conferenceContext.setEndTime(new Date());
                if (conferenceContext.getAttendeeOperation() != null)
                {
                    try
                    {
                        conferenceContext.getAttendeeOperation().cancel(null);
                    }
                    catch (Throwable e)
                    {
                        logger.error("结束会议时取消会议当前操作失败", e);
                    }
                }
                
                // 保存历史记录
                conferenceContext.getHistoryConference().setEndReasonsType(endReasonsType);
                BeanFactory.getBean(IBusiHistoryConferenceService.class).saveHistory(conferenceContext.getHistoryConference(), conferenceContext);
                // 会议结束推送mqtt
                try
                {
                    pushEndMessageToMqtt(conferenceContext.getConferenceNumber(), conferenceContext);
                }
                catch (Exception e)
                {
                    logger.error("结束会议时取消会议当前操作失败", e);
                }
                
                ConferenceContextCache.getInstance().deleteCascadeConferenceContexts(conferenceContext.getConferenceNumber());
                ConferenceContext upConferenceContext = ConferenceContextCache.getInstance().getUpConferenceContext(conferenceContext);
                if (upConferenceContext != null && upConferenceContext.getCascade() != null)
                {
                    FmeAttendee fmeAttendee = upConferenceContext.getCascade().remove(conferenceContext.getConferenceNumber());
                    if (fmeAttendee != null)
                    {
                        try
                        {
                            new HangUpAttendeeProcessor(conferenceContext.getConferenceNumber(), fmeAttendee.getId()).process();
                        }
                        catch (Throwable e)
                        {
                            logger.error("解除级联绑定失败", e);
                        }
                    }
                }

                
                BusiTemplateConference btc = BeanFactory.getBean(BusiTemplateConferenceMapper.class).selectBusiTemplateConferenceById(conferenceContext.getTemplateConferenceId());
                BusiConferenceAppointment busiConferenceAppointment = conferenceContext.getConferenceAppointment();
                if (busiConferenceAppointment != null)
                {
                    AppointmentConferenceRepeatRate appointmentConferenceRepeatRate = AppointmentConferenceRepeatRate.convert(busiConferenceAppointment.getRepeatRate());
                    if (appointmentConferenceRepeatRate == AppointmentConferenceRepeatRate.CUSTOM)
                    {
                        if (YesOrNo.convert(btc.getIsAutoCreateConferenceNumber()) == YesOrNo.YES)
                        {
                            busiConferenceAppointment.setIsStart(null);
                            BeanFactory.getBean(BusiConferenceAppointmentMapper.class).updateBusiConferenceAppointment(busiConferenceAppointment);
                            BeanFactory.getBean(IBusiConferenceAppointmentService.class).deleteBusiConferenceAppointmentById(busiConferenceAppointment.getId());
                        }
                    }
//                    else
//                    {
                        // 只回收coSpace，不回收生成的会议号
                        BeanFactory.getBean(ICoSpaceService.class).recoveryCospace(conferenceContext.getDeptId(), conferenceContext.getConferenceNumber());
//                    }
                    if (btc.getStreamingEnabled() == StreamingEnabledType.CLOUDS.getValue()) {
                        BusiTemplateConference busiTemplateConference = BeanFactory.getBean(BusiTemplateConferenceMapper.class).selectBusiTemplateConferenceById(conferenceContext.getTemplateConferenceId());
                        if (busiTemplateConference != null) {
                            busiTemplateConference.setStreamUrl(null);
                            BeanFactory.getBean(BusiTemplateConferenceMapper.class).updateBusiTemplateConference(busiTemplateConference);
                        }
                    }
                }
                else
                {
                    if (btc.getIsAutoCreateStreamUrl() == 1) {
                        BusiTemplateConference busiTemplateConference = BeanFactory.getBean(BusiTemplateConferenceMapper.class).selectBusiTemplateConferenceById(conferenceContext.getTemplateConferenceId());
                        busiTemplateConference.setStreamUrl(null);
                        BeanFactory.getBean(BusiTemplateConferenceMapper.class).updateBusiTemplateConference(busiTemplateConference);
                    }
                    if (YesOrNo.convert(btc.getIsAutoCreateConferenceNumber()) == YesOrNo.YES)
                    {
                        btc.setConferenceNumber(null);
                        BeanFactory.getBean(BusiTemplateConferenceMapper.class).updateBusiTemplateConference(btc);
                        BeanFactory.getBean(IBusiConferenceNumberService.class).deleteBusiConferenceNumberById(Long.parseLong(conferenceContext.getConferenceNumber()));
                        BeanFactory.getBean(ICoSpaceService.class).recoveryCospace(conferenceContext.getDeptId(), conferenceContext.getConferenceNumber());
                    }
                }
                //相同会议场景过滤任务
                TaskService taskService = BeanFactory.getBean(TaskService.class);
                RemoveDuplicatesModeConferenceHistoryTask removeDuplicatesModeConferenceHistoryTask = new RemoveDuplicatesModeConferenceHistoryTask(McuType.FME.getCode(), 3000);
                taskService.addTask(removeDuplicatesModeConferenceHistoryTask);
                //解散IM群组
                try {
                    IMService imService = BeanFactory.getBean(IMService.class);
                    Object imGroupIdObj = btc.getBusinessProperties().get("imGroupId");
                    if (imGroupIdObj != null) {
                        String imGroupId = (String) imGroupIdObj;
                        imService.destroyGroup(imGroupId);
                    }
                } catch (Exception e) {
                }
            }
            catch (Throwable e)
            {
                logger.error("结束会议失败: " + conferenceContext.getConferenceNumber(), e);
            }
            finally 
            {
                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_ENDED, "会议已结束");
                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已结束");
            }

            ConferenceContextCache.getInstance().remove(contextKey);

            // 清理缓存数据
            conferenceContext.clear();
        }
    }

    /**
     * <pre>会议结束推送mqtt</pre>
     * @author sinhy
     * @since 2021-12-13 15:03 
     * @param conferenceNumber
     * @param conferenceContext void
     */
    private void pushEndMessageToMqtt(String conferenceNumber, ConferenceContext conferenceContext)
    {
        List<BaseAttendee> mqttJoinTerminals = new ArrayList<>();
        ConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
                if (a instanceof TerminalAttendee)
                {
                    TerminalAttendee ta = (TerminalAttendee) a;
                    BusiTerminal bt = TerminalCache.getInstance().get(ta.getTerminalId());
                    if (!ObjectUtils.isEmpty(bt.getSn()))
                    {
                        mqttJoinTerminals.add(ta);
                    }
                }
        });
        
        BeanFactory.getBean(IMqttService.class).endConference(conferenceNumber, mqttJoinTerminals, new ArrayList<>(conferenceContext.getLiveTerminals()),conferenceContext);
    }
    
    /**
     * <pre>发送参会者例会前端通知和更新消息</pre>
     * @author lilinhai
     * @since 2021-02-07 15:51 
     * @param conferenceContext
     */
    @SuppressWarnings("unused")
    private void sendAttendeeOutUpdateMessage(ConferenceContext conferenceContext)
    {
        sendAttendeeOutUpdateMessage(conferenceContext, conferenceContext.getMasterAttendee());
        for (Attendee attendee : conferenceContext.getAttendees())
        {
            sendAttendeeOutUpdateMessage(conferenceContext, attendee);
        }
        
        conferenceContext.getMasterAttendees().forEach((a) -> {
            sendAttendeeOutUpdateMessage(conferenceContext, a);
        });
        
        conferenceContext.getCascadeAttendeesMap().forEach((deptId, as) -> {
            as.forEach((a) -> {
                sendAttendeeOutUpdateMessage(conferenceContext, a);
            });
        });
        
        if (!ObjectUtils.isEmpty(conferenceContext.getFmeAttendees()))
        {
            for (Attendee attendee : conferenceContext.getFmeAttendees())
            {
                sendAttendeeOutUpdateMessage(conferenceContext, attendee);
            }
        }
    }

    /**
     * <pre>发送参会者例会前端通知和更新消息</pre>
     * @author lilinhai
     * @since 2021-02-07 15:50 
     * @param conferenceContext
     * @param attendee void
     */
    private void sendAttendeeOutUpdateMessage(ConferenceContext conferenceContext, Attendee attendee)
    {
        if (attendee != null && attendee.getConferenceNumber().equals(conferenceContext.getConferenceNumber()))
        {
            attendee.resetUpdateMap();
            attendee.setMeetingStatus(AttendeeMeetingStatus.OUT.getValue());
            WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
            
            if (attendee.containsUpdateField("meetingStatus"))
            {
                StringBuilder messageTip = new StringBuilder();
                messageTip.append("【").append(attendee.getName()).append("】离会");
                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
            }
        }
    }
}
