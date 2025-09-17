package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiTemplatePollingScheme;

/**
 * 轮询方案Mapper接口
 * 
 * @author lilinhai
 * @date 2021-02-25
 */
public interface BusiTemplatePollingSchemeMapper 
{
    /**
     * 查询轮询方案
     * 
     * @param id 轮询方案ID
     * @return 轮询方案
     */
    public BusiTemplatePollingScheme selectBusiTemplatePollingSchemeById(Long id);

    /**
     * 查询轮询方案列表
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @return 轮询方案集合
     */
    public List<BusiTemplatePollingScheme> selectBusiTemplatePollingSchemeList(BusiTemplatePollingScheme busiTemplatePollingScheme);

    /**
     * 新增轮询方案
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @return 结果
     */
    public int insertBusiTemplatePollingScheme(BusiTemplatePollingScheme busiTemplatePollingScheme);

    /**
     * 修改轮询方案
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @return 结果
     */
    public int updateBusiTemplatePollingScheme(BusiTemplatePollingScheme busiTemplatePollingScheme);

    /**
     * 删除轮询方案
     * 
     * @param id 轮询方案ID
     * @return 结果
     */
    public int deleteBusiTemplatePollingSchemeById(Long id);
    
    /**
     * 根据模板删除轮询方案
     * @author lilinhai
     * @since 2021-03-09 14:44 
     * @param templateConferenceId
     * @return int
     */
    public int deleteBusiTemplatePollingSchemeByTemplateConferenceId(Long templateConferenceId);

    /**
     * 批量删除轮询方案
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiTemplatePollingSchemeByIds(Long[] ids);
}
