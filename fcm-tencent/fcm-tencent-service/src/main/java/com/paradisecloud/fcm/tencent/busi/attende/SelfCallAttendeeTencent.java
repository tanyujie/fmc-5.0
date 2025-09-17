/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : OtherAttendee.java
 * Package     : com.paradisecloud.fcm.fme.model.busi
 * @author lilinhai 
 * @since 2021-02-02 17:47
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.tencent.busi.attende;

/**
 * <pre>主叫的与会者</pre>
 * @author lilinhai
 * @since 2021-02-02 17:47
 * @version V1.0  
 */
public class SelfCallAttendeeTencent extends AttendeeTencent
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-02 17:47 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 与会者关联终端id
     */
    private Long terminalId;
    /**
     * 终端类型
     */
    private int terminalType;

    /**
     * <p>Get Method   :   terminalId Long</p>
     * @return terminalId
     */
    @Override
    public Long getTerminalId()
    {
        return terminalId;
    }

    /**
     * <p>Set Method   :   terminalId Long</p>
     * @param terminalId
     */
    @Override
    public void setTerminalId(Long terminalId)
    {
        this.terminalId = terminalId;
    }

    public int getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(int terminalType) {
        this.terminalType = terminalType;
    }
}
