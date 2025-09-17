package com.paradisecloud.fcm.tencent.service2.interfaces;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContext;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentTemplatePollingDept;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentTemplatePollingPaticipant;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentTemplatePollingScheme;
import com.paradisecloud.fcm.tencent.model.operation.polling.PollingScheme;

import java.util.List;

/**
 * 轮询方案Service接口
 * 
 * @author lilinhai
 * @date 2021-02-25
 */
public interface IBusiTencentTemplatePollingSchemeService
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
    public List<BusiMcuTencentTemplatePollingScheme> selectBusiTemplatePollingSchemeList(BusiMcuTencentTemplatePollingScheme busiTemplatePollingScheme);

    /**
     * 新增轮询方案
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @param templatePollingPaticipants 
     * @param templatePollingDepts 
     * @return 结果
     */
    public int insertBusiTemplatePollingScheme(BusiMcuTencentTemplatePollingScheme busiTemplatePollingScheme
            , List<BusiMcuTencentTemplatePollingDept> templatePollingDepts
            , List<BusiMcuTencentTemplatePollingPaticipant> templatePollingPaticipants);

    /**
     * 修改轮询方案
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @param templatePollingPaticipants 
     * @param templatePollingDepts 
     * @return 结果
     */
    public int updateBusiTemplatePollingScheme(BusiMcuTencentTemplatePollingScheme busiTemplatePollingScheme
            , List<BusiMcuTencentTemplatePollingDept> templatePollingDepts
            , List<BusiMcuTencentTemplatePollingPaticipant> templatePollingPaticipants);

    /**
     * 保持轮询方案基本信息
     * @author lilinhai
     * @since 2021-02-26 15:42 
     * @return int
     */
    int updateBusiTemplatePollingSchemes(List<BusiMcuTencentTemplatePollingScheme> templatePollingSchemes);
    
    /**
     * 入会方案对象转换
     * @author lilinhai
     * @since 2021-05-12 10:54 
     * @param ps
     * @param conferenceContext
     * @return PollingScheme
     */
    PollingScheme convert(BusiMcuTencentTemplatePollingScheme ps, TencentConferenceContext conferenceContext);
    
    /**
     * 删除轮询方案信息
     * 
     * @param id 轮询方案ID
     * @return 结果
     */
    public int deleteBusiTemplatePollingSchemeById(Long id);
}
