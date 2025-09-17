package com.paradisecloud.fcm.ops.cloud.mqtt.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.PurchaseType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.dao.mapper.BusiMcuTencentConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuTencentTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentTemplateConference;
import com.paradisecloud.fcm.dao.model.vo.BusiOpsResourceVo;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.ops.cloud.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.cache.BaseWebSocketMessagePusher;
import com.paradisecloud.fcm.service.model.CloudConference;
import com.paradisecloud.fcm.service.ops.OpsDataCache;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContext;
import com.paradisecloud.fcm.tencent.cache.TencentWebSocketMessagePusher;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiTencentConferenceService;
import com.paradisecloud.fcm.terminal.fs.util.SpringContextUtil;
import com.sinhy.spring.BeanFactory;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author zyz
 *
 */
public class OpsMessageCallback implements MqttCallback
{
	
	private static final Logger LOG = LoggerFactory.getLogger(MqttCallback.class);
	
	/**
	 * 当客户端丢失了服务端的连接之后，触发的回调函数
	 */
	public void connectionLost(Throwable cause) 
	{
		LOG.error("当客户端丢失了服务端的连接之后，触发的回调函数=====> {}" , cause.getCause());
		OpsEmqClient emqClient = (OpsEmqClient) SpringContextUtil.getBean("opsEmqClient");
		emqClient.close();
	}
	

	/**
	 * 当订阅者收到消息了，消息到达了上层应用(触发的回调)
	 * 注意：该方法由mqtt客户端同步调用，在此方法未正确返回之前，客户端不会发送ack确认消息到broker
	 * 一旦该方法向外抛出了异常，客户端会异常关闭，当再次连接时，所有Qos1,Qos2且客户端未进行ack确认的消息,都将由broker服务器再次发送到客户端
	 */
	public void messageArrived(String topic, MqttMessage message) throws Exception 
	{
		LOG.info("订阅到了消息;topic={},messageid={},qos={},msg={}",topic,message.getId(),message.getQos(),new String(message.getPayload()));

		JSONObject jsonObject = JSONObject.parseObject(new String(message.getPayload()));
		String action = jsonObject.getString(MqttConfigConstant.ACTION);
		String clientId = jsonObject.getString(MqttConfigConstant.CLIENTID);
		String messageId = jsonObject.getString(MqttConfigConstant.MESSAGE_ID);
		JSONObject jsonS = jsonObject.getJSONObject(MqttConfigConstant.JSON_DATA_STR);

		switch (action) {
			case "register": {
				processRegister(jsonS, clientId, messageId);
				break;
			}
			case "endConference": {
				processEndConference(jsonS, clientId, messageId);
				break;
			}
			default: {
				break;
			}
		}
	}
	
	/**
	 * 针对的是发布者，发布者发布消息完成之后，产生的方法回调
	 * 不同的Qos，认定发布完成的时机，是不一样的
	 * Qos0:消息被网络发出后触发一次
	 * Qos1:当收到broker的puback消息后触发
	 * Qos2:当收到broker的pubcom消息后触发
	 */
	@Override
	public void deliveryComplete(IMqttDeliveryToken token)
	{
		int messageId = token.getMessageId();
//		int inFlightMessageCount = token.getClient().getInFlightMessageCount();
//		IMqttDeliveryToken[] bufferedMessageCount = token.getClient().getPendingDeliveryTokens();
//		String clientId = token.getClient().getClientId();
//		try 
//		{
//			JSONObject jSONObject = (JSONObject)JSONObject.parse(new String(token.getMessage().getPayload()));
//			String conferenceNum = (String)jSONObject.get("conferenceNum");
//			Integer action = (Integer)jSONObject.get("action");
//			直播申请入会或者会议发言
//			terminalActionService.terminalLiveLaunchAction(clientId, conferenceNum, action);
			LOG.info("发布者消息发送成功之后,messageId={}", messageId);
//		} 
//		catch (MqttException e) 
//		{
//			e.printStackTrace();
//		}
	}

