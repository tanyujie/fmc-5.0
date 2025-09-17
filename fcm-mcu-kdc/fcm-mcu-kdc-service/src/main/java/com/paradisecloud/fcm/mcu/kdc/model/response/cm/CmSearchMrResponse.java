package com.paradisecloud.fcm.mcu.kdc.model.response.cm;

import com.paradisecloud.fcm.mcu.kdc.model.response.CommonResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CmSearchMrResponse extends CommonResponse {

    private Integer total;
    private List<ConfInfo> confs;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<ConfInfo> getConfs() {
        return confs;
    }

    public void setConfs(List<ConfInfo> confs) {
        this.confs = confs;
    }

    public static class ConfInfo {

        private String name;
        private String conf_id;
        private Integer conf_type;
        private String start_time;
        private String end_time;
        private Integer duration;
        private Integer bitrate;
        private String encrypted_key;
        private Integer public_conf;
        private String meeting_id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getConf_id() {
            return conf_id;
        }

        public void setConf_id(String conf_id) {
            this.conf_id = conf_id;
        }

        public Integer getConf_type() {
            return conf_type;
        }

        public void setConf_type(Integer conf_type) {
            this.conf_type = conf_type;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public Integer getDuration() {
            return duration;
        }

        public void setDuration(Integer duration) {
            this.duration = duration;
        }

        public Integer getBitrate() {
            return bitrate;
        }

        public void setBitrate(Integer bitrate) {
            this.bitrate = bitrate;
        }

        public String getEncrypted_key() {
            return encrypted_key;
        }

        public void setEncrypted_key(String encrypted_key) {
            this.encrypted_key = encrypted_key;
        }

        public Integer getPublic_conf() {
            return public_conf;
        }

        public void setPublic_conf(Integer public_conf) {
            this.public_conf = public_conf;
        }

        public String getMeeting_id() {
            return meeting_id;
        }

        public void setMeeting_id(String meeting_id) {
            this.meeting_id = meeting_id;
        }
    }
}
