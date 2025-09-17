package com.paradisecloud.fcm.smc2.cache;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paradisecloud.com.fcm.smc.modle.*;
import com.paradisecloud.com.fcm.smc.modle.request.MultiPicPollRequest;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2ConferenceAppointment;
import com.paradisecloud.fcm.smc2.model.AttendeeOperation;
import com.paradisecloud.fcm.smc2.model.DefaultViewOperation;
import com.paradisecloud.fcm.smc2.model.DeptWeight;
import com.paradisecloud.fcm.smc2.model.SyncInformation;
import com.paradisecloud.fcm.smc2.model.attendee.*;
import com.paradisecloud.fcm.smc2.model.attendee.operation.ChangeMasterAttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.smc2.model.layout.ChairManSmc2PollingThread;
import com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq;
import com.paradisecloud.fcm.smc2.model.request.MultiPicInfoTalkReq;
import com.paradisecloud.system.model.SysDeptCache;
import io.jsonwebtoken.lang.Assert;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2023/4/20 10:54
 */

public class Smc2ConferenceContext extends BaseConferenceContext<AttendeeSmc2> {

    private String id;
    private SmcConference conference;
    @JsonIgnore
    private SmcMultiConferenceService multiConferenceService;
    @JsonIgnore
    private SmcConferenceTemplate.StreamServiceDTO streamService;
    private SmcConferenceTemplate.ConfPresetParamDTO confPresetParam;
    @JsonIgnore
    private SmcConferenceTemplate.SubtitleServiceDTO subtitleService;
    private List<AttendeeSmc2> attendees= new ArrayList<>();
    @JsonIgnore
    private List<ParticipantRspDto> participants;
    @JsonIgnore
    private DetailConference detailConference;
    @JsonIgnore
    private List<SmcParitipantsStateRep.ContentDTO> content=new ArrayList<>();
    private String monitorNumber;
    private String accessCode;
    private ParticipantRspDto masterParticipant;
    private SmcParitipantsStateRep.ContentDTO masterSmcParticiPant;
    /** 带宽 */
    private Integer rate;
    private Integer smc2TemplateId;
    private String smc2conferenceId;
    @JsonIgnore
    private Smc2Bridge smc2Bridge;
    private ChairManSmc2PollingThread chairManSmc2PollingThread;
    private List<SmcParitipantsStateRep.ContentDTO> participantOrderList=new ArrayList<>();
    private Map<String, Object> monitorParticipantMap = new ConcurrentHashMap<>();

    private String chairmanId;
    private com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq.MultiPicInfoDTO multiPicInfo;

    private volatile boolean startRound;
    private String lockPresenterId;
    /**
     * 租户ID
     */
    private volatile String tenantId = "";
    /**
     * 业务领域类型
     */
    private Integer businessFieldType;

    /**
     * 是否自动拉终端
     */
    private boolean isAutoCallTerminal;
    /**
     * 会议密码
     */
    private String conferencePassword;


    /** 会议创建者用户id */
    private Long createUserId;
    /**
     * 会议备注
     */
    private String remarks;
    /**
     * 模板会议类型：1级联，2普通
     */
    @JsonIgnore
    private Integer type;

    /**
     * 是否开启直播
     */
    private volatile boolean streaming;


    private String  spokesmanId;

    /**
     * 直播地址
     */
    private volatile String streamingUrl;

    /** 直播urlList */
    private volatile List<String> streamUrlList = new ArrayList<>();
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
     * 演示会场
     */
    private String presentation;

    private String presentationId;
    /**
     * 会议开始时间
     */
    private Date startTime;

    /**
     * 会议开始时间
     */
    private Date endTime;
    /**
     * 会议所使用的模板
     */
    private String name;
    /**
     * 是否预约
     */
    private boolean isAppointment;
    /**
     * 主会场与会者
     */
    private volatile AttendeeSmc2 masterAttendee;
    /**
     * 所有级联子会议的主会场集合
     */
    private volatile List<AttendeeSmc2> masterAttendees = new ArrayList<>();

    /**
     * MCU自身充当的参会者
     */
    private volatile List<McuAttendeeSmc2> mcuAttendees = new ArrayList<>();
    /**
     * 預約會議類型
     */
    private String conferenceTimeType;

    private BusiHistoryConference historyConference;

    /**
     * 当前正在进行的参会者操作（轮询/选看/点名）
     */
    private volatile AttendeeOperation attendeeOperation;

