package com.paradisecloud.fcm.fme.model.busi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.model.BusiConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.fme.model.busi.attendee.*;
import com.paradisecloud.fcm.fme.model.busi.cascade.Cascade;
import com.paradisecloud.fcm.fme.model.busi.cascade.UpCascade;
import com.paradisecloud.fcm.fme.model.busi.core.DeptWeight;
import com.paradisecloud.fcm.fme.model.busi.core.SyncInformation;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.*;
import com.paradisecloud.fcm.fme.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.fme.model.busi.operation.DefaultViewOperation;
import com.paradisecloud.fcm.fme.model.busi.operation.PollingAttendeeOpreation;
import com.paradisecloud.fcm.fme.model.cms.CallLegProfile;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.McuBridge;
import com.paradisecloud.system.model.SysDeptCache;
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
public class ConferenceContext extends BaseConferenceContext<Attendee> implements Serializable
{

    private McuBridge mcuBridge;

    public ConferenceContext() {
        init();
    }

    public ConferenceContext(McuBridge mcuBridge) {
        setMcuBridge(mcuBridge);
    }

    private void init() {
        setMcuTypeObject(McuType.FME);
        if (this.mcuBridge != null) {
            setMcuId(mcuBridge.getMcuId());
        }
    }

    public void setMcuBridge(McuBridge mcuBridge) {
        this.mcuBridge = mcuBridge;
        init();
    }

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-02 14:04
     */
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private final Object syncLock = new Object();

    /**
     * 会议的ID
     */
    private String id;

    /**
     * 会议的（UUID）coSpaceId
     */
    @JsonIgnore
    private String coSpaceId;

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
     * 入会方案配置ID（关联FME里面的入会方案记录ID，会控端不存）
     */
    @JsonIgnore
    private volatile CallLegProfile callLegProfile;

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

    /** 直播urlList */
    private volatile List<String> streamUrlList = new ArrayList<>();

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

   private String conferenceMode = "DIRECT";

    /**********************************************************************************************************/

    /**
     * 主会场与会者
     */
    private volatile Attendee masterAttendee;

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
    private BusiConferenceAppointment conferenceAppointment;

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
     * 会议主导方的参会者
     */
    private volatile List<Attendee> attendees = new ArrayList<>();

    /**
     * 所有级联子会议的主会场集合
     */
    private volatile List<Attendee> masterAttendees = new ArrayList<>();

    /**
     * FME自身充当的参会者
     */
    private volatile List<FmeAttendee> fmeAttendees = new ArrayList<>();

    /**
     * FME自身充当的参会者
     */
    private volatile List<McuAttendee> mcuAttendees = new ArrayList<>();

    /**
     * 部门权重
     */
    private volatile List<DeptWeight> deptWeights = new ArrayList<>();

    /**
     * 级联方的参会者映射表，key为部门ID
     */
    private volatile Map<Long, List<Attendee>> cascadeAttendeesMap = new ConcurrentHashMap<>();

    /**
     * 所有地州的主会场map映射（用于做逻辑判断，不暴露给前端），key：attendeeId
     */
    private volatile Set<String> masterAttendeeIdSet = new HashSet<>();

    /**
     * 所有已配置的参会者映射key为remoteParty
     */
    @JsonIgnore
    private volatile Map<String, Attendee> attendeeMap = new ConcurrentHashMap<>();

    /**
     * 直播终端列表
     */
    @JsonIgnore
    private volatile List<TerminalAttendee> liveTerminals = new ArrayList<>();

    /**
     * 直播终端Map key:sn
     */
    private volatile Map<String, TerminalAttendee> liveTerminalMap = new ConcurrentHashMap();

    @JsonIgnore
    private volatile RemotePartyAttendeesMap remotePartyAttendeesMap = new RemotePartyAttendeesMap();

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

    /**
     * FME终端ID映射
     */
    @JsonIgnore
    private volatile Map<Long, TerminalAttendee> terminalAttendeeMap = new ConcurrentHashMap<>();

