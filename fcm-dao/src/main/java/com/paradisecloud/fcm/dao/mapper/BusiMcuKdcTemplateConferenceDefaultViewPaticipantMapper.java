package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiMcuKdcTemplateConferenceDefaultViewPaticipant;

/**
 * 默认视图的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2021-04-08
 */
public interface BusiMcuKdcTemplateConferenceDefaultViewPaticipantMapper
{
    /**
     * 查询默认视图的参会者
     * 
     * @param id 默认视图的参会者ID
     * @return 默认视图的参会者
     */
    public BusiMcuKdcTemplateConferenceDefaultViewPaticipant selectBusiMcuKdcTemplateConferenceDefaultViewPaticipantById(Long id);

    /**
     * 查询默认视图的参会者列表
     * 
     * @param busiTemplateConferenceDefaultViewPaticipant 默认视图的参会者
     * @return 默认视图的参会者集合
     */
    public List<BusiMcuKdcTemplateConferenceDefaultViewPaticipant> selectBusiMcuKdcTemplateConferenceDefaultViewPaticipantList(BusiMcuKdcTemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant);

    /**
     * 新增默认视图的参会者
     * 
     * @param busiTemplateConferenceDefaultViewPaticipant 默认视图的参会者
     * @return 结果
     */
    public int insertBusiMcuKdcTemplateConferenceDefaultViewPaticipant(BusiMcuKdcTemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant);

    /**
     * 修改默认视图的参会者
     * 
     * @param busiTemplateConferenceDefaultViewPaticipant 默认视图的参会者
     * @return 结果
     */
    public int updateBusiMcuKdcTemplateConferenceDefaultViewPaticipant(BusiMcuKdcTemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant);

    /**
     * 删除默认视图的参会者
     * 
     * @param id 默认视图的参会者ID
     * @return 结果
     */
    public int deleteBusiMcuKdcTemplateConferenceDefaultViewPaticipantById(Long id);
    
    public int deleteBusiMcuKdcTemplateConferenceDefaultViewPaticipantByTemplateParticipantId(Long templateParticipantId);
    
    /**
     * 根据模板ID批量删除默认视图的参会者信息
     * @author lilinhai
     * @since 2021-04-08 16:30 
     * @param templateConferenceId
     * @return int
     */
    public int deleteBusiMcuKdcTemplateConferenceDefaultViewPaticipantByTemplateConferenceId(Long templateConferenceId);

    /**
     * 批量删除默认视图的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuKdcTemplateConferenceDefaultViewPaticipantByIds(Long[] ids);
}
