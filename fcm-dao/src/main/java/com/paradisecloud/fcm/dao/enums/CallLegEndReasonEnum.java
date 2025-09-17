package com.paradisecloud.fcm.dao.enums;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @Description
 * @Author johnson liu
 * @Date 2021/6/6 11:05
 **/
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CallLegEndReasonEnum implements BaseEnum {
    API_INITIATED_TEARDOWN(1, "apiInitiatedTeardown", "会控挂断"),

    CALL_DEACTIVATED(2, "callDeactivated", "呼叫不活跃"),
    CALL_ENDED(3, "callEnded", "呼叫正常挂断"),
    CALL_MOVED(4, "callMoved", "呼叫被移动"),
    CLIENT_INITIATED_TEARDOWN(5, "clientInitiatedTeardown", "客户端初始化"),
    CONFIRMATION_TIMEOUT(6, "confirmationTimeOut", "确认超时"),
    DNS_FAILURE(7, "dnsFailure", "域名解析错误"),
    ENCRYPTION_REQUIRED(8, "encryptionRequired", "加密不匹配"),
    ERROR(9, "error", "远端故障"),
    INCORRECT_PASSCODE(10, "incorrectPasscode", "密码输入错误"),
    IVR_TIMEOUT(11, "ivrTimeout", "语音应答超时"),
    IVR_UNKNOWN_CALL(12, "ivrUnknownCall", "呼叫转移号码错误"),
    LOCAL_TEARDOWN(13, "localTeardown", "本端正常挂断"),
    PARTICIPANT_LIMIT_REACHED(14, "participantLimitReached", "与会者容量限制"),
    REMOTE_BUSY(15, "remoteBusy", "远端忙"),
    REMOTE_REJECTED(16, "remoteRejected", "远端拒绝"),
    REMOTE_TEARDOWN(17, "remoteTeardown", "远端正常挂断"),
    RINGING_TIMEOUT(18, "ringingTimeout", "振铃超时"),
    TENANT_PARTICIPANT_LIMIT_REACHED(19, "tenantParticipantLimitReached", "租户与会者容量限制"),
    TIMEOUT(20, "timeout", "信令超时"),

    API_INITIATED_TEARDOWN_SMC3(196619, "apiInitiatedTeardown", "会控挂断"),
    LOCAL_TEARDOWN_SMC3(196622, "localTeardown", "本端正常挂断"),
    UNKNOWN_DESTINATION(21, "unknownDestination", "呼叫地址无效");

    private int code;
    private String name;
    private String description;

    private static final Map<String, CallLegEndReasonEnum> MAP = new HashMap<>();
    static
    {
        for (CallLegEndReasonEnum callLegEndReasonEnum : values())
        {
            MAP.put(callLegEndReasonEnum.name, callLegEndReasonEnum);
        }
    }

    CallLegEndReasonEnum(int code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    @Override
    public Object getEnumCode() {
        return code;
    }

    @Override
    public String getDisplayName() {
        return description;
    }

    /**
     * 根据code获取枚举名称
     *
     * @param code 或者name 枚举值
     * @return 对象枚举对象
     */
    public static String getValue(Integer code) {
        for (CallLegEndReasonEnum reasonEnum : values()) {
            if (Integer.valueOf(reasonEnum.getCode()).equals(code)) {
                return reasonEnum.getName();
            }
        }
        return null;
    }

    /**
     * 根据name值获获取对应的枚举对象
     *
     * @param name
     * @return
     */
    public static CallLegEndReasonEnum getEnumObjectByName(String name) {
        return MAP.get(name);
    }

    /**
     * 根据code获取对应的枚举对象
     *
     * @param code
     * @return
     */
    public static CallLegEndReasonEnum getEnumObjectByCode(int code) {
        for (CallLegEndReasonEnum reasonEnum : values()) {
            if (reasonEnum.getCode() == code) {
                return reasonEnum;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    private String getDescription() {
        return description;
    }
}
