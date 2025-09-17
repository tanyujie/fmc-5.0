package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

/**
 * 录制文件记录对象 busi_records
 *
 * @author lilinhai
 * @date 2021-05-07
 */
@Schema(description = "录制文件记录")
public class BusiRecords extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 活跃会议室用的会议号
     */
    @Schema(description = "活跃会议室用的会议号")
    @Excel(name = "活跃会议室用的会议号")
    private Long conferenceNumber;

    /**
     * coSpaceId
     */
    @Schema(description = "coSpaceId")
    @Excel(name = "coSpaceId")
    private String coSpaceId;

    /**
     * 文件名
     */
    @Schema(description = "文件名")
    @Excel(name = "文件名")
    private String fileName;

    /**
     * 真实文件名
     */
    @Schema(description = "真实文件名")
    @Excel(name = "真实文件名")
    private String realName;

    /**
     * 模板名称
     */
    @Schema(description = "模板名称")
    @Excel(name = "模板名称")
    private String templateName;

    /**
     * 部门ID
     */
    @Schema(description = "部门ID")
    @Excel(name = "部门ID")
    private Long deptId;

    /**
     * 文件大小
     */
    @Schema(description = "文件大小")
    @Excel(name = "文件大小")
    private String fileSize;

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

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setConferenceNumber(Long conferenceNumber) {
        this.conferenceNumber = conferenceNumber;
    }

    public Long getConferenceNumber() {
        return conferenceNumber;
    }

    public void setCoSpaceId(String coSpaceId) {
        this.coSpaceId = coSpaceId;
    }

    public String getCoSpaceId() {
        return coSpaceId;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRealName() {
        return realName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getRecordsFileStatus() {
        return recordsFileStatus;
    }

    public void setRecordsFileStatus(Integer recordsFileStatus) {
        this.recordsFileStatus = recordsFileStatus;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("conferenceNumber", getConferenceNumber())
                .append("coSpaceId", getCoSpaceId())
                .append("fileName", getFileName())
                .append("realName", getRealName())
                .append("templateName", getTemplateName())
                .append("deptId", getDeptId())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("fileSize", getFileSize())
                .append("deleteTime", getDeleteTime())
                .append("recordsFileStatus", getRecordsFileStatus())
                .toString();
    }
}
