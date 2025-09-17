package com.paradisecloud.fcm.mcu.zj.model.response.cc;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

public class CcMrStatusResponse extends CommonResponse {

    private String mr_id;
    private Integer mr_locked;
    private Integer auto_invite;
    private Integer cont_enabled;
    private Integer live_enabled;
    private Integer rec_enabled;
    private String record_uuid;
    private String rtmp_url;
    private String record_copy;
    private String[] spec_focus_usr_ids;
    private Long flv_fsize;
    private String auto_focus_usr_id;
    private Integer bypass_enabled;

    // 未完待续

    public String getMr_id() {
        return mr_id;
    }

    public void setMr_id(String mr_id) {
        this.mr_id = mr_id;
    }

    public Integer getMr_locked() {
        return mr_locked;
    }

    public void setMr_locked(Integer mr_locked) {
        this.mr_locked = mr_locked;
    }

    public Integer getAuto_invite() {
        return auto_invite;
    }

    public void setAuto_invite(Integer auto_invite) {
        this.auto_invite = auto_invite;
    }

    public Integer getCont_enabled() {
        return cont_enabled;
    }

    public void setCont_enabled(Integer cont_enabled) {
        this.cont_enabled = cont_enabled;
    }

    public Integer getLive_enabled() {
        return live_enabled;
    }

    public void setLive_enabled(Integer live_enabled) {
        this.live_enabled = live_enabled;
    }

    public Integer getRec_enabled() {
        return rec_enabled;
    }

    public void setRec_enabled(Integer rec_enabled) {
        this.rec_enabled = rec_enabled;
    }

    public String getRecord_uuid() {
        return record_uuid;
    }

    public void setRecord_uuid(String record_uuid) {
        this.record_uuid = record_uuid;
    }

    public String getRtmp_url() {
        return rtmp_url;
    }

    public void setRtmp_url(String rtmp_url) {
        this.rtmp_url = rtmp_url;
    }

    public String getRecord_copy() {
        return record_copy;
    }

    public void setRecord_copy(String record_copy) {
        this.record_copy = record_copy;
    }

    public String[] getSpec_focus_usr_ids() {
        return spec_focus_usr_ids;
    }

    public void setSpec_focus_usr_ids(String[] spec_focus_usr_ids) {
        this.spec_focus_usr_ids = spec_focus_usr_ids;
    }

    public Long getFlv_fsize() {
        return flv_fsize;
    }

    public void setFlv_fsize(Long flv_fsize) {
        this.flv_fsize = flv_fsize;
    }

    public String getAuto_focus_usr_id() {
        return auto_focus_usr_id;
    }

    public void setAuto_focus_usr_id(String auto_focus_usr_id) {
        this.auto_focus_usr_id = auto_focus_usr_id;
    }

    public Integer getBypass_enabled() {
        return bypass_enabled;
    }

    public void setBypass_enabled(Integer bypass_enabled) {
        this.bypass_enabled = bypass_enabled;
    }
}
