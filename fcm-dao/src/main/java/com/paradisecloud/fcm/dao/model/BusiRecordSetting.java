package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * 录制管理对象 busi_record_setting
 *
 * @author lilinhai
 * @date 2021-04-29
 */
@Schema(description = "录制管理")
public class BusiRecordSetting extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 录制路径 */
    @Schema(description = "录制路径")
    @Excel(name = "录制路径")
    private String url;

    /** nfs存储录制文件路径 */
    @Schema(description = "nfs存储录制文件路径")
    @Excel(name = "nfs存储录制文件路径")
    private String path;

    /** 录制文件夹名称 */
    @Schema(description = "录制文件夹名称")
    @Excel(name = "录制文件夹名称")
    private String folder;

    /** 合并视频文件名 */
    @Schema(description = "合并视频文件名")
    @Excel(name = "合并视频文件名")
    private String mergeName;

    /** 合并视频文件封面名称 */
    @Schema(description = "合并视频文件封面名称")
    @Excel(name = "合并视频文件封面名称")
    private String mergeCoverName;

    /** 状态{1:启用;2禁用} */
    @Schema(description = "状态{1:启用;2禁用}")
    @Excel(name = "状态{1:启用;2禁用}")
    private Integer status;

    /** 描述 */
    @Schema(description = "描述")
    @Excel(name = "描述")
    private String description;

    /** 活跃会议室对应的部门 */
    @Schema(description = "活跃会议室对应的部门")
    @Excel(name = "活跃会议室对应的部门")
    private Long deptId;

    /** 文件保留范围( 1一个月 ，2两个月， 3三个月) */
    @Schema(description = "文件保留范围( 1一个月 ，2两个月， 3三个月)")
    @Excel(name = "文件保留范围( 1一个月 ，2两个月， 3三个月)")
    private Integer retentionType;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getUrl()
    {
        return url;
    }
    public void setPath(String path)
    {
        this.path = path;
    }

    public String getPath()
    {
        return path;
    }
    public void setFolder(String folder)
    {
        this.folder = folder;
    }

    public String getFolder()
    {
        return folder;
    }
    public void setMergeName(String mergeName)
    {
        this.mergeName = mergeName;
    }

    public String getMergeName()
    {
        return mergeName;
    }
    public void setMergeCoverName(String mergeCoverName)
    {
        this.mergeCoverName = mergeCoverName;
    }

    public String getMergeCoverName()
    {
        return mergeCoverName;
    }
    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getStatus()
    {
        return status;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }
    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setRetentionType(Integer retentionType)
    {
        this.retentionType = retentionType;
    }

    public Integer getRetentionType()
    {
        return retentionType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("url", getUrl())
                .append("path", getPath())
                .append("folder", getFolder())
                .append("mergeName", getMergeName())
                .append("mergeCoverName", getMergeCoverName())
                .append("status", getStatus())
                .append("description", getDescription())
                .append("deptId", getDeptId())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("retentionType", getRetentionType())
                .toString();
    }
}
