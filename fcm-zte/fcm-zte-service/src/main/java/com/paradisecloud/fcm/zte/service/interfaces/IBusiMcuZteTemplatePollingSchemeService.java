package com.paradisecloud.fcm.zte.service.interfaces;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiMcuZteTemplatePollingDept;
import com.paradisecloud.fcm.dao.model.BusiMcuZteTemplatePollingPaticipant;
import com.paradisecloud.fcm.dao.model.BusiMcuZteTemplatePollingScheme;
import com.paradisecloud.fcm.zte.attendee.model.polling.PollingScheme;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;

import java.util.List;

/**
 * 轮询方案Service接口
 * 
 * @author lilinhai
 * @date 2021-02-25
 */
public interface IBusiMcuZteTemplatePollingSchemeService
{
    /**
     * 查询轮询方案
     * 
     * @param id 轮询方案ID
     * @return 轮询方案
     */
    ModelBean selectBusiMcuZteTemplatePollingSchemeById(Long id);

    /**
     * 查询轮询方案列表
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @return 轮询方案集合
     */
    List<BusiMcuZteTemplatePollingScheme> selectBusiMcuZteTemplatePollingSchemeList(BusiMcuZteTemplatePollingScheme busiTemplatePollingScheme);

    /**
     * 新增轮询方案
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @param templatePollingPaticipants 
     * @param templatePollingDepts 
     * @return 结果
     */
    int insertBusiMcuZteTemplatePollingScheme(BusiMcuZteTemplatePollingScheme busiTemplatePollingScheme
            , List<BusiMcuZteTemplatePollingDept> templatePollingDepts
            , List<BusiMcuZteTemplatePollingPaticipant> templatePollingPaticipants);

    /**
     * 修改轮询方案
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @param templatePollingPaticipants 
     * @param templatePollingDepts 
     * @return 结果
     */
    int updateBusiMcuZteTemplatePollingScheme(BusiMcuZteTemplatePollingScheme busiTemplatePollingScheme
            , List<BusiMcuZteTemplatePollingDept> templatePollingDepts
            , List<BusiMcuZteTemplatePollingPaticipant> templatePollingPaticipants);

    /**
     * 保持轮询方案基本信息
     * @author lilinhai
     * @since 2021-02-26 15:42 
     * @return int
     */
    int updateBusiMcuZteTemplatePollingSchemes(List<BusiMcuZteTemplatePollingScheme> templatePollingSchemes);
    
    /**
     * 入会方案对象转换
     * @author lilinhai
     * @since 2021-05-13 10:54 
     * @param ps
     * @param conferenceContext
     * @return PollingScheme
     */
    PollingScheme convert(BusiMcuZteTemplatePollingScheme ps, McuZteConferenceContext conferenceContext);
    
    /**
     * 删除轮询方案信息
     * 
     * @param id 轮询方案ID
     * @return 结果
     */
    int deleteBusiMcuZteTemplatePollingSchemeById(Long id);
}
