package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuSmc2TemplateConferenceDefaultViewDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SMC2.0MCU默认视图的部门显示顺序Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuSmc2TemplateConferenceDefaultViewDeptMapper
{
    /**
     * 查询SMC2.0MCU默认视图的部门显示顺序
     * 
     * @param id SMC2.0MCU默认视图的部门显示顺序ID
     * @return SMC2.0MCU默认视图的部门显示顺序
     */
    public BusiMcuSmc2TemplateConferenceDefaultViewDept selectBusiMcuSmc2TemplateConferenceDefaultViewDeptById(Long id);

    /**
     * 查询SMC2.0MCU默认视图的部门显示顺序列表
     * 
     * @param busiMcuSmc2TemplateConferenceDefaultViewDept SMC2.0MCU默认视图的部门显示顺序
     * @return SMC2.0MCU默认视图的部门显示顺序集合
     */
    public List<BusiMcuSmc2TemplateConferenceDefaultViewDept> selectBusiMcuSmc2TemplateConferenceDefaultViewDeptList(BusiMcuSmc2TemplateConferenceDefaultViewDept busiMcuSmc2TemplateConferenceDefaultViewDept);

    /**
     * 新增SMC2.0MCU默认视图的部门显示顺序
     * 
     * @param busiMcuSmc2TemplateConferenceDefaultViewDept SMC2.0MCU默认视图的部门显示顺序
     * @return 结果
     */
    public int insertBusiMcuSmc2TemplateConferenceDefaultViewDept(BusiMcuSmc2TemplateConferenceDefaultViewDept busiMcuSmc2TemplateConferenceDefaultViewDept);

    /**
     * 修改SMC2.0MCU默认视图的部门显示顺序
     * 
     * @param busiMcuSmc2TemplateConferenceDefaultViewDept SMC2.0MCU默认视图的部门显示顺序
     * @return 结果
     */
    public int updateBusiMcuSmc2TemplateConferenceDefaultViewDept(BusiMcuSmc2TemplateConferenceDefaultViewDept busiMcuSmc2TemplateConferenceDefaultViewDept);

    /**
     * 删除SMC2.0MCU默认视图的部门显示顺序
     * 
     * @param id SMC2.0MCU默认视图的部门显示顺序ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2TemplateConferenceDefaultViewDeptById(Long id);

    /**
     * 批量删除SMC2.0MCU默认视图的部门显示顺序
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2TemplateConferenceDefaultViewDeptByIds(Long[] ids);

    void deleteBusiMcuSmc2TemplateConferenceDefaultViewDeptByTemplateConferenceId(Long id);

    void deleteBusiMcuSmc2TemplateConferenceDefaultViewDeptByTemplateConferenceIdAndDeptId(@Param("templateConferenceId") Long templateConferenceId, @Param("deptId") Long deptId);
}
