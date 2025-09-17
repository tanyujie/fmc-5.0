package com.paradisecloud.fcm.mcu.kdc.model.response.cc;

import com.paradisecloud.fcm.mcu.kdc.model.response.CommonResponse;

import java.io.Serializable;
import java.util.List;

public class CcGetTerminalInfoResponse extends CommonResponse {

    /**
     * dual : 1
     * protocol : 1
     * inspection : 0
     * dv_rcv_chn : [{"chn_id":1,"chn_alias":"","chn_label":0,"format":4,"bitrate":0,"resolution":13}]
     * ip : 172.16.100.66
     * poll : 0
     * a_rcv_chn : [{"chn_id":1,"chn_label":0,"chn_alias":""}]
     * account_type : 7
     * dv_snd_chn : [{"chn_id":1,"chn_alias":"","chn_label":0,"format":4,"bitrate":928,"resolution":13}]
     * silence : 0
     * snd_volume : 0
     * alias : 172.16.100.66
     * a_snd_chn : [{"chn_id":1,"chn_label":0,"chn_alias":""}]
     * v_rcv_chn : [{"chn_id":1,"chn_alias":"","chn_label":0,"format":4,"bitrate":1856,"resolution":12}]
     * type : 1
     * mix : 0
     * mute : 0
     * upload : 0
     * rcv_volume : 0
     * call_mode : 2
     * v_snd_chn : [{"chn_id":1,"chn_label":0,"chn_alias":"","cur_video_idx":0,"format":4,"bitrate":1856,"resolution":12}]
     * bitrate : 2048
     * online : 1
     * mt_id : 2
     * rec : 0
     * vmp : 1
     * e164 :
     * account : 172.16.100.66
     */

    private Integer dual;
    private Integer protocol;
    private Integer inspection;
    private String ip;
    private Integer poll;
    private Integer account_type;
    private Integer silence;
    private Integer snd_volume;
    private String alias;
    private Integer type;
    private Integer mix;
    private Integer mute;
    private Integer upload;
    private Integer rcv_volume;
    private Integer call_mode;
    private Integer bitrate;
    private Integer online;
    private String mt_id;
    private Integer rec;
    private Integer vmp;
    private String e164;
    private String account;
    private List<Chn> dv_rcv_chn;
    private List<Chn> a_rcv_chn;
    private List<Chn> dv_snd_chn;
    private List<Chn> a_snd_chn;
    private List<Chn> v_rcv_chn;
    private List<Chn> v_snd_chn;

    public Integer getDual() {
        return dual;
    }

    public void setDual(Integer dual) {
        this.dual = dual;
    }

    public Integer getProtocol() {
        return protocol;
    }

    public void setProtocol(Integer protocol) {
        this.protocol = protocol;
    }

    public Integer getInspection() {
        return inspection;
    }

