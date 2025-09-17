package com.paradisecloud.fcm.service.conference.cascade;

import com.paradisecloud.fcm.common.enumer.AttendeeMeetingStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.cache.BaseWebSocketMessagePusher;

/**
 * @author nj
 * @date 2024/2/4 15:50
 */
public class CascadeInviteMessagePush {

    public static void push(String conferenceId,String remote){
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        BaseAttendee attendeeByRemoteParty = baseConferenceContext.getAttendeeByRemoteParty(remote);
        if(attendeeByRemoteParty!=null){
            attendeeByRemoteParty.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
            BaseWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(baseConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeByRemoteParty);
        }
    }

}
