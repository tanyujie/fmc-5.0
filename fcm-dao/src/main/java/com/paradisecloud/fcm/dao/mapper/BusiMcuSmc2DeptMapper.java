package com.paradisecloud.fcm.dao.mapper;


import com.paradisecloud.fcm.dao.model.BusiMcuSmc2Dept;

import java.util.List;

/**
 * SMC2.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuSmc2DeptMapper
{
    /**
     * 查询SMC2.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param id SMC2.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return SMC2.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     */
    public BusiMcuSmc2Dept selectBusiMcuSmc2DeptById(Long id);

    /**
     * 查询SMC2.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）列表
     * 
     * @param busiMcuSmc2Dept SMC2.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return SMC2.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）集合
     */
    public List<BusiMcuSmc2Dept> selectBusiMcuSmc2DeptList(BusiMcuSmc2Dept busiMcuSmc2Dept);

    /**
     * 新增SMC2.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuSmc2Dept SMC2.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    public int insertBusiMcuSmc2Dept(BusiMcuSmc2Dept busiMcuSmc2Dept);

    /**
     * 修改SMC2.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuSmc2Dept SMC2.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    public int updateBusiMcuSmc2Dept(BusiMcuSmc2Dept busiMcuSmc2Dept);

    /**
     * 删除SMC2.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param id SMC2.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2DeptById(Long id);

    /**
     * 批量删除SMC2.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2DeptByIds(Long[] ids);
}
