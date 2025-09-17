/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : McuDataCache.java
 * Package     : com.paradisecloud.sync.model.cache
 * @author lilinhai 
 * @since 2020-12-02 10:14
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.cache.model;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.fcm.common.enumer.ParticipantState;
import com.paradisecloud.fcm.fme.cache.exception.CallNotExistException;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.paradisecloud.fcm.fme.model.cms.CallBrandingProfile;
import com.paradisecloud.fcm.fme.model.cms.CallLegProfile;
import com.paradisecloud.fcm.fme.model.cms.CallProfile;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.fme.model.cms.CompatibilityProfile;
import com.paradisecloud.fcm.fme.model.cms.DialInSecurityProfile;
import com.paradisecloud.fcm.fme.model.cms.DtmfProfile;
import com.paradisecloud.fcm.fme.model.cms.InboundDialPlanRule;
import com.paradisecloud.fcm.fme.model.cms.IvrBrandingProfile;
import com.paradisecloud.fcm.fme.model.cms.OutboundDialPlanRule;
import com.paradisecloud.fcm.fme.model.cms.Tenant;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;
import com.paradisecloud.fcm.fme.model.cms.system.SystemStatus;
import com.sinhy.exception.SystemException;

/**  
 * <pre>Mcu数据缓存</pre>
 * @author lilinhai
 * @since 2020-12-02 10:14
 * @version V1.0  
 */
public class DataCache
{
    
    /**
     * 活跃会议室的与会者缓存，key为Participant的uuid
     */
    private volatile Map<String, Participant> participantMap;
    
    /**
     * key为callId，value为与会者集合(key=uri)
     */
    private volatile Map<String, UriParticipantsMap> callUriParticipantsMap;
    
    /**
     * 入会方案缓存
     */
    private volatile Map<String, CallLegProfile> callLegProfileMap;
    
    /**
     * 虚拟会议室参数
     */
    private volatile Map<String, CallProfile> callProfileMap;
    
    /**
     * DTMF参数
     */
    private volatile Map<String, DtmfProfile> dtmfProfileMap;
    
    /**
     * CompatibilityProfile参数
     */
    private volatile Map<String, CompatibilityProfile> compatibilityProfileMap;
    
    /**
     * DialInSecurityProfile参数
     */
    private volatile Map<String, DialInSecurityProfile> dialInSecurityProfileMap;
    
    /**
     * CallBrandingProfile参数
     */
    private volatile Map<String, CallBrandingProfile> callBrandingProfileMap;
    
    private volatile Map<String, OutboundDialPlanRule> outboundDialPlanRuleMap;
    private volatile Map<String, InboundDialPlanRule> inboundDialPlanRuleMap;
    
    /**
     * CallBrandingProfile参数
     */
    private volatile Map<String, IvrBrandingProfile> ivrBrandingProfileMap;
    
    /**
     * Tenant参数
     */
    private volatile Map<String, Tenant> tenantMap;
    
    /**
     * FME原始数据CoSpace映射，key为uuid
     */
    private volatile Map<String, CoSpace> coSpaceMap;
    
    /**
     * 会议号映射cospace实体
     */
    private volatile Map<String, CoSpace> conferenceNumberCoSpaceMap;
    
    /**
     * key:callId, value：call
     */
    private volatile Map<String, Call> callMap;
    
    /**
     * key:coSpaceId, value：call
     */
    private volatile Map<String, Call> coSpaceCallMap;
    private volatile SplitScreenCreaterMap splitScreenCreaterMap;
    
    /**
     * FME状态信息
     */
    private SystemStatus systemStatus;
    
    /**
     * <pre>根据缓存类型选择</pre>
     * @author lilinhai
     * @param fmeBridge 
     * @since 2020-12-02 13:41 
     * @param cacheType void
     */
    public DataCache(FmeBridge fmeBridge)
    {
        participantMap = new ConcurrentHashMap<>();
        callUriParticipantsMap = new ConcurrentHashMap<>();
        
        callLegProfileMap = new ConcurrentHashMap<>();
        callProfileMap = new ConcurrentHashMap<>();
        dtmfProfileMap = new ConcurrentHashMap<>();
        compatibilityProfileMap = new ConcurrentHashMap<>();
        dialInSecurityProfileMap = new ConcurrentHashMap<>();
        callBrandingProfileMap = new ConcurrentHashMap<>();
        ivrBrandingProfileMap = new ConcurrentHashMap<>();
        outboundDialPlanRuleMap = new ConcurrentHashMap<>();
        inboundDialPlanRuleMap = new ConcurrentHashMap<>();
        tenantMap = new ConcurrentHashMap<>();
        
        coSpaceMap = new ConcurrentHashMap<>();
        conferenceNumberCoSpaceMap = new ConcurrentHashMap<>();
        
        callMap = new ConcurrentHashMap<>();
        coSpaceCallMap = new ConcurrentHashMap<>();
        splitScreenCreaterMap = new SplitScreenCreaterMap();
        fmeBridge.getFmeLogger().logInfo("DATA-CACHE-----------缓存初始化成功", true, false);
    }
    
