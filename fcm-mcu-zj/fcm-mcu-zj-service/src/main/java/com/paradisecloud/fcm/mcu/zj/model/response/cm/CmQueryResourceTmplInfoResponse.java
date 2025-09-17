package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;
import java.util.List;

public class CmQueryResourceTmplInfoResponse extends CommonResponse {

    private Integer[] max_chair_mosics;
    private String[] chair_copys;
    private Integer[] max_mosics;
    private Integer[] max_spk_mosics;
    private String[] res_bws;
    private Integer[] single_views;
    private Integer[] max_guest_mosics;
    private Integer[] ids;
    private Integer[] mr_counts;
    private String[] extras;
    private Integer[] has_records;
    private String[] names;
    private Integer[] is_defaults;
    private Integer[] is_readonlys;
    private Integer[] has_mosics;

    public Integer[] getMax_chair_mosics() {
        return max_chair_mosics;
    }

    public void setMax_chair_mosics(Integer[] max_chair_mosics) {
        this.max_chair_mosics = max_chair_mosics;
    }

    public String[] getChair_copys() {
        return chair_copys;
    }

    public void setChair_copys(String[] chair_copys) {
        this.chair_copys = chair_copys;
    }

    public Integer[] getMax_mosics() {
        return max_mosics;
    }

    public void setMax_mosics(Integer[] max_mosics) {
        this.max_mosics = max_mosics;
    }

    public Integer[] getMax_spk_mosics() {
        return max_spk_mosics;
    }

    public void setMax_spk_mosics(Integer[] max_spk_mosics) {
        this.max_spk_mosics = max_spk_mosics;
    }

    public String[] getRes_bws() {
        return res_bws;
    }

    public void setRes_bws(String[] res_bws) {
        this.res_bws = res_bws;
    }

    public Integer[] getSingle_views() {
        return single_views;
    }

    public void setSingle_views(Integer[] single_views) {
        this.single_views = single_views;
    }

    public Integer[] getMax_guest_mosics() {
        return max_guest_mosics;
    }

    public void setMax_guest_mosics(Integer[] max_guest_mosics) {
        this.max_guest_mosics = max_guest_mosics;
    }

    public Integer[] getIds() {
        return ids;
    }

    public void setIds(Integer[] ids) {
        this.ids = ids;
    }

    public Integer[] getMr_counts() {
        return mr_counts;
    }

    public void setMr_counts(Integer[] mr_counts) {
        this.mr_counts = mr_counts;
    }

    public String[] getExtras() {
        return extras;
    }

    public void setExtras(String[] extras) {
        this.extras = extras;
    }

    public Integer[] getHas_records() {
        return has_records;
    }

    public void setHas_records(Integer[] has_records) {
        this.has_records = has_records;
    }

    public String[] getNames() {
        return names;
    }

    public void setNames(String[] names) {
        this.names = names;
    }

    public Integer[] getIs_defaults() {
        return is_defaults;
    }

    public void setIs_defaults(Integer[] is_defaults) {
        this.is_defaults = is_defaults;
    }

    public Integer[] getIs_readonlys() {
        return is_readonlys;
    }

    public void setIs_readonlys(Integer[] is_readonlys) {
        this.is_readonlys = is_readonlys;
    }

    public Integer[] getHas_mosics() {
        return has_mosics;
    }

    public void setHas_mosics(Integer[] has_mosics) {
        this.has_mosics = has_mosics;
    }
}
