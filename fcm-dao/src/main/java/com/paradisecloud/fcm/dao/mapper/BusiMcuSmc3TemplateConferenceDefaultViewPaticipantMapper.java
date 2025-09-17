package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplateConferenceDefaultViewPaticipant;

import java.util.List;

/**
 * SMC3.0MCU默认视图的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
public interface BusiMcuSmc3TemplateConferenceDefaultViewPaticipantMapper 
{
    /**
     * 查询SMC3.0MCU默认视图的参会者
     * 
     * @param id SMC3.0MCU默认视图的参会者ID
     * @return SMC3.0MCU默认视图的参会者
     */
    public BusiMcuSmc3TemplateConferenceDefaultViewPaticipant selectBusiMcuSmc3TemplateConferenceDefaultViewPaticipantById(Long id);

    /**
     * 查询SMC3.0MCU默认视图的参会者列表
     * 
     * @param busiMcuSmc3TemplateConferenceDefaultViewPaticipant SMC3.0MCU默认视图的参会者
     * @return SMC3.0MCU默认视图的参会者集合
     */
    public List<BusiMcuSmc3TemplateConferenceDefaultViewPaticipant> selectBusiMcuSmc3TemplateConferenceDefaultViewPaticipantList(BusiMcuSmc3TemplateConferenceDefaultViewPaticipant busiMcuSmc3TemplateConferenceDefaultViewPaticipant);

    /**
     * 新增SMC3.0MCU默认视图的参会者
     * 
     * @param busiMcuSmc3TemplateConferenceDefaultViewPaticipant SMC3.0MCU默认视图的参会者
     * @return 结果
     */
    public int insertBusiMcuSmc3TemplateConferenceDefaultViewPaticipant(BusiMcuSmc3TemplateConferenceDefaultViewPaticipant busiMcuSmc3TemplateConferenceDefaultViewPaticipant);

    /**
     * 修改SMC3.0MCU默认视图的参会者
     * 
     * @param busiMcuSmc3TemplateConferenceDefaultViewPaticipant SMC3.0MCU默认视图的参会者
     * @return 结果
     */
    public int updateBusiMcuSmc3TemplateConferenceDefaultViewPaticipant(BusiMcuSmc3TemplateConferenceDefaultViewPaticipant busiMcuSmc3TemplateConferenceDefaultViewPaticipant);

    /**
     * 删除SMC3.0MCU默认视图的参会者
     * 
     * @param id SMC3.0MCU默认视图的参会者ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3TemplateConferenceDefaultViewPaticipantById(Long id);

    /**
     * 批量删除SMC3.0MCU默认视图的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3TemplateConferenceDefaultViewPaticipantByIds(Long[] ids);

    void deleteBusiMcuSmc3TemplateConferenceDefaultViewPaticipantByTemplateConferenceId(Long id);

    void deleteBusiMcuSmc3TemplateConferenceDefaultViewPaticipantByTemplateParticipantId(Long id);
}
