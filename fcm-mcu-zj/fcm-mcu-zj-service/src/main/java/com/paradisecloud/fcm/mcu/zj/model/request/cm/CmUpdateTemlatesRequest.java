package com.paradisecloud.fcm.mcu.zj.model.request.cm;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;
import java.util.List;

public class CmUpdateTemlatesRequest extends CommonRequest {

    /**
     * room_cuid : 8002703
     * mr_template_name : lxl-Test-8002703
     * mr_template_id : 11
     * join_pwd : 7687
     * ctrl_pwd : 2703
     * supervisor_pwd : 6876
     * live_pwd : 7687
     * duration : 86400
     * bandwidth : 1024
     * invite_dep_ids : []
     * invite_usr_ids : [1]
     * cared_status : []
     * invite_endpoint_ids : [4]
     * endpoint_cared_status : []
     * auto_invite : 1
     * lock : 0
     * extra : uytuyt
     * mr_template_type : 1
     * creator_usr_id : 1
     * creator_usr_nick_name : lxl
     * current_department : [2]
     * auto_record : 1
     * room_name : lxl-Test_2703
     * room_id : 53
     * max_call : 1000
     * features : 011011111111111
     * resource_template_id : 27
     * enable_guest : 1
     * force_mcu_layout : 1
     * same_layout : 1
     * is_default : 0
     * region_id : -1
     * mr_scene : conference
     * all_guests_mute : 1
     * mr_vtx_mode : 1
     * allow_unmute_self : 1
     * speaker_usr_id : -1
     * focus_usr_id :
     * live_push_url : cnRtcDovLzE3Mi4xNi4wLjIwMC9saXZlL2hk
     * enable_bypass : 1
     * layout_pre_cfg : {"mosic_config":[{"mosic_id":"auto","role":"speaker","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":1},{"role":"chair","layout_mode":2,"mosic_id":"auto","roles_lst":[],"view_has_self":0,"poll_secs":10},{"mosic_id":"auto","role":"guest","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":0}],"show_camera_off":0}
     */

    private String room_cuid;
    private String mr_template_name;
    private int mr_template_id;
    private String join_pwd;
    private String ctrl_pwd;
    private String supervisor_pwd;
    private String live_pwd;
    private int duration;
    private int bandwidth;
    private int auto_invite;
    private int lock;
    private String extra;
    private int mr_template_type;
    private int creator_usr_id;
    private String creator_usr_nick_name;
    private int auto_record;
    private String room_name;
    private int room_id;
    private int max_call;
    private String features;
    private int resource_template_id;
    private int enable_guest;
    private int force_mcu_layout;
    private int same_layout;
    private int is_default;
    private String region_id;
    private String mr_scene;
    private int all_guests_mute;
    private int mr_vtx_mode;
    private int allow_unmute_self;
    private int speaker_usr_id;
    private String focus_usr_id;
    private String live_push_url;
    private int enable_bypass;
    private LayoutPreCfgResponse layout_pre_cfg;
    private List<Integer> invite_dep_ids;
    private List<Integer> invite_usr_ids;
    private List<Integer> cared_status;
    private List<Integer> invite_endpoint_ids;
    private List<Integer> endpoint_cared_status;
    private List<Integer> current_department;

    public String getRoom_cuid() {
        return room_cuid;
    }

    public void setRoom_cuid(String room_cuid) {
        this.room_cuid = room_cuid;
    }

    public String getMr_template_name() {
        return mr_template_name;
    }

    public void setMr_template_name(String mr_template_name) {
        this.mr_template_name = mr_template_name;
    }

    public int getMr_template_id() {
        return mr_template_id;
    }

    public void setMr_template_id(int mr_template_id) {
        this.mr_template_id = mr_template_id;
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

    public String getLive_pwd() {
        return live_pwd;
    }

    public void setLive_pwd(String live_pwd) {
        this.live_pwd = live_pwd;
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

    public int getAuto_invite() {
        return auto_invite;
    }

    public void setAuto_invite(int auto_invite) {
        this.auto_invite = auto_invite;
    }

    public int getLock() {
        return lock;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public int getMr_template_type() {
        return mr_template_type;
    }

    public void setMr_template_type(int mr_template_type) {
        this.mr_template_type = mr_template_type;
    }

    public int getCreator_usr_id() {
        return creator_usr_id;
    }

    public void setCreator_usr_id(int creator_usr_id) {
        this.creator_usr_id = creator_usr_id;
    }

    public String getCreator_usr_nick_name() {
        return creator_usr_nick_name;
    }

    public void setCreator_usr_nick_name(String creator_usr_nick_name) {
        this.creator_usr_nick_name = creator_usr_nick_name;
    }

    public int getAuto_record() {
        return auto_record;
    }

    public void setAuto_record(int auto_record) {
        this.auto_record = auto_record;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public int getMax_call() {
        return max_call;
    }

    public void setMax_call(int max_call) {
        this.max_call = max_call;
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

    public int getIs_default() {
        return is_default;
    }

    public void setIs_default(int is_default) {
        this.is_default = is_default;
    }

    public String getRegion_id() {
        return region_id;
    }

    public void setRegion_id(String region_id) {
        this.region_id = region_id;
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

    public int getEnable_bypass() {
        return enable_bypass;
    }

    public void setEnable_bypass(int enable_bypass) {
        this.enable_bypass = enable_bypass;
    }

    public LayoutPreCfgResponse getLayout_pre_cfg() {
        return layout_pre_cfg;
    }

    public void setLayout_pre_cfg(LayoutPreCfgResponse layout_pre_cfg) {
        this.layout_pre_cfg = layout_pre_cfg;
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

    public List<Integer> getCurrent_department() {
        return current_department;
    }

    public void setCurrent_department(List<Integer> current_department) {
        this.current_department = current_department;
    }

    public List<Integer> getInvite_dep_ids() {
        return invite_dep_ids;
    }

    public void setInvite_dep_ids(List<Integer> invite_dep_ids) {
        this.invite_dep_ids = invite_dep_ids;
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

    public static class LayoutPreCfgResponse {
        /**
         * mosic_config : [{"mosic_id":"auto","role":"speaker","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":1},{"role":"chair","layout_mode":2,"mosic_id":"auto","roles_lst":[],"view_has_self":0,"poll_secs":10},{"mosic_id":"auto","role":"guest","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":0}]
         * show_camera_off : 0
         */

        private int show_camera_off;
        private List<MosicConfigResponse> mosic_config;

        public int getShow_camera_off() {
            return show_camera_off;
        }

        public void setShow_camera_off(int show_camera_off) {
            this.show_camera_off = show_camera_off;
        }

        public List<MosicConfigResponse> getMosic_config() {
            return mosic_config;
        }

        public void setMosic_config(List<MosicConfigResponse> mosic_config) {
            this.mosic_config = mosic_config;
        }

        public static class MosicConfigResponse {
            /**
             * mosic_id : auto
             * role : speaker
             * roles_lst : []
             * layout_mode : 2
             * poll_secs : 10
             * view_has_self : 1
             */

            private String mosic_id;
            private String role;
            private int layout_mode;
            private int poll_secs;
            private int view_has_self;
            private List<String> roles_lst;

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

            public List<String> getRoles_lst() {
                return roles_lst;
            }

            public void setRoles_lst(List<String> roles_lst) {
                this.roles_lst = roles_lst;
            }
        }
    }
}
