/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ConferenceDebuggerController.java
 * Package     : com.paradisecloud.fcm.web.controller.business
 * @author paradise 
 * @since 2021-03-05 10:37
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.web.controller.business;

import java.util.ArrayList;
import java.util.List;

import com.sinhy.utils.DateUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.FmeAttendee;
import com.paradisecloud.fcm.fme.model.busi.cascade.Cascade;
import com.paradisecloud.fcm.fme.model.busi.cascade.UpCascade;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;
import com.paradisecloud.system.model.SysDeptCache;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**  
 * <pre>会议室调试器控制层</pre>
 * @author paradise
 * @since 2021-03-05 10:37
 * @version V1.0  
 */
@RestController
@RequestMapping("/conference-debugger")
@Tag(name = "会议室调试器控制层")
public class ConferenceDebuggerController extends BaseController
{
    
    
    /**
     * 查询所有会议
     */
    @GetMapping("/allConference")
    @Operation(summary = "查询所有会议")
    public RestResponse viewAllConferenceInMemery()
    {
        JSONObject topJsonObj = new JSONObject();
        topJsonObj.put("totalConference", ConferenceContextCache.getInstance().size());
        JSONArray ja = new JSONArray();
        ConferenceContextCache.getInstance().values().forEach((cc) -> {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("conferenceNumber", cc.getConferenceNumber());
            jsonObj.put("conferenceName", cc.getName());
            jsonObj.put("startTime", cc.getStartTime());
            jsonObj.put("duration", DateUtils.toTimeDuration(System.currentTimeMillis() - cc.getStartTime().getTime()));
            jsonObj.put("deptId", cc.getDeptId());
            jsonObj.put("deptName", SysDeptCache.getInstance().get(cc.getDeptId()).getDeptName());
            ja.add(jsonObj);
        });
        topJsonObj.put("conferences", ja);
        return RestResponse.success(topJsonObj);
    }
    
    /**
     * 查询轮询方案列表
     */
    @GetMapping("/attendeeImportance/{conferenceNumber}")
    @Operation(summary = "根据会议室号码获取所有参会权重信息，JSON返回")
    public RestResponse attendeeImportance(@PathVariable("conferenceNumber") String conferenceNumber)
    {
        JSONObject logJson = null;
        try
        {
            ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().get(conferenceNumber);
            if (mainConferenceContext != null)
            {
                logJson = buildConferenceJson(mainConferenceContext);
                List<JSONObject> subsConferences = new ArrayList<>();
                ConferenceContextCache.getInstance().downwardProcessingCascadeConferenceContext(mainConferenceContext, (cc) -> {
                    if (cc != mainConferenceContext)
                    {
                        JSONObject subsConference = buildConferenceJson(cc);
                        subsConferences.add(subsConference);
                    }
                });
                if (!ObjectUtils.isEmpty(subsConferences))
                {
                    logJson.put("subsConferences", subsConferences);
                }
            }
            else
            {
                return fail(1003453, "会议室不存在：" + conferenceNumber);
            }
        }
        catch (Throwable e)
        {
            logger.error("AttendeeImportanceDebugger error", e);
        }
        return success(logJson);
    }

