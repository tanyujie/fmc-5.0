package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

public class CmDeleteUsrResponse extends CommonResponse {

    private Integer[] fail_ids;
    private String[] fail_reasons;

    public Integer[] getFail_ids() {
        return fail_ids;
    }

    public void setFail_ids(Integer[] fail_ids) {
        this.fail_ids = fail_ids;
    }

    public String[] getFail_reasons() {
        return fail_reasons;
    }

    public void setFail_reasons(String[] fail_reasons) {
        this.fail_reasons = fail_reasons;
    }
}
