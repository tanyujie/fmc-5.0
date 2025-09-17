package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * callBranding模板对象 busi_profile_call_branding
 * 
 * @author lilinhai
 * @date 2021-07-27
 */
@Schema(description = "callBranding模板")
public class BusiProfileCallBranding extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** callBranding模板uuid，fme侧记录id */
    @Schema(description = "callBranding模板uuid，fme侧记录id")
    @Excel(name = "callBranding模板uuid，fme侧记录id")
    private String callBrandingProfileUuid;

    /** profile名称 */
    @Schema(description = "profile名称")
    @Excel(name = "profile名称")
    private String name;

    /** 关联部门ID */
    @Schema(description = "关联部门ID")
    @Excel(name = "关联部门ID")
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
    public void setCallBrandingProfileUuid(String callBrandingProfileUuid) 
    {
        this.callBrandingProfileUuid = callBrandingProfileUuid;
    }

    public String getCallBrandingProfileUuid() 
    {
        return callBrandingProfileUuid;
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
            .append("callBrandingProfileUuid", getCallBrandingProfileUuid())
            .append("name", getName())
            .append("deptId", getDeptId())
            .append("isDefault", getIsDefault())
            .toString();
    }
}
