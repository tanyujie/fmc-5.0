package com.paradisecloud.fcm.mqtt.scheduler;

import java.io.IOException;
import java.util.*;

import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.dao.mapper.BusiOpsMapper;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomDoorplateMapper;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomLotMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mqtt.cache.MqttClusterCache;
import com.paradisecloud.fcm.mqtt.enums.MqttBridgeStatus;
import com.paradisecloud.fcm.mqtt.enums.MqttType;
import com.paradisecloud.fcm.mqtt.interfaces.IEmqxBrokerHealthService;
import com.paradisecloud.fcm.mqtt.task.OpsPushRegisterTask;
import com.paradisecloud.fcm.ops.cloud.cache.OpsCache;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomDoorplateCache;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomLotCache;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.mqtt.cache.MqttBridgeCache;
import com.paradisecloud.fcm.mqtt.cache.MqttDeptMappingCache;
import com.paradisecloud.fcm.mqtt.client.EmqClient;
import com.paradisecloud.fcm.mqtt.common.ResponseTerminal;
import com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.mqtt.constant.MqttHealthConstant;
import com.paradisecloud.fcm.mqtt.enums.QosEnum;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiRegisterTerminalService;
import com.paradisecloud.fcm.mqtt.model.MqttBridge;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.util.SpringContextUtil;
import com.sinhy.http.HttpObjectCreator;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.utils.ThreadUtils;

import javax.annotation.Resource;

@Component
public class MonitorMqttServeAndUser extends Thread implements InitializingBean , ApplicationRunner {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private IBusiRegisterTerminalService busiRegisterTerminalService;

	@Resource
	private IEmqxBrokerHealthService emqxBrokerHealthService;

	@Resource
	private BusiSmartRoomDoorplateMapper busiSmartRoomDoorplateMapper;

	@Resource
	private BusiSmartRoomLotMapper busiSmartRoomLotMapper;

	@Resource
	private BusiOpsMapper busiOpsMapper;

	@Resource
	private TaskService taskService;
	
	private HttpRequester httpRequester = HttpObjectCreator.getInstance().createHttpRequester(MqttConfigConstant.MQTT_BACK_NAME, MqttConfigConstant.MQTT_BACK_PASSWORD, false);
	
	@Override
    public void run()
    {
		//监测FMQ服务和FMQ的用户
		logger.info("===========> 监测FMQ服务是否正常！");
        ThreadUtils.sleep(10 * 1000);
        while (true)
        {
			try {
				//获取FMQ服务信息
				this.getFmqServerInfo();

				if (TerminalCache.getInstance().isNeedUpdateMqStatus()
						|| SmartRoomDoorplateCache.getInstance().isNeedUpdateMqStatus()
						|| SmartRoomLotCache.getInstance().isNeedUpdateMqStatus()
						|| OpsCache.getInstance().isNeedUpdateMqStatus()) {
					//获取FMQ服务上的终端
					this.getUserServerInfo();
					TerminalCache.getInstance().setNeedUpdateMqStatus(false);
					SmartRoomDoorplateCache.getInstance().setNeedUpdateMqStatus(false);
					SmartRoomLotCache.getInstance().setNeedUpdateMqStatus(false);
					OpsCache.getInstance().setNeedUpdateMqStatus(false);
				}

			} catch (Throwable e) {
				logger.error("FMQ服务出现异常！", e);
			} finally {
				ThreadUtils.sleep(10000);
			}
        }
    }

	private void getUserServerInfo() {
		this.updateMqttUserStatus();
	}

	private void getFmqServerInfo() {
		List<MqttBridge> mqttBridges = MqttBridgeCache.getInstance().getMqttBridges();
    	if(null != mqttBridges && mqttBridges.size() > 0) {
    		for (MqttBridge mqttBridge : mqttBridges) {

				String ip = mqttBridge.getBusiMqtt().getIp();
				Set<String> fmqIpList = ExternalConfigCache.getInstance().getFmqIpList();
				if (fmqIpList.contains(ip)) {
					//检查mqtt服务是否正常运行
					this.checkMqttServerIsnormal(mqttBridge);
				} else {
					mqttBridge.setBrokerHealthMap(null);
				}
			}
    	}
	}

