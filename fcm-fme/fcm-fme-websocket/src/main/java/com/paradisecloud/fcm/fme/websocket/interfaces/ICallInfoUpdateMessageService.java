/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ICallListUpdateMessageService.java
 * Package     : com.paradisecloud.sync.service.interfaces
 * @author lilinhai 
 * @since 2020-12-10 15:47
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.websocket.interfaces;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.websocket.callinfo.CallInfoUpdateMessage;

/**  
 * <pre>CallInfoUpdateMessage业务处理类</pre>
 * @author lilinhai
 * @since 2020-12-10 15:47
 * @version V1.0  
 */
public interface ICallInfoUpdateMessageService
{
    
    /**
     * <pre>处理CallInfoUpdateMessage消息</pre>
     * @author lilinhai
     * @since 2020-12-14 13:45 
     * @param callListUpdateMessage
     * @param apiUtilNew void
     */
    void process(CallInfoUpdateMessage callInfoUpdateMessage, JSONObject messageObj, FmeBridge apiUtilNew);
}
