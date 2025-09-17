package com.paradisecloud.fcm.fme.model.websocket.subscription;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 订阅元素
 *
 * @author zt1994 2019/8/30 15:31
 */
@Getter
@Setter
@ToString
public class SubscriptionElements
{
    
    /**
     * callInfo 订阅元素
     */
    public static final String[] CALL_INFO_ELEMENTS = {"name", "participants", "distributedInstances", "recording", "endpointRecording", "streaming", "lockState", "callType", "callCorrelator",
            "joinAudioMuteOverride"};
    
    /**
     * callRoster 订阅元素
     */
    public static final String[] CALL_ROSTER_ELEMENTS = {"name", "uri", "state", "direction", "audioMuted", "videoMuted", "importance", "layout",
            /*"activeSpeaker",*/
            "presenter", "endpointRecording"};
    
    /**
     * calls 订阅元素
     */
    public static final String[] CALLS_ELEMENTS = {"name", "participants", "recording", "endpointRecording", "streaming", "lockState", "callType", "callCorrelator"};
    
    /**
     * calls 心跳检测订阅元素
     */
    public static final String[] CALLS_HEART_CHECK = {"name"};
    
}
