package com.paradisecloud.fcm.fme.model.cms;

import java.util.Objects;

import com.alibaba.fastjson.JSONObject;

/**
 * call 呼叫
 *
 * @author zt1994 2019/8/23 15:26
 */
public class Call
{
    
    /**
     * 唯一id
     */
    private String id;
    
    /**
     * 如果调用表示coSpace的实例化，则该值将出现并保存coSpace的id
     */
    private String coSpace;
    
    /**
     * 在此调用的所有分布式实例中具有相同的id。
     */
    private String callCorrelator;
    
    /**
     * coSpace 这个 call 是 space 的实例化 forwarding 这是一个转发或网关 call adHoc 这是一个特别的多方通话 lyncConferencing
     * 这个call leg参与了一个Lync会议
     */
    private String callType;
    
    /**
     * 拥有此call的租户id
     */
    private String tenant;
    
    /**
     * 调用的持续时间，如调用开始后的秒数
     */
    private Integer durationSeconds;
    
    /**
     * call 可视名称
     */
    private String name;
    
    /**
     * 当前 call 中有效的 call leg 的数量
     */
    private Integer numCallLegs;
    
    /**
     * 同时出现在 call 中的 call leg 的最大数量
     */
    private Integer maxCallLegs;
    
    /**
     * 请求所在的call bridge在本地承载此调用中的参与者数量
     */
    private Integer numParticipantsLocal;
    
    /**
     * 由其他call bridge承载的此呼叫的参与者数量
     */
    private Integer numParticipantsRemote;
    
    /**
     * 托管此调用中的参与者的其他调用桥的数量
     */
    private Integer numDistributedInstances;
    
    /**
     * 只有当call leg在此调用中积极呈现时，才会出现presenterCallLeg值
     */
    private String presenterCallLeg;
    
    /**
     * 指示调用是否锁定(true)或未锁定(false)。
     */
    private Boolean locked;
    
    /**
     * 如果为true，则为录制
     */
    private Boolean recording;
    
    /**
     * 如果为true，则为直播
     */
    private Boolean streaming;
    
    /**
     * 录制状态
     */
    private Boolean recordingStatus;
    
    /**
     * 直播状态
     */
    private Boolean streamingStatus;
    
    /**
     * 如果是真的，参与者有权自己静音和不静音
     */
    private Boolean allowAllMuteSelf;
    
    /**
     * 如果是真的，参与者有权发言。如果为false，则此权限取决于callLegProfile中允许的presentationcontribution。默认的是 false
     */
    private Boolean allowAllPresentationContribution;
    
    /**
     * 如果为true，新参与者在加入电话会议时将静音 如果为false，新参与者在加入电话会议时将取消静音 如果未设置，新参与者将使用来自callLegProfile的音频静音值 call
     */
    private Boolean joinAudioMuteOverride;
    
    /**
     * 要显示给call中的每个参与者的文本(仅在配置messageDuration为非零时才显示)
     */
    private String messageText;
    
    /**
     * 在屏幕上显示配置的messageText的位置(for SIP终端)
     */
    private String messagePosition;
    
    /**
     * top|middle|bottom 在屏幕上显示配置的messageText的时间(以秒为单位)。键入字符串permanent将导致该字符串被永久显示，直到重新配置为止。
     */
    private String messageDuration;
    
    /**
     * Set panePlacementSelfPaneMode = self or skip or blank or <unset> on /coSpaces, /coSpaces/<coSpace id>, /calls or /calls/<call id> as required.
     */
    private String panePlacementSelfPaneMode;
    
    /**
     * 布局窗格最高权重
     */
    private Integer panePlacementHighestImportance;
    
    /**
     * 剩余显示时间
     */
    private String messageTimeRemaining;
    
    /**
     * 如果设置，则显示此call的所有者。它可以是这个call的coSpace的meetingScheduler，也可以是这个调用的所有者的名称或所有者的Jid
     */
    private String ownerName;
    
    /**
     * 如果为真，则当没有参与者时，此调用被认为是“active for load balanced”。 这意味着对空会议的第一个调用优先负载平衡。通过将此参数设置为false，可以优先使用空会议来防止负载平衡。
     * 如果在创建(POST)操作中没有提供此参数，则默认为“true”。
     */
    private Boolean activeWhenEmpty;
    
    /**
     * 如果设置true，这个call中的一个参与者将在外部记录会议(目前只标记Skype或Lync客户记录会议)
     */
    private Boolean endpointRecording;
    
