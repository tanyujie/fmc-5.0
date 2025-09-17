package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

public class CmGetUsrOnlineStatusResponse extends CommonResponse {

    private Integer[] usr_ids;
    private Integer[] online_status;
    private Integer[] fail_usr_ids;

    public Integer[] getUsr_ids() {
        return usr_ids;
    }

    public void setUsr_ids(Integer[] usr_ids) {
        this.usr_ids = usr_ids;
    }

    public Integer[] getOnline_status() {
        return online_status;
    }

    public void setOnline_status(Integer[] online_status) {
        this.online_status = online_status;
    }

    public Integer[] getFail_usr_ids() {
        return fail_usr_ids;
    }

    public void setFail_usr_ids(Integer[] fail_usr_ids) {
        this.fail_usr_ids = fail_usr_ids;
    }
}