    /**
     * <pre>构建会议json</pre>
     * @author Administrator
     * @since 2021-03-06 21:49 
     * @param cc
     * @return JSONObject
     */
    private JSONObject buildConferenceJson(ConferenceContext cc)
    {
        JSONObject conferenceJson = new JSONObject();
        conferenceJson.put("conferenceName", cc.getName());
        conferenceJson.put("conferenceNumber", cc.getConferenceNumber());
        conferenceJson.put("startTime", cc.getStartTime());
        conferenceJson.put("duration", DateUtils.toTimeDuration(System.currentTimeMillis() - cc.getStartTime().getTime()));
        conferenceJson.put("deptName", SysDeptCache.getInstance().get(cc.getDeptId()).getDeptName());
        
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceContext(cc);
        
        conferenceJson.put("panePlacementHighestImportance", fmeBridge.getDataCache().getCoSpaceByConferenceNumber(cc.getConferenceNumber()).getPanePlacementHighestImportance());
        
        if (cc.getMasterAttendee() != null && cc.getMasterAttendee().isMeetingJoined())
        {
            conferenceJson.put("master", attendeeInfo(cc.getMasterAttendee()));
        }
        
        List<JSONObject> attendeeJsons = new ArrayList<>();
        for (Attendee attendee : cc.getAttendees())
        {
            if (attendee.isMeetingJoined())
            {
                attendeeJsons.add(attendeeInfo(attendee));
            }
        }
        
        if (!ObjectUtils.isEmpty(attendeeJsons))
        {
            conferenceJson.put("attendees", attendeeJsons);
        }
        
        // 本会议中其它部门的参会者
        JSONArray otherDeptAttendees = new JSONArray();
        
        // 跨部门单体会议（非级联）的参会者
        cc.getCascadeAttendeesMap().forEach((deptId, as) -> {
            
            // 判断该部门是否是级联关系
            boolean isExistFmeAttendee = false;
            for (FmeAttendee fmeAttendee : cc.getFmeAttendees())
            {
                if (fmeAttendee.getCascadeDeptId() == deptId.longValue())
                {
                    isExistFmeAttendee = true;
                    break;
                }
            }
            
            // 不存在级联关系，则直接呼到上级部门会议中
            if (!isExistFmeAttendee)
            {
                JSONObject deptAttendeeInfo = new JSONObject();
                deptAttendeeInfo.put("deptName", SysDeptCache.getInstance().get(deptId).getDeptName());
                deptAttendeeInfo.put("deptId", deptId);
                for (Attendee masterAttendee0 : cc.getMasterAttendees())
                {
                    if (masterAttendee0.getDeptId() == deptId.longValue())
                    {
                        if (masterAttendee0.isMeetingJoined())
                        {
                            deptAttendeeInfo.put("master", attendeeInfo(masterAttendee0));
                        }
                        break;
                    }
                }
                
                List<JSONObject> otherDeptAttendeeJsons = new ArrayList<>();
                
                // 参会者
                for (Attendee attendee : as)
                {
                    if (attendee.isMeetingJoined())
                    {
                        otherDeptAttendeeJsons.add(attendeeInfo(attendee));
                    }
                }
                
                if (!otherDeptAttendeeJsons.isEmpty() || deptAttendeeInfo.containsKey("master"))
                {
                    deptAttendeeInfo.put("attendees", otherDeptAttendeeJsons);
                    otherDeptAttendees.add(deptAttendeeInfo);
                }
            }
        });
        
        if (!otherDeptAttendees.isEmpty())
        {
            conferenceJson.put("otherDeptAttendees", otherDeptAttendees);
        }
        
        List<JSONObject> cascadeFmeAttendees = new ArrayList<>();
        Cascade cascade = cc.getCascade();
        if (cascade != null)
        {
            cascade.eachFmeAttendee((fa)->{
                cascadeFmeAttendees.add(attendeeInfo(fa));
            });
            
            conferenceJson.put("cascade", cascadeFmeAttendees);
        }
        
        List<JSONObject> upCascadeFmeAttendees = new ArrayList<>();
        UpCascade upCascade = cc.getUpCascade();
        if (upCascade != null)
        {
            upCascade.eachUpFmeAttendee((fa)->{
                upCascadeFmeAttendees.add(attendeeInfo(fa));
            });
            
            conferenceJson.put("upCascade", upCascadeFmeAttendees);
        }
        return conferenceJson;
    }
    
    private JSONObject attendeeInfo(Attendee attendee)
    {
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(attendee);
        JSONObject m = new JSONObject();
        
        if (attendee instanceof FmeAttendee)
        {
            FmeAttendee fmeAttendee = (FmeAttendee) attendee;
            m.put("cascadeDeptName", SysDeptCache.getInstance().get(fmeAttendee.getCascadeDeptId()).getDeptName());
            m.put("cascadeConferenceNumber", fmeAttendee.getCascadeConferenceNumber());
        }
        m.put("name", attendee.getName());
        m.put("type", attendee.getClass().getSimpleName());
        m.put("remoteParty", attendee.getRemoteParty());
        m.put("participantUuid", attendee.getParticipantUuid());
        m.put("importance", attendee.getImportance());
        m.put("callId", attendee.getCallId());
        m.put("fmeIp", fmeBridge.getBusiFme().getIp());
        
        // 真实权重，实时查询FME侧数据返回
        Participant participant = null;
        if (!ObjectUtils.isEmpty(attendee.getParticipantUuid()))
        {
            participant = fmeBridge.getDataCache().getParticipantByUuid(attendee.getParticipantUuid());
            if (participant != null)
            {
                if (participant.getConfiguration() != null && participant.getConfiguration().getImportance() != null)
                {
                    m.put("realImportance", participant.getConfiguration().getImportance());
                }
                else
                {
                    m.put("realImportance", "");
                }
            }
        }
        return m;
    }
}
