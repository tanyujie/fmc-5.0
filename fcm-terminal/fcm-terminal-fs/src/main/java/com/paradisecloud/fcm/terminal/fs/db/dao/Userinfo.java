package com.paradisecloud.fcm.terminal.fs.db.dao;

import com.paradisecloud.common.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * free switch的用户信息对象 userinfo
 *
 * @author
 * @date 2022-04-25
 */
public class Userinfo
{

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 用户名 */
    @Schema(description = "用户名")
    @Excel(name = "用户名")
    private String username;

    /** 密码 */
    @Schema(description = "密码")
    @Excel(name = "密码")
    private String password;


    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
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
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("username", getUsername())
                .append("password", getPassword())
                .toString();
    }
}
