package com.paradisecloud.fcm.mcu.kdc.model.response.cm;

import com.paradisecloud.fcm.mcu.kdc.model.response.CommonResponse;
import org.json.JSONObject;
import org.json.XML;

public class CmLoginResponse extends CommonResponse {

    private String usermoid;
    private String userdomainmoid;
    private String userdomainname;
    private String cookie;

    public String getUsermoid() {
        return usermoid;
    }

    public void setUsermoid(String usermoid) {
        this.usermoid = usermoid;
    }

    public String getUserdomainmoid() {
        return userdomainmoid;
    }

    public void setUserdomainmoid(String userdomainmoid) {
        this.userdomainmoid = userdomainmoid;
    }

    public String getUserdomainname() {
        return userdomainname;
    }

    public void setUserdomainname(String userdomainname) {
        this.userdomainname = userdomainname;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }
}
