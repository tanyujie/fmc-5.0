package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuSmc3ClusterMap;

import java.util.List;

/**
 * SMC3.0MCU-终端组中间（多对多）Mapper接口
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
public interface BusiMcuSmc3ClusterMapMapper 
{
    /**
     * 查询SMC3.0MCU-终端组中间（多对多）
     * 
     * @param id SMC3.0MCU-终端组中间（多对多）ID
     * @return SMC3.0MCU-终端组中间（多对多）
     */
    public BusiMcuSmc3ClusterMap selectBusiMcuSmc3ClusterMapById(Long id);

    /**
     * 查询SMC3.0MCU-终端组中间（多对多）列表
     * 
     * @param busiMcuSmc3ClusterMap SMC3.0MCU-终端组中间（多对多）
     * @return SMC3.0MCU-终端组中间（多对多）集合
     */
    public List<BusiMcuSmc3ClusterMap> selectBusiMcuSmc3ClusterMapList(BusiMcuSmc3ClusterMap busiMcuSmc3ClusterMap);

    /**
     * 新增SMC3.0MCU-终端组中间（多对多）
     * 
     * @param busiMcuSmc3ClusterMap SMC3.0MCU-终端组中间（多对多）
     * @return 结果
     */
    public int insertBusiMcuSmc3ClusterMap(BusiMcuSmc3ClusterMap busiMcuSmc3ClusterMap);

    /**
     * 修改SMC3.0MCU-终端组中间（多对多）
     * 
     * @param busiMcuSmc3ClusterMap SMC3.0MCU-终端组中间（多对多）
     * @return 结果
     */
    public int updateBusiMcuSmc3ClusterMap(BusiMcuSmc3ClusterMap busiMcuSmc3ClusterMap);

    /**
     * 删除SMC3.0MCU-终端组中间（多对多）
     * 
     * @param id SMC3.0MCU-终端组中间（多对多）ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3ClusterMapById(Long id);

    /**
     * 批量删除SMC3.0MCU-终端组中间（多对多）
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3ClusterMapByIds(Long[] ids);
}
