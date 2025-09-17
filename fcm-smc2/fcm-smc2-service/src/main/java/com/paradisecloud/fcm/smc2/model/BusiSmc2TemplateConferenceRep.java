package com.paradisecloud.fcm.smc2.model;

import com.paradisecloud.fcm.dao.model.BusiSmc2TemplateTerminal;

import java.util.List;

/**
 * @author nj
 * @date 2023/5/11 14:59
 */
public class BusiSmc2TemplateConferenceRep {

    private Integer id;

    /** 部门id */
    private Integer deptId;

    /** 模板ID */
    private String smcTemplateId;

    /** 模板名称 */
    private String templateName;

    /** 模板名称 */
    private String subject;

    /** 会议时长分钟数 */
    private Integer duration;

    /** 会议类型 */
    private String type;

    /** 带宽 */
    private Integer rate;

    /** 直播 */
    private Integer supportLive=2;

    /** 录制 */
    private Integer supportRecord=2;

    /** 数据会议 */
    private Integer amcRecord=2;

    /** 自动静音 */
    private Integer autoMute=2;

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
    private Integer enableDataConf=2;

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

    private List<BusiSmc2TemplateTerminal> templateTerminalList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public String getSmcTemplateId() {
        return smcTemplateId;
    }

    public void setSmcTemplateId(String smcTemplateId) {
        this.smcTemplateId = smcTemplateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Integer getSupportLive() {
        return supportLive;
    }

    public void setSupportLive(Integer supportLive) {
        this.supportLive = supportLive;
    }

    public Integer getSupportRecord() {
        return supportRecord;
    }

    public void setSupportRecord(Integer supportRecord) {
        this.supportRecord = supportRecord;
    }

    public Integer getAmcRecord() {
        return amcRecord;
    }

    public void setAmcRecord(Integer amcRecord) {
        this.amcRecord = amcRecord;
    }

    public Integer getAutoMute() {
        return autoMute;
    }

    public void setAutoMute(Integer autoMute) {
        this.autoMute = autoMute;
    }

    public String getChairmanPassword() {
        return chairmanPassword;
    }

    public void setChairmanPassword(String chairmanPassword) {
        this.chairmanPassword = chairmanPassword;
    }

    public String getGuestPassword() {
        return guestPassword;
    }

    public void setGuestPassword(String guestPassword) {
        this.guestPassword = guestPassword;
    }

    public String getVmrNumber() {
        return vmrNumber;
    }

    public void setVmrNumber(String vmrNumber) {
        this.vmrNumber = vmrNumber;
    }

    public Integer getMaxParticipantNum() {
        return maxParticipantNum;
    }

    public void setMaxParticipantNum(Integer maxParticipantNum) {
        this.maxParticipantNum = maxParticipantNum;
    }

    public Integer getMasterTerminalId() {
        return masterTerminalId;
    }

    public void setMasterTerminalId(Integer masterTerminalId) {
        this.masterTerminalId = masterTerminalId;
    }

    public Integer getEnableDataConf() {
        return enableDataConf;
    }

    public void setEnableDataConf(Integer enableDataConf) {
        this.enableDataConf = enableDataConf;
    }

    public Integer getSmc2TemplateId() {
        return smc2TemplateId;
    }

    public void setSmc2TemplateId(Integer smc2TemplateId) {
        this.smc2TemplateId = smc2TemplateId;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreatUser() {
        return creatUser;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setCreatUser(String creatUser) {
        this.creatUser = creatUser;
    }

    public List<BusiSmc2TemplateTerminal> getTemplateTerminalList() {
        return templateTerminalList;
    }

    public void setTemplateTerminalList(List<BusiSmc2TemplateTerminal> templateTerminalList) {
        this.templateTerminalList = templateTerminalList;
    }
}
