package com.paradisecloud.fcm.mqtt.client;

import javax.annotation.PreDestroy;
import javax.net.ssl.*;

import com.paradisecloud.fcm.dao.model.BusiMqtt;
import com.paradisecloud.fcm.mqtt.cache.MqttBridgeCache;
import com.paradisecloud.fcm.mqtt.model.MqttBridge;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.mqtt.enums.QosEnum;
import com.paradisecloud.fcm.mqtt.model.MqttProperties;
import com.sinhy.exception.SystemException;

import java.io.InputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author zyz
 *
 */
@Component
public class EmqClient 
{
	
	private static final Logger LOG = LoggerFactory.getLogger(EmqClient.class);
	
	private IMqttClient mqttClient;
	
	@Autowired
	private MqttCallback mqttCallback;
	
	
	public void init(MqttProperties properties, BusiMqtt busiMqtt)
	{
		MqttClientPersistence memoryPersistence = new MemoryPersistence();
		
		try 
		{
			String brokerUrl = "";
			if (busiMqtt.getUseSsl() == 1) {
				brokerUrl = MqttConfigConstant.SSL + busiMqtt.getIp() + MqttConfigConstant.COLON + MqttConfigConstant.DEFAULT_SSL_TCP_PORT;
			} else {
				brokerUrl = MqttConfigConstant.TCP + busiMqtt.getIp() + MqttConfigConstant.COLON + busiMqtt.getTcpPort();
			}
			mqttClient = new MqttClient(brokerUrl, properties.getClientId(), memoryPersistence);
		} 
		catch (MqttException e) 
		{
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "客户端初始化mqttClient对象失败,errorMsg={"+e.getMessage()+"},borkerUrl={"+properties.getBrokerUrl()+"},clientId={"+properties.getClientId()+"}");
		}
	}
	
	/**
	 * 连接broker
	 * @param mqttProperties
	 */
	public Boolean connect(MqttProperties mqttProperties) 
	{
		MqttBridge mqttBridge = MqttBridgeCache.getInstance().getMqttBridgeByIp(mqttProperties.getBrokerUrl());
		BusiMqtt busiMqtt = mqttBridge.getBusiMqtt();
		Boolean connectSuccessFlg = false;
		init(mqttProperties, busiMqtt);
		MqttConnectOptions connectOptions = new MqttConnectOptions();
		
		//是否启动重连
//		connectOptions.setAutomaticReconnect(true);
		connectOptions.setConnectionTimeout(5);
		connectOptions.setKeepAliveInterval(MqttConfigConstant.KEEP_ALIVE);
		connectOptions.setUserName(mqttProperties.getUserName());
		connectOptions.setPassword(mqttProperties.getPassword().toCharArray());
		connectOptions.setCleanSession(true);
		connectOptions.setMqttVersion(MqttConfigConstant.MQTT_VERSION);
		connectOptions.setMaxInflight(MqttConfigConstant.MAX_INFLIGHT);
		
		mqttClient.setCallback(mqttCallback);
		
		try {
			if (busiMqtt.getUseSsl() == 1) {
				connectOptions.setUserName(MqttConfigConstant.DEFAULT_SSL_USER_NAME);
				connectOptions.setPassword(MqttConfigConstant.DEFAULT_SSL_PASSWORD.toCharArray());
				connectOptions.setSocketFactory(getSslSocketFactory());
				connectOptions.setHttpsHostnameVerificationEnabled(false);
				connectOptions.setSSLHostnameVerifier(new HostnameVerifier() {
					@Override
					public boolean verify(String s, SSLSession sslSession) {
						return true;
					}
				});
			}
			mqttClient.connect(connectOptions);
			LOG.info(mqttProperties.getUserName()+"用户===========连接EMQX BROKER服务器成功=========="+mqttProperties.getBrokerUrl());
			connectSuccessFlg = true;
		} 
		catch (MqttSecurityException e) 
		{
			connectSuccessFlg = false;
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "mqtt客户端连接服务端失败" + e.getMessage());
		} 
		catch (MqttException e) 
		{
			connectSuccessFlg = false;
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "mqtt客户端连接服务端失败" + e.getMessage());
		}
		catch (Exception e) {
			connectSuccessFlg = false;
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "mqtt客户端连接服务端失败" + e.getMessage());
		}
		
		return connectSuccessFlg;
	}
	
	/**
	 * 断开连接
	 * @PreDestroy:客户端应用程序退出之前，自动的与服务端断开连接
	 */
	@PreDestroy
	public void disConnect() 
	{
		try 
		{
			mqttClient.disconnect();
		} 
		catch (MqttException e) 
		{
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "断开连接产生异常，异常信息" + e.getMessage());
		}
	}
	
	/**
	 * 客户端重新连接
	 * @return 
	 */
	public Boolean reConnect() 
	{
		Boolean reConnect = false;
		try 
		{
			mqttClient.reconnect();
			reConnect = true;
		} 
		catch (MqttException e) 
		{
			reConnect = false;
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "客户端重新连接，失败原因" + e.getMessage());
		}
		return reConnect;
	}
	
	
	/**
	 * 客户端发布
	 * @param topic
	 * @param msg
	 * @param qos
	 * @param retain
	 */
	public void publish(String topic, String msg, QosEnum qos, boolean retain) 
	{
		MqttMessage mqttMessage = new MqttMessage();
		mqttMessage.setPayload(msg.getBytes());
		mqttMessage.setQos(qos.value());
		mqttMessage.setRetained(retain);
		
		try {
			mqttClient.publish(topic, mqttMessage);
			LOG.info("用户发布主题"+topic+"成功,=========消息内容是"+msg);
		} 
		catch (MqttPersistenceException e) 
		{
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "发布消息失败" + e.getMessage());
		} 
		catch (MqttException e) 
		{
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "发布消息失败" + e.getMessage());
		}
	}

	/**
	 * 客户端发布
	 * @param topic
	 * @param msg
	 * @param qos
	 * @param retain
	 */
	public void publish(String topic, byte[] msg, QosEnum qos, boolean retain)
	{
		MqttMessage mqttMessage = new MqttMessage();
		mqttMessage.setPayload(msg);
		mqttMessage.setQos(qos.value());
		mqttMessage.setRetained(retain);

		try {
			mqttClient.publish(topic, mqttMessage);
			LOG.info("用户发布主题"+topic+"成功,=========消息内容是"+new String(msg));
		}
		catch (MqttPersistenceException e)
		{
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "发布消息失败" + e.getMessage());
		}
		catch (MqttException e)
		{
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "发布消息失败" + e.getMessage());
		}
	}
	
	/**
	 * 客户端订阅
	 * @param topicFilter
	 * @param qos
	 */
	public void subscribe(String topicFilter,QosEnum qos) 
	{
		try 
		{
			int qos2 = qos.value();
			String[] topicFilters = {topicFilter};
			int[] qosV = {qos2};
			mqttClient.subscribe(topicFilters, qosV);
			LOG.info("用户订阅主题"+topicFilter+"成功!");
		} 
		catch (MqttException e) 
		{
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "订阅主题失败" + e.getMessage());
		}
	}
	
	/**
	 * 客户端取消订阅
	 * @param topicFilter
	 */
	public void unSubscribe(String topicFilter) 
	{
		try 
		{
			mqttClient.unsubscribe(topicFilter);
		} 
		catch (MqttException e) 
		{
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "取消订阅失败" + e.getMessage());
		}
	}

	public IMqttClient getMqttClient() 
	{
		return mqttClient;
	}

	public void setMqttClient(IMqttClient mqttClient) 
	{
		this.mqttClient = mqttClient;
	}

	public SSLSocketFactory getSslSocketFactory() throws Exception {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("fmq.jks");
		KeyStore keyStore = KeyStore.getInstance("JKS");
		keyStore.load(inputStream, "fmq123456".toCharArray());

		TrustManager[] tm = new TrustManager[]{new X509ExtendedTrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {

			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {

			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {

			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {

			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		}};

		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, tm, new SecureRandom());

		return sslContext.getSocketFactory();
	}
	
}
