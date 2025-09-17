package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

/**
 * 【请填写功能名称】对象 busi_packet
 * 
 * @author lilinhai
 * @date 2024-07-09
 */
@Schema(description = "【请填写功能名称】")
public class BusiPacket extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Integer id;

    /** 服务器IP */
    @Schema(description = "服务器IP")
    @Excel(name = "服务器IP")
    @NotBlank
    private String ip;

    /** 账户 */
    @Schema(description = "账户")
    @Excel(name = "账户")
    @NotBlank
    private String username;

    /** 密码 */
    @Schema(description = "密码")
    @Excel(name = "密码")
    @NotBlank
    private String password;

    public void setId(Integer id) 
    {
        this.id = id;
    }

    public Integer getId() 
    {
        return id;
    }
    public void setIp(String ip) 
    {
        this.ip = ip;
    }

    public String getIp() 
    {
        return ip;
    }
    public void setUsername(String username) 
    {
        this.username = username;
    }

    public String getUsername() 
    {
        return username;
    }
    public void setPassword(String password) 
    {
        this.password = password;
    }

    public String getPassword() 
    {
        return password;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("ip", getIp())
            .append("username", getUsername())
            .append("password", getPassword())
            .toString();
    }
}
