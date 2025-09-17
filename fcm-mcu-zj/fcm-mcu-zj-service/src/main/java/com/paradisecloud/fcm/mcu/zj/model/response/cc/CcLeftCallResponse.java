package com.paradisecloud.fcm.mcu.zj.model.response.cc;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

public class CcLeftCallResponse extends CommonResponse {

    private Integer ep_id;
    private String reason;
    private Integer duration;
    private String nick_name;

    public Integer getEp_id() {
        return ep_id;
    }

    public void setEp_id(Integer ep_id) {
        this.ep_id = ep_id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }
}
