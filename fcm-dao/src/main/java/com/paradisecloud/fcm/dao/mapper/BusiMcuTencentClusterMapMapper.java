package com.paradisecloud.fcm.dao.mapper;





import com.paradisecloud.fcm.dao.model.BusiMcuTencentClusterMap;

import java.util.List;

/**
 * Tencent.0MCU-终端组中间（多对多）Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuTencentClusterMapMapper
{
    /**
     * 查询Tencent.0MCU-终端组中间（多对多）
     * 
     * @param id Tencent.0MCU-终端组中间（多对多）ID
     * @return Tencent.0MCU-终端组中间（多对多）
     */
    public BusiMcuTencentClusterMap selectBusiMcuTencentClusterMapById(Long id);

    /**
     * 查询Tencent.0MCU-终端组中间（多对多）列表
     * 
     * @param busiMcuTencentClusterMap Tencent.0MCU-终端组中间（多对多）
     * @return Tencent.0MCU-终端组中间（多对多）集合
     */
    public List<BusiMcuTencentClusterMap> selectBusiMcuTencentClusterMapList(BusiMcuTencentClusterMap busiMcuTencentClusterMap);

    /**
     * 新增Tencent.0MCU-终端组中间（多对多）
     * 
     * @param busiMcuTencentClusterMap Tencent.0MCU-终端组中间（多对多）
     * @return 结果
     */
    public int insertBusiMcuTencentClusterMap(BusiMcuTencentClusterMap busiMcuTencentClusterMap);

    /**
     * 修改Tencent.0MCU-终端组中间（多对多）
     * 
     * @param busiMcuTencentClusterMap Tencent.0MCU-终端组中间（多对多）
     * @return 结果
     */
    public int updateBusiMcuTencentClusterMap(BusiMcuTencentClusterMap busiMcuTencentClusterMap);

    /**
     * 删除Tencent.0MCU-终端组中间（多对多）
     * 
     * @param id Tencent.0MCU-终端组中间（多对多）ID
     * @return 结果
     */
    public int deleteBusiMcuTencentClusterMapById(Long id);

    /**
     * 批量删除Tencent.0MCU-终端组中间（多对多）
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuTencentClusterMapByIds(Long[] ids);
}
