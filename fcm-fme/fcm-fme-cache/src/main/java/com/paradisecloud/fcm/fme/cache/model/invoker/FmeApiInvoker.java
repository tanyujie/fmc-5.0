/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeInvoker.java
 * Package     : com.paradisecloud.fcm.fme.cache.model.fmeinvoker
 * @author lilinhai 
 * @since 2021-01-28 18:42
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.cache.model.invoker;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.json.XML;
import org.springframework.util.ObjectUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.fme.model.response.failure.FailureDetailsInfo;
import com.sinhy.exception.SystemException;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.model.GenericValue;

/**  
 * <pre>FME调用器</pre>
 * @author lilinhai
 * @since 2021-01-28 18:42
 * @version V1.0  
 */
public abstract class FmeApiInvoker
{
    
    protected HttpRequester httpRequester;
    protected String rootUrl;

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-01-28 18:46 
     * @param httpRequester
     * @param rootUrl 
     */
    protected FmeApiInvoker(HttpRequester httpRequester, String rootUrl)
    {
        super();
        this.httpRequester = httpRequester;
        this.rootUrl = rootUrl;
    }

    /**
     * <pre>获取http响应后转换的实体</pre>
     * @author Administrator
     * @since 2020-12-27 00:05 
     * @param <T>
     * @param api
     * @param type
     * @return T
     */
    protected <T> T getEntity(String api, Class<T> type)
    {
        String json = getXmlToJsonBodyString(api);
        if (!ObjectUtils.isEmpty(json))
        {
            return JSON.parseObject(json, type);
        }
        else
        {
            return null;
        }
    }


    protected <T> T getEntityABC(String api, Class<T> type)
    {
        String json = getXmlToJsonBodyString(api);
        if (!ObjectUtils.isEmpty(json))
        {
            return JSON.parseObject(json, type);
        }
        else
        {
            return null;
        }
    }
    
    protected JSONObject getJSONObject(String api)
    {
        String json = getJsonBodyString(api);
        if (!ObjectUtils.isEmpty(json))
        {
            return JSON.parseObject(json);
        }
        else
        {
            return null;
        }
    }
    
    protected String getJsonBodyString(String api)
    {
        StringBuilder jsonBuilder = new StringBuilder();
        GenericValue<FailureDetailsInfo> genericValue = new GenericValue<>();
        httpRequester.get(rootUrl + api, new HttpResponseProcessorAdapter()
        {
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException
            {
                if (isJsonContentType(httpResponse))
                {
                    jsonBuilder.append(getBodyContent(httpResponse));
                }
            }
            
            public void fail(HttpResponse httpResponse)
            {
                if (isXmlContentType(httpResponse))
                {
                    setErrorInfo(genericValue, getBodyContent(httpResponse));
                }
            }
        });
        
        if (genericValue.getValue() != null)
        {
            throw new SystemException(genericValue.getValue().toString());
        }
        
        return jsonBuilder.toString();
    }
    
    /**
     * 执行get操作命令的API string
     *
     * @param api api接口
     * @return json 字符串
     */
    protected String getXmlToJsonBodyString(String api)
    {
        StringBuilder jsonBuilder = new StringBuilder();
        GenericValue<FailureDetailsInfo> genericValue = new GenericValue<>();
        httpRequester.get(rootUrl + api, new HttpResponseProcessorAdapter()
        {
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException
            {
                if (isXmlContentType(httpResponse))
                {
                    jsonBuilder.append(XML.toJSONObject(getBodyContent(httpResponse)).toString());
                }
            }
            
            public void fail(HttpResponse httpResponse)
            {
                if (isXmlContentType(httpResponse))
                {
                    setErrorInfo(genericValue, getBodyContent(httpResponse));
                }
            }
        });
        
        if (genericValue.getValue() != null)
        {
            throw new SystemException(genericValue.getValue().toString());
        }
        
        return jsonBuilder.toString();
    }
    
    protected void setErrorInfo(GenericValue<FailureDetailsInfo> genericValue, String xmlContent)
    {
        genericValue.setValue(JSON.parseObject(XML.toJSONObject(xmlContent).toString().replaceAll("\" : null", "\" : \"\""), FailureDetailsInfo.class));
    }
    
}
