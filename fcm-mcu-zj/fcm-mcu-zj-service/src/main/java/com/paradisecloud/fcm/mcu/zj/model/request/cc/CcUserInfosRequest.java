package com.paradisecloud.fcm.mcu.zj.model.request.cc;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

public class CcUserInfosRequest extends CommonRequest {

    private String[] usr_ids;

    public String[] getUsr_ids() {
        return usr_ids;
    }

    public void setUsr_ids(String[] usr_ids) {
        this.usr_ids = usr_ids;
    }
}
