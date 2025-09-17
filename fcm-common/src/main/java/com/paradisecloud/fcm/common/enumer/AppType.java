package com.paradisecloud.fcm.common.enumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>软件类型</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-01-19 17:58
 */
public enum AppType {
    // 100-199 机顶盒
    SET_TOP_BOX("100", "机顶盒", new String[]{"topBox"}),
    // 200-299 手机端
    MOBILE_ANDROID("200", "手机（安卓）", new String[]{"mobile"}),
    // 300-499 维海德专业端
    VHD_C2("301", "维海德C2", new String[]{"VHD C2", "VHD C2H"}),
    VHD_C9("302", "维海德C9", new String[]{"VHD C9", "VHD C9 Pro", "VHD C9H", "VHD C9S", "C9S(1U)"}, true),
    VHD_T632("303", "维海德T632", new String[]{"VHD T632"}, true),
    VHD_T635("304", "维海德T635", new String[]{"VHD T635"}, true),
    VHD_C3("305", "维海德C3", new String[]{"VHD C3"}, true),
    VHD_CX200("306", "维海德CX200", new String[]{"VHD CX200"}, true),
    VHD_CX310("307", "维海德CX310", new String[]{"VHD CX310"}, true),
    // 500-599 智慧屏
    SMART("500", "智慧屏", new String[]{"smart"}),
    // 600-699 电子门牌
    DOORPLATE("600", "电子门牌", new String[]{"doorplate"}),
    // 700-799 安卓桌面
    TTY_LAUNCHER("700", "桌面", new String[]{"ttyLauncher"}, true),
    // 800-899 OPS
    OPS("800", "OPS", new String[]{"ops"}),
    ;

    public static final int UPDATE_TYPE_APP = 0;
    public static final int UPDATE_TYPE_OTA = 1;

    /**
     * 类型代码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型代码
     */
    private String[] types;

    /**
     * 名称
     */
    private boolean supportSecondaryStream;

    /**
     * 升级类型 0:APP升级 1:OTA升级
     */
    private int updateType;

    private static final Map<String, AppType> MAP = new HashMap<>();
    private static final Map<String, AppType> TYPE_MAP = new HashMap<>();
    static
    {
        for (AppType appType : values())
        {
            MAP.put(appType.getCode(), appType);
            String[] types = appType.getTypes();
            for (String type : types) {
                TYPE_MAP.put(type, appType);
            }
        }
    }

    AppType(String code, String name, String[] types) {
        this.code = code;
        this.name = name;
        this.types = types;
        this.supportSecondaryStream = false;
        this.updateType = 0;
    }

    AppType(String code, String name, String[] types, boolean supportSecondaryStream) {
        this.code = code;
        this.name = name;
        this.types = types;
        this.supportSecondaryStream = supportSecondaryStream;
        this.updateType = 0;
    }

    AppType(String code, String name, String[] types, int updateType) {
        this.code = code;
        this.name = name;
        this.types = types;
        this.supportSecondaryStream = false;
        this.updateType = updateType;
    }

    AppType(String code, String name, String[] types, boolean supportSecondaryStream, int updateType) {
        this.code = code;
        this.name = name;
        this.types = types;
        this.supportSecondaryStream = supportSecondaryStream;
        this.updateType = updateType;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String[] getTypes() {
        return types;
    }

    public boolean isSupportSecondaryStream() {
        return supportSecondaryStream;
    }

    public int getUpdateType() {
        return updateType;
    }

    public static AppType convert(String code) {
        if (code != null) {
            return MAP.get(code);
        }
        return null;
    }

    public static AppType convertByType(String type) {
        if (type != null) {
            return TYPE_MAP.get(type);
        }
        return null;
    }

    public boolean isAppUpdate() {
        return updateType == UPDATE_TYPE_APP;
    }

    public boolean isOtaUpdate() {
        return updateType == UPDATE_TYPE_OTA;
    }

    public static List<AppType> getMqttTypeList() {
        List<AppType> appTypeList = new ArrayList<>();
        appTypeList.add(AppType.SET_TOP_BOX);
        appTypeList.add(AppType.DOORPLATE);
        appTypeList.add(AppType.TTY_LAUNCHER);
        appTypeList.add(AppType.SMART);
        return appTypeList;
    }
}