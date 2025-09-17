package com.paradisecloud.fcm.service.conference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.system.model.SysDeptCache;

import java.util.HashMap;
import java.util.Map;

public class BaseAttendee {

    @JsonIgnore
    private String contextKey;
    private boolean commonlyUsed;
    @JsonIgnore
    private boolean upCascadeRollCall;
    @JsonIgnore
    private boolean upCascadeBroadcast;
    @JsonIgnore
    private boolean otherImportance;

    /**
     * 发送到前端前的增量更新map
     */
    @JsonIgnore
    protected volatile Map<String, Object> updateMap = new HashMap<>();

    /**
     * 与会者UUID，web服务端自动生成（java生成），丢给前端展示用
     */
    private String id;

    /**
     * 呼入会后fme侧返回的真实与会者ID
     */
//    @JsonIgnore
    private volatile String participantUuid;

    /**
     * 终端IP
     */
    private String ip;

    /**
     * 终端IP（IP和域名使用）
     */
    private String ipNew;

    /**
     * 参会者所属的conferenceNumber
     */
    @JsonIgnore
    protected String conferenceNumber;

    /**
     * 参会者的remoteParty
     */
    private String remoteParty;

    /**
     * 参会者的remoteParty（IP和域名使用）
     */
    private String remotePartyNew;

    /**
     * 参会者属于哪个call
     */
    @JsonIgnore
    private String callId;

    private String dtmfStr;

    /**
     * 与会者名字
     */
    private volatile String name;

    /**
     * 与会者显示顺序，越大越靠前
     */
    private Integer weight;

    /**
     * 权重值（参数下发给fme后，回调成功进行绑定设置）
     */
    @JsonIgnore
    private volatile Integer importance;
    /**
     * 是否是主动挂断（页面上点挂断属于主动挂断），默认false
     */
    @JsonIgnore
    private volatile boolean isHangUp;

    /**
     * 与会者在线状态，1在线，2离线
     */
    private volatile int onlineStatus = TerminalOnlineStatus.OFFLINE.getValue();

    /**
     * 与会者在会状态，1在会，2离会
     */
    private volatile int meetingStatus = AttendeeMeetingStatus.OUT.getValue();

    /**
     * 与会者选看状态，2未选看，1选看
     */
    private volatile int chooseSeeStatus = AttendeeChooseSeeStatus.NO.getValue();

    /**
     * 与会者对话状态，2未对话，1对话
     */
    private volatile int talkStatus = AttendeeTalkStatus.NO.getValue();

    /**
     * 与会者点名状态
     */
    private volatile int callTheRollStatus = AttendeeCallTheRollStatus.NO.getValue();

    /**
     * 与会者广播状态
     */
    private volatile int broadcastStatus = BroadcastStatus.NO.getValue();

    /**
     * 与会者混音状态
     */
    private volatile int mixingStatus = AttendeeMixingStatus.NO.getValue();

    /**
     * 镜头状态
     */
    private volatile int videoStatus = AttendeeVideoStatus.NO.getValue();

    /**
     * 双流状态
     */
    private volatile int presentStatus = YesOrNo.NO.getValue();

    /**
     * 与会者轮询状态
     */
    private volatile int roundRobinStatus = AttendeeRoundRobinStatus.NO.getValue();

    /**
     * 举手
     */
    private volatile int raiseHandStatus = RaiseHandStatus.NO.getValue();

    /**
     * 扬声器状态
     */
    private volatile int speakerStatus = YesOrNo.YES.getValue();
    /**
     * 终端归属部门
     */
    private long deptId;

    /**
     * 部门名
     */
    private String deptName;

    /**
     * 是否锁定
     */
    private boolean isLocked;

    /**
     * 与会者关联终端id
     */
    private Long terminalId;

    /**
     * 与会者关联用户id
     */
    private Long userId;

    /**
     * 终端sn
     */
    private String sn;

    /**
     * 发起的自动呼叫次数，每次遇到该与会者不在线就开始做自动呼叫动作
     */
    @JsonIgnore
    private int autoCallTimes;

    /**
     * 呼叫请求发送时间
     */
    @JsonIgnore
    private Long callRequestSentTime;

    /**
     * 最后活跃讲话时间
     */
    @JsonIgnore
    private Long lastActiveSpeakTime;

    private long meetingJoinedTime = 0;

    /**
     * 默认要外呼
     */
    private int attendType = AttendType.OUT_BOUND.getValue();

    public String getContextKey() {
        return contextKey;
    }

