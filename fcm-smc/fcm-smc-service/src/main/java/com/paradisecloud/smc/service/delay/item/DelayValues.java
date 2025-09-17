package com.paradisecloud.smc.service.delay.item;

import lombok.Getter;

/**
 * @author nj
 */
@Getter
public class DelayValues {
    private String id;

    public enum Type {
       SMC_CONFERENCE_AUTO_END
    }

    private Type type;
    /**
     * 业务编号
     */
    private String businessId;

    private Long expireTime;

    public DelayValues(String id, Type type, String businessId, Long expireTime) {
        this.id = id;
        this.type = type;
        this.businessId = businessId;
        this.expireTime = expireTime;
    }
}
