package com.paradisecloud.fcm.web.cloud;

/**
 * @author nj
 * @date 2024/7/16 9:49
 */
public class CloudMeetingParams {
    private String secretId;
    private String secretKey;
    private String additionalParam;


    public CloudMeetingParams(String secretId, String secretKey, String additionalParam) {
        this.secretId = secretId;
        this.secretKey = secretKey;
        this.additionalParam = additionalParam;
    }

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

    public String getAdditionalParam() {
        return additionalParam;
    }

    public void setAdditionalParam(String additionalParam) {
        this.additionalParam = additionalParam;
    }
}
