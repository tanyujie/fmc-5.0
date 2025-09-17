package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

import java.util.List;

public class CmAddMrTemplatesResponse extends CommonResponse {

    /**
     * fail_add_usr_names : []
     * fail_add_dep_names : []
     * fail_add_endpoint_names : []
     * fail_add_endpoint_ids : []
     * fail_dep_ids : []
     * cmdid : add_mr_template_rsp
     * fail_add_usr_ids : []
     * fail_add_dep_ids : []
     * mr_template_id : 13
     * result : 0/success
     */

    private int mr_template_id;
    private String result;
    private List<String> fail_add_usr_names;
    private List<String> fail_add_dep_names;
    private List<String> fail_add_endpoint_names;
    private List<Integer> fail_add_endpoint_ids;
    private List<Integer> fail_dep_ids;
    private List<Integer> fail_add_usr_ids;
    private List<Integer> fail_add_dep_ids;

    public int getMr_template_id() {
        return mr_template_id;
    }

    public void setMr_template_id(int mr_template_id) {
        this.mr_template_id = mr_template_id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<String> getFail_add_usr_names() {
        return fail_add_usr_names;
    }

    public void setFail_add_usr_names(List<String> fail_add_usr_names) {
        this.fail_add_usr_names = fail_add_usr_names;
    }

    public List<String> getFail_add_dep_names() {
        return fail_add_dep_names;
    }

    public void setFail_add_dep_names(List<String> fail_add_dep_names) {
        this.fail_add_dep_names = fail_add_dep_names;
    }

    public List<String> getFail_add_endpoint_names() {
        return fail_add_endpoint_names;
    }

    public void setFail_add_endpoint_names(List<String> fail_add_endpoint_names) {
        this.fail_add_endpoint_names = fail_add_endpoint_names;
    }

    public List<Integer> getFail_add_endpoint_ids() {
        return fail_add_endpoint_ids;
    }

    public void setFail_add_endpoint_ids(List<Integer> fail_add_endpoint_ids) {
        this.fail_add_endpoint_ids = fail_add_endpoint_ids;
    }

    public List<Integer> getFail_dep_ids() {
        return fail_dep_ids;
    }

    public void setFail_dep_ids(List<Integer> fail_dep_ids) {
        this.fail_dep_ids = fail_dep_ids;
    }

    public List<Integer> getFail_add_usr_ids() {
        return fail_add_usr_ids;
    }

    public void setFail_add_usr_ids(List<Integer> fail_add_usr_ids) {
        this.fail_add_usr_ids = fail_add_usr_ids;
    }

    public List<Integer> getFail_add_dep_ids() {
        return fail_add_dep_ids;
    }

    public void setFail_add_dep_ids(List<Integer> fail_add_dep_ids) {
        this.fail_add_dep_ids = fail_add_dep_ids;
    }
}
