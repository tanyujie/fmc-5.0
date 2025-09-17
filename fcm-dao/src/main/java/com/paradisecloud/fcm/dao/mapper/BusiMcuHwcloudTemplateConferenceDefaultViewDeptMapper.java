package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudTemplateConferenceDefaultViewDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Hwcloud.0MCU默认视图的部门显示顺序Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuHwcloudTemplateConferenceDefaultViewDeptMapper
{
    /**
     * 查询Hwcloud.0MCU默认视图的部门显示顺序
     * 
     * @param id Hwcloud.0MCU默认视图的部门显示顺序ID
     * @return Hwcloud.0MCU默认视图的部门显示顺序
     */
    public BusiMcuHwcloudTemplateConferenceDefaultViewDept selectBusiMcuHwcloudTemplateConferenceDefaultViewDeptById(Long id);

    /**
     * 查询Hwcloud.0MCU默认视图的部门显示顺序列表
     * 
     * @param busiMcuHwcloudTemplateConferenceDefaultViewDept Hwcloud.0MCU默认视图的部门显示顺序
     * @return Hwcloud.0MCU默认视图的部门显示顺序集合
     */
    public List<BusiMcuHwcloudTemplateConferenceDefaultViewDept> selectBusiMcuHwcloudTemplateConferenceDefaultViewDeptList(BusiMcuHwcloudTemplateConferenceDefaultViewDept busiMcuHwcloudTemplateConferenceDefaultViewDept);

    /**
     * 新增Hwcloud.0MCU默认视图的部门显示顺序
     * 
     * @param busiMcuHwcloudTemplateConferenceDefaultViewDept Hwcloud.0MCU默认视图的部门显示顺序
     * @return 结果
     */
    public int insertBusiMcuHwcloudTemplateConferenceDefaultViewDept(BusiMcuHwcloudTemplateConferenceDefaultViewDept busiMcuHwcloudTemplateConferenceDefaultViewDept);

    /**
     * 修改Hwcloud.0MCU默认视图的部门显示顺序
     * 
     * @param busiMcuHwcloudTemplateConferenceDefaultViewDept Hwcloud.0MCU默认视图的部门显示顺序
     * @return 结果
     */
    public int updateBusiMcuHwcloudTemplateConferenceDefaultViewDept(BusiMcuHwcloudTemplateConferenceDefaultViewDept busiMcuHwcloudTemplateConferenceDefaultViewDept);

    /**
     * 删除Hwcloud.0MCU默认视图的部门显示顺序
     * 
     * @param id Hwcloud.0MCU默认视图的部门显示顺序ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudTemplateConferenceDefaultViewDeptById(Long id);

    /**
     * 批量删除Hwcloud.0MCU默认视图的部门显示顺序
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudTemplateConferenceDefaultViewDeptByIds(Long[] ids);

    void deleteBusiMcuHwcloudTemplateConferenceDefaultViewDeptByTemplateConferenceId(Long id);

    void deleteBusiMcuHwcloudTemplateConferenceDefaultViewDeptByTemplateConferenceIdAndDeptId(@Param("templateConferenceId") Long templateConferenceId, @Param("deptId") Long deptId);
}
