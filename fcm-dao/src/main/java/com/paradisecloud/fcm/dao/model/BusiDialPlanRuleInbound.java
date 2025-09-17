package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 内呼计划对象 busi_dial_plan_rule_inbound
 * 
 * @author lilinhai
 * @date 2022-05-13
 */
@Schema(description = "内呼计划")
public class BusiDialPlanRuleInbound extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Integer id;

    /** 业务uuid */
    @Schema(description = "业务uuid")
    @Excel(name = "业务uuid")
    private String planUuid;

    /** 部门 */
    @Schema(description = "部门")
    @Excel(name = "部门")
    private Long deptId;

    public void setId(Integer id) 
    {
        this.id = id;
    }

    public Integer getId() 
    {
        return id;
    }
    public void setPlanUuid(String planUuid) 
    {
        this.planUuid = planUuid;
    }

    public String getPlanUuid() 
    {
        return planUuid;
    }
    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("planUuid", getPlanUuid())
            .append("deptId", getDeptId())
            .toString();
    }
}
