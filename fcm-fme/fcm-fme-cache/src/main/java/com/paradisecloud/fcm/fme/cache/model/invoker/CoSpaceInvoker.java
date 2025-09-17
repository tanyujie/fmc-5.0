/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CoSpaceInvoker.java
 * Package     : com.paradisecloud.fcm.fme.cache.model
 * @author lilinhai 
 * @since 2021-01-28 18:38
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
import com.paradisecloud.fcm.fme.model.response.cospace.CoSpaceInfoResponse;
import com.paradisecloud.fcm.fme.model.response.cospace.CoSpacesResponse;
import com.paradisecloud.fcm.fme.model.response.failure.FailureDetailsInfo;
import com.sinhy.exception.SystemException;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.model.GenericValue;

/**  
 * <pre>FME会议室接口调用器</pre>
 * @author lilinhai
 * @since 2021-01-28 18:38
 * @version V1.0  
 */
public class CoSpaceInvoker extends FmeApiInvoker
{

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-01-28 18:46 
     * @param httpRequester
     * @param rootUrl 
     */
    public CoSpaceInvoker(HttpRequester httpRequester, String rootUrl)
    {
        super(httpRequester, rootUrl);
    }
    
    /**
     * <pre>创建会议室，返回会议室ID</pre>
     * @author lilinhai
     * @since 2021-01-30 16:33 
     * @param nameValuePairs
     * @return String
     */
    public String createCoSpace(List<NameValuePair> nameValuePairs)
    {
        StringBuilder idBuilder = new StringBuilder();
        GenericValue<FailureDetailsInfo> genericValue = new GenericValue<>();
        httpRequester.post(rootUrl + "coSpaces", nameValuePairs, new HttpResponseProcessorAdapter()
        {
            public void success(HttpResponse httpResponse, ContentType contentType)
            {
                Header locationHeader = httpResponse.getFirstHeader("Location");
                if (locationHeader != null)
                {
                    String location = locationHeader.getValue().trim();
                    if (!ObjectUtils.isEmpty(location))
                    {
                        idBuilder.append(location.replaceAll("^/api/v1/coSpaces/", ""));
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
     * <pre>根据会议室ID删除会议室</pre>
     * @author lilinhai
     * @since 2021-01-26 16:28 
     * @param id
     * @param nameValuePairs
     * @return String
     */
    public RestResponse deleteCoSpace(String id)
    {
        GenericValue<FailureDetailsInfo> genericValue = new GenericValue<>();
        httpRequester.delete(rootUrl + "coSpaces/" + id, new HttpResponseProcessorAdapter()
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
    
    /**
     * <pre>更新会议室</pre>
     * @author lilinhai
     * @since 2021-01-26 16:28 
     * @param id
     * @param nameValuePairs
     * @return String
     */
    public RestResponse updateCoSpace(String id, List<NameValuePair> nameValuePairs)
    {
        GenericValue<FailureDetailsInfo> genericValue = new GenericValue<>();
        String url = rootUrl + "coSpaces/" + id;
        httpRequester.put(url, nameValuePairs, new HttpResponseProcessorAdapter()
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
    
    /**
     * <pre>获取单个CoSpace</pre>
     * @author lilinhai
     * @since 2020-12-26 18:13 
     * @param coSpaceId
     * @return CallInfoResponse
     */
    public CoSpaceInfoResponse getCoSpaceInfo(String coSpaceId)
    {
        return getEntity("coSpaces/" + coSpaceId, CoSpaceInfoResponse.class);
    }
    
    /**
     * <pre>分页查找未结束的会议室</pre>
     * @author Administrator
     * @since 2020-12-26 23:18 
     * @param offset
     * @return CoSpacesResponse
     */
    public CoSpacesResponse getCoSpaces(int offset)
    {
        return getEntity("coSpaces?offset=" + offset, CoSpacesResponse.class);
    }
}
