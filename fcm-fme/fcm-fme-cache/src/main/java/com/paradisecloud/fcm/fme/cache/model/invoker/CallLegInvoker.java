/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CallLegInvoker.java
 * Package     : com.paradisecloud.fcm.fme.cache.model.fmeinvoker
 * @author lilinhai 
 * @since 2021-01-28 19:00
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.cache.model.invoker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.util.Base64Utils;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.fme.model.response.callleg.CallLegInfoResponse;
import com.paradisecloud.fcm.fme.model.response.callleg.CallLegsResponse;
import com.paradisecloud.fcm.fme.model.response.failure.FailureDetailsInfo;
import com.sinhy.exception.SystemException;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.model.GenericValue;

/**  
 * <pre>与会者开关麦信息调用器</pre>
 * @author lilinhai
 * @since 2021-01-28 19:00
 * @version V1.0  
 */
public class CallLegInvoker extends FmeApiInvoker
{

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-01-28 19:01 
     * @param httpRequester
     * @param rootUrl 
     */
    public CallLegInvoker(HttpRequester httpRequester, String rootUrl)
    {
        super(httpRequester, rootUrl);
    }
    
    /**
     * <pre>查找一个与会者所有的CallLeg（一般一个与会者只有一个CallLeg）</pre>
     * @author Administrator
     * @since 2020-12-26 23:17 
     * @param participantId
     * @return CallLegsResponse
     */
    public CallLegsResponse getCallLegs(String participantId)
    {
        return getEntity("participants/" + participantId + "/callLegs", CallLegsResponse.class);
    }
    
    /**
     * <pre>根据callLegId获取指定的CallLeg（与会者开关麦信息）</pre>
     * @author Administrator
     * @since 2020-12-26 23:15 
     * @param callLegId
     * @return CallLegInfoResponse
     */
    public CallLegInfoResponse getCallLeg(String callLegId)
    {
        return getEntity("callLegs/" + callLegId, CallLegInfoResponse.class);
    }
    
    public RestResponse generateKeyframe(String callLegId)
    {
        GenericValue<FailureDetailsInfo> genericValue = new GenericValue<>();
        StringBuilder resultBuilder = new StringBuilder();
        String url = rootUrl + "callLegs/" + callLegId + "/generateKeyframe";
        httpRequester.post(url, new HttpResponseProcessorAdapter()
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
     * <pre>创建（呼入）一个与会者</pre>
     * 
     * @author Administrator
     * @since 2020-12-26 23:23
     * @param callId
     * @param nameValuePairs
     * @return String
     */
    public String createCallLeg(String callId, List<NameValuePair> nameValuePairs)
    {
        StringBuilder participantIdBuilder = new StringBuilder();
        String url = rootUrl + "calls/" + callId + "/callLegs";
        
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
                        participantIdBuilder.append(location.replaceAll("^/api/v1/callLegs/", ""));
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
    
    public RestResponse updateCallLeg(String id, List<NameValuePair> nameValuePairs)
    {
        GenericValue<FailureDetailsInfo> genericValue = new GenericValue<>();
        StringBuilder resultBuilder = new StringBuilder();
        String url = rootUrl + "callLegs/" + id;
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
    
    public String takeSnapshot(String callLegId, String direction, Integer maxWidth)
    {
        GenericValue<FailureDetailsInfo> genericValueError = new GenericValue<>();
        GenericValue<String> genericValue = new GenericValue<>();
        httpRequester.get(rootUrl + "callLegs/" + callLegId + "/snapshot?direction=" + direction + "&maxWidth=" + maxWidth, new HttpResponseProcessorAdapter()
        {
            public void success(HttpResponse httpResponse, ContentType contentType)
            {
                if (!isXmlContentType(httpResponse))
                {
                    try
                    {
                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                        httpResponse.getEntity().writeTo(outStream);
                        
                        // 得到图片的二进制数据，以二进制封装得到数据，具有通用性
                        byte[] data = outStream.toByteArray();
                        String snapshot = Base64Utils.encodeToString(data);
                        genericValue.setValue(snapshot);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            
            public void fail(HttpResponse httpResponse)
            {
                if (isXmlContentType(httpResponse))
                {
                    setErrorInfo(genericValueError, getBodyContent(httpResponse));
                }
            }
        });
        
        if (genericValueError.getValue() != null)
        {
            throw new SystemException(1005435, "该功能仅仅支持2.8版本及以下的FME：" + genericValueError.getValue().toString());
        }
        
        return genericValue.getValue();
    }
    
    public void cameraControl(String callLegId, String pan, String tilt, String zoom, String focus)
    {
        GenericValue<FailureDetailsInfo> genericValue = new GenericValue<>();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (pan != null)
        {
            params.add(new BasicNameValuePair("pan", pan));
        }
        
        if (tilt != null)
        {
            params.add(new BasicNameValuePair("tilt", tilt));
        }
        
        if (zoom != null)
        {
            params.add(new BasicNameValuePair("zoom", zoom));
        }
        
        if (focus != null)
        {
            params.add(new BasicNameValuePair("focus", focus));
        }
        
        httpRequester.put(rootUrl + "callLegs/" + callLegId + "/cameraControl", params,  new HttpResponseProcessorAdapter()
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
            throw new SystemException(1005435, genericValue.getValue().toString());
        }
    }
}
