package com.paradisecloud.fcm.dao.model;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 直播录制文件记录对象 busi_live_records
 * 
 * @author lilinhai
 * @date 2024-05-21
 */
@Schema(description = "直播录制文件记录")
public class BusiLiveRecords extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 直播ID */
    @Schema(description = "直播ID")
    @Excel(name = "直播ID")
    private Long liveId;

    /** 文件名 */
    @Schema(description = "文件名")
    @Excel(name = "文件名")
    private String fileName;

    /** 文件URL */
    @Schema(description = "文件URL")
    @Excel(name = "文件URL")
    private String fileUrl;

    /** 删除时间 */
    @Schema(description = "删除时间")
    @Excel(name = "删除时间", width = 30)
    private Date deleteTime;

    /** 删除人 */
    @Schema(description = "删除人")
    @Excel(name = "删除人")
    private String deleteBy;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setLiveId(Long liveId) 
    {
        this.liveId = liveId;
    }

    public Long getLiveId() 
    {
        return liveId;
    }
    public void setFileName(String fileName) 
    {
        this.fileName = fileName;
    }

    public String getFileName() 
    {
        return fileName;
    }
    public void setFileUrl(String fileUrl) 
    {
        this.fileUrl = fileUrl;
    }

    public String getFileUrl() 
    {
        return fileUrl;
    }
    public void setDeleteTime(Date deleteTime) 
    {
        this.deleteTime = deleteTime;
    }

    public Date getDeleteTime() 
    {
        return deleteTime;
    }
    public void setDeleteBy(String deleteBy) 
    {
        this.deleteBy = deleteBy;
    }

    public String getDeleteBy() 
    {
        return deleteBy;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("liveId", getLiveId())
            .append("fileName", getFileName())
            .append("fileUrl", getFileUrl())
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .append("updateTime", getUpdateTime())
            .append("updateBy", getUpdateBy())
            .append("deleteTime", getDeleteTime())
            .append("deleteBy", getDeleteBy())
            .toString();
    }
}
