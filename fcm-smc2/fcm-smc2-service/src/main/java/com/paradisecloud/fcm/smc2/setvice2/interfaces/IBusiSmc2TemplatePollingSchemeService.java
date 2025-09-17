package com.paradisecloud.fcm.smc2.setvice2.interfaces;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2TemplatePollingDept;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2TemplatePollingPaticipant;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2TemplatePollingScheme;
import com.paradisecloud.fcm.smc2.model.attendee.operation.polling.PollingScheme;

import java.util.List;

/**
 * 轮询方案Service接口
 * 
 * @author lilinhai
 * @date 2021-02-25
 */
public interface IBusiSmc2TemplatePollingSchemeService
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
    public List<BusiMcuSmc2TemplatePollingScheme> selectBusiTemplatePollingSchemeList(BusiMcuSmc2TemplatePollingScheme busiTemplatePollingScheme);

    /**
     * 新增轮询方案
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @param templatePollingPaticipants 
     * @param templatePollingDepts 
     * @return 结果
     */
    public int insertBusiTemplatePollingScheme(BusiMcuSmc2TemplatePollingScheme busiTemplatePollingScheme
            , List<BusiMcuSmc2TemplatePollingDept> templatePollingDepts
            , List<BusiMcuSmc2TemplatePollingPaticipant> templatePollingPaticipants);

    /**
     * 修改轮询方案
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @param templatePollingPaticipants 
     * @param templatePollingDepts 
     * @return 结果
     */
    public int updateBusiTemplatePollingScheme(BusiMcuSmc2TemplatePollingScheme busiTemplatePollingScheme
            , List<BusiMcuSmc2TemplatePollingDept> templatePollingDepts
            , List<BusiMcuSmc2TemplatePollingPaticipant> templatePollingPaticipants);

    /**
     * 保持轮询方案基本信息
     * @author lilinhai
     * @since 2021-02-26 15:42 
     * @return int
     */
    int updateBusiTemplatePollingSchemes(List<BusiMcuSmc2TemplatePollingScheme> templatePollingSchemes);
    
    /**
     * 入会方案对象转换
     * @author lilinhai
     * @since 2021-05-12 10:54 
     * @param ps
     * @param conferenceContext
     * @return PollingScheme
     */
    PollingScheme convert(BusiMcuSmc2TemplatePollingScheme ps, Smc2ConferenceContext conferenceContext);
    
    /**
     * 删除轮询方案信息
     * 
     * @param id 轮询方案ID
     * @return 结果
     */
    public int deleteBusiTemplatePollingSchemeById(Long id);
}
