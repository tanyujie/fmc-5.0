package com.paradisecloud.fcm.mcu.zj.model.request.cm;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

import java.util.ArrayList;
import java.util.List;

public class CmAddRoomRequest extends CommonRequest {


    /**
     * mr_scene : conference
     * room_mark : 8547
     * room_name : 会议室8547
     * bandwidth : 1024
     * join_pwd : 5554
     * ctrl_pwd : 8547
     * live_pwd : 5465
     * supervisor_pwd : 6666
     * max_call : 1000
     * room_type : 2
     * belong_to_departments : [2]
     * resource_template_id : 27
     * disable : 0
     * force_mcu_layout : 1
     * same_layout : 1
     * enable_guest : 1
     * region_id : -1
     * enable_adhoc : 0
     * all_guests_mute : 1
     * mr_vtx_mode : 1
     * allow_unmute_self : 1
     * speaker_usr_id : 4
     * enable_bypass : 1
     * live_push_url : cnRtcDovLzE3Mi4xNi4wLjIwMC9saXZlL2hk
     * layout_pre_cfg : {"show_camera_off":0,"mosic_config":[{"role":"speaker","view_has_self":0},{"role":"chair","view_has_self":0}]}
     * focus_speaker : 1
     * features : 011011111111111
     * focus_usr_id : 4
     */

    private String mr_scene;
    private String room_mark;
    private String room_name;
    private int bandwidth;
    private String join_pwd;
    private String ctrl_pwd;
    private String live_pwd;
    private String supervisor_pwd;
    private int duration;
    private int max_call;
    private int room_type;
    private int resource_template_id;
    private int disable;
    private int force_mcu_layout;
    private int same_layout;
    private int enable_guest;
    private String region_id;
    private int enable_adhoc;
    private int all_guests_mute;
    private int mr_vtx_mode;
    private int allow_unmute_self;
    private int speaker_usr_id;
    private int enable_bypass;
    private String live_push_url;
    private LayoutPreCfg layout_pre_cfg;
    private int focus_speaker;
    private String features;
    private String focus_usr_id;
    private List<Integer> belong_to_departments;

    public String getMr_scene() {
        return mr_scene;
    }

    public void setMr_scene(String mr_scene) {
        this.mr_scene = mr_scene;
    }

    public String getRoom_mark() {
        return room_mark;
    }

