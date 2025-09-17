package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuZteClusterMap;

import java.util.List;

/**
 * 中兴MCU-终端组中间（多对多）Mapper接口
 * 
 * @author lilinhai
 * @date 2024-04-09
 */
public interface BusiMcuZteClusterMapMapper 
{
    /**
     * 查询中兴MCU-终端组中间（多对多）
     * 
     * @param id 中兴MCU-终端组中间（多对多）ID
     * @return 中兴MCU-终端组中间（多对多）
     */
    public BusiMcuZteClusterMap selectBusiMcuZteClusterMapById(Long id);

    /**
     * 查询中兴MCU-终端组中间（多对多）列表
     * 
     * @param busiMcuZteClusterMap 中兴MCU-终端组中间（多对多）
     * @return 中兴MCU-终端组中间（多对多）集合
     */
    public List<BusiMcuZteClusterMap> selectBusiMcuZteClusterMapList(BusiMcuZteClusterMap busiMcuZteClusterMap);

    /**
     * 新增中兴MCU-终端组中间（多对多）
     * 
     * @param busiMcuZteClusterMap 中兴MCU-终端组中间（多对多）
     * @return 结果
     */
    public int insertBusiMcuZteClusterMap(BusiMcuZteClusterMap busiMcuZteClusterMap);

    /**
     * 修改中兴MCU-终端组中间（多对多）
     * 
     * @param busiMcuZteClusterMap 中兴MCU-终端组中间（多对多）
     * @return 结果
     */
    public int updateBusiMcuZteClusterMap(BusiMcuZteClusterMap busiMcuZteClusterMap);

    /**
     * 删除中兴MCU-终端组中间（多对多）
     * 
     * @param id 中兴MCU-终端组中间（多对多）ID
     * @return 结果
     */
    public int deleteBusiMcuZteClusterMapById(Long id);

    /**
     * 批量删除中兴MCU-终端组中间（多对多）
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuZteClusterMapByIds(Long[] ids);
}
