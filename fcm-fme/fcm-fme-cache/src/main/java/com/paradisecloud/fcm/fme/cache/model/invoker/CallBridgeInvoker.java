/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CallBridgeInvoker.java
 * Package     : com.paradisecloud.fcm.fme.cache.model.invoker
 * @author sinhy 
 * @since 2021-09-01 20:12
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.cache.model.invoker;

import com.paradisecloud.fcm.fme.model.response.callbridge.CallBridgesResponse;
import com.sinhy.http.HttpRequester;

/**  
 * <pre>CallBridge调用器</pre>
 * @author sinhy
 * @since 2021-09-01 20:12
 * @version V1.0  
 */
public class CallBridgeInvoker extends FmeApiInvoker
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-01 20:12 
     * @param httpRequester
     * @param rootUrl 
     */
    public CallBridgeInvoker(HttpRequester httpRequester, String rootUrl)
    {
        super(httpRequester, rootUrl);
    }
    
    /**
     * <pre>分页获取CallBridge</pre>
     * @author lilinhai
     * @since 2021-01-26 15:31 
     * @param offset
     * @return CallBridgesResponse
     */
    public CallBridgesResponse getCallBridges(int offset)
    {
        return getEntity("callBridges?offset=" + offset, CallBridgesResponse.class);
    }
    
}
