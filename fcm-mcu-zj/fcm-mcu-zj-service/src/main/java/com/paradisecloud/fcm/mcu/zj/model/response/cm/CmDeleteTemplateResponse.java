package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

import java.util.List;

public class CmDeleteTemplateResponse extends CommonResponse {
    private List<Integer> fail_ids;

    public List<Integer> getFail_ids() {
        return fail_ids;
    }

    public void setFail_ids(List<Integer> fail_ids) {
        this.fail_ids = fail_ids;
    }
}
