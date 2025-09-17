package com.paradisecloud.fcm.fme.model.cms.participant;

import java.util.Date;
import java.util.Objects;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.ParticipantState;
import com.paradisecloud.fcm.fme.model.cms.callleg.CallLeg;

/**
 * 与会者
 *
 * @author zt1994 2019/8/27 14:06
 */
public class Participant
{
    
    private String id;

    /**
     * 终端ID（逻辑使用）
     */
    private Long terminalId;
    
    /**
     * 名称
     */
    private volatile String name;
    
    /**
     * call id
     */
    private String call;
    
    /**
     * 租户 id
     */
    private String tenant;
    
    /**
     * call bridge id
     */
    private String callBridge;
    
    /**
     * 与此参与者关联的URI
     */
    private String uri;
    
    /**
     * <pre>获取参会者IP地州</pre>
     * @author lilinhai
     * @since 2021-02-07 09:55 
     * @return String
     */
    public String getIp()
    {
        if (uri.contains("@"))
        {
            return uri.split("@")[1];
        }
        return uri;
    }
    
    /**
     * 首先由调用使用或向调用发出信号的远程地址桥
     */
    private String originalUri;
    
    /**
     * 与此参与者关联的当前活动调用腿的数量。此值只对发出请求的调用桥的本地参与者显示
     */
    private Integer numCallLegs;
    
    /**
     * 与此参与者关联的userJid
     */
    private String userJid;
    
    /**
     * 该参与者是否被认为是“激活者”
     */
    private Boolean isActivator;
    
    /**
     * 是否可用移动
     */
    private Boolean canMove;
    
    /**
     * 如果这个参与者是通过移动一个参与者创建的(by将movedParticipant参数发布到对象/调用/<调用id>/参与者)，然后id指示从原始参与者移动此参与者
     */
    private String movedParticipant;
    
    /**
     * 如果这个参与者是通过移动一个参与者创建的，那么这个ID就表示指向这个参与者所在的原始参与者的调用桥
     */
    private String movedParticipantCallBridge;
    
    /**
     * 与会者状态
     */
    private ParticipantStatus status;
    
    public boolean is(ParticipantState state)
    {
        return state == ParticipantState.convert(status.getState());
    }
    
    /**
     * 与会者配置
     */
    private volatile ParticipantConfiguration configuration;
    
    /*************自定义属性*****************************************************************************************************************************/
    
    /**
     * 参会者的开关麦信息（该对象是自定义的，但也是FME侧的通与会者关联，属于自己封装）
     */
    private volatile CallLeg callLeg;
    
    /**
     * webSocket消息中的参会者实时信息RosterUpdate
     */
    private JSONObject rosterUpdate;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 配对成功的attendeeId
     */
    private volatile String attendeeId;
    private volatile boolean isFirstSettingInMeetingCompleted;

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

