package com.paradisecloud.fcm.mcu.kdc.model.request.cc;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.fcm.mcu.kdc.model.request.CommonRequest;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class CcAddMixingTerminalRequest extends CommonRequest {

    private String conf_id;
    private String mix_id;

    private List<Mt> members;

    public String getConf_id() {
        return conf_id;
    }

    public void setConf_id(String conf_id) {
        this.conf_id = conf_id;
    }

    public String getMix_id() {
        return mix_id;
    }

    public void setMix_id(String mix_id) {
        this.mix_id = mix_id;
    }

    public List<Mt> getMembers() {
        return members;
    }

    public void setMembers(List<Mt> members) {
        this.members = members;
    }

    public static class Mt {

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
