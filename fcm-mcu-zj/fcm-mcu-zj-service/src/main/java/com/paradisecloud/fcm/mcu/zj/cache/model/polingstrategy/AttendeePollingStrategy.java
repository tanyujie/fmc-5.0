/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeePollingStrategy.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.polingstrategy
 * @author sinhy 
 * @since 2021-09-09 20:32
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.zj.cache.model.polingstrategy;

import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.mcu.zj.attendee.model.polling.DeptPollingAttendees;
import com.paradisecloud.fcm.mcu.zj.attendee.model.polling.PollingAttendee;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.layout.SplitScreen;
import com.paradisecloud.fcm.mcu.zj.model.busi.layout.splitscreen.OneSplitScreen;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**  
 * <pre>抽象参会者轮询策略</pre>
 * @author sinhy
 * @since 2021-09-09 20:32
 * @version V1.0  
 */
public abstract class AttendeePollingStrategy
{
    
    /**
     * 解析出相关的轮询参会
     * @since 2021-09-09 20:36 
     * @param conferenceContext
     * @param deptPollingAttendeesList
     * @return List<PollingAttendee>
     */
    public abstract List<PollingAttendee> parse(McuZjConferenceContext conferenceContext, List<DeptPollingAttendees> deptPollingAttendeesList, SplitScreen splitScreen);
    
    protected List<PollingAttendee> parseSpecifiedRangeAndDeptFirst(McuZjConferenceContext conferenceContext, List<DeptPollingAttendees> deptPollingAttendeesList)
    {
        List<PollingAttendee> pollingAttendeeList = new ArrayList<>();
        List<PollingAttendee> nonDeptPas = null;
        List<DeptPollingAttendees> deptPollingAttendeesMainList = new ArrayList<>();
        for (DeptPollingAttendees deptPollingAttendees : deptPollingAttendeesList)
        {
            List<PollingAttendee> pas = deptPollingAttendees.getPollingAttendees();
            PollingAttendee paMain = null;
            for (PollingAttendee pollingAttendee : pas)
            {
                if (conferenceContext.getMasterAttendeeIdSet().contains(pollingAttendee.getAttendee().getId()))
                {
                    paMain = pollingAttendee;
                    break;
                }
            }
            
            if (paMain == null)
            {
                nonDeptPas = pas;
            }
            else
            {
                pollingAttendeeList.add(paMain);
                deptPollingAttendeesMainList.add(deptPollingAttendees);
            }
        }
        
        if (nonDeptPas != null)
        {
            for (PollingAttendee pollingAttendee : nonDeptPas)
            {
                pollingAttendeeList.add(pollingAttendee);
            }
        }
        
        for (DeptPollingAttendees deptPollingAttendees : deptPollingAttendeesMainList)
        {
            List<PollingAttendee> pollingAttendees = deptPollingAttendees.getPollingAttendees();
            if (nonDeptPas == pollingAttendees)
            {
                continue;
            }
            for (PollingAttendee pollingAttendee : pollingAttendees)
            {
                if (!conferenceContext.getMasterAttendeeIdSet().contains(pollingAttendee.getAttendee().getId()))
                {
                    pollingAttendeeList.add(pollingAttendee);
                }
            }
        }
        return pollingAttendeeList;
    }
    
    /**
     * 全局轮询列表解析(非组织架构优先)
     * @author sinhy
     * @since 2021-09-09 21:48 
     * @param conferenceContext
     * @return List<PollingAttendee>
     */
    protected List<PollingAttendee> parseGlobalPolling(McuZjConferenceContext conferenceContext, Map<String, PollingAttendee> m, SplitScreen splitScreen)
    {
        List<PollingAttendee> pollingAttendeeList = new ArrayList<>();
        if (conferenceContext.getMasterAttendee() != null)
        {
            add(pollingAttendeeList, conferenceContext.getMasterAttendee(), m, splitScreen);
            if (conferenceContext.getMasterAttendee().getDeptId() != conferenceContext.getDeptId().longValue() && conferenceContext.getMasterAttendeeIdSet().contains(conferenceContext.getMasterAttendee().getId()))
            {
                List<AttendeeForMcuZj> as = conferenceContext.getCascadeAttendeesMap().get(conferenceContext.getMasterAttendee().getDeptId());
                if (as != null)
                {
                    for (AttendeeForMcuZj attendee : new ArrayList<>(as))
                    {
                        add(pollingAttendeeList, attendee, m, splitScreen);
                    }
                }
            }
        }
        
        for (AttendeeForMcuZj attendee : new ArrayList<>(conferenceContext.getAttendees()))
        {
            add(pollingAttendeeList, attendee, m, splitScreen);
        }
        
        for (AttendeeForMcuZj attendee : conferenceContext.getMasterAttendees())
        {
            add(pollingAttendeeList, attendee, m, splitScreen);
            List<AttendeeForMcuZj> as = conferenceContext.getCascadeAttendeesMap().get(attendee.getDeptId());
            if (as != null)
            {
                for (AttendeeForMcuZj a : new ArrayList<>(as))
                {
                    add(pollingAttendeeList, a, m, splitScreen);
                }
            }
        }
        return pollingAttendeeList;
    }
    
