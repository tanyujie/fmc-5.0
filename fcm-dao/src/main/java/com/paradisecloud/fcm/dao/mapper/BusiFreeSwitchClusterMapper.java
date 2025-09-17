package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchCluster;

/**
 * FreeSwitch集群Mapper接口
 * 
 * @author lilinhai
 * @date 2021-03-17
 */
public interface BusiFreeSwitchClusterMapper 
{
    /**
     * 查询FreeSwitch集群
     * 
     * @param id FreeSwitch集群ID
     * @return FreeSwitch集群
     */
    public BusiFreeSwitchCluster selectBusiFreeSwitchClusterById(Long id);

    /**
     * 查询FreeSwitch集群列表
     * 
     * @param busiFreeSwitchCluster FreeSwitch集群
     * @return FreeSwitch集群集合
     */
    public List<BusiFreeSwitchCluster> selectBusiFreeSwitchClusterList(BusiFreeSwitchCluster busiFreeSwitchCluster);

    /**
     * 新增FreeSwitch集群
     * 
     * @param busiFreeSwitchCluster FreeSwitch集群
     * @return 结果
     */
    public int insertBusiFreeSwitchCluster(BusiFreeSwitchCluster busiFreeSwitchCluster);

    /**
     * 修改FreeSwitch集群
     * 
     * @param busiFreeSwitchCluster FreeSwitch集群
     * @return 结果
     */
    public int updateBusiFreeSwitchCluster(BusiFreeSwitchCluster busiFreeSwitchCluster);

    /**
     * 删除FreeSwitch集群
     * 
     * @param id FreeSwitch集群ID
     * @return 结果
     */
    public int deleteBusiFreeSwitchClusterById(Long id);

    /**
     * 批量删除FreeSwitch集群
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiFreeSwitchClusterByIds(Long[] ids);
}
