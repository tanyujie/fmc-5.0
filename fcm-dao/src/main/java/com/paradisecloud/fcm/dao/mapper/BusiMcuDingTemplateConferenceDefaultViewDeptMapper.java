package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuDingTemplateConferenceDefaultViewDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Ding.0MCU默认视图的部门显示顺序Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuDingTemplateConferenceDefaultViewDeptMapper
{
    /**
     * 查询Ding.0MCU默认视图的部门显示顺序
     * 
     * @param id Ding.0MCU默认视图的部门显示顺序ID
     * @return Ding.0MCU默认视图的部门显示顺序
     */
    public BusiMcuDingTemplateConferenceDefaultViewDept selectBusiMcuDingTemplateConferenceDefaultViewDeptById(Long id);

    /**
     * 查询Ding.0MCU默认视图的部门显示顺序列表
     * 
     * @param busiMcuDingTemplateConferenceDefaultViewDept Ding.0MCU默认视图的部门显示顺序
     * @return Ding.0MCU默认视图的部门显示顺序集合
     */
    public List<BusiMcuDingTemplateConferenceDefaultViewDept> selectBusiMcuDingTemplateConferenceDefaultViewDeptList(BusiMcuDingTemplateConferenceDefaultViewDept busiMcuDingTemplateConferenceDefaultViewDept);

    /**
     * 新增Ding.0MCU默认视图的部门显示顺序
     * 
     * @param busiMcuDingTemplateConferenceDefaultViewDept Ding.0MCU默认视图的部门显示顺序
     * @return 结果
     */
    public int insertBusiMcuDingTemplateConferenceDefaultViewDept(BusiMcuDingTemplateConferenceDefaultViewDept busiMcuDingTemplateConferenceDefaultViewDept);

    /**
     * 修改Ding.0MCU默认视图的部门显示顺序
     * 
     * @param busiMcuDingTemplateConferenceDefaultViewDept Ding.0MCU默认视图的部门显示顺序
     * @return 结果
     */
    public int updateBusiMcuDingTemplateConferenceDefaultViewDept(BusiMcuDingTemplateConferenceDefaultViewDept busiMcuDingTemplateConferenceDefaultViewDept);

    /**
     * 删除Ding.0MCU默认视图的部门显示顺序
     * 
     * @param id Ding.0MCU默认视图的部门显示顺序ID
     * @return 结果
     */
    public int deleteBusiMcuDingTemplateConferenceDefaultViewDeptById(Long id);

    /**
     * 批量删除Ding.0MCU默认视图的部门显示顺序
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuDingTemplateConferenceDefaultViewDeptByIds(Long[] ids);

    void deleteBusiMcuDingTemplateConferenceDefaultViewDeptByTemplateConferenceId(Long id);

    void deleteBusiMcuDingTemplateConferenceDefaultViewDeptByTemplateConferenceIdAndDeptId(@Param("templateConferenceId") Long templateConferenceId, @Param("deptId") Long deptId);
}
