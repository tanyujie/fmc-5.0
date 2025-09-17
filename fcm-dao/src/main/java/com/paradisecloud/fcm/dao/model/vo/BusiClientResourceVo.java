package com.paradisecloud.fcm.dao.model.vo;

import com.paradisecloud.fcm.dao.model.BusiClientResource;

public class BusiClientResourceVo extends BusiClientResource {

    private Long clientId;
    private String purchaseTypeName;
    private String purchaseTypeAlias;
    private String mcuTypeName;
    private String mcuTypeAlias;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getPurchaseTypeName() {
        return purchaseTypeName;
    }

    public void setPurchaseTypeName(String purchaseTypeName) {
        this.purchaseTypeName = purchaseTypeName;
    }

    public String getPurchaseTypeAlias() {
        return purchaseTypeAlias;
    }

    public void setPurchaseTypeAlias(String purchaseTypeAlias) {
        this.purchaseTypeAlias = purchaseTypeAlias;
    }

    public String getMcuTypeName() {
        return mcuTypeName;
    }

    public void setMcuTypeName(String mcuTypeName) {
        this.mcuTypeName = mcuTypeName;
    }

    public String getMcuTypeAlias() {
        return mcuTypeAlias;
    }

    public void setMcuTypeAlias(String mcuTypeAlias) {
        this.mcuTypeAlias = mcuTypeAlias;
    }
}
