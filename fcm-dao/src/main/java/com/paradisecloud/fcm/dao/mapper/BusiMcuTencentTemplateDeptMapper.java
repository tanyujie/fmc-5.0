package com.paradisecloud.fcm.dao.mapper;




import com.paradisecloud.fcm.dao.model.BusiMcuTencentTemplateDept;

import java.util.List;

/**
 * Tencent.0MCU会议模板的级联部门Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuTencentTemplateDeptMapper
{
    /**
     * 查询Tencent.0MCU会议模板的级联部门
     * 
     * @param id Tencent.0MCU会议模板的级联部门ID
     * @return Tencent.0MCU会议模板的级联部门
     */
    public BusiMcuTencentTemplateDept selectBusiMcuTencentTemplateDeptById(Long id);

    /**
     * 查询Tencent.0MCU会议模板的级联部门列表
     * 
     * @param busiMcuTencentTemplateDept Tencent.0MCU会议模板的级联部门
     * @return Tencent.0MCU会议模板的级联部门集合
     */
    public List<BusiMcuTencentTemplateDept> selectBusiMcuTencentTemplateDeptList(BusiMcuTencentTemplateDept busiMcuTencentTemplateDept);

    /**
     * 新增Tencent.0MCU会议模板的级联部门
     * 
     * @param busiMcuTencentTemplateDept Tencent.0MCU会议模板的级联部门
     * @return 结果
     */
    public int insertBusiMcuTencentTemplateDept(BusiMcuTencentTemplateDept busiMcuTencentTemplateDept);

    /**
     * 修改Tencent.0MCU会议模板的级联部门
     * 
     * @param busiMcuTencentTemplateDept Tencent.0MCU会议模板的级联部门
     * @return 结果
     */
    public int updateBusiMcuTencentTemplateDept(BusiMcuTencentTemplateDept busiMcuTencentTemplateDept);

    /**
     * 删除Tencent.0MCU会议模板的级联部门
     * 
     * @param id Tencent.0MCU会议模板的级联部门ID
     * @return 结果
     */
    public int deleteBusiMcuTencentTemplateDeptById(Long id);

    /**
     * 批量删除Tencent.0MCU会议模板的级联部门
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuTencentTemplateDeptByIds(Long[] ids);
}
