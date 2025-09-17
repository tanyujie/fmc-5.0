/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : TerminalInfo.java
 * Package     : com.paradisecloud.fcm.common.model.terminal
 * @author lilinhai 
 * @since 2021-03-02 13:05
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.common.message.terminal;

import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;

/**  
 * <pre>终端事件消息</pre>
 * @author lilinhai
 * @since 2021-03-02 13:05
 * @version V1.0  
 */
public class TerminalOnlineStatusMessage
{
    
    private long terminalId;
    
    /**
     * 终端在线状态枚举
     */
    private TerminalOnlineStatus onlineStatus;

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-03-02 13:10 
     * @param terminalId
     * @param onlineStatus 
     */
    public TerminalOnlineStatusMessage(long terminalId, int onlineStatus)
    {
        this(terminalId, TerminalOnlineStatus.convert(onlineStatus));
    }
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-03-02 13:10 
     * @param terminalId
     * @param onlineStatus 
     */
    public TerminalOnlineStatusMessage(long terminalId, TerminalOnlineStatus onlineStatus)
    {
        super();
        this.terminalId = terminalId;
        this.onlineStatus = onlineStatus;
    }

    /**
     * <p>Get Method   :   terminalId long</p>
     * @return terminalId
     */
    public long getTerminalId()
    {
        return terminalId;
    }

    /**
     * <p>Get Method   :   onlineStatus TerminalOnlineStatus</p>
     * @return onlineStatus
     */
    public TerminalOnlineStatus getOnlineStatus()
    {
        return onlineStatus;
    }

    @Override
    public String toString()
    {
        return "TerminalOnlineStatusMessage [terminalId=" + terminalId + ", onlineStatus=" + onlineStatus + "]";
    }
}
