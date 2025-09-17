package com.paradisecloud.fcm.mcu.kdc.model.response.cc;

import com.paradisecloud.fcm.mcu.kdc.model.response.CommonResponse;

import java.util.List;

public class CcMrInfoResponse extends CommonResponse {

    /**
     * safe_conf : 0
     * video_formats : [{"bitrate":2048,"format":4,"frame":30,"resolution":12}]
     * user_domain_moid : 06977e80-b38d-4156-9605-9ae0faa6acc2
     * max_join_mt : 192
     * watermark : 1
     * cascade_mode : 0
     * encrypted_auth : 0
     * mute : 0
     * doubleflow : 0
     * user_domain_name : 默认用户域
     * voice_activity_detection : 0
     * meeting_id : cf845b58223e441eaf41e49593462b5e
     * conf_id : 7770012
     * video_quality : 0
     * mix_enable : 0
     * cascade_return_para : 0
     * encrypted_key :
     * platform_id : b6745ba0-b22d-426d-836c-4a6966bd0f5b
     * bitrate : 2048
     * creator : {"mobile":"","name":"zb","telephone":"","account_type":1,"account":"a4b6f275-d021-48ba-9d43-e751ad004711"}
     * name : zb会议模板
     * closed_conf : 0
     * call_mode : 0
     * conf_category : 1
     * enable_audience : 0
     * meeting_room_name :
     * need_password : 0
     * call_Integererval : 10
     * force_mute : 0
     * preoccupy_resource : 1
     * public_conf : 0
     * multi_stream : 0
     * call_times : 0
     * fec_mode : 0
     * subordinate_cascade : 0
     * enable_rtc : 0
     * vacIntegererval : 0
     * conf_protocol : 1
     * silence : 0
     * cascade_return : 0
     * superior_cascade : 0
     * one_reforming : 0
     * force_broadcast : 0
     * conf_type : 0
     * encrypted_type : 0
     * conf_level : 1
     * cascade_upload : 0
     * dual_mode : 1
     * poll_enable : 0
     * vmp_enable : 1
     * anonymous_mt : 0
     * duration : 0
     * mute_filter : 0
     * machine_room_moid : f2f77aae-d68a-4e1f-8e50-a5d491fb41f5
     * start_time : 2023-07-26T15:48:11+08:00
     * auto_end : 0
     * end_time : 2023-07-26T15:48:11+08:00
     */

    private Integer safe_conf;
    private String user_domain_moid;
    private Integer max_join_mt;
    private Integer watermark;
    private Integer cascade_mode;
    private Integer encrypted_auth;
    private Integer mute;
    private Integer doubleflow;
    private String user_domain_name;
    private Integer voice_activity_detection;
    private String meeting_id;
    private String conf_id;
    private Integer video_quality;
    private Integer mix_enable;
    private Integer cascade_return_para;
    private String encrypted_key;
    private String platform_id;
    private Integer bitrate;
    private Creator creator;
    private String name;
    private Integer closed_conf;
    private Integer call_mode;
    private Integer conf_category;
    private Integer enable_audience;
    private String meeting_room_name;
    private Integer need_password;
    private Integer call_Integererval;
    private Integer force_mute;
    private Integer preoccupy_resource;
    private Integer public_conf;
    private Integer multi_stream;
    private Integer call_times;
    private Integer fec_mode;
    private Integer subordinate_cascade;
    private Integer enable_rtc;
    private Integer vacIntegererval;
    private Integer conf_protocol;
    private Integer silence;
    private Integer cascade_return;
    private Integer superior_cascade;
    private Integer one_reforming;
    private Integer force_broadcast;
    private Integer conf_type;
    private Integer encrypted_type;
    private Integer conf_level;
    private Integer cascade_upload;
    private Integer dual_mode;
    private Integer poll_enable;
    private Integer vmp_enable;
    private Integer anonymous_mt;
    private Integer duration;
    private Integer mute_filter;
    private String machine_room_moid;
    private String start_time;
    private Integer auto_end;
    private String end_time;
    private List<VideoFormat> video_formats;

    public Integer getSafe_conf() {
        return safe_conf;
    }

    public void setSafe_conf(Integer safe_conf) {
        this.safe_conf = safe_conf;
    }

    public String getUser_domain_moid() {
        return user_domain_moid;
    }

    public void setUser_domain_moid(String user_domain_moid) {
        this.user_domain_moid = user_domain_moid;
    }

    public Integer getMax_join_mt() {
        return max_join_mt;
    }

    public void setMax_join_mt(Integer max_join_mt) {
        this.max_join_mt = max_join_mt;
    }

    public Integer getWatermark() {
        return watermark;
    }

    public void setWatermark(Integer watermark) {
        this.watermark = watermark;
    }

