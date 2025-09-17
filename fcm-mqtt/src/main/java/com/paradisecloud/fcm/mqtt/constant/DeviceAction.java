package com.paradisecloud.fcm.mqtt.constant;

public interface DeviceAction {

    // 注册
    String REGISTER = "register";
    // 会议室信息
    String MEETING_ROOM_INFO = "meetingRoomInfo";
    // 会议室信息
    String DELETE_TERMINAL = "deleteTerminal";
    // 预约会议室
    String CREATE_SMARTROOM_BOOK = "createSmartRoomBook";
    // 展示信息
    String INFO_DISPLAY = "infoDisplay";
    // ASR签名
    String ASR_SIGN = "asrSign";
    // 更新license
    String UPDATE_LICENSE = "updateLicense";
}
