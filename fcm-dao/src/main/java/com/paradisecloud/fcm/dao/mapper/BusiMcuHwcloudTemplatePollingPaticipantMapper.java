package com.paradisecloud.fcm.dao.mapper;




import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudTemplatePollingPaticipant;

import java.util.List;

/**
 * Hwcloud.0MCU轮询方案的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuHwcloudTemplatePollingPaticipantMapper
{
    /**
     * 查询Hwcloud.0MCU轮询方案的参会者
     * 
     * @param id Hwcloud.0MCU轮询方案的参会者ID
     * @return Hwcloud.0MCU轮询方案的参会者
     */
    public BusiMcuHwcloudTemplatePollingPaticipant selectBusiMcuHwcloudTemplatePollingPaticipantById(Long id);

    /**
     * 查询Hwcloud.0MCU轮询方案的参会者列表
     * 
     * @param busiMcuHwcloudTemplatePollingPaticipant Hwcloud.0MCU轮询方案的参会者
     * @return Hwcloud.0MCU轮询方案的参会者集合
     */
    public List<BusiMcuHwcloudTemplatePollingPaticipant> selectBusiMcuHwcloudTemplatePollingPaticipantList(BusiMcuHwcloudTemplatePollingPaticipant busiMcuHwcloudTemplatePollingPaticipant);

    /**
     * 新增Hwcloud.0MCU轮询方案的参会者
     * 
     * @param busiMcuHwcloudTemplatePollingPaticipant Hwcloud.0MCU轮询方案的参会者
     * @return 结果
     */
    public int insertBusiMcuHwcloudTemplatePollingPaticipant(BusiMcuHwcloudTemplatePollingPaticipant busiMcuHwcloudTemplatePollingPaticipant);

    /**
     * 修改Hwcloud.0MCU轮询方案的参会者
     * 
     * @param busiMcuHwcloudTemplatePollingPaticipant Hwcloud.0MCU轮询方案的参会者
     * @return 结果
     */
    public int updateBusiMcuHwcloudTemplatePollingPaticipant(BusiMcuHwcloudTemplatePollingPaticipant busiMcuHwcloudTemplatePollingPaticipant);

    /**
     * 删除Hwcloud.0MCU轮询方案的参会者
     * 
     * @param id Hwcloud.0MCU轮询方案的参会者ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudTemplatePollingPaticipantById(Long id);

    /**
     * 批量删除Hwcloud.0MCU轮询方案的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudTemplatePollingPaticipantByIds(Long[] ids);

    void deletePollingPaticipantByPollingSchemeId(Long id);

    int deleteBusiMcuHwcloudTemplatePollingPaticipantByAttendeeIds(String[] toArray);
}
