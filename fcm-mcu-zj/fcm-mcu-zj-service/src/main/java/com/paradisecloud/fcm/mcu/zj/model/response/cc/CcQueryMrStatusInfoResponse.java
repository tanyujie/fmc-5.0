package com.paradisecloud.fcm.mcu.zj.model.response.cc;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

import java.util.List;
import java.util.Map;

public class CcQueryMrStatusInfoResponse extends CommonResponse  {

    /**
     * auto_focus_usr_id :
     * https_port : 443
     * rtmp_url : http://127.0.0.1:80/liveplay/cluster/8008038166614600807
     * live_service_type : builtin
     * roll_call : 1
     * mr_atx_mode : 0
     * live_enabled : 0
     * mr_id : 8008038
     * guest_enabled : 1
     * mic_gain_auto : 0
     * cont_policy_9x : 0
     * spec_focus_usr_ids : ["a957e75d76"]
     * mr_locked : 0
     * show_camera_off : 1
     * push_url :
     * non_main_venue_layout : 0
     * all_guests_mute : 1
     * guest_poll : 1
     * rollcall_hist : {"a957e75d76":"172.16.100.66"}
     * cont_enabled : 1
     * mr_vtx_mode : 0
     * amixer_group : [16384]
     * record_uuid : 46d73509-26f4-4b26-9dda-a844d9d45e5c
     * rec_enabled : 0
     * speaker_poll : 1
     * chair_poll : 1
     * record_copy : guest
     * bypass_enabled : 0
     * chair_usr_id :
     * spk_usr_id :
     * subtitle_position : {"horizontal_margin":5,"vertical_margin":5,"horizontal_position":"left","vertical_position":"bottom"}
     * allow_unmute_self : 0
     * rtmp_url3rd :
     * force_mcu_layout : 1
     * rtmp3rdcfg_original_stat :
     * flv_fsize : 0
     * auto_invite : 0
     * chair_copy : speaker
     * original_all_guests_mute : 0
     * same_layout : 0
     * chat_permission : 1
     * spk_locked : 0
     * random_mrid : 8008038166614600807
     * must_https : 0
     * cascade_mode :
     * rtmp_bypass_push :
     */

    private String auto_focus_usr_id;
    private int https_port;
    private String rtmp_url;
    private String live_service_type;
    private int roll_call;
    private int mr_atx_mode;
    private int live_enabled;
    private String mr_id;
    private int guest_enabled;
    private int mic_gain_auto;
    private int cont_policy_9x;
    private int mr_locked;
    private int show_camera_off;
    private String push_url;
    private int non_main_venue_layout;
    private int all_guests_mute;
    private int guest_poll;
    private Map<String, String> rollcall_hist;
    private int cont_enabled;
    private int mr_vtx_mode;
    private String record_uuid;
    private int rec_enabled;
    private int speaker_poll;
    private int chair_poll;
    private String record_copy;
    private int bypass_enabled;
    private String chair_usr_id;
    private String spk_usr_id;
    private SubtitlePositionResponse subtitle_position;
    private int allow_unmute_self;
    private String rtmp_url3rd;
    private int force_mcu_layout;
    private String rtmp3rdcfg_original_stat;
    private int flv_fsize;
    private int auto_invite;
    private String chair_copy;
    private int original_all_guests_mute;
    private int same_layout;
    private int chat_permission;
    private int spk_locked;
    private String random_mrid;
    private int must_https;
    private String cascade_mode;
    private String rtmp_bypass_push;
    private List<String> spec_focus_usr_ids;
    private List<Integer> amixer_group;

    public String getAuto_focus_usr_id() {
        return auto_focus_usr_id;
    }

    public void setAuto_focus_usr_id(String auto_focus_usr_id) {
        this.auto_focus_usr_id = auto_focus_usr_id;
    }

    public int getHttps_port() {
        return https_port;
    }

    public void setHttps_port(int https_port) {
        this.https_port = https_port;
    }

    public String getRtmp_url() {
        return rtmp_url;
    }

    public void setRtmp_url(String rtmp_url) {
        this.rtmp_url = rtmp_url;
    }

    public String getLive_service_type() {
        return live_service_type;
    }

    public void setLive_service_type(String live_service_type) {
        this.live_service_type = live_service_type;
    }

    public int getRoll_call() {
        return roll_call;
    }

