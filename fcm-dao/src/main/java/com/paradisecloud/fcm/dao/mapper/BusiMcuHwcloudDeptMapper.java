package com.paradisecloud.fcm.dao.mapper;





import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudDept;

import java.util.List;

/**
 * Hwcloud.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuHwcloudDeptMapper
{
    /**
     * 查询Hwcloud.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param id Hwcloud.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return Hwcloud.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     */
    public BusiMcuHwcloudDept selectBusiMcuHwcloudDeptById(Long id);

    /**
     * 查询Hwcloud.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）列表
     * 
     * @param busiMcuHwcloudDept Hwcloud.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return Hwcloud.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）集合
     */
    public List<BusiMcuHwcloudDept> selectBusiMcuHwcloudDeptList(BusiMcuHwcloudDept busiMcuHwcloudDept);

    /**
     * 新增Hwcloud.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuHwcloudDept Hwcloud.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    public int insertBusiMcuHwcloudDept(BusiMcuHwcloudDept busiMcuHwcloudDept);

    /**
     * 修改Hwcloud.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuHwcloudDept Hwcloud.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    public int updateBusiMcuHwcloudDept(BusiMcuHwcloudDept busiMcuHwcloudDept);

    /**
     * 删除Hwcloud.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param id Hwcloud.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudDeptById(Long id);

    /**
     * 批量删除Hwcloud.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudDeptByIds(Long[] ids);
}