    private volatile AttendeeOperation lastAttendeeOperation;

    private volatile DefaultViewOperation defaultViewOperation;
    private MultiPicInfoTalkReq multiPicInfoTalkReq;
    private int endReasonsType;
    private SyncInformation syncInformation;

    public DefaultViewOperation getDefaultViewOperation() {
        return defaultViewOperation;
    }

    public void setDefaultViewOperation(DefaultViewOperation defaultViewOperation) {
        this.defaultViewOperation = defaultViewOperation;
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

    public String getConferenceTimeType() {
        return conferenceTimeType;
    }

    public void setConferenceTimeType(String conferenceTimeType) {
        this.conferenceTimeType = conferenceTimeType;
    }

    private boolean cascade;
    private String version ;
    private String createUser;

    public Map<String, AttendeeSmc2> getUuidAttendeeMapByUri(String remoteParty)
    {
        return remotePartyAttendeesMap.get(remoteParty);
    }
    /**
     * 终端ID映射
     */
    @JsonIgnore
    private volatile Map<String, AttendeeSmc2> participantAttendeeAllMap = new ConcurrentHashMap<>();

    @Override
    public List<TerminalAttendeeSmc2> getLiveTerminals() {
        return liveTerminals;
    }

    /**
     * 带宽1,2,3,4,5,6M
     */
    private Integer bandwidth;
    /**
     * 预约会议类型:1:预约会议;2:即时会议
     */
    private Integer appointmentType;

    @Override
    public Integer getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Integer bandwidth) {
        this.bandwidth = bandwidth;
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


    public int getEndReasonsType() {
        return endReasonsType;
    }

    public void setEndReasonsType(int endReasonsType) {
        this.endReasonsType = endReasonsType;
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

    private volatile RemotePartyParticipantMapSmc remotePartyParticipantMapSmc = new RemotePartyParticipantMapSmc();



    public List<McuAttendeeSmc2> getFmeAttendees()
    {
        return fmeAttendees;
    }

    /**
     * FME自身充当的参会者
     */
    private volatile List<McuAttendeeSmc2> fmeAttendees = new ArrayList<>();

    public void setFmeAttendees(List<McuAttendeeSmc2> fmeAttendees)
    {
        this.fmeAttendees = fmeAttendees;
    }
    /**
     * 模板绑定的会议号
     */
    private String conferenceNumber;

    public synchronized void addAttendee(AttendeeSmc2 attendee)
    {
        attendee.setConferenceNumber(String.valueOf(conferenceNumber));
        attendee.setConferenceId(getId());
        if (attendee instanceof TerminalAttendeeSmc2) {
            TerminalAttendeeSmc2 terminalAttendee = (TerminalAttendeeSmc2) attendee;
            TerminalAttendeeSmc2 old = terminalAttendeeMap.get(terminalAttendee.getTerminalId());
            if (old != null) {
                old.syncTo(terminalAttendee);
            }
            terminalAttendeeMap.put(terminalAttendee.getTerminalId(), terminalAttendee);
        }
        if (this.attendees.contains(attendee)) {
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
    public void addAttendeeToTerminalAllMap(AttendeeSmc2 attendee) {
        if (attendee.getTerminalId() != null) {
            terminalAttendeeAllMap.put(attendee.getTerminalId(), attendee);
        }
    }

    public void addLiveTerminal(TerminalAttendeeSmc2 terminal)
    {
        this.liveTerminals.add(terminal);
        if (terminal.getSn() != null && terminal.getSn().length() > 0) {
            this.liveTerminalMap.put(terminal.getSn(), terminal);
        }
    }


    @Override
    public String getConferencePassword() {
        return conferencePassword;
    }

    public void setConferencePassword(String conferencePassword) {
        this.conferencePassword = conferencePassword;
    }

    @Override
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    /**
     * fcm集群调用，其它请勿调用
     *
     * @param oldRemoteParty
     * @param attendee
     */
    @Override
    public void updateAttendeeToRemotePartyMap(String oldRemoteParty, AttendeeSmc2 attendee) {
        if (StringUtils.hasText(oldRemoteParty)) {
            Map<String, AttendeeSmc2> uuidAttendeeMap = remotePartyAttendeesMap.get(oldRemoteParty);
            if (uuidAttendeeMap != null) {
                remotePartyAttendeesMap.remove(oldRemoteParty);
            }
        }
        if (StringUtils.hasText(attendee.getRemoteParty())) {
            remotePartyAttendeesMap.addAttendee(attendee);
        }
    }
    @JsonIgnore
    private final Object syncLock = new Object();

    private BusiMcuSmc2ConferenceAppointment conferenceAppointment;

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

    @Override
    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public void setMcuAttendeeRemoteParty(String attendeeIp)
    {
        this.fmeAttendeeRemoteParty = accessCode + "@" + attendeeIp;
    }

    /**
     * MCU终端ID映射
     */
    @JsonIgnore
    private volatile Map<Long, TerminalAttendeeSmc2> terminalAttendeeMap = new ConcurrentHashMap<>();

    /**
     * 级联方的参会者映射表，key为部门ID
     */
    private volatile Map<Long, List<AttendeeSmc2>> cascadeAttendeesMap = new ConcurrentHashMap<>();
    /**
     * 所有已配置的参会者映射key为remoteParty
     */
    @JsonIgnore
    private volatile Map<String, AttendeeSmc2> attendeeMap = new ConcurrentHashMap<>();

    public SmcParitipantsStateRep.ContentDTO getMasterSmcParticiPant() {
        return masterSmcParticiPant;
    }

    public void setMasterSmcParticiPant(SmcParitipantsStateRep.ContentDTO masterSmcParticiPant) {
        this.masterSmcParticiPant = masterSmcParticiPant;
    }

    public Map<String, Object> getMonitorParticipantMap() {
        return monitorParticipantMap;
    }

    public void setMonitorParticipantMap(Map<String, Object> monitorParticipantMap) {
        this.monitorParticipantMap = monitorParticipantMap;
    }

    /**
     * 添加地州主会场信息
     * @author lilinhai
     * @since 2021-02-20 16:34
     * @param masterAttendee void
     */
    public void addMasterAttendee(AttendeeSmc2 masterAttendee)
    {
        this.masterAttendees.add(masterAttendee);
        this.masterAttendeeIdSet.add(masterAttendee.getId());
    }

    /**
     * 所有与会者
     */
    private final Map<String, SmcParitipantsStateRep.ContentDTO > smcParticiPantMap = new ConcurrentHashMap<>();
    /**
     * 添加会议主导方的终端参会者
     * @param attendee
     */
    public synchronized void addParticipant(ParticipantRspDto attendee)
    {

        this.participants.add(attendee);
    }

    public void addAttendeeToIdMap(AttendeeSmc2 attendee)
    {
        this.attendeeMap.put(attendee.getId(), attendee);
    }

    public void addAttendeeToRemotePartyMap(AttendeeSmc2 attendee)
    {
        remotePartyAttendeesMap.addAttendee(attendee);
    }

    public void addParticipantToRemotePartyMap(SmcParitipantsStateRep.ContentDTO packTerminalParticipantRep)
    {
        remotePartyParticipantMapSmc.addAttendee(packTerminalParticipantRep);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public SmcConference getConference() {
        return conference;
    }

    public void setConference(SmcConference conference) {
        this.conference = conference;
    }

    public SmcMultiConferenceService getMultiConferenceService() {
        return multiConferenceService;
    }

    public void setMultiConferenceService(SmcMultiConferenceService multiConferenceService) {
        this.multiConferenceService = multiConferenceService;
    }

    public SmcConferenceTemplate.StreamServiceDTO getStreamService() {
        return streamService;
    }

    public void setStreamService(SmcConferenceTemplate.StreamServiceDTO streamService) {
        this.streamService = streamService;
    }

    public SmcConferenceTemplate.ConfPresetParamDTO getConfPresetParam() {
        return confPresetParam;
    }

    public void setConfPresetParam(SmcConferenceTemplate.ConfPresetParamDTO confPresetParam) {
        this.confPresetParam = confPresetParam;
    }

    public SmcConferenceTemplate.SubtitleServiceDTO getSubtitleService() {
        return subtitleService;
    }

    public void setSubtitleService(SmcConferenceTemplate.SubtitleServiceDTO subtitleService) {
        this.subtitleService = subtitleService;
    }

    public Object getSyncLock() {
        return syncLock;
    }

    @Override
    public List<AttendeeSmc2> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<AttendeeSmc2> attendees) {
        this.attendees = attendees;
    }

    public List<ParticipantRspDto> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantRspDto> participants) {
        this.participants = participants;
    }


    public DetailConference getDetailConference() {
        return detailConference;
    }

    public void setDetailConference(DetailConference detailConference) {
        this.detailConference = detailConference;
    }

    public List<SmcParitipantsStateRep.ContentDTO> getContent() {
        return content;
    }

    public void setContent(List<SmcParitipantsStateRep.ContentDTO> content) {
        this.content = content;
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

    @Override
    public List<AttendeeSmc2> getMasterAttendees() {
        return masterAttendees;
    }

    public void setMasterAttendees(List<AttendeeSmc2> masterAttendees) {
        this.masterAttendees = masterAttendees;
    }

    @Override
    public List<McuAttendeeSmc2> getMcuAttendees() {
        return mcuAttendees;
    }

    public void setMcuAttendees(List<McuAttendeeSmc2> mcuAttendees) {
        this.mcuAttendees = mcuAttendees;
    }

    @Override
    public AttendeeSmc2 getMasterAttendee() {
        return masterAttendee;
    }

    /**
     * 设置主会场
     * @author lilinhai
     * @since 2021-02-08 15:36
     * @param masterAttendee void
     */
    public synchronized void setMasterAttendee(AttendeeSmc2 masterAttendee)
    {
        Assert.notNull(masterAttendee, "【" + SysDeptCache.getInstance().get(masterAttendee.getDeptId()).getDeptName() + "】主会场不能为空！");
        if (this.masterAttendee != null)
        {
            if (this.masterAttendee.getDeptId() == deptId.longValue())
            {
                attendees.add(this.masterAttendee);
                Collections.sort(attendees);
            }
            else
            {
                if (this.masterAttendeeIdSet.contains(this.masterAttendee.getId()))
                {
                    this.masterAttendees.add(this.masterAttendee);
                    Collections.sort(this.masterAttendees);
                }
                else
                {
                    List<AttendeeSmc2> as = this.cascadeAttendeesMap.get(this.masterAttendee.getDeptId());
                    if (as != null)
                    {
                        as.add(this.masterAttendee);
                        Collections.sort(as);
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
                List<AttendeeSmc2> as = this.cascadeAttendeesMap.get(masterAttendee.getDeptId());
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

    public void addCascadeAttendee(AttendeeSmc2 attendee)
    {
        List<AttendeeSmc2> cascadeAttendees = cascadeAttendeesMap.get(attendee.getDeptId());
        if (cascadeAttendees == null)
        {
            cascadeAttendees = new ArrayList<>();
            cascadeAttendeesMap.put(attendee.getDeptId(), cascadeAttendees);
        }
        cascadeAttendees.add(attendee);

        if (attendee instanceof TerminalAttendeeSmc2)
        {
            TerminalAttendeeSmc2 terminalAttendee = (TerminalAttendeeSmc2) attendee;
            terminalAttendeeMap.put(terminalAttendee.getTerminalId(), terminalAttendee);
        }
    }

    @Override
    public Map<Long, List<AttendeeSmc2>> getCascadeAttendeesMap()
    {
        return cascadeAttendeesMap;
    }

    public ParticipantRspDto getMasterParticipant() {
        return masterParticipant;
    }

    public void setMasterParticipant(ParticipantRspDto masterParticipant) {
        this.masterParticipant = masterParticipant;
    }

    public void addContent(SmcParitipantsStateRep.ContentDTO packTerminalParticipantRep) {
        if(this.content.contains(packTerminalParticipantRep)){
            this.content.remove(packTerminalParticipantRep);
        }
        this.content.add(packTerminalParticipantRep);
        addParticipantToIdMap(packTerminalParticipantRep);
        addParticipantToRemotePartyMap(packTerminalParticipantRep);
    }
    /**
     * 是否锁定
     */
    private volatile boolean locked;

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void addParticipantToIdMap(SmcParitipantsStateRep.ContentDTO attendee)
    {
        this.smcParticiPantMap.put(attendee.getGeneralParam().getId(), attendee);
    }

    public Integer getSmc2TemplateId() {
        return smc2TemplateId;
    }

    public void setSmc2TemplateId(Integer smc2TemplateId) {
        this.smc2TemplateId = smc2TemplateId;
    }

    @Override
    public BusiHistoryConference getHistoryConference() {
        return historyConference;
    }

    public void setHistoryConference(BusiHistoryConference historyConference) {
        this.historyConference = historyConference;
    }

    @Override
    public AttendeeSmc2 getAttendeeByTerminalId(Long terminalId) {
        if (terminalId != null) {
            return terminalAttendeeAllMap.get(terminalId);
        }
        return null;
    }

    public String getCoSpaceId() {
        String coSpaceId = this.smc2TemplateId +this.getTenantId()+ this.getAccessCode();
        if (this.templateCreateTime != null) {
            coSpaceId += "-" + this.templateCreateTime.getTime();
        }
        if(this.conference!=null){
            coSpaceId+="-"+this.getSmc2conferenceId();
        }
        return coSpaceId;
    }

    public SmcParitipantsStateRep.ContentDTO  getParticipant(String participantId) {
        return   smcParticiPantMap.get(participantId);
    }

    public Smc2ConferenceContext(Smc2Bridge smc2Bridge) {
        this.smc2Bridge = smc2Bridge;
        this.multiConferenceService=new SmcMultiConferenceService();
        ConferenceState conferenceState=new ConferenceState();
        ConferenceUiParam conferenceUiParam=new ConferenceUiParam();
        DetailConference detailConference = new DetailConference();
        detailConference.setConferenceState(conferenceState);
        detailConference.setConferenceUiParam(conferenceUiParam);
        this.detailConference=detailConference;
        this.version="2.0";
        this.setMcuTypeObject(McuType.SMC2);
    }
    public Smc2ConferenceContext( ) {
        this.version="2.0";
    }

    public Date getTemplateCreateTime() {
        return templateCreateTime;
    }

    public void setTemplateCreateTime(Date templateCreateTime) {
        this.templateCreateTime = templateCreateTime;
    }

    public Smc2Bridge getSmc2Bridge() {
        return smc2Bridge;
    }

    public void setSmc2Bridge(Smc2Bridge smc2Bridge) {
        this.smc2Bridge = smc2Bridge;
    }

    public void removeByParticipantId(String id) {
        SmcParitipantsStateRep.ContentDTO  contentDTO = this.smcParticiPantMap.remove(id);
        if(contentDTO!=null){
            this.content.remove(contentDTO);
            this.masterAttendees.remove(contentDTO);
        }

        this.remotePartyParticipantMapSmc.removeParticipantByRemotePartyAndUuid(contentDTO.getGeneralParam().getUri(),id);
    }

    private MultiPicPollRequest chairmanMultiPicPollRequest;

    private MultiPicPollRequest multiPicPollRequest;
    /**
     * 多画面
     */
    private MultiPicInfoReq multiPicInfoReq;

    public MultiPicPollRequest getMultiPicPollRequest() {
        return multiPicPollRequest;
    }

    public void setMultiPicPollRequest(MultiPicPollRequest multiPicPollRequest) {
        this.multiPicPollRequest = multiPicPollRequest;
    }

    public MultiPicPollRequest getChairmanMultiPicPollRequest() {
        return chairmanMultiPicPollRequest;
    }

    public void setChairmanMultiPicPollRequest(MultiPicPollRequest chairmanMultiPicPollRequest) {
        this.chairmanMultiPicPollRequest = chairmanMultiPicPollRequest;
    }

    public MultiPicInfoReq getMultiPicInfoReq() {
        return multiPicInfoReq;
    }

    public void setMultiPicInfoReq(MultiPicInfoReq multiPicInfoReq) {
        this.multiPicInfoReq = multiPicInfoReq;
    }

    public String getParticiPantIdBySiteUri(String siteUri){
        if(Strings.isBlank(siteUri)){
            return null;
        }
        Map<String, AttendeeSmc2> uuidAttendeeMapByUri =getUuidAttendeeMapByUri(siteUri);
        if(uuidAttendeeMapByUri!=null){
            for (Map.Entry<String, AttendeeSmc2> stringContentDTOEntry : uuidAttendeeMapByUri.entrySet()) {
                AttendeeSmc2 value = stringContentDTOEntry.getValue();
                if(value!=null){
                    return value.getId();
                }

            }
        }
        return null;
    }

    public List<String>  getAllUri(){
        List<String> urls=new ArrayList<>();
        List<SmcParitipantsStateRep.ContentDTO> contents = this.getContent();
        if(!CollectionUtils.isEmpty(contents)){
            for (SmcParitipantsStateRep.ContentDTO content : contents) {
                String uri = content.getGeneralParam().getUri();
                urls.add(uri);
            }
        }
        return urls;
    }

    public ChairManSmc2PollingThread getChairManSmc2PollingThread() {
        return chairManSmc2PollingThread;
    }

    public synchronized void setChairManSmc2PollingThread(ChairManSmc2PollingThread chairManSmc2PollingThread) {
        this.chairManSmc2PollingThread = chairManSmc2PollingThread;
    }

    public List<SmcParitipantsStateRep.ContentDTO> getParticipantOrderList() {
        return participantOrderList;
    }

    public void setParticipantOrderList(List<SmcParitipantsStateRep.ContentDTO> participantOrderList) {
        this.participantOrderList = participantOrderList;
    }

    public SyncInformation getSyncInformation() {
        return syncInformation;
    }

    public void setSyncInformation(SyncInformation syncInformation) {
        this.syncInformation = syncInformation;
    }

    /**
     * 所有地州的主会场map映射（用于做逻辑判断，不暴露给前端），key：attendeeId
     */
    private volatile Set<String> masterAttendeeIdSet = new HashSet<>();

    /**
     * 部门权重
     */
    private volatile List<DeptWeight> deptWeights = new ArrayList<>();

    /**
     * 直播终端列表
     */
    @JsonIgnore
    private volatile List<TerminalAttendeeSmc2> liveTerminals = new ArrayList<>();

    /**
     * 直播终端Map key:sn
     */
    private volatile Map<String, TerminalAttendeeSmc2> liveTerminalMap = new ConcurrentHashMap();

    @JsonIgnore
    private volatile RemotePartyAttendeesMapSmc2 remotePartyAttendeesMap = new RemotePartyAttendeesMapSmc2();




    /**
     * 终端ID映射
     */
    @JsonIgnore
    private volatile Map<Long, AttendeeSmc2> terminalAttendeeAllMap = new ConcurrentHashMap<>();



    /**
     * 参会者重呼开始时间（呼叫成功后或呼叫超过30分钟后，会清除该记录）
     */
    @JsonIgnore
    private volatile Map<String, Long> recallAttendeeBeginTimeMap = new ConcurrentHashMap<>();



    public String getSmc2conferenceId() {
        return smc2conferenceId;
    }

    public void setSmc2conferenceId(String smc2conferenceId) {
        this.smc2conferenceId = smc2conferenceId;
    }

    public synchronized AttendeeSmc2 removeAttendeeById(String id)
    {
        AttendeeSmc2 attendee = this.attendeeMap.remove(id);
        if (attendee != null)
        {
            attendees.remove(attendee);

            this.masterAttendeeIdSet.remove(id);
            this.masterAttendees.remove(attendee);

            List<AttendeeSmc2> as = cascadeAttendeesMap.get(attendee.getDeptId());
            if (as != null)
            {
                as.remove(attendee);
            }

            if (attendee instanceof TerminalAttendeeSmc2) {
                TerminalAttendeeSmc2 ta = (TerminalAttendeeSmc2) attendee;
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

    public Long removeRecallAttendeeBeginTime(AttendeeSmc2 attendee)
    {
        return recallAttendeeBeginTimeMap.remove(attendee.getId());
    }

    public void removeAttendeeFromTerminalAllMap(AttendeeSmc2 attendee) {
        if (attendee.getTerminalId() != null) {
            terminalAttendeeAllMap.remove(attendee.getTerminalId());
        }
    }

    public List<DeptWeight> getDeptWeights() {
        return deptWeights;
    }

    public void setDeptWeights(List<DeptWeight> deptWeights) {
        this.deptWeights = deptWeights;
    }

    public RemotePartyParticipantMapSmc getRemotePartyParticipantMapSmc() {
        return remotePartyParticipantMapSmc;
    }

    public void setRemotePartyParticipantMapSmc(RemotePartyParticipantMapSmc remotePartyParticipantMapSmc) {
        this.remotePartyParticipantMapSmc = remotePartyParticipantMapSmc;
    }

    @Override
    public Map<Long, TerminalAttendeeSmc2> getTerminalAttendeeMap() {
        return terminalAttendeeMap;
    }

    public void setTerminalAttendeeMap(Map<Long, TerminalAttendeeSmc2> terminalAttendeeMap) {
        this.terminalAttendeeMap = terminalAttendeeMap;
    }

    public void setCascadeAttendeesMap(Map<Long, List<AttendeeSmc2>> cascadeAttendeesMap) {
        this.cascadeAttendeesMap = cascadeAttendeesMap;
    }

    public Map<String, AttendeeSmc2> getAttendeeMap() {
        return attendeeMap;
    }

    public void setAttendeeMap(Map<String, AttendeeSmc2> attendeeMap) {
        this.attendeeMap = attendeeMap;
    }

    public Map<String, SmcParitipantsStateRep.ContentDTO> getSmcParticiPantMap() {
        return smcParticiPantMap;
    }

    public Set<String> getMasterAttendeeIdSet() {
        return masterAttendeeIdSet;
    }

    public void setMasterAttendeeIdSet(Set<String> masterAttendeeIdSet) {
        this.masterAttendeeIdSet = masterAttendeeIdSet;
    }

    @Override
    public BusiMcuSmc2ConferenceAppointment getConferenceAppointment()
    {
        return conferenceAppointment;
    }

    /**
     * <p>Set Method   :   conferenceAppointment BusiConferenceAppointment</p>
     * @param conferenceAppointment
     */
    public void setConferenceAppointment(BusiMcuSmc2ConferenceAppointment conferenceAppointment)
    {
        this.conferenceAppointment = conferenceAppointment;
    }



    /**
     * 是否开启讨论（所有会场权重相等，同时开启语音激励）
     */
    private volatile boolean discuss;

    /**
     * 是否开启录制
     */
    private volatile boolean recorded;

    /** 是否启用会议时长 */
    private Integer durationEnabled;

    /** 会议时长单位分钟 */
    private Integer durationTime;

    private Boolean enableUnmuteByGuest=Boolean.TRUE;

    private Boolean enableSiteNameEditByGuest=Boolean.TRUE;;

    private Boolean directing=Boolean.FALSE;;

    private Boolean enableVoiceActive=Boolean.FALSE;;

    private volatile String broadId;
    private volatile Boolean secretTalk;
    @Override
    public String getMcuCallIp() {
        return smc2Bridge.getBusiSmc2().getScUrl();
    }

    public boolean isDiscuss() {
        return discuss;
    }

    public void setDiscuss(boolean discuss) {
        this.discuss = discuss;
    }

    public boolean isRecorded() {
        return recorded;
    }

    public void setRecorded(boolean recorded) {
        this.recorded = recorded;
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
    public String getConferenceNumber() {
        return conferenceNumber;
    }

    @Override
    public void setConferenceNumber(String conferenceNumber) {
        this.conferenceNumber = conferenceNumber;
    }

    public boolean isAutoCallTerminal() {
        return isAutoCallTerminal;
    }

    public void setAutoCallTerminal(boolean autoCallTerminal) {
        isAutoCallTerminal = autoCallTerminal;
    }

    public Integer getBusinessFieldType() {
        return businessFieldType;
    }

    public void setBusinessFieldType(Integer businessFieldType) {
        this.businessFieldType = businessFieldType;
    }

    @Override
    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public MultiPicInfoTalkReq getMultiPicInfoTalkReq() {
        return multiPicInfoTalkReq;
    }

    public void setMultiPicInfoTalkReq(MultiPicInfoTalkReq multiPicInfoTalkReq) {
        this.multiPicInfoTalkReq = multiPicInfoTalkReq;
    }

    /**
     * <p>Set Method   :   deptWeights List<DeptWeight></p>
     */
    public void addDeptWeight(DeptWeight deptWeight)
    {
        this.deptWeights.add(deptWeight);
    }

    public Map<String, AttendeeSmc2> getParticipantAttendeeAllMap() {
        return participantAttendeeAllMap;
    }

    public void setParticipantAttendeeAllMap(Map<String, AttendeeSmc2> participantAttendeeAllMap) {
        this.participantAttendeeAllMap = participantAttendeeAllMap;
    }

    public synchronized void clearMasterAttendee()
    {
        this.masterAttendee = null;
    }


    public void addFmeAttendee(McuAttendeeSmc2 fmeAttendee)
    {
        fmeAttendee.setConferenceNumber(String.valueOf(conferenceNumber));
        fmeAttendee.setDeptId(deptId);
        this.fmeAttendees.add(fmeAttendee);
        this.attendeeMap.put(fmeAttendee.getId(), fmeAttendee);
        if (fmeAttendee.getRemoteParty() != null)
        {
            this.attendeeMap.put(fmeAttendee.getRemoteParty(), fmeAttendee);
        }
    }

    /**
     * 添加FME类型的终端参会者
     *
     * @param attendee
     */
    public void addMcuAttendee(McuAttendeeSmc2 attendee) {
        attendee.setConferenceNumber(conferenceNumber);
        attendee.setContextKey(getContextKey());
        attendee.setDeptId(deptId);
        this.mcuAttendees.add(attendee);
        this.addAttendee(attendee);
    }

    /**
     * 删除MCU类型参会终端
     *
     * @param mcuAttendee
     */
    public void removeMcuAttendee(McuAttendeeSmc2 mcuAttendee) {
        this.mcuAttendees.remove(mcuAttendee);
        this.removeAttendeeById(mcuAttendee.getId());
    }

    public void removeMasterAttendee(String id) {
        if(this.masterAttendee!=null){
            if(Objects.equals(id,this.masterAttendee.getId())){
                this.removeAttendeeById(id);
                this.masterAttendee=null;
            }
        }
    }

    @Override
    public AttendeeSmc2 getAttendeeById(String attendeeId)
    {
        return attendeeMap.get(attendeeId);
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }

    @Override
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq.MultiPicInfoDTO getMultiPicInfo() {
        return multiPicInfo;
    }

    public void setMultiPicInfo(com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq.MultiPicInfoDTO multiPicInfo) {
        this.multiPicInfo = multiPicInfo;
    }

    public AttendeeSmc2 getAttendeeBySmc2Id(String participantId) {
        return this.participantAttendeeAllMap.get(participantId);
    }

    public boolean isStartRound() {
        return startRound;
    }

    public void setStartRound(boolean startRound) {
        this.startRound = startRound;
    }

    public String getChairmanId() {
        return chairmanId;
    }

    public void setChairmanId(String chairmanId) {
        this.chairmanId = chairmanId;
    }

    public String getLockPresenterId() {
        return lockPresenterId;
    }

    public void setLockPresenterId(String lockPresenterId) {
        this.lockPresenterId = lockPresenterId;
    }

    public Boolean getEnableUnmuteByGuest() {
        return enableUnmuteByGuest;
    }

    public void setEnableUnmuteByGuest(Boolean enableUnmuteByGuest) {
        this.enableUnmuteByGuest = enableUnmuteByGuest;
    }

    public Boolean getEnableSiteNameEditByGuest() {
        return enableSiteNameEditByGuest;
    }

    public void setEnableSiteNameEditByGuest(Boolean enableSiteNameEditByGuest) {
        this.enableSiteNameEditByGuest = enableSiteNameEditByGuest;
    }

    public Boolean getDirecting() {
        return directing;
    }

    public void setDirecting(Boolean directing) {
        this.directing = directing;
    }

    public Boolean getEnableVoiceActive() {
        return enableVoiceActive;
    }

    public void setEnableVoiceActive(Boolean enableVoiceActive) {
        this.enableVoiceActive = enableVoiceActive;
    }


    public String getBroadId() {
        return broadId;
    }

    public void setBroadId(String broadId) {
        this.broadId = broadId;
    }

    public String getSpokesmanId() {
        return spokesmanId;
    }

    public void setSpokesmanId(String spokesmanId) {
        this.spokesmanId = spokesmanId;
    }

    public Boolean getSecretTalk() {
        return secretTalk;
    }

    public void setSecretTalk(Boolean secretTalk) {
        this.secretTalk = secretTalk;
    }

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    public String getPresentationId() {
        return presentationId;
    }

    public void setPresentationId(String presentationId) {
        this.presentationId = presentationId;
    }

    private volatile String presentAttendeeId = null;

    @Override
    public String getPresentAttendeeId() {
        return presentAttendeeId;
    }

    public void setPresentAttendeeId(String presentAttendeeId) {
        this.presentAttendeeId = presentAttendeeId;
    }

    public void defaultAttendeeOperation(){

        if(Strings.isNotBlank(chairmanId)){
            setLastAttendeeOperation(this.getAttendeeOperation());
            ChangeMasterAttendeeOperation changeMasterAttendeeOperation = new ChangeMasterAttendeeOperation(this, masterAttendee);
            setAttendeeOperation(changeMasterAttendeeOperation);
            changeMasterAttendeeOperation.operate();
        }else {
            MultiPicInfoReq.MultiPicInfoDTO multiPicInfo =getMultiPicInfo();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("multiPicInfo",multiPicInfo);
            jsonObject.put("conferenceId",getSmc2conferenceId());
            jsonObject.put("broadcast",false);
            setLastAttendeeOperation(this.getAttendeeOperation());
            DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(this,jsonObject);
            setAttendeeOperation(defaultAttendeeOperation);
            defaultAttendeeOperation.operate();
        }
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

}