    public void setRoll_call(int roll_call) {
        this.roll_call = roll_call;
    }

    public int getMr_atx_mode() {
        return mr_atx_mode;
    }

    public void setMr_atx_mode(int mr_atx_mode) {
        this.mr_atx_mode = mr_atx_mode;
    }

    public int getLive_enabled() {
        return live_enabled;
    }

    public void setLive_enabled(int live_enabled) {
        this.live_enabled = live_enabled;
    }

    public String getMr_id() {
        return mr_id;
    }

    public void setMr_id(String mr_id) {
        this.mr_id = mr_id;
    }

    public int getGuest_enabled() {
        return guest_enabled;
    }

    public void setGuest_enabled(int guest_enabled) {
        this.guest_enabled = guest_enabled;
    }

    public int getMic_gain_auto() {
        return mic_gain_auto;
    }

    public void setMic_gain_auto(int mic_gain_auto) {
        this.mic_gain_auto = mic_gain_auto;
    }

    public int getCont_policy_9x() {
        return cont_policy_9x;
    }

    public void setCont_policy_9x(int cont_policy_9x) {
        this.cont_policy_9x = cont_policy_9x;
    }

    public int getMr_locked() {
        return mr_locked;
    }

    public void setMr_locked(int mr_locked) {
        this.mr_locked = mr_locked;
    }

    public int getShow_camera_off() {
        return show_camera_off;
    }

    public void setShow_camera_off(int show_camera_off) {
        this.show_camera_off = show_camera_off;
    }

    public String getPush_url() {
        return push_url;
    }

    public void setPush_url(String push_url) {
        this.push_url = push_url;
    }

    public int getNon_main_venue_layout() {
        return non_main_venue_layout;
    }

    public void setNon_main_venue_layout(int non_main_venue_layout) {
        this.non_main_venue_layout = non_main_venue_layout;
    }

    public int getAll_guests_mute() {
        return all_guests_mute;
    }

    public void setAll_guests_mute(int all_guests_mute) {
        this.all_guests_mute = all_guests_mute;
    }

    public int getGuest_poll() {
        return guest_poll;
    }

    public void setGuest_poll(int guest_poll) {
        this.guest_poll = guest_poll;
    }

    public int getCont_enabled() {
        return cont_enabled;
    }

    public void setCont_enabled(int cont_enabled) {
        this.cont_enabled = cont_enabled;
    }

    public int getMr_vtx_mode() {
        return mr_vtx_mode;
    }

    public void setMr_vtx_mode(int mr_vtx_mode) {
        this.mr_vtx_mode = mr_vtx_mode;
    }

    public String getRecord_uuid() {
        return record_uuid;
    }

    public void setRecord_uuid(String record_uuid) {
        this.record_uuid = record_uuid;
    }

    public int getRec_enabled() {
        return rec_enabled;
    }

    public void setRec_enabled(int rec_enabled) {
        this.rec_enabled = rec_enabled;
    }

    public int getSpeaker_poll() {
        return speaker_poll;
    }

    public void setSpeaker_poll(int speaker_poll) {
        this.speaker_poll = speaker_poll;
    }

    public int getChair_poll() {
        return chair_poll;
    }

    public void setChair_poll(int chair_poll) {
        this.chair_poll = chair_poll;
    }

    public String getRecord_copy() {
        return record_copy;
    }

    public void setRecord_copy(String record_copy) {
        this.record_copy = record_copy;
    }

    public int getBypass_enabled() {
        return bypass_enabled;
    }

    public void setBypass_enabled(int bypass_enabled) {
        this.bypass_enabled = bypass_enabled;
    }

    public String getChair_usr_id() {
        return chair_usr_id;
    }

    public void setChair_usr_id(String chair_usr_id) {
        this.chair_usr_id = chair_usr_id;
    }

    public String getSpk_usr_id() {
        return spk_usr_id;
    }

    public void setSpk_usr_id(String spk_usr_id) {
        this.spk_usr_id = spk_usr_id;
    }

    public SubtitlePositionResponse getSubtitle_position() {
        return subtitle_position;
    }

    public void setSubtitle_position(SubtitlePositionResponse subtitle_position) {
        this.subtitle_position = subtitle_position;
    }

    public int getAllow_unmute_self() {
        return allow_unmute_self;
    }

    public void setAllow_unmute_self(int allow_unmute_self) {
        this.allow_unmute_self = allow_unmute_self;
    }

