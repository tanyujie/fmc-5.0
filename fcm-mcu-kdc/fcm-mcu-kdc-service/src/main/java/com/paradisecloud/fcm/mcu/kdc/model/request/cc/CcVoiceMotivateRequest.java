package com.paradisecloud.fcm.mcu.kdc.model.request.cc;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.fcm.mcu.kdc.model.request.CommonRequest;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;


public class CcVoiceMotivateRequest extends CommonRequest {

    private String conf_id;

    /**
     * state : 1
     * vacinterval : 5
     */

    private int state;
    private int vacinterval;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getVacinterval() {
        return vacinterval;
    }

    public void setVacinterval(int vacinterval) {
        this.vacinterval = vacinterval;
    }

    public String getConf_id() {
        return conf_id;
    }

    public void setConf_id(String conf_id) {
        this.conf_id = conf_id;
    }

    @Override
    public List<NameValuePair> buildToList() {
        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("params", JSON.toJSONString(this)));
        return list;
    }
}
