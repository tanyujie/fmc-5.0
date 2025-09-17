package com.paradisecloud.fcm.mcu.zj.model.response.cc;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

import java.io.Serializable;
import java.util.List;

public class CcQueryMrEpsMediaResponse extends CommonResponse {

    private Integer refer_num;
    private String remote_addr;
    private String uuid;
    private String local_addr;
    private List<Data> data;

    public Integer getRefer_num() {
        return refer_num;
    }

    public void setRefer_num(Integer refer_num) {
        this.refer_num = refer_num;
    }

    public String getRemote_addr() {
        return remote_addr;
    }

    public void setRemote_addr(String remote_addr) {
        this.remote_addr = remote_addr;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLocal_addr() {
        return local_addr;
    }

    public void setLocal_addr(String local_addr) {
        this.local_addr = local_addr;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Data implements Serializable {

        private String node;
        private Integer rx_bitrate;
        private String ssrc;
        private Integer tx_packets_sent;
        private Integer rx_packets_received;
        private Integer rx_packets_lost;
        private Integer start_time;
        private String rx_resolution;
        private Integer rx_jitter;
        private Float tx_lost_rate;
        private String tx_resolution;
        private Float rx_lost_rate;
        private String tx_codec;
        private Integer id;
        private String tx_packets_lost;
        private Integer fur;
        private String rx_codec;
        private Integer tx_bitrate;
        private Integer tx_jitter;
        private Integer fps;
        private String type;
        private String rx_width;
        private String tx_width;
        private String rx_height;
        private String tx_height;

        public String getNode() {
            return node;
        }

        public void setNode(String node) {
            this.node = node;
        }

        public Integer getRx_bitrate() {
            return rx_bitrate;
        }

        public void setRx_bitrate(Integer rx_bitrate) {
            this.rx_bitrate = rx_bitrate;
        }

        public String getSsrc() {
            return ssrc;
        }

        public void setSsrc(String ssrc) {
            this.ssrc = ssrc;
        }

        public Integer getTx_packets_sent() {
            return tx_packets_sent;
        }

        public void setTx_packets_sent(Integer tx_packets_sent) {
            this.tx_packets_sent = tx_packets_sent;
        }

        public Integer getRx_packets_received() {
            return rx_packets_received;
        }

        public void setRx_packets_received(Integer rx_packets_received) {
            this.rx_packets_received = rx_packets_received;
        }

        public Integer getRx_packets_lost() {
            return rx_packets_lost;
        }

        public void setRx_packets_lost(Integer rx_packets_lost) {
            this.rx_packets_lost = rx_packets_lost;
        }

        public Integer getStart_time() {
            return start_time;
        }

        public void setStart_time(Integer start_time) {
            this.start_time = start_time;
        }

        public String getRx_resolution() {
            return rx_resolution;
        }

        public void setRx_resolution(String rx_resolution) {
            this.rx_resolution = rx_resolution;
        }

        public Integer getRx_jitter() {
            return rx_jitter;
        }

        public void setRx_jitter(Integer rx_jitter) {
            this.rx_jitter = rx_jitter;
        }

        public Float getTx_lost_rate() {
            return tx_lost_rate;
        }

        public void setTx_lost_rate(Float tx_lost_rate) {
            this.tx_lost_rate = tx_lost_rate;
        }

        public String getTx_resolution() {
            return tx_resolution;
        }

        public void setTx_resolution(String tx_resolution) {
            this.tx_resolution = tx_resolution;
        }

        public Float getRx_lost_rate() {
            return rx_lost_rate;
        }

        public void setRx_lost_rate(Float rx_lost_rate) {
            this.rx_lost_rate = rx_lost_rate;
        }

        public String getTx_codec() {
            return tx_codec;
        }

        public void setTx_codec(String tx_codec) {
            this.tx_codec = tx_codec;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getTx_packets_lost() {
            return tx_packets_lost;
        }

        public void setTx_packets_lost(String tx_packets_lost) {
            this.tx_packets_lost = tx_packets_lost;
        }

        public Integer getFur() {
            return fur;
        }

        public void setFur(Integer fur) {
            this.fur = fur;
        }

        public String getRx_codec() {
            return rx_codec;
        }

        public void setRx_codec(String rx_codec) {
            this.rx_codec = rx_codec;
        }

        public Integer getTx_bitrate() {
            return tx_bitrate;
        }

        public void setTx_bitrate(Integer tx_bitrate) {
            this.tx_bitrate = tx_bitrate;
        }

        public Integer getTx_jitter() {
            return tx_jitter;
        }

        public void setTx_jitter(Integer tx_jitter) {
            this.tx_jitter = tx_jitter;
        }

        public Integer getFps() {
            return fps;
        }

        public void setFps(Integer fps) {
            this.fps = fps;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getRx_width() {
            return rx_width;
        }

        public void setRx_width(String rx_width) {
            this.rx_width = rx_width;
        }

        public String getTx_width() {
            return tx_width;
        }

        public void setTx_width(String tx_width) {
            this.tx_width = tx_width;
        }

        public String getRx_height() {
            return rx_height;
        }

        public void setRx_height(String rx_height) {
            this.rx_height = rx_height;
        }

        public String getTx_height() {
            return tx_height;
        }

        public void setTx_height(String tx_height) {
            this.tx_height = tx_height;
        }
    }
}
