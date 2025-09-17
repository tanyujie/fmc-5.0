package com.paradisecloud.fcm.fme.service.impls.vo;

import com.paradisecloud.common.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

/**
 * @author johnson liu
 * @date 2021/4/29 11:36
 */
public class BusiConferenceNumberVo {
    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 创建者ID */
    @Schema(description = "创建者ID")
    @Excel(name = "创建者ID")
    private Long createUserId;

    /** 归属公司ID */
    @Schema(description = "归属公司ID")
    @Excel(name = "归属公司ID")
    private Long deptId;

    /** 创建者用户名 */
    @Schema(description = "创建者用户名")
    @Excel(name = "创建者用户名")
    private String createUserName;

    /** 会议号类型：1级联，2普通 */
    @Schema(description = "会议号类型：1级联，2普通")
    @Excel(name = "会议号类型：1级联，2普通")
    private Integer type;

    /** 备注信息 */
    @Schema(description = "备注信息")
    @Excel(name = "备注信息")
    private String remarks;

    /** 号码状态：1闲置，10已预约，100会议中 */
    @Schema(description = "号码状态：1闲置，10已预约，100会议中")
    @Excel(name = "号码状态：1闲置，10已预约，100会议中")
    private Integer status;

    /** 录制文件数量 */
    @Schema(description = "录制文件数量")
    @Excel(name = "录制文件数量")
    private int recordFileNum;

    /** 最近一次录制时间 */
    @Schema(description = "最近一次录制时间")
    @Excel(name = "最近一次录制时间")
    private Date recordingTimeOfLate;

    /** 会议名 */
    @Schema(description = "会议名")
    @Excel(name = "会议名")
    private String name;

    /** 活跃会议室spaceId */
    @Schema(description = "活跃会议室spaceId")
    @Excel(name = "活跃会议室spaceId")
    private String coSpaceId;

    @Schema(description = "文件大小")
    @Excel(name = "文件大小")
    private String fileSize;

    /**
     * 活跃会议室用的会议号
     */
    @Schema(description = "活跃会议室用的会议号")
    @Excel(name = "活跃会议室用的会议号")
    private Long conferenceNumber;

    /**
     * 放入回收站时间
     */
    @Schema(description = "放入回收站时间")
    @Excel(name = "放入回收站时间")
    private Date deleteTime;

    /**
     * 录制文件状态
     */
    @Schema(description = "录制文件状态")
    @Excel(name = "录制文件状态")
    private Integer recordsFileStatus;

    private Integer remainDays;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public int getRecordFileNum() {
        return recordFileNum;
    }

    public void setRecordFileNum(int recordFileNum) {
        this.recordFileNum = recordFileNum;
    }

    public Date getRecordingTimeOfLate() {
        return recordingTimeOfLate;
    }

    public void setRecordingTimeOfLate(Date recordingTimeOfLate) {
        this.recordingTimeOfLate = recordingTimeOfLate;
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

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public Long getConferenceNumber() {
        return conferenceNumber;
    }

    public void setConferenceNumber(Long conferenceNumber) {
        this.conferenceNumber = conferenceNumber;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    public Integer getRecordsFileStatus() {
        return recordsFileStatus;
    }

    public void setRecordsFileStatus(Integer recordsFileStatus) {
        this.recordsFileStatus = recordsFileStatus;
    }

    public Integer getRemainDays() {
        return remainDays;
    }

    public void setRemainDays(Integer remainDays) {
        this.remainDays = remainDays;
    }
}
