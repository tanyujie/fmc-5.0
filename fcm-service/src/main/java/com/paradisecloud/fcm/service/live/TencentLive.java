package com.paradisecloud.fcm.service.live;

public class TencentLive implements Live {

    private String secretId;
    private String secretKey;
    private String authKey;
    private String region;
    private String domainName;
    private String appName;
    private String pullDomainName;
    private String playbackDomainName;
    private String noticeSecretKey;

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPullDomainName() {
        return pullDomainName;
    }

    public void setPullDomainName(String pullDomainName) {
        this.pullDomainName = pullDomainName;
    }

    public String getNoticeSecretKey() {
        return noticeSecretKey;
    }

    public void setNoticeSecretKey(String noticeSecretKey) {
        this.noticeSecretKey = noticeSecretKey;
    }

    public String getPlaybackDomainName() {
        return playbackDomainName;
    }

    public void setPlaybackDomainName(String playbackDomainName) {
        this.playbackDomainName = playbackDomainName;
    }

}
