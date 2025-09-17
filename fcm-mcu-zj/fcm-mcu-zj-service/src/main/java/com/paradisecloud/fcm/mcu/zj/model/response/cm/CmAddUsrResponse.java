package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

public class CmAddUsrResponse extends CommonResponse {

    private String result;
    private Integer usr_id;
    private String create_priv_room;
    private Integer[] fail_dep_ids;
    private Integer[] has_no_privilege_dep_ids;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getUsr_id() {
        return usr_id;
    }

    public void setUsr_id(Integer usr_id) {
        this.usr_id = usr_id;
    }

    public String getCreate_priv_room() {
        return create_priv_room;
    }

    public void setCreate_priv_room(String create_priv_room) {
        this.create_priv_room = create_priv_room;
    }

    public Integer[] getFail_dep_ids() {
        return fail_dep_ids;
    }

    public void setFail_dep_ids(Integer[] fail_dep_ids) {
        this.fail_dep_ids = fail_dep_ids;
    }

    public Integer[] getHas_no_privilege_dep_ids() {
        return has_no_privilege_dep_ids;
    }

    public void setHas_no_privilege_dep_ids(Integer[] has_no_privilege_dep_ids) {
        this.has_no_privilege_dep_ids = has_no_privilege_dep_ids;
    }
}
