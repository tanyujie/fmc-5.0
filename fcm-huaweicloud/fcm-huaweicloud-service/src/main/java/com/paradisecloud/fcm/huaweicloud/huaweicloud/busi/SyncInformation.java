/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : SynchronizationInformation.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.core
 * @author sinhy 
 * @since 2021-08-30 16:53
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.huaweicloud.huaweicloud.busi;

/**  
 * <pre>同步信息</pre>
 * @author sinhy
 * @since 2021-08-30 16:53
 * @version V1.0  
 */
public class SyncInformation
{
    
    /**
     * 待同步的call总数
     */
    private volatile int totalCallCount;
    
    /**
     * 已同步的call数量
     */
    private volatile int syncCallCount;
    
    /**
     * 待同步的当前call的总参会者数
     */
    private volatile int currentCallTotalParticipantCount;
    
    /**
     * 待同步的当前call的所属FME
     */
    private volatile String currentCallFmeIp;
    
    /**
     * 当前call已同步的参会数
     */
    private volatile int syncCurrentCallParticipantCount;
    
    /**
     * 是否在同步中
     */
    private volatile boolean inProgress;
    
    private String reason;

    /**
     * <p>Get Method   :   totalCallCount int</p>
     * @return totalCallCount
     */
    public int getTotalCallCount()
    {
        return totalCallCount;
    }

    /**
     * <p>Set Method   :   totalCallCount int</p>
     * @param totalCallCount
     */
    public void setTotalCallCount(int totalCallCount)
    {
        this.totalCallCount = totalCallCount;
    }

    /**
     * <p>Get Method   :   syncCallCount int</p>
     * @return syncCallCount
     */
    public int getSyncCallCount()
    {
        return syncCallCount;
    }

    /**
     * <p>Set Method   :   syncCallCount int</p>
     * @param syncCallCount
     */
    public void addSyncCallCount()
    {
        this.syncCallCount++;
    }

    /**
     * <p>Get Method   :   currentCallTotalParticipantCount int</p>
     * @return currentCallTotalParticipantCount
     */
    public int getCurrentCallTotalParticipantCount()
    {
        return currentCallTotalParticipantCount;
    }

    /**
     * <p>Set Method   :   currentCallTotalParticipantCount int</p>
     * @param currentCallTotalParticipantCount
     */
    public void setCurrentCallTotalParticipantCount(int currentCallTotalParticipantCount)
    {
        this.currentCallTotalParticipantCount = currentCallTotalParticipantCount;
    }

    /**
     * <p>Get Method   :   syncCurrentCallParticipantCount int</p>
     * @return syncCurrentCallParticipantCount
     */
    public int getSyncCurrentCallParticipantCount()
    {
        return syncCurrentCallParticipantCount;
    }

    /**
     * <p>Set Method   :   syncCurrentCallParticipantCount int</p>
     * @param syncCurrentCallParticipantCount
     */
    public void setSyncCurrentCallParticipantCount(int syncCurrentCallParticipantCount)
    {
        this.syncCurrentCallParticipantCount = syncCurrentCallParticipantCount;
    }
    
    /**
     * <p>Set Method   :   syncCurrentCallParticipantCount int</p>
     * @param syncCurrentCallParticipantCount
     */
    public void addSyncCurrentCallParticipantCount()
    {
        this.syncCurrentCallParticipantCount++;
    }

    /**
     * <p>Get Method   :   inProgress boolean</p>
     * @return inProgress
     */
    public boolean isInProgress()
    {
        return inProgress;
    }

    /**
     * <p>Set Method   :   inProgress boolean</p>
     * @param inProgress
     */
    public void setInProgress(boolean inProgress)
    {
        this.inProgress = inProgress;
    }

    /**
     * <p>Get Method   :   currentCallFmeIp int</p>
     * @return currentCallFmeIp
     */
    public String getCurrentCallFmeIp()
    {
        return currentCallFmeIp;
    }

    /**
     * <p>Set Method   :   currentCallFmeIp int</p>
     * @param currentCallFmeIp
     */
    public void setCurrentCallFmeIp(String currentCallFmeIp)
    {
        this.currentCallFmeIp = currentCallFmeIp;
    }

    /**
     * <p>Get Method   :   reason String</p>
     * @return reason
     */
    public String getReason()
    {
        return reason;
    }

    /**
     * <p>Set Method   :   reason String</p>
     * @param reason
     */
    public void setReason(String reason)
    {
        this.reason = reason;
    }
}
