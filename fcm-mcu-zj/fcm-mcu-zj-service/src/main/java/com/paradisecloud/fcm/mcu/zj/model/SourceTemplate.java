package com.paradisecloud.fcm.mcu.zj.model;

import com.paradisecloud.common.model.ModelBean;

import java.util.List;

public class SourceTemplate {

    private Integer max_chair_mosic;
    private String chair_copy;
    private Integer max_mosic;
    private Integer max_spk_mosic;
    private String res_bw;
    private Integer single_view;
    private Integer max_guest_mosic;
    private Integer id;
    private Integer mr_count;
    private String extra;
    private Integer has_record;
    private String name;
    private Integer is_default;
    private Integer is_readonly;
    private Integer has_mosic;
    private String sourceName;
    private boolean supportRollCall;
    private boolean supportSplitScreen;
    private boolean supportPolling;
    private boolean supportChooseSee;
    private boolean supportTalk;
    private boolean supportBroadcast;
    private List<ModelBean> speakerSplitScreenList;
    private List<ModelBean> guestSplitScreenList;
    private float evaluationResourceCount = 0;

    public Integer getMax_chair_mosic() {
        return max_chair_mosic;
    }

    public void setMax_chair_mosic(Integer max_chair_mosic) {
        this.max_chair_mosic = max_chair_mosic;
    }

    public String getChair_copy() {
        return chair_copy;
    }

    public void setChair_copy(String chair_copy) {
        this.chair_copy = chair_copy;
    }

    public Integer getMax_mosic() {
        return max_mosic;
    }

    public void setMax_mosic(Integer max_mosic) {
        this.max_mosic = max_mosic;
    }

    public Integer getMax_spk_mosic() {
        return max_spk_mosic;
    }

    public void setMax_spk_mosic(Integer max_spk_mosic) {
        this.max_spk_mosic = max_spk_mosic;
    }

    public String getRes_bw() {
        return res_bw;
    }

    public void setRes_bw(String res_bw) {
        this.res_bw = res_bw;
    }

    public Integer getSingle_view() {
        return single_view;
    }

    public void setSingle_view(Integer single_view) {
        this.single_view = single_view;
    }

    public Integer getMax_guest_mosic() {
        return max_guest_mosic;
    }

    public void setMax_guest_mosic(Integer max_guest_mosic) {
        this.max_guest_mosic = max_guest_mosic;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMr_count() {
        return mr_count;
    }

    public void setMr_count(Integer mr_count) {
        this.mr_count = mr_count;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public Integer getHas_record() {
        return has_record;
    }

    public void setHas_record(Integer has_record) {
        this.has_record = has_record;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIs_default() {
        return is_default;
    }

    public void setIs_default(Integer is_default) {
        this.is_default = is_default;
    }

    public Integer getIs_readonly() {
        return is_readonly;
    }

    public void setIs_readonly(Integer is_readonly) {
        this.is_readonly = is_readonly;
    }

    public Integer getHas_mosic() {
        return has_mosic;
    }

    public void setHas_mosic(Integer has_mosic) {
        this.has_mosic = has_mosic;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public boolean isSupportRollCall() {
        return supportRollCall;
    }

    public void setSupportRollCall(boolean supportRollCall) {
        this.supportRollCall = supportRollCall;
    }

    public boolean isSupportSplitScreen() {
        return supportSplitScreen;
    }

    public void setSupportSplitScreen(boolean supportSplitScreen) {
        this.supportSplitScreen = supportSplitScreen;
    }

    public boolean isSupportPolling() {
        return supportPolling;
    }

    public void setSupportPolling(boolean supportPolling) {
        this.supportPolling = supportPolling;
    }

    public boolean isSupportChooseSee() {
        return supportChooseSee;
    }

    public void setSupportChooseSee(boolean supportChooseSee) {
        this.supportChooseSee = supportChooseSee;
    }

    public boolean isSupportTalk() {
        return supportTalk;
    }

    public void setSupportTalk(boolean supportTalk) {
        this.supportTalk = supportTalk;
    }

    public boolean isSupportBroadcast() {
        return supportBroadcast;
    }

    public void setSupportBroadcast(boolean supportBroadcast) {
        this.supportBroadcast = supportBroadcast;
    }

    public List<ModelBean> getSpeakerSplitScreenList() {
        return speakerSplitScreenList;
    }

    public void setSpeakerSplitScreenList(List<ModelBean> speakerSplitScreenList) {
        this.speakerSplitScreenList = speakerSplitScreenList;
    }

    public List<ModelBean> getGuestSplitScreenList() {
        return guestSplitScreenList;
    }

    public void setGuestSplitScreenList(List<ModelBean> guestSplitScreenList) {
        this.guestSplitScreenList = guestSplitScreenList;
    }

    public float getEvaluationResourceCount() {
        return evaluationResourceCount;
    }

    public void setEvaluationResourceCount(float evaluationResourceCount) {
        this.evaluationResourceCount = evaluationResourceCount;
    }
}
