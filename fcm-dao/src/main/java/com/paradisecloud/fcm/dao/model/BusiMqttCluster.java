package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 *  busi_mqtt_cluster
 * 
 * @author zyz
 * @date 2021-07-21
 */
@Schema(description = "mqtt集群信息")
public class BusiMqttCluster extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    @Schema(description = "主键id")
    private Long id;

    /** 集群组名 */
    @Schema(description = "集群组名")
    @Excel(name = "集群组名")
    private String mqttClusterName;

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
    public void setMqttClusterName(String mqttClusterName) 
    {
        this.mqttClusterName = mqttClusterName;
    }

    public String getMqttClusterName() 
    {
        return mqttClusterName;
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
            .append("mqttClusterName", getMqttClusterName())
            .append("description", getDescription())
            .toString();
    }
}
