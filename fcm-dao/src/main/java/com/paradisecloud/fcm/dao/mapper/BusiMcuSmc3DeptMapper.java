package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuSmc3Dept;

import java.util.List;

/**
 * SMC3.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）Mapper接口
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
public interface BusiMcuSmc3DeptMapper 
{
    /**
     * 查询SMC3.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param id SMC3.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return SMC3.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     */
    public BusiMcuSmc3Dept selectBusiMcuSmc3DeptById(Long id);

    /**
     * 查询SMC3.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）列表
     * 
     * @param busiMcuSmc3Dept SMC3.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return SMC3.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）集合
     */
    public List<BusiMcuSmc3Dept> selectBusiMcuSmc3DeptList(BusiMcuSmc3Dept busiMcuSmc3Dept);

    /**
     * 新增SMC3.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuSmc3Dept SMC3.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    public int insertBusiMcuSmc3Dept(BusiMcuSmc3Dept busiMcuSmc3Dept);

    /**
     * 修改SMC3.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuSmc3Dept SMC3.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    public int updateBusiMcuSmc3Dept(BusiMcuSmc3Dept busiMcuSmc3Dept);

    /**
     * 删除SMC3.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param id SMC3.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3DeptById(Long id);

    /**
     * 批量删除SMC3.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3DeptByIds(Long[] ids);
}
