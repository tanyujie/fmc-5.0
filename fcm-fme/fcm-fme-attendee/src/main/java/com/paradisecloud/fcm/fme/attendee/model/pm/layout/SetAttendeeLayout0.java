/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : GetCallLegByParticipantUuid.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.pm.attendee
 * @author sinhy 
 * @since 2021-09-17 21:09
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.pm.layout;

import java.lang.reflect.Method;

import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.fme.attendee.model.enumer.AttendeeLayoutSetMode;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;

public class SetAttendeeLayout0 extends SetAttendeeLayout1
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-17 21:09 
     * @param method 
     */
    protected SetAttendeeLayout0(Method method)
    {
        super(method);
    }
    
    public void setAttendeeLayout(String conferenceId, String layout, AttendeeLayoutSetMode attendeeLayoutSetMode)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext cc = ConferenceContextCache.getInstance().get(contextKey);
        if (cc != null)
        {
            setAttendeeLayout(cc, layout, attendeeLayoutSetMode);
        }
        else
        {
            logger.error("找不到contextKey：" + contextKey);
        }
    }
    
}
