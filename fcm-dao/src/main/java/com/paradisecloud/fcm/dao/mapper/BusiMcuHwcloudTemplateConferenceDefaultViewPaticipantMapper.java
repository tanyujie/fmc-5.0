package com.paradisecloud.fcm.dao.mapper;




import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudTemplateConferenceDefaultViewPaticipant;

import java.util.List;

/**
 * Hwcloud.0MCU默认视图的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuHwcloudTemplateConferenceDefaultViewPaticipantMapper
{
    /**
     * 查询Hwcloud.0MCU默认视图的参会者
     * 
     * @param id Hwcloud.0MCU默认视图的参会者ID
     * @return Hwcloud.0MCU默认视图的参会者
     */
    public BusiMcuHwcloudTemplateConferenceDefaultViewPaticipant selectBusiMcuHwcloudTemplateConferenceDefaultViewPaticipantById(Long id);

    /**
     * 查询Hwcloud.0MCU默认视图的参会者列表
     * 
     * @param busiMcuHwcloudTemplateConferenceDefaultViewPaticipant Hwcloud.0MCU默认视图的参会者
     * @return Hwcloud.0MCU默认视图的参会者集合
     */
    public List<BusiMcuHwcloudTemplateConferenceDefaultViewPaticipant> selectBusiMcuHwcloudTemplateConferenceDefaultViewPaticipantList(BusiMcuHwcloudTemplateConferenceDefaultViewPaticipant busiMcuHwcloudTemplateConferenceDefaultViewPaticipant);

    /**
     * 新增Hwcloud.0MCU默认视图的参会者
     * 
     * @param busiMcuHwcloudTemplateConferenceDefaultViewPaticipant Hwcloud.0MCU默认视图的参会者
     * @return 结果
     */
    public int insertBusiMcuHwcloudTemplateConferenceDefaultViewPaticipant(BusiMcuHwcloudTemplateConferenceDefaultViewPaticipant busiMcuHwcloudTemplateConferenceDefaultViewPaticipant);

    /**
     * 修改Hwcloud.0MCU默认视图的参会者
     * 
     * @param busiMcuHwcloudTemplateConferenceDefaultViewPaticipant Hwcloud.0MCU默认视图的参会者
     * @return 结果
     */
    public int updateBusiMcuHwcloudTemplateConferenceDefaultViewPaticipant(BusiMcuHwcloudTemplateConferenceDefaultViewPaticipant busiMcuHwcloudTemplateConferenceDefaultViewPaticipant);

    /**
     * 删除Hwcloud.0MCU默认视图的参会者
     * 
     * @param id Hwcloud.0MCU默认视图的参会者ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudTemplateConferenceDefaultViewPaticipantById(Long id);

    /**
     * 批量删除Hwcloud.0MCU默认视图的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudTemplateConferenceDefaultViewPaticipantByIds(Long[] ids);

    void deleteBusiMcuHwcloudTemplateConferenceDefaultViewPaticipantByTemplateConferenceId(Long id);

    void deleteBusiMcuHwcloudTemplateConferenceDefaultViewPaticipantByTemplateParticipantId(Long id);
}
