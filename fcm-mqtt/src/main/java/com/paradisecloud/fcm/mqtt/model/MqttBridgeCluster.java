package com.paradisecloud.fcm.mqtt.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiMqttCluster;
import com.paradisecloud.fcm.mqtt.enums.MqttType;

public class MqttBridgeCluster {
	
	private volatile List<MqttBridge> mqttBridges = new ArrayList<MqttBridge>();
	private BusiMqttCluster busiMqttCluster;
	private MqttType mqttType;
	private Long spareMqttId;
	
	
	public synchronized void addMqttBridge(MqttBridge mqttBridge) {
		if(!mqttBridges.contains(mqttBridge)) 
		{
			mqttBridges.add(mqttBridge);
		}
	}
	
	public MqttBridgeCluster() {
		
	}

	public MqttBridgeCluster(BusiMqttCluster busiMqttCluster) {
		this.busiMqttCluster = busiMqttCluster;
	}

	public synchronized boolean deleteMqttBridge(MqttBridge mqttBridge) {
		return mqttBridges.remove(mqttBridge);
	}
	
	public List<MqttBridge> getMqttBridges() {
		return mqttBridges;
	}
	public void setMqttBridges(List<MqttBridge> mqttBridges) {
		this.mqttBridges = mqttBridges;
	}
	public MqttType getMqttType() {
		return mqttType;
	}
	public void setMqttType(MqttType mqttType) {
		this.mqttType = mqttType;
	}
	public Long getSpareMqttId() {
		return spareMqttId;
	}
	public void setSpareMqttId(Long spareMqttId) {
		this.spareMqttId = spareMqttId;
	}

	public List<MqttBridge> getAvailableMqttBridges() {
		List<MqttBridge> usefulMqttBridges = new ArrayList<>();
        for (Iterator<MqttBridge> iterator = mqttBridges.iterator(); iterator.hasNext();)
        {
        	MqttBridge mqttBridge = iterator.next();
            if (mqttBridge.isAvailable())
            {
            	usefulMqttBridges.add(mqttBridge);
            }
        }
        return usefulMqttBridges;
	}

	public BusiMqttCluster getBusiMqttCluster() {
		return busiMqttCluster;
	}

	public void setBusiMqttCluster(BusiMqttCluster busiMqttCluster) {
		this.busiMqttCluster = busiMqttCluster;
	}
}
