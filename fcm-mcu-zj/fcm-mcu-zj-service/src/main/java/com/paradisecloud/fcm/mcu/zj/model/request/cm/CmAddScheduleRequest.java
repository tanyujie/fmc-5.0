package com.paradisecloud.fcm.mcu.zj.model.request.cm;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

import java.util.ArrayList;
import java.util.List;

public class CmAddScheduleRequest extends CommonRequest {

    /**
     * cmdid : add_schedule
     * mr_id : 8001177
     * invite_dep_ids : []
     * invite_usr_ids : []
     * mr_name : lxl-TEST
     * join_pwd :
     * ctrl_pwd : 1177
     * duration : 7200
     * bandwidth : 1024
     * auto_invite : 1
     * auto_record : 0
     * extra : 刘禧龙测试会议
     * live_pwd :
     * features : 010001000001010
     * resource_template_id : 27
     * max_call : 1000
     * enable_guest : 1
     * force_mcu_layout : 1
     * mr_scene : conference
     * all_guests_mute : 0
     * speaker_usr_id : -1
     * allow_unmute_self : 1
     * same_layout : 0
     * mr_vtx_mode : 0
     * focus_usr_id :
     * live_push_url :
     * supervisor_pwd :
     * enable_bypass : 0
     * layout_pre_cfg : {"mosic_config":[{"mosic_id":"auto","role":"speaker","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":0},{"mosic_id":"auto","role":"chair","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":0},{"mosic_id":"auto","role":"guest","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":0}],"show_camera_off":1}
     */

    private int room_id;
    private String mr_name;
    private String join_pwd;
    private String ctrl_pwd;
    private int start_dtm;
    private int duration;
    private int bandwidth;
    private List<Integer> invite_dep_ids;
    private List<Integer> invite_usr_ids;
    private List<Integer> invite_endpoint_ids;
    private List<Integer> cared_status;
    private List<Integer> endpoint_cared_status;
    private String weekly_period;
    private int auto_invite;
    private int auto_record;
    private String extra;
    private String live_pwd;
    private String features;
    private int resource_template_id;
    private int max_call;
    private int enable_guest;
    private int force_mcu_layout;
    private String mr_scene;
    private int all_guests_mute;
    private int speaker_usr_id;
    private int allow_unmute_self;
    private int same_layout;
    private int mr_vtx_mode;
    private String focus_usr_id;
    private String live_push_url;
    private String supervisor_pwd;
    private int enable_bypass;
    private LayoutPreCfg layout_pre_cfg;

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public String getMr_name() {
        return mr_name;
    }

