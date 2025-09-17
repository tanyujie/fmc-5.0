/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : SystemInvoker.java
 * Package     : com.paradisecloud.fcm.fme.cache.model.invoker
 * @author sinhy 
 * @since 2021-07-23 11:46
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.cache.model.invoker;

import com.paradisecloud.fcm.fme.model.response.system.ConfigurationClusterResponse;
import com.paradisecloud.fcm.fme.model.response.system.SystemStatusResponse;
import com.sinhy.http.HttpRequester;

/**  
 * <pre>FME系统信息调用器</pre>
 * @author sinhy
 * @since 2021-07-23 11:46
 * @version V1.0  
 */
public class SystemInvoker extends FmeApiInvoker
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-07-23 11:46 
     * @param httpRequester
     * @param rootUrl 
     */
    public SystemInvoker(HttpRequester httpRequester, String rootUrl)
    {
        super(httpRequester, rootUrl);
    }
    
    public SystemStatusResponse getSystemStatus()
    {
        return getEntity("system/status", SystemStatusResponse.class);
    }
    
    public ConfigurationClusterResponse getConfigurationCluster()
    {
        return getEntity("system/configuration/cluster", ConfigurationClusterResponse.class);
    }
}
