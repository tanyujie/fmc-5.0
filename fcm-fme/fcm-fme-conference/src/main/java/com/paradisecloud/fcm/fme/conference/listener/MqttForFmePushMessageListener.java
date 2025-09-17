package com.paradisecloud.fcm.fme.conference.listener;

import com.alibaba.fastjson.JSONObject;

public interface MqttForFmePushMessageListener {

    void onPushMessage(Integer code, String message, String topic, String action, JSONObject jObj, String clientId, String messageId);

    void onPushMessage(Integer code, String message, String topic, String action, JSONObject jObj, String messageId, int qos);
}
