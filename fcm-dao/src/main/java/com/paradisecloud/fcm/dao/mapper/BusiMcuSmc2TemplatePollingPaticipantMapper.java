package com.paradisecloud.fcm.dao.mapper;


import com.paradisecloud.fcm.dao.model.BusiMcuSmc2TemplatePollingPaticipant;

import java.util.List;

/**
 * SMC2.0MCU轮询方案的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuSmc2TemplatePollingPaticipantMapper
{
    /**
     * 查询SMC2.0MCU轮询方案的参会者
     * 
     * @param id SMC2.0MCU轮询方案的参会者ID
     * @return SMC2.0MCU轮询方案的参会者
     */
    public BusiMcuSmc2TemplatePollingPaticipant selectBusiMcuSmc2TemplatePollingPaticipantById(Long id);

    /**
     * 查询SMC2.0MCU轮询方案的参会者列表
     * 
     * @param busiMcuSmc2TemplatePollingPaticipant SMC2.0MCU轮询方案的参会者
     * @return SMC2.0MCU轮询方案的参会者集合
     */
    public List<BusiMcuSmc2TemplatePollingPaticipant> selectBusiMcuSmc2TemplatePollingPaticipantList(BusiMcuSmc2TemplatePollingPaticipant busiMcuSmc2TemplatePollingPaticipant);

    /**
     * 新增SMC2.0MCU轮询方案的参会者
     * 
     * @param busiMcuSmc2TemplatePollingPaticipant SMC2.0MCU轮询方案的参会者
     * @return 结果
     */
    public int insertBusiMcuSmc2TemplatePollingPaticipant(BusiMcuSmc2TemplatePollingPaticipant busiMcuSmc2TemplatePollingPaticipant);

    /**
     * 修改SMC2.0MCU轮询方案的参会者
     * 
     * @param busiMcuSmc2TemplatePollingPaticipant SMC2.0MCU轮询方案的参会者
     * @return 结果
     */
    public int updateBusiMcuSmc2TemplatePollingPaticipant(BusiMcuSmc2TemplatePollingPaticipant busiMcuSmc2TemplatePollingPaticipant);

    /**
     * 删除SMC2.0MCU轮询方案的参会者
     * 
     * @param id SMC2.0MCU轮询方案的参会者ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2TemplatePollingPaticipantById(Long id);

    /**
     * 批量删除SMC2.0MCU轮询方案的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2TemplatePollingPaticipantByIds(Long[] ids);

    void deletePollingPaticipantByPollingSchemeId(Long id);

    int deleteBusiMcuSmc2TemplatePollingPaticipantByAttendeeIds(String[] toArray);
}
