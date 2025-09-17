package com.paradisecloud.fcm.dao.model.vo;

/**
 * 终端报告终端概况类
 */
public class TerminalReportTerminalOverviewVo {

    /**
     * 参会终端数
     */
    private Long terminalCount;
    /**
     * 参会时长（秒）
     */
    private Long joinedSeconds;

    public Long getTerminalCount() {
        return terminalCount;
    }

    public void setTerminalCount(Long terminalCount) {
        this.terminalCount = terminalCount;
    }

    public Long getJoinedSeconds() {
        return joinedSeconds;
    }

    public void setJoinedSeconds(Long joinedSeconds) {
        this.joinedSeconds = joinedSeconds;
    }

    @Override
    public String toString() {
        return "TerminalReportTerminalOverviewVo{" +
                "terminalCount=" + terminalCount +
                ", joinedSeconds=" + joinedSeconds +
                '}';
    }
}
