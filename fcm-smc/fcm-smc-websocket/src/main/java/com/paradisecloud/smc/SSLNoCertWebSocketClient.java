package com.paradisecloud.smc;

import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;
import org.java_websocket.client.WebSocketClient;


/**
 * @author nj
 * @date 2023/2/16 10:01
 */
public abstract class SSLNoCertWebSocketClient extends WebSocketClient {

    public static final String WSS = "wss://";

    public SSLNoCertWebSocketClient(URI serverUri, String message) {
        super(serverUri);
        if(serverUri.toString().contains(WSS)){
            trustAllHosts(this);
        }
    }


        private void trustAllHosts(SSLNoCertWebSocketClient client){

            TrustManager[] trustAllCerts = {
                    new X509TrustManager() {
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
                    }
            };

            try{
                SSLContext ssl =SSLContext.getInstance("SSL");
                ssl.init(null, trustAllCerts,new java.security.SecureRandom());
                SSLSocketFactory socketFactory = ssl.getSocketFactory();
                this.setSocketFactory(socketFactory);
                HttpsURLConnection.setDefaultSSLSocketFactory(ssl.getSocketFactory());
                HostnameVerifier allHostsValid = new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                };
                HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

}