    /**
     * 终端ID映射
     */
    @JsonIgnore
    private volatile Map<Long, Attendee> terminalAttendeeAllMap = new ConcurrentHashMap<>();
    /**
     * 终端IP映射
     */
    @JsonIgnore
    private volatile Map<String, Attendee> ipAttendeeAllMap = new ConcurrentHashMap<>();

    /**
     * 参会者重呼开始时间（呼叫成功后或呼叫超过30分钟后，会清除该记录）
     */
    @JsonIgnore
    private volatile Map<String, Long> recallAttendeeBeginTimeMap = new ConcurrentHashMap<>();

    @JsonIgnore
    private volatile SyncInformation syncInformation;

    @JsonIgnore
    private String  cloudtencentId;

    @JsonIgnore
    private String  cloudHwcloudId;

    /** 是否启用会议时长 */
    private Integer durationEnabled;

    /** 会议时长单位分钟 */
    private Integer durationTime;

    /** 是否自动创建直播URL 1自动，2手动 */
    private Integer isAutoCreateStreamUrl;

    /** 主持人(用户id) */
    private Long presenter;

    /** 会议创建者用户id */
    private Long createUserId;

    /** 会议中观看直播的终端数*/
    private Integer liveTerminalCount;

    private String monitorNumber;
    private String monitorId;

    private volatile Map<String, Integer> resourcesSnapshotCount =new ConcurrentHashMap<>();

    private volatile Map<String, List<Attendee>> resourcesSnapshotAttendee =new ConcurrentHashMap<>();
    private volatile boolean startRound;

    public boolean isStartRound() {
        return startRound;
    }

    public void setStartRound(boolean startRound) {
        this.startRound = startRound;
    }

    public Map<String, Integer> getResourcesSnapshotCount() {
        return resourcesSnapshotCount;
    }

