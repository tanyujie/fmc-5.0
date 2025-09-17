package com.paradisecloud.fcm.smc2.setvice2.interfaces;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2Dept;

import java.util.List;

/**
 * MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）Service接口
 * 
 * @author lilinhai
 * @date 2021-02-17
 */
public interface IBusiMcuSmc2DeptService 
{
    
    /**
     * 查询MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param id MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     */
    public ModelBean selectBusiMcuSmc2DeptById(Long id);
    
    ModelBean selectBusiMcuSmc2DeptByDeptId(Long deptId);

    /**
     * 查询MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）列表
     * 
     * @param busiMcuSmc2Dept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）集合
     */
    public List<ModelBean> selectBusiMcuSmc2DeptList(BusiMcuSmc2Dept busiMcuSmc2Dept);

    /**
     * 新增MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuSmc2Dept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    public int insertBusiMcuSmc2Dept(BusiMcuSmc2Dept busiMcuSmc2Dept);

    /**
     * 修改MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuSmc2Dept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    public int updateBusiMcuSmc2Dept(BusiMcuSmc2Dept busiMcuSmc2Dept);

    /**
     * 批量删除MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param ids 需要删除的MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2DeptByIds(Long[] ids);

    /**
     * 删除MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）信息
     * 
     * @param id MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2DeptById(Long id);
    
    ModelBean toModelBean(BusiMcuSmc2Dept busiMcuSmc2Dept);
}
