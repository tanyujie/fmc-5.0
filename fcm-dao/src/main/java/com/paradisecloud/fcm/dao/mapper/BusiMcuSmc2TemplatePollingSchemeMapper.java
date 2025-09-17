package com.paradisecloud.fcm.dao.mapper;


import com.paradisecloud.fcm.dao.model.BusiMcuSmc2TemplatePollingScheme;

import java.util.List;

/**
 * SMC2.0MCU轮询方案Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuSmc2TemplatePollingSchemeMapper
{
    /**
     * 查询SMC2.0MCU轮询方案
     * 
     * @param id SMC2.0MCU轮询方案ID
     * @return SMC2.0MCU轮询方案
     */
    public BusiMcuSmc2TemplatePollingScheme selectBusiMcuSmc2TemplatePollingSchemeById(Long id);

    /**
     * 查询SMC2.0MCU轮询方案列表
     * 
     * @param busiMcuSmc2TemplatePollingScheme SMC2.0MCU轮询方案
     * @return SMC2.0MCU轮询方案集合
     */
    public List<BusiMcuSmc2TemplatePollingScheme> selectBusiMcuSmc2TemplatePollingSchemeList(BusiMcuSmc2TemplatePollingScheme busiMcuSmc2TemplatePollingScheme);

    /**
     * 新增SMC2.0MCU轮询方案
     * 
     * @param busiMcuSmc2TemplatePollingScheme SMC2.0MCU轮询方案
     * @return 结果
     */
    public int insertBusiMcuSmc2TemplatePollingScheme(BusiMcuSmc2TemplatePollingScheme busiMcuSmc2TemplatePollingScheme);

    /**
     * 修改SMC2.0MCU轮询方案
     * 
     * @param busiMcuSmc2TemplatePollingScheme SMC2.0MCU轮询方案
     * @return 结果
     */
    public int updateBusiMcuSmc2TemplatePollingScheme(BusiMcuSmc2TemplatePollingScheme busiMcuSmc2TemplatePollingScheme);

    /**
     * 删除SMC2.0MCU轮询方案
     * 
     * @param id SMC2.0MCU轮询方案ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2TemplatePollingSchemeById(Long id);

    /**
     * 批量删除SMC2.0MCU轮询方案
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2TemplatePollingSchemeByIds(Long[] ids);
}
