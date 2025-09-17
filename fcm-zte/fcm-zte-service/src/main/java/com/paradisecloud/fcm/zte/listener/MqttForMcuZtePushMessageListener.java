package com.paradisecloud.fcm.zte.listener;

import com.alibaba.fastjson.JSONObject;

public interface MqttForMcuZtePushMessageListener {

    void onPushMessage(Integer code, String message, String topic, String action, JSONObject jObj, String clientId, String messageId);

    void onPushMessage(Integer code, String message, String topic, String action, JSONObject jObj, String messageId, int qos);
}
