/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : UpdateByParticipant0.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.core.proxymethods
 * @author sinhy 
 * @since 2021-09-17 16:13
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.pm.attendeefield;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.constant.ConfigConstant;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.model.busi.attendee.*;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.fme.attendee.model.queue.AttendeeStatusLayoutMessageQueue;
import com.paradisecloud.fcm.fme.attendee.task.MuteStatusCheckTask;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.system.service.ISysConfigService;
import com.sinhy.spring.BeanFactory;
import org.springframework.util.ObjectUtils;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.AttendeeMixingStatus;
import com.paradisecloud.fcm.common.enumer.AttendeeVideoStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.fme.attendee.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.fme.attendee.model.queue.AttendeeStatusMessageQueue;
import com.paradisecloud.fcm.fme.attendee.model.updateprocessor.OtherAttendeeUpdateProcessor;
import com.paradisecloud.fcm.fme.attendee.model.updateprocessor.RegisteredAttendeeUpdateProcessor;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.message.AttendeeStatusMessage;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;
import com.sinhy.proxy.ProxyMethod;

public class UpdateByParticipant1 extends ProxyMethod
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-17 16:27 
     * @param method 
     */
    protected UpdateByParticipant1(Method method)
    {
        super(method);
    }

    public void updateByParticipant(FmeBridge fmeBridge, ConferenceContext conferenceContext, Participant participant, Attendee a)
    {
        synchronized (a)
        {

            try {
                boolean meetingJoinedOld = a.isMeetingJoined();
                a.resetUpdateMap();
                a.setCallId(participant.getCall());

                JSONObject rosterUpdate = participant.getRosterUpdate();
                if (rosterUpdate != null)
                {
                    if (rosterUpdate.getBoolean("audioMuted") != null)
                    {
                        a.setMixingStatus(rosterUpdate.getBoolean("audioMuted") ? AttendeeMixingStatus.NO.getValue() : AttendeeMixingStatus.YES.getValue());
                        MuteStatusCheckTask muteStatusCheckTask = new MuteStatusCheckTask(conferenceContext.getConferenceNumber(), 1000, conferenceContext);
                        BeanFactory.getBean(TaskService.class).addTask(muteStatusCheckTask);
                    }

                    if (rosterUpdate.getBoolean("videoMuted") != null)
                    {
                        a.setVideoStatus(rosterUpdate.getBoolean("videoMuted") ? AttendeeVideoStatus.NO.getValue() : AttendeeVideoStatus.YES.getValue());
                    }

                    if (rosterUpdate.containsKey("layout"))
                    {
                        a.setLayout(rosterUpdate.getString("layout"));
                    }

                    if (rosterUpdate.containsKey("importance"))
                    {
                        a.setImportance(rosterUpdate.getInteger("importance"));
                    }

                    Boolean activeSpeaker = rosterUpdate.getBoolean("activeSpeaker");
                    if (activeSpeaker != null && activeSpeaker)
                    {
                        Long lastActiveSpeakTime = a.getLastActiveSpeakTime();
                        if (lastActiveSpeakTime == null || System.currentTimeMillis() - lastActiveSpeakTime > 500)
                        {
                            a.setLastActiveSpeakTime(System.currentTimeMillis());
                            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + participant.getName() + "】正在发言！");
                            Map<String, Object> updateMap = new HashMap<>();
                            updateMap.put(a.getId(), activeSpeaker);
                            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_SPEAKER, updateMap);
                        }
                    }

                    String presentAttendId = conferenceContext.getPresentAttendeeId();
                    Boolean presenter = rosterUpdate.getBoolean("presenter");
                    if (presenter != null)
                    {
                        if (presenter)
                        {
                            a.setPresentStatus(YesOrNo.YES.getValue());
                            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + participant.getName() + "】正在共享屏幕！");
                            BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);
                            conferenceContext.setPresentAttendeeId(a.getId());
                        }
                        else
                        {
                            a.setPresentStatus(YesOrNo.NO.getValue());
                            if (a.getId().equals(presentAttendId)) {
                                conferenceContext.setPresentAttendeeId(null);
                            }
                        }
                    }
                }

                if (participant.getCallLeg() != null)
                {
                    if (participant.getCallLeg().getConfiguration() != null)
                    {
                        Boolean f = null;
                        if (!a.containsUpdateField("mixingStatus"))
                        {
                            f = participant.getCallLeg().getConfiguration().getRxAudioMute();
                            f = f == null ? true : f;
                            a.setMixingStatus(f ? AttendeeMixingStatus.NO.getValue() : AttendeeMixingStatus.YES.getValue());
                        }

                        if (!a.containsUpdateField("rxVideoMute"))
                        {
                            f = participant.getCallLeg().getConfiguration().getRxVideoMute();
                            f = f == null ? false : f;
                            a.setVideoStatus(f ? AttendeeVideoStatus.NO.getValue() : AttendeeVideoStatus.YES.getValue());
                        }
                    }

                    if (participant.getCallLeg().getStatus() != null && participant.getCallLeg().getStatus().getLayout() != null && !a.containsUpdateField("layout"))
                    {
                        a.setLayout(participant.getCallLeg().getStatus().getLayout());
                    }
                }

                if (participant.getConfiguration() != null && !a.containsUpdateField("importance"))
                {
                    a.setImportance(participant.getConfiguration().getImportance());
                }

                if (a.containsUpdateField("importance"))
                {
                    AttendeeImportance attendeeImportance = AttendeeImportance.convert(a.getImportance());
                    if (attendeeImportance != null)
                    {
                        try
                        {
                            attendeeImportance.processAttendeeWebsocketMessage(a);
                        }
                        catch (Throwable e)
                        {
                            logger.error("权重消息处理出错processAttendeeWebsocketMessage", e);
                        }
                    }
                }

                if (a instanceof TerminalAttendee || a instanceof FmeAttendee || a instanceof McuAttendee)
                {
                    new RegisteredAttendeeUpdateProcessor(participant, a, conferenceContext).process();
                }
                else
                {
                    new OtherAttendeeUpdateProcessor(fmeBridge, participant, a, conferenceContext).process();
                }

                if (a.isMeetingJoined() && !ObjectUtils.isEmpty(participant.getName()))
                {
                    Long terminalId = null;
                    String name = "";
                    if (a instanceof TerminalAttendee) {
                        terminalId = ((TerminalAttendee) a).getTerminalId();
                    } else if (a instanceof SelfCallAttendee) {
                        terminalId = ((SelfCallAttendee) a).getTerminalId();
                    }
                    if (terminalId != null) {
                        BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
                        if (busiTerminal != null) {
                            if (!ObjectUtils.isEmpty(busiTerminal.getName())) {
                                name += busiTerminal.getName();
                                if (!name.equals(participant.getName())) {
                                    name += "(" + participant.getName() + ")";
                                }
                            }
                        }
                    }
                    if (!ObjectUtils.isEmpty(name)) {
                        a.setName(name);
                    } else {
                        a.setName(participant.getName());
                    }
                }

