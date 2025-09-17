package com.paradisecloud.fcm.service.conference;

public class McuBridge {

    private Long mcuId;
    private String callIp;
    private Integer callPort;

    public void setMcuId(Long mcuId) {
        this.mcuId = mcuId;
    }

    public Long getMcuId() {
        return mcuId;
    }

    public String getCallIp() {
        return callIp;
    }

    public void setCallIp(String callIp) {
        this.callIp = callIp;
    }

    public Integer getCallPort() {
        return callPort;
    }

    public void setCallPort(Integer callPort) {
        this.callPort = callPort;
    }
}
