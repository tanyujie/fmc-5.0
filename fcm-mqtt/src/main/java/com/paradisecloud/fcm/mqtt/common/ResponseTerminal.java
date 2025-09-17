package com.paradisecloud.fcm.mqtt.common;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.mqtt.enums.QosEnum;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.dao.mapper.BusiMqttMapper;
import com.paradisecloud.fcm.dao.model.BusiMqtt;
import com.paradisecloud.fcm.mqtt.cache.MqttBridgeCache;
import com.paradisecloud.fcm.mqtt.client.EmqClient;
import com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.mqtt.constant.ResponseInfo;
import com.paradisecloud.fcm.mqtt.model.MqttBridge;
import com.paradisecloud.fcm.mqtt.model.MqttProperties;
import com.paradisecloud.fcm.terminal.fs.util.SpringContextUtil;
import com.sinhy.exception.SystemException;
import com.sinhy.http.HttpObjectCreator;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessorAdapter;

public abstract class ResponseTerminal {
	
	private HttpRequester httpRequester = HttpObjectCreator.getInstance().createHttpRequester(MqttConfigConstant.MQTT_BACK_NAME, MqttConfigConstant.MQTT_BACK_PASSWORD, false);
	
	private static final ResponseTerminal INSTANCE = new ResponseTerminal() 
	{
		
	};
		
	public static ResponseTerminal getInstance() 
	{
		return INSTANCE;
	}

	public void responseTerminalSuccess(String terminalTopic, String action, JSONObject jObj, String clientId, String messageId) {
		responseTerminal(ResponseInfo.CODE_200, ResponseInfo.SUCCESS, terminalTopic, action, jObj, clientId, messageId);
	}
	
	public void responseTerminalFailed(String terminalTopic, String action, JSONObject jObj, String clientId, String messageId) {
		responseTerminal(ResponseInfo.CODE_500, ResponseInfo.FAILED, terminalTopic, action, jObj, clientId, messageId);
	}

	public void responseTerminal(Integer code, String message, String terminalTopic, String action, JSONObject jObj, String clientId, String messageId) {
		JSONObject jObject = new JSONObject();
		jObject.put(MqttConfigConstant.CODE, code);
		jObject.put(MqttConfigConstant.MSG, message);
		jObject.put(MqttConfigConstant.ACTION, action);
		jObject.put(MqttConfigConstant.MESSAGE_ID, messageId);
		jObject.put(MqttConfigConstant.JSON_DATA_STR, jObj);
		PublisMessage.getInstance().publishTopicMsg(terminalTopic, clientId, jObject.toString(), false);
	}

	public void responseTerminalSuccessByQOS(String terminalTopic, String action, JSONObject jObj, String messageId, QosEnum qos){
		responseTerminalByQOS(ResponseInfo.CODE_200, ResponseInfo.SUCCESS, terminalTopic, action, jObj, messageId, qos);
	}

	public void responseTerminalFailedByQOS(String terminalTopic, String action, JSONObject jObj, String messageId, QosEnum qos){
		responseTerminalByQOS(ResponseInfo.CODE_500, ResponseInfo.FAILED, terminalTopic, action, jObj, messageId, qos);
	}

	public void responseTerminalByQOS(Integer code, String message, String terminalTopic, String action, JSONObject jObj, String messageId, QosEnum qos){
		JSONObject jObject = new JSONObject();
		jObject.put(MqttConfigConstant.CODE, code);
		jObject.put(MqttConfigConstant.MSG, message);
		jObject.put(MqttConfigConstant.ACTION, action);
		jObject.put(MqttConfigConstant.MESSAGE_ID, messageId);
		jObject.put(MqttConfigConstant.JSON_DATA_STR, jObj);
		if(StringUtils.isNotEmpty(terminalTopic))
		{
			String[] topicArr = terminalTopic.split(MqttConfigConstant.SLASH);
			if(topicArr.length >= 1)
			{
				try {
					EmqClient emqClient = (EmqClient) SpringContextUtil.getBean("emqClient");
					if(null != emqClient) {
						emqClient.publish(terminalTopic, jObject.toString(), qos, false);
					}
				} catch (Exception e) {
				}
			}
		}
	}
	
	public Boolean pingIp(String ip) 
	{
      if (null == ip || 0 == ip.length()) {
           return false;
      }
      
      try 
      {
          InetAddress.getByName(ip);
          return true;
     } 
     catch (IOException e) 
     {
          return false;
     }
	}
	
	
	public Boolean pingIpAndPort(String ip, Integer port) {
		if (null == ip || 0 == ip.length() || port < 1024 || port > 65535)
		{
			  return false;
		}
		
	    if (!pingIp(ip)) 
	    {
	        return false;
	    }
	    
	    Socket s = new Socket();
	    try 
	    {
	         SocketAddress add = new InetSocketAddress(ip, port);
	         s.connect(add, 500);// 超时3秒
	         return true;
	    } 
	    catch (IOException e) 
	    {
	          return false;
	    } 
	    finally 
	    {
	        try 
	        {
	              s.close();
	         } 
	         catch (Exception e) 
	        {
	        	 
	        }
	    }
	}
	
