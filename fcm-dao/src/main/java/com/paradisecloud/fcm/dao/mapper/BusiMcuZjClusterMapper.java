package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuZjCluster;

import java.util.List;

/**
 * 紫荆MCU集群Mapper接口
 * 
 * @author lilinhai
 * @date 2022-10-10
 */
public interface BusiMcuZjClusterMapper 
{
    /**
     * 查询紫荆MCU集群
     * 
     * @param id 紫荆MCU集群ID
     * @return 紫荆MCU集群
     */
    public BusiMcuZjCluster selectBusiMcuZjClusterById(Long id);

    /**
     * 查询紫荆MCU集群列表
     * 
     * @param busiMcuZjCluster 紫荆MCU集群
     * @return 紫荆MCU集群集合
     */
    public List<BusiMcuZjCluster> selectBusiMcuZjClusterList(BusiMcuZjCluster busiMcuZjCluster);

    /**
     * 新增紫荆MCU集群
     * 
     * @param busiMcuZjCluster 紫荆MCU集群
     * @return 结果
     */
    public int insertBusiMcuZjCluster(BusiMcuZjCluster busiMcuZjCluster);

    /**
     * 修改紫荆MCU集群
     * 
     * @param busiMcuZjCluster 紫荆MCU集群
     * @return 结果
     */
    public int updateBusiMcuZjCluster(BusiMcuZjCluster busiMcuZjCluster);

    /**
     * 删除紫荆MCU集群
     * 
     * @param id 紫荆MCU集群ID
     * @return 结果
     */
    public int deleteBusiMcuZjClusterById(Long id);

    /**
     * 批量删除紫荆MCU集群
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuZjClusterByIds(Long[] ids);
}
