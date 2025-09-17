package com.paradisecloud.smc3.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiPicker;

import java.util.List;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author lilinhai
 * @date 2024-06-13
 */
public interface IBusiPickerService 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public BusiPicker selectBusiPickerById(Integer id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param busiPicker 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<BusiPicker> selectBusiPickerList(BusiPicker busiPicker);

    /**
     * 新增【请填写功能名称】
     * 
     * @param busiPicker 【请填写功能名称】
     * @return 结果
     */
    public int insertBusiPicker(BusiPicker busiPicker);

    /**
     * 修改【请填写功能名称】
     * 
     * @param busiPicker 【请填写功能名称】
     * @return 结果
     */
    public int updateBusiPicker(BusiPicker busiPicker);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiPickerByIds(Integer[] ids);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiPickerById(Integer id);

    int updateBusiPickerDeptIds(List<Integer> apiDepts,List<Integer> accessDepts);


}
