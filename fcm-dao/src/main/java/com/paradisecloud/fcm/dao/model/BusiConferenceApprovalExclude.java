package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 会议审批排除对象 busi_conference_approval_exclude
 * 
 * @author lilinhai
 * @date 2025-03-22
 */
@Schema(description = "会议审批排除")
public class BusiConferenceApprovalExclude extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 类型 0：部门 1：用户 */
    @Schema(description = "类型 0：部门 1：用户")
    @Excel(name = "类型 0：部门 1：用户")
    private Integer type;

    /** 排除ID 类型为部门时为部门ID，类型为用户时为用户ID */
    @Schema(description = "排除ID 类型为部门时为部门ID，类型为用户时为用户ID")
    @Excel(name = "排除ID 类型为部门时为部门ID，类型为用户时为用户ID")
    private Long excludeId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setType(Integer type) 
    {
        this.type = type;
    }

    public Integer getType() 
    {
        return type;
    }
    public void setExcludeId(Long excludeId) 
    {
        this.excludeId = excludeId;
    }

    public Long getExcludeId() 
    {
        return excludeId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("type", getType())
            .append("excludeId", getExcludeId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .toString();
    }
}
