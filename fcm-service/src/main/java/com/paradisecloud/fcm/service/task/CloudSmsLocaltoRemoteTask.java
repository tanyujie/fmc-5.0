package com.paradisecloud.fcm.service.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.service.eunm.NotifyType;
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
import org.springframework.web.client.RestTemplate;
import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;


/**
 * @author nj
 * @date 2024/4/2 10:15
 */
public class CloudSmsLocaltoRemoteTask extends Task {
    public static final int HTTPCODE = 200;
    private static final Logger log = LoggerFactory.getLogger(CloudSmsLocaltoRemoteTask.class);

    private NotifyType notifyType;
    private String conferenceNumber;
    private  String phone;
    private String conferenceName;
    private  String startTime;
    private String endTime;


    public CloudSmsLocaltoRemoteTask(String id, long delayInMilliseconds,
                         String conferenceName,
                         String conferenceNumber,
                         String phone,
                         String startTime,
                         String endTime,
                         NotifyType notifyType) {
        super("sms_ops_" + id, delayInMilliseconds);
        this.phone = phone;
        this.notifyType = notifyType;
        this.conferenceName=conferenceName;
        this.conferenceNumber=conferenceNumber;
        this.startTime=startTime;
        this.endTime=endTime;
    }
    @Override
    public void run() {
        log.info("CloudSmsLocaltoRemoteTask。"+conferenceName);
         String cloudUrl = ExternalConfigCache.getInstance().getCloudUrl();
        log.info("ops验证cloudUrl："+cloudUrl);
        if(cloudUrl==null){
            cloudUrl="https://218.28.249.134:8899/fcm";
        }
        //String cloudUrl="https://218.28.249.134:8899/fcm";
        log.info("cloudUrl=====================================:"+cloudUrl);
        try {
            RestTemplate restTemplate = new RestTemplate();
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
            if (entity != null) {
                int statusCodeValue = entity.getStatusCodeValue();
                log.info("getToken statusCodeValue:{}", statusCodeValue);
                if (statusCodeValue == HTTPCODE) {

                    String body_1 = entity.getBody();
                    JSONObject parseObject_1 = JSONObject.parseObject(body_1);
                    Object data_1 = parseObject_1.get("data");

                    JSONObject dataJason = JSONObject.parseObject(data_1.toString());
                    String token = dataJason.getString("token");

                    headers.setBearerAuth(token);

                    log.info("开始发送短信:");
                    JSONObject jsonObjectSms = new JSONObject();
                    jsonObjectSms.put("conferenceName", conferenceName);
                    jsonObjectSms.put("phone", phone);
                    jsonObjectSms.put("conferenceNumber", conferenceNumber);
                    if(Strings.isNotBlank(startTime)){
                        jsonObjectSms.put("startTime", startTime);
                    }
                    if(Strings.isNotBlank(endTime)){
                        jsonObjectSms.put("endTime", endTime);
                    }
                    jsonObjectSms.put("notifyType", notifyType.name());

                    formEntity = new HttpEntity<>(JSON.toJSONString(jsonObjectSms), headers);
                    ResponseEntity<String> certificateEntity = restTemplate.postForEntity(cloudUrl + "/ops/web/sendSms", formEntity, String.class);
                    if (certificateEntity != null) {
                        int statusCodeValue_cer = certificateEntity.getStatusCodeValue();
                        log.info("sendSms reslutstatusCodeValue:{}", statusCodeValue_cer);
                        if (statusCodeValue_cer == HTTPCODE) {
                            log.info("SMS send success ");
                        }
                    }
                } else {
                    log.info("CloudSmsLocaltoRemoteTask 获取token HTTPCODE  no 200 ");
                }
            } else {
                log.info("CloudSmsLocaltoRemoteTask 获取token 失败 ");
            }

        } catch (Exception e) {
            log.info("CloudSmsLocaltoRemoteTask error "+e.getMessage());
        }

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
