package com.paradisecloud.fcm.mqtt.impls;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.mapper.BusiMqttClusterMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMqttDeptMapper;
import com.paradisecloud.fcm.dao.model.BusiMqttCluster;
import com.paradisecloud.fcm.dao.model.BusiMqttDept;
import com.paradisecloud.fcm.mqtt.cache.MqttBridgeCache;
import com.paradisecloud.fcm.mqtt.cache.MqttClusterCache;
import com.paradisecloud.fcm.mqtt.cache.MqttDeptMappingCache;
import com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.mqtt.constant.MqttTipConstant;
import com.paradisecloud.fcm.mqtt.enums.MqttType;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiMqttClusterService;
import com.sinhy.exception.SystemException;

/**
 * mqtt集群业务层处理
 * 
 * @author zyz
 * @date 2021-07-21
 */
@Service
public class BusiMqttClusterServiceImpl implements IBusiMqttClusterService 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(BusiMqttClusterServiceImpl.class);
	
    @Autowired
    private BusiMqttClusterMapper busiMqttClusterMapper;
    
    @Autowired
    private BusiMqttDeptMapper busiMqttDeptMapper;

    /**
             * 查询具体mqtt集群的信息
     * 
     * @param id 
     * @return BusiMqttCluster
     */
    @Override
    public BusiMqttCluster selectBusiMqttClusterById(Long id)
    {
        return busiMqttClusterMapper.selectBusiMqttClusterById(id);
    }

    /**
             * 查询mqtt集群列表
     * 
     * @param busiMqttCluster 
     * @return List<BusiMqttCluster>
     */
    @Override
    public List<BusiMqttCluster> selectBusiMqttClusterList(BusiMqttCluster busiMqttCluster)
    {
        return busiMqttClusterMapper.selectBusiMqttClusterList(busiMqttCluster);
    }

    /**
            * 新增mqtt集群信息
     * 
     * @param busiMqttCluster
     * @return int
     */
    @Override
    public void insertBusiMqttCluster(BusiMqttCluster busiMqttCluster)
    {
        busiMqttCluster.setCreateTime(new Date());
        busiMqttClusterMapper.insertBusiMqttCluster(busiMqttCluster);
        MqttClusterCache.getInstance().put(busiMqttCluster.getId(), busiMqttCluster);
    }

    /**
             * 修改mqtt集群信息
     * 
     * @param busiMqttCluster
     * @return int
     */
    @Override
    public void updateBusiMqttCluster(BusiMqttCluster busiMqttCluster)
    {
        busiMqttCluster.setUpdateTime(new Date());
        busiMqttClusterMapper.updateBusiMqttCluster(busiMqttCluster);
        MqttClusterCache.getInstance().put(busiMqttCluster.getId(), busiMqttClusterMapper.selectBusiMqttClusterById(busiMqttCluster.getId()));
    }

    /**
             * 批量删除mqtt集群信息
     * 
     * @param ids 
     * @return int
     */
    @Override
    public int deleteBusiMqttClusterByIds(Long[] ids)
    {
        return busiMqttClusterMapper.deleteBusiMqttClusterByIds(ids);
    }

    /**
             * 删除mqtt集群信息
     * 
     * @param id 
     * @return int
     */
    @Override
    public void deleteBusiMqttClusterById(Long id)
    {
    	if(MqttBridgeCache.getInstance().mqttNodeIsUse(id)) 
    	{
    		throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_ONE_TH, "FMQ集群的删除,需要先删除集群下的所有节点，才能在删除mqtt集群!");
    	}
    	
    	BusiMqttDept busiMqttDept = new BusiMqttDept();
    	busiMqttDept.setMqttId(id);
    	busiMqttDept.setMqttType(MqttType.CLUSTER.getValue());
    	List<BusiMqttDept> busiMqttDeptList = busiMqttDeptMapper.selectBusiMqttDeptList(busiMqttDept);
    	if(!ObjectUtils.isEmpty(busiMqttDeptList)) 
    	{
    		throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_ONE_SIX, MqttTipConstant.MQTT_CLUSTER_TENANT_TIP);
    	}
    	
    	int cl = busiMqttClusterMapper.deleteBusiMqttClusterById(id);
    	
    	if(cl > 0) {
    		MqttClusterCache.getInstance().remove(id);
    		LOGGER.info("删除FMQ集群成功, id:{}",id);
    	}
    }

	@Override
	public List<ModelBean> getAllMqttCluster() {
		List<BusiMqttCluster> gs = new ArrayList<>(MqttClusterCache.getInstance().values());
        List<ModelBean> ms = new ArrayList<>();
        for (BusiMqttCluster busiMqttCluster : gs)
        {
            ModelBean m = new ModelBean(busiMqttCluster);
            m.put("bindTenantCount", MqttDeptMappingCache.getInstance().getBindMqttNodeCount(MqttType.CLUSTER, busiMqttCluster.getId()));
            ms.add(m);
        }
        return ms;
	}
}
