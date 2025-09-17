package com.paradisecloud.fcm.dao.model;


import com.paradisecloud.common.core.model.BaseEntity;

/**
 * 部门smc模板关联对象 busi_smc2_dept_template
 * 
 * @author lilinhai
 * @date 2023-04-19
 */
public class BusiSmc2DeptTemplate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Integer id;

    /** 部门id */
    private Integer deptId;

    /** 模板ID */
    private String smcTemplateId;

    /** 模板名称 */
    private String templateName;

    /** 会议时长分钟数 */
    private Integer duration;

    /** 会议类型 */
    private String type;

    /** 带宽 */
    private Integer rate;

    /** 直播 */
    private Integer supportLive;

    /** 录制 */
    private Integer supportRecord;

    /** 数据会议 */
    private Integer amcRecord;

    /** 自动静音 */
    private Integer autoMute;

    /** 主席密码 */
    private String chairmanPassword;

    /** 嘉宾密码 */
    private String guestPassword;

    /** 虚拟号码 */
    private String vmrNumber;

    /** 最大入会数 */
    private Integer maxParticipantNum;

    /** 主会场ID */
    private Integer masterTerminalId;

    /** 数据会议 */
    private Integer enableDataConf;

    /** 模板id */
    private Integer smc2TemplateId;

    /** 会议激活号码,Ad Hoc模板时必须填写 */
    private String accessCode;

    /** 计费码 */
    private String billCode;

    /** 会议密码 */
    private String password;

    /** 创建者 */
    private String creatUser;

    private String confid;

    public String getConfid() {
        return confid;
    }

    public void setConfid(String confid) {
        this.confid = confid;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getId() 
    {
        return id;
    }
    public void setDeptId(Integer deptId) 
    {
        this.deptId = deptId;
    }

    public Integer getDeptId() 
    {
        return deptId;
    }
    public void setSmcTemplateId(String smcTemplateId) 
    {
        this.smcTemplateId = smcTemplateId;
    }

    public String getSmcTemplateId() 
    {
        return smcTemplateId;
    }
    public void setTemplateName(String templateName) 
    {
        this.templateName = templateName;
    }

    public String getTemplateName() 
    {
        return templateName;
    }
    public void setDuration(Integer duration) 
    {
        this.duration = duration;
    }

    public Integer getDuration() 
    {
        return duration;
    }
    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }
    public void setRate(Integer rate) 
    {
        this.rate = rate;
    }

    public Integer getRate() 
    {
        return rate;
    }
    public void setSupportLive(Integer supportLive) 
    {
        this.supportLive = supportLive;
    }

    public Integer getSupportLive() 
    {
        return supportLive;
    }
    public void setSupportRecord(Integer supportRecord) 
    {
        this.supportRecord = supportRecord;
    }

    public Integer getSupportRecord() 
    {
        return supportRecord;
    }
    public void setAmcRecord(Integer amcRecord) 
    {
        this.amcRecord = amcRecord;
    }

    public Integer getAmcRecord() 
    {
        return amcRecord;
    }
    public void setAutoMute(Integer autoMute) 
    {
        this.autoMute = autoMute;
    }

    public Integer getAutoMute() 
    {
        return autoMute;
    }
    public void setChairmanPassword(String chairmanPassword) 
    {
        this.chairmanPassword = chairmanPassword;
    }

    public String getChairmanPassword() 
    {
        return chairmanPassword;
    }
    public void setGuestPassword(String guestPassword) 
    {
        this.guestPassword = guestPassword;
    }

    public String getGuestPassword() 
    {
        return guestPassword;
    }
    public void setVmrNumber(String vmrNumber) 
    {
        this.vmrNumber = vmrNumber;
    }

    public String getVmrNumber() 
    {
        return vmrNumber;
    }
    public void setMaxParticipantNum(Integer maxParticipantNum) 
    {
        this.maxParticipantNum = maxParticipantNum;
    }

    public Integer getMaxParticipantNum() 
    {
        return maxParticipantNum;
    }
    public void setMasterTerminalId(Integer masterTerminalId) 
    {
        this.masterTerminalId = masterTerminalId;
    }

    public Integer getMasterTerminalId() 
    {
        return masterTerminalId;
    }
    public void setEnableDataConf(Integer enableDataConf) 
    {
        this.enableDataConf = enableDataConf;
    }

    public Integer getEnableDataConf() 
    {
        return enableDataConf;
    }
    public void setSmc2TemplateId(Integer smc2TemplateId) 
    {
        this.smc2TemplateId = smc2TemplateId;
    }

    public Integer getSmc2TemplateId() 
    {
        return smc2TemplateId;
    }
    public void setAccessCode(String accessCode) 
    {
        this.accessCode = accessCode;
    }

    public String getAccessCode() 
    {
        return accessCode;
    }
    public void setBillCode(String billCode) 
    {
        this.billCode = billCode;
    }

    public String getBillCode() 
    {
        return billCode;
    }
    public void setPassword(String password) 
    {
        this.password = password;
    }

    public String getPassword() 
    {
        return password;
    }
    public void setCreatUser(String creatUser) 
    {
        this.creatUser = creatUser;
    }

    public String getCreatUser() 
    {
        return creatUser;
    }

    @Override
    public String toString() {
        return "BusiSmc2DeptTemplate{" +
                "id=" + id +
                ", deptId=" + deptId +
                ", smcTemplateId='" + smcTemplateId + '\'' +
                ", templateName='" + templateName + '\'' +
                ", duration=" + duration +
                ", type='" + type + '\'' +
                ", rate=" + rate +
                ", supportLive=" + supportLive +
                ", supportRecord=" + supportRecord +
                ", amcRecord=" + amcRecord +
                ", autoMute=" + autoMute +
                ", chairmanPassword='" + chairmanPassword + '\'' +
                ", guestPassword='" + guestPassword + '\'' +
                ", vmrNumber='" + vmrNumber + '\'' +
                ", maxParticipantNum=" + maxParticipantNum +
                ", masterTerminalId=" + masterTerminalId +
                ", enableDataConf=" + enableDataConf +
                ", smc2TemplateId=" + smc2TemplateId +
                ", accessCode='" + accessCode + '\'' +
                ", billCode='" + billCode + '\'' +
                ", password='" + password + '\'' +
                ", creatUser='" + creatUser + '\'' +
                '}';
    }
}
