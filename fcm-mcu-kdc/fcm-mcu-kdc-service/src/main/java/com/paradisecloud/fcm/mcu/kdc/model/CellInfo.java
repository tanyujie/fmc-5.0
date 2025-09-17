package com.paradisecloud.fcm.mcu.kdc.model;

public class CellInfo {

    private int chn_idx;
    private int member_type;
    private String mt_id;
    private int mt_chn_idx;

    public int getChn_idx() {
        return chn_idx;
    }

    public void setChn_idx(int chn_idx) {
        this.chn_idx = chn_idx;
    }

    public int getMember_type() {
        return member_type;
    }

    public void setMember_type(int member_type) {
        this.member_type = member_type;
    }

    public String getMt_id() {
        return mt_id;
    }

    public void setMt_id(String mt_id) {
        this.mt_id = mt_id;
    }

    public int getMt_chn_idx() {
        return mt_chn_idx;
    }

    public void setMt_chn_idx(int mt_chn_idx) {
        this.mt_chn_idx = mt_chn_idx;
    }
}
