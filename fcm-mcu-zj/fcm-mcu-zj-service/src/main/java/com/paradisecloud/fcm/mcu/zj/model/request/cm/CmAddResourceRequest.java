package com.paradisecloud.fcm.mcu.zj.model.request.cm;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

public class CmAddResourceRequest extends CommonRequest {

    /**
     * name : 支持传统分屏,两视角,总16分屏,带录制,360P30@1M
     * has_h265 : 1
     * has_record : 1
     * has_mosic : 1
     * max_mosic : 16
     * max_spk_mosic : 8
     * max_guest_mosic : 8
     * max_chair_mosic : 1
     * chair_copy : speaker
     * res_bw : 360P30@1M
     * is_readonly : 0
     * is_default : 3
     * extra :
     * single_view : 0
     */

    private String name;
    private int has_h265;
    private int has_record;
    private int has_mosic;
    private int max_mosic;
    private int max_spk_mosic;
    private int max_guest_mosic;
    private int max_chair_mosic;
    private String chair_copy;
    private String res_bw;
    private int is_readonly;
    private int is_default;
    private String extra;
    private int single_view;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHas_h265() {
        return has_h265;
    }

    public void setHas_h265(int has_h265) {
        this.has_h265 = has_h265;
    }

    public int getHas_record() {
        return has_record;
    }

    public void setHas_record(int has_record) {
        this.has_record = has_record;
    }

    public int getHas_mosic() {
        return has_mosic;
    }

    public void setHas_mosic(int has_mosic) {
        this.has_mosic = has_mosic;
    }

    public int getMax_mosic() {
        return max_mosic;
    }

    public void setMax_mosic(int max_mosic) {
        this.max_mosic = max_mosic;
    }

    public int getMax_spk_mosic() {
        return max_spk_mosic;
    }

    public void setMax_spk_mosic(int max_spk_mosic) {
        this.max_spk_mosic = max_spk_mosic;
    }

    public int getMax_guest_mosic() {
        return max_guest_mosic;
    }

    public void setMax_guest_mosic(int max_guest_mosic) {
        this.max_guest_mosic = max_guest_mosic;
    }

    public int getMax_chair_mosic() {
        return max_chair_mosic;
    }

    public void setMax_chair_mosic(int max_chair_mosic) {
        this.max_chair_mosic = max_chair_mosic;
    }

    public String getChair_copy() {
        return chair_copy;
    }

    public void setChair_copy(String chair_copy) {
        this.chair_copy = chair_copy;
    }

    public String getRes_bw() {
        return res_bw;
    }

    public void setRes_bw(String res_bw) {
        this.res_bw = res_bw;
    }

    public int getIs_readonly() {
        return is_readonly;
    }

    public void setIs_readonly(int is_readonly) {
        this.is_readonly = is_readonly;
    }

    public int getIs_default() {
        return is_default;
    }

    public void setIs_default(int is_default) {
        this.is_default = is_default;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public int getSingle_view() {
        return single_view;
    }

    public void setSingle_view(int single_view) {
        this.single_view = single_view;
    }

    public static CmAddResourceRequest buildDefaultRequest() {
        CmAddResourceRequest cmAddResourceRequest = new CmAddResourceRequest();
        cmAddResourceRequest.setName("");
        cmAddResourceRequest.setHas_h265(0);
        cmAddResourceRequest.setHas_record(0);
        cmAddResourceRequest.setHas_mosic(1);
        cmAddResourceRequest.setMax_mosic(50);
        cmAddResourceRequest.setMax_spk_mosic(25);
        cmAddResourceRequest.setMax_guest_mosic(25);
        cmAddResourceRequest.setMax_chair_mosic(25);
        cmAddResourceRequest.setChair_copy("speaker");
        cmAddResourceRequest.setRes_bw("720P30@1M");
        cmAddResourceRequest.setIs_readonly(0);
        cmAddResourceRequest.setIs_default(0);
        cmAddResourceRequest.setExtra("");
        cmAddResourceRequest.setSingle_view(0);

        return cmAddResourceRequest;
    }
}
