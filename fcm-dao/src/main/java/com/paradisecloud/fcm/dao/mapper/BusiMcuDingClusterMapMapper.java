package com.paradisecloud.fcm.dao.mapper;




import com.paradisecloud.fcm.dao.model.BusiMcuDingClusterMap;

import java.util.List;

/**
 * Ding.0MCU-终端组中间（多对多）Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuDingClusterMapMapper
{
    /**
     * 查询Ding.0MCU-终端组中间（多对多）
     * 
     * @param id Ding.0MCU-终端组中间（多对多）ID
     * @return Ding.0MCU-终端组中间（多对多）
     */
    public BusiMcuDingClusterMap selectBusiMcuDingClusterMapById(Long id);

    /**
     * 查询Ding.0MCU-终端组中间（多对多）列表
     * 
     * @param busiMcuDingClusterMap Ding.0MCU-终端组中间（多对多）
     * @return Ding.0MCU-终端组中间（多对多）集合
     */
    public List<BusiMcuDingClusterMap> selectBusiMcuDingClusterMapList(BusiMcuDingClusterMap busiMcuDingClusterMap);

    /**
     * 新增Ding.0MCU-终端组中间（多对多）
     * 
     * @param busiMcuDingClusterMap Ding.0MCU-终端组中间（多对多）
     * @return 结果
     */
    public int insertBusiMcuDingClusterMap(BusiMcuDingClusterMap busiMcuDingClusterMap);

    /**
     * 修改Ding.0MCU-终端组中间（多对多）
     * 
     * @param busiMcuDingClusterMap Ding.0MCU-终端组中间（多对多）
     * @return 结果
     */
    public int updateBusiMcuDingClusterMap(BusiMcuDingClusterMap busiMcuDingClusterMap);

    /**
     * 删除Ding.0MCU-终端组中间（多对多）
     * 
     * @param id Ding.0MCU-终端组中间（多对多）ID
     * @return 结果
     */
    public int deleteBusiMcuDingClusterMapById(Long id);

    /**
     * 批量删除Ding.0MCU-终端组中间（多对多）
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuDingClusterMapByIds(Long[] ids);
}