//                if (a.getUpdateMap().size() > 1)
                {
                    Map<String, Object> updateMap = new HashMap<>(a.getUpdateMap());
                    updateMap.put("id", a.getId());
                    updateMap.put("onlineStatus", a.getOnlineStatus());
                    updateMap.put("meetingStatus", a.getMeetingStatus());
                    WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, updateMap);
                    AttendeeStatusMessageQueue.getInstance().put(new AttendeeStatusMessage(a));
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                    // 发送消息
                    String showEnable = BeanFactory.getBean(ISysConfigService.class).selectConfigByKey(ConfigConstant.CONFIG_KEY_SHOW_TERMINAL_SYSTEM_MESSAGE_ENABLE);
                    if (ConfigConstant.SHOW_TERMINAL_SYSTEM_MESSAGE_ENABLED.equals(showEnable)) {
                        Attendee attendee = a;
                        if (attendee != null) {
                            String message = "【" + attendee.getName() + "】";
                            if (attendee.isMeetingJoined()) {
                                if (!meetingJoinedOld) {
                                    message += "加入会议";
                                    BeanFactory.getBean(IAttendeeService.class).sendSystemMessage(conferenceContext.getId(), message, 5);
                                }
                            } else {
                                message += "离开会议";
                                BeanFactory.getBean(IAttendeeService.class).sendSystemMessage(conferenceContext.getId(), message, 5);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }

            AttendeeStatusLayoutMessageQueue.getInstance().put(new AttendeeStatusMessage(a));
            participant.setRosterUpdate(null);
        }
    }
}
