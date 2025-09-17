package com.paradisecloud.fcm.common.enumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum RoomLevel {
    ANYONE(0, "任何人"),
    ACCOUNT_LOGIN(1, "账号登陆"),
    FACE_LOGIN(2, "人脸识别")
    ;

    /**
     * 代码
     */
    private int code;

    /**
     * 名称
     */
    private String name;


    private static final Map<Integer, RoomLevel> MAP = new HashMap<>();
    static {
        for (RoomLevel roomLevel : values()) {
            MAP.put(roomLevel.getCode(), roomLevel);
        }
    }

    RoomLevel(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static RoomLevel convert(int code) {
        return MAP.get(code);
    }

    public static List<Map<String, Object>> getRoomLevelList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (RoomLevel roomLevel : RoomLevel.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", roomLevel.getName());
            map.put("code", roomLevel.getCode());
            list.add(map);
        }
        return list;
    }
}
