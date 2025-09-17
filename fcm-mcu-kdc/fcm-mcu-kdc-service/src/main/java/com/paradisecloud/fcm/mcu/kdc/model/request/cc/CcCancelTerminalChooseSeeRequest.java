package com.paradisecloud.fcm.mcu.kdc.model.request.cc;

import com.paradisecloud.fcm.mcu.kdc.model.request.CommonRequest;

public class CcCancelTerminalChooseSeeRequest extends CommonRequest {
    private String conf_id;
    private Integer mt_id;
    private Integer mode;

    public String getConf_id() {
        return conf_id;
    }

    public void setConf_id(String conf_id) {
        this.conf_id = conf_id;
    }

    public Integer getMt_id() {
        return mt_id;
    }

    public void setMt_id(Integer mt_id) {
        this.mt_id = mt_id;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }
}
