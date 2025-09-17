package com.paradisecloud.fcm.dao.mapper;




import com.paradisecloud.fcm.dao.model.BusiMcuDingTemplateDept;

import java.util.List;

/**
 * Ding.0MCU会议模板的级联部门Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuDingTemplateDeptMapper
{
    /**
     * 查询Ding.0MCU会议模板的级联部门
     * 
     * @param id Ding.0MCU会议模板的级联部门ID
     * @return Ding.0MCU会议模板的级联部门
     */
    public BusiMcuDingTemplateDept selectBusiMcuDingTemplateDeptById(Long id);

    /**
     * 查询Ding.0MCU会议模板的级联部门列表
     * 
     * @param busiMcuDingTemplateDept Ding.0MCU会议模板的级联部门
     * @return Ding.0MCU会议模板的级联部门集合
     */
    public List<BusiMcuDingTemplateDept> selectBusiMcuDingTemplateDeptList(BusiMcuDingTemplateDept busiMcuDingTemplateDept);

    /**
     * 新增Ding.0MCU会议模板的级联部门
     * 
     * @param busiMcuDingTemplateDept Ding.0MCU会议模板的级联部门
     * @return 结果
     */
    public int insertBusiMcuDingTemplateDept(BusiMcuDingTemplateDept busiMcuDingTemplateDept);

    /**
     * 修改Ding.0MCU会议模板的级联部门
     * 
     * @param busiMcuDingTemplateDept Ding.0MCU会议模板的级联部门
     * @return 结果
     */
    public int updateBusiMcuDingTemplateDept(BusiMcuDingTemplateDept busiMcuDingTemplateDept);

    /**
     * 删除Ding.0MCU会议模板的级联部门
     * 
     * @param id Ding.0MCU会议模板的级联部门ID
     * @return 结果
     */
    public int deleteBusiMcuDingTemplateDeptById(Long id);

    /**
     * 批量删除Ding.0MCU会议模板的级联部门
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuDingTemplateDeptByIds(Long[] ids);
}
