package com.paradisecloud.fcm.mcu.zj.model.request.cm;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

import java.util.ArrayList;
import java.util.List;

public class CmAddUsrRequest extends CommonRequest {

    private String usr_mark;
    private String option;
    private String login_id;
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
    private Integer is_endpoint;
    private Integer valid_period;
    private List<Integer> belong_to_departments;
    private Integer send_invite_email;
    private String server_addr;

    public String getUsr_mark() {
        return usr_mark;
    }

    public void setUsr_mark(String usr_mark) {
        this.usr_mark = usr_mark;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

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

    public Integer getIs_endpoint() {
        return is_endpoint;
    }

    public void setIs_endpoint(Integer is_endpoint) {
        this.is_endpoint = is_endpoint;
    }

    public Integer getValid_period() {
        return valid_period;
    }

    public void setValid_period(Integer valid_period) {
        this.valid_period = valid_period;
    }

    public List<Integer> getBelong_to_departments() {
        return belong_to_departments;
    }

    public void setBelong_to_departments(List<Integer> belong_to_departments) {
        this.belong_to_departments = belong_to_departments;
    }

    public Integer getSend_invite_email() {
        return send_invite_email;
    }

    public void setSend_invite_email(Integer send_invite_email) {
        this.send_invite_email = send_invite_email;
    }

    public String getServer_addr() {
        return server_addr;
    }

    public void setServer_addr(String server_addr) {
        this.server_addr = server_addr;
    }

    public static CmAddUsrRequest buildDefaultRequestForAddEps() {
        CmAddUsrRequest cmAddUsrRequest = new CmAddUsrRequest();
        cmAddUsrRequest.setNick_name("");
        cmAddUsrRequest.setLogin_id("");
        cmAddUsrRequest.setLogin_pwd("");
        cmAddUsrRequest.setUsr_mark("");
        List<Integer> belongToDepartments = new ArrayList<>();
        belongToDepartments.add(2);// 总部
        cmAddUsrRequest.setBelong_to_departments(belongToDepartments);
        cmAddUsrRequest.setCall_addr("");
        cmAddUsrRequest.setPtotocol_type(2);//  1 H323 协议， 2 SIP 协议， 3 多流协议， 5：RTSP 协议， 默认为多流协议
        cmAddUsrRequest.setOption("endpoint");
        cmAddUsrRequest.setIs_endpoint(1);

        return cmAddUsrRequest;
    }
}
