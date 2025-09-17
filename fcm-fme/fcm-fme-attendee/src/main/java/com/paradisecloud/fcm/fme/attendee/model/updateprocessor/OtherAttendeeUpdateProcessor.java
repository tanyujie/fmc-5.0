/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : OtherAttendeeUpdateProcessor.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai 
 * @since 2021-02-19 15:13
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.updateprocessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.constant.ConfigConstant;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.attendee.model.busiprocessor.MasterChangeAttendeeProcessor;
import com.paradisecloud.fcm.fme.model.busi.attendee.AttendeeCountingStatistics;
import com.paradisecloud.fcm.fme.model.busi.attendee.MinutesAttendee;
import com.paradisecloud.fcm.service.conference.cascade.CascadeInviteMessagePush;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.fme.model.busi.attendee.LiveBroadcastAttendee;
import com.paradisecloud.system.service.ISysConfigService;
import com.sinhy.spring.BeanFactory;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.fcm.common.enumer.AttendeeMeetingStatus;
import com.paradisecloud.fcm.common.enumer.ParticipantState;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.fme.attendee.utils.AttendeeMessagePusher;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;
import org.springframework.util.StringUtils;

/**  
 * <pre>其它参会者更新处理器</pre>
 * @author lilinhai
 * @since 2021-02-19 15:13
 * @version V1.0  
 */
public class OtherAttendeeUpdateProcessor extends AttendeeUpdateProcessor
{

    private FmeBridge fmeBridge;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-19 15:16 
     * @param participant
     * @param a
     * @param conferenceContext
     */
    public OtherAttendeeUpdateProcessor(FmeBridge fmeBridge, Participant participant, Attendee a, ConferenceContext conferenceContext)
    {
        super(participant, a, conferenceContext);
        this.fmeBridge = fmeBridge;
    }

