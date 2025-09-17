package com.paradisecloud.fcm.service.interfaces;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiTemplateDept;

/**
 * 会议模板的级联部门Service接口
 * 
 * @author lilinhai
 * @date 2021-01-29
 */
public interface IBusiTemplateDeptService 
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
     * 批量删除会议模板的级联部门
     * 
     * @param ids 需要删除的会议模板的级联部门ID
     * @return 结果
     */
    public int deleteBusiTemplateDeptByIds(Long[] ids);

    /**
     * 删除会议模板的级联部门信息
     * 
     * @param id 会议模板的级联部门ID
     * @return 结果
     */
    public int deleteBusiTemplateDeptById(Long id);
}
