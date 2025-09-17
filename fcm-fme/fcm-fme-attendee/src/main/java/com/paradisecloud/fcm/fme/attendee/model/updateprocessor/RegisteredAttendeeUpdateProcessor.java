/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeUpdateProcessor.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai 
 * @since 2021-02-19 15:05
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.updateprocessor;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.AttendeeMeetingStatus;
import com.paradisecloud.fcm.common.enumer.ParticipantState;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.ViewTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.ViewTemplateConference;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.model.busi.attendee.*;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.fme.attendee.model.attendeemessage.AttendeeMessageQueue;
import com.paradisecloud.fcm.fme.attendee.model.attendeemessage.CallFailedAttendeeMessage;
import com.paradisecloud.fcm.fme.attendee.model.busiprocessor.MixingAttendeeProcessor;
import com.paradisecloud.fcm.fme.attendee.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.fme.attendee.utils.AttendeeMessagePusher;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;
import com.paradisecloud.fcm.fme.model.parambuilder.ParticipantParamBuilder;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.spring.BeanFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**  
 * <pre>参会者更新处理器</pre>
 * @author lilinhai
 * @since 2021-02-19 15:05
 * @version V1.0  
 */
public class RegisteredAttendeeUpdateProcessor extends AttendeeUpdateProcessor
{
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-19 15:11 
     * @param participant
     * @param a
     * @param conferenceContext
     */
    public RegisteredAttendeeUpdateProcessor(Participant participant, Attendee a, ConferenceContext conferenceContext)
    {
        super(participant, a, conferenceContext);
    }
    
    public void process()
    {
        if (participant.is(ParticipantState.CONNECTED))
        {
            attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
            
            // 呼入成功后，重置主动挂断
            attendee.setHangUp(false);
            attendee.setCallRequestSentTime(null);
        }
        else
        {
            attendee.leaveMeeting();
            
            // 离会后，若是终端没有主动挂断，则主动拉会
            if (!(attendee instanceof UpFmeAttendee))
            {
                AttendeeMessageQueue.getInstance().put(new CallFailedAttendeeMessage(attendee, 3));
            }
        }
        
        // 已配置的终端与会者更新
        if (attendee instanceof TerminalAttendee)
        {
            processTerminalAttendeeUpdate();
        }
        // FME类型的与会者
        else if (attendee instanceof FmeAttendee)
        {
            processFmeAttendee();
        }
        // MCU类型的与会者
        else if (attendee instanceof McuAttendee)
        {
            processMcuAttendeeUpdate();
        }
    }

