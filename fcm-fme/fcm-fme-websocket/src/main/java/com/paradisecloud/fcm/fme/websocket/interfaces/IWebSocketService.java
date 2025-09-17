/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : IMcuWebSocketMessageService.java
 * Package     : com.paradisecloud.sync.service.interfaces
 * @author lilinhai 
 * @since 2020-12-11 13:53
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.websocket.interfaces;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.websocket.callinfo.CallInfoUpdateMessage;
import com.paradisecloud.fcm.fme.model.websocket.calllist.CallListUpdateMessage;
import com.paradisecloud.fcm.fme.model.websocket.roster.RosterUpdateMessage;

/**  
 * <pre>websocket消息业务处理类</pre>
 * @author lilinhai
 * @since 2020-12-11 13:53
 * @version V1.0  
 */
public interface IWebSocketService
{
    
    /**
     * <pre>同步全量数据</pre>
     * @author lilinhai
     * @since 2020-12-11 14:20 
     * @param fmeBridge void
     */
    void syncAllData(FmeBridge fmeBridge);
    
    /**
     * <pre>处理CallListUpdateMessage消息</pre>
     * @author lilinhai
     * @since 2020-12-10 15:48 
     * @param callListUpdateMessage void
     * @param fmeBridge 
     */
    void process(CallListUpdateMessage callListUpdateMessage, JSONObject json, FmeBridge fmeBridge);
    
    /**
     * <pre>处理CallInfoUpdateMessage</pre>
     * @author lilinhai
     * @since 2020-12-14 13:50 
     * @param callInfoUpdateMessage
     * @param fmeBridge void
     */
    void process(CallInfoUpdateMessage callInfoUpdateMessage, JSONObject messageObj, FmeBridge fmeBridge);

    /**
     * <pre>处理与会者</pre>
     * @author lilinhai
     * @since 2020-12-14 14:47 
     * @param rosterUpdateMessage
     * @param json 
     * @param fmeBridge void
     */
    void process(RosterUpdateMessage rosterUpdateMessage, JSONObject json, FmeBridge fmeBridge);
}
