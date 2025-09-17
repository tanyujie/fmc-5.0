/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : TerminalAttendee.java
 * Package     : com.paradisecloud.fcm.fme.model.busi
 * @author lilinhai 
 * @since 2021-02-02 14:54
 * @version  V1.0
 */ 
package com.paradisecloud.smc3.busi.attende;

/**
 * <pre>FME自身充当的参会者</pre>
 * @author lilinhai
 * @since 2021-02-02 14:54
 * @version V1.0  
 */
public class McuAttendeeSmc3 extends AttendeeSmc3
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-02 14:55
     */
    private static final long serialVersionUID = 1L;

    /**
     * 级联子会议的会议号
     */
    private String cascadeConferenceNumber;

    /**
     * 级联的部门
     */
    private long cascadeDeptId;

    /**
     * 级联的部门名
     */
    private String cascadeDeptName;

    /**
     * MCU自身ID
     */
    private Long cascadeMcuId;

    /**
     * <p>Get Method   :   cascadeMcuId Long</p>
     * @return cascadeMcuId
     */
    public Long getCascadeMcuId()
    {
        return cascadeMcuId;
    }

    /**
     * <p>Set Method   :   cascadeMcuId Long</p>
     * @param cascadeMcuId
     */
    public void setCascadeMcuId(Long cascadeMcuId)
    {
        this.cascadeMcuId = cascadeMcuId;
    }

    /**
     * <p>Get Method   :   conferenceNumber Long</p>
     * @return conferenceNumber
     */
    public String getCascadeConferenceNumber()
    {
        return cascadeConferenceNumber;
    }

    /**
     * <p>Set Method   :   conferenceNumber Long</p>
     * @param conferenceNumber
     */
    public void setCascadeConferenceNumber(String conferenceNumber)
    {
        this.cascadeConferenceNumber = conferenceNumber;
        if (getIp() != null)
        {
            setRemoteParty(conferenceNumber + "@" + getIp());
        }
    }

    /**
     * <p>Get Method   :   cascadeDeptId long</p>
     * @return cascadeDeptId
     */
    public long getCascadeDeptId()
    {
        return cascadeDeptId;
    }

    /**
     * <p>Set Method   :   cascadeDeptId long</p>
     * @param cascadeDeptId
     */
    public void setCascadeDeptId(long cascadeDeptId)
    {
        this.cascadeDeptId = cascadeDeptId;
    }

    public String getCascadeDeptName() {
        return cascadeDeptName;
    }

    public void setCascadeDeptName(String cascadeDeptName) {
        this.cascadeDeptName = cascadeDeptName;
    }

    /**
     * 会议id
     */
    private String cascadeConferenceId;

    private String cascadeMcuType;

    private Long cascadeTemplateId;

    private String cascadeAttendeId;
    /**
     * 上级会议id
     */
    private String upCascadeConferenceId;

    public String getCascadeConferenceId() {
        return cascadeConferenceId;
    }

    public void setCascadeConferenceId(String cascadeConferenceId) {
        this.cascadeConferenceId = cascadeConferenceId;
    }

    public String getCascadeMcuType() {
        return cascadeMcuType;
    }

    public void setCascadeMcuType(String cascadeMcuType) {
        this.cascadeMcuType = cascadeMcuType;
    }

    public Long getCascadeTemplateId() {
        return cascadeTemplateId;
    }

    public void setCascadeTemplateId(Long cascadeTemplateId) {
        this.cascadeTemplateId = cascadeTemplateId;
        if (getIp() != null && getIp().length() > 0) {
            setRemoteParty(cascadeTemplateId + "@" + getIp());
        }
    }

    public String getUpCascadeConferenceId() {
        return upCascadeConferenceId;
    }

    public void setUpCascadeConferenceId(String upCascadeConferenceId) {
        this.upCascadeConferenceId = upCascadeConferenceId;
    }

    public boolean isMcuAttendee() {
        return true;
    }

    public String getCascadeAttendeId() {
        return cascadeAttendeId;
    }

    public void setCascadeAttendeId(String cascadeAttendeId) {
        this.cascadeAttendeId = cascadeAttendeId;
    }
}
