package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 直播服务器-直播集群组中间（多对多）对象 busi_live_cluster_map
 * 
 * @author lilinhai
 * @date 2022-10-26
 */
@Schema(description = "直播服务器-直播集群组中间（多对多）")
public class BusiLiveClusterMap extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 集群ID */
    @Schema(description = "集群ID")
    @Excel(name = "集群ID")
    private Long clusterId;

    /** live的ID */
    @Schema(description = "live的ID")
    @Excel(name = "live的ID")
    private Long liveId;

    /** 节点在集群中的权重值 */
    @Schema(description = "节点在集群中的权重值")
    @Excel(name = "节点在集群中的权重值")
    private Integer weight;

    /** 直播服务器类型: 0:拉流 1:推流 */
    @Schema(description = "直播服务器类型: 0:拉流 1:推流")
    @Excel(name = "直播服务器类型: 0:拉流 1:推流")
    private Integer liveType;

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
    public void setLiveId(Long liveId) 
    {
        this.liveId = liveId;
    }

    public Long getLiveId() 
    {
        return liveId;
    }
    public void setWeight(Integer weight) 
    {
        this.weight = weight;
    }

    public Integer getWeight() 
    {
        return weight;
    }
    public void setLiveType(Integer liveType) 
    {
        this.liveType = liveType;
    }

    public Integer getLiveType() 
    {
        return liveType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("clusterId", getClusterId())
            .append("liveId", getLiveId())
            .append("weight", getWeight())
            .append("liveType", getLiveType())
            .toString();
    }
}
