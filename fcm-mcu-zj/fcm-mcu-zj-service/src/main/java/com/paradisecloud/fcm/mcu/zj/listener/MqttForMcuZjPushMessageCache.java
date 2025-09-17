package com.paradisecloud.fcm.mcu.zj.listener;


public class MqttForMcuZjPushMessageCache {

    private static MqttForMcuZjPushMessageCache INSTANCE = new MqttForMcuZjPushMessageCache();

    private MqttForMcuZjPushMessageListener mqttForMcuZjPushMessageListener;

    public static MqttForMcuZjPushMessageCache getInstance() {
        return INSTANCE;
    }

    public MqttForMcuZjPushMessageListener getMqttForMcuZjPushMessageListener() {
        return mqttForMcuZjPushMessageListener;
    }

    public void setMqttForMcuZjPushMessageListener(MqttForMcuZjPushMessageListener mqttForMcuZjPushMessageListener) {
        this.mqttForMcuZjPushMessageListener = mqttForMcuZjPushMessageListener;
    }
}
