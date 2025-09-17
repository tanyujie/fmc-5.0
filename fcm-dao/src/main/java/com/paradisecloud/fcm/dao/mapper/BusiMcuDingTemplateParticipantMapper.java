package com.paradisecloud.fcm.dao.mapper;




import com.paradisecloud.fcm.dao.model.BusiMcuDingTemplateParticipant;

import java.util.List;

/**
 * Ding.0MCU会议模板的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuDingTemplateParticipantMapper
{
    /**
     * 查询Ding.0MCU会议模板的参会者
     * 
     * @param id Ding.0MCU会议模板的参会者ID
     * @return Ding.0MCU会议模板的参会者
     */
    public BusiMcuDingTemplateParticipant selectBusiMcuDingTemplateParticipantById(Long id);

    /**
     * 查询Ding.0MCU会议模板的参会者列表
     * 
     * @param busiMcuDingTemplateParticipant Ding.0MCU会议模板的参会者
     * @return Ding.0MCU会议模板的参会者集合
     */
    public List<BusiMcuDingTemplateParticipant> selectBusiMcuDingTemplateParticipantList(BusiMcuDingTemplateParticipant busiMcuDingTemplateParticipant);

    /**
     * 新增Ding.0MCU会议模板的参会者
     * 
     * @param busiMcuDingTemplateParticipant Ding.0MCU会议模板的参会者
     * @return 结果
     */
    public int insertBusiMcuDingTemplateParticipant(BusiMcuDingTemplateParticipant busiMcuDingTemplateParticipant);

    /**
     * 修改Ding.0MCU会议模板的参会者
     * 
     * @param busiMcuDingTemplateParticipant Ding.0MCU会议模板的参会者
     * @return 结果
     */
    public int updateBusiMcuDingTemplateParticipant(BusiMcuDingTemplateParticipant busiMcuDingTemplateParticipant);

    /**
     * 删除Ding.0MCU会议模板的参会者
     * 
     * @param id Ding.0MCU会议模板的参会者ID
     * @return 结果
     */
    public int deleteBusiMcuDingTemplateParticipantById(Long id);

    /**
     * 批量删除Ding.0MCU会议模板的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuDingTemplateParticipantByIds(Long[] ids);
}
