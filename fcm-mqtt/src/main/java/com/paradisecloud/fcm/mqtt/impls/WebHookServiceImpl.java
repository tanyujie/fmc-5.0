package com.paradisecloud.fcm.mqtt.impls;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.dao.model.BusiMqtt;
import com.paradisecloud.fcm.mqtt.cache.MqttBridgeCache;
import com.paradisecloud.fcm.mqtt.client.EmqClient;
import com.paradisecloud.fcm.mqtt.common.ResponseTerminal;
import com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.mqtt.enums.QosEnum;
import com.paradisecloud.fcm.mqtt.enums.TerminalActionEnum;
import com.paradisecloud.fcm.mqtt.interfaces.ITerminalActionService;
import com.paradisecloud.fcm.mqtt.interfaces.IWebHookService;
import com.paradisecloud.fcm.mqtt.model.MqttBridge;
import com.paradisecloud.fcm.mqtt.utils.MqttThreadPool;
import com.paradisecloud.fcm.terminal.fs.util.SpringContextUtil;
import com.sinhy.exception.SystemException;
import com.sinhy.http.HttpObjectCreator;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessorAdapter;

/**
 * @author zyz
 *
 */
@Service
public class WebHookServiceImpl implements IWebHookService 
{
	
	private static Map<String, Object> brokerUrlMap = new HashMap<String, Object>();
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WebHookServiceImpl.class);
	
//	private final RestTemplate restTemplate = GetRestTemplate.getInstance().getRestTemplateObj();
	
	private HttpRequester httpRequester = HttpObjectCreator.getInstance().createHttpRequester(MqttConfigConstant.MQTT_BACK_NAME, MqttConfigConstant.MQTT_BACK_PASSWORD, false);
	
	@Autowired
	private ITerminalActionService  terminalActionService;
 
	@Override
	public void terminalModePublish(String clientid, String brokerUrl, String nodeName, String topicFilter, QosEnum qos2,boolean status) 
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(MqttConfigConstant.CLIENTID, clientid);
		params.put(MqttConfigConstant.TOPIC, topicFilter);
		params.put(MqttConfigConstant.QOS, qos2.value());
		
		if(StringUtils.isNotEmpty(nodeName)) {
			String emqxName = nodeName.split(MqttConfigConstant.AT)[0];
			String idExist = (String)brokerUrlMap.get(emqxName);
			if(StringUtils.isEmpty(idExist)) 
			{
				brokerUrlMap.put(emqxName, brokerUrl);
			}
		}
		
		if(status) 
		{
			params.put(MqttConfigConstant.PAYLOAD, "{"+MqttConfigConstant.CLIENTID+":\'"+clientid+"\',"+MqttConfigConstant.ACTION+":\'"+TerminalActionEnum.ON_LINE.value()+"\'}");
		}else {
			params.put(MqttConfigConstant.PAYLOAD, "{"+MqttConfigConstant.CLIENTID+":\'"+clientid+"\',"+MqttConfigConstant.ACTION+":\'"+TerminalActionEnum.OFF_LINE.value()+"\'}");
		}
		
