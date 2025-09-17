package com.paradisecloud.smc3.model.request;

import lombok.Getter;

/**
 * @author nj
 * @date 2023/3/10 10:26
 */
@Getter
public enum CameraSerialNumDto {

    NO1(1, "第一个摄像机"),
    NO2(2, "第二个摄像机"),
    NO3(3, "第三个摄像机"),
    NO4(4, "第四个摄像机"),
    NO5(5, "第五个摄像机");

    private final int code;
    private final String desc;

    CameraSerialNumDto(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CameraSerialNumDto getByCode(int code) {
        for (CameraSerialNumDto item : values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }
}
