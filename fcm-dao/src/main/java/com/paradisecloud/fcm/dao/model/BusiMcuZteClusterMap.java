package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 中兴MCU-终端组中间（多对多）对象 busi_mcu_zte_cluster_map
 * 
 * @author lilinhai
 * @date 2024-04-09
 */
@Schema(description = "中兴MCU-终端组中间（多对多）")
public class BusiMcuZteClusterMap extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** FME集群ID */
    @Schema(description = "FME集群ID")
    @Excel(name = "FME集群ID")
    private Long clusterId;

    /** MCU的ID */
    @Schema(description = "MCU的ID")
    @Excel(name = "MCU的ID")
    private Long mcuId;

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
    public void setMcuId(Long mcuId) 
    {
        this.mcuId = mcuId;
    }

    public Long getMcuId() 
    {
        return mcuId;
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
            .append("mcuId", getMcuId())
            .append("weight", getWeight())
            .toString();
    }
}
