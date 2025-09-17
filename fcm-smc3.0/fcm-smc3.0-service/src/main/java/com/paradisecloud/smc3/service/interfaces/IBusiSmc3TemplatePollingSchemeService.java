package com.paradisecloud.smc3.service.interfaces;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.operation.polling.PollingScheme;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplatePollingDept;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplatePollingPaticipant;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplatePollingScheme;

import java.util.List;

/**
 * 轮询方案Service接口
 * 
 * @author lilinhai
 * @date 2021-02-25
 */
public interface IBusiSmc3TemplatePollingSchemeService
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
    public List<BusiMcuSmc3TemplatePollingScheme> selectBusiTemplatePollingSchemeList(BusiMcuSmc3TemplatePollingScheme busiTemplatePollingScheme);

    /**
     * 新增轮询方案
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @param templatePollingPaticipants 
     * @param templatePollingDepts 
     * @return 结果
     */
    public int insertBusiTemplatePollingScheme(BusiMcuSmc3TemplatePollingScheme busiTemplatePollingScheme
            , List<BusiMcuSmc3TemplatePollingDept> templatePollingDepts
            , List<BusiMcuSmc3TemplatePollingPaticipant> templatePollingPaticipants);

    /**
     * 修改轮询方案
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @param templatePollingPaticipants 
     * @param templatePollingDepts 
     * @return 结果
     */
    public int updateBusiTemplatePollingScheme(BusiMcuSmc3TemplatePollingScheme busiTemplatePollingScheme
            , List<BusiMcuSmc3TemplatePollingDept> templatePollingDepts
            , List<BusiMcuSmc3TemplatePollingPaticipant> templatePollingPaticipants);

    /**
     * 保持轮询方案基本信息
     * @author lilinhai
     * @since 2021-02-26 15:42 
     * @return int
     */
    int updateBusiTemplatePollingSchemes(List<BusiMcuSmc3TemplatePollingScheme> templatePollingSchemes);
    
    /**
     * 入会方案对象转换
     * @author lilinhai
     * @since 2021-05-13 10:54 
     * @param ps
     * @param conferenceContext
     * @return PollingScheme
     */
    PollingScheme convert(BusiMcuSmc3TemplatePollingScheme ps, Smc3ConferenceContext conferenceContext);
    
    /**
     * 删除轮询方案信息
     * 
     * @param id 轮询方案ID
     * @return 结果
     */
    public int deleteBusiTemplatePollingSchemeById(Long id);
}
