package com.paradisecloud.fcm.mcu.kdc.model.request.cc;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.fcm.mcu.kdc.model.request.CommonRequest;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CcStartTerminalChooseSeeRequest extends CommonRequest {

    /**
     * mode : 1
     * src : {"type":2,"mt_id":"1"}
     * dst : {"mt_id":"1"}
     */

    private String conf_id;
    private int mode;
    private Src src;
    private Dst dst;

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public Src getSrc() {
        return src;
    }

    public void setSrc(Src src) {
        this.src = src;
    }

    public Dst getDst() {
        return dst;
    }

    public void setDst(Dst dst) {
        this.dst = dst;
    }

    public String getConf_id() {
        return conf_id;
    }

    public void setConf_id(String conf_id) {
        this.conf_id = conf_id;
    }

    public static class Src {
        /**
         * type : 2
         * mt_id : 1
         */

        private int type;
        private String mt_id;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getMt_id() {
            return mt_id;
        }

        public void setMt_id(String mt_id) {
            this.mt_id = mt_id;
        }
    }

    public static class Dst {
        /**
         * mt_id : 1
         */

        private String mt_id;

        public String getMt_id() {
            return mt_id;
        }

        public void setMt_id(String mt_id) {
            this.mt_id = mt_id;
        }
    }

    @Override
    public List<NameValuePair> buildToList() {
        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("params", JSON.toJSONString(this)));
        return list;
    }
}
