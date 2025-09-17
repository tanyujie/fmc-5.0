package com.paradisecloud.fcm.mqtt.model;

public class MqttStatusData {
	
	private String Status;
	
	private Integer dropMessageNum;
	
	private Integer connected;
	
	private Integer disconnected;

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public Integer getDropMessageNum() {
		return dropMessageNum;
	}

	public void setDropMessageNum(Integer dropMessageNum) {
		this.dropMessageNum = dropMessageNum;
	}

	public Integer getConnected() {
		return connected;
	}

	public void setConnected(Integer connected) {
		this.connected = connected;
	}

	public Integer getDisconnected() {
		return disconnected;
	}

	public void setDisconnected(Integer disconnected) {
		this.disconnected = disconnected;
	}

	@Override
	public String toString() {
		return "MqttStatusData [Status=" + Status + ", dropMessageNum=" + dropMessageNum + ", connected=" + connected
				+ ", disconnected=" + disconnected + "]";
	}
}
