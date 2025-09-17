package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiMcuPlcTemplateParticipant;

/**
 * 紫荆MCU会议模板的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
public interface BusiMcuPlcTemplateParticipantMapper 
{
    /**
     * 查询会议模板的参会者
     * 
     * @param id 会议模板的参会者ID
     * @return 会议模板的参会者
     */
    public BusiMcuPlcTemplateParticipant selectBusiMcuPlcTemplateParticipantById(Long id);

    /**
     * 查询会议模板的参会者列表
     * 
     * @param busiMcuPlcTemplateParticipant 会议模板的参会者
     * @return 会议模板的参会者集合
     */
    public List<BusiMcuPlcTemplateParticipant> selectBusiMcuPlcTemplateParticipantList(BusiMcuPlcTemplateParticipant busiMcuPlcTemplateParticipant);

    /**
     * 新增会议模板的参会者
     * 
     * @param busiMcuPlcTemplateParticipant 会议模板的参会者
     * @return 结果
     */
    public int insertBusiMcuPlcTemplateParticipant(BusiMcuPlcTemplateParticipant busiMcuPlcTemplateParticipant);

    /**
     * 修改会议模板的参会者
     * 
     * @param busiMcuPlcTemplateParticipant 会议模板的参会者
     * @return 结果
     */
    public int updateBusiMcuPlcTemplateParticipant(BusiMcuPlcTemplateParticipant busiMcuPlcTemplateParticipant);

    /**
     * 删除会议模板的参会者
     * 
     * @param id 会议模板的参会者ID
     * @return 结果
     */
    public int deleteBusiMcuPlcTemplateParticipantById(Long id);

    /**
     * 批量删除会议模板的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuPlcTemplateParticipantByIds(Long[] ids);
}
