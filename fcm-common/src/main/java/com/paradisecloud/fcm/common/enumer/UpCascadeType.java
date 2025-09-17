package com.paradisecloud.fcm.common.enumer;

import java.util.HashMap;
import java.util.Map;

public enum UpCascadeType {
    AUTO_CREATE(0, "自动生成模板"),
    SELECT_TEMPLATE_OUT_MEETING(1, "手动选择未开会模板"),
    SELECT_TEMPLATE_IN_MEETING(2, "手动选择已开会模板")
    ;

    private int code;
    private String name;

    UpCascadeType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    private static final Map<Integer, UpCascadeType> MAP = new HashMap<>();
    static {
        for (UpCascadeType upCascadeType : values()) {
            MAP.put(upCascadeType.getCode(), upCascadeType);
        }
    }

    public static UpCascadeType convert(int code) {
        return MAP.get(code);
    }
}
