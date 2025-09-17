package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuKdcClusterMap;

import java.util.List;

/**
 * 紫荆MCU-终端组中间（多对多）Mapper接口
 * 
 * @author lilinhai
 * @date 2022-10-10
 */
public interface BusiMcuKdcClusterMapMapper
{
    /**
     * 查询紫荆MCU-终端组中间（多对多）
     * 
     * @param id 紫荆MCU-终端组中间（多对多）ID
     * @return 紫荆MCU-终端组中间（多对多）
     */
    public BusiMcuKdcClusterMap selectBusiMcuKdcClusterMapById(Long id);

    /**
     * 查询紫荆MCU-终端组中间（多对多）列表
     * 
     * @param busiMcuKdcClusterMap 紫荆MCU-终端组中间（多对多）
     * @return 紫荆MCU-终端组中间（多对多）集合
     */
    public List<BusiMcuKdcClusterMap> selectBusiMcuKdcClusterMapList(BusiMcuKdcClusterMap busiMcuKdcClusterMap);

    /**
     * 新增紫荆MCU-终端组中间（多对多）
     * 
     * @param busiMcuKdcClusterMap 紫荆MCU-终端组中间（多对多）
     * @return 结果
     */
    public int insertBusiMcuKdcClusterMap(BusiMcuKdcClusterMap busiMcuKdcClusterMap);

    /**
     * 修改紫荆MCU-终端组中间（多对多）
     * 
     * @param busiMcuKdcClusterMap 紫荆MCU-终端组中间（多对多）
     * @return 结果
     */
    public int updateBusiMcuKdcClusterMap(BusiMcuKdcClusterMap busiMcuKdcClusterMap);

    /**
     * 删除紫荆MCU-终端组中间（多对多）
     * 
     * @param id 紫荆MCU-终端组中间（多对多）ID
     * @return 结果
     */
    public int deleteBusiMcuKdcClusterMapById(Long id);

    /**
     * 批量删除紫荆MCU-终端组中间（多对多）
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuKdcClusterMapByIds(Long[] ids);
}
