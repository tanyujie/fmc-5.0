package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuZjClusterMap;

import java.util.List;

/**
 * 紫荆MCU-终端组中间（多对多）Mapper接口
 * 
 * @author lilinhai
 * @date 2022-10-10
 */
public interface BusiMcuZjClusterMapMapper 
{
    /**
     * 查询紫荆MCU-终端组中间（多对多）
     * 
     * @param id 紫荆MCU-终端组中间（多对多）ID
     * @return 紫荆MCU-终端组中间（多对多）
     */
    public BusiMcuZjClusterMap selectBusiMcuZjClusterMapById(Long id);

    /**
     * 查询紫荆MCU-终端组中间（多对多）列表
     * 
     * @param busiMcuZjClusterMap 紫荆MCU-终端组中间（多对多）
     * @return 紫荆MCU-终端组中间（多对多）集合
     */
    public List<BusiMcuZjClusterMap> selectBusiMcuZjClusterMapList(BusiMcuZjClusterMap busiMcuZjClusterMap);

    /**
     * 新增紫荆MCU-终端组中间（多对多）
     * 
     * @param busiMcuZjClusterMap 紫荆MCU-终端组中间（多对多）
     * @return 结果
     */
    public int insertBusiMcuZjClusterMap(BusiMcuZjClusterMap busiMcuZjClusterMap);

    /**
     * 修改紫荆MCU-终端组中间（多对多）
     * 
     * @param busiMcuZjClusterMap 紫荆MCU-终端组中间（多对多）
     * @return 结果
     */
    public int updateBusiMcuZjClusterMap(BusiMcuZjClusterMap busiMcuZjClusterMap);

    /**
     * 删除紫荆MCU-终端组中间（多对多）
     * 
     * @param id 紫荆MCU-终端组中间（多对多）ID
     * @return 结果
     */
    public int deleteBusiMcuZjClusterMapById(Long id);

    /**
     * 批量删除紫荆MCU-终端组中间（多对多）
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuZjClusterMapByIds(Long[] ids);
}
