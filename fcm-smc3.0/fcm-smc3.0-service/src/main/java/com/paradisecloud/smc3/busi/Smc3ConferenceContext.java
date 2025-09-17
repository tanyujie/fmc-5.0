package com.paradisecloud.smc3.busi;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3ConferenceAppointment;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.smc3.busi.attende.*;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.busi.cascade.Cascade;
import com.paradisecloud.smc3.busi.cascade.UpCascade;
import com.paradisecloud.smc3.busi.operation.AttendeeOperation;
import com.paradisecloud.smc3.busi.operation.ChangeMasterAttendeeOperation;
import com.paradisecloud.smc3.busi.operation.DefaultViewOperation;
import com.paradisecloud.smc3.busi.operation.PollingAttendeeOperation;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.smc3.invoker.ConferenceState;
import com.paradisecloud.smc3.model.ConstAPI;
import com.paradisecloud.smc3.model.PollOperateTypeDto;
import com.paradisecloud.smc3.model.SmcConferenceTemplate;
import com.paradisecloud.smc3.model.request.*;
import com.paradisecloud.system.model.SysDeptCache;
import io.jsonwebtoken.lang.Assert;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 活跃会议上下文实体类
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-02 15:00
 */
public class Smc3ConferenceContext extends BaseConferenceContext implements Serializable {

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     *
     * @since 2021-02-02 14:04
     */
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private final Object syncLock = new Object();
    @JsonIgnore
    private List<TemplateNode> cascadeTree;
    private List<ConferenceNode> cascadeConferenceTree;

    private volatile Boolean subscribe;
    private volatile Boolean secretTalk;
    private List<String> changeParticipants = new ArrayList<>();
    private String category;
    private String smc3conferenceId;
    private String chairmanId;
    private String lockPresenterId;
    private String groupId;
    private String parentConferenceId;
    @JsonIgnore
    private String parentConferenceContextKey;
    private Long cascadeLocalTemplateId;
    private String monitorNumber;
    private String monitorId;
    @JsonIgnore
    private String cospaceId;
    /**
     * 会议的ID（UUID）coSpaceId
     */
    private String id;

    private TextTipsSetting banner;

    private TextTipsSetting caption;

    /**
     * 模板会议的创建时间
     */
    private Date templateCreateTime;

    /**
     * 会议所使用的模板
     */
    private String name;

    /**
     * 会议备注
     */
    private String remarks;

    /**
     * 部门ID
     */
    private Long deptId;

    private MultiPicInfoTalkReq multiPicInfoTalkReq;


    private SmcConferenceTemplate.ConfPresetParamDTO confPresetParamDTO;
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

    /**
     * 直播urlList
     */
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
    private volatile AttendeeSmc3 streamingAttendee;

    /**
     * 允许所有人静音自己
     */
    private volatile boolean allowAllMuteSelf;
    /**
     * 允许所有人静音自己
     */
    private volatile boolean quiet;
    /**
     * 允许辅流控制
     */
    private volatile boolean allowAllPresentationContribution;

    private Boolean presentationContributionAllowed;


    private Boolean enableUnmuteByGuest=Boolean.TRUE;

    private Boolean enableSiteNameEditByGuest=Boolean.TRUE;;

    private Boolean directing=Boolean.FALSE;;

    private Boolean enableVoiceActive=Boolean.FALSE;;
    /**
     * 新加入用户静音
     */
    private volatile boolean joinAudioMuteOverride;

    /**
     * 是否开启讨论（所有会场权重相等，同时开启语音激励）
     */
    private volatile boolean discuss;

    /**********************************************************************************************************/

    /**
     * 主会场与会者
     */
    private volatile AttendeeSmc3 masterAttendee;

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

    /**********************************************************************************************************/

    /**
     * 会议是否开始
     */
    private volatile boolean isStart;

    /**
     * 是否预约
     */
    private boolean isAppointment;

    /**
     * 预约会议类型:1:预约会议;2:即时会议
     */
    private Integer appointmentType;

    /**
     * 预约任务的ID
     */
    private BusiMcuSmc3ConferenceAppointment conferenceAppointment;

    /**
     * 历史记录
     */
    @JsonIgnore
    private BusiHistoryConference historyConference;

    /**
     * 业务领域类型
     */
    private Integer businessFieldType;

    /**
     * 业务属性
     */
    private Map<String, Object> businessProperties;

    /**
     * 是否结束
     */
    @JsonIgnore
    private volatile boolean isEnd;

    /**
     * 会议开始时间
     */
    private Date startTime;
    /**
     * 会议最大人数
     */
    private Integer maxParticipantNum;

    /**
     * 会议结束时间
     */
    @JsonIgnore
    private Date endTime;

    @JsonIgnore
    private Smc3Bridge smc3Bridge;

    /**
     * 此会议自身作为FME参会的remoteParty
     */
    @JsonIgnore
    private String fmeAttendeeRemoteParty;

    /**
     * 模板会议的ID，标记出是哪个模板发起的，好从模板点击进入会议进行关联
     */
    private Long templateConferenceId;

    /**
     * 是否自动拉终端
     */
    private boolean isAutoCallTerminal;
    /**
     * 是否下级级联
     */
    private boolean isDownCascade;
    /**
     * 会议主导方的参会者
     */
    private volatile List<AttendeeSmc3> attendees = new ArrayList<>();

    private volatile List<AttendeeSmc3> cascadeAttendees = new ArrayList<>();

    private volatile List<AttendeeSmc3> displayAttendees = new ArrayList<>();
    /**
     * 所有级联子会议的主会场集合
     */
    private volatile List<AttendeeSmc3> masterAttendees = new ArrayList<>();

    /**
     * FME自身充当的参会者
     */
    private volatile List<McuAttendeeSmc3> fmeAttendees = new ArrayList<>();

    /**
     * 部门权重
     */
    private volatile List<DeptWeight> deptWeights = new ArrayList<>();

    /**
     * 级联方的参会者映射表，key为部门ID
     */
    private volatile Map<Long, List<AttendeeSmc3>> cascadeAttendeesMap = new ConcurrentHashMap<>();

    /**
     * 所有地州的主会场map映射（用于做逻辑判断，不暴露给前端），key：attendeeId
     */
    private volatile Set<String> masterAttendeeIdSet = new HashSet<>();

    /**
     * 所有已配置的参会者映射key为remoteParty
     */
    @JsonIgnore
    private volatile Map<String, AttendeeSmc3> attendeeMap = new ConcurrentHashMap<>();

    /**
     * 直播终端列表
     */
    @JsonIgnore
    private volatile List<TerminalAttendeeSmc3> liveTerminals = new ArrayList<>();

    /**
     * 直播终端Map key:sn
     */
    private volatile Map<String, TerminalAttendeeSmc3> liveTerminalMap = new ConcurrentHashMap();

    @JsonIgnore
    private volatile RemotePartyAttendeesMap remotePartyAttendeesMap = new RemotePartyAttendeesMap();


    /**
     * FME终端ID映射
     */
    @JsonIgnore
    private volatile Map<Long, TerminalAttendeeSmc3> terminalAttendeeMap = new ConcurrentHashMap<>();

