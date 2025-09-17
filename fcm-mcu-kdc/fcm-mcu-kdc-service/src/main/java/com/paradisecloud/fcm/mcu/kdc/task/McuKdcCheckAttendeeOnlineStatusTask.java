package com.paradisecloud.fcm.mcu.kdc.task;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.ViewTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.ViewTemplateConference;
import com.paradisecloud.fcm.mcu.kdc.cache.AttendeeCountingStatistics;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcConferenceContextCache;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.McuAttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.TerminalAttendeeForMcuKdc;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.sinhy.spring.BeanFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class McuKdcCheckAttendeeOnlineStatusTask extends DelayTask {

    public McuKdcCheckAttendeeOnlineStatusTask(String id, long delayInMilliseconds) {
        super("check_attendee_o_s_" + id, delayInMilliseconds);
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        Collection<McuKdcConferenceContext> conferenceContexts = McuKdcConferenceContextCache.getInstance().values();
        if (conferenceContexts != null) {
            for (McuKdcConferenceContext conferenceContext : conferenceContexts) {
                updateConference(conferenceContext);
            }
        }
    }

    private void updateConference(McuKdcConferenceContext conferenceContext) {
        try {
            if (!conferenceContext.isEnd()) {
                Map<Long, TerminalAttendeeForMcuKdc> terminalAttendeeMap = conferenceContext.getTerminalAttendeeMap();
                for (TerminalAttendeeForMcuKdc terminalAttendee : terminalAttendeeMap.values()) {
                    if (terminalAttendee.isMeetingJoined()) {
                        continue;
                    }
                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendee.getTerminalId());
                    if (terminalAttendee.getOnlineStatus() == busiTerminal.getOnlineStatus()) {
                        continue;
                    }
                    synchronized (terminalAttendee) {
                        terminalAttendee.resetUpdateMap();

                        // 同步终端在线状态
                        terminalAttendee.setOnlineStatus(busiTerminal.getOnlineStatus());

                        // 在线消息
                        if (terminalAttendee.containsUpdateField("onlineStatus")) {
                            TerminalOnlineStatus onlineStatus = TerminalOnlineStatus.convert((int) terminalAttendee.getUpdateMap().get("onlineStatus"));
                            StringBuilder messageTip = new StringBuilder();
                            messageTip.append("【").append(terminalAttendee.getName()).append("】").append(onlineStatus.getName());

                            // 消息和参会者信息同步到主级会议
                            McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                            Map<String, Object> updateMap = new HashMap<>(terminalAttendee.getUpdateMap());
                            updateMap.put("ip", terminalAttendee.getIp());
                            updateMap.put("ipNew", terminalAttendee.getIpNew());
                            McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, updateMap);

                        }
                    }
                }
                List<McuAttendeeForMcuKdc> mcuAttendees = conferenceContext.getMcuAttendees();
                for (McuAttendeeForMcuKdc mcuAttendee : mcuAttendees) {
                    if (mcuAttendee.isMeetingJoined()) {
                        continue;
                    }
                    int newOnlineStatus = TerminalOnlineStatus.OFFLINE.getValue();
                    String contextKey = EncryptIdUtil.parasToContextKey(mcuAttendee.getId());
                    BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                    if (baseConferenceContext != null) {
                        newOnlineStatus = TerminalOnlineStatus.ONLINE.getValue();
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
                            McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                            McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                            continue;
                        }
                    }
                    if (mcuAttendee.getOnlineStatus() == newOnlineStatus) {
                        continue;
                    }
                    synchronized (mcuAttendee) {
                        mcuAttendee.resetUpdateMap();

                        // 同步终端在线状态
                        mcuAttendee.setOnlineStatus(newOnlineStatus);

                        // 在线消息
                        if (mcuAttendee.containsUpdateField("onlineStatus")) {
                            TerminalOnlineStatus onlineStatus = TerminalOnlineStatus.convert((int) mcuAttendee.getUpdateMap().get("onlineStatus"));
                            StringBuilder messageTip = new StringBuilder();
                            messageTip.append("【").append(mcuAttendee.getName()).append("】").append(onlineStatus.getName());

                            // 消息和参会者信息同步到主级会议
                            McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                            McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, mcuAttendee.getUpdateMap());

                        }
                    }
                }
            }
        } catch (Throwable e) {
        }
    }
}
