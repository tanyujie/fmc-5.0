package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

public class CmQuerySysResourceStatisticsResponse extends CommonResponse {
    private int system_resource_count;
    private float used_resource_count;

    public int getSystem_resource_count() {
        return system_resource_count;
    }

    public void setSystem_resource_count(int system_resource_count) {
        this.system_resource_count = system_resource_count;
    }

    public float getUsed_resource_count() {
        return used_resource_count;
    }

    public void setUsed_resource_count(float used_resource_count) {
        this.used_resource_count = used_resource_count;
    }
}
