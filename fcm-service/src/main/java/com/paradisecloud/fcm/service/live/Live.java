package com.paradisecloud.fcm.service.live;

public interface Live {
    String getSecretId();

    String getSecretKey();

    String getAuthKey();

    String getRegion();

    String getDomainName();

    String getAppName();

    String getPullDomainName();

    String getPlaybackDomainName();

    String getNoticeSecretKey();
}
