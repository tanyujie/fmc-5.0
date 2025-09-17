package com.paradisecloud.fcm.dao.model.vo;

import com.paradisecloud.fcm.dao.model.BusiClient;

import java.util.List;

public class BusiClientVo extends BusiClient {

    private String email;
    private String phoneNumber;
    private Boolean register;
    private List<BusiClientResourceVo> resourceList;
    private boolean autoUpdate;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean isRegister() {
        return register;
    }

    public void setRegister(Boolean register) {
        this.register = register;
    }

    public List<BusiClientResourceVo> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<BusiClientResourceVo> resourceList) {
        this.resourceList = resourceList;
    }

    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }
}
