/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeInvoker.java
 * Package     : com.paradisecloud.fcm.fme.cache.model.fmeinvoker
 * @author lilinhai 
 * @since 2021-01-28 18:42
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.terminal.fsbc.invoker;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;

import com.paradisecloud.fcm.terminal.fsbc.model.FsbcLogger;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessorAdapter;

/**  
 * <pre>FSBC调用器</pre>
 * @author lilinhai
 * @since 2021-01-28 18:42
 * @version V1.0  
 */
public abstract class FsbcApiInvoker
{
    
    /**
     * HTTP客户端请求器
     */
    protected HttpRequester httpRequester;
    
    /**
     * 请求根URL
     */
    protected String rootUrl;
    
    protected FsbcLogger fsbcLogger;

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-01-28 18:46 
     * @param httpRequester
     * @param rootUrl 
     */
    protected FsbcApiInvoker(HttpRequester httpRequester, String rootUrl, FsbcLogger fsbcLogger)
    {
        super();
        this.httpRequester = httpRequester;
        this.rootUrl = rootUrl;
        this.fsbcLogger = fsbcLogger;
    }

    /**
     * 执行get操作命令的API string
     *
     * @param api api接口
     * @return json 字符串
     */
    protected String getXmlBodyString(String api)
    {
        StringBuilder jsonBuilder = new StringBuilder();
        httpRequester.get(rootUrl + api, new HttpResponseProcessorAdapter()
        {
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException
            {
                if (isXmlContentType(httpResponse))
                {
                    jsonBuilder.append(getBodyContent(httpResponse));
                }
            }

            @Override
            public void fail(HttpResponse httpResponse)
            {
                System.out.println();
            }
        });
        return jsonBuilder.toString();
    }
}
