package com.paradisecloud.fcm.fme.conference.listener;

public class MqttForFmePushMessageCache {

    private static MqttForFmePushMessageCache INSTANCE = new MqttForFmePushMessageCache();

    private MqttForFmePushMessageListener mqttForFmePushMessageListener;

    public static MqttForFmePushMessageCache getInstance() {
        return INSTANCE;
    }

    public MqttForFmePushMessageListener getMqttForFmePushMessageListener() {
        return mqttForFmePushMessageListener;
    }

    public void setMqttForFmePushMessageListener(MqttForFmePushMessageListener mqttForFmePushMessageListener) {
        this.mqttForFmePushMessageListener = mqttForFmePushMessageListener;
    }
}
