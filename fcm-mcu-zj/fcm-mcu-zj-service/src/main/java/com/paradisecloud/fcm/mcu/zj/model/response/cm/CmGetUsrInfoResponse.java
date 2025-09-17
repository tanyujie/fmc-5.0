package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

public class CmGetUsrInfoResponse extends CommonResponse {

    private Integer[] usr_ids;
    private String[] cuids;
    private String[] usr_marks;
    private String[] login_ids;
    private String[] nick_names;
    private String[] call_addrs;

    // 未完待续

    public Integer[] getUsr_ids() {
        return usr_ids;
    }

    public void setUsr_ids(Integer[] usr_ids) {
        this.usr_ids = usr_ids;
    }

    public String[] getCuids() {
        return cuids;
    }

    public void setCuids(String[] cuids) {
        this.cuids = cuids;
    }

    public String[] getUsr_marks() {
        return usr_marks;
    }

    public void setUsr_marks(String[] usr_marks) {
        this.usr_marks = usr_marks;
    }

    public String[] getNick_names() {
        return nick_names;
    }

    public String[] getLogin_ids() {
        return login_ids;
    }

    public void setLogin_ids(String[] login_ids) {
        this.login_ids = login_ids;
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
