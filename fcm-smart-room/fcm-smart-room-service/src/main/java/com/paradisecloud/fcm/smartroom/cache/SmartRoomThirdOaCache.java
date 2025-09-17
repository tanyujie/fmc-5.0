/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : EduClassCache.java
 * Package     : com.paradisecloud.fcm.fme.cache.edu
 * @author sinhy 
 * @since 2021-10-19 18:06
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.smartroom.cache;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomThirdOa;

/**  
 * <pre>物联网关缓存</pre>
 * @author sinhy
 * @since 2021-10-19 18:06
 * @version V1.0  
 */
public class SmartRoomThirdOaCache extends JavaCache<Long, BusiSmartRoomThirdOa>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-10-19 18:06
     */
    private static final long serialVersionUID = 1L;
    private static final SmartRoomThirdOaCache INSTANCE = new SmartRoomThirdOaCache();

    /**
     * <pre>构造方法</pre>
     * @author sinhy
     * @since 2021-01-22 18:07
     */
    private SmartRoomThirdOaCache()
    {
    }
    
    public synchronized BusiSmartRoomThirdOa add(BusiSmartRoomThirdOa busiSmartRoomThirdOa) {
        return super.put(busiSmartRoomThirdOa.getId(), busiSmartRoomThirdOa);
    }
    
    public BusiSmartRoomThirdOa remove(Long id) {
        BusiSmartRoomThirdOa busiSmartRoomThirdOa = super.remove(id);
         return busiSmartRoomThirdOa;
    }

    public static SmartRoomThirdOaCache getInstance()
    {
        return INSTANCE;
    }
}
