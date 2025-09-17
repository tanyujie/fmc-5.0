package com.paradisecloud.smc3.model;


public abstract class SmcConferenceContextBase {

    private Long deptId;

    private ConferenceState conferenceState;

    private ConferenceSettingState conferenceSettingState;
    private Boolean isCascade;

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public ConferenceState getConferenceState() {
        return conferenceState;
    }

    public void setConferenceState(ConferenceState conferenceState) {
        this.conferenceState = conferenceState;
    }

    public Boolean getCascade() {
        return isCascade;
    }

    public void setCascade(Boolean cascade) {
        isCascade = cascade;
    }
}