    /**
     * <pre>将IvrBrandingProfile实体更新到缓存中</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param ivrBrandingProfile
     * @return boolean
     */
    public boolean update(IvrBrandingProfile ivrBrandingProfile) 
    {
        if (!ObjectUtils.isEmpty(ivrBrandingProfile.getId()))
        {
            ivrBrandingProfileMap.put(ivrBrandingProfile.getId(), ivrBrandingProfile);
            return true;
        }
        return false;
    }
    
    /**
     * <pre>获取IvrBrandingProfile</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param uuid
     * @return CallLegProfile
     */
    public IvrBrandingProfile getIvrBrandingProfile(String uuid)
    {
        return getIvrBrandingProfile(uuid, false);
    }
    
    
    /**
     * <pre>获取IvrBrandingProfile</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param uuid
     * @return CallLegProfile
     */
    public IvrBrandingProfile getIvrBrandingProfile(String uuid, boolean isThrowException)
    {
        IvrBrandingProfile ivrBrandingProfile = ivrBrandingProfileMap.get(uuid);
        if (ivrBrandingProfile == null && isThrowException)
        {
            throw new SystemException(1002324, "找不到ivrBrandingProfile：" + uuid);
        }
        return ivrBrandingProfile;
    }
    
    /**
     * <pre>删除IvrBrandingProfile</pre>
     * @author lilinhai
     * @since 2021-02-01 16:36 
     * @param uuid
     * @return CallLegProfile
     */
    public IvrBrandingProfile deleteIvrBrandingProfile(String uuid)
    {
        return ivrBrandingProfileMap.remove(uuid);
    }
    
    /**********************************ivrBrandingProfile*****************************************************/
    
    /**
     * <pre>将callBrandingProfile实体更新到缓存中</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param callLegProfile
     * @return boolean
     */
    public boolean update(CallBrandingProfile callBrandingProfile) 
    {
        if (!ObjectUtils.isEmpty(callBrandingProfile.getId()))
        {
            callBrandingProfileMap.put(callBrandingProfile.getId(), callBrandingProfile);
            return true;
        }
        return false;
    }
    
    /**
     * <pre>获取CallBrandingProfile</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param uuid
     * @return CallLegProfile
     */
    public CallBrandingProfile getCallBrandingProfile(String uuid)
    {
        return getCallBrandingProfile(uuid, false);
    }
    
    
    /**
     * <pre>获取CallBrandingProfile</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param uuid
     * @return CallLegProfile
     */
    public CallBrandingProfile getCallBrandingProfile(String uuid, boolean isThrowException)
    {
        CallBrandingProfile callBrandingProfile = callBrandingProfileMap.get(uuid);
        if (callBrandingProfile == null && isThrowException)
        {
            throw new SystemException(1002324, "找不到CallBrandingProfile：" + uuid);
        }
        return callBrandingProfile;
    }
    
    /**
     * <pre>删除CallBrandingProfile</pre>
     * @author lilinhai
     * @since 2021-02-01 16:36 
     * @param uuid
     * @return CallLegProfile
     */
    public CallBrandingProfile deleteCallBrandingProfile(String uuid)
    {
        return callBrandingProfileMap.remove(uuid);
    }
    
    /**********************************CallBrandingProfile*****************************************************/
    
    /**
     * <pre>将outboundDialPlanRule实体更新到缓存中</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param outboundDialPlanRule
     * @return boolean
     */
    public boolean update(OutboundDialPlanRule outboundDialPlanRule) 
    {
        if (!ObjectUtils.isEmpty(outboundDialPlanRule.getId()))
        {
            outboundDialPlanRuleMap.put(outboundDialPlanRule.getId(), outboundDialPlanRule);
            return true;
        }
        return false;
    }
    
    /**
     * <pre>获取CallBrandingProfile</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param uuid
     * @return CallLegProfile
     */
    public OutboundDialPlanRule getOutboundDialPlanRule(String uuid)
    {
        return getOutboundDialPlanRule(uuid, false);
    }
    
    
    /**
     * <pre>获取outboundDialPlanRule</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param uuid
     * @return outboundDialPlanRule
     */
    public OutboundDialPlanRule getOutboundDialPlanRule(String uuid, boolean isThrowException)
    {
        OutboundDialPlanRule outboundDialPlanRule = outboundDialPlanRuleMap.get(uuid);
        if (outboundDialPlanRule == null && isThrowException)
        {
            throw new SystemException(1002324, "找不到outboundDialPlanRule：" + uuid);
        }
        return outboundDialPlanRule;
    }
    
