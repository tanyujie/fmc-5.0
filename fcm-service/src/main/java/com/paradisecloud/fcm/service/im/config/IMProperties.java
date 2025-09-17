package com.paradisecloud.fcm.service.im.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ttclouds.im")
public class IMProperties {

    private String active;
    private Tencent tencent;

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public Tencent getTencent() {
        return tencent;
    }

    public void setTencent(Tencent tencent) {
        this.tencent = tencent;
    }

    public static class Tencent {

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
    }
}
