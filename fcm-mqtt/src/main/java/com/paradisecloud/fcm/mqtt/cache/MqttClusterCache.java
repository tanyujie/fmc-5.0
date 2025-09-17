package com.paradisecloud.fcm.mqtt.cache;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.dao.model.BusiMqttCluster;

public class MqttClusterCache extends JavaCache<Long, BusiMqttCluster> 
{

	private static final long serialVersionUID = 1L;
	private static final MqttClusterCache INSTANCE = new MqttClusterCache();
	
	
	public MqttClusterCache() 
	{
		super();
	}
	
	public static MqttClusterCache getInstance() 
	{
		return INSTANCE;
		
	}
	
	
}
