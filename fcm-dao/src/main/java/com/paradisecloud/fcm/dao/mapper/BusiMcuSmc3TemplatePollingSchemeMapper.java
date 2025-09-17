package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplatePollingScheme;

import java.util.List;

/**
 * SMC3.0MCU轮询方案Mapper接口
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
public interface BusiMcuSmc3TemplatePollingSchemeMapper 
{
    /**
     * 查询SMC3.0MCU轮询方案
     * 
     * @param id SMC3.0MCU轮询方案ID
     * @return SMC3.0MCU轮询方案
     */
    public BusiMcuSmc3TemplatePollingScheme selectBusiMcuSmc3TemplatePollingSchemeById(Long id);

    /**
     * 查询SMC3.0MCU轮询方案列表
     * 
     * @param busiMcuSmc3TemplatePollingScheme SMC3.0MCU轮询方案
     * @return SMC3.0MCU轮询方案集合
     */
    public List<BusiMcuSmc3TemplatePollingScheme> selectBusiMcuSmc3TemplatePollingSchemeList(BusiMcuSmc3TemplatePollingScheme busiMcuSmc3TemplatePollingScheme);

    /**
     * 新增SMC3.0MCU轮询方案
     * 
     * @param busiMcuSmc3TemplatePollingScheme SMC3.0MCU轮询方案
     * @return 结果
     */
    public int insertBusiMcuSmc3TemplatePollingScheme(BusiMcuSmc3TemplatePollingScheme busiMcuSmc3TemplatePollingScheme);

    /**
     * 修改SMC3.0MCU轮询方案
     * 
     * @param busiMcuSmc3TemplatePollingScheme SMC3.0MCU轮询方案
     * @return 结果
     */
    public int updateBusiMcuSmc3TemplatePollingScheme(BusiMcuSmc3TemplatePollingScheme busiMcuSmc3TemplatePollingScheme);

    /**
     * 删除SMC3.0MCU轮询方案
     * 
     * @param id SMC3.0MCU轮询方案ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3TemplatePollingSchemeById(Long id);

    /**
     * 批量删除SMC3.0MCU轮询方案
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3TemplatePollingSchemeByIds(Long[] ids);
}
