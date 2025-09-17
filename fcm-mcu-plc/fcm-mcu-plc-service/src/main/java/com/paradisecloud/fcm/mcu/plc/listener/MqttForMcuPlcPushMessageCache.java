package com.paradisecloud.fcm.mcu.plc.listener;


public class MqttForMcuPlcPushMessageCache {

    private static MqttForMcuPlcPushMessageCache INSTANCE = new MqttForMcuPlcPushMessageCache();

    private MqttForMcuPlcPushMessageListener mqttForMcuPlcPushMessageListener;

    public static MqttForMcuPlcPushMessageCache getInstance() {
        return INSTANCE;
    }

    public MqttForMcuPlcPushMessageListener getMqttForMcuPlcPushMessageListener() {
        return mqttForMcuPlcPushMessageListener;
    }

    public void setMqttForMcuPlcPushMessageListener(MqttForMcuPlcPushMessageListener mqttForMcuPlcPushMessageListener) {
        this.mqttForMcuPlcPushMessageListener = mqttForMcuPlcPushMessageListener;
    }
}