	/**
	 * 获取mqtt配置信息
	 */
	public void getMqttConfigInfo() {
			BusiMqtt busiMqtt = new BusiMqtt();
			BusiMqttMapper busiMqttMapper = (BusiMqttMapper) SpringContextUtil.getBean("busiMqttMapper");
			List<BusiMqtt> busiMqttList = busiMqttMapper.selectBusiMqttList(busiMqtt);
			if(null != busiMqttList && busiMqttList.size() > 0) {
				for (BusiMqtt busiMqtt2 : busiMqttList) {
					String ip = busiMqtt2.getIp();
					String fmcRootUrl = ExternalConfigCache.getInstance().getFmcRootUrl();
					if (!fmcRootUrl.contains(ip)) {
						continue;
					}

					Boolean isSuccess = this.pingIp(busiMqtt2.getIp());
					if(isSuccess) {
						MqttProperties mqttProperties = new MqttProperties();
						mqttProperties.setUserName(busiMqtt2.getUserName());
						mqttProperties.setPassword(busiMqtt2.getPassword());
						mqttProperties.setBrokerUrl(busiMqtt2.getIp());
						mqttProperties.setClientId("FCMSYSTEM");
						EmqClient emqClient = (EmqClient) SpringContextUtil.getBean("emqClient");
						if(null != emqClient) {
							Boolean isConnect = emqClient.connect(mqttProperties);
							if (isConnect) {
								emqClient.subscribe("platform/#", QosEnum.QOS2);
								String nodeName = busiMqtt2.getNodeName();
								String s1 = "$SYS/brokers/" + nodeName + "/clients/#";
								emqClient.subscribe(s1, QosEnum.QOS2);
							}

	//						if(isConnect) {
	//							emqClient.subscribe("platform/#", QosEnum.QOS2);
	//						}
						}
					}
				}
			}
	}
	
	 public Boolean checkNodeConnectMqtt(String nodeUrl) {
	    	Map<Object, Object> map = new HashMap<>();
	    	httpRequester.get(nodeUrl, new HttpResponseProcessorAdapter() {
				
				@Override
				public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
					try {
						JSONObject object = (JSONObject) JSONObject.parse(getBodyContent(httpResponse));
						JSONArray arrObj = (JSONArray)object.get("data");
	                    if(arrObj.size() > 0) {
	                    	map.put("isExist", true);
	                    }else {
	                    	map.put("isExist", false);
						}
					} catch (Exception e) {
						throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "MQTT获取节点数据异常!");
					}
				}
			});
	    	
			return (Boolean) map.get("isExist");
		}
	
	public Integer byIpGetBusiMqttInfo(String brokerUrl) {
		MqttBridge mqttBridge = MqttBridgeCache.getInstance().getMqttBridgeByIp(brokerUrl);
		if(null != mqttBridge && null != mqttBridge.getBusiMqtt().getManagementPort()) 
		{
			return mqttBridge.getBusiMqtt().getManagementPort();
		}
		return null;
	}
	
	public Boolean connectMqttServer(String userName, String password, String clientid, String url) {
		MqttProperties mqttProperties = new MqttProperties();
		mqttProperties.setUserName(userName);
		mqttProperties.setPassword(password);
		mqttProperties.setBrokerUrl(url);
		mqttProperties.setClientId(clientid);
		
		EmqClient emqClient = (EmqClient) SpringContextUtil.getBean("emqClient");
		Boolean isConnect = emqClient.connect(mqttProperties);
		return isConnect;
	}
	
	public Boolean terminalIsConnect(BusiMqtt busiMqtt, String clientId) {
		Boolean isConn = false;
		List<String> onlineStatus = new ArrayList<String>();
		Integer tcpPort = busiMqtt.getManagementPort();
		String httpUrl = MqttConfigConstant.HTTP + busiMqtt.getIp() + MqttConfigConstant.COLON + tcpPort + MqttConfigConstant.API_AND_VERSION;
		String connUrl = httpUrl + "/clients/" + clientId;
		
		httpRequester.get(connUrl, new HttpResponseProcessorAdapter() {
			
			@Override
			public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
				try {
					
                    String nodeData = getBodyContent(httpResponse);
                    if(StringUtils.isNotEmpty(nodeData)) 
    				{
    					
    					JSONObject jsonObject = (JSONObject) JSONObject.parse(nodeData);
                    	String data = jsonObject.getString(MqttConfigConstant.JSON_DATA_STR);
    					JSONArray array = (JSONArray) JSONArray.parse(data);
    					if(null != array && array.size() > 0) {
							JSONObject jsonObj = (JSONObject)array.get(0);
							Boolean connect = jsonObj.getBoolean("connected");
							if(connect) {
								onlineStatus.add("1");
							}else {
								onlineStatus.add("2");
							}
    					}else {
    						onlineStatus.add("2");
    					}
    					
    				}
				} catch (Exception e) {
					throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "连接MQTT服务失败!");
				}
			}
		});
		
		if(onlineStatus.size() > 0 && "1".equals(onlineStatus.get(0))) {
			isConn = true;
		}
		
	return isConn;
	}
}
