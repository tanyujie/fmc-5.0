package com.paradisecloud.fcm.mcu.zj.model.request.cc;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

public class CcUpdateBypassUrlRequest extends CommonRequest {

    private String bypass_url;
    private Integer enable_bypass;

    public String getBypass_url() {
        return bypass_url;
    }

    public void setBypass_url(String bypass_url) {
        this.bypass_url = bypass_url;
    }

    public Integer getEnable_bypass() {
        return enable_bypass;
    }

    public void setEnable_bypass(Integer enable_bypass) {
        this.enable_bypass = enable_bypass;
    }
}
