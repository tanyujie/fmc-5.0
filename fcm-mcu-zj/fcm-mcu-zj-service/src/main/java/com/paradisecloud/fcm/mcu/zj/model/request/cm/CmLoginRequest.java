package com.paradisecloud.fcm.mcu.zj.model.request.cm;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

import java.math.BigDecimal;

public class CmLoginRequest extends CommonRequest {

    private String login_id;
    private String login_pwd;
    private int login_type;
    private String cert_code;
    private BigDecimal date_time;

    public String getLogin_id() {
        return login_id;
    }

    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }

    public String getLogin_pwd() {
        return login_pwd;
    }

    public void setLogin_pwd(String login_pwd) {
        this.login_pwd = login_pwd;
    }

    public int getLogin_type() {
        return login_type;
    }

    public void setLogin_type(int login_type) {
        this.login_type = login_type;
    }

    public String getCert_code() {
        return cert_code;
    }

    public void setCert_code(String cert_code) {
        this.cert_code = cert_code;
    }

    public BigDecimal getDate_time() {
        return date_time;
    }

    public void setDate_time(BigDecimal date_time) {
        this.date_time = date_time;
    }
}
