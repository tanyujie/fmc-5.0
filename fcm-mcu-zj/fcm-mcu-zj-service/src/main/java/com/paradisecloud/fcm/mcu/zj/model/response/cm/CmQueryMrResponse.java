package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

public class CmQueryMrResponse extends CommonResponse {

    private String[] mr_ids;
    private Integer[] create_dtms;
    private Integer[] mr_lockeds;
    private String[] mr_names;
    private Integer[] durations;
    private Integer[] bandwidths;
    private String[] launcher_usr_names;
    private String[] join_pwds;
    private String[] ctrl_pwds;
    private String[] supervisor_pwds;
    private String[] extras;
    private Integer[] mr_modes;
    private String[] live_pwds;
    private String[] uuids;
    private Integer[] auto_invites;
    private Integer[] all_guests_mutes;
    private Integer[] allow_unmute_selfs;
    private Integer[] enable_guests;
    private String[] features;
    private Integer[] force_mcu_layouts;
    private Integer[] same_layouts;
    private Integer[][] invite_endpoint_ids;
    private Integer[][] invite_usr_ids;
    private Integer[] speaker_usr_ids;
    private String[] focus_usr_ids;
    private Integer[] mr_vtx_mode;

    public String[] getMr_ids() {
        return mr_ids;
    }

    public void setMr_ids(String[] mr_ids) {
        this.mr_ids = mr_ids;
    }

    public Integer[] getCreate_dtms() {
        return create_dtms;
    }

    public void setCreate_dtms(Integer[] create_dtms) {
        this.create_dtms = create_dtms;
    }

    public Integer[] getMr_lockeds() {
        return mr_lockeds;
    }

    public void setMr_lockeds(Integer[] mr_lockeds) {
        this.mr_lockeds = mr_lockeds;
    }

    public String[] getMr_names() {
        return mr_names;
    }

    public void setMr_names(String[] mr_names) {
        this.mr_names = mr_names;
    }

    public Integer[] getDurations() {
        return durations;
    }

    public void setDurations(Integer[] durations) {
        this.durations = durations;
    }

    public Integer[] getBandwidths() {
        return bandwidths;
    }

    public void setBandwidths(Integer[] bandwidths) {
        this.bandwidths = bandwidths;
    }

    public String[] getLauncher_usr_names() {
        return launcher_usr_names;
    }

    public void setLauncher_usr_names(String[] launcher_usr_names) {
        this.launcher_usr_names = launcher_usr_names;
    }

    public String[] getJoin_pwds() {
        return join_pwds;
    }

    public void setJoin_pwds(String[] join_pwds) {
        this.join_pwds = join_pwds;
    }

    public String[] getCtrl_pwds() {
        return ctrl_pwds;
    }

    public void setCtrl_pwds(String[] ctrl_pwds) {
        this.ctrl_pwds = ctrl_pwds;
    }

    public String[] getSupervisor_pwds() {
        return supervisor_pwds;
    }

    public void setSupervisor_pwds(String[] supervisor_pwds) {
        this.supervisor_pwds = supervisor_pwds;
    }

    public String[] getExtras() {
        return extras;
    }

    public void setExtras(String[] extras) {
        this.extras = extras;
    }

    public Integer[] getMr_modes() {
        return mr_modes;
    }

    public void setMr_modes(Integer[] mr_modes) {
        this.mr_modes = mr_modes;
    }

    public String[] getLive_pwds() {
        return live_pwds;
    }

    public void setLive_pwds(String[] live_pwds) {
        this.live_pwds = live_pwds;
    }

    public String[] getUuids() {
        return uuids;
    }

    public void setUuids(String[] uuids) {
        this.uuids = uuids;
    }

    public Integer[] getAuto_invites() {
        return auto_invites;
    }

    public void setAuto_invites(Integer[] auto_invites) {
        this.auto_invites = auto_invites;
    }

    public Integer[] getAll_guests_mutes() {
        return all_guests_mutes;
    }

    public void setAll_guests_mutes(Integer[] all_guests_mutes) {
        this.all_guests_mutes = all_guests_mutes;
    }

    public Integer[] getAllow_unmute_selfs() {
        return allow_unmute_selfs;
    }

    public void setAllow_unmute_selfs(Integer[] allow_unmute_selfs) {
        this.allow_unmute_selfs = allow_unmute_selfs;
    }

    public Integer[] getEnable_guests() {
        return enable_guests;
    }

    public void setEnable_guests(Integer[] enable_guests) {
        this.enable_guests = enable_guests;
    }

    public String[] getFeatures() {
        return features;
    }

    public void setFeatures(String[] features) {
        this.features = features;
    }

    public Integer[] getForce_mcu_layouts() {
        return force_mcu_layouts;
    }

    public void setForce_mcu_layouts(Integer[] force_mcu_layouts) {
        this.force_mcu_layouts = force_mcu_layouts;
    }

    public Integer[] getSame_layouts() {
        return same_layouts;
    }

    public void setSame_layouts(Integer[] same_layouts) {
        this.same_layouts = same_layouts;
    }

    public Integer[][] getInvite_endpoint_ids() {
        return invite_endpoint_ids;
    }

    public void setInvite_endpoint_ids(Integer[][] invite_endpoint_ids) {
        this.invite_endpoint_ids = invite_endpoint_ids;
    }

    public Integer[][] getInvite_usr_ids() {
        return invite_usr_ids;
    }

    public void setInvite_usr_ids(Integer[][] invite_usr_ids) {
        this.invite_usr_ids = invite_usr_ids;
    }

    public Integer[] getSpeaker_usr_ids() {
        return speaker_usr_ids;
    }

    public void setSpeaker_usr_ids(Integer[] speaker_usr_ids) {
        this.speaker_usr_ids = speaker_usr_ids;
    }

    public String[] getFocus_usr_ids() {
        return focus_usr_ids;
    }

    public void setFocus_usr_ids(String[] focus_usr_ids) {
        this.focus_usr_ids = focus_usr_ids;
    }

    public Integer[] getMr_vtx_mode() {
        return mr_vtx_mode;
    }

    public void setMr_vtx_mode(Integer[] mr_vtx_mode) {
        this.mr_vtx_mode = mr_vtx_mode;
    }
}
