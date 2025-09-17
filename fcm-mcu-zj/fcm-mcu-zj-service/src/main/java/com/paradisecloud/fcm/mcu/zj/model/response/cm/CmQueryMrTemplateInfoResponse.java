package com.paradisecloud.fcm.mcu.zj.model.response.cm;
import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

import java.util.List;

public class CmQueryMrTemplateInfoResponse extends CommonResponse {

    /**
     * invite_dep_names : [[],[],[],[],[]]
     * invite_endpoint_nick_names : [[],[],["22222"],["11111"],[]]
     * features : ["011001100001010","011001100001010","010001000001010","011011111111111","010001000001010"]
     * priority_mps : [0,0,0,0,0]
     * locks : [0,0,0,0,0]
     * all_guests_mutes : [0,0,0,1,0]
     * enable_guests : [1,1,1,1,1]
     * same_layouts : [0,0,0,1,1]
     * layout_pre_cfg : [{"mosic_config":[{"mosic_id":"auto","role":"speaker","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":0},{"mosic_id":"auto","role":"chair","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":0},{"mosic_id":"auto","role":"guest","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":0}],"show_camera_off":1},{"mosic_config":[{"mosic_id":"auto","role":"speaker","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":0},{"mosic_id":"auto","role":"chair","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":0},{"mosic_id":"auto","role":"guest","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":0}],"show_camera_off":1},{"mosic_config":[{"mosic_id":"auto","role":"speaker","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":0},{"mosic_id":"auto","role":"chair","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":0},{"mosic_id":"auto","role":"guest","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":0}],"show_camera_off":1},{"mosic_config":[{"mosic_id":"auto","role":"speaker","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":1},{"mosic_id":"auto","role":"chair","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":0},{"mosic_id":"auto","role":"guest","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":0}],"show_camera_off":0},{"mosic_config":[{"mosic_id":"auto","role":"speaker","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":1},{"mosic_id":"auto","role":"chair","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":0},{"mosic_id":"auto","role":"guest","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":0}],"show_camera_off":1}]
     * endpoint_cared_status : [[],[],[],[],[]]
     * room_ids : [26,29,52,53,53]
     * invite_endpoint_ids : [[],[],[5],[4],[]]
     * auto_stop_secs : [120,120,120,120,120]
     * mr_template_names : ["test","rrt","lxl-TEST","lxl-Test-8002703","lxl-Test-8002703"]
     * ctrl_pwds : ["5613","3025","1177","2703","2703"]
     * chairman_usr_ids : [-1,-1,-1,-1,-1]
     * resource_template_ids : [27,27,27,27,27]
     * speaker_usr_ids : [-1,-1,-1,-1,-1]
     * guest_layoutids : [-1,-1,-1,-1,-1]
     * room_max_calls : [1000,1000,1000,1000,1000]
     * live_pwds : ["","","","7687",""]
     * cared_status : [[],[],[],[],[]]
     * mr_vtx_mode : [0,0,0,1,0]
     * invite_usr_ids : [[],[],[1],[1],[]]
     * join_pwds : ["","","","7687",""]
     * is_defaults : [0,0,0,0,0]
     * durations : [7200,7200,7200,86400,86400]
     * auto_records : [0,0,0,1,0]
     * enable_bypass : [0,0,0,1,0]
     * live_push_urls : ["","","","cnRtcDovLzE3Mi4xNi4wLjIwMC9saXZlL2hk",""]
     * cmdid : mr_templates_info
     * creator_usr_ids : [1,2,-800,1,1]
     * room_cuids : ["8005613","8003025","8001177","8002703","8002703"]
     * mr_template_types : [1,1,1,1,1]
     * invite_usr_nick_names : [[],[],["lxl"],["lxl"],[]]
     * max_calls : [1000,1000,1000,1000,1000]
     * region_ids : ["-1","-1","-1","-1","-1"]
     * mr_template_ids : [1,3,10,11,12]
     * invite_dep_ids : [[],[],[],[],[]]
     * supervisor_pwds : ["","","","6876",""]
     * speaker_layoutids : [-1,-1,-1,-1,-1]
     * mr_scenes : ["conference","conference","conference","conference","conference"]
     * bandwidths : [1024,1024,1024,1024,1024]
     * force_mcu_layouts : [1,1,1,1,1]
     * allow_unmute_selfs : [1,1,1,1,0]
     * creator_usr_nick_names : ["lxl","zb","","lxl","lxl"]
     * chairman_layoutids : [-1,-1,-1,-1,-1]
     * extras : ["","","刘禧龙测试会议","uytuyt",""]
     * room_names : ["会议室5613","会议室3025","lxl-1177","lxl-Test_2703","lxl-Test_2703"]
     * focus_usr_ids : ["","","","",""]
     * auto_mutes : [2,2,2,2,2]
     * current_departments : [[2],[2],[2],[2],[2]]
     * auto_invites : [1,1,1,1,1]
     */

