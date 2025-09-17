package com.paradisecloud.fcm.ops.cloud.mqtt.common;

import com.paradisecloud.fcm.ops.cloud.mqtt.client.OpsEmqClient;
import com.paradisecloud.fcm.ops.cloud.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.ops.cloud.mqtt.enums.QosEnum;
import com.paradisecloud.fcm.ops.cloud.mqtt.utils.MqttThreadPool;
import com.paradisecloud.fcm.terminal.fs.util.SpringContextUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PublishMessage {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PublishMessage.class);
	
	private static final PublishMessage INSTANCE = new PublishMessage()
	{
		
	};
		
	public static PublishMessage getInstance()
	{
		return INSTANCE;
	}

	public void publishTopicMsg(String topic, String clientId, String msg, boolean flag) {
		if(!MqttThreadPool.isShutDown())
		{
			MqttThreadPool.exec(new Runnable() 
			{
				
				@Override
				public void run() 
				{
					if(StringUtils.isNotEmpty(topic))
					{
						String[] topicArr = topic.split(MqttConfigConstant.SLASH);
						if(topicArr.length >= 1) 
						{
							try {
								OpsEmqClient emqClient = (OpsEmqClient) SpringContextUtil.getBean("opsEmqClient");
								if(null != emqClient) {
									emqClient.publish(topic, msg, QosEnum.QOS2, flag);
								}
							} catch (Exception e) {
								LOGGER.error("发布主题失败!" , e);
							}
						}
					}
				}
			});
		}
	}

	public void publishTopicMsg(String topic, byte[] msg, boolean flag) {
		if(!MqttThreadPool.isShutDown())
		{
			MqttThreadPool.exec(new Runnable()
			{

				@Override
				public void run()
				{
					if(StringUtils.isNotEmpty(topic))
					{
						String[] topicArr = topic.split(MqttConfigConstant.SLASH);
						if(topicArr.length >= 1)
						{
							try {
								OpsEmqClient emqClient = (OpsEmqClient) SpringContextUtil.getBean("opsEmqClient");
								if(null != emqClient) {
									emqClient.publish(topic, msg, QosEnum.QOS2, flag);
								}
							} catch (Exception e) {
								LOGGER.error("发布主题失败!" , e);
							}
						}
					}
				}
			});
		}
	}
	
}
