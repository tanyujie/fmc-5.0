package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 入会方案配置，控制参会者进入会议的方案对象 busi_call_leg_profile
 * 
 * @author lilinhai
 * @date 2021-01-26
 */
@Schema(description = "入会方案配置，控制参会者进入会议的方案")
public class BusiCallLegProfile extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 入会方案对应的fme里面的记录的uuid */
    @Schema(description = "入会方案对应的fme里面的记录的uuid")
    @Excel(name = "入会方案对应的fme里面的记录的uuid")
    private String callLegProfileUuid;

    /** 是否是默认入会方案:1是，2否 */
    @Schema(description = "是否是默认入会方案:1是，2否")
    @Excel(name = "是否是默认入会方案:1是，2否")
    private Integer type;

    /** 入会方案归属部门 */
    @Schema(description = "入会方案归属部门")
    @Excel(name = "入会方案归属部门")
    private Long deptId;

    /** 入会方案归属的fme */
    @Schema(description = "入会方案归属的fme")
    @Excel(name = "入会方案归属的fme")
    private Long fmeId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setCallLegProfileUuid(String callLegProfileUuid) 
    {
        this.callLegProfileUuid = callLegProfileUuid;
    }

    public String getCallLegProfileUuid() 
    {
        return callLegProfileUuid;
    }
    public void setType(Integer type) 
    {
        this.type = type;
    }

    public Integer getType() 
    {
        return type;
    }
    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }
    public void setFmeId(Long fmeId) 
    {
        this.fmeId = fmeId;
    }

    public Long getFmeId() 
    {
        return fmeId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("callLegProfileUuid", getCallLegProfileUuid())
            .append("type", getType())
            .append("deptId", getDeptId())
            .append("fmeId", getFmeId())
            .toString();
    }
}
