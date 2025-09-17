/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : LayoutTemplateInvoker.java
 * Package     : com.paradisecloud.fcm.fme.cache.model.invoker
 * @author sinhy 
 * @since 2021-09-14 14:23
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.cache.model.invoker;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.fme.model.response.layout.LayoutTemplatesResponse;
import com.sinhy.http.HttpRequester;

public class LayoutTemplateInvoker extends FmeApiInvoker
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-14 14:23 
     * @param httpRequester
     * @param rootUrl 
     */
    public LayoutTemplateInvoker(HttpRequester httpRequester, String rootUrl)
    {
        super(httpRequester, rootUrl);
    }
    
    public LayoutTemplatesResponse getLayoutTemplates(int offset)
    {
        return getEntity("layoutTemplates?offset=" + offset, LayoutTemplatesResponse.class);
    }
    
    public JSONObject getLayoutTemplate(String id)
    {
        return getJSONObject("layoutTemplates/" + id + "/template");
    }
}
