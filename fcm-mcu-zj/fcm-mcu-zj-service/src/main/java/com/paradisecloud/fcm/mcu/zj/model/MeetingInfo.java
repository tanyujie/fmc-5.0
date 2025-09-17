package com.paradisecloud.fcm.mcu.zj.model;

public class MeetingInfo extends MeetingBaseInfo {

    private Integer mr_locked;
    private String launcher_usr_name;
    private String join_pwd;
    private String ctrl_pwd;
    private String supervisor_pwd;
    private String extras;
    private String live_pwd;
    private String uuid;
    private Integer auto_invite;
    private Integer all_guests_mute;
    private Integer allow_unmute_self;
    private Integer enable_guest;
    private Integer force_mcu_layout;
    private Integer same_layout;
    private Integer[] invite_endpoint_id;
    private Integer[] invite_usr_id;
    private Integer speaker_usr_id;
    private String focus_usr_id;
    private Integer mr_vtx_mode;

    public Integer getMr_locked() {
        return mr_locked;
    }

    public void setMr_locked(Integer mr_locked) {
        this.mr_locked = mr_locked;
    }

    public String getLauncher_usr_name() {
        return launcher_usr_name;
    }

    public void setLauncher_usr_name(String launcher_usr_name) {
        this.launcher_usr_name = launcher_usr_name;
    }

    public String getJoin_pwd() {
        return join_pwd;
    }

    public void setJoin_pwd(String join_pwd) {
        this.join_pwd = join_pwd;
    }

    public String getCtrl_pwd() {
        return ctrl_pwd;
    }

    public void setCtrl_pwd(String ctrl_pwd) {
        this.ctrl_pwd = ctrl_pwd;
    }

    public String getSupervisor_pwd() {
        return supervisor_pwd;
    }

    public void setSupervisor_pwd(String supervisor_pwd) {
        this.supervisor_pwd = supervisor_pwd;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getLive_pwd() {
        return live_pwd;
    }

    public void setLive_pwd(String live_pwd) {
        this.live_pwd = live_pwd;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getAuto_invite() {
        return auto_invite;
    }

    public void setAuto_invite(Integer auto_invite) {
        this.auto_invite = auto_invite;
    }

    public Integer getAll_guests_mute() {
        return all_guests_mute;
    }

    public void setAll_guests_mute(Integer all_guests_mute) {
        this.all_guests_mute = all_guests_mute;
    }

    public Integer getAllow_unmute_self() {
        return allow_unmute_self;
    }

    public void setAllow_unmute_self(Integer allow_unmute_self) {
        this.allow_unmute_self = allow_unmute_self;
    }

    public Integer getEnable_guest() {
        return enable_guest;
    }

    public void setEnable_guest(Integer enable_guest) {
        this.enable_guest = enable_guest;
    }

    public Integer getForce_mcu_layout() {
        return force_mcu_layout;
    }

    public void setForce_mcu_layout(Integer force_mcu_layout) {
        this.force_mcu_layout = force_mcu_layout;
    }

    public Integer getSame_layout() {
        return same_layout;
    }

    public void setSame_layout(Integer same_layout) {
        this.same_layout = same_layout;
    }

    public Integer[] getInvite_endpoint_id() {
        return invite_endpoint_id;
    }

    public void setInvite_endpoint_id(Integer[] invite_endpoint_id) {
        this.invite_endpoint_id = invite_endpoint_id;
    }

    public Integer[] getInvite_usr_id() {
        return invite_usr_id;
    }

    public void setInvite_usr_id(Integer[] invite_usr_id) {
        this.invite_usr_id = invite_usr_id;
    }

    public Integer getSpeaker_usr_id() {
        return speaker_usr_id;
    }

    public void setSpeaker_usr_id(Integer speaker_usr_id) {
        this.speaker_usr_id = speaker_usr_id;
    }

    public String getFocus_usr_id() {
        return focus_usr_id;
    }

    public void setFocus_usr_id(String focus_usr_id) {
        this.focus_usr_id = focus_usr_id;
    }

    public Integer getMr_vtx_mode() {
        return mr_vtx_mode;
    }

    public void setMr_vtx_mode(Integer mr_vtx_mode) {
        this.mr_vtx_mode = mr_vtx_mode;
    }
}
