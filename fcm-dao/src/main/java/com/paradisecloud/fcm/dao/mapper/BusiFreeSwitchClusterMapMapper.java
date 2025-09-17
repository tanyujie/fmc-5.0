package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchClusterMap;

/**
 * FreeSwitch-终端组中间（多对多）Mapper接口
 * 
 * @author lilinhai
 * @date 2021-03-17
 */
public interface BusiFreeSwitchClusterMapMapper 
{
    /**
     * 查询FreeSwitch-终端组中间（多对多）
     * 
     * @param id FreeSwitch-终端组中间（多对多）ID
     * @return FreeSwitch-终端组中间（多对多）
     */
    public BusiFreeSwitchClusterMap selectBusiFreeSwitchClusterMapById(Long id);

    /**
     * 查询FreeSwitch-终端组中间（多对多）列表
     * 
     * @param busiFreeSwitchClusterMap FreeSwitch-终端组中间（多对多）
     * @return FreeSwitch-终端组中间（多对多）集合
     */
    public List<BusiFreeSwitchClusterMap> selectBusiFreeSwitchClusterMapList(BusiFreeSwitchClusterMap busiFreeSwitchClusterMap);

    /**
     * 新增FreeSwitch-终端组中间（多对多）
     * 
     * @param busiFreeSwitchClusterMap FreeSwitch-终端组中间（多对多）
     * @return 结果
     */
    public int insertBusiFreeSwitchClusterMap(BusiFreeSwitchClusterMap busiFreeSwitchClusterMap);

    /**
     * 修改FreeSwitch-终端组中间（多对多）
     * 
     * @param busiFreeSwitchClusterMap FreeSwitch-终端组中间（多对多）
     * @return 结果
     */
    public int updateBusiFreeSwitchClusterMap(BusiFreeSwitchClusterMap busiFreeSwitchClusterMap);

    /**
     * 删除FreeSwitch-终端组中间（多对多）
     * 
     * @param id FreeSwitch-终端组中间（多对多）ID
     * @return 结果
     */
    public int deleteBusiFreeSwitchClusterMapById(Long id);

    /**
     * 批量删除FreeSwitch-终端组中间（多对多）
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiFreeSwitchClusterMapByIds(Long[] ids);
}
