package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

public class CmGetRoomHistoryListResponse extends CommonResponse {

    private Integer[] lst_call_cnts;
    private Integer[] lst_duration;
    private Integer[] lst_history_idx;
    private Integer[] lst_launch_dtm;
    private String[] lst_mrids;
    private String[] lst_mrnames;
    private String[] lst_peak_calls;
    private String[] lst_stop_reasons;
    private String[] lst_tenant_ids;

    public Integer[] getLst_call_cnts() {
        return lst_call_cnts;
    }

    public void setLst_call_cnts(Integer[] lst_call_cnts) {
        this.lst_call_cnts = lst_call_cnts;
    }

    public Integer[] getLst_duration() {
        return lst_duration;
    }

    public void setLst_duration(Integer[] lst_duration) {
        this.lst_duration = lst_duration;
    }

    public Integer[] getLst_history_idx() {
        return lst_history_idx;
    }

    public void setLst_history_idx(Integer[] lst_history_idx) {
        this.lst_history_idx = lst_history_idx;
    }

    public Integer[] getLst_launch_dtm() {
        return lst_launch_dtm;
    }

    public void setLst_launch_dtm(Integer[] lst_launch_dtm) {
        this.lst_launch_dtm = lst_launch_dtm;
    }

    public String[] getLst_mrids() {
        return lst_mrids;
    }

    public void setLst_mrids(String[] lst_mrids) {
        this.lst_mrids = lst_mrids;
    }

    public String[] getLst_mrnames() {
        return lst_mrnames;
    }

    public void setLst_mrnames(String[] lst_mrnames) {
        this.lst_mrnames = lst_mrnames;
    }

    public String[] getLst_peak_calls() {
        return lst_peak_calls;
    }

    public void setLst_peak_calls(String[] lst_peak_calls) {
        this.lst_peak_calls = lst_peak_calls;
    }

    public String[] getLst_stop_reasons() {
        return lst_stop_reasons;
    }

    public void setLst_stop_reasons(String[] lst_stop_reasons) {
        this.lst_stop_reasons = lst_stop_reasons;
    }

    public String[] getLst_tenant_ids() {
        return lst_tenant_ids;
    }

    public void setLst_tenant_ids(String[] lst_tenant_ids) {
        this.lst_tenant_ids = lst_tenant_ids;
    }
}