	public Set<String> fcmsystemIP(){
		Set<String> onlineClientSet = new HashSet<>();
		List<MqttBridge> mqttBridges = MqttBridgeCache.getInstance().getMqttBridges();
		if(null != mqttBridges && mqttBridges.size() > 0) {
			for (MqttBridge mqttBridge : mqttBridges) {
				BusiMqtt busiMqtt = mqttBridge.getBusiMqtt();

				String clientId = "FCMSYSTEM";
				String ip = busiMqtt.getIp();
				Integer port = busiMqtt.getManagementPort();
				String brokerUrl = MqttConfigConstant.HTTP + ip + MqttConfigConstant.COLON + port + MqttConfigConstant.API_AND_VERSION;
				String subUrl = brokerUrl + "/clients/" + clientId;
				httpRequester.get(subUrl, new HttpResponseProcessorAdapter() {

					@Override
					public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
						try {
							String nodeData = getBodyContent(httpResponse);
							logger.info("++++++++++++++++++++++++++++++++>>> nodeData" + nodeData.toString());
							JSONObject jsonObject = (JSONObject) JSONObject.parse(nodeData);
							String data = jsonObject.getString(MqttConfigConstant.JSON_DATA_STR);
							JSONArray array = (JSONArray) JSONArray.parse(data);
							if (array != null){
								JSONObject jsonObj = (JSONObject) array.get(0);
								if (jsonObj.containsKey("ip_address")){
									String Ip =(String) jsonObj.get("ip_address");
									onlineClientSet.add(Ip);
								}

							}

						} catch (Exception e) {
							logger.error("FMQ获取节点数据异常！", e);
						}
					}

				});
			}
		}
		return onlineClientSet;
	}

	private void checkMqttServerIsnormal(MqttBridge mqttBridge) {
		BusiMqtt busiMqtt = mqttBridge.getBusiMqtt();
		String s1 = "$SYS/brokers/" + busiMqtt.getNodeName() + "/clients/#";

		int tcpPort = busiMqtt.getTcpPort();
		if (busiMqtt.getUseSsl() != null && busiMqtt.getUseSsl() == 1) {
			tcpPort = MqttConfigConstant.DEFAULT_SSL_TCP_PORT;
		}
		Boolean isPing = ResponseTerminal.getInstance().pingIpAndPort(busiMqtt.getIp(), tcpPort);
		if (isPing) {
			String clientId = "FCMSYSTEM";
			String ip = busiMqtt.getIp();
			Integer port = busiMqtt.getManagementPort();
			String brokerUrl = MqttConfigConstant.HTTP + ip + MqttConfigConstant.COLON + port + MqttConfigConstant.API_AND_VERSION;
			Map<String, Object> onlineMap = this.getMqttServerIsOnlineInfo(brokerUrl, busiMqtt.getNodeName());
			if (!onlineMap.isEmpty()) {
				String online = (String) onlineMap.get(MqttHealthConstant.NODE_STATUS);
				if ("Running".equals(online)) {
					if (!mqttBridge.isAvailable()) {
						TerminalCache.getInstance().setNeedUpdateMqStatus(true);
						mqttBridge.setMqttBridgeStatus(MqttBridgeStatus.AVAILABLE);
					}
					Boolean isConnect = ResponseTerminal.getInstance().terminalIsConnect(busiMqtt, clientId);
					if (isConnect) {
						//检查上面的会控是否订阅主题
						this.checkSubscriptionTopicIsExits(brokerUrl, clientId);
					} else {
						Boolean isPingIp = ResponseTerminal.getInstance().pingIpAndPort(busiMqtt.getIp(), tcpPort);
						if (isPingIp) {
							Boolean connectMqttServer = ResponseTerminal.getInstance().connectMqttServer(busiMqtt.getUserName(), busiMqtt.getPassword(), clientId, ip);
							if (connectMqttServer) {

								//重新订阅主题
								EmqClient emqClient = (EmqClient) SpringContextUtil.getBean("emqClient");
								if (null != emqClient) {
									emqClient.subscribe(s1, QosEnum.QOS2);
									emqClient.subscribe("platform/#", QosEnum.QOS2);
								}
							}
						}
					}
				} else {
					if (mqttBridge.isAvailable()) {
						TerminalCache.getInstance().setNeedUpdateMqStatus(true);
						mqttBridge.setMqttBridgeStatus(MqttBridgeStatus.NOT_AVAILABLE);
					}
				}
			} else {
				if (mqttBridge.isAvailable()) {
					TerminalCache.getInstance().setNeedUpdateMqStatus(true);
					mqttBridge.setMqttBridgeStatus(MqttBridgeStatus.NOT_AVAILABLE);
				}
			}

			Map<String, Object> emqxBrokerHealth = emqxBrokerHealthService.emqxBrokerHealth(busiMqtt.getIp(), busiMqtt.getManagementPort(), busiMqtt.getNodeName(), null);
			mqttBridge.setBrokerHealthMap(emqxBrokerHealth);
		} else {
			mqttBridge.setMqttBridgeStatus(MqttBridgeStatus.NOT_AVAILABLE);
			TerminalCache.getInstance().setNeedUpdateMqStatus(true);
			logger.error(MqttConfigConstant.EXCEPTION_ONE_th_th_F + "请检查[" + busiMqtt.getIp() + "]上的FMQ服务,连接异常!");
		}
	}
	
	private void checkSubscriptionTopicIsExits(String url, String clientId) {
		String subUrl = url + "/subscriptions/" + clientId;
		
		httpRequester.get(subUrl, new HttpResponseProcessorAdapter() {
			
			@Override
			public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
				try {
					JSONObject object = (JSONObject) JSONObject.parse(getBodyContent(httpResponse));
					JSONArray arrObj = (JSONArray)object.get("data");
					if(arrObj.size() <= 0) {
						//重新订阅主题
						EmqClient emqClient = (EmqClient) SpringContextUtil.getBean("emqClient");
						String s1 = null;
						if (emqClient.getMqttClient() != null) {
							IMqttClient mqttClient = emqClient.getMqttClient();
							emqClient.subscribe("platform/#", QosEnum.QOS2);
							List<MqttBridge> mqttBridges = MqttBridgeCache.getInstance().getMqttBridges();
							for (int i = 0; i < mqttBridges.size(); i++) {
								if (mqttClient.getServerURI().contains(mqttBridges.get(i).getBusiMqtt().getIp())) {
									String nodeName = mqttBridges.get(i).getBusiMqtt().getNodeName();
									s1 = "$SYS/brokers/" + nodeName + "/clients/#";
									emqClient.subscribe(s1, QosEnum.QOS2);
								}
							}
						}
					}
					
					logger.info("MQTTSERVER==================>" + arrObj.toString());
				} catch (Exception e) {
					logger.error("FMQ获取节点数据异常！", e);
				}
			}
			
		});
		
	}

	public Map<String, Object> getMqttServerIsOnlineInfo(String brokerUrl, String nodeName) {
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
					} catch (Exception e) {
						logger.error("FMQ服务在线运行异常！", e);
					}
				}
			});
		} 
		catch (Exception e) 
		{
			logger.error("FMQ服务在线运行异常！", e);
		}
		return nodeMap;
	}
	
	public void updateMqttUserStatus() {
		logger.info("++++++++++++++++++++++++++++++++ 更新终端FMQ状态开始");
		Map<Long, MqttBridge> mqttBridgeMap = new HashMap<>();
		Collection<BusiMqttDept> values = MqttDeptMappingCache.getInstance().values();
		for (BusiMqttDept busiMqttDept : values) {
			if (MqttType.CLUSTER.getValue() == busiMqttDept.getMqttType()) {
				BusiMqttCluster busiMqttCluster = MqttClusterCache.getInstance().get(busiMqttDept.getMqttId());
				if (busiMqttCluster != null) {
					Map<Long, BusiMqttClusterMap> busiMqttClusterMaps = MqttBridgeCache.getInstance().getBusiMqttClusterMapByClusterId(busiMqttCluster.getId());
					if (busiMqttClusterMaps != null) {
						for (BusiMqttClusterMap busiMqttClusterMap : busiMqttClusterMaps.values()) {
							MqttBridge mqttBridge = MqttBridgeCache.getInstance().get(busiMqttClusterMap.getMqttId());
							if (mqttBridge != null) {
								mqttBridgeMap.put(mqttBridge.getBusiMqtt().getId(), mqttBridge);
							}
						}
					}
				}
			} else {
				MqttBridge mqttBridge = MqttBridgeCache.getInstance().get(busiMqttDept.getMqttId());
				if (mqttBridge != null) {
					mqttBridgeMap.put(mqttBridge.getBusiMqtt().getId(), mqttBridge);
				}
			}
		}

		Collection<BusiTerminal> originalTerminals = TerminalCache.getInstance().values();

		Set<String> onlineClientSet = new HashSet<>();
		for (MqttBridge mqttBridge : mqttBridgeMap.values()) {
			if (null != mqttBridge) {
				final Map<String, Integer> tempValueMap = new HashMap<>();
				tempValueMap.put("hasNext", 1);
				tempValueMap.put("page", 1);
				int limit = 5000;
				for (; tempValueMap.get("hasNext") > 0; ) {
					tempValueMap.put("hasNext", 0);
					BusiMqtt busiMqtt = mqttBridge.getBusiMqtt();
					Integer managementPort = busiMqtt.getManagementPort();
					String httpUrl = MqttConfigConstant.HTTP + busiMqtt.getIp() + MqttConfigConstant.COLON + managementPort + MqttConfigConstant.API_AND_VERSION;
					String connUrl = httpUrl + "/clients" + "?_page=" + tempValueMap.get("page") + "&_limit=" + limit;
					httpRequester.get(connUrl, new HttpResponseProcessorAdapter() {

						@Override
						public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
							try {

//							logger.info("++++++++++++++++++++++++++++++++>>> connUrl" + connUrl);
								String nodeData = getBodyContent(httpResponse);
								logger.info("++++++++++++++++++++++++++++++++>>> nodeData" + nodeData.toString());
								if (StringUtils.isNotEmpty(nodeData)) {
									JSONObject jsonObject = (JSONObject) JSONObject.parse(nodeData);
									JSONObject jsonObjectMeta = jsonObject.getJSONObject("meta");
									Boolean hasNext = jsonObjectMeta.getBoolean("hasnext");
									if (hasNext != null && hasNext) {
										tempValueMap.put("hasNext", 1);
										Integer page = jsonObjectMeta.getIntValue("page");
										tempValueMap.put("page", page + 1);
									}
									String data = jsonObject.getString(MqttConfigConstant.JSON_DATA_STR);
									JSONArray array = (JSONArray) JSONArray.parse(data);

									if (null != array && array.size() > 0) {
										for (int i = 0; i < array.size(); i++) {
											JSONObject jsonObj = (JSONObject) array.get(i);
											Boolean connect = jsonObj.getBoolean("connected");
											if (connect) {
												onlineClientSet.add(jsonObj.getString("clientid"));
											}
										}
									}
								}
							} catch (Exception e) {
								logger.error("更新FMQ用户状态失败！", e);
							}
						}
					});
				}
			}
		}
		for (BusiTerminal busiTerminal : originalTerminals) {
			if (onlineClientSet.contains(busiTerminal.getSn())) {
				if (busiTerminal.getMqttOnlineStatus() == TerminalOnlineStatus.OFFLINE.getValue()) {
					busiTerminal.setMqttOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
					busiRegisterTerminalService.updateBusiTerminalStatus(busiTerminal);
				}
			} else {
				if (busiTerminal.getMqttOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
					busiTerminal.setMqttOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
					busiRegisterTerminalService.updateBusiTerminalStatus(busiTerminal);
				}
			}
		}
		Collection<BusiSmartRoomDoorplate> busiSmartRoomDoorplates = SmartRoomDoorplateCache.getInstance().values();
		for (BusiSmartRoomDoorplate busiSmartRoomDoorplate : busiSmartRoomDoorplates) {
			if (onlineClientSet.contains(busiSmartRoomDoorplate.getSn())) {
				if (busiSmartRoomDoorplate.getMqttOnlineStatus() == null || busiSmartRoomDoorplate.getMqttOnlineStatus() == TerminalOnlineStatus.OFFLINE.getValue()) {
					busiSmartRoomDoorplate.setMqttOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
					SmartRoomDoorplateCache.getInstance().add(busiSmartRoomDoorplate);
					busiSmartRoomDoorplateMapper.updateBusiSmartRoomDoorplate(busiSmartRoomDoorplate);
				}
			} else {
				if (busiSmartRoomDoorplate.getMqttOnlineStatus() == null || busiSmartRoomDoorplate.getMqttOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
					busiSmartRoomDoorplate.setMqttOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
					SmartRoomDoorplateCache.getInstance().add(busiSmartRoomDoorplate);
					busiSmartRoomDoorplateMapper.updateBusiSmartRoomDoorplate(busiSmartRoomDoorplate);
				}
			}
		}
		Collection<BusiSmartRoomLot> busiSmartRoomLots = SmartRoomLotCache.getInstance().values();
		for (BusiSmartRoomLot busiSmartRoomLot : busiSmartRoomLots) {
			if (onlineClientSet.contains(busiSmartRoomLot.getClientId())) {
				if (busiSmartRoomLot.getMqttOnlineStatus() == null || busiSmartRoomLot.getMqttOnlineStatus() == TerminalOnlineStatus.OFFLINE.getValue()) {
					busiSmartRoomLot.setMqttOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
					SmartRoomLotCache.getInstance().add(busiSmartRoomLot);
					busiSmartRoomLotMapper.updateBusiSmartRoomLot(busiSmartRoomLot);
				}
			} else {
				if (busiSmartRoomLot.getMqttOnlineStatus() == null || busiSmartRoomLot.getMqttOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
					busiSmartRoomLot.setMqttOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
					SmartRoomLotCache.getInstance().add(busiSmartRoomLot);
					busiSmartRoomLotMapper.updateBusiSmartRoomLot(busiSmartRoomLot);
				}
			}
		}
		Collection<BusiOps> busiOpss = OpsCache.getInstance().values();
		for (BusiOps busiOps : busiOpss) {
			if (onlineClientSet.contains(busiOps.getSn())) {
				if (busiOps.getMqttOnlineStatus() == null || busiOps.getMqttOnlineStatus() == TerminalOnlineStatus.OFFLINE.getValue()) {
					busiOps.setMqttOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
					OpsCache.getInstance().add(busiOps);
					busiOpsMapper.updateBusiOps(busiOps);
				}
				Long registerLastPushTime = OpsCache.getInstance().getRegisterLastPushTime(busiOps.getId());
				if (registerLastPushTime == null || System.currentTimeMillis() - registerLastPushTime > 24 * 60 * 60000) {
					OpsPushRegisterTask opsPushRegisterTask = new OpsPushRegisterTask(String.valueOf(busiOps.getId()), 100, busiOps.getId());
					taskService.addTask(opsPushRegisterTask);
				}
			} else {
				if (busiOps.getMqttOnlineStatus() == null || busiOps.getMqttOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
					busiOps.setMqttOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
					OpsCache.getInstance().add(busiOps);
					busiOpsMapper.updateBusiOps(busiOps);
				}
			}
		}

		logger.info("++++++++++++++++++++++++++++++++ 更新终端FMQ状态结束");
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.start();
	}

	/**
	 * Callback used to run the bean.
	 *
	 * @param args incoming application arguments
	 * @throws Exception on error
	 */
	@Override
	public void run(ApplicationArguments args) throws Exception {
		//this.getUserServerInfo();
	}
}
