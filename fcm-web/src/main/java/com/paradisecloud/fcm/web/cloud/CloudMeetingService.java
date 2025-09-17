package com.paradisecloud.fcm.web.cloud;

import com.paradisecloud.fcm.service.conference.BaseConferenceContext;

/**
 * @author nj
 * @date 2024/7/16 9:43
 */
public interface CloudMeetingService {
    Object queryMeetingResources(CloudMeetingParams meetingParams);
    BaseConferenceContext createMeeting(String name,int duration);
}
