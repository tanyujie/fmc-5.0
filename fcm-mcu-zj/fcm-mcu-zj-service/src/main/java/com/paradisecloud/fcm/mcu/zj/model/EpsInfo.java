package com.paradisecloud.fcm.mcu.zj.model;

public class EpsInfo {

    private String mr_id;
    private String usr_id;
    private String usr_uri;
    private String usr_name;
    private Integer enter_dtm;
    private String dev_type;
    private Integer ep_version;
    private Integer ep_id;
    private Integer proto_type;
    private String gbk_name;
    private String ep_uuid;
    private String usr_stat;

    public String getMr_id() {
        return mr_id;
    }

    public void setMr_id(String mr_id) {
        this.mr_id = mr_id;
    }

    public String getUsr_id() {
        return usr_id;
    }

    public void setUsr_id(String usr_id) {
        this.usr_id = usr_id;
    }

    public String getUsr_uri() {
        return usr_uri;
    }

    public void setUsr_uri(String usr_uri) {
        this.usr_uri = usr_uri;
    }

    public String getUsr_name() {
        return usr_name;
    }

    public void setUsr_name(String usr_name) {
        this.usr_name = usr_name;
    }

    public Integer getEnter_dtm() {
        return enter_dtm;
    }

    public void setEnter_dtm(Integer enter_dtm) {
        this.enter_dtm = enter_dtm;
    }

    public String getDev_type() {
        return dev_type;
    }

    public void setDev_type(String dev_type) {
        this.dev_type = dev_type;
    }

    public Integer getEp_version() {
        return ep_version;
    }

    public void setEp_version(Integer ep_version) {
        this.ep_version = ep_version;
    }

    public Integer getEp_id() {
        return ep_id;
    }

    public void setEp_id(Integer ep_id) {
        this.ep_id = ep_id;
    }

    public Integer getProto_type() {
        return proto_type;
    }

    public void setProto_type(Integer proto_type) {
        this.proto_type = proto_type;
    }

    public String getGbk_name() {
        return gbk_name;
    }

    public void setGbk_name(String gbk_name) {
        this.gbk_name = gbk_name;
    }

    public String getEp_uuid() {
        return ep_uuid;
    }

    public void setEp_uuid(String ep_uuid) {
        this.ep_uuid = ep_uuid;
    }

    public String getUsr_stat() {
        return usr_stat;
    }

    public void setUsr_stat(String usr_stat) {
        this.usr_stat = usr_stat;
    }
}
