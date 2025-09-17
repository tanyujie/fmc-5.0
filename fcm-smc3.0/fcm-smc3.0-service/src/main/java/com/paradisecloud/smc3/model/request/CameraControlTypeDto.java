package com.paradisecloud.smc3.model.request;

import lombok.Getter;

/**
 * @author nj
 * @date 2023/3/10 9:33
 */
@Getter
public enum CameraControlTypeDto {

    UO(11, "上"),
    DOWN(12, "下"),
    LEFT(13, "左"),
    RIGHT(14, "右"),
    MAGNIFY(15, "放大"),
    REDUCE(16, "缩小"),
    CLOSE_FOCUS(17, "向近聚焦"),
    FAR_FOCUS(18, "向远聚焦");

    private final int code;
    private final String desc;

    CameraControlTypeDto(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CameraControlTypeDto getByCode(int code) {
        for (CameraControlTypeDto item : values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }
}
