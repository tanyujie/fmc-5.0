package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplatePollingPaticipant;

import java.util.List;

/**
 * SMC3.0MCU轮询方案的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
public interface BusiMcuSmc3TemplatePollingPaticipantMapper 
{
    /**
     * 查询SMC3.0MCU轮询方案的参会者
     * 
     * @param id SMC3.0MCU轮询方案的参会者ID
     * @return SMC3.0MCU轮询方案的参会者
     */
    public BusiMcuSmc3TemplatePollingPaticipant selectBusiMcuSmc3TemplatePollingPaticipantById(Long id);

    /**
     * 查询SMC3.0MCU轮询方案的参会者列表
     * 
     * @param busiMcuSmc3TemplatePollingPaticipant SMC3.0MCU轮询方案的参会者
     * @return SMC3.0MCU轮询方案的参会者集合
     */
    public List<BusiMcuSmc3TemplatePollingPaticipant> selectBusiMcuSmc3TemplatePollingPaticipantList(BusiMcuSmc3TemplatePollingPaticipant busiMcuSmc3TemplatePollingPaticipant);

    /**
     * 新增SMC3.0MCU轮询方案的参会者
     * 
     * @param busiMcuSmc3TemplatePollingPaticipant SMC3.0MCU轮询方案的参会者
     * @return 结果
     */
    public int insertBusiMcuSmc3TemplatePollingPaticipant(BusiMcuSmc3TemplatePollingPaticipant busiMcuSmc3TemplatePollingPaticipant);

    /**
     * 修改SMC3.0MCU轮询方案的参会者
     * 
     * @param busiMcuSmc3TemplatePollingPaticipant SMC3.0MCU轮询方案的参会者
     * @return 结果
     */
    public int updateBusiMcuSmc3TemplatePollingPaticipant(BusiMcuSmc3TemplatePollingPaticipant busiMcuSmc3TemplatePollingPaticipant);

    /**
     * 删除SMC3.0MCU轮询方案的参会者
     * 
     * @param id SMC3.0MCU轮询方案的参会者ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3TemplatePollingPaticipantById(Long id);

    /**
     * 批量删除SMC3.0MCU轮询方案的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3TemplatePollingPaticipantByIds(Long[] ids);

    void deletePollingPaticipantByPollingSchemeId(Long id);

    int deleteBusiMcuSmc3TemplatePollingPaticipantByAttendeeIds(String[] toArray);
}
