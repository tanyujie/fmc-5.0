package com.paradisecloud.fcm.web.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.cache.LicenseCache;
import com.paradisecloud.fcm.service.util.TencentCloudUtil;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.net.InetAddress;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CloudTokenUtil {

    public static final int HTTPCODE = 200;
    private static final Logger log = LoggerFactory.getLogger(TencentCloudUtil.class);

    public static String getToken() {



        String cloudUrl = ExternalConfigCache.getInstance().getCloudUrl();
        if(Strings.isBlank(cloudUrl)){
           // return null;
            cloudUrl="https://218.28.249.134:8899/fcm";
        }
        log.info("cloudUrl=====================================:"+cloudUrl);
        try {

            // 正则表达式匹配IPv4地址
//            String ipPattern = "(\\d{1,3}\\.){3}\\d{1,3}";
//
//            Pattern pattern = Pattern.compile(ipPattern);
//            Matcher matcher = pattern.matcher(cloudUrl);
//
//            if (matcher.find()) {
//                String ipAddress = matcher.group();
//                InetAddress inet = InetAddress.getByName(ipAddress);
//                boolean isReachable = inet.isReachable(1000);{
//                    if(!isReachable){
//                        return null;
//                    }
//                }
//            } else {
//             return null;
//            }

            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(5000);
            factory.setReadTimeout(5000);

            RestTemplate restTemplate = new RestTemplate(factory);

            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new NoopHostnameVerifier());


            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", "adminops");
            jsonObject.put("password", "Admin@2024");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/json;charset=UTF-8"));
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());
            headers.add("Accept-Charset", "UTF-8");
            HttpEntity<String> formEntity = new HttpEntity<>(JSON.toJSONString(jsonObject), headers);
            ResponseEntity<String> entity = restTemplate.postForEntity(cloudUrl + "/user/loginNoCode", formEntity, String.class);
          //  ResponseEntity<String> entity = restTemplate.getForEntity(cloudUrl + "/user/loginSn?sn="+ LicenseCache.getInstance().getSn(), String.class);
            if (entity != null) {
                int statusCodeValue = entity.getStatusCodeValue();
                log.info("getToken statusCodeValue:{}", statusCodeValue);
                if (statusCodeValue == HTTPCODE) {

                    String body_1 = entity.getBody();
                    JSONObject parseObject_1 = JSONObject.parseObject(body_1);
                    Object data_1 = parseObject_1.get("data");

                    JSONObject dataJason = JSONObject.parseObject(data_1.toString());
                    String token = dataJason.getString("token");

                return token;
                }
            }

        } catch (Exception e) {
            log.info("create cloud tencent conference error:{}", e.getMessage());
        }

        return null;
    }




    private HttpComponentsClientHttpRequestFactory createRequestFactory() {
        HttpClient httpClient = HttpClientBuilder.create()
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    private static class NoopHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }

    private static class TrustAnyTrustManager implements X509TrustManager {
        private TrustAnyTrustManager() {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        private TrustAnyHostnameVerifier() {
        }

        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

}
