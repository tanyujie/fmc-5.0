package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiSmc2Dept;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author lilinhai
 * @date 2023-05-17
 */
public interface BusiSmc2DeptMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public BusiSmc2Dept selectBusiSmc2DeptById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param busiSmc2Dept 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<BusiSmc2Dept> selectBusiSmc2DeptList(BusiSmc2Dept busiSmc2Dept);

    /**
     * 新增【请填写功能名称】
     * 
     * @param busiSmc2Dept 【请填写功能名称】
     * @return 结果
     */
    public int insertBusiSmc2Dept(BusiSmc2Dept busiSmc2Dept);

    /**
     * 修改【请填写功能名称】
     * 
     * @param busiSmc2Dept 【请填写功能名称】
     * @return 结果
     */
    public int updateBusiSmc2Dept(BusiSmc2Dept busiSmc2Dept);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiSmc2DeptById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiSmc2DeptByIds(Long[] ids);
}