    private String cmdid;
    private List<List<String>> invite_dep_names;
    private List<List<String>> invite_endpoint_nick_names;
    private List<String> features;
    private List<Integer> priority_mps;
    private List<Integer> locks;
    private List<Integer> all_guests_mutes;
    private List<Integer> enable_guests;
    private List<Integer> same_layouts;
    private List<LayoutPreCfgResponse> layout_pre_cfg;
    private List<List<Integer>> endpoint_cared_status;
    private List<Integer> room_ids;
    private List<List<Integer>> invite_endpoint_ids;
    private List<Integer> auto_stop_secs;
    private List<String> mr_template_names;
    private List<String> ctrl_pwds;
    private List<Integer> chairman_usr_ids;
    private List<Integer> resource_template_ids;
    private List<Integer> speaker_usr_ids;
    private List<Integer> guest_layoutids;
    private List<Integer> room_max_calls;
    private List<String> live_pwds;
    private List<List<Integer>> cared_status;
    private List<Integer> mr_vtx_mode;
    private List<List<Integer>> invite_usr_ids;
    private List<String> join_pwds;
    private List<Integer> is_defaults;
    private List<Integer> durations;
    private List<Integer> auto_records;
    private List<Integer> enable_bypass;
    private List<String> live_push_urls;
    private List<Integer> creator_usr_ids;
    private List<String> room_cuids;
    private List<Integer> mr_template_types;
    private List<List<String>> invite_usr_nick_names;
    private List<Integer> max_calls;
    private List<String> region_ids;
    private List<Integer> mr_template_ids;
    private List<List<Integer>> invite_dep_ids;
    private List<String> supervisor_pwds;
    private List<Integer> speaker_layoutids;
    private List<String> mr_scenes;
    private List<Integer> bandwidths;
    private List<Integer> force_mcu_layouts;
    private List<Integer> allow_unmute_selfs;
    private List<String> creator_usr_nick_names;
    private List<Integer> chairman_layoutids;
    private List<String> extras;
    private List<String> room_names;
    private List<String> focus_usr_ids;
    private List<Integer> auto_mutes;
    private List<List<Integer>> current_departments;
    private List<Integer> auto_invites;

    @Override
    public String getCmdid() {
        return cmdid;
    }

    @Override
    public void setCmdid(String cmdid) {
        this.cmdid = cmdid;
    }

    public List<List<String>> getInvite_dep_names() {
        return invite_dep_names;
    }

    public void setInvite_dep_names(List<List<String>> invite_dep_names) {
        this.invite_dep_names = invite_dep_names;
    }

    public List<List<String>> getInvite_endpoint_nick_names() {
        return invite_endpoint_nick_names;
    }

