package com.paradisecloud.fcm.mqtt.impls;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.dao.model.BusiMqtt;
import com.paradisecloud.fcm.mqtt.cache.MqttBridgeCache;
import com.paradisecloud.fcm.mqtt.common.ResponseTerminal;
import com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.mqtt.constant.MqttHealthConstant;
import com.paradisecloud.fcm.mqtt.constant.MqttHealthIndicatorConstant;
import com.paradisecloud.fcm.mqtt.interfaces.IEmqxBrokerHealthService;
import com.paradisecloud.fcm.mqtt.model.MqttBridge;
import com.sinhy.exception.SystemException;
import com.sinhy.http.HttpObjectCreator;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessorAdapter;

/**
 * @author zyz
 *
 */
@Service
public class EmqxBrokerHealthServiceImpl implements IEmqxBrokerHealthService 
{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EmqxBrokerHealthServiceImpl.class);

	private HttpRequester httpRequester = HttpObjectCreator.getInstance().createHttpRequester(MqttConfigConstant.MQTT_BACK_NAME, MqttConfigConstant.MQTT_BACK_PASSWORD, false);
	
	@Override
	public Map<String, Object> emqxBrokerHealth(String brokerUrl, Integer port, String nodeName, HttpServletResponse response) {
		Map<String, Object> emqxHealthMap = new HashMap<String, Object>();
		
		String httpUrl = MqttConfigConstant.HTTP + brokerUrl + MqttConfigConstant.COLON + port + MqttConfigConstant.API_AND_VERSION;
		
		
		//节点信息
		emqxHealthMap = getNodeInfo(emqxHealthMap, httpUrl, nodeName, response);
		
		
		//运行统计
		emqxHealthMap = getRunStatistics(emqxHealthMap, httpUrl, nodeName, response);
		
		//内存告警
		emqxHealthMap = getCpuAlarms(emqxHealthMap, httpUrl, response);
		
		//返回指定节点下所有监控指标数据
		emqxHealthMap = getNodeMetrics(emqxHealthMap, httpUrl, nodeName, response);
		
		return emqxHealthMap;
	}

	/**
	 * //节点信息
	 * @param emqxHealthMap
	 * @param response 
	 * @param request 
	 * @param brokerUrl 
	 * @param brokerUrl 
	 * @param restTemplate
	 * @param brokerUrl
	 * @return
	 */
	private Map<String, Object> getNodeInfo(Map<String, Object> emqxHealthMap, String url, String name, HttpServletResponse response) 
	{
		try {
			String nodeUrl = url + "/nodes/" + name;
			
			//请求服务器不同的类型的健康参数
			emqxHealthMap = this.requestServerHealthParam(nodeUrl, emqxHealthMap, MqttConfigConstant.NODE_INFO, response);
		} catch (Exception e) {
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "获取FMQ节点信息异常!");
		}
		return emqxHealthMap;
	}
	
	private Map<String, Object> requestServerHealthParam(String nodeUrl, Map<String, Object> emqxHealthMap, String healthData, HttpServletResponse response) {
		httpRequester.get(nodeUrl, new HttpResponseProcessorAdapter() {
					
					@Override
					public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
						try {
		                    emqxHealthMap.put(healthData, getBodyContent(httpResponse).toString());
						} catch (Exception e) {
							throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "获取FMQ运行数据异常!");
						}
					}
				});
		
		return emqxHealthMap;
	}

	/**
	 * //运行统计
	 * @param emqxHealthMap
	 * @param nodeName 
	 * @param response 
	 * @param request 
	 * @param restTemplate
	 * @param brokerUrl
	 * @return
	 */
	private Map<String, Object> getRunStatistics(Map<String, Object> emqxHealthMap, String url, String nodeName, HttpServletResponse response) 
	{
		try 
		{
			String runUrl = url + "/nodes/"+nodeName+"/stats";
			
			//请求服务器不同的类型的健康参数
			emqxHealthMap = this.requestServerHealthParam(runUrl, emqxHealthMap, MqttConfigConstant.RUN_STATISTICS, response);
		} 
		catch (Exception e) 
		{
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "获取FMQ运行统计数据异常!");
		}
		
		return emqxHealthMap;
	}
	
	/**
	 * //内存告警
	 * @param emqxHealthMap
	 * @param response 
	 * @param request 
	 * @param restTemplate
	 * @param brokerUrl
	 * @return
	 */
	private Map<String, Object> getCpuAlarms(Map<String, Object> emqxHealthMap, String url, HttpServletResponse response) 
	{
		try 
		{
			String alarmsUrl = url + "/alarms";
			
			//请求服务器不同的类型的健康参数
			emqxHealthMap = this.requestServerHealthParam(alarmsUrl, emqxHealthMap, MqttConfigConstant.CPU_ALARMS, response);
		} 
		catch (Exception e) 
		{
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "FMQ CPU告警异常!");
		}
		return emqxHealthMap;
	}
	
	/**
	 * 返回指定节点下所有监控指标数据
	 * @param emqxHealthMap
	 * @param response 
	 * @param request 
	 * @param httpUrl
	 * @param nodeName
	 * @return
	 */
	private Map<String, Object> getNodeMetrics(Map<String, Object> emqxHealthMap, String url, String name, HttpServletResponse response) 
	{
		try 
		{
			String metricUrl = url + "/nodes/" + name + "/metrics";
			
			//请求服务器不同的类型的健康参数
			emqxHealthMap = this.requestServerHealthParam(metricUrl, emqxHealthMap, MqttConfigConstant.NODE_METRICS, response);
		} 
		catch (Exception e) 
		{
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "FMQ获取监控指标异常!");
		}
		return emqxHealthMap;
	}

	@Override
	public List<Map<String, Object>> dealEmqxBrokerHealthData(HttpServletResponse response) throws UnknownHostException, IOException 
	{
		List<Map<String, Object>> healthList = new ArrayList<Map<String, Object>>();
		List<MqttBridge> mqttBridges = MqttBridgeCache.getInstance().getMqttBridges();
		if(null != mqttBridges && mqttBridges.size() > 0) 
		{
			for (MqttBridge mqttBridge : mqttBridges) 
			{
				BusiMqtt busiMqtt = mqttBridge.getBusiMqtt();
				Map<String, Object> combineMap = new HashMap<String, Object>();
				if(null != busiMqtt) 
				{
					String nodeName = busiMqtt.getNodeName();
					int tcpPort = busiMqtt.getTcpPort();
					if (busiMqtt.getUseSsl() != null && busiMqtt.getUseSsl() == 1) {
						tcpPort = MqttConfigConstant.DEFAULT_SSL_TCP_PORT;
					}
					Boolean isSuccess = ResponseTerminal.getInstance().pingIpAndPort(busiMqtt.getIp(), tcpPort);
					if (isSuccess) 
					{
						
//						Map<String, Object> brokerHealth = this.emqxBrokerHealth(busiMqtt.getIp(), busiMqtt.getManagementPort(), nodeName, response);
						Map<String, Object> brokerHealth = mqttBridge.getBrokerHealthMap();
						if (brokerHealth == null || brokerHealth.isEmpty()) {
							continue;
						}
						brokerHealth.remove(MqttConfigConstant.CPU_ALARMS);
						brokerHealth.remove(MqttConfigConstant.NODE_METRICS);
						brokerHealth.forEach((key,value)->{
							Map<String, Object> healthMap = new HashMap<String, Object>();
							String healhtData = (String) brokerHealth.get(key);
							JSONObject jso=JSON.parseObject(healhtData);
							JSONObject jsonObject = jso.getJSONObject(MqttConfigConstant.JSON_DATA_STR);
							if(key.equals(MqttConfigConstant.NODE_INFO)) 
							{
								
								Integer onlineStatus = null;
								String memoryTotal = jsonObject.getString(MqttHealthConstant.MEMORY_TOTAL);
								String memoryUsed = jsonObject.getString(MqttHealthConstant.MEMORY_USED);
								String isOnline = jsonObject.getString(MqttHealthConstant.NODE_STATUS);
								if(StringUtils.isNotEmpty(isOnline) && isOnline.equals(MqttConfigConstant.MQTT_SERVER_RUNNING)) 
								{
									onlineStatus = TerminalOnlineStatus.ONLINE.getValue();
								}
								else
								{
									onlineStatus = TerminalOnlineStatus.OFFLINE.getValue();
								}
								healthMap.put(MqttHealthIndicatorConstant.NODE_NAME, jsonObject.getString(MqttHealthConstant.NODE_NAME));
								healthMap.put(MqttHealthIndicatorConstant.OTP_RELEASE, jsonObject.getString(MqttHealthConstant.OTP_RELEASE));
								healthMap.put(MqttHealthIndicatorConstant.UPTIME, jsonObject.getString(MqttHealthConstant.UPTIME));
								healthMap.put(MqttHealthIndicatorConstant.NODE_STATUS, onlineStatus);
								healthMap.put(MqttHealthIndicatorConstant.PROCESS_AVAILABLE, jsonObject.getString(MqttHealthConstant.PROCESS_AVAILABLE));
								healthMap.put(MqttHealthIndicatorConstant.PROCESS_USED, jsonObject.getString(MqttHealthConstant.PROCESS_USED));
								if (StringUtils.isNumeric(memoryTotal.substring(memoryTotal.length() - 1))) {
									healthMap.put(MqttHealthIndicatorConstant.MEMORY_TOTAL, Long.valueOf(memoryTotal)/MqttConfigConstant.CONVERSION_1024 + "M");
								} else {
									healthMap.put(MqttHealthIndicatorConstant.MEMORY_TOTAL, memoryTotal);
								}
								if (StringUtils.isNumeric(memoryTotal.substring(memoryTotal.length() - 1))) {
									healthMap.put(MqttHealthIndicatorConstant.MEMORY_USED, Long.valueOf(memoryUsed) / MqttConfigConstant.CONVERSION_1024 + "M");
								} else {
									healthMap.put(MqttHealthIndicatorConstant.MEMORY_USED, memoryUsed);
								}
								healthMap.put(MqttHealthIndicatorConstant.LOAD_ONE, jsonObject.getString(MqttHealthConstant.LOAD_ONE));
								healthMap.put(MqttHealthIndicatorConstant.LOAD_FIVE, jsonObject.getString(MqttHealthConstant.LOAD_FIVE));
								healthMap.put(MqttHealthIndicatorConstant.LOAD_FIFTEEN, jsonObject.getString(MqttHealthConstant.LOAD_FIFTEEN));
								healthMap.put(MqttHealthIndicatorConstant.MAX_FDS, jsonObject.getString(MqttHealthConstant.MAX_FDS));
								combineMap.putAll(healthMap);
							}
							
							if(key.equals(MqttConfigConstant.RUN_STATISTICS)) 
							{
								healthMap.put(MqttHealthIndicatorConstant.CONNECTIONS_COUNT, jsonObject.getString(MqttHealthConstant.CONNECTIONS_COUNT));
								healthMap.put(MqttHealthIndicatorConstant.CONNECTIONS_MAX, jsonObject.getString(MqttHealthConstant.CONNECTIONS_MAX));
								healthMap.put(MqttHealthIndicatorConstant.TOPICS_COUNT, jsonObject.getString(MqttHealthConstant.TOPICS_COUNT));
								healthMap.put(MqttHealthIndicatorConstant.TOPICS_MAX, jsonObject.getString(MqttHealthConstant.TOPICS_MAX));
								healthMap.put(MqttHealthIndicatorConstant.RETAINED_COUNT, jsonObject.getString(MqttHealthConstant.RETAINED_COUNT));
								healthMap.put(MqttHealthIndicatorConstant.RETAINED_MAX, jsonObject.getString(MqttHealthConstant.RETAINED_MAX));
								healthMap.put(MqttHealthIndicatorConstant.SUBSCRIBERS_COUNT, jsonObject.getString(MqttHealthConstant.SUBSCRIBERS_COUNT));
								healthMap.put(MqttHealthIndicatorConstant.SUBSCRIBERS_MAX, jsonObject.getString(MqttHealthConstant.SUBSCRIBERS_MAX));
								healthMap.put(MqttHealthIndicatorConstant.SESSIONS_COUNT, jsonObject.getString(MqttHealthConstant.SESSIONS_COUNT));
								healthMap.put(MqttHealthIndicatorConstant.SESSIONS_MAX, jsonObject.getString(MqttHealthConstant.SESSIONS_MAX));
								healthMap.put(MqttHealthIndicatorConstant.SUBSCRIPTIONS_SHARED_COUNT, jsonObject.getString(MqttHealthConstant.SUBSCRIPTIONS_SHARED_COUNT));
								healthMap.put(MqttHealthIndicatorConstant.SUBSCRIPTIONS_SHARED_MAX, jsonObject.getString(MqttHealthConstant.SUBSCRIPTIONS_SHARED_MAX));
								combineMap.putAll(healthMap);
							}
						});
						healthList.add(combineMap);
					}
					else 
					{
						Map<String, Object> noHealthMap = new HashMap<String, Object>();
						String noData = MqttHealthIndicatorConstant.HORIZONTAL_LINE;
						noHealthMap.put(MqttHealthIndicatorConstant.NODE_NAME, nodeName);
						noHealthMap.put(MqttHealthIndicatorConstant.OTP_RELEASE, noData);
						noHealthMap.put(MqttHealthIndicatorConstant.UPTIME, noData);
						noHealthMap.put(MqttHealthIndicatorConstant.NODE_STATUS, noData);
						noHealthMap.put(MqttHealthIndicatorConstant.PROCESS_AVAILABLE, noData);
						noHealthMap.put(MqttHealthIndicatorConstant.PROCESS_USED, noData);
						noHealthMap.put(MqttHealthIndicatorConstant.MEMORY_TOTAL, noData);
						noHealthMap.put(MqttHealthIndicatorConstant.MEMORY_USED, noData);
						noHealthMap.put(MqttHealthIndicatorConstant.LOAD_ONE, noData);
						noHealthMap.put(MqttHealthIndicatorConstant.LOAD_FIVE, noData);
						noHealthMap.put(MqttHealthIndicatorConstant.LOAD_FIFTEEN, noData);
						noHealthMap.put(MqttHealthIndicatorConstant.MAX_FDS, noData);
						
						noHealthMap.put(MqttHealthIndicatorConstant.CONNECTIONS_COUNT, noData);
						noHealthMap.put(MqttHealthIndicatorConstant.CONNECTIONS_MAX, noData);
						noHealthMap.put(MqttHealthIndicatorConstant.TOPICS_COUNT, noData);
						noHealthMap.put(MqttHealthIndicatorConstant.TOPICS_MAX, noData);
						noHealthMap.put(MqttHealthIndicatorConstant.RETAINED_COUNT, noData);
						noHealthMap.put(MqttHealthIndicatorConstant.RETAINED_MAX, noData);
						noHealthMap.put(MqttHealthIndicatorConstant.SUBSCRIBERS_COUNT, noData);
						noHealthMap.put(MqttHealthIndicatorConstant.SUBSCRIBERS_MAX, noData);
						noHealthMap.put(MqttHealthIndicatorConstant.SESSIONS_COUNT, noData);
						noHealthMap.put(MqttHealthIndicatorConstant.SESSIONS_MAX, noData);
						noHealthMap.put(MqttHealthIndicatorConstant.SUBSCRIPTIONS_SHARED_COUNT, noData);
						noHealthMap.put(MqttHealthIndicatorConstant.SUBSCRIPTIONS_SHARED_MAX, noData);
						healthList.add(noHealthMap);
					}
				}
			}
		}
		return healthList;
	}

	@Override
	public Map<String, Object> getMqttServerIsOnlineInfo(String brokerUrl, String nodeName, HttpServletResponse response) {
		Map<String, Object> nodeMap = new HashMap<String, Object>();
		try 
		{
			String url = brokerUrl + "/nodes/" + nodeName;
			httpRequester.get(url, new HttpResponseProcessorAdapter() {
				
				@Override
				public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
					try {
	                    String nodeData = getBodyContent(httpResponse);
	                    JSONObject jso=JSON.parseObject(nodeData);
	        			JSONObject jsonObject = jso.getJSONObject(MqttConfigConstant.JSON_DATA_STR);
	        			String onlineStatus = jsonObject.getString(MqttHealthConstant.NODE_STATUS);
	        			nodeMap.put(MqttHealthConstant.NODE_STATUS, onlineStatus);
	        			LOGGER.info(getBodyContent(httpResponse));
					} catch (Exception e) {
						throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "FMQ在线运行异常!");
					}
				}
			});
		} 
		catch (Exception e) 
		{
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "FMQ在线运行异常!");
		}
		return nodeMap;
	}
}
