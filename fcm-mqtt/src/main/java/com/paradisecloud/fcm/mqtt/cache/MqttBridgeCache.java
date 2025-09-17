package com.paradisecloud.fcm.mqtt.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.ObjectUtils;

import com.paradisecloud.fcm.dao.model.BusiMqttClusterMap;
import com.paradisecloud.fcm.dao.model.BusiMqttDept;
import com.paradisecloud.fcm.mqtt.enums.MqttType;
import com.paradisecloud.fcm.mqtt.model.MqttBridge;
import com.paradisecloud.fcm.mqtt.model.MqttBridgeCluster;
import com.paradisecloud.fcm.mqtt.model.MqttBridgeCollection;

/**
 * @author zyz
 *
 */
public abstract class MqttBridgeCache {
	
	private static final MqttBridgeCache INSTANCE = new MqttBridgeCache() 
	{
		
	};
	
	//用于存放BusiMqtt对象，key是ip
	private Map<String, MqttBridge> ipToMqttBridgeMap = new ConcurrentHashMap<String, MqttBridge>();
	
	//用于存放BusiMqtt对象，key是id
	private Map<Long, MqttBridge> mqttBridgeMap = new ConcurrentHashMap<Long, MqttBridge>();
	
	//一个mqtt集群
	private Map<Long, MqttBridgeCluster> mqttBridgeClusterMap = new ConcurrentHashMap<Long, MqttBridgeCluster>();

	//一个mqtt集群Map
	private Map<Long, ConcurrentHashMap<Long, BusiMqttClusterMap>> mqttClusterMap = new ConcurrentHashMap<Long, ConcurrentHashMap<Long, BusiMqttClusterMap>>();
	
	
	public void updateMqttBridge(MqttBridge mqttBridge) 
	{
		
		ipToMqttBridgeMap.put(mqttBridge.getBusiMqtt().getIp(), mqttBridge);
		
		mqttBridgeMap.put(mqttBridge.getBusiMqtt().getId(), mqttBridge);
	}
	
	/**
	 * 根据ip获取MqttBridge对象
	 * @param ip
	 * @return
	 */
	public MqttBridge getMqttBridgeByIp(String ip) 
	{
		return ipToMqttBridgeMap.get(ip);
	}
	
	/**
	 * 移除MqttBridge对象
	 * @param ip
	 */
	public void delMqttBridgeByIp(String ip) 
	{
		ipToMqttBridgeMap.remove(ip);
	}
	
	/**
	 * 根据id获取MqttBridge对象
	 * @param id
	 * @return
	 */
	public MqttBridge getMqttBridgeById(Long id) 
	{
		return mqttBridgeMap.get(id);
	}
	
	/**
	 * 移除MqttBridge对象
	 * @param id
	 */
	public void delMqttBridge(Long id) 
	{
		mqttBridgeMap.remove(id);
	}

	public boolean mqttNodeIsUse(Long id) 
	{
		MqttBridgeCluster mqttBridgeCluster = getBusiMqttClusterById(id);
		return null != mqttBridgeCluster && !ObjectUtils.isEmpty(mqttBridgeCluster.getMqttBridges());
	}

	/**
	 * 根据id获取mqtt集群
	 * @param id
	 * @return
	 */
	public MqttBridgeCluster getBusiMqttClusterById(Long id) 
	{
		return mqttBridgeClusterMap.get(id);
	}
	
	
	
	public static MqttBridgeCache getInstance() 
	{
		return INSTANCE;
	}
	
	/**
	 * 关联一个mqtt集群关联多个节点
	 * @param busiMqttClusterMap
	 */
	public void updateMqttCluster(BusiMqttClusterMap busiMqttClusterMap) 
	{
		MqttBridge mqttBridge = mqttBridgeMap.get(busiMqttClusterMap.getMqttId());
		MqttBridgeCluster mqttBridgeCluster = getBusiMqttClusterById(busiMqttClusterMap.getClusterId());
		if(null == mqttBridgeCluster)
		{
			mqttBridgeCluster = new MqttBridgeCluster();
			mqttBridgeClusterMap.put(busiMqttClusterMap.getClusterId(), mqttBridgeCluster);
		}
		mqttBridgeCluster.addMqttBridge(mqttBridge);
		ConcurrentHashMap<Long, BusiMqttClusterMap> mqttClusters = mqttClusterMap.get(busiMqttClusterMap.getClusterId());
		if (mqttClusters == null) {
			mqttClusters = new ConcurrentHashMap<>();
			mqttClusterMap.put(busiMqttClusterMap.getClusterId(), mqttClusters);
		}
		mqttClusters.put(busiMqttClusterMap.getId(), busiMqttClusterMap);
	}