//		params.put("retain", true);
		
		//设置头信息
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		final HttpEntity<Map<String,Object>> entity = new HttpEntity<Map<String, Object>>(params, headers);
		
		Integer manPort = ResponseTerminal.getInstance().byIpGetBusiMqttInfo(brokerUrl);
		
		if(!MqttThreadPool.isShutDown()) 
		{
			MqttThreadPool.exec(new Runnable() 
			{
				
				@Override
				public void run() 
				{
					
					String httpUrl = MqttConfigConstant.HTTP + brokerUrl 
										+ MqttConfigConstant.COLON + manPort 
										+ MqttConfigConstant.API_AND_VERSION + "/mqtt/publish";
//					ResponseEntity<String> postForEntity = restTemplate.postForEntity(httpUrl, entity, String.class);
//					LOGGER.info(Thread.currentThread().getName()+"=======> 自动发布的结果:{}",postForEntity.getBody());
				}
			});
		}
	}

	@Override
	public Map<String, Object> getBrokerUrlMap() 
	{
		return brokerUrlMap;
	}

	@Override
	public void monitorTerminalStatus(Map<String, Object> paramsMap)
	{

		LOGGER.info("emqx 触发 webhook,请求体数据={}",paramsMap);

		String action = (String) paramsMap.get(MqttConfigConstant.ACTION);
		String terminalIp = (String) paramsMap.get(MqttConfigConstant.IPADDRESS);
		String clientId = (String) paramsMap.get(MqttConfigConstant.CLIENT_ID);
		String userName = (String) paramsMap.get(MqttConfigConstant.USERNAME);
		String nodeName = (String) paramsMap.get(MqttConfigConstant.MQTT_NODE_NAME);
		String reason = (String) paramsMap.get(MqttConfigConstant.TERMINAL_OFF_LINE_REASON);
		LOGGER.info("====================>终端的nodeName",nodeName);


		if(!clientId.contains("FCMSYSTEM")) {
			if(StringUtils.isNotEmpty(nodeName))
			{
				String[] nodeSp = nodeName.split(MqttConfigConstant.AT);
				String mqttIp = nodeSp[1];
				if (nodeSp.length > 0) {

					if(action.equals(MqttConfigConstant.CLIENT_CONNECT))
					{

						//终端上线，fcm需做的处理
					    // 1、如果是新终端，保存信息并根据信息关联租户
						// 2、如果是已有终端就更新状态
						// 3、如果是信令相关的终端，需查询信令信息，会控在发布主题信息，通知终端

						terminalActionService.notifyFcmDealData(clientId, TerminalActionEnum.ON_LINE.value(), userName, mqttIp, terminalIp);
						LOGGER.info("客户端{}接入本系统",clientId);
					}

					if(action.equals(MqttConfigConstant.CLIENT_DISCONNECTED))
					{
						terminalActionService.notifyFcmDealData(clientId, TerminalActionEnum.OFF_LINE.value(), userName, mqttIp, terminalIp);
						LOGGER.info("客户端{}下线",clientId);
					}
				}
			}
		}

		else {
			if(action.equals(MqttConfigConstant.CLIENT_CONNACK))
			{
				//检查会控的订阅主题是否存在
				this.checkSubscriptionTopicIsExits(nodeName,clientId);

			} else if(action.equals(MqttConfigConstant.CLIENT_DISCONNECTED)) {

				ResponseTerminal.getInstance().getMqttConfigInfo();
			}
		}
	}

	private void checkSubscriptionTopicIsExits(String nodeName, String clientId) {
		String[] mqttNode = nodeName.split(MqttConfigConstant.AT);
		if(mqttNode.length > 1) {
			String mqttIp = mqttNode[1];
			MqttBridge mqttBridge = MqttBridgeCache.getInstance().getMqttBridgeByIp(mqttIp);
			BusiMqtt busiMqtt = mqttBridge.getBusiMqtt();
			String httpUrl = MqttConfigConstant.HTTP + mqttIp + MqttConfigConstant.COLON + busiMqtt.getManagementPort() + MqttConfigConstant.API_AND_VERSION + "/subscriptions/" + clientId;
			
			httpRequester.get(httpUrl, new HttpResponseProcessorAdapter() {
				
				@Override
				public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
					try {
						Boolean isConnect = ResponseTerminal.getInstance().terminalIsConnect(busiMqtt, clientId);
						if(isConnect) {
							JSONObject object = (JSONObject) JSONObject.parse(getBodyContent(httpResponse));
							JSONArray arrObj = (JSONArray)object.get("data");
							if(arrObj.size() <= 0) {
								
								//重新订阅主题
								EmqClient emqClient = (EmqClient) SpringContextUtil.getBean("emqClient");
						    	emqClient.subscribe("platform/#", QosEnum.QOS2);
							}
						} 
					} catch (Exception e) {
						throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "FMQ获取节点数据异常!");
					}
				}
				
			});
		}
		
	}
}