    /**
     * 终端ID映射
     */
    @JsonIgnore
    private volatile Map<Long, AttendeeSmc3> terminalAttendeeAllMap = new ConcurrentHashMap<>();

    /**
     * 终端ID映射
     */
    @JsonIgnore
    private volatile Map<String, AttendeeSmc3> participantAttendeeAllMap = new ConcurrentHashMap<>();

    /**
     * 参会者重呼开始时间（呼叫成功后或呼叫超过30分钟后，会清除该记录）
     */
    @JsonIgnore
    private volatile Map<String, Long> recallAttendeeBeginTimeMap = new ConcurrentHashMap<>();

    private MultiPicPollRequest multiPicPollRequest;

    private MultiPicPollRequest chairmanMultiPicPollRequest;

    private ConferenceState.StateDTO.MultiPicInfoDTO multiPicInfo;

    /**
     * 下级级联信息
     */
    @JsonIgnore
    private Cascade cascade;

    /**
     * 上级级联信息
     */
    @JsonIgnore
    private UpCascade upCascade;

    private String smcTemplateId;

    /**
     * 是否启用会议时长
     */
    private Integer durationEnabled;

    /**
     * 会议时长单位分钟
     */
    private Integer durationTime;

    /**
     * 是否自动创建直播URL 1自动，2手动
     */
    private Integer isAutoCreateStreamUrl;

    /**
     * 主持人(用户id)
     */
    private Long presenter;

    /**
     * 共享材料
     */
    private String presenterId;

    /**
     * 共享材料
     */
    private AttendeeSmc3 presenterAttendee;

    /**
     * 会议创建者用户id
     */
    private Long createUserId;

    /**
     * 会议中观看直播的终端数
     */
    private Integer liveTerminalCount;

    private volatile Map<String, Integer> resourcesSnapshotCount = new ConcurrentHashMap<>();

    private volatile Map<String, List<AttendeeSmc3>> resourcesSnapshotAttendee = new ConcurrentHashMap<>();

    private volatile boolean startRound;
    /**
     * 参会者映射表，key为terminalId,value为终端绑定的userId
     */
    @JsonIgnore
    private volatile Map<Long, Long> terminalUserMap = new ConcurrentHashMap<>();
    /**
     * 租户ID
     */
    private volatile String tenantId = "";
    // 支持的参数，分屏，点名，选看，对话，广播，单视角等
    private boolean supportRollCall = true;
    private boolean supportSplitScreen = true;
    private boolean supportPolling = true;
    private boolean supportChooseSee = true;
    private boolean supportTalk = true;
    private boolean supportBroadcast = true;
    private boolean singleView = false;
    /**
     * 静音类型 0:不静音 1:静音
     */
    private Integer muteType = 1;
    /**
     * 静音状态：1：观众静音 2：全体静音
     */
    private volatile Integer muteStatus = 2;
    private int endReasonsType;
    private SyncInformation syncInformation;
    /**
     * FME自身充当的参会者
     */
    private volatile List<McuAttendeeSmc3> mcuAttendees = new ArrayList<>();
    private String chairmanPassword;
    private String guestPassword;

    public Smc3ConferenceContext(Smc3Bridge smc3Bridge) {
        this.smc3Bridge = smc3Bridge;
        this.setMcuTypeObject(McuType.SMC3);
    }

    public boolean isStartRound() {
        return startRound;
    }

    public void setStartRound(boolean startRound) {
        this.startRound = startRound;
    }

    public Map<String, Integer> getResourcesSnapshotCount() {
        return resourcesSnapshotCount;
    }

