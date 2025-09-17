package com.paradisecloud.fcm.smc2.service;

import com.paradisecloud.com.fcm.smc.modle.SmcConferenceTemplate;
import com.paradisecloud.com.fcm.smc.modle.TemplateTerminal;
import com.paradisecloud.fcm.dao.model.BusiSmc2DeptTemplate;
import com.paradisecloud.fcm.smc2.model.BusiSmc2TemplateConferenceRequest;

import java.util.List;

/**
 * 部门smc模板关联Service接口
 * 
 * @author lilinhai
 * @date 2023-04-19
 */
public interface IBusiSmc2DeptTemplateService 
{
    /**
     * 查询部门smc模板关联
     * 
     * @param id 部门smc模板关联ID
     * @return 部门smc模板关联
     */
    public BusiSmc2DeptTemplate selectBusiSmc2DeptTemplateById(Integer id);

    /**
     * 查询部门smc模板关联列表
     * 
     * @param busiSmc2DeptTemplate 部门smc模板关联
     * @return 部门smc模板关联集合
     */
    public List<BusiSmc2DeptTemplate> selectBusiSmc2DeptTemplateList(BusiSmc2DeptTemplate busiSmc2DeptTemplate);

    /**
     * 新增部门smc模板关联
     * 
     * @param busiSmc2DeptTemplate 部门smc模板关联
     * @return 结果
     */
    public SmcConferenceTemplate insertBusiSmc2DeptTemplate(BusiSmc2DeptTemplate busiSmc2DeptTemplate, List<TemplateTerminal> templateTerminalList);

    /**
     * 修改部门smc模板关联
     * 
     * @param busiSmc2DeptTemplate 部门smc模板关联
     * @return 结果
     */
    public int updateBusiSmc2DeptTemplate(BusiSmc2TemplateConferenceRequest templateConferenceRequest);

    public SmcConferenceTemplate updateBusiSmc2DeptTemplate(BusiSmc2DeptTemplate busiSmc2DeptTemplate, List<TemplateTerminal> templateTerminalList);

    /**
     * 批量删除部门smc模板关联
     * 
     * @param ids 需要删除的部门smc模板关联ID
     * @return 结果
     */
    public int deleteBusiSmc2DeptTemplateByIds(Integer[] ids);

    /**
     * 删除部门smc模板关联信息
     * 
     * @param id 部门smc模板关联ID
     * @return 结果
     */
    public int deleteBusiSmc2DeptTemplateById(Integer id);

    List<BusiSmc2DeptTemplate> queryTemplateListByDeptId(Long deptId);
}
