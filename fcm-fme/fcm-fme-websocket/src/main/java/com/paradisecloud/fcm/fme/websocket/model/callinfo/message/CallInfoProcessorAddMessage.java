/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : RosterUpdateProcessorAddMessage.java
 * Package     : com.paradisecloud.fcm.fme.websocket.model.roster.message
 * @author sinhy 
 * @since 2021-09-07 23:45
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.websocket.model.callinfo.message;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.fme.attendee.interfaces.ICallService;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.sinhy.spring.BeanFactory;

public class CallInfoProcessorAddMessage extends CallInfoProcessorMessage
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-07 23:46 
     * @param fmeBridge
     * @param updateItem 
     */
    public CallInfoProcessorAddMessage(FmeBridge fmeBridge, JSONObject updateItem)
    {
        super(fmeBridge, updateItem);
    }

    @Override
    protected void process0()
    {
        try
        {
            fmeBridge.checkCallCount();
            BeanFactory.getBean(ICallService.class).syncCall(fmeBridge, itemId);
        }
        catch (Throwable e)
        {
            fmeBridge.getCallInvoker().deleteCall(itemId);
            logger.error("Add Call Failed: " + updateItem, e);
        }
    }
    
}
