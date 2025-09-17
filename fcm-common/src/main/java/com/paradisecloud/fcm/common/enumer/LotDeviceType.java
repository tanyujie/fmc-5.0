package com.paradisecloud.fcm.common.enumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum LotDeviceType {
    POWER_SEQUENCER(1, "时序电源"),
    ;

    /**
     * 代码
     */
    private int code;

    /**
     * 名称
     */
    private String name;


    private static final Map<Integer, LotDeviceType> MAP = new HashMap<>();
    static {
        for (LotDeviceType lotDeviceType : values()) {
            MAP.put(lotDeviceType.getCode(), lotDeviceType);
        }
    }

    LotDeviceType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static LotDeviceType convert(int code) {
        return MAP.get(code);
    }

    public static List<Map<String, Object>> getLotDeviceTypeList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (LotDeviceType lotDeviceType : LotDeviceType.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", lotDeviceType.getName());
            map.put("code", lotDeviceType.getCode());
            list.add(map);
        }
        return list;
    }
}
