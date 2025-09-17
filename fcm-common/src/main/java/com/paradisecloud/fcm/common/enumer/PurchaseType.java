package com.paradisecloud.fcm.common.enumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum PurchaseType {
    MEETING("meeting", "会议"),
    LIVE("live", "直播"),
    IM("im", "IM"),
    SUBTITLES("subtitles", "字幕"),
    TRANSLATION("translation", "翻译")
    ;

    /**
     * 代码
     */
    private String code;

    /**
     * 名称
     */
    private String name;


    private static final Map<String, PurchaseType> MAP = new HashMap<>();
    static {
        for (PurchaseType deviceType : values()) {
            MAP.put(deviceType.getCode(), deviceType);
        }
    }

    PurchaseType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static PurchaseType convert(String code) {
        return MAP.get(code);
    }

    public static List<Map<String, Object>> getPurchaseTypeList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (PurchaseType purchaseType : PurchaseType.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", purchaseType.getName());
            map.put("code", purchaseType.getCode());
            list.add(map);
        }
        return list;
    }

    public static List<Map<String, Object>> getPurchaseTypeListForClient() {
        List<Map<String, Object>> list = new ArrayList<>();
        {
            PurchaseType purchaseType = PurchaseType.SUBTITLES;
            Map<String, Object> map = new HashMap<>();
            map.put("name", purchaseType.getName());
            map.put("code", purchaseType.getCode());
            list.add(map);
        }
        {
            PurchaseType purchaseType = PurchaseType.TRANSLATION;
            Map<String, Object> map = new HashMap<>();
            map.put("name", purchaseType.getName());
            map.put("code", purchaseType.getCode());
            list.add(map);
        }
        return list;
    }
}