	private void processRegister(JSONObject jsonS, String clientId, String messageId) {
		Boolean registered = jsonS.getBoolean("registered");
		if (registered == null) {
			registered = false;
		}
		String cloudToken = null;
		if (registered) {
			cloudToken = jsonS.getString("cloudToken");
		}
		List<BusiOpsResourceVo> resources = new ArrayList<>();
		JSONArray resourcesJsonArray = jsonS.getJSONArray("resources");
		if (resourcesJsonArray != null) {
			for (int i = 0; i < resourcesJsonArray.size(); i++) {
				BusiOpsResourceVo resource = resourcesJsonArray.getObject(i, BusiOpsResourceVo.class);
				resources.add(resource);
			}
		}

		OpsDataCache.getInstance().setRegistered(registered);
		OpsDataCache.getInstance().setCloudToken(cloudToken);
		OpsDataCache.getInstance().setResources(resources);
		int cloudLiveTime = 0;
		int subtitlesTime = 0;
		int tencentTime = 0;
		int imTime = 0;
		for (BusiOpsResourceVo busiOpsResource : resources) {
			if (PurchaseType.LIVE.getCode().equals(busiOpsResource.getPurchaseType())) {
				cloudLiveTime = -1;
			} else if (PurchaseType.SUBTITLES.getCode().equals(busiOpsResource.getPurchaseType())) {
				subtitlesTime = -1;
			} else if (PurchaseType.MEETING.getCode().equals(busiOpsResource.getPurchaseType())) {
				if (McuType.MCU_TENCENT.getCode().equals(busiOpsResource.getMcuType())) {
					tencentTime = -1;
				}
			} else if (PurchaseType.IM.getCode().equals(busiOpsResource.getPurchaseType())) {
				imTime = -1;
			}
		}
		OpsDataCache.getInstance().setCloudLiveTime(cloudLiveTime);
		OpsDataCache.getInstance().setAsrTime(subtitlesTime);
		OpsDataCache.getInstance().setTencentTime(tencentTime);
		OpsDataCache.getInstance().setImTime(imTime);
	}

	private void processEndConference(JSONObject jsonS, String clientId, String messageId) {
		String conferenceId = jsonS.getString("conferenceId");
		if (StringUtils.isNotEmpty(conferenceId)) {
			ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
			Long id = conferenceIdVo.getId();
			McuType mcuType = conferenceIdVo.getMcuType();
			switch (mcuType) {
				case MCU_TENCENT: {
					BusiMcuTencentConferenceAppointmentMapper conferenceAppointmentMapper = BeanFactory.getBean(BusiMcuTencentConferenceAppointmentMapper.class);
					BusiMcuTencentTemplateConferenceMapper busiMcuTencentTemplateConferenceMapper = BeanFactory.getBean(BusiMcuTencentTemplateConferenceMapper.class);
					BusiMcuTencentConferenceAppointment busiMcuTencentConferenceAppointmentCon = new BusiMcuTencentConferenceAppointment();
					busiMcuTencentConferenceAppointmentCon.setIsCloudConference(1);
					busiMcuTencentConferenceAppointmentCon.setCloudConferenceId(conferenceId);
					List<BusiMcuTencentConferenceAppointment> busiMcuTencentConferenceAppointmentList = conferenceAppointmentMapper.selectBusiMcuTencentConferenceAppointmentList(busiMcuTencentConferenceAppointmentCon);
					if (busiMcuTencentConferenceAppointmentList.size() > 0) {
						BusiMcuTencentConferenceAppointment busiMcuTencentConferenceAppointment = busiMcuTencentConferenceAppointmentList.get(0);
						BusiMcuTencentTemplateConference busiMcuTencentTemplateConference = busiMcuTencentTemplateConferenceMapper.selectBusiMcuTencentTemplateConferenceById(busiMcuTencentConferenceAppointment.getId());
						if (busiMcuTencentTemplateConference != null) {
							if (busiMcuTencentTemplateConference.getUpCascadeId() != null) {
								String contextKey = EncryptIdUtil.generateContextKey(busiMcuTencentTemplateConference.getUpCascadeId(), busiMcuTencentTemplateConference.getUpCascadeMcuType());
								BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
								if (baseConferenceContext != null) {
									HashMap hashMap = new HashMap();
									hashMap.put("conferenceId", baseConferenceContext.getId());
									hashMap.put("isCloudConference", true);
									hashMap.put("downCascadeConferenceId", conferenceId);
									BaseWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(baseConferenceContext.getId(), WebsocketMessageType.DOWN_CASCADE_CONFERENCE_REMOVED, hashMap);
									if (baseConferenceContext instanceof ConferenceContext) {
										ConferenceContext conferenceContext = (ConferenceContext) baseConferenceContext;
										if (conferenceContext.getCloudConferenceList() != null) {
											CloudConference cloudConferenceNeedRemove = null;
											for (CloudConference cloudConference : conferenceContext.getCloudConferenceList()) {
												if (conferenceId.equals(cloudConference.getCascadeConferenceId())) {
													cloudConferenceNeedRemove = cloudConference;
													break;
												}
											}
											if (cloudConferenceNeedRemove != null) {
												conferenceContext.getCloudConferenceList().remove(cloudConferenceNeedRemove);
											}
										}
									}
								}
							}
						}

						BaseWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, WebsocketMessageType.CONFERENCE_ENDED, "会议已结束");
						BaseWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, WebsocketMessageType.MESSAGE_TIP, "会议已结束");

					}
					break;
				}
			}
		}
	}

}