    public Map<String, List<AttendeeSmc3>> getResourcesSnapshotAttendee() {
        return resourcesSnapshotAttendee;
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

    public Map<String, AttendeeSmc3> getParticipantAttendeeAllMap() {
        return participantAttendeeAllMap;
    }

    public void setParticipantAttendeeAllMap(Map<String, AttendeeSmc3> participantAttendeeAllMap) {
        this.participantAttendeeAllMap = participantAttendeeAllMap;
    }

    /**
     * <p>Get Method   :   name String</p>
     *
     * @return name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * <p>Set Method   :   name String</p>
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Get Method   :   remarks String</p>
     *
     * @return remarks
     */
    @Override
    public String getRemarks() {
        return remarks;
    }

    /**
     * <p>Set Method   :   remarks String</p>
     *
     * @param remarks
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * <p>Get Method   :   deptId Long</p>
     *
     * @return deptId
     */
    @Override
    public Long getDeptId() {
        return deptId;
    }

    /**
     * <p>Set Method   :   deptId Long</p>
     *
     * @param deptId
     */
    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    /**
     * <p>Get Method   :   bandwidth Integer</p>
     *
     * @return bandwidth
     */
    @Override
    public Integer getBandwidth() {
        return bandwidth;
    }

    /**
     * <p>Set Method   :   bandwidth Integer</p>
     *
     * @param bandwidth
     */
    public void setBandwidth(Integer bandwidth) {
        this.bandwidth = bandwidth;
    }

    /**
     * <p>Get Method   :   conferenceNumber Long</p>
     *
     * @return conferenceNumber
     */
    @Override
    public String getConferenceNumber() {
        return conferenceNumber;
    }

    /**
     * <p>Set Method   :   conferenceNumber Long</p>
     *
     * @param conferenceNumber
     */
    @Override
    public void setConferenceNumber(String conferenceNumber) {
        this.conferenceNumber = conferenceNumber;
    }

    /**
     * <p>Get Method   :   type Integer</p>
     *
     * @return type
     */
    public Integer getType() {
        return type;
    }

    /**
     * <p>Set Method   :   type Integer</p>
     *
     * @param type
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * <p>Get Method   :   attendees List<TerminalAttendee></p>
     *
     * @return attendees
     */
    @Override
    public List<AttendeeSmc3> getAttendees() {
        return attendees;
    }

    /**
     * <p>Set Method   :   attendees List<TerminalAttendee></p>
     *
     * @param attendees
     */
    public void setAttendees(List<AttendeeSmc3> attendees) {
        this.attendees = attendees;
    }

    public AttendeeCountingStatistics getAttendeeCountingStatistics() {
        return new AttendeeCountingStatistics(this);
    }

    /**
     * 添加会议主导方的终端参会者
     *
     * @param attendee
     */
    public synchronized void addAttendee(AttendeeSmc3 attendee) {
        attendee.setConferenceNumber(String.valueOf(conferenceNumber));
        attendee.setConferenceId(getId());
        if (attendee instanceof TerminalAttendeeSmc3) {
            TerminalAttendeeSmc3 terminalAttendee = (TerminalAttendeeSmc3) attendee;
            TerminalAttendeeSmc3 old = terminalAttendeeMap.get(terminalAttendee.getTerminalId());
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


        if(Objects.equals(ConstAPI.CASCADE,this.getCategory())){
            if(Strings.isNotBlank(this.parentConferenceContextKey)){
                Smc3ConferenceContext smc3ConferenceContext = Smc3ConferenceContextCache.getInstance().get(this.parentConferenceContextKey);
                smc3ConferenceContext.addCascadeAttendeeNew(attendee);
            }
        }
    }


    public synchronized void addCascadeAttendeeNew(AttendeeSmc3 attendee) {
        if (attendee instanceof TerminalAttendeeSmc3) {
            TerminalAttendeeSmc3 terminalAttendee = (TerminalAttendeeSmc3) attendee;
            TerminalAttendeeSmc3 old = terminalAttendeeMap.get(terminalAttendee.getTerminalId());
            if (old != null) {
                old.syncTo(terminalAttendee);
            }
            terminalAttendeeMap.put(terminalAttendee.getTerminalId(), terminalAttendee);
        }
        if (this.cascadeAttendees.contains(attendee)) {
            this.cascadeAttendees.remove(attendee);
        }
        this.cascadeAttendees.add(attendee);

        addAttendeeToIdMap(attendee);
        addAttendeeToRemotePartyMap(attendee);
        addAttendeeToTerminalAllMap(attendee);
        if (Strings.isNotBlank((attendee.getParticipantUuid()))){
            participantAttendeeAllMap.put(attendee.getParticipantUuid(), attendee);
        }

        if(Objects.equals(ConstAPI.CASCADE,this.getCategory())){
            if(Strings.isNotBlank(this.parentConferenceContextKey)){
                Smc3ConferenceContext smc3ConferenceContext = Smc3ConferenceContextCache.getInstance().get(this.parentConferenceContextKey);
                smc3ConferenceContext.addCascadeAttendeeNew(attendee);
            }
        }
    }

    public void addAttendeeToIdMap(AttendeeSmc3 attendee) {
        this.attendeeMap.put(attendee.getId(), attendee);
    }

    public void addAttendeeToRemotePartyMap(AttendeeSmc3 attendee) {
        remotePartyAttendeesMap.addAttendee(attendee);

    }

    public RemotePartyAttendeesMap getRemotePartyAttendeesMap() {
        return remotePartyAttendeesMap;
    }

    public void addAttendeeToTerminalAllMap(AttendeeSmc3 attendee) {
        if (attendee.getTerminalId() != null) {
            terminalAttendeeAllMap.put(attendee.getTerminalId(), attendee);
        }
    }

    public void removeAttendeeFromTerminalAllMap(AttendeeSmc3 attendee) {
        if (attendee.getTerminalId() != null) {
            terminalAttendeeAllMap.remove(attendee.getTerminalId());
        }
    }

    @Override
    public AttendeeSmc3 getAttendeeByTerminalId(Long terminalId) {
        if (terminalId != null) {
            return terminalAttendeeAllMap.get(terminalId);
        }
        return null;
    }


    @Override
    public void updateAttendeeToRemotePartyMap(String oldRemoteParty, BaseAttendee attendee) {
        super.updateAttendeeToRemotePartyMap(oldRemoteParty, attendee);
        if(attendee instanceof  AttendeeSmc3){
            AttendeeSmc3 attendeeSmc3=(AttendeeSmc3)attendee;
            if (StringUtils.hasText(oldRemoteParty)) {
                Map<String, AttendeeSmc3> uuidAttendeeMap = remotePartyAttendeesMap.get(oldRemoteParty);
                if (uuidAttendeeMap != null) {
                    remotePartyAttendeesMap.remove(oldRemoteParty);
                }
            }
            if (StringUtils.hasText(attendeeSmc3.getRemoteParty())) {
                remotePartyAttendeesMap.addAttendee(attendeeSmc3);
            }
        }

    }

    /**
     * <p>Get Method   :   masterAttendee Attendee</p>
     *
     * @return masterAttendee
     */
    @Override
    public AttendeeSmc3 getMasterAttendee() {
        return masterAttendee;
    }

    /**
     * 设置主会场
     *
     * @param masterAttendee void
     * @author lilinhai
     * @since 2021-02-08 15:36
     */
    public synchronized void setMasterAttendee(AttendeeSmc3 masterAttendee) {
        Assert.notNull(masterAttendee, "【" + SysDeptCache.getInstance().get(masterAttendee.getDeptId()).getDeptName() + "】主会场不能为空！");
        if (this.masterAttendee != null) {
            if(Objects.equals(ConstAPI.NORMAL,this.getCategory())||Objects.equals(this.getId(),this.masterAttendee.getConferenceId())){
                if (this.masterAttendee.getDeptId() == deptId.longValue()) {
                    attendees.add(this.masterAttendee);
                    Collections.sort(attendees);
                } else {
                    if (this.masterAttendeeIdSet.contains(this.masterAttendee.getId())) {
                        this.masterAttendees.add(this.masterAttendee);
                        Collections.sort(this.masterAttendees);
                    } else {
                        List<AttendeeSmc3> as = this.cascadeAttendeesMap.get(this.masterAttendee.getDeptId());
                        if (as != null) {
                            as.add(this.masterAttendee);
                            Collections.sort(as);
                        }
                    }
                }
            }
        }

        if (masterAttendee.getDeptId() != deptId.longValue()) {
            if (this.masterAttendeeIdSet.contains(masterAttendee.getId())) {
                this.masterAttendees.remove(masterAttendee);
            } else {
                List<AttendeeSmc3> as = this.cascadeAttendeesMap.get(masterAttendee.getDeptId());
                if (!ObjectUtils.isEmpty(as)) {
                    as.remove(masterAttendee);
                }
            }
        } else {
            attendees.remove(masterAttendee);
        }
        this.masterAttendee = masterAttendee;
        this.masterAttendee.setContextKey(getContextKey());
    }

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
     * 添加地州主会场信息
     *
     * @param masterAttendee void
     * @author lilinhai
     * @since 2021-02-20 16:34
     */
    public void addMasterAttendee(AttendeeSmc3 masterAttendee) {
        this.masterAttendees.add(masterAttendee);
        this.masterAttendeeIdSet.add(masterAttendee.getId());
    }

    /**
     * <p>Get Method   :   masterAttendees List<Attendee></p>
     *
     * @return masterAttendees
     */
    @Override
    public List<AttendeeSmc3> getMasterAttendees() {
        return masterAttendees;
    }

    /**
     * <p>Get Method   :   fmeAddtendees List<FmeAttendee></p>
     *
     * @return fmeAddtendees
     */
    public List<McuAttendeeSmc3> getFmeAttendees() {
        return fmeAttendees;
    }

    /**
     * <p>Set Method   :   fmeAddtendees List<FmeAttendee></p>
     *
     * @param fmeAttendees
     */
    public void setFmeAttendees(List<McuAttendeeSmc3> fmeAttendees) {
        this.fmeAttendees = fmeAttendees;
    }

    /**
     * 添加FME类型的终端参会者
     *
     * @param fmeAttendee
     */
    public void addFmeAttendee(McuAttendeeSmc3 fmeAttendee) {
        fmeAttendee.setConferenceNumber(String.valueOf(conferenceNumber));
        fmeAttendee.setDeptId(deptId);
        this.fmeAttendees.add(fmeAttendee);
        this.attendeeMap.put(fmeAttendee.getId(), fmeAttendee);
        if (fmeAttendee.getRemoteParty() != null) {
            this.attendeeMap.put(fmeAttendee.getRemoteParty(), fmeAttendee);
        }
    }

    /**
     * 添加下级级联会议FME参会者
     *
     * @param fmeAttendee void
     * @author lilinhai
     * @since 2021-03-04 10:02
     */
    public void putFmeAttendee(McuAttendeeSmc3 fmeAttendee) {
        if (fmeAttendee == null) {
            return;
        }
        if (cascade == null) {
            cascade = new Cascade();
        }
        cascade.add(fmeAttendee);
    }

    /**
     * 添加上级级联会议FME参会者
     *
     * @param fmeAttendee void
     * @author lilinhai
     * @since 2021-03-04 10:03
     */
    public void putUpFmeAttendee(UpMcuAttendeeSmc3 fmeAttendee) {
        if (fmeAttendee == null) {
            return;
        }
        if (upCascade == null) {
            upCascade = new UpCascade();
        }
        upCascade.add(fmeAttendee);
    }

    public UpMcuAttendeeSmc3 getUpFmeAttendee(String conferenceNumber) {
        if (upCascade == null) {
            return null;
        }
        return upCascade.get(conferenceNumber);
    }

    public UpMcuAttendeeSmc3 removeUpFmeAttendee(String conferenceNumber) {
        if (upCascade == null) {
            return null;
        }
        return upCascade.remove(conferenceNumber);
    }

    public McuAttendeeSmc3 getFmeAttendee(String conferenceNumber) {
        if (cascade == null) {
            return null;
        }
        return cascade.get(conferenceNumber);
    }

    public McuAttendeeSmc3 removeFmeAttendee(String conferenceNumber) {
        if (cascade == null) {
            return null;
        }
        return cascade.remove(conferenceNumber);
    }

    /**
     * <p>Get Method   :   cascade Cascade</p>
     *
     * @return cascade
     */
    public Cascade getCascade() {
        return cascade;
    }

    /**
     * <p>Get Method   :   upCascade UpCascade</p>
     *
     * @return upCascade
     */
    public UpCascade getUpCascade() {
        return upCascade;
    }

    /**
     * <p>Get Method   :   cascadeAttendeesMap Map<Long,List<TerminalAttendee>></p>
     *
     * @return cascadeAttendeesMap
     */
    @Override
    public Map<Long, List<AttendeeSmc3>> getCascadeAttendeesMap() {
        return cascadeAttendeesMap;
    }

    /**
     * <p>Set Method   :   cascadeAttendeesMap Map<Long,List<TerminalAttendee>></p>
     *
     * @param cascadeAttendeesMap
     */
    public void setCascadeAttendeesMap(Map<Long, List<AttendeeSmc3>> cascadeAttendeesMap) {
        this.cascadeAttendeesMap = cascadeAttendeesMap;
    }

    /**
     * <pre>添加级联会议的参会者</pre>
     *
     * @author lilinhai
     * @since 2021-02-02 15:09  void
     */
    public void addCascadeAttendee(AttendeeSmc3 attendee) {
        List<AttendeeSmc3> cascadeAttendees = cascadeAttendeesMap.get(attendee.getDeptId());
        if (cascadeAttendees == null) {
            cascadeAttendees = new ArrayList<>();
            cascadeAttendeesMap.put(attendee.getDeptId(), cascadeAttendees);
        }
        cascadeAttendees.add(attendee);

        if (attendee instanceof TerminalAttendeeSmc3) {
            TerminalAttendeeSmc3 terminalAttendee = (TerminalAttendeeSmc3) attendee;
            terminalAttendeeMap.put(terminalAttendee.getTerminalId(), terminalAttendee);
        }
        addAttendeeToTerminalAllMap(attendee);
        attendee.setContextKey(getContextKey());

        if(Objects.equals(ConstAPI.CASCADE,this.getCategory())){
            if(Strings.isNotBlank(this.parentConferenceContextKey)){
                Smc3ConferenceContext smc3ConferenceContext = Smc3ConferenceContextCache.getInstance().get(this.parentConferenceContextKey);
                smc3ConferenceContext.addCascadeAttendeeNew(attendee);
            }
        }
    }

    /**
     * <p>Get Method   :   startTime Date</p>
     *
     * @return startTime
     */
    @Override
    public Date getStartTime() {
        return startTime;
    }

    /**
     * <p>Set Method   :   startTime Date</p>
     *
     * @param startTime
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
        this.setStart(true);
    }

    /**
     * <p>Get Method   :   endTime Date</p>
     *
     * @return endTime
     */
    @Override
    public Date getEndTime() {
        return endTime;
    }

    /**
     * <p>Set Method   :   endTime Date</p>
     *
     * @param endTime
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * <p>Get Method   :   id String</p>
     *
     * @return id
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * <p>Set Method   :   id String</p>
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * <p>Get Method   :   templateConferenceId Long</p>
     *
     * @return templateConferenceId
     */
    @Override
    public Long getTemplateConferenceId() {
        return templateConferenceId;
    }

    /**
     * <p>Set Method   :   templateConferenceId Long</p>
     *
     * @param templateConferenceId
     */
    @Override
    public void setTemplateConferenceId(Long templateConferenceId) {
        this.templateConferenceId = templateConferenceId;
    }

    /**
     * <p>Get Method   :   isStart boolean</p>
     *
     * @return isStart
     */
    @Override
    public boolean isStart() {
        return isStart;
    }

    /**
     * <p>Set Method   :   isStart boolean</p>
     *
     * @param isStart
     */
    public void setStart(boolean isStart) {
        this.isStart = isStart;
    }

    /**
     * <p>Get Method   :   isEnd boolean</p>
     *
     * @return isEnd
     */
    @Override
    public boolean isEnd() {
        return isEnd;
    }

    /**
     * <p>Set Method   :   isEnd boolean</p>
     *
     * @param isEnd
     */
    public void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }

    public Map<String, AttendeeSmc3> getUuidAttendeeMapByUri(String remoteParty) {
        return remotePartyAttendeesMap.get(remoteParty);
    }

    /**
     * <p>Get Method   :   attendeeMap Map<String,Attendee></p>
     *
     * @return attendeeMap
     */
    @Override
    public AttendeeSmc3 getAttendeeById(String attendeeId) {
        return attendeeMap.get(attendeeId);
    }

    /**
     * <p>Set Method   :   attendeeMap Map<String,Attendee></p>
     *
     * @param id
     */
    public synchronized AttendeeSmc3 removeAttendeeById(String id) {
        AttendeeSmc3 attendee = this.attendeeMap.remove(id);
        if (attendee != null) {
            attendees.remove(attendee);

            this.masterAttendeeIdSet.remove(id);
            this.masterAttendees.remove(attendee);

            List<AttendeeSmc3> as = cascadeAttendeesMap.get(attendee.getDeptId());
            if (as != null) {
                as.remove(attendee);
            }

            if (attendee instanceof TerminalAttendeeSmc3) {
                TerminalAttendeeSmc3 ta = (TerminalAttendeeSmc3) attendee;
                terminalAttendeeMap.remove(ta.getTerminalId());
                if (TerminalType.isRtsp(ta.getTerminalType())) {
                    String rtsp_uri = (String) ta.getBusinessProperties().get("rtsp_uri");
                    this.remotePartyAttendeesMap.removeAttendeeByRemotePartyAndUuid(rtsp_uri, id);
                }
            }
            this.remotePartyAttendeesMap.removeAttendeeByRemotePartyAndUuid(attendee.getRemoteParty(), id);
            removeRecallAttendeeBeginTime(attendee);
            removeAttendeeFromTerminalAllMap(attendee);
            if(attendee.getSmcParticipant()!= null&&attendee.getSmcParticipant().getGeneralParam()!= null){
                this.participantAttendeeAllMap.remove(attendee.getSmcParticipant().getGeneralParam().getId());
            }
        }


        return attendee;
    }

    /**
     * <p>Set Method   :   attendeeMap Map<String,Attendee></p>
     *
     * @param attendeeMap
     */
    public void setAttendeeMap(Map<String, AttendeeSmc3> attendeeMap) {
        this.attendeeMap = attendeeMap;
    }

    /**
     * <p>Get Method   :   terminalAttendeeMap Map<Long,FmeAttendee></p>
     *
     * @return terminalAttendeeMap
     */
    @Override
    public Map<Long, TerminalAttendeeSmc3> getTerminalAttendeeMap() {
        return terminalAttendeeMap;
    }

    /**
     * <p>Set Method   :   terminalAttendeeMap Map<Long,FmeAttendee></p>
     *
     * @param terminalAttendeeMap
     */
    public void setTerminalAttendeeMap(Map<Long, TerminalAttendeeSmc3> terminalAttendeeMap) {
        this.terminalAttendeeMap = terminalAttendeeMap;
    }

    /**
     * <p>Get Method   :   attendeeOperation AttendeeOperation</p>
     *
     * @return attendeeOperation
     */
    public AttendeeOperation getAttendeeOperation() {
        return attendeeOperation;
    }

    /**
     * <p>Set Method   :   attendeeOperation AttendeeOperation</p>
     *
     * @param attendeeOperation
     */
    public void setAttendeeOperation(AttendeeOperation attendeeOperation) {
        this.attendeeOperation = attendeeOperation;
    }

    /**
     * <p>Get Method   :   lastAttendeeOperation AttendeeOperation</p>
     *
     * @return lastAttendeeOperation
     */
    public AttendeeOperation getLastAttendeeOperation() {
        return lastAttendeeOperation;
    }

    /**
     * <p>Set Method   :   lastAttendeeOperation AttendeeOperation</p>
     *
     * @param lastAttendeeOperation
     */
    public void setLastAttendeeOperation(AttendeeOperation lastAttendeeOperation) {
        this.lastAttendeeOperation = lastAttendeeOperation;
    }

    /**
     * <p>Get Method   :   defaultAttendeeOperation AttendeeOperation</p>
     *
     * @return defaultAttendeeOperation
     */
    public DefaultViewOperation getDefaultViewOperation() {
        return defaultViewOperation;
    }

    /**
     * <p>Set Method   :   defaultAttendeeOperation AttendeeOperation</p>
     *
     * @param defaultViewOperation
     */
    public void setDefaultViewOperation(DefaultViewOperation defaultViewOperation) {
        this.defaultViewOperation = defaultViewOperation;
    }

    public boolean getRoundRobin() {
        return attendeeOperation != null && attendeeOperation instanceof PollingAttendeeOperation;
    }

    public boolean getRoundRobinPaused() {
        return getRoundRobin() && ((PollingAttendeeOperation) attendeeOperation).isPause();
    }

    public boolean isDefaultViewRunning() {
        return attendeeOperation == defaultViewOperation;
    }

    /**
     * <p>Get Method   :   recallAttendeeBeginTimeMap Map<String,Long></p>
     *
     * @return recallAttendeeBeginTimeMap
     */
    public Long getRecallAttendeeBeginTime(AttendeeSmc3 attendee) {
        return recallAttendeeBeginTimeMap.get(attendee.getId());
    }

    /**
     * <p>Get Method   :   recallAttendeeBeginTimeMap Map<String,Long></p>
     *
     * @return recallAttendeeBeginTimeMap
     */
    public Long removeRecallAttendeeBeginTime(AttendeeSmc3 attendee) {
        return recallAttendeeBeginTimeMap.remove(attendee.getId());
    }

    /**
     * <p>Set Method   :   recallAttendeeBeginTimeMap Map<String,Long></p>
     */
    public void addRecallAttendeeBeginTime(AttendeeSmc3 attendee) {
        if (!this.recallAttendeeBeginTimeMap.containsKey(attendee.getId())) {
            this.recallAttendeeBeginTimeMap.put(attendee.getId(), System.currentTimeMillis());
        }
    }

    /**
     * <p>Get Method   :   fmeAttendeeRemoteParty String</p>
     *
     * @return fmeAttendeeRemoteParty
     */
    public String getFmeAttendeeRemoteParty() {
        return fmeAttendeeRemoteParty;
    }

    /**
     * <p>Set Method   :   fmeAttendeeRemoteParty String</p>
     *
     * @param attendeeIp
     */
    public void setFmeAttendeeRemoteParty(String attendeeIp) {
        this.fmeAttendeeRemoteParty = conferenceNumber + "@" + attendeeIp;
    }

    /**
     * <p>Get Method   :   isAutoCallTerminal boolean</p>
     *
     * @return isAutoCallTerminal
     */
    public boolean isAutoCallTerminal() {
        return isAutoCallTerminal;
    }

    /**
     * <p>Set Method   :   isAutoCallTerminal boolean</p>
     *
     * @param isAutoCallTerminal
     */
    public void setAutoCallTerminal(boolean isAutoCallTerminal) {
        this.isAutoCallTerminal = isAutoCallTerminal;
    }

    public boolean isMain() {
        return upCascade == null || upCascade.getFmeAttendeeMap().isEmpty();
    }

    /**
     * <p>Get Method   :   masterAttendeeIdSet Set<String></p>
     *
     * @return masterAttendeeIdSet
     */
    public Set<String> getMasterAttendeeIdSet() {
        return masterAttendeeIdSet;
    }

    /**
     * 清除缓存
     *
     * @author lilinhai
     * @since 2021-03-03 11:29  void
     */
    public void clear() {
        attendees.clear();
        masterAttendees.clear();
        fmeAttendees.clear();
        cascadeAttendeesMap.clear();
        attendeeMap.clear();
        terminalAttendeeMap.clear();
        recallAttendeeBeginTimeMap.clear();
        liveTerminals.clear();
        liveTerminalMap.clear();
        cascade = null;
        upCascade = null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Smc3ConferenceContext other = (Smc3ConferenceContext) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "ConferenceContext [id=" + id + ", name=" + name + ", deptId=" + deptId + ", bandwidth=" + bandwidth
                + ", conferenceNumber=" + conferenceNumber + ", type=" + type + "]";
    }

    /**
     * <p>Get Method   :   isAppointment boolean</p>
     *
     * @return isAppointment
     */
    @Override
    public boolean isAppointment() {
        return isAppointment;
    }

    /**
     * <p>Set Method   :   isAppointment boolean</p>
     *
     * @param isAppointment
     */
    public void setAppointment(boolean isAppointment) {
        this.isAppointment = isAppointment;
    }

    public Integer getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(Integer appointmentType) {
        this.appointmentType = appointmentType;
    }

    /**
     * <p>Get Method   :   conferenceAppointment BusiConferenceAppointment</p>
     *
     * @return conferenceAppointment
     */
    @Override
    public BusiMcuSmc3ConferenceAppointment getConferenceAppointment() {
        return conferenceAppointment;
    }

    /**
     * <p>Set Method   :   conferenceAppointment BusiConferenceAppointment</p>
     *
     * @param conferenceAppointment
     */
    public void setConferenceAppointment(BusiMcuSmc3ConferenceAppointment conferenceAppointment) {
        this.conferenceAppointment = conferenceAppointment;
    }

    /**
     * <p>Get Method   :   historyConference BusiHistoryConference</p>
     *
     * @return historyConference
     */
    @Override
    public BusiHistoryConference getHistoryConference() {
        return historyConference;
    }

    /**
     * <p>Set Method   :   historyConference BusiHistoryConference</p>
     *
     * @param historyConference
     */
    public void setHistoryConference(BusiHistoryConference historyConference) {
        this.historyConference = historyConference;
    }

    /**
     * <p>Get Method   :   locked boolean</p>
     *
     * @return locked
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * <p>Set Method   :   locked boolean</p>
     *
     * @param locked
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * <p>Get Method   :   discuss boolean</p>
     *
     * @return discuss
     */
    public boolean isDiscuss() {
        return discuss;
    }

    /**
     * <p>Set Method   :   discuss boolean</p>
     *
     * @param discuss
     */
    public void setDiscuss(boolean discuss) {
        this.discuss = discuss;
    }

    /**
     * <p>Get Method   :   recorded boolean</p>
     *
     * @return recorded
     */
    public boolean isRecorded() {
        return recorded;
    }

    /**
     * <p>Set Method   :   recorded boolean</p>
     *
     * @param recorded
     */
    public void setRecorded(boolean recorded) {
        this.recorded = recorded;
    }

    /**
     * <p>Get Method   :   streaming boolean</p>
     *
     * @return streaming
     */
    @Override
    public boolean isStreaming() {
        return streaming;
    }

    /**
     * <p>Set Method   :   streaming boolean</p>
     *
     * @param streaming
     */
    public void setStreaming(boolean streaming) {
        this.streaming = streaming;
    }

    /**
     * <p>Get Method   :   streamingUrl String</p>
     *
     * @return streamingUrl
     */
    @Override
    public String getStreamingUrl() {
        return streamingUrl;
    }

    /**
     * <p>Set Method   :   streamingUrl String</p>
     *
     * @param streamingUrl
     */
    @Override
    public void setStreamingUrl(String streamingUrl) {
        this.streamingUrl = streamingUrl;
    }

    public String getStreamingRemoteParty() {
        return streamingRemoteParty;
    }

    public void setStreamingRemoteParty(String streamingRemoteParty) {
        this.streamingRemoteParty = streamingRemoteParty;
    }

    public String getStreamingName() {
        return streamingName;
    }

    public void setStreamingName(String streamingName) {
        this.streamingName = streamingName;
    }

    public AttendeeSmc3 getStreamingAttendee() {
        return streamingAttendee;
    }

    public void setStreamingAttendee(AttendeeSmc3 streamingAttendee) {
        this.streamingAttendee = streamingAttendee;
    }

    /**
     * <p>Get Method   :   allowAllMuteSelf boolean</p>
     *
     * @return allowAllMuteSelf
     */
    public boolean isAllowAllMuteSelf() {
        return allowAllMuteSelf;
    }

    /**
     * <p>Set Method   :   allowAllMuteSelf boolean</p>
     *
     * @param allowAllMuteSelf
     */
    public void setAllowAllMuteSelf(boolean allowAllMuteSelf) {
        this.allowAllMuteSelf = allowAllMuteSelf;
    }

    /**
     * <p>Set Method   :   allowAllPresentationContribution boolean</p>
     *
     * @param allowAllPresentationContribution
     */
    public void setAllowAllPresentationContribution(boolean allowAllPresentationContribution) {
        this.allowAllPresentationContribution = allowAllPresentationContribution;
    }

    /**
     * <p>Get Method   :   joinAudioMuteOverride boolean</p>
     *
     * @return joinAudioMuteOverride
     */
    public boolean isJoinAudioMuteOverride() {
        return joinAudioMuteOverride;
    }

    /**
     * <p>Set Method   :   joinAudioMuteOverride boolean</p>
     *
     * @param joinAudioMuteOverride
     */
    public void setJoinAudioMuteOverride(boolean joinAudioMuteOverride) {
        this.joinAudioMuteOverride = joinAudioMuteOverride;
    }

    /**
     * <p>Get Method   :   syncLock Object</p>
     *
     * @return syncLock
     */
    public Object getSyncLock() {
        return syncLock;
    }

    /**
     * <p>Get Method   :   deptWeights List<DeptWeight></p>
     *
     * @return deptWeights
     */
    public List<DeptWeight> getDeptWeights() {
        return deptWeights;
    }

    /**
     * <p>Set Method   :   deptWeights List<DeptWeight></p>
     */
    public void addDeptWeight(DeptWeight deptWeight) {
        this.deptWeights.add(deptWeight);
    }

    /**
     * <p>Get Method   :   businessFieldType Integer</p>
     *
     * @return businessFieldType
     */
    public Integer getBusinessFieldType() {
        return businessFieldType;
    }

    /**
     * <p>Set Method   :   businessFieldType Integer</p>
     *
     * @param businessFieldType
     */
    public void setBusinessFieldType(Integer businessFieldType) {
        this.businessFieldType = businessFieldType;
    }

    /**
     * <p>Get Method   :   businessProperties JSONObject</p>
     *
     * @return businessProperties
     */
    public Map<String, Object> getBusinessProperties() {
        return businessProperties;
    }

    /**
     * <p>Set Method   :   businessProperties JSONObject</p>
     *
     * @param businessProperties
     */
    public void setBusinessProperties(Map<String, Object> businessProperties) {
        this.businessProperties = businessProperties;
    }

    /**
     * <p>Get Method   :   liveTerminals List<BusiTerminal></p>
     *
     * @return liveTerminals
     */
    @Override
    public List<TerminalAttendeeSmc3> getLiveTerminals() {
        return liveTerminals;
    }

    /**
     * <p>Get Method   :   liveTerminals List<BusiTerminal></p>
     *
     * @return liveTerminals
     */
    @Override
    public TerminalAttendeeSmc3 getLiveTerminal(String sn) {
        return liveTerminalMap.get(sn);
    }

    /**
     * <p>Set Method   :   liveTerminals List<BusiTerminal></p>
     *
     * @param terminal
     */
    public void addLiveTerminal(TerminalAttendeeSmc3 terminal) {
        this.liveTerminals.add(terminal);
        if (terminal.getSn() != null && terminal.getSn().length() > 0) {
            this.liveTerminalMap.put(terminal.getSn(), terminal);
        }
    }

    /**
     * <p>Get Method   :   conferencePassword String</p>
     *
     * @return conferencePassword
     */
    @Override
    public String getConferencePassword() {
        return conferencePassword;
    }

    /**
     * <p>Set Method   :   conferencePassword String</p>
     *
     * @param conferencePassword
     */
    public void setConferencePassword(String conferencePassword) {
        this.conferencePassword = conferencePassword;
    }

    @Override
    public Integer getIsAutoCreateStreamUrl() {
        return isAutoCreateStreamUrl;
    }

    public void setIsAutoCreateStreamUrl(Integer isAutoCreateStreamUrl) {
        this.isAutoCreateStreamUrl = isAutoCreateStreamUrl;
    }

    @Override
    public Long getPresenter() {
        return presenter;
    }

    @Override
    public void setPresenter(Long presenter) {
        this.presenter = presenter;
    }

    @Override
    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    @Override
    public List<String> getStreamUrlList() {
        return streamUrlList;
    }

    public void addStreamUrlList(List<String> streamUrlList) {
        this.streamUrlList.addAll(streamUrlList);
    }

    public Integer getLiveTerminalCount() {
        return liveTerminalCount;
    }

    public void setLiveTerminalCount(Integer liveTerminalCount) {
        this.liveTerminalCount = liveTerminalCount;
    }

    public void add(Long terminalId, Long uerId) {
        terminalUserMap.put(terminalId, uerId);
    }

    @Override
    public Long getUserIdByTerminalId(Long terminalId) {
        return terminalUserMap.get(terminalId);
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }

    @Override
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public boolean isSupportRollCall() {
        return supportRollCall;
    }

    public void setSupportRollCall(boolean supportRollCall) {
        this.supportRollCall = supportRollCall;
    }

    @Override
    public boolean isSupportSplitScreen() {
        return supportSplitScreen;
    }

    public void setSupportSplitScreen(boolean supportSplitScreen) {
        this.supportSplitScreen = supportSplitScreen;
    }

    @Override
    public boolean isSupportPolling() {
        return supportPolling;
    }

    public void setSupportPolling(boolean supportPolling) {
        this.supportPolling = supportPolling;
    }

    @Override
    public boolean isSupportChooseSee() {
        return supportChooseSee;
    }

    public void setSupportChooseSee(boolean supportChooseSee) {
        this.supportChooseSee = supportChooseSee;
    }

    @Override
    public boolean isSupportTalk() {
        return supportTalk;
    }

    public void setSupportTalk(boolean supportTalk) {
        this.supportTalk = supportTalk;
    }

    @Override
    public boolean isSupportBroadcast() {
        return supportBroadcast;
    }

    public void setSupportBroadcast(boolean supportBroadcast) {
        this.supportBroadcast = supportBroadcast;
    }

    @Override
    public boolean isSingleView() {
        return singleView;
    }

    public void setSingleView(boolean singleView) {
        this.singleView = singleView;
    }

    public boolean isDownCascade() {
        return isDownCascade;
    }

    public void setDownCascade(boolean downCascade) {
        isDownCascade = downCascade;
    }

    public Integer getMuteType() {
        return muteType;
    }

    public void setMuteType(Integer muteType) {
        this.muteType = muteType;
    }

    public Integer getMuteStatus() {
        return muteStatus;
    }

    public void setMuteStatus(Integer muteStatus) {
        this.muteStatus = muteStatus;
    }

    public String getCoSpaceId() {
        String coSpaceId = getTenantId() + this.conferenceNumber;
        if (this.templateCreateTime != null) {
            coSpaceId += "-" +this.templateConferenceId+"-"+ this.templateCreateTime.getTime();
        }
        coSpaceId += "-SMC3";
        return coSpaceId;
    }

    public Date getTemplateCreateTime() {
        return templateCreateTime;
    }

    public void setTemplateCreateTime(Date templateCreateTime) {
        this.templateCreateTime = templateCreateTime;
    }

    public Boolean getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Boolean subscribe) {
        this.subscribe = subscribe;
    }

