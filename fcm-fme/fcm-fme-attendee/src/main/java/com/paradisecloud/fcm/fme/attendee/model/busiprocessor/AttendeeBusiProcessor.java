/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeProcessor.java
 * Package     : com.paradisecloud.fcm.fme.service.model
 * @author lilinhai 
 * @since 2021-02-09 10:58
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.busiprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.paradisecloud.fcm.fme.attendee.model.core.AttendeeInfo;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;

/**  
 * <pre>参会者业务处理器</pre>
 * @author lilinhai
 * @since 2021-02-09 10:58
 * @version V1.0  
 */
public abstract class AttendeeBusiProcessor
{
    
    protected Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * 子会议
     */
    protected ConferenceContext conferenceContext;
    
    protected Attendee targetAttendee;
    
    /**
     * 会议级联方会议桥工具对象
     */
    protected FmeBridge fmeBridge;
    
    protected String attendeeId;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-09 11:00 
     * @param contextKey
     * @param attendeeId 
     */
    protected AttendeeBusiProcessor(String contextKey, String attendeeId)
    {
        this.attendeeId = attendeeId;
        AttendeeInfo attendeeInfo = new AttendeeInfo(contextKey, attendeeId);
        this.conferenceContext = attendeeInfo.getAttendeeConferenceContext();
        this.targetAttendee = attendeeInfo.getAttendee();
        if (targetAttendee != null && targetAttendee.isMeetingJoined())
        {
            this.fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(targetAttendee);
            Assert.notNull(fmeBridge, "参会者不存在：" + targetAttendee.getName());
        }
        else
        {
            this.fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceContext(conferenceContext);
        }
    }
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-09 11:09
     * @param attendee
     */
    protected AttendeeBusiProcessor(Attendee attendee)
    {
        this.attendeeId = attendee.getId();
        this.conferenceContext = ConferenceContextCache.getInstance().get(attendee.getContextKey());
        this.targetAttendee = attendee;
        if (targetAttendee.isMeetingJoined())
        {
            this.fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(targetAttendee);
        }
        else
        {
            this.fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceContext(conferenceContext);
        }
    }
    
    public abstract void process();
    
    /**
     * <p>Get Method   :   conferenceContext ConferenceContext</p>
     * @return conferenceContext
     */
    public ConferenceContext getConferenceContext()
    {
        return conferenceContext;
    }

    /**
     * <p>Get Method   :   targetAttendee Attendee</p>
     * @return targetAttendee
     */
    public Attendee getTargetAttendee()
    {
        return targetAttendee;
    }
}