	/**
	 * 从mqtt集群删除node
	 * @param busiMqttClusterMap
	 */
	public void removeMqttCluster(BusiMqttClusterMap busiMqttClusterMap) {
		MqttBridgeCluster mqttBridgeCluster = getBusiMqttClusterById(busiMqttClusterMap.getClusterId());
		if (null != mqttBridgeCluster) 
		{
			MqttBridge mqttBridge = mqttBridgeMap.get(busiMqttClusterMap.getMqttId());
			if(null != mqttBridge) 
			{
				mqttBridgeCluster.deleteMqttBridge(mqttBridge);
			}
		}
		ConcurrentHashMap<Long, BusiMqttClusterMap> mqttClusters = mqttClusterMap.get(busiMqttClusterMap.getClusterId());
		if (mqttClusters != null) {
			mqttClusters.remove(busiMqttClusterMap.getId());
		}
	}

	/**
	 * 根据id获取mqttBridge对象
	 * @param mqttId
	 * @return
	 */
	public MqttBridge get(Long mqttId) 
	{
		return mqttBridgeMap.get(mqttId);
	}

	/**
	 * 通过租户id,获取相关的mqtt节点信息
	 * @param deptId
	 * @return
	 */
	public MqttBridgeCollection getDeptAboutMqttBridge(Long deptId) 
	{
		
		MqttBridgeCollection mqttBridgeCollection = new MqttBridgeCollection();
		BusiMqttDept busiMqttDept = MqttDeptMappingCache.getInstance().getBindMqttNode(deptId);
		if(null == busiMqttDept) 
		{
			return null;
		}
		
		// mqtt的node
        if (busiMqttDept.getMqttType().intValue() == MqttType.SINGLE_NODE.getValue())
        {
            MqttBridge mqttBridge = mqttBridgeMap.get(busiMqttDept.getMqttId());
            
            // 可用则直接添加，不可用则转备用
//            if (mqttBridge.isAvailable())
//            {
            	mqttBridgeCollection.addMqttBridge(mqttBridge);
//            }
            // 转备用
//            else
//            {
//            	mqttBridge = getSpareMqttBridge(mqttBridge);
//                if (mqttBridge != null)
//                {
//                	mqttBridgeCollection.addMqttBridge(mqttBridge);
//                }
//            }
        }
        else
        {
            // 根据集群ID获取集群下可用的mqtt集合
            this.addAvailableMqttBridge(mqttBridgeCollection, busiMqttDept.getMqttId());
        }
        
        return ObjectUtils.isEmpty(mqttBridgeCollection.getMqttBridges()) ? null : mqttBridgeCollection;
	}

	public void addAvailableMqttBridge(MqttBridgeCollection mqttBridgeCollection, Long mqttId) {
		 // 根据集群ID获取集群下可用的mqtt集合
        MqttBridgeCluster mqttBridgeCluster = getByMqttClusterId(mqttId);
        if (mqttBridgeCluster == null)
        {
            return;
        }
        
        MqttBridgeCluster bridgeCluster = mqttBridgeClusterMap.get(mqttBridgeCluster.getBusiMqttCluster().getId());
        if(null != bridgeCluster)
        {
        	List<MqttBridge> usefulMqttBridges = bridgeCluster.getMqttBridges();
            if (!ObjectUtils.isEmpty(usefulMqttBridges))
            {
            	mqttBridgeCollection.setMqttBridges(usefulMqttBridges);
            }
          
        }
	}

	/**
	 *根据集群id获取下面的所有节点
	 * @param mqttId
	 * @return
	 */
	public MqttBridgeCluster getByMqttClusterId(Long id) {
		return  new MqttBridgeCluster(MqttClusterCache.getInstance().get(id));
	}
	
	/**
	 * 获取mqtt节点的列表信息
	 * @return
	 */
	public List<MqttBridge> getMqttBridges() 
	{
		return new ArrayList<MqttBridge>(mqttBridgeMap.values());
		
	}

	/**
	 * 获取集群id下面所有节点
	 * @param clusterId
	 * @return
	 */
	public Map<Long, BusiMqttClusterMap> getBusiMqttClusterMapByClusterId(long clusterId) {
		return mqttClusterMap.get(clusterId);
	}

}
