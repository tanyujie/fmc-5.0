package com.paradisecloud.fcm.common.enumer;

import java.util.HashMap;
import java.util.Map;

public enum Region {
    HENAN("henan", "河南联通"),
    HUNAN_JIDU("hunan_jidu", "湖南缉毒"),
    GZGD("gzgd", "贵州广电"),
    OPS("ops", "OPS"),
    SHUMU("shumu", "术木医疗")
    ;

    /**
     * 代码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    private static final Map<String, Region> MAP = new HashMap<>();
    static {
        for (Region appType : values()) {
            MAP.put(appType.getCode(), appType);
        }
    }

    Region(String code, String name) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Region convert(String code) {
        return MAP.get(code);
    }
}
