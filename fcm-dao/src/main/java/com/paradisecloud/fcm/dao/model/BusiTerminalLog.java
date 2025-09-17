package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 终端日志信息对象 busi_terminal_log
 * 
 * @author lilinhai
 * @date 2021-10-13
 */
@Schema(description = "终端日志信息")
public class BusiTerminalLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    @Schema(description = "主键id")
    private Long id;

    /** 日志名称 */
    @Schema(description = "日志名称")
    @Excel(name = "日志名称")
    private String logFileName;

    /** 日志大小 */
    @Schema(description = "日志大小")
    @Excel(name = "日志大小")
    private Long logSize;

    /** 终端序列号 */
    @Schema(description = "终端序列号")
    @Excel(name = "终端序列号")
    private String sn;

    /** 终端id */
    @Schema(description = "终端id")
    @Excel(name = "终端id")
    private Long terminalId;

    /** 日志文件路劲 */
    @Schema(description = "日志文件路劲")
    @Excel(name = "日志文件路劲")
    private String logFilePath;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setLogFileName(String logFileName) 
    {
        this.logFileName = logFileName;
    }

    public String getLogFileName() 
    {
        return logFileName;
    }
    public void setLogSize(Long logSize)
    {
        this.logSize = logSize;
    }

    public Long getLogSize()
    {
        return logSize;
    }
    public void setSn(String sn) 
    {
        this.sn = sn;
    }

    public String getSn() 
    {
        return sn;
    }
    public void setTerminalId(Long terminalId) 
    {
        this.terminalId = terminalId;
    }

    public Long getTerminalId() 
    {
        return terminalId;
    }
    public void setLogFilePath(String logFilePath) 
    {
        this.logFilePath = logFilePath;
    }

    public String getLogFilePath() 
    {
        return logFilePath;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("logFileName", getLogFileName())
            .append("logSize", getLogSize())
            .append("sn", getSn())
            .append("terminalId", getTerminalId())
            .append("logFilePath", getLogFilePath())
            .toString();
    }
}
