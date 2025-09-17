package com.paradisecloud.fcm.mqtt.model;

public class MqttPublishData {
	
	private String clientId;
	
	private String ip;
	
	private String action;
	
	private String isAgree;
	
	private String topic;
	
	private String qos;
	
	private String userName;
	
	private String password;
	
	private String fromTerminal;
	
	private String toTerminal;

	public String getFromTerminal() {
		return fromTerminal;
	}

	public void setFromTerminal(String fromTerminal) {
		this.fromTerminal = fromTerminal;
	}

	public String getToTerminal() {
		return toTerminal;
	}

	public void setToTerminal(String toTerminal) {
		this.toTerminal = toTerminal;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getIsAgree() {
		return isAgree;
	}

	public void setIsAgree(String isAgree) {
		this.isAgree = isAgree;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getQos() {
		return qos;
	}

	public void setQos(String qos) {
		this.qos = qos;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "MqttPublishData [clientId=" + clientId + ", ip=" + ip + ", action=" + action + ", isAgree=" + isAgree
				+ ", topic=" + topic + ", qos=" + qos + ", userName=" + userName + ", password=" + password
				+ ", fromTerminal=" + fromTerminal + ", toTerminal=" + toTerminal + "]";
	}

	public MqttPublishData(String clientId, String ip, String action, String isAgree, String topic, String qos,
			String userName, String password, String fromTerminal, String toTerminal) {
		super();
		this.clientId = clientId;
		this.ip = ip;
		this.action = action;
		this.isAgree = isAgree;
		this.topic = topic;
		this.qos = qos;
		this.userName = userName;
		this.password = password;
		this.fromTerminal = fromTerminal;
		this.toTerminal = toTerminal;
	}

	public MqttPublishData() {
		super();
	}
}
