/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : IAttendeeWebsocketMessageService.java
 * Package     : com.paradisecloud.fcm.fme.attendee.interfaces
 * @author sinhy 
 * @since 2021-12-27 11:25
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.interfaces;

import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.message.AttendeeStatusMessage;

/**  
 * 参会者WS消息通知
 * @author sinhy
 * @since 2021-12-27 11:25
 * @version V1.0  
 */
public interface IAttendeeWebsocketMessageService
{
    
    /**
     * 订阅参会者更新消息
     * @author sinhy
     * @since 2021-12-27 11:27 
     * @param wsMsg void
     */
    void subscribe(AttendeeStatusMessage wsMsg);

    int updateBusiRecords(boolean recording, String conferenceNumber, ConferenceContext conferenceContext);
}
