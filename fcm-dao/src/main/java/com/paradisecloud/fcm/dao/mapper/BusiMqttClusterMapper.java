package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiMqttCluster;

/**
 * Mapper接口
 * 
 * @author zyz
 * @date 2021-07-21
 */
public interface BusiMqttClusterMapper 
{
    /**
            * 查询mqtt集群信息
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
    public int insertBusiMqttCluster(BusiMqttCluster busiMqttCluster);

    /**
             * 修改mqtt集群信息
     * 
     * @param busiMqttCluster 
     * @return int
     */
    public int updateBusiMqttCluster(BusiMqttCluster busiMqttCluster);

    /**
            * 删除mqtt集群信息
     * 
     * @param id 
     * @return int
     */
    public int deleteBusiMqttClusterById(Long id);

    /**
             * 批量删除mqtt集群信息
     * 
     * @param ids 
     * @return int
     */
    public int deleteBusiMqttClusterByIds(Long[] ids);
}
