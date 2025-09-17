package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuZteTemplateConferenceDefaultViewDept;
import com.paradisecloud.fcm.dao.model.BusiMcuZteTemplateConferenceDefaultViewDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 中兴MCU默认视图的部门显示顺序Mapper接口
 * 
 * @author lilinhai
 * @date 2024-04-09
 */
public interface BusiMcuZteTemplateConferenceDefaultViewDeptMapper 
{
    /**
     * 查询默认视图的部门显示顺序
     *
     * @param id 默认视图的部门显示顺序ID
     * @return 默认视图的部门显示顺序
     */
    public BusiMcuZteTemplateConferenceDefaultViewDept selectBusiMcuZteTemplateConferenceDefaultViewDeptById(Long id);

    /**
     * 查询默认视图的部门显示顺序列表
     *
     * @param busiTemplateConferenceDefaultViewDept 默认视图的部门显示顺序
     * @return 默认视图的部门显示顺序集合
     */
    public List<BusiMcuZteTemplateConferenceDefaultViewDept> selectBusiMcuZteTemplateConferenceDefaultViewDeptList(BusiMcuZteTemplateConferenceDefaultViewDept busiTemplateConferenceDefaultViewDept);

    /**
     * 新增默认视图的部门显示顺序
     *
     * @param busiTemplateConferenceDefaultViewDept 默认视图的部门显示顺序
     * @return 结果
     */
    public int insertBusiMcuZteTemplateConferenceDefaultViewDept(BusiMcuZteTemplateConferenceDefaultViewDept busiTemplateConferenceDefaultViewDept);

    /**
     * 修改默认视图的部门显示顺序
     *
     * @param busiTemplateConferenceDefaultViewDept 默认视图的部门显示顺序
     * @return 结果
     */
    public int updateBusiMcuZteTemplateConferenceDefaultViewDept(BusiMcuZteTemplateConferenceDefaultViewDept busiTemplateConferenceDefaultViewDept);

    /**
     * 删除默认视图的部门显示顺序
     *
     * @param id 默认视图的部门显示顺序ID
     * @return 结果
     */
    public int deleteBusiMcuZteTemplateConferenceDefaultViewDeptById(Long id);

    /**
     * 根据会议模板ID批量删除默认视图的部门信息
     * @author lilinhai
     * @since 2021-04-08 16:21 
     * @param templateConferenceId
     * @return int
     */
    public int deleteBusiMcuZteTemplateConferenceDefaultViewDeptByTemplateConferenceId(Long templateConferenceId);

    int deleteBusiMcuZteTemplateConferenceDefaultViewDeptByTemplateConferenceIdAndDeptId(@Param("templateConferenceId") Long templateConferenceId, @Param("deptId") Long deptId);

    /**
     * 批量删除默认视图的部门显示顺序
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuZteTemplateConferenceDefaultViewDeptByIds(Long[] ids);
}
