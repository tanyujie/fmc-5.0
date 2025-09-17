package com.paradisecloud.fcm.ding.busi.attende;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paradisecloud.fcm.ding.cache.DingConferenceContext;
import com.paradisecloud.fcm.ding.cache.DingConferenceContextCache;
import com.paradisecloud.fcm.ding.model.SmcParitipantsStateRep;
import com.paradisecloud.fcm.ding.model.operation.AttendeeOperation;
import com.paradisecloud.fcm.ding.model.operation.CallTheRollAttendeeOperation;
import com.paradisecloud.fcm.ding.model.operation.ChangeMasterAttendeeOperation;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.service.conference.BaseAttendee;


import java.io.Serializable;
import java.util.Objects;
import java.util.Vector;

/**
 * extends SmcParitipantsStateRep.ContentDTO
 * 会议与会者信息
 * @author lilinhai
 * @since 2021-02-07 10:02
 * @version V1.0
 */
public abstract class AttendeeDing extends BaseAttendee implements Serializable, Comparable<AttendeeDing>,Cloneable
{

    private static AttendeeDing instanceWithTrueValue = null;
    private static Vector<AttendeeDing> allInstances = new Vector<>();
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-02 14:50
     */
    private static final long serialVersionUID = 1L;
    private String conferenceId;
    @JsonIgnore
    private SmcParitipantsStateRep.ContentDTO smcParticipant;

    /**
     * 默认布局：单分屏
     */
    private volatile String layout = "1";

    private volatile Integer videoSwitchAttribute=YesOrNo.NO.getValue();
    /**
     * 是否锁定会议材料
     */
    private volatile boolean lockPresenter;


    private volatile Boolean isMaster=Boolean.FALSE;

    private String protocol;

    private volatile Integer volume;

    /**
     * nick_name
     */
    private String nickName;
    /**
     * ms_open_id
     */
    private String ms_open_id;
    private Integer instanceid;
    /**
     * 0: 普通成员角色
     * 1: 是创建者权限
     * 2: 主持人权限
     * 3: 创建者权限+主持人权限
     * 4: 游客
     * 5: 游客+主持人权限
     * 6: 联席主持人
     * 7:  创建者+联席主持人
     * 8: restApi接口指派的主持人
     */
    private Integer userRole;

    public AttendeeDing() {
        allInstances.add(this);
    }

    /**
     *
     */
    private volatile Boolean host;
    /**
     * 离会
     * @author sinhy
     * @since 2021-09-09 14:44  void
     */
    @Override
    public void leaveMeeting()
    {
        setMeetingStatus(AttendeeMeetingStatus.OUT.getValue());
        setParticipantUuid(null);
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
        if(isMaster){
            operationCancel();
        }
    }




    public void hangup()
    {
        setMeetingStatus(AttendeeMeetingStatus.OUT.getValue());
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
        if(isMaster){
            operationCancel();
        }
    }

    private void operationCancel() {
        try {
            DingConferenceContext DingConferenceContext = DingConferenceContextCache.getInstance().get(getContextKey());
            AttendeeOperation attendeeOperation = DingConferenceContext.getAttendeeOperation();
            if (attendeeOperation != null) {
                if (attendeeOperation instanceof ChangeMasterAttendeeOperation) {
                    ChangeMasterAttendeeOperation changeMasterAttendeeOperation = (ChangeMasterAttendeeOperation) attendeeOperation;
                    changeMasterAttendeeOperation.cancel();
                }else if (attendeeOperation instanceof CallTheRollAttendeeOperation){
                    CallTheRollAttendeeOperation callTheRollAttendeeOperation = (CallTheRollAttendeeOperation) attendeeOperation;
                    callTheRollAttendeeOperation.cancel();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void updateLayout(){
        updateMap.put("layout",layout);
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




    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }




    public void syncTo(AttendeeDing attendee)
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
        if (attendee instanceof TerminalAttendeeDing && this instanceof TerminalAttendeeDing)
        {
            TerminalAttendeeDing terminalAttendee = (TerminalAttendeeDing) attendee;
            TerminalAttendeeDing thisTerminalAttendee = (TerminalAttendeeDing) this;
            terminalAttendee.setTerminalType(thisTerminalAttendee.getTerminalType());
            terminalAttendee.setTerminalTypeName(thisTerminalAttendee.getTerminalTypeName());
        }
    }

    @Override
    public int compareTo(AttendeeDing o)
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

    public SmcParitipantsStateRep.ContentDTO getSmcParticipant() {
        return smcParticipant;
    }

    public void setSmcParticipant(SmcParitipantsStateRep.ContentDTO smcParticipant) {
        this.smcParticipant = smcParticipant;
    }

    public boolean isLockPresenter() {
        return lockPresenter;
    }

    public void setLockPresenter(boolean lockPresenter) {
        this.lockPresenter = lockPresenter;
        updateMap.put("lockPresenter", lockPresenter);
    }



    public Integer getVideoSwitchAttribute() {
        return videoSwitchAttribute;
    }

    public void setVideoSwitchAttribute(int videoSwitchAttribute) {
        if(this.videoSwitchAttribute!= videoSwitchAttribute){
            this.videoSwitchAttribute = videoSwitchAttribute;
            updateVideoSwitchAttribute();
        }
    }

    protected  void updateVideoSwitchAttribute(){
        updateMap.put("videoSwitchAttribute", videoSwitchAttribute);
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        if(!Objects.equals(volume,this.volume)){
            this.volume = volume;
            updateVolume();
        }
    }

    protected  void updateVolume(){
        updateMap.put("volume", volume);
    }

    public String getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId;
    }

    public Boolean getMaster() {
        return isMaster;
    }

    public void setMaster(Boolean master) {
        isMaster = master;
        if (isMaster) {
            instanceWithTrueValue = this;
            resetOtherInstances();
        }
    }
    private static void resetOtherInstances() {
        for (AttendeeDing instance : allInstances) {
            if (instance != instanceWithTrueValue) {
                instance.isMaster = false;
            }
        }
    }


    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        if(!Objects.equals(nickName,this.nickName)){
            this.nickName = nickName;
            updateNickName();
        }
    }

    public String getMs_open_id() {
        return ms_open_id;
    }

    public void setMs_open_id(String ms_open_id) {
        this.ms_open_id = ms_open_id;
    }

    public Integer getInstanceid() {
        return instanceid;
    }

    public void setInstanceid(Integer instanceid) {
        this.instanceid = instanceid;
    }

    public Integer getUserRole() {
        return userRole;
    }

    public void setUserRole(Integer userRole) {
        if(!Objects.equals(userRole,this.userRole)){
            this.userRole = userRole;
            updateUserRole();
        }
    }

    public Boolean getHost() {
        return host;
    }

    public void setHost(Boolean host) {
        if(!Objects.equals(host,this.host)){
            this.host = host;
            updateHost();
        }
    }

    protected  void updateNickName(){
        updateMap.put("nickName", nickName);
    }

    protected  void updateUserRole(){
        updateMap.put("userRole", userRole);
    }

    protected  void updateHost(){
        updateMap.put("host", host);
    }



}
