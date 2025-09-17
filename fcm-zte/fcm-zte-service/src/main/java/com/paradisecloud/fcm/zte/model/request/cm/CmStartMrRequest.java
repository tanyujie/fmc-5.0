package com.paradisecloud.fcm.zte.model.request.cm;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.zte.model.request.CommonRequest;

import java.util.Date;

public class CmStartMrRequest extends CommonRequest {

    private String conferenceNumber;
    private String conferenceName;
    private String ConferencePassword;
    private Date StartTime;
    private int Duration;
    private int MultiViewNumber=25;

    private String MultiPicControl;
    private int MaxParticipants;
    private int InviteWithSDP=1;
    private int EnableMcuTitle=1;
    private int EnableMcuBanner=1;
    private String Account;

    public String getAccount() {
        return Account;
    }

    public void setAccount(String account) {
        Account = account;
    }

    public String getConferenceNumber() {
        return conferenceNumber;
    }

    public void setConferenceNumber(String conferenceNumber) {
        this.conferenceNumber = conferenceNumber;
    }

    public String getConferenceName() {
        return conferenceName;
    }

    public void setConferenceName(String conferenceName) {
        this.conferenceName = conferenceName;
    }

    public String getConferencePassword() {
        return ConferencePassword;
    }

    public void setConferencePassword(String conferencePassword) {
        ConferencePassword = conferencePassword;
    }

    public Date getStartTime() {
        return StartTime;
    }

    public void setStartTime(Date startTime) {
        StartTime = startTime;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

    public int getMultiViewNumber() {
        return MultiViewNumber;
    }

    public void setMultiViewNumber(int multiViewNumber) {
        MultiViewNumber = multiViewNumber;
    }

    public String getMultiPicControl() {
        return MultiPicControl;
    }

    public void setMultiPicControl(String multiPicControl) {
        MultiPicControl = multiPicControl;
    }

    public int getMaxParticipants() {
        return MaxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        MaxParticipants = maxParticipants;
    }

    public int getInviteWithSDP() {
        return InviteWithSDP;
    }

    public void setInviteWithSDP(int inviteWithSDP) {
        InviteWithSDP = inviteWithSDP;
    }

    public int getEnableMcuTitle() {
        return EnableMcuTitle;
    }

    public void setEnableMcuTitle(int enableMcuTitle) {
        EnableMcuTitle = enableMcuTitle;
    }

    public int getEnableMcuBanner() {
        return EnableMcuBanner;
    }

    public void setEnableMcuBanner(int enableMcuBanner) {
        EnableMcuBanner = enableMcuBanner;
    }


    @Override
    public String buildToXml() {

        String xml = "";

        return xml;
    }
}