    /**
     * <pre>处理FME参会者</pre>
     * @author Administrator
     * @since 2021-03-06 23:16  void
     */
    private void processFmeAttendee()
    {
        if (attendee instanceof UpFmeAttendee)
        {
            processUpFmeAttendee();
        }
        else 
        {
            FmeAttendee fmeAttendee = (FmeAttendee) attendee;
            fmeAttendee.setName(SysDeptCache.getInstance().get(fmeAttendee.getCascadeDeptId()).getDeptName());
            fmeAttendee.setOnlineStatus(FmeBridgeCache.getInstance().get(fmeAttendee.getFmeId()).getBusiFme().getStatus());
            if (fmeAttendee.getMeetingStatus() == AttendeeMeetingStatus.OUT.getValue())
            {
                conferenceContext.removeFmeAttendee(fmeAttendee.getCascadeConferenceNumber());
                
                StringBuilder messageTip = new StringBuilder();
                messageTip.append("【").append(fmeAttendee.getName()).append("】").append("集群已断开");
                
                // 消息和参会者信息同步到主级会议
                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, attendee.isHangUp() ? WebsocketMessageType.MESSAGE_TIP : WebsocketMessageType.MESSAGE_ERROR, messageTip);
            }
            else
            {
                // 绑定下级级联关系
                if (conferenceContext.getCascade() == null || conferenceContext.getCascade().get(fmeAttendee.getCascadeConferenceNumber()) == null)
                {
                    conferenceContext.putFmeAttendee(fmeAttendee);
                    
                    StringBuilder messageTip = new StringBuilder();
                    messageTip.append("【").append(fmeAttendee.getName()).append("】").append("集群已连接");
                    
                    // 消息和参会者信息同步到主级会议
                    WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                }
            }
        }
    }

    /**
     * <pre>处理上级级联FME参会</pre>
     * @author Administrator
     * @since 2021-03-06 23:16  void
     */
    private void processUpFmeAttendee()
    {
        UpFmeAttendee upFmeAttendee = (UpFmeAttendee) attendee;
        upFmeAttendee.setName(SysDeptCache.getInstance().get(upFmeAttendee.getCascadeDeptId()).getDeptName());
        upFmeAttendee.setOnlineStatus(FmeBridgeCache.getInstance().get(upFmeAttendee.getFmeId()).getBusiFme().getStatus());
        if (attendee.getMeetingStatus() == AttendeeMeetingStatus.IN.getValue())
        {
            if (conferenceContext.getUpCascade() == null || conferenceContext.getUpCascade().get(upFmeAttendee.getCascadeConferenceNumber()) == null)
            {
                // 绑定上级级联关系
                conferenceContext.putUpFmeAttendee(upFmeAttendee);
                
                StringBuilder messageTip = new StringBuilder();
                messageTip.append("【").append(upFmeAttendee.getName()).append("】").append("集群已连接");
                
                // 消息和参会者信息同步到主级会议
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                
                try
                {
                    FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(upFmeAttendee);
                    if (AttendeeImportance.UP_FME.getStartValue() != upFmeAttendee.getImportance())
                    {
                        fmeBridge.getParticipantInvoker().updateParticipant(upFmeAttendee.getParticipantUuid()
                                , new ParticipantParamBuilder().importance(AttendeeImportance.UP_FME.getStartValue()).build());
                    }
                    
                    // 设置开音
                    new MixingAttendeeProcessor(attendee, false).process();
                }
                catch (Throwable e)
                {
                    logger.error("AttendeeImportance.MAIN_FME更新失败", e);
                }
            }
        }
        // 如果级联主会议结束
        else if (attendee.getMeetingStatus() == AttendeeMeetingStatus.OUT.getValue())
        {
            upFmeAttendee.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
            conferenceContext.removeUpFmeAttendee(upFmeAttendee.getCascadeConferenceNumber());
            
            StringBuilder messageTip = new StringBuilder();
            messageTip.append("【").append(upFmeAttendee.getName()).append("】").append("集群已断开");
            
            // 消息和参会者信息同步到主级会议
            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, attendee.isHangUp() ? WebsocketMessageType.MESSAGE_TIP : WebsocketMessageType.MESSAGE_ERROR, messageTip);
        }
    }

    /**
     * <pre> 已配置的终端与会者更新</pre>
     * @author lilinhai
     * @since 2021-03-03 15:51  void
     */
    private void processTerminalAttendeeUpdate()
    {
        TerminalAttendee terminalAttendee = (TerminalAttendee) attendee;
        BusiTerminal bt = TerminalCache.getInstance().get(terminalAttendee.getTerminalId());
        terminalAttendee.setTerminalType(bt.getType());
        terminalAttendee.setTerminalTypeName(TerminalType.convert(bt.getType()).getDisplayName());
        if (attendee.isMeetingJoined())
        {
            terminalAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
            BeanFactory.getBean(IMqttService.class).sendJoinConferenceToPushTargetTerminal(conferenceContext, attendee);
        }
        else
        {
            terminalAttendee.setOnlineStatus(bt.getOnlineStatus());
            terminalAttendee.setName(bt.getName());
            BeanFactory.getBean(IMqttService.class).sendLeftConferenceToPushTargetTerminal(conferenceContext, attendee);
        }
        
        // 消息推送
        AttendeeMessagePusher.getInstance().pushMixingStatusMessage(attendee, conferenceContext);
        AttendeeMessagePusher.getInstance().pushMeetingJoinedMessage(attendee, participant, conferenceContext);
        AttendeeMessagePusher.getInstance().pushOnlineMessage(attendee, conferenceContext);
    }

    /**
     * <pre> 已配置的MCU与会者更新</pre>
     * @author lilinhai
     * @since 2021-03-03 15:51  void
     */
    private void processMcuAttendeeUpdate()
    {
        McuAttendee mcuAttendee = (McuAttendee) attendee;

        String contextKey = EncryptIdUtil.parasToContextKey(mcuAttendee.getId());
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (baseConferenceContext != null)
        {
            String remoteParty = baseConferenceContext.getTenantId() + baseConferenceContext.getConferenceNumber() + "@" + baseConferenceContext.getMcuCallIp();
            if (baseConferenceContext.getMcuCallPort() != null && baseConferenceContext.getMcuCallPort() != 5060) {
                remoteParty += ":" + baseConferenceContext.getMcuCallPort();
            }
            mcuAttendee.setRemoteParty(remoteParty);
            mcuAttendee.setIp(baseConferenceContext.getMcuCallIp());
            mcuAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
        }
        else
        {
            mcuAttendee.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
        }
        if (baseConferenceContext == null) {
            ViewTemplateConferenceMapper viewTemplateConferenceMapper = BeanFactory.getBean(ViewTemplateConferenceMapper.class);
            ViewTemplateConference viewTemplateConferenceConCascade = new ViewTemplateConference();
            viewTemplateConferenceConCascade.setId(mcuAttendee.getCascadeTemplateId());
            viewTemplateConferenceConCascade.setMcuType(mcuAttendee.getCascadeMcuType());
            viewTemplateConferenceConCascade.setUpCascadeId(conferenceContext.getTemplateConferenceId());
            viewTemplateConferenceConCascade.setUpCascadeMcuType(conferenceContext.getMcuType());
            List<ViewTemplateConference> viewTemplateConferenceList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceConCascade);
            if (viewTemplateConferenceList == null || viewTemplateConferenceList.size() == 0) {
                conferenceContext.removeMcuAttendee(mcuAttendee);
                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("id", mcuAttendee.getId());
                updateMap.put("deptId", mcuAttendee.getDeptId());
                updateMap.put("mcuAttendee", mcuAttendee.isMcuAttendee());
                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                return;
            }
        }

        // 消息推送
        AttendeeMessagePusher.getInstance().pushMixingStatusMessage(attendee, conferenceContext);
        AttendeeMessagePusher.getInstance().pushMeetingJoinedMessage(attendee, participant, conferenceContext);
        AttendeeMessagePusher.getInstance().pushOnlineMessage(attendee, conferenceContext);
    }
}
