/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : TerminalCache.java
 * Package     : com.paradisecloud.fcm.terminal
 * @author lilinhai 
 * @since 2021-01-22 18:06
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.tencent.cache;


import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentCluster;

/**
 * <pre>FME集群缓存</pre>
 * @author lilinhai
 * @since 2021-01-22 18:06
 * @version V1.0  
 */
public class TencentClusterCache extends JavaCache<Long, BusiMcuTencentCluster>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-01-28 13:36
     */
    private static final long serialVersionUID = 1L;
    private static final TencentClusterCache INSTANCE = new TencentClusterCache();

    /**
     * <pre>构造方法</pre>
     * @author lilinhai
     * @since 2021-01-22 18:07
     */
    private TencentClusterCache()
    {
    }
    
    public static TencentClusterCache getInstance()
    {
        return INSTANCE;
    }
}
