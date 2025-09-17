package com.paradisecloud.fcm;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @description 采用undertow作为服务器, 支持https服务配置
 * @memo 备注信息
 */

@Configuration
public class WebserverConfiguration {

    /**
     * http使能
     */
    @Value("${server.https-enable}")
    private boolean httpsEnable;
    /**
     * http服务端口
     */
    @Value("${server.https-port}")
    private Integer httpsPort;
    /**
     * https key-store
     */
    @Value("${server.https-key-store}")
    private String httpsKeyStore;
    /**
     * http key-store-password
     */
    @Value("${server.https-key-store-password}")
    private String httpsKeyStorePassword;
    /**
     * http key-store-type
     */
    @Value("${server.https-key-store-type}")
    private String httpsKeyStoreType;

    @Bean
    public ServletWebServerFactory undertowFactory() {
        UndertowServletWebServerFactory undertowServletWebServerFactory = new UndertowServletWebServerFactory();

        if (httpsEnable) {
            undertowServletWebServerFactory.addBuilderCustomizers((Undertow.Builder builder) -> {
                builder.addHttpsListener(httpsPort, "0.0.0.0", createSslContext());
                // 开启http2
                builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true);
            });
        }

        return undertowServletWebServerFactory;
    }


    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        objectMapper.setDateFormat(simpleDateFormat);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    private SSLContext createSslContext() {
        SSLContext sslContext = null;
        FileInputStream instream = null;
        KeyStore trustStore = null;
        try {
            trustStore = KeyStore.getInstance(httpsKeyStoreType);
            //直接读取项目下的目录
            instream = new FileInputStream(ResourceUtils.getFile(httpsKeyStore));
            trustStore.load(instream, httpsKeyStorePassword.toCharArray());
            // 相信自己的CA和所有自签名的证书
            sslContext = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            }).loadKeyMaterial(trustStore, httpsKeyStorePassword.toCharArray()).build();
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | KeyManagementException | UnrecoverableKeyException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                instream.close();
            } catch (IOException e) {
            }
        }
        return sslContext;
    }
}
