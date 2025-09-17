package com.paradisecloud.fcm.mqtt.interfaces;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * @author zyz
 *
 */
public interface IEmqxBrokerHealthService 
{
	
	public Map<String, Object> emqxBrokerHealth(String brokerUrl, Integer port, String nodeName, HttpServletResponse response);

	public List<Map<String, Object>> dealEmqxBrokerHealthData(HttpServletResponse response) throws UnknownHostException, IOException;
	
	public Map<String, Object> getMqttServerIsOnlineInfo(String brokerUrl, String nodeName, HttpServletResponse response); 
}
