package com.paradisecloud.fcm.mcu.zj.listener;

import com.alibaba.fastjson.JSONObject;

public interface MqttForMcuZjPushMessageListener {

    void onPushMessage(Integer code, String message, String topic, String action, JSONObject jObj, String clientId, String messageId);

    void onPushMessage(Integer code, String message, String topic, String action, JSONObject jObj, String messageId, int qos);
}
