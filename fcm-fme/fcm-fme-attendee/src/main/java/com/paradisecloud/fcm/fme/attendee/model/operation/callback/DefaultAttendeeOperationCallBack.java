/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DefaultAttendeeOperationCallBack.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.operation.callback
 * @author lilinhai 
 * @since 2021-05-10 13:55
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.operation.callback;

import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.operation.OperationCallback;

public class DefaultAttendeeOperationCallBack implements OperationCallback
{
    
    private boolean isOk;
    
    @Override
    public void success(ConferenceContext conferenceContext, Attendee attendee)
    {
        if (isOk)
        {
            exec();
        }
    }
    
    @Override
    public void success()
    {
        
    }
    
    @Override
    public void fail(int code, String message)
    {
        
    }
    
    public void exec()
    {
        
    }

    /**
     * <p>Get Method   :   isOk boolean</p>
     * @return isOk
     */
    public boolean isOk()
    {
        return isOk;
    }

    /**
     * <p>Set Method   :   isOk boolean</p>
     * @param isOk
     */
    public void setOk(boolean isOk)
    {
        this.isOk = isOk;
    }
}
