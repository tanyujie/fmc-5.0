package com.paradisecloud.fcm.dao.mapper;




import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudCluster;

import java.util.List;

/**
 * Hwcloud.0MCU集群Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuHwcloudClusterMapper
{
    /**
     * 查询Hwcloud.0MCU集群
     * 
     * @param id Hwcloud.0MCU集群ID
     * @return Hwcloud.0MCU集群
     */
    public BusiMcuHwcloudCluster selectBusiMcuHwcloudClusterById(Long id);

    /**
     * 查询Hwcloud.0MCU集群列表
     * 
     * @param busiMcuHwcloudCluster Hwcloud.0MCU集群
     * @return Hwcloud.0MCU集群集合
     */
    public List<BusiMcuHwcloudCluster> selectBusiMcuHwcloudClusterList(BusiMcuHwcloudCluster busiMcuHwcloudCluster);

    /**
     * 新增Hwcloud.0MCU集群
     * 
     * @param busiMcuHwcloudCluster Hwcloud.0MCU集群
     * @return 结果
     */
    public int insertBusiMcuHwcloudCluster(BusiMcuHwcloudCluster busiMcuHwcloudCluster);

    /**
     * 修改Hwcloud.0MCU集群
     * 
     * @param busiMcuHwcloudCluster Hwcloud.0MCU集群
     * @return 结果
     */
    public int updateBusiMcuHwcloudCluster(BusiMcuHwcloudCluster busiMcuHwcloudCluster);

    /**
     * 删除Hwcloud.0MCU集群
     * 
     * @param id Hwcloud.0MCU集群ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudClusterById(Long id);

    /**
     * 批量删除Hwcloud.0MCU集群
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudClusterByIds(Long[] ids);
}