    public void setInvite_endpoint_nick_names(List<List<String>> invite_endpoint_nick_names) {
        this.invite_endpoint_nick_names = invite_endpoint_nick_names;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public List<Integer> getPriority_mps() {
        return priority_mps;
    }

    public void setPriority_mps(List<Integer> priority_mps) {
        this.priority_mps = priority_mps;
    }

    public List<Integer> getLocks() {
        return locks;
    }

    public void setLocks(List<Integer> locks) {
        this.locks = locks;
    }

    public List<Integer> getAll_guests_mutes() {
        return all_guests_mutes;
    }

    public void setAll_guests_mutes(List<Integer> all_guests_mutes) {
        this.all_guests_mutes = all_guests_mutes;
    }

    public List<Integer> getEnable_guests() {
        return enable_guests;
    }

    public void setEnable_guests(List<Integer> enable_guests) {
        this.enable_guests = enable_guests;
    }

    public List<Integer> getSame_layouts() {
        return same_layouts;
    }

    public void setSame_layouts(List<Integer> same_layouts) {
        this.same_layouts = same_layouts;
    }

    public List<LayoutPreCfgResponse> getLayout_pre_cfg() {
        return layout_pre_cfg;
    }

    public void setLayout_pre_cfg(List<LayoutPreCfgResponse> layout_pre_cfg) {
        this.layout_pre_cfg = layout_pre_cfg;
    }

    public List<List<Integer>> getEndpoint_cared_status() {
        return endpoint_cared_status;
    }

    public void setEndpoint_cared_status(List<List<Integer>> endpoint_cared_status) {
        this.endpoint_cared_status = endpoint_cared_status;
    }

    public List<Integer> getRoom_ids() {
        return room_ids;
    }

    public void setRoom_ids(List<Integer> room_ids) {
        this.room_ids = room_ids;
    }

    public List<List<Integer>> getInvite_endpoint_ids() {
        return invite_endpoint_ids;
    }

    public void setInvite_endpoint_ids(List<List<Integer>> invite_endpoint_ids) {
        this.invite_endpoint_ids = invite_endpoint_ids;
    }

    public List<Integer> getAuto_stop_secs() {
        return auto_stop_secs;
    }

    public void setAuto_stop_secs(List<Integer> auto_stop_secs) {
        this.auto_stop_secs = auto_stop_secs;
    }

    public List<String> getMr_template_names() {
        return mr_template_names;
    }

    public void setMr_template_names(List<String> mr_template_names) {
        this.mr_template_names = mr_template_names;
    }

    public List<String> getCtrl_pwds() {
        return ctrl_pwds;
    }

    public void setCtrl_pwds(List<String> ctrl_pwds) {
        this.ctrl_pwds = ctrl_pwds;
    }

    public List<Integer> getChairman_usr_ids() {
        return chairman_usr_ids;
    }

    public void setChairman_usr_ids(List<Integer> chairman_usr_ids) {
        this.chairman_usr_ids = chairman_usr_ids;
    }

    public List<Integer> getResource_template_ids() {
        return resource_template_ids;
    }

    public void setResource_template_ids(List<Integer> resource_template_ids) {
        this.resource_template_ids = resource_template_ids;
    }

    public List<Integer> getSpeaker_usr_ids() {
        return speaker_usr_ids;
    }

    public void setSpeaker_usr_ids(List<Integer> speaker_usr_ids) {
        this.speaker_usr_ids = speaker_usr_ids;
    }

    public List<Integer> getGuest_layoutids() {
        return guest_layoutids;
    }

    public void setGuest_layoutids(List<Integer> guest_layoutids) {
        this.guest_layoutids = guest_layoutids;
    }

    public List<Integer> getRoom_max_calls() {
        return room_max_calls;
    }

    public void setRoom_max_calls(List<Integer> room_max_calls) {
        this.room_max_calls = room_max_calls;
    }

    public List<String> getLive_pwds() {
        return live_pwds;
    }

    public void setLive_pwds(List<String> live_pwds) {
        this.live_pwds = live_pwds;
    }

    public List<List<Integer>> getCared_status() {
        return cared_status;
    }

    public void setCared_status(List<List<Integer>> cared_status) {
        this.cared_status = cared_status;
    }

    public List<Integer> getMr_vtx_mode() {
        return mr_vtx_mode;
    }

    public void setMr_vtx_mode(List<Integer> mr_vtx_mode) {
        this.mr_vtx_mode = mr_vtx_mode;
    }

    public List<List<Integer>> getInvite_usr_ids() {
        return invite_usr_ids;
    }

    public void setInvite_usr_ids(List<List<Integer>> invite_usr_ids) {
        this.invite_usr_ids = invite_usr_ids;
    }

    public List<String> getJoin_pwds() {
        return join_pwds;
    }

    public void setJoin_pwds(List<String> join_pwds) {
        this.join_pwds = join_pwds;
    }

    public List<Integer> getIs_defaults() {
        return is_defaults;
    }

    public void setIs_defaults(List<Integer> is_defaults) {
        this.is_defaults = is_defaults;
    }

    public List<Integer> getDurations() {
        return durations;
    }

    public void setDurations(List<Integer> durations) {
        this.durations = durations;
    }

    public List<Integer> getAuto_records() {
        return auto_records;
    }

    public void setAuto_records(List<Integer> auto_records) {
        this.auto_records = auto_records;
    }

    public List<Integer> getEnable_bypass() {
        return enable_bypass;
    }

    public void setEnable_bypass(List<Integer> enable_bypass) {
        this.enable_bypass = enable_bypass;
    }

    public List<String> getLive_push_urls() {
        return live_push_urls;
    }

    public void setLive_push_urls(List<String> live_push_urls) {
        this.live_push_urls = live_push_urls;
    }

    public List<Integer> getCreator_usr_ids() {
        return creator_usr_ids;
    }

    public void setCreator_usr_ids(List<Integer> creator_usr_ids) {
        this.creator_usr_ids = creator_usr_ids;
    }

    public List<String> getRoom_cuids() {
        return room_cuids;
    }

    public void setRoom_cuids(List<String> room_cuids) {
        this.room_cuids = room_cuids;
    }

    public List<Integer> getMr_template_types() {
        return mr_template_types;
    }

    public void setMr_template_types(List<Integer> mr_template_types) {
        this.mr_template_types = mr_template_types;
    }

    public List<List<String>> getInvite_usr_nick_names() {
        return invite_usr_nick_names;
    }

    public void setInvite_usr_nick_names(List<List<String>> invite_usr_nick_names) {
        this.invite_usr_nick_names = invite_usr_nick_names;
    }

    public List<Integer> getMax_calls() {
        return max_calls;
    }

    public void setMax_calls(List<Integer> max_calls) {
        this.max_calls = max_calls;
    }

    public List<String> getRegion_ids() {
        return region_ids;
    }

    public void setRegion_ids(List<String> region_ids) {
        this.region_ids = region_ids;
    }

    public List<Integer> getMr_template_ids() {
        return mr_template_ids;
    }

    public void setMr_template_ids(List<Integer> mr_template_ids) {
        this.mr_template_ids = mr_template_ids;
    }

    public List<List<Integer>> getInvite_dep_ids() {
        return invite_dep_ids;
    }

    public void setInvite_dep_ids(List<List<Integer>> invite_dep_ids) {
        this.invite_dep_ids = invite_dep_ids;
    }

    public List<String> getSupervisor_pwds() {
        return supervisor_pwds;
    }

    public void setSupervisor_pwds(List<String> supervisor_pwds) {
        this.supervisor_pwds = supervisor_pwds;
    }

    public List<Integer> getSpeaker_layoutids() {
        return speaker_layoutids;
    }

    public void setSpeaker_layoutids(List<Integer> speaker_layoutids) {
        this.speaker_layoutids = speaker_layoutids;
    }

    public List<String> getMr_scenes() {
        return mr_scenes;
    }

    public void setMr_scenes(List<String> mr_scenes) {
        this.mr_scenes = mr_scenes;
    }

    public List<Integer> getBandwidths() {
        return bandwidths;
    }

    public void setBandwidths(List<Integer> bandwidths) {
        this.bandwidths = bandwidths;
    }

    public List<Integer> getForce_mcu_layouts() {
        return force_mcu_layouts;
    }

    public void setForce_mcu_layouts(List<Integer> force_mcu_layouts) {
        this.force_mcu_layouts = force_mcu_layouts;
    }

    public List<Integer> getAllow_unmute_selfs() {
        return allow_unmute_selfs;
    }

    public void setAllow_unmute_selfs(List<Integer> allow_unmute_selfs) {
        this.allow_unmute_selfs = allow_unmute_selfs;
    }

    public List<String> getCreator_usr_nick_names() {
        return creator_usr_nick_names;
    }

    public void setCreator_usr_nick_names(List<String> creator_usr_nick_names) {
        this.creator_usr_nick_names = creator_usr_nick_names;
    }

    public List<Integer> getChairman_layoutids() {
        return chairman_layoutids;
    }

    public void setChairman_layoutids(List<Integer> chairman_layoutids) {
        this.chairman_layoutids = chairman_layoutids;
    }

    public List<String> getExtras() {
        return extras;
    }

    public void setExtras(List<String> extras) {
        this.extras = extras;
    }

    public List<String> getRoom_names() {
        return room_names;
    }

    public void setRoom_names(List<String> room_names) {
        this.room_names = room_names;
    }

    public List<String> getFocus_usr_ids() {
        return focus_usr_ids;
    }

    public void setFocus_usr_ids(List<String> focus_usr_ids) {
        this.focus_usr_ids = focus_usr_ids;
    }

    public List<Integer> getAuto_mutes() {
        return auto_mutes;
    }

    public void setAuto_mutes(List<Integer> auto_mutes) {
        this.auto_mutes = auto_mutes;
    }

    public List<List<Integer>> getCurrent_departments() {
        return current_departments;
    }

    public void setCurrent_departments(List<List<Integer>> current_departments) {
        this.current_departments = current_departments;
    }

    public List<Integer> getAuto_invites() {
        return auto_invites;
    }

    public void setAuto_invites(List<Integer> auto_invites) {
        this.auto_invites = auto_invites;
    }

    public static class LayoutPreCfgResponse {
        /**
         * mosic_config : [{"mosic_id":"auto","role":"speaker","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":0},{"mosic_id":"auto","role":"chair","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":0},{"mosic_id":"auto","role":"guest","roles_lst":[],"layout_mode":2,"poll_secs":10,"view_has_self":0}]
         * show_camera_off : 1
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
             * view_has_self : 0
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
