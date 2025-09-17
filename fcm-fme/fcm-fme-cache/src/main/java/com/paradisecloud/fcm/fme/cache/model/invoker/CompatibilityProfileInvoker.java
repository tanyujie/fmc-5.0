/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CompatibilityLegProfileInvoker.java
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
import com.paradisecloud.fcm.fme.model.response.compatibilityprofile.CompatibilityProfileInfoResponse;
import com.paradisecloud.fcm.fme.model.response.compatibilityprofile.CompatibilityProfilesResponse;
import com.paradisecloud.fcm.fme.model.response.failure.FailureDetailsInfo;
import com.sinhy.exception.SystemException;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.model.GenericValue;

/**  
 * <pre>CompatibilityProfile调用器</pre>
 * @author lilinhai
 * @since 2021-01-28 18:51
 * @version V1.0  
 */
public class CompatibilityProfileInvoker extends FmeApiInvoker
{

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-01-28 18:51 
     * @param httpRequester
     * @param rootUrl 
     */
    public CompatibilityProfileInvoker(HttpRequester httpRequester, String rootUrl)
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
    public CompatibilityProfilesResponse getCompatibilityProfiles(int offset)
    {
        return getEntity("compatibilityProfiles?offset=" + offset, CompatibilityProfilesResponse.class);
    }
    
    /**
     * <pre>获取单个入会方案</pre>
     * @author lilinhai
     * @since 2021-01-26 15:31 
     * @param offset
     * @return CompatibilityLegProfilesResponse
     */
    public CompatibilityProfileInfoResponse getCompatibilityProfile(String id)
    {
        return getEntity("compatibilityProfiles/" + id, CompatibilityProfileInfoResponse.class);
    }
    
    /**
     * <pre>创建入会方案</pre>
     * @author Administrator
     * @since 2020-12-26 23:23 
     * @param callId
     * @param nameValuePairs
     * @return String
     */
    public String createCompatibilityProfile(List<NameValuePair> nameValuePairs)
    {
        GenericValue<FailureDetailsInfo> genericValue = new GenericValue<>();
        StringBuilder idBuilder = new StringBuilder();
        String url = rootUrl + "compatibilityProfiles";
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
                        idBuilder.append(location.replaceAll("^/api/v1/compatibilityProfiles/", ""));
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
    public RestResponse updateCompatibilityProfile(String id, List<NameValuePair> nameValuePairs)
    {
        GenericValue<FailureDetailsInfo> genericValue = new GenericValue<>();
        StringBuilder resultBuilder = new StringBuilder();
        String url = rootUrl + "compatibilityProfiles/" + id;
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
    public RestResponse deleteCompatibilityProfile(String id)
    {
        GenericValue<FailureDetailsInfo> genericValue = new GenericValue<>();
        StringBuilder resultBuilder = new StringBuilder();
        String url = rootUrl + "compatibilityProfiles/" + id;
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
