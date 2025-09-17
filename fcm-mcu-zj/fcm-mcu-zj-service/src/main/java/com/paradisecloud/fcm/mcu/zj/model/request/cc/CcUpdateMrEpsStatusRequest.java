package com.paradisecloud.fcm.mcu.zj.model.request.cc;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

public class CcUpdateMrEpsStatusRequest extends CommonRequest {

    public static final String ACTION_a_rx = "a_rx";
    public static final String ACTION_v_rx = "v_rx";
    public static final String ACTION_a_tx = "a_tx";
    public static final String ACTION_v_tx = "v_tx";
    public static final String ACTION_drop = "drop";
    public static final String ACTION_call = "call";
    public static final String ACTION_add_participants = "add_participants";
    public static final String ACTION_del_participants = "del_participants";
    public static final String ACTION_chair = "chair";
    public static final String ACTION_speaker = "speaker";
    public static final String ACTION_focus = "focus";
    public static final String ACTION_is_controller = "is_controller";
    public static final String ACTION_displayname = "displayname";
    public static final String ACTION_Clear_hand = "clear_hand";
    public static final String ACTION_can_content = "can_content";
    public static final String ACTION_clear_all_hand = "clear_all_hand";
    public static final String ACTION_mic_gain = "mic_gain";
    public static final String ACTION_unlock = "unlock";

    private String action;
    private String[] usr_ids;
    private Object value;
    private Integer[] dep_ids;
    private Integer recursion;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String[] getUsr_ids() {
        return usr_ids;
    }

    public void setUsr_ids(String[] usr_ids) {
        this.usr_ids = usr_ids;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Integer[] getDep_ids() {
        return dep_ids;
    }

    public void setDep_ids(Integer[] dep_ids) {
        this.dep_ids = dep_ids;
    }

    public Integer getRecursion() {
        return recursion;
    }

    public void setRecursion(Integer recursion) {
        this.recursion = recursion;
    }
}
