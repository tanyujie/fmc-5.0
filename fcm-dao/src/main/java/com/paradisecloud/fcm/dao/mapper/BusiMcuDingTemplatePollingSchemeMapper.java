package com.paradisecloud.fcm.dao.mapper;




import com.paradisecloud.fcm.dao.model.BusiMcuDingTemplatePollingScheme;

import java.util.List;

/**
 * Ding.0MCU轮询方案Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuDingTemplatePollingSchemeMapper
{
    /**
     * 查询Ding.0MCU轮询方案
     * 
     * @param id Ding.0MCU轮询方案ID
     * @return Ding.0MCU轮询方案
     */
    public BusiMcuDingTemplatePollingScheme selectBusiMcuDingTemplatePollingSchemeById(Long id);

    /**
     * 查询Ding.0MCU轮询方案列表
     * 
     * @param busiMcuDingTemplatePollingScheme Ding.0MCU轮询方案
     * @return Ding.0MCU轮询方案集合
     */
    public List<BusiMcuDingTemplatePollingScheme> selectBusiMcuDingTemplatePollingSchemeList(BusiMcuDingTemplatePollingScheme busiMcuDingTemplatePollingScheme);

    /**
     * 新增Ding.0MCU轮询方案
     * 
     * @param busiMcuDingTemplatePollingScheme Ding.0MCU轮询方案
     * @return 结果
     */
    public int insertBusiMcuDingTemplatePollingScheme(BusiMcuDingTemplatePollingScheme busiMcuDingTemplatePollingScheme);

    /**
     * 修改Ding.0MCU轮询方案
     * 
     * @param busiMcuDingTemplatePollingScheme Ding.0MCU轮询方案
     * @return 结果
     */
    public int updateBusiMcuDingTemplatePollingScheme(BusiMcuDingTemplatePollingScheme busiMcuDingTemplatePollingScheme);

    /**
     * 删除Ding.0MCU轮询方案
     * 
     * @param id Ding.0MCU轮询方案ID
     * @return 结果
     */
    public int deleteBusiMcuDingTemplatePollingSchemeById(Long id);

    /**
     * 批量删除Ding.0MCU轮询方案
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuDingTemplatePollingSchemeByIds(Long[] ids);
}
