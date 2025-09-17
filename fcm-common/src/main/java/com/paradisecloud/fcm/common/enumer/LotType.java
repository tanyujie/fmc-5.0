package com.paradisecloud.fcm.common.enumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum LotType {
    SERIAL_DEVICE(1, "串口设备"),
    ;

    /**
     * 代码
     */
    private int code;

    /**
     * 名称
     */
    private String name;


    private static final Map<Integer, LotType> MAP = new HashMap<>();
    static {
        for (LotType lotType : values()) {
            MAP.put(lotType.getCode(), lotType);
        }
    }

    LotType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static LotType convert(int code) {
        return MAP.get(code);
    }

    public static List<Map<String, Object>> getLotTypeList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (LotType lotType : LotType.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", lotType.getName());
            map.put("code", lotType.getCode());
            list.add(map);
        }
        return list;
    }
}
