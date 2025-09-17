package com.paradisecloud.fcm.mcu.zj.model.response.cc;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

public class CcMrParticipantsResponse extends CommonResponse {

    private String mr_id;
    private String[] added_usr_ids;
    private String[] added_usr_names;
    private Integer[] added_usr_types;
    private String[] added_usr_attr;
    private String[] added_usr_devtypes;
    private String[] added_call_addrs;
    private String[] added_gbk_names;
    private Integer[] added_usr_deps;
    private String[] deleted_usr_ids;

    public String getMr_id() {
        return mr_id;
    }

    public void setMr_id(String mr_id) {
        this.mr_id = mr_id;
    }

    public String[] getAdded_usr_ids() {
        return added_usr_ids;
    }

    public void setAdded_usr_ids(String[] added_usr_ids) {
        this.added_usr_ids = added_usr_ids;
    }

    public String[] getAdded_usr_names() {
        return added_usr_names;
    }

    public void setAdded_usr_names(String[] added_usr_names) {
        this.added_usr_names = added_usr_names;
    }

    public Integer[] getAdded_usr_types() {
        return added_usr_types;
    }

    public void setAdded_usr_types(Integer[] added_usr_types) {
        this.added_usr_types = added_usr_types;
    }

    public String[] getAdded_usr_attr() {
        return added_usr_attr;
    }

    public void setAdded_usr_attr(String[] added_usr_attr) {
        this.added_usr_attr = added_usr_attr;
    }

    public String[] getAdded_usr_devtypes() {
        return added_usr_devtypes;
    }

    public void setAdded_usr_devtypes(String[] added_usr_devtypes) {
        this.added_usr_devtypes = added_usr_devtypes;
    }

    public String[] getAdded_call_addrs() {
        return added_call_addrs;
    }

    public void setAdded_call_addrs(String[] added_call_addrs) {
        this.added_call_addrs = added_call_addrs;
    }

    public String[] getAdded_gbk_names() {
        return added_gbk_names;
    }

    public void setAdded_gbk_names(String[] added_gbk_names) {
        this.added_gbk_names = added_gbk_names;
    }

    public Integer[] getAdded_usr_deps() {
        return added_usr_deps;
    }

    public void setAdded_usr_deps(Integer[] added_usr_deps) {
        this.added_usr_deps = added_usr_deps;
    }

    public String[] getDeleted_usr_ids() {
        return deleted_usr_ids;
    }

    public void setDeleted_usr_ids(String[] deleted_usr_ids) {
        this.deleted_usr_ids = deleted_usr_ids;
    }
}
