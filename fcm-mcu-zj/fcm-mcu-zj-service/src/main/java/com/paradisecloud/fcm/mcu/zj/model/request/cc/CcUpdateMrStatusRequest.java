package com.paradisecloud.fcm.mcu.zj.model.request.cc;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

import java.util.HashMap;
import java.util.Map;

public class CcUpdateMrStatusRequest extends CommonRequest {

    /**
     * roll_call : 1
     * mr_atx_mode : 0
     * enable_live : 0
     * enable_guest : 1
     * record_copy : speaker
     * lock_mr : 1
     * show_camera_off : 0
     * all_guests_mute : 1
     * guest_poll : 1
     * cont_enabled : 1
     * mr_vtx_mode : 0
     * speaker_poll : 1
     * chair_poll : 1
     * cmdid : one_mr_status
     * force_mcu_layout : 1
     * auto_invite : 1
     * same_layout : 1
     */

    public static final String PARAM_roll_call = "roll_call";
    public static final String PARAM_mr_atx_mode = "mr_atx_mode";
    public static final String PARAM_enable_live = "enable_live";
    public static final String PARAM_enable_guest = "enable_guest";
    public static final String PARAM_record_copy = "record_copy";
    public static final String PARAM_lock_mr = "lock_mr";
    public static final String PARAM_show_camera_off = "show_camera_off";
    public static final String PARAM_all_guests_mute = "all_guests_mute";
    public static final String PARAM_guest_poll = "guest_poll";
    public static final String PARAM_cont_enabled = "cont_enabled";
    public static final String PARAM_mr_vtx_mode = "mr_vtx_mode";
    public static final String PARAM_speaker_poll = "speaker_poll";
    public static final String PARAM_chair_poll = "chair_poll";
    public static final String PARAM_force_mcu_layout = "force_mcu_layout";
    public static final String PARAM_auto_invite = "auto_invite";
    public static final String PARAM_same_layout = "same_layout";
    public static final String PARAM_enable_rec = "enable_rec";
    public static final String PARAM_enable_bypass = "enable_bypass";

    private String mrStatusAction;

    private Object mrStatusValue;

    public String getMrStatusAction() {
        return mrStatusAction;
    }

    public void setMrStatusAction(String mrStatusAction) {
        this.mrStatusAction = mrStatusAction;
    }

    public Object getMrStatusValue() {
        return mrStatusValue;
    }

    public void setMrStatusValue(Object mrStatusValue) {
        this.mrStatusValue = mrStatusValue;
    }

    public Map<String, Object> buildParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("cmdid", getCmdid());
        params.put(getMrStatusAction(), getMrStatusValue());
        return params;
    }

}
