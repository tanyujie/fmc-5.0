/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ChooseToSeeOperationCallBack.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.operation.callback
 * @author lilinhai 
 * @since 2021-03-05 11:16
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.operation.callback;

import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.operation.OperationCallback;

/**  
 * <pre>选看回调</pre>
 * @author lilinhai
 * @since 2021-03-05 11:16
 * @version V1.0  
 */
public class ChooseToSeeOperationCallBack implements OperationCallback
{

    @Override
    public void success(ConferenceContext conferenceContext, Attendee attendee)
    {
    }
    
    @Override
    public void success()
    {
        
    }

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author lilinhai
     * @since 2021-03-05 11:17 
     * @param code
     * @param message
     * @see com.paradisecloud.fcm.fme.model.busi.operation.OperationCallback#fail(int, java.lang.String)
     */
    @Override
    public void fail(int code, String message)
    {
        
    }
    
}
