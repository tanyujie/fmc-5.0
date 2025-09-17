package com.paradisecloud.com.fcm.smc.modle;

/**
 * @author nj
 * @date 2023/6/25 11:05
 */
public enum VideoProtocolEnum {

    H261(0),
    H263(1),
    H264_BP(3),
    H264_HP(4),
    H265(8),
    INVALID(255);

    private int code;

    VideoProtocolEnum(int code) {
        this.code = code;
    }

    public static VideoProtocolEnum getValueByCode(int code) {
        VideoProtocolEnum[] values = VideoProtocolEnum.values();
        for (VideoProtocolEnum value : values) {
            if(value.code==code) {
                return  value;
            }
        }
        return null;
    }
}
