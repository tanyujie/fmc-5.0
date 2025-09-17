/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : GetLayoutTemplates.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.conference
 * @author sinhy 
 * @since 2021-09-18 11:27
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.model.pm.conference;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.sinhy.proxy.ProxyMethod;

public class GetLayoutTemplates extends ProxyMethod
{
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-18 11:27 
     * @param method 
     */
    public GetLayoutTemplates(Method method)
    {
        super(method);
    }
    
    public Set<String> getLayoutTemplates(Long deptId)
    {
        List<FmeBridge> fbs = FmeBridgeCache.getInstance().getFmeBridgesByDept(deptId);
        return fbs.get(0).getDataCache().getSplitScreenCreaterMap().keySet();
    }
}
