/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : IDefaultAttendeeOperationPackageService.java
 * Package     : com.paradisecloud.fcm.fme.service.interfaces
 * @author lilinhai 
 * @since 2021-04-12 18:47
 * @version  V1.0
 */ 
package com.paradisecloud.smc3.service.interfaces;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.smc3.busi.DefaultAttendeeOperation;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplateConference;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplateParticipant;

import java.util.Map;

/**  
 * <pre>默认参会者操作对象封装器</pre>
 * @author lilinhai
 * @since 2021-04-12 18:47
 * @version V1.0  
 */
public interface IDefaultAttendeeSmc3OperationPackageService
{
    
    /**
     * 包装默认参会者操作
     * @author lilinhai
     * @since 2021-04-12 18:50 
     * @param conferenceContext
     * @param tc 
     * @param busiTemplateParticipantMap 
     * @return DefaultAttendeeOperation
     */
    DefaultAttendeeOperation packing(Smc3ConferenceContext conferenceContext, BusiMcuSmc3TemplateConference tc, Map<Long, BusiMcuSmc3TemplateParticipant> busiTemplateParticipantMap);

    /**
     * <pre>修改会议默认视图</pre>
     * @author lilinhai
     * @since 2021-04-14 14:44 
     * @param conferenceId
     * @param jsonObj void
     */
    void updateDefaultViewConfigInfo(String conferenceId, JSONObject jsonObj);

    /**
     * <pre>获取显示布局数据对象</pre>
     * @author sinhy
     * @since 2021-09-03 22:59 
     * @param conferenceId
     * @return Object
     */
    JSONObject defaultViewData(String conferenceId);
}
