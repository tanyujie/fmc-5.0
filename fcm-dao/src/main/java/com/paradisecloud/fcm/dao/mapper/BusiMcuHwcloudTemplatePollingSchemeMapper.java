package com.paradisecloud.fcm.dao.mapper;




import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudTemplatePollingScheme;

import java.util.List;

/**
 * Hwcloud.0MCU轮询方案Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuHwcloudTemplatePollingSchemeMapper
{
    /**
     * 查询Hwcloud.0MCU轮询方案
     * 
     * @param id Hwcloud.0MCU轮询方案ID
     * @return Hwcloud.0MCU轮询方案
     */
    public BusiMcuHwcloudTemplatePollingScheme selectBusiMcuHwcloudTemplatePollingSchemeById(Long id);

    /**
     * 查询Hwcloud.0MCU轮询方案列表
     * 
     * @param busiMcuHwcloudTemplatePollingScheme Hwcloud.0MCU轮询方案
     * @return Hwcloud.0MCU轮询方案集合
     */
    public List<BusiMcuHwcloudTemplatePollingScheme> selectBusiMcuHwcloudTemplatePollingSchemeList(BusiMcuHwcloudTemplatePollingScheme busiMcuHwcloudTemplatePollingScheme);

    /**
     * 新增Hwcloud.0MCU轮询方案
     * 
     * @param busiMcuHwcloudTemplatePollingScheme Hwcloud.0MCU轮询方案
     * @return 结果
     */
    public int insertBusiMcuHwcloudTemplatePollingScheme(BusiMcuHwcloudTemplatePollingScheme busiMcuHwcloudTemplatePollingScheme);

    /**
     * 修改Hwcloud.0MCU轮询方案
     * 
     * @param busiMcuHwcloudTemplatePollingScheme Hwcloud.0MCU轮询方案
     * @return 结果
     */
    public int updateBusiMcuHwcloudTemplatePollingScheme(BusiMcuHwcloudTemplatePollingScheme busiMcuHwcloudTemplatePollingScheme);

    /**
     * 删除Hwcloud.0MCU轮询方案
     * 
     * @param id Hwcloud.0MCU轮询方案ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudTemplatePollingSchemeById(Long id);

    /**
     * 批量删除Hwcloud.0MCU轮询方案
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudTemplatePollingSchemeByIds(Long[] ids);
}
