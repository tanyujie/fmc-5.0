package com.paradisecloud.fcm.mqtt.model;

import com.paradisecloud.fcm.dao.model.BusiMqtt;
import com.paradisecloud.fcm.mqtt.enums.MqttBridgeStatus;

import java.util.Map;

public class MqttBridge {
	
	private volatile BusiMqtt busiMqtt;
	
	private volatile MqttBridgeStatus mqttBridgeStatus;

	private volatile Map<String, Object> brokerHealthMap;

	public MqttBridge() 
	{
		
	}

	public MqttBridge(BusiMqtt busiMqtt) {
		super();
		this.busiMqtt = busiMqtt;
	}

	public BusiMqtt getBusiMqtt() 
	{
		return busiMqtt;
	}

	public void setBusiMqtt(BusiMqtt busiMqtt) 
	{
		this.busiMqtt = busiMqtt;
	}
	
	public boolean isAvailable()
    {
        return mqttBridgeStatus == MqttBridgeStatus.AVAILABLE;
    }

	public MqttBridgeStatus getMqttBridgeStatus() {
		return mqttBridgeStatus;
	}

	public void setMqttBridgeStatus(MqttBridgeStatus mqttBridgeStatus) {
		this.mqttBridgeStatus = mqttBridgeStatus;
	}

	public Map<String, Object> getBrokerHealthMap() {
		return brokerHealthMap;
	}

	public void setBrokerHealthMap(Map<String, Object> brokerHealthMap) {
		this.brokerHealthMap = brokerHealthMap;
	}
}