    public void setContextKey(String contextKey) {
        this.contextKey = contextKey;
    }

    public boolean isCommonlyUsed() {
        return commonlyUsed;
    }

    public void setCommonlyUsed(boolean commonlyUsed) {
        this.commonlyUsed = commonlyUsed;
    }

    public boolean isUpCascadeRollCall() {
        return upCascadeRollCall;
    }

    public void setUpCascadeRollCall(boolean upCascadeRollCall) {
        this.upCascadeRollCall = upCascadeRollCall;
    }

    public boolean isUpCascadeBroadcast() {
        return upCascadeBroadcast;
    }

    public void setUpCascadeBroadcast(boolean upCascadeBroadcast) {
        this.upCascadeBroadcast = upCascadeBroadcast;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParticipantUuid() {
        return participantUuid;
    }

    public synchronized void setParticipantUuid(String participantUuid) {
        this.participantUuid = participantUuid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        if (ip != null && !ip.equals(this.ip)) {
            this.ip = ip;
            updateIp();
            setIpNew(ip);
        }
    }

    public String getIpNew() {
        return ipNew;
    }

    public void setIpNew(String ipNew) {
        this.ipNew = ipNew;
    }

    public String getConferenceNumber() {
        return conferenceNumber;
    }

    public void setConferenceNumber(String conferenceNumber) {
        this.conferenceNumber = conferenceNumber;
    }

    public void setRemoteParty(String remoteParty) {
        this.remoteParty = remoteParty;
        setRemotePartyNew(remoteParty);
    }

    public String getRemoteParty() {
        return remoteParty;
    }

    public String getRemotePartyNew() {
        return remotePartyNew;
    }

    public void setRemotePartyNew(String remotePartyNew) {
        this.remotePartyNew = remotePartyNew;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            updateName();
        }
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getImportance() {
        return importance;
    }

    public synchronized void setImportance(Integer importance) {
        if (!importanceEqual(importance)) {
            this.importance = importance;
            updateMap.put("importance", importance);
        }
    }

    public boolean importanceEqual(Integer importance) {
        if (this.importance != null && importance != null) {
            return this.importance.intValue() == importance.intValue();
        }

        if (this.importance == null && importance == null) {
            return true;
        }

        return false;
    }


    public boolean isHangUp() {
        return isHangUp;
    }

    public void setHangUp(boolean isHangUp) {
        if (isHangUp != this.isHangUp) {
            this.isHangUp = isHangUp;
            updateMap.put("isHangUp", isHangUp);
            setAutoCallTimes(0);
        }
    }


    public int getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(int onlineStatus) {
        if (onlineStatus != this.onlineStatus) {
            this.onlineStatus = onlineStatus;
            updateOnlineStatus();
            setAutoCallTimes(0);
        }
    }

    public boolean isOnline() {
        return onlineStatus == TerminalOnlineStatus.ONLINE.getValue();
    }

    public int getMeetingStatus() {
        return meetingStatus;
    }

    public boolean isMeetingJoined() {
        return meetingStatus == AttendeeMeetingStatus.IN.getValue();
    }

    public void setMeetingStatus(int meetingStatus) {
        if (this.meetingStatus != meetingStatus) {
            this.meetingStatus = meetingStatus;
            updateMeetingStatus();
        }
        if (this.meetingStatus == AttendeeMeetingStatus.IN.getValue()) {
            setMeetingJoinedTime(System.currentTimeMillis());
        } else {
            setMeetingJoinedTime(0);
        }
    }


    /**
     * <p>Get Method   :   chooseSeeStatus int</p>
     *
     * @return chooseSeeStatus
     */
    public int getChooseSeeStatus() {
        return chooseSeeStatus;
    }

    /**
     * <p>Get Method   :   talkStatus int</p>
     *
     * @return talkStatus
     */
    public int getTalkStatus() {
        return talkStatus;
    }

    /**
     * <p>Get Method   :   broadcastStatus int</p>
     *
     * @return broadcastStatus
     */
    public int getBroadcastStatus() {
        return broadcastStatus;
    }

    /**
     * <p>Set Method   :   chooseSeeStatus int</p>
     *
     * @param chooseSeeStatus
     */
    public void setChooseSeeStatus(int chooseSeeStatus) {
        if (this.chooseSeeStatus != chooseSeeStatus) {
            this.chooseSeeStatus = chooseSeeStatus;
            updateChooseSeeStatus();
        }
    }

    /**
     * <p>Set Method   :   talkStatus int</p>
     *
     * @param talkStatus
     */
    public void setTalkStatus(int talkStatus) {
        if (this.talkStatus != talkStatus) {
            this.talkStatus = talkStatus;
            updateTalkStatus();
        }
    }


    public void setBroadcastStatus(int broadcastStatus) {
        if (this.broadcastStatus != broadcastStatus) {
            this.broadcastStatus = broadcastStatus;
            updateBroadcastStatus();
        }
    }


    public int getCallTheRollStatus() {
        return callTheRollStatus;
    }

    public void setCallTheRollStatus(int callTheRollStatus) {
        if (this.callTheRollStatus != callTheRollStatus) {
            this.callTheRollStatus = callTheRollStatus;
            updateCallTheRollStatus();
        }
    }

    public int getMixingStatus() {
        return mixingStatus;
    }

    public void setMixingStatus(int mixingStatus) {
        if (this.mixingStatus != mixingStatus) {
            this.mixingStatus = mixingStatus;
            updateMixingStatus();
        }
    }

    public void setVideoStatus(int videoStatus) {
        if (this.videoStatus != videoStatus) {
            this.videoStatus = videoStatus;
            updateVideoStatus();
        }
    }

    public int getVideoStatus() {
        return videoStatus;
    }


    public int getPresentStatus() {
        return presentStatus;
    }

    public void setPresentStatus(int presentStatus) {
        if (this.presentStatus != presentStatus) {
            this.presentStatus = presentStatus;
            updatePresentStatus();
        }
    }

    public int getRoundRobinStatus() {
        return roundRobinStatus;
    }

    public void setRoundRobinStatus(int roundRobinStatus) {
        if (this.roundRobinStatus != roundRobinStatus) {
            this.roundRobinStatus = roundRobinStatus;
            updateRoundRobinStatus();
        }
    }


    public int getRaiseHandStatus() {
        return raiseHandStatus;
    }

    public void setRaiseHandStatus(int raiseHandStatus) {
        if (raiseHandStatus != this.raiseHandStatus) {
            this.raiseHandStatus = raiseHandStatus;
            updateRaiseHandStatus();
        }
    }

    public long getDeptId() {
        return deptId;
    }

    public void setDeptId(long deptId) {
        this.deptId = deptId;
        if (this.deptId > 0) {
            this.deptName = SysDeptCache.getInstance().get(deptId).getDeptName();
        }
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }


    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean isLocked) {
        if (this.isLocked != isLocked) {
            this.isLocked = isLocked;
            updateMap.put("locked", isLocked);
        }
    }


