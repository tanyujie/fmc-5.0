package com.paradisecloud.fcm.mcu.zj.service.interfaces;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiMcuZjTemplatePollingDept;
import com.paradisecloud.fcm.dao.model.BusiMcuZjTemplatePollingPaticipant;
import com.paradisecloud.fcm.dao.model.BusiMcuZjTemplatePollingScheme;
import com.paradisecloud.fcm.mcu.zj.attendee.model.polling.PollingScheme;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;

import java.util.List;

/**
 * 轮询方案Service接口
 * 
 * @author lilinhai
 * @date 2021-02-25
 */
public interface IBusiMcuZjTemplatePollingSchemeService 
{
    /**
     * 查询轮询方案
     * 
     * @param id 轮询方案ID
     * @return 轮询方案
     */
    ModelBean selectBusiMcuZjTemplatePollingSchemeById(Long id);

    /**
     * 查询轮询方案列表
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @return 轮询方案集合
     */
    List<BusiMcuZjTemplatePollingScheme> selectBusiMcuZjTemplatePollingSchemeList(BusiMcuZjTemplatePollingScheme busiTemplatePollingScheme);

    /**
     * 新增轮询方案
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @param templatePollingPaticipants 
     * @param templatePollingDepts 
     * @return 结果
     */
    int insertBusiMcuZjTemplatePollingScheme(BusiMcuZjTemplatePollingScheme busiTemplatePollingScheme
            , List<BusiMcuZjTemplatePollingDept> templatePollingDepts
            , List<BusiMcuZjTemplatePollingPaticipant> templatePollingPaticipants);

    /**
     * 修改轮询方案
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @param templatePollingPaticipants 
     * @param templatePollingDepts 
     * @return 结果
     */
    int updateBusiMcuZjTemplatePollingScheme(BusiMcuZjTemplatePollingScheme busiTemplatePollingScheme
            , List<BusiMcuZjTemplatePollingDept> templatePollingDepts
            , List<BusiMcuZjTemplatePollingPaticipant> templatePollingPaticipants);

    /**
     * 保持轮询方案基本信息
     * @author lilinhai
     * @since 2021-02-26 15:42 
     * @return int
     */
    int updateBusiMcuZjTemplatePollingSchemes(List<BusiMcuZjTemplatePollingScheme> templatePollingSchemes);
    
    /**
     * 入会方案对象转换
     * @author lilinhai
     * @since 2021-05-13 10:54 
     * @param ps
     * @param conferenceContext
     * @return PollingScheme
     */
    PollingScheme convert(BusiMcuZjTemplatePollingScheme ps, McuZjConferenceContext conferenceContext);
    
    /**
     * 删除轮询方案信息
     * 
     * @param id 轮询方案ID
     * @return 结果
     */
    int deleteBusiMcuZjTemplatePollingSchemeById(Long id);
}
