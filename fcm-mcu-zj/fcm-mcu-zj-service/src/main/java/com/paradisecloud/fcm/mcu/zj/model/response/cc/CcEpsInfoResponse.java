package com.paradisecloud.fcm.mcu.zj.model.response.cc;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

import java.util.List;

/**
 * 获取在线会场列表
 */
public class CcEpsInfoResponse extends CommonResponse {

    private String mr_id;
    private String[] usr_ids;
    private String[] usr_uris;
    private String[] usr_names;
    private Integer[] enter_dtms;
    private String[] dev_type;
    private Integer[] ep_version;
    private Integer[] ep_ids;
    private Integer[] proto_types;
    private String[] gbk_names;
    private String[] ep_uuid;

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

    public String[] getUsr_uris() {
        return usr_uris;
    }

    public void setUsr_uris(String[] usr_uris) {
        this.usr_uris = usr_uris;
    }

    public String[] getUsr_names() {
        return usr_names;
    }

    public void setUsr_names(String[] usr_names) {
        this.usr_names = usr_names;
    }

    public Integer[] getEnter_dtms() {
        return enter_dtms;
    }

    public void setEnter_dtms(Integer[] enter_dtms) {
        this.enter_dtms = enter_dtms;
    }

    public String[] getDev_type() {
        return dev_type;
    }

    public void setDev_type(String[] dev_type) {
        this.dev_type = dev_type;
    }

    public Integer[] getEp_version() {
        return ep_version;
    }

    public void setEp_version(Integer[] ep_version) {
        this.ep_version = ep_version;
    }

    public Integer[] getEp_ids() {
        return ep_ids;
    }

    public void setEp_ids(Integer[] ep_ids) {
        this.ep_ids = ep_ids;
    }

    public Integer[] getProto_types() {
        return proto_types;
    }

    public void setProto_types(Integer[] proto_types) {
        this.proto_types = proto_types;
    }

    public String[] getGbk_names() {
        return gbk_names;
    }

    public void setGbk_names(String[] gbk_names) {
        this.gbk_names = gbk_names;
    }

    public String[] getEp_uuid() {
        return ep_uuid;
    }

    public void setEp_uuid(String[] ep_uuid) {
        this.ep_uuid = ep_uuid;
    }
}
