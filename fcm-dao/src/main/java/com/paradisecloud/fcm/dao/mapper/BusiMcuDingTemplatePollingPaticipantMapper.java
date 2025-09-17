package com.paradisecloud.fcm.dao.mapper;



import com.paradisecloud.fcm.dao.model.BusiMcuDingTemplatePollingPaticipant;

import java.util.List;

/**
 * Ding.0MCU轮询方案的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuDingTemplatePollingPaticipantMapper
{
    /**
     * 查询Ding.0MCU轮询方案的参会者
     * 
     * @param id Ding.0MCU轮询方案的参会者ID
     * @return Ding.0MCU轮询方案的参会者
     */
    public BusiMcuDingTemplatePollingPaticipant selectBusiMcuDingTemplatePollingPaticipantById(Long id);

    /**
     * 查询Ding.0MCU轮询方案的参会者列表
     * 
     * @param busiMcuDingTemplatePollingPaticipant Ding.0MCU轮询方案的参会者
     * @return Ding.0MCU轮询方案的参会者集合
     */
    public List<BusiMcuDingTemplatePollingPaticipant> selectBusiMcuDingTemplatePollingPaticipantList(BusiMcuDingTemplatePollingPaticipant busiMcuDingTemplatePollingPaticipant);

    /**
     * 新增Ding.0MCU轮询方案的参会者
     * 
     * @param busiMcuDingTemplatePollingPaticipant Ding.0MCU轮询方案的参会者
     * @return 结果
     */
    public int insertBusiMcuDingTemplatePollingPaticipant(BusiMcuDingTemplatePollingPaticipant busiMcuDingTemplatePollingPaticipant);

    /**
     * 修改Ding.0MCU轮询方案的参会者
     * 
     * @param busiMcuDingTemplatePollingPaticipant Ding.0MCU轮询方案的参会者
     * @return 结果
     */
    public int updateBusiMcuDingTemplatePollingPaticipant(BusiMcuDingTemplatePollingPaticipant busiMcuDingTemplatePollingPaticipant);

    /**
     * 删除Ding.0MCU轮询方案的参会者
     * 
     * @param id Ding.0MCU轮询方案的参会者ID
     * @return 结果
     */
    public int deleteBusiMcuDingTemplatePollingPaticipantById(Long id);

    /**
     * 批量删除Ding.0MCU轮询方案的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuDingTemplatePollingPaticipantByIds(Long[] ids);

    void deletePollingPaticipantByPollingSchemeId(Long id);

    int deleteBusiMcuDingTemplatePollingPaticipantByAttendeeIds(String[] toArray);
}
