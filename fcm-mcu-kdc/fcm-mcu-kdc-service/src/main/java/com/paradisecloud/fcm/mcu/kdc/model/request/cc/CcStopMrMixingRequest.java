package com.paradisecloud.fcm.mcu.kdc.model.request.cc;

import com.paradisecloud.fcm.mcu.kdc.model.request.CommonRequest;

public class CcStopMrMixingRequest extends CommonRequest {

    private String conf_id;
    private String mix_id;

    public String getConf_id() {
        return conf_id;
    }

    public void setConf_id(String conf_id) {
        this.conf_id = conf_id;
    }

    public String getMix_id() {
        return mix_id;
    }

    public void setMix_id(String mix_id) {
        this.mix_id = mix_id;
    }
}