    public String getRtmp_url3rd() {
        return rtmp_url3rd;
    }

    public void setRtmp_url3rd(String rtmp_url3rd) {
        this.rtmp_url3rd = rtmp_url3rd;
    }

    public int getForce_mcu_layout() {
        return force_mcu_layout;
    }

    public void setForce_mcu_layout(int force_mcu_layout) {
        this.force_mcu_layout = force_mcu_layout;
    }

    public String getRtmp3rdcfg_original_stat() {
        return rtmp3rdcfg_original_stat;
    }

    public void setRtmp3rdcfg_original_stat(String rtmp3rdcfg_original_stat) {
        this.rtmp3rdcfg_original_stat = rtmp3rdcfg_original_stat;
    }

    public int getFlv_fsize() {
        return flv_fsize;
    }

    public void setFlv_fsize(int flv_fsize) {
        this.flv_fsize = flv_fsize;
    }

    public int getAuto_invite() {
        return auto_invite;
    }

    public void setAuto_invite(int auto_invite) {
        this.auto_invite = auto_invite;
    }

    public String getChair_copy() {
        return chair_copy;
    }

    public void setChair_copy(String chair_copy) {
        this.chair_copy = chair_copy;
    }

    public int getOriginal_all_guests_mute() {
        return original_all_guests_mute;
    }

    public void setOriginal_all_guests_mute(int original_all_guests_mute) {
        this.original_all_guests_mute = original_all_guests_mute;
    }

    public int getSame_layout() {
        return same_layout;
    }

    public void setSame_layout(int same_layout) {
        this.same_layout = same_layout;
    }

    public int getChat_permission() {
        return chat_permission;
    }

    public void setChat_permission(int chat_permission) {
        this.chat_permission = chat_permission;
    }

    public int getSpk_locked() {
        return spk_locked;
    }

    public void setSpk_locked(int spk_locked) {
        this.spk_locked = spk_locked;
    }

    public String getRandom_mrid() {
        return random_mrid;
    }

    public void setRandom_mrid(String random_mrid) {
        this.random_mrid = random_mrid;
    }

    public int getMust_https() {
        return must_https;
    }

    public void setMust_https(int must_https) {
        this.must_https = must_https;
    }

    public String getCascade_mode() {
        return cascade_mode;
    }

    public void setCascade_mode(String cascade_mode) {
        this.cascade_mode = cascade_mode;
    }

    public String getRtmp_bypass_push() {
        return rtmp_bypass_push;
    }

    public void setRtmp_bypass_push(String rtmp_bypass_push) {
        this.rtmp_bypass_push = rtmp_bypass_push;
    }

    public List<String> getSpec_focus_usr_ids() {
        return spec_focus_usr_ids;
    }

    public void setSpec_focus_usr_ids(List<String> spec_focus_usr_ids) {
        this.spec_focus_usr_ids = spec_focus_usr_ids;
    }

    public List<Integer> getAmixer_group() {
        return amixer_group;
    }

    public void setAmixer_group(List<Integer> amixer_group) {
        this.amixer_group = amixer_group;
    }

    public Map<String, String> getRollcall_hist() {
        return rollcall_hist;
    }

    public void setRollcall_hist(Map<String, String> rollcall_hist) {
        this.rollcall_hist = rollcall_hist;
    }

    public static class SubtitlePositionResponse {
        /**
         * horizontal_margin : 5
         * vertical_margin : 5
         * horizontal_position : left
         * vertical_position : bottom
         */

        private int horizontal_margin;
        private int vertical_margin;
        private String horizontal_position;
        private String vertical_position;

        public int getHorizontal_margin() {
            return horizontal_margin;
        }

        public void setHorizontal_margin(int horizontal_margin) {
            this.horizontal_margin = horizontal_margin;
        }

        public int getVertical_margin() {
            return vertical_margin;
        }

        public void setVertical_margin(int vertical_margin) {
            this.vertical_margin = vertical_margin;
        }

        public String getHorizontal_position() {
            return horizontal_position;
        }

        public void setHorizontal_position(String horizontal_position) {
            this.horizontal_position = horizontal_position;
        }

        public String getVertical_position() {
            return vertical_position;
        }

        public void setVertical_position(String vertical_position) {
            this.vertical_position = vertical_position;
        }
    }
}
