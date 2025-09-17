package com.paradisecloud.fcm.mcu.kdc.service.interfaces;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiMcuKdcTemplatePollingDept;
import com.paradisecloud.fcm.dao.model.BusiMcuKdcTemplatePollingPaticipant;
import com.paradisecloud.fcm.dao.model.BusiMcuKdcTemplatePollingScheme;
import com.paradisecloud.fcm.mcu.kdc.attendee.model.polling.PollingScheme;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;

import java.util.List;

/**
 * 轮询方案Service接口
 * 
 * @author lilinhai
 * @date 2021-02-25
 */
public interface IBusiMcuKdcTemplatePollingSchemeService 
{
    /**
     * 查询轮询方案
     * 
     * @param id 轮询方案ID
     * @return 轮询方案
     */
    ModelBean selectBusiMcuKdcTemplatePollingSchemeById(Long id);

    /**
     * 查询轮询方案列表
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @return 轮询方案集合
     */
    List<BusiMcuKdcTemplatePollingScheme> selectBusiMcuKdcTemplatePollingSchemeList(BusiMcuKdcTemplatePollingScheme busiTemplatePollingScheme);

    /**
     * 新增轮询方案
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @param templatePollingPaticipants 
     * @param templatePollingDepts 
     * @return 结果
     */
    int insertBusiMcuKdcTemplatePollingScheme(BusiMcuKdcTemplatePollingScheme busiTemplatePollingScheme
            , List<BusiMcuKdcTemplatePollingDept> templatePollingDepts
            , List<BusiMcuKdcTemplatePollingPaticipant> templatePollingPaticipants);

    /**
     * 修改轮询方案
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @param templatePollingPaticipants 
     * @param templatePollingDepts 
     * @return 结果
     */
    int updateBusiMcuKdcTemplatePollingScheme(BusiMcuKdcTemplatePollingScheme busiTemplatePollingScheme
            , List<BusiMcuKdcTemplatePollingDept> templatePollingDepts
            , List<BusiMcuKdcTemplatePollingPaticipant> templatePollingPaticipants);

    /**
     * 保持轮询方案基本信息
     * @author lilinhai
     * @since 2021-02-26 15:42 
     * @return int
     */
    int updateBusiMcuKdcTemplatePollingSchemes(List<BusiMcuKdcTemplatePollingScheme> templatePollingSchemes);
    
    /**
     * 入会方案对象转换
     * @author lilinhai
     * @since 2021-05-13 10:54 
     * @param ps
     * @param conferenceContext
     * @return PollingScheme
     */
    PollingScheme convert(BusiMcuKdcTemplatePollingScheme ps, McuKdcConferenceContext conferenceContext);
    
    /**
     * 删除轮询方案信息
     * 
     * @param id 轮询方案ID
     * @return 结果
     */
    int deleteBusiMcuKdcTemplatePollingSchemeById(Long id);
}
