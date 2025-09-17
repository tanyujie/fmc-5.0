package com.paradisecloud.fcm.huaweicloud.huaweicloud.cache;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;

import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudConferenceAppointment;

import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.AttendeeCountingStatistics;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.DeptWeight;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.SyncInformation;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.AttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.McuAttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.RemotePartyAttendeesMap;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.TerminalAttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.event.model.WaitingListNotify;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.*;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation.AttendeeOperation;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation.DefaultViewOperation;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.system.model.SysDeptCache;
import io.jsonwebtoken.lang.Assert;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2023/4/20 10:54
 */

public class HwcloudConferenceContext extends BaseConferenceContext<AttendeeHwcloud> implements Serializable {

    private String meetingUUID;
    private String id;
    private String name;
    private Integer instanceid;
    private String msopenid;
    private String monitorNumber;
    private String accessCode;
    /** 带宽 */
    private Integer rate;
    private String meetingId;
    private String meetingStatus;
    @JsonIgnore
    private HwcloudBridge hwcloudBridge;
    @JsonIgnore
    private HwcloudMeetingBridge hwcloudMeetingBridge;

    private ConfPresetParamDTO confPresetParam;

    private Map<String, Object> monitorParticipantMap = new ConcurrentHashMap<>();
    /**
     * 用户设置的多画面信息
     */
    private MultiPicInfoNotify multiPicInfoNotify;


    /**
     * 用户设置的多画面信息
     */
    private PresetMultiPicReqDto multiPicInfo;

    /**
     * 等候室
     */
    private Map<String, Object> waitingParticipantMap = new ConcurrentHashMap<>();
    /**
     * 此会议自身作为MCU参会的remoteParty
     */
    private String fmeAttendeeRemoteParty;

    /**
     * 模板会议的ID，标记出是哪个模板发起的，好从模板点击进入会议进行关联
     */
    private Long templateConferenceId;
    /**
     * 会议是否开始
     */
    private volatile boolean isStart;
    /**
     * 部门ID
     */
    private Long deptId;
    /**
     * 是否结束
     */
    private volatile boolean isEnd;

    /**
     * 会议开始时间
     */
    private Date startTime;

    /**
     * 会议开始时间
     */
    private Date endTime;

    /**
     * 是否预约
     */
    private boolean isAppointment;

    private boolean isLiving;

    /**
     * 預約會議類型
     */
    private String conferenceTimeType;

    private BusiHistoryConference historyConference;

    private String  joinUrl;
    private String settings;
    private Integer meetingType;
    private String recurringRule;
    private String HwcloudUser;
    /**
     * 会议备注
     */
    private String remarks;
    /**
     * 会议密码
     */
    private String conferencePassword;

    /**
     * 带宽1,2,3,4,5,6M
     */
    private Integer bandwidth;

    /**
     * 模板绑定的会议号
     */
    private String conferenceNumber;

    /**
     * 模板会议类型：1级联，2普通
     */
    @JsonIgnore
    private Integer type;
    /**
     * 是否自动拉终端
     */
    private boolean isAutoCallTerminal;
    /**
     * 是否锁定
     */
    private volatile boolean locked;

    /**
     * 是否开启录制
     */
    private volatile boolean recorded;

    /**
     * 是否开启直播
     */
    private volatile boolean streaming;

    /**
     * 直播地址
     */
    private volatile String streamingUrl;

    /** 直播urlList */
    private volatile List<String> streamUrlList = new ArrayList<>();

    /**
     * 直播终端remoteParty
     */
    private volatile String streamingRemoteParty;

    /**
     * 直播名
     */
    private volatile String streamingName;

    /**
     * 直播终端
     */
    private volatile AttendeeHwcloud streamingAttendee;

    /**
     * 允许所有人静音自己
     */
    private volatile boolean allowAllMuteSelf;
    /**
     * 业务领域类型
     */
    private Integer businessFieldType;

    /** 是否启用会议时长 */
    private Integer durationEnabled;

    /** 会议时长单位分钟 */
    private Integer durationTime;


    private String chairmanPassword;


    public String getConferenceTimeType() {
        return conferenceTimeType;
    }

    public void setConferenceTimeType(String conferenceTimeType) {
        this.conferenceTimeType = conferenceTimeType;
    }

    private boolean cascade;
    private String version ;
    private String createUser;
    /** 会议创建者用户id */
    private Long createUserId;
    private Boolean lock;
    /**
     * 允许参会者聊天设置：
     * 0：允许参会者自由聊天
     * 1：仅允许参会者公开聊天
     * 2：仅允许私聊主持人
     */
    private Integer allowChat;

    private Boolean hideMeetingCodePassword;

    private Boolean shareScreen;

