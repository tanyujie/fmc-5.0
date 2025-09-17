package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户终端对象 busi_user_terminal
 * 
 * @author lilinhai
 * @date 2022-06-24
 */
@Schema(description = "用户终端")
public class BusiUserTerminal extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 用户ID */
    @Schema(description = "用户ID")
    @Excel(name = "用户ID")
    private Long userId;

    /** 终端ID */
    @Schema(description = "终端ID")
    @Excel(name = "终端ID")
    private Long terminalId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setTerminalId(Long terminalId) 
    {
        this.terminalId = terminalId;
    }

    public Long getTerminalId() 
    {
        return terminalId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("terminalId", getTerminalId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .toString();
    }
}
