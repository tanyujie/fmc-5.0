package com.paradisecloud.fcm.mcu.kdc.model.request.cc;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.fcm.mcu.kdc.model.request.CommonRequest;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class CcSetMrSecureRequest extends CommonRequest {

    /**
     * password : 123456
     * safe_conf : 1
     * closed_conf : 0
     */
    private String conf_id;
    private String password;
    private int safe_conf;
    private int closed_conf;

    public String getConf_id() {
        return conf_id;
    }

    public void setConf_id(String conf_id) {
        this.conf_id = conf_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSafe_conf() {
        return safe_conf;
    }

    public void setSafe_conf(int safe_conf) {
        this.safe_conf = safe_conf;
    }

    public int getClosed_conf() {
        return closed_conf;
    }

    public void setClosed_conf(int closed_conf) {
        this.closed_conf = closed_conf;
    }

    @Override
    public List<NameValuePair> buildToList() {
        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("params", JSON.toJSONString(this)));
        return list;
    }
}
