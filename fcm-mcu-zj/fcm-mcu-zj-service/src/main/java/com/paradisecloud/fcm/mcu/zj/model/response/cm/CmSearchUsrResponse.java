package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

public class CmSearchUsrResponse extends CommonResponse {

    private Integer[] usr_ids;

    public Integer[] getUsr_ids() {
        return usr_ids;
    }

    public void setUsr_ids(Integer[] usr_ids) {
        this.usr_ids = usr_ids;
    }
}
