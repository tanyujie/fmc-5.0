package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 入会文件对象 busi_meeting_file
 * 
 * @author lilinhai
 * @date 2024-03-29
 */
@Schema(description = "入会文件")
public class BusiMeetingFile extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    @Schema(description = "主键id")
    private Long id;

    /** 文件名称 */
    @Schema(description = "文件名称")
    @Excel(name = "文件名称")
    private String fileName;

    /** 文件url */
    @Schema(description = "文件url")
    @Excel(name = "文件url")
    private String url;

    /** 入会名称 */
    @Schema(description = "入会名称")
    @Excel(name = "入会名称")
    private String participantName;

    /** 转码状态：0：未转码，1：已转码 */
    @Schema(description = "转码状态：0：未转码，1：已转码,2：转码中,4：回调失败")
    @Excel(name = "转码状态：0：未转码，1：已转码")
    private Integer codecStatus;

    /** 文件类型 */
    @Schema(description = "文件类型")
    @Excel(name = "文件类型")
    private String fileType;

    /** 文件大小 */
    @Schema(description = "文件大小")
    @Excel(name = "文件大小")
    private String fileSize;

    /** 创建人 */
    @Schema(description = "创建人")
    @Excel(name = "创建人")
    private Long createUserId;

    /** 文件状态：0：删除，1:正常 */
    @Schema(description = "文件状态：0：删除，1:正常")
    @Excel(name = "文件状态：0：删除，1:正常")
    private Integer fileStatus;

    /** 归属部门id */
    @Schema(description = "归属部门id")
    @Excel(name = "归属部门id")
    private Integer deptId;

    /** 备注 */
    @Schema(description = "备注")
    @Excel(name = "备注")
    private String remark;

    /** 转码路径 */
    @Schema(description = "转码路径")
    @Excel(name = "转码路径")
    private String outFile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getFileName() 
    {
        return fileName;
    }
    public void setUrl(String url) 
    {
        this.url = url;
    }

    public String getUrl() 
    {
        return url;
    }
    public void setParticipantName(String participantName) 
    {
        this.participantName = participantName;
    }

    public String getParticipantName() 
    {
        return participantName;
    }
    public void setCodecStatus(Integer codecStatus) 
    {
        this.codecStatus = codecStatus;
    }

    public Integer getCodecStatus() 
    {
        return codecStatus;
    }
    public void setFileType(String fileType) 
    {
        this.fileType = fileType;
    }

    public String getFileType() 
    {
        return fileType;
    }
    public void setFileSize(String fileSize) 
    {
        this.fileSize = fileSize;
    }

    public String getFileSize() 
    {
        return fileSize;
    }
    public void setCreateUserId(Long createUserId)
    {
        this.createUserId = createUserId;
    }

    public Long getCreateUserId()
    {
        return createUserId;
    }
    public void setFileStatus(Integer fileStatus) 
    {
        this.fileStatus = fileStatus;
    }

    public Integer getFileStatus() 
    {
        return fileStatus;
    }
    public void setDeptId(Integer deptId) 
    {
        this.deptId = deptId;
    }

    public Integer getDeptId() 
    {
        return deptId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("fileName", getFileName())
            .append("url", getUrl())
            .append("participantName", getParticipantName())
            .append("codecStatus", getCodecStatus())
            .append("fileType", getFileType())
            .append("fileSize", getFileSize())
            .append("createTime", getCreateTime())
            .append("createUserId", getCreateUserId())
            .append("updateTime", getUpdateTime())
            .append("fileStatus", getFileStatus())
            .append("deptId", getDeptId())
            .toString();
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOutFile() {
        return outFile;
    }

    public void setOutFile(String outFile) {
        this.outFile = outFile;
    }
}
