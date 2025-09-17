package com.paradisecloud.fcm.dao.model.vo;

import com.paradisecloud.fcm.dao.model.BusiOps;

import java.util.List;

public class BusiOpsVo extends BusiOps {

    private String email;
    private String phoneNumber;
    private Boolean register;
    private List<BusiOpsResourceVo> resourceList;
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

    public List<BusiOpsResourceVo> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<BusiOpsResourceVo> resourceList) {
        this.resourceList = resourceList;
    }

    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }
}
