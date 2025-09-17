/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeCallCache.java
 * Package     : com.paradisecloud.fcm.fme.cache
 * @author lilinhai 
 * @since 2021-02-08 13:52
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.cache;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;

/**  
 * <pre>参会者呼叫缓存，用于做呼叫成功和失败通知</pre>
 * @author lilinhai
 * @since 2021-02-08 13:52
 * @version V1.0  
 */
public class AttendeeCallCache extends JavaCache<String, Attendee>
{
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-08 13:54 
     */
    private static final long serialVersionUID = 1L;
    private static final AttendeeCallCache INSTANCE = new AttendeeCallCache();
    
    
    
    private AttendeeCallCache()
    {
        
    }
    
    public static AttendeeCallCache getInstance()
    {
        return INSTANCE;
    }
}
