package com.paradisecloud.fcm.mqtt.cache;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.dao.model.BusiMqttDept;
import com.paradisecloud.fcm.mqtt.enums.MqttType;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;

public class MqttDeptMappingCache extends JavaCache<Long, BusiMqttDept> {

	private static final long serialVersionUID = 1L;
	private static final MqttDeptMappingCache INSTANCE = new MqttDeptMappingCache();
	
	public MqttDeptMappingCache() 
	{
		super();
	}
	
	public static MqttDeptMappingCache getInstance() 
	{
		return INSTANCE;
		
	}

	public BusiMqttDept getBindMqttNode(Object key) 
	{
		
		BusiMqttDept busiMqttDept = super.get(key);
		if(null == busiMqttDept) 
		{
			SysDept sysDept = SysDeptCache.getInstance().get(key);
			if(null != sysDept.getParentId() && sysDept.getParentId().longValue() > 0) 
			{
				return getBindMqttNode(sysDept.getParentId());
			}
			else
			{
				return null;
			}
			
		}
		return busiMqttDept;
	}
	
	
	/**
	 * 根据deptId，获取对象
	 * @param key
	 * @return
	 */
	public BusiMqttDept getBindDeptMqtt(Object key) 
	{
		return super.get(key);
	}

	/**
	 * mqtt绑定租户的数量
	 * @param singleNode
	 * @param id
	 * @return
	 */
	public Object getBindMqttNodeCount(MqttType mqttType, Long id) {
		int count = 0;
		for (BusiMqttDept busiMqttDept : values()) {
			if(MqttType.convert(busiMqttDept.getMqttType()) == mqttType && busiMqttDept.getMqttId() == id)
			{
				count++;
			}
		}
		
		return count;
	}
	
	
}