    public List<OutboundDialPlanRule> getOutboundDialPlanRules()
    {
        return new ArrayList<>(outboundDialPlanRuleMap.values());
    }
    
    /**
     * <pre>删除OutboundDialPlanRule</pre>
     * @author lilinhai
     * @since 2021-02-01 16:36 
     * @param uuid
     * @return OutboundDialPlanRule
     */
    public OutboundDialPlanRule deleteOutboundDialPlanRule(String uuid)
    {
        return outboundDialPlanRuleMap.remove(uuid);
    }
    
    /**********************************OutboundDialPlanRule*****************************************************/
    
    /**
     * <pre>将inboundDialPlanRule实体更新到缓存中</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param inboundDialPlanRule
     * @return boolean
     */
    public boolean update(InboundDialPlanRule inboundDialPlanRule) 
    {
        if (!ObjectUtils.isEmpty(inboundDialPlanRule.getId()))
        {
            inboundDialPlanRuleMap.put(inboundDialPlanRule.getId(), inboundDialPlanRule);
            return true;
        }
        return false;
    }
    
    /**
     * <pre>获取CallBrandingProfile</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param uuid
     * @return CallLegProfile
     */
    public InboundDialPlanRule getInboundDialPlanRule(String uuid)
    {
        return getInboundDialPlanRule(uuid, false);
    }
    
    /**
     * <pre>获取CallBrandingProfile</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param uuid
     * @return CallLegProfile
     */
    public List<InboundDialPlanRule> getInboundDialPlanRules()
    {
        return new ArrayList<>(inboundDialPlanRuleMap.values());
    }
    
    /**
     * <pre>获取inboundDialPlanRule</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param uuid
     * @return inboundDialPlanRule
     */
    public InboundDialPlanRule getInboundDialPlanRule(String uuid, boolean isThrowException)
    {
        InboundDialPlanRule inboundDialPlanRule = inboundDialPlanRuleMap.get(uuid);
        if (inboundDialPlanRule == null && isThrowException)
        {
            throw new SystemException(1002324, "找不到inboundDialPlanRule：" + uuid);
        }
        return inboundDialPlanRule;
    }
    
    /**
     * <pre>删除InboundDialPlanRule</pre>
     * @author lilinhai
     * @since 2021-02-01 16:36 
     * @param uuid
     * @return InboundDialPlanRule
     */
    public InboundDialPlanRule deleteInboundDialPlanRule(String uuid)
    {
        return inboundDialPlanRuleMap.remove(uuid);
    }
    
    /**********************************InboundDialPlanRule*****************************************************/
    
    /**
     * <pre>将dialInSecurityProfile实体更新到缓存中</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param callLegProfile
     * @return boolean
     */
    public boolean update(DialInSecurityProfile dialInSecurityProfile) 
    {
        if (!ObjectUtils.isEmpty(dialInSecurityProfile.getId()))
        {
            dialInSecurityProfileMap.put(dialInSecurityProfile.getId(), dialInSecurityProfile);
            return true;
        }
        return false;
    }
    
    /**
     * <pre>获取DialInSecurityProfile</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param uuid
     * @return CallLegProfile
     */
    public DialInSecurityProfile getDialInSecurityProfile(String uuid)
    {
        return getDialInSecurityProfile(uuid, false);
    }
    
    
    /**
     * <pre>获取dialInSecurityProfile</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param uuid
     * @return CallLegProfile
     */
    public DialInSecurityProfile getDialInSecurityProfile(String uuid, boolean isThrowException)
    {
        DialInSecurityProfile dialInSecurityProfile = dialInSecurityProfileMap.get(uuid);
        if (dialInSecurityProfile == null && isThrowException)
        {
            throw new SystemException(1002324, "找不到dialInSecurityProfile：" + uuid);
        }
        return dialInSecurityProfile;
    }
    
    /**
     * <pre>删除DialInSecurityProfile</pre>
     * @author lilinhai
     * @since 2021-02-01 16:36 
     * @param uuid
     * @return CallLegProfile
     */
    public DialInSecurityProfile deleteDialInSecurityProfile(String uuid)
    {
        return dialInSecurityProfileMap.remove(uuid);
    }
    
    /**********************************dialInSecurityProfile*****************************************************/
    
    /**
     * <pre>将Compatibility实体更新到缓存中</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param callLegProfile
     * @return boolean
     */
    public boolean update(CompatibilityProfile compatibilityProfile) 
    {
        if (!ObjectUtils.isEmpty(compatibilityProfile.getId()))
        {
            compatibilityProfileMap.put(compatibilityProfile.getId(), compatibilityProfile);
            return true;
        }
        return false;
    }
    
