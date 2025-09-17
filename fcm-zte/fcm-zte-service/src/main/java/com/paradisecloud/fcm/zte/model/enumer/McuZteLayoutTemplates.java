package com.paradisecloud.fcm.zte.model.enumer;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.zte.model.busi.layout.SplitScreen;
import com.paradisecloud.fcm.zte.model.busi.layout.splitscreen.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum McuZteLayoutTemplates {
    AUTOMATIC("automatic", "自动","automatic", 0),
    AUTO("auto", "自动","auto", 0),
    SCREEN_1_0("0", "一分屏","SCREEN_1_0", 1),
    SCREEN_2_0("0", "二分屏","SCREEN_2_0", 2),
    SCREEN_2_1("1", "二分屏","SCREEN_2_1", 2),
    SCREEN_3_0("0", "三分屏","SCREEN_3_0",3),
    SCREEN_3_1("1", "三分屏","SCREEN_3_1",3),
    SCREEN_3_2("2", "三分屏","SCREEN_3_2",3),
    SCREEN_4_0("0", "四分屏", "SCREEN_4_0",4),
    SCREEN_4_1("1", "四分屏", "SCREEN_4_1",4),
    SCREEN_4_2("2", "四分屏", "SCREEN_4_2",4),
    SCREEN_5_1("1", "五分屏", "SCREEN_5_1",5),
    SCREEN_6_0("0", "一大五小","SCREEN_6_0", 6),
    SCREEN_6_1("1", "一大五小","SCREEN_6_1",6),
    SCREEN_6_2("2", "一大五小","SCREEN_6_2",6),
    SCREEN_6_3("3", "一大五小","SCREEN_6_3",6),
    SCREEN_7_0("10", "七分屏","SCREEN_7_0",7),
    SCREEN_7_1("1", "七分屏","SCREEN_7_1",7),
    SCREEN_7_2("2", "七分屏","SCREEN_7_2",7),
    SCREEN_7_3("3", "七分屏","SCREEN_7_3",7),

    SCREEN_8_0("0", "八分屏","SCREEN_8_0", 8),
    SCREEN_8_1("1", "八分屏","SCREEN_8_1",8),
    SCREEN_8_2("2", "八分屏","SCREEN_8_2",8),
    SCREEN_8_3("3", "八分屏","SCREEN_8_3",8),

    SCREEN_9_0("0", "九分屏","SCREEN_9_0", 9),
  
    
    SCREEN_10_0("0", "十分屏","SCREEN_10_0", 10),
    SCREEN_10_1("1", "十分屏","SCREEN_10_1", 10),
    SCREEN_10_2("2", "十分屏","SCREEN_10_2",10),
    SCREEN_10_3("3", "十分屏","SCREEN_10_3",10),
    SCREEN_10_4("4", "十分屏","SCREEN_10_4",10),
    SCREEN_10_5("5", "十分屏","SCREEN_10_5",10),

    SCREEN_13_0("0", "十三分屏","SCREEN_13_0", 13),
    SCREEN_13_1("1", "十三分屏","SCREEN_13_1", 13),
    SCREEN_13_2("2", "十三分屏","SCREEN_13_2",13),
    SCREEN_13_3("3", "十三分屏","SCREEN_13_3",13),
    SCREEN_13_4("4", "十三分屏","SCREEN_13_4",13),

    SCREEN_16_0("0", "十六分屏","SCREEN_16_0", 16),
    SCREEN_17_4("4", "十七分屏","SCREEN_17_4", 17),
    SCREEN_25_0("0", "二十五分屏", "SCREEN_25_0",25),
    ;

    private String code;
    private String name;
    private String desc;
    private int num;

    private McuZteLayoutTemplates(String code, String name, int num) {
        this.code = code;
        this.name = name;
        this.num = num;
    }
    private McuZteLayoutTemplates(String code, String desc,String name, int num) {
        this.code = code;
        this.name = name;
        this.num = num;
        this.desc=desc;
    }


    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getNum() {
        return num;
    }



    public static List<Map<String, String>> getLayoutTemplateList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (McuZteLayoutTemplates layoutTemplates : McuZteLayoutTemplates.values()) {

            Map<String, String> map = new HashMap<>();
            map.put("name", layoutTemplates.getName());
            map.put("value", layoutTemplates.getCode());
            list.add(map);
        }
        return list;
    }

    public static List<ModelBean> getLayoutTemplateScreenList() {
        List<ModelBean> list = new ArrayList<>();
        for (McuZteLayoutTemplates layoutTemplates : McuZteLayoutTemplates.values()) {

            ModelBean modelBean = new ModelBean();
            modelBean.put("name", layoutTemplates.getName());
            modelBean.put("value", getScreenLayout(layoutTemplates));
            modelBean.put("isDefault", layoutTemplates == McuZteLayoutTemplates.AUTO);
            list.add(modelBean);
        }
        return list;
    }

    public static String getScreenLayout(McuZteLayoutTemplates layoutTemplates) {
        if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_1_0) {
            return OneSplitScreen.LAYOUT;
        } else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_4_0) {
            return FourSplitScreen.LAYOUT;
        }else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_4_1) {
            return FourSplitScreen.LAYOUT;
        }else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_4_2) {
            return FourSplitScreen.LAYOUT;
        }
        else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_5_1) {
            return FiveSplitScreen.LAYOUT;
        } else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_9_0) {
            return NineSplitScreen.LAYOUT;
        } else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_16_0){
            return SixteenSplitScreen.LAYOUT;
        } else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_6_0) {
            return OnePlusFiveSplitScreen.LAYOUT;
        }
        else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_6_1) {
            return OnePlusFiveSplitScreen.LAYOUT;
        }
        else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_6_2) {
            return OnePlusFiveSplitScreen.LAYOUT;
        }
        else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_6_3) {
            return OnePlusFiveSplitScreen.LAYOUT;
        }else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_7_0) {
            return SevenSplitScreen.LAYOUT;
        }else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_7_1) {
            return SevenSplitScreen.LAYOUT;
        }else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_7_2) {
            return SevenSplitScreen.LAYOUT;
        }else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_7_3) {
            return SevenSplitScreen.LAYOUT;
        }else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_8_0) {
            return EightSplitScreen.LAYOUT;
        }else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_8_1) {
            return EightSplitScreen.LAYOUT;
        }else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_8_2) {
            return EightSplitScreen.LAYOUT;
        }else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_8_3) {
            return EightSplitScreen.LAYOUT;
        }
        else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_10_0) {
            return OnePlusNineSplitScreen.LAYOUT;
        }
        else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_10_1) {
            return OnePlusNineSplitScreen.LAYOUT;
        }else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_10_2) {
            return OnePlusNineSplitScreen.LAYOUT;
        }else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_10_3) {
            return OnePlusNineSplitScreen.LAYOUT;
        }else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_10_4) {
            return OnePlusNineSplitScreen.LAYOUT;
        }else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_10_5) {
            return OnePlusNineSplitScreen.LAYOUT;
        }
        else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_13_0) {
            return ThirteenSplitScreen.LAYOUT;
        }
        else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_13_1) {
            return ThirteenSplitScreen.LAYOUT;
        }else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_13_2) {
            return ThirteenSplitScreen.LAYOUT;
        }else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_13_3) {
            return ThirteenSplitScreen.LAYOUT;
        }else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_13_4) {
            return ThirteenSplitScreen.LAYOUT;
        }
        else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_3_0) {
            return ThreeSplitScreen.LAYOUT;
        }
        else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_3_1) {
            return ThreeSplitScreen.LAYOUT;
        }
        else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_3_2) {
            return ThreeSplitScreen.LAYOUT;
        }
        else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_2_0) {
            return TwoSplitScreen.LAYOUT;
        }
        else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_2_1) {
            return TwoSplitScreen.LAYOUT;
        }
        else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_17_4) {
            return SeventeenSplitScreen.LAYOUT;
        } else if (layoutTemplates  == McuZteLayoutTemplates.SCREEN_25_0) {
            return TwentyFiveSplitScreen.LAYOUT;
        }
        else {
            return AutomaticSplitScreen.LAYOUT;
        }
    }

    public static McuZteLayoutTemplates convert(SplitScreen splitScreen) {
        if (splitScreen instanceof OneSplitScreen) {
            return McuZteLayoutTemplates.SCREEN_1_0;
        } else if (splitScreen instanceof FourSplitScreen) {
            return McuZteLayoutTemplates.SCREEN_4_0;
        } else if (splitScreen instanceof NineSplitScreen) {
            return McuZteLayoutTemplates.SCREEN_9_0;
        } else if (splitScreen instanceof SixteenSplitScreen) {
            return McuZteLayoutTemplates.SCREEN_16_0;
        } else if (splitScreen instanceof OnePlusFiveSplitScreen) {
            return McuZteLayoutTemplates.SCREEN_6_0;
        } else if (splitScreen instanceof TwentyFiveSplitScreen) {
            return McuZteLayoutTemplates.SCREEN_25_0;
        } else if (splitScreen instanceof OnePlusNineSplitScreen) {
            return McuZteLayoutTemplates.SCREEN_10_0;
        } else if (splitScreen instanceof TwoSplitScreen) {
            return McuZteLayoutTemplates.SCREEN_2_0;
        }else if (splitScreen instanceof ThreeSplitScreen) {
            return McuZteLayoutTemplates.SCREEN_3_0;
        }else if (splitScreen instanceof FiveSplitScreen) {
            return McuZteLayoutTemplates.SCREEN_5_1;
        }else if (splitScreen instanceof SevenSplitScreen) {
            return McuZteLayoutTemplates.SCREEN_7_0;
        }else if (splitScreen instanceof EightSplitScreen) {
            return McuZteLayoutTemplates.SCREEN_8_0;
        }else if (splitScreen instanceof ThirteenSplitScreen) {
            return McuZteLayoutTemplates.SCREEN_13_0;
        }else if (splitScreen instanceof SeventeenSplitScreen) {
            return McuZteLayoutTemplates.SCREEN_17_4;
        }

        else {
            return McuZteLayoutTemplates.AUTO;
        }
    }

}
