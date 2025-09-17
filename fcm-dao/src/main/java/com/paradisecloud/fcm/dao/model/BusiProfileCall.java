package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * call模板对象 busi_profile_call
 * 
 * @author lilinhai
 * @date 2021-07-27
 */
@Schema(description = "call模板")
public class BusiProfileCall extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** callprofile的id，fme侧记录数据的id */
    @Schema(description = "callprofile的id，fme侧记录数据的id")
    @Excel(name = "callprofile的id，fme侧记录数据的id")
    private String callProfileUuid;

    /** profile名称 */
    @Schema(description = "profile名称")
    @Excel(name = "profile名称")
    private String name;

    /** 部门ID */
    @Schema(description = "部门ID")
    @Excel(name = "部门ID")
    private Long deptId;

    /** 是否默认 */
    @Schema(description = "是否默认")
    @Excel(name = "是否默认")
    private Integer isDefault;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setCallProfileUuid(String callProfileUuid) 
    {
        this.callProfileUuid = callProfileUuid;
    }

    public String getCallProfileUuid() 
    {
        return callProfileUuid;
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
    public void setIsDefault(Integer isDefault) 
    {
        this.isDefault = isDefault;
    }

    public Integer getIsDefault() 
    {
        return isDefault;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("callProfileUuid", getCallProfileUuid())
            .append("name", getName())
            .append("deptId", getDeptId())
            .append("isDefault", getIsDefault())
            .toString();
    }
}
