package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuKdcCluster;

import java.util.List;

/**
 * 紫荆MCU集群Mapper接口
 * 
 * @author lilinhai
 * @date 2022-10-10
 */
public interface BusiMcuKdcClusterMapper 
{
    /**
     * 查询紫荆MCU集群
     * 
     * @param id 紫荆MCU集群ID
     * @return 紫荆MCU集群
     */
    public BusiMcuKdcCluster selectBusiMcuKdcClusterById(Long id);

    /**
     * 查询紫荆MCU集群列表
     * 
     * @param busiMcuKdcCluster 紫荆MCU集群
     * @return 紫荆MCU集群集合
     */
    public List<BusiMcuKdcCluster> selectBusiMcuKdcClusterList(BusiMcuKdcCluster busiMcuKdcCluster);

    /**
     * 新增紫荆MCU集群
     * 
     * @param busiMcuKdcCluster 紫荆MCU集群
     * @return 结果
     */
    public int insertBusiMcuKdcCluster(BusiMcuKdcCluster busiMcuKdcCluster);

    /**
     * 修改紫荆MCU集群
     * 
     * @param busiMcuKdcCluster 紫荆MCU集群
     * @return 结果
     */
    public int updateBusiMcuKdcCluster(BusiMcuKdcCluster busiMcuKdcCluster);

    /**
     * 删除紫荆MCU集群
     * 
     * @param id 紫荆MCU集群ID
     * @return 结果
     */
    public int deleteBusiMcuKdcClusterById(Long id);

    /**
     * 批量删除紫荆MCU集群
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuKdcClusterByIds(Long[] ids);
}
