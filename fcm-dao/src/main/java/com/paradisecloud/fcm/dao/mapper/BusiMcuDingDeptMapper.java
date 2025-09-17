package com.paradisecloud.fcm.dao.mapper;


import com.paradisecloud.fcm.dao.model.BusiMcuDingDept;

import java.util.List;

/**
 * Ding.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuDingDeptMapper
{
    /**
     * 查询Ding.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param id Ding.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return Ding.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     */
    public BusiMcuDingDept selectBusiMcuDingDeptById(Long id);

    /**
     * 查询Ding.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）列表
     * 
     * @param busiMcuDingDept Ding.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return Ding.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）集合
     */
    public List<BusiMcuDingDept> selectBusiMcuDingDeptList(BusiMcuDingDept busiMcuDingDept);

    /**
     * 新增Ding.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuDingDept Ding.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    public int insertBusiMcuDingDept(BusiMcuDingDept busiMcuDingDept);

    /**
     * 修改Ding.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuDingDept Ding.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    public int updateBusiMcuDingDept(BusiMcuDingDept busiMcuDingDept);

    /**
     * 删除Ding.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param id Ding.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    public int deleteBusiMcuDingDeptById(Long id);

    /**
     * 批量删除Ding.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuDingDeptByIds(Long[] ids);
}
