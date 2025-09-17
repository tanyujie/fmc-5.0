package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiFmeDept;
import com.paradisecloud.fcm.dao.model.BusiMcuPlcDept;

import java.util.List;

/**
 * MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）Mapper接口
 * 
 * @author lilinhai
 * @date 2022-10-10
 */
public interface BusiMcuPlcDeptMapper 
{
    /**
     * 查询MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param id MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     */
    public BusiMcuPlcDept selectBusiMcuPlcDeptById(Long id);

    /**
     * 根据部门ID查询MCU组分配租户的中间
     *
     * @param deptId
     * @return
     */
    BusiMcuPlcDept selectBusiMcuPlcDeptByDeptId(Long deptId);

    /**
     * 查询MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）列表
     * 
     * @param busiMcuPlcDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）集合
     */
    public List<BusiMcuPlcDept> selectBusiMcuPlcDeptList(BusiMcuPlcDept busiMcuPlcDept);

    /**
     * 新增MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuPlcDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    public int insertBusiMcuPlcDept(BusiMcuPlcDept busiMcuPlcDept);

    /**
     * 修改MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuPlcDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    public int updateBusiMcuPlcDept(BusiMcuPlcDept busiMcuPlcDept);

    /**
     * 删除MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param id MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    public int deleteBusiMcuPlcDeptById(Long id);

    /**
     * 批量删除MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuPlcDeptByIds(Long[] ids);
}
