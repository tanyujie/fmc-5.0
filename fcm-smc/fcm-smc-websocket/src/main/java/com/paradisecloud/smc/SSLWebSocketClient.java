/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : SSLWebSocketClient.java
 * Package : com.paradisecloud.fcm.fme.websocket.core
 * 
 * @author sinhy
 * 
 * @since 2021-09-16 18:23
 * 
 * @version V1.0
 */
package com.paradisecloud.smc;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;

import java.net.Socket;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;


/**
 * <pre>请加上该类的描述</pre>
 * 
 * @author sinhy
 * @since 2021-09-16 18:23
 * @version V1.0
 */
public abstract class SSLWebSocketClient extends WebSocketClient
{
    //  String wsStr=ip+baseUrl+"?"+"timestamp="+System.currentTimeMillis()+"signature="+getSignature()+"&username="+username;
    /**
     * <pre>构造方法</pre>
     * 
     * @author sinhy
     * @since 2021-09-16 18:24
     * @param serverUri
     * @param protocolDraft
     */
    protected SSLWebSocketClient(URI serverUri, Draft protocolDraft)
    {
        super(serverUri, protocolDraft);
        this.trustAllHosts();
        this.setConnectionLostTimeout(0);
    }
    
    private void trustAllHosts()
    {
        try
        {
            TrustManager[] trustAllCerts = new TrustManager[] {new X509ExtendedTrustManager()
            {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException
                {
                    
                }
                
                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException
                {
                    
                }
                
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException
                {
                    
                }
                
                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException
                {
                    
                }
                
                public X509Certificate[] getAcceptedIssuers()
                {
                    return null;
                }
                
                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException
                {
                }
                
                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException
                {
                }
            }};
            
            SSLContext ssl = SSLContext.getInstance("SSL");
            ssl.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory socketFactory = ssl.getSocketFactory();
            this.setSocketFactory(socketFactory);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }
}
