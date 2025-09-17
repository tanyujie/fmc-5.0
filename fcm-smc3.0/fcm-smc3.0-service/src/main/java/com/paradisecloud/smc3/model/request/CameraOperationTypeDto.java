package com.paradisecloud.smc3.model.request;

import lombok.Getter;

/**
 * @author nj
 * @date 2023/3/10 9:19
 */
@Getter
public enum CameraOperationTypeDto {

    START(1, "启动"),
    STOP(3, "停止"),
    CHANGE(5, "切换");

    private final int code;
    private final String desc;

    CameraOperationTypeDto(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CameraOperationTypeDto getByCode(int code) {
        for (CameraOperationTypeDto item : values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }


}
