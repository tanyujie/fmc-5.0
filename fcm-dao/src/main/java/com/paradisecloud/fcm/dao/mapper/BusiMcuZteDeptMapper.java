package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuZteDept;

import java.util.List;

/**
 * FME组分配租户的中间（一个MCU组可以分配给多个租户，一对多）Mapper接口
 * 
 * @author lilinhai
 * @date 2024-04-09
 */
public interface BusiMcuZteDeptMapper 
{
    /**
     * 查询FME组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param id FME组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return FME组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     */
    public BusiMcuZteDept selectBusiMcuZteDeptById(Long id);

    /**
     * 查询FME组分配租户的中间（一个MCU组可以分配给多个租户，一对多）列表
     * 
     * @param busiMcuZteDept FME组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return FME组分配租户的中间（一个MCU组可以分配给多个租户，一对多）集合
     */
    public List<BusiMcuZteDept> selectBusiMcuZteDeptList(BusiMcuZteDept busiMcuZteDept);

    /**
     * 新增FME组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuZteDept FME组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    public int insertBusiMcuZteDept(BusiMcuZteDept busiMcuZteDept);

    /**
     * 修改FME组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuZteDept FME组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    public int updateBusiMcuZteDept(BusiMcuZteDept busiMcuZteDept);

    /**
     * 删除FME组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param id FME组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    public int deleteBusiMcuZteDeptById(Long id);

    /**
     * 批量删除FME组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuZteDeptByIds(Long[] ids);

    BusiMcuZteDept selectBusiMcuZteDeptByDeptId(Long deptId);
}
