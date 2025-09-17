package com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudTemplatePollingDept;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudTemplatePollingPaticipant;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudTemplatePollingScheme;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation.polling.PollingScheme;

import java.util.List;

/**
 * 轮询方案Service接口
 * 
 * @author lilinhai
 * @date 2021-02-25
 */
public interface IBusiHwcloudTemplatePollingSchemeService
{
    /**
     * 查询轮询方案
     * 
     * @param id 轮询方案ID
     * @return 轮询方案
     */
    public ModelBean selectBusiTemplatePollingSchemeById(Long id);

    /**
     * 查询轮询方案列表
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @return 轮询方案集合
     */
    public List<BusiMcuHwcloudTemplatePollingScheme> selectBusiTemplatePollingSchemeList(BusiMcuHwcloudTemplatePollingScheme busiTemplatePollingScheme);

    /**
     * 新增轮询方案
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @param templatePollingPaticipants 
     * @param templatePollingDepts 
     * @return 结果
     */
    public int insertBusiTemplatePollingScheme(BusiMcuHwcloudTemplatePollingScheme busiTemplatePollingScheme
            , List<BusiMcuHwcloudTemplatePollingDept> templatePollingDepts
            , List<BusiMcuHwcloudTemplatePollingPaticipant> templatePollingPaticipants);

    /**
     * 修改轮询方案
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @param templatePollingPaticipants 
     * @param templatePollingDepts 
     * @return 结果
     */
    public int updateBusiTemplatePollingScheme(BusiMcuHwcloudTemplatePollingScheme busiTemplatePollingScheme
            , List<BusiMcuHwcloudTemplatePollingDept> templatePollingDepts
            , List<BusiMcuHwcloudTemplatePollingPaticipant> templatePollingPaticipants);

    /**
     * 保持轮询方案基本信息
     * @author lilinhai
     * @since 2021-02-26 15:42 
     * @return int
     */
    int updateBusiTemplatePollingSchemes(List<BusiMcuHwcloudTemplatePollingScheme> templatePollingSchemes);
    
    /**
     * 入会方案对象转换
     * @author lilinhai
     * @since 2021-05-12 10:54 
     * @param ps
     * @param conferenceContext
     * @return PollingScheme
     */
    PollingScheme convert(BusiMcuHwcloudTemplatePollingScheme ps, HwcloudConferenceContext conferenceContext);
    
    /**
     * 删除轮询方案信息
     * 
     * @param id 轮询方案ID
     * @return 结果
     */
    public int deleteBusiTemplatePollingSchemeById(Long id);
}
