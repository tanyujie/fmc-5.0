package com.paradisecloud.fcm.mqtt.impls;

import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeLayoutWebsocketMessageService;
import com.paradisecloud.fcm.fme.conference.model.queue.LayOutStatusMessageQueue;
import com.paradisecloud.fcm.fme.model.busi.message.AttendeeStatusMessage;
import org.springframework.stereotype.Service;

/**
 * @author nj
 * @date 2023/1/12 13:32
 */
@Service
public class AttendeeLayoutWebsocketMessageServiceImpl implements IAttendeeLayoutWebsocketMessageService {

    @Override
    public void subscribe(AttendeeStatusMessage wsMsg) {
        LayOutStatusMessageQueue.getInstance().put(wsMsg);
    }
}