    /**
     * <pre>获取CompatibilityProfile</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param uuid
     * @return CallLegProfile
     */
    public CompatibilityProfile getCompatibilityProfile(String uuid)
    {
        return getCompatibilityProfile(uuid, false);
    }
    
    
    /**
     * <pre>获取compatibilityProfile</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param uuid
     * @return CallLegProfile
     */
    public CompatibilityProfile getCompatibilityProfile(String uuid, boolean isThrowException)
    {
        CompatibilityProfile compatibilityProfile = compatibilityProfileMap.get(uuid);
        if (compatibilityProfile == null && isThrowException)
        {
            throw new SystemException(1002324, "找不到compatibilityProfile：" + uuid);
        }
        return compatibilityProfile;
    }
    
    /**
     * <pre>删除CompatibilityProfile</pre>
     * @author lilinhai
     * @since 2021-02-01 16:36 
     * @param uuid
     * @return CallLegProfile
     */
    public CompatibilityProfile deleteCompatibilityProfile(String uuid)
    {
        return compatibilityProfileMap.remove(uuid);
    }
    
    /**********************************compatibilityProfile*****************************************************/
    
    /**
     * <pre>将DTMF实体更新到缓存中</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param callLegProfile
     * @return boolean
     */
    public boolean update(DtmfProfile dtmfProfile) 
    {
        if (!ObjectUtils.isEmpty(dtmfProfile.getId()))
        {
            dtmfProfileMap.put(dtmfProfile.getId(), dtmfProfile);
            return true;
        }
        return false;
    }
    
    /**
     * <pre>获取DTMF profile</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param uuid
     * @return CallLegProfile
     */
    public DtmfProfile getDtmfProfile(String uuid)
    {
        return getDtmfProfile(uuid, false);
    }
    
    
    /**
     * <pre>获取DTMF profile</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param uuid
     * @return CallLegProfile
     */
    public DtmfProfile getDtmfProfile(String uuid, boolean isThrowException)
    {
        DtmfProfile dtmfProfile = dtmfProfileMap.get(uuid);
        if (dtmfProfile == null && isThrowException)
        {
            throw new SystemException(1002324, "找不到DTMF profile：" + uuid);
        }
        return dtmfProfile;
    }
    
    /**
     * <pre>删除DTMF profile</pre>
     * @author lilinhai
     * @since 2021-02-01 16:36 
     * @param uuid
     * @return CallLegProfile
     */
    public DtmfProfile deleteDtmfProfile(String uuid)
    {
        return dtmfProfileMap.remove(uuid);
    }
    
    /**********************************DtmfProfile*****************************************************/
    
    
    /**
     * <pre>将入会方案更新到缓存中</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param callLegProfile
     * @return boolean
     */
    public boolean update(CallLegProfile callLegProfile) 
    {
        if (!ObjectUtils.isEmpty(callLegProfile.getId()))
        {
            callLegProfileMap.put(callLegProfile.getId(), callLegProfile);
            return true;
        }
        return false;
    }
    
    /**
     * <pre>获取入会方案</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param uuid
     * @return CallLegProfile
     */
    public CallLegProfile getCallLegProfile(String uuid)
    {
        return getCallLegProfile(uuid, false);
    }
    
    
    /**
     * <pre>获取入会方案</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param uuid
     * @return CallLegProfile
     */
    public CallLegProfile getCallLegProfile(String uuid, boolean isThrowException)
    {
        CallLegProfile callLegProfile = callLegProfileMap.get(uuid);
        if (callLegProfile == null && isThrowException)
        {
            throw new SystemException(1002324, "找不到CallLegProfile：" + uuid);
        }
        return callLegProfile;
    }
    
    /**
     * <pre>删除入会方案</pre>
     * @author lilinhai
     * @since 2021-02-01 16:36 
     * @param uuid
     * @return CallLegProfile
     */
    public CallLegProfile deleteCallLegProfile(String uuid)
    {
        return callLegProfileMap.remove(uuid);
    }
    
    /**********************************CallLegProfile*********************************************************/
    
    /**
     * <pre>将Call实体更新到缓存中</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param callLegProfile
     * @return boolean
     */
    public boolean update(CallProfile callProfile) 
    {
        if (!ObjectUtils.isEmpty(callProfile.getId()))
        {
            callProfileMap.put(callProfile.getId(), callProfile);
            return true;
        }
        return false;
    }
    
