package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplateConferenceDefaultViewDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SMC3.0MCU默认视图的部门显示顺序Mapper接口
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
public interface BusiMcuSmc3TemplateConferenceDefaultViewDeptMapper 
{
    /**
     * 查询SMC3.0MCU默认视图的部门显示顺序
     * 
     * @param id SMC3.0MCU默认视图的部门显示顺序ID
     * @return SMC3.0MCU默认视图的部门显示顺序
     */
    public BusiMcuSmc3TemplateConferenceDefaultViewDept selectBusiMcuSmc3TemplateConferenceDefaultViewDeptById(Long id);

    /**
     * 查询SMC3.0MCU默认视图的部门显示顺序列表
     * 
     * @param busiMcuSmc3TemplateConferenceDefaultViewDept SMC3.0MCU默认视图的部门显示顺序
     * @return SMC3.0MCU默认视图的部门显示顺序集合
     */
    public List<BusiMcuSmc3TemplateConferenceDefaultViewDept> selectBusiMcuSmc3TemplateConferenceDefaultViewDeptList(BusiMcuSmc3TemplateConferenceDefaultViewDept busiMcuSmc3TemplateConferenceDefaultViewDept);

    /**
     * 新增SMC3.0MCU默认视图的部门显示顺序
     * 
     * @param busiMcuSmc3TemplateConferenceDefaultViewDept SMC3.0MCU默认视图的部门显示顺序
     * @return 结果
     */
    public int insertBusiMcuSmc3TemplateConferenceDefaultViewDept(BusiMcuSmc3TemplateConferenceDefaultViewDept busiMcuSmc3TemplateConferenceDefaultViewDept);

    /**
     * 修改SMC3.0MCU默认视图的部门显示顺序
     * 
     * @param busiMcuSmc3TemplateConferenceDefaultViewDept SMC3.0MCU默认视图的部门显示顺序
     * @return 结果
     */
    public int updateBusiMcuSmc3TemplateConferenceDefaultViewDept(BusiMcuSmc3TemplateConferenceDefaultViewDept busiMcuSmc3TemplateConferenceDefaultViewDept);

    /**
     * 删除SMC3.0MCU默认视图的部门显示顺序
     * 
     * @param id SMC3.0MCU默认视图的部门显示顺序ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3TemplateConferenceDefaultViewDeptById(Long id);

    /**
     * 批量删除SMC3.0MCU默认视图的部门显示顺序
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3TemplateConferenceDefaultViewDeptByIds(Long[] ids);

    void deleteBusiMcuSmc3TemplateConferenceDefaultViewDeptByTemplateConferenceId(Long id);

    void deleteBusiMcuSmc3TemplateConferenceDefaultViewDeptByTemplateConferenceIdAndDeptId(@Param("templateConferenceId") Long templateConferenceId, @Param("deptId") Long deptId);
}
