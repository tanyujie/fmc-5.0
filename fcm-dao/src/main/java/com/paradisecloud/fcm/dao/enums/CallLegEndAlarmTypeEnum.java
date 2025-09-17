package com.paradisecloud.fcm.dao.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author johnson liu
 * @date 2021/6/9 17:56
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CallLegEndAlarmTypeEnum implements BaseEnum{
    PACKET_LOSS(1, "packetLoss", "丢包告警"),
    EXCESSIVE_JITTER(2, "excessiveJitter", "网络抖动告警"),
    HIGH_ROUND_TRIP_TIME(3, "highRoundTripTime", "服务器与远程参会者连接时间超长告警"),
    ;

    private int code;
    private String name;
    private String description;


    CallLegEndAlarmTypeEnum(int code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    @Override
    public Object getEnumCode() {
        return code;
    }

    @Override
    public String getDisplayName() {
        return description;
    }

    /**
     * 根据code获取枚举名称
     *
     * @param code 或者name 枚举值
     * @return 对象枚举对象
     */
    public static String getValue(Integer code) {
        for (CallLegEndAlarmTypeEnum typeEnum : values()) {
            if (Integer.valueOf(typeEnum.getCode()).equals(code)) {
                return typeEnum.getName();
            }
        }
        return null;
    }

    /**
     * 根据name值获获取对应的枚举对象
     *
     * @param name
     * @return
     */
    public static CallLegEndAlarmTypeEnum getEnumObjectByName(String name) {
        for (CallLegEndAlarmTypeEnum typeEnum : values()) {
            if (typeEnum.getName().equals(name)) {
                return typeEnum;
            }
        }
        return null;
    }

    /**
     * 根据code获取对应的枚举对象
     *
     * @param code
     * @return
     */
    public static CallLegEndAlarmTypeEnum getEnumObjectByCode(int code) {
        for (CallLegEndAlarmTypeEnum typeEnum : values()) {
            if (typeEnum.getCode() == code) {
                return typeEnum;
            }
        }
        return null;
    }

    private String getName() {
        return name;
    }

    private String getDescription() {
        return description;
    }
}
