/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : IAttendeeLayoutService.java
 * Package     : com.paradisecloud.fcm.fme.attendee.interfaces
 * @author lilinhai 
 * @since 2021-04-01 15:54
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.interfaces;

import com.paradisecloud.fcm.fme.attendee.model.enumer.AttendeeLayoutSetMode;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;

/**  
 * <pre>参会者布局业务处理</pre>
 * @author lilinhai
 * @since 2021-04-01 15:54
 * @version V1.0  
 */
public interface IAttendeeLayoutService
{
    
    /**
     * 根据参会者布局设置模式设置参会者布局
     * @author lilinhai
     * @since 2021-04-01 17:05 
     * @param conferenceId
     * @param layout
     * @param attendeeLayoutSetMode void
     */
    void setAttendeeLayout(String conferenceId, String layout, AttendeeLayoutSetMode attendeeLayoutSetMode);
    
    /**
     * 根据参会者布局设置模式设置参会者布局
     * @author lilinhai
     * @since 2021-04-09 15:19 
     * @param cc
     * @param layout
     * @param attendeeLayoutSetMode void
     */
    void setAttendeeLayout(ConferenceContext cc, String layout, AttendeeLayoutSetMode attendeeLayoutSetMode);
    
    void setAttendeeLayout(Attendee a, String layout);
    
    /**
     * 设置参会者默认布局
     * @author lilinhai
     * @since 2021-04-01 15:57 
     * @param conferenceId
     * @param attendeeId
     * @param layout void
     */
    void setAttendeeLayout(String conferenceId, String attendeeId, String layout);
}
