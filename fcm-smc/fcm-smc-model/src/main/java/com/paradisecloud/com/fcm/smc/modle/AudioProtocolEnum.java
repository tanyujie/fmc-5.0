package com.paradisecloud.com.fcm.smc.modle;

/**
 * @author nj
 * @date 2023/6/25 11:43
 */
public enum AudioProtocolEnum {
    G711U(0),
    G711A(1),
    G722(2),
    G723_1(3),
    G728(4),
    G729(5),
    AAC_LD_S(6),
    AAC_LC_S(7),
    AAC_LD_D(8),
    AAC_LC_D(9),
    AAC_LD_T(10),
    ILBC(11),
    HW_WB(12),
    AMR(13),
    G722_1_C(14),
    LDX(15),
    HWLD_S(16),
    HWLD_D(17),
    G719(18),
    OPUS(19),
    INVALID(255);

    private int code;

    AudioProtocolEnum(int code) {
        this.code = code;
    }

    public static AudioProtocolEnum getValueByCode(int code) {
        AudioProtocolEnum[] values = AudioProtocolEnum.values();
        for (AudioProtocolEnum value : values) {
            if(value.code==code) {
                return  value;
            }
        }
        return null;
    }
}
