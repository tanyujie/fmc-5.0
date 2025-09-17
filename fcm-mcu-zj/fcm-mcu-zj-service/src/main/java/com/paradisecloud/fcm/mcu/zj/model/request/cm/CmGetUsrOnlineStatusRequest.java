package com.paradisecloud.fcm.mcu.zj.model.request.cm;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

public class CmGetUsrOnlineStatusRequest extends CommonRequest {

    private Integer[] usr_ids;

    public Integer[] getUsr_ids() {
        return usr_ids;
    }

    public void setUsr_ids(Integer[] usr_ids) {
        this.usr_ids = usr_ids;
    }
}