    public Integer getCascade_mode() {
        return cascade_mode;
    }

    public void setCascade_mode(Integer cascade_mode) {
        this.cascade_mode = cascade_mode;
    }

    public Integer getEncrypted_auth() {
        return encrypted_auth;
    }

    public void setEncrypted_auth(Integer encrypted_auth) {
        this.encrypted_auth = encrypted_auth;
    }

    public Integer getMute() {
        return mute;
    }

    public void setMute(Integer mute) {
        this.mute = mute;
    }

    public Integer getDoubleflow() {
        return doubleflow;
    }

    public void setDoubleflow(Integer doubleflow) {
        this.doubleflow = doubleflow;
    }

    public String getUser_domain_name() {
        return user_domain_name;
    }

    public void setUser_domain_name(String user_domain_name) {
        this.user_domain_name = user_domain_name;
    }

    public Integer getVoice_activity_detection() {
        return voice_activity_detection;
    }

    public void setVoice_activity_detection(Integer voice_activity_detection) {
        this.voice_activity_detection = voice_activity_detection;
    }

    public String getMeeting_id() {
        return meeting_id;
    }

    public void setMeeting_id(String meeting_id) {
        this.meeting_id = meeting_id;
    }

    public String getConf_id() {
        return conf_id;
    }

    public void setConf_id(String conf_id) {
        this.conf_id = conf_id;
    }

    public Integer getVideo_quality() {
        return video_quality;
    }

    public void setVideo_quality(Integer video_quality) {
        this.video_quality = video_quality;
    }

    public Integer getMix_enable() {
        return mix_enable;
    }

    public void setMix_enable(Integer mix_enable) {
        this.mix_enable = mix_enable;
    }

    public Integer getCascade_return_para() {
        return cascade_return_para;
    }

    public void setCascade_return_para(Integer cascade_return_para) {
        this.cascade_return_para = cascade_return_para;
    }

    public String getEncrypted_key() {
        return encrypted_key;
    }

    public void setEncrypted_key(String encrypted_key) {
        this.encrypted_key = encrypted_key;
    }

    public String getPlatform_id() {
        return platform_id;
    }

    public void setPlatform_id(String platform_id) {
        this.platform_id = platform_id;
    }

    public Integer getBitrate() {
        return bitrate;
    }

    public void setBitrate(Integer bitrate) {
        this.bitrate = bitrate;
    }

    public Creator getCreator() {
        return creator;
    }

