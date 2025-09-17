/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : Sync.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.conference
 * @author sinhy 
 * @since 2021-09-18 10:55
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.model.pm.conference;

import java.lang.reflect.Method;

import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.fme.cache.utils.FcmThreadPool;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;

public class Sync2 extends Sync
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-18 10:55 
     * @param method 
     */
    protected Sync2(Method method)
    {
        super(method);
    }
 
    public void sync(String conferenceId)
    {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
        FcmThreadPool.exec(()->{
            sync(conferenceContext, "手工触发同步");
        });
    }
}
