package com.paradisecloud.fcm.mcu.zj.model.request.cm;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CmModRoomRequest extends CommonRequest {

    /**
     * room_id : 28
     * room_mark : 8547
     * room_name : 8547
     * bandwidth : 128
     * join_pwd : 4324
     * max_call : 100
     * ctrl_pwd : 3324
     * supervisor_pwd : 3222
     * live_pwd : 3422
     * room_type : 2
     * resource_template_id : 5
     * enable_guest : 1
     * force_mcu_layout : 1
     * same_layout : 1
     * region_id : 0
     * enable_adhoc : 0
     * mr_scene : conference
     * all_guests_mute : 1
     * mr_vtx_mode : 1
     * allow_unmute_self : 1
     * speaker_usr_id : 4
     * live_push_url : cnRtcDovLzE3Mi4xNi4wLjIwMC9saXZlL2hk
     * enable_bypass : 1
     * focus_speaker : 1
     * layout_pre_cfg : {"mosic_config":[{"role":"speaker","view_has_self":0},{"role":"chair","view_has_self":0}],"show_camera_off":0}
     * features : 011011111111111
     * focus_usr_id : 4
     */

    private int room_id;
    private String room_mark;
    private String room_name;
    private int bandwidth;
    private String join_pwd;
    private int max_call;
    private String ctrl_pwd;
    private String supervisor_pwd;
    private int duration;
    private String live_pwd;
    private int room_type;
    private int resource_template_id;
    private int enable_guest;
    private int force_mcu_layout;
    private int same_layout;
    private String region_id;
    private int enable_adhoc;
    private String mr_scene;
    private int all_guests_mute;
    private int mr_vtx_mode;
    private int allow_unmute_self;
    private int speaker_usr_id;
    private String live_push_url;
    private int enable_bypass;
    private int focus_speaker;
    private LayoutPreCfg layout_pre_cfg;
    private String features;
    private String focus_usr_id;

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
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

    public int getMax_call() {
        return max_call;
    }

    public void setMax_call(int max_call) {
        this.max_call = max_call;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getLive_pwd() {
        return live_pwd;
    }

    public void setLive_pwd(String live_pwd) {
        this.live_pwd = live_pwd;
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

    public int getSame_layout() {
        return same_layout;
    }

    public void setSame_layout(int same_layout) {
        this.same_layout = same_layout;
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

    public String getLive_push_url() {
        return live_push_url;
    }

    public void setLive_push_url(String live_push_url) {
        this.live_push_url = live_push_url;
    }

    public int getEnable_bypass() {
        return enable_bypass;
    }

    public void setEnable_bypass(int enable_bypass) {
        this.enable_bypass = enable_bypass;
    }

    public int getFocus_speaker() {
        return focus_speaker;
    }

    public void setFocus_speaker(int focus_speaker) {
        this.focus_speaker = focus_speaker;
    }

    public LayoutPreCfg getLayout_pre_cfg() {
        return layout_pre_cfg;
    }

    public void setLayout_pre_cfg(LayoutPreCfg layout_pre_cfg) {
        this.layout_pre_cfg = layout_pre_cfg;
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

    public static class LayoutPreCfg implements Serializable {
        /**
         * mosic_config : [{"role":"speaker","view_has_self":0},{"role":"chair","view_has_self":0}]
         * show_camera_off : 0
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

        public static class MosicConfig implements Serializable {
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



    public static CmModRoomRequest buildDefaultRequest() {
        CmModRoomRequest cmModRoomRequest = new CmModRoomRequest();
        cmModRoomRequest.setRoom_name("");// 会议室81234
        cmModRoomRequest.setRoom_mark("");// 81234
        cmModRoomRequest.setCtrl_pwd("");// 123456
        cmModRoomRequest.setJoin_pwd("");
        cmModRoomRequest.setLive_pwd("");
        cmModRoomRequest.setSupervisor_pwd("");
        cmModRoomRequest.setDuration(172800);
        cmModRoomRequest.setLive_push_url("");
        cmModRoomRequest.setRoom_type(2);
        cmModRoomRequest.setMr_scene("conference");
        cmModRoomRequest.setBandwidth(1024);
        cmModRoomRequest.setMax_call(1000);
        cmModRoomRequest.setResource_template_id(27);
        cmModRoomRequest.setForce_mcu_layout(1);
        cmModRoomRequest.setAllow_unmute_self(0);
        cmModRoomRequest.setEnable_bypass(0);
        cmModRoomRequest.setFocus_usr_id("");
        cmModRoomRequest.setSpeaker_usr_id(-1);
        cmModRoomRequest.setSame_layout(0);
        cmModRoomRequest.setEnable_guest(1);
        cmModRoomRequest.setRegion_id("-1");
        cmModRoomRequest.setEnable_adhoc(1);
        cmModRoomRequest.setAll_guests_mute(1);
        cmModRoomRequest.setMr_vtx_mode(0);
        CmModRoomRequest.LayoutPreCfg layoutPreCfg = new CmModRoomRequest.LayoutPreCfg();
        layoutPreCfg.setShow_camera_off(1);

        CmModRoomRequest.LayoutPreCfg.MosicConfig mosicConfig = new CmModRoomRequest.LayoutPreCfg.MosicConfig();
        mosicConfig.setRole("speaker");
        mosicConfig.setView_has_self(1);
        CmModRoomRequest.LayoutPreCfg.MosicConfig mosicConfig2 = new CmModRoomRequest.LayoutPreCfg.MosicConfig();
        mosicConfig2.setRole("chair");
        mosicConfig2.setView_has_self(1);
        CmModRoomRequest.LayoutPreCfg.MosicConfig mosicConfig3 = new CmModRoomRequest.LayoutPreCfg.MosicConfig();
        mosicConfig3.setRole("guest");
        mosicConfig3.setView_has_self(1);
        List<CmModRoomRequest.LayoutPreCfg.MosicConfig> mosicConfigList = new ArrayList<>();
        mosicConfigList.add(mosicConfig);
        mosicConfigList.add(mosicConfig2);
//        mosicConfigList.add(mosicConfig3);

        layoutPreCfg.setMosic_config(mosicConfigList);
        cmModRoomRequest.setLayout_pre_cfg(layoutPreCfg);
        cmModRoomRequest.setFocus_speaker(0);
        cmModRoomRequest.setFeatures("010001000001010");

        return cmModRoomRequest;
    }
}
