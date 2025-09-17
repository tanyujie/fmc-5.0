package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuPlcCluster;

import java.util.List;

/**
 * 紫荆MCU集群Mapper接口
 * 
 * @author lilinhai
 * @date 2022-10-10
 */
public interface BusiMcuPlcClusterMapper 
{
    /**
     * 查询紫荆MCU集群
     * 
     * @param id 紫荆MCU集群ID
     * @return 紫荆MCU集群
     */
    public BusiMcuPlcCluster selectBusiMcuPlcClusterById(Long id);

    /**
     * 查询紫荆MCU集群列表
     * 
     * @param busiMcuPlcCluster 紫荆MCU集群
     * @return 紫荆MCU集群集合
     */
    public List<BusiMcuPlcCluster> selectBusiMcuPlcClusterList(BusiMcuPlcCluster busiMcuPlcCluster);

    /**
     * 新增紫荆MCU集群
     * 
     * @param busiMcuPlcCluster 紫荆MCU集群
     * @return 结果
     */
    public int insertBusiMcuPlcCluster(BusiMcuPlcCluster busiMcuPlcCluster);

    /**
     * 修改紫荆MCU集群
     * 
     * @param busiMcuPlcCluster 紫荆MCU集群
     * @return 结果
     */
    public int updateBusiMcuPlcCluster(BusiMcuPlcCluster busiMcuPlcCluster);

    /**
     * 删除紫荆MCU集群
     * 
     * @param id 紫荆MCU集群ID
     * @return 结果
     */
    public int deleteBusiMcuPlcClusterById(Long id);

    /**
     * 批量删除紫荆MCU集群
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuPlcClusterByIds(Long[] ids);
}
