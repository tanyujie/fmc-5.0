package com.paradisecloud.fcm.mcu.plc.service.interfaces;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiMcuPlcTemplatePollingDept;
import com.paradisecloud.fcm.dao.model.BusiMcuPlcTemplatePollingPaticipant;
import com.paradisecloud.fcm.dao.model.BusiMcuPlcTemplatePollingScheme;
import com.paradisecloud.fcm.mcu.plc.attendee.model.polling.PollingScheme;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;

import java.util.List;

/**
 * 轮询方案Service接口
 * 
 * @author lilinhai
 * @date 2021-02-25
 */
public interface IBusiMcuPlcTemplatePollingSchemeService 
{
    /**
     * 查询轮询方案
     * 
     * @param id 轮询方案ID
     * @return 轮询方案
     */
    ModelBean selectBusiMcuPlcTemplatePollingSchemeById(Long id);

    /**
     * 查询轮询方案列表
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @return 轮询方案集合
     */
    List<BusiMcuPlcTemplatePollingScheme> selectBusiMcuPlcTemplatePollingSchemeList(BusiMcuPlcTemplatePollingScheme busiTemplatePollingScheme);

    /**
     * 新增轮询方案
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @param templatePollingPaticipants 
     * @param templatePollingDepts 
     * @return 结果
     */
    int insertBusiMcuPlcTemplatePollingScheme(BusiMcuPlcTemplatePollingScheme busiTemplatePollingScheme
            , List<BusiMcuPlcTemplatePollingDept> templatePollingDepts
            , List<BusiMcuPlcTemplatePollingPaticipant> templatePollingPaticipants);

    /**
     * 修改轮询方案
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @param templatePollingPaticipants 
     * @param templatePollingDepts 
     * @return 结果
     */
    int updateBusiMcuPlcTemplatePollingScheme(BusiMcuPlcTemplatePollingScheme busiTemplatePollingScheme
            , List<BusiMcuPlcTemplatePollingDept> templatePollingDepts
            , List<BusiMcuPlcTemplatePollingPaticipant> templatePollingPaticipants);

    /**
     * 保持轮询方案基本信息
     * @author lilinhai
     * @since 2021-02-26 15:42 
     * @return int
     */
    int updateBusiMcuPlcTemplatePollingSchemes(List<BusiMcuPlcTemplatePollingScheme> templatePollingSchemes);
    
    /**
     * 入会方案对象转换
     * @author lilinhai
     * @since 2021-05-13 10:54 
     * @param ps
     * @param conferenceContext
     * @return PollingScheme
     */
    PollingScheme convert(BusiMcuPlcTemplatePollingScheme ps, McuPlcConferenceContext conferenceContext);
    
    /**
     * 删除轮询方案信息
     * 
     * @param id 轮询方案ID
     * @return 结果
     */
    int deleteBusiMcuPlcTemplatePollingSchemeById(Long id);
}