    /**
     * <pre>获取Call profile</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param uuid
     * @return CallLegProfile
     */
    public CallProfile getCallProfile(String uuid)
    {
        return getCallProfile(uuid, false);
    }
    
    
    /**
     * <pre>获取Call profile</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param uuid
     * @return CallLegProfile
     */
    public CallProfile getCallProfile(String uuid, boolean isThrowException)
    {
        CallProfile callProfile = callProfileMap.get(uuid);
        if (callProfile == null && isThrowException)
        {
            throw new SystemException(1002324, "找不到Call profile：" + uuid);
        }
        return callProfile;
    }
    
    /**
     * <pre>删除Call profile</pre>
     * @author lilinhai
     * @since 2021-02-01 16:36 
     * @param uuid
     * @return CallLegProfile
     */
    public CallProfile deleteCallProfile(String uuid)
    {
        return callProfileMap.remove(uuid);
    }
    
    /**********************************CallProfile*****************************************************/
    
    /**
     * <pre>将Tenant实体更新到缓存中</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param callLegProfile
     * @return boolean
     */
    public boolean update(Tenant tenant) 
    {
        if (!ObjectUtils.isEmpty(tenant.getId()))
        {
            tenantMap.put(tenant.getId(), tenant);
            return true;
        }
        return false;
    }
    
    /**
     * <pre>获取Tenant</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param uuid
     * @return CallLegProfile
     */
    public Tenant getTenant(String uuid)
    {
        return getTenant(uuid, false);
    }
    
    
    /**
     * <pre>获取Tenant</pre>
     * @author lilinhai
     * @since 2021-02-01 16:12 
     * @param uuid
     * @return CallLegProfile
     */
    public Tenant getTenant(String uuid, boolean isThrowException)
    {
        Tenant tenant = tenantMap.get(uuid);
        if (tenant == null && isThrowException)
        {
            throw new SystemException(1002324, "找不到Tenant：" + uuid);
        }
        return tenant;
    }
    
    /**
     * <pre>删除Tenant</pre>
     * @author lilinhai
     * @since 2021-02-01 16:36 
     * @param uuid
     * @return CallLegProfile
     */
    public Tenant deleteTenant(String uuid)
    {
        return tenantMap.remove(uuid);
    }
    
    /**********************************compatibilityProfile*****************************************************/
    
    /**
     * <pre>将CoSpace对象更新到缓存</pre>
     * @author lilinhai
     * @since 2021-02-02 10:24 
     * @param coSpace void
     */
    public void update(CoSpace coSpace)
    {
        if (!Objects.isNull(coSpace)&&!ObjectUtils.isEmpty(coSpace.getUri()))
        {
            coSpaceMap.put(coSpace.getId(), coSpace);
            conferenceNumberCoSpaceMap.put(coSpace.getUri(), coSpace);
        }
    }
    
    /**
     * 返回coSpace数量
     * @author Administrator
     * @since 2021-03-13 11:01 
     * @return int
     */
    public int getCoSpaceCount()
    {
        return coSpaceMap.size();
    }
    
    /**
     * 获取当前fme桥上的参会者数量
     * @author Administrator
     * @since 2021-03-13 11:05 
     * @return int
     */
    public int getParticipantCount()
    {
        int participantCount = 0;
        for (Entry<String, Participant> e : participantMap.entrySet())
        {
            ParticipantState participantStatus = ParticipantState.convert(e.getValue().getStatus().getState());
            if (participantStatus == ParticipantState.CONNECTED)
            {
                participantCount++;
            }
        }
        
        return participantCount;
    }
    
    public int getCallCount()
    {
        return callMap.size();
    }
    
    public List<Call> getCalls()
    {
        return new ArrayList<>(callMap.values());
    }
    
    /**
     * <pre>从内存获取CoSpace对象</pre>
     * @author lilinhai
     * @since 2021-02-02 10:25 
     * @param uuid
     * @return CoSpace
     */
    public CoSpace getCoSpaceByUuid(String uuid)
    {
        return coSpaceMap.get(uuid);
    }
    
    /**
     * 根据coSpaceId获取会议号
     * @author Administrator
     * @since 2021-03-03 23:39 
     * @param callId
     * @return String
     */
    public String getConferenceNumberByCoSpaceId(String coSpaceId)
    {
        CoSpace coSpace = getCoSpaceByUuid(coSpaceId);
        if (coSpace != null)
        {
            return coSpace.getUri();
        }
        return null;
    }
    
    /**
     * 根据callId获取会议号
     * @author Administrator
     * @since 2021-03-03 23:39 
     * @param callId
     * @return String
     */
    public String getConferenceNumberByCallId(String callId)
    {
        Call call = getCallByUuid(callId);
        if (call != null)
        {
            return getConferenceNumberByCoSpaceId(call.getCoSpace());
        }
        return null;
    }
    
