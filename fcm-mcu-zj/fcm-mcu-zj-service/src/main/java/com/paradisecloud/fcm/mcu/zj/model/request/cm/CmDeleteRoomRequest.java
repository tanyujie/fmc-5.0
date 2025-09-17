package com.paradisecloud.fcm.mcu.zj.model.request.cm;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;
import java.util.List;

public class CmDeleteRoomRequest extends CommonRequest {

    private Integer[] room_ids;

    public Integer[] getRoom_ids() {
        return room_ids;
    }

    public void setRoom_ids(Integer[] room_ids) {
        this.room_ids = room_ids;
    }
}
