/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : TerminalInfo.java
 * Package     : com.paradisecloud.fcm.common.model.terminal
 * @author lilinhai 
 * @since 2021-03-02 13:05
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.cache.model.message;

import com.paradisecloud.fcm.fme.cache.model.FmeBridge;

/**  
 * <pre>FME桥消息</pre>
 * @author lilinhai
 * @since 2021-03-02 13:05
 * @version V1.0  
 */
public class FmeBridgeMessage
{
    
    protected FmeBridge fmeBridge;

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-03-02 16:38 
     * @param fmeBridge 
     */
    public FmeBridgeMessage(FmeBridge fmeBridge)
    {
        super();
        this.fmeBridge = fmeBridge;
    }

    /**
     * <p>Get Method   :   fmeBridge FmeBridge</p>
     * @return fmeBridge
     */
    public FmeBridge getFmeBridge()
    {
        return fmeBridge;
    }

    @Override
    public String toString()
    {
        return "FmeBridgeMessage [fmeBridge=" + fmeBridge + "]";
    }
}