    public Map<String, List<Attendee>> getResourcesSnapshotAttendee() {
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
    public List<Attendee> getAttendees()
    {
        return attendees;
    }

    /**
     * <p>Set Method   :   attendees List<TerminalAttendee></p>
     * @param attendees
     */
    public void setAttendees(List<Attendee> attendees)
    {
        this.attendees = attendees;
    }

    /**
     * 添加会议主导方的终端参会者
     * @param attendee
     */
    public synchronized void addAttendee(Attendee attendee)
    {
        attendee.setConferenceNumber(conferenceNumber);
        attendee.setContextKey(getContextKey());
        if (attendee instanceof TerminalAttendee)
        {
            TerminalAttendee terminalAttendee = (TerminalAttendee) attendee;
            TerminalAttendee old = terminalAttendeeMap.get(terminalAttendee.getTerminalId());
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
        addAttendeeToIpAllMap(attendee);
    }

    public void addAttendeeToIdMap(Attendee attendee)
    {
        this.attendeeMap.put(attendee.getId(), attendee);
    }

    public void addAttendeeToRemotePartyMap(Attendee attendee)
    {
        if (StringUtils.hasText(attendee.getRemoteParty())) {
            remotePartyAttendeesMap.addAttendee(attendee);
        }
    }

    public RemotePartyAttendeesMap getRemotePartyAttendeesMap() {
        return remotePartyAttendeesMap;
    }

    public void addAttendeeToTerminalAllMap(Attendee attendee) {
        if (attendee.getTerminalId() != null) {
            terminalAttendeeAllMap.put(attendee.getTerminalId(), attendee);
        }
    }

    public void removeAttendeeFromTerminalAllMap(Attendee attendee) {
        if (attendee.getTerminalId() != null) {
            terminalAttendeeAllMap.remove(attendee.getTerminalId());
        }
    }

    @Override
    public Attendee getAttendeeByTerminalId(Long terminalId) {
        if (terminalId != null) {
            return terminalAttendeeAllMap.get(terminalId);
        }
        return null;
    }

    public void addAttendeeToIpAllMap(Attendee attendee) {
        if (attendee.getIp() != null) {
            ipAttendeeAllMap.put(attendee.getIp(), attendee);
        }
    }

    public void removeAttendeeFromIpAllMap(Attendee attendee) {
        if (attendee.getIp() != null) {
            ipAttendeeAllMap.remove(attendee.getIp());
        }
    }

    /**
     * fcm集群调用，其它请勿调用
     *
     * @param oldRemoteParty
     * @param attendee
     */
    @Override
    public void updateAttendeeToRemotePartyMap(String oldRemoteParty, Attendee attendee) {
        if (StringUtils.hasText(oldRemoteParty)) {
            Map<String, Attendee> uuidAttendeeMap = remotePartyAttendeesMap.get(oldRemoteParty);
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
    public Attendee getMasterAttendee()
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
    public synchronized void setMasterAttendee(Attendee masterAttendee)
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
                    List<Attendee> as = this.cascadeAttendeesMap.get(this.masterAttendee.getDeptId());
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
                List<Attendee> as = this.cascadeAttendeesMap.get(masterAttendee.getDeptId());
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
    public void addMasterAttendee(Attendee masterAttendee)
    {
        this.masterAttendees.add(masterAttendee);
        this.masterAttendeeIdSet.add(masterAttendee.getId());
    }

    /**
     * <p>Get Method   :   masterAttendees List<Attendee></p>
     * @return masterAttendees
     */
    @Override
    public List<Attendee> getMasterAttendees()
    {
        return masterAttendees;
    }

    /**
     * <p>Get Method   :   fmeAddtendees List<FmeAttendee></p>
     * @return fmeAddtendees
     */
    public List<FmeAttendee> getFmeAttendees()
    {
        return fmeAttendees;
    }

    /**
     * <p>Set Method   :   fmeAddtendees List<FmeAttendee></p>
     * @param fmeAttendees
     */
    public void setFmeAttendees(List<FmeAttendee> fmeAttendees)
    {
        this.fmeAttendees = fmeAttendees;
    }

    /**
     * <p>Get Method   :   mcuAttendees List<McuAttendee></p>
     * @return fmeAddtendees
     */
    @Override
    public List<McuAttendee> getMcuAttendees()
    {
        return mcuAttendees;
    }

    /**
     * <p>Set Method   :   fmeAddtendees List<McuAttendee></p>
     * @param mcuAttendees
     */
    public void setMcuAttendees(List<McuAttendee> mcuAttendees)
    {
        this.mcuAttendees = mcuAttendees;
    }

    /**
     * 添加FME类型的终端参会者
     * @param fmeAttendee
     */
    public void addFmeAttendee(FmeAttendee fmeAttendee)
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
     * @param attendee
     */
    public void addMcuAttendee(McuAttendee attendee)
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
    public void removeMcuAttendee(McuAttendee mcuAttendee) {
        this.mcuAttendees.remove(mcuAttendee);
        this.removeAttendeeById(mcuAttendee.getId());
    }

    /**
     * 添加下级级联会议FME参会者
     * @author lilinhai
     * @since 2021-03-04 10:02
     * @param fmeAttendee void
     */
    public void putFmeAttendee(FmeAttendee fmeAttendee)
    {
        if (fmeAttendee == null)
        {
            return;
        }
        if (cascade == null)
        {
            cascade = new Cascade();
        }
        cascade.add(fmeAttendee);
    }

    /**
     * 添加上级级联会议FME参会者
     * @author lilinhai
     * @since 2021-03-04 10:03
     * @param fmeAttendee void
     */
    public void putUpFmeAttendee(UpFmeAttendee fmeAttendee)
    {
        if (fmeAttendee == null)
        {
            return;
        }
        if (upCascade == null)
        {
            upCascade = new UpCascade();
        }
        upCascade.add(fmeAttendee);
    }

    public UpFmeAttendee getUpFmeAttendee(String conferenceNumber)
    {
        if (upCascade == null)
        {
            return null;
        }
        return upCascade.get(conferenceNumber);
    }

    public UpFmeAttendee removeUpFmeAttendee(String conferenceNumber)
    {
        if (upCascade == null)
        {
            return null;
        }
        return upCascade.remove(conferenceNumber);
    }

    public FmeAttendee getFmeAttendee(String conferenceNumber)
    {
        if (cascade == null)
        {
            return null;
        }
        return cascade.get(conferenceNumber);
    }

    public FmeAttendee removeFmeAttendee(String conferenceNumber)
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
    public Cascade getCascade()
    {
        return cascade;
    }

    /**
     * <p>Get Method   :   upCascade UpCascade</p>
     * @return upCascade
     */
    public UpCascade getUpCascade()
    {
        return upCascade;
    }

    /**
     * <p>Get Method   :   cascadeAttendeesMap Map<Long,List<TerminalAttendee>></p>
     * @return cascadeAttendeesMap
     */
    @Override
    public Map<Long, List<Attendee>> getCascadeAttendeesMap()
    {
        return cascadeAttendeesMap;
    }

    /**
     * <p>Set Method   :   cascadeAttendeesMap Map<Long,List<TerminalAttendee>></p>
     * @param cascadeAttendeesMap
     */
    public void setCascadeAttendeesMap(Map<Long, List<Attendee>> cascadeAttendeesMap)
    {
        this.cascadeAttendeesMap = cascadeAttendeesMap;
    }

    /**
     * <pre>添加级联会议的参会者</pre>
     * @author lilinhai
     * @since 2021-02-02 15:09  void
     */
    public void addCascadeAttendee(Attendee attendee)
    {
        attendee.setContextKey(getContextKey());
        List<Attendee> cascadeAttendees = cascadeAttendeesMap.get(attendee.getDeptId());
        if (cascadeAttendees == null)
        {
            cascadeAttendees = new ArrayList<>();
            cascadeAttendeesMap.put(attendee.getDeptId(), cascadeAttendees);
        }
        cascadeAttendees.add(attendee);

        if (attendee instanceof TerminalAttendee)
        {
            TerminalAttendee terminalAttendee = (TerminalAttendee) attendee;
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

    public String getCoSpaceId() {
        return coSpaceId;
    }

    public void setCoSpaceId(String coSpaceId) {
        this.coSpaceId = coSpaceId;
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

    public Map<String, Attendee> getUuidAttendeeMapByUri(String remoteParty)
    {
        return remotePartyAttendeesMap.get(remoteParty);
    }

    /**
     * <p>Get Method   :   attendeeMap Map<String,Attendee></p>
     * @return attendeeMap
     */
    @Override
    public Attendee getAttendeeById(String attendeeId)
    {
        return attendeeMap.get(attendeeId);
    }

    /**
     * <p>Set Method   :   attendeeMap Map<String,Attendee></p>
     * @param id
     */
    public synchronized Attendee removeAttendeeById(String id)
    {
        Attendee attendee = this.attendeeMap.remove(id);
        if (attendee != null)
        {
            attendees.remove(attendee);

            this.masterAttendeeIdSet.remove(id);
            this.masterAttendees.remove(attendee);

            List<Attendee> as = cascadeAttendeesMap.get(attendee.getDeptId());
            if (as != null)
            {
                as.remove(attendee);
            }

            if (attendee instanceof TerminalAttendee) {
                TerminalAttendee ta = (TerminalAttendee) attendee;
                terminalAttendeeMap.remove(ta.getTerminalId());
                if (TerminalType.isRtsp(ta.getTerminalType())) {
                    String rtsp_uri = (String) ta.getBusinessProperties().get("rtsp_uri");
                    this.remotePartyAttendeesMap.removeAttendeeByRemotePartyAndUuid(rtsp_uri, id);
                }
            }
            this.remotePartyAttendeesMap.removeAttendeeByRemotePartyAndUuid(attendee.getRemoteParty(), id);
            removeRecallAttendeeBeginTime(attendee);
            removeAttendeeFromTerminalAllMap(attendee);
            removeCommonlyUsedAttendees(attendee);
            removeAttendeeFromIpAllMap(attendee);
        }
        return attendee;
    }

    /**
     * <p>Set Method   :   attendeeMap Map<String,Attendee></p>
     * @param attendeeMap
     */
    public void setAttendeeMap(Map<String, Attendee> attendeeMap)
    {
        this.attendeeMap = attendeeMap;
    }

    /**
     * <p>Get Method   :   terminalAttendeeMap Map<Long,FmeAttendee></p>
     * @return terminalAttendeeMap
     */
    @Override
    public Map<Long, TerminalAttendee> getTerminalAttendeeMap()
    {
        return terminalAttendeeMap;
    }

    /**
     * <p>Set Method   :   terminalAttendeeMap Map<Long,FmeAttendee></p>
     * @param terminalAttendeeMap
     */
    public void setTerminalAttendeeMap(Map<Long, TerminalAttendee> terminalAttendeeMap)
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

    public boolean getRoundRobin()
    {
        return attendeeOperation != null && attendeeOperation instanceof PollingAttendeeOpreation;
    }

    public boolean getRoundRobinPaused()
    {
        return getRoundRobin() && ((PollingAttendeeOpreation)attendeeOperation).isPause();
    }

    public boolean isDefaultViewRunning()
    {
        return attendeeOperation == defaultViewOperation;
    }

    /**
     * <p>Get Method   :   recallAttendeeBeginTimeMap Map<String,Long></p>
     * @return recallAttendeeBeginTimeMap
     */
    public Long getRecallAttendeeBeginTime(Attendee attendee)
    {
        return recallAttendeeBeginTimeMap.get(attendee.getId());
    }

    /**
     * <p>Get Method   :   recallAttendeeBeginTimeMap Map<String,Long></p>
     * @return recallAttendeeBeginTimeMap
     */
    public Long removeRecallAttendeeBeginTime(Attendee attendee)
    {
        return recallAttendeeBeginTimeMap.remove(attendee.getId());
    }

    /**
     * <p>Set Method   :   recallAttendeeBeginTimeMap Map<String,Long></p>
     * @param attendee
     */
    public void addRecallAttendeeBeginTime(Attendee attendee)
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
    public String getFmeAttendeeRemoteParty()
    {
        return fmeAttendeeRemoteParty;
    }

    /**
     * <p>Set Method   :   fmeAttendeeRemoteParty String</p>
     * @param attendeeIp
     */
    public void setFmeAttendeeRemoteParty(String attendeeIp)
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
        return upCascade == null || upCascade.getFmeAttendeeMap().isEmpty();
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
        ConferenceContext other = (ConferenceContext) obj;
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

    /**
     * <p>Get Method   :   callLegProfile CallLegProfile</p>
     * @return callLegProfile
     */
    public CallLegProfile getCallLegProfile()
    {
        return callLegProfile;
    }

    /**
     * <p>Set Method   :   callLegProfile CallLegProfile</p>
     * @param callLegProfile
     */
    public void setCallLegProfile(CallLegProfile callLegProfile)
    {
        this.callLegProfile = callLegProfile;
    }

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
    public BusiConferenceAppointment getConferenceAppointment()
    {
        return conferenceAppointment;
    }

    /**
     * <p>Set Method   :   conferenceAppointment BusiConferenceAppointment</p>
     * @param conferenceAppointment
     */
    public void setConferenceAppointment(BusiConferenceAppointment conferenceAppointment)
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

    /**
     * <p>Get Method   :   syncInformation SyncInformation</p>
     * @return syncInformation
     */
    public SyncInformation getSyncInformation()
    {
        return syncInformation;
    }

    /**
     * <p>Set Method   :   syncInformation SyncInformation</p>
     * @param syncInformation
     */
    public void setSyncInformation(SyncInformation syncInformation)
    {
        this.syncInformation = syncInformation;
    }

    /**
     * <p>Get Method   :   syncLock Object</p>
     * @return syncLock
     */
    public Object getSyncLock()
    {
        return syncLock;
    }

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
    public List<TerminalAttendee> getLiveTerminals()
    {
        return liveTerminals;
    }

    /**
     * <p>Get Method   :   liveTerminals List<BusiTerminal></p>
     * @return liveTerminals
     */
    @Override
    public TerminalAttendee getLiveTerminal(String sn)
    {
        return liveTerminalMap.get(sn);
    }

    /**
     * <p>Set Method   :   liveTerminals List<BusiTerminal></p>
     * @param terminal
     */
    public void addLiveTerminal(TerminalAttendee terminal)
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

    public Attendee getAttendeeByIp(String ip) {
        return ipAttendeeAllMap.get(ip);
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

    // 支持的参数，分屏，点名，选看，对话，广播，单视角等
    private boolean supportRollCall = true;
    private boolean supportSplitScreen = true;
    private boolean supportPolling = true;
    private boolean supportChooseSee = true;
    private boolean supportTalk = true;
    private boolean supportBroadcast = true;
    private boolean singleView = false;

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

    @Override
    public List<ModelBean> getSpeakerSplitScreenList() {
        List<ModelBean> list = new ArrayList<>();
        {
            ModelBean modelBean = new ModelBean();
            modelBean.put("name", "一分屏");
            modelBean.put("value", OneSplitScreen.LAYOUT);
            modelBean.put("isDefault", false);
            list.add(modelBean);
        }
        {
            ModelBean modelBean = new ModelBean();
            modelBean.put("name", "四分屏");
            modelBean.put("value", FourSplitScreen.LAYOUT);
            modelBean.put("isDefault", false);
            list.add(modelBean);
        }
        {
            ModelBean modelBean = new ModelBean();
            modelBean.put("name", "九分屏");
            modelBean.put("value", NineSplitScreen.LAYOUT);
            modelBean.put("isDefault", false);
            list.add(modelBean);
        }
        {
            ModelBean modelBean = new ModelBean();
            modelBean.put("name", "自动");
            modelBean.put("value", AutomaticSplitScreen.LAYOUT);
            modelBean.put("isDefault", false);
            list.add(modelBean);
        }
        {
            ModelBean modelBean = new ModelBean();
            modelBean.put("name", "全等");
            modelBean.put("value", AllEqualSplitScreen.LAYOUT);
            modelBean.put("isDefault", true);
            list.add(modelBean);
        }
        {
            ModelBean modelBean = new ModelBean();
            modelBean.put("name", "一大N小");
            modelBean.put("value", OnePlusNSplitScreen.LAYOUT);
            modelBean.put("isDefault", false);
            list.add(modelBean);
        }
        return list;
    }

    @Override
    public String getMcuCallIp() {
        if (mcuBridge != null) {
            return mcuBridge.getCallIp();
        }
        return null;
    }

    @Override
    public Integer getMcuCallPort() {
        if (mcuBridge != null) {
            return mcuBridge.getCallPort();
        }
        return null;
    }

    private volatile String presentAttendeeId = null;

    @Override
    public String getPresentAttendeeId() {
        return presentAttendeeId;
    }

    public void setPresentAttendeeId(String presentAttendeeId) {
        this.presentAttendeeId = presentAttendeeId;
    }

    public String getConferenceMode() {
        return conferenceMode;
    }

    public void setConferenceMode(String conferenceMode) {
        this.conferenceMode = conferenceMode;
    }

    public String getCloudtencentId() {
        return cloudtencentId;
    }

    public void setCloudtencentId(String cloudtencentId) {
        this.cloudtencentId = cloudtencentId;
    }

    public String getMonitorNumber() {
        return monitorNumber;
    }

    public void setMonitorNumber(String monitorNumber) {
        this.monitorNumber = monitorNumber;
    }

    public String getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(String monitorId) {
        this.monitorId = monitorId;
    }

    public String getCloudHwcloudId() {
        return cloudHwcloudId;
    }

    public void setCloudHwcloudId(String cloudHwcloudId) {
        this.cloudHwcloudId = cloudHwcloudId;
    }

    /**
     * 会议主导方的参会者临时终端列表
     */
    private volatile List<Attendee> attendeesOps = new ArrayList<>();

    public List<Attendee> getAttendeesOps() {
        return attendeesOps;
    }

    public void setAttendeesOps(List<Attendee> attendeesOps) {
        this.attendeesOps = attendeesOps;
    }

    private boolean motionCapture;

    public boolean isMotionCapture() {
        return motionCapture;
    }

    public void setMotionCapture(boolean motionCapture) {
        this.motionCapture = motionCapture;
    }
}
