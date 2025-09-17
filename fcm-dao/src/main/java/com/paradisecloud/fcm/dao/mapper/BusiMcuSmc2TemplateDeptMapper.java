package com.paradisecloud.fcm.dao.mapper;


import com.paradisecloud.fcm.dao.model.BusiMcuSmc2TemplateDept;

import java.util.List;

/**
 * SMC2.0MCU会议模板的级联部门Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuSmc2TemplateDeptMapper
{
    /**
     * 查询SMC2.0MCU会议模板的级联部门
     * 
     * @param id SMC2.0MCU会议模板的级联部门ID
     * @return SMC2.0MCU会议模板的级联部门
     */
    public BusiMcuSmc2TemplateDept selectBusiMcuSmc2TemplateDeptById(Long id);

    /**
     * 查询SMC2.0MCU会议模板的级联部门列表
     * 
     * @param busiMcuSmc2TemplateDept SMC2.0MCU会议模板的级联部门
     * @return SMC2.0MCU会议模板的级联部门集合
     */
    public List<BusiMcuSmc2TemplateDept> selectBusiMcuSmc2TemplateDeptList(BusiMcuSmc2TemplateDept busiMcuSmc2TemplateDept);

    /**
     * 新增SMC2.0MCU会议模板的级联部门
     * 
     * @param busiMcuSmc2TemplateDept SMC2.0MCU会议模板的级联部门
     * @return 结果
     */
    public int insertBusiMcuSmc2TemplateDept(BusiMcuSmc2TemplateDept busiMcuSmc2TemplateDept);

    /**
     * 修改SMC2.0MCU会议模板的级联部门
     * 
     * @param busiMcuSmc2TemplateDept SMC2.0MCU会议模板的级联部门
     * @return 结果
     */
    public int updateBusiMcuSmc2TemplateDept(BusiMcuSmc2TemplateDept busiMcuSmc2TemplateDept);

    /**
     * 删除SMC2.0MCU会议模板的级联部门
     * 
     * @param id SMC2.0MCU会议模板的级联部门ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2TemplateDeptById(Long id);

    /**
     * 批量删除SMC2.0MCU会议模板的级联部门
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2TemplateDeptByIds(Long[] ids);
}
