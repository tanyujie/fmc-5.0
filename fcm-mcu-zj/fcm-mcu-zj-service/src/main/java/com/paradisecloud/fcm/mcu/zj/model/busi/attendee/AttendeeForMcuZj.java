package com.paradisecloud.fcm.mcu.zj.model.busi.attendee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.dao.enums.CallLegEndReasonEnum;

import java.io.Serializable;

/**
 * 会议与会者信息
 * @author lilinhai
 * @since 2021-02-07 10:02
 * @version V1.0
 */
public abstract class AttendeeForMcuZj extends BaseAttendee implements Serializable, Comparable<AttendeeForMcuZj>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-02 14:50
     */
    private static final long serialVersionUID = 1L;

    /**
     * 终端ID
     */
    @JsonIgnore
    private volatile Integer epId;

    /**
     * 终端用户ID
     */
    @JsonIgnore
    private volatile String epUserId;

    /**
     * 终端类型
     * 0 自动
     * 1 H323
     * 2 SIP
     * 3 ZJCS
     * 4 WebRTC
     */
    private volatile Integer protoType;

    @JsonIgnore
    private volatile CallLegEndReasonEnum callLegEndReasonEnum;

    /**
     * 默认布局：单分屏
     */
    private volatile String layout = "1";

    /**
     *
     */
    @JsonIgnore
    private volatile String lastControlCameraMove;

    /**
     * 最后控制摄像头
     */
    @JsonIgnore
    private volatile long lastControlCameraTime;

    /**
     * 正在说话
     */
    private volatile boolean attendeeSpeaker;

    public Integer getEpId() {
        return epId;
    }

    public void setEpId(Integer epId) {
        this.epId = epId;
    }

    public String getEpUserId() {
        return epUserId;
    }

    public void setEpUserId(String epUserId) {
        this.epUserId = epUserId;
    }

    public Integer getProtoType() {
        return protoType;
    }

    public void setProtoType(Integer protoType) {
        this.protoType = protoType;
    }

    public CallLegEndReasonEnum getCallLegEndReasonEnum() {
        return callLegEndReasonEnum;
    }

    public void setCallLegEndReasonEnum(CallLegEndReasonEnum callLegEndReasonEnum) {
        this.callLegEndReasonEnum = callLegEndReasonEnum;
    }

    /**
     * <p>Get Method   :   defaultLayout String</p>
     * @return defaultLayout
     */
    public String getLayout()
    {
        return layout;
    }

    /**
     * <p>Set Method   :   defaultLayout String</p>
     */
    public void setLayout(String layout)
    {
        if (!this.layout.equals(layout))
        {
            this.layout = layout;
            updateLayout();
        }
    }

    public synchronized void updateLayout()
    {
        updateMap.put("layout", layout);
    }

    public String getLastControlCameraMove() {
        return lastControlCameraMove;
    }

    public void setLastControlCameraMove(String lastControlCameraMove) {
        this.lastControlCameraMove = lastControlCameraMove;
    }

    public long getLastControlCameraTime() {
        return lastControlCameraTime;
    }

    public void setLastControlCameraTime(long lastControlCameraTime) {
        this.lastControlCameraTime = lastControlCameraTime;
    }

    public boolean isAttendeeSpeaker() {
        return attendeeSpeaker;
    }

    public void setAttendeeSpeaker(boolean attendeeSpeaker) {
        this.attendeeSpeaker = attendeeSpeaker;
    }

    /**
     * 对象属性同步（用于级联会议上下级，同一个参会者的数据同步）
     * @author lilinhai
     * @since 2021-02-07 14:04
     * @param attendee void
     */
    public void syncTo(AttendeeForMcuZj attendee)
    {
        attendee.resetUpdateMap();
        attendee.setName(getName());
        attendee.setOnlineStatus(getOnlineStatus());
        attendee.setMeetingStatus(getMeetingStatus());
        attendee.setChooseSeeStatus(getChooseSeeStatus());
        attendee.setTalkStatus(getTalkStatus());
        attendee.setCallTheRollStatus(getCallTheRollStatus());
        attendee.setMixingStatus(getMixingStatus());
        attendee.setVideoStatus(getVideoStatus());
        attendee.setPresentStatus(getPresentStatus());
        attendee.setRoundRobinStatus(getRoundRobinStatus());
        attendee.setParticipantUuid(getParticipantUuid());
        attendee.setBroadcastStatus(getBroadcastStatus());
        attendee.setRaiseHandStatus(getRaiseHandStatus());
        attendee.setLastActiveSpeakTime(getLastActiveSpeakTime());
        attendee.setCallId(getCallId());
        attendee.setImportance(getImportance());
        attendee.setHangUp(isHangUp());

        // 已配置的终端与会者
        if (attendee instanceof TerminalAttendeeForMcuZj && this instanceof TerminalAttendeeForMcuZj)
        {
            TerminalAttendeeForMcuZj terminalAttendee = (TerminalAttendeeForMcuZj) attendee;
            TerminalAttendeeForMcuZj thisTerminalAttendee = (TerminalAttendeeForMcuZj) this;
            terminalAttendee.setTerminalType(thisTerminalAttendee.getTerminalType());
            terminalAttendee.setTerminalTypeName(thisTerminalAttendee.getTerminalTypeName());
        }
    }

    @Override
    public int compareTo(AttendeeForMcuZj o)
    {
        return o.getWeight().compareTo(this.getWeight());
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Attendee [id=")
                .append(getId())
                .append(", participantUuid=")
                .append(getParticipantUuid())
                .append(", attendeeType=")
                .append(getAttendeeType())
                .append(", ip=")
                .append(getIp())
                .append(", layout=")
                .append(getLayout())
                .append(", conferenceNumber=")
                .append(getConferenceNumber())
                .append(", remoteParty=")
                .append(getRemoteParty())
                .append(", name=")
                .append(getName())
                .append(", importance=")
                .append(getImportance())
                .append(", meetingStatus=")
                .append(getMeetingStatus())
                .append("]");
        return builder.toString();
    }
}
