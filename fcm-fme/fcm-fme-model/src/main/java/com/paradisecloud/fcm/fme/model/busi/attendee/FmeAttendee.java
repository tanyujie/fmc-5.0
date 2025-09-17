/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : TerminalAttendee.java
 * Package     : com.paradisecloud.fcm.fme.model.busi
 * @author lilinhai 
 * @since 2021-02-02 14:54
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.model.busi.attendee;

/**  
 * <pre>FME自身充当的参会者</pre>
 * @author lilinhai
 * @since 2021-02-02 14:54
 * @version V1.0  
 */
public class FmeAttendee extends Attendee
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
     * fme自身ID
     */
    private Long fmeId;
    
    /**
     * <p>Get Method   :   fmeId Long</p>
     * @return fmeId
     */
    public Long getFmeId()
    {
        return fmeId;
    }

    /**
     * <p>Set Method   :   fmeId Long</p>
     * @param fmeId
     */
    public void setFmeId(Long fmeId)
    {
        this.fmeId = fmeId;
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
}
