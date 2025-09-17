package com.paradisecloud.fcm.telep.service.interfaces;

import com.paradisecloud.fcm.telep.dao.model.BusiTeleDept;

import java.util.List;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author lilinhai
 * @date 2022-10-11
 */
public interface IBusiTeleDeptService 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public BusiTeleDept selectBusiTeleDeptById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param busiTeleDept 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<BusiTeleDept> selectBusiTeleDeptList(BusiTeleDept busiTeleDept);

    /**
     * 新增【请填写功能名称】
     * 
     * @param busiTeleDept 【请填写功能名称】
     * @return 结果
     */
    public int insertBusiTeleDept(BusiTeleDept busiTeleDept);

    /**
     * 修改【请填写功能名称】
     * 
     * @param busiTeleDept 【请填写功能名称】
     * @return 结果
     */
    public int updateBusiTeleDept(BusiTeleDept busiTeleDept);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiTeleDeptByIds(Long[] ids);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiTeleDeptById(Long id);
}
