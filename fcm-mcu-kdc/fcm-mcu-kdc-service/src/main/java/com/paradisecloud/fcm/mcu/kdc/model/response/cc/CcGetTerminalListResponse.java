package com.paradisecloud.fcm.mcu.kdc.model.response.cc;

import com.paradisecloud.fcm.mcu.kdc.model.response.CommonResponse;

import java.util.List;

public class CcGetTerminalListResponse extends CommonResponse {

    private List<Mt> mts;

    public List<Mt> getMts() {
        return mts;
    }

    public void setMts(List<Mt> mts) {
        this.mts = mts;
    }

    public static class Mt {

        private String mt_id;
        private String account;
        private String ip;
        private Integer type;
        private String alias;
        private Integer online;
        private String e164;
        private Integer mix;
        private Integer call_mode;
        private Integer protocol;
        private Integer vmp;
        private Integer bitrate;
        private Integer dual;
        private Integer mute;

        public String getMt_id() {
            return mt_id;
        }

        public void setMt_id(String mt_id) {
            this.mt_id = mt_id;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public Integer getOnline() {
            return online;
        }

        public void setOnline(Integer online) {
            this.online = online;
        }

        public String getE164() {
            return e164;
        }

        public void setE164(String e164) {
            this.e164 = e164;
        }

        public Integer getMix() {
            return mix;
        }

        public void setMix(Integer mix) {
            this.mix = mix;
        }

        public Integer getCall_mode() {
            return call_mode;
        }

        public void setCall_mode(Integer call_mode) {
            this.call_mode = call_mode;
        }

        public Integer getProtocol() {
            return protocol;
        }

        public void setProtocol(Integer protocol) {
            this.protocol = protocol;
        }

        public Integer getVmp() {
            return vmp;
        }

        public void setVmp(Integer vmp) {
            this.vmp = vmp;
        }

        public Integer getBitrate() {
            return bitrate;
        }

        public void setBitrate(Integer bitrate) {
            this.bitrate = bitrate;
        }

        public Integer getDual() {
            return dual;
        }

        public void setDual(Integer dual) {
            this.dual = dual;
        }

        public Integer getMute() {
            return mute;
        }

        public void setMute(Integer mute) {
            this.mute = mute;
        }
    }
}
