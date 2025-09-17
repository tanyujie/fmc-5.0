package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiMcuZjTemplateParticipant;

/**
 * 紫荆MCU会议模板的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
public interface BusiMcuZjTemplateParticipantMapper 
{
    /**
     * 查询会议模板的参会者
     * 
     * @param id 会议模板的参会者ID
     * @return 会议模板的参会者
     */
    public BusiMcuZjTemplateParticipant selectBusiMcuZjTemplateParticipantById(Long id);

    /**
     * 查询会议模板的参会者列表
     * 
     * @param busiMcuZjTemplateParticipant 会议模板的参会者
     * @return 会议模板的参会者集合
     */
    public List<BusiMcuZjTemplateParticipant> selectBusiMcuZjTemplateParticipantList(BusiMcuZjTemplateParticipant busiMcuZjTemplateParticipant);

    /**
     * 新增会议模板的参会者
     * 
     * @param busiMcuZjTemplateParticipant 会议模板的参会者
     * @return 结果
     */
    public int insertBusiMcuZjTemplateParticipant(BusiMcuZjTemplateParticipant busiMcuZjTemplateParticipant);

    /**
     * 修改会议模板的参会者
     * 
     * @param busiMcuZjTemplateParticipant 会议模板的参会者
     * @return 结果
     */
    public int updateBusiMcuZjTemplateParticipant(BusiMcuZjTemplateParticipant busiMcuZjTemplateParticipant);

    /**
     * 删除会议模板的参会者
     * 
     * @param id 会议模板的参会者ID
     * @return 结果
     */
    public int deleteBusiMcuZjTemplateParticipantById(Long id);

    /**
     * 批量删除会议模板的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuZjTemplateParticipantByIds(Long[] ids);
}
