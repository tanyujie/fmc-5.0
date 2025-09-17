package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuPlcClusterMap;

import java.util.List;

/**
 * 紫荆MCU-终端组中间（多对多）Mapper接口
 * 
 * @author lilinhai
 * @date 2022-10-10
 */
public interface BusiMcuPlcClusterMapMapper 
{
    /**
     * 查询紫荆MCU-终端组中间（多对多）
     * 
     * @param id 紫荆MCU-终端组中间（多对多）ID
     * @return 紫荆MCU-终端组中间（多对多）
     */
    public BusiMcuPlcClusterMap selectBusiMcuPlcClusterMapById(Long id);

    /**
     * 查询紫荆MCU-终端组中间（多对多）列表
     * 
     * @param busiMcuPlcClusterMap 紫荆MCU-终端组中间（多对多）
     * @return 紫荆MCU-终端组中间（多对多）集合
     */
    public List<BusiMcuPlcClusterMap> selectBusiMcuPlcClusterMapList(BusiMcuPlcClusterMap busiMcuPlcClusterMap);

    /**
     * 新增紫荆MCU-终端组中间（多对多）
     * 
     * @param busiMcuPlcClusterMap 紫荆MCU-终端组中间（多对多）
     * @return 结果
     */
    public int insertBusiMcuPlcClusterMap(BusiMcuPlcClusterMap busiMcuPlcClusterMap);

    /**
     * 修改紫荆MCU-终端组中间（多对多）
     * 
     * @param busiMcuPlcClusterMap 紫荆MCU-终端组中间（多对多）
     * @return 结果
     */
    public int updateBusiMcuPlcClusterMap(BusiMcuPlcClusterMap busiMcuPlcClusterMap);

    /**
     * 删除紫荆MCU-终端组中间（多对多）
     * 
     * @param id 紫荆MCU-终端组中间（多对多）ID
     * @return 结果
     */
    public int deleteBusiMcuPlcClusterMapById(Long id);

    /**
     * 批量删除紫荆MCU-终端组中间（多对多）
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuPlcClusterMapByIds(Long[] ids);
}
