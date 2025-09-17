package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;
import java.util.List;

public class CmQueryMrTemplateResponse extends CommonResponse {

    private List<Integer> mr_template_ids;

    public List<Integer> getMr_template_ids() {
        return mr_template_ids;
    }

    public void setMr_template_ids(List<Integer> mr_template_ids) {
        this.mr_template_ids = mr_template_ids;
    }
}
