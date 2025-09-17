package com.paradisecloud.fcm.fme.model.busi.attendee;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.OneSplitScreen;
import com.paradisecloud.fcm.service.conference.BaseAttendee;

/**
 * 会议与会者信息
 * @author lilinhai
 * @since 2021-02-07 10:02
 * @version V1.0
 */
public abstract class Attendee extends BaseAttendee implements Serializable, Comparable<Attendee>,Cloneable
{
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-02 14:50 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 默认布局：单分屏
     */
    private volatile String layout = OneSplitScreen.LAYOUT;
    
    @JsonIgnore
    private volatile FixedSettings fixedSettings = new FixedSettings();

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
     * @param layout
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

    /**
     * <p>Get Method   :   fixedSettings FixedSettings</p>
     * @return fixedSettings
     */
    public FixedSettings getFixedSettings()
    {
        return fixedSettings;
    }

    /**
     * <p>Set Method   :   fixedSettings FixedSettings</p>
     * @param fixedSettings
     */
    public void setFixedSettings(FixedSettings fixedSettings)
    {
        this.fixedSettings = fixedSettings;
    }
    
    public String getAttendeeType()
    {
        return getClass().getSimpleName();
    }
    /**
     * 离会
     */
    public void leaveMeeting()
    {
        setParticipantUuid(null);
        super.leaveMeeting();
    }

    /**
     * 对象属性同步（用于级联会议上下级，同一个参会者的数据同步）
     * @author lilinhai
     * @since 2021-02-07 14:04 
     * @param attendee void
     */
    public void syncTo(Attendee attendee)
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
        if (attendee instanceof TerminalAttendee && this instanceof TerminalAttendee)
        {
            TerminalAttendee terminalAttendee = (TerminalAttendee) attendee;
            TerminalAttendee thisTerminalAttendee = (TerminalAttendee) this;
            terminalAttendee.setTerminalType(thisTerminalAttendee.getTerminalType());
            terminalAttendee.setTerminalTypeName(thisTerminalAttendee.getTerminalTypeName());
        }
    }

    @Override
    public int compareTo(Attendee o)
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

    @Override
    public Attendee clone() throws CloneNotSupportedException {
        Attendee cloneAttendee = (Attendee)super.clone();

        cloneAttendee.setWeight(this.getWeight());
        cloneAttendee.setCallId(this.getCallId());
        cloneAttendee.setBroadcastStatus(this.getBroadcastStatus());
        cloneAttendee.setVideoStatus(this.getVideoStatus());
        cloneAttendee.setParticipantUuid(this.getParticipantUuid());
        cloneAttendee.setAutoCallTimes(this.getAutoCallTimes());

        cloneAttendee.setCallRequestSentTime(this.getCallRequestSentTime());
        cloneAttendee.setChooseSeeStatus(this.getChooseSeeStatus());
        cloneAttendee.setConferenceNumber(this.getConferenceNumber());
        cloneAttendee.setDeptId(this.getDeptId());
        cloneAttendee.setDeptName(this.getDeptName());
        cloneAttendee.setFixedSettings(this.getFixedSettings());
        cloneAttendee.setCallTheRollStatus(this.getCallTheRollStatus());
        cloneAttendee.setHangUp(this.isHangUp());
        cloneAttendee.setId(this.getId());
        cloneAttendee.setIp(this.getIp());
        cloneAttendee.setIpNew(this.getIpNew());
        cloneAttendee.setImportance(this.getImportance());
        cloneAttendee.setLastActiveSpeakTime(this.getLastActiveSpeakTime());
        cloneAttendee.setLayout(this.getLayout());
        cloneAttendee.setLocked(this.isLocked());
        cloneAttendee.setMeetingStatus(this.getMeetingStatus());
        cloneAttendee.setMixingStatus(this.getMixingStatus());
        cloneAttendee.setName(this.getName());
        cloneAttendee.setOnlineStatus(this.getOnlineStatus());
        cloneAttendee.setPresentStatus(this.getPresentStatus());
        cloneAttendee.setRaiseHandStatus(this.getRaiseHandStatus());
        cloneAttendee.setRoundRobinStatus(this.getRoundRobinStatus());
        cloneAttendee.setRemoteParty(this.getRemoteParty());
        cloneAttendee.setRemotePartyNew(this.getRemotePartyNew());
        cloneAttendee.setTalkStatus(this.getTalkStatus());
        return cloneAttendee;

    }
}
