package com.paradisecloud.fcm.mqtt.impls;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.paradisecloud.fcm.mqtt.enums.MqttBridgeStatus;
import com.paradisecloud.fcm.mqtt.model.MqttBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.dao.mapper.BusiMqttClusterMapMapper;
import com.paradisecloud.fcm.dao.model.BusiMqttClusterMap;
import com.paradisecloud.fcm.mqtt.cache.MqttBridgeCache;
import com.paradisecloud.fcm.mqtt.common.ResponseTerminal;
import com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.mqtt.constant.MqttHealthConstant;
import com.paradisecloud.fcm.mqtt.constant.MqttTipConstant;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiMqttClusterMapService;
import com.paradisecloud.fcm.mqtt.interfaces.IEmqxBrokerHealthService;
import com.sinhy.exception.SystemException;

/**
 * mqtt集群关联
 * 
 * @author zyz
 * @date 2021-07-21
 */
@Service
public class BusiMqttClusterMapServiceImpl implements IBusiMqttClusterMapService 
{	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BusiMqttClusterMapServiceImpl.class);
	
    @Autowired
    private BusiMqttClusterMapMapper busiMqttClusterMapMapper;
    
    @Autowired
    private IEmqxBrokerHealthService emqxBrokerHealthService;

    /**
     * 查询具体mqtt集群关联信息
     * 
     * @param id 
     * @return BusiMqttClusterMap
     */
    @Override
    public BusiMqttClusterMap selectBusiMqttClusterMapById(Long id)
    {
        return busiMqttClusterMapMapper.selectBusiMqttClusterMapById(id);
    }

    /**
     * 查询mqtt集群关联信息
     * 
     * @param busiMqttClusterMap
     * @return List<BusiMqttClusterMap>
     */
    @Override
    public List<ModelBean> selectBusiMqttClusterMapList(BusiMqttClusterMap busiMqttClusterMap, HttpServletRequest request, HttpServletResponse response)
    {
    	List<ModelBean> modelBeans = new ArrayList<ModelBean>();
    	List<BusiMqttClusterMap> busiMqttClusterMapList = busiMqttClusterMapMapper.selectBusiMqttClusterMapList(busiMqttClusterMap);
    	for (BusiMqttClusterMap mqttClusterMap : busiMqttClusterMapList) 
    	{
			ModelBean modelBean = new ModelBean(mqttClusterMap);
			MqttBridge mqttBridge = MqttBridgeCache.getInstance().get(mqttClusterMap.getMqttId());
			ModelBean mb = new ModelBean(mqttBridge.getBusiMqtt());
			Long id = (Long)mb.get(MqttConfigConstant.ID);
			String ipAddr = (String)mb.get(MqttConfigConstant.IP);
			Integer managementPort = (Integer)mb.get(MqttConfigConstant.MANAGEMENT_PORT);
			Integer tcpPort = (Integer)mb.get(MqttConfigConstant.TCP_PORT);
			if (mqttBridge.getBusiMqtt().getUseSsl() != null && mqttBridge.getBusiMqtt().getUseSsl() == 1) {
				tcpPort = MqttConfigConstant.DEFAULT_SSL_TCP_PORT;
			}
			String nodeName = (String) mb.get(MqttConfigConstant.EMQX_NODE_NAME);
			String httpUrl = MqttConfigConstant.HTTP + ipAddr + MqttConfigConstant.COLON + managementPort + MqttConfigConstant.API_AND_VERSION;
			Boolean isSuccess = ResponseTerminal.getInstance().pingIpAndPort(ipAddr, tcpPort);
			if (isSuccess) 
			{
//				Map<String, Object> emqxBrokerHealth = emqxBrokerHealthService.getMqttServerIsOnlineInfo(httpUrl, nodeName, response);
				MqttBridgeStatus mqttBridgeStatus = mqttBridge.getMqttBridgeStatus();
				if (mqttBridgeStatus == MqttBridgeStatus.AVAILABLE) {
					mb.put(MqttConfigConstant.MQTT_STATUS_STR, TerminalOnlineStatus.ONLINE.getValue());
				} else {
					mb.put(MqttConfigConstant.MQTT_STATUS_STR, TerminalOnlineStatus.OFFLINE.getValue());
				}
			}
			else 
			{
				mb.put(MqttConfigConstant.MQTT_STATUS_STR, TerminalOnlineStatus.OFFLINE.getValue());
			}
			
			mb.remove("id");
			mb.remove("createTime");
			mb.remove("updateTime");
			mb.remove("username");
			mb.remove("password");
			modelBean.putAll(mb);
			modelBeans.add(modelBean);
		}
    	return modelBeans;
    }

    /**
     * 新增mqtt集群关联信息
     * 
     * @param busiMqttClusterMap
     * @return int
     */
    @Override
    public int insertBusiMqttClusterMap(BusiMqttClusterMap busiMqttClusterMap)
    {
        try {
        	 busiMqttClusterMap.setCreateTime(new Date());
        	 busiMqttClusterMap.setWeight(busiMqttClusterMap.getWeight());
             int clu = busiMqttClusterMapMapper.insertBusiMqttClusterMap(busiMqttClusterMap);
             if(clu > 0)
             {
             	MqttBridgeCache.getInstance().updateMqttCluster(busiMqttClusterMap);
             }
             return clu;
		} catch (Throwable e) {
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_F_F_F, MqttTipConstant.CLUSTER_SAME_NODE_TIP);
		}
    }

    /**
     * 修改mqtt集群关联表信息
     * 
     * @param busiMqttClusterMap
     * @return int
     */
    @Override
    public int updateBusiMqttClusterMap(BusiMqttClusterMap busiMqttClusterMap)
    {
    	BusiMqttClusterMap mqttClusterMap = selectBusiMqttClusterMapById(busiMqttClusterMap.getId());
        busiMqttClusterMap.setUpdateTime(new Date());
        int cl = busiMqttClusterMapMapper.updateBusiMqttClusterMap(busiMqttClusterMap);
        if(cl > 0) 
        {
        	MqttBridgeCache.getInstance().updateMqttCluster(mqttClusterMap);
        }
        return cl;
    }

    /**
     * 批量删除mqtt集群关联信息
     * 
     * @param ids
     * @return int
     */
    @Override
    public int deleteBusiMqttClusterMapByIds(Long[] ids)
    {
        return busiMqttClusterMapMapper.deleteBusiMqttClusterMapByIds(ids);
    }

    /**
     *删除mqtt集群关联信息
     * 
     * @param id 
     * @return int
     */
    @Override
    public int deleteBusiMqttClusterMapById(Long id)
    {	
    	BusiMqttClusterMap busiMqttClusterMap = selectBusiMqttClusterMapById(id);
    	int cl = busiMqttClusterMapMapper.deleteBusiMqttClusterMapById(id);
    	if(cl > 0) 
    	{
    		MqttBridgeCache.getInstance().removeMqttCluster(busiMqttClusterMap);
    	}
        return cl;
    }
}
