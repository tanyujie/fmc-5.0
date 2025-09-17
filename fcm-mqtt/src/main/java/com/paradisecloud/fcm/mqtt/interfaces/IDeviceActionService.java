package com.paradisecloud.fcm.mqtt.interfaces;

import com.alibaba.fastjson.JSONObject;

public interface IDeviceActionService {
    void register(JSONObject jsonS, String clientId);

    void meetingRoomInfo(JSONObject jsonS, String clientId);

    void createSmartRoomBook(JSONObject jsonS, String clientId);

    void pushInfoDisplay(JSONObject jsonS, String clientId);
}