    /**
     * <pre>从内存获取CoSpace对象</pre>
     * @author lilinhai
     * @since 2021-02-02 10:25 
     * @param conferenceNumvber
     * @return CoSpace
     */
    public CoSpace getCoSpaceByConferenceNumber(String conferenceNumvber)
    {
        return conferenceNumberCoSpaceMap.get(conferenceNumvber);
    }
    
    /**
     * <pre>从内存获取CoSpace对象</pre>
     * @author lilinhai
     * @since 2021-02-02 10:25 
     * @param conferenceNumvber
     * @return CoSpace
     */
    public CoSpace getCoSpaceByConferenceNumber(long conferenceNumber)
    {
        return getCoSpaceByConferenceNumber(String.valueOf(conferenceNumber));
    }
    
    /**
     * <pre>将CoSpace对象从内存删除</pre>
     * @author lilinhai
     * @since 2021-02-02 10:25 
     * @param uuid
     * @return CoSpace
     */
    public CoSpace deleteCoSpace(String uuid)
    {
        CoSpace cs = coSpaceMap.remove(uuid);
        if (cs != null)
        {
            conferenceNumberCoSpaceMap.remove(cs.getUri());
        }
        return cs;
    }
    
    /**
     * <pre>将CoSpace对象从内存删除</pre>
     * @author lilinhai
     * @since 2021-02-02 10:25 
     * @param uuid
     * @return CoSpace
     */
    public CoSpace deleteCoSpaceByUri(String uri)
    {
        CoSpace cs = conferenceNumberCoSpaceMap.remove(uri);
        if (cs != null)
        {
            coSpaceMap.remove(cs.getId());
        }
        return cs;
    }
    
    /*******************************CoSpace********************************************************/
    
    /**
     * <pre>将Call对象更新到缓存</pre>
     * @author lilinhai
     * @since 2021-02-02 10:24 
     * @param coSpace void
     */
    public void update(Call call)
    {
        // 如果存在Bridge，则表明该call不属于本节点，属于集群中其它节点
        if (ObjectUtils.isEmpty(call.getBridge()))
        {
            initCallLock(call);
            callMap.put(call.getId(), call);
            coSpaceCallMap.put(call.getCoSpace(), call);
        }
    }
    
    /**
     * <pre>根据call的uuid获取call对象</pre>
     * @author lilinhai
     * @since 2021-02-02 11:33 
     * @param uuid
     * @return Call
     */
    public Call getCallByUuid(String uuid)
    {
        return callMap.get(uuid);
    }
    
    /**
     * <pre>根据call的uuid获取call对象</pre>
     * @author lilinhai
     * @since 2021-02-02 11:33 
     * @param uuid
     * @return Call
     */
    public Call getCallByCoSpaceUuid(String uuid)
    {
        return coSpaceCallMap.get(uuid);
    }
    
    /**
     * <pre>根据call的uuid获取call对象</pre>
     * @author lilinhai
     * @since 2021-02-02 11:33 
     * @param uuid
     * @return Call
     */
    public Call getCallByConferenceNumber(String conferenceNumber)
    {
        CoSpace coSpace = conferenceNumberCoSpaceMap.get(conferenceNumber);
        if (coSpace == null)
        {
            return null;
        }
        return getCallByCoSpaceUuid(coSpace.getId());
    }
    
    /**
     * <pre>从内存中删除单个call</pre>
     * @author lilinhai
     * @since 2021-02-02 11:36 
     * @param uuid
     * @return Call
     */
    public Call deleteCallByUuid(String uuid)
    {
        Call c = callMap.remove(uuid);
        if (c != null)
        {
            coSpaceCallMap.remove(c.getCoSpace());
            UriParticipantsMap pm = callUriParticipantsMap.remove(c.getId());
            if (pm != null)
            {
                pm.forEach((k, v) -> {
                    for (Iterator<Participant> it = v.values().iterator(); it.hasNext();)
                    {
                        Participant p = it.next();
                        participantMap.remove(p.getId());
                    }
                });
            }
        }
        return c;
    }
    
    /**
     * <pre>从内存中删除单个call</pre>
     * @author lilinhai
     * @since 2021-02-02 11:36 
     * @param uuid
     * @return Call
     */
    public Call deleteCallByCoSpaceUuid(String coSpace)
    {
        Call c = coSpaceCallMap.remove(coSpace);
        if (c != null)
        {
            callMap.remove(c.getId());
            UriParticipantsMap pm = callUriParticipantsMap.remove(c.getId());
            if (pm != null)
            {
                pm.forEach((k, v) -> {
                    for (Iterator<Participant> it = v.values().iterator(); it.hasNext();)
                    {
                        Participant p = it.next();
                        participantMap.remove(p.getId());
                    }
                });
            }
        }
        return c;
    }
    
