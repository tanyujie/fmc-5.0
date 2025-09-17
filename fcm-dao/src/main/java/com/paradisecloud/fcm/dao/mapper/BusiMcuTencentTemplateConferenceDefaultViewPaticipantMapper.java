package com.paradisecloud.fcm.dao.mapper;




import com.paradisecloud.fcm.dao.model.BusiMcuTencentTemplateConferenceDefaultViewPaticipant;

import java.util.List;

/**
 * Tencent.0MCU默认视图的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuTencentTemplateConferenceDefaultViewPaticipantMapper
{
    /**
     * 查询Tencent.0MCU默认视图的参会者
     * 
     * @param id Tencent.0MCU默认视图的参会者ID
     * @return Tencent.0MCU默认视图的参会者
     */
    public BusiMcuTencentTemplateConferenceDefaultViewPaticipant selectBusiMcuTencentTemplateConferenceDefaultViewPaticipantById(Long id);

    /**
     * 查询Tencent.0MCU默认视图的参会者列表
     * 
     * @param busiMcuTencentTemplateConferenceDefaultViewPaticipant Tencent.0MCU默认视图的参会者
     * @return Tencent.0MCU默认视图的参会者集合
     */
    public List<BusiMcuTencentTemplateConferenceDefaultViewPaticipant> selectBusiMcuTencentTemplateConferenceDefaultViewPaticipantList(BusiMcuTencentTemplateConferenceDefaultViewPaticipant busiMcuTencentTemplateConferenceDefaultViewPaticipant);

    /**
     * 新增Tencent.0MCU默认视图的参会者
     * 
     * @param busiMcuTencentTemplateConferenceDefaultViewPaticipant Tencent.0MCU默认视图的参会者
     * @return 结果
     */
    public int insertBusiMcuTencentTemplateConferenceDefaultViewPaticipant(BusiMcuTencentTemplateConferenceDefaultViewPaticipant busiMcuTencentTemplateConferenceDefaultViewPaticipant);

    /**
     * 修改Tencent.0MCU默认视图的参会者
     * 
     * @param busiMcuTencentTemplateConferenceDefaultViewPaticipant Tencent.0MCU默认视图的参会者
     * @return 结果
     */
    public int updateBusiMcuTencentTemplateConferenceDefaultViewPaticipant(BusiMcuTencentTemplateConferenceDefaultViewPaticipant busiMcuTencentTemplateConferenceDefaultViewPaticipant);

    /**
     * 删除Tencent.0MCU默认视图的参会者
     * 
     * @param id Tencent.0MCU默认视图的参会者ID
     * @return 结果
     */
    public int deleteBusiMcuTencentTemplateConferenceDefaultViewPaticipantById(Long id);

    /**
     * 批量删除Tencent.0MCU默认视图的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuTencentTemplateConferenceDefaultViewPaticipantByIds(Long[] ids);

    void deleteBusiMcuTencentTemplateConferenceDefaultViewPaticipantByTemplateConferenceId(Long id);

    void deleteBusiMcuTencentTemplateConferenceDefaultViewPaticipantByTemplateParticipantId(Long id);
}
