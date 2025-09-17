package com.paradisecloud.fcm.fme.attendee.interfaces;

import com.paradisecloud.fcm.fme.model.busi.message.AttendeeStatusMessage;

/**
 * @author nj
 * @date 2023/1/12 13:40
 */
public interface IAttendeeLayoutWebsocketMessageService {

    /**
     * 订阅参会者更新消息
     * @author sinhy
     * @since 2021-12-27 11:27
     * @param wsMsg void
     */
    void subscribe(AttendeeStatusMessage wsMsg);

}
