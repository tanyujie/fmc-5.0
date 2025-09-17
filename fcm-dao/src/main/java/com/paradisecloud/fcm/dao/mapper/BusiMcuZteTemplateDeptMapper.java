package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuZteTemplateDept;

import java.util.List;

/**
 * 中兴MCU会议模板的级联部门Mapper接口
 * 
 * @author lilinhai
 * @date 2024-04-09
 */
public interface BusiMcuZteTemplateDeptMapper 
{
    /**
     * 查询中兴MCU会议模板的级联部门
     * 
     * @param id 中兴MCU会议模板的级联部门ID
     * @return 中兴MCU会议模板的级联部门
     */
    public BusiMcuZteTemplateDept selectBusiMcuZteTemplateDeptById(Long id);

    /**
     * 查询中兴MCU会议模板的级联部门列表
     * 
     * @param busiMcuZteTemplateDept 中兴MCU会议模板的级联部门
     * @return 中兴MCU会议模板的级联部门集合
     */
    public List<BusiMcuZteTemplateDept> selectBusiMcuZteTemplateDeptList(BusiMcuZteTemplateDept busiMcuZteTemplateDept);

    /**
     * 新增中兴MCU会议模板的级联部门
     * 
     * @param busiMcuZteTemplateDept 中兴MCU会议模板的级联部门
     * @return 结果
     */
    public int insertBusiMcuZteTemplateDept(BusiMcuZteTemplateDept busiMcuZteTemplateDept);

    /**
     * 修改中兴MCU会议模板的级联部门
     * 
     * @param busiMcuZteTemplateDept 中兴MCU会议模板的级联部门
     * @return 结果
     */
    public int updateBusiMcuZteTemplateDept(BusiMcuZteTemplateDept busiMcuZteTemplateDept);

    /**
     * 删除中兴MCU会议模板的级联部门
     * 
     * @param id 中兴MCU会议模板的级联部门ID
     * @return 结果
     */
    public int deleteBusiMcuZteTemplateDeptById(Long id);

    /**
     * 批量删除中兴MCU会议模板的级联部门
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuZteTemplateDeptByIds(Long[] ids);
}
