package com.paradisecloud.fcm.smc2.model.layout;

/**
 * @author nj
 * @date 2023/5/29 9:19
 */
public enum ContinuousPresenceModeEnum {

    CP_1_1(1),
    CP_2_1(2),
    CP_2_2(3),
    CP_2_3(4),
    CP_3_1(5),
    CP_3_2(6),
    CP_3_3(7),
    CP_3_4(8),
    CP_3_5(9),
    CP_3_6(10),
    CP_4_1(11),
    CP_4_2(12),
    CP_4_3(13),
    CP_4_4(14),
    CP_4_5(15),
    CP_4_6(16),
    CP_5_1(17),
    CP_5_2(18),
    CP_5_3(19),
    CP_5_4(20),
    CP_6_1(21),
    CP_6_2(22),
    CP_6_3(23),
    CP_6_4(24),
    CP_6_5(25),
    CP_7_1(26),
    CP_7_2(27),
    CP_7_3(28),
    CP_7_4(29),
    CP_7_5(30),
    CP_8_1(31),
    CP_8_2(32),
    CP_8_3(33),
    CP_8_4(34),
    CP_9_1(35),
    CP_10_1(36),
    CP_10_2(37),
    CP_10_3(38),
    CP_10_4(39),
    CP_10_5(40),
    CP_10_6(41),
    CP_13_1(42),
    CP_13_2(43),
    CP_13_3(44),
    CP_13_4(45),
    CP_13_5(46),
    CP_16_1(47),
    CP_20_1(48),
    CP_24_1(49),
    CP_2_6(50),
    CP_3_8(51),
    CP_4_7(52),
    CP_5_6(53),
    CP_6_7(54),
    CP_7_6(55);
    private int value;

    ContinuousPresenceModeEnum(int value) {
        this.value = value;
    }


    public int getValue() {
        return value;
    }

    public  static int getModelValue(int firstIntValue,int secondIntValue){
        ContinuousPresenceModeEnum continuousPresenceModeEnum = null;
        try {
            continuousPresenceModeEnum = ContinuousPresenceModeEnum.valueOf("CP_" + firstIntValue + "_" + secondIntValue);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        if(continuousPresenceModeEnum!=null){
            return continuousPresenceModeEnum.value;
        }
        return -1;
    }
}
