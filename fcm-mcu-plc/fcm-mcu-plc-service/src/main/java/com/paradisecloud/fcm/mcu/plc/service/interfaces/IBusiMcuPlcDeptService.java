package com.paradisecloud.fcm.mcu.plc.service.interfaces;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiMcuPlcDept;

import java.util.List;

/**
 * MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）Service接口
 * 
 * @author lilinhai
 * @date 2021-03-17
 */
public interface IBusiMcuPlcDeptService 
{
    
    /**
     * 查询MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param id MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     */
    public ModelBean selectBusiMcuPlcDeptById(Long id);
    
    ModelBean selectBusiMcuPlcDeptByDeptId(Long deptId);

    /**
     * 查询MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）列表
     * 
     * @param busiMcuPlcDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）集合
     */
    public List<ModelBean> selectBusiMcuPlcDeptList(BusiMcuPlcDept busiMcuPlcDept);

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
     * 批量删除MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param ids 需要删除的MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    public int deleteBusiMcuPlcDeptByIds(Long[] ids);

    /**
     * 删除MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）信息
     * 
     * @param id MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    public int deleteBusiMcuPlcDeptById(Long id);
    
    ModelBean toModelBean(BusiMcuPlcDept busiMcuPlcDept);
}
