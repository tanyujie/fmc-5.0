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
 * <pre>信令未断开的参会者消息</pre>
 * @author lilinhai
 * @since 2021-03-02 13:51
 * @version V1.0  
 */
public class SignalingNotDisconnectedAttendeeMessage extends CallFailedAttendeeMessage
{

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-03-02 14:05 
     * @param attendee 
     */
    public SignalingNotDisconnectedAttendeeMessage(Attendee attendee)
    {
        super(attendee);
        
        // 此种场景等待120秒开始重呼
        this.waitingSeconds = 120;
    }
}
