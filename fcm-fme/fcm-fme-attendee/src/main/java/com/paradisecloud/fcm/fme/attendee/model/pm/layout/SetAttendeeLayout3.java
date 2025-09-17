/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : SetAttendeeLayout3.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.pm.layout
 * @author sinhy 
 * @since 2021-09-17 21:35
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.pm.layout;

import java.lang.reflect.Method;

import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.fme.attendee.model.core.AttendeeInfo;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.parambuilder.ParticipantParamBuilder;

public class SetAttendeeLayout3 extends SetAttendeeLayout2
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-17 21:35 
     * @param method 
     */
    protected SetAttendeeLayout3(Method method)
    {
        super(method);
    }
    
    public void setAttendeeLayout(String conferenceId, String attendeeId, String layout)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext cc = ConferenceContextCache.getInstance().get(contextKey);
        if (cc != null)
        {
            ParticipantParamBuilder participantParamBuilder = new ParticipantParamBuilder();
            participantParamBuilder.defaultLayout(layout).chosenLayout(layout);
            
            AttendeeInfo ai = new AttendeeInfo(contextKey, attendeeId);
            if (ai.getAttendee() == null)
            {
                StringBuilder messageTip = new StringBuilder();
                messageTip.append("修改单个参会布局失败，找不到参会者：" + attendeeId);
                logger.error(messageTip.toString());
                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(ConferenceContextCache.getInstance().get(contextKey), WebsocketMessageType.MESSAGE_ERROR, messageTip);
                return;
            }
            
            setAttendeeLayout(ai.getAttendee(), layout);
        }
        else
        {
            logger.error("找不到会议：" + contextKey);
        }
    }
}
