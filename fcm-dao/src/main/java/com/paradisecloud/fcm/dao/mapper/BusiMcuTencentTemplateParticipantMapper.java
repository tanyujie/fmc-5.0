package com.paradisecloud.fcm.dao.mapper;




import com.paradisecloud.fcm.dao.model.BusiMcuTencentTemplateParticipant;

import java.util.List;

/**
 * Tencent.0MCU会议模板的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuTencentTemplateParticipantMapper
{
    /**
     * 查询Tencent.0MCU会议模板的参会者
     * 
     * @param id Tencent.0MCU会议模板的参会者ID
     * @return Tencent.0MCU会议模板的参会者
     */
    public BusiMcuTencentTemplateParticipant selectBusiMcuTencentTemplateParticipantById(Long id);

    /**
     * 查询Tencent.0MCU会议模板的参会者列表
     * 
     * @param busiMcuTencentTemplateParticipant Tencent.0MCU会议模板的参会者
     * @return Tencent.0MCU会议模板的参会者集合
     */
    public List<BusiMcuTencentTemplateParticipant> selectBusiMcuTencentTemplateParticipantList(BusiMcuTencentTemplateParticipant busiMcuTencentTemplateParticipant);

    /**
     * 新增Tencent.0MCU会议模板的参会者
     * 
     * @param busiMcuTencentTemplateParticipant Tencent.0MCU会议模板的参会者
     * @return 结果
     */
    public int insertBusiMcuTencentTemplateParticipant(BusiMcuTencentTemplateParticipant busiMcuTencentTemplateParticipant);

    /**
     * 修改Tencent.0MCU会议模板的参会者
     * 
     * @param busiMcuTencentTemplateParticipant Tencent.0MCU会议模板的参会者
     * @return 结果
     */
    public int updateBusiMcuTencentTemplateParticipant(BusiMcuTencentTemplateParticipant busiMcuTencentTemplateParticipant);

    /**
     * 删除Tencent.0MCU会议模板的参会者
     * 
     * @param id Tencent.0MCU会议模板的参会者ID
     * @return 结果
     */
    public int deleteBusiMcuTencentTemplateParticipantById(Long id);

    /**
     * 批量删除Tencent.0MCU会议模板的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuTencentTemplateParticipantByIds(Long[] ids);
}
