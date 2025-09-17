package com.paradisecloud.fcm.smc.cache.modle;

import com.paradisecloud.com.fcm.smc.modle.SmcConferenceContext;
import com.paradisecloud.com.fcm.smc.modle.SmcConferenceContextBase;
import com.paradisecloud.com.fcm.smc.modle.request.MultiPicInfoReq;
import com.paradisecloud.com.fcm.smc.modle.response.VideoSourceRep;
import com.paradisecloud.common.cache.JavaCache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author nj
 * @date 2022/9/20 11:38
 */
public class SmcConferenceContextCache extends JavaCache<String, SmcConferenceContextBase> {

    private static final SmcConferenceContextCache INSTANCE = new SmcConferenceContextCache();
    public static SmcConferenceContextCache getInstance()
    {
        return INSTANCE;
    }

    private SmcConferenceContextCache()
    {

    }
    /**
     * 会议ID
     *
     */
    private  Map<String, ConferenceState> smcConferenceStateMap = new ConcurrentHashMap<>();

    /**
     * 会议ID
     *
     */
    private  Map<String, Object> terminalURiIdMap = new ConcurrentHashMap<>();

    /**
     * 会议ID
     *
     */
    private  Map<String, Boolean> smcConferenceSubcribeMap = new ConcurrentHashMap<>();
    /**
     * 会议ID
     *
     */
    private Map<String, SmcConferenceContextBase> smcConferenceContextsMap = new ConcurrentHashMap<>();

    /**
     * 会议ID
     *
     */
    private Map<String, SmcConferenceContext> smcConferenceContextMap = new ConcurrentHashMap<>();
    /**
     * 模板id
     */
    private Map<String, SmcConferenceContextBase> templateIdConference = new ConcurrentHashMap<>();


    private Map<String, Map<String,String>> cascadeConference = new ConcurrentHashMap<>();


    private Map<String, String> chooseParticipantMap = new ConcurrentHashMap<>();

    private Map<String, Object> monitorParticipantMap = new ConcurrentHashMap<>();

    private Map<String, List<VideoSourceRep>> VideoSourceRepMap = new ConcurrentHashMap<>();

    private Map<String, List<VideoSourceRep>> chairmanIdVideoSourceRepMap = new ConcurrentHashMap<>();
    /**
     * 会议布局
     */
    private Map<String, MultiPicInfoReq> localMultiPicInfoMap = new ConcurrentHashMap<>();

    public Map<String, MultiPicInfoReq> getLocalMultiPicInfoMap() {
        return localMultiPicInfoMap;
    }

    public Map<String, List<VideoSourceRep>> getChairmanIdVideoSourceRepMap() {
        return chairmanIdVideoSourceRepMap;
    }

    public Map<String, List<VideoSourceRep>> getVideoSourceRepMap() {
        return VideoSourceRepMap;
    }

    public Map<String, String> getChooseParticipantMap() {
        return chooseParticipantMap;
    }



    public Map<String, SmcConferenceContextBase> getSmcConferenceContextsMap() {
        return smcConferenceContextsMap;
    }

    public Map<String, SmcConferenceContext> getSmcConferenceContextMap() {
        return smcConferenceContextMap;
    }

    public Map<String, SmcConferenceContextBase> getTemplateIdConference() {
        return templateIdConference;
    }



    public Map<String,  Map<String,String>> getCascadeConference() {
        return cascadeConference;
    }

    public Map<String, Object> getMonitorParticipantMap() {
        return monitorParticipantMap;
    }

    public Map<String, ConferenceState> getSmcConferenceStateMap() {
        return smcConferenceStateMap;
    }

    public void setSmcConferenceStateMap(Map<String, ConferenceState> smcConferenceStateMap) {
        this.smcConferenceStateMap = smcConferenceStateMap;
    }

    public Map<String, Boolean> getSmcConferenceSubcribeMap() {
        return smcConferenceSubcribeMap;
    }

    public void setSmcConferenceSubcribeMap(Map<String, Boolean> smcConferenceSubcribeMap) {
        this.smcConferenceSubcribeMap = smcConferenceSubcribeMap;
    }

    public static Map<String,Thread> chairmanPollingThread=new ConcurrentHashMap<>();

    public  Map<String, Thread> getChairmanPollingThread() {
        return chairmanPollingThread;
    }

    public synchronized void cleanCacheMap(String conferenceId) {
        SmcBridgeCache.getInstance().getConferenceBridge().remove(conferenceId);
        SmcConferenceContextCache.getInstance().getChooseParticipantMap().remove(conferenceId);
        SmcConferenceContextCache.getInstance().getLocalMultiPicInfoMap().remove(conferenceId);
        SmcConferenceContextCache.getInstance().getChairmanIdVideoSourceRepMap().remove(conferenceId);
        chairmanPollingThread.remove(conferenceId);
        smcConferenceStateMap.remove(conferenceId);
        smcConferenceSubcribeMap.remove(conferenceId);
        terminalURiIdMap.clear();
    }

    private Map<String, List<String>> particiPantsMap = new ConcurrentHashMap<>();
    private Map<String, String> groupIdMap = new ConcurrentHashMap<>();

    public Map<String, String> getGroupIdMap() {
        return groupIdMap;
    }

    public void setGroupIdMap(Map<String, String> groupIdMap) {
        this.groupIdMap = groupIdMap;
    }

    public Map<String, List<String>> getParticiPantsMap() {
        return particiPantsMap;
    }

    public void setParticiPantsMap(Map<String, List<String>> particiPantsMap) {
        this.particiPantsMap = particiPantsMap;
    }

    public Map<String, Object> getTerminalURiIdMap() {
        return terminalURiIdMap;
    }

    public void setTerminalURiIdMap(Map<String, Object> terminalURiIdMap) {
        this.terminalURiIdMap = terminalURiIdMap;
    }
}
