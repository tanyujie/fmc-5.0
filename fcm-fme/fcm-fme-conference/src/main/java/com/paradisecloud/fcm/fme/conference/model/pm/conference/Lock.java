/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : Lock.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.conference
 * @author sinhy 
 * @since 2021-09-18 10:59
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.model.pm.conference;

import java.lang.reflect.Method;
import java.util.HashMap;

import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.fme.attendee.model.queue.AttendeeStatusMessageQueue;
import com.paradisecloud.fcm.fme.attendee.utils.ConferenceContextUtils;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.message.AttendeeStatusMessage;
import com.paradisecloud.fcm.fme.model.parambuilder.CallParamBuilder;
import com.sinhy.spring.BeanFactory;

public class Lock extends UpdateCall
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-18 11:00 
     * @param method 
     */
    protected Lock(Method method)
    {
        super(method);
    }
    
    public void lock(String conferenceId, Boolean locked)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        
        updateCall(contextKey, new CallParamBuilder().locked(locked).build());
        
        mainConferenceContext.setLocked(locked);
        
        // 解除锁定后，推送消息更新前端状态
        if (!locked)
        {
            ConferenceContextUtils.eachNonFmeAttendeeInConference(mainConferenceContext, (a) -> {
                if (a.isLocked())
                {
                    synchronized (a)
                    {
                        a.resetUpdateMap();
                        a.setLocked(locked);
                        if (a.getUpdateMap().size() > 1)
                        {
                            WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(mainConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(a.getUpdateMap()));
                            AttendeeStatusMessageQueue.getInstance().put(new AttendeeStatusMessage(a));
                        }
                    }
                }
            });
        }
        
        // 消息和参会者信息同步到主级会议
        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.CONFERENCE_LOCK, locked);
        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已" + (locked ? "" : "解除") + "锁定");

        BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(mainConferenceContext);
    }
}