    public Long getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(Long terminalId) {
        this.terminalId = terminalId;
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
     * <p>Get Method   :   call String</p>
     * @return call
     */
    public String getCall()
    {
        return call;
    }
    /**
     * <p>Set Method   :   call String</p>
     * @param call
     */
    public void setCall(String call)
    {
        this.call = call;
    }
    /**
     * <p>Get Method   :   tenant String</p>
     * @return tenant
     */
    public String getTenant()
    {
        return tenant;
    }
    /**
     * <p>Set Method   :   tenant String</p>
     * @param tenant
     */
    public void setTenant(String tenant)
    {
        this.tenant = tenant;
    }
    /**
     * <p>Get Method   :   callBridge String</p>
     * @return callBridge
     */
    public String getCallBridge()
    {
        return callBridge;
    }
    /**
     * <p>Set Method   :   callBridge String</p>
     * @param callBridge
     */
    public void setCallBridge(String callBridge)
    {
        this.callBridge = callBridge;
    }
    /**
     * <p>Get Method   :   uri String</p>
     * @return uri
     */
    public String getUri()
    {
        return uri;
    }
    /**
     * <p>Set Method   :   uri String</p>
     * @param uri
     */
    public void setUri(String uri)
    {
        this.uri = uri;
    }
    /**
     * <p>Get Method   :   originalUri String</p>
     * @return originalUri
     */
    public String getOriginalUri()
    {
        return originalUri;
    }
    /**
     * <p>Set Method   :   originalUri String</p>
     * @param originalUri
     */
    public void setOriginalUri(String originalUri)
    {
        this.originalUri = originalUri;
    }
    /**
     * <p>Get Method   :   numCallLegs Integer</p>
     * @return numCallLegs
     */
    public Integer getNumCallLegs()
    {
        return numCallLegs;
    }
    /**
     * <p>Set Method   :   numCallLegs Integer</p>
     * @param numCallLegs
     */
    public void setNumCallLegs(Integer numCallLegs)
    {
        this.numCallLegs = numCallLegs;
    }
    /**
     * <p>Get Method   :   userJid String</p>
     * @return userJid
     */
    public String getUserJid()
    {
        return userJid;
    }
    /**
     * <p>Set Method   :   userJid String</p>
     * @param userJid
     */
    public void setUserJid(String userJid)
    {
        this.userJid = userJid;
    }
    /**
     * <p>Get Method   :   isActivator Boolean</p>
     * @return isActivator
     */
    public Boolean getIsActivator()
    {
        return isActivator;
    }
    /**
     * <p>Set Method   :   isActivator Boolean</p>
     * @param isActivator
     */
    public void setIsActivator(Boolean isActivator)
    {
        this.isActivator = isActivator;
    }
    /**
     * <p>Get Method   :   canMove Boolean</p>
     * @return canMove
     */
    public Boolean getCanMove()
    {
        return canMove;
    }
    /**
     * <p>Set Method   :   canMove Boolean</p>
     * @param canMove
     */
    public void setCanMove(Boolean canMove)
    {
        this.canMove = canMove;
    }
    /**
     * <p>Get Method   :   movedParticipant String</p>
     * @return movedParticipant
     */
    public String getMovedParticipant()
    {
        return movedParticipant;
    }
    /**
     * <p>Set Method   :   movedParticipant String</p>
     * @param movedParticipant
     */
    public void setMovedParticipant(String movedParticipant)
    {
        this.movedParticipant = movedParticipant;
    }
    /**
     * <p>Get Method   :   movedParticipantCallBridge String</p>
     * @return movedParticipantCallBridge
     */
    public String getMovedParticipantCallBridge()
    {
        return movedParticipantCallBridge;
    }
    /**
     * <p>Set Method   :   movedParticipantCallBridge String</p>
     * @param movedParticipantCallBridge
     */
    public void setMovedParticipantCallBridge(String movedParticipantCallBridge)
    {
        this.movedParticipantCallBridge = movedParticipantCallBridge;
    }
    /**
     * <p>Get Method   :   status ParticipantStatus</p>
     * @return status
     */
    public ParticipantStatus getStatus()
    {
        return status;
    }
    /**
     * <p>Set Method   :   status ParticipantStatus</p>
     * @param status
     */
    public void setStatus(ParticipantStatus status)
    {
        this.status = status;
    }
    /**
     * <p>Get Method   :   configuration ParticipantConfiguration</p>
     * @return configuration
     */
    public ParticipantConfiguration getConfiguration()
    {
        return configuration;
    }
    /**
     * <p>Set Method   :   configuration ParticipantConfiguration</p>
     * @param configuration
     */
    public void setConfiguration(ParticipantConfiguration configuration)
    {
        this.configuration = configuration;
    }
    /**
     * <p>Get Method   :   callLeg CallLeg</p>
     * @return callLeg
     */
    public CallLeg getCallLeg()
    {
        return callLeg;
    }
    /**
     * <p>Set Method   :   callLeg CallLeg</p>
     * @param callLeg
     */
    public void setCallLeg(CallLeg callLeg)
    {
        this.callLeg = callLeg;
    }
    /**
     * <p>Get Method   :   rosterUpdate JSONObject</p>
     * @return rosterUpdate
     */
    public JSONObject getRosterUpdate()
    {
        return rosterUpdate;
    }
    /**
     * <p>Set Method   :   rosterUpdate JSONObject</p>
     * @param rosterUpdate
     */
    public void setRosterUpdate(JSONObject rosterUpdate)
    {
        this.rosterUpdate = rosterUpdate;
    }
    /**
     * <p>Get Method   :   createTime Date</p>
     * @return createTime
     */
    public Date getCreateTime()
    {
        return createTime;
    }
    /**
     * <p>Set Method   :   createTime Date</p>
     * @param createTime
     */
    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }
    /**
     * <p>Get Method   :   attendeeId String</p>
     * @return attendeeId
     */
    public String getAttendeeId()
    {
        return attendeeId;
    }
    /**
     * <p>Set Method   :   attendeeId String</p>
     * @param attendeeId
     */
    public void setAttendeeId(String attendeeId)
    {
        this.attendeeId = attendeeId;
    }
    /**
     * <p>Get Method   :   isFirstSettingInMeetingCompleted boolean</p>
     * @return isFirstSettingInMeetingCompleted
     */
    public boolean isFirstSettingInMeetingCompleted()
    {
        return isFirstSettingInMeetingCompleted;
    }
    /**
     * <p>Set Method   :   isFirstSettingInMeetingCompleted boolean</p>
     * @param isFirstSettingInMeetingCompleted
     */
    public void setFirstSettingInMeetingCompleted(boolean isFirstSettingInMeetingCompleted)
    {
        this.isFirstSettingInMeetingCompleted = isFirstSettingInMeetingCompleted;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Participant other = (Participant) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public String toString()
    {
        return "Participant [id=" + id + ", name=" + name + ", call=" + call + ", uri=" + uri + ", status=" + status + ", attendeeId=" + attendeeId + "]";
    }
    
    
    /**
     * 同步新对象属性
     * @author sinhy
     * @since 2022-01-06 18:31 
     * @param participant void
     */
    public synchronized void sync(Participant participant)
    {
        this.name = participant.getName();
        this.tenant = participant.getTenant();
        this.callBridge =  participant.getCallBridge();
        this.uri = participant.getUri();
        this.originalUri = participant.getOriginalUri();
        this.numCallLegs = participant.getNumCallLegs();
        
        this.userJid = participant.getUserJid();
        this.isActivator = participant.getIsActivator();
        this.canMove = participant.getCanMove();
        this.movedParticipant = participant.getMovedParticipant();
        this.movedParticipantCallBridge = participant.getMovedParticipantCallBridge();
        this.status = participant.getStatus();
        this.configuration = participant.getConfiguration();
        
        this.callLeg = participant.getCallLeg();
        this.rosterUpdate = participant.getRosterUpdate();
        this.createTime = participant.getCreateTime();
    }
}
