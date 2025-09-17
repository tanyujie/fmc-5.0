/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeDataCache.java
 * Package     : com.paradisecloud.fcm.fme.service.model
 * @author lilinhai 
 * @since 2021-02-05 14:55
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.ObjectUtils;

import com.paradisecloud.fcm.common.enumer.ParticipantState;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.ParticipantInfo;
import com.paradisecloud.fcm.fme.cache.model.enumer.FmeBridgeProcessingStrategy;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;
import com.sinhy.model.GenericValue;

/**  
 * <pre>FME缓存工具，兼容集群</pre>
 * @author lilinhai
 * @since 2021-02-05 14:55
 * @version V1.0  
 */
public class FmeDataCache
{
    public  static boolean initiFlag=false;

    public  static Set<Long> initiTemplateId=new HashSet<>();
    /**
     * <pre>从缓存中获取call</pre>
     * @author lilinhai
     * @since 2021-02-05 15:16 
     * @param deptId
     * @param uuid
     * @return Call
     */
    public static Call getCallByCoSpaceUuid(long deptId, String coSpaceUuid)
    {
        GenericValue<Call> genericValue = new GenericValue<>();
        FmeBridgeCache.getInstance().doBreakFmeBridgeBusiness(deptId, new FmeBridgeAddpterProcessor()
        {
            public void process(FmeBridge fmeBridge)
            {
                if (genericValue.getValue() == null)
                {
                    Call call = fmeBridge.getDataCache().getCallByCoSpaceUuid(coSpaceUuid);
                    if (call != null)
                    {
                        genericValue.setValue(call);
                        setBreak(true);
                    }
                }
            }
        });
        return genericValue.getValue();
    }
    
    /**
     * <pre>从缓存中获取call</pre>
     * @author lilinhai
     * @since 2021-02-05 15:16 
     * @param deptId
     * @param uuid
     * @return Call
     */
    public static CoSpace getCoSpaceByConferenceNumber(long deptId, String conferenceNumber)
    {
        GenericValue<CoSpace> genericValue = new GenericValue<>();
        FmeBridgeCache.getInstance().doBreakFmeBridgeBusiness(deptId, new FmeBridgeAddpterProcessor()
        {
            public void process(FmeBridge fmeBridge)
            {
                if (genericValue.getValue() == null)
                {
                    CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(conferenceNumber);
                    if (coSpace != null)
                    {
                        genericValue.setValue(coSpace);
                        setBreak(true);
                    }
                }
            }
        });
        return genericValue.getValue();
    }
    
    /**
     * 从缓存中获取CoSpace
     * @author sinhy
     * @since 2021-12-14 20:44 
     * @param coSpaceId
     * @return CoSpace
     */
    public static CoSpace getCoSpaceById(String coSpaceId)
    {
        List<FmeBridge> fbs = FmeBridgeCache.getInstance().getAvailableFmeBridges();
        if (!ObjectUtils.isEmpty(fbs))
        {
            for (FmeBridge fmeBridge : fbs)
            {
                CoSpace p = fmeBridge.getDataCache().getCoSpaceByUuid(coSpaceId);
                if (p != null)
                {
                    return p;
                }
            }
        }
        return null;
    }
    
    /**
     * <pre>从指定部门对应的FME缓存中根据URI获取Participant</pre>
     * @author lilinhai
     * @since 2021-02-05 15:16 
     * @param deptId
     * @param uuid
     * @return Call
     */
    public static ParticipantInfo getParticipantByConferenceNumberAndUri(long deptId, String conferenceNumber, String uri)
    {
        GenericValue<ParticipantInfo> genericValue = new GenericValue<>();
        FmeBridgeCache.getInstance().doBreakFmeBridgeBusiness(deptId, new FmeBridgeAddpterProcessor()
        {
            public void process(FmeBridge fmeBridge)
            {
                if (genericValue.getValue() == null)
                {
                    Participant participant = fmeBridge.getDataCache().getParticipantByConferenceNumberAndUri(conferenceNumber, uri);
                    if (participant != null && participant.is(ParticipantState.CONNECTED))
                    {
                        genericValue.setValue(new ParticipantInfo(participant, fmeBridge));
                        setBreak(true);
                    }
                }
            }
        });
        return genericValue.getValue();
    }
    
    /**
     * <pre>从所有FME数据缓存中获取Participant</pre>
     * @author lilinhai
     * @since 2021-02-05 15:16 
     * @param deptId
     * @param uuid
     * @return Call
     */
    public static ParticipantInfo getParticipantByUuid(FmeBridge fmeBridge, String uuid)
    {
        GenericValue<ParticipantInfo> gv = new GenericValue<>();
        fmeBridge.doFmeBridgeBusiness(FmeBridgeProcessingStrategy.BREAK, new FmeBridgeAddpterProcessor()
        {
            public void process(FmeBridge fmeBridge)
            {
                Participant p = fmeBridge.getDataCache().getParticipantByUuid(uuid);
                if (p != null)
                {
                    gv.setValue(new ParticipantInfo(p, fmeBridge));
                }
            }
        });
        return gv.getValue();
    }
    
    /**
     * <pre>从所有FME数据缓存中获取Participant</pre>
     * @author lilinhai
     * @since 2021-02-05 15:16 
     * @param deptId
     * @param uuid
     * @return Call
     */
    public static ParticipantInfo getParticipantByUuid(String uuid)
    {
        List<FmeBridge> fbs = FmeBridgeCache.getInstance().getAvailableFmeBridges();
        if (!ObjectUtils.isEmpty(fbs))
        {
            for (FmeBridge fmeBridge : fbs)
            {
                Participant p = fmeBridge.getDataCache().getParticipantByUuid(uuid);
                if (p != null)
                {
                    return new ParticipantInfo(p, fmeBridge);
                }
            }
        }
        return null;
    }
    
    /**
     * <pre>从所有FME数据缓存中获取Participant</pre>
     * @author lilinhai
     * @since 2021-02-05 15:16 
     * @param deptId
     * @param uuid
     * @return Call
     */
    public static Call getCallByUuid(String uuid)
    {
        List<FmeBridge> fbs = FmeBridgeCache.getInstance().getAvailableFmeBridges();
        if (!ObjectUtils.isEmpty(fbs))
        {
            for (FmeBridge fmeBridge : fbs)
            {
                Call p = fmeBridge.getDataCache().getCallByUuid(uuid);
                if (p != null)
                {
                    return p;
                }
            }
        }
        return null;
    }
}
