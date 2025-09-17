package com.paradisecloud.fcm.tencent.model;

/**
 * @author nj
 * @date 2024/2/4 9:26
 */
public enum MRACityEnum {

    BEIJING("42.187.185.2"),
    SHANGHAI("1.13.136.2"),
    CHONGQING("139.186.243.2"),
    GUANGZHOU("106.55.205.2"),
    HONG_KONG("129.226.105.2"),
    FRANKFURT("43.157.73.2");

    private final String ipAddress;

    MRACityEnum(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }


}
