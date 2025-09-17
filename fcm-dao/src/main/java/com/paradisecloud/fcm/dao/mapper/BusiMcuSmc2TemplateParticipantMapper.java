package com.paradisecloud.fcm.dao.mapper;


import com.paradisecloud.fcm.dao.model.BusiMcuSmc2TemplateParticipant;

import java.util.List;

/**
 * SMC2.0MCU会议模板的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuSmc2TemplateParticipantMapper
{
    /**
     * 查询SMC2.0MCU会议模板的参会者
     * 
     * @param id SMC2.0MCU会议模板的参会者ID
     * @return SMC2.0MCU会议模板的参会者
     */
    public BusiMcuSmc2TemplateParticipant selectBusiMcuSmc2TemplateParticipantById(Long id);

    /**
     * 查询SMC2.0MCU会议模板的参会者列表
     * 
     * @param busiMcuSmc2TemplateParticipant SMC2.0MCU会议模板的参会者
     * @return SMC2.0MCU会议模板的参会者集合
     */
    public List<BusiMcuSmc2TemplateParticipant> selectBusiMcuSmc2TemplateParticipantList(BusiMcuSmc2TemplateParticipant busiMcuSmc2TemplateParticipant);

    /**
     * 新增SMC2.0MCU会议模板的参会者
     * 
     * @param busiMcuSmc2TemplateParticipant SMC2.0MCU会议模板的参会者
     * @return 结果
     */
    public int insertBusiMcuSmc2TemplateParticipant(BusiMcuSmc2TemplateParticipant busiMcuSmc2TemplateParticipant);

    /**
     * 修改SMC2.0MCU会议模板的参会者
     * 
     * @param busiMcuSmc2TemplateParticipant SMC2.0MCU会议模板的参会者
     * @return 结果
     */
    public int updateBusiMcuSmc2TemplateParticipant(BusiMcuSmc2TemplateParticipant busiMcuSmc2TemplateParticipant);

    /**
     * 删除SMC2.0MCU会议模板的参会者
     * 
     * @param id SMC2.0MCU会议模板的参会者ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2TemplateParticipantById(Long id);

    /**
     * 批量删除SMC2.0MCU会议模板的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2TemplateParticipantByIds(Long[] ids);
}
