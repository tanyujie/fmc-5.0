package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiMcuPlcTemplateDept;

/**
 * 紫荆MCU会议模板的级联部门Mapper接口
 * 
 * @author lilinhai
 * @date 2021-01-29
 */
public interface BusiMcuPlcTemplateDeptMapper 
{
    /**
     * 查询会议模板的级联部门
     * 
     * @param id 会议模板的级联部门ID
     * @return 会议模板的级联部门
     */
    public BusiMcuPlcTemplateDept selectBusiMcuPlcTemplateDeptById(Long id);

    /**
     * 查询会议模板的级联部门列表
     * 
     * @param busiMcuPlcTemplateDept 会议模板的级联部门
     * @return 会议模板的级联部门集合
     */
    public List<BusiMcuPlcTemplateDept> selectBusiMcuPlcTemplateDeptList(BusiMcuPlcTemplateDept busiMcuPlcTemplateDept);

    /**
     * 新增会议模板的级联部门
     * 
     * @param busiMcuPlcTemplateDept 会议模板的级联部门
     * @return 结果
     */
    public int insertBusiMcuPlcTemplateDept(BusiMcuPlcTemplateDept busiMcuPlcTemplateDept);

    /**
     * 修改会议模板的级联部门
     * 
     * @param busiMcuPlcTemplateDept 会议模板的级联部门
     * @return 结果
     */
    public int updateBusiMcuPlcTemplateDept(BusiMcuPlcTemplateDept busiMcuPlcTemplateDept);

    /**
     * 删除会议模板的级联部门
     * 
     * @param id 会议模板的级联部门ID
     * @return 结果
     */
    public int deleteBusiMcuPlcTemplateDeptById(Long id);

    /**
     * 批量删除会议模板的级联部门
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuPlcTemplateDeptByIds(Long[] ids);
}
