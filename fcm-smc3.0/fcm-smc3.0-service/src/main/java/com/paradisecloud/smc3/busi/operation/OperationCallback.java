/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : OperationCallback.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.operation
 * @author lilinhai 
 * @since 2021-03-05 10:46
 * @version  V1.0
 */ 
package com.paradisecloud.smc3.busi.operation;


import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;

/**
 * <pre>会议操作回调器</pre>
 * @author lilinhai
 * @since 2021-03-05 10:46
 * @version V1.0  
 */
public interface OperationCallback
{
    default void success(Smc3ConferenceContext conferenceContext, AttendeeSmc3 attendee)
    {
        
    }
    
    default void success()
    {
        
    }
    
    default void fail(int code, String message)
    {
        
    }
}
