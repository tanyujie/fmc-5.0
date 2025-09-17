
package com.paradisecloud.fcm.zte.service.interfaces;

import com.alibaba.fastjson.JSONObject;

import com.paradisecloud.fcm.dao.model.BusiMcuZteTemplateConference;
import com.paradisecloud.fcm.dao.model.BusiMcuZteTemplateParticipant;
import com.paradisecloud.fcm.zte.attendee.model.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;


import java.util.Map;

/**
 * <pre>默认参会者操作对象封装器</pre>
 * @author lilinhai
 * @since 2021-04-12 18:47
 * @version V1.0
 */
public interface IDefaultAttendeeOperationPackageForMcuZteService
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
    DefaultAttendeeOperation packing(McuZteConferenceContext conferenceContext, BusiMcuZteTemplateConference tc, Map<Long, BusiMcuZteTemplateParticipant> busiTemplateParticipantMap);

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
