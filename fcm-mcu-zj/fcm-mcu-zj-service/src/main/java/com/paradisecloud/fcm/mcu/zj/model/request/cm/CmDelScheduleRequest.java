package com.paradisecloud.fcm.mcu.zj.model.request.cm;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

import java.util.List;

public class CmDelScheduleRequest extends CommonRequest {
    private Integer[] schedule_ids;

    public Integer[] getSchedule_ids() {
        return schedule_ids;
    }

    public void setSchedule_ids(Integer[] schedule_ids) {
        this.schedule_ids = schedule_ids;
    }
}
