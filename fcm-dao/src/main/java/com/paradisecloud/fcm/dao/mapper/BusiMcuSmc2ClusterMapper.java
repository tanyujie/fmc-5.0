package com.paradisecloud.fcm.dao.mapper;


import com.paradisecloud.fcm.dao.model.BusiMcuSmc2Cluster;

import java.util.List;

/**
 * SMC2.0MCU集群Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuSmc2ClusterMapper
{
    /**
     * 查询SMC2.0MCU集群
     * 
     * @param id SMC2.0MCU集群ID
     * @return SMC2.0MCU集群
     */
    public BusiMcuSmc2Cluster selectBusiMcuSmc2ClusterById(Long id);

    /**
     * 查询SMC2.0MCU集群列表
     * 
     * @param busiMcuSmc2Cluster SMC2.0MCU集群
     * @return SMC2.0MCU集群集合
     */
    public List<BusiMcuSmc2Cluster> selectBusiMcuSmc2ClusterList(BusiMcuSmc2Cluster busiMcuSmc2Cluster);

    /**
     * 新增SMC2.0MCU集群
     * 
     * @param busiMcuSmc2Cluster SMC2.0MCU集群
     * @return 结果
     */
    public int insertBusiMcuSmc2Cluster(BusiMcuSmc2Cluster busiMcuSmc2Cluster);

    /**
     * 修改SMC2.0MCU集群
     * 
     * @param busiMcuSmc2Cluster SMC2.0MCU集群
     * @return 结果
     */
    public int updateBusiMcuSmc2Cluster(BusiMcuSmc2Cluster busiMcuSmc2Cluster);

    /**
     * 删除SMC2.0MCU集群
     * 
     * @param id SMC2.0MCU集群ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2ClusterById(Long id);

    /**
     * 批量删除SMC2.0MCU集群
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2ClusterByIds(Long[] ids);
}
