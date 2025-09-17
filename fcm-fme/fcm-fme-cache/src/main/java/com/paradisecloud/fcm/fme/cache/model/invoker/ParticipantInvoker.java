/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : ParticipantInvoker.java
 * Package : com.paradisecloud.fcm.fme.cache.model.fmeinvoker
 * 
 * @author lilinhai
 * 
 * @since 2021-01-28 19:04
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.fme.cache.model.invoker;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.fme.cache.model.enumer.ParticipantBulkOperationMode;
import com.paradisecloud.fcm.fme.model.response.failure.FailureDetailsInfo;
import com.paradisecloud.fcm.fme.model.response.participant.ParticipantInfoResponse;
import com.paradisecloud.fcm.fme.model.response.participant.ParticipantsResponse;
import com.sinhy.exception.SystemException;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessor;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.model.GenericValue;

/**
 * <pre>与会者调用器</pre>
 * 
 * @author lilinhai
 * @since 2021-01-28 19:04
 * @version V1.0
 */
public class ParticipantInvoker extends FmeApiInvoker
{
    
    /**
     * <pre>构造方法</pre>
     * 
     * @author lilinhai
     * @since 2021-01-28 19:04
     * @param httpRequester
     * @param rootUrl
     */
    public ParticipantInvoker(HttpRequester httpRequester, String rootUrl)
    {
        super(httpRequester, rootUrl);
    }
    
    /**
     * <pre>创建（呼入）一个与会者</pre>
     * 
     * @author Administrator
     * @since 2020-12-26 23:23
     * @param callId
     * @param nameValuePairs
     * @return String
     */
    public String createParticipant(String callId, List<NameValuePair> nameValuePairs)
    {
        StringBuilder participantIdBuilder = new StringBuilder();
        String url = rootUrl + "calls/" + callId + "/participants";
        
        GenericValue<FailureDetailsInfo> genericValue = new GenericValue<>();
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
                        participantIdBuilder.append(location.replaceAll("^/api/v1/participants/", ""));
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
        
        return participantIdBuilder.toString();
    }
    
    /**
     * <pre>根据participantId查找与会者详情信息</pre>
     * 
     * @author Administrator
     * @since 2020-12-26 23:17
     * @param participantId
     * @return ParticipantInfoResponse
     */
    public ParticipantInfoResponse getParticipant(String participantId)
    {
        return getEntity("participants/" + participantId, ParticipantInfoResponse.class);
    }
    
    /**
     * <pre>根据participantId删除</pre>
     * 
     * @author Administrator
     * @since 2020-12-26 23:17
     * @param participantId
     * @return RestResponse
     */
    public RestResponse deleteParticipant(String participantId)
    {
        GenericValue<FailureDetailsInfo> genericValue = new GenericValue<>();
        httpRequester.delete(rootUrl + "participants/" + participantId, new HttpResponseProcessorAdapter()
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
     * <pre>分页查找与会者Participant</pre>
     * 
     * @author Administrator
     * @since 2020-12-26 23:16
     * @param offset
     * @return ParticipantsResponse
     */
    public ParticipantsResponse getParticipants(int offset)
    {
        return getEntity("participants?offset=" + offset, ParticipantsResponse.class);
    }
    
    /**
     * <pre>修改与会者基础信息</pre>
     * 
     * @author Administrator
     * @since 2020-12-27 17:57
     * @param participantId
     * @param nameValuePairs
     * @param responseProcessor void
     */
    public void updateParticipant(String participantId, List<NameValuePair> nameValuePairs, HttpResponseProcessor responseProcessor)
    {
        httpRequester.put(rootUrl + "participants/" + participantId, nameValuePairs, responseProcessor);
    }
    
    /**
     * <pre>修改与会者基础信息</pre>
     * 
     * @author Administrator
     * @since 2020-12-27 17:57
     * @param callId
     * @param nameValuePairs
     * @param participantBulkOperationMode void
     */
    public RestResponse bulkUpdateParticipant(String callId, List<NameValuePair> nameValuePairs, ParticipantBulkOperationMode participantBulkOperationMode, String... participantIds)
    {
        Assert.notNull(participantBulkOperationMode, "批量模式不能为空！");
        StringBuilder pids = new StringBuilder();
        for (String pId : participantIds)
        {
            if (!ObjectUtils.isEmpty(pids))
            {
                pids.append(",");
            }
            pids.append(pId);
        }
        
        GenericValue<FailureDetailsInfo> genericValue = new GenericValue<>();
        httpRequester.put(rootUrl + "calls/" + callId + "/participants/*?filterIds=" + pids.toString() + "&mode=" + participantBulkOperationMode.getValue(), nameValuePairs, new HttpResponseProcessorAdapter()
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
     * <pre>修改与会者基础信息</pre>
     * 
     * @author Administrator
     * @since 2020-12-27 17:57
     * @param participantId
     * @param nameValuePairs
     */
    public RestResponse updateParticipant(String participantId, List<NameValuePair> nameValuePairs)
    {
        GenericValue<FailureDetailsInfo> genericValue = new GenericValue<>();
        httpRequester.put(rootUrl + "participants/" + participantId, nameValuePairs, new HttpResponseProcessorAdapter()
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
}
