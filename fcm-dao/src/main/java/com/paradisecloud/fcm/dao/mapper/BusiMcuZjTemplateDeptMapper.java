package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiMcuZjTemplateDept;

/**
 * 紫荆MCU会议模板的级联部门Mapper接口
 * 
 * @author lilinhai
 * @date 2021-01-29
 */
public interface BusiMcuZjTemplateDeptMapper 
{
    /**
     * 查询会议模板的级联部门
     * 
     * @param id 会议模板的级联部门ID
     * @return 会议模板的级联部门
     */
    public BusiMcuZjTemplateDept selectBusiMcuZjTemplateDeptById(Long id);

    /**
     * 查询会议模板的级联部门列表
     * 
     * @param busiMcuZjTemplateDept 会议模板的级联部门
     * @return 会议模板的级联部门集合
     */
    public List<BusiMcuZjTemplateDept> selectBusiMcuZjTemplateDeptList(BusiMcuZjTemplateDept busiMcuZjTemplateDept);

    /**
     * 新增会议模板的级联部门
     * 
     * @param busiMcuZjTemplateDept 会议模板的级联部门
     * @return 结果
     */
    public int insertBusiMcuZjTemplateDept(BusiMcuZjTemplateDept busiMcuZjTemplateDept);

    /**
     * 修改会议模板的级联部门
     * 
     * @param busiMcuZjTemplateDept 会议模板的级联部门
     * @return 结果
     */
    public int updateBusiMcuZjTemplateDept(BusiMcuZjTemplateDept busiMcuZjTemplateDept);

    /**
     * 删除会议模板的级联部门
     * 
     * @param id 会议模板的级联部门ID
     * @return 结果
     */
    public int deleteBusiMcuZjTemplateDeptById(Long id);

    /**
     * 批量删除会议模板的级联部门
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuZjTemplateDeptByIds(Long[] ids);
}
