package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 智慧办公第三方OA对象 busi_smart_room_third_oa
 * 
 * @author lilinhai
 * @date 2024-03-04
 */
@Schema(description = "智慧办公第三方OA")
public class BusiSmartRoomThirdOa extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** OA类型 0：非第三方 1：企业微信 2：钉钉 */
    @Schema(description = "OA类型 0：非第三方 1：企业微信 2：钉钉")
    @Excel(name = "OA类型 0：非第三方 1：企业微信 2：钉钉")
    private Integer oaType;

    /** 服务器地址 */
    @Schema(description = "服务器地址")
    @Excel(name = "服务器地址")
    private String host;

    /** 端口 */
    @Schema(description = "端口")
    @Excel(name = "端口")
    private Integer port;

    /** 第三方ID */
    @Schema(description = "第三方ID")
    @Excel(name = "第三方ID")
    private String suiteId;

    /** 第三方secret */
    @Schema(description = "第三方secret")
    @Excel(name = "第三方secret")
    private String suiteSecret;

    /** 第三方access token */
    @Schema(description = "第三方access token")
    @Excel(name = "第三方access token")
    private String suiteAccessToken;

    /** 第三方access token过期时间 */
    @Schema(description = "第三方access token过期时间")
    @Excel(name = "第三方access token过期时间")
    private String suiteAccessTokenExpiredTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setOaType(Integer oaType) 
    {
        this.oaType = oaType;
    }

    public Integer getOaType() 
    {
        return oaType;
    }
    public void setHost(String host) 
    {
        this.host = host;
    }

    public String getHost() 
    {
        return host;
    }
    public void setPort(Integer port) 
    {
        this.port = port;
    }

    public Integer getPort() 
    {
        return port;
    }
    public void setSuiteId(String suiteId) 
    {
        this.suiteId = suiteId;
    }

    public String getSuiteId() 
    {
        return suiteId;
    }
    public void setSuiteSecret(String suiteSecret) 
    {
        this.suiteSecret = suiteSecret;
    }

    public String getSuiteSecret() 
    {
        return suiteSecret;
    }
    public void setSuiteAccessToken(String suiteAccessToken) 
    {
        this.suiteAccessToken = suiteAccessToken;
    }

    public String getSuiteAccessToken() 
    {
        return suiteAccessToken;
    }

    public String getSuiteAccessTokenExpiredTime() {
        return suiteAccessTokenExpiredTime;
    }

    public void setSuiteAccessTokenExpiredTime(String suiteAccessTokenExpiredTime) {
        this.suiteAccessTokenExpiredTime = suiteAccessTokenExpiredTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("oaType", getOaType())
            .append("host", getHost())
            .append("port", getPort())
            .append("suiteId", getSuiteId())
            .append("suiteSecret", getSuiteSecret())
            .append("suiteAccessToken", getSuiteAccessToken())
            .append("suiteAccessTokenExpiredTime", getSuiteAccessTokenExpiredTime())
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .append("updateTime", getUpdateTime())
            .append("updateBy", getUpdateBy())
            .append("remark", getRemark())
            .toString();
    }
}
