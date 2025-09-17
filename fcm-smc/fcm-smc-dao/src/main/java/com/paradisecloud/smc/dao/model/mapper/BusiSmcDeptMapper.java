package com.paradisecloud.smc.dao.model.mapper;

import com.paradisecloud.smc.dao.model.BusiSmcDept;
import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author liuxilong
 * @date 2022-08-25
 */
public interface BusiSmcDeptMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public BusiSmcDept selectBusiSmcDeptById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param busiSmcDept 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<BusiSmcDept> selectBusiSmcDeptList(BusiSmcDept busiSmcDept);

    /**
     * 新增【请填写功能名称】
     * 
     * @param busiSmcDept 【请填写功能名称】
     * @return 结果
     */
    public int insertBusiSmcDept(BusiSmcDept busiSmcDept);

    /**
     * 修改【请填写功能名称】
     * 
     * @param busiSmcDept 【请填写功能名称】
     * @return 结果
     */
    public int updateBusiSmcDept(BusiSmcDept busiSmcDept);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiSmcDeptById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiSmcDeptByIds(Long[] ids);
}
