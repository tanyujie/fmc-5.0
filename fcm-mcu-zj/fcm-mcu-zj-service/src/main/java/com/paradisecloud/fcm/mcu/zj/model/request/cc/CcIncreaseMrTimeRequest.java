package com.paradisecloud.fcm.mcu.zj.model.request.cc;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

public class CcIncreaseMrTimeRequest extends CommonRequest {
    private Integer increase_secs;

    public Integer getIncrease_secs() {
        return increase_secs;
    }

    public void setIncrease_secs(Integer increase_secs) {
        this.increase_secs = increase_secs;
    }
}
