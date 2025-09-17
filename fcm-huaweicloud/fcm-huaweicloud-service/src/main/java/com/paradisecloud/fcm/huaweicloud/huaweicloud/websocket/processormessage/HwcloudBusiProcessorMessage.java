/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : BusiProcessorMessgae.java
 * Package     : com.paradisecloud.fcm.fme.websocket.model.processormessage
 * @author sinhy 
 * @since 2021-09-15 10:40
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.processormessage;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudBridge;
import com.sinhy.core.processormessage.ProcessorMessage;

public abstract class HwcloudBusiProcessorMessage extends ProcessorMessage<JSONObject>
{

    protected HwcloudBridge hwcloudBridge;
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-12 15:50 
     * @param hwcloudBridge
     * @param updateItem
     * @param itemId 
     */
    protected HwcloudBusiProcessorMessage(HwcloudBridge hwcloudBridge, JSONObject updateItem, String itemId)
    {
        super(updateItem, itemId);
        this.hwcloudBridge = hwcloudBridge;
    }
}
