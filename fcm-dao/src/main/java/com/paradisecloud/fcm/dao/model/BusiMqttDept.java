package com.paradisecloud.fcm.dao.model;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * busi_mqtt_dept
 * 
 * @author zyz
 * @date 2021-07-21
 */
@Schema(description = "mqtt信息与租户的关联")
public class BusiMqttDept extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    @Schema(description = "主键id")
    private Long id;

    /** 修改时间 */
    @Schema(description = "修改时间")
    @Excel(name = "修改时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateTime;

    /** 关联租户 */
    @Schema(description = "关联租户")
    @Excel(name = "关联租户")
    private Long deptId;

    /** 1单节点，100集群 */
    @Schema(description = "1单节点，100集群")
    @Excel(name = "1单节点，100集群")
    private Integer mqttType;

    /** 当mqtt_type为1是，指向busi_mqtt的id字段，为100指向busi_mqtt_cluster的id字段 */
    @Schema(description = "当mqtt_type为1是，指向busi_mqtt的id字段，为100指向busi_mqtt_cluster的id字段")
    @Excel(name = "当mqtt_type为1是，指向busi_mqtt的id字段，为100指向busi_mqtt_cluster的id字段")
    private Long mqttId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setUpdateTime(Date updateTime) 
    {
        this.updateTime = updateTime;
    }

    public Date getUpdateTime() 
    {
        return updateTime;
    }
    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }
    public void setMqttType(Integer mqttType) 
    {
        this.mqttType = mqttType;
    }

    public Integer getMqttType() 
    {
        return mqttType;
    }
    public void setMqttId(Long mqttId) 
    {
        this.mqttId = mqttId;
    }

    public Long getMqttId() 
    {
        return mqttId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("deptId", getDeptId())
            .append("mqttType", getMqttType())
            .append("mqttId", getMqttId())
            .toString();
    }
}
