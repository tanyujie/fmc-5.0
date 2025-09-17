package com.paradisecloud.fcm.mcu.zj.model.response.cc;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

import java.util.Date;

public class CcMrInfoResponse extends CommonResponse {
    private String mr_id;
    private String mr_name;
    private String room_name;
    private Date create_dtm;
    private Date real_launch_dtm;
    private Date current_dtm;
    private Integer duration;
    private Integer bandwidth;
    private String launcher_uid;
    private Integer mr_mode;
    private String features;

    public String getMr_id() {
        return mr_id;
    }

    public void setMr_id(String mr_id) {
        this.mr_id = mr_id;
    }

    public String getMr_name() {
        return mr_name;
    }

    public void setMr_name(String mr_name) {
        this.mr_name = mr_name;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public Date getCreate_dtm() {
        return create_dtm;
    }

    public void setCreate_dtm(Date create_dtm) {
        this.create_dtm = create_dtm;
    }

    public Date getReal_launch_dtm() {
        return real_launch_dtm;
    }

    public void setReal_launch_dtm(Date real_launch_dtm) {
        this.real_launch_dtm = real_launch_dtm;
    }

    public Date getCurrent_dtm() {
        return current_dtm;
    }

    public void setCurrent_dtm(Date current_dtm) {
        this.current_dtm = current_dtm;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Integer bandwidth) {
        this.bandwidth = bandwidth;
    }

    public String getLauncher_uid() {
        return launcher_uid;
    }

    public void setLauncher_uid(String launcher_uid) {
        this.launcher_uid = launcher_uid;
    }

    public Integer getMr_mode() {
        return mr_mode;
    }

    public void setMr_mode(Integer mr_mode) {
        this.mr_mode = mr_mode;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }
}
