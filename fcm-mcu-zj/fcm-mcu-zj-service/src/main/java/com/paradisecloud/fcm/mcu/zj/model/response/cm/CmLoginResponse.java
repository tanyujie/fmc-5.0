package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

public class CmLoginResponse extends CommonResponse {

    private String tenant_id;
    private String result;
    private int usr_type;
    private String last_login_ip;
    private int last_login_time;
    private String session_id;
    private String activate;
    private int is_email_global_config;
    private int is_pwd_changed;
    private String[] tenant_tags;
    private String usr_mark;
    private String admin_nick_name;
    private Long[] manager_deps;
    private String[] manager_privileges;
    private String pwd_regulation;
    private int stand_alone;
    private String nick_name;
    private Integer[][] all_manager_senior_dep_ids;
    private Integer[][] all_senior_dep_ids;

    public String getTenant_id() {
        return tenant_id;
    }

    public void setTenant_id(String tenant_id) {
        this.tenant_id = tenant_id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getUsr_type() {
        return usr_type;
    }

    public void setUsr_type(int usr_type) {
        this.usr_type = usr_type;
    }

    public String getLast_login_ip() {
        return last_login_ip;
    }

    public void setLast_login_ip(String last_login_ip) {
        this.last_login_ip = last_login_ip;
    }

    public int getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(int last_login_time) {
        this.last_login_time = last_login_time;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getActivate() {
        return activate;
    }

    public void setActivate(String activate) {
        this.activate = activate;
    }

    public int getIs_email_global_config() {
        return is_email_global_config;
    }

    public void setIs_email_global_config(int is_email_global_config) {
        this.is_email_global_config = is_email_global_config;
    }

    public int getIs_pwd_changed() {
        return is_pwd_changed;
    }

    public void setIs_pwd_changed(int is_pwd_changed) {
        this.is_pwd_changed = is_pwd_changed;
    }

    public String[] getTenant_tags() {
        return tenant_tags;
    }

    public void setTenant_tags(String[] tenant_tags) {
        this.tenant_tags = tenant_tags;
    }

    public String getUsr_mark() {
        return usr_mark;
    }

    public void setUsr_mark(String usr_mark) {
        this.usr_mark = usr_mark;
    }

    public String getAdmin_nick_name() {
        return admin_nick_name;
    }

    public void setAdmin_nick_name(String admin_nick_name) {
        this.admin_nick_name = admin_nick_name;
    }

    public Long[] getManager_deps() {
        return manager_deps;
    }

    public void setManager_deps(Long[] manager_deps) {
        this.manager_deps = manager_deps;
    }

    public String[] getManager_privileges() {
        return manager_privileges;
    }

    public void setManager_privileges(String[] manager_privileges) {
        this.manager_privileges = manager_privileges;
    }

    public String getPwd_regulation() {
        return pwd_regulation;
    }

    public void setPwd_regulation(String pwd_regulation) {
        this.pwd_regulation = pwd_regulation;
    }

    public int getStand_alone() {
        return stand_alone;
    }

    public void setStand_alone(int stand_alone) {
        this.stand_alone = stand_alone;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public Integer[][] getAll_manager_senior_dep_ids() {
        return all_manager_senior_dep_ids;
    }

    public void setAll_manager_senior_dep_ids(Integer[][] all_manager_senior_dep_ids) {
        this.all_manager_senior_dep_ids = all_manager_senior_dep_ids;
    }

    public Integer[][] getAll_senior_dep_ids() {
        return all_senior_dep_ids;
    }

    public void setAll_senior_dep_ids(Integer[][] all_senior_dep_ids) {
        this.all_senior_dep_ids = all_senior_dep_ids;
    }
}
