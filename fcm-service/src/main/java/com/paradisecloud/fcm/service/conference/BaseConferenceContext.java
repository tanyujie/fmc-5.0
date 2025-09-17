package com.paradisecloud.fcm.service.conference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.PollOperateTypeDto;
import com.paradisecloud.fcm.common.model.MinutesParam;
import com.paradisecloud.fcm.dao.model.BusiConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.service.minutes.MinutesFileHandler;
import com.paradisecloud.fcm.service.model.CloudConference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseConferenceContext<T extends BaseAttendee> {

    @JsonIgnore
    private McuType mcuTypeObject;
    private volatile String upCascadeConferenceId;
    private volatile Integer upCascadeIndex;
    private Long mcuId;
    private volatile String upCascadeRemoteParty;
    private String conferenceRemoteParty;

    private Boolean multiPicBroadcastStatus;

    private Boolean lockPresenterStatus;

    private String multiPicPollStatus=PollOperateTypeDto.CANCEL.name();;

    private PollOperateTypeDto chairmanPollStatus=PollOperateTypeDto.CANCEL;

    private boolean multiScreenRollCall;

    private Integer streamingEnabled;
    private Integer recordingEnabled;
    private Integer minutesEnabled;

    private MinutesParam minutesParam;
    private Logger minutesLogger = null;
    private String createUserName;
    /**
     * 审批会议
     */
    private boolean approvedConference;

    /**
     * 是否开启直播
     */
    private volatile boolean streaming;
    /**
     * 开启直播时间
     */
    private volatile long streamingStartTime;
    /**
     * 直播地址
     */
    private volatile String streamingUrl;
    /**
     * 云直播地址
     */
    @JsonIgnore
    private volatile String cloudsStreamingUrl;
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
    private volatile T streamingAttendee;
    /**
     * 是否开启录制
     */
    private volatile boolean recorded;
    /**
     * 录制终端remoteParty
     */
    private volatile String recordingRemoteParty;
    /**
     * 录制终端
     */
    private volatile T recordingAttendee;

    /** 会议中观看直播的终端数*/
    private Integer liveTerminalCount;

    private String tencentRemoteParty;
    /**
     * 会议纪要终端remoteParty
     */
    private volatile String minutesRemoteParty;
    private volatile T minutesAttendee;
    /**
     * 是否开启会议纪要
     */
    private volatile boolean minutes;

    private volatile String dtmf;

    private String packetConferenceId;

    private List<CloudConference> cloudConferenceList=new ArrayList<>();
    private String sn;

    public String getPacketConferenceId() {
        return packetConferenceId;
    }

    public void setPacketConferenceId(String packetConferenceId) {
        this.packetConferenceId = packetConferenceId;
    }

    public String getId() {
        return null;
    }

    public void setId(String id) {
    }

    public String getTenantId() {
        return null;
    }

    public void setTenantId(String tenantId) {
    }

    public String getConferenceNumber() {
        return null;
    }

    public void setConferenceNumber(String conferenceNumber) {
    }

    public String getName() {
        return null;
    }

    public void setName(String name) {
    }

    public McuType getMcuTypeObject() {
        return this.mcuTypeObject;
    }

    public void setMcuTypeObject(McuType mcuTypeObject) {
        this.mcuTypeObject = mcuTypeObject;
    }

    public String getMcuType() {
        if (mcuTypeObject != null) {
            return mcuTypeObject.getCode();
        }
        return null;
    }

    public String getMcuTypeAlias() {
        if (mcuTypeObject != null) {
            return mcuTypeObject.getAlias();
        }
        return null;
    }

    public Long getDeptId() {
        return null;
    }

    public void setDeptId(Long deptId) {
    }

    public Long getMcuId() {
        return mcuId;
    }

    public void setMcuId(Long mcuId) {
        this.mcuId = mcuId;
    }

    public void setTemplateConferenceId(Long templateConferenceId) {
    }

    public Long getTemplateConferenceId() {
        return null;
    }

    @JsonIgnore
    public String getContextKey() {
        String key = "";
        try {
            key = EncryptIdUtil.generateContextKey(getTemplateConferenceId(), getMcuTypeObject());
        } catch (Exception e) {
        }
        return key;
    }

    public boolean isStart() {
        return false;
    }

    public boolean isEnd() {
        return false;
    }

    public T getAttendeeByTerminalId(Long terminalId) {
        return null;
    }

    public T getAttendeeById(String attendeeId) {
        return null;
    }

    public T getAttendeeByPUuid(String smcPid) {
        return null;
    }

    public T getAttendeeByRemoteParty(String remoteParty) {
        return null;
    }

    public Long getUserIdByTerminalId(Long terminalId) {
        return null;
    }

    public void setPresenter(Long presenter) {
    }

    public Long getPresenter() {
        return null;
    }

    public Long getCreateUserId() {
        return null;
    }

    public boolean isAppointment() {
        return false;
    }

    public BusiConferenceAppointment getConferenceAppointment() {
        return null;
    }

    public Map getTerminalAttendeeMap() {
        return null;
    }

    public T getLiveTerminal(String sn) {
        return null;
    }

    public Date getStartTime() {
        return null;
    }

    public Date getEndTime() {
        return null;
    }

    public String getConferencePassword() {
        return null;
    }

    public Integer getIsAutoCreateStreamUrl() {
        return null;
    }

    public List<String> getStreamUrlList() {
        return null;
    }

    public boolean isSupportRollCall() {
        return false;
    }

    public boolean isSupportSplitScreen() {
        return false;
    }

    public boolean isSupportPolling() {
        return false;
    }

    public boolean isSupportChooseSee() {
        return false;
    }

    public boolean isSupportTalk() {
        return false;
    }

    public boolean isSupportBroadcast() {
        return false;
    }

    public boolean isSingleView() {
        return false;
    }

    public List<ModelBean> getSpeakerSplitScreenList() {
        return null;
    }

    public List<T> getAttendees() {
        return null;
    }

    public T getMasterAttendee() {
        return null;
    }

    public List<T> getMasterAttendees() {
        return null;
    }

    public Integer getDurationTime() {
        return null;
    }

    public String getUpCascadeConferenceId() {
        return upCascadeConferenceId;
    }

    public void setUpCascadeConferenceId(String upCascadeConferenceId) {
        this.upCascadeConferenceId = upCascadeConferenceId;
    }

    public Integer getUpCascadeIndex() {
        return upCascadeIndex;
    }

    public void setUpCascadeIndex(Integer upCascadeIndex) {
        this.upCascadeIndex = upCascadeIndex;
    }

    public String getUpCascadeRemoteParty() {
        return upCascadeRemoteParty;
    }

    public void setUpCascadeRemoteParty(String upCascadeRemoteParty) {
        this.upCascadeRemoteParty = upCascadeRemoteParty;
    }

    public String getConferenceRemoteParty() {
        return conferenceRemoteParty;
    }

    public void setConferenceRemoteParty(String conferenceRemoteParty) {
        this.conferenceRemoteParty = conferenceRemoteParty;
    }

    public List<? extends BaseAttendee> getLiveTerminals() {
        return null;
    }

    public String getRemarks() {
        return null;
    }

    public void addCommonlyUsedAttendees(T attendee) {
        if (attendee != null) {
            attendee.setCommonlyUsed(true);
        }
    }

    public void removeCommonlyUsedAttendees(T attendee) {
        if (attendee != null) {
            attendee.setCommonlyUsed(false);
        }
    }

    public List<? extends BaseAttendee> getMcuAttendees() {
        return new ArrayList<>();
    }

    public BusiHistoryConference getHistoryConference() {
        return null;
    }

    public boolean isDownCascadeConference() {
        if (StringUtils.isNotEmpty(getUpCascadeConferenceId())) {
            return true;
        }
        return false;
    }

    public boolean isUpCascadeConference() {
        if (getMcuAttendees().size() > 0) {
            return true;
        }
        return false;
    }

    public String getMcuCallIp() {
        return null;
    }

    public Integer getMcuCallPort() {
        return null;
    }

    public Map<Long, List<T>>  getCascadeAttendeesMap() {
        return null;
    }

    public Integer getBandwidth() {
        return null;
    }

    public void updateAttendeeToRemotePartyMap(String oldRemoteParty, T attendee) {
    }

    public String getPresentAttendeeId() {
        return null;
    }

    public Boolean getMultiPicBroadcastStatus() {
        return multiPicBroadcastStatus;
    }

    public void setMultiPicBroadcastStatus(Boolean multiPicBroadcastStatus) {
        this.multiPicBroadcastStatus = multiPicBroadcastStatus;
    }

    public Boolean getLockPresenterStatus() {
        return lockPresenterStatus;
    }

    public void setLockPresenterStatus(Boolean lockPresenterStatus) {
        this.lockPresenterStatus = lockPresenterStatus;
    }

    public String getMultiPicPollStatus() {
        return multiPicPollStatus;
    }

    public void setMultiPicPollStatus(String multiPicPollStatus) {
        this.multiPicPollStatus = multiPicPollStatus;
    }

    public com.paradisecloud.fcm.common.enumer.PollOperateTypeDto getChairmanPollStatus() {
        return chairmanPollStatus;
    }

    public void setChairmanPollStatus(com.paradisecloud.fcm.common.enumer.PollOperateTypeDto chairmanPollStatus) {
        this.chairmanPollStatus = chairmanPollStatus;
    }

    public boolean isMultiScreenRollCall() {
        return multiScreenRollCall;
    }

    public void setMultiScreenRollCall(boolean multiScreenRollCall) {
        this.multiScreenRollCall = multiScreenRollCall;
    }

    public Integer getStreamingEnabled() {
        return streamingEnabled;
    }

    public void setStreamingEnabled(Integer streamingEnabled) {
        this.streamingEnabled = streamingEnabled;
    }

    public Integer getRecordingEnabled() {
        return recordingEnabled;
    }

    public void setRecordingEnabled(Integer recordingEnabled) {
        this.recordingEnabled = recordingEnabled;
    }

    public Integer getMinutesEnabled() {
        return minutesEnabled;
    }

    public void setMinutesEnabled(Integer minutesEnabled) {
        this.minutesEnabled = minutesEnabled;
    }

    public boolean isApprovedConference() {
        return approvedConference;
    }

    public void setApprovedConference(boolean approvedConference) {
        this.approvedConference = approvedConference;
    }

    public boolean isStreaming() {
        return streaming;
    }

    public void setStreaming(boolean streaming) {
        this.streaming = streaming;
        if (streaming) {
            setStreamingStartTime(System.currentTimeMillis());
        } else {
            setStreamingStartTime(0);
        }
    }

    public long getStreamingStartTime() {
        return streamingStartTime;
    }

    public void setStreamingStartTime(long streamingStartTime) {
        this.streamingStartTime = streamingStartTime;
    }

    public String getStreamingUrl() {
        return streamingUrl;
    }

    public void setStreamingUrl(String streamingUrl) {
        this.streamingUrl = streamingUrl;
    }

    public String getCloudsStreamingUrl() {
        return cloudsStreamingUrl;
    }

    public void setCloudsStreamingUrl(String cloudsStreamingUrl) {
        this.cloudsStreamingUrl = cloudsStreamingUrl;
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

    public T getStreamingAttendee() {
        return streamingAttendee;
    }

    public void setStreamingAttendee(T streamingAttendee) {
        streamingAttendee.setConferenceNumber(getConferenceNumber());
        streamingAttendee.setContextKey(getContextKey());
        this.streamingAttendee = streamingAttendee;
    }

    public boolean isRecorded() {
        return recorded;
    }

    public void setRecorded(boolean recorded) {
        this.recorded = recorded;
    }

    public String getRecordingRemoteParty() {
        return recordingRemoteParty;
    }

    public void setRecordingRemoteParty(String recordingRemoteParty) {
        this.recordingRemoteParty = recordingRemoteParty;
    }

    public T getRecordingAttendee() {
        return recordingAttendee;
    }

    public void setRecordingAttendee(T recordingAttendee) {
        recordingAttendee.setConferenceNumber(getConferenceNumber());
        recordingAttendee.setContextKey(getContextKey());
        this.recordingAttendee = recordingAttendee;
    }

    public Integer getBusinessFieldType() {
        return null;
    }

    public String getTencentRemoteParty() {
        return tencentRemoteParty;
    }

    public void setTencentRemoteParty(String tencentRemoteParty) {
        this.tencentRemoteParty = tencentRemoteParty;
    }

    public String getDtmf() {
        return dtmf;
    }

    public void setDtmf(String dtmf) {
        this.dtmf = dtmf;
    }

    public Integer getLiveTerminalCount() {
        return liveTerminalCount;
    }

    public void setLiveTerminalCount(Integer liveTerminalCount) {
        this.liveTerminalCount = liveTerminalCount;
    }

    public String getMinutesRemoteParty() {
        return minutesRemoteParty;
    }

    public void setMinutesRemoteParty(String minutesRemoteParty) {
        this.minutesRemoteParty = minutesRemoteParty;
    }

    public T getMinutesAttendee() {
        return minutesAttendee;
    }

    public void setMinutesAttendee(T minutesAttendee) {
        if (minutesAttendee != null) {
            minutesAttendee.setConferenceNumber(getConferenceNumber());
            minutesAttendee.setContextKey(getContextKey());
        }
        this.minutesAttendee = minutesAttendee;
    }

    public boolean isMinutes() {
        return minutes;
    }

    public void setMinutes(boolean minutes) {
        this.minutes = minutes;
    }

    public String getCoSpaceId() {
        return null;
    }

    public void setCoSpaceId(String coSpaceId) {
    }

    public MinutesParam getMinutesParam() {
        return minutesParam;
    }

    public void setMinutesParam(MinutesParam minutesParam) {
        this.minutesParam = minutesParam;
    }

    public void openMinutesLog() throws Exception {
        if (minutesLogger == null) {
            try {
                minutesLogger = Logger.getLogger("minutes_log_" + getId());
                MinutesFileHandler handler = MinutesFileHandler.createHandler(getCoSpaceId(), getConferenceNumber(), getHistoryConference().getId());
                minutesLogger.addHandler(handler);
            } catch (Exception e) {
                closeMinutesLog();
                throw e;
            }
        }
    }

    public void closeMinutesLog() {
        if (minutesLogger != null) {
            Handler[] handlers = minutesLogger.getHandlers();
            if (handlers != null) {
                for (Handler handler : handlers) {
                    try {
                        handler.close();
                    } catch (Exception e) {
                    }
                }
            }
            minutesLogger = null;
        }
    }

    public void minutesLog(String message) {
        if (minutesLogger != null) {
            minutesLogger.log(Level.INFO, message);
        }
    }

    public List<CloudConference> getCloudConferenceList() {
        return cloudConferenceList;
    }

    public void setCloudConferenceList(List<CloudConference> cloudConferenceList) {
        this.cloudConferenceList = cloudConferenceList;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    private boolean streamingCustomsLayout = false;
    private boolean recordingCustomsLayout = false;
    private boolean streamingLayoutFollow = true;
    private boolean recordingLayoutFollow = true;

    public boolean isStreamingCustomsLayout() {
        return streamingCustomsLayout;
    }

    public void setStreamingCustomsLayout(boolean streamingCustomsLayout) {
        this.streamingCustomsLayout = streamingCustomsLayout;
    }

    public boolean isRecordingCustomsLayout() {
        return recordingCustomsLayout;
    }

    public void setRecordingCustomsLayout(boolean recordingCustomsLayout) {
        this.recordingCustomsLayout = recordingCustomsLayout;
    }

    public boolean isStreamingLayoutFollow() {
        return streamingLayoutFollow;
    }

    public void setStreamingLayoutFollow(boolean streamingLayoutFollow) {
        this.streamingLayoutFollow = streamingLayoutFollow;
    }

    public boolean isRecordingLayoutFollow() {
        return recordingLayoutFollow;
    }

    public void setRecordingLayoutFollow(boolean recordingLayoutFollow) {
        this.recordingLayoutFollow = recordingLayoutFollow;
    }

    private long systemMessageEndTime = 0;

    public long getSystemMessageEndTime() {
        return systemMessageEndTime;
    }

    public void setSystemMessageEndTime(long systemMessageEndTime) {
        this.systemMessageEndTime = systemMessageEndTime;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }
}
