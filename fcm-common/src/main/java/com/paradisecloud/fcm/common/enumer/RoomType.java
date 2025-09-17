package com.paradisecloud.fcm.common.enumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum RoomType {
    MEETING_ROOM(0, "会议室"),
    ;

    /**
     * 代码
     */
    private int code;

    /**
     * 名称
     */
    private String name;


    private static final Map<Integer, RoomType> MAP = new HashMap<>();
    static {
        for (RoomType roomType : values()) {
            MAP.put(roomType.getCode(), roomType);
        }
    }

    RoomType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static RoomType convert(int code) {
        return MAP.get(code);
    }

    public static List<Map<String, Object>> getRoomTypeList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (RoomType roomType : RoomType.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", roomType.getName());
            map.put("code", roomType.getCode());
            list.add(map);
        }
        return list;
    }
}