    public void setRoom_mark(String room_mark) {
        this.room_mark = room_mark;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public int getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(int bandwidth) {
        this.bandwidth = bandwidth;
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

    public String getLive_pwd() {
        return live_pwd;
    }

    public void setLive_pwd(String live_pwd) {
        this.live_pwd = live_pwd;
    }

    public String getSupervisor_pwd() {
        return supervisor_pwd;
    }

    public void setSupervisor_pwd(String supervisor_pwd) {
        this.supervisor_pwd = supervisor_pwd;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMax_call() {
        return max_call;
    }

    public void setMax_call(int max_call) {
        this.max_call = max_call;
    }

    public int getRoom_type() {
        return room_type;
    }

    public void setRoom_type(int room_type) {
        this.room_type = room_type;
    }

    public int getResource_template_id() {
        return resource_template_id;
    }

    public void setResource_template_id(int resource_template_id) {
        this.resource_template_id = resource_template_id;
    }

    public int getDisable() {
        return disable;
    }

    public void setDisable(int disable) {
        this.disable = disable;
    }

    public int getForce_mcu_layout() {
        return force_mcu_layout;
    }

    public void setForce_mcu_layout(int force_mcu_layout) {
        this.force_mcu_layout = force_mcu_layout;
    }

    public int getSame_layout() {
        return same_layout;
    }

    public void setSame_layout(int same_layout) {
        this.same_layout = same_layout;
    }

    public int getEnable_guest() {
        return enable_guest;
    }

    public void setEnable_guest(int enable_guest) {
        this.enable_guest = enable_guest;
    }

    public String getRegion_id() {
        return region_id;
    }

    public void setRegion_id(String region_id) {
        this.region_id = region_id;
    }

    public int getEnable_adhoc() {
        return enable_adhoc;
    }

    public void setEnable_adhoc(int enable_adhoc) {
        this.enable_adhoc = enable_adhoc;
    }

    public int getAll_guests_mute() {
        return all_guests_mute;
    }

    public void setAll_guests_mute(int all_guests_mute) {
        this.all_guests_mute = all_guests_mute;
    }

    public int getMr_vtx_mode() {
        return mr_vtx_mode;
    }

    public void setMr_vtx_mode(int mr_vtx_mode) {
        this.mr_vtx_mode = mr_vtx_mode;
    }

    public int getAllow_unmute_self() {
        return allow_unmute_self;
    }

    public void setAllow_unmute_self(int allow_unmute_self) {
        this.allow_unmute_self = allow_unmute_self;
    }

    public int getSpeaker_usr_id() {
        return speaker_usr_id;
    }

    public void setSpeaker_usr_id(int speaker_usr_id) {
        this.speaker_usr_id = speaker_usr_id;
    }

    public int getEnable_bypass() {
        return enable_bypass;
    }

    public void setEnable_bypass(int enable_bypass) {
        this.enable_bypass = enable_bypass;
    }

    public String getLive_push_url() {
        return live_push_url;
    }

    public void setLive_push_url(String live_push_url) {
        this.live_push_url = live_push_url;
    }

    public LayoutPreCfg getLayout_pre_cfg() {
        return layout_pre_cfg;
    }

    public void setLayout_pre_cfg(LayoutPreCfg layout_pre_cfg) {
        this.layout_pre_cfg = layout_pre_cfg;
    }

    public int getFocus_speaker() {
        return focus_speaker;
    }

    public void setFocus_speaker(int focus_speaker) {
        this.focus_speaker = focus_speaker;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getFocus_usr_id() {
        return focus_usr_id;
    }

    public void setFocus_usr_id(String focus_usr_id) {
        this.focus_usr_id = focus_usr_id;
    }

    public List<Integer> getBelong_to_departments() {
        return belong_to_departments;
    }

    public void setBelong_to_departments(List<Integer> belong_to_departments) {
        this.belong_to_departments = belong_to_departments;
    }

    public static class LayoutPreCfg {
        /**
         * show_camera_off : 0
         * mosic_config : [{"role":"speaker","view_has_self":0},{"role":"chair","view_has_self":0}]
         */

        private int show_camera_off;
        private List<MosicConfig> mosic_config;

        public int getShow_camera_off() {
            return show_camera_off;
        }

        public void setShow_camera_off(int show_camera_off) {
            this.show_camera_off = show_camera_off;
        }

        public List<MosicConfig> getMosic_config() {
            return mosic_config;
        }

        public void setMosic_config(List<MosicConfig> mosic_config) {
            this.mosic_config = mosic_config;
        }

        public static class MosicConfig {
            /**
             * role : speaker
             * view_has_self : 0
             */

            private String role;
            private int view_has_self;

            public String getRole() {
                return role;
            }

            public void setRole(String role) {
                this.role = role;
            }

            public int getView_has_self() {
                return view_has_self;
            }

            public void setView_has_self(int view_has_self) {
                this.view_has_self = view_has_self;
            }
        }
    }

    public static CmAddRoomRequest buildDefaultRequest() {
        CmAddRoomRequest cmAddRoomRequest = new CmAddRoomRequest();
        cmAddRoomRequest.setRoom_name("");// 会议室81234
        cmAddRoomRequest.setRoom_mark("");// 81234
        cmAddRoomRequest.setCtrl_pwd("");// 123456
        cmAddRoomRequest.setJoin_pwd("");
        cmAddRoomRequest.setLive_pwd("");
        cmAddRoomRequest.setSupervisor_pwd("");
        cmAddRoomRequest.setDuration(172800);
        cmAddRoomRequest.setLive_push_url("");
        List<Integer> belong_to_departments = new ArrayList<>();
        belong_to_departments.add(2);
        cmAddRoomRequest.setBelong_to_departments(belong_to_departments);
        cmAddRoomRequest.setRoom_type(2);
        cmAddRoomRequest.setMr_scene("conference");
        cmAddRoomRequest.setBandwidth(1024);
        cmAddRoomRequest.setMax_call(1000);
        cmAddRoomRequest.setResource_template_id(27);
        cmAddRoomRequest.setDisable(0);
        cmAddRoomRequest.setForce_mcu_layout(1);
        cmAddRoomRequest.setAllow_unmute_self(0);
        cmAddRoomRequest.setEnable_bypass(0);
        cmAddRoomRequest.setFocus_usr_id("");
        cmAddRoomRequest.setSpeaker_usr_id(-1);
        cmAddRoomRequest.setSame_layout(0);
        cmAddRoomRequest.setEnable_guest(1);
        cmAddRoomRequest.setRegion_id("-1");
        cmAddRoomRequest.setEnable_adhoc(1);
        cmAddRoomRequest.setAll_guests_mute(1);
        cmAddRoomRequest.setMr_vtx_mode(0);
        LayoutPreCfg layoutPreCfg = new LayoutPreCfg();
        layoutPreCfg.setShow_camera_off(1);

        LayoutPreCfg.MosicConfig mosicConfig = new LayoutPreCfg.MosicConfig();
        mosicConfig.setRole("speaker");
        mosicConfig.setView_has_self(1);
        LayoutPreCfg.MosicConfig mosicConfig2 = new LayoutPreCfg.MosicConfig();
        mosicConfig2.setRole("chair");
        mosicConfig2.setView_has_self(1);
        LayoutPreCfg.MosicConfig mosicConfig3 = new LayoutPreCfg.MosicConfig();
        mosicConfig3.setRole("guest");
        mosicConfig3.setView_has_self(1);
        List<LayoutPreCfg.MosicConfig> mosicConfigList = new ArrayList<>();
        mosicConfigList.add(mosicConfig);
        mosicConfigList.add(mosicConfig2);
//        mosicConfigList.add(mosicConfig3);

        layoutPreCfg.setMosic_config(mosicConfigList);
        cmAddRoomRequest.setLayout_pre_cfg(layoutPreCfg);
        cmAddRoomRequest.setFocus_speaker(0);
        cmAddRoomRequest.setFeatures("010011100001110");

        return cmAddRoomRequest;
    }
}
