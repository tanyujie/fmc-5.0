package com.paradisecloud.fcm.service.conference.service;

import com.paradisecloud.fcm.common.enumer.McuType;

/**
 * @author nj
 * @date 2023/6/12 14:55
 */
public enum McuStrategyEnum {

    FME(McuType.FME.getCode(), new String[]{"com.paradisecloud.fcm.fme.conference.cascade.FmeCascadeConference"}),
    MCU_ZJ(McuType.MCU_ZJ.getCode(), new String[]{"com.paradisecloud.fcm.mcu.zj.cascade.McuZjCascadeConference"}),
    MCU_PLC(McuType.MCU_PLC.getCode(), new String[]{"com.paradisecloud.fcm.mcu.plc.cascade.McuPlcCascadeConference"}),
    MCU_KDC(McuType.MCU_KDC.getCode(), new String[]{"com.paradisecloud.fcm.mcu.kdc.cascade.McuKdcCascadeConference"}),
    SMC3(McuType.SMC3.getCode(), new String[]{"com.paradisecloud.smc3.busi.cascade.Smc3CascadeConference"}),
    SMC2(McuType.SMC2.getCode(), new String[]{"com.paradisecloud.fcm.smc2.cascade.Smc2CascadeConference"}),
    MCU_ZTE(McuType.MCU_ZTE.getCode(), new String[]{"com.paradisecloud.fcm.zte.cascade.McuZteCascadeConference"}),
    TENCENT(McuType.MCU_TENCENT.getCode(), new String[]{"com.paradisecloud.fcm.tencent.cascade.TencentCascadeConference"});

    private String type;
    private String[] values;

    private McuStrategyEnum(String type, String[] values) {
        this.type = type;
        this.values = values;
    }

    public String[] getValue() {
        return values;
    }

    public String getType() {
        return type;
    }

    public static McuStrategyEnum convert(String type) {
        for (McuStrategyEnum mcuStrategyEnum : values()) {
            if (mcuStrategyEnum.getType().equals(type)) {
                return mcuStrategyEnum;
            }
        }
        return null;
    }

}
