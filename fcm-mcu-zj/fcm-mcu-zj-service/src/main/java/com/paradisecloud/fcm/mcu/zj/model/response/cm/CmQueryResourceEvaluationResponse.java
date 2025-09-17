package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

public class CmQueryResourceEvaluationResponse extends CommonResponse {

    private Integer[] resource_evaluations;

    public Integer[] getResource_evaluations() {
        return resource_evaluations;
    }

    public void setResource_evaluations(Integer[] resource_evaluations) {
        this.resource_evaluations = resource_evaluations;
    }
}