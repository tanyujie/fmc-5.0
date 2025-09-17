package com.paradisecloud.fcm.mcu.zj.model.request.cc;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

public class CcLoginRequest extends CommonRequest {

    private String mr_id;
    private String ctrl_pwd;
    private String cert_code;

    public String getMr_id() {
        return mr_id;
    }

    public void setMr_id(String mr_id) {
        this.mr_id = mr_id;
    }

    public String getCtrl_pwd() {
        return ctrl_pwd;
    }

    public void setCtrl_pwd(String ctrl_pwd) {
        this.ctrl_pwd = ctrl_pwd;
    }

    public String getCert_code() {
        return cert_code;
    }

    public void setCert_code(String cert_code) {
        this.cert_code = cert_code;
    }
}
