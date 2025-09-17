/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ConferenceAttendeeImportanceMonitorThread.java
 * Package     : com.paradisecloud.fcm.fme.conference.core
 * @author sinhy 
 * @since 2021-08-23 10:49
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.core;

import com.paradisecloud.fcm.fme.cache.utils.FcmThreadPool;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.sinhy.model.AsyncBlockingMessageProcessor;

/**  
 * <pre>会议室参会者权重监听线程</pre>
 * @author sinhy
 * @since 2021-08-23 10:49
 * @version V1.0  
 */
public class ConferenceAttendeeImportanceMonitor extends AsyncBlockingMessageProcessor<ConferenceContext>
{

    private static final ConferenceAttendeeImportanceMonitor INSTANCE = new ConferenceAttendeeImportanceMonitor();
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-08-23 10:50 
     * @param name
     * @param queueSize 
     */
    private ConferenceAttendeeImportanceMonitor()
    {
        super("ConferenceAttendeeImportanceMonitor", 1000);
    }

    @Override
    protected void process(ConferenceContext cc)
    {
        FcmThreadPool.exec(()->{
            new ConferenceAttendeeImportanceCorrector(cc).correct();
        });
    }
    
    
    public static ConferenceAttendeeImportanceMonitor getInstance()
    {
        return INSTANCE;
    }
}
