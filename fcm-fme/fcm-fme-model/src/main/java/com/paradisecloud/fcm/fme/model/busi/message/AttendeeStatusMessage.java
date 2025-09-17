/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeStatusMessage.java
 * Package     : com.paradisecloud.fcm.common.message.attendee
 * @author sinhy 
 * @since 2021-12-27 11:34
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.model.busi.message;

import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;

/**  
 * 参会者状态消息
 * @author sinhy
 * @since 2021-12-27 11:34
 * @version V1.0  
 */
public class AttendeeStatusMessage
{
    
    private final Attendee attendee;

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-12-27 11:35 
     * @param attendee 
     */
    public AttendeeStatusMessage(Attendee attendee)
    {
        super();
        this.attendee = attendee;
    }

    /**
     * <p>Get Method   :   attendee Attendee</p>
     * @return attendee
     */
    public Attendee getAttendee()
    {
        return attendee;
    }
}
