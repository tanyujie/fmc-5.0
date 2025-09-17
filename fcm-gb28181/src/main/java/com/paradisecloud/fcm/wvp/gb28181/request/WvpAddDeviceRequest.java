package com.paradisecloud.fcm.wvp.gb28181.request;

public class WvpAddDeviceRequest extends WvpCommonRequest{

    private String deviceId;
    private String name;
    private String password;
    private  int subscribeCycleForCatalog;
    private int subscribeCycleForMobilePosition;

    private int mobilePositionSubmissionInterval=5;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSubscribeCycleForCatalog() {
        return subscribeCycleForCatalog;
    }

    public void setSubscribeCycleForCatalog(int subscribeCycleForCatalog) {
        this.subscribeCycleForCatalog = subscribeCycleForCatalog;
    }

    public int getSubscribeCycleForMobilePosition() {
        return subscribeCycleForMobilePosition;
    }

    public void setSubscribeCycleForMobilePosition(int subscribeCycleForMobilePosition) {
        this.subscribeCycleForMobilePosition = subscribeCycleForMobilePosition;
    }

    public int getMobilePositionSubmissionInterval() {
        return mobilePositionSubmissionInterval;
    }

    public void setMobilePositionSubmissionInterval(int mobilePositionSubmissionInterval) {
        this.mobilePositionSubmissionInterval = mobilePositionSubmissionInterval;
    }
}
