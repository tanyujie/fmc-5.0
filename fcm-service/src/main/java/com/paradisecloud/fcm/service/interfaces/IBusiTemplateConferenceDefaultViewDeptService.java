package com.paradisecloud.fcm.service.interfaces;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiTemplateConferenceDefaultViewDept;

/**
 * 默认视图的部门显示顺序Service接口
 * 
 * @author lilinhai
 * @date 2021-04-08
 */
public interface IBusiTemplateConferenceDefaultViewDeptService 
{
    /**
     * 查询默认视图的部门显示顺序
     * 
     * @param id 默认视图的部门显示顺序ID
     * @return 默认视图的部门显示顺序
     */
    public BusiTemplateConferenceDefaultViewDept selectBusiTemplateConferenceDefaultViewDeptById(Long id);

    /**
     * 查询默认视图的部门显示顺序列表
     * 
     * @param busiTemplateConferenceDefaultViewDept 默认视图的部门显示顺序
     * @return 默认视图的部门显示顺序集合
     */
    public List<BusiTemplateConferenceDefaultViewDept> selectBusiTemplateConferenceDefaultViewDeptList(BusiTemplateConferenceDefaultViewDept busiTemplateConferenceDefaultViewDept);

    /**
     * 新增默认视图的部门显示顺序
     * 
     * @param busiTemplateConferenceDefaultViewDept 默认视图的部门显示顺序
     * @return 结果
     */
    public int insertBusiTemplateConferenceDefaultViewDept(BusiTemplateConferenceDefaultViewDept busiTemplateConferenceDefaultViewDept);

    /**
     * 修改默认视图的部门显示顺序
     * 
     * @param busiTemplateConferenceDefaultViewDept 默认视图的部门显示顺序
     * @return 结果
     */
    public int updateBusiTemplateConferenceDefaultViewDept(BusiTemplateConferenceDefaultViewDept busiTemplateConferenceDefaultViewDept);

    /**
     * 批量删除默认视图的部门显示顺序
     * 
     * @param ids 需要删除的默认视图的部门显示顺序ID
     * @return 结果
     */
    public int deleteBusiTemplateConferenceDefaultViewDeptByIds(Long[] ids);

    /**
     * 删除默认视图的部门显示顺序信息
     * 
     * @param id 默认视图的部门显示顺序ID
     * @return 结果
     */
    public int deleteBusiTemplateConferenceDefaultViewDeptById(Long id);
}