    public void setMr_name(String mr_name) {
        this.mr_name = mr_name;
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

    public int getStart_dtm() {
        return start_dtm;
    }

    public void setStart_dtm(int start_dtm) {
        this.start_dtm = start_dtm;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    public List<Integer> getInvite_dep_ids() {
        return invite_dep_ids;
    }

    public void setInvite_dep_ids(List<Integer> invite_dep_ids) {
        this.invite_dep_ids = invite_dep_ids;
    }

    public List<Integer> getInvite_usr_ids() {
        return invite_usr_ids;
    }

    public void setInvite_usr_ids(List<Integer> invite_usr_ids) {
        this.invite_usr_ids = invite_usr_ids;
    }

    public List<Integer> getInvite_endpoint_ids() {
        return invite_endpoint_ids;
    }

    public void setInvite_endpoint_ids(List<Integer> invite_endpoint_ids) {
        this.invite_endpoint_ids = invite_endpoint_ids;
    }

    public List<Integer> getCared_status() {
        return cared_status;
    }

    public void setCared_status(List<Integer> cared_status) {
        this.cared_status = cared_status;
    }

    public List<Integer> getEndpoint_cared_status() {
        return endpoint_cared_status;
    }

    public void setEndpoint_cared_status(List<Integer> endpoint_cared_status) {
        this.endpoint_cared_status = endpoint_cared_status;
    }

    public String getWeekly_period() {
        return weekly_period;
    }

    public void setWeekly_period(String weekly_period) {
        this.weekly_period = weekly_period;
    }

    public int getAuto_invite() {
        return auto_invite;
    }

    public void setAuto_invite(int auto_invite) {
        this.auto_invite = auto_invite;
    }

    public int getAuto_record() {
        return auto_record;
    }

    public void setAuto_record(int auto_record) {
        this.auto_record = auto_record;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getLive_pwd() {
        return live_pwd;
    }

    public void setLive_pwd(String live_pwd) {
        this.live_pwd = live_pwd;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public int getResource_template_id() {
        return resource_template_id;
    }

    public void setResource_template_id(int resource_template_id) {
        this.resource_template_id = resource_template_id;
    }

    public int getMax_call() {
        return max_call;
    }

    public void setMax_call(int max_call) {
        this.max_call = max_call;
    }

    public int getEnable_guest() {
        return enable_guest;
    }

    public void setEnable_guest(int enable_guest) {
        this.enable_guest = enable_guest;
    }

    public int getForce_mcu_layout() {
        return force_mcu_layout;
    }

    public void setForce_mcu_layout(int force_mcu_layout) {
        this.force_mcu_layout = force_mcu_layout;
    }

    public String getMr_scene() {
        return mr_scene;
    }

    public void setMr_scene(String mr_scene) {
        this.mr_scene = mr_scene;
    }

    public int getAll_guests_mute() {
        return all_guests_mute;
    }

    public void setAll_guests_mute(int all_guests_mute) {
        this.all_guests_mute = all_guests_mute;
    }

    public int getSpeaker_usr_id() {
        return speaker_usr_id;
    }

    public void setSpeaker_usr_id(int speaker_usr_id) {
        this.speaker_usr_id = speaker_usr_id;
    }

    public int getAllow_unmute_self() {
        return allow_unmute_self;
    }

    public void setAllow_unmute_self(int allow_unmute_self) {
        this.allow_unmute_self = allow_unmute_self;
    }

    public int getSame_layout() {
        return same_layout;
    }

    public void setSame_layout(int same_layout) {
        this.same_layout = same_layout;
    }

    public int getMr_vtx_mode() {
        return mr_vtx_mode;
    }

    public void setMr_vtx_mode(int mr_vtx_mode) {
        this.mr_vtx_mode = mr_vtx_mode;
    }

    public String getFocus_usr_id() {
        return focus_usr_id;
    }

    public void setFocus_usr_id(String focus_usr_id) {
        this.focus_usr_id = focus_usr_id;
    }

    public String getLive_push_url() {
        return live_push_url;
    }

    public void setLive_push_url(String live_push_url) {
        this.live_push_url = live_push_url;
    }

    public String getSupervisor_pwd() {
        return supervisor_pwd;
    }

    public void setSupervisor_pwd(String supervisor_pwd) {
        this.supervisor_pwd = supervisor_pwd;
    }

    public int getEnable_bypass() {
        return enable_bypass;
    }

    public void setEnable_bypass(int enable_bypass) {
        this.enable_bypass = enable_bypass;
    }

    public LayoutPreCfg getLayout_pre_cfg() {
        return layout_pre_cfg;
    }

    public void setLayout_pre_cfg(LayoutPreCfg layout_pre_cfg) {
        this.layout_pre_cfg = layout_pre_cfg;
    }

    public static class LayoutPreCfg {
        /**
         * mosic_config : [{"mosic_id":"auto","role":"speaker","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":0},{"mosic_id":"auto","role":"chair","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":0},{"mosic_id":"auto","role":"guest","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":0}]
         * show_camera_off : 1
         */

        private int show_camera_off;
        private List<LayoutPreCfg.MosicConfig> mosic_config;

        public int getShow_camera_off() {
            return show_camera_off;
        }

        public void setShow_camera_off(int show_camera_off) {
            this.show_camera_off = show_camera_off;
        }

        public List<LayoutPreCfg.MosicConfig> getMosic_config() {
            return mosic_config;
        }

        public void setMosic_config(List<LayoutPreCfg.MosicConfig> mosic_config) {
            this.mosic_config = mosic_config;
        }

        public static class MosicConfig {
            /**
             * mosic_id : auto
             * role : speaker
             * roles_lst : []
             * layout_mode : 2
             * poll_secs : 10
             * view_has_self : 0
             */

            private String mosic_id;
            private String role;
            private int layout_mode;
            private int poll_secs;
            private int view_has_self;
            private List<?> roles_lst;

            public String getMosic_id() {
                return mosic_id;
            }

            public void setMosic_id(String mosic_id) {
                this.mosic_id = mosic_id;
            }

            public String getRole() {
                return role;
            }

            public void setRole(String role) {
                this.role = role;
            }

            public int getLayout_mode() {
                return layout_mode;
            }

            public void setLayout_mode(int layout_mode) {
                this.layout_mode = layout_mode;
            }

            public int getPoll_secs() {
                return poll_secs;
            }

            public void setPoll_secs(int poll_secs) {
                this.poll_secs = poll_secs;
            }

            public int getView_has_self() {
                return view_has_self;
            }

            public void setView_has_self(int view_has_self) {
                this.view_has_self = view_has_self;
            }

            public List<?> getRoles_lst() {
                return roles_lst;
            }

            public void setRoles_lst(List<?> roles_lst) {
                this.roles_lst = roles_lst;
            }
        }
    }

    public static CmAddScheduleRequest buildDefaultRequest() {
        CmAddScheduleRequest cmAddSchedulesRequest = new CmAddScheduleRequest();
        cmAddSchedulesRequest.setRoom_id(0);// 1
        cmAddSchedulesRequest.setCtrl_pwd("");// 123456
        cmAddSchedulesRequest.setJoin_pwd("");// 1111
        cmAddSchedulesRequest.setSupervisor_pwd("");
        cmAddSchedulesRequest.setStart_dtm(0);
        cmAddSchedulesRequest.setDuration(172800);// 360000000
        cmAddSchedulesRequest.setBandwidth(2048);
        cmAddSchedulesRequest.setWeekly_period("0");
        cmAddSchedulesRequest.setExtra("");
        cmAddSchedulesRequest.setLive_pwd("");
        cmAddSchedulesRequest.setLive_push_url("");
        cmAddSchedulesRequest.setAuto_invite(0);
        cmAddSchedulesRequest.setAuto_record(0);
        cmAddSchedulesRequest.setResource_template_id(27);
        cmAddSchedulesRequest.setMax_call(1000);
        cmAddSchedulesRequest.setEnable_guest(1);
        cmAddSchedulesRequest.setForce_mcu_layout(1);
        cmAddSchedulesRequest.setMr_scene("conference");
        cmAddSchedulesRequest.setSame_layout(1);
        cmAddSchedulesRequest.setAll_guests_mute(1);
        cmAddSchedulesRequest.setMr_vtx_mode(0);
        cmAddSchedulesRequest.setSpeaker_usr_id(-1);
        cmAddSchedulesRequest.setAllow_unmute_self(0);
        cmAddSchedulesRequest.setEnable_bypass(0);
        cmAddSchedulesRequest.setFeatures("010011100001110");
        cmAddSchedulesRequest.setFocus_usr_id("");
        LayoutPreCfg layoutPreCfg = new LayoutPreCfg();
        layoutPreCfg.setShow_camera_off(1);

        LayoutPreCfg.MosicConfig mosicConfig = new LayoutPreCfg.MosicConfig();
        mosicConfig.setRole("speaker");
        mosicConfig.setView_has_self(0);
        mosicConfig.setMosic_id("auto");
        mosicConfig.setRoles_lst(new ArrayList<>());
        mosicConfig.setView_has_self(1);
        mosicConfig.setPoll_secs(10);
        mosicConfig.setLayout_mode(2);
        LayoutPreCfg.MosicConfig mosicConfig2 = new LayoutPreCfg.MosicConfig();
        mosicConfig2.setRole("chair");
        mosicConfig2.setView_has_self(0);
        mosicConfig2.setMosic_id("auto");
        mosicConfig2.setRoles_lst(new ArrayList<>());
        mosicConfig2.setView_has_self(0);
        mosicConfig2.setPoll_secs(10);
        mosicConfig2.setLayout_mode(2);
        LayoutPreCfg.MosicConfig mosicConfig3 = new LayoutPreCfg.MosicConfig();
        mosicConfig3.setRole("guest");
        mosicConfig3.setView_has_self(0);
        mosicConfig3.setMosic_id("auto");
        mosicConfig3.setRoles_lst(new ArrayList<>());
        mosicConfig3.setView_has_self(0);
        mosicConfig3.setPoll_secs(10);
        mosicConfig3.setLayout_mode(2);
        List<LayoutPreCfg.MosicConfig> mosicConfigList = new ArrayList<>();
        mosicConfigList.add(mosicConfig);
        mosicConfigList.add(mosicConfig2);
        mosicConfigList.add(mosicConfig3);

        layoutPreCfg.setMosic_config(mosicConfigList);
        cmAddSchedulesRequest.setLayout_pre_cfg(layoutPreCfg);

        return cmAddSchedulesRequest;
    }
}