    public List<String> getChangeParticipants() {
        return changeParticipants;
    }

    public void setChangeParticipants(List<String> changeParticipants) {
        this.changeParticipants = changeParticipants;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public SyncInformation getSyncInformation() {
        return syncInformation;
    }

    public void setSyncInformation(SyncInformation syncInformation) {
        this.syncInformation = syncInformation;
    }

    public int getEndReasonsType() {
        return endReasonsType;
    }

    public void setEndReasonsType(int endReasonsType) {
        this.endReasonsType = endReasonsType;
    }

    public String getSmcTemplateId() {
        return smcTemplateId;
    }

    public void setSmcTemplateId(String smcTemplateId) {
        this.smcTemplateId = smcTemplateId;
    }

    public String getSmc3conferenceId() {
        return smc3conferenceId;
    }

    public void setSmc3conferenceId(String smc3conferenceId) {
        this.smc3conferenceId = smc3conferenceId;
    }

    public Smc3Bridge getSmc3Bridge() {
        return smc3Bridge;
    }

    public void setSmc3Bridge(Smc3Bridge smc3Bridge) {
        this.smc3Bridge = smc3Bridge;
    }

    public AttendeeSmc3 getAttendeeBySmc3Id(String participantId) {
        return this.participantAttendeeAllMap.get(participantId);
    }


    public MultiPicPollRequest getMultiPicPollRequest() {
        return multiPicPollRequest;
    }

    public void setMultiPicPollRequest(MultiPicPollRequest multiPicPollRequest) {
        this.multiPicPollRequest = multiPicPollRequest;
    }

    public void setChairmanParticipantMultiPicPoll(MultiPicPollRequest multiPicPollRequest) {
        this.chairmanMultiPicPollRequest = multiPicPollRequest;
    }

    public MultiPicPollRequest getChairmanMultiPicPollRequest() {
        return chairmanMultiPicPollRequest;
    }

//    public MultiPicInfoReq getMultiPicInfo() {
//        return multiPicInfo;
//    }
//
//    public void setMultiPicInfo(MultiPicInfoReq multiPicInfo) {
//        this.multiPicInfo = multiPicInfo;
//    }


    public ConferenceState.StateDTO.MultiPicInfoDTO getMultiPicInfo() {
        return multiPicInfo;
    }

    public void setMultiPicInfo(ConferenceState.StateDTO.MultiPicInfoDTO multiPicInfo) {
        this.multiPicInfo = multiPicInfo;
    }

    @Override
    public BaseAttendee getAttendeeByPUuid(String smcPid) {
        return getAttendeeBySmc3Id(smcPid);
    }

    @Override
    public String getMcuCallIp() {
        if (smc3Bridge != null) {
            if (StringUtils.hasText(smc3Bridge.getBusiSMC().getIp())) {
                return smc3Bridge.getBusiSMC().getScUrl();
            }
        }
        return null;
    }

    @Override
    public Integer getMcuCallPort() {
        if (smc3Bridge != null) {
            if (smc3Bridge.getBusiSMC().getCallPort() != null) {
                return smc3Bridge.getBusiSMC().getCallPort();
            }
        }
        return null;
    }

    /**
     * 添加FME类型的终端参会者
     *
     * @param attendee
     */
    public void addMcuAttendee(McuAttendeeSmc3 attendee) {
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
    public void removeMcuAttendee(McuAttendeeSmc3 mcuAttendee) {
        this.mcuAttendees.remove(mcuAttendee);
        this.removeAttendeeById(mcuAttendee.getId());
    }

    public void setMcuAttendeeRemoteParty(String attendeeIp) {
        this.fmeAttendeeRemoteParty = conferenceNumber + "@" + attendeeIp;
    }

    public String getChairmanPassword() {
        return chairmanPassword;
    }

    public void setChairmanPassword(String chairmanPassword) {
        this.chairmanPassword = chairmanPassword;
    }

    public String getGuestPassword() {
        return guestPassword;
    }

    public void setGuestPassword(String guestPassword) {
        this.guestPassword = guestPassword;
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

    public Integer getMaxParticipantNum() {
        return maxParticipantNum;
    }

    public void setMaxParticipantNum(Integer maxParticipantNum) {
        this.maxParticipantNum = maxParticipantNum;
    }

    public String getLockPresenterId() {
        return lockPresenterId;
    }

    public void setLockPresenterId(String lockPresenterId) {
        this.lockPresenterId = lockPresenterId;
    }

    public String getMonitorNumber() {
        return monitorNumber;
    }

    public void setMonitorNumber(String monitorNumber) {
        this.monitorNumber = monitorNumber;
    }

    public boolean isQuiet() {
        return quiet;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    @Override
    public List<McuAttendeeSmc3> getMcuAttendees()
    {
        return mcuAttendees;
    }

    private volatile String broadId;

    public String getBroadId() {
        return broadId;
    }

    public void setBroadId(String broadId) {
        this.broadId = broadId;
    }

    public String getChairmanId() {
        return chairmanId;
    }

    public void setChairmanId(String chairmanId) {
        this.chairmanId = chairmanId;
    }

    public TextTipsSetting getBanner() {
        return banner;
    }

    public void setBanner(TextTipsSetting banner) {
        this.banner = banner;
    }

    public TextTipsSetting getCaption() {
        return caption;
    }

    public void setCaption(TextTipsSetting caption) {
        this.caption = caption;
    }

    public Boolean getSecretTalk() {
        return secretTalk;
    }

    public void setSecretTalk(Boolean secretTalk) {
        this.secretTalk = secretTalk;
    }

    public MultiPicInfoTalkReq getMultiPicInfoTalkReq() {
        return multiPicInfoTalkReq;
    }

    public void setMultiPicInfoTalkReq(MultiPicInfoTalkReq multiPicInfoTalkReq) {
        this.multiPicInfoTalkReq = multiPicInfoTalkReq;
    }

    public String getPresenterId() {
        return presenterId;
    }

    public void setPresenterId(String presenterId) {
        this.presenterId = presenterId;
    }

    public String getCospaceId() {
        return cospaceId;
    }

    public void setCospaceId(String cospaceId) {
        this.cospaceId = cospaceId;
    }

    public String getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(String monitorId) {
        this.monitorId = monitorId;
    }


    private volatile String presentAttendeeId = null;

    @Override
    public String getPresentAttendeeId() {
        return presentAttendeeId;
    }

    public void setPresentAttendeeId(String presentAttendeeId) {
        this.presentAttendeeId = presentAttendeeId;
    }


    private void clearMonitor() {
        this.monitorId =null;
        this.monitorNumber =null;
        this.cospaceId=null;
    }

    public SmcConferenceTemplate.ConfPresetParamDTO getConfPresetParamDTO() {
        return confPresetParamDTO;
    }

    public void setConfPresetParamDTO(SmcConferenceTemplate.ConfPresetParamDTO confPresetParamDTO) {
        this.confPresetParamDTO = confPresetParamDTO;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void defaultAttendeeOperation(){

        if(Strings.isNotBlank(chairmanId)){
            setLastAttendeeOperation(this.getAttendeeOperation());
            ChangeMasterAttendeeOperation changeMasterAttendeeOperation = new ChangeMasterAttendeeOperation(this, masterAttendee);
            setAttendeeOperation(changeMasterAttendeeOperation);
            changeMasterAttendeeOperation.operate();
        }else {
            ConferenceState.StateDTO.MultiPicInfoDTO multiPicInfo =getMultiPicInfo();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("multiPicInfo",multiPicInfo);
            jsonObject.put("conferenceId",getSmc3conferenceId());
            jsonObject.put("broadcast",false);
            setLastAttendeeOperation(this.getAttendeeOperation());
            DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(this,jsonObject);
            setAttendeeOperation(defaultAttendeeOperation);
            defaultAttendeeOperation.operate();
        }
    }

    public String getParentConferenceId() {
        return parentConferenceId;
    }

    public void setParentConferenceId(String parentConferenceId) {
        this.parentConferenceId = parentConferenceId;
    }

    public List<TemplateNode> getCascadeTree() {
        return cascadeTree;
    }

    public void setCascadeTree(List<TemplateNode> cascadeTree) {
        this.cascadeTree = cascadeTree;
    }

    public Long getCascadeLocalTemplateId() {
        return cascadeLocalTemplateId;
    }

    public void setCascadeLocalTemplateId(Long cascadeLocalTemplateId) {
        this.cascadeLocalTemplateId = cascadeLocalTemplateId;
    }

    public List<ConferenceNode> getCascadeConferenceTree() {

        List<ConferenceNode> conferenceNodeList=new ArrayList<>();
        if(Strings.isNotBlank(this.parentConferenceId)){
            for (ConferenceNode conferenceNode : cascadeConferenceTree) {
                if(Objects.equals(conferenceNode.getConferenceId(),this.id)){
                    conferenceNodeList.add(conferenceNode);
                }
                if(Objects.equals(conferenceNode.getParentConferenceId(),this.id)){
                    conferenceNodeList.add(conferenceNode);
                }
            }
            return conferenceNodeList;
        }
        return cascadeConferenceTree;
    }

    public void setCascadeConferenceTree(List<ConferenceNode> cascadeConferenceTree) {
        this.cascadeConferenceTree = cascadeConferenceTree;
    }

    public List<AttendeeSmc3> getCascadeAttendees() {
        return cascadeAttendees;
    }

    public void setCascadeAttendees(List<AttendeeSmc3> cascadeAttendees) {
        this.cascadeAttendees = cascadeAttendees;
    }

    public String getParentConferenceContextKey() {
        return parentConferenceContextKey;
    }

    public void setParentConferenceContextKey(String parentConferenceContextKey) {
        this.parentConferenceContextKey = parentConferenceContextKey;
    }

    public List<AttendeeSmc3> getDisplayAttendees() {
        return displayAttendees;
    }

    public void setDisplayAttendees(List<AttendeeSmc3> displayAttendees) {
        this.displayAttendees = displayAttendees;
    }

    public AttendeeSmc3 getPresenterAttendee() {
        return presenterAttendee;
    }

    public void setPresenterAttendee(AttendeeSmc3 presenterAttendee) {
        this.presenterAttendee = presenterAttendee;
    }
}