    @Override
    public void process()
    {
        if (participant.is(ParticipantState.DISCONNECT))
        {
            if (attendee instanceof LiveBroadcastAttendee) {
                LiveBroadcastAttendee liveBroadcastAttendee = (LiveBroadcastAttendee) attendee;
                if (StringUtils.hasText(liveBroadcastAttendee.getRemotePartyOrigin())) {
                    if (conferenceContext.isStreaming())
                    {
                        conferenceContext.setStreaming(false);
                        // 向所有客户端通知会议的录制状态
                        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.STREAMING, false);
                        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已关闭直播");
                        BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);
                        return;
                    }
                }
            } else if (attendee instanceof MinutesAttendee) {
                MinutesAttendee minutesAttendee = (MinutesAttendee) attendee;
                if (StringUtils.hasText(minutesAttendee.getRemoteParty())) {
                    if (conferenceContext.isMinutes())
                    {
                        conferenceContext.setMinutes(false);
                        conferenceContext.setMinutesAttendee(null);
                        // 向所有客户端通知会议的录制状态
                        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MINUTES, false);
                        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已关闭纪要");
                        return;
                    }
                }
            }
            attendee.leaveMeeting();
            
            // 从缓存中移除
            conferenceContext.removeAttendeeById(attendee.getId());
            
            if (participant.getId() != null && fmeBridge != null)
            {
                fmeBridge.getDataCache().deleteParticipantByUuid(participant.getId());
            }
            
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("id", attendee.getId());
            updateMap.put("deptId", attendee.getDeptId());
            updateMap.put("mcuAttendee", attendee.isMcuAttendee());
            WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
            String reason = "【" + attendee.getName() + "】离会";
            if (participant.getRosterUpdate() != null && !ObjectUtils.isEmpty(participant.getRosterUpdate().getString("reason")))
            {
                reason += ("：" + participant.getRosterUpdate().getString("reason"));
            }
            WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, reason);
            
            if (attendee == conferenceContext.getMasterAttendee())
            {
                Map<String, Object> data = new HashMap<>();
                data.put("oldMasterAttendee", attendee);
                data.put("newMasterAttendee", null);
                WebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MASTER_CHANGED, data);
                
                StringBuilder messageTip = new StringBuilder();
                messageTip.append("主会场已离会【").append(attendee.getName()).append("】");
                WebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                
                conferenceContext.clearMasterAttendee();
            }
            LoggerFactory.getLogger(getClass()).info("推送删除与会者消息成功：" + attendee.getUpdateMap());
            BeanFactory.getBean(IMqttService.class).sendLeftConferenceToPushTargetTerminal(conferenceContext, attendee);
            // 发送消息
            String showEnable = BeanFactory.getBean(ISysConfigService.class).selectConfigByKey(ConfigConstant.CONFIG_KEY_SHOW_TERMINAL_SYSTEM_MESSAGE_ENABLE);
            if (ConfigConstant.SHOW_TERMINAL_SYSTEM_MESSAGE_ENABLED.equals(showEnable)) {
                if (attendee != null) {
                    String message = "【" + attendee.getName() + "】";
                    if (attendee.isMeetingJoined()) {
                        message += "加入会议";
                    } else {
                        message += "离开会议";
                    }
                    BeanFactory.getBean(IAttendeeService.class).sendSystemMessage(conferenceContext.getId(), message, 5);
                }
            }
        }
        else
        {
            if (attendee instanceof LiveBroadcastAttendee) {
                LiveBroadcastAttendee liveBroadcastAttendee = (LiveBroadcastAttendee) attendee;
                if (StringUtils.hasText(liveBroadcastAttendee.getRemotePartyOrigin())) {
                    if (!conferenceContext.isStreaming())
                    {
                        conferenceContext.setStreaming(true);
                        conferenceContext.setStreamingAttendee(attendee);

                        // 向所有客户端通知会议的录制状态
                        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.STREAMING, true);
                        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已开启直播");

                        attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                        attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
                        BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);
                        return;
                    }
                }
            } else if (attendee instanceof MinutesAttendee) {
                MinutesAttendee minutesAttendee = (MinutesAttendee) attendee;
                if (StringUtils.hasText(minutesAttendee.getRemoteParty())) {
                    if (!conferenceContext.isMinutes())
                    {
                        conferenceContext.setMinutes(true);
                        conferenceContext.setMinutesAttendee(attendee);

                        // 向所有客户端通知会议的录制状态
                        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MINUTES, true);
                        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已开启纪要");

                        attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                        attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
                        return;
                    }
                }
            }
            attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
            attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
            AttendeeMessagePusher.getInstance().pushMixingStatusMessage(attendee, conferenceContext);
            AttendeeMessagePusher.getInstance().pushMeetingJoinedMessage(attendee, participant, conferenceContext);
            AttendeeMessagePusher.getInstance().pushOnlineMessage(attendee, conferenceContext);
            BeanFactory.getBean(IMqttService.class).sendJoinConferenceToPushTargetTerminal(conferenceContext, attendee);
            String remoteParty = attendee.getRemoteParty();
            if (remoteParty.contains(":")) {
                remoteParty = remoteParty.substring(0, remoteParty.indexOf(":"));
            }
            if (remoteParty.contains(";")) {
                remoteParty = remoteParty.substring(0, remoteParty.indexOf(";"));
            }
            if (remoteParty.equals(conferenceContext.getUpCascadeRemoteParty()) ||
                    (conferenceContext.getUpCascadeRemoteParty() != null && conferenceContext.getUpCascadeRemoteParty().contains(remoteParty))||
                    Objects.equals(conferenceContext.getTencentRemoteParty(),remoteParty)) {
                new MasterChangeAttendeeProcessor(conferenceContext.getContextKey(), attendee.getId()).process();
                if (Objects.equals(conferenceContext.getTencentRemoteParty(), remoteParty)) {
                    String upCascadeConferenceId = conferenceContext.getUpCascadeConferenceId();
                    CascadeInviteMessagePush.push(upCascadeConferenceId, conferenceContext.getConferenceRemoteParty());
                }
            }
        }
    }
    

    
}
