/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CredentialInvoker.java
 * Package     : com.paradisecloud.fcm.terminal.fsbc.invoker
 * @author lilinhai 
 * @since 2021-04-21 15:29
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.terminal.fsbc.invoker;

import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcCredential;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcLogger;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.model.GenericValue;

/**  
 * <pre>凭据调用器</pre>
 * @author lilinhai
 * @since 2021-04-21 15:29
 * @version V1.0  
 */
public class CredentialInvoker extends FsbcApiInvoker
{

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-04-21 15:29 
     * @param httpRequester
     * @param rootUrl 
     */
    public CredentialInvoker(HttpRequester httpRequester, String rootUrl, FsbcLogger fsbcLogger)
    {
        super(httpRequester, rootUrl, fsbcLogger);
    }
    
    public RestResponse create(FsbcCredential fsbcCredential)
    {
        GenericValue<String> genericValue = new GenericValue<>();
        String url = rootUrl + "/common/credential";
        StringEntity stringEntity = new StringEntity(fsbcCredential.toJSONString(), ContentType.APPLICATION_JSON);
        httpRequester.post(url, stringEntity, new HttpResponseProcessorAdapter()
        {
            public void success(HttpResponse httpResponse, ContentType contentType)
            {
                fsbcLogger.logInfo("新增账号信息成功：" + fsbcCredential, true, false);
            }
            
            public void fail(HttpResponse httpResponse)
            {
                if (isJsonContentType(httpResponse))
                {
                    JSONObject r = JSONObject.parseObject(getBodyContent(httpResponse));
                    if (r.containsKey("Message"))
                    {
                        genericValue.setValue(r.getString("Message"));
                    }
                    else
                    {
                        genericValue.setValue(r.toJSONString());
                    }
                }
                else if (isHtmlContentType(httpResponse))
                {
                    genericValue.setValue(getBodyContent(httpResponse));
                }
            }
        });
        
        if (genericValue.getValue() != null)
        {
            return RestResponse.fail(1005435, genericValue.getValue().toString());
        }
        
        return RestResponse.success();
    }
    
    public RestResponse update(FsbcCredential fsbcCredential)
    {
        GenericValue<String> genericValue = new GenericValue<>();
        String url = rootUrl + "/common/credential";
        StringEntity stringEntity = new StringEntity(fsbcCredential.toJSONString(), ContentType.APPLICATION_JSON);
        httpRequester.put(url, stringEntity, new HttpResponseProcessorAdapter()
        {
            public void success(HttpResponse httpResponse, ContentType contentType)
            {
                fsbcLogger.logInfo("修改账号信息成功：" + fsbcCredential, true, false);
            }
            
            public void fail(HttpResponse httpResponse)
            {
                if (isJsonContentType(httpResponse))
                {
                    JSONObject r = JSONObject.parseObject(getBodyContent(httpResponse));
                    if (r.containsKey("Message"))
                    {
                        genericValue.setValue(r.getString("Message"));
                    }
                    else
                    {
                        genericValue.setValue(r.toJSONString());
                    }
                }
                else if (isHtmlContentType(httpResponse))
                {
                    genericValue.setValue(getBodyContent(httpResponse));
                }
            }
        });
        
        if (genericValue.getValue() != null)
        {
            return RestResponse.fail(1005436, genericValue.getValue().toString());
        }
        
        return RestResponse.success();
    }
    
    public RestResponse delete(FsbcCredential fsbcCredential)
    {
        GenericValue<String> genericValue = new GenericValue<>();
        String url = rootUrl + "/common/credential";
        StringEntity stringEntity = new StringEntity(fsbcCredential.toJSONString(), ContentType.APPLICATION_JSON);
        httpRequester.delete(url, stringEntity, new HttpResponseProcessorAdapter()
        {
            public void success(HttpResponse httpResponse, ContentType contentType)
            {
                fsbcLogger.logInfo("删除账号信息成功：" + fsbcCredential, true, false);
            }
            
            public void fail(HttpResponse httpResponse)
            {
                if (isJsonContentType(httpResponse))
                {
                    JSONObject r = JSONObject.parseObject(getBodyContent(httpResponse));
                    if (r.containsKey("Message"))
                    {
                        genericValue.setValue(r.getString("Message"));
                    }
                    else
                    {
                        genericValue.setValue(r.toJSONString());
                    }
                }
                else if (isHtmlContentType(httpResponse))
                {
                    genericValue.setValue(getBodyContent(httpResponse));
                }
            }
        });
        
        if (genericValue.getValue() != null)
        {
            return RestResponse.fail(1005437, genericValue.getValue().toString());
        }
        
        return RestResponse.success();
    }
}
