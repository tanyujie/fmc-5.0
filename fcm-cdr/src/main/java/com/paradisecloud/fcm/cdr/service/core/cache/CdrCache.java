package com.paradisecloud.fcm.cdr.service.core.cache;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.BusiHistoryParticipant;
import com.paradisecloud.fcm.dao.model.CdrCall;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author johnson liu
 * @date 2021/5/26 17:12
 */
public class CdrCache extends JavaCache<String, ConferenceContext>
{
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * 
     * @since 2021-12-15 11:53
     */
    private static final long serialVersionUID = 1L;
    
    private static final CdrCache INSTANCE = new CdrCache();
    
    /**
     * 存储接收到的最大的CorrelationIndex
     */
    private Map<String, Integer> receivedCdrCorrelationIndexMap = new ConcurrentHashMap<>();
    
    /**
     * 根据CallId缓存CdrCall
     */
    private Map<String, CdrCall> cdrCallMap = new ConcurrentHashMap<>();
    
    /**
     * 根据callLegId缓存历史与会者
     */
    private Map<String, BusiHistoryParticipant> historyParticipantMap = new ConcurrentHashMap<>();
    
    /**
     * 根据coSpaceId缓存历史会议
     */
    private Map<String, BusiHistoryConference> historyConferenceMap = new ConcurrentHashMap<>();
    
    /**
     * 与会者保存与否;key:callLegId
     */
    private Map<String, Boolean> callLegSaveMap = new ConcurrentHashMap<>();
    
    public static CdrCache getInstance()
    {
        return INSTANCE;
    }
    
    /**
     * 私有化构造器
     */
    private CdrCache()
    {
    }
    
    public Integer putCorrelationIndex(String key, Integer correlatorIndex)
    {
        return receivedCdrCorrelationIndexMap.put(key, correlatorIndex);
    }
    
    public Integer getCorrelationIndex(String key)
    {
        return receivedCdrCorrelationIndexMap.get(key);
    }
    
    public Boolean putCallLegSaveMap(String key, Boolean isAddParticipant)
    {
        return callLegSaveMap.put(key, isAddParticipant);
    }
    
    public Boolean getCallLegSaveMap(String key)
    {
        return callLegSaveMap.get(key);
    }
    
    public Boolean removeCallLegSaveMap(String key)
    {
        return callLegSaveMap.remove(key);
    }
    
    public CdrCall putCdrCallMap(String key, CdrCall cdrCall)
    {
        return cdrCallMap.put(key, cdrCall);
    }
    
    public CdrCall removeCdrCallMap(String key)
    {
        return cdrCallMap.remove(key);
    }
    
    public CdrCall getCdrCallMap(String key)
    {
        return cdrCallMap.get(key);
    }
    
    public BusiHistoryParticipant putHistoryParticipantMap(String callLegId, BusiHistoryParticipant historyParticipant)
    {
        return historyParticipantMap.put(callLegId, historyParticipant);
    }
    
    public BusiHistoryParticipant removeHistoryParticipantMap(String callLegId)
    {
        return historyParticipantMap.remove(callLegId);
    }
    
    public BusiHistoryParticipant getHistoryParticipant(String callLegId)
    {
        return historyParticipantMap.get(callLegId);
    }
    
    public BusiHistoryConference putHistoryConferenceMap(String coSpaceId, BusiHistoryConference historyConference)
    {
        return historyConferenceMap.put(coSpaceId, historyConference);
    }
    
    public BusiHistoryConference removeHistoryConferenceMap(String coSpaceId)
    {
        return historyConferenceMap.remove(coSpaceId);
    }
    
    public BusiHistoryConference getHistoryConference(String coSpaceId)
    {
        return historyConferenceMap.get(coSpaceId);
    }
}
