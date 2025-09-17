package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuZteTemplateParticipant;

import java.util.List;

/**
 * 中兴MCU会议模板的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2024-04-09
 */
public interface BusiMcuZteTemplateParticipantMapper 
{
    /**
     * 查询中兴MCU会议模板的参会者
     * 
     * @param id 中兴MCU会议模板的参会者ID
     * @return 中兴MCU会议模板的参会者
     */
    public BusiMcuZteTemplateParticipant selectBusiMcuZteTemplateParticipantById(Long id);

    /**
     * 查询中兴MCU会议模板的参会者列表
     * 
     * @param busiMcuZteTemplateParticipant 中兴MCU会议模板的参会者
     * @return 中兴MCU会议模板的参会者集合
     */
    public List<BusiMcuZteTemplateParticipant> selectBusiMcuZteTemplateParticipantList(BusiMcuZteTemplateParticipant busiMcuZteTemplateParticipant);

    /**
     * 新增中兴MCU会议模板的参会者
     * 
     * @param busiMcuZteTemplateParticipant 中兴MCU会议模板的参会者
     * @return 结果
     */
    public int insertBusiMcuZteTemplateParticipant(BusiMcuZteTemplateParticipant busiMcuZteTemplateParticipant);

    /**
     * 修改中兴MCU会议模板的参会者
     * 
     * @param busiMcuZteTemplateParticipant 中兴MCU会议模板的参会者
     * @return 结果
     */
    public int updateBusiMcuZteTemplateParticipant(BusiMcuZteTemplateParticipant busiMcuZteTemplateParticipant);

    /**
     * 删除中兴MCU会议模板的参会者
     * 
     * @param id 中兴MCU会议模板的参会者ID
     * @return 结果
     */
    public int deleteBusiMcuZteTemplateParticipantById(Long id);

    /**
     * 批量删除中兴MCU会议模板的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuZteTemplateParticipantByIds(Long[] ids);
}
