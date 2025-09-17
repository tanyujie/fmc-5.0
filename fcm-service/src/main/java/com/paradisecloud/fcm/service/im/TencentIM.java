package com.paradisecloud.fcm.service.im;

public class TencentIM implements IM {

    private String sdkAppId;
    private String secretKey;
    private String adminUserId;

    public String getSdkAppId() {
        return sdkAppId;
    }

    public void setSdkAppId(String sdkAppId) {
        this.sdkAppId = sdkAppId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(String adminUserId) {
        this.adminUserId = adminUserId;
    }

    private TLSSigAPIv2 tlsSigAPIv2 = null;

    @Override
    public String genUserSig(String userId, long expire) {
        if (tlsSigAPIv2 == null) {
            tlsSigAPIv2 = new TLSSigAPIv2(Long.valueOf(sdkAppId), secretKey);
        }
        return tlsSigAPIv2.genUserSig(userId, expire);
    }

}
