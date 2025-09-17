package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

import java.util.List;

public class CmAddScheduleResponse extends CommonResponse {

    private int schedule_id;
    private String result;
    private List<Integer> fail_add_usr_ids;
    private List<String> fail_add_usr_names;
    private List<Integer> fail_add_endpoint_ids;
    private List<String> fail_add_endpoint_names;
    private List<Integer> fail_add_dep_ids;
    private List<String> fail_add_dep_names;

    public int getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(int schedule_id) {
        this.schedule_id = schedule_id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<Integer> getFail_add_usr_ids() {
        return fail_add_usr_ids;
    }

    public void setFail_add_usr_ids(List<Integer> fail_add_usr_ids) {
        this.fail_add_usr_ids = fail_add_usr_ids;
    }

    public List<String> getFail_add_usr_names() {
        return fail_add_usr_names;
    }

    public void setFail_add_usr_names(List<String> fail_add_usr_names) {
        this.fail_add_usr_names = fail_add_usr_names;
    }

    public List<Integer> getFail_add_endpoint_ids() {
        return fail_add_endpoint_ids;
    }

    public void setFail_add_endpoint_ids(List<Integer> fail_add_endpoint_ids) {
        this.fail_add_endpoint_ids = fail_add_endpoint_ids;
    }

    public List<String> getFail_add_endpoint_names() {
        return fail_add_endpoint_names;
    }

    public void setFail_add_endpoint_names(List<String> fail_add_endpoint_names) {
        this.fail_add_endpoint_names = fail_add_endpoint_names;
    }

    public List<Integer> getFail_add_dep_ids() {
        return fail_add_dep_ids;
    }

    public void setFail_add_dep_ids(List<Integer> fail_add_dep_ids) {
        this.fail_add_dep_ids = fail_add_dep_ids;
    }

    public List<String> getFail_add_dep_names() {
        return fail_add_dep_names;
    }

    public void setFail_add_dep_names(List<String> fail_add_dep_names) {
        this.fail_add_dep_names = fail_add_dep_names;
    }
}
