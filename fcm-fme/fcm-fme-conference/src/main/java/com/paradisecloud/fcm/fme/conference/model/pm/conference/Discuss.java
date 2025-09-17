/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : Discuss.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.conference
 * @author sinhy 
 * @since 2021-09-18 11:25
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.model.pm.conference;

import java.lang.reflect.Method;

import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.fme.attendee.model.operation.DiscussAttendeeOperation;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.sinhy.proxy.ProxyMethod;

public class Discuss extends ProxyMethod
{
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-18 11:25 
     * @param method 
     */
    public Discuss(Method method)
    {
        super(method);
    }
    
    public void discuss(String conferenceId)
    {
        new Thread(() -> {
            String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
            ConferenceContext conferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
            conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
            DiscussAttendeeOperation dao = new DiscussAttendeeOperation(conferenceContext);
            conferenceContext.setAttendeeOperation(dao);
            conferenceContext.getLastAttendeeOperation().cancel(dao);
        }).start();

//        String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
//        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(conferenceNumber);
    }
}
