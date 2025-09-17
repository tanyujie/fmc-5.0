/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CallLegProfileInvoker.java
 * Package     : com.paradisecloud.fcm.fme.cache.model.fmeinvoker
 * @author lilinhai 
 * @since 2021-01-28 18:51
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.cache.model.invoker;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.fme.model.response.callbranding.CallBrandingProfileInfoResponse;
import com.paradisecloud.fcm.fme.model.response.callbranding.CallBrandingProfilesResponse;
import com.paradisecloud.fcm.fme.model.response.failure.FailureDetailsInfo;
import com.sinhy.exception.SystemException;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.model.GenericValue;

/**  
 * <pre>CallProfile调用器</pre>
 * @author lilinhai
 * @since 2021-01-28 18:51
 * @version V1.0  
 */
public class CallBrandingProfileInvoker extends FmeApiInvoker
{

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-01-28 18:51 
     * @param httpRequester
     * @param rootUrl 
     */
    public CallBrandingProfileInvoker(HttpRequester httpRequester, String rootUrl)
    {
        super(httpRequester, rootUrl);
    }
    
    /**
     * <pre>分页获取入会方案</pre>
     * @author lilinhai
     * @since 2021-01-26 15:31 
     * @param offset
     * @return CallLegProfilesResponse
     */
    public CallBrandingProfilesResponse getCallBrandingProfiles(int offset)
    {
        return getEntity("callBrandingProfiles?offset=" + offset, CallBrandingProfilesResponse.class);
    }
    
    /**
     * <pre>获取单个入会方案</pre>
     * @author lilinhai
     * @since 2021-01-26 15:31 
     * @param offset
     * @return CallLegProfilesResponse
     */
    public CallBrandingProfileInfoResponse getCallBrandingProfile(String id)
    {
        return getEntity("callBrandingProfiles/" + id, CallBrandingProfileInfoResponse.class);
    }
    
    /**
     * <pre>创建入会方案</pre>
     * @author Administrator
     * @since 2020-12-26 23:23 
     * @param callId
     * @param nameValuePairs
     * @return String
     */
    public String createCallBrandingProfile(List<NameValuePair> nameValuePairs)
    {
        GenericValue<FailureDetailsInfo> genericValue = new GenericValue<>();
        StringBuilder idBuilder = new StringBuilder();
        String url = rootUrl + "callBrandingProfiles";
        httpRequester.post(url, nameValuePairs, new HttpResponseProcessorAdapter()
        {
            public void success(HttpResponse httpResponse, ContentType contentType)
            {
                Header locationHeader = httpResponse.getFirstHeader("Location");
                if (locationHeader != null)
                {
                    String location = locationHeader.getValue().trim();
                    if (!ObjectUtils.isEmpty(location))
                    {
                        idBuilder.append(location.replaceAll("^/api/v1/callBrandingProfiles/", ""));
                    }
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
            throw new SystemException(1005435, genericValue.getValue().toString());
        }
        
        return idBuilder.toString();
    }
    
    /**
     * <pre>更新入会方案</pre>
     * @author lilinhai
     * @since 2021-01-26 16:28 
     * @param id
     * @param nameValuePairs
     * @return String
     */
    public RestResponse updateCallBrandingProfile(String id, List<NameValuePair> nameValuePairs)
    {
        GenericValue<FailureDetailsInfo> genericValue = new GenericValue<>();
        StringBuilder resultBuilder = new StringBuilder();
        String url = rootUrl + "callBrandingProfiles/" + id;
        httpRequester.put(url, nameValuePairs, new HttpResponseProcessorAdapter()
        {
            public void success(HttpResponse httpResponse, ContentType contentType)
            {
                if (isXmlContentType(httpResponse))
                {
                    resultBuilder.append("fail");
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
    
    /**
     * <pre>更新入会方案</pre>
     * @author lilinhai
     * @since 2021-01-26 16:28 
     * @param id
     * @param nameValuePairs
     * @return String
     */
    public RestResponse deleteCallBrandingProfile(String id)
    {
        GenericValue<FailureDetailsInfo> genericValue = new GenericValue<>();
        StringBuilder resultBuilder = new StringBuilder();
        String url = rootUrl + "callBrandingProfiles/" + id;
        httpRequester.delete(url, new HttpResponseProcessorAdapter()
        {
            public void success(HttpResponse httpResponse, ContentType contentType)
            {
                if (isXmlContentType(httpResponse))
                {
                    resultBuilder.append("fail");
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
}
