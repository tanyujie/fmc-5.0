package com.paradisecloud.fcm.service.im;

public interface IM {

    String getSdkAppId();

    String genUserSig(String userId, long expire);

    String getSecretKey();

    String getAdminUserId();
}
