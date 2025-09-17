package com.paradisecloud.fcm.common.enumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum  McuType {
    FME("fme", "FME", "FME"),
    SMC3("smc3", "SMC3", "华为3.0"),
    SMC2("smc2", "SMC2", "华为2.0"),
    MCU_TENCENT("mcu-tencent", "MCU-TENCENT", "腾讯"),
    MCU_HWCLOUD("mcu-hwcloud", "MCU-HWCLOUD", "华为云"),
    MCU_DING("mcu-ding", "MCU-DING", "钉钉"),
    MCU_ZJ("mcu-zj", "MCU-ZJ", "紫荆"),
    MCU_PLC("mcu-plc", "MCU-PLC", "宝利通"),
    MCU_KDC("mcu-kdc", "MCU-KDC", "科达"),
    MCU_ZTE("mcu-zte", "MCU-ZTE", "中兴")
    ;

    /**
     * 代码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 别名
     */
    private String alias;


    private static final Map<String, McuType> MAP = new HashMap<>();
    static {
        for (McuType mcuType : values()) {
            MAP.put(mcuType.getCode(), mcuType);
        }
    }

    McuType(String code, String name, String alias) {
        this.code = code;
        this.name = name;
        this.alias = alias;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    public static McuType convert(String code) {
        if (code == null) {
            return FME;
        }
        McuType mcuType = MAP.get(code);
        if (mcuType == null) {
            mcuType = FME;
        }
        return mcuType;
    }

    public static List<Map<String, String>> getMcuTypeList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (McuType mcuType : McuType.values()) {
            Map<String, String> map = new HashMap<>();
            map.put("name", mcuType.getName());
            map.put("code", mcuType.getCode());
            map.put("alias", mcuType.getAlias());
            list.add(map);
        }
        return list;
    }
}
