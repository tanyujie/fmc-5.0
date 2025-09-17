package com.paradisecloud.fcm.dao.model.vo;

import java.util.Date;

public class TerminalAppVersion {

    private long type;
    private String versionCode;
    private String versionName;
    private Date createTime;

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "TerminalAppVersion{" +
                "type=" + type +
                ", versionCode='" + versionCode + '\'' +
                ", versionName='" + versionName + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
