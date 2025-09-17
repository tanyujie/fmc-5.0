package com.paradisecloud.fcm.mcu.zj.cache.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.BusiMcuZj;
import com.paradisecloud.fcm.dao.model.BusiMcuZjConferenceAppointment;
import com.paradisecloud.fcm.mcu.zj.attendee.model.operation.PollingAttendeeOperation;
import com.paradisecloud.fcm.mcu.zj.cache.AttendeeCountingStatistics;
import com.paradisecloud.fcm.mcu.zj.cache.api.ConferenceControlApi;
import com.paradisecloud.fcm.mcu.zj.model.EpsInfo;
import com.paradisecloud.fcm.mcu.zj.model.UserInfo;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.*;
import com.paradisecloud.fcm.mcu.zj.model.busi.cascade.CascadeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.cascade.UpCascadeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.mcu.zj.model.busi.operation.DefaultViewOperation;
import com.paradisecloud.fcm.mcu.zj.model.core.McuZjSyncInformation;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.http.HttpObjectCreator;
import com.sinhy.http.HttpRequester;
import io.jsonwebtoken.lang.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 活跃会议上下文实体类
 * @author lilinhai
 * @since 2021-02-02 15:00
 * @version V1.0
 */
public class McuZjConferenceContext extends BaseConferenceContext<AttendeeForMcuZj> implements Serializable
{
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-02 14:04 
     */
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private final Object syncLock = new Object();

    @JsonIgnore
    private String baseUrl;
    @JsonIgnore
    private HttpRequester httpRequester;
    @JsonIgnore
    private volatile String sessionId = "";
    @JsonIgnore
    private ConferenceControlApi conferenceControlApi;
    @JsonIgnore
    private volatile long lastUpdateTime = 0;
    private volatile String tenantId = "";
    private volatile Integer resourceTemplateId;
    private volatile String presentAttendeeId = null;
    private volatile Long messageCloseTime = null;
    private volatile boolean invitingTerminal = false;
    private volatile String lastRaiseHandAttendeeId;

    private boolean supportRollCall;
    private boolean supportSplitScreen;
    private boolean supportPolling;
    private boolean supportChooseSee;
    private boolean supportTalk;
    private boolean supportBroadcast;
    private boolean singleView;
    private List<ModelBean> speakerSplitScreenList;
    private List<ModelBean> guestSplitScreenList;

    private String supervisorPassword;

    /**
     * 参会者映射表，key为epId
     */
    @JsonIgnore
    private volatile Map<Integer, AttendeeForMcuZj> epAttendeesMap = new ConcurrentHashMap<>();
    /**
     * 参会者映射表，key为epUserId
     */
    @JsonIgnore
    private volatile Map<String, AttendeeForMcuZj> epUserAttendeesMap = new ConcurrentHashMap<>();
    /**
     * 参会者映射表，key为call_addr
     */
    @JsonIgnore
    private volatile Map<String, UserInfo> callAddrUserInfoMap = new ConcurrentHashMap<>();
    /**
     * 参会者映射表，key为userId
     */
    @JsonIgnore
    private volatile Map<String, UserInfo> userInfoMap = new ConcurrentHashMap<>();
    /**
     * 参会者映射表，key为userId
     */
    @JsonIgnore
    private volatile Map<String, EpsInfo> epUserEpsInfoMap = new ConcurrentHashMap<>();
    @JsonIgnore
    private volatile McuZjSyncInformation syncInformation;

    /**
     * 模板绑定的会议号
     */
    @JsonIgnore
    private McuZjBridge mcuZjBridge;

    public McuZjConferenceContext(McuZjBridge mcuZjBridge) {
        this.mcuZjBridge = mcuZjBridge;
        setMcuTypeObject(McuType.MCU_ZJ);
        setMultiScreenRollCall(true);
        init();
    }

    private void init() {
        destroy();
        baseUrl = "https://" + mcuZjBridge.getBusiMcuZj().getIp() + ":" + mcuZjBridge.getBusiMcuZj().getPort();
        httpRequester = HttpObjectCreator.getInstance().createHttpRequester();
        conferenceControlApi = new ConferenceControlApi(this);
    }

    public void cleanLoginInfo() {
        setLastUpdateTime(0);
        setSessionId("");
    }

    public McuZjBridge getMcuZjBridge() {
        return mcuZjBridge;
    }

