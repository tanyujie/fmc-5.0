package com.paradisecloud.fcm.dao.model;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * cdr使用情况对象 cdr_terminal_usage
 * 
 * @author lilinhai
 * @date 2022-06-08
 */
@Schema(description = "cdr使用情况")
public class CdrTerminalUsage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 部门Id */
    @Schema(description = "部门Id")
    @Excel(name = "部门Id")
    private Long deptId;

    /** 终端Id */
    @Schema(description = "终端Id")
    @Excel(name = "终端Id")
    private Long terminalId;

    /** 日期 */
    @Schema(description = "日期")
    @Excel(name = "日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date date;

    /** 参会数量 */
    @Schema(description = "参会数量")
    @Excel(name = "参会数量")
    private Integer num;

    /** 参会时长（秒） */
    @Schema(description = "参会时长（秒）")
    @Excel(name = "参会时长", readConverterExp = "秒=")
    private Integer durationSeconds;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public Long getDeptId()
    {
        return deptId;
    }
    public void setTerminalId(Long terminalId)
    {
        this.terminalId = terminalId;
    }

    public Long getTerminalId()
    {
        return terminalId;
    }
    public void setDate(Date date) 
    {
        this.date = date;
    }

    public Date getDate() 
    {
        return date;
    }
    public void setNum(Integer num) 
    {
        this.num = num;
    }

    public Integer getNum() 
    {
        return num;
    }
    public void setDurationSeconds(Integer durationSeconds) 
    {
        this.durationSeconds = durationSeconds;
    }

    public Integer getDurationSeconds() 
    {
        return durationSeconds;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deptId", getDeptId())
            .append("terminalId", getTerminalId())
            .append("date", getDate())
            .append("num", getNum())
            .append("durationSeconds", getDurationSeconds())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
