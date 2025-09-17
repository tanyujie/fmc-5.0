package com.paradisecloud.fcm.huaweicloud.huaweicloud.model.message;

/**
 * @author nj
 * @date 2024/2/29 16:00
 */
public enum ErrorCode {

    MCU_NO_STREAM_DISCONNECT(11111001, "MCU 无码流挂断（网络原因）"),
    VENUE_NO_STREAM_DISCONNECT(11111002, "会场无码流挂断（网络原因或者客户端异常）"),
    SIP_SIGNAL_TIMEOUT(11111003, "SIP 信令超时（网络原因）"),
    WEBSOCKET_TIMEOUT(11111004, "Websocket 超时（网络原因）"),
    VENUE_DISCONNECT_BY_HOST(11076002, "会场被主持人从软终端上挂断"),
    VENUE_DISCONNECT(11076010, "会场挂断"),
    VENUE_DISCONNECT_BY_PORTAL(11076016, "会场被 Portal 挂断");

    private final int code;
    private final String description;

    ErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
