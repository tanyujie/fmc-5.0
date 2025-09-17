/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2022, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeBackgroundInvoker.java
 * Package     : com.paradisecloud.fcm.fme.cache.model.invoker
 * @author sinhy 
 * @since 2022-01-06 16:09
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.cache.model.invoker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.json.XML;
import org.slf4j.LoggerFactory;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.fme.cache.model.enumer.OutgoingH264chpValue;
import com.paradisecloud.fcm.fme.model.response.failure.FailureDetailsInfo;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.model.GenericValue;
import com.sinhy.utils.RegExpUtils;

/**  
 * @author sinhy
 * @since 2022-01-06 16:09
 * @version V1.0  
 */
public class FmeBackgroundInvoker extends FmeApiInvoker
{

    private static final Pattern P1 = Pattern.compile("<input type\\s*=\\s*\"hidden\" name\\s*=\\s*\"Acano-Session-Key\" value\\s*=\\s*\"\\w+\"/>");
    private static final Pattern P2 = Pattern.compile("value\\s*=\\s*\"\\w+\"");
    private static final Pattern P3 = Pattern.compile("\"\\w+\"");
    private static final Pattern P4 = Pattern.compile("\\w+");
    
    private String username;

    private String password;
    
    private volatile Map<String, String> lastRefreshedHeaders = new HashMap<>();
    private volatile Long lastHeaderRequestTime;
    
    public FmeBackgroundInvoker(HttpRequester httpRequester, String rootUrl, String username, String password)
    {
        super(httpRequester, rootUrl);
        this.username = username;
        this.password = password;
    }
 
    private Map<String, String> getRequestHeaders()
    {
        if (lastHeaderRequestTime != null && (System.currentTimeMillis() - lastHeaderRequestTime ) < (80 * 1000))
        {
            return lastRefreshedHeaders;
        }
        
        httpRequester.get(rootUrl + "authenticate.html", new HttpResponseProcessorAdapter() 
        {
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException
            {
                String acanoSessionKey = RegExpUtils.extractContent(getBodyContent(httpResponse), P1, P2, P3, P4);
                List<NameValuePair> nameValuePairList = new ArrayList<>();
                nameValuePairList.add(new BasicNameValuePair("0", username));
                nameValuePairList.add(new BasicNameValuePair("1", password));
                nameValuePairList.add(new BasicNameValuePair("redirect", ""));
                nameValuePairList.add(new BasicNameValuePair("Acano-Session-Key", acanoSessionKey));
                httpRequester.post(rootUrl + "authenticate.html", nameValuePairList, new HttpResponseProcessorAdapter() 
                {
                    public void success(HttpResponse httpResponse, ContentType contentType) throws IOException
                    {
                        Header[] hs = httpResponse.getHeaders("Set-Cookie");
                        if (hs != null && hs.length > 0)
                        {
                            String val = hs[0].getValue();
                            Map<String, String> requestHeaders = new HashMap<>();
                            requestHeaders.put("Cookie", val);
                            
                            //  获取另一个请求的响应header（Acano-Session-Key）https://172.16.100.126:9443/cmd.xml?_=1611285930583
                            httpRequester.get(rootUrl + "cmd.xml?_=" + System.currentTimeMillis(), requestHeaders, new HttpResponseProcessorAdapter() 
                            {
                                public void success(HttpResponse httpResponse, ContentType contentType) throws IOException
                                {
                                    String acanoSessionKey = httpResponse.getFirstHeader("Acano-Session-Key").getValue();
                                    lastRefreshedHeaders.clear();
                                    lastRefreshedHeaders.put("Cookie", val);
                                    lastRefreshedHeaders.put("Acano-Session-Key", acanoSessionKey);
                                    lastHeaderRequestTime = System.currentTimeMillis();
                                }
                            });
                        }
                    }
                });
            }
        });
        
        return lastRefreshedHeaders;
    }
    
    public RestResponse allowOutgoingH264chp(OutgoingH264chpValue val)
    {
        GenericValue<FailureDetailsInfo> genericValue = new GenericValue<>();
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        nameValuePairList.add(new BasicNameValuePair("cmd", "allow_outgoing_h264chp " + val.getValue()));
        nameValuePairList.add(new BasicNameValuePair("cmd_button", ""));
        httpRequester.post(rootUrl + "cmd.xml", getRequestHeaders(), nameValuePairList, new HttpResponseProcessorAdapter() 
        {
            public void success(HttpResponse httpResponse, ContentType contentType)
            {
                if (isXmlContentType(httpResponse))
                {
                    setErrorInfo(genericValue, getBodyContent(httpResponse));
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
        
        return RestResponse.success();
    }
    
    public StringBuilder downloadEventLog()
    {
        StringBuilder content = new StringBuilder();
        httpRequester.get(rootUrl + "log.txt", getRequestHeaders(), new HttpResponseProcessorAdapter() 
        {
            public void success(HttpResponse httpResponse, ContentType contentType)
            {
                content.append(getBodyContent(httpResponse));
            }
            
            public void fail(HttpResponse httpResponse)
            {
                content.append(getBodyContent(httpResponse));
            }
        });
        
        return content;
    }
    
    public StringBuilder detailedTracing()
    {
        StringBuilder content = new StringBuilder();
        httpRequester.get(rootUrl + "detailed_tracing.xml", getRequestHeaders(), new HttpResponseProcessorAdapter() 
        {
            public void success(HttpResponse httpResponse, ContentType contentType)
            {
                if (isXmlContentType(httpResponse))
                {
                    content.append(XML.toJSONObject(getBodyContent(httpResponse)).toString());
                }
            }
            
            public void fail(HttpResponse httpResponse)
            {
                content.append(getBodyContent(httpResponse));
            }
        });
        
        return content;
    }
    
    public RestResponse detailedTracing(List<NameValuePair> nameValuePairList)
    {
        GenericValue<FailureDetailsInfo> genericValue = new GenericValue<>();
        LoggerFactory.getLogger(getClass()).info("请求地址：{}, 请求参数：{}", rootUrl + "detailed_tracing.xml", nameValuePairList);
        httpRequester.post(rootUrl + "detailed_tracing.xml", getRequestHeaders(), nameValuePairList, new HttpResponseProcessorAdapter() 
        {
            public void success(HttpResponse httpResponse, ContentType contentType)
            {
                if (isXmlContentType(httpResponse))
                {
                    setErrorInfo(genericValue, getBodyContent(httpResponse));
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
            return RestResponse.fail(genericValue.getValue().toString());
        }
        
        return RestResponse.success();
    }

    public StringBuilder detailedH264chp(List<NameValuePair> nameValuePairList)
    {
        StringBuilder content = new StringBuilder();

        LoggerFactory.getLogger(getClass()).info("请求地址：{}, 请求参数：{}", rootUrl + "cmd.xml", nameValuePairList);
        nameValuePairList.add(new BasicNameValuePair("cmd_button", ""));
        httpRequester.post(rootUrl + "cmd.xml", getRequestHeaders(), nameValuePairList, new HttpResponseProcessorAdapter()
        {
            public void success(HttpResponse httpResponse, ContentType contentType)
            {
                if (isXmlContentType(httpResponse))
                {
                    content.append(XML.toJSONObject(getBodyContent(httpResponse)).toString());
                }
            }

            public void fail(HttpResponse httpResponse)
            {
                content.append(getBodyContent(httpResponse));
            }
        });



        return content;
    }
}
