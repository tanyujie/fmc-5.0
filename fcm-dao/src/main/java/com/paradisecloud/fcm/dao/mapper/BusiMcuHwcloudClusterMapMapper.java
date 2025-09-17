package com.paradisecloud.fcm.dao.mapper;





import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudClusterMap;

import java.util.List;

/**
 * Hwcloud.0MCU-终端组中间（多对多）Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuHwcloudClusterMapMapper
{
    /**
     * 查询Hwcloud.0MCU-终端组中间（多对多）
     * 
     * @param id Hwcloud.0MCU-终端组中间（多对多）ID
     * @return Hwcloud.0MCU-终端组中间（多对多）
     */
    public BusiMcuHwcloudClusterMap selectBusiMcuHwcloudClusterMapById(Long id);

    /**
     * 查询Hwcloud.0MCU-终端组中间（多对多）列表
     * 
     * @param busiMcuHwcloudClusterMap Hwcloud.0MCU-终端组中间（多对多）
     * @return Hwcloud.0MCU-终端组中间（多对多）集合
     */
    public List<BusiMcuHwcloudClusterMap> selectBusiMcuHwcloudClusterMapList(BusiMcuHwcloudClusterMap busiMcuHwcloudClusterMap);

    /**
     * 新增Hwcloud.0MCU-终端组中间（多对多）
     * 
     * @param busiMcuHwcloudClusterMap Hwcloud.0MCU-终端组中间（多对多）
     * @return 结果
     */
    public int insertBusiMcuHwcloudClusterMap(BusiMcuHwcloudClusterMap busiMcuHwcloudClusterMap);

    /**
     * 修改Hwcloud.0MCU-终端组中间（多对多）
     * 
     * @param busiMcuHwcloudClusterMap Hwcloud.0MCU-终端组中间（多对多）
     * @return 结果
     */
    public int updateBusiMcuHwcloudClusterMap(BusiMcuHwcloudClusterMap busiMcuHwcloudClusterMap);

    /**
     * 删除Hwcloud.0MCU-终端组中间（多对多）
     * 
     * @param id Hwcloud.0MCU-终端组中间（多对多）ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudClusterMapById(Long id);

    /**
     * 批量删除Hwcloud.0MCU-终端组中间（多对多）
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudClusterMapByIds(Long[] ids);
}
