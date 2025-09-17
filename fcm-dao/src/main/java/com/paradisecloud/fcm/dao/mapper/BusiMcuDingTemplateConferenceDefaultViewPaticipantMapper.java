package com.paradisecloud.fcm.dao.mapper;




import com.paradisecloud.fcm.dao.model.BusiMcuDingTemplateConferenceDefaultViewPaticipant;

import java.util.List;

/**
 * Ding.0MCU默认视图的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuDingTemplateConferenceDefaultViewPaticipantMapper
{
    /**
     * 查询Ding.0MCU默认视图的参会者
     * 
     * @param id Ding.0MCU默认视图的参会者ID
     * @return Ding.0MCU默认视图的参会者
     */
    public BusiMcuDingTemplateConferenceDefaultViewPaticipant selectBusiMcuDingTemplateConferenceDefaultViewPaticipantById(Long id);

    /**
     * 查询Ding.0MCU默认视图的参会者列表
     * 
     * @param busiMcuDingTemplateConferenceDefaultViewPaticipant Ding.0MCU默认视图的参会者
     * @return Ding.0MCU默认视图的参会者集合
     */
    public List<BusiMcuDingTemplateConferenceDefaultViewPaticipant> selectBusiMcuDingTemplateConferenceDefaultViewPaticipantList(BusiMcuDingTemplateConferenceDefaultViewPaticipant busiMcuDingTemplateConferenceDefaultViewPaticipant);

    /**
     * 新增Ding.0MCU默认视图的参会者
     * 
     * @param busiMcuDingTemplateConferenceDefaultViewPaticipant Ding.0MCU默认视图的参会者
     * @return 结果
     */
    public int insertBusiMcuDingTemplateConferenceDefaultViewPaticipant(BusiMcuDingTemplateConferenceDefaultViewPaticipant busiMcuDingTemplateConferenceDefaultViewPaticipant);

    /**
     * 修改Ding.0MCU默认视图的参会者
     * 
     * @param busiMcuDingTemplateConferenceDefaultViewPaticipant Ding.0MCU默认视图的参会者
     * @return 结果
     */
    public int updateBusiMcuDingTemplateConferenceDefaultViewPaticipant(BusiMcuDingTemplateConferenceDefaultViewPaticipant busiMcuDingTemplateConferenceDefaultViewPaticipant);

    /**
     * 删除Ding.0MCU默认视图的参会者
     * 
     * @param id Ding.0MCU默认视图的参会者ID
     * @return 结果
     */
    public int deleteBusiMcuDingTemplateConferenceDefaultViewPaticipantById(Long id);

    /**
     * 批量删除Ding.0MCU默认视图的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuDingTemplateConferenceDefaultViewPaticipantByIds(Long[] ids);

    void deleteBusiMcuDingTemplateConferenceDefaultViewPaticipantByTemplateConferenceId(Long id);

    void deleteBusiMcuDingTemplateConferenceDefaultViewPaticipantByTemplateParticipantId(Long id);
}
