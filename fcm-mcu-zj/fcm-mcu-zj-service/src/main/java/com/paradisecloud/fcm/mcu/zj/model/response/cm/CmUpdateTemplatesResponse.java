package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;
import java.util.List;

public class CmUpdateTemplatesResponse extends CommonResponse {

    private List<Integer> fail_mod_endpoint_ids;
    private List<Integer> fail_mod_dep_ids;
    private List<String> fail_mod_usr_names;
    private List<Integer> fail_mod_usr_ids;
    private List<String> fail_mod_dep_names;
    private List<String> fail_mod_endpoint_names;

    public List<Integer> getFail_mod_endpoint_ids() {
        return fail_mod_endpoint_ids;
    }

    public void setFail_mod_endpoint_ids(List<Integer> fail_mod_endpoint_ids) {
        this.fail_mod_endpoint_ids = fail_mod_endpoint_ids;
    }

    public List<Integer> getFail_mod_dep_ids() {
        return fail_mod_dep_ids;
    }

    public void setFail_mod_dep_ids(List<Integer> fail_mod_dep_ids) {
        this.fail_mod_dep_ids = fail_mod_dep_ids;
    }

    public List<String> getFail_mod_usr_names() {
        return fail_mod_usr_names;
    }

    public void setFail_mod_usr_names(List<String> fail_mod_usr_names) {
        this.fail_mod_usr_names = fail_mod_usr_names;
    }

    public List<Integer> getFail_mod_usr_ids() {
        return fail_mod_usr_ids;
    }

    public void setFail_mod_usr_ids(List<Integer> fail_mod_usr_ids) {
        this.fail_mod_usr_ids = fail_mod_usr_ids;
    }

    public List<String> getFail_mod_dep_names() {
        return fail_mod_dep_names;
    }

    public void setFail_mod_dep_names(List<String> fail_mod_dep_names) {
        this.fail_mod_dep_names = fail_mod_dep_names;
    }

    public List<String> getFail_mod_endpoint_names() {
        return fail_mod_endpoint_names;
    }

    public void setFail_mod_endpoint_names(List<String> fail_mod_endpoint_names) {
        this.fail_mod_endpoint_names = fail_mod_endpoint_names;
    }
}
