/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : TerminalAttendee.java
 * Package     : com.paradisecloud.fcm.fme.model.busi
 * @author lilinhai 
 * @since 2021-02-02 14:54
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.tencent.busi.attende;

/**
 * <pre>FME自身充当的参会者</pre>
 * @author lilinhai
 * @since 2021-02-02 14:54
 * @version V1.0  
 */
public class McuAttendeeTencent extends AttendeeTencent
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
     * FME级联的部门
     */
    private long cascadeDeptId;
    
    /**
     * MCU自身ID
     */
    private Long mcuId;
    
    /**
     * <p>Get Method   :   fmeId Long</p>
     * @return fmeId
     */
    public Long getMcuId()
    {
        return mcuId;
    }




    /**
     * 级联的部门名
     */
    private String cascadeDeptName;

    /**
     * MCU自身ID
     */
    private Long cascadeMcuId;

    public String getCascadeDeptName() {
        return cascadeDeptName;
    }

    public void setCascadeDeptName(String cascadeDeptName) {
        this.cascadeDeptName = cascadeDeptName;
    }

    public Long getCascadeMcuId() {
        return cascadeMcuId;
    }

    public void setCascadeMcuId(Long cascadeMcuId) {
        this.cascadeMcuId = cascadeMcuId;
    }

    /**
     * <p>Set Method   :   fmeId Long</p>
     * @param mcuId
     */
    public void setMcuId(Long mcuId)
    {
        this.mcuId = mcuId;
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

    @Override
    public boolean isMcuAttendee() {
        return true;
    }
}
