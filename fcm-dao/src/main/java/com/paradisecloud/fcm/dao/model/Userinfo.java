package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 【FS注册用户】对象 userinfo
 * 
 * @author lilinhai
 * @date 2024-07-15
 */
@Schema(description = "【FS注册用户】")
public class Userinfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键Id */
    @Schema(description = "主键Id")
    private Integer id;

    /** 用户名 */
    @Schema(description = "用户名")
    @Excel(name = "用户名", readConverterExp = "用户名")
    private String username;

    /** 密码 */
    @Schema(description = "密码")
    @Excel(name = "密码", readConverterExp = "密码")
    private String password;

    public void setId(Integer id) 
    {
        this.id = id;
    }

    public Integer getId() 
    {
        return id;
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
            .append("username", getUsername())
            .append("password", getPassword())
            .toString();
    }
}
