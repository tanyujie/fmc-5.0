package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 *  busi_mqtt_cluster_map
 * 
 * @author zyz
 * @date 2021-07-21
 */
@Schema(description = "matt集群关联表")
public class BusiMqttClusterMap extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    @Schema(description = "主键id")
    private Long id;

    /** mqtt集群id */
    @Schema(description = "mqtt集群id")
    @Excel(name = "mqtt集群id")
    private Long clusterId;

    /** mqtt的id */
    @Schema(description = "mqtt的id")
    @Excel(name = "mqtt的id")
    private Long mqttId;
    
    /** 节点在集群中的权重 */
    @Schema(description = "节点在集群中的权重")
    @Excel(name = "节点在集群中的权重")
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
    public void setMqttId(Long mqttId) 
    {
        this.mqttId = mqttId;
    }

    public Long getMqttId() 
    {
        return mqttId;
    }
    
    public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	@Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("clusterId", getClusterId())
            .append("mqttId", getMqttId())
            .append("weight", getWeight())
            .toString();
    }
}
