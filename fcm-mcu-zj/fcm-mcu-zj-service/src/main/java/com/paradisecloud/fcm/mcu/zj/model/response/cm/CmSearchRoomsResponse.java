package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

public class CmSearchRoomsResponse extends CommonResponse {
    private Integer[] room_ids;

    public Integer[] getRoom_ids() {
        return room_ids;
    }

    public void setRoom_ids(Integer[] room_ids) {
        this.room_ids = room_ids;
    }
}
