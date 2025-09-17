package com.paradisecloud.fcm.dao.model.vo;

import java.util.Date;

public class ConferenceApprovalSearchVo {

    /**
     * 当前页面
     */
    private Integer pageNum;
    /**
     * 每页显示条数
     */
    private Integer pageSize;
    /**
     * 部门ID
     */
    private Long deptId;
    /**
     * 审批状态
     */
    private Integer approvalStatus;
    /**
     * 会议名称
     */
    private String name;
    /**
     * 申请开始时间
     */
    private Date createStartTime;
    /**
     * 申请开始时间
     */
    private Date createEndTime;
    /**
     * 审批开始时间
     */
    private Date approvalStartTime;
    /**
     * 审批开始时间
     */
    private Date approvalEndTime;

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

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateStartTime() {
        return createStartTime;
    }

    public void setCreateStartTime(Date createStartTime) {
        this.createStartTime = createStartTime;
    }

    public Date getCreateEndTime() {
        return createEndTime;
    }

    public void setCreateEndTime(Date createEndTime) {
        this.createEndTime = createEndTime;
    }

    public Date getApprovalStartTime() {
        return approvalStartTime;
    }

    public void setApprovalStartTime(Date approvalStartTime) {
        this.approvalStartTime = approvalStartTime;
    }

    public Date getApprovalEndTime() {
        return approvalEndTime;
    }

    public void setApprovalEndTime(Date approvalEndTime) {
        this.approvalEndTime = approvalEndTime;
    }
}
