package com.paradisecloud.fcm.mcu.kdc.model.request.cc;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.fcm.mcu.kdc.model.request.CommonRequest;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class CcMrForceMuteRequest extends CommonRequest {

    private String conf_id;
    /**
     * value : 1
     * force_mute : 1
     */

    private int value;
    private int force_mute;

    public String getConf_id() {
        return conf_id;
    }

    public void setConf_id(String conf_id) {
        this.conf_id = conf_id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getForce_mute() {
        return force_mute;
    }

    public void setForce_mute(int force_mute) {
        this.force_mute = force_mute;
    }

    @Override
    public List<NameValuePair> buildToList() {
        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("params", JSON.toJSONString(this)));
        return list;
    }
}
