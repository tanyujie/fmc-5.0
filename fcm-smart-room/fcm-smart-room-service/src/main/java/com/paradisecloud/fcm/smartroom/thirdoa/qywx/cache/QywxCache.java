package com.paradisecloud.fcm.smartroom.thirdoa.qywx.cache;

import com.sinhy.http.HttpRequester;

public class QywxCache {

    private static final QywxCache INSTANCE = new QywxCache();

    private String corpId;
    private String corpSecret;
    private volatile String accessToken;
    private volatile long expiredTime;
    private HttpRequester httpRequester;

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getCorpSecret() {
        return corpSecret;
    }

    public void setCorpSecret(String corpSecret) {
        this.corpSecret = corpSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(long expiredTime) {
        this.expiredTime = expiredTime;
    }

    public static QywxCache getInstance() {
        return INSTANCE;
    }

    public HttpRequester getHttpRequester() {
        return httpRequester;
    }

    public void setHttpRequester(HttpRequester httpRequester) {
        this.httpRequester = httpRequester;
    }
}
