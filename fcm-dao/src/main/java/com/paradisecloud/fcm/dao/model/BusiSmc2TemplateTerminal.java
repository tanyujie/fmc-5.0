package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;


/**
 * 【请填写功能名称】对象 busi_smc2_template_terminal
 * 
 * @author lilinhai
 * @date 2023-04-20
 */
public class BusiSmc2TemplateTerminal extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Integer id;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Integer smc2TemplateId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Integer terminalId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Integer terminalDeptId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String smcnumber;

    /** 排序 */
    @Excel(name = "排序")
    private Integer weight;

    /** 与会者id */
    @Excel(name = "与会者id")
    private String  participantId;

    /**
     * 参会类型：1入会，2直播
     */
    private Integer attendType;

    private Integer deptId;

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public Integer getAttendType() {
        return attendType;
    }

    public void setAttendType(Integer attendType) {
        this.attendType = attendType;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getId() 
    {
        return id;
    }
    public void setSmc2TemplateId(Integer smc2TemplateId) 
    {
        this.smc2TemplateId = smc2TemplateId;
    }

    public Integer getSmc2TemplateId() 
    {
        return smc2TemplateId;
    }
    public void setTerminalId(Integer terminalId) 
    {
        this.terminalId = terminalId;
    }

    public Integer getTerminalId() 
    {
        return terminalId;
    }
    public void setTerminalDeptId(Integer terminalDeptId) 
    {
        this.terminalDeptId = terminalDeptId;
    }

    public Integer getTerminalDeptId() 
    {
        return terminalDeptId;
    }
    public void setSmcnumber(String smcnumber) 
    {
        this.smcnumber = smcnumber;
    }

    public String getSmcnumber() 
    {
        return smcnumber;
    }
    public void setWeight(Integer weight) 
    {
        this.weight = weight;
    }

    public Integer getWeight() 
    {
        return weight;
    }

    @Override
    public String toString() {
        return "BusiSmc2TemplateTerminal{" +
                "id=" + id +
                ", smc2TemplateId=" + smc2TemplateId +
                ", terminalId=" + terminalId +
                ", terminalDeptId=" + terminalDeptId +
                ", smcnumber='" + smcnumber + '\'' +
                ", weight=" + weight +
                ", participantId='" + participantId + '\'' +
                ", attendType=" + attendType +
                '}';
    }
}
