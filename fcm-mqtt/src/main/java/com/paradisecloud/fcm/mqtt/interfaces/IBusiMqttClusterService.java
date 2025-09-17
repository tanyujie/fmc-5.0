package com.paradisecloud.fcm.mqtt.interfaces;

import java.util.List;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiMqttCluster;

/**
 * mqtt集群接口
 * 
 * @author zyz
 * @date 2021-07-21
 */
public interface IBusiMqttClusterService 
{
    /**
             * 查询具体的mqtt集群信息
     * 
     * @param id 
     * @return BusiMqttCluster
     */
    public BusiMqttCluster selectBusiMqttClusterById(Long id);

    /**
             * 查询mqtt集群信息列表
     * 
     * @param busiMqttCluster 
     * @return List<BusiMqttCluster>
     */
    public List<BusiMqttCluster> selectBusiMqttClusterList(BusiMqttCluster busiMqttCluster);

    /**
             * 新增mqtt集群信息
     * 
     * @param busiMqttCluster 
     * @return int
     */
    public void insertBusiMqttCluster(BusiMqttCluster busiMqttCluster);

    /**
             * 修改mqtt集群信息
     * 
     * @param busiMqttCluster
     * @return int
     */
    public void updateBusiMqttCluster(BusiMqttCluster busiMqttCluster);

    /**
             * 批量删除mqtt集群信息
     * 
     * @param ids
     * @return int
     */
    public int deleteBusiMqttClusterByIds(Long[] ids);

    /**
            * 删除mqtt集群信息
     * 
     * @param id 
     * @return int
     */
    public void deleteBusiMqttClusterById(Long id);

	/**
	 * 得到mqtt的集群消息
	 * @return
	 */
	public List<ModelBean> getAllMqttCluster();
}
