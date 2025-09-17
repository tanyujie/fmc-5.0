package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplateDept;

import java.util.List;

/**
 * SMC3.0MCU会议模板的级联部门Mapper接口
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
public interface BusiMcuSmc3TemplateDeptMapper 
{
    /**
     * 查询SMC3.0MCU会议模板的级联部门
     * 
     * @param id SMC3.0MCU会议模板的级联部门ID
     * @return SMC3.0MCU会议模板的级联部门
     */
    public BusiMcuSmc3TemplateDept selectBusiMcuSmc3TemplateDeptById(Long id);

    /**
     * 查询SMC3.0MCU会议模板的级联部门列表
     * 
     * @param busiMcuSmc3TemplateDept SMC3.0MCU会议模板的级联部门
     * @return SMC3.0MCU会议模板的级联部门集合
     */
    public List<BusiMcuSmc3TemplateDept> selectBusiMcuSmc3TemplateDeptList(BusiMcuSmc3TemplateDept busiMcuSmc3TemplateDept);

    /**
     * 新增SMC3.0MCU会议模板的级联部门
     * 
     * @param busiMcuSmc3TemplateDept SMC3.0MCU会议模板的级联部门
     * @return 结果
     */
    public int insertBusiMcuSmc3TemplateDept(BusiMcuSmc3TemplateDept busiMcuSmc3TemplateDept);

    /**
     * 修改SMC3.0MCU会议模板的级联部门
     * 
     * @param busiMcuSmc3TemplateDept SMC3.0MCU会议模板的级联部门
     * @return 结果
     */
    public int updateBusiMcuSmc3TemplateDept(BusiMcuSmc3TemplateDept busiMcuSmc3TemplateDept);

    /**
     * 删除SMC3.0MCU会议模板的级联部门
     * 
     * @param id SMC3.0MCU会议模板的级联部门ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3TemplateDeptById(Long id);

    /**
     * 批量删除SMC3.0MCU会议模板的级联部门
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3TemplateDeptByIds(Long[] ids);
}
