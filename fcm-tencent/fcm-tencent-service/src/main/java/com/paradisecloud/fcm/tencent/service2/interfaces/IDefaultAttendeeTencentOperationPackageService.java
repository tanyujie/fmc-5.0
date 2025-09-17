/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : IDefaultAttendeeOperationPackageService.java
 * Package     : com.paradisecloud.fcm.fme.service.interfaces
 * @author lilinhai 
 * @since 2021-04-12 18:47
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.tencent.service2.interfaces;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContext;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentTemplateConference;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentTemplateParticipant;
import com.paradisecloud.fcm.tencent.model.operation.DefaultAttendeeOperation;


import java.util.Map;

/**  
 * <pre>默认参会者操作对象封装器</pre>
 * @author lilinhai
 * @since 2021-04-12 18:47
 * @version V1.0  
 */
public interface IDefaultAttendeeTencentOperationPackageService
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
    DefaultAttendeeOperation packing(TencentConferenceContext conferenceContext, BusiMcuTencentTemplateConference tc, Map<Long, BusiMcuTencentTemplateParticipant> busiTemplateParticipantMap);

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
     * @since 2021-09-02 22:59 
     * @param conferenceId
     * @return Object
     */
    JSONObject defaultViewData(String conferenceId);
}
