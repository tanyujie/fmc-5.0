package com.paradisecloud.fcm.fme.conference.task;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.ViewTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.ViewTemplateConference;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.AttendeeCountingStatistics;
import com.paradisecloud.fcm.fme.model.busi.attendee.McuAttendee;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.sinhy.spring.BeanFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckAttendeeOnlineStatusTask extends Task {

    public CheckAttendeeOnlineStatusTask(String id, long delayInMilliseconds) {
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
        Collection<ConferenceContext> conferenceContexts = ConferenceContextCache.getInstance().values();
        if (conferenceContexts != null) {
            for (ConferenceContext conferenceContext : conferenceContexts) {
                updateConference(conferenceContext);
            }
        }
    }

    private void updateConference(ConferenceContext conferenceContext) {
        try {
            if (!conferenceContext.isEnd()) {
                List<McuAttendee> mcuAttendees = conferenceContext.getMcuAttendees();
                for (McuAttendee mcuAttendee : mcuAttendees) {
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
                            WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
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
                            WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                            WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, mcuAttendee.getUpdateMap());
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);

                        }
                    }
                }
            }
        } catch (Throwable e) {
        }
    }
}
