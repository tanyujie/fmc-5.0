package com.paradisecloud.fcm.dao.model.vo;

import java.util.Date;

/**
 * 终端报告查询类
 */
public class TerminalReportSearchVo {

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 当前页面
     */
    private Integer pageNum;

    /**
     * 每页显示条数
     */
    private Integer pageSize;

    /**
     * 终端ID
     */
    private Long terminalId;

    /**
     * 终端名称
     */
    private String terminalName;

    /**
     * 终端类型
     */
    private Integer terminalType;

    /**
     * 是否入会
     */
    private Boolean isJoin;

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(Long terminalId) {
        this.terminalId = terminalId;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    public Integer getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(Integer terminalType) {
        this.terminalType = terminalType;
    }

    public Boolean getIsJoin() {
        return isJoin;
    }

    public void setIsJoin(Boolean isJoin) {
        this.isJoin = isJoin;
    }

    @Override
    public String toString() {
        return "TerminalReportSearchVo{" +
                "deptId=" + deptId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", terminalId=" + terminalId +
                ", terminalName='" + terminalName + '\'' +
                ", isJoined=" + isJoin +
                ", terminalType=" + terminalType +
                '}';
    }
}
