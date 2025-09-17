/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : BusiProcessorMessgae.java
 * Package     : com.paradisecloud.fcm.fme.websocket.model.processormessage
 * @author sinhy 
 * @since 2021-09-15 10:40
 * @version  V1.0
 */ 
package com.paradisecloud.smc.processormessage;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.sinhy.core.processormessage.ProcessorMessage;

public abstract class BusiProcessorMessage extends ProcessorMessage<JSONObject>
{

    protected SmcBridge smcBridge;
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-12 15:50 
     * @param fmeBridge
     * @param updateItem
     * @param itemId 
     */
    protected BusiProcessorMessage(SmcBridge fmeBridge, JSONObject updateItem, String itemId)
    {
        super(updateItem, itemId);
        this.smcBridge = fmeBridge;
    }
}
