package com.paradisecloud.fcm.mcu.zj.model.response.cc;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

public class CcLoginResponse extends CommonResponse {

    private String mr_id;
    private String result;
    private String sess_id;
    private Integer mr_start_dtm;
    private Integer schedule_start_dtm;
    private String schedule_name;
    private Integer stand_alone;
    private String extra;
    private Integer invalid_time;
    private String after_sale_mgr_email;
    private String expired_notify;

    public String getMr_id() {
        return mr_id;
    }

    public void setMr_id(String mr_id) {
        this.mr_id = mr_id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getSess_id() {
        return sess_id;
    }

    public void setSess_id(String sess_id) {
        this.sess_id = sess_id;
    }

    public Integer getMr_start_dtm() {
        return mr_start_dtm;
    }

    public void setMr_start_dtm(Integer mr_start_dtm) {
        this.mr_start_dtm = mr_start_dtm;
    }

    public Integer getSchedule_start_dtm() {
        return schedule_start_dtm;
    }

    public void setSchedule_start_dtm(Integer schedule_start_dtm) {
        this.schedule_start_dtm = schedule_start_dtm;
    }

    public String getSchedule_name() {
        return schedule_name;
    }

    public void setSchedule_name(String schedule_name) {
        this.schedule_name = schedule_name;
    }

    public Integer getStand_alone() {
        return stand_alone;
    }

    public void setStand_alone(Integer stand_alone) {
        this.stand_alone = stand_alone;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public Integer getInvalid_time() {
        return invalid_time;
    }

    public void setInvalid_time(Integer invalid_time) {
        this.invalid_time = invalid_time;
    }

    public String getAfter_sale_mgr_email() {
        return after_sale_mgr_email;
    }

    public void setAfter_sale_mgr_email(String after_sale_mgr_email) {
        this.after_sale_mgr_email = after_sale_mgr_email;
    }

    public String getExpired_notify() {
        return expired_notify;
    }

    public void setExpired_notify(String expired_notify) {
        this.expired_notify = expired_notify;
    }
}
