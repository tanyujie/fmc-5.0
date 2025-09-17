package com.paradisecloud.fcm.mcu.zj.model.response.cc;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

public class CcEpsStatusResponse extends CommonResponse {
    private String mr_id;
    private String[] usr_ids;
    private String[] usr_stats;

    public String getMr_id() {
        return mr_id;
    }

    public void setMr_id(String mr_id) {
        this.mr_id = mr_id;
    }

    public String[] getUsr_ids() {
        return usr_ids;
    }

    public void setUsr_ids(String[] usr_ids) {
        this.usr_ids = usr_ids;
    }

    public String[] getUsr_stats() {
        return usr_stats;
    }

    public void setUsr_stats(String[] usr_stats) {
        this.usr_stats = usr_stats;
    }
}