    /**
     * 全局轮询列表解析（组织架构优先）
     * @author sinhy
     * @since 2021-09-09 21:48 
     * @param conferenceContext
     * @return List<PollingAttendee>
     */
    protected List<PollingAttendee> parseGlobalPollingAndDeptFirst(McuZjConferenceContext conferenceContext, Map<String, PollingAttendee> m, SplitScreen splitScreen)
    {
        List<PollingAttendee> pollingAttendeeList = new ArrayList<>();
        if (conferenceContext.getMasterAttendee() != null)
        {
            add(pollingAttendeeList, conferenceContext.getMasterAttendee(), m, splitScreen);
        }
        
        for (AttendeeForMcuZj attendee : new ArrayList<>(conferenceContext.getMasterAttendees()))
        {
            add(pollingAttendeeList, attendee, m, splitScreen);
        }
        
        if (conferenceContext.getMasterAttendee() != null)
        {
            if (conferenceContext.getMasterAttendee().getDeptId() == conferenceContext.getDeptId().longValue())
            {
                for (AttendeeForMcuZj attendee : new ArrayList<>(conferenceContext.getAttendees()))
                {
                    add(pollingAttendeeList, attendee, m, splitScreen);
                }
            }
            else if (conferenceContext.getMasterAttendeeIdSet().contains(conferenceContext.getMasterAttendee().getId()))
            {
                List<AttendeeForMcuZj> as = conferenceContext.getCascadeAttendeesMap().get(conferenceContext.getMasterAttendee().getDeptId());
                if (as != null)
                {
                    for (AttendeeForMcuZj attendee : new ArrayList<>(as))
                    {
                        add(pollingAttendeeList, attendee, m, splitScreen);
                    }
                }
            }
        }
        else
        {
            for (AttendeeForMcuZj attendee : new ArrayList<>(conferenceContext.getAttendees()))
            {
                add(pollingAttendeeList, attendee, m, splitScreen);
            }
        }
        
        for (AttendeeForMcuZj attendee : conferenceContext.getMasterAttendees())
        {
            List<AttendeeForMcuZj> as = conferenceContext.getCascadeAttendeesMap().get(attendee.getDeptId());
            for (AttendeeForMcuZj a : new ArrayList<>(as))
            {
                add(pollingAttendeeList, a, m, splitScreen);
            }
        }
        return pollingAttendeeList;
    }
    
    /**
     * 选定轮询列表解析
     * @author sinhy
     * @since 2021-09-09 21:52
     * @param deptPollingAttendeesList
     * @return List<PollingAttendee>
     */
    protected List<PollingAttendee> parseSpecifiedRangePolling(List<DeptPollingAttendees> deptPollingAttendeesList)
    {
        List<PollingAttendee> pollingAttendeeList = new ArrayList<>();
        for (DeptPollingAttendees deptPollingAttendees : deptPollingAttendeesList)
        {
            List<PollingAttendee> pollingAttendees = deptPollingAttendees.getPollingAttendees();
            for (PollingAttendee pollingAttendee : pollingAttendees)
            {
                pollingAttendeeList.add(pollingAttendee);
            }
        }
        return pollingAttendeeList;
    }
    
    protected Map<String, PollingAttendee> toMap(List<PollingAttendee> pas)
    {
        Map<String, PollingAttendee> m = new HashMap<>();
        for (PollingAttendee pollingAttendee : pas)
        {
            m.put(pollingAttendee.getAttendee().getId(), pollingAttendee);
        }
        return m;
    }
    
    private void add(List<PollingAttendee> pollingAttendeeList, AttendeeForMcuZj a, Map<String, PollingAttendee> m, SplitScreen splitScreen)
    {
        if (m != null && m.containsKey(a.getId()))
        {
            return;
        }
        if (a.isMcuAttendee()) {
            if (splitScreen instanceof OneSplitScreen) {
                BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(a.getCascadeConferenceId()));
                if (downCascadeConferenceContext != null) {
                    List<BaseAttendee> downCascadeAttendeeTempList = new ArrayList<>();
                    downCascadeAttendeeTempList.addAll(downCascadeConferenceContext.getAttendees());
                    downCascadeAttendeeTempList.addAll(downCascadeConferenceContext.getMasterAttendees());
                    for (Object value : downCascadeConferenceContext.getCascadeAttendeesMap().values()) {
                        if (!ObjectUtils.isEmpty(value)) {
                            downCascadeAttendeeTempList.addAll((Collection) value);
                        }
                    }
                    if (downCascadeAttendeeTempList.size() > 0) {
                        for (BaseAttendee downCascadeAttendeeTemp : downCascadeAttendeeTempList) {
                            BaseAttendee downCascadeAttendee = new BaseAttendee();
                            BeanUtils.copyProperties(downCascadeAttendeeTemp, downCascadeAttendee);
                            downCascadeAttendee.setDeptId(-downCascadeConferenceContext.getUpCascadeIndex());
                            downCascadeAttendee.setDeptName(downCascadeConferenceContext.getName());
                            PollingAttendee pollingAttendee = new PollingAttendee();
                            pollingAttendee.setDownCascadeAttendee(downCascadeAttendee);
                            pollingAttendeeList.add(pollingAttendee);
                        }
                    } else {
                        pollingAttendeeList.add(new PollingAttendee(a));
                    }
                }
            } else {
                pollingAttendeeList.add(new PollingAttendee(a));
            }
        } else {
            pollingAttendeeList.add(new PollingAttendee(a));
        }
    }
}