    /*******************************Call********************************************************/
    
    /**
     * <pre>更新与会者缓存</pre>
     * @author lilinhai
     * @since 2021-02-02 13:31 
     * @param participant void
     */
    public Participant update(Participant participant)
    {
        Call call = getCallByUuid(participant.getCall());
        if (call != null)
        {
            // 若与会者包含会议桥信息，则表示该与会者不属于本FME节点，无需保存在本节点上
            if (ObjectUtils.isEmpty(participant.getCallBridge()))
            {
                participant.setCreateTime(new Date());
                Participant old = participantMap.get(participant.getId());
                if (old != null)
                {
                    old.sync(participant);
                    return old;
                }
                else
                {
                    synchronized (call.getLock())
                    {
                        old = participantMap.get(participant.getId());
                        if (old != null)
                        {
                            old.sync(participant);
                            LoggerFactory.getLogger(getClass()).info("Concurrency causes the program to execute the code branch: " + old);
                            return old;
                        }
                        else
                        {
                            UriParticipantsMap pm = callUriParticipantsMap.get(participant.getCall());
                            if (pm == null)
                            {
                                pm = new UriParticipantsMap();
                                callUriParticipantsMap.put(participant.getCall(), pm);
                            }
                            pm.addParticipant(participant);
                            participantMap.put(participant.getId(), participant);
                            return participant;
                        }
                    }
                }
            }
            return null;
        }
        else
        {
            throw new CallNotExistException("call已不存在，无法更新与会者到缓存", participant);
        }
    }
    
    /**
     * <pre>根据与会者的uuid获取与会者对象，包含该与会者的开关麦信息</pre>
     * @author lilinhai
     * @since 2021-02-02 13:32 
     * @param uuid
     * @return Participant
     */
    public Participant getParticipantByUuid(String uuid)
    {
        if (uuid == null)
        {
            return null;
        }
        return participantMap.get(uuid);
    }
    
    /**
     * <pre>根据与会者的uuid获取与会者对象，包含该与会者的开关麦信息</pre>
     * @author lilinhai
     * @since 2021-02-02 13:32 
     * @param uuid
     * @return Participant
     */
    public Participant getParticipantByCallAndUri(String callId, String uri)
    {
        UriParticipantsMap uriParticipantMap = callUriParticipantsMap.get(callId);
        return uriParticipantMap == null ? null : uriParticipantMap.getByUri(uri);
    }
    
    /**
     * <pre>根据与会者的uuid获取与会者对象，包含该与会者的开关麦信息</pre>
     * @author lilinhai
     * @since 2021-02-02 13:32 
     * @param uuid
     * @return Participant
     */
    public Participant getParticipantByConferenceNumberAndUri(String conferenceNumber, String uri)
    {
        Call call = getCallByConferenceNumber(conferenceNumber);
        if (call != null)
        {
            return getParticipantByCallAndUri(call.getId(), uri);
        }
        return null;
    }
    
    public UriParticipantsMap getUriParticipantMapByConferenceNumber(String conferenceNumber)
    {
        Call call = getCallByConferenceNumber(conferenceNumber);
        if (call != null)
        {
            return callUriParticipantsMap.get(call.getId());
        }
        return null;
    }
    
    public UriParticipantsMap getUriParticipantMapByCallId(String callId)
    {
        if (ObjectUtils.isEmpty(callId))
        {
            return null;
        }
        return callUriParticipantsMap.get(callId);
    }
    
    /**
     * <pre>从内存中根据uuid移除与会者信息</pre>
     * @author lilinhai
     * @since 2021-02-02 13:33 
     * @param uuid
     * @return Participant
     */
    public Participant deleteParticipantByUuid(String uuid)
    {
        Participant p = participantMap.remove(uuid);
        if (p != null)
        {
            UriParticipantsMap uriParticipantMap = callUriParticipantsMap.get(p.getCall());
            if (uriParticipantMap != null)
            {
                uriParticipantMap.removeParticipantByUriAndUuid(p.getUri(), uuid);
            }
            return p;
        }
        return null;
    }
    
    /**
     * <pre>从内存中根据uri移除与会者信息</pre>
     * @author lilinhai
     * @since 2021-02-02 13:33 
     * @param uuid
     * @return Participant
     */
    public Map<String, Participant> deleteParticipantByUri(String callId, String uri)
    {
        UriParticipantsMap uriParticipantMap = callUriParticipantsMap.get(callId);
        if (uriParticipantMap != null)
        {
            Map<String, Participant> m = uriParticipantMap.remove(uri);
            if (m != null)
            {
                m.values();
                for (Participant p : m.values())
                {
                    participantMap.remove(p.getId());
                }
                return m;
            }
        }
        return null;
    }
    
