package com.paradisecloud.fcm.dao.mapper;




import com.paradisecloud.fcm.dao.model.BusiMcuTencentTemplatePollingScheme;

import java.util.List;

/**
 * Tencent.0MCU轮询方案Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuTencentTemplatePollingSchemeMapper
{
    /**
     * 查询Tencent.0MCU轮询方案
     * 
     * @param id Tencent.0MCU轮询方案ID
     * @return Tencent.0MCU轮询方案
     */
    public BusiMcuTencentTemplatePollingScheme selectBusiMcuTencentTemplatePollingSchemeById(Long id);

    /**
     * 查询Tencent.0MCU轮询方案列表
     * 
     * @param busiMcuTencentTemplatePollingScheme Tencent.0MCU轮询方案
     * @return Tencent.0MCU轮询方案集合
     */
    public List<BusiMcuTencentTemplatePollingScheme> selectBusiMcuTencentTemplatePollingSchemeList(BusiMcuTencentTemplatePollingScheme busiMcuTencentTemplatePollingScheme);

    /**
     * 新增Tencent.0MCU轮询方案
     * 
     * @param busiMcuTencentTemplatePollingScheme Tencent.0MCU轮询方案
     * @return 结果
     */
    public int insertBusiMcuTencentTemplatePollingScheme(BusiMcuTencentTemplatePollingScheme busiMcuTencentTemplatePollingScheme);

    /**
     * 修改Tencent.0MCU轮询方案
     * 
     * @param busiMcuTencentTemplatePollingScheme Tencent.0MCU轮询方案
     * @return 结果
     */
    public int updateBusiMcuTencentTemplatePollingScheme(BusiMcuTencentTemplatePollingScheme busiMcuTencentTemplatePollingScheme);

    /**
     * 删除Tencent.0MCU轮询方案
     * 
     * @param id Tencent.0MCU轮询方案ID
     * @return 结果
     */
    public int deleteBusiMcuTencentTemplatePollingSchemeById(Long id);

    /**
     * 批量删除Tencent.0MCU轮询方案
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuTencentTemplatePollingSchemeByIds(Long[] ids);
}
