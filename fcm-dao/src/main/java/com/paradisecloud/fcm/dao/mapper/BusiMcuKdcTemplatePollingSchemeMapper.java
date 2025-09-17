package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiMcuKdcTemplatePollingScheme;

/**
 * 轮询方案Mapper接口
 * 
 * @author lilinhai
 * @date 2021-02-25
 */
public interface BusiMcuKdcTemplatePollingSchemeMapper 
{
    /**
     * 查询轮询方案
     * 
     * @param id 轮询方案ID
     * @return 轮询方案
     */
    public BusiMcuKdcTemplatePollingScheme selectBusiMcuKdcTemplatePollingSchemeById(Long id);

    /**
     * 查询轮询方案列表
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @return 轮询方案集合
     */
    public List<BusiMcuKdcTemplatePollingScheme> selectBusiMcuKdcTemplatePollingSchemeList(BusiMcuKdcTemplatePollingScheme busiTemplatePollingScheme);

    /**
     * 新增轮询方案
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @return 结果
     */
    public int insertBusiMcuKdcTemplatePollingScheme(BusiMcuKdcTemplatePollingScheme busiTemplatePollingScheme);

    /**
     * 修改轮询方案
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @return 结果
     */
    public int updateBusiMcuKdcTemplatePollingScheme(BusiMcuKdcTemplatePollingScheme busiTemplatePollingScheme);

    /**
     * 删除轮询方案
     * 
     * @param id 轮询方案ID
     * @return 结果
     */
    public int deleteBusiMcuKdcTemplatePollingSchemeById(Long id);
    
    /**
     * 根据模板删除轮询方案
     * @author lilinhai
     * @since 2021-03-09 14:44 
     * @param templateConferenceId
     * @return int
     */
    public int deleteBusiMcuKdcTemplatePollingSchemeByTemplateConferenceId(Long templateConferenceId);

    /**
     * 批量删除轮询方案
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuKdcTemplatePollingSchemeByIds(Long[] ids);
}
