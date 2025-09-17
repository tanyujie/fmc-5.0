package com.paradisecloud.smc3.busi.attende;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.operation.AttendeeOperation;
import com.paradisecloud.smc3.busi.operation.CallTheRollAttendeeOperation;
import com.paradisecloud.smc3.busi.operation.ChairmanPollingAttendeeOperation;
import com.paradisecloud.smc3.busi.operation.ChangeMasterAttendeeOperation;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.smc3.model.ChooseMultiPicInfo;
import com.paradisecloud.smc3.model.DialMode;
import com.paradisecloud.smc3.model.response.SmcParitipantsStateRep;

import java.io.Serializable;
import java.util.Objects;

/**
 * extends SmcParitipantsStateRep.ContentDTO
 * 会议与会者信息
 * @author lilinhai
 * @since 2021-02-07 10:02
 * @version V1.0
 */
public abstract class AttendeeSmc3 extends BaseAttendee implements Serializable, Comparable<AttendeeSmc3>,Cloneable
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-02 14:50
     */
    private static final long serialVersionUID = 1L;
    private String conferenceId;
    @JsonIgnore
    private SmcParitipantsStateRep.ContentDTO smcParticipant;

    private ChooseMultiPicInfo.MultiPicInfoDTO multiPicInfo;
    /**
     * 默认布局：单分屏
     */
    private volatile String layout = "1";

    private volatile Integer videoSwitchAttribute=YesOrNo.NO.getValue();
    /**
     * 是否锁定会议材料
     */
    private volatile boolean lockPresenter;

    private String protocol;

    private volatile Integer volume;

    private Integer rate = 0;

    private DialMode dialMode;

    private String dtmfInfo;

    private String serviceZoneId;

    private Integer audioProtocol;

    private Integer videoProtocol;

    private Integer videoResolution;

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public DialMode getDialMode() {
        return dialMode;
    }

    public void setDialMode(DialMode dialMode) {
        this.dialMode = dialMode;
    }

    public String getServiceZoneId() {
        return serviceZoneId;
    }

    public void setServiceZoneId(String serviceZoneId) {
        this.serviceZoneId = serviceZoneId;
    }

    public Integer getAudioProtocol() {
        return audioProtocol;
    }

    public void setAudioProtocol(Integer audioProtocol) {
        this.audioProtocol = audioProtocol;
    }

    public Integer getVideoProtocol() {
        return videoProtocol;
    }

    public void setVideoProtocol(Integer videoProtocol) {
        this.videoProtocol = videoProtocol;
    }

    public Integer getVideoResolution() {
        return videoResolution;
    }

    public void setVideoResolution(Integer videoResolution) {
        this.videoResolution = videoResolution;
    }

    public String getDtmfInfo() {
        return dtmfInfo;
    }

    public void setDtmfInfo(String dtmfInfo) {
        this.dtmfInfo = dtmfInfo;
    }

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
        if(isMaster()){
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
        if(isMaster()){
            operationCancel();
        }
    }

    private void operationCancel() {
        Smc3ConferenceContext smc3ConferenceContext = Smc3ConferenceContextCache.getInstance().get(getContextKey());
        AttendeeOperation attendeeOperation = smc3ConferenceContext.getAttendeeOperation();
        if (attendeeOperation != null) {
            if (attendeeOperation instanceof ChangeMasterAttendeeOperation) {
                ChangeMasterAttendeeOperation changeMasterAttendeeOperation = (ChangeMasterAttendeeOperation) attendeeOperation;
                changeMasterAttendeeOperation.cancel();
            }else if (attendeeOperation instanceof CallTheRollAttendeeOperation){
                CallTheRollAttendeeOperation callTheRollAttendeeOperation = (CallTheRollAttendeeOperation) attendeeOperation;
                callTheRollAttendeeOperation.cancel();
            }else if (attendeeOperation instanceof ChairmanPollingAttendeeOperation){
                ChairmanPollingAttendeeOperation chairmanPollingAttendeeOperation = (ChairmanPollingAttendeeOperation) attendeeOperation;
                chairmanPollingAttendeeOperation.cancel();
            }
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




    public void syncTo(AttendeeSmc3 attendee)
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
        if (attendee instanceof TerminalAttendeeSmc3 && this instanceof TerminalAttendeeSmc3)
        {
            TerminalAttendeeSmc3 terminalAttendee = (TerminalAttendeeSmc3) attendee;
            TerminalAttendeeSmc3 thisTerminalAttendee = (TerminalAttendeeSmc3) this;
            terminalAttendee.setTerminalType(thisTerminalAttendee.getTerminalType());
            terminalAttendee.setTerminalTypeName(thisTerminalAttendee.getTerminalTypeName());
        }
    }

    @Override
    public int compareTo(AttendeeSmc3 o)
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

    public ChooseMultiPicInfo.MultiPicInfoDTO getMultiPicInfo() {
        return multiPicInfo;
    }

    public void setMultiPicInfo(ChooseMultiPicInfo.MultiPicInfoDTO multiPicInfo) {
        this.multiPicInfo = multiPicInfo;
        updateMap.put("multiPicInfo", multiPicInfo);
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

    public boolean isMaster() {
        Smc3ConferenceContext smc3ConferenceContext = Smc3ConferenceContextCache.getInstance().get(getContextKey());
        if(smc3ConferenceContext==null){
            return false;
        }
        AttendeeSmc3 masterAttendee = smc3ConferenceContext.getMasterAttendee();
        if(masterAttendee!=null&&masterAttendee.getId().equals(this.getId())){
            return true;
        }
        return false;
    }


}
