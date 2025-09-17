package com.paradisecloud.fcm.mcu.zj.model.request.cm;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

public class CmModUsrRequest extends CommonRequest {

    private Integer usr_id;
    private String option;
    private String login_pwd;
    private String phone_no;
    private String nick_name;
    private Integer priority;
    private String email;
    private Integer usr_type;
    private String call_addr;
    private Integer ptotocol_type;
    private Integer max_room;
    private String duty;
    private Integer gender;
    private String fixed_phone;
    private Integer valid_period;

    public Integer getUsr_id() {
        return usr_id;
    }

    public void setUsr_id(Integer usr_id) {
        this.usr_id = usr_id;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getLogin_pwd() {
        return login_pwd;
    }

    public void setLogin_pwd(String login_pwd) {
        this.login_pwd = login_pwd;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getUsr_type() {
        return usr_type;
    }

    public void setUsr_type(Integer usr_type) {
        this.usr_type = usr_type;
    }

    public String getCall_addr() {
        return call_addr;
    }

    public void setCall_addr(String call_addr) {
        this.call_addr = call_addr;
    }

    public Integer getPtotocol_type() {
        return ptotocol_type;
    }

    public void setPtotocol_type(Integer ptotocol_type) {
        this.ptotocol_type = ptotocol_type;
    }

    public Integer getMax_room() {
        return max_room;
    }

    public void setMax_room(Integer max_room) {
        this.max_room = max_room;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getFixed_phone() {
        return fixed_phone;
    }

    public void setFixed_phone(String fixed_phone) {
        this.fixed_phone = fixed_phone;
    }

    public Integer getValid_period() {
        return valid_period;
    }

    public void setValid_period(Integer valid_period) {
        this.valid_period = valid_period;
    }
}
