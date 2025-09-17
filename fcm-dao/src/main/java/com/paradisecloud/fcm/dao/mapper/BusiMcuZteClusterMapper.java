package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuZteCluster;

import java.util.List;

/**
 * 中兴MCU集群Mapper接口
 * 
 * @author lilinhai
 * @date 2024-04-09
 */
public interface BusiMcuZteClusterMapper 
{
    /**
     * 查询中兴MCU集群
     * 
     * @param id 中兴MCU集群ID
     * @return 中兴MCU集群
     */
    public BusiMcuZteCluster selectBusiMcuZteClusterById(Long id);

    /**
     * 查询中兴MCU集群列表
     * 
     * @param busiMcuZteCluster 中兴MCU集群
     * @return 中兴MCU集群集合
     */
    public List<BusiMcuZteCluster> selectBusiMcuZteClusterList(BusiMcuZteCluster busiMcuZteCluster);

    /**
     * 新增中兴MCU集群
     * 
     * @param busiMcuZteCluster 中兴MCU集群
     * @return 结果
     */
    public int insertBusiMcuZteCluster(BusiMcuZteCluster busiMcuZteCluster);

    /**
     * 修改中兴MCU集群
     * 
     * @param busiMcuZteCluster 中兴MCU集群
     * @return 结果
     */
    public int updateBusiMcuZteCluster(BusiMcuZteCluster busiMcuZteCluster);

    /**
     * 删除中兴MCU集群
     * 
     * @param id 中兴MCU集群ID
     * @return 结果
     */
    public int deleteBusiMcuZteClusterById(Long id);

    /**
     * 批量删除中兴MCU集群
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuZteClusterByIds(Long[] ids);
}
