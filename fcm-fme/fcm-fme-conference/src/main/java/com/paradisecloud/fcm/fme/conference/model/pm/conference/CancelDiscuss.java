/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CancelDiscuss.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.conference
 * @author sinhy 
 * @since 2021-09-18 11:26
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.model.pm.conference;

import java.lang.reflect.Method;

import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.operation.AttendeeOperation;
import com.sinhy.proxy.ProxyMethod;

public class CancelDiscuss extends ProxyMethod
{
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-18 11:26 
     * @param method 
     */
    public CancelDiscuss(Method method)
    {
        super(method);
    }
    
    public void cancelDiscuss(String conferenceId)
    {
        new Thread(() -> {
            try
            {
                ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
                if (conferenceContext.getAttendeeOperation() != null)
                {
                    AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
                    conferenceContext.setLastAttendeeOperation(attendeeOperation);
                    conferenceContext.setAttendeeOperation(conferenceContext.getDefaultViewOperation());
                    attendeeOperation.cancel(conferenceContext.getDefaultViewOperation());
                }
            }
            catch (Throwable e)
            {
                logger.error("cancelCallTheRoll error", e);
            }
        }).start();

//        String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
//        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(conferenceNumber);
    }
}
