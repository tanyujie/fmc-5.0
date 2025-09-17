package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiMcuPlcTemplateConferenceDefaultViewPaticipant;

/**
 * 默认视图的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2021-04-08
 */
public interface BusiMcuPlcTemplateConferenceDefaultViewPaticipantMapper 
{
    /**
     * 查询默认视图的参会者
     * 
     * @param id 默认视图的参会者ID
     * @return 默认视图的参会者
     */
    public BusiMcuPlcTemplateConferenceDefaultViewPaticipant selectBusiMcuPlcTemplateConferenceDefaultViewPaticipantById(Long id);

    /**
     * 查询默认视图的参会者列表
     * 
     * @param busiTemplateConferenceDefaultViewPaticipant 默认视图的参会者
     * @return 默认视图的参会者集合
     */
    public List<BusiMcuPlcTemplateConferenceDefaultViewPaticipant> selectBusiMcuPlcTemplateConferenceDefaultViewPaticipantList(BusiMcuPlcTemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant);

    /**
     * 新增默认视图的参会者
     * 
     * @param busiTemplateConferenceDefaultViewPaticipant 默认视图的参会者
     * @return 结果
     */
    public int insertBusiMcuPlcTemplateConferenceDefaultViewPaticipant(BusiMcuPlcTemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant);

    /**
     * 修改默认视图的参会者
     * 
     * @param busiTemplateConferenceDefaultViewPaticipant 默认视图的参会者
     * @return 结果
     */
    public int updateBusiMcuPlcTemplateConferenceDefaultViewPaticipant(BusiMcuPlcTemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant);

    /**
     * 删除默认视图的参会者
     * 
     * @param id 默认视图的参会者ID
     * @return 结果
     */
    public int deleteBusiMcuPlcTemplateConferenceDefaultViewPaticipantById(Long id);
    
    public int deleteBusiMcuPlcTemplateConferenceDefaultViewPaticipantByTemplateParticipantId(Long templateParticipantId);
    
    /**
     * 根据模板ID批量删除默认视图的参会者信息
     * @author lilinhai
     * @since 2021-04-08 16:30 
     * @param templateConferenceId
     * @return int
     */
    public int deleteBusiMcuPlcTemplateConferenceDefaultViewPaticipantByTemplateConferenceId(Long templateConferenceId);

    /**
     * 批量删除默认视图的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuPlcTemplateConferenceDefaultViewPaticipantByIds(Long[] ids);
}
