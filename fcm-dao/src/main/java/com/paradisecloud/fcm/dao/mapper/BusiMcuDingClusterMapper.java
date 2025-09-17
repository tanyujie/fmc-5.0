package com.paradisecloud.fcm.dao.mapper;




import com.paradisecloud.fcm.dao.model.BusiMcuDingCluster;

import java.util.List;

/**
 * Ding.0MCU集群Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuDingClusterMapper
{
    /**
     * 查询Ding.0MCU集群
     * 
     * @param id Ding.0MCU集群ID
     * @return Ding.0MCU集群
     */
    public BusiMcuDingCluster selectBusiMcuDingClusterById(Long id);

    /**
     * 查询Ding.0MCU集群列表
     * 
     * @param busiMcuDingCluster Ding.0MCU集群
     * @return Ding.0MCU集群集合
     */
    public List<BusiMcuDingCluster> selectBusiMcuDingClusterList(BusiMcuDingCluster busiMcuDingCluster);

    /**
     * 新增Ding.0MCU集群
     * 
     * @param busiMcuDingCluster Ding.0MCU集群
     * @return 结果
     */
    public int insertBusiMcuDingCluster(BusiMcuDingCluster busiMcuDingCluster);

    /**
     * 修改Ding.0MCU集群
     * 
     * @param busiMcuDingCluster Ding.0MCU集群
     * @return 结果
     */
    public int updateBusiMcuDingCluster(BusiMcuDingCluster busiMcuDingCluster);

    /**
     * 删除Ding.0MCU集群
     * 
     * @param id Ding.0MCU集群ID
     * @return 结果
     */
    public int deleteBusiMcuDingClusterById(Long id);

    /**
     * 批量删除Ding.0MCU集群
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuDingClusterByIds(Long[] ids);
}
