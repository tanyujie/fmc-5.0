package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ops配置信息对象 busi_ops_info
 * 
 * @author lilinhai
 * @date 2024-05-27
 */
@Schema(description = "ops配置信息")
public class BusiOpsInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Integer id;

    /** IP 地址 */
    @Schema(description = "IP 地址")
    @Excel(name = "IP 地址")
    private String ipAddress;

    @Schema(description = "fmeIp 地址")
    @Excel(name = "fmeIp 地址")
    private String fmeIp;

    /** 子网掩码 */
    @Schema(description = "子网掩码")
    @Excel(name = "子网掩码")
    private String subnetMask;

    /** 网关 */
    @Schema(description = "网关")
    @Excel(name = "网关")
    private String gatewayName;

    /** 初始admin密码 */
    @Schema(description = "初始admin密码")
    @Excel(name = "初始admin密码")
    private String password;

    @Schema(description = "sn")
    @Excel(name = "sn")
    private String sn;

    @Schema(description = "publicIp")
    @Excel(name = "publicIp")
    private String publicIp;

    public void setId(Integer id) 
    {
        this.id = id;
    }

    public Integer getId() 
    {
        return id;
    }
    public void setIpAddress(String ipAddress) 
    {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() 
    {
        return ipAddress;
    }
    public void setSubnetMask(String subnetMask) 
    {
        this.subnetMask = subnetMask;
    }

    public String getSubnetMask() 
    {
        return subnetMask;
    }
    public void setGatewayName(String gatewayName) 
    {
        this.gatewayName = gatewayName;
    }

    public String getGatewayName() 
    {
        return gatewayName;
    }
    public void setPassword(String password) 
    {
        this.password = password;
    }

    public String getPassword() 
    {
        return password;
    }


    public String getFmeIp() {
        return fmeIp;
    }

    public void setFmeIp(String fmeIp) {
        this.fmeIp = fmeIp;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("ipAddress", getIpAddress())
            .append("subnetMask", getSubnetMask())
            .append("gatewayName", getGatewayName())
            .append("password", getPassword())
                .append("fmeIp", getFmeIp())
                .append("sn", getSn())
                .append("public",getPublicIp())
            .toString();
    }
}