    public Long getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(Long terminalId) {
        this.terminalId = terminalId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public int getAutoCallTimes() {
        return autoCallTimes;
    }

    public void setAutoCallTimes(int autoCallTimes) {
        this.autoCallTimes = autoCallTimes;
    }

    public void setCallRequestSentTime(Long callRequestSentTime) {
        this.callRequestSentTime = callRequestSentTime;
    }

    public Long getCallRequestSentTime() {
        return callRequestSentTime;
    }

    public Long getLastActiveSpeakTime() {
        return lastActiveSpeakTime;
    }

    public void setLastActiveSpeakTime(Long lastActiveSpeakTime) {
        this.lastActiveSpeakTime = lastActiveSpeakTime;
    }

    public long getMeetingJoinedTime() {
        return meetingJoinedTime;
    }

    public void setMeetingJoinedTime(long meetingJoinedTime) {
        this.meetingJoinedTime = meetingJoinedTime;
    }

    public int getAttendType() {
        return attendType;
    }

    public void setAttendType(int attendType) {
        this.attendType = attendType;
    }

    public Map<String, Object> getUpdateMap() {
        return updateMap;
    }

    public synchronized void resetUpdateMap() {
        updateMap.clear();
        updateMap.put("id", id);
    }

    public boolean containsUpdateField(String fieldName) {
        return fieldName != null && updateMap.containsKey(fieldName);
    }

    public synchronized void updateMeetingStatus() {
        updateMap.put("meetingStatus", meetingStatus);
    }

    public synchronized void updateOnlineStatus() {
        updateMap.put("onlineStatus", onlineStatus);
    }

    public synchronized void updateRaiseHandStatus() {
        updateMap.put("raiseHandStatus", raiseHandStatus);
    }

    public synchronized void updateName() {
        updateMap.put("name", name);
    }

    public synchronized void updateIp() {
        updateMap.put("ip", ip);
    }

    public synchronized void updateChooseSeeStatus() {
        updateMap.put("chooseSeeStatus", chooseSeeStatus);
    }

    public synchronized void updateTalkStatus() {
        updateMap.put("talkStatus", talkStatus);
    }

    public synchronized void updateCallTheRollStatus() {
        updateMap.put("callTheRollStatus", callTheRollStatus);
    }

    public synchronized void updateMixingStatus() {
        updateMap.put("mixingStatus", mixingStatus);
    }

    public synchronized void updateVideoStatus() {
        updateMap.put("videoStatus", videoStatus);
    }

    public synchronized void updatePresentStatus() {
        updateMap.put("presentStatus", presentStatus);
    }

    public synchronized void updateRoundRobinStatus() {
        updateMap.put("roundRobinStatus", roundRobinStatus);
    }

    public synchronized void updateBroadcastStatus() {
        updateMap.put("broadcastStatus", broadcastStatus);
    }

    public synchronized void updateSpeakerStatus() {
        updateMap.put("speakerStatus", speakerStatus);
    }
    public int getSpeakerStatus() {
        return speakerStatus;
    }

    public void setSpeakerStatus(int speakerStatus) {
        if (speakerStatus != this.speakerStatus) {
            this.speakerStatus = speakerStatus;
            updateSpeakerStatus();
        }
    }
    /**
     * 离会
     */
    public void leaveMeeting()
    {
        setMeetingStatus(AttendeeMeetingStatus.OUT.getValue());
//        setParticipantUuid(null);
        setCallRequestSentTime(null);

        // 离会后清除轮询，选看和点名状态
        setBroadcastStatus(BroadcastStatus.NO.getValue());
        setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
        setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
        setTalkStatus(AttendeeTalkStatus.NO.getValue());
        setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
        setMixingStatus(AttendeeMixingStatus.NO.getValue());
        setVideoStatus(AttendeeVideoStatus.NO.getValue());
        setPresentStatus(YesOrNo.NO.getValue());
        setLocked(false);
        updateBroadcastStatus();
        updateRoundRobinStatus();
        updateChooseSeeStatus();
        updateTalkStatus();
        updateCallTheRollStatus();
        updateMixingStatus();
        updateMeetingStatus();
        updateVideoStatus();
    }

    public String getAttendeeType() {
        return getClass().getSimpleName();
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        BaseAttendee other = (BaseAttendee) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }

    // 级联会议相关
    /**
     * 会议id
     */
    private String cascadeConferenceId;

    private String cascadeMcuType;

    private Long cascadeTemplateId;

    /**
     * 上级会议id
     */
    private String upCascadeConferenceId;

    private int upCascadeIndex;

    public String getCascadeConferenceId() {
        return cascadeConferenceId;
    }

    public void setCascadeConferenceId(String cascadeConferenceId) {
        this.cascadeConferenceId = cascadeConferenceId;
    }

    public String getCascadeMcuType() {
        return cascadeMcuType;
    }

    public void setCascadeMcuType(String cascadeMcuType) {
        this.cascadeMcuType = cascadeMcuType;
    }

    public Long getCascadeTemplateId() {
        return cascadeTemplateId;
    }

    public void setCascadeTemplateId(Long cascadeTemplateId) {
        this.cascadeTemplateId = cascadeTemplateId;
        if (getIp() != null && getIp().length() > 0) {
            setRemoteParty(cascadeTemplateId + "@" + getIp());
        }
    }

    public String getUpCascadeConferenceId() {
        return upCascadeConferenceId;
    }

    public void setUpCascadeConferenceId(String upCascadeConferenceId) {
        this.upCascadeConferenceId = upCascadeConferenceId;
    }

    public int getUpCascadeIndex() {
        return upCascadeIndex;
    }

    public void setUpCascadeIndex(int upCascadeIndex) {
        this.upCascadeIndex = upCascadeIndex;
    }

    public boolean isMcuAttendee() {
        return false;
    }

    public String getDtmfStr() {
        return dtmfStr;
    }

    public void setDtmfStr(String dtmfStr) {
        this.dtmfStr = dtmfStr;
    }

    private boolean masterAttendee;

    public boolean isMasterAttendee() {
        return masterAttendee;
    }

    public void setMasterAttendee(boolean masterAttendee) {
        this.masterAttendee = masterAttendee;
    }

    public boolean isRecorder() {
        return false;
    }

    public boolean isLiveBroadcast() {
        return false;
    }

    public boolean isOtherImportance() {
        return otherImportance;
    }

    public void setOtherImportance(boolean otherImportance) {
        this.otherImportance = otherImportance;
    }
}
