/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : RegistrationInvoker.java
 * Package     : com.paradisecloud.fcm.terminal.fsbc.invoker
 * @author lilinhai 
 * @since 2021-04-22 18:35
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.terminal.fsbc.invoker;

import java.io.StringReader;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import com.paradisecloud.fcm.terminal.fsbc.model.FsbcLogger;
import com.sinhy.http.HttpRequester;

/**  
 * <pre>fsbc终端在线状态调用器</pre>
 * @author lilinhai
 * @since 2021-04-22 18:35
 * @version V1.0  
 */
public class RegistrationInvoker extends FsbcApiInvoker
{

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-04-22 18:35 
     * @param httpRequester
     * @param rootUrl
     * @param fsbcLogger 
     */
    public RegistrationInvoker(HttpRequester httpRequester, String rootUrl, FsbcLogger fsbcLogger)
    {
        super(httpRequester, rootUrl, fsbcLogger);
    }
    
    public Document getRegistrations()
    {
        SAXReader reader = new SAXReader();
        Document document = null;
        try (StringReader sr = new StringReader(getXmlBodyString("")))
        {
            document = reader.read(sr);
        }
        catch (DocumentException e)
        {
            throw new RuntimeException(e);
        }
        return document;
    }
}
