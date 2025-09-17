package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * FME集群对象 busi_fme_cluster
 * 
 * @author lilinhai
 * @date 2021-03-19
 */
@Schema(description = "FME集群")
public class BusiFmeCluster extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 组名，最长32 */
    @Schema(description = "组名，最长32")
    @Excel(name = "组名，最长32")
    private String name;

    /** 备用fme类型，1单节点，100集群 */
    @Schema(description = "备用fme类型，1单节点，100集群")
    @Excel(name = "备用fme类型，1单节点，100集群")
    private Integer spareFmeType;

    /** 当fme_type为1是，指向busi_fme的id字段，为100指向busi_fme_cluster的id字段 */
    @Schema(description = "当fme_type为1是，指向busi_fme的id字段，为100指向busi_fme_cluster的id字段")
    @Excel(name = "当fme_type为1是，指向busi_fme的id字段，为100指向busi_fme_cluster的id字段")
    private Long spareFmeId;

    /** 备注信息 */
    @Schema(description = "备注信息")
    @Excel(name = "备注信息")
    private String description;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setSpareFmeType(Integer spareFmeType) 
    {
        this.spareFmeType = spareFmeType;
    }

    public Integer getSpareFmeType() 
    {
        return spareFmeType;
    }
    public void setSpareFmeId(Long spareFmeId) 
    {
        this.spareFmeId = spareFmeId;
    }

    public Long getSpareFmeId() 
    {
        return spareFmeId;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("name", getName())
            .append("spareFmeType", getSpareFmeType())
            .append("spareFmeId", getSpareFmeId())
            .append("description", getDescription())
            .toString();
    }
}