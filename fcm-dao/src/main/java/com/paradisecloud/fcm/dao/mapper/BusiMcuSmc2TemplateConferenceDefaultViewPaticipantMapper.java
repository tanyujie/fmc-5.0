package com.paradisecloud.fcm.dao.mapper;


import com.paradisecloud.fcm.dao.model.BusiMcuSmc2TemplateConferenceDefaultViewPaticipant;

import java.util.List;

/**
 * SMC2.0MCU默认视图的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuSmc2TemplateConferenceDefaultViewPaticipantMapper
{
    /**
     * 查询SMC2.0MCU默认视图的参会者
     * 
     * @param id SMC2.0MCU默认视图的参会者ID
     * @return SMC2.0MCU默认视图的参会者
     */
    public BusiMcuSmc2TemplateConferenceDefaultViewPaticipant selectBusiMcuSmc2TemplateConferenceDefaultViewPaticipantById(Long id);

    /**
     * 查询SMC2.0MCU默认视图的参会者列表
     * 
     * @param busiMcuSmc2TemplateConferenceDefaultViewPaticipant SMC2.0MCU默认视图的参会者
     * @return SMC2.0MCU默认视图的参会者集合
     */
    public List<BusiMcuSmc2TemplateConferenceDefaultViewPaticipant> selectBusiMcuSmc2TemplateConferenceDefaultViewPaticipantList(BusiMcuSmc2TemplateConferenceDefaultViewPaticipant busiMcuSmc2TemplateConferenceDefaultViewPaticipant);

    /**
     * 新增SMC2.0MCU默认视图的参会者
     * 
     * @param busiMcuSmc2TemplateConferenceDefaultViewPaticipant SMC2.0MCU默认视图的参会者
     * @return 结果
     */
    public int insertBusiMcuSmc2TemplateConferenceDefaultViewPaticipant(BusiMcuSmc2TemplateConferenceDefaultViewPaticipant busiMcuSmc2TemplateConferenceDefaultViewPaticipant);

    /**
     * 修改SMC2.0MCU默认视图的参会者
     * 
     * @param busiMcuSmc2TemplateConferenceDefaultViewPaticipant SMC2.0MCU默认视图的参会者
     * @return 结果
     */
    public int updateBusiMcuSmc2TemplateConferenceDefaultViewPaticipant(BusiMcuSmc2TemplateConferenceDefaultViewPaticipant busiMcuSmc2TemplateConferenceDefaultViewPaticipant);

    /**
     * 删除SMC2.0MCU默认视图的参会者
     * 
     * @param id SMC2.0MCU默认视图的参会者ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2TemplateConferenceDefaultViewPaticipantById(Long id);

    /**
     * 批量删除SMC2.0MCU默认视图的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2TemplateConferenceDefaultViewPaticipantByIds(Long[] ids);

    void deleteBusiMcuSmc2TemplateConferenceDefaultViewPaticipantByTemplateConferenceId(Long id);

    void deleteBusiMcuSmc2TemplateConferenceDefaultViewPaticipantByTemplateParticipantId(Long id);
}
