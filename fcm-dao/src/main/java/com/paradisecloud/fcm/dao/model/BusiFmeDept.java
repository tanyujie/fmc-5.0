package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）对象 busi_fme_dept
 * 
 * @author lilinhai
 * @date 2021-03-17
 */
@Schema(description = "FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）")
public class BusiFmeDept extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 分配给的租户 */
    @Schema(description = "分配给的租户")
    @Excel(name = "分配给的租户")
    private Long deptId;

    /** 1单节点，100集群 */
    @Schema(description = "1单节点，100集群")
    @Excel(name = "1单节点，100集群")
    private Integer fmeType;

    /** 当fme_type为1是，指向busi_fme的id字段，为100指向busi_fme_cluster的id字段 */
    @Schema(description = "当fme_type为1是，指向busi_fme的id字段，为100指向busi_fme_cluster的id字段")
    @Excel(name = "当fme_type为1是，指向busi_fme的id字段，为100指向busi_fme_cluster的id字段")
    private Long fmeId;

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
    public void setFmeType(Integer fmeType) 
    {
        this.fmeType = fmeType;
    }

    public Integer getFmeType() 
    {
        return fmeType;
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
            .append("deptId", getDeptId())
            .append("fmeType", getFmeType())
            .append("fmeId", getFmeId())
            .toString();
    }
}
