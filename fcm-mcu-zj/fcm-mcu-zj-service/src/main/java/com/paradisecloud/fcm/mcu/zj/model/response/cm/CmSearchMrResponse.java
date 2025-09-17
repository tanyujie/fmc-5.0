package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

public class CmSearchMrResponse extends CommonResponse {

    private String[] mr_ids;

    public String[] getMr_ids() {
        return mr_ids;
    }

    public void setMr_ids(String[] mr_ids) {
        this.mr_ids = mr_ids;
    }
}
