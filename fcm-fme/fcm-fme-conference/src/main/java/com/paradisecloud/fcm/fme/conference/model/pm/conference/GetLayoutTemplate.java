/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : GetLayoutTemplate.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.conference
 * @author sinhy 
 * @since 2021-09-18 11:28
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.model.pm.conference;

import java.lang.reflect.Method;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.sinhy.proxy.ProxyMethod;

public class GetLayoutTemplate extends ProxyMethod
{
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-18 11:28 
     * @param method 
     */
    public GetLayoutTemplate(Method method)
    {
        super(method);
    }
    
    public JSONObject getLayoutTemplate(Long deptId, String name)
    {
        List<FmeBridge> fbs = FmeBridgeCache.getInstance().getFmeBridgesByDept(deptId);
        return fbs.get(0).getDataCache().getSplitScreenCreaterMap().get(name).getLayoutTemplate();
    }
}
