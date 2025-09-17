package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 终端系统信息对象 busi_terminal_sys_info
 * 
 * @author zyz
 * @date 2021-10-11
 */
@Schema(description = "终端系统信息")
public class BusiTerminalSysInfo extends BaseEntity
{
	private static final long serialVersionUID = 1L;

    /** 主键id */
    @Schema(description = "主键id")
    private Long id;

    /** 终端序列号 */
    @Schema(description = "终端序列号")
    @Excel(name = "终端序列号")
    private String sn;

    /** mac地址 */
    @Schema(description = "mac地址")
    @Excel(name = "mac地址")
    private String macAddr;

    /** 系统版本 */
    @Schema(description = "系统版本")
    @Excel(name = "系统版本")
    private String systemVersion;

    /** boot版本 */
    @Schema(description = "boot版本")
    @Excel(name = "boot版本")
    private String bootVersion;

    /** arm版本 */
    @Schema(description = "arm版本")
    @Excel(name = "arm版本")
    private String armVersion;

    /** fpga版本 */
    @Schema(description = "fpga版本")
    @Excel(name = "fpga版本")
    private String fpgaVersion;

    /** 终端类型 */
    @Schema(description = "终端类型")
    @Excel(name = "终端类型")
    private String terminalType;

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
    public void setMacAddr(String macAddr) 
    {
        this.macAddr = macAddr;
    }

    public String getMacAddr() 
    {
        return macAddr;
    }
    public void setSystemVersion(String systemVersion) 
    {
        this.systemVersion = systemVersion;
    }

    public String getSystemVersion() 
    {
        return systemVersion;
    }
    public void setBootVersion(String bootVersion) 
    {
        this.bootVersion = bootVersion;
    }

    public String getBootVersion() 
    {
        return bootVersion;
    }
    public void setArmVersion(String armVersion) 
    {
        this.armVersion = armVersion;
    }

    public String getArmVersion() 
    {
        return armVersion;
    }
    public void setFpgaVersion(String fpgaVersion) 
    {
        this.fpgaVersion = fpgaVersion;
    }

    public String getFpgaVersion() 
    {
        return fpgaVersion;
    }
    public void setTerminalType(String terminalType) 
    {
        this.terminalType = terminalType;
    }

    public String getTerminalType() 
    {
        return terminalType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("sn", getSn())
            .append("macAddr", getMacAddr())
            .append("systemVersion", getSystemVersion())
            .append("bootVersion", getBootVersion())
            .append("armVersion", getArmVersion())
            .append("fpgaVersion", getFpgaVersion())
            .append("terminalType", getTerminalType())
            .toString();
    }
}
