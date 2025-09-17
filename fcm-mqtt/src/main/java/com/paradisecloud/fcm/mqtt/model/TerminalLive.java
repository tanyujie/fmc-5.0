package com.paradisecloud.fcm.mqtt.model;

import com.paradisecloud.fcm.dao.model.BusiTerminal;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.xml.crypto.Data;

public class TerminalLive {
    private Long id;

    @Override
    public String toString() {
        return "TerminalLive{" +
                "id=" + id +
                ", mac='" + mac + '\'' +
                ", type=" + type +
                ", credential='" + credential + '\'' +
                ", intranetIp='" + intranetIp + '\'' +
                ", name='" + name + '\'' +
                ", onlineStatus=" + onlineStatus +
                ", mqttOnlineStatus=" + mqttOnlineStatus +
                ", joinTime='" + joinTime + '\'' +
                ", outTime='" + outTime + '\'' +
                ", status=" + status +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private String mac;

    private Integer type;

    private String credential;

    private String intranetIp;

    private String name;

    private Integer onlineStatus;

    private Integer mqttOnlineStatus;

    //终端加入直播时间
    private String joinTime;

    //终端退出直播时间
    private String outTime;

    //终端在直播中的状态
    private int status;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

    public String getIntranetIp() {
        return intranetIp;
    }

    public void setIntranetIp(String intranetIp) {
        this.intranetIp = intranetIp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(Integer onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public Integer getMqttOnlineStatus() {
        return mqttOnlineStatus;
    }

    public void setMqttOnlineStatus(Integer mqttOnlineStatus) {
        this.mqttOnlineStatus = mqttOnlineStatus;
    }

    public String getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(String joinTime) {
        this.joinTime = joinTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
