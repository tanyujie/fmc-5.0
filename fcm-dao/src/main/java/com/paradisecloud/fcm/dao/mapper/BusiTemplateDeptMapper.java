package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiTemplateDept;

/**
 * 会议模板的级联部门Mapper接口
 * 
 * @author lilinhai
 * @date 2021-01-29
 */
public interface BusiTemplateDeptMapper 
{
    /**
     * 查询会议模板的级联部门
     * 
     * @param id 会议模板的级联部门ID
     * @return 会议模板的级联部门
     */
    public BusiTemplateDept selectBusiTemplateDeptById(Long id);

    /**
     * 查询会议模板的级联部门列表
     * 
     * @param busiTemplateDept 会议模板的级联部门
     * @return 会议模板的级联部门集合
     */
    public List<BusiTemplateDept> selectBusiTemplateDeptList(BusiTemplateDept busiTemplateDept);

    /**
     * 新增会议模板的级联部门
     * 
     * @param busiTemplateDept 会议模板的级联部门
     * @return 结果
     */
    public int insertBusiTemplateDept(BusiTemplateDept busiTemplateDept);

    /**
     * 修改会议模板的级联部门
     * 
     * @param busiTemplateDept 会议模板的级联部门
     * @return 结果
     */
    public int updateBusiTemplateDept(BusiTemplateDept busiTemplateDept);

    /**
     * 删除会议模板的级联部门
     * 
     * @param id 会议模板的级联部门ID
     * @return 结果
     */
    public int deleteBusiTemplateDeptById(Long id);

    /**
     * 批量删除会议模板的级联部门
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiTemplateDeptByIds(Long[] ids);
}