    /**
     * 不管观众是被Skype还是Lync的客户端控制住了声音。只有当这个电话是Skype/Lync会议时才会出现
     */
    private Boolean lyncAudienceMute;
    
    private JSONObject callInfo;
    
    /**
     * 所属的会议桥地址
     */
    private String bridge;
    private Object lock;

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
     * <p>Get Method   :   coSpace String</p>
     * @return coSpace
     */
    public String getCoSpace()
    {
        return coSpace;
    }

    /**
     * <p>Set Method   :   coSpace String</p>
     * @param coSpace
     */
    public void setCoSpace(String coSpace)
    {
        this.coSpace = coSpace;
    }

    /**
     * <p>Get Method   :   callCorrelator String</p>
     * @return callCorrelator
     */
    public String getCallCorrelator()
    {
        return callCorrelator;
    }

    /**
     * <p>Set Method   :   callCorrelator String</p>
     * @param callCorrelator
     */
    public void setCallCorrelator(String callCorrelator)
    {
        this.callCorrelator = callCorrelator;
    }

    /**
     * <p>Get Method   :   callType String</p>
     * @return callType
     */
    public String getCallType()
    {
        return callType;
    }

    /**
     * <p>Set Method   :   callType String</p>
     * @param callType
     */
    public void setCallType(String callType)
    {
        this.callType = callType;
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
     * <p>Get Method   :   durationSeconds Integer</p>
     * @return durationSeconds
     */
    public Integer getDurationSeconds()
    {
        return durationSeconds;
    }

    /**
     * <p>Set Method   :   durationSeconds Integer</p>
     * @param durationSeconds
     */
    public void setDurationSeconds(Integer durationSeconds)
    {
        this.durationSeconds = durationSeconds;
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
     * <p>Get Method   :   maxCallLegs Integer</p>
     * @return maxCallLegs
     */
    public Integer getMaxCallLegs()
    {
        return maxCallLegs;
    }

    /**
     * <p>Set Method   :   maxCallLegs Integer</p>
     * @param maxCallLegs
     */
    public void setMaxCallLegs(Integer maxCallLegs)
    {
        this.maxCallLegs = maxCallLegs;
    }

    /**
     * <p>Get Method   :   numParticipantsLocal Integer</p>
     * @return numParticipantsLocal
     */
    public Integer getNumParticipantsLocal()
    {
        return numParticipantsLocal;
    }

    /**
     * <p>Set Method   :   numParticipantsLocal Integer</p>
     * @param numParticipantsLocal
     */
    public void setNumParticipantsLocal(Integer numParticipantsLocal)
    {
        this.numParticipantsLocal = numParticipantsLocal;
    }

    /**
     * <p>Get Method   :   numParticipantsRemote Integer</p>
     * @return numParticipantsRemote
     */
    public Integer getNumParticipantsRemote()
    {
        return numParticipantsRemote;
    }

    /**
     * <p>Set Method   :   numParticipantsRemote Integer</p>
     * @param numParticipantsRemote
     */
    public void setNumParticipantsRemote(Integer numParticipantsRemote)
    {
        this.numParticipantsRemote = numParticipantsRemote;
    }

    /**
     * <p>Get Method   :   numDistributedInstances Integer</p>
     * @return numDistributedInstances
     */
    public Integer getNumDistributedInstances()
    {
        return numDistributedInstances;
    }

    /**
     * <p>Set Method   :   numDistributedInstances Integer</p>
     * @param numDistributedInstances
     */
    public void setNumDistributedInstances(Integer numDistributedInstances)
    {
        this.numDistributedInstances = numDistributedInstances;
    }

    /**
     * <p>Get Method   :   presenterCallLeg String</p>
     * @return presenterCallLeg
     */
    public String getPresenterCallLeg()
    {
        return presenterCallLeg;
    }

    /**
     * <p>Set Method   :   presenterCallLeg String</p>
     * @param presenterCallLeg
     */
    public void setPresenterCallLeg(String presenterCallLeg)
    {
        this.presenterCallLeg = presenterCallLeg;
    }

    /**
     * <p>Get Method   :   locked Boolean</p>
     * @return locked
     */
    public Boolean getLocked()
    {
        return locked;
    }

    /**
     * <p>Set Method   :   locked Boolean</p>
     * @param locked
     */
    public void setLocked(Boolean locked)
    {
        this.locked = locked;
    }

    /**
     * <p>Get Method   :   recording Boolean</p>
     * @return recording
     */
    public Boolean getRecording()
    {
        return recording;
    }

    /**
     * <p>Set Method   :   recording Boolean</p>
     * @param recording
     */
    public void setRecording(Boolean recording)
    {
        this.recording = recording;
    }

    /**
     * <p>Get Method   :   streaming Boolean</p>
     * @return streaming
     */
    public Boolean getStreaming()
    {
        return streaming;
    }

    /**
     * <p>Set Method   :   streaming Boolean</p>
     * @param streaming
     */
    public void setStreaming(Boolean streaming)
    {
        this.streaming = streaming;
    }

    /**
     * <p>Get Method   :   recordingStatus Boolean</p>
     * @return recordingStatus
     */
    public Boolean getRecordingStatus()
    {
        return recordingStatus;
    }

    /**
     * <p>Set Method   :   recordingStatus Boolean</p>
     * @param recordingStatus
     */
    public void setRecordingStatus(Boolean recordingStatus)
    {
        this.recordingStatus = recordingStatus;
    }

    /**
     * <p>Get Method   :   streamingStatus Boolean</p>
     * @return streamingStatus
     */
    public Boolean getStreamingStatus()
    {
        return streamingStatus;
    }

    /**
     * <p>Set Method   :   streamingStatus Boolean</p>
     * @param streamingStatus
     */
    public void setStreamingStatus(Boolean streamingStatus)
    {
        this.streamingStatus = streamingStatus;
    }

    /**
     * <p>Get Method   :   allowAllMuteSelf Boolean</p>
     * @return allowAllMuteSelf
     */
    public Boolean getAllowAllMuteSelf()
    {
        return allowAllMuteSelf;
    }

    /**
     * <p>Set Method   :   allowAllMuteSelf Boolean</p>
     * @param allowAllMuteSelf
     */
    public void setAllowAllMuteSelf(Boolean allowAllMuteSelf)
    {
        this.allowAllMuteSelf = allowAllMuteSelf;
    }

    /**
     * <p>Get Method   :   allowAllPresentationContribution Boolean</p>
     * @return allowAllPresentationContribution
     */
    public Boolean getAllowAllPresentationContribution()
    {
        return allowAllPresentationContribution;
    }

    /**
     * <p>Set Method   :   allowAllPresentationContribution Boolean</p>
     * @param allowAllPresentationContribution
     */
    public void setAllowAllPresentationContribution(Boolean allowAllPresentationContribution)
    {
        this.allowAllPresentationContribution = allowAllPresentationContribution;
    }

    /**
     * <p>Get Method   :   joinAudioMuteOverride Boolean</p>
     * @return joinAudioMuteOverride
     */
    public Boolean getJoinAudioMuteOverride()
    {
        return joinAudioMuteOverride;
    }

    /**
     * <p>Set Method   :   joinAudioMuteOverride Boolean</p>
     * @param joinAudioMuteOverride
     */
    public void setJoinAudioMuteOverride(Boolean joinAudioMuteOverride)
    {
        this.joinAudioMuteOverride = joinAudioMuteOverride;
    }

    /**
     * <p>Get Method   :   messageText String</p>
     * @return messageText
     */
    public String getMessageText()
    {
        return messageText;
    }

    /**
     * <p>Set Method   :   messageText String</p>
     * @param messageText
     */
    public void setMessageText(String messageText)
    {
        this.messageText = messageText;
    }

    /**
     * <p>Get Method   :   messagePosition String</p>
     * @return messagePosition
     */
    public String getMessagePosition()
    {
        return messagePosition;
    }

    /**
     * <p>Set Method   :   messagePosition String</p>
     * @param messagePosition
     */
    public void setMessagePosition(String messagePosition)
    {
        this.messagePosition = messagePosition;
    }

    /**
     * <p>Get Method   :   messageDuration String</p>
     * @return messageDuration
     */
    public String getMessageDuration()
    {
        return messageDuration;
    }

    /**
     * <p>Set Method   :   messageDuration String</p>
     * @param messageDuration
     */
    public void setMessageDuration(String messageDuration)
    {
        this.messageDuration = messageDuration;
    }

    /**
     * <p>Get Method   :   panePlacementSelfPaneMode String</p>
     * @return panePlacementSelfPaneMode
     */
    public String getPanePlacementSelfPaneMode()
    {
        return panePlacementSelfPaneMode;
    }

    /**
     * <p>Set Method   :   panePlacementSelfPaneMode String</p>
     * @param panePlacementSelfPaneMode
     */
    public void setPanePlacementSelfPaneMode(String panePlacementSelfPaneMode)
    {
        this.panePlacementSelfPaneMode = panePlacementSelfPaneMode;
    }

    /**
     * <p>Get Method   :   panePlacementHighestImportance Integer</p>
     * @return panePlacementHighestImportance
     */
    public Integer getPanePlacementHighestImportance()
    {
        return panePlacementHighestImportance;
    }

    /**
     * <p>Set Method   :   panePlacementHighestImportance Integer</p>
     * @param panePlacementHighestImportance
     */
    public void setPanePlacementHighestImportance(Integer panePlacementHighestImportance)
    {
        this.panePlacementHighestImportance = panePlacementHighestImportance;
    }

    /**
     * <p>Get Method   :   messageTimeRemaining String</p>
     * @return messageTimeRemaining
     */
    public String getMessageTimeRemaining()
    {
        return messageTimeRemaining;
    }

    /**
     * <p>Set Method   :   messageTimeRemaining String</p>
     * @param messageTimeRemaining
     */
    public void setMessageTimeRemaining(String messageTimeRemaining)
    {
        this.messageTimeRemaining = messageTimeRemaining;
    }

    /**
     * <p>Get Method   :   ownerName String</p>
     * @return ownerName
     */
    public String getOwnerName()
    {
        return ownerName;
    }

    /**
     * <p>Set Method   :   ownerName String</p>
     * @param ownerName
     */
    public void setOwnerName(String ownerName)
    {
        this.ownerName = ownerName;
    }

    /**
     * <p>Get Method   :   activeWhenEmpty Boolean</p>
     * @return activeWhenEmpty
     */
    public Boolean getActiveWhenEmpty()
    {
        return activeWhenEmpty;
    }

    /**
     * <p>Set Method   :   activeWhenEmpty Boolean</p>
     * @param activeWhenEmpty
     */
    public void setActiveWhenEmpty(Boolean activeWhenEmpty)
    {
        this.activeWhenEmpty = activeWhenEmpty;
    }

    /**
     * <p>Get Method   :   endpointRecording Boolean</p>
     * @return endpointRecording
     */
    public Boolean getEndpointRecording()
    {
        return endpointRecording;
    }

    /**
     * <p>Set Method   :   endpointRecording Boolean</p>
     * @param endpointRecording
     */
    public void setEndpointRecording(Boolean endpointRecording)
    {
        this.endpointRecording = endpointRecording;
    }

    /**
     * <p>Get Method   :   lyncAudienceMute Boolean</p>
     * @return lyncAudienceMute
     */
    public Boolean getLyncAudienceMute()
    {
        return lyncAudienceMute;
    }

    /**
     * <p>Set Method   :   lyncAudienceMute Boolean</p>
     * @param lyncAudienceMute
     */
    public void setLyncAudienceMute(Boolean lyncAudienceMute)
    {
        this.lyncAudienceMute = lyncAudienceMute;
    }

    /**
     * <p>Get Method   :   bridge String</p>
     * @return bridge
     */
    public String getBridge()
    {
        return bridge;
    }

    /**
     * <p>Set Method   :   bridge String</p>
     * @param bridge
     */
    public void setBridge(String bridge)
    {
        this.bridge = bridge;
    }
    
    /**
     * <p>Get Method   :   lock Object</p>
     * @return lock
     */
    public Object getLock()
    {
        return lock;
    }

    /**
     * <p>Set Method   :   lock Object</p>
     * @param lock
     */
    public void setLock(Object lock)
    {
        this.lock = lock;
    }
    
    /**
     * <p>Get Method   :   callInfo JSONObject</p>
     * @return callInfo
     */
    public JSONObject getCallInfo()
    {
        return callInfo;
    }

    /**
     * <p>Set Method   :   callInfo JSONObject</p>
     * @param callInfo
     */
    public void setCallInfo(JSONObject callInfo)
    {
        this.callInfo = callInfo;
    }

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author sinhy
     * @since 2021-09-11 14:35 
     * @return
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author sinhy
     * @since 2021-09-11 14:35 
     * @param obj
     * @return
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Call other = (Call) obj;
        return Objects.equals(id, other.id);
    }

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author sinhy
     * @since 2021-09-11 14:35 
     * @return
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "Call [id=" + id + ", durationSeconds=" + durationSeconds + ", name=" + name + ", numParticipantsLocal=" + numParticipantsLocal + "]";
    }
    
}
