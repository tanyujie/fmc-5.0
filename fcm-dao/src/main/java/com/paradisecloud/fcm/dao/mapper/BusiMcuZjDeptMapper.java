package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiFmeDept;
import com.paradisecloud.fcm.dao.model.BusiMcuZjDept;

import java.util.List;

/**
 * MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）Mapper接口
 * 
 * @author lilinhai
 * @date 2022-10-10
 */
public interface BusiMcuZjDeptMapper 
{
    /**
     * 查询MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param id MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     */
    public BusiMcuZjDept selectBusiMcuZjDeptById(Long id);

    /**
     * 根据部门ID查询MCU组分配租户的中间
     *
     * @param deptId
     * @return
     */
    BusiMcuZjDept selectBusiMcuZjDeptByDeptId(Long deptId);

    /**
     * 查询MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）列表
     * 
     * @param busiMcuZjDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）集合
     */
    public List<BusiMcuZjDept> selectBusiMcuZjDeptList(BusiMcuZjDept busiMcuZjDept);

    /**
     * 新增MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuZjDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    public int insertBusiMcuZjDept(BusiMcuZjDept busiMcuZjDept);

    /**
     * 修改MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuZjDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    public int updateBusiMcuZjDept(BusiMcuZjDept busiMcuZjDept);

    /**
     * 删除MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param id MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    public int deleteBusiMcuZjDeptById(Long id);

    /**
     * 批量删除MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuZjDeptByIds(Long[] ids);
}
