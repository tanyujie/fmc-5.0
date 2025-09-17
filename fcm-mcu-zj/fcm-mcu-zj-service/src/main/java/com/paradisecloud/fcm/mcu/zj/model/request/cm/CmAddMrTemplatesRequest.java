package com.paradisecloud.fcm.mcu.zj.model.request.cm;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

import java.util.ArrayList;
import java.util.List;

public class CmAddMrTemplatesRequest extends CommonRequest {

    /**
     * cmdid : add_mr_template
     * mr_scene : conference
     * room_cuid : 8007969
     * mr_template_name : cjcs
     * duration : 7200
     * bandwidth : 1024
     * extra :
     * invite_usr_ids : []
     * invite_endpoint_ids : []
     * invite_dep_ids : []
     * mr_template_type : 1
     * join_pwd :
     * ctrl_pwd : 7969
     * supervisor_pwd :
     * live_pwd :
     * auto_invite : 1
     * auto_record : 0
     * belong_to_departments : [2]
     * resource_template_id : 27
     * max_call : 1000
     * enable_guest : 1
     * force_mcu_layout : 1
     * same_layout : 0
     * is_default : 0
     * all_guests_mute : 0
     * mr_vtx_mode : 0
     * speaker_usr_id : -1
     * allow_unmute_self : 0
     * enable_bypass : 0
     * live_push_url :
     * layout_pre_cfg : {"mosic_config":[{"role":"speaker","layout_mode":2,"mosic_id":"auto","roles_lst":[],"view_has_self":1,"poll_secs":10},{"role":"chair","layout_mode":2,"mosic_id":"auto","roles_lst":[],"view_has_self":0,"poll_secs":10},{"role":"guest","layout_mode":2,"mosic_id":"auto","roles_lst":[],"view_has_self":0,"poll_secs":10}],"show_camera_off":1}
     * department_id : 2
     * features : 010001000001010
     * focus_usr_id :
     */

    private String mr_scene;
    private String room_cuid;
    private String mr_template_name;
    private int duration;
    private int bandwidth;
    private String extra;
    private int mr_template_type;
    private String join_pwd;
    private String ctrl_pwd;
    private String supervisor_pwd;
    private String live_pwd;
    private int auto_invite;
    private int auto_record;
    private int resource_template_id;
    private int max_call;
    private int enable_guest;
    private int force_mcu_layout;
    private int same_layout;
    private int is_default;
    private int all_guests_mute;
    private int mr_vtx_mode;
    private int speaker_usr_id;
    private int allow_unmute_self;
    private int enable_bypass;
    private String live_push_url;
    private LayoutPreCfg layout_pre_cfg;
    private int department_id;
    private String features;
    private String focus_usr_id;
    private List<Integer> belong_to_departments;
    private List<Integer> invite_usr_ids;
    private List<Integer> invite_endpoint_ids;
    private List<Integer> invite_dep_ids;

    public String getMr_scene() {
        return mr_scene;
    }

    public void setMr_scene(String mr_scene) {
        this.mr_scene = mr_scene;
    }

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

    public int getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(int department_id) {
        this.department_id = department_id;
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

    public List<Integer> getInvite_dep_ids() {
        return invite_dep_ids;
    }

    public void setInvite_dep_ids(List<Integer> invite_dep_ids) {
        this.invite_dep_ids = invite_dep_ids;
    }

    public static class LayoutPreCfg {
        /**
         * mosic_config : [{"role":"speaker","layout_mode":2,"mosic_id":"auto","roles_lst":[],"view_has_self":1,"poll_secs":10},{"role":"chair","layout_mode":2,"mosic_id":"auto","roles_lst":[],"view_has_self":0,"poll_secs":10},{"role":"guest","layout_mode":2,"mosic_id":"auto","roles_lst":[],"view_has_self":0,"poll_secs":10}]
         * show_camera_off : 1
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
             * layout_mode : 2
             * mosic_id : auto
             * roles_lst : []
             * view_has_self : 1
             * poll_secs : 10
             */

            private String role;
            private int layout_mode;
            private String mosic_id;
            private int view_has_self;
            private int poll_secs;
            private List<?> roles_lst;


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

            public String getMosic_id() {
                return mosic_id;
            }

            public void setMosic_id(String mosic_id) {
                this.mosic_id = mosic_id;
            }

            public int getView_has_self() {
                return view_has_self;
            }

            public void setView_has_self(int view_has_self) {
                this.view_has_self = view_has_self;
            }

            public int getPoll_secs() {
                return poll_secs;
            }

            public void setPoll_secs(int poll_secs) {
                this.poll_secs = poll_secs;
            }

            public List<?> getRoles_lst() {
                return roles_lst;
            }

            public void setRoles_lst(List<?> roles_lst) {
                this.roles_lst = roles_lst;
            }
        }
    }

    public static CmAddMrTemplatesRequest buildDefaultRequest() {
        CmAddMrTemplatesRequest cmAddMrTemplatesRequest = new CmAddMrTemplatesRequest();
        cmAddMrTemplatesRequest.setRoom_cuid("");// 80081234
        cmAddMrTemplatesRequest.setCtrl_pwd("");// 123456
        cmAddMrTemplatesRequest.setJoin_pwd("");// 1111
        cmAddMrTemplatesRequest.setSupervisor_pwd("");
        cmAddMrTemplatesRequest.setDuration(172800);
        cmAddMrTemplatesRequest.setBandwidth(2048);
        cmAddMrTemplatesRequest.setMr_template_name("");
        List<Integer> belongToDepartments = new ArrayList();
        belongToDepartments.add(2);
        cmAddMrTemplatesRequest.setBelong_to_departments(belongToDepartments);
        cmAddMrTemplatesRequest.setInvite_dep_ids(new ArrayList<>());
        cmAddMrTemplatesRequest.setInvite_endpoint_ids(new ArrayList<>());
        cmAddMrTemplatesRequest.setInvite_usr_ids(new ArrayList<>());
        cmAddMrTemplatesRequest.setMr_template_type(1);
        cmAddMrTemplatesRequest.setLive_pwd("");
        cmAddMrTemplatesRequest.setLive_push_url("");
        cmAddMrTemplatesRequest.setAuto_invite(1);
        cmAddMrTemplatesRequest.setAuto_record(0);
        cmAddMrTemplatesRequest.setResource_template_id(27);
        cmAddMrTemplatesRequest.setMax_call(1000);
        cmAddMrTemplatesRequest.setEnable_guest(1);
        cmAddMrTemplatesRequest.setForce_mcu_layout(1);
        cmAddMrTemplatesRequest.setSame_layout(1);
        cmAddMrTemplatesRequest.setIs_default(0);
        cmAddMrTemplatesRequest.setAll_guests_mute(0);
        cmAddMrTemplatesRequest.setMr_vtx_mode(0);
        cmAddMrTemplatesRequest.setSpeaker_usr_id(-1);
        cmAddMrTemplatesRequest.setAllow_unmute_self(0);
        cmAddMrTemplatesRequest.setEnable_bypass(0);
        cmAddMrTemplatesRequest.setDepartment_id(2);
        cmAddMrTemplatesRequest.setFeatures("010011100001110");
        cmAddMrTemplatesRequest.setFocus_usr_id("");
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
        cmAddMrTemplatesRequest.setLayout_pre_cfg(layoutPreCfg);

        return cmAddMrTemplatesRequest;
    }
}
