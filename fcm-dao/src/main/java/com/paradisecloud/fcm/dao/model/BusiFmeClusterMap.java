package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * FME-终端组中间（多对多）对象 busi_fme_cluster_map
 * 
 * @author lilinhai
 * @date 2021-03-17
 */
@Schema(description = "FME-终端组中间（多对多）")
public class BusiFmeClusterMap extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** FME集群ID */
    @Schema(description = "FME集群ID")
    @Excel(name = "FME集群ID")
    private Long clusterId;

    /** FME的ID */
    @Schema(description = "FME的ID")
    @Excel(name = "FME的ID")
    private Long fmeId;

    /** 节点在集群中的权重值 */
    @Schema(description = "节点在集群中的权重值")
    @Excel(name = "节点在集群中的权重值")
    private Integer weight;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setClusterId(Long clusterId) 
    {
        this.clusterId = clusterId;
    }

    public Long getClusterId() 
    {
        return clusterId;
    }
    public void setFmeId(Long fmeId) 
    {
        this.fmeId = fmeId;
    }

    public Long getFmeId() 
    {
        return fmeId;
    }
    public void setWeight(Integer weight) 
    {
        this.weight = weight;
    }

    public Integer getWeight() 
    {
        return weight;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("clusterId", getClusterId())
            .append("fmeId", getFmeId())
            .append("weight", getWeight())
            .toString();
    }
}
