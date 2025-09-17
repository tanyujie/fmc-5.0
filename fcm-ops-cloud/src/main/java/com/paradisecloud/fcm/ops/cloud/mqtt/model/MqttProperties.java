package com.paradisecloud.fcm.ops.cloud.mqtt.model;


/**
 * @author zyz
 *
 */
public class MqttProperties 
{
	
	private String brokerUrl;
	
	private String clientId;
	
	private String userName;
	
	private String password;

	public String getBrokerUrl() 
	{
		return brokerUrl;
	}

	public void setBrokerUrl(String brokerUrl) 
	{
		this.brokerUrl = brokerUrl;
	}

	public String getClientId() 
	{
		return clientId;
	}

	public void setClientId(String clientId) 
	{
		this.clientId = clientId;
	}


	public String getUserName() 
	{
		return userName;
	}

	public void setUserName(String userName) 
	{
		this.userName = userName;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password) 
	{
		this.password = password;
	}

	@Override
	public String toString() 
	{
		return "MqttProperties [brokerUrl=" + brokerUrl + ", clientId=" + clientId + ", userName=" + userName
				+ ", password=" + password + "]";
	}

	
}
