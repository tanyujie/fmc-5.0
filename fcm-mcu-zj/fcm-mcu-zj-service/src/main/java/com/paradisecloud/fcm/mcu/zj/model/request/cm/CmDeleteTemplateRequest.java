package com.paradisecloud.fcm.mcu.zj.model.request.cm;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

import java.util.List;

public class CmDeleteTemplateRequest extends CommonRequest {
    private List<Integer> mr_template_ids;

    public List<Integer> getMr_template_ids() {
        return mr_template_ids;
    }

    public void setMr_template_ids(List<Integer> mr_template_ids) {
        this.mr_template_ids = mr_template_ids;
    }
}
