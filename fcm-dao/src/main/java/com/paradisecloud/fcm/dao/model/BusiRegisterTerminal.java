package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 需绑定fs账号的终端对象 busi_register_terminal
 * 
 * @author zyz
 * @date 2021-11-04
 */
@Schema(description = "需绑定fs账号的终端")
public class BusiRegisterTerminal extends BaseEntity
{
	private static final long serialVersionUID = 1L;

    /** 主键id */
    @Schema(description = "主键id")
    private Long id;

    /** 终端序列号 */
    @Schema(description = "终端序列号")
    @Excel(name = "终端序列号")
    private String sn;

    /** 终端内网ip地址 */
    @Schema(description = "终端内网ip地址")
    @Excel(name = "终端内网ip地址")
    private String ip;

    /** 注册码 */
    @Schema(description = "注册码")
    @Excel(name = "注册码")
    private String code;

    /** mac地址 */
    @Schema(description = "mac地址")
    @Excel(name = "mac地址")
    private String mac;

    /** 终端类型 */
    @Schema(description = "终端类型")
    @Excel(name = "终端类型")
    private String terminalType;

    /** 终端id */
    @Schema(description = "终端id")
    @Excel(name = "终端id")
    private Long terminalId;

    /** 关联终端 0、未关联，1关联 */
    @Schema(description = "关联终端 0、未关联，1关联")
    @Excel(name = "关联终端 0、未关联，1关联")
    private String isRelated;
    
    /** sip账号 */
    @Schema(description = "sip账号")
    @Excel(name = "sip账号")
    private String credential;

    /** APP版本号 */
    @Schema(description = "APP版本号")
    @Excel(name = "APP版本号")
    private String appVersionCode;

    /** APP版本名 */
    @Schema(description = "APP版本名")
    @Excel(name = "APP版本名")
    private String appVersionName;

    /** 连接IP */
    @Schema(description = "连接IP")
    @Excel(name = "连接IP")
    private String connectIp;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setSn(String sn) 
    {
        this.sn = sn;
    }

    public String getSn() 
    {
        return sn;
    }
    public void setIp(String ip) 
    {
        this.ip = ip;
    }

    public String getIp() 
    {
        return ip;
    }
    public void setCode(String code) 
    {
        this.code = code;
    }

    public String getCode() 
    {
        return code;
    }
    public void setMac(String mac) 
    {
        this.mac = mac;
    }

    public String getMac() 
    {
        return mac;
    }
    public void setTerminalType(String terminalType) 
    {
        this.terminalType = terminalType;
    }

    public String getTerminalType() 
    {
        return terminalType;
    }
    public void setTerminalId(Long terminalId) 
    {
        this.terminalId = terminalId;
    }

    public Long getTerminalId() 
    {
        return terminalId;
    }
    public void setIsRelated(String isRelated) 
    {
        this.isRelated = isRelated;
    }

    public String getIsRelated() 
    {
        return isRelated;
    }

    public void setCredential(String credential) 
    {
        this.credential = credential;
    }

    public String getCredential() 
    {
        return credential;
    }

    public String getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(String appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public String getConnectIp() {
        return connectIp;
    }

    public void setConnectIp(String connectIp) {
        this.connectIp = connectIp;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("sn", getSn())
            .append("ip", getIp())
            .append("code", getCode())
            .append("mac", getMac())
            .append("terminalType", getTerminalType())
            .append("terminalId", getTerminalId())
            .append("isRelated", getIsRelated())
            .append("credential", getCredential())
            .append("appVersionCode", getAppVersionCode())
            .append("appVersionName", getAppVersionName())
            .append("connectIp", getConnectIp())
            .toString();
    }
}
