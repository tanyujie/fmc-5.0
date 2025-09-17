package com.paradisecloud.fcm.dao.model.vo;

import com.paradisecloud.fcm.dao.model.BusiHistoryParticipant;
import org.springframework.beans.BeanUtils;

public class BusiHistoryParticipantConferenceVo extends BusiHistoryParticipant {

    public BusiHistoryParticipantConferenceVo() {
    }

    public BusiHistoryParticipantConferenceVo(BusiHistoryParticipant busiHistoryParticipant) {
        BeanUtils.copyProperties(busiHistoryParticipant, this);
    }

    /** 会议名 */
    private String historyConferenceName;

    /** 会议号 */
    private String historyConferenceNumber;

    public String getHistoryConferenceName() {
        return historyConferenceName;
    }

    public void setHistoryConferenceName(String historyConferenceName) {
        this.historyConferenceName = historyConferenceName;
    }

    public String getHistoryConferenceNumber() {
        return historyConferenceNumber;
    }

    public void setHistoryConferenceNumber(String historyConferenceNumber) {
        this.historyConferenceNumber = historyConferenceNumber;
    }
}
