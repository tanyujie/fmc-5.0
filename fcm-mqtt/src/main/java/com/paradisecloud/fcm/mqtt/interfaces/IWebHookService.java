package com.paradisecloud.fcm.mqtt.interfaces;

import java.util.Map;

import com.paradisecloud.fcm.mqtt.enums.QosEnum;

/**
 * @author zyz
 *
 */
public interface IWebHookService 
{

	public void terminalModePublish(String clientid, String brokerUrl, String nodeName, String topicFilter, QosEnum qos2,boolean status);
	
	public Map<String, Object> getBrokerUrlMap();

	public void monitorTerminalStatus(Map<String, Object> paramsMap);

}
