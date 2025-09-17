package com.paradisecloud.fcm.mcu.zj.model.request.cm;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

public class CmDeleteUsrRequest extends CommonRequest {

    private Integer[] usr_ids;
    private String option;
    private Integer[] is_del_priv_rooms;

    public Integer[] getUsr_ids() {
        return usr_ids;
    }

    public void setUsr_ids(Integer[] usr_ids) {
        this.usr_ids = usr_ids;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public Integer[] getIs_del_priv_rooms() {
        return is_del_priv_rooms;
    }

    public void setIs_del_priv_rooms(Integer[] is_del_priv_rooms) {
        this.is_del_priv_rooms = is_del_priv_rooms;
    }
}
