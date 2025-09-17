package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * SMC3.0MCU终端信息对象 busi_mcu_smc3
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
@Schema(description = "SMC3.0MCU终端信息")
public class BusiMcuHwcloud extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 企业id */
    @Schema(description = "企业id")
    @Excel(name = "企业id")
    private String appId;


    /** 安全凭证密钥KEY */
    @Schema(description = "安全凭证密钥KEY")
    @Excel(name = "安全凭证密钥KEY")
    private String appKey;

    /** tencent显示名字 */
    @Schema(description = "tencent显示名字")
    @Excel(name = "tencent显示名字")
    private String name;

    /** smc在线状态：1在线，2离线，3删除 */
    @Schema(description = "smc在线状态：1在线，2离线，3删除")
    @Excel(name = "smc在线状态：1在线，2离线，3删除")
    private Integer status;

    /** tencent会议容量 */
    @Schema(description = "tencent会议容量")
    @Excel(name = "tencent会议容量")
    private Integer capacity;

    /** 备用（本节点宕机后指向的备用节点） */
    @Schema(description = "备用（本节点宕机后指向的备用节点）")
    @Excel(name = "备用", readConverterExp = "本=节点宕机后指向的备用节点")
    private Long spareSmcId;

    /** API调用IP */
    @Schema(description = "API调用IP")
    @Excel(name = "API调用IP")
    private String apiIp;

    /** API调用端口 */
    @Schema(description = "API调用端口")
    @Excel(name = "API调用端口")
    private Integer apiPort;

    /** 呼叫IP */
    @Schema(description = "呼叫IP")
    @Excel(name = "呼叫IP")
    private String callIp;

    /** 呼叫端口 */
    @Schema(description = "呼叫端口")
    @Excel(name = "呼叫端口")
    private Integer callPort;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getStatus()
    {
        return status;
    }
    public void setCapacity(Integer capacity)
    {
        this.capacity = capacity;
    }

    public Integer getCapacity()
    {
        return capacity;
    }
    public void setSpareSmcId(Long spareSmcId)
    {
        this.spareSmcId = spareSmcId;
    }

    public Long getSpareSmcId()
    {
        return spareSmcId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getApiIp() {
        return apiIp;
    }

    public void setApiIp(String apiIp) {
        this.apiIp = apiIp;
    }

    public Integer getApiPort() {
        return apiPort;
    }

    public void setApiPort(Integer apiPort) {
        this.apiPort = apiPort;
    }

    public String getCallIp() {
        return callIp;
    }

    public void setCallIp(String callIp) {
        this.callIp = callIp;
    }

    public Integer getCallPort() {
        return callPort;
    }

    public void setCallPort(Integer callPort) {
        this.callPort = callPort;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("appId", getAppId())
                .append("name", getName())
                .append("status", getStatus())
                .append("capacity", getCapacity())
                .append("spareSmcId", getSpareSmcId())
                .append("apiIp", getApiIp())
                .append("apiPort", getApiPort())
                .append("callIp", getCallIp())
                .append("callPort", getCallPort())
                .toString();
    }


}
