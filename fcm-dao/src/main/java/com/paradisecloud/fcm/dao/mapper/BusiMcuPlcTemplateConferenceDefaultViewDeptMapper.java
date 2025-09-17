package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.paradisecloud.fcm.dao.model.BusiMcuPlcTemplateConferenceDefaultViewDept;

/**
 * 默认视图的部门显示顺序Mapper接口
 * 
 * @author lilinhai
 * @date 2021-04-08
 */
public interface BusiMcuPlcTemplateConferenceDefaultViewDeptMapper 
{
    /**
     * 查询默认视图的部门显示顺序
     * 
     * @param id 默认视图的部门显示顺序ID
     * @return 默认视图的部门显示顺序
     */
    public BusiMcuPlcTemplateConferenceDefaultViewDept selectBusiMcuPlcTemplateConferenceDefaultViewDeptById(Long id);

    /**
     * 查询默认视图的部门显示顺序列表
     * 
     * @param busiTemplateConferenceDefaultViewDept 默认视图的部门显示顺序
     * @return 默认视图的部门显示顺序集合
     */
    public List<BusiMcuPlcTemplateConferenceDefaultViewDept> selectBusiMcuPlcTemplateConferenceDefaultViewDeptList(BusiMcuPlcTemplateConferenceDefaultViewDept busiTemplateConferenceDefaultViewDept);

    /**
     * 新增默认视图的部门显示顺序
     * 
     * @param busiTemplateConferenceDefaultViewDept 默认视图的部门显示顺序
     * @return 结果
     */
    public int insertBusiMcuPlcTemplateConferenceDefaultViewDept(BusiMcuPlcTemplateConferenceDefaultViewDept busiTemplateConferenceDefaultViewDept);

    /**
     * 修改默认视图的部门显示顺序
     * 
     * @param busiTemplateConferenceDefaultViewDept 默认视图的部门显示顺序
     * @return 结果
     */
    public int updateBusiMcuPlcTemplateConferenceDefaultViewDept(BusiMcuPlcTemplateConferenceDefaultViewDept busiTemplateConferenceDefaultViewDept);

    /**
     * 删除默认视图的部门显示顺序
     * 
     * @param id 默认视图的部门显示顺序ID
     * @return 结果
     */
    public int deleteBusiMcuPlcTemplateConferenceDefaultViewDeptById(Long id);
    
    /**
     * 根据会议模板ID批量删除默认视图的部门信息
     * @author lilinhai
     * @since 2021-04-08 16:21 
     * @param templateConferenceId
     * @return int
     */
    public int deleteBusiMcuPlcTemplateConferenceDefaultViewDeptByTemplateConferenceId(Long templateConferenceId);
    
    int deleteBusiMcuPlcTemplateConferenceDefaultViewDeptByTemplateConferenceIdAndDeptId(@Param("templateConferenceId") Long templateConferenceId, @Param("deptId") Long deptId);

    /**
     * 批量删除默认视图的部门显示顺序
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuPlcTemplateConferenceDefaultViewDeptByIds(Long[] ids);
}
