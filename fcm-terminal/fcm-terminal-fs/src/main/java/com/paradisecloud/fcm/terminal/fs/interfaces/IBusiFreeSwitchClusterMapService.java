package com.paradisecloud.fcm.terminal.fs.interfaces;

import com.paradisecloud.fcm.dao.model.BusiFreeSwitchClusterMap;

import java.util.List;

/**
 * FreeSwitch-终端组中间（多对多）Service接口
 * 
 * @author lilinhai
 * @date 2021-03-17
 */
public interface IBusiFreeSwitchClusterMapService
{
    /**
     * 查询FreeSwitch-终端组中间（多对多）
     * 
     * @param id FreeSwitch-终端组中间（多对多）ID
     * @return FreeSwitch-终端组中间（多对多）
     */
    BusiFreeSwitchClusterMap selectBusiFreeSwitchClusterMapById(Long id);

    /**
     * 查询FreeSwitch-终端组中间（多对多）列表
     * 
     * @param busiFreeSwitchClusterMap FreeSwitch-终端组中间（多对多）
     * @return FreeSwitch-终端组中间（多对多）集合
     */
    List<BusiFreeSwitchClusterMap> selectBusiFreeSwitchClusterMapList(BusiFreeSwitchClusterMap busiFreeSwitchClusterMap);

    /**
     * 新增FreeSwitch-终端组中间（多对多）
     * 
     * @param busiFreeSwitchClusterMap FreeSwitch-终端组中间（多对多）
     * @return 结果
     */
    int insertBusiFreeSwitchClusterMap(BusiFreeSwitchClusterMap busiFreeSwitchClusterMap);
    
    /**
     *修改FreeSwitch-终端组中间（多对多）
     * 
     * @param busiFreeSwitchClusterMap FreeSwitch-终端组中间（多对多）
     * @return 结果
     */
    int updateBusiFreeSwitchClusterMap(BusiFreeSwitchClusterMap busiFreeSwitchClusterMap);

    /**
     * 删除FreeSwitch-终端组中间（多对多）信息
     * 
     * @param id FreeSwitch-终端组中间（多对多）ID
     * @return 结果
     */
    int deleteBusiFreeSwitchClusterMapById(Long id);
}
