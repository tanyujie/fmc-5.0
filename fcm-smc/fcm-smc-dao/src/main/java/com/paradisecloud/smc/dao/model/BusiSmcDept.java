package com.paradisecloud.smc.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 【请填写功能名称】对象 busi_smc_dept
 * 
 * @author liuxilong
 * @date 2022-08-25
 */
@Schema(description = "【请填写功能名称】")
public class BusiSmcDept extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 分配给的租户 */
    @Schema(description = "分配给的租户")
    @Excel(name = "分配给的租户")
    private Long deptId;

    /** 1单节点，100集群 */
    @Schema(description = "1单节点，100集群")
    @Excel(name = "1单节点，100集群")
    private Integer smcType;

    /** 当smc_type为1是，指向busi_smc的id字段，为100指向busi_smc_cluster的id字段 */
    @Schema(description = "当smc_type为1是，指向busi_smc的id字段，为100指向busi_smc_cluster的id字段")
    @Excel(name = "当smc_type为1是，指向busi_smc的id字段，为100指向busi_smc_cluster的id字段")
    private Long smcId;

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
    public void setSmcType(Integer smcType)
    {
        this.smcType = smcType;
    }

    public Integer getSmcType()
    {
        return smcType;
    }
    public void setSmcId(Long smcId)
    {
        this.smcId = smcId;
    }

    public Long getSmcId()
    {
        return smcId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("deptId", getDeptId())
            .append("smcType", getSmcType())
            .append("smcId", getSmcId())
            .toString();
    }
}
