package com.paradisecloud.fcm.dao.model.vo;

import com.paradisecloud.fcm.dao.model.BusiHistoryParticipant;
import com.paradisecloud.fcm.dao.model.BusiHistoryParticipantTerminal;
import org.springframework.beans.BeanUtils;

public class BusiHistoryParticipantTerminalVo extends BusiHistoryParticipant {

    public BusiHistoryParticipantTerminalVo(BusiHistoryParticipant busiHistoryParticipant) {
        BeanUtils.copyProperties(busiHistoryParticipant, this);
    }

    public BusiHistoryParticipantTerminalVo(BusiHistoryParticipantTerminal busiHistoryParticipantTerminal) {
        BeanUtils.copyProperties(busiHistoryParticipantTerminal, this);
        setJoinedTimes(busiHistoryParticipantTerminal.getJoinedTimes());
    }

    private int joinedTimes;

    public int getJoinedTimes() {
        return joinedTimes;
    }

    public void setJoinedTimes(int joinedTimes) {
        this.joinedTimes = joinedTimes;
    }
}