    @JsonIgnore
    public BusiMcuZj getBusiMcuZj() {
        return mcuZjBridge.getBusiMcuZj();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public HttpRequester getHttpRequester() {
        return this.httpRequester;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public ConferenceControlApi getConferenceControlApi() {
        return conferenceControlApi;
    }

    public void setConferenceControlApi(ConferenceControlApi conferenceControlApi) {
        this.conferenceControlApi = conferenceControlApi;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }

    @Override
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getResourceTemplateId() {
        return resourceTemplateId;
    }

    public void setResourceTemplateId(Integer resourceTemplateId) {
        this.resourceTemplateId = resourceTemplateId;
    }

    @Override
    public String getPresentAttendeeId() {
        return presentAttendeeId;
    }

    public void setPresentAttendeeId(String presentAttendeeId) {
        this.presentAttendeeId = presentAttendeeId;
    }

    public boolean hasPresent() {
        if (StringUtils.hasText(this.presentAttendeeId)) {
            AttendeeForMcuZj attendeeForMcuZj = getAttendeeById(this.presentAttendeeId);
            if (attendeeForMcuZj != null && attendeeForMcuZj.isMeetingJoined()) {
                return true;
            }
        }
        return false;
    }

    public Long getMessageCloseTime() {
        return messageCloseTime;
    }

    public void setMessageCloseTime(Long messageCloseTime) {
        this.messageCloseTime = messageCloseTime;
    }

    public boolean isInvitingTerminal() {
        return invitingTerminal;
    }

    public void setInvitingTerminal(boolean invitingTerminal) {
        this.invitingTerminal = invitingTerminal;
    }

    public String getLastRaiseHandAttendeeId() {
        return lastRaiseHandAttendeeId;
    }

    public void setLastRaiseHandAttendeeId(String lastRaiseHandAttendeeId) {
        this.lastRaiseHandAttendeeId = lastRaiseHandAttendeeId;
    }

    public String getCoSpaceId() {
        String coSpaceId = this.tenantId + this.conferenceNumber;
        if (this.templateCreateTime != null) {
            coSpaceId += "-" + this.templateCreateTime.getTime();
        }
        coSpaceId += "-zj";
        return coSpaceId;
    }

    public void addEpUserAttendee(AttendeeForMcuZj attendeeForMcuZj) {
        if (StringUtils.hasText(attendeeForMcuZj.getEpUserId())) {
            this.epUserAttendeesMap.put(attendeeForMcuZj.getEpUserId(), attendeeForMcuZj);
        }
        if (attendeeForMcuZj.getEpId() != null) {
            this.epAttendeesMap.put(attendeeForMcuZj.getEpId(), attendeeForMcuZj);
        }
    }

    public void removeEpUserAttendee(AttendeeForMcuZj attendeeForMcuZj) {
        if (StringUtils.hasText(attendeeForMcuZj.getEpUserId())) {
            this.epUserAttendeesMap.remove(attendeeForMcuZj.getEpUserId());
        }
        if (attendeeForMcuZj.getEpId() != null) {
            this.epAttendeesMap.remove(attendeeForMcuZj.getEpId());
        }
    }

    public AttendeeForMcuZj getAttendeeByEpUserId(String epUserId) {
        return this.epUserAttendeesMap.get(epUserId);
    }

    public AttendeeForMcuZj getAttendeeByEpId(Integer epId) {
        return this.epAttendeesMap.get(epId);
    }

    public void addUserInfo(UserInfo userInfo) {
        if (StringUtils.hasText(userInfo.getCall_addr())) {
            this.callAddrUserInfoMap.put(userInfo.getCall_addr(), userInfo);
        }
        this.userInfoMap.put(userInfo.getUsr_id(), userInfo);
    }

    public UserInfo getUserInfoByCallAddr(String callAddr) {
        return this.callAddrUserInfoMap.get(callAddr);
    }

    public UserInfo getUserInfo(String userId) {
        return this.userInfoMap.get(userId);
    }

    public void addEpsInfo(EpsInfo epsInfo) {
        this.epUserEpsInfoMap.put(epsInfo.getUsr_id(), epsInfo);
    }

    public EpsInfo removeEpsInfo(EpsInfo epsInfo) {
        return this.epUserEpsInfoMap.remove(epsInfo.getUsr_id());
    }

    public List<EpsInfo> getEpsInfoList() {
        if (this.epUserEpsInfoMap.size() > 0) {
            return new ArrayList<>(this.epUserEpsInfoMap.values());
        }
        return null;
    }

    public EpsInfo getEpsInfo(String userId) {
        return epUserEpsInfoMap.get(userId);
    }

    /**
     * <p>Get Method   :   syncLock Object</p>
     * @return syncLock
     */
    public Object getSyncLock()
    {
        return syncLock;
    }

    public McuZjSyncInformation getSyncInformation() {
        return syncInformation;
    }

    public void setSyncInformation(McuZjSyncInformation syncInformation) {
        this.syncInformation = syncInformation;
    }

    public void destroy()
    {
        if (httpRequester != null)
        {
            httpRequester.destroy();
        }
    }

    /**
     * 会议的ID（UUID）coSpaceId
     */
    private String id;

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
     * 会议控制密码
     */
    @JsonIgnore
    private String conferenceCtrlPassword;

    /**
     * 是否锁定
     */
    private volatile boolean locked;

    /**
     * 允许所有人静音自己
     */
    private volatile boolean allowAllMuteSelf;

    /**
     * 允许辅流控制
     */
    private volatile boolean allowAllPresentationContribution;

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
    private volatile AttendeeForMcuZj masterAttendee;

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
     * 当前正在进行的参会者操作（观众）（点名）
     */
    @JsonIgnore
    private volatile AttendeeOperation attendeeOperationForGuest;

    @JsonIgnore
    private volatile AttendeeOperation lastAttendeeOperationForGuest;

    /**
     * 默认视图（观众）
     */
    @JsonIgnore
    private volatile DefaultViewOperation defaultViewOperationForGuest;

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
    private BusiMcuZjConferenceAppointment conferenceAppointment;

    /**
     * 历史记录
     */
    @JsonIgnore
    private BusiHistoryConference historyConference;

    /**
     * 业务领域类型
     */
    private Integer businessFieldType;

    /** 业务属性 */
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
     * 会议结束时间
     */
    @JsonIgnore
    private Date endTime;

    /**
     * 此会议自身作为MCU参会的remoteParty
     */
    @JsonIgnore
    private String fmeAttendeeRemoteParty;

    /**
     * 模板会议的ID，标记出是哪个模板发起的，好从模板点击进入会议进行关联
     */
    private Long templateConferenceId;

    /**
     * 模板会议的创建时间
     */
    private Date templateCreateTime;

    /**
     * 是否自动拉终端
     */
    private boolean isAutoCallTerminal;

    /**
     * 会议主导方的参会者
     */
    private volatile List<AttendeeForMcuZj> attendees = new ArrayList<>();

    /**
     * 所有级联子会议的主会场集合
     */
    private volatile List<AttendeeForMcuZj> masterAttendees = new ArrayList<>();

    /**
     * MCU自身充当的参会者
     */
    private volatile List<McuAttendeeForMcuZj> mcuAttendees = new ArrayList<>();

    /**
     * 部门权重
     */
    private volatile List<DeptWeight> deptWeights = new ArrayList<>();

    /**
     * 级联方的参会者映射表，key为部门ID
     */
    private volatile Map<Long, List<AttendeeForMcuZj>> cascadeAttendeesMap = new ConcurrentHashMap<>();

    /**
     * 所有地州的主会场map映射（用于做逻辑判断，不暴露给前端），key：attendeeId
     */
    private volatile Set<String> masterAttendeeIdSet = new HashSet<>();

    /**
     * 所有已配置的参会者映射key为remoteParty
     */
    @JsonIgnore
    private volatile Map<String, AttendeeForMcuZj> attendeeMap = new ConcurrentHashMap<>();

    /**
     * 直播终端列表
     */
    @JsonIgnore
    private volatile List<TerminalAttendeeForMcuZj> liveTerminals = new ArrayList<>();

    /**
     * 直播终端Map key:sn
     */
    private volatile Map<String, TerminalAttendeeForMcuZj> liveTerminalMap = new ConcurrentHashMap();

    @JsonIgnore
    private volatile RemotePartyAttendeesMapForMcu remotePartyAttendeesMap = new RemotePartyAttendeesMapForMcu();

    /**
     * 下级级联信息
     */
    @JsonIgnore
    private CascadeForMcuZj cascade;

    /**
     * 上级级联信息
     */
    @JsonIgnore
    private UpCascadeForMcuZj upCascade;

    /**
     * MCU终端ID映射
     */
    @JsonIgnore
    private volatile Map<Long, TerminalAttendeeForMcuZj> terminalAttendeeMap = new ConcurrentHashMap<>();

    /**
     * 终端ID映射
     */
    @JsonIgnore
    private volatile Map<Long, AttendeeForMcuZj> terminalAttendeeAllMap = new ConcurrentHashMap<>();

    /**
     * 参会者重呼开始时间（呼叫成功后或呼叫超过30分钟后，会清除该记录）
     */
    @JsonIgnore
    private volatile Map<String, Long> recallAttendeeBeginTimeMap = new ConcurrentHashMap<>();

    /** 是否自动创建直播URL 1自动，2手动 */
    private Integer isAutoCreateStreamUrl;

    private volatile List<String> streamUrlList = new ArrayList<>();

    /** 会议中观看直播的终端数*/
    private Integer liveTerminalCount;

    @Override
    public Integer getIsAutoCreateStreamUrl() {
        return isAutoCreateStreamUrl;
    }

    public void setIsAutoCreateStreamUrl(Integer isAutoCreateStreamUrl) {
        this.isAutoCreateStreamUrl = isAutoCreateStreamUrl;
    }

    @Override
    public List<String> getStreamUrlList() {
        return streamUrlList;
    }

    public void setStreamUrlList(List<String> streamUrlList) {
        this.streamUrlList = streamUrlList;
    }

    public Integer getLiveTerminalCount() {
        return liveTerminalCount;
    }

    public void setLiveTerminalCount(Integer liveTerminalCount) {
        this.liveTerminalCount = liveTerminalCount;
    }

    //    @JsonIgnore
//    private volatile SyncInformation syncInformation;



    /** 是否启用会议时长 */
    private Integer durationEnabled;

    /** 会议时长单位分钟 */
    private Integer durationTime;

    /** 会议结束原因*/
    private Integer endReasonsType;

    private String messageBannerText;

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

    /**
     * <p>Get Method   :   name String</p>
     * @return name
     */
    public String getName()
    {
        return name;
    }

    /**
     * <p>Set Method   :   name String</p>
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * <p>Get Method   :   remarks String</p>
     * @return remarks
     */
    @Override
    public String getRemarks()
    {
        return remarks;
    }

    /**
     * <p>Set Method   :   remarks String</p>
     * @param remarks
     */
    public void setRemarks(String remarks)
    {
        this.remarks = remarks;
    }

    /**
     * <p>Get Method   :   deptId Long</p>
     * @return deptId
     */
    public Long getDeptId()
    {
        return deptId;
    }

    /**
     * <p>Set Method   :   deptId Long</p>
     * @param deptId
     */
    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    /**
     * <p>Get Method   :   bandwidth Integer</p>
     * @return bandwidth
     */
    @Override
    public Integer getBandwidth()
    {
        return bandwidth;
    }

    /**
     * <p>Set Method   :   bandwidth Integer</p>
     * @param bandwidth
     */
    public void setBandwidth(Integer bandwidth)
    {
        this.bandwidth = bandwidth;
    }

    /**
     * <p>Get Method   :   conferenceNumber Long</p>
     * @return conferenceNumber
     */
    @Override
    public String getConferenceNumber()
    {
        return conferenceNumber;
    }

    /**
     * <p>Set Method   :   conferenceNumber Long</p>
     * @param conferenceNumber
     */
    @Override
    public void setConferenceNumber(String conferenceNumber)
    {
        this.conferenceNumber = conferenceNumber;
    }

    public String getConferenceCtrlPassword() {
        return conferenceCtrlPassword;
    }

    public void setConferenceCtrlPassword(String conferenceCtrlPassword) {
        this.conferenceCtrlPassword = conferenceCtrlPassword;
    }

    /**
     * <p>Get Method   :   type Integer</p>
     * @return type
     */
    public Integer getType()
    {
        return type;
    }

    /**
     * <p>Set Method   :   type Integer</p>
     * @param type
     */
    public void setType(Integer type)
    {
        this.type = type;
    }

    /**
     * <p>Get Method   :   attendees List<TerminalAttendee></p>
     * @return attendees
     */
    @Override
    public List<AttendeeForMcuZj> getAttendees()
    {
        return attendees;
    }

    /**
     * <p>Set Method   :   attendees List<TerminalAttendee></p>
     * @param attendees
     */
    public void setAttendees(List<AttendeeForMcuZj> attendees)
    {
        this.attendees = attendees;
    }

    /**
     * 添加会议主导方的终端参会者
     * @param attendee
     */
    public synchronized void addAttendee(AttendeeForMcuZj attendee)
    {
        attendee.setConferenceNumber(conferenceNumber);
        attendee.setContextKey(getContextKey());
        if (attendee instanceof TerminalAttendeeForMcuZj)
        {
            TerminalAttendeeForMcuZj terminalAttendee = (TerminalAttendeeForMcuZj) attendee;
            TerminalAttendeeForMcuZj old = terminalAttendeeMap.get(terminalAttendee.getTerminalId());
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
    }

    public void addAttendeeToIdMap(AttendeeForMcuZj attendee)
    {
        this.attendeeMap.put(attendee.getId(), attendee);
    }

    public void addAttendeeToRemotePartyMap(AttendeeForMcuZj attendee)
    {
        if (StringUtils.hasText(attendee.getRemoteParty())) {
            remotePartyAttendeesMap.addAttendee(attendee);
        }
    }

    public Map<String, AttendeeForMcuZj> getAttendeeMapByUri(String remoteParty) {
        return remotePartyAttendeesMap.get(remoteParty);
    }

    public void addAttendeeToTerminalAllMap(AttendeeForMcuZj attendee) {
        if (attendee.getTerminalId() != null) {
            terminalAttendeeAllMap.put(attendee.getTerminalId(), attendee);
        }
    }

    public void removeAttendeeFromTerminalAllMap(AttendeeForMcuZj attendee) {
        if (attendee.getTerminalId() != null) {
            terminalAttendeeAllMap.remove(attendee.getTerminalId());
        }
    }

    @Override
    public AttendeeForMcuZj getAttendeeByTerminalId(Long terminalId) {
        if (terminalId != null) {
            return terminalAttendeeAllMap.get(terminalId);
        }
        return null;
    }

    /**
     * fcm集群调用，其它请勿调用
     *
     * @param oldRemoteParty
     * @param attendee
     */
    public void updateAttendeeToRemotePartyMap(String oldRemoteParty, AttendeeForMcuZj attendee) {
        if (StringUtils.hasText(oldRemoteParty)) {
            Map<String, AttendeeForMcuZj> uuidAttendeeMap = remotePartyAttendeesMap.get(oldRemoteParty);
            if (uuidAttendeeMap != null) {
                remotePartyAttendeesMap.remove(oldRemoteParty);
            }
        }
        if (StringUtils.hasText(attendee.getRemoteParty())) {
            remotePartyAttendeesMap.addAttendee(attendee);
        }
    }

    /**
     * <p>Get Method   :   masterAttendee Attendee</p>
     * @return masterAttendee
     */
    @Override
    public AttendeeForMcuZj getMasterAttendee()
    {
        return masterAttendee;
    }

    /**
     * 清空主会场
     * @author lilinhai
     * @since 2021-02-08 15:36 void
     */
    public synchronized void clearMasterAttendee()
    {
        this.masterAttendee = null;
    }

    /**
     * 设置主会场
     * @author lilinhai
     * @since 2021-02-08 15:36 
     * @param masterAttendee void
     */
    public synchronized void setMasterAttendee(AttendeeForMcuZj masterAttendee)
    {
        Assert.notNull(masterAttendee, "【" + SysDeptCache.getInstance().get(masterAttendee.getDeptId()).getDeptName() + "】主会场不能为空！");
        if (this.masterAttendee != null)
        {
            if (this.masterAttendee.getDeptId() == deptId.longValue())
            {
                if (!attendees.contains(this.masterAttendee)) {
                    attendees.add(this.masterAttendee);
                    Collections.sort(attendees);
                }
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
                    List<AttendeeForMcuZj> as = this.cascadeAttendeesMap.get(this.masterAttendee.getDeptId());
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
                List<AttendeeForMcuZj> as = this.cascadeAttendeesMap.get(masterAttendee.getDeptId());
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
        this.masterAttendee.setContextKey(getContextKey());
    }

    /**
     * 添加地州主会场信息
     * @author lilinhai
     * @since 2021-02-20 16:34 
     * @param masterAttendee void
     */
    public void addMasterAttendee(AttendeeForMcuZj masterAttendee)
    {
        this.masterAttendees.add(masterAttendee);
        this.masterAttendeeIdSet.add(masterAttendee.getId());
    }

    /**
     * <p>Get Method   :   masterAttendees List<Attendee></p>
     * @return masterAttendees
     */
    @Override
    public List<AttendeeForMcuZj> getMasterAttendees()
    {
        return masterAttendees;
    }

    /**
     * <p>Get Method   :   fmeAddtendees List<McuAttendee></p>
     * @return fmeAddtendees
     */
    @Override
    public List<McuAttendeeForMcuZj> getMcuAttendees()
    {
        return mcuAttendees;
    }

    /**
     * <p>Set Method   :   mcuAddtendees List<McuAttendee></p>
     * @param mcuAttendees
     */
    public void setMcuAttendees(List<McuAttendeeForMcuZj> mcuAttendees)
    {
        this.mcuAttendees = mcuAttendees;
    }

    /**
     * 添加MCU类型的终端参会者
     * @param attendee
     */
    public void addMcuAttendee(McuAttendeeForMcuZj attendee)
    {
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
    public void removeMcuAttendee(McuAttendeeForMcuZj mcuAttendee) {
        this.mcuAttendees.remove(mcuAttendee);
        this.removeAttendeeById(mcuAttendee.getId());
    }

    /**
     * 添加下级级联会议MCU参会者
     * @author lilinhai
     * @since 2021-03-04 10:02 
     * @param fmeAttendee void
     */
    public void putMcuAttendee(McuAttendeeForMcuZj fmeAttendee)
    {
        if (fmeAttendee == null)
        {
            return;
        }
        if (cascade == null)
        {
            cascade = new CascadeForMcuZj();
        }
        cascade.add(fmeAttendee);
    }

    /**
     * 添加上级级联会议MCU参会者
     * @author lilinhai
     * @since 2021-03-04 10:03 
     * @param fmeAttendee void
     */
    public void putUpMcuAttendee(UpMcuAttendeeForMcuZj fmeAttendee)
    {
        if (fmeAttendee == null)
        {
            return;
        }
        if (upCascade == null)
        {
            upCascade = new UpCascadeForMcuZj();
        }
        upCascade.add(fmeAttendee);
    }

    public UpMcuAttendeeForMcuZj getUpMcuAttendee(String conferenceNumber)
    {
        if (upCascade == null)
        {
            return null;
        }
        return upCascade.get(conferenceNumber);
    }

    public UpMcuAttendeeForMcuZj removeUpMcuAttendee(String conferenceNumber)
    {
        if (upCascade == null)
        {
            return null;
        }
        return upCascade.remove(conferenceNumber);
    }

    public McuAttendeeForMcuZj getMcuAttendee(String conferenceNumber)
    {
        if (cascade == null)
        {
            return null;
        }
        return cascade.get(conferenceNumber);
    }

    public McuAttendeeForMcuZj removeMcuAttendee(String conferenceNumber)
    {
        if (cascade == null)
        {
            return null;
        }
        return cascade.remove(conferenceNumber);
    }

    /**
     * <p>Get Method   :   cascade Cascade</p>
     * @return cascade
     */
    public CascadeForMcuZj getCascade()
    {
        return cascade;
    }

    /**
     * <p>Get Method   :   upCascade UpCascade</p>
     * @return upCascade
     */
    public UpCascadeForMcuZj getUpCascade()
    {
        return upCascade;
    }

    /**
     * <p>Get Method   :   cascadeAttendeesMap Map<Long,List<TerminalAttendee>></p>
     * @return cascadeAttendeesMap
     */
    @Override
    public Map<Long, List<AttendeeForMcuZj>> getCascadeAttendeesMap()
    {
        return cascadeAttendeesMap;
    }

    /**
     * <p>Set Method   :   cascadeAttendeesMap Map<Long,List<TerminalAttendee>></p>
     * @param cascadeAttendeesMap
     */
    public void setCascadeAttendeesMap(Map<Long, List<AttendeeForMcuZj>> cascadeAttendeesMap)
    {
        this.cascadeAttendeesMap = cascadeAttendeesMap;
    }

    /**
     * <pre>添加级联会议的参会者</pre>
     * @author lilinhai
     * @since 2021-02-02 15:09  void
     */
    public void addCascadeAttendee(AttendeeForMcuZj attendee)
    {
        attendee.setContextKey(getContextKey());
        List<AttendeeForMcuZj> cascadeAttendees = cascadeAttendeesMap.get(attendee.getDeptId());
        if (cascadeAttendees == null)
        {
            cascadeAttendees = new ArrayList<>();
            cascadeAttendeesMap.put(attendee.getDeptId(), cascadeAttendees);
        }
        cascadeAttendees.add(attendee);

        if (attendee instanceof TerminalAttendeeForMcuZj)
        {
            TerminalAttendeeForMcuZj terminalAttendee = (TerminalAttendeeForMcuZj) attendee;
            terminalAttendeeMap.put(terminalAttendee.getTerminalId(), terminalAttendee);
        }
        addAttendeeToTerminalAllMap(attendee);
    }

    /**
     * <p>Get Method   :   startTime Date</p>
     * @return startTime
     */
    @Override
    public Date getStartTime()
    {
        return startTime;
    }

    /**
     * <p>Set Method   :   startTime Date</p>
     * @param startTime
     */
    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
        this.setStart(true);
    }

    /**
     * <p>Get Method   :   endTime Date</p>
     * @return endTime
     */
    @Override
    public Date getEndTime()
    {
        return endTime;
    }

    /**
     * <p>Set Method   :   endTime Date</p>
     * @param endTime
     */
    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    /**
     * <p>Get Method   :   id String</p>
     * @return id
     */
    public String getId()
    {
        return id;
    }

    /**
     * <p>Set Method   :   id String</p>
     * @param id
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * <p>Get Method   :   templateConferenceId Long</p>
     * @return templateConferenceId
     */
    @Override
    public Long getTemplateConferenceId()
    {
        return templateConferenceId;
    }

    /**
     * <p>Set Method   :   templateConferenceId Long</p>
     * @param templateConferenceId
     */
    @Override
    public void setTemplateConferenceId(Long templateConferenceId)
    {
        this.templateConferenceId = templateConferenceId;
    }

    public Date getTemplateCreateTime() {
        return templateCreateTime;
    }

    public void setTemplateCreateTime(Date templateCreateTime) {
        this.templateCreateTime = templateCreateTime;
    }

    /**
     * <p>Get Method   :   isStart boolean</p>
     * @return isStart
     */
    @Override
    public boolean isStart()
    {
        return isStart;
    }

    /**
     * <p>Set Method   :   isStart boolean</p>
     * @param isStart
     */
    public void setStart(boolean isStart)
    {
        this.isStart = isStart;
    }

    /**
     * <p>Get Method   :   isEnd boolean</p>
     * @return isEnd
     */
    @Override
    public boolean isEnd()
    {
        return isEnd;
    }

    /**
     * <p>Set Method   :   isEnd boolean</p>
     * @param isEnd
     */
    public void setEnd(boolean isEnd)
    {
        this.isEnd = isEnd;
    }

    public Map<String, AttendeeForMcuZj> getUuidAttendeeMapByUri(String remoteParty)
    {
        return remotePartyAttendeesMap.get(remoteParty);
    }

    /**
     * <p>Get Method   :   attendeeMap Map<String,Attendee></p>
     * @return attendeeMap
     */
    @Override
    public AttendeeForMcuZj getAttendeeById(String attendeeId)
    {
        return attendeeMap.get(attendeeId);
    }

    /**
     * <p>Set Method   :   attendeeMap Map<String,Attendee></p>
     * @param id
     */
    public synchronized AttendeeForMcuZj removeAttendeeById(String id)
    {
        AttendeeForMcuZj attendee = this.attendeeMap.remove(id);
        if (attendee != null)
        {
            attendees.remove(attendee);

            this.masterAttendeeIdSet.remove(id);
            this.masterAttendees.remove(attendee);

            List<AttendeeForMcuZj> as = cascadeAttendeesMap.get(attendee.getDeptId());
            if (as != null)
            {
                as.remove(attendee);
            }

            if (attendee instanceof TerminalAttendeeForMcuZj)
            {
                TerminalAttendeeForMcuZj ta = (TerminalAttendeeForMcuZj)attendee;
                terminalAttendeeMap.remove(ta.getTerminalId());
            }

            this.remotePartyAttendeesMap.removeAttendeeByRemotePartyAndUuid(attendee.getRemoteParty(), id);
            removeRecallAttendeeBeginTime(attendee);
            removeAttendeeFromTerminalAllMap(attendee);
            removeCommonlyUsedAttendees(attendee);
        }
        return attendee;
    }

    /**
     * <p>Set Method   :   attendeeMap Map<String,Attendee></p>
     * @param attendeeMap
     */
    public void setAttendeeMap(Map<String, AttendeeForMcuZj> attendeeMap)
    {
        this.attendeeMap = attendeeMap;
    }

    /**
     * <p>Get Method   :   terminalAttendeeMap Map<Long,McuAttendee></p>
     * @return terminalAttendeeMap
     */
    @Override
    public Map<Long, TerminalAttendeeForMcuZj> getTerminalAttendeeMap()
    {
        return terminalAttendeeMap;
    }

    /**
     * <p>Set Method   :   terminalAttendeeMap Map<Long,McuAttendee></p>
     * @param terminalAttendeeMap
     */
    public void setTerminalAttendeeMap(Map<Long, TerminalAttendeeForMcuZj> terminalAttendeeMap)
    {
        this.terminalAttendeeMap = terminalAttendeeMap;
    }

    /**
     * <p>Get Method   :   attendeeOperation AttendeeOperation</p>
     * @return attendeeOperation
     */
    public AttendeeOperation getAttendeeOperation()
    {
        return attendeeOperation;
    }

    /**
     * <p>Set Method   :   attendeeOperation AttendeeOperation</p>
     * @param attendeeOperation
     */
    public void setAttendeeOperation(AttendeeOperation attendeeOperation)
    {
        this.attendeeOperation = attendeeOperation;
        this.attendeeOperation.setLastUpdateTime(0);
    }

    /**
     * <p>Get Method   :   lastAttendeeOperation AttendeeOperation</p>
     * @return lastAttendeeOperation
     */
    public AttendeeOperation getLastAttendeeOperation()
    {
        return lastAttendeeOperation;
    }

    /**
     * <p>Set Method   :   lastAttendeeOperation AttendeeOperation</p>
     * @param lastAttendeeOperation
     */
    public void setLastAttendeeOperation(AttendeeOperation lastAttendeeOperation)
    {
        this.lastAttendeeOperation = lastAttendeeOperation;
        this.lastAttendeeOperation.setLastUpdateTime(9999999999999L);
    }

    /**
     * <p>Get Method   :   defaultAttendeeOperation AttendeeOperation</p>
     * @return defaultAttendeeOperation
     */
    public DefaultViewOperation getDefaultViewOperation()
    {
        return defaultViewOperation;
    }

    /**
     * <p>Set Method   :   defaultAttendeeOperation AttendeeOperation</p>
     * @param defaultViewOperation
     */
    public void setDefaultViewOperation(DefaultViewOperation defaultViewOperation)
    {
        this.defaultViewOperation = defaultViewOperation;
    }

    /**
     * <p>Get Method   :   attendeeOperation AttendeeOperation</p>
     * @return attendeeOperation
     */
    public AttendeeOperation getAttendeeOperationForGuest()
    {
        return attendeeOperationForGuest;
    }

    /**
     * <p>Set Method   :   attendeeOperation AttendeeOperation</p>
     * @param attendeeOperationForGuest
     */
    public void setAttendeeOperationForGuest(AttendeeOperation attendeeOperationForGuest)
    {
        this.attendeeOperationForGuest = attendeeOperationForGuest;
        this.attendeeOperationForGuest.setLastUpdateTime(0);
    }

    /**
     * <p>Get Method   :   lastAttendeeOperation AttendeeOperation</p>
     * @return lastAttendeeOperation
     */
    public AttendeeOperation getLastAttendeeOperationForGuest()
    {
        return lastAttendeeOperationForGuest;
    }

    /**
     * <p>Set Method   :   lastAttendeeOperation AttendeeOperation</p>
     * @param lastAttendeeOperationForGuest
     */
    public void setLastAttendeeOperationForGuest(AttendeeOperation lastAttendeeOperationForGuest)
    {
        this.lastAttendeeOperationForGuest = lastAttendeeOperationForGuest;
        this.lastAttendeeOperationForGuest.setLastUpdateTime(9999999999999L);
    }

    /**
     * <p>Get Method   :   defaultAttendeeOperation AttendeeOperation</p>
     * @return defaultAttendeeOperation
     */
    public DefaultViewOperation getDefaultViewOperationForGuest()
    {
        return defaultViewOperationForGuest;
    }

    /**
     * <p>Set Method   :   defaultAttendeeOperation AttendeeOperation</p>
     * @param defaultViewOperationForGuest
     */
    public void setDefaultViewOperationForGuest(DefaultViewOperation defaultViewOperationForGuest)
    {
        this.defaultViewOperationForGuest = defaultViewOperationForGuest;
    }

    public boolean getRoundRobin()
    {
        return attendeeOperation != null && attendeeOperation instanceof PollingAttendeeOperation;
    }

    public boolean getRoundRobinPaused()
    {
        return getRoundRobin() && ((PollingAttendeeOperation) attendeeOperation).isPause();
    }

    public boolean isDefaultViewRunning()
    {
        return attendeeOperation == defaultViewOperation;
    }

    /**
     * <p>Get Method   :   recallAttendeeBeginTimeMap Map<String,Long></p>
     * @return recallAttendeeBeginTimeMap
     */
    public Long getRecallAttendeeBeginTime(AttendeeForMcuZj attendee)
    {
        return recallAttendeeBeginTimeMap.get(attendee.getId());
    }

    /**
     * <p>Get Method   :   recallAttendeeBeginTimeMap Map<String,Long></p>
     * @return recallAttendeeBeginTimeMap
     */
    public Long removeRecallAttendeeBeginTime(AttendeeForMcuZj attendee)
    {
        return recallAttendeeBeginTimeMap.remove(attendee.getId());
    }

    /**
     * <p>Set Method   :   recallAttendeeBeginTimeMap Map<String,Long></p>
     */
    public void addRecallAttendeeBeginTime(AttendeeForMcuZj attendee)
    {
        if (!this.recallAttendeeBeginTimeMap.containsKey(attendee.getId()))
        {
            this.recallAttendeeBeginTimeMap.put(attendee.getId(), System.currentTimeMillis());
        }
    }

    /**
     * <p>Get Method   :   fmeAttendeeRemoteParty String</p>
     * @return fmeAttendeeRemoteParty
     */
    public String getMcuAttendeeRemoteParty()
    {
        return fmeAttendeeRemoteParty;
    }

    /**
     * <p>Set Method   :   fmeAttendeeRemoteParty String</p>
     * @param attendeeIp
     */
    public void setMcuAttendeeRemoteParty(String attendeeIp)
    {
        this.fmeAttendeeRemoteParty = conferenceNumber + "@" + attendeeIp;
    }

    public AttendeeCountingStatistics getAttendeeCountingStatistics()
    {
        return new AttendeeCountingStatistics(this);
    }

    /**
     * <p>Get Method   :   isAutoCallTerminal boolean</p>
     * @return isAutoCallTerminal
     */
    public boolean isAutoCallTerminal()
    {
        return isAutoCallTerminal;
    }

    /**
     * <p>Set Method   :   isAutoCallTerminal boolean</p>
     * @param isAutoCallTerminal
     */
    public void setAutoCallTerminal(boolean isAutoCallTerminal)
    {
        this.isAutoCallTerminal = isAutoCallTerminal;
    }

    public boolean isMain()
    {
        return upCascade == null || upCascade.getMcuAttendeeMap().isEmpty();
    }

    /**
     * <p>Get Method   :   masterAttendeeIdSet Set<String></p>
     * @return masterAttendeeIdSet
     */
    public Set<String> getMasterAttendeeIdSet()
    {
        return masterAttendeeIdSet;
    }

    /**
     * 清除缓存
     * @author lilinhai
     * @since 2021-03-03 11:29  void
     */
    public void clear()
    {
        attendees.clear();
        masterAttendees.clear();
        mcuAttendees.clear();
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
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        McuZjConferenceContext other = (McuZjConferenceContext) obj;
        if (id == null)
        {
            if (other.id != null) return false;
        }
        else if (!id.equals(other.id)) return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "ConferenceContext [id=" + id + ", name=" + name + ", deptId=" + deptId + ", bandwidth=" + bandwidth
                + ", conferenceNumber=" + conferenceNumber + ", type=" + type + "]";
    }

//    /**
//     * <p>Get Method   :   callLegProfile CallLegProfile</p>
//     * @return callLegProfile
//     */
//    public CallLegProfile getCallLegProfile()
//    {
//        return callLegProfile;
//    }
//
//    /**
//     * <p>Set Method   :   callLegProfile CallLegProfile</p>
//     * @param callLegProfile
//     */
//    public void setCallLegProfile(CallLegProfile callLegProfile)
//    {
//        this.callLegProfile = callLegProfile;
//    }

    /**
     * <p>Get Method   :   isAppointment boolean</p>
     * @return isAppointment
     */
    @Override
    public boolean isAppointment()
    {
        return isAppointment;
    }

    /**
     * <p>Set Method   :   isAppointment boolean</p>
     * @param isAppointment
     */
    public void setAppointment(boolean isAppointment)
    {
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
     * @return conferenceAppointment
     */
    @Override
    public BusiMcuZjConferenceAppointment getConferenceAppointment()
    {
        return conferenceAppointment;
    }

    /**
     * <p>Set Method   :   conferenceAppointment BusiConferenceAppointment</p>
     * @param conferenceAppointment
     */
    public void setConferenceAppointment(BusiMcuZjConferenceAppointment conferenceAppointment)
    {
        this.conferenceAppointment = conferenceAppointment;
    }

    /**
     * <p>Get Method   :   historyConference BusiHistoryConference</p>
     * @return historyConference
     */
    @Override
    public BusiHistoryConference getHistoryConference()
    {
        return historyConference;
    }

    /**
     * <p>Set Method   :   historyConference BusiHistoryConference</p>
     * @param historyConference
     */
    public void setHistoryConference(BusiHistoryConference historyConference)
    {
        this.historyConference = historyConference;
    }

    /**
     * <p>Get Method   :   locked boolean</p>
     * @return locked
     */
    public boolean isLocked()
    {
        return locked;
    }

    /**
     * <p>Set Method   :   locked boolean</p>
     * @param locked
     */
    public void setLocked(boolean locked)
    {
        this.locked = locked;
    }

    /**
     * <p>Get Method   :   discuss boolean</p>
     * @return discuss
     */
    public boolean isDiscuss()
    {
        return discuss;
    }

    /**
     * <p>Set Method   :   discuss boolean</p>
     * @param discuss
     */
    public void setDiscuss(boolean discuss)
    {
        this.discuss = discuss;
    }

    /**
     * <p>Get Method   :   allowAllMuteSelf boolean</p>
     * @return allowAllMuteSelf
     */
    public boolean isAllowAllMuteSelf()
    {
        return allowAllMuteSelf;
    }

    /**
     * <p>Set Method   :   allowAllMuteSelf boolean</p>
     * @param allowAllMuteSelf
     */
    public void setAllowAllMuteSelf(boolean allowAllMuteSelf)
    {
        this.allowAllMuteSelf = allowAllMuteSelf;
    }

    /**
     * <p>Get Method   :   allowAllPresentationContribution boolean</p>
     * @return allowAllPresentationContribution
     */
    public boolean isAllowAllPresentationContribution()
    {
        return allowAllPresentationContribution;
    }

    /**
     * <p>Set Method   :   allowAllPresentationContribution boolean</p>
     * @param allowAllPresentationContribution
     */
    public void setAllowAllPresentationContribution(boolean allowAllPresentationContribution)
    {
        this.allowAllPresentationContribution = allowAllPresentationContribution;
    }

    /**
     * <p>Get Method   :   joinAudioMuteOverride boolean</p>
     * @return joinAudioMuteOverride
     */
    public boolean isJoinAudioMuteOverride()
    {
        return joinAudioMuteOverride;
    }

    /**
     * <p>Set Method   :   joinAudioMuteOverride boolean</p>
     * @param joinAudioMuteOverride
     */
    public void setJoinAudioMuteOverride(boolean joinAudioMuteOverride)
    {
        this.joinAudioMuteOverride = joinAudioMuteOverride;
    }

//    /**
//     * <p>Get Method   :   syncInformation SyncInformation</p>
//     * @return syncInformation
//     */
//    public SyncInformation getSyncInformation()
//    {
//        return syncInformation;
//    }
//
//    /**
//     * <p>Set Method   :   syncInformation SyncInformation</p>
//     * @param syncInformation
//     */
//    public void setSyncInformation(SyncInformation syncInformation)
//    {
//        this.syncInformation = syncInformation;
//    }
//
//    /**
//     * <p>Get Method   :   syncLock Object</p>
//     * @return syncLock
//     */
//    public Object getSyncLock()
//    {
//        return syncLock;
//    }

    /**
     * <p>Get Method   :   deptWeights List<DeptWeight></p>
     * @return deptWeights
     */
    public List<DeptWeight> getDeptWeights()
    {
        return deptWeights;
    }

    /**
     * <p>Set Method   :   deptWeights List<DeptWeight></p>
     * @param deptWeight
     */
    public void addDeptWeight(DeptWeight deptWeight)
    {
        this.deptWeights.add(deptWeight);
    }

    /**
     * <p>Get Method   :   businessFieldType Integer</p>
     * @return businessFieldType
     */
    public Integer getBusinessFieldType()
    {
        return businessFieldType;
    }

    /**
     * <p>Set Method   :   businessFieldType Integer</p>
     * @param businessFieldType
     */
    public void setBusinessFieldType(Integer businessFieldType)
    {
        this.businessFieldType = businessFieldType;
    }

    /**
     * <p>Get Method   :   businessProperties JSONObject</p>
     * @return businessProperties
     */
    public Map<String, Object> getBusinessProperties()
    {
        return businessProperties;
    }

    /**
     * <p>Set Method   :   businessProperties JSONObject</p>
     * @param businessProperties
     */
    public void setBusinessProperties(Map<String, Object> businessProperties)
    {
        this.businessProperties = businessProperties;
    }

    /**
     * <p>Get Method   :   liveTerminals List<BusiTerminal></p>
     * @return liveTerminals
     */
    @Override
    public List<TerminalAttendeeForMcuZj> getLiveTerminals()
    {
        return liveTerminals;
    }

    /**
     * <p>Get Method   :   liveTerminals List<BusiTerminal></p>
     * @return liveTerminals
     */
    @Override
    public TerminalAttendeeForMcuZj getLiveTerminal(String sn)
    {
        return liveTerminalMap.get(sn);
    }

    /**
     * <p>Set Method   :   liveTerminals List<BusiTerminal></p>
     * @param terminal
     */
    public void addLiveTerminal(TerminalAttendeeForMcuZj terminal)
    {
        this.liveTerminals.add(terminal);
        if (terminal.getSn() != null && terminal.getSn().length() > 0) {
            this.liveTerminalMap.put(terminal.getSn(), terminal);
        }
    }

    /**
     * <p>Get Method   :   conferencePassword String</p>
     * @return conferencePassword
     */
    @Override
    public String getConferencePassword()
    {
        return conferencePassword;
    }

    /**
     * <p>Set Method   :   conferencePassword String</p>
     * @param conferencePassword
     */
    public void setConferencePassword(String conferencePassword)
    {
        this.conferencePassword = conferencePassword;
    }

    public Integer getEndReasonsType() {
        return endReasonsType;
    }

    public void setEndReasonsType(Integer endReasonsType) {
        this.endReasonsType = endReasonsType;
    }

    public String getMessageBannerText() {
        return messageBannerText;
    }

    public void setMessageBannerText(String messageBannerText) {
        this.messageBannerText = messageBannerText;
    }

    /** 主持人(用户id) */
    private Long presenter;

    /** 会议创建者用户id */
    private Long createUserId;

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

    /**
     * 参会者映射表，key为terminalId,value为终端绑定的userId
     */
    @JsonIgnore
    private volatile Map<Long, Long> terminalUserMap = new ConcurrentHashMap<>();

    public void add(Long terminalId, Long uerId) {
        terminalUserMap.put(terminalId, uerId);
    }

    @Override
    public Long getUserIdByTerminalId(Long terminalId) {
        return terminalUserMap.get(terminalId);
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

    @Override
    public List<ModelBean> getSpeakerSplitScreenList() {
        return speakerSplitScreenList;
    }

    public void setSpeakerSplitScreenList(List<ModelBean> speakerSplitScreenList) {
        this.speakerSplitScreenList = speakerSplitScreenList;
    }

    public List<ModelBean> getGuestSplitScreenList() {
        return guestSplitScreenList;
    }

    public void setGuestSplitScreenList(List<ModelBean> guestSplitScreenList) {
        this.guestSplitScreenList = guestSplitScreenList;
    }

    public String getSupervisorPassword() {
        return supervisorPassword;
    }

    public void setSupervisorPassword(String supervisorPassword) {
        this.supervisorPassword = supervisorPassword;
    }

    public String getMcuIp() {
        if (mcuZjBridge != null) {
            if (StringUtils.hasText(mcuZjBridge.getBusiMcuZj().getProxyHost())) {
                return mcuZjBridge.getBusiMcuZj().getProxyHost();
            }
            if (StringUtils.hasText(mcuZjBridge.getBusiMcuZj().getIp())) {
                return mcuZjBridge.getBusiMcuZj().getIp();
            }
        }
        return "";
    }

    public Integer getMcuPort() {
        if (mcuZjBridge != null) {
            if (mcuZjBridge.getBusiMcuZj().getProxyPort() != null) {
                return mcuZjBridge.getBusiMcuZj().getProxyPort();
            }
            if (mcuZjBridge.getBusiMcuZj().getPort() != null) {
                return mcuZjBridge.getBusiMcuZj().getPort();
            }
        }
        return null;
    }

    @Override
    public String getMcuCallIp() {
        if (mcuZjBridge != null) {
            if (StringUtils.hasText(mcuZjBridge.getBusiMcuZj().getIp())) {
                return mcuZjBridge.getBusiMcuZj().getIp();
            }
        }
        return null;
    }

    @Override
    public Integer getMcuCallPort() {
        if (mcuZjBridge != null) {
            if (mcuZjBridge.getBusiMcuZj().getCallPort() != null) {
                return mcuZjBridge.getBusiMcuZj().getCallPort();
            }
        }
        return null;
    }

    /**
     * 静音类型 0:不静音 1:静音
     */
    private Integer muteType = 1;

    /**
     * 静音状态：1：观众静音 2：全体静音
     */
    private volatile Integer muteStatus = 2;

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
}
