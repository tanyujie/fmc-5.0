package com.paradisecloud.fcm.zte.listener;


public class MqttForMcuZtePushMessageCache {

    private static MqttForMcuZtePushMessageCache INSTANCE = new MqttForMcuZtePushMessageCache();

    private MqttForMcuZtePushMessageListener mqttForMcuZtePushMessageListener;

    public static MqttForMcuZtePushMessageCache getInstance() {
        return INSTANCE;
    }

    public MqttForMcuZtePushMessageListener getMqttForMcuZtePushMessageListener() {
        return mqttForMcuZtePushMessageListener;
    }

    public void setMqttForMcuZtePushMessageListener(MqttForMcuZtePushMessageListener mqttForMcuZtePushMessageListener) {
        this.mqttForMcuZtePushMessageListener = mqttForMcuZtePushMessageListener;
    }
}