    private Boolean enableRedEnvelope;

    private Boolean muteAll=false;

    private Boolean autoWaitingRoom;

    private Boolean allowUnmuteBySelf;

    private Boolean onlyEnterpriseUserAllowed;

    private Boolean playIvrOnJoin;

    private Integer participantJoinMute;

    private int endReasonsType;

    private Integer lockSharing;

    private Integer callInRestriction;

    private Integer vas;

    /**
     * 当前正在进行的参会者操作（轮询/选看/点名）
     */
    @JsonIgnore
    private volatile AttendeeOperation attendeeOperation;

    @JsonIgnore
    private volatile AttendeeOperation lastAttendeeOperation;

    /**
     * 默认视图
     */
    @JsonIgnore
    private volatile DefaultViewOperation defaultViewOperation;

    /**
     * 主会场与会者
     */
    private volatile AttendeeHwcloud masterAttendee;
    /**
     * 会议主导方的参会者
     */
    private volatile List<AttendeeHwcloud> attendees = new ArrayList<>();

    /**
     * 所有级联子会议的主会场集合
     */
    private volatile List<AttendeeHwcloud> masterAttendees = new ArrayList<>();

    /**
     * FME自身充当的参会者
     */
    private volatile List<AttendeeHwcloud> fmeAttendees = new ArrayList<>();

    /**
     * 部门权重
     */
    private volatile List<DeptWeight> deptWeights = new ArrayList<>();

    /**
     * 级联方的参会者映射表，key为部门ID
     */
    private volatile Map<Long, List<AttendeeHwcloud>> cascadeAttendeesMap = new ConcurrentHashMap<>();

    /**
     * 所有地州的主会场map映射（用于做逻辑判断，不暴露给前端），key：attendeeId
     */
    private volatile Set<String> masterAttendeeIdSet = new HashSet<>();

    /**
     * 所有已配置的参会者映射key为remoteParty
     */
    @JsonIgnore
    private volatile Map<String, AttendeeHwcloud> attendeeMap = new ConcurrentHashMap<>();

    /**
     * 直播终端列表
     */
    @JsonIgnore
    private volatile List<TerminalAttendeeHwcloud> liveTerminals = new ArrayList<>();

    /**
     * 直播终端Map key:sn
     */
    private volatile Map<String, TerminalAttendeeHwcloud> liveTerminalMap = new ConcurrentHashMap();

    @JsonIgnore
    private volatile RemotePartyAttendeesMap remotePartyAttendeesMap = new RemotePartyAttendeesMap();

    @JsonIgnore
    private volatile RemotePartyAttendeesMap msopenIdAttendeesMap = new RemotePartyAttendeesMap();
    /**
     * 预约会议类型:1:预约会议;2:即时会议
     */
    private Integer appointmentType;

    /**
     * FME终端ID映射
     */
    @JsonIgnore
    private volatile Map<Long, TerminalAttendeeHwcloud> terminalAttendeeMap = new ConcurrentHashMap<>();

    /**
     * 终端ID映射
     */
    @JsonIgnore
    private volatile Map<Long, AttendeeHwcloud> terminalAttendeeAllMap = new ConcurrentHashMap<>();

    /**
     * 终端ID映射
     */
    @JsonIgnore
    private volatile Map<String, AttendeeHwcloud> participantAttendeeAllMap = new ConcurrentHashMap<>();
    /**
     * 清空主会场
     *
     * @author lilinhai
     * @since 2021-02-08 15:36 void
     */
    public synchronized void clearMasterAttendee() {
        this.masterAttendee = null;
    }
    /**
     * 参会者重呼开始时间（呼叫成功后或呼叫超过30分钟后，会清除该记录）
     */
    @JsonIgnore
    private volatile Map<String, Long> recallAttendeeBeginTimeMap = new ConcurrentHashMap<>();

    private SyncInformation syncInformation;

    @JsonIgnore
    private final Object syncLock = new Object();

    public SyncInformation getSyncInformation() {
        return syncInformation;
    }

    public void setSyncInformation(SyncInformation syncInformation) {
        this.syncInformation = syncInformation;
    }

    public Object getSyncLock() {
        return syncLock;
    }

    @Override
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean isCascade() {
        return cascade;
    }

    public void setCascade(boolean cascade) {
        this.cascade = cascade;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }




    private BusiMcuHwcloudConferenceAppointment conferenceAppointment;

    @Override
    public boolean isAppointment()
    {
        return isAppointment;
    }

    public void setAppointment(boolean isAppointment)
    {
        this.isAppointment = isAppointment;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
        this.setStart(true);
    }

    @Override
    public boolean isStart()
    {
        return isStart;
    }

