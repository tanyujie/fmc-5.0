package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

import java.util.List;

public class CmDelScheduleResponse extends CommonResponse {
    private Integer[] fail_ids;

    public Integer[] getFail_ids() {
        return fail_ids;
    }

    public void setFail_ids(Integer[] fail_ids) {
        this.fail_ids = fail_ids;
    }
}
