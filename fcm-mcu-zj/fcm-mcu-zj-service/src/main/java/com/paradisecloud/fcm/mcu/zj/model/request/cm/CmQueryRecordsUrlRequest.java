package com.paradisecloud.fcm.mcu.zj.model.request.cm;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

public class CmQueryRecordsUrlRequest extends CommonRequest {
    private String server_addr;
    private String uuid;

    public String getServer_addr() {
        return server_addr;
    }

    public void setServer_addr(String server_addr) {
        this.server_addr = server_addr;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
