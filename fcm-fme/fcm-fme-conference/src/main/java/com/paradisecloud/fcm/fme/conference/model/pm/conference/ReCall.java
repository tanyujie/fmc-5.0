/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ReCall.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.conference
 * @author sinhy 
 * @since 2021-09-27 21:48
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.model.pm.conference;

import java.lang.reflect.Method;

import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.attendee.utils.ConferenceContextUtils;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.utils.FcmThreadPool;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.sinhy.proxy.ProxyMethod;
import com.sinhy.spring.BeanFactory;

public class ReCall extends ProxyMethod
{
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-27 21:48 
     * @param method 
     */
    public ReCall(Method method)
    {
        super(method);
    }
    
    /**
     * <pre>一键呼入</pre>
     * @author sinhy
     * @since 2021-09-27 21:47 
     * @param conferenceId void
     */
    public void reCall(String conferenceId)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        ConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            if (!a.isMeetingJoined())
            {
                FcmThreadPool.exec(() -> {
                    BeanFactory.getBean(IAttendeeService.class).callAttendee(a);
                });
            }
        });
    }
}
