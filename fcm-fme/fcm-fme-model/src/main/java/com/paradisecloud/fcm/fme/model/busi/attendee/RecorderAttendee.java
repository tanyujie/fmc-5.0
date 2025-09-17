/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2022, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : RecorderAttendee.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.attendee
 * @author sinhy 
 * @since 2022-01-17 11:10
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.model.busi.attendee;

/**  
 * 录制器参会
 * @author sinhy
 * @since 2022-01-17 11:10
 * @version V1.0  
 */
public class RecorderAttendee extends SelfCallAttendee
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2022-01-17 11:50 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * <p>Get Method   :   isRecorder boolean</p>
     * @return isRecorder
     */
    public boolean isRecorder()
    {
        return true;
    }
}
