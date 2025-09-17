package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiMqttClusterMap;

/**
 * Mapper接口
 * 
 * @author zyz
 * @date 2021-07-21
 */
public interface BusiMqttClusterMapMapper 
{
    /**
             * 查询mqtt集群关联信息
     * 
     * @param id 
     * @return BusiMqttClusterMap
     */
    public BusiMqttClusterMap selectBusiMqttClusterMapById(Long id);

    /**
             * 查询mqtt集群关联信息列表
     * 
     * @param busiMqttClusterMap 
     * @return List<BusiMqttClusterMap>
     */
    public List<BusiMqttClusterMap> selectBusiMqttClusterMapList(BusiMqttClusterMap busiMqttClusterMap);

    /**
             * 新增mqtt集群关联信息
     * 
     * @param busiMqttClusterMap 
     * @return int
     */
    public int insertBusiMqttClusterMap(BusiMqttClusterMap busiMqttClusterMap);

    /**
             * 修改mqtt集群关联信息
     * 
     * @param busiMqttClusterMap 
     * @return int
     */
    public int updateBusiMqttClusterMap(BusiMqttClusterMap busiMqttClusterMap);

    /**
            * 删除mqtt集群关联信息
     * 
     * @param id
     * @return int
     */
    public int deleteBusiMqttClusterMapById(Long id);

    /**
             * 批量删除mqtt集群关联信息
     * 
     * @param ids 
     * @return int
     */
    public int deleteBusiMqttClusterMapByIds(Long[] ids);
}
