package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiMcuKdcTemplateDept;

/**
 * 紫荆MCU会议模板的级联部门Mapper接口
 * 
 * @author lilinhai
 * @date 2021-01-29
 */
public interface BusiMcuKdcTemplateDeptMapper
{
    /**
     * 查询会议模板的级联部门
     * 
     * @param id 会议模板的级联部门ID
     * @return 会议模板的级联部门
     */
    public BusiMcuKdcTemplateDept selectBusiMcuKdcTemplateDeptById(Long id);

    /**
     * 查询会议模板的级联部门列表
     * 
     * @param busiMcuKdcTemplateDept 会议模板的级联部门
     * @return 会议模板的级联部门集合
     */
    public List<BusiMcuKdcTemplateDept> selectBusiMcuKdcTemplateDeptList(BusiMcuKdcTemplateDept busiMcuKdcTemplateDept);

    /**
     * 新增会议模板的级联部门
     * 
     * @param busiMcuKdcTemplateDept 会议模板的级联部门
     * @return 结果
     */
    public int insertBusiMcuKdcTemplateDept(BusiMcuKdcTemplateDept busiMcuKdcTemplateDept);

    /**
     * 修改会议模板的级联部门
     * 
     * @param busiMcuKdcTemplateDept 会议模板的级联部门
     * @return 结果
     */
    public int updateBusiMcuKdcTemplateDept(BusiMcuKdcTemplateDept busiMcuKdcTemplateDept);

    /**
     * 删除会议模板的级联部门
     * 
     * @param id 会议模板的级联部门ID
     * @return 结果
     */
    public int deleteBusiMcuKdcTemplateDeptById(Long id);

    /**
     * 批量删除会议模板的级联部门
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuKdcTemplateDeptByIds(Long[] ids);
}
