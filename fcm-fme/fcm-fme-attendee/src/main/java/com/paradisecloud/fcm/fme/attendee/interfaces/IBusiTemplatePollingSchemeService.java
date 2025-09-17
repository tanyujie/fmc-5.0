package com.paradisecloud.fcm.fme.attendee.interfaces;

import java.util.List;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiTemplatePollingDept;
import com.paradisecloud.fcm.dao.model.BusiTemplatePollingPaticipant;
import com.paradisecloud.fcm.dao.model.BusiTemplatePollingScheme;
import com.paradisecloud.fcm.fme.attendee.model.polling.PollingScheme;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;

/**
 * 轮询方案Service接口
 * 
 * @author lilinhai
 * @date 2021-02-25
 */
public interface IBusiTemplatePollingSchemeService 
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
    public List<BusiTemplatePollingScheme> selectBusiTemplatePollingSchemeList(BusiTemplatePollingScheme busiTemplatePollingScheme);

    /**
     * 新增轮询方案
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @param templatePollingPaticipants 
     * @param templatePollingDepts 
     * @return 结果
     */
    public int insertBusiTemplatePollingScheme(BusiTemplatePollingScheme busiTemplatePollingScheme
            , List<BusiTemplatePollingDept> templatePollingDepts
            , List<BusiTemplatePollingPaticipant> templatePollingPaticipants);

    /**
     * 修改轮询方案
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @param templatePollingPaticipants 
     * @param templatePollingDepts 
     * @return 结果
     */
    public int updateBusiTemplatePollingScheme(BusiTemplatePollingScheme busiTemplatePollingScheme
            , List<BusiTemplatePollingDept> templatePollingDepts
            , List<BusiTemplatePollingPaticipant> templatePollingPaticipants);

    /**
     * 保持轮询方案基本信息
     * @author lilinhai
     * @since 2021-02-26 15:42 
     * @param busiTemplatePollingScheme
     * @return int
     */
    int updateBusiTemplatePollingSchemes(List<BusiTemplatePollingScheme> templatePollingSchemes);
    
    /**
     * 入会方案对象转换
     * @author lilinhai
     * @since 2021-05-13 10:54 
     * @param ps
     * @param conferenceContext
     * @return PollingScheme
     */
    PollingScheme convert(BusiTemplatePollingScheme ps, ConferenceContext conferenceContext);
    
    /**
     * 删除轮询方案信息
     * 
     * @param id 轮询方案ID
     * @return 结果
     */
    public int deleteBusiTemplatePollingSchemeById(Long id);
}
