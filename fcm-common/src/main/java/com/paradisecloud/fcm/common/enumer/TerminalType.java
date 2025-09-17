package com.paradisecloud.fcm.common.enumer;

import java.util.HashMap;
import java.util.Map;

import com.github.pagehelper.util.StringUtil;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.sinhy.exception.SystemException;

/**
 * <pre>终端类型</pre>
 * @author lilinhai
 * @since 2021-01-19 17:58
 * @version V1.0
 */
public enum TerminalType
{
    /**
     * IP终端
     */
    GB28181(1650, "GB-28181"),
    /**
     * IP终端
     */
    IP(1640, "IP终端"),
    /**
     * RTSP
     */
    RTSP(1630, "RTSP"),
    /**
     * SMC_NUMBER
     */
    SMC_NUMBER(1620, "SMC-NUMBER"),
    /**
     *思科模板
     */
    MCU_TEMPLATE_CISCO(1610, "MCU_TEMPLATE(思科)"),
    /**
     * FME_TEMPLATE
     */
    FME_TEMPLATE(1600, "FME_TEMPLATE"),
    /**
     * SMC_SIP
     */
    SMC_SIP(1500, "SMC-SIP"),
    /**
     * SMC_IP
     */
    SMC_IP(1501, "SMC-IP"),
    /**
     * SMC2_SIP
     */
    SMC2_SIP(1510, "SMC2-SIP"),
    /**
     * HW_CLOUD
     */
    HW_CLOUD(1520, "HW-CLOUD"),

    /**
     * FCM
     */
    FCM_SIP(1300, "FCM-SIP"),

    /**
     * FSBC服务器上注册的终端
     */
    FSBC_SIP(1100, "FSBC-SIP"),

    /**
     * FSBC服务器上注册的终端
     */
    FSBC_H323(1200, "FSBC-H323"),

    /**
     * MCU-ZJ服务器上注册的终端
     */
    ZJ_SIP(2100, "ZJ-SIP"),

    /**
     * MCU-ZJ服务器上注册的终端
     */
    ZJ_H323(2200, "ZJ-H323"),

    /**
     * FMQ
     */
    FMQ(900, "FMQ"),

    /**
     * Windows
     */
    WINDOWS(890, "Windows"),

    /**
     * Android
     */
    ANDROID(860, "Android（安卓）"),

    /**
     * 科莱
     */
    VHD(310, "VHD（维海德）"),

    /**
     * IOS
     */
    IOS(830, "IOS（苹果）"),

    /**
     * Huawei
     */
    Huawei(690, "Huawei（华为）"),

    /**
     * ZTE
     */
    ZTE(680, "ZTE（中兴）"),

    /**
     * Cisco
     */
    Cisco(670, "Cisco（思科)"),

    /**
     * Poly
     */
    Poly(660, "Poly（宝利通)"),;

    /**
     * 设备数据ID
     */
    private final int id;

    /**
     * 对外显示名称
     */
    private final String displayName;

    private static final Map<Integer, TerminalType> MAP = new HashMap<>();
    static
    {
        for (TerminalType recordType : values())
        {
            MAP.put(recordType.id, recordType);
        }
    }

    TerminalType(int id, String displayName)
    {
        this.id = id;
        this.displayName = displayName;
    }

    public static boolean isSMCSIP(Integer value) {
        TerminalType tt = convert(value);
        return tt == SMC_SIP;
    }
    public static boolean isSMCIP(Integer value) {
        TerminalType tt = convert(value);
        return tt == SMC_IP;
    }
    public static boolean isSMC2SIP(Integer value) {
        TerminalType tt = convert(value);
        return tt == SMC2_SIP;
    }
    public static boolean isHwCloud(Integer value) {
        TerminalType tt = convert(value);
        return tt == HW_CLOUD;
    }

    public static boolean isRtsp(Integer value) {
        TerminalType tt = convert(value);
        return tt == RTSP;
    }

    public static boolean isMcuTemplateCisco(Integer value) {
        TerminalType tt = convert(value);
        return tt == MCU_TEMPLATE_CISCO;
    }

    public static boolean isWindows(Integer type) {
        TerminalType tt = convert(type);
        return tt == WINDOWS;
    }

    public static boolean isGB28181(Integer type) {
        TerminalType tt = convert(type);
        return tt == GB28181;
    }

    public int getId()
    {
        return id;
    }

    public String getDisplayName() {
        return (id != 310) ? displayName : StringUtil.isNotEmpty(ExternalConfigCache.getInstance().getTerminalTypeName_310()) ? ExternalConfigCache.getInstance().getTerminalTypeName_310() : displayName;
    }

    public static boolean isFSBC(int value) {
        TerminalType tt = convert(value);
        return tt == FSBC_SIP || tt == FSBC_H323;
    }

    public static boolean isSMCNUMBER(int value) {
        TerminalType tt = convert(value);
        return tt == SMC_NUMBER;
    }

    public static boolean isFCMSIP(int value) {
        TerminalType tt = convert(value);
        return tt == FCM_SIP;
    }

    public static boolean isFSIP(int value) {
        TerminalType tt = convert(value);
        return tt == FSBC_SIP || tt == FCM_SIP || tt == ZJ_SIP;
    }

    public static boolean isCisco(int value) {
        TerminalType tt = convert(value);
        return tt == Cisco;
    }

    public static boolean isZJ(int value) {
        TerminalType tt = convert(value);
        return tt == ZJ_SIP || tt == ZJ_H323;
    }

    public static boolean isIp(int value) {
        TerminalType tt = convert(value);
        return tt == IP || tt == SMC_IP || tt == WINDOWS || tt == Cisco|| tt == VHD || tt == Huawei|| tt == Poly || tt == ZTE|| tt == ANDROID || tt == IOS|| tt == FMQ||tt == FME_TEMPLATE;
    }

    public static boolean isOnlyIP(int value) {
        TerminalType tt = convert(value);
        return tt == IP;
    }

    public static boolean isZTE(int value) {
        TerminalType tt = convert(value);
        return tt ==ZTE;
    }


    public static boolean isFmeTemplate(int value) {
        TerminalType tt = convert(value);
        return tt == FME_TEMPLATE;
    }

    public static TerminalType convert(int value)
    {
        TerminalType t = MAP.get(value);
        if (t == null)
        {
            throw new SystemException("非法的" + TerminalType.class.getSimpleName() + "枚举值：" + value);
        }
        return t;
    }

    public static TerminalType convert(int value, boolean noError)
    {
        TerminalType t = MAP.get(value);
        return t;
    }
    
}