    public void setCreator(Creator creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getClosed_conf() {
        return closed_conf;
    }

    public void setClosed_conf(Integer closed_conf) {
        this.closed_conf = closed_conf;
    }

    public Integer getCall_mode() {
        return call_mode;
    }

    public void setCall_mode(Integer call_mode) {
        this.call_mode = call_mode;
    }

    public Integer getConf_category() {
        return conf_category;
    }

    public void setConf_category(Integer conf_category) {
        this.conf_category = conf_category;
    }

    public Integer getEnable_audience() {
        return enable_audience;
    }

    public void setEnable_audience(Integer enable_audience) {
        this.enable_audience = enable_audience;
    }

    public String getMeeting_room_name() {
        return meeting_room_name;
    }

    public void setMeeting_room_name(String meeting_room_name) {
        this.meeting_room_name = meeting_room_name;
    }

    public Integer getNeed_password() {
        return need_password;
    }

    public void setNeed_password(Integer need_password) {
        this.need_password = need_password;
    }

    public Integer getCall_Integererval() {
        return call_Integererval;
    }

    public void setCall_Integererval(Integer call_Integererval) {
        this.call_Integererval = call_Integererval;
    }

    public Integer getForce_mute() {
        return force_mute;
    }

    public void setForce_mute(Integer force_mute) {
        this.force_mute = force_mute;
    }

    public Integer getPreoccupy_resource() {
        return preoccupy_resource;
    }

    public void setPreoccupy_resource(Integer preoccupy_resource) {
        this.preoccupy_resource = preoccupy_resource;
    }

    public Integer getPublic_conf() {
        return public_conf;
    }

    public void setPublic_conf(Integer public_conf) {
        this.public_conf = public_conf;
    }

    public Integer getMulti_stream() {
        return multi_stream;
    }

    public void setMulti_stream(Integer multi_stream) {
        this.multi_stream = multi_stream;
    }

    public Integer getCall_times() {
        return call_times;
    }

    public void setCall_times(Integer call_times) {
        this.call_times = call_times;
    }

    public Integer getFec_mode() {
        return fec_mode;
    }

    public void setFec_mode(Integer fec_mode) {
        this.fec_mode = fec_mode;
    }

    public Integer getSubordinate_cascade() {
        return subordinate_cascade;
    }

    public void setSubordinate_cascade(Integer subordinate_cascade) {
        this.subordinate_cascade = subordinate_cascade;
    }

    public Integer getEnable_rtc() {
        return enable_rtc;
    }

    public void setEnable_rtc(Integer enable_rtc) {
        this.enable_rtc = enable_rtc;
    }

    public Integer getVacIntegererval() {
        return vacIntegererval;
    }

    public void setVacIntegererval(Integer vacIntegererval) {
        this.vacIntegererval = vacIntegererval;
    }

    public Integer getConf_protocol() {
        return conf_protocol;
    }

    public void setConf_protocol(Integer conf_protocol) {
        this.conf_protocol = conf_protocol;
    }

    public Integer getSilence() {
        return silence;
    }

    public void setSilence(Integer silence) {
        this.silence = silence;
    }

    public Integer getCascade_return() {
        return cascade_return;
    }

    public void setCascade_return(Integer cascade_return) {
        this.cascade_return = cascade_return;
    }

    public Integer getSuperior_cascade() {
        return superior_cascade;
    }

    public void setSuperior_cascade(Integer superior_cascade) {
        this.superior_cascade = superior_cascade;
    }

    public Integer getOne_reforming() {
        return one_reforming;
    }

    public void setOne_reforming(Integer one_reforming) {
        this.one_reforming = one_reforming;
    }

    public Integer getForce_broadcast() {
        return force_broadcast;
    }

    public void setForce_broadcast(Integer force_broadcast) {
        this.force_broadcast = force_broadcast;
    }

    public Integer getConf_type() {
        return conf_type;
    }

    public void setConf_type(Integer conf_type) {
        this.conf_type = conf_type;
    }

    public Integer getEncrypted_type() {
        return encrypted_type;
    }

    public void setEncrypted_type(Integer encrypted_type) {
        this.encrypted_type = encrypted_type;
    }

    public Integer getConf_level() {
        return conf_level;
    }

    public void setConf_level(Integer conf_level) {
        this.conf_level = conf_level;
    }

    public Integer getCascade_upload() {
        return cascade_upload;
    }

    public void setCascade_upload(Integer cascade_upload) {
        this.cascade_upload = cascade_upload;
    }

    public Integer getDual_mode() {
        return dual_mode;
    }

    public void setDual_mode(Integer dual_mode) {
        this.dual_mode = dual_mode;
    }

    public Integer getPoll_enable() {
        return poll_enable;
    }

    public void setPoll_enable(Integer poll_enable) {
        this.poll_enable = poll_enable;
    }

    public Integer getVmp_enable() {
        return vmp_enable;
    }

    public void setVmp_enable(Integer vmp_enable) {
        this.vmp_enable = vmp_enable;
    }

    public Integer getAnonymous_mt() {
        return anonymous_mt;
    }

    public void setAnonymous_mt(Integer anonymous_mt) {
        this.anonymous_mt = anonymous_mt;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getMute_filter() {
        return mute_filter;
    }

    public void setMute_filter(Integer mute_filter) {
        this.mute_filter = mute_filter;
    }

    public String getMachine_room_moid() {
        return machine_room_moid;
    }

    public void setMachine_room_moid(String machine_room_moid) {
        this.machine_room_moid = machine_room_moid;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public Integer getAuto_end() {
        return auto_end;
    }

    public void setAuto_end(Integer auto_end) {
        this.auto_end = auto_end;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public List<VideoFormat> getVideo_formats() {
        return video_formats;
    }

    public void setVideo_formats(List<VideoFormat> video_formats) {
        this.video_formats = video_formats;
    }

    public static class Creator {
        /**
         * mobile :
         * name : zb
         * telephone :
         * account_type : 1
         * account : a4b6f275-d021-48ba-9d43-e751ad004711
         */

        private String mobile;
        private String name;
        private String telephone;
        private Integer account_type;
        private String account;

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public Integer getAccount_type() {
            return account_type;
        }

        public void setAccount_type(Integer account_type) {
            this.account_type = account_type;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }
    }

    public static class VideoFormat {
        /**
         * bitrate : 2048
         * format : 4
         * frame : 30
         * resolution : 12
         */

        private Integer bitrate;
        private Integer format;
        private Integer frame;
        private Integer resolution;

        public Integer getBitrate() {
            return bitrate;
        }

        public void setBitrate(Integer bitrate) {
            this.bitrate = bitrate;
        }

        public Integer getFormat() {
            return format;
        }

        public void setFormat(Integer format) {
            this.format = format;
        }

        public Integer getFrame() {
            return frame;
        }

        public void setFrame(Integer frame) {
            this.frame = frame;
        }

        public Integer getResolution() {
            return resolution;
        }

        public void setResolution(Integer resolution) {
            this.resolution = resolution;
        }
    }
}
