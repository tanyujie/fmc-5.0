package com.paradisecloud.fcm.terminal.fs.model;

import com.paradisecloud.fcm.dao.model.BusiFreeSwitch;

/**
 * 服务器资源信息对象 busiFreeSwitch
 * Fcm服务器时间 fcmDate
 */
public class FcmServerConfig {
    private BusiFreeSwitch busiFreeSwitch;
    private String fcmDate;

    @Override
    public String toString() {
        return "FcmServerConfig{" +
                "busiFreeswitch=" + busiFreeSwitch +
                ", fcmDate='" + fcmDate + '\'' +
                '}';
    }

    public BusiFreeSwitch getBusiFreeSwitch() {
        return busiFreeSwitch;
    }

    public void setBusiFreeswitch(BusiFreeSwitch busiFreeswitch) {
        this.busiFreeSwitch = busiFreeswitch;
    }

    public String getFcmDate() {
        return fcmDate;
    }

    public void setFcmDate(String fcmDate) {
        this.fcmDate = fcmDate;
    }
}