    public void setInspection(Integer inspection) {
        this.inspection = inspection;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPoll() {
        return poll;
    }

    public void setPoll(Integer poll) {
        this.poll = poll;
    }

    public Integer getAccount_type() {
        return account_type;
    }

    public void setAccount_type(Integer account_type) {
        this.account_type = account_type;
    }

    public Integer getSilence() {
        return silence;
    }

    public void setSilence(Integer silence) {
        this.silence = silence;
    }

    public Integer getSnd_volume() {
        return snd_volume;
    }

    public void setSnd_volume(Integer snd_volume) {
        this.snd_volume = snd_volume;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getMix() {
        return mix;
    }

    public void setMix(Integer mix) {
        this.mix = mix;
    }

    public Integer getMute() {
        return mute;
    }

    public void setMute(Integer mute) {
        this.mute = mute;
    }

    public Integer getUpload() {
        return upload;
    }

    public void setUpload(Integer upload) {
        this.upload = upload;
    }

    public Integer getRcv_volume() {
        return rcv_volume;
    }

    public void setRcv_volume(Integer rcv_volume) {
        this.rcv_volume = rcv_volume;
    }

    public Integer getCall_mode() {
        return call_mode;
    }

    public void setCall_mode(Integer call_mode) {
        this.call_mode = call_mode;
    }

    public Integer getBitrate() {
        return bitrate;
    }

    public void setBitrate(Integer bitrate) {
        this.bitrate = bitrate;
    }

    public Integer getOnline() {
        return online;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }

    public String getMt_id() {
        return mt_id;
    }

    public void setMt_id(String mt_id) {
        this.mt_id = mt_id;
    }

    public Integer getRec() {
        return rec;
    }

    public void setRec(Integer rec) {
        this.rec = rec;
    }

    public Integer getVmp() {
        return vmp;
    }

    public void setVmp(Integer vmp) {
        this.vmp = vmp;
    }

    public String getE164() {
        return e164;
    }

    public void setE164(String e164) {
        this.e164 = e164;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public List<Chn> getDv_rcv_chn() {
        return dv_rcv_chn;
    }

    public void setDv_rcv_chn(List<Chn> dv_rcv_chn) {
        this.dv_rcv_chn = dv_rcv_chn;
    }

    public List<Chn> getA_rcv_chn() {
        return a_rcv_chn;
    }

    public void setA_rcv_chn(List<Chn> a_rcv_chn) {
        this.a_rcv_chn = a_rcv_chn;
    }

    public List<Chn> getDv_snd_chn() {
        return dv_snd_chn;
    }

    public void setDv_snd_chn(List<Chn> dv_snd_chn) {
        this.dv_snd_chn = dv_snd_chn;
    }

    public List<Chn> getA_snd_chn() {
        return a_snd_chn;
    }

    public void setA_snd_chn(List<Chn> a_snd_chn) {
        this.a_snd_chn = a_snd_chn;
    }

    public List<Chn> getV_rcv_chn() {
        return v_rcv_chn;
    }

    public void setV_rcv_chn(List<Chn> v_rcv_chn) {
        this.v_rcv_chn = v_rcv_chn;
    }

    public List<Chn> getV_snd_chn() {
        return v_snd_chn;
    }

    public void setV_snd_chn(List<Chn> v_snd_chn) {
        this.v_snd_chn = v_snd_chn;
    }

    public static class Chn {
        /**
         * chn_id : 1
         * chn_label : 0
         * chn_alias :
         * cur_video_idx : 0
         * format : 4
         * bitrate : 1856
         * resolution : 12
         */

        private Integer chn_id;
        private Integer chn_label;
        private String chn_alias;
        private Integer cur_video_idx;
        private Integer format;
        private Integer bitrate;
        private Integer resolution;

        public Integer getChn_id() {
            return chn_id;
        }

        public void setChn_id(Integer chn_id) {
            this.chn_id = chn_id;
        }

        public Integer getChn_label() {
            return chn_label;
        }

        public void setChn_label(Integer chn_label) {
            this.chn_label = chn_label;
        }

        public String getChn_alias() {
            return chn_alias;
        }

        public void setChn_alias(String chn_alias) {
            this.chn_alias = chn_alias;
        }

        public Integer getCur_video_idx() {
            return cur_video_idx;
        }

        public void setCur_video_idx(Integer cur_video_idx) {
            this.cur_video_idx = cur_video_idx;
        }

        public Integer getFormat() {
            return format;
        }

        public void setFormat(Integer format) {
            this.format = format;
        }

        public Integer getBitrate() {
            return bitrate;
        }

        public void setBitrate(Integer bitrate) {
            this.bitrate = bitrate;
        }

        public Integer getResolution() {
            return resolution;
        }

        public void setResolution(Integer resolution) {
            this.resolution = resolution;
        }
    }

    /**
     * 1	SQCIF (128×96)
     * 2	SCIF (176×144)
     * 3	CIF (352×288)
     * 4	2CIF(352×576)
     * 5	4CIF (704×576)
     * 6	16CIF (1408×1152)
     * 7	自适应，仅用于MPEG4
     * 8	SIF (352×240)
     * 9	2SIF (354×480)
     * 10	4SIF (704×480)
     * 11	VGA (640×480)
     * 12	SVGA (800×600)
     * 13	XGA (1024×768)
     * 14	WXGA (1280×768 | 1280×800)
     * 15	WCIF (512×288)
     * 21	SQCIF (112×96)
     * 22	SQCIF (96×80)
     * 31	Wide 4CIF (1024×576)
     * 32	720 (1280×720)
     * 33	SXGA (1280×1024)
     * 34	UXGA (1600×1200)
     * 35	1080P (1920×1080p 1920×544i)
     * 37	WSXGA (1440×900)
     * 38	XVGA (1280×960)
     * 39	WSXGA+ (1680x1050)
     * 41	1440×816 3:4
     * 42	1280×720 2:3
     * 43	960×544 1:2
     * 44	640×368 1:3
     * 45	480×272 1:4
     * 46	384×272 1:5
     * 51	960×544 3:4
     * 52	864×480 2:3
     * 53	640×368 1:2
     * 54	432×240 1:3
     * 55	320×192 1:4
     * 56	640x544
     * 57	320x272
     * 58	640x480
     * 61	480×352, iPad专用
     * 62	4k2k (3840×2160)
     * 63	1536×864
     * 64	1536×1080
     * 65	2304×1296
     * 66	2560×1440
     * 67	2880×1620
     */
    public String resolutionRatio(Integer resolutionRatioInt) {
        String resolutionRatioStr = null;
        switch (resolutionRatioInt) {
            case 1:
                resolutionRatioStr = "128×96";
                break;
            case 2:
                resolutionRatioStr = "176×144";
                break;
            case 3:
                resolutionRatioStr = "352×288";
                break;
            case 4:
                resolutionRatioStr = "352×576";
                break;
            case 5:
                resolutionRatioStr = "704×576";
                break;
            case 6:
                resolutionRatioStr = "1408×1152";
                break;
            case 7:
                resolutionRatioStr = "1024×728";
                break;
            case 8:
                resolutionRatioStr = "352×240";
                break;
            case 9:
                resolutionRatioStr = "354×480";
                break;
            case 10:
                resolutionRatioStr = "704×480";
                break;
            case 11:
                resolutionRatioStr = "640×480";
                break;
            case 12:
                resolutionRatioStr = "800×600";
                break;
            case 13:
                resolutionRatioStr = "1024×768";
                break;
            case 14:
                resolutionRatioStr = "1280×768";
                break;
            case 15:
                resolutionRatioStr = "512×288";
                break;
            case 21:
                resolutionRatioStr = "112×96";
                break;
            case 22:
                resolutionRatioStr = "96×80";
                break;
            case 31:
                resolutionRatioStr = "1024×576";
                break;
            case 32:
                resolutionRatioStr = "1280×720";
                break;
            case 33:
                resolutionRatioStr = "1280×1024";
                break;
            case 34:
                resolutionRatioStr = "1600×1200";
                break;
            case 35:
                resolutionRatioStr = "1920×1080";
                break;
            case 37:
                resolutionRatioStr = "1440×900";
                break;
            case 38:
                resolutionRatioStr = "1280×960";
                break;
            case 39:
                resolutionRatioStr = "1680x1050";
                break;
            case 41:
                resolutionRatioStr = "1440×816";
                break;
            case 42:
                resolutionRatioStr = "1280×720";
                break;
            case 43:
                resolutionRatioStr = "960×544";
                break;
            case 44:
                resolutionRatioStr = "640×368";
                break;
            case 45:
                resolutionRatioStr = "480×272";
                break;
            case 46:
                resolutionRatioStr = "384×272";
                break;
            case 51:
                resolutionRatioStr = "960×544";
                break;
            case 52:
                resolutionRatioStr = "864×480";
                break;
            case 53:
                resolutionRatioStr = "640×368";
                break;
            case 54:
                resolutionRatioStr = "432×240";
                break;
            case 55:
                resolutionRatioStr = "320×192";
                break;
            case 56:
                resolutionRatioStr = "640x544";
                break;
            case 57:
                resolutionRatioStr = "320x272";
                break;
            case 58:
                resolutionRatioStr = "640x480";
                break;
            case 61:
                resolutionRatioStr = "480×352";
                break;
            case 62:
                resolutionRatioStr = "3840×2160";
                break;
            case 63:
                resolutionRatioStr = "1536×864";
                break;
            case 64:
                resolutionRatioStr = "1536×1080";
                break;
            case 65:
                resolutionRatioStr = "2304×1296";
                break;
            case 66:
                resolutionRatioStr = "2560×1440";
                break;
            case 67:
                resolutionRatioStr = "2880×1620";
                break;
            default:
                return null;
        }

        return resolutionRatioStr;
    }

    /**
     * 双流格式，获取级联终端信息时不返回
     * 1-MPEG;
     * 2-H.261;
     * 3-H.263;
     * 4-H.264_HP;
     * 5-H.264_BP;
     * 6-H.265;
     * 7-H.263+;
     *
     * @param format
     * @return
     */
    public String getFormatStr(Integer format) {
        String formatStr = null;
        switch (format) {
            case 1:
                formatStr = "MPEG";
                break;
            case 2:
                formatStr = "H.261";
                break;
            case 3:
                formatStr = "H.263";
                break;
            case 4:
                formatStr = "H.264_HP";
                break;
            case 5:
                formatStr = "H.264_BP";
                break;
            case 6:
                formatStr = "H.265";
                break;
            case 7:
                formatStr = "H.263+";
                break;
            default:
                formatStr = "";
        }
        return formatStr;
    }
}
