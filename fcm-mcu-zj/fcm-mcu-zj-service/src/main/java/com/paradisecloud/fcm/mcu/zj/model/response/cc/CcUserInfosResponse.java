package com.paradisecloud.fcm.mcu.zj.model.response.cc;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

public class CcUserInfosResponse extends CommonResponse {

    private String[] usr_ids;
    private String[] nick_names;
    private String[] call_addrs;

    // 未完待续

    public String[] getUsr_ids() {
        return usr_ids;
    }

    public void setUsr_ids(String[] usr_ids) {
        this.usr_ids = usr_ids;
    }

    public String[] getNick_names() {
        return nick_names;
    }

    public void setNick_names(String[] nick_names) {
        this.nick_names = nick_names;
    }

    public String[] getCall_addrs() {
        return call_addrs;
    }

    public void setCall_addrs(String[] call_addrs) {
        this.call_addrs = call_addrs;
    }
}
