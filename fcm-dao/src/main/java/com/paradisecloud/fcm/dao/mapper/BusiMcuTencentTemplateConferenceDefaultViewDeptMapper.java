package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuTencentTemplateConferenceDefaultViewDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Tencent.0MCU默认视图的部门显示顺序Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuTencentTemplateConferenceDefaultViewDeptMapper
{
    /**
     * 查询Tencent.0MCU默认视图的部门显示顺序
     * 
     * @param id Tencent.0MCU默认视图的部门显示顺序ID
     * @return Tencent.0MCU默认视图的部门显示顺序
     */
    public BusiMcuTencentTemplateConferenceDefaultViewDept selectBusiMcuTencentTemplateConferenceDefaultViewDeptById(Long id);

    /**
     * 查询Tencent.0MCU默认视图的部门显示顺序列表
     * 
     * @param busiMcuTencentTemplateConferenceDefaultViewDept Tencent.0MCU默认视图的部门显示顺序
     * @return Tencent.0MCU默认视图的部门显示顺序集合
     */
    public List<BusiMcuTencentTemplateConferenceDefaultViewDept> selectBusiMcuTencentTemplateConferenceDefaultViewDeptList(BusiMcuTencentTemplateConferenceDefaultViewDept busiMcuTencentTemplateConferenceDefaultViewDept);

    /**
     * 新增Tencent.0MCU默认视图的部门显示顺序
     * 
     * @param busiMcuTencentTemplateConferenceDefaultViewDept Tencent.0MCU默认视图的部门显示顺序
     * @return 结果
     */
    public int insertBusiMcuTencentTemplateConferenceDefaultViewDept(BusiMcuTencentTemplateConferenceDefaultViewDept busiMcuTencentTemplateConferenceDefaultViewDept);

    /**
     * 修改Tencent.0MCU默认视图的部门显示顺序
     * 
     * @param busiMcuTencentTemplateConferenceDefaultViewDept Tencent.0MCU默认视图的部门显示顺序
     * @return 结果
     */
    public int updateBusiMcuTencentTemplateConferenceDefaultViewDept(BusiMcuTencentTemplateConferenceDefaultViewDept busiMcuTencentTemplateConferenceDefaultViewDept);

    /**
     * 删除Tencent.0MCU默认视图的部门显示顺序
     * 
     * @param id Tencent.0MCU默认视图的部门显示顺序ID
     * @return 结果
     */
    public int deleteBusiMcuTencentTemplateConferenceDefaultViewDeptById(Long id);

    /**
     * 批量删除Tencent.0MCU默认视图的部门显示顺序
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuTencentTemplateConferenceDefaultViewDeptByIds(Long[] ids);

    void deleteBusiMcuTencentTemplateConferenceDefaultViewDeptByTemplateConferenceId(Long id);

    void deleteBusiMcuTencentTemplateConferenceDefaultViewDeptByTemplateConferenceIdAndDeptId(@Param("templateConferenceId") Long templateConferenceId, @Param("deptId") Long deptId);
}
