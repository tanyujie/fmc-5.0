package com.paradisecloud.fcm.mcu.plc.model;

public class CellInfo {

    private String id;
    private String forceStatus;
    private String forceId = "";
    private String sourceId = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getForceStatus() {
        return forceStatus;
    }

    public void setForceStatus(String forceStatus) {
        this.forceStatus = forceStatus;
    }

    public String getForceId() {
        return forceId;
    }

    public void setForceId(String forceId) {
        this.forceId = forceId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
}
