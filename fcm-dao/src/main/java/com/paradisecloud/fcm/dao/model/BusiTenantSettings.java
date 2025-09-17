package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 租户设置对象 busi_tenant_settings
 * 
 * @author lilinhai
 * @date 2021-08-04
 */
@Schema(description = "租户设置")
public class BusiTenantSettings extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 部门ID */
    @Schema(description = "部门ID")
    @Excel(name = "部门ID")
    private Long deptId;

    /** 对应的fme的租户uuid */
    @Schema(description = "对应的fme的租户uuid")
    @Excel(name = "对应的fme的租户uuid")
    private String fmeTenantUuid;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }
    public void setFmeTenantUuid(String fmeTenantUuid) 
    {
        this.fmeTenantUuid = fmeTenantUuid;
    }

    public String getFmeTenantUuid() 
    {
        return fmeTenantUuid;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("deptId", getDeptId())
            .append("fmeTenantUuid", getFmeTenantUuid())
            .toString();
    }
}
