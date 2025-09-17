package com.paradisecloud.fcm.common.enumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum DeviceType {
    DOORPLATE(0, "电子门牌"),
    LOT(1, "物联网关"),
    // 10及以下
    LOT_DEVICE(11, "物联网设备"),
    TERMINAL(12, "注册终端"),
    TENCENT_ROOMS(13, "腾讯Rooms"),
    // 100及以下

    OTHER_DEVICE(999, "其它设备")
    ;

    /**
     * 代码
     */
    private int code;

    /**
     * 名称
     */
    private String name;


    private static final Map<Integer, DeviceType> MAP = new HashMap<>();
    static {
        for (DeviceType deviceType : values()) {
            MAP.put(deviceType.getCode(), deviceType);
        }
    }

    DeviceType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static DeviceType convert(int code) {
        return MAP.get(code);
    }

    public static List<Map<String, Object>> getDeviceTypeList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DeviceType deviceType : DeviceType.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", deviceType.getName());
            map.put("code", deviceType.getCode());
            list.add(map);
        }
        return list;
    }

    public static List<Map<String, Object>> getDeviceTypeListParty() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DeviceType deviceType : DeviceType.values()) {
            if (deviceType.getCode() > 100) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", deviceType.getName());
                map.put("code", deviceType.getCode());
                list.add(map);
            }
        }
        return list;
    }

    public static List<Map<String, Object>> getDeviceTypeListBind() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DeviceType deviceType : DeviceType.values()) {
            if (deviceType.getCode() > 10) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", deviceType.getName());
                map.put("code", deviceType.getCode());
                list.add(map);
            }
        }
        return list;
    }

    public static Boolean isBindId(Integer code) {
        if (code == TERMINAL.getCode() || code == TENCENT_ROOMS.getCode()) {
            return true;
        }
        return false;
    }
}
