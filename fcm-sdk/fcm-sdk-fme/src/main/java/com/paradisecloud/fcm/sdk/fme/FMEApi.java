/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : FMEApi.java
 * Package : com.paradisecloud.fcm.fme.sdk
 * 
 * @author lilinhai
 * 
 * @since 2021-04-28 11:30
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.sdk.fme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paradisecloud.common.core.controller.BaseController;
import com.sinhy.http.HttpObjectCreator;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessorAdapter;

@RestController
@RequestMapping(FmeApiConstant.FME_API_ROOT_PATH)
public class FMEApi extends BaseController
{
    
    private HttpRequester httpRequester = HttpObjectCreator.getInstance().createHttpRequester("admin", "P@rad1se", false);
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-04-28 15:20  
     */
    public FMEApi()
    {
        
    }

    @GetMapping("/{fmeIp}/**")
    public void forwardGet(@PathVariable("fmeIp") String fmeIp, HttpServletRequest request, HttpServletResponse response)
    {
        // 解析url
        String url = parseUrl(fmeIp, request);
        
        addCommonHeaders(response);
        httpRequester.get(url, new HttpResponseProcessorAdapter()
        {
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException
            {
                try
                {
                    response.setStatus(httpResponse.getStatusLine().getStatusCode());
                    if (contentType != null)
                    {
                        response.setContentType(contentType.getMimeType() + "; charset=UTF-8");
                    }
                    response.getWriter().print(getBodyContent(httpResponse));
                }
                catch (IOException e)
                {
                }
            }
            
            @Override
            public void fail(HttpResponse httpResponse)
            {
                try
                {
                    response.setStatus(httpResponse.getStatusLine().getStatusCode());
                    response.setContentType(ContentType.get(httpResponse.getEntity()).getMimeType() + "; charset=UTF-8");
                    response.getWriter().print(getBodyContent(httpResponse));
                }
                catch (IOException e)
                {
                }
            }
        });
    }

    @PostMapping("/{fmeIp}/**")
    public void forwardPost(@PathVariable("fmeIp") String fmeIp, HttpServletRequest request, HttpServletResponse response)
    {
        Enumeration<String> ne = request.getParameterNames();
        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        while (ne.hasMoreElements())
        {
            String name = ne.nextElement();
            nameValuePairList.add(new BasicNameValuePair(name, request.getParameter(name)));
        }
        
        addCommonHeaders(response);
        
        // 解析url
        String url = parseUrl(fmeIp, request);
        httpRequester.post(url, nameValuePairList, new HttpResponseProcessorAdapter()
        {
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException
            {
                try
                {
                    response.setStatus(httpResponse.getStatusLine().getStatusCode());
                    if (contentType != null)
                    {
                        response.setContentType(contentType.getMimeType() + "; charset=UTF-8");
                    }
                    
                    Header h = httpResponse.getFirstHeader("Location");
                    if (h != null)
                    {
                        response.getWriter().print(h.getValue().substring(h.getValue().lastIndexOf('/') + 1));
                    }
                    else
                    {
                        Header authTokenHeader = httpResponse.getFirstHeader("X-Cisco-CMS-Auth-Token");
                        if (authTokenHeader != null)
                        {
                            String authToken = authTokenHeader.getValue().trim();
                            if (!ObjectUtils.isEmpty(authToken))
                            {
                                response.getWriter().print(authToken);
                            }
                        }
                        else
                        {
                            response.getWriter().print(getBodyContent(httpResponse));
                        }
                    }
                }
                catch (IOException e)
                {
                }
            }
            
            @Override
            public void fail(HttpResponse httpResponse)
            {
                try
                {
                    response.setStatus(httpResponse.getStatusLine().getStatusCode());
                    response.setContentType(ContentType.get(httpResponse.getEntity()).getMimeType() + "; charset=UTF-8");
                    response.getWriter().print(getBodyContent(httpResponse));
                }
                catch (IOException e)
                {
                }
            }
        });
    }

    @PutMapping("/{fmeIp}/**")
    public void forwardPut(@PathVariable("fmeIp") String fmeIp, HttpServletRequest request, HttpServletResponse response)
    {
        Enumeration<String> ne = request.getParameterNames();
        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        while (ne.hasMoreElements())
        {
            String name = ne.nextElement();
            nameValuePairList.add(new BasicNameValuePair(name, request.getParameter(name)));
        }
        
        addCommonHeaders(response);
        
        // 解析url
        String url = parseUrl(fmeIp, request);
        if (url.contains("/participants?filterIds="))
        {
            url = url.replace("/participants?filterIds=", "/participants/*?filterIds=");
        }
        
        httpRequester.put(url, nameValuePairList, new HttpResponseProcessorAdapter()
        {
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException
            {
                try
                {
                    response.setStatus(httpResponse.getStatusLine().getStatusCode());
                    if (contentType != null)
                    {
                        response.setContentType(contentType.getMimeType() + "; charset=UTF-8");
                    }
                    response.getWriter().print(getBodyContent(httpResponse));
                }
                catch (IOException e)
                {
                }
            }
            
            @Override
            public void fail(HttpResponse httpResponse)
            {
                try
                {
                    response.setStatus(httpResponse.getStatusLine().getStatusCode());
                    response.setContentType(ContentType.get(httpResponse.getEntity()).getMimeType() + "; charset=UTF-8");
                    response.getWriter().print(getBodyContent(httpResponse));
                }
                catch (IOException e)
                {
                }
            }
        });
    }
    
    @DeleteMapping("/{fmeIp}/**")
    public void forwardDelete(@PathVariable("fmeIp") String fmeIp, HttpServletRequest request, HttpServletResponse response)
    {
        addCommonHeaders(response);
        
        // 解析url
        String url = parseUrl(fmeIp, request);
        httpRequester.delete(url, new HttpResponseProcessorAdapter()
        {
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException
            {
                try
                {
                    response.setStatus(httpResponse.getStatusLine().getStatusCode());
                    if (contentType != null)
                    {
                        response.setContentType(contentType.getMimeType() + "; charset=UTF-8");
                    }
                    response.getWriter().print(getBodyContent(httpResponse));
                }
                catch (IOException e)
                {
                }
            }
            
            @Override
            public void fail(HttpResponse httpResponse)
            {
                try
                {
                    response.setStatus(httpResponse.getStatusLine().getStatusCode());
                    response.setContentType(ContentType.get(httpResponse.getEntity()).getMimeType() + "; charset=UTF-8");
                    response.getWriter().print(getBodyContent(httpResponse));
                }
                catch (IOException e)
                {
                }
            }
        });
    
    }
    
    private String parseUrl(String fmeIp, HttpServletRequest request)
    {
        String uri = request.getServletPath().replaceAll("^" + FmeApiConstant.FME_API_ROOT_PATH + "/" + fmeIp, "");
        String url = "https://" + fmeIp;
        if (!fmeIp.contains(":"))
        {
            url += ":9443";
        }
        url += ("/api/v1" + uri);
        if (!ObjectUtils.isEmpty(request.getQueryString()))
        {
            url += "?" + request.getQueryString();
        }
        return url;
    }
    
    private void addCommonHeaders(HttpServletResponse response)
    {
//        response.addHeader("copyright", "ttclouds.cn");
    }
}
