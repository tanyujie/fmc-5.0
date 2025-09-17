package com.paradisecloud.fcm.dao.mapper;



import com.paradisecloud.fcm.dao.model.BusiMcuSmc2;

import java.util.List;

/**
 * Smc2.0MCU终端信息Mapper接口
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
public interface BusiMcuSmc2Mapper 
{
    /**
     * 查询Smc2.0MCU终端信息
     * 
     * @param id Smc2.0MCU终端信息ID
     * @return Smc2.0MCU终端信息
     */
    public BusiMcuSmc2 selectBusiMcuSmc2ById(Long id);

    /**
     * 查询Smc2.0MCU终端信息列表
     * 
     * @param busiMcuSmc2 Smc2.0MCU终端信息
     * @return Smc2.0MCU终端信息集合
     */
    public List<BusiMcuSmc2> selectBusiMcuSmc2List(BusiMcuSmc2 busiMcuSmc2);

    /**
     * 新增Smc2.0MCU终端信息
     * 
     * @param busiMcuSmc2 Smc2.0MCU终端信息
     * @return 结果
     */
    public int insertBusiMcuSmc2(BusiMcuSmc2 busiMcuSmc2);

    /**
     * 修改Smc2.0MCU终端信息
     * 
     * @param busiMcuSmc2 Smc2.0MCU终端信息
     * @return 结果
     */
    public int updateBusiMcuSmc2(BusiMcuSmc2 busiMcuSmc2);

    /**
     * 删除Smc2.0MCU终端信息
     * 
     * @param id Smc2.0MCU终端信息ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2ById(Long id);

    /**
     * 批量删除Smc2.0MCU终端信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2ByIds(Long[] ids);
}
