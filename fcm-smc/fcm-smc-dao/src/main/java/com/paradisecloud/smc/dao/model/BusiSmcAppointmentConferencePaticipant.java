package com.paradisecloud.smc.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 【请填写功能名称】对象 busi_smc_appointment_conference_paticipant
 * 
 * @author lilinhai
 * @date 2023-03-16
 */
@Schema(description = "【请填写功能名称】")
public class BusiSmcAppointmentConferencePaticipant extends BaseEntity
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
    private Long terminalId;

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

    private long terminalDeptId;


    private long deptId;

    public long getDeptId() {
        return deptId;
    }

    public void setDeptId(long deptId) {
        this.deptId = deptId;
    }

    public long getTerminalDeptId() {
        return terminalDeptId;
    }

    public void setTerminalDeptId(long terminalDeptId) {
        this.terminalDeptId = terminalDeptId;
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
    public void setTerminalId(Long terminalId)
    {
        this.terminalId = terminalId;
    }

    public Long getTerminalId()
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

    @Override
    public String toString() {
        return "BusiSmcAppointmentConferencePaticipant{" +
                "id=" + id +
                ", conferenceId='" + conferenceId + '\'' +
                ", appointmentId=" + appointmentId +
                ", terminalId=" + terminalId +
                ", weight=" + weight +
                ", smcnumber='" + smcnumber + '\'' +
                ", terminalName='" + terminalName + '\'' +
                ", terminalDeptId=" + terminalDeptId +
                '}';
    }
}
