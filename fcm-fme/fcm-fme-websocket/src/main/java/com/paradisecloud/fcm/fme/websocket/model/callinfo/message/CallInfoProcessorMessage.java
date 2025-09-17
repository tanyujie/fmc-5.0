/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : RosterUpdateProcessorMessage.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.core.roster
 * @author sinhy 
 * @since 2021-09-07 23:35
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.websocket.model.callinfo.message;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.websocket.model.processormessage.BusiProcessorMessage;

public abstract class CallInfoProcessorMessage extends BusiProcessorMessage
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-07 23:38 
     * @param updateItem
     * @param fmeBridge 
     */
    protected CallInfoProcessorMessage(FmeBridge fmeBridge, JSONObject updateItem)
    {
        super(fmeBridge, updateItem, updateItem.getString("call"));
    }
}
