package com.paradisecloud.fcm.common.enumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum OaType {
    QYWX(1, "企业微信"),
    DING(2, "钉钉"),
    ;

    /**
     * 代码
     */
    private int code;

    /**
     * 名称
     */
    private String name;


    private static final Map<Integer, OaType> MAP = new HashMap<>();
    static {
        for (OaType oaType : values()) {
            MAP.put(oaType.getCode(), oaType);
        }
    }

    OaType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static OaType convert(int code) {
        return MAP.get(code);
    }

    public static List<Map<String, Object>> getOaTypeList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OaType oaType : OaType.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", oaType.getName());
            map.put("code", oaType.getCode());
            list.add(map);
        }
        return list;
    }
}
