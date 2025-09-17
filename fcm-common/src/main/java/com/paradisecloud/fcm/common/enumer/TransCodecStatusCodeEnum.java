package com.paradisecloud.fcm.common.enumer;

/**
 * @author nj
 * @date 2024/4/1 10:26
 */
public enum TransCodecStatusCodeEnum {


    UN_TRANS(0, "未转换"),
    TRANS_SUCCESS(1, "转换成功"),
    TRANS_ING(2,"转码中"),
    CALLBACK_FAIL(4, "回调失败");

    private TransCodecStatusCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final int code;
    private final String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
