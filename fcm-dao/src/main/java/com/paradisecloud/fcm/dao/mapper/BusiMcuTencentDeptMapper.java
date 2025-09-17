package com.paradisecloud.fcm.dao.mapper;





import com.paradisecloud.fcm.dao.model.BusiMcuTencentDept;

import java.util.List;

/**
 * Tencent.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuTencentDeptMapper
{
    /**
     * 查询Tencent.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param id Tencent.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return Tencent.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     */
    public BusiMcuTencentDept selectBusiMcuTencentDeptById(Long id);

    /**
     * 查询Tencent.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）列表
     * 
     * @param busiMcuTencentDept Tencent.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return Tencent.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）集合
     */
    public List<BusiMcuTencentDept> selectBusiMcuTencentDeptList(BusiMcuTencentDept busiMcuTencentDept);

    /**
     * 新增Tencent.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuTencentDept Tencent.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    public int insertBusiMcuTencentDept(BusiMcuTencentDept busiMcuTencentDept);

    /**
     * 修改Tencent.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuTencentDept Tencent.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    public int updateBusiMcuTencentDept(BusiMcuTencentDept busiMcuTencentDept);

    /**
     * 删除Tencent.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param id Tencent.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    public int deleteBusiMcuTencentDeptById(Long id);

    /**
     * 批量删除Tencent.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuTencentDeptByIds(Long[] ids);
}
