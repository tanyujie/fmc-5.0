package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuZteTemplatePollingScheme;

import java.util.List;

/**
 * 中兴MCU轮询方案Mapper接口
 * 
 * @author lilinhai
 * @date 2024-04-09
 */
public interface BusiMcuZteTemplatePollingSchemeMapper 
{
    /**
     * 查询中兴MCU轮询方案
     * 
     * @param id 中兴MCU轮询方案ID
     * @return 中兴MCU轮询方案
     */
    public BusiMcuZteTemplatePollingScheme selectBusiMcuZteTemplatePollingSchemeById(Long id);

    /**
     * 查询中兴MCU轮询方案列表
     * 
     * @param busiMcuZteTemplatePollingScheme 中兴MCU轮询方案
     * @return 中兴MCU轮询方案集合
     */
    public List<BusiMcuZteTemplatePollingScheme> selectBusiMcuZteTemplatePollingSchemeList(BusiMcuZteTemplatePollingScheme busiMcuZteTemplatePollingScheme);

    /**
     * 新增中兴MCU轮询方案
     * 
     * @param busiMcuZteTemplatePollingScheme 中兴MCU轮询方案
     * @return 结果
     */
    public int insertBusiMcuZteTemplatePollingScheme(BusiMcuZteTemplatePollingScheme busiMcuZteTemplatePollingScheme);

    /**
     * 修改中兴MCU轮询方案
     * 
     * @param busiMcuZteTemplatePollingScheme 中兴MCU轮询方案
     * @return 结果
     */
    public int updateBusiMcuZteTemplatePollingScheme(BusiMcuZteTemplatePollingScheme busiMcuZteTemplatePollingScheme);

    /**
     * 删除中兴MCU轮询方案
     * 
     * @param id 中兴MCU轮询方案ID
     * @return 结果
     */
    public int deleteBusiMcuZteTemplatePollingSchemeById(Long id);

    /**
     * 批量删除中兴MCU轮询方案
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuZteTemplatePollingSchemeByIds(Long[] ids);
}
