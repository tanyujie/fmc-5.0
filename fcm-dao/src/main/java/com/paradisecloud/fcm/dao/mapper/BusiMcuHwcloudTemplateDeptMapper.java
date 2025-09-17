package com.paradisecloud.fcm.dao.mapper;




import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudTemplateDept;

import java.util.List;

/**
 * Hwcloud.0MCU会议模板的级联部门Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuHwcloudTemplateDeptMapper
{
    /**
     * 查询Hwcloud.0MCU会议模板的级联部门
     * 
     * @param id Hwcloud.0MCU会议模板的级联部门ID
     * @return Hwcloud.0MCU会议模板的级联部门
     */
    public BusiMcuHwcloudTemplateDept selectBusiMcuHwcloudTemplateDeptById(Long id);

    /**
     * 查询Hwcloud.0MCU会议模板的级联部门列表
     * 
     * @param busiMcuHwcloudTemplateDept Hwcloud.0MCU会议模板的级联部门
     * @return Hwcloud.0MCU会议模板的级联部门集合
     */
    public List<BusiMcuHwcloudTemplateDept> selectBusiMcuHwcloudTemplateDeptList(BusiMcuHwcloudTemplateDept busiMcuHwcloudTemplateDept);

    /**
     * 新增Hwcloud.0MCU会议模板的级联部门
     * 
     * @param busiMcuHwcloudTemplateDept Hwcloud.0MCU会议模板的级联部门
     * @return 结果
     */
    public int insertBusiMcuHwcloudTemplateDept(BusiMcuHwcloudTemplateDept busiMcuHwcloudTemplateDept);

    /**
     * 修改Hwcloud.0MCU会议模板的级联部门
     * 
     * @param busiMcuHwcloudTemplateDept Hwcloud.0MCU会议模板的级联部门
     * @return 结果
     */
    public int updateBusiMcuHwcloudTemplateDept(BusiMcuHwcloudTemplateDept busiMcuHwcloudTemplateDept);

    /**
     * 删除Hwcloud.0MCU会议模板的级联部门
     * 
     * @param id Hwcloud.0MCU会议模板的级联部门ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudTemplateDeptById(Long id);

    /**
     * 批量删除Hwcloud.0MCU会议模板的级联部门
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudTemplateDeptByIds(Long[] ids);
}
