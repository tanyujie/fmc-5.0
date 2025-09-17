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

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.fme.model.response.call.CallInfoResponse;
import com.paradisecloud.fcm.fme.model.response.call.CallsResponse;
import com.paradisecloud.fcm.fme.model.response.failure.FailureDetailsInfo;
import com.paradisecloud.fcm.fme.model.response.participant.ParticipantsResponse;
import com.sinhy.exception.SystemException;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.model.GenericValue;

/**  
 * <pre>FME活跃会议室接口调用器</pre>
 * @author lilinhai
 * @since 2021-01-28 18:38
 * @version V1.0  
 */
public class CallInvoker extends FmeApiInvoker
{

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-01-28 18:46 
     * @param httpRequester
     * @param rootUrl 
     */
    public CallInvoker(HttpRequester httpRequester, String rootUrl)
    {
        super(httpRequester, rootUrl);
    }
    
    /**
     * <pre>获取单个CallInfo</pre>
     * @author lilinhai
     * @since 2020-12-26 18:13 
     * @param callId
     * @return CallInfoResponse
     */
    public CallInfoResponse getCallInfo(String callId)
    {
        return getEntity("calls/" + callId, CallInfoResponse.class);
    }
    
    /**
     * <pre>分页查找当前call下的与会者Participant</pre>
     * 
     * @author Administrator
     * @since 2020-12-26 23:16
     * @param offset
     * @return ParticipantsResponse
     */
    public ParticipantsResponse getParticipants(String callId, int offset)
    {
        return getEntity("calls/" + callId + "/participants?offset=" + offset, ParticipantsResponse.class);
    }
    
    /**
     * <pre>分页查找进行中的会议Call</pre>
     * @author Administrator
     * @since 2020-12-26 23:16 
     * @param offset
     * @return CallsResponse
     */
    public CallsResponse getCalls(int offset)
    {
        return getEntity("calls?offset=" + offset, CallsResponse.class);
    }
    
    /**
     * <pre>结束会议</pre>
     * @author lilinhai
     * @since 2021-02-04 13:44 
     * @param callId void
     */
    public void deleteCall(String callId)
    {
        String url = rootUrl + "calls/" + callId;
        httpRequester.delete(url);
    }
    
    /**
     * <pre>根据coSpaceId创建一个正在进行的会议</pre>
     * @author Administrator
     * @since 2020-12-26 23:23 
     * @param nameValuePairs
     * @return String
     */
    public String createCall(List<NameValuePair> nameValuePairs)
    {
        GenericValue<FailureDetailsInfo> genericValue = new GenericValue<>();
        StringBuilder callIdBuilder = new StringBuilder();
        String url = rootUrl + "calls";
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
                        callIdBuilder.append(location.replaceAll("^/api/v1/calls/", ""));
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
        return callIdBuilder.toString();
    }
    
    /**
     * <pre>更新Call</pre>
     * @author lilinhai
     * @since 2021-01-26 16:28 
     * @param id
     * @param nameValuePairs
     * @return String
     */
    public RestResponse updateCall(String id, List<NameValuePair> nameValuePairs)
    {
        GenericValue<FailureDetailsInfo> genericValue = new GenericValue<>();
        String url = rootUrl + "calls/" + id;
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
     * <pre>根据coSpaceId创建一个正在进行的会议</pre>
     * @author Administrator
     * @since 2020-12-26 23:23 
     * @param nameValuePairs
     * @return String
     */
    public String createCall(String coSpaceId, String name)
    {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("coSpace", coSpaceId));
        nameValuePairs.add(new BasicNameValuePair("name", name));
        return createCall(nameValuePairs);
    }
}
