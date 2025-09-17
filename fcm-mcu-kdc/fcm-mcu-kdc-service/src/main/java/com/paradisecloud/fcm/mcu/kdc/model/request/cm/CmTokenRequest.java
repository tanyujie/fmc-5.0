package com.paradisecloud.fcm.mcu.kdc.model.request.cm;

import com.paradisecloud.fcm.mcu.kdc.model.request.CommonRequest;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class CmTokenRequest extends CommonRequest {

    private String oauth_consumer_key;
    private String oauth_consumer_secret;

    public String getOauth_consumer_key() {
        return oauth_consumer_key;
    }

    public void setOauth_consumer_key(String oauth_consumer_key) {
        this.oauth_consumer_key = oauth_consumer_key;
    }

    public String getOauth_consumer_secret() {
        return oauth_consumer_secret;
    }

    public void setOauth_consumer_secret(String oauth_consumer_secret) {
        this.oauth_consumer_secret = oauth_consumer_secret;
    }

    @Override
    public List<NameValuePair> buildToList() {
        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("oauth_consumer_key", oauth_consumer_key));
        list.add(new BasicNameValuePair("oauth_consumer_secret", oauth_consumer_secret));
        return list;
    }
}
