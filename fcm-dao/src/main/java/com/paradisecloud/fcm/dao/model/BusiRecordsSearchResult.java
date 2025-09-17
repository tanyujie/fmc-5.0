package com.paradisecloud.fcm.dao.model;

import java.util.Date;

public class BusiRecordsSearchResult {

    private Long conferenceNumber;
    private String name;
    private String coSpaceId;
    private Long deptId;
    private Date recordingTimeOfLate;
    private Integer recordFileNum;

    public Long getConferenceNumber() {
        return conferenceNumber;
    }

    public void setConferenceNumber(Long conferenceNumber) {
        this.conferenceNumber = conferenceNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoSpaceId() {
        return coSpaceId;
    }

    public void setCoSpaceId(String coSpaceId) {
        this.coSpaceId = coSpaceId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Date getRecordingTimeOfLate() {
        return recordingTimeOfLate;
    }

    public void setRecordingTimeOfLate(Date recordingTimeOfLate) {
        this.recordingTimeOfLate = recordingTimeOfLate;
    }

    public Integer getRecordFileNum() {
        return recordFileNum;
    }

    public void setRecordFileNum(Integer recordFileNum) {
        this.recordFileNum = recordFileNum;
    }

}
