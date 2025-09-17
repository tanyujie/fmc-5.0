package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 终端动作对象 busi_terminal_action
 * 
 * @author zyz
 * @date 2021-07-31
 */
@Schema(description = "终端动作对象")
public class BusiTerminalAction extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    @Schema(description = "主键id")
    private Long id;

    /** 终端id */
    @Schema(description = "终端id")
    @Excel(name = "终端id")
    private Long terminalId;

    /** 终端序列号sn */
    @Schema(description = "终端序列号sn")
    @Excel(name = "终端序列号sn")
    private String terminalSn;

    /** 会议号 */
    @Schema(description = "会议号")
    @Excel(name = "会议号")
    private Long conferenceNum;

    /** 终端所属部门id */
    @Schema(description = "终端所属部门id")
    @Excel(name = "终端所属部门id")
    private Long deptId;

    /** 设备的ip地址 */
    @Schema(description = "设备的ip地址")
    @Excel(name = "设备的ip地址")
    private String ip;

    /** 终端名字 */
    @Schema(description = "终端名字")
    @Excel(name = "终端名字")
    private String terminalName;

    /** 终端动作，枚举值int类型 */
    @Schema(description = "终端动作，枚举值int类型")
    @Excel(name = "终端动作，枚举值int类型")
    private Integer actionType;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setTerminalId(Long terminalId) 
    {
        this.terminalId = terminalId;
    }

    public Long getTerminalId() 
    {
        return terminalId;
    }
    public void setTerminalSn(String terminalSn) 
    {
        this.terminalSn = terminalSn;
    }

    public String getTerminalSn() 
    {
        return terminalSn;
    }
    public void setConferenceNum(Long conferenceNum) 
    {
        this.conferenceNum = conferenceNum;
    }

    public Long getConferenceNum() 
    {
        return conferenceNum;
    }
    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }
    public void setIp(String ip) 
    {
        this.ip = ip;
    }

    public String getIp() 
    {
        return ip;
    }
    public void setTerminalName(String terminalName) 
    {
        this.terminalName = terminalName;
    }

    public String getTerminalName() 
    {
        return terminalName;
    }
    public void setActionType(Integer actionType) 
    {
        this.actionType = actionType;
    }

    public Integer getActionType() 
    {
        return actionType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("terminalId", getTerminalId())
            .append("terminalSn", getTerminalSn())
            .append("conferenceNum", getConferenceNum())
            .append("deptId", getDeptId())
            .append("ip", getIp())
            .append("terminalName", getTerminalName())
            .append("actionType", getActionType())
            .toString();
    }
}
