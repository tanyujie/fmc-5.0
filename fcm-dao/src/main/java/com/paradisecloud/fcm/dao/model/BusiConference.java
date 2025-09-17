package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 活跃会议室信息，用于存放活跃的会议室对象 busi_conference
 * 
 * @author lilinhai
 * @date 2021-02-04
 */
@Schema(description = "活跃会议室信息，用于存放活跃的会议室")
public class BusiConference extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 创建者ID */
    @Schema(description = "创建者ID")
    @Excel(name = "创建者ID")
    private Long createUserId;

    /** 创建者用户名 */
    @Schema(description = "创建者用户名")
    @Excel(name = "创建者用户名")
    private String createUserName;

    /** 会议名 */
    @Schema(description = "会议名")
    @Excel(name = "会议名")
    private String name;

    /** 是否是会议的主体（发起者） */
    @Schema(description = "是否是会议的主体（发起者）")
    @Excel(name = "是否是会议的主体", readConverterExp = "发=起者")
    private Integer isMain;

    /** 活跃会议室用的会议号 */
    @Schema(description = "活跃会议室用的会议号")
    @Excel(name = "活跃会议室用的会议号")
    private Long conferenceNumber;

    /** 活跃会议室spaceId */
    @Schema(description = "活跃会议室spaceId")
    @Excel(name = "活跃会议室spaceId")
    private String coSpaceId;

    /** 所有级联在一起的会议和子会议的相同ID */
    @Schema(description = "所有级联在一起的会议和子会议的相同ID")
    @Excel(name = "所有级联在一起的会议和子会议的相同ID")
    private String cascadeId;

    /** 活跃会议室对应的部门 */
    @Schema(description = "活跃会议室对应的部门")
    @Excel(name = "活跃会议室对应的部门")
    private Long deptId;

    /** 当前正在进行中的会议室序列化数据 */
    @Schema(description = "当前正在进行中的会议室序列化数据")
    @Excel(name = "当前正在进行中的会议室序列化数据")
    private byte[] data;

    /** 模板会议的ID，标记出是哪个模板发起的，好从模板点击进入会议进行关联 */
    @Schema(description = "模板会议的ID，标记出是哪个模板发起的，好从模板点击进入会议进行关联")
    @Excel(name = "模板会议的ID，标记出是哪个模板发起的，好从模板点击进入会议进行关联")
    private Long templateConferenceId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setCreateUserId(Long createUserId) 
    {
        this.createUserId = createUserId;
    }

    public Long getCreateUserId() 
    {
        return createUserId;
    }
    public void setCreateUserName(String createUserName) 
    {
        this.createUserName = createUserName;
    }

    public String getCreateUserName() 
    {
        return createUserName;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setIsMain(Integer isMain) 
    {
        this.isMain = isMain;
    }

    public Integer getIsMain() 
    {
        return isMain;
    }
    public void setConferenceNumber(Long conferenceNumber) 
    {
        this.conferenceNumber = conferenceNumber;
    }

    public Long getConferenceNumber() 
    {
        return conferenceNumber;
    }
    public void setCoSpaceId(String coSpaceId) 
    {
        this.coSpaceId = coSpaceId;
    }

    public String getCoSpaceId() 
    {
        return coSpaceId;
    }
    public void setCascadeId(String cascadeId) 
    {
        this.cascadeId = cascadeId;
    }

    public String getCascadeId() 
    {
        return cascadeId;
    }
    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }
    public void setData(byte[] data) 
    {
        this.data = data;
    }

    public byte[] getData() 
    {
        return data;
    }
    public void setTemplateConferenceId(Long templateConferenceId) 
    {
        this.templateConferenceId = templateConferenceId;
    }

    public Long getTemplateConferenceId() 
    {
        return templateConferenceId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("createUserId", getCreateUserId())
            .append("createUserName", getCreateUserName())
            .append("name", getName())
            .append("isMain", getIsMain())
            .append("conferenceNumber", getConferenceNumber())
            .append("coSpaceId", getCoSpaceId())
            .append("cascadeId", getCascadeId())
            .append("deptId", getDeptId())
            .append("data", getData())
            .append("templateConferenceId", getTemplateConferenceId())
            .toString();
    }
}