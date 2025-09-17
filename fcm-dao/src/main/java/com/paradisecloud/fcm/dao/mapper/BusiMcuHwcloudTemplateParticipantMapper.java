package com.paradisecloud.fcm.dao.mapper;




import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudTemplateParticipant;

import java.util.List;

/**
 * Hwcloud.0MCU会议模板的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuHwcloudTemplateParticipantMapper
{
    /**
     * 查询Hwcloud.0MCU会议模板的参会者
     * 
     * @param id Hwcloud.0MCU会议模板的参会者ID
     * @return Hwcloud.0MCU会议模板的参会者
     */
    public BusiMcuHwcloudTemplateParticipant selectBusiMcuHwcloudTemplateParticipantById(Long id);

    /**
     * 查询Hwcloud.0MCU会议模板的参会者列表
     * 
     * @param busiMcuHwcloudTemplateParticipant Hwcloud.0MCU会议模板的参会者
     * @return Hwcloud.0MCU会议模板的参会者集合
     */
    public List<BusiMcuHwcloudTemplateParticipant> selectBusiMcuHwcloudTemplateParticipantList(BusiMcuHwcloudTemplateParticipant busiMcuHwcloudTemplateParticipant);

    /**
     * 新增Hwcloud.0MCU会议模板的参会者
     * 
     * @param busiMcuHwcloudTemplateParticipant Hwcloud.0MCU会议模板的参会者
     * @return 结果
     */
    public int insertBusiMcuHwcloudTemplateParticipant(BusiMcuHwcloudTemplateParticipant busiMcuHwcloudTemplateParticipant);

    /**
     * 修改Hwcloud.0MCU会议模板的参会者
     * 
     * @param busiMcuHwcloudTemplateParticipant Hwcloud.0MCU会议模板的参会者
     * @return 结果
     */
    public int updateBusiMcuHwcloudTemplateParticipant(BusiMcuHwcloudTemplateParticipant busiMcuHwcloudTemplateParticipant);

    /**
     * 删除Hwcloud.0MCU会议模板的参会者
     * 
     * @param id Hwcloud.0MCU会议模板的参会者ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudTemplateParticipantById(Long id);

    /**
     * 批量删除Hwcloud.0MCU会议模板的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudTemplateParticipantByIds(Long[] ids);
}