    /*****************************************System*********************************************************************/
    
    /**
     * <p>Get Method   :   participantMap Map<String,Participant></p>
     * @return participantMap
     */
    public List<Participant> getParticipants()
    {
        return new ArrayList<>(participantMap.values());
    }

    /**
     * <p>Get Method   :   systemStatus SystemStatus</p>
     * @return systemStatus
     */
    public SystemStatus getSystemStatus()
    {
        return systemStatus;
    }

    /**
     * <p>Set Method   :   systemStatus SystemStatus</p>
     * @param systemStatus
     */
    public void setSystemStatus(SystemStatus systemStatus)
    {
        this.systemStatus = systemStatus;
    }
    
    /**
     * <p>Get Method   :   callMap Map<String,Call></p>
     * @return callMap
     */
    public void eachCall(CallProcessor callProcessor)
    {
        new ArrayList<>(callMap.values()).forEach((call) -> {
            callProcessor.process(call);
        });
    }
    
    /**
     * <p>Get Method   :   callMap Map<String,Call></p>
     * @return callMap
     */
    public void eachCoSpace(CoSpaceProcessor coSpaceProcessor)
    {
        new ArrayList<>(coSpaceMap.values()).forEach((coSpace) -> {
            coSpaceProcessor.process(coSpace);
        });
    }
    
    /**
     * <p>Get Method   :   splitScreenCreaterMap SplitScreenCreaterMap</p>
     * @return splitScreenCreaterMap
     */
    public SplitScreenCreaterMap getSplitScreenCreaterMap()
    {
        return splitScreenCreaterMap;
    }
    
    /**
     * call回调处理器
     * @author lilinhai
     * @since 2021-03-02 17:10
     * @version V1.0
     */
    public static interface CallProcessor
    {
        void process(Call call);
    }
    
    /**
     * call回调处理器
     * @author lilinhai
     * @since 2021-03-02 17:10
     * @version V1.0
     */
    public static interface CoSpaceProcessor
    {
        void process(CoSpace coSpace);
    }

    /**
     * <pre>清除所有缓存，websocket每次重连会执行该方法</pre>
     * @author lilinhai
     * @since 2021-02-02 10:54  void
     */
    public void clear()
    {
        if (available())
        {
            participantMap.clear();
            callUriParticipantsMap.clear();
            
            callLegProfileMap.clear();
            callProfileMap.clear();
            dtmfProfileMap.clear();
            compatibilityProfileMap.clear();
            dialInSecurityProfileMap.clear();
            callBrandingProfileMap.clear();
            ivrBrandingProfileMap.clear();
            tenantMap.clear();
            
            coSpaceMap.clear();
            conferenceNumberCoSpaceMap.clear();
            
            callMap.clear();
            coSpaceCallMap.clear();
            splitScreenCreaterMap.clear();
        }
    }
    
    /**
     * <pre>清除所有缓存，删除FME的时候会执行该方法</pre>
     * @author lilinhai
     * @since 2021-02-02 10:54  void
     */
    public void clearAndDestroy()
    {
        clear();
        
        // 销毁对象
        participantMap = null;
        callUriParticipantsMap = null;
        
        callLegProfileMap = null;
        callProfileMap = null;
        dtmfProfileMap = null;
        compatibilityProfileMap = null;
        dialInSecurityProfileMap = null;
        callBrandingProfileMap = null;
        ivrBrandingProfileMap = null;
        outboundDialPlanRuleMap = null;
        inboundDialPlanRuleMap = null;
        tenantMap = null;
        
        coSpaceMap = null;
        conferenceNumberCoSpaceMap = null;
        
        callMap = null;
        coSpaceCallMap = null;
        splitScreenCreaterMap = null;
    }
    
    public boolean available()
    {
        return participantMap != null 
                && callUriParticipantsMap != null 
                && callLegProfileMap != null 
                && callProfileMap != null
                && dtmfProfileMap != null
                && compatibilityProfileMap != null
                && dialInSecurityProfileMap != null
                && callBrandingProfileMap != null
                && ivrBrandingProfileMap != null
                && outboundDialPlanRuleMap != null
                && inboundDialPlanRuleMap != null
                && tenantMap != null
                && coSpaceMap != null 
                && conferenceNumberCoSpaceMap != null 
                && callMap != null 
                && coSpaceCallMap != null;
    }
    
    private void initCallLock(Call call)
    {
        if (call.getLock() == null)
        {
            synchronized (callMap)
            {
                if (call.getLock() == null)
                {
                    Call old = callMap.get(call.getId());
                    if (old != null && old.getLock() != null)
                    {
                        call.setLock(old.getLock());                
                    }
                    else
                    {
                        call.setLock(new Object());
                    }
                }
            }
        }
    }
}
