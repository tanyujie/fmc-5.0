package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplateParticipant;

import java.util.List;

/**
 * SMC3.0MCU会议模板的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
public interface BusiMcuSmc3TemplateParticipantMapper 
{
    /**
     * 查询SMC3.0MCU会议模板的参会者
     * 
     * @param id SMC3.0MCU会议模板的参会者ID
     * @return SMC3.0MCU会议模板的参会者
     */
    public BusiMcuSmc3TemplateParticipant selectBusiMcuSmc3TemplateParticipantById(Long id);

    /**
     * 查询SMC3.0MCU会议模板的参会者列表
     * 
     * @param busiMcuSmc3TemplateParticipant SMC3.0MCU会议模板的参会者
     * @return SMC3.0MCU会议模板的参会者集合
     */
    public List<BusiMcuSmc3TemplateParticipant> selectBusiMcuSmc3TemplateParticipantList(BusiMcuSmc3TemplateParticipant busiMcuSmc3TemplateParticipant);

    /**
     * 新增SMC3.0MCU会议模板的参会者
     * 
     * @param busiMcuSmc3TemplateParticipant SMC3.0MCU会议模板的参会者
     * @return 结果
     */
    public int insertBusiMcuSmc3TemplateParticipant(BusiMcuSmc3TemplateParticipant busiMcuSmc3TemplateParticipant);

    /**
     * 修改SMC3.0MCU会议模板的参会者
     * 
     * @param busiMcuSmc3TemplateParticipant SMC3.0MCU会议模板的参会者
     * @return 结果
     */
    public int updateBusiMcuSmc3TemplateParticipant(BusiMcuSmc3TemplateParticipant busiMcuSmc3TemplateParticipant);

    /**
     * 删除SMC3.0MCU会议模板的参会者
     * 
     * @param id SMC3.0MCU会议模板的参会者ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3TemplateParticipantById(Long id);

    /**
     * 批量删除SMC3.0MCU会议模板的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3TemplateParticipantByIds(Long[] ids);
}
