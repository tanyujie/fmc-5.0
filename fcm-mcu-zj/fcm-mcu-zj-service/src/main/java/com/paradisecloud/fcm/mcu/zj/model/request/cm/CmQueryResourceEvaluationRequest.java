package com.paradisecloud.fcm.mcu.zj.model.request.cm;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

public class CmQueryResourceEvaluationRequest extends CommonRequest {

    private Integer[] resource_template_ids;
    private Integer[] max_calls;

    public Integer[] getResource_template_ids() {
        return resource_template_ids;
    }

    public void setResource_template_ids(Integer[] resource_template_ids) {
        this.resource_template_ids = resource_template_ids;
    }

    public Integer[] getMax_calls() {
        return max_calls;
    }

    public void setMax_calls(Integer[] max_calls) {
        this.max_calls = max_calls;
    }
}