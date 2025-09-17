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
 * <pre>呼叫失败的参会者消息</pre>
 * @author lilinhai
 * @since 2021-03-02 13:51
 * @version V1.0  
 */
public class CallFailedAttendeeMessage extends AttendeeMessage
{
    
    /**
     * 等待多久开始处理（秒）,默认三秒，原因是这是真实的响应时间
     */
    protected int waitingSeconds = 80;

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-03-02 14:05 
     * @param attendee 
     */
    public CallFailedAttendeeMessage(Attendee attendee)
    {
        super(attendee);
    }
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-03-08 18:53 
     * @param attendee
     * @param waitingSeconds 
     */
    public CallFailedAttendeeMessage(Attendee attendee, int waitingSeconds)
    {
        super(attendee);
        this.waitingSeconds = waitingSeconds;
    }

    @Override
    public boolean isNeedProcess()
    {
        return System.currentTimeMillis() - timestamp > 1000 * waitingSeconds && super.isNeedProcess();
    }
}
