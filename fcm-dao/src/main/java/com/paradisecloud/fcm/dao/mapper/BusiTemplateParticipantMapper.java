package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiTemplateParticipant;

/**
 * 会议模板的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
public interface BusiTemplateParticipantMapper 
{
    /**
     * 查询会议模板的参会者
     * 
     * @param id 会议模板的参会者ID
     * @return 会议模板的参会者
     */
    public BusiTemplateParticipant selectBusiTemplateParticipantById(Long id);

    /**
     * 查询会议模板的参会者列表
     * 
     * @param busiTemplateParticipant 会议模板的参会者
     * @return 会议模板的参会者集合
     */
    public List<BusiTemplateParticipant> selectBusiTemplateParticipantList(BusiTemplateParticipant busiTemplateParticipant);

    /**
     * 新增会议模板的参会者
     * 
     * @param busiTemplateParticipant 会议模板的参会者
     * @return 结果
     */
    public int insertBusiTemplateParticipant(BusiTemplateParticipant busiTemplateParticipant);

    /**
     * 修改会议模板的参会者
     * 
     * @param busiTemplateParticipant 会议模板的参会者
     * @return 结果
     */
    public int updateBusiTemplateParticipant(BusiTemplateParticipant busiTemplateParticipant);

    /**
     * 删除会议模板的参会者
     * 
     * @param id 会议模板的参会者ID
     * @return 结果
     */
    public int deleteBusiTemplateParticipantById(Long id);

    /**
     * 批量删除会议模板的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiTemplateParticipantByIds(Long[] ids);
}
