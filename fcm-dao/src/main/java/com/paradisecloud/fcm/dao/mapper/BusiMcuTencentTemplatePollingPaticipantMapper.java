package com.paradisecloud.fcm.dao.mapper;




import com.paradisecloud.fcm.dao.model.BusiMcuTencentTemplatePollingPaticipant;

import java.util.List;

/**
 * Tencent.0MCU轮询方案的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuTencentTemplatePollingPaticipantMapper
{
    /**
     * 查询Tencent.0MCU轮询方案的参会者
     * 
     * @param id Tencent.0MCU轮询方案的参会者ID
     * @return Tencent.0MCU轮询方案的参会者
     */
    public BusiMcuTencentTemplatePollingPaticipant selectBusiMcuTencentTemplatePollingPaticipantById(Long id);

    /**
     * 查询Tencent.0MCU轮询方案的参会者列表
     * 
     * @param busiMcuTencentTemplatePollingPaticipant Tencent.0MCU轮询方案的参会者
     * @return Tencent.0MCU轮询方案的参会者集合
     */
    public List<BusiMcuTencentTemplatePollingPaticipant> selectBusiMcuTencentTemplatePollingPaticipantList(BusiMcuTencentTemplatePollingPaticipant busiMcuTencentTemplatePollingPaticipant);

    /**
     * 新增Tencent.0MCU轮询方案的参会者
     * 
     * @param busiMcuTencentTemplatePollingPaticipant Tencent.0MCU轮询方案的参会者
     * @return 结果
     */
    public int insertBusiMcuTencentTemplatePollingPaticipant(BusiMcuTencentTemplatePollingPaticipant busiMcuTencentTemplatePollingPaticipant);

    /**
     * 修改Tencent.0MCU轮询方案的参会者
     * 
     * @param busiMcuTencentTemplatePollingPaticipant Tencent.0MCU轮询方案的参会者
     * @return 结果
     */
    public int updateBusiMcuTencentTemplatePollingPaticipant(BusiMcuTencentTemplatePollingPaticipant busiMcuTencentTemplatePollingPaticipant);

    /**
     * 删除Tencent.0MCU轮询方案的参会者
     * 
     * @param id Tencent.0MCU轮询方案的参会者ID
     * @return 结果
     */
    public int deleteBusiMcuTencentTemplatePollingPaticipantById(Long id);

    /**
     * 批量删除Tencent.0MCU轮询方案的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuTencentTemplatePollingPaticipantByIds(Long[] ids);

    void deletePollingPaticipantByPollingSchemeId(Long id);

    int deleteBusiMcuTencentTemplatePollingPaticipantByAttendeeIds(String[] toArray);
}
