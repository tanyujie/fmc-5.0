package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 【请填写功能名称】对象 busi_smc2_appointment_conference_paticipant
 * 
 * @author lilinhai
 * @date 2023-05-04
 */
@Schema(description = "【请填写功能名称】")
public class BusiSmc2AppointmentConferencePaticipant extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Integer id;

    /** 会议ID */
    @Schema(description = "会议ID")
    @Excel(name = "会议ID")
    private String conferenceId;

    /** 预约ID */
    @Schema(description = "预约ID")
    @Excel(name = "预约ID")
    private Integer appointmentId;

    /** 终端id */
    @Schema(description = "终端id")
    @Excel(name = "终端id")
    private Integer terminalId;

    /** 排序 */
    @Schema(description = "排序")
    @Excel(name = "排序")
    private Integer weight;

    /** uri */
    @Schema(description = "uri")
    @Excel(name = "uri")
    private String smcnumber;

    /** 终端名字 */
    @Schema(description = "终端名字")
    @Excel(name = "终端名字")
    private String terminalName;

    /** 终端部门id */
    @Schema(description = "终端部门id")
    @Excel(name = "终端部门id")
    private Integer terminalDeptId;

    private String participantId;
    private Integer attendType;

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public Integer getAttendType() {
        return attendType;
    }

    public void setAttendType(Integer attendType) {
        this.attendType = attendType;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getId() 
    {
        return id;
    }
    public void setConferenceId(String conferenceId) 
    {
        this.conferenceId = conferenceId;
    }

    public String getConferenceId() 
    {
        return conferenceId;
    }
    public void setAppointmentId(Integer appointmentId) 
    {
        this.appointmentId = appointmentId;
    }

    public Integer getAppointmentId() 
    {
        return appointmentId;
    }
    public void setTerminalId(Integer terminalId) 
    {
        this.terminalId = terminalId;
    }

    public Integer getTerminalId() 
    {
        return terminalId;
    }
    public void setWeight(Integer weight) 
    {
        this.weight = weight;
    }

    public Integer getWeight() 
    {
        return weight;
    }
    public void setSmcnumber(String smcnumber) 
    {
        this.smcnumber = smcnumber;
    }

    public String getSmcnumber() 
    {
        return smcnumber;
    }
    public void setTerminalName(String terminalName) 
    {
        this.terminalName = terminalName;
    }

    public String getTerminalName() 
    {
        return terminalName;
    }
    public void setTerminalDeptId(Integer terminalDeptId) 
    {
        this.terminalDeptId = terminalDeptId;
    }

    public Integer getTerminalDeptId() 
    {
        return terminalDeptId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("conferenceId", getConferenceId())
            .append("appointmentId", getAppointmentId())
            .append("terminalId", getTerminalId())
            .append("weight", getWeight())
            .append("smcnumber", getSmcnumber())
            .append("terminalName", getTerminalName())
            .append("terminalDeptId", getTerminalDeptId())
            .toString();
    }
}
