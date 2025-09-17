package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 终端升级对象 busi_terminal_upgrade
 * 
 * @author zyz
 * @date 2021-10-11
 */
@Schema(description = "终端升级")
public class BusiTerminalUpgrade extends BaseEntity
{
	 /** 主键id */
    @Schema(description = "主键id")
    private Long id;

    /** 终端类型 */
    @Schema(description = "终端类型")
    @Excel(name = "终端类型")
    private String terminalType;

    /** 服务器地址 */
    @Schema(description = "服务器地址")
    @Excel(name = "服务器地址")
    private String serverUrl;

    /** 版本号 */
    @Schema(description = "版本号")
    @Excel(name = "版本号")
    private String versionNum;

    /** 版本名 */
    @Schema(description = "版本名")
    @Excel(name = "版本名")
    private String versionName;

    /** 版本描述 */
    @Schema(description = "版本描述")
    @Excel(name = "版本描述")
    private String versionDescription;


    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setTerminalType(String terminalType) 
    {
        this.terminalType = terminalType;
    }

    public String getTerminalType() 
    {
        return terminalType;
    }
    public void setServerUrl(String serverUrl) 
    {
        this.serverUrl = serverUrl;
    }

    public String getServerUrl() 
    {
        return serverUrl;
    }
    public void setVersionNum(String versionNum) 
    {
        this.versionNum = versionNum;
    }

    public String getVersionNum() 
    {
        return versionNum;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionDescription() {
        return versionDescription;
    }

    public void setVersionDescription(String versionDescription) {
        this.versionDescription = versionDescription;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("terminalType", getTerminalType())
            .append("serverUrl", getServerUrl())
            .append("versionNum", getVersionNum())
            .append("versionName", getVersionName())
            .append("versionDescription", getVersionDescription())
            .toString();
    }
}
