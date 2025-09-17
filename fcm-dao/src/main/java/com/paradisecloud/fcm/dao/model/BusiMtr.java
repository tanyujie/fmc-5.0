package com.paradisecloud.fcm.dao.model;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * MTR检测记录对象 busi_mtr
 * 
 * @author lilinhai
 * @date 2023-12-27
 */
@Schema(description = "MTR检测记录")
public class BusiMtr extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 源地址 */
    @Schema(description = "源地址")
    @Excel(name = "源地址")
    private String sourceIp;

    /** 目标地址 */
    @Schema(description = "目标地址")
    @Excel(name = "目标地址")
    private String targetIp;

    /** 次数 */
    @Schema(description = "次数")
    @Excel(name = "次数")
    private Integer times;

    /** 文件名 */
    @Schema(description = "文件名")
    @Excel(name = "文件名")
    private String fileName;

    /** PID */
    @Schema(description = "PID")
    @Excel(name = "PID")
    private String pid;

    /** 状态：0：检测中 1：检测完成 2：检测错误 */
    @Schema(description = "状态：0：检测中 1：检测完成 2：检测错误")
    @Excel(name = "状态：0：检测中 1：检测完成 2：检测错误")
    private Integer status;

    /** 名称 */
    @Schema(description = "名称")
    @Excel(name = "名称")
    private String name;

    /** 删除时间 */
    @Schema(description = "删除时间")
    @Excel(name = "删除时间", width = 30)
    private Date deleteTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setSourceIp(String sourceIp) 
    {
        this.sourceIp = sourceIp;
    }

    public String getSourceIp() 
    {
        return sourceIp;
    }
    public void setTargetIp(String targetIp) 
    {
        this.targetIp = targetIp;
    }

    public String getTargetIp() 
    {
        return targetIp;
    }
    public void setTimes(Integer times) 
    {
        this.times = times;
    }

    public Integer getTimes() 
    {
        return times;
    }
    public void setFileName(String fileName) 
    {
        this.fileName = fileName;
    }

    public String getFileName() 
    {
        return fileName;
    }
    public void setPid(String pid) 
    {
        this.pid = pid;
    }

    public String getPid() 
    {
        return pid;
    }
    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setDeleteTime(Date deleteTime) 
    {
        this.deleteTime = deleteTime;
    }

    public Date getDeleteTime() 
    {
        return deleteTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("sourceIp", getSourceIp())
            .append("targetIp", getTargetIp())
            .append("times", getTimes())
            .append("fileName", getFileName())
            .append("pid", getPid())
            .append("status", getStatus())
            .append("name", getName())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("deleteTime", getDeleteTime())
            .toString();
    }
}
