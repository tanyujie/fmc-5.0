package com.paradisecloud.fcm.mcu.kdc.model.request.cm;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.fcm.mcu.kdc.model.request.CommonRequest;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class CmStartMrRequest extends CommonRequest {

    private int create_type;
    private String template_id;
    private String name;
    private int duration;
    private int bitrate;
    private int closed_conf;
    private int safe_conf;
    private String password;
    private int encrypted_type;
    private int encrypted_auth;
    private int conf_type;
    private int call_mode;
    private int call_times;
    private int call_interval;
    private int fec_mode;
    private int mute_filter;
    private int mute;
    private int silence;
    private int video_quality;
    private String encrypted_key;
    private int dual_mode;
    private int doubleflow;
    private int voice_activity_detection;
    private int vacinterval;
    private int cascade_mode;
    private int cascade_upload;
    private int cascade_return;
    private int cascade_return_para;
    private int public_conf;
    private int max_join_mt;
    private int auto_end;
    private int preoccupy_resource;
    private int one_reforming;
    private String platform_id;
    private Vmp vmp;
    private List<VideoFormat> video_formats;
    private List<Integer> audio_formats;

    public static class Vmp {
        /**
         * mode : 2
         * layout : 1
         * voice_hint : 0
         * broadcast : 0
         * show_mt_name : 1
         * mt_name_style : {"font_size":1,"font_color":"#FFFFFF","position":1}
         * members : []
         */

        private int mode;
        private int layout;
        private int voice_hint;
        private int broadcast;
        private int show_mt_name;
        private MtNameStyle mt_name_style;

        public int getMode() {
            return mode;
        }

        public void setMode(int mode) {
            this.mode = mode;
        }

        public int getLayout() {
            return layout;
        }

        public void setLayout(int layout) {
            this.layout = layout;
        }

        public int getVoice_hint() {
            return voice_hint;
        }

        public void setVoice_hint(int voice_hint) {
            this.voice_hint = voice_hint;
        }

        public int getBroadcast() {
            return broadcast;
        }

        public void setBroadcast(int broadcast) {
            this.broadcast = broadcast;
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
    }

    public static class VideoFormat {
        /**
         * format : 5
         * resolution : 12
         * frame : 30
         * bitrate : 1024
         */

        private int format;
        private int resolution;
        private int frame;
        private int bitrate;

        public int getFormat() {
            return format;
        }

        public void setFormat(int format) {
            this.format = format;
        }

        public int getResolution() {
            return resolution;
        }

        public void setResolution(int resolution) {
            this.resolution = resolution;
        }

        public int getFrame() {
            return frame;
        }

        public void setFrame(int frame) {
            this.frame = frame;
        }

        public int getBitrate() {
            return bitrate;
        }

        public void setBitrate(int bitrate) {
            this.bitrate = bitrate;
        }
    }

    public int getCreate_type() {
        return create_type;
    }

    public void setCreate_type(int create_type) {
        this.create_type = create_type;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public int getClosed_conf() {
        return closed_conf;
    }

    public void setClosed_conf(int closed_conf) {
        this.closed_conf = closed_conf;
    }

    public int getSafe_conf() {
        return safe_conf;
    }

    public void setSafe_conf(int safe_conf) {
        this.safe_conf = safe_conf;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getEncrypted_type() {
        return encrypted_type;
    }

    public void setEncrypted_type(int encrypted_type) {
        this.encrypted_type = encrypted_type;
    }

    public int getEncrypted_auth() {
        return encrypted_auth;
    }

    public void setEncrypted_auth(int encrypted_auth) {
        this.encrypted_auth = encrypted_auth;
    }

    public int getConf_type() {
        return conf_type;
    }

    public void setConf_type(int conf_type) {
        this.conf_type = conf_type;
    }

    public int getCall_mode() {
        return call_mode;
    }

    public void setCall_mode(int call_mode) {
        this.call_mode = call_mode;
    }

    public int getCall_times() {
        return call_times;
    }

    public void setCall_times(int call_times) {
        this.call_times = call_times;
    }

    public int getCall_interval() {
        return call_interval;
    }

    public void setCall_interval(int call_interval) {
        this.call_interval = call_interval;
    }

    public int getFec_mode() {
        return fec_mode;
    }

    public void setFec_mode(int fec_mode) {
        this.fec_mode = fec_mode;
    }

    public int getMute_filter() {
        return mute_filter;
    }

    public void setMute_filter(int mute_filter) {
        this.mute_filter = mute_filter;
    }

    public int getMute() {
        return mute;
    }

    public void setMute(int mute) {
        this.mute = mute;
    }

    public int getSilence() {
        return silence;
    }

    public void setSilence(int silence) {
        this.silence = silence;
    }

    public int getVideo_quality() {
        return video_quality;
    }

    public void setVideo_quality(int video_quality) {
        this.video_quality = video_quality;
    }

    public String getEncrypted_key() {
        return encrypted_key;
    }

    public void setEncrypted_key(String encrypted_key) {
        this.encrypted_key = encrypted_key;
    }

    public int getDual_mode() {
        return dual_mode;
    }

    public void setDual_mode(int dual_mode) {
        this.dual_mode = dual_mode;
    }

    public int getDoubleflow() {
        return doubleflow;
    }

    public void setDoubleflow(int doubleflow) {
        this.doubleflow = doubleflow;
    }

    public int getVoice_activity_detection() {
        return voice_activity_detection;
    }

    public void setVoice_activity_detection(int voice_activity_detection) {
        this.voice_activity_detection = voice_activity_detection;
    }

    public int getVacinterval() {
        return vacinterval;
    }

    public void setVacinterval(int vacinterval) {
        this.vacinterval = vacinterval;
    }

    public int getCascade_mode() {
        return cascade_mode;
    }

    public void setCascade_mode(int cascade_mode) {
        this.cascade_mode = cascade_mode;
    }

    public int getCascade_upload() {
        return cascade_upload;
    }

    public void setCascade_upload(int cascade_upload) {
        this.cascade_upload = cascade_upload;
    }

    public int getCascade_return() {
        return cascade_return;
    }

    public void setCascade_return(int cascade_return) {
        this.cascade_return = cascade_return;
    }

    public int getCascade_return_para() {
        return cascade_return_para;
    }

    public void setCascade_return_para(int cascade_return_para) {
        this.cascade_return_para = cascade_return_para;
    }

    public int getPublic_conf() {
        return public_conf;
    }

    public void setPublic_conf(int public_conf) {
        this.public_conf = public_conf;
    }

    public int getMax_join_mt() {
        return max_join_mt;
    }

    public void setMax_join_mt(int max_join_mt) {
        this.max_join_mt = max_join_mt;
    }

    public int getAuto_end() {
        return auto_end;
    }

    public void setAuto_end(int auto_end) {
        this.auto_end = auto_end;
    }

    public int getPreoccupy_resource() {
        return preoccupy_resource;
    }

    public void setPreoccupy_resource(int preoccupy_resource) {
        this.preoccupy_resource = preoccupy_resource;
    }

    public int getOne_reforming() {
        return one_reforming;
    }

    public void setOne_reforming(int one_reforming) {
        this.one_reforming = one_reforming;
    }

    public String getPlatform_id() {
        return platform_id;
    }

    public void setPlatform_id(String platform_id) {
        this.platform_id = platform_id;
    }

    public Vmp getVmp() {
        return vmp;
    }

    public void setVmp(Vmp vmp) {
        this.vmp = vmp;
    }

    public List<VideoFormat> getVideo_formats() {
        return video_formats;
    }

    public void setVideo_formats(List<VideoFormat> video_formats) {
        this.video_formats = video_formats;
    }

    public List<Integer> getAudio_formats() {
        return audio_formats;
    }

    public void setAudio_formats(List<Integer> audio_formats) {
        this.audio_formats = audio_formats;
    }

    @Override
    public List<NameValuePair> buildToList() {
        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("params", JSON.toJSONString(this)));
        return list;
    }

    public static CmStartMrRequest buildDefaultRequest() {
        CmStartMrRequest cmStartMrRequest = new CmStartMrRequest();
        cmStartMrRequest.setCreate_type(1);
        cmStartMrRequest.setTemplate_id("");
        cmStartMrRequest.setName("会议");//会议名
        cmStartMrRequest.setDuration(2880);
        cmStartMrRequest.setBitrate(1024);//带宽
        cmStartMrRequest.setClosed_conf(0);
        cmStartMrRequest.setSafe_conf(0);
        cmStartMrRequest.setPassword("");//会议密码
        cmStartMrRequest.setEncrypted_type(0);
        cmStartMrRequest.setEncrypted_auth(0);
        cmStartMrRequest.setConf_type(0);
        cmStartMrRequest.setCall_mode(0);
        cmStartMrRequest.setCall_times(0);
        cmStartMrRequest.setCall_interval(20);
        cmStartMrRequest.setFec_mode(0);
        cmStartMrRequest.setMute_filter(1);
        cmStartMrRequest.setMute(1);//开启哑音
        cmStartMrRequest.setSilence(0);
        cmStartMrRequest.setVideo_quality(0);
        cmStartMrRequest.setEncrypted_key("");
        cmStartMrRequest.setDual_mode(1);
        cmStartMrRequest.setDoubleflow(0);
        cmStartMrRequest.setVoice_activity_detection(0);
        cmStartMrRequest.setVacinterval(3);
        cmStartMrRequest.setCascade_mode(0);
        cmStartMrRequest.setCascade_upload(0);
        cmStartMrRequest.setCascade_return(0);
        cmStartMrRequest.setCascade_return_para(0);
        cmStartMrRequest.setPublic_conf(1);
        cmStartMrRequest.setMax_join_mt(192);
        cmStartMrRequest.setAuto_end(1);
        cmStartMrRequest.setPreoccupy_resource(1);
        cmStartMrRequest.setOne_reforming(0);
        cmStartMrRequest.setPlatform_id("");
        List<VideoFormat> videoFormatList = new ArrayList<>();
        {
            VideoFormat videoFormat = new VideoFormat();
            videoFormat.setFormat(5);
            videoFormat.setResolution(12);
            videoFormat.setFrame(30);
            videoFormat.setBitrate(1024);
            videoFormatList.add(videoFormat);
        }
        cmStartMrRequest.setVideo_formats(videoFormatList);
        List<Integer> audioFormatList = new ArrayList<>();
        audioFormatList.add(2);
        audioFormatList.add(3);
        cmStartMrRequest.setAudio_formats(audioFormatList);
        Vmp vmp = new Vmp();
        vmp.setMode(2);
        vmp.setLayout(1);
        vmp.setVoice_hint(0);
        vmp.setBroadcast(0);
        vmp.setShow_mt_name(1);
        Vmp.MtNameStyle mtNameStyle = new Vmp.MtNameStyle();
        mtNameStyle.setFont_size(1);
        mtNameStyle.setFont_color("#FFFFFF");
        mtNameStyle.setPosition(1);
        vmp.setMt_name_style(mtNameStyle);
        cmStartMrRequest.setVmp(vmp);

        return cmStartMrRequest;
    }

}
