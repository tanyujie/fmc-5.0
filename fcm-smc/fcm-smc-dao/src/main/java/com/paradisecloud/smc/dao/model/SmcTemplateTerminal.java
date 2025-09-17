package com.paradisecloud.smc.dao.model;

import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author nj
 * @date 2022/9/19 10:27
 */
@Schema(description = "【请填写功能名称】")
public class SmcTemplateTerminal  extends BaseEntity {

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 终端id */
    @Schema(description = "终端id")
    @Excel(name = "终端id")
    private Long terminalId;

    /** 终端部门id */
    @Schema(description = "终端部门id")
    @Excel(name = "终端部门id")
    private Long terminalDeptId;

    private int weight;

    /** smc模板 */
    @Schema(description = "smc模板")
    @Excel(name = "smc模板")
    private String  smcTemplateId;

    /** number */
    @Schema(description = "number")
    @Excel(name = "number")
    private String  smcnumber;


    private long deptId;

    public long getDeptId() {
        return deptId;
    }

    public void setDeptId(long deptId) {
        this.deptId = deptId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getSmcnumber() {
        return smcnumber;
    }

    public void setSmcnumber(String smcnumber) {
        this.smcnumber = smcnumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(Long terminalId) {
        this.terminalId = terminalId;
    }

    public String getSmcTemplateId() {
        return smcTemplateId;
    }

    public void setSmcTemplateId(String smcTemplateId) {
        this.smcTemplateId = smcTemplateId;
    }

    public Long getTerminalDeptId() {
        return terminalDeptId;
    }

    public void setTerminalDeptId(Long terminalDeptId) {
        this.terminalDeptId = terminalDeptId;
    }

    @Override
    public String toString() {
        return "SmcTemplateTerminal{" +
                "id=" + id +
                ", terminalId=" + terminalId +
                ", terminalDeptId=" + terminalDeptId +
                ", smcTemplateId='" + smcTemplateId + '\'' +
                ", smcnumber='" + smcnumber + '\'' +
                '}';
    }
}
