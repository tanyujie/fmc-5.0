package com.paradisecloud.fcm.mcu.plc.model.enumer;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.mcu.plc.model.busi.layout.SplitScreen;
import com.paradisecloud.fcm.mcu.plc.model.busi.layout.splitscreen.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum McuPlcLayoutTemplates {
    AUTO("auto", "自动", 0),
    SCREEN_1("1x1", "一分屏", 1),
    SCREEN_2("1x2", "二分屏", 2, true),
//    SCREEN_3("pin", "三分屏"),
//    SCREEN_3_2("pin_2", "一大二小右"),
//    SCREEN_3_3("pin_3", "一大二小下"),
    SCREEN_4("2x2", "四分屏", 4),
//    SCREEN_4_2("2*2_2", "一大三小右"),
//    SCREEN_4_3("2*2_3", "一大三小下"),
//    SCREEN_5("5", "五分屏"),
//    SCREEN_5_2("5_2", "一大四小右"),
//    SCREEN_5_3("5_3", "一大四小下"),
    SCREEN_6("1and5", "一大五小", 6),
//    SCREEN_6_2("1+5_2", "一大五小右"),
//    SCREEN_6_3("1+5_3", "一大五小下"),
//    SCREEN_6_4("1+5_4", "二大四小下"),
//    SCREEN_6_5("1+5_5", "六全等"),
    SCREEN_8("1and7", "一大七小", 8),
    SCREEN_9("3x3", "九分屏", 9),
//    SCREEN_10("2+8_2", "一大九小", 10),
//    SCREEN_13("1+12", "一大十二小"),
    SCREEN_16("4x4", "十六分屏", 16),
//    SCREEN_21("21", "一大二十小"),
//    SCREEN_25("5*5", "二十五分屏", 25),
    ;

    private String code;
    private String name;
    private int num;
    private boolean privateLayout;

    private McuPlcLayoutTemplates(String code, String name, int num) {
        this.code = code;
        this.name = name;
        this.num = num;
    }

    private McuPlcLayoutTemplates(String code, String name, int num, boolean privateLayout) {
        this.code = code;
        this.name = name;
        this.num = num;
        this.privateLayout = privateLayout;
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

    public boolean isPrivateLayout() {
        return privateLayout;
    }

    public void setPrivateLayout(boolean privateLayout) {
        this.privateLayout = privateLayout;
    }

    public static List<Map<String, String>> getLayoutTemplateList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (McuPlcLayoutTemplates layoutTemplates : McuPlcLayoutTemplates.values()) {
            if (layoutTemplates.isPrivateLayout()) {
                continue;
            }
            Map<String, String> map = new HashMap<>();
            map.put("name", layoutTemplates.getName());
            map.put("value", layoutTemplates.getCode());
            list.add(map);
        }
        return list;
    }

    public static List<ModelBean> getLayoutTemplateScreenList() {
        List<ModelBean> list = new ArrayList<>();
        for (McuPlcLayoutTemplates layoutTemplates : McuPlcLayoutTemplates.values()) {
            if (layoutTemplates.isPrivateLayout()) {
                continue;
            }

            ModelBean modelBean = new ModelBean();
            modelBean.put("name", layoutTemplates.getName());
            modelBean.put("value", getScreenLayout(layoutTemplates));
            modelBean.put("isDefault", layoutTemplates == McuPlcLayoutTemplates.AUTO);
            list.add(modelBean);
        }
        return list;
    }

    public static String getScreenLayout(McuPlcLayoutTemplates layoutTemplates) {
        if (layoutTemplates  == McuPlcLayoutTemplates.SCREEN_1) {
            return OneSplitScreen.LAYOUT;
        } else if (layoutTemplates  == McuPlcLayoutTemplates.SCREEN_4) {
            return FourSplitScreen.LAYOUT;
        } else if (layoutTemplates  == McuPlcLayoutTemplates.SCREEN_9) {
            return NineSplitScreen.LAYOUT;
        } else if (layoutTemplates  == McuPlcLayoutTemplates.SCREEN_16) {
            return SixteenSplitScreen.LAYOUT;
        } else if (layoutTemplates  == McuPlcLayoutTemplates.SCREEN_6) {
            return OnePlusFiveSplitScreen.LAYOUT;
        } else if (layoutTemplates  == McuPlcLayoutTemplates.SCREEN_8) {
            return OnePlusSevenSplitScreen.LAYOUT;
        } else {
            return AutomaticSplitScreen.LAYOUT;
        }
    }

    public static McuPlcLayoutTemplates convert(SplitScreen splitScreen) {
        if (splitScreen instanceof OneSplitScreen) {
            return McuPlcLayoutTemplates.SCREEN_1;
        } else if (splitScreen instanceof FourSplitScreen) {
            return McuPlcLayoutTemplates.SCREEN_4;
        } else if (splitScreen instanceof NineSplitScreen) {
            return McuPlcLayoutTemplates.SCREEN_9;
        } else if (splitScreen instanceof SixteenSplitScreen) {
            return McuPlcLayoutTemplates.SCREEN_16;
        } else if (splitScreen instanceof OnePlusFiveSplitScreen) {
            return McuPlcLayoutTemplates.SCREEN_6;
        } else if (splitScreen instanceof OnePlusSevenSplitScreen) {
            return McuPlcLayoutTemplates.SCREEN_8;
        } else {
            return McuPlcLayoutTemplates.AUTO;
        }
    }
}
