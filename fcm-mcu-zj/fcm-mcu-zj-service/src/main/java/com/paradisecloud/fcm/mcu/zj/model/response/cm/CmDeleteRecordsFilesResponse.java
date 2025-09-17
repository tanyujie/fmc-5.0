package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

public class CmDeleteRecordsFilesResponse extends CommonResponse {
    private String[] fail_uuids;

    public String[] getFail_uuids() {
        return fail_uuids;
    }

    public void setFail_uuids(String[] fail_uuids) {
        this.fail_uuids = fail_uuids;
    }
}
