package com.paradisecloud.fcm.dao.mapper;


import com.paradisecloud.fcm.dao.model.BusiMcuSmc2ClusterMap;

import java.util.List;

/**
 * SMC2.0MCU-终端组中间（多对多）Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuSmc2ClusterMapMapper
{
    /**
     * 查询SMC2.0MCU-终端组中间（多对多）
     * 
     * @param id SMC2.0MCU-终端组中间（多对多）ID
     * @return SMC2.0MCU-终端组中间（多对多）
     */
    public BusiMcuSmc2ClusterMap selectBusiMcuSmc2ClusterMapById(Long id);

    /**
     * 查询SMC2.0MCU-终端组中间（多对多）列表
     * 
     * @param busiMcuSmc2ClusterMap SMC2.0MCU-终端组中间（多对多）
     * @return SMC2.0MCU-终端组中间（多对多）集合
     */
    public List<BusiMcuSmc2ClusterMap> selectBusiMcuSmc2ClusterMapList(BusiMcuSmc2ClusterMap busiMcuSmc2ClusterMap);

    /**
     * 新增SMC2.0MCU-终端组中间（多对多）
     * 
     * @param busiMcuSmc2ClusterMap SMC2.0MCU-终端组中间（多对多）
     * @return 结果
     */
    public int insertBusiMcuSmc2ClusterMap(BusiMcuSmc2ClusterMap busiMcuSmc2ClusterMap);

    /**
     * 修改SMC2.0MCU-终端组中间（多对多）
     * 
     * @param busiMcuSmc2ClusterMap SMC2.0MCU-终端组中间（多对多）
     * @return 结果
     */
    public int updateBusiMcuSmc2ClusterMap(BusiMcuSmc2ClusterMap busiMcuSmc2ClusterMap);

    /**
     * 删除SMC2.0MCU-终端组中间（多对多）
     * 
     * @param id SMC2.0MCU-终端组中间（多对多）ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2ClusterMapById(Long id);

    /**
     * 批量删除SMC2.0MCU-终端组中间（多对多）
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2ClusterMapByIds(Long[] ids);
}