    public void setStart(boolean isStart)
    {
        this.isStart = isStart;
    }

    @Override
    public boolean isEnd()
    {
        return isEnd;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }
    /**
     * 模板会议的创建时间
     */
    private Date templateCreateTime;
    /**
     * <p>Set Method   :   isEnd boolean</p>
     * @param isEnd
     */
    public void setEnd(boolean isEnd)
    {
        this.isEnd = isEnd;
        if(isEnd){
            this.isStart=false;
        }
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(long deptId) {
        this.deptId = deptId;
    }

    public void setMcuAttendeeRemoteParty(String attendeeIp)
    {
        this.fmeAttendeeRemoteParty = accessCode + "@" + attendeeIp;
    }





    public Map<String, Object> getMonitorParticipantMap() {
        return monitorParticipantMap;
    }

    public void setMonitorParticipantMap(Map<String, Object> monitorParticipantMap) {
        this.monitorParticipantMap = monitorParticipantMap;
    }


    /**
     * 所有与会者
     */
    private final Map<String, SmcParitipantsStateRep.ContentDTO> smcParticiPantMap = new ConcurrentHashMap<>();

    private String chairmanLayoutId;

    private String chooseLayoutId;

    private String callTheRollLayoutId;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }





    @Override
    public List<AttendeeHwcloud> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<AttendeeHwcloud> attendees) {
        this.attendees = attendees;
    }




    public String getMonitorNumber() {
        return monitorNumber;
    }

    public void setMonitorNumber(String monitorNumber) {
        this.monitorNumber = monitorNumber;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public String getFmeAttendeeRemoteParty() {
        return fmeAttendeeRemoteParty;
    }

    public void setFmeAttendeeRemoteParty(String fmeAttendeeRemoteParty) {
        this.fmeAttendeeRemoteParty = fmeAttendeeRemoteParty;
    }

    @Override
    public Long getTemplateConferenceId() {
        return templateConferenceId;
    }

    @Override
    public void setTemplateConferenceId(Long templateConferenceId) {
        this.templateConferenceId = templateConferenceId;
    }

    @Override
    public Date getStartTime() {
        return startTime;
    }



    public void addParticipantToIdMap(SmcParitipantsStateRep.ContentDTO attendee)
    {
        this.smcParticiPantMap.put(attendee.getGeneralParam().getId(), attendee);
    }




    @Override
    public BusiHistoryConference getHistoryConference() {
        return historyConference;
    }

    public void setHistoryConference(BusiHistoryConference historyConference) {
        this.historyConference = historyConference;
    }

    public String getCoSpaceId() {
        String coSpaceId = this.templateConferenceId + this.getConferenceNumber();
        if (this.templateCreateTime != null) {
            coSpaceId += "-" + this.templateCreateTime.getTime();
        }
        if(this.accessCode!=null){
            coSpaceId+="-"+this.getAccessCode();
        }
        return coSpaceId;
    }

    public SmcParitipantsStateRep.ContentDTO getParticipant(String participantId) {
      return   smcParticiPantMap.get(participantId);
    }

    @Override
    public AttendeeHwcloud getAttendeeByPUuid(String smcPid) {
        return getAttendeeBySmcId(smcPid);
    }


    public Map<String, AttendeeHwcloud> getUuidAttendeeMapByMsOpenId(String msopenid)
    {
        return remotePartyAttendeesMap.get(msopenid);
    }

    public AttendeeHwcloud getUuidAttendeeByMsOpenId(String msopenid)
    {
        Map<String, AttendeeHwcloud> attendeeHwcloudMap = remotePartyAttendeesMap.get(msopenid);
        if(attendeeHwcloudMap==null){
            return null;
        }
        for (Map.Entry<String, AttendeeHwcloud> attendeeHwcloudEntry : attendeeHwcloudMap.entrySet()) {
            AttendeeHwcloud value = attendeeHwcloudEntry.getValue();
            return value;
        }
        return null;
    }

    public AttendeeHwcloud getAttendeeBySmcId(String participantId) {
        return this.participantAttendeeAllMap.get(participantId);
    }


    public HwcloudConferenceContext( ) {
        this.version="Hwcloud4.0";
    }

    public HwcloudConferenceContext(HwcloudBridge hwcloudBridge) {
        this.hwcloudBridge = hwcloudBridge;
        this.version="4.0";
        this.setMcuTypeObject(McuType.MCU_HWCLOUD);
    }

    public Date getTemplateCreateTime() {
        return templateCreateTime;
    }

    public void setTemplateCreateTime(Date templateCreateTime) {
        this.templateCreateTime = templateCreateTime;
    }

    /**
     * 添加FME类型的终端参会者
     *
     * @param attendee
     */
    public void addMcuAttendee(McuAttendeeHwcloud attendee) {
        attendee.setConferenceNumber(conferenceNumber);
        attendee.setContextKey(getContextKey());
        attendee.setDeptId(deptId);
        this.mcuAttendees.add(attendee);
        this.addAttendee(attendee);
    }


    @Override
    public String getMcuCallIp() {

        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public HwcloudBridge getHwcloudBridge() {
        return hwcloudBridge;
    }

    public void setHwcloudBridge(HwcloudBridge hwcloudBridge) {
        this.hwcloudBridge = hwcloudBridge;
    }

    @Override
    public BusiMcuHwcloudConferenceAppointment getConferenceAppointment() {
        return conferenceAppointment;
    }

    public void setConferenceAppointment(BusiMcuHwcloudConferenceAppointment conferenceAppointment) {
        this.conferenceAppointment = conferenceAppointment;
    }

    public Map<String, SmcParitipantsStateRep.ContentDTO> getSmcParticiPantMap() {
        return smcParticiPantMap;
    }

    public Integer getInstanceid() {
        return instanceid;
    }

    public void setInstanceid(Integer instanceid) {
        this.instanceid = instanceid;
    }

    public String getJoinUrl() {
        return joinUrl;
    }

    public void setJoinUrl(String joinUrl) {
        this.joinUrl = joinUrl;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public Integer getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(Integer meetingType) {
        this.meetingType = meetingType;
    }

    public String getRecurringRule() {
        return recurringRule;
    }

    public void setRecurringRule(String recurringRule) {
        this.recurringRule = recurringRule;
    }

    public String getHwcloudUser() {
        return HwcloudUser;
    }

    public void setHwcloudUser(String HwcloudUser) {
        this.HwcloudUser = HwcloudUser;
    }

    public boolean isLiving() {
        return isLiving;
    }

    public void setLiving(boolean living) {
        isLiving = living;
    }

    public String getMsopenid() {
        return msopenid;
    }

    public void setMsopenid(String msopenid) {
        this.msopenid = msopenid;
    }



    public Boolean getLock() {
        return lock;
    }

    public void setLock(Boolean lock) {
        this.lock = lock;
    }

    public Integer getAllowChat() {
        return allowChat;
    }

    public void setAllowChat(Integer allowChat) {
        this.allowChat = allowChat;
    }

    public Boolean getHideMeetingCodePassword() {
        return hideMeetingCodePassword;
    }

    public void setHideMeetingCodePassword(Boolean hideMeetingCodePassword) {
        this.hideMeetingCodePassword = hideMeetingCodePassword;
    }

    public Boolean getShareScreen() {
        return shareScreen;
    }

    public void setShareScreen(Boolean shareScreen) {
        this.shareScreen = shareScreen;
    }

    public Boolean getEnableRedEnvelope() {
        return enableRedEnvelope;
    }

    public void setEnableRedEnvelope(Boolean enableRedEnvelope) {
        this.enableRedEnvelope = enableRedEnvelope;
    }

    public Boolean getMuteAll() {
        return muteAll;
    }

    public void setMuteAll(Boolean muteAll) {
        this.muteAll = muteAll;
    }

    @Override
    public Map<Long, List<AttendeeHwcloud>> getCascadeAttendeesMap() {
        return cascadeAttendeesMap;
    }

    public void setCascadeAttendeesMap(Map<Long, List<AttendeeHwcloud>> cascadeAttendeesMap) {
        this.cascadeAttendeesMap = cascadeAttendeesMap;
    }

    @Override
    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    @Override
    public String getConferencePassword() {
        return conferencePassword;
    }

    public void setConferencePassword(String conferencePassword) {
        this.conferencePassword = conferencePassword;
    }

    @Override
    public Integer getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Integer bandwidth) {
        this.bandwidth = bandwidth;
    }

    @Override
    public String getConferenceNumber() {
        return conferenceNumber;
    }

    @Override
    public void setConferenceNumber(String conferenceNumber) {
        this.conferenceNumber = conferenceNumber;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    @Override
    public boolean isRecorded() {
        return recorded;
    }

    @Override
    public void setRecorded(boolean recorded) {
        this.recorded = recorded;
    }

    @Override
    public boolean isStreaming() {
        return streaming;
    }
    @Override
    public void setStreaming(boolean streaming) {
        this.streaming = streaming;
    }

    @Override
    public String getStreamingUrl() {
        return streamingUrl;
    }

    @Override
    public void setStreamingUrl(String streamingUrl) {
        this.streamingUrl = streamingUrl;
    }

    @Override
    public List<String> getStreamUrlList() {
        return streamUrlList;
    }

    public void setStreamUrlList(List<String> streamUrlList) {
        this.streamUrlList = streamUrlList;
    }
    @Override
    public String getStreamingRemoteParty() {
        return streamingRemoteParty;
    }
    @Override
    public void setStreamingRemoteParty(String streamingRemoteParty) {
        this.streamingRemoteParty = streamingRemoteParty;
    }
    @Override
    public String getStreamingName() {
        return streamingName;
    }
    @Override
    public void setStreamingName(String streamingName) {
        this.streamingName = streamingName;
    }
    @Override
    public AttendeeHwcloud getStreamingAttendee() {
        return streamingAttendee;
    }
    @Override
    public void setStreamingAttendee(AttendeeHwcloud streamingAttendee) {
        this.streamingAttendee = streamingAttendee;
    }

    public boolean isAllowAllMuteSelf() {
        return allowAllMuteSelf;
    }

    public void setAllowAllMuteSelf(boolean allowAllMuteSelf) {
        this.allowAllMuteSelf = allowAllMuteSelf;
    }

    public AttendeeOperation getAttendeeOperation() {
        return attendeeOperation;
    }

    public void setAttendeeOperation(AttendeeOperation attendeeOperation) {
        this.attendeeOperation = attendeeOperation;
    }

    public AttendeeOperation getLastAttendeeOperation() {
        return lastAttendeeOperation;
    }

    public void setLastAttendeeOperation(AttendeeOperation lastAttendeeOperation) {
        this.lastAttendeeOperation = lastAttendeeOperation;
    }

    public DefaultViewOperation getDefaultViewOperation() {
        return defaultViewOperation;
    }

    public void setDefaultViewOperation(DefaultViewOperation defaultViewOperation) {
        this.defaultViewOperation = defaultViewOperation;
    }

    @Override
    public List<AttendeeHwcloud> getMasterAttendees() {
        return masterAttendees;
    }

    public void setMasterAttendees(List<AttendeeHwcloud> masterAttendees) {
        this.masterAttendees = masterAttendees;
    }

    public List<AttendeeHwcloud> getFmeAttendees() {
        return fmeAttendees;
    }

    public void setFmeAttendees(List<AttendeeHwcloud> fmeAttendees) {
        this.fmeAttendees = fmeAttendees;
    }

    public List<DeptWeight> getDeptWeights() {
        return deptWeights;
    }

    public void setDeptWeights(List<DeptWeight> deptWeights) {
        this.deptWeights = deptWeights;
    }

    public Set<String> getMasterAttendeeIdSet() {
        return masterAttendeeIdSet;
    }

    public void setMasterAttendeeIdSet(Set<String> masterAttendeeIdSet) {
        this.masterAttendeeIdSet = masterAttendeeIdSet;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Map<String, AttendeeHwcloud> getAttendeeMap() {
        return attendeeMap;
    }

    public void setAttendeeMap(Map<String, AttendeeHwcloud> attendeeMap) {
        this.attendeeMap = attendeeMap;
    }

    @Override
    public List<TerminalAttendeeHwcloud> getLiveTerminals() {
        return liveTerminals;
    }

    public void setLiveTerminals(List<TerminalAttendeeHwcloud> liveTerminals) {
        this.liveTerminals = liveTerminals;
    }

    public Map<String, TerminalAttendeeHwcloud> getLiveTerminalMap() {
        return liveTerminalMap;
    }

    public void setLiveTerminalMap(Map<String, TerminalAttendeeHwcloud> liveTerminalMap) {
        this.liveTerminalMap = liveTerminalMap;
    }

    public RemotePartyAttendeesMap getRemotePartyAttendeesMap() {
        return remotePartyAttendeesMap;
    }

    public void setRemotePartyAttendeesMap(RemotePartyAttendeesMap remotePartyAttendeesMap) {
        this.remotePartyAttendeesMap = remotePartyAttendeesMap;
    }

    @Override
    public Map<Long, TerminalAttendeeHwcloud> getTerminalAttendeeMap() {
        return terminalAttendeeMap;
    }

    public void setTerminalAttendeeMap(Map<Long, TerminalAttendeeHwcloud> terminalAttendeeMap) {
        this.terminalAttendeeMap = terminalAttendeeMap;
    }

    public Map<Long, AttendeeHwcloud> getTerminalAttendeeAllMap() {
        return terminalAttendeeAllMap;
    }

    public void setTerminalAttendeeAllMap(Map<Long, AttendeeHwcloud> terminalAttendeeAllMap) {
        this.terminalAttendeeAllMap = terminalAttendeeAllMap;
    }

    public Map<String, AttendeeHwcloud> getParticipantAttendeeAllMap() {
        return participantAttendeeAllMap;
    }

    public void setParticipantAttendeeAllMap(Map<String, AttendeeHwcloud> participantAttendeeAllMap) {
        this.participantAttendeeAllMap = participantAttendeeAllMap;
    }

    public Map<String, Long> getRecallAttendeeBeginTimeMap() {
        return recallAttendeeBeginTimeMap;
    }

    public void setRecallAttendeeBeginTimeMap(Map<String, Long> recallAttendeeBeginTimeMap) {
        this.recallAttendeeBeginTimeMap = recallAttendeeBeginTimeMap;
    }

    @Override
    public AttendeeHwcloud getMasterAttendee() {
        return masterAttendee;
    }




    public synchronized void setMasterAttendee(AttendeeHwcloud masterAttendee)
    {
        Assert.notNull(masterAttendee, "【" + SysDeptCache.getInstance().get(masterAttendee.getDeptId()).getDeptName() + "】主会场不能为空！");
        if (this.masterAttendee != null)
        {
            if (this.masterAttendee.getDeptId() == deptId.longValue())
            {
                attendees.add(this.masterAttendee);
               // Collections.sort(attendees);
            }
            else
            {
                if (this.masterAttendeeIdSet.contains(this.masterAttendee.getId()))
                {
                    this.masterAttendees.add(this.masterAttendee);
                   // Collections.sort(this.masterAttendees);
                }
                else
                {
                    List<AttendeeHwcloud> as = this.cascadeAttendeesMap.get(this.masterAttendee.getDeptId());
                    if (as != null)
                    {
                        as.add(this.masterAttendee);
                     //   Collections.sort(as);
                    }
                }
            }
        }

        if (masterAttendee.getDeptId() != deptId.longValue())
        {
            if (this.masterAttendeeIdSet.contains(masterAttendee.getId()))
            {
                this.masterAttendees.remove(masterAttendee);
            }
            else
            {
                List<AttendeeHwcloud> as = this.cascadeAttendeesMap.get(masterAttendee.getDeptId());
                if (!ObjectUtils.isEmpty(as))
                {
                    as.remove(masterAttendee);
                }
            }
        }
        else
        {
            attendees.remove(masterAttendee);
        }
        this.masterAttendee = masterAttendee;
        masterAttendee.setMaster(true);
        this.masterAttendee.setContextKey(getContextKey());
    }

    /**
     * 添加会议主导方的终端参会者
     * @param attendee
     */
    public synchronized void addAttendee(AttendeeHwcloud attendee)
    {
        if (attendee instanceof TerminalAttendeeHwcloud)
        {
            TerminalAttendeeHwcloud terminalAttendee = (TerminalAttendeeHwcloud) attendee;
            TerminalAttendeeHwcloud old = terminalAttendeeMap.get(terminalAttendee.getTerminalId());
            if (old != null)
            {
                old.syncTo(terminalAttendee);
            }
            terminalAttendeeMap.put(terminalAttendee.getTerminalId(), terminalAttendee);
        }
        if (this.attendees.contains(attendee))
        {
            this.attendees.remove(attendee);
        }
        this.attendees.add(attendee);

        addAttendeeToIdMap(attendee);
        addAttendeeToRemotePartyMap(attendee);
        addAttendeeToTerminalAllMap(attendee);
        if (Strings.isNotBlank((attendee.getParticipantUuid()))){
            participantAttendeeAllMap.put(attendee.getParticipantUuid(), attendee);
        }
        attendee.setContextKey(getContextKey());
    }


    @Override
    public void updateAttendeeToRemotePartyMap(String oldRemoteParty, AttendeeHwcloud attendee) {
        if (StringUtils.hasText(oldRemoteParty)) {
            Map<String, AttendeeHwcloud> uuidAttendeeMap = remotePartyAttendeesMap.get(oldRemoteParty);
            if (uuidAttendeeMap != null) {
                remotePartyAttendeesMap.remove(oldRemoteParty);
            }
        }
        if (StringUtils.hasText(attendee.getRemoteParty())) {
            remotePartyAttendeesMap.addAttendee(attendee);
        }
    }

    public void addAttendeeToIdMap(AttendeeHwcloud attendee)
    {
        this.attendeeMap.put(attendee.getId(), attendee);
    }

    public void addAttendeeToRemotePartyMap(AttendeeHwcloud attendee)
    {
        remotePartyAttendeesMap.addAttendee(attendee);
    }

    public void addAttendeeToTerminalAllMap(AttendeeHwcloud attendee) {
        if (attendee.getTerminalId() != null) {
            terminalAttendeeAllMap.put(attendee.getTerminalId(), attendee);
        }
    }
    /**
     * MCU自身充当的参会者
     */
    private volatile List<McuAttendeeHwcloud> mcuAttendees = new ArrayList<>();
    /**
     * 删除MCU类型参会终端
     *
     * @param mcuAttendee
     */
    public void removeMcuAttendee(McuAttendeeHwcloud mcuAttendee) {
        this.mcuAttendees.remove(mcuAttendee);
        this.removeAttendeeById(mcuAttendee.getId());
    }
    public synchronized AttendeeHwcloud removeAttendeeById(String id)
    {
        AttendeeHwcloud attendee = this.attendeeMap.remove(id);
        if (attendee != null)
        {
            attendees.remove(attendee);

            this.masterAttendeeIdSet.remove(id);
            this.masterAttendees.remove(attendee);

            List<AttendeeHwcloud> as = cascadeAttendeesMap.get(attendee.getDeptId());
            if (as != null)
            {
                as.remove(attendee);
            }

            if (attendee instanceof TerminalAttendeeHwcloud) {
                TerminalAttendeeHwcloud ta = (TerminalAttendeeHwcloud) attendee;
                terminalAttendeeMap.remove(ta.getTerminalId());
                if (TerminalType.isRtsp(ta.getTerminalType())) {
                    String rtsp_uri = (String) ta.getBusinessProperties().get("rtsp_uri");
                    this.remotePartyAttendeesMap.removeAttendeeByRemotePartyAndUuid(rtsp_uri, id);
                }
            }
            this.remotePartyAttendeesMap.removeAttendeeByRemotePartyAndUuid(attendee.getRemoteParty(), id);
            removeRecallAttendeeBeginTime(attendee);
            removeAttendeeFromTerminalAllMap(attendee);
            this.participantAttendeeAllMap.remove(attendee.getSmcParticipant().getGeneralParam().getId());

        }
        return attendee;
    }
    public Long removeRecallAttendeeBeginTime(AttendeeHwcloud attendee)
    {
        return recallAttendeeBeginTimeMap.remove(attendee.getId());
    }

    public void removeAttendeeFromTerminalAllMap(AttendeeHwcloud attendee) {
        if (attendee.getTerminalId() != null) {
            terminalAttendeeAllMap.remove(attendee.getTerminalId());
        }
    }

    @Override
    public AttendeeHwcloud getAttendeeById(String attendeeId)
    {
        return attendeeMap.get(attendeeId);
    }


    public Integer getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(Integer appointmentType) {
        this.appointmentType = appointmentType;
    }

    public AttendeeCountingStatistics getAttendeeCountingStatistics()
    {
        return new AttendeeCountingStatistics(this);
    }

    public void setEndReasonsType(int endReasonsType) {
        this.endReasonsType = endReasonsType;
    }

    @Override
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isAutoCallTerminal() {
        return isAutoCallTerminal;
    }

    public void setAutoCallTerminal(boolean autoCallTerminal) {
        isAutoCallTerminal = autoCallTerminal;
    }

    @Override
    public Integer getBusinessFieldType() {
        return businessFieldType;
    }

    public void setBusinessFieldType(Integer businessFieldType) {
        this.businessFieldType = businessFieldType;
    }

    public Integer getDurationEnabled() {
        return durationEnabled;
    }

    public void setDurationEnabled(Integer durationEnabled) {
        this.durationEnabled = durationEnabled;
    }

    @Override
    public Integer getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(Integer durationTime) {
        this.durationTime = durationTime;
    }

    @Override
    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public int getEndReasonsType() {
        return endReasonsType;
    }

    @Override
    public List<McuAttendeeHwcloud> getMcuAttendees() {
        return mcuAttendees;
    }

    public void setMcuAttendees(List<McuAttendeeHwcloud> mcuAttendees) {
        this.mcuAttendees = mcuAttendees;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public Map<String, AttendeeHwcloud> getUuidAttendeeMapByUri(String remoteParty) {
        return remotePartyAttendeesMap.get(remoteParty);
    }

    public String getMeetingStatus() {
        return meetingStatus;
    }

    public void setMeetingStatus(String meetingStatus) {
        this.meetingStatus = meetingStatus;
    }

    public Boolean getAutoWaitingRoom() {
        return autoWaitingRoom;
    }

    public void setAutoWaitingRoom(Boolean autoWaitingRoom) {
        this.autoWaitingRoom = autoWaitingRoom;
    }

    public Boolean getAllowUnmuteBySelf() {
        return allowUnmuteBySelf;
    }

    public void setAllowUnmuteBySelf(Boolean allowUnmuteBySelf) {
        this.allowUnmuteBySelf = allowUnmuteBySelf;
    }

    public Boolean getOnlyEnterpriseUserAllowed() {
        return onlyEnterpriseUserAllowed;
    }

    public void setOnlyEnterpriseUserAllowed(Boolean onlyEnterpriseUserAllowed) {
        this.onlyEnterpriseUserAllowed = onlyEnterpriseUserAllowed;
    }

    public Boolean getPlayIvrOnJoin() {
        return playIvrOnJoin;
    }

    public Integer getParticipantJoinMute() {
        return participantJoinMute;
    }

    public void setParticipantJoinMute(Integer participantJoinMute) {
        this.participantJoinMute = participantJoinMute;
    }

    public void setPlayIvrOnJoin(Boolean playIvrOnJoin) {
        this.playIvrOnJoin = playIvrOnJoin;
    }

    public String getChairmanLayoutId() {
        return chairmanLayoutId;
    }

    public void setChairmanLayoutId(String chairmanLayoutId) {
        this.chairmanLayoutId = chairmanLayoutId;
    }

    public String getChooseLayoutId() {
        return chooseLayoutId;
    }

    public void setChooseLayoutId(String chooseLayoutId) {
        this.chooseLayoutId = chooseLayoutId;
    }

    public String getCallTheRollLayoutId() {
        return callTheRollLayoutId;
    }

    public void setCallTheRollLayoutId(String callTheRollLayoutId) {
        this.callTheRollLayoutId = callTheRollLayoutId;
    }
    private volatile String presentAttendeeId = null;

    @Override
    public String getPresentAttendeeId() {
        return presentAttendeeId;
    }

    public void setPresentAttendeeId(String presentAttendeeId) {
        this.presentAttendeeId = presentAttendeeId;
    }

    @Override
    public AttendeeHwcloud getAttendeeByRemoteParty(String remoteParty) {
        Map<String, AttendeeHwcloud> stringAttendeeHwcloudMap = remotePartyAttendeesMap.get(remoteParty);
        if(stringAttendeeHwcloudMap==null){
            return null;
        }
        for (AttendeeHwcloud value : stringAttendeeHwcloudMap.values()) {
            return  value;
        }
        return null;
    }

    /**
     * 租户ID
     */
    private volatile String tenantId = "";

    @Override
    public String getTenantId() {
        return tenantId;
    }

    @Override
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    /**
     * 清除缓存
     */
    public void clear()
    {
        attendees.clear();
        masterAttendees.clear();
        fmeAttendees.clear();
        cascadeAttendeesMap.clear();
        attendeeMap.clear();
        terminalAttendeeMap.clear();
        recallAttendeeBeginTimeMap.clear();
        liveTerminals.clear();
        liveTerminalMap.clear();
        cascade = false;
    }

    public MultiPicInfoNotify getMultiPicInfoNotify() {
        return multiPicInfoNotify;
    }

    public void setMultiPicInfoNotify(MultiPicInfoNotify multiPicInfoNotify) {
        this.multiPicInfoNotify = multiPicInfoNotify;
    }

    public HwcloudMeetingBridge getHwcloudMeetingBridge() {
        return hwcloudMeetingBridge;
    }

    public void setHwcloudMeetingBridge(HwcloudMeetingBridge hwcloudMeetingBridge) {
        this.hwcloudMeetingBridge = hwcloudMeetingBridge;
    }

    public String getMeetingUUID() {
        return meetingUUID;
    }

    public void setMeetingUUID(String meetingUUID) {
        this.meetingUUID = meetingUUID;
    }

    public ConfPresetParamDTO getConfPresetParam() {
        return confPresetParam;
    }

    public void setConfPresetParam(ConfPresetParamDTO confPresetParam) {
        this.confPresetParam = confPresetParam;
    }

    public Map<String, Object> getWaitingParticipantMap() {
        return waitingParticipantMap;
    }

    public void setWaitingParticipantMap(Map<String, Object> waitingParticipantMap) {
        this.waitingParticipantMap = waitingParticipantMap;
    }

    public Integer getLockSharing() {
        return lockSharing;
    }

    public void setLockSharing(Integer lockSharing) {
        this.lockSharing = lockSharing;
    }

    public Integer getCallInRestriction() {
        return callInRestriction;
    }

    public void setCallInRestriction(Integer callInRestriction) {
        this.callInRestriction = callInRestriction;
    }

    public Integer getVas() {
        return vas;
    }

    public void setVas(Integer vas) {
        this.vas = vas;
    }

    public PresetMultiPicReqDto getMultiPicInfo() {
        return multiPicInfo;
    }

    public void setMultiPicInfo(PresetMultiPicReqDto multiPicInfo) {
        this.multiPicInfo = multiPicInfo;
    }

    public String getChairmanPassword() {
        return chairmanPassword;
    }

    public void setChairmanPassword(String chairmanPassword) {
        this.chairmanPassword = chairmanPassword;
    }
}


