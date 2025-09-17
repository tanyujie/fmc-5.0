package com.paradisecloud.fcm.dao.mapper;




import com.paradisecloud.fcm.dao.model.BusiMcuTencentCluster;

import java.util.List;

/**
 * Tencent.0MCU集群Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuTencentClusterMapper
{
    /**
     * 查询Tencent.0MCU集群
     * 
     * @param id Tencent.0MCU集群ID
     * @return Tencent.0MCU集群
     */
    public BusiMcuTencentCluster selectBusiMcuTencentClusterById(Long id);

    /**
     * 查询Tencent.0MCU集群列表
     * 
     * @param busiMcuTencentCluster Tencent.0MCU集群
     * @return Tencent.0MCU集群集合
     */
    public List<BusiMcuTencentCluster> selectBusiMcuTencentClusterList(BusiMcuTencentCluster busiMcuTencentCluster);

    /**
     * 新增Tencent.0MCU集群
     * 
     * @param busiMcuTencentCluster Tencent.0MCU集群
     * @return 结果
     */
    public int insertBusiMcuTencentCluster(BusiMcuTencentCluster busiMcuTencentCluster);

    /**
     * 修改Tencent.0MCU集群
     * 
     * @param busiMcuTencentCluster Tencent.0MCU集群
     * @return 结果
     */
    public int updateBusiMcuTencentCluster(BusiMcuTencentCluster busiMcuTencentCluster);

    /**
     * 删除Tencent.0MCU集群
     * 
     * @param id Tencent.0MCU集群ID
     * @return 结果
     */
    public int deleteBusiMcuTencentClusterById(Long id);

    /**
     * 批量删除Tencent.0MCU集群
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuTencentClusterByIds(Long[] ids);
}
