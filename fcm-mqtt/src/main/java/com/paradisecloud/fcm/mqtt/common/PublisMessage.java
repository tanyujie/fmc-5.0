package com.paradisecloud.fcm.mqtt.common;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paradisecloud.fcm.mqtt.client.EmqClient;
import com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.mqtt.enums.QosEnum;
import com.paradisecloud.fcm.mqtt.utils.MqttThreadPool;
import com.paradisecloud.fcm.terminal.fs.util.SpringContextUtil;

public abstract class PublisMessage {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PublisMessage.class);
	
	private static final PublisMessage INSTANCE = new PublisMessage() 
	{
		
	};
		
	public static PublisMessage getInstance() 
	{
		return INSTANCE;
	}

	public void publishTopicMsg(String terminalTopic, String clientId, String msg, boolean flag) {
		if(!MqttThreadPool.isShutDown()) 
		{
			MqttThreadPool.exec(new Runnable() 
			{
				
				@Override
				public void run() 
				{
					if(StringUtils.isNotEmpty(terminalTopic)) 
					{
						String[] topicArr = terminalTopic.split(MqttConfigConstant.SLASH);
						if(topicArr.length >= 1) 
						{
							try {
								EmqClient emqClient = (EmqClient) SpringContextUtil.getBean("emqClient");
								if(null != emqClient) {
									emqClient.publish(terminalTopic, msg, QosEnum.QOS2, flag);
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
								EmqClient emqClient = (EmqClient) SpringContextUtil.getBean("emqClient");
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
