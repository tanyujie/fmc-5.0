package com.paradisecloud.fcm.dao.model;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 信息展示对象 busi_info_display
 * 
 * @author lilinhai
 * @date 2024-05-13
 */
@Schema(description = "信息展示")
public class BusiInfoDisplay extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 名称 */
    @Schema(description = "名称")
    @Excel(name = "名称")
    private String name;

    /** 部门ID */
    @Schema(description = "部门ID")
    @Excel(name = "部门ID")
    private Long deptId;

    /** 类型 1：开机信息 2：一般信息 */
    @Schema(description = "类型 1：开机信息 2：一般信息")
    @Excel(name = "类型 1：开机信息 2：一般信息")
    private Integer type;

    /** 显示类型 1：图片 2：视频 */
    @Schema(description = "显示类型 1：图片 2：视频")
    @Excel(name = "显示类型 1：图片 2：视频")
    private Integer displayType;

    /** 地址或数据 */
    @Schema(description = "地址或数据")
    @Excel(name = "地址或数据")
    private String urlData;

    /** 推送类型 1：终端 2：选中会议室"*/
    @Schema(description = "推送类型 1：终端 2：选中会议室")
    @Excel(name = "推送类型 1：终端 2：选中会议室")
    private Integer pushType;

    /** 推送对象 1：本部门终端 2：本部门及下级部门终端 3：自定义选择"*/
    @Schema(description = "推送对象 1：本部门终端 2：本部门及下级部门终端 3：自定义选择")
    @Excel(name = "推送对象 1：本部门终端 2：本部门及下级部门终端 3：自定义选择")
    private Integer pushObject;

    /** 推送终端 */
    @Schema(description = "推送终端")
    @Excel(name = "推送终端")
    private String pushTerminalIds;

    /** 最后推送时间 */
    @Schema(description = "最后推送时间")
    @Excel(name = "最后推送时间", width = 30)
    private Date lastPushTime;

    /** 状态 1：启用 2：未启用 */
    @Schema(description = "状态 1：启用 2：未启用")
    @Excel(name = "状态 1：启用 2：未启用")
    private Integer status;

    /** 展示时长 */
    @Schema(description = "展示时长")
    @Excel(name = "展示时长")
    private Integer durationTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }
    public void setType(Integer type) 
    {
        this.type = type;
    }

    public Integer getType() 
    {
        return type;
    }
    public void setDisplayType(Integer displayType) 
    {
        this.displayType = displayType;
    }

    public Integer getDisplayType() 
    {
        return displayType;
    }
    public void setUrlData(String urlData) 
    {
        this.urlData = urlData;
    }

    public String getUrlData() 
    {
        return urlData;
    }
    public void setPushType(Integer pushType) 
    {
        this.pushType = pushType;
    }

    public Integer getPushType() 
    {
        return pushType;
    }
    public void setPushTerminalIds(String pushTerminalIds) 
    {
        this.pushTerminalIds = pushTerminalIds;
    }

    public String getPushTerminalIds() 
    {
        return pushTerminalIds;
    }
    public void setLastPushTime(Date lastPushTime) 
    {
        this.lastPushTime = lastPushTime;
    }

    public Date getLastPushTime() 
    {
        return lastPushTime;
    }
    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }

    public Integer getPushObject() {
        return pushObject;
    }

    public void setPushObject(Integer pushObject) {
        this.pushObject = pushObject;
    }

    public Integer getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(Integer durationTime) {
        this.durationTime = durationTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("deptId", getDeptId())
            .append("type", getType())
            .append("displayType", getDisplayType())
            .append("urlData", getUrlData())
            .append("pushType", getPushType())
            .append("pushTerminalIds", getPushTerminalIds())
            .append("lastPushTime", getLastPushTime())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("pushObject", getPushObject())
            .append("durationTime", getDurationTime())
            .toString();
    }
}
