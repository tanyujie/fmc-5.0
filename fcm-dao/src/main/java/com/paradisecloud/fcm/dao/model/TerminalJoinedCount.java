package com.paradisecloud.fcm.dao.model;

/**
 * 会议终端入会次数
 */
public class TerminalJoinedCount {

    /**
     * 入会次数
     */
    private int joinedTimes;
    /**
     * 入会时长
     */
    private int durationSeconds;

    public int getJoinedTimes() {
        return joinedTimes;
    }

    public void setJoinedTimes(int joinedTimes) {
        this.joinedTimes = joinedTimes;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
}
