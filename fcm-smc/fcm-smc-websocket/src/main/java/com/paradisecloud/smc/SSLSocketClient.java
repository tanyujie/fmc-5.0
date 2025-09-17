package com.paradisecloud.smc;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author nj
 * @date 2023/2/16 11:09
 */
public class SSLSocketClient {

    public static X509TrustManager myX509TrustManager = new X509TrustManager() {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };


    public static SSLSocketFactory getSSLSocketFactory() {

        try {

            SSLContext sslContext = SSLContext.getInstance("SSL");

            sslContext.init(null, getTrustManager(), new SecureRandom());

            return sslContext.getSocketFactory();

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

    }


    private static TrustManager[] getTrustManager() {

        TrustManager[] trustAllCerts = new TrustManager[]{

                myX509TrustManager

        };

        return trustAllCerts;

    }
    public static HostnameVerifier getHostnameVerifier() {
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        };
        return hostnameVerifier;
    }

    public static void main(String[] args) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()

                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())

                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.myX509TrustManager).build();


        Request request = new Request.Builder().url("url").build();

        EchoWebSocketListener listener = new EchoWebSocketListener();

        okHttpClient.newWebSocket(request,listener);
        // client.dispatcher().executorService().shutdown();
    }

}
