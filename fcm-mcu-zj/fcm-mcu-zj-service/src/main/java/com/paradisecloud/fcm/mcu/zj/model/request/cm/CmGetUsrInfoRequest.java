package com.paradisecloud.fcm.mcu.zj.model.request.cm;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

public class CmGetUsrInfoRequest extends CommonRequest {

    private Integer[] usr_ids;
    private Integer[] last_modify_dtms;

    public Integer[] getUsr_ids() {
        return usr_ids;
    }

    public void setUsr_ids(Integer[] usr_ids) {
        this.usr_ids = usr_ids;
    }

    public Integer[] getLast_modify_dtms() {
        return last_modify_dtms;
    }

    public void setLast_modify_dtms(Integer[] last_modify_dtms) {
        this.last_modify_dtms = last_modify_dtms;
    }
}
