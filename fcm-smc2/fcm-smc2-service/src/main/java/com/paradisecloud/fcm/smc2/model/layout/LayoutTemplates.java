package com.paradisecloud.fcm.smc2.model.layout;

import java.util.*;

public enum LayoutTemplates {
    AUTO("auto", "自动", "auto",0),
    SCREEN_1("1", "一分屏", "speakerOnly",1),
    SCREEN_4("2", "四分屏", "allEqualQuarters",4),
    SCREEN_9("3", "九分屏", "allEqualNinths",9),
    SCREEN_16("4", "十六分屏", "allEqualSixteenths",16),
    SCREEN_6("5", "一大五小", "onePlusFive",6),
    SCREEN_8("6", "一大七小", "onePlusSeven",8),
    SCREEN_12("9", "十二屏", "allEqualTwelve",12),
    SCREEN_2("16", "二分屏", "two",2),
    ;

    private String code;
    private String name;
    private String layout;
    private int num;

    private LayoutTemplates(String code, String name, String layout, int num) {
        this.code = code;
        this.name = name;
        this.layout = layout;
        this.num = num;
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

    public String getLayout() {
        return layout;
    }



    public static List<Map<String, String>> getLayoutTemplateList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (LayoutTemplates layoutTemplates : LayoutTemplates.values()) {
            Map<String, String> map = new HashMap<>();
            map.put("name", layoutTemplates.getName());
            map.put("value", layoutTemplates.getCode());
            list.add(map);
        }
        return list;
    }

    public static LayoutTemplates convert(SplitScreen splitScreen) {
        return LayoutTemplates.AUTO;
    }



    public static LayoutTemplates convert(String layout) {
        List<Map<String, String>> list = new ArrayList<>();
        for (LayoutTemplates layoutTemplate : LayoutTemplates.values()) {
            if(Objects.equals(layoutTemplate.getLayout(),layout)){
                return layoutTemplate;
            }
        }
        return LayoutTemplates.SCREEN_1;
    }
}
