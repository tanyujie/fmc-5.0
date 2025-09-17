package com.paradisecloud.fcm.mcu.kdc.model.enumer;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.mcu.kdc.model.busi.layout.SplitScreen;
import com.paradisecloud.fcm.mcu.kdc.model.busi.layout.splitscreen.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum McuKdcLayoutTemplates {
    AUTO("0", "自动", 0),
    SCREEN_1("1", "一分屏", 1),
    SCREEN_2("2", "二分屏", 2, true),
    SCREEN_2_1("3", "一大一小右下分屏", 2, true),
    SCREEN_4("5", "四分屏", 4),
    SCREEN_6("6", "一大五小", 6),
    SCREEN_8("7", "一大七小", 8),
    SCREEN_9("8", "九分屏", 9),
    SCREEN_16("11", "十六分屏", 16),
    SCREEN_25("27", "二十五分屏", 25),
    ;

    private String code;
    private String name;
    private int num;
    private boolean privateLayout;

    private McuKdcLayoutTemplates(String code, String name, int num) {
        this.code = code;
        this.name = name;
        this.num = num;
    }

    private McuKdcLayoutTemplates(String code, String name, int num, boolean privateLayout) {
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
        for (McuKdcLayoutTemplates layoutTemplates : McuKdcLayoutTemplates.values()) {
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
        for (McuKdcLayoutTemplates layoutTemplates : McuKdcLayoutTemplates.values()) {
            if (layoutTemplates.isPrivateLayout()) {
                continue;
            }

            ModelBean modelBean = new ModelBean();
            modelBean.put("name", layoutTemplates.getName());
            modelBean.put("value", getScreenLayout(layoutTemplates));
            modelBean.put("isDefault", layoutTemplates == McuKdcLayoutTemplates.AUTO);
            list.add(modelBean);
        }
        return list;
    }

    public static String getScreenLayout(McuKdcLayoutTemplates layoutTemplates) {
        if (layoutTemplates  == McuKdcLayoutTemplates.SCREEN_1) {
            return OneSplitScreen.LAYOUT;
        } else if (layoutTemplates  == McuKdcLayoutTemplates.SCREEN_4) {
            return FourSplitScreen.LAYOUT;
        } else if (layoutTemplates  == McuKdcLayoutTemplates.SCREEN_9) {
            return NineSplitScreen.LAYOUT;
        } else if (layoutTemplates  == McuKdcLayoutTemplates.SCREEN_16) {
            return SixteenSplitScreen.LAYOUT;
        } else if (layoutTemplates  == McuKdcLayoutTemplates.SCREEN_25) {
            return TwentyFiveSplitScreen.LAYOUT;
        } else if (layoutTemplates  == McuKdcLayoutTemplates.SCREEN_6) {
            return OnePlusFiveSplitScreen.LAYOUT;
        } else if (layoutTemplates  == McuKdcLayoutTemplates.SCREEN_8) {
            return OnePlusSevenSplitScreen.LAYOUT;
        } else {
            return AutomaticSplitScreen.LAYOUT;
        }
    }

    public static McuKdcLayoutTemplates convert(SplitScreen splitScreen) {
        if (splitScreen instanceof OneSplitScreen) {
            return McuKdcLayoutTemplates.SCREEN_1;
        } else if (splitScreen instanceof FourSplitScreen) {
            return McuKdcLayoutTemplates.SCREEN_4;
        } else if (splitScreen instanceof NineSplitScreen) {
            return McuKdcLayoutTemplates.SCREEN_9;
        } else if (splitScreen instanceof SixteenSplitScreen) {
            return McuKdcLayoutTemplates.SCREEN_16;
        } else if (splitScreen instanceof TwentyFiveSplitScreen) {
            return McuKdcLayoutTemplates.SCREEN_25;
        } else if (splitScreen instanceof OnePlusFiveSplitScreen) {
            return McuKdcLayoutTemplates.SCREEN_6;
        } else if (splitScreen instanceof OnePlusSevenSplitScreen) {
            return McuKdcLayoutTemplates.SCREEN_8;
        } else {
            return McuKdcLayoutTemplates.AUTO;
        }
    }
}
