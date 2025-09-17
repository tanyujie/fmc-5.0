package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

/**
 * 智慧办公物联网关对象 busi_smart_room_lot
 * 
 * @author lilinhai
 * @date 2024-03-04
 */
@Schema(description = "智慧办公物联网关")
public class BusiSmartRoomLot extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 网关名称 */
    @Schema(description = "网关名称")
    @Excel(name = "网关名称")
    private String lotName;

    /** 网关类型 0：未指定 */
    @Schema(description = "网关类型 0：未指定")
    @Excel(name = "网关类型 0：未指定")
    private Integer lotType;

    /** 网关位置 */
    @Schema(description = "网关位置")
    @Excel(name = "网关位置")
    private String lotPosition;

    /** mqtt连接状态：1在线，2离线 */
    @Schema(description = "mqtt连接状态：1在线，2离线")
    @Excel(name = "mqtt连接状态：1在线，2离线")
    private Integer mqttOnlineStatus;

    /** 品牌 */
    @Schema(description = "品牌")
    @Excel(name = "品牌")
    private String brand;

    /** 网关型号 */
    @Schema(description = "网关型号")
    @Excel(name = "设备型号")
    private String lotModel;

    /** 客户端ID */
    @Schema(description = "客户端ID")
    @Excel(name = "客户端ID")
    private String clientId;

    /** 发布主题 */
    @Schema(description = "发布主题")
    @Excel(name = "发布主题")
    private String pubTopic;

    /** 订阅主题 */
    @Schema(description = "订阅主题")
    @Excel(name = "订阅主题")
    private String subTopic;

    /** 详情（更多网关信息） */
    @Schema(description = "详情（更多网关信息）")
    @Excel(name = "详情（更多网关信息）")
    private Map<String, Object> details;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setLotName(String lotName) 
    {
        this.lotName = lotName;
    }

    public String getLotName() 
    {
        return lotName;
    }
    public void setLotType(Integer lotType) 
    {
        this.lotType = lotType;
    }

    public Integer getLotType() 
    {
        return lotType;
    }
    public void setLotPosition(String lotPosition) 
    {
        this.lotPosition = lotPosition;
    }

    public String getLotPosition() 
    {
        return lotPosition;
    }

    public Integer getMqttOnlineStatus() {
        return mqttOnlineStatus;
    }

    public void setMqttOnlineStatus(Integer mqttOnlineStatus) {
        this.mqttOnlineStatus = mqttOnlineStatus;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getLotModel() {
        return lotModel;
    }

    public void setLotModel(String lotModel) {
        this.lotModel = lotModel;
    }

    public void setClientId(String clientId)
    {
        this.clientId = clientId;
    }

    public String getClientId() 
    {
        return clientId;
    }
    public void setPubTopic(String pubTopic) 
    {
        this.pubTopic = pubTopic;
    }

    public String getPubTopic() 
    {
        return pubTopic;
    }
    public void setSubTopic(String subTopic) 
    {
        this.subTopic = subTopic;
    }

    public String getSubTopic() 
    {
        return subTopic;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("lotName", getLotName())
            .append("lotType", getLotType())
            .append("lotPosition", getLotPosition())
            .append("mqttOnlineStatus", getMqttOnlineStatus())
            .append("brand", getBrand())
            .append("lotModel", getLotModel())
            .append("clientId", getClientId())
            .append("pubTopic", getPubTopic())
            .append("subTopic", getSubTopic())
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .append("updateTime", getUpdateTime())
            .append("updateBy", getUpdateBy())
            .append("remark", getRemark())
            .append("details", getDetails())
            .toString();
    }
}
