package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuZteTemplatePollingPaticipant;

import java.util.List;

/**
 * 中兴MCU轮询方案的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2024-04-09
 */
public interface BusiMcuZteTemplatePollingPaticipantMapper 
{
    /**
     * 查询中兴MCU轮询方案的参会者
     * 
     * @param id 中兴MCU轮询方案的参会者ID
     * @return 中兴MCU轮询方案的参会者
     */
    public BusiMcuZteTemplatePollingPaticipant selectBusiMcuZteTemplatePollingPaticipantById(Long id);

    /**
     * 查询中兴MCU轮询方案的参会者列表
     * 
     * @param busiMcuZteTemplatePollingPaticipant 中兴MCU轮询方案的参会者
     * @return 中兴MCU轮询方案的参会者集合
     */
    public List<BusiMcuZteTemplatePollingPaticipant> selectBusiMcuZteTemplatePollingPaticipantList(BusiMcuZteTemplatePollingPaticipant busiMcuZteTemplatePollingPaticipant);

    /**
     * 新增中兴MCU轮询方案的参会者
     * 
     * @param busiMcuZteTemplatePollingPaticipant 中兴MCU轮询方案的参会者
     * @return 结果
     */
    public int insertBusiMcuZteTemplatePollingPaticipant(BusiMcuZteTemplatePollingPaticipant busiMcuZteTemplatePollingPaticipant);

    /**
     * 修改中兴MCU轮询方案的参会者
     * 
     * @param busiMcuZteTemplatePollingPaticipant 中兴MCU轮询方案的参会者
     * @return 结果
     */
    public int updateBusiMcuZteTemplatePollingPaticipant(BusiMcuZteTemplatePollingPaticipant busiMcuZteTemplatePollingPaticipant);

    /**
     * 删除中兴MCU轮询方案的参会者
     * 
     * @param id 中兴MCU轮询方案的参会者ID
     * @return 结果
     */
    public int deleteBusiMcuZteTemplatePollingPaticipantById(Long id);

    /**
     * 批量删除中兴MCU轮询方案的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuZteTemplatePollingPaticipantByIds(Long[] ids);

    int deleteBusiMcuZteTemplatePollingPaticipantByAttendeeIds(String[] attendeeIds);


    void deletePollingPaticipantByPollingSchemeId(Long id);
}
