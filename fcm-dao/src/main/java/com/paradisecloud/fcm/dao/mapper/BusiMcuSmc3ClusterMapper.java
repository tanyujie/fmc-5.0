package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuSmc3Cluster;

import java.util.List;

/**
 * SMC3.0MCU集群Mapper接口
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
public interface BusiMcuSmc3ClusterMapper 
{
    /**
     * 查询SMC3.0MCU集群
     * 
     * @param id SMC3.0MCU集群ID
     * @return SMC3.0MCU集群
     */
    public BusiMcuSmc3Cluster selectBusiMcuSmc3ClusterById(Long id);

    /**
     * 查询SMC3.0MCU集群列表
     * 
     * @param busiMcuSmc3Cluster SMC3.0MCU集群
     * @return SMC3.0MCU集群集合
     */
    public List<BusiMcuSmc3Cluster> selectBusiMcuSmc3ClusterList(BusiMcuSmc3Cluster busiMcuSmc3Cluster);

    /**
     * 新增SMC3.0MCU集群
     * 
     * @param busiMcuSmc3Cluster SMC3.0MCU集群
     * @return 结果
     */
    public int insertBusiMcuSmc3Cluster(BusiMcuSmc3Cluster busiMcuSmc3Cluster);

    /**
     * 修改SMC3.0MCU集群
     * 
     * @param busiMcuSmc3Cluster SMC3.0MCU集群
     * @return 结果
     */
    public int updateBusiMcuSmc3Cluster(BusiMcuSmc3Cluster busiMcuSmc3Cluster);

    /**
     * 删除SMC3.0MCU集群
     * 
     * @param id SMC3.0MCU集群ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3ClusterById(Long id);

    /**
     * 批量删除SMC3.0MCU集群
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3ClusterByIds(Long[] ids);
}
