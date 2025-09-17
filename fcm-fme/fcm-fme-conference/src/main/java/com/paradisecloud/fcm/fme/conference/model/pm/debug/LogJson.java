/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : LogJson.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.debug
 * @author sinhy 
 * @since 2021-09-18 08:29
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.model.pm.debug;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.ObjectUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.ParticipantState;
import com.paradisecloud.fcm.fme.attendee.interfaces.ICallService;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.UriParticipantsMap;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.FmeAttendee;
import com.paradisecloud.fcm.fme.model.busi.cascade.Cascade;
import com.paradisecloud.fcm.fme.model.busi.cascade.UpCascade;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.exception.SystemException;
import com.sinhy.proxy.ProxyMethod;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;

public class LogJson extends ProxyMethod
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-18 08:29 
     * @param method 
     */
    protected LogJson(Method method)
    {
        super(method);
    }
    
    public JSONObject logJson(String conferenceNumber)
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
                throw new SystemException(1003344, "会议室不存在：" + conferenceNumber);
            }
        }
        catch (SystemException e) 
        {
            throw e;
        }
        catch (Throwable e)
        {
            logger.error("AttendeeImportanceDebugger error", e);
            throw new SystemException(1003344, e.getMessage());
        }
        return logJson;
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
        
        CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(cc.getConferenceNumber());
        conferenceJson.put("panePlacementHighestImportance", coSpace.getPanePlacementHighestImportance());
        conferenceJson.put("panePlacementSelfPaneMode", coSpace.getPanePlacementSelfPaneMode());
        conferenceJson.put("coSpaceLayout", coSpace.getDefaultLayout());
        conferenceJson.put("callLegId", coSpace.getId());
        conferenceJson.put("callLegProfileId", coSpace.getCallLegProfile());
        conferenceJson.put("callProfileId", coSpace.getCallProfile());
        conferenceJson.put("dialInSecurityProfileId", coSpace.getDialInSecurityProfile());
        conferenceJson.put("callBrandingProfileId", coSpace.getCallBrandingProfile());
        conferenceJson.put("coSpaceId", coSpace.getId());
        conferenceJson.put("passcode", coSpace.getPasscode());
        conferenceJson.put("fmeIp", fmeBridge.getBridgeAddress());
        
        List<JSONObject> calls = new ArrayList<JSONObject>();
        FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(cc.getDeptId(), new FmeBridgeAddpterProcessor()
        {
            public void process(FmeBridge fmeBridge)
            {
                Call call = fmeBridge.getDataCache().getCallByCoSpaceUuid(coSpace.getId());
                if (call != null)
                {
                    BeanFactory.getBean(ICallService.class).syncCall(fmeBridge, call.getId());
                    JSONObject callObj = new JSONObject();
                    callObj.put("callId", call.getId());
                    
                    UriParticipantsMap uriParticipantMap = fmeBridge.getDataCache().getUriParticipantMapByCallId(call.getId());
                    int fcmCount = 0;
                    
                    List<JSONObject> fcmParticipants = new ArrayList<>();
                    if (uriParticipantMap != null)
                    {
                        for (Entry<String, Map<String, Participant>> e : uriParticipantMap.entrySet())
                        {
                            Map<String, Participant> m = e.getValue();
                            for (Participant p : new ArrayList<>(m.values()))
                            {
                                if (p.is(ParticipantState.CONNECTED))
                                {
                                    fcmCount++;
                                }
                                
                                JSONObject fcmParticipant = new JSONObject();
                                fcmParticipant.put("state", p.getStatus().getState());
                                fcmParticipant.put("id", p.getId());
                                fcmParticipant.put("attendeeId", p.getAttendeeId());
                                fcmParticipant.put("name", p.getName());
                                if (p.getCallLeg() != null)
                                {
                                    fcmParticipant.put("layout", p.getCallLeg().getStatus().getLayout());
                                }
                                else
                                {
                                    fcmParticipant.put("layout", "");
                                }
                                fcmParticipant.put("importance", p.getConfiguration() == null ? "异常" : p.getConfiguration().getImportance());
                                fcmParticipant.put("uri", p.getUri());
                                fcmParticipants.add(fcmParticipant);
                            }
                        }
                    }
                    
                    callObj.put("fcmParticipants", fcmParticipants);
                    callObj.put("fcmCount", fcmCount);
                    callObj.put("fmeCount", call.getNumParticipantsLocal());
                    callObj.put("locked", call.getLocked());
                    callObj.put("recording", call.getRecording());
                    callObj.put("streaming", call.getStreaming());
                    callObj.put("fmeIp", fmeBridge.getBridgeAddress());
                    calls.add(callObj);
                }
            }
        });
        
        conferenceJson.put("calls", calls);
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
        m.put("type", attendee.getAttendeeType());
        m.put("remoteParty", attendee.getRemoteParty());
        m.put("participantUuid", attendee.getParticipantUuid());
        m.put("importance", attendee.getImportance());
        m.put("callId", attendee.getCallId());
        m.put("fcmLayout", attendee.getLayout());
        m.put("mixingStatus", attendee.getMixingStatus());
        m.put("videoStatus", attendee.getVideoStatus());
        m.put("fmeIp", fmeBridge.getBridgeAddress());
        
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
                
                if (participant.getCallLeg() != null && participant.getCallLeg().getConfiguration() != null)
                {
                    m.put("defaultLayout", participant.getCallLeg().getConfiguration().getDefaultLayout());
                    m.put("chosenLayout", participant.getCallLeg().getConfiguration().getChosenLayout());
                    m.put("activeLayout", participant.getCallLeg().getStatus().getActiveLayout());
                    m.put("layout", participant.getCallLeg().getStatus().getLayout());
                    m.put("callLegId", participant.getCallLeg().getId());
                }
                else
                {
                    m.put("defaultLayout", "");
                    m.put("chosenLayout", "");
                    m.put("activeLayout", "");
                    m.put("layout", "");
                    m.put("callLegId", "");
                }
            }
            
            Call call = fmeBridge.getDataCache().getCallByUuid(attendee.getCallId());
            if (call != null)
            {
                m.put("coSpaceId", call.getCoSpace());
            }
            else
            {
                m.put("coSpaceId", null);
            }
        }
        return m;
    }
}
