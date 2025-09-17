package com.paradisecloud.fcm.mcu.kdc.listener;


public class MqttForMcuKdcPushMessageCache {

    private static MqttForMcuKdcPushMessageCache INSTANCE = new MqttForMcuKdcPushMessageCache();

    private MqttForMcuKdcPushMessageListener mqttForMcuKdcPushMessageListener;

    public static MqttForMcuKdcPushMessageCache getInstance() {
        return INSTANCE;
    }

    public MqttForMcuKdcPushMessageListener getMqttForMcuKdcPushMessageListener() {
        return mqttForMcuKdcPushMessageListener;
    }

    public void setMqttForMcuKdcPushMessageListener(MqttForMcuKdcPushMessageListener mqttForMcuKdcPushMessageListener) {
        this.mqttForMcuKdcPushMessageListener = mqttForMcuKdcPushMessageListener;
    }
}
