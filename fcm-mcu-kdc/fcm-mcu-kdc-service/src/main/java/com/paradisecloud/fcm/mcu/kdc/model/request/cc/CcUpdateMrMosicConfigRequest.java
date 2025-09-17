package com.paradisecloud.fcm.mcu.kdc.model.request.cc;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.fcm.mcu.kdc.model.CellInfo;
import com.paradisecloud.fcm.mcu.kdc.model.enumer.McuKdcLayoutTemplates;
import com.paradisecloud.fcm.mcu.kdc.model.request.CommonRequest;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class CcUpdateMrMosicConfigRequest extends CommonRequest {

    private String conf_id;
    private String vmp_id;
    private int mode;
    private int except_self;
    private int layout;
    private int broadcast;
    private int voice_hint;
    private int show_mt_name;
    private MtNameStyle mt_name_style;
    private List<CellInfo> members;


    public String getConf_id() {
        return conf_id;
    }

    public void setConf_id(String conf_id) {
        this.conf_id = conf_id;
    }

    public String getVmp_id() {
        return vmp_id;
    }

    public void setVmp_id(String vmp_id) {
        this.vmp_id = vmp_id;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getExcept_self() {
        return except_self;
    }

    public void setExcept_self(int except_self) {
        this.except_self = except_self;
    }

    public int getLayout() {
        return layout;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    public int getBroadcast() {
        return broadcast;
    }

    public void setBroadcast(int broadcast) {
        this.broadcast = broadcast;
    }

    public int getVoice_hint() {
        return voice_hint;
    }

    public void setVoice_hint(int voice_hint) {
        this.voice_hint = voice_hint;
    }

    public int getShow_mt_name() {
        return show_mt_name;
    }

    public void setShow_mt_name(int show_mt_name) {
        this.show_mt_name = show_mt_name;
    }

    public MtNameStyle getMt_name_style() {
        return mt_name_style;
    }

    public void setMt_name_style(MtNameStyle mt_name_style) {
        this.mt_name_style = mt_name_style;
    }

    public List<CellInfo> getMembers() {
        return members;
    }

    public void setMembers(List<CellInfo> members) {
        this.members = members;
    }

    @Override
    public List<NameValuePair> buildToList() {
        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("params", JSON.toJSONString(this)));
        return list;
    }

    public static class MtNameStyle {
        /**
         * font_size : 1
         * font_color : #FFFFFF
         * position : 1
         */

        private int font_size;
        private String font_color;
        private int position;

        public int getFont_size() {
            return font_size;
        }

        public void setFont_size(int font_size) {
            this.font_size = font_size;
        }

        public String getFont_color() {
            return font_color;
        }

        public void setFont_color(String font_color) {
            this.font_color = font_color;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }

    /**
     * mode : 1
     * except_self : 0
     * layout : 5
     * broadcast : 1
     * voice_hint : 1
     * show_mt_name : 1
     * mt_name_style : {"font_size":1,"font_color":"#FFFFFF","position":1}
     * members : [{"chn_idx":0,"member_type":1,"mt_id":"1","mt_chn_idx":0},{"chn_idx":1,"member_type":1,"mt_id":"","mt_chn_idx":0}]
     */
    public static CcUpdateMrMosicConfigRequest buildDefaultRequest() {
        CcUpdateMrMosicConfigRequest ccUpdateMrMosicConfigRequest = new CcUpdateMrMosicConfigRequest();
        ccUpdateMrMosicConfigRequest.setVmp_id("1");
        ccUpdateMrMosicConfigRequest.setMode(2);
        ccUpdateMrMosicConfigRequest.setExcept_self(0);
        ccUpdateMrMosicConfigRequest.setLayout(Integer.valueOf(McuKdcLayoutTemplates.SCREEN_1.getCode()));
        ccUpdateMrMosicConfigRequest.setBroadcast(0);
        ccUpdateMrMosicConfigRequest.setVoice_hint(1);
        ccUpdateMrMosicConfigRequest.setShow_mt_name(1);
        MtNameStyle mtNameStyle = new MtNameStyle();
        mtNameStyle.setFont_color("1");
        mtNameStyle.setFont_color("#FFFFFF");
        mtNameStyle.setPosition(1);
        ccUpdateMrMosicConfigRequest.setMt_name_style(mtNameStyle);
        return ccUpdateMrMosicConfigRequest;
    }
}
