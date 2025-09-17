package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

import java.util.List;

public class CmDeleteRoomResponse extends CommonResponse {

    private List<String> fail_reasons;
    private List<Integer> fail_ids;

    public List<String> getFail_reasons() {
        return fail_reasons;
    }

    public void setFail_reasons(List<String> fail_reasons) {
        this.fail_reasons = fail_reasons;
    }

    public List<Integer> getFail_ids() {
        return fail_ids;
    }

    public void setFail_ids(List<Integer> fail_ids) {
        this.fail_ids = fail_ids;
    }
}
