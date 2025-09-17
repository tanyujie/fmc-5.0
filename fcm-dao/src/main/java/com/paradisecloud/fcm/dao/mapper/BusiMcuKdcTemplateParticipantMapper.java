package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiMcuKdcTemplateParticipant;

/**
 * 紫荆MCU会议模板的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
public interface BusiMcuKdcTemplateParticipantMapper 
{
    /**
     * 查询会议模板的参会者
     * 
     * @param id 会议模板的参会者ID
     * @return 会议模板的参会者
     */
    public BusiMcuKdcTemplateParticipant selectBusiMcuKdcTemplateParticipantById(Long id);

    /**
     * 查询会议模板的参会者列表
     * 
     * @param busiMcuKdcTemplateParticipant 会议模板的参会者
     * @return 会议模板的参会者集合
     */
    public List<BusiMcuKdcTemplateParticipant> selectBusiMcuKdcTemplateParticipantList(BusiMcuKdcTemplateParticipant busiMcuKdcTemplateParticipant);

    /**
     * 新增会议模板的参会者
     * 
     * @param busiMcuKdcTemplateParticipant 会议模板的参会者
     * @return 结果
     */
    public int insertBusiMcuKdcTemplateParticipant(BusiMcuKdcTemplateParticipant busiMcuKdcTemplateParticipant);

    /**
     * 修改会议模板的参会者
     * 
     * @param busiMcuKdcTemplateParticipant 会议模板的参会者
     * @return 结果
     */
    public int updateBusiMcuKdcTemplateParticipant(BusiMcuKdcTemplateParticipant busiMcuKdcTemplateParticipant);

    /**
     * 删除会议模板的参会者
     * 
     * @param id 会议模板的参会者ID
     * @return 结果
     */
    public int deleteBusiMcuKdcTemplateParticipantById(Long id);

    /**
     * 批量删除会议模板的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuKdcTemplateParticipantByIds(Long[] ids);
}
