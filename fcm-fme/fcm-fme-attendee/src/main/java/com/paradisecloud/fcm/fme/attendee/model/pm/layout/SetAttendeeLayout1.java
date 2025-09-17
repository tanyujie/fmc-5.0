/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : SetAttendeeLayout1.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.pm.layout
 * @author sinhy 
 * @since 2021-09-17 21:27
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.pm.layout;

import java.lang.reflect.Method;

import com.paradisecloud.fcm.fme.attendee.interfaces.ICallegService;
import com.paradisecloud.fcm.fme.attendee.model.enumer.AttendeeLayoutSetMode;
import com.paradisecloud.fcm.fme.attendee.utils.ConferenceContextUtils;
import com.paradisecloud.fcm.fme.cache.utils.FcmThreadPool;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.cms.callleg.CallLeg;
import com.sinhy.spring.BeanFactory;

public class SetAttendeeLayout1 extends SetAttendeeLayout2
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-17 21:27 
     * @param method 
     */
    protected SetAttendeeLayout1(Method method)
    {
        super(method);
    }
    
    public void setAttendeeLayout(ConferenceContext cc, String layout, AttendeeLayoutSetMode attendeeLayoutSetMode)
    {
        try
        {
            if (attendeeLayoutSetMode == AttendeeLayoutSetMode.MASTER)
            {
                setAttendeeLayout(cc.getMasterAttendee(), layout);
            }
            else if (attendeeLayoutSetMode == AttendeeLayoutSetMode.SUB || attendeeLayoutSetMode == AttendeeLayoutSetMode.ALL)
            {
                ConferenceContextUtils.eachNonFmeAttendeeInConference(cc, (a) -> {
                    if (a != cc.getMasterAttendee() || attendeeLayoutSetMode == AttendeeLayoutSetMode.ALL)
                    {
                        if (a.isMeetingJoined() && !a.getFixedSettings().getChosenLayout().isFixed())
                        {
                            //直播录制不跟随设置分屏
                            if (a.isLiveBroadcast()) {
                                if (cc.isStreamingCustomsLayout()) {
                                    return;
                                }
                            }
                            if (a.isRecorder()) {
                                if (cc.isRecordingCustomsLayout()) {
                                    return;
                                }
                            }
                            CallLeg callLeg = BeanFactory.getBean(ICallegService.class).getCallLeg(a);
                            if (callLeg != null && !layoutEquals(callLeg, layout))
 //                           if (callLeg != null)
                            {
                                FcmThreadPool.exec(()->{
                                    setAttendeeLayout(a, layout, callLeg);
                                });
                            }
                        }
                    }
                });
                if (cc.getStreamingAttendee() != null) {
                    Attendee attendee = cc.getStreamingAttendee();
                    if (!cc.isStreamingCustomsLayout()) {
                        CallLeg callLeg = BeanFactory.getBean(ICallegService.class).getCallLeg(attendee);
                        if (callLeg != null && !layoutEquals(callLeg, layout)) {
                            setAttendeeLayout(attendee, layout, callLeg);
                        }
                    }
                }
                if (cc.getRecordingAttendee() != null) {
                    Attendee attendee = cc.getRecordingAttendee();
                    if (!cc.isRecordingCustomsLayout()) {
                        CallLeg callLeg = BeanFactory.getBean(ICallegService.class).getCallLeg(attendee);
                        if (callLeg != null && !layoutEquals(callLeg, layout)) {
                            setAttendeeLayout(attendee, layout, callLeg);
                        }
                    }
                }
            }
        }
        catch (Throwable e)
        {
            logger.error("setAttendeeLayout(ConferenceContext cc, String layout, AttendeeLayoutSetMode attendeeLayoutSetMode) error", e);
        }
    }
}
