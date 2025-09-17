package com.paradisecloud.fcm.common.cache;



/**
 * 共通参数缓存
 */
public class LicenseCache {

    private String sn;
    private Boolean recorder;
    private Boolean streamer;
    private String quality;
    private Integer recorderLimit;
    private Integer liveLimit;
    private Integer participantLimit;
    private Integer conferenceLimit;
    private String termianlType;
    private Integer termianlAmount;
    private String monitor;
    private Integer defaultParticipantLimit;
    private Boolean localRecoder;
    private Boolean fileMeeting;
    private String participantLimitTime;
    private Boolean schedule;
    private Boolean useRecorderLimit;
    private Integer useableSpace;
    private String streamQuality;
    private Integer tencentTime;
    private Integer asrTime;
    private Integer cloudLiveTime;
    private LicenseCache() {

    }

    public static LicenseCache getInstance() {
        LicenseCache instance = LicenseCache.InnerClass.INSTANCE;

        return instance;
    }


    private static class InnerClass {
        private final static LicenseCache INSTANCE = new LicenseCache();
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Boolean getRecorder() {
        return recorder;
    }

    public void setRecorder(Boolean recorder) {
        this.recorder = recorder;
    }

    public Boolean getStreamer() {
        return streamer;
    }

    public void setStreamer(Boolean streamer) {
        this.streamer = streamer;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public Integer getRecorderLimit() {
        return recorderLimit;
    }

    public void setRecorderLimit(Integer recorderLimit) {
        this.recorderLimit = recorderLimit;
    }

    public Integer getLiveLimit() {
        return liveLimit;
    }

    public void setLiveLimit(Integer liveLimit) {
        this.liveLimit = liveLimit;
    }

    public Integer getParticipantLimit() {
        return participantLimit;
    }

    public void setParticipantLimit(Integer participantLimit) {
        this.participantLimit = participantLimit;
    }

    public Integer getConferenceLimit() {
        return conferenceLimit;
    }

    public void setConferenceLimit(Integer conferenceLimit) {
        this.conferenceLimit = conferenceLimit;
    }

    public String getTermianlType() {
        return termianlType;
    }

    public void setTermianlType(String termianlType) {
        this.termianlType = termianlType;
    }

    public Integer getTermianlAmount() {
        return termianlAmount;
    }

    public void setTermianlAmount(Integer termianlAmount) {
        this.termianlAmount = termianlAmount;
    }

    public String getMonitor() {
        return monitor;
    }

    public void setMonitor(String monitor) {
        this.monitor = monitor;
    }

    public Integer getDefaultParticipantLimit() {
        return defaultParticipantLimit;
    }

    public void setDefaultParticipantLimit(Integer defaultParticipantLimit) {
        this.defaultParticipantLimit = defaultParticipantLimit;
    }

    public Boolean getLocalRecoder() {
        return localRecoder;
    }

    public void setLocalRecoder(Boolean localRecoder) {
        this.localRecoder = localRecoder;
    }

    public Boolean getFileMeeting() {
        return fileMeeting;
    }

    public void setFileMeeting(Boolean fileMeeting) {
        this.fileMeeting = fileMeeting;
    }

    public String getParticipantLimitTime() {
        return participantLimitTime;
    }

    public void setParticipantLimitTime(String participantLimitTime) {
        this.participantLimitTime = participantLimitTime;
    }

    public Boolean getSchedule() {
        return schedule;
    }

    public void setSchedule(Boolean schedule) {
        this.schedule = schedule;
    }

    public Boolean getUseRecorderLimit() {
        return useRecorderLimit;
    }

    public void setUseRecorderLimit(Boolean useRecorderLimit) {
        this.useRecorderLimit = useRecorderLimit;
    }

    public Integer getUseableSpace() {
        return useableSpace;
    }

    public void setUseableSpace(Integer useableSpace) {
        this.useableSpace = useableSpace;
    }

    public String getStreamQuality() {
        return streamQuality;
    }

    public Integer getCloudLiveTime() {
        if(cloudLiveTime==null){
            return 0;
        }
        return cloudLiveTime;
    }

    public void setCloudLiveTime(Integer cloudLiveTime) {
        this.cloudLiveTime = cloudLiveTime;
    }

    public void setStreamQuality(String streamQuality) {
        this.streamQuality = streamQuality;
    }

    public Integer getTencentTime() {
        if(tencentTime==null){
            return 0;
        }
        return tencentTime;
    }

    public void setTencentTime(Integer tencentTime) {
        this.tencentTime = tencentTime;
    }

    public Integer getAsrTime() {
        if(asrTime==null){
            return 0;
        }
        return asrTime;
    }

    public void setAsrTime(Integer asrTime) {
        this.asrTime = asrTime;
    }
}
