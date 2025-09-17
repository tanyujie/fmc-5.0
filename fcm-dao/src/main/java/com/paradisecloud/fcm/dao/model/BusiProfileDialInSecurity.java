package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 呼入安全模板对象 busi_profile_dial_in_security
 * 
 * @author lilinhai
 * @date 2021-07-27
 */
@Schema(description = "呼入安全模板")
public class BusiProfileDialInSecurity extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 呼入安全模板uuid，fme侧记录id */
    @Schema(description = "呼入安全模板uuid，fme侧记录id")
    @Excel(name = "呼入安全模板uuid，fme侧记录id")
    private String dialInSecurityProfileUuid;

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
    public void setDialInSecurityProfileUuid(String dialInSecurityProfileUuid) 
    {
        this.dialInSecurityProfileUuid = dialInSecurityProfileUuid;
    }

    public String getDialInSecurityProfileUuid() 
    {
        return dialInSecurityProfileUuid;
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
            .append("dialInSecurityProfileUuid", getDialInSecurityProfileUuid())
            .append("deptId", getDeptId())
            .append("isDefault", getIsDefault())
            .toString();
    }
}
