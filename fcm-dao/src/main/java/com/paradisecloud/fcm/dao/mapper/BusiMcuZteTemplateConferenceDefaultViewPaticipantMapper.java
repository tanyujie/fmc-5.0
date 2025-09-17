package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuZteTemplateConferenceDefaultViewPaticipant;

import java.util.List;

/**
 * 中兴MCU默认视图的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2024-04-09
 */
public interface BusiMcuZteTemplateConferenceDefaultViewPaticipantMapper 
{
    /**
     * 查询中兴MCU默认视图的参会者
     * 
     * @param id 中兴MCU默认视图的参会者ID
     * @return 中兴MCU默认视图的参会者
     */
    public BusiMcuZteTemplateConferenceDefaultViewPaticipant selectBusiMcuZteTemplateConferenceDefaultViewPaticipantById(Long id);

    /**
     * 查询中兴MCU默认视图的参会者列表
     * 
     * @param busiMcuZteTemplateConferenceDefaultViewPaticipant 中兴MCU默认视图的参会者
     * @return 中兴MCU默认视图的参会者集合
     */
    public List<BusiMcuZteTemplateConferenceDefaultViewPaticipant> selectBusiMcuZteTemplateConferenceDefaultViewPaticipantList(BusiMcuZteTemplateConferenceDefaultViewPaticipant busiMcuZteTemplateConferenceDefaultViewPaticipant);

    /**
     * 新增中兴MCU默认视图的参会者
     * 
     * @param busiMcuZteTemplateConferenceDefaultViewPaticipant 中兴MCU默认视图的参会者
     * @return 结果
     */
    public int insertBusiMcuZteTemplateConferenceDefaultViewPaticipant(BusiMcuZteTemplateConferenceDefaultViewPaticipant busiMcuZteTemplateConferenceDefaultViewPaticipant);

    /**
     * 修改中兴MCU默认视图的参会者
     * 
     * @param busiMcuZteTemplateConferenceDefaultViewPaticipant 中兴MCU默认视图的参会者
     * @return 结果
     */
    public int updateBusiMcuZteTemplateConferenceDefaultViewPaticipant(BusiMcuZteTemplateConferenceDefaultViewPaticipant busiMcuZteTemplateConferenceDefaultViewPaticipant);

    /**
     * 删除中兴MCU默认视图的参会者
     * 
     * @param id 中兴MCU默认视图的参会者ID
     * @return 结果
     */
    public int deleteBusiMcuZteTemplateConferenceDefaultViewPaticipantById(Long id);

    /**
     * 批量删除中兴MCU默认视图的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuZteTemplateConferenceDefaultViewPaticipantByIds(Long[] ids);

    public int deleteBusiMcuZteTemplateConferenceDefaultViewPaticipantByTemplateParticipantId(Long templateParticipantId);

    /**
     * 根据模板ID批量删除默认视图的参会者信息
     * @author lilinhai
     * @since 2021-04-08 16:30
     * @param templateConferenceId
     * @return int
     */
    public int deleteBusiMcuZteTemplateConferenceDefaultViewPaticipantByTemplateConferenceId(Long templateConferenceId);



}
