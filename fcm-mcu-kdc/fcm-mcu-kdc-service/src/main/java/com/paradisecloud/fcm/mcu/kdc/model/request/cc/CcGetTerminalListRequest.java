package com.paradisecloud.fcm.mcu.kdc.model.request.cc;

import com.paradisecloud.fcm.mcu.kdc.model.request.CommonRequest;

public class CcGetTerminalListRequest extends CommonRequest {

    private String conf_id;

    public String getConf_id() {
        return conf_id;
    }

    public void setConf_id(String conf_id) {
        this.conf_id = conf_id;
    }
}
