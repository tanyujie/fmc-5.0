package com.paradisecloud.fcm.common.vo;

import com.paradisecloud.fcm.common.enumer.McuType;

public class EncryptIdVo {

    private Long id;
    private McuType mcuType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public McuType getMcuType() {
        return mcuType;
    }

    public void setMcuType(McuType mcuType) {
        this.mcuType = mcuType;
    }
}
