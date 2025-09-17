/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : OfflineAttendeeMessage.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.message
 * @author lilinhai 
 * @since 2021-03-02 13:51
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.attendeemessage;

import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;

/**  
 * <pre>FME不可用的参会者消息</pre>
 * @author lilinhai
 * @since 2021-03-02 13:51
 * @version V1.0  
 */
public class FmeNotAvailableAttendeeMessage extends AttendeeMessage
{
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-03-02 17:40 
     * @param attendee
     * @param fmeBridge 
     */
    public FmeNotAvailableAttendeeMessage(Attendee attendee)
    {
        super(attendee);
    }

}
