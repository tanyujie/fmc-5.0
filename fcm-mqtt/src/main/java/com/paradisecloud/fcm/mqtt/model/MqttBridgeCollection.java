package com.paradisecloud.fcm.mqtt.model;

import java.util.ArrayList;
import java.util.List;


public class MqttBridgeCollection {
	
	private volatile List<MqttBridge> mqttBridges = new ArrayList<MqttBridge>();
	
	private MqttBridge mMqttBridge;

	public List<MqttBridge> getMqttBridges() {
		return mqttBridges;
	}

	public void setMqttBridges(List<MqttBridge> mqttBridges) {
		this.mqttBridges = mqttBridges;
	}

	public MqttBridge getmMqttBridge() {
		return mMqttBridge;
	}

	public void setmMqttBridge(MqttBridge mMqttBridge) {
		this.mMqttBridge = mMqttBridge;
	}
	
	 public void addMqttBridge(MqttBridge mqttBridge)
	    {
	        this.mqttBridges.add(mqttBridge);
	    }
	
	
}